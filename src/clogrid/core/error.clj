(ns clogrid.core.error
  (:require [clojure.algo.monads :refer [defmonad domonad]]))

(defrecord Failure [message])

(defn fail [message] (Failure. message))

(defprotocol ComputationFailed
  "A protocol that determines if a computation has resulted in a failure.
   This allows the definition of what constitutes a failure to be extended
   to new types by the consumer."
  (has-failed? [self]))

(extend-protocol ComputationFailed
  Object
  (has-failed? [_] false)

  Failure
  (has-failed? [_] true)

  Exception
  (has-failed? [_] true))

(defn- try-apply [f & args]
  (try
    (apply f args)
    (catch Exception e e)))

(defmonad error-m
          [m-result identity
           m-bind   (fn [m f] (if (has-failed? m)
                                m
                                (try-apply f m)))])


(defmacro attempt-all
  "Attempts to evaluate all bindings forms. If one of them is failed
  the rest aren't executed and the error is passed to the fail handler
  called `else`. If none of the binding failed then `return` is
  returned from attemp-all

  Example usage:

  ```clojure
  (attempt-all [a (not-failing)
                b (not-failing)]
                b
                (fn [failure] (log/error failuer))) ;;returns b
  ```

  ```clojure
  (attempt-all [a (failing)
                b (not-failing)] ;;this line won't be even called
                b
                (fn [failure] (log/error failure))) ;;logs failure
  ```
  "
  ([bindings return] `(domonad error-m ~bindings ~return))
  ([bindings return else]
     `(let [result# (attempt-all ~bindings ~return)]
        (if (has-failed? result#)
          (~else result#)
          result#))))
