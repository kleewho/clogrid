(ns clogrid.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :refer [api]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.middleware :refer [wrap-base-url]]
            [metrics.ring.expose :refer [expose-metrics-as-json]]
            [metrics.ring.instrument :refer [instrument]]
            [clogrid.schedule.client :as schedule]
            [clogrid.core.grid :as grid]
            [clojure.data.json :as json]))


(defn channel-info [region channel]
  (schedule/channel region channel))

(defroutes app-routes
           (GET "/:region/channels.json" [region :as {query-params :query-params}]
                (json/write-str (grid/get-channels-with-broadcasts region query-params)))
           (GET "/:region/:channel.json" [region channel] (schedule/get-channel region channel))
           (route/not-found "Not Found"))

(def app
  (->
    (routes app-routes)
    (api)
    (wrap-base-url)
    (instrument)
    (expose-metrics-as-json)))
