(ns clogrid.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.middleware :refer [wrap-base-url]]
            [metrics.ring.expose :refer [expose-metrics-as-json]]
            [metrics.ring.instrument :refer [instrument]]))

(defroutes app-routes
  (GET "/:region/channels.json" [region]
       (str "Cruel " region "!"))
  (GET "/:region/:channel.json" [region channel]
       (str channel " " region "!"))
  (route/not-found "Not Found"))

(defn calculateUserRegion [region token]
  )

    private String calculateUserRegion(@Nonnull final String region, @Nonnull final String token) {
        checkNotNull(region);
        checkNotNull(token);
        final Collection<UserView> collection;
        try {
            collection = customerAdapterClient.getCustomer(region, token).getData();
        } catch (final CustomerApiException e) {
            LOG.error("CustomerApiException occured when retrieving customer data, customer api client call failed", e);
            throw new ApiRuntimeException(ScheduleErrorCodes.CUSTOMER_API_UNKNOWN_ERROR, "CustomerAdapterClient call failed with message: "+e.getMessage());
        }
        if (collection.isEmpty()) {
            LOG.error("Empty collection returned when retrieving customer data, customer does not exist");
            throw new ApiRuntimeException(ScheduleErrorCodes.CUSTOMER_DOES_NOT_EXIST_ERROR, "Customer with given token does not exist in given region");
        } else {
            final UserView customer = collection.iterator().next();
            if (containsCityCode(customer)) {
                return customer.getRegionId();
            } else {
                return region;
            }
        }
    }

(def app
  (-> (routes app-routes)
      (wrap-base-url)
      (instrument)
      (expose-metrics-as-json)))
