(ns ceu.routes
  (:require [compojure.core :refer [GET POST defroutes context]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ceu.live_users :as live-users]
            [clojure.string :as s]
            [clojure.walk :refer [keywordize-keys]]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)


(defn parse-query-string [qs]
  (->> (s/split qs #"&")
       (map #(s/split % #"="))
       (map #(update-in % [1] read-string))
       flatten
       (apply hash-map)
       keywordize-keys
       ))





(defroutes main-routes
  ;(context "/geochat" [] ceu.views.geochat.routes/routes)

  (GET "/" {live-users :live-users}
       (live-users/show-all-users live-users))

  (POST "/register/:nickname" [nickname :as {live-users :live-users}]
        (live-users/rest-register live-users nickname))

  (POST "/ping/:nickname" [nickname :as {live-users :live-users
                                         query-string :query-string}]
        (live-users/rest-ping live-users nickname
                              (parse-query-string query-string)))

  (GET "/ws/:nickname" req (live-users/ws-chat req))

  (POST "/talk" req "talk")


  (route/files "/" {:root "resources/public"})
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler
  (-> main-routes
      handler/site))


