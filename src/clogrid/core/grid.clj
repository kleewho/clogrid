(ns clogrid.core.grid
  (:require [clogrid.schedule.client :as schedule]
            ))


(defn decorate-with-ref [fields]
  (if (nil? fields) "ref" (clojure.string/join "," (conj (set (clojure.string/split fields #",")) "ref"))))

(defn get-channels-with-ref [region {fields  "fields" :as query-params}]
  (let
    [fields-with-ref (decorate-with-ref fields)
     query-params-with-fields (conj query-params {"fields" fields-with-ref})]
    ((schedule/get-channels region query-params-with-fields) :data)))

(defn get-broadcasts-for-refs [region query-params refs]
   ((schedule/get-broadcasts region {"channel.ref" refs "limit" 666 "fields" "channel.ref"}) :data))

(defn get-channels-with-broadcasts [region query-params]
  (let
    [channels (get-channels-with-ref region query-params)
     refs (clojure.string/join "," (map (fn [channel] (channel :ref)) channels))
     broadcasts (get-broadcasts-for-refs region query-params refs)]
    (partition-by (fn [broadcast] ((broadcast :channel) :ref)) broadcasts)))
