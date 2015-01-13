(ns clogrid.schedule.client
  (:require [clj-http.client :as client]))

(def lgi-io "http://api.lgi.io/kraken/v2/schedule/data/")

(defn channel [region channel-id]
  (client/get (str lgi-io region "/channels.json")
              {:query-params {"fields" (str "ref," channel-id)}}))

(defn channels [region]
  (client/get (str lgi-io region "/channels.json")))