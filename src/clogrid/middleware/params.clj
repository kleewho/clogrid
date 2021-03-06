(ns clogrid.middleware.params)

(defn- flat-params [params]
  (reduce-kv (fn [res k v]
               (assoc res k
                      (clojure.string/join ","
                                           (flatten (vector v)))))
             {} params))

(defn wrap-flat-multiple-params
  "Wraps a handler in middleware that flattens all params

  Given this query `?a=v1&a=v2`

  instead of `[\"v1\" \"v2\"]` it will flatten it to single
  comma separated string `\"v1,v2\"`
  "
  [handler]
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
   :_exclude #{:start :end :limit}
   :_all identity})

(defn selecting-function [params-selector key]
  (if-let [f (params-selector key)]
    f
    (if ((set (params-selector :_exclude)) key)
      nil
      (params-selector :_all))))

(defn get-params [params params-selector]
  (reduce-kv (fn [res k v]
               (if-let [f (selecting-function params-selector k)]
                 (conj res {k (f v)})
                 res))
             {}
             params))

(defn wrap-grid-params
  "Wraps a handler in middleware that will pick some
  params as a separate group

  Having this query `?a=prefix.v1,v2&b=v3&c=v4

  And given this configuration

  ```clojure
  {:a select-not-prefixed
   :_exclude #{:b}
   :_all identity}
  ```

  It will add to the request as `params-name` this map:

  ```clojure
    {:params-name {:a \"v2\"
                   :c \"v4\"}}
  ```

  Configuration is a map where key is a query param and value is
  function which should be used when selecting this query param.
  Additionally there are special keys:

  * _exclude - for excluding completely some query-params
  * _all - used if the query param isn't implicit included nor excluded

  "
  [handler params-selector params-name]
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
