(ns user
  (:require [ceu.core :refer :all]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]
            [clojure.string :as s]))

(timbre/refer-timbre)

(def system nil)


(defn init []
  (alter-var-root #'system
                  (constantly (ceu-system {:port 4000}))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start)
  )


(comment

  (go)

  (stop)

  (start)

  (-> system :live-users
      (.register "john")

  )
)















(comment
  ;public static double geoPointDistanceDegree(Point p1, Point p2, double rad) {
  ;     double lat1, lat2, lon1, lon2;

;       lat1 = p1.getOrigin(0) * Math.PI / 180;
;       lat2 = p2.getOrigin(0) * Math.PI / 180;
;       lon1 = p1.getOrigin(1) * Math.PI / 180;
;       lon2 = p2.getOrigin(1) * Math.PI / 180;

;       if (lat1 == lat2 && lon1 == lon2) {
;           return 0;
;       }
;       return Math.acos(
;               (Math.sin(lat1) * Math.sin(lat2)
;               + Math.cos(lat1) * Math.cos(lat2)) * Math.cos(Math.abs(lon2 - lon1))) * rad;
;   }

  )


(defn geoconvert [{latitude1 :lat longitude1 :lon :as pt1}
                  {latitude2 :lat longitude2 :lon :as pt2} rad]


  )
