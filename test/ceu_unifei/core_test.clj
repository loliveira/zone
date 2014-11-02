(ns ceu.core-test
  (:require [com.stuartsierra.component :as component]
            [midje.sweet :refer [=>  throws fact facts]]
            [ceu.core :refer :all]
            [ceu.live_users :as live-users]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)


(facts "about the rest api"
  (let [system (component/start-system
                (ceu-system {:port 4000}))]
    (try
      (fact "about register"
            (let [live-users (-> system :live-users)]
              (.register live-users "zé") => (contains? "zé")

            ))

      (finally
       (component/stop system)))))

