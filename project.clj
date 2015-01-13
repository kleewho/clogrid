(defproject clogrid "0.1.0-SNAPSHOT"
  :description "grid: microservice for epg grid"
  :url "lgi.io/grid"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [metrics-clojure "2.4.0"]
                 [metrics-clojure-graphite "2.4.0"]
                 [metrics-clojure-ring "2.4.0"]
                 [com.lgi.epg.api/customer-api-adapter-client "1.0-SNAPSHOT"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler clogrid.core.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
