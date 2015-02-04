(ns clogrid.middleware.params-test
  (:require [clogrid.core.params :refer :all]
            [clojure.test :refer :all]))

(deftest test-wrap-flat-multiple-params

  (testing "params without any vectors"
    (let [request {:params {:a "a" :b "b"}}
          result ((wrap-flat-multiple-params identity) request)]
      (is (= result request))))

  (testing "params with vector"
    (let [request {:params {:a "a" :b ["b" "c" "d"]}}
          result ((wrap-flat-multiple-params identity) request)]
      (is (= (-> result :params :b) "b,c,d"))))

  (testing "params with vector with comma separated values"
    (let [request {:params {:a "a" :b ["b" "c,d" "e"]}}
          result ((wrap-flat-multiple-params identity) request)]
      (is (= (-> result :params :b) "b,c,d,e")))))

(deftest test-get-params

  (testing "just simple operations on params"
    (let [result (get-params {:a "a" :b "b" :c "c"} {:a identity :b (fn [x] (str x "2"))})]
      (is (= result {:a "a" :b "b2"}))))

  (testing "broadcasts params selector tested"
    (let [result (get-params {:fields "broadcasts.b,broadcasts.c,d,e"
                              :start "someDate"} broadcasts-params)]
      (is (= result {:fields "b,c" :start "someDate"}))))

  (testing "channels params selector tested"
    (let [result (get-params {:fields "broadcasts.b,broadcasts.c,d,e"
                              :start "someDate"
                              :limit "30"
                              :ref "ref"} channels-params)]
      (is (= result {:fields "d,e" :ref "ref"})))))

(deftest test-middleware-is-in-correct-order

  (let [result ((wrap-grid-defaults identity) {:params {:fields ["a,broadcasts.b" "c,d" "broadcasts.e"]}})]
    (is (= (-> result :channels-params :fields) "a,c,d"))
    (is (= (-> result :broadcasts-params :fields) "b,e")))

  (let [result ((wrap-grid-defaults identity) {:params {:fields ["a,broadcasts.b" "c,d" "broadcasts.e"]
                                                        :sort "start"
                                                        :start "start"
                                                        :limit "15"}})]
    (is (= (result :broadcasts-params) {:fields "b,e" :start "start"}))))
