(ns clogrid.core.params-test
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

(deftest test-broadcasts-fields

  (testing "no fields"
    (let [result ((wrap-broadcasts-fields identity) {:params {}})]
      (is (= (result :broadcasts-fields) nil))))

  (testing "params without broadcasts fields"
    (let [result ((wrap-broadcasts-fields identity) {:params {:fields "a,b"}})]
      (is (= (result :broadcasts-fields) nil))))

  (testing "params with one broadcasts field"
    (let [result ((wrap-broadcasts-fields identity) {:params {:fields "a,broadcasts.b"}})]
      (is (= (result :broadcasts-fields) "b"))))

  (testing "params with only broadcasts fields"
    (let [result ((wrap-broadcasts-fields identity) {:params {:fields "broadcasts.b,broadcasts.c"}})]
      (is (= (result :broadcasts-fields) "b,c")))))

(deftest test-channels-fields

  (testing "no fields"
    (let [result ((wrap-channels-fields identity) {:params {}})]
      (is (= (result :channels-fields) nil))))

  (testing "params with only channels fields"
    (let [result ((wrap-channels-fields identity) {:params {:fields "a,b"}})]
      (is (= (result :channels-fields) "a,b"))))

  (testing "params with one channels field"
    (let [result ((wrap-channels-fields identity) {:params {:fields "a,broadcasts.b"}})]
      (is (= (result :channels-fields) "a"))))

  (testing "params without channels fields"
    (let [result ((wrap-channels-fields identity) {:params {:fields "broadcasts.b,broadcasts.c"}})]
      (is (= (result :channels-fields) nil)))))



(deftest test-middleware-is-in-correct-order
  (let [result ((wrap-grid-defaults identity) {:params {:fields ["a,broadcasts.b" "c,d" "broadcasts.e"]}})]
    (is (= (result :channels-fields) "a,c,d"))
    (is (= (result :broadcasts-fields) "b,e"))))
