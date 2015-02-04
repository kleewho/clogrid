(ns clogrid.schedule.client
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [environ.core :refer [env]]))

(def lgi-io (env :schedule-url "http://api.lgi.io/kraken/v2/schedule/data/"))
(def channels-endpoint "/channels.json")
(def broadcasts-endpoint "/broadcasts.json")

(defn get-schedule-data [region endpoint query-params]
  (let [response (client/get
                   (str lgi-io region endpoint) {:query-params query-params})]
    (:data (json/read-str (response :body) :key-fn keyword))))

(defn get-channels [region query-params]
  (get-schedule-data region channels-endpoint query-params))

(defn get-broadcasts [region query-params]
  (get-schedule-data region broadcasts-endpoint query-params))
