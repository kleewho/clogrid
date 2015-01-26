(ns clogrid.core.grid
  (:require [clogrid.core.error :as error]
            [clogrid.core.error-helpers :refer [log-as return-as]]
            [clogrid.schedule.client :as schedule]))

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

(defn- get-channels [region query-params]
  (let [{fields :fields} query-params]
    (error/attempt-all
     [fields-with-ref (add-to-fields fields "ref")
      channels (schedule/get-channels
                region
                (conj query-params {:fields fields-with-ref}))]
     channels
     (log-as :error))))

(defn- get-broadcasts [region query-params bcasts-fields]
  (let [fields bcasts-fields]
    (error/attempt-all
     [fields-with-channel-ref (add-to-fields fields "channel.ref")
      broadcasts (schedule/get-broadcasts
                  region
                  (conj query-params {:fields fields-with-channel-ref}))]
     broadcasts
     (log-as :error))))

(defn get-grid [region query-params bcasts-fields]
  (error/attempt-all
   [channels (get-channels region query-params)
    channels-refs (retrieve-refs channels)
    broadcasts (get-broadcasts region (conj query-params {:channel.ref channels-refs}) bcasts-fields)
    grid (merge-channels-with-broadcasts channels broadcasts)]
   grid
   (log-as :error (return-as 503))))
