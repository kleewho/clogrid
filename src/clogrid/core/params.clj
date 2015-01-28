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

(defn- get-fields [fields selector]
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

(defn wrap-fields [handler fields-selector fields-name]
  (fn [request]
    (if-let [fields (-> request :params :fields)]
      (let [selected-fields (get-fields fields fields-selector)]
        (handler (assoc request fields-name selected-fields)))
      (handler request))))

(defn wrap-grid-defaults [handler]
  (-> handler
      (wrap-fields broadcasts-fields-selector :broadcasts-fields)
      (wrap-fields channels-fields-selector :channels-fields)
      wrap-flat-multiple-params))
