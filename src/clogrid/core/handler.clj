(ns clogrid.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [metrics.ring.expose :refer [expose-metrics-as-json]]
            [metrics.ring.instrument :refer [instrument]]
            [metrics.reporters.graphite :as graphite]
            [clogrid.schedule.client :as schedule]
            [clogrid.core.grid :as grid]
            [clogrid.core.params :refer [wrap-grid-defaults]]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log])
  (:import (java.util.concurrent TimeUnit)
           (com.codahale.metrics MetricFilter)))

(def GR (graphite/reporter {:prefix "clogrid"
                            :rate-unit TimeUnit/SECONDS
                            :duration-unit TimeUnit/MILLISECONDS
                            :filter MetricFilter/ALL}))
;; (graphite/start GR 1)

(defroutes app-routes
  (GET "/:region/grid.json" [region :as request]
       (json/write-str (grid/get-grid region
                                      (request :params)
                                      (request :broadcasts-fields)
                                      (request :channels-fields))))
  (GET "/:region/:channel.json" [region channel] (schedule/get-channel region channel))
  (route/not-found "Not Found"))

(defn init []
  (log/info "
Hi man.

This is POC implementation of GRID made in clojure.
The main question is:
how much CLOC will change?
Will it be x0.5, x0.1, x0.05?

The authors wish you happy hacking. Wax on, wax off
Lukasz & Patryk
"))

(def app
  (->
   (routes app-routes)
   (wrap-grid-defaults)
   (wrap-defaults api-defaults)
   (expose-metrics-as-json)
   (instrument)))
