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

(defn- get-broadcasts-fields [fields]
  (->>
   (clojure.string/split fields #",")
   (filter is-broadcasts-field?)
   (map (fn [f] (clojure.string/replace-first f #"broadcasts." "")))
   (clojure.string/join ",")
   (not-empty)))

(defn wrap-broadcasts-fields [handler]
  (fn [request]
    (if-let [fields (-> request :params :fields)]
      (let [broadcasts-fields (get-broadcasts-fields fields)]
        (handler (assoc request :broadcasts-fields broadcasts-fields)))
      (handler request))))

(defn wrap-grid-defaults [handler]
  (-> handler
      wrap-broadcasts-fields
      wrap-flat-multiple-params))
