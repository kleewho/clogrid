(ns clogrid.core.error-helpers
  (:require [clojure.tools.logging :as log]))

(defn log-as [level f]
  (fn [error]
    (log/log level error)
    (f error)))

(defn return-as [return-code]
  (fn [error] return-code))
