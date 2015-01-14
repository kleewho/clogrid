(ns clogrid.schedule.client
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def lgi-io "http://api.lgi.io/kraken/v2/schedule/data/")



(defn channel [region channel-id]
  (client/get (str lgi-io region "/channels.json")
              {:query-params {"ref"    channel-id
                              "fields" "ref,name"}
               :content-type :json}))

(defn channels [region]
  (client/get (str lgi-io region "/channels.json")))