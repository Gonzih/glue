(ns glue.api-test
   (:require [cljs.test :refer-macros [deftest is testing run-tests]]
             [glue.api :as glue]))

(deftest kebab->camel
  (testing :basic
    (is (= "clickMe" (glue/kebab->camel "click-me")))))

(deftest adjust-name
  (testing :basic
    (is (= "clickMe" (glue/adjust-name :click-me)))))

(deftest jsget
  (testing :basic
    (let [obj (clj->js {:number 3})]
      (is (= 3 (glue/jsget obj :number)))))
  (testing :nested
    (let [obj (clj->js {:some {:nested {:key :value}}})]
      (is (= "value" (glue/jsget obj :some :nested :key))))))

(deftest jsset
  (testing :basic
    (let [obj (clj->js {:number 0})]
      (glue/jsset obj :number 5)
      (is (= 5 (glue/jsget obj :number)))))
  (testing :nested
    (let [obj (clj->js {:some {:nested {:key ""}}})]
      (glue/jsset obj :some :nested :key "second value")
      (is (= "second value" (glue/jsget obj :some :nested :key))))))

(deftest jsupdate-raw
  (testing :basic
    (let [obj (clj->js {:number 0})]
      (glue/jsupdate obj :number inc)
      (is (= 1 (glue/jsget obj :number))))))

(deftest jsupdate
  (testing :basic
    (let [obj (clj->js {:number 0})]
      (glue/jsupdate obj :number inc)
      (is (= 1 (glue/jsget obj :number)))))
  (testing :clj-conversion
    (let [obj (clj->js {:items [1 2 3]})]
      (glue/jsupdate obj :items #(conj % 4))
      (is (= [1 2 3 4] (js->clj (glue/jsget obj :items)))))))
