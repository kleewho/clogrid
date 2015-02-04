(ns clogrid.metrics.graphite-reporter
  (:require [metrics.reporters.graphite :as graphite])
  (:import (java.util.concurrent TimeUnit)
           (com.codahale.metrics MetricFilter)))


(def GR (graphite/reporter {:prefix "clogrid"
                            :rate-unit TimeUnit/SECONDS
                            :duration-unit TimeUnit/MILLISECONDS
                            :filter MetricFilter/ALL}))
(defn start []
  (graphite/start GR 1))
