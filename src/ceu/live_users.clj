(ns ceu.live_users
  (:require [com.stuartsierra.component :as component]
            [clj-time.local :as local-time]
            [clj-time.coerce :as corce]
            [taoensso.timbre :as timbre]
            [ring.util.response :refer [response]]
            [org.httpkit.server :refer [with-channel websocket? send!
                                        on-close on-receive]]
            [cheshire.core :as json]
            [clojure.walk :refer [keywordize-keys]]

            [ceu.filters :as filters]
            ))



(timbre/refer-timbre)

;------------------------------------------------------------------------------------

(defn- generate-last-seen []
  (-> (local-time/local-now)
      corce/to-string))

(defn register-user [m nick]
  (assoc m nick {:last-seen (generate-last-seen)}))

(defn register-user-channel [m nick channel]
  (assoc m nick {:channel channel}))


(defn channel-to-nick [m channel]
  (let [nick (->> m
                 (filter (fn [[k {ch :channel}]] (= ch channel)))
                 ffirst)]
    (debug "channel-to-nick " channel nick)
    nick))

(defn unregister-user [m channel]
    (->> (channel-to-nick m channel)
         (dissoc m)))

(defn update-user-info [m nick coords]
  (trace "update-user-info - " coords)
  (if (m nick)
    (-> (assoc-in m [nick :last-seen] (generate-last-seen))
        (assoc-in [nick :coords] coords))
    m))



;-------- API ----------------------------------------------------------------------------

(defprotocol LiveUsersApi
  (register [component nick])
  (register-channel [component nick channel])
  (unregister [component channel])
  (ping [component nick coords])
  )

(defrecord LiveUsers []
  component/Lifecycle
  LiveUsersApi

  (start [{users :users :as component}]
         (if users component
           (do
             (assoc component :users (atom {})))))

  (stop [{users :users :as component}]
        (if-not  users
          component
    (assoc component :users nil)))

  (register [{users :users :as component} nick]
            (swap! users register-user nick))

  (register-channel [{users :users :as component} nick channel]
                    (swap! users register-user-channel nick channel))

  (unregister [{users :users :as component} channel]
              (swap! users unregister-user channel))

  (ping [{users :users :as component} nick coords]
            (swap! users update-user-info nick coords)
        ))


(defn new-live-users
  "constructor"
  []
  (map->LiveUsers {:users nil}))



;-------- Rest API entry point ----------------------------------------------------------------------------


(defn prepare-to-json [m]
  (->> m
    (map (fn [[k v]]
            {k (update-in v [:channel] str)}))
    (apply merge)))

(defn show-all-users [live-users]
  (let [users (-> live-users :users)]
    (-> @users
        prepare-to-json
        response)))

(defn rest-register [live-users nickname]
  (-> (.register live-users nickname)
      prepare-to-json
      response))

(defn rest-ping [live-users nickname coords]
  (->> (.ping live-users nickname coords)
       prepare-to-json
       response ))








; ----------------------------------------------------------

(defn distance [{latitude1 :lat longitude1 :lon :as pt1}
                {latitude2 :lat longitude2 :lon :as pt2}]
  (debug "distance - " pt1 pt2)
  (let [R 6371 ; km
        φ1  (Math/toRadians latitude1)
        φ2  (Math/toRadians latitude2)
        Δφ  (-> (- latitude2 latitude1) Math/toRadians)
        Δλ  (-> (- longitude2 longitude1) Math/toRadians)
        a   (+ (* (Math/sin (/ Δφ 2)) (Math/sin (/ Δφ 2)))
               (* (Math/cos φ1) (Math/cos φ2) (Math/sin (/ Δλ 2)) (Math/sin (/ Δλ 2))))
        c   (* 2 (Math/atan2 (Math/sqrt a) (Math/sqrt (- 1 a))))]
    (* c R 1000)))





var a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
        Math.cos(φ1) * Math.cos(φ2) *
        Math.sin(Δλ/2) * Math.sin(Δλ/2);
var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

var d = R * c;
  )


(defn calc-distance [live-users nickname-from coords-to]
  (let [users @(-> live-users :users)
        {:keys [coords]} (users nickname-from)]
    (if coords
      (distance coords coords-to))))

; ----------------------------------------------------------


(defn ws-relay-to-users [live-users data]
  (let [users (-> live-users :users)
        {:keys [message nickname]} (json/parse-string data true)]

    (debug "ws-relay-to-users data - " message nickname)

    (doseq [[u info] @users]
      (let [{:keys [coords channel]} info]
        (when (and coords channel)
          (let [d (calc-distance live-users nickname coords)
                relay-data (json/generate-string {:message message :nickname nickname :distance d})]
            (when-not (send! channel relay-data false)
              (debug "ws-relay-to-users can't send data to " channel)
              (.unregister live-users channel))
            ))))))



(defn ws-chat [{live-users :live-users cookie :cookie
                {nickname :nickname} :params
                :as req}]
  (with-channel req channel
    (on-close channel  (fn [status]
                         (debug "unregister " channel)
                         (.unregister live-users channel)))

    (on-receive channel (fn [data]
                          (ws-relay-to-users live-users data)))

    ;(debug "chat-handler - req - " (pprint-str value))
    ;(debug "chat-handler - cookie-store - " (.read-session cookie-store value))

    (.register-channel live-users nickname channel)


    (if (websocket? channel)
      (debug "WebSocket channel " channel)
      (do
        (send! channel "heheheheheeh" false)
        (debug "HTTP channel!")))))




























