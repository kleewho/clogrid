(ns clogrid.schedule.client
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def lgi-io "http://api.lgi.io/kraken/v2/schedule/data/")
(def channelsEndpoint "/channels.json")



(defn channel [region channel-id]
  (client/get (str lgi-io region "/channels.json")
              {:query-params {"ref"    channel-id
                              "fields" "ref,name"}
               :content-type :json}))

(defn getChannel [region channel-id]
  (let [response (client/get (str lgi-io region channelsEndpoint)
                            {:query-params {"ref"    channel-id
                                            "fields" "ref,name"}
                             :content-type :json})] 
	(response :body)))

(defn getChannels [region]
(let [response (client/get (str lgi-io region channelsEndpoint))]
	(response :body)))
