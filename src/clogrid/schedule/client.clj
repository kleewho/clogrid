(ns clogrid.schedule.client
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def lgi-io "http://api.lgi.io/kraken/v2/schedule/data/")
(def channels "/channels.json")
(def broadcasts "/broadcasts.json")



(defn channel [region channel-id]
  (client/get (str lgi-io region "/channels.json")
              {:query-params {"ref"    channel-id
                              "fields" "ref,name"}
               :content-type :json}))

(defn channels [region]
  (client/get (str lgi-io region channels)))

(defn broadcast [region broadcast-id]
  (client/get (str lgi-io region broadcasts)
              {:query-params {"fields" (str "id," broadcast-id)}}))

(defn broadcasts [region]
  (client/get (str lgi-io region broadcasts)))