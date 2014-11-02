(ns ceu.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [ceu.core :as core]))

(defn -main [& args]
  (let [[port] args]
    (component/start
      (core/ceu-system {:port (Integer/parseInt port)}))))

