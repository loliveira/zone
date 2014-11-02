(defproject ceu-unifei "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]

                 [midje "1.6.3"]

                 [http-kit "2.1.19"]
                 [javax.servlet/servlet-api "2.5"]

                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-core "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [ring/ring-devel "1.3.1"]

                 [com.taoensso/timbre "3.3.1"]
                 [com.stuartsierra/component "0.2.2"]



                 [compojure "1.2.1"]
                 [clj-time "0.8.0"]
                 [cheshire "5.3.1"]

                 ]

  :profiles {:dev {:plugins [[lein-midje "3.1.1"]]
                   :source-paths ["dev"]}}

  :main ceu.main
  )
