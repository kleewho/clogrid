(ns clogrid.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :refer [api]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.middleware :refer [wrap-base-url]]
            [metrics.ring.expose :refer [expose-metrics-as-json]]
            [metrics.ring.instrument :refer [instrument]]
            [metrics.reporters.graphite :as graphite]
            [clogrid.schedule.client :as schedule]
            [clogrid.core.grid :as grid]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log])
  (:import (java.util.concurrent TimeUnit)
           (com.codahale.metrics MetricFilter)))

(def GR (graphite/reporter {:prefix "clogrid"
                            :rate-unit TimeUnit/SECONDS
                            :duration-unit TimeUnit/MILLISECONDS
                            :filter MetricFilter/ALL}))
(graphite/start GR 1)

(defroutes app-routes
           (GET "/:region/channels.json" [region :as {query-params :query-params}]
                (json/write-str (grid/get-channels-with-broadcasts region query-params)))
           (GET "/:region/:channel.json" [region channel] (schedule/get-channel region channel))
           (route/not-found "Not Found"))

(def app
  (do
    (log/info "
Hi man.

This is POC implementation of GRID made in clojure.
The main question is:
how much CLOC will change?
Will it be x0.5, x0.1, x0.05?

The authors wish you happy hacking. Wax on, wax off
Lukasz & Patryk
")
    (->
     (routes app-routes)
     (api)
     (wrap-base-url)
     (instrument)
     (expose-metrics-as-json))))
