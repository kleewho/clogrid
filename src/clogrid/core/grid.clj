(ns clogrid.core.grid
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clogrid.core.error :as error]
            [clogrid.core.error-helpers :refer [log-as return-as]]
            [clogrid.schedule.client :as schedule]
            [clojure.string :as str]))

(def formatter (f/formatters :date-time-no-ms))

(defn- str-date [date]
  (f/unparse formatter date))

(defn- add-to-fields [fields field]
  (if (nil? fields)
    field
    (clojure.string/join "," (conj (set (clojure.string/split fields #",")) field))))

(defn- retrieve-refs [channels]
  (clojure.string/join "," (map (fn [c] (c :ref)) channels)))

(defn- merge-channels-with-broadcasts [channels broadcasts]
  (let [grouped-broadcasts (group-by (fn [b] (:ref (:channel b))) broadcasts)]
    (map (fn [c] (conj
                  c
                  {:broadcasts (grouped-broadcasts (:ref c))})) channels)))

(defn- get-channels [region query-params fields]
  (error/attempt-all
   [fields-with-ref (add-to-fields fields "ref")
    channels (schedule/get-channels
              region
              (conj query-params
                    {:fields fields-with-ref}
                    {:limit 32000}))
    channels]
   (log-as :error identity)))

(defn- get-broadcasts [region query-params fields]
  (error/attempt-all
   [fields-with-channel-ref (add-to-fields fields "channel.ref")
    broadcasts (schedule/get-broadcasts
                region
                {:fields fields-with-channel-ref
                 :limit 32000
                 :start< (get query-params
                              :end
                              (str-date (t/plus (t/now) (t/hours 6))))
                 :end> (get query-params
                            :start
                            (str-date (t/minus (t/now) (t/hours 1))))
                 :sort "start"})]
   broadcasts
   (log-as :error identity)))

(defn get-grid [region query-params bcasts-fields channels-fields]
  (error/attempt-all
   [channels (get-channels region query-params channels-fields)
    channels-refs (retrieve-refs channels)
    broadcasts (get-broadcasts region (conj query-params {:channel.ref channels-refs}) bcasts-fields)
    grid (merge-channels-with-broadcasts channels broadcasts)]
   grid
   (log-as :error (return-as 503))))
