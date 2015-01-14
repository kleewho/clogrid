(ns clogrid.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.middleware :refer [wrap-base-url]]
            [metrics.ring.expose :refer [expose-metrics-as-json]]
            [metrics.ring.instrument :refer [instrument]]
            [clogrid.schedule.client :as schedule]))

(defroutes app-routes
           (GET "/:region/channels.json" [region] (str "Cruel " region "!"))
           (GET "/:region/:channel.json" [region channel] (schedule/channel region channel))
           (route/not-found "Not Found"))

(def app
  (-> (routes app-routes)
      (wrap-base-url)
      (instrument)
      (expose-metrics-as-json)))
