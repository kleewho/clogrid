(ns clogrid.schedule.client
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def lgi-io "http://api.lgi.io/kraken/v2/schedule/data/")
(def channels-endpoint "/channels.json")
(def broadcasts-endpoint "/broadcasts.json")

(defn get-channel [region channel-id]
  (let [response (client/get (str lgi-io region broadcasts-endpoint)
                             {:query-params {"ref"    channel-id
                                             "fields" "ref,name"}
                              :content-type :json})]
    (response :body)))

(defn get-channels [region query-params]
  (let [response (client/get
                   (str lgi-io region channels-endpoint) {:query-params query-params})]
    (json/read-str (response :body))))

(defn get-broadcasts [region query-params]
  (let [response (client/get
                   (str lgi-io region broadcasts-endpoint) {:query-params query-params})]
    (json/read-str (response :body))))

