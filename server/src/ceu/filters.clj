(ns ceu.filters
  (:require [taoensso.timbre :as timbre]))

(timbre/refer-timbre)



(comment

(defn set-info [channels from ik iv]
  (map
    (fn [entry]
      (let [k (-> (first entry) key)
            info (-> (first entry) val)]
        {k (assoc info ik iv)}))
    channels))



(defn filter-distance [channels]
  (filter
    (fn [entry]
      (let [[_ {:keys [distance]}] (first entry)]
        (<= distance 10000)))
    channels))

(defn apply-filters [channels from data]
  (->
    (calc-distance channels from)
    filter-distance
    (set-info from :msg (data :msg))
    (set-info from :nick (data :nick))
    ))



)
