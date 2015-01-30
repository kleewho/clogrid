(ns clogrid.core.params)

(defn- flat-params [params]
  (reduce-kv (fn [res k v]
               (assoc res k
                      (clojure.string/join ","
                                           (flatten (vector v)))))
             {} params))

(defn wrap-flat-multiple-params [handler]
  (fn [request]
    (if-let [params (request :params)]
      (let [flatten-params (flat-params params)]
        (handler (assoc request :params flatten-params)))
      (handler request))))

(defn- is-broadcasts-field? [field]
  (.contains field "broadcasts"))

(defn- get-fields [selector fields]
  (->>
   (clojure.string/split fields #",")
   (selector)
   (clojure.string/join ",")
   (not-empty)))

(defn- broadcasts-fields-selector [fields]
  (map #(clojure.string/replace-first % #"broadcasts." "")
       (filter is-broadcasts-field? fields)))

(defn- channels-fields-selector [fields]
  (filter (comp not is-broadcasts-field?) fields))

(def broadcasts-params
  {:fields (partial get-fields broadcasts-fields-selector)
   :start identity
   :end identity})

(def channels-params
  {:fields (partial get-fields channels-fields-selector)
   :exclude (comp not #{:start :end})
   :default identity})

(defn get-params [params params-selector]
  (reduce-kv (fn [res k v]
               (if-let [f (params-selector k)]
                 (conj res {k (f v)})
                 res))
             {}
             params))

(defn wrap-grid-params [handler params-selector params-name]
  (fn [request]
    (if-let [params (request :params)]
      (let [selected-params (get-params params params-selector)]
        (handler (conj request {params-name selected-params})))
      (handler request))))

(defn wrap-broadcasts-params [handler]
  (wrap-grid-params handler broadcasts-params :broadcasts-params))

(defn wrap-channels-params [handler]
  (wrap-grid-params handler channels-params :channels-params))

(defn wrap-grid-defaults [handler]
  (-> handler
      wrap-broadcasts-params
      wrap-channels-params
      wrap-flat-multiple-params))
