(ns clogrid.schedule.client
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def lgi-io "http://api.lgi.io/kraken/v2/schedule/data/")
(def channels-endpoint "/channels.json")
(def broadcasts-endpoint "/broadcasts.json")

; this one seems to be not used - and besides it uses wrong endpoint
; TODO: fix or remove this function
(defn get-channel [region channel-id]
  (let [response (client/get (str lgi-io region broadcasts-endpoint)
                             {:query-params {"ref"    channel-id
                                             "fields" "ref,name"}
                              :content-type :json})]
    (response :body)))

(defn get-schedule-data [region endpoint query-params]
  (let [response (client/get
                   (str lgi-io region endpoint) {:query-params query-params})]
    (json/read-str (response :body) :key-fn keyword)))

(defn get-channels [region query-params]
  (get-schedule-data region channels-endpoint query-params))

(defn get-broadcasts [region query-params]
  (get-schedule-data region broadcasts-endpoint query-params))
