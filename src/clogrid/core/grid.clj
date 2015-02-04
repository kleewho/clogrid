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

(defn- merge-channels-with-broadcasts [channels broadcasts]
  (let [grouped-broadcasts (group-by (fn [b] (:ref (:channel b))) broadcasts)]
    (map (fn [c] (conj
                  c
                  {:broadcasts (grouped-broadcasts (:ref c))})) channels)))

(defn- get-channels [region params]
  (error/attempt-all
   [fields-with-ref (add-to-fields (params :fields) "ref")
    channels (schedule/get-channels
              region
              (assoc params
                     :fields fields-with-ref
                     :limit 32000))]
   channels
   (log-as :error)))

(defn- get-broadcasts [region params]
  (error/attempt-all
   [fields-with-channel-ref (add-to-fields (params :fields) "channel.ref")
    broadcasts (schedule/get-broadcasts
                region
                {:fields fields-with-channel-ref
                 :limit 32000
                 :start< (get params
                              :end
                              (str-date (t/plus (t/now) (t/hours 6))))
                 :end> (get params
                            :start
                            (str-date (t/minus (t/now) (t/hours 1))))
                 :sort "start"})]
   broadcasts
   (log-as :error)))

(defn get-grid
  "Fetches channels and broadcasts in specified time range
  and embeds broadcasts into channels. To join them channel.ref is used.
  "
  [region bcasts-params channels-params]
  (error/attempt-all
   [channels (get-channels region channels-params)
    broadcasts (get-broadcasts region bcasts-params)
    grid (merge-channels-with-broadcasts channels broadcasts)]
   grid
   (log-as :error (return-as 503))))
