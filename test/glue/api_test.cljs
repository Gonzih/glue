(ns glue.api-test
   (:require [cljs.test :refer-macros [deftest is testing run-tests]]
             [glue.api :as glue]))

(deftest kebab->camel
  (testing :basic
    (is (= "clickMe" (glue/kebab->camel "click-me")))))

(deftest convert-name
  (testing :basic
    (is (= "clickMe" (glue/convert-name :click-me)))))

(deftest convert-data
  (testing :basic
    (let [f (fn [] {:a "A"})]
      (is (= "A" (.-a ((glue/convert-data f))))))))

(deftest convert-method
  (testing :basic
    (let [f (fn [this state _] state)
          state {:field "B"}]
      (is (= "B" (:field ((glue/convert-method f state))))))))

(deftest convert-computed-prop
  (testing :basic
    (let [f (fn [this state] state)
          state {:field "C"}]
      (is (= "C" (.-field ((glue/convert-computed-prop f state))))))))

(deftest convert-methods
  (testing :basic
    (let [methods {:met (fn [this state] state)}
          state {:field "D"}]
      (is (= "D" (:field ((get (glue/convert-methods methods state) "met"))))))))

(deftest convert-computed-props
  (testing :basic
    (let [props {:met (fn [this state] state)}
          state {:field "E"}]
      (is (= "E" (.-field ((get (glue/convert-computed-props props state) "met"))))))))

(deftest convert-props
  (testing :basic
    (is (= ["propOne" "propTwo" "prop"] (glue/convert-props [:prop-one :prop-two :prop])))))

(deftest generate-comp-properties-for-state
  (testing :basic
    (let [state {:one (atom 1)}
          conv (glue/generate-comp-properties-for-state state)]
      (is (= 1 ((get conv "one")))))))

(deftest convert-component-config
  (testing :delete-state
    (let [config {:state {:state-one (glue/atom :one)}
                  :template "#template-id"}]
      (is (nil? (:state (glue/convert-component-config config)))))))
