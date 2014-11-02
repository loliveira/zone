(ns ceu.core
  (:require [com.stuartsierra.component :as component]
            [ceu.http :as http-server]
            [ceu.live_users :as live_users]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

(timbre/set-level! :debug)


(defn ceu-system [{port :port}]
  (component/system-map
   :live-users (live_users/new-live-users)
   :app (component/using
          (http-server/new-http-server {:port port})
          [:live-users])))

