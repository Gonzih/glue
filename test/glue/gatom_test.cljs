(ns glue.gatom-test
  (:refer-clojure :exclude [atom])
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [glue.gatom :refer [atom]]))

(deftest GAtom
  (testing :deref
    (let [state (atom 5)]
      (is (= 5 @state))))

  (testing :reset!
    (let [state (atom 5)]
      (reset! state 10)
      (is (= 10 @state))))

  (testing :swap!
    (let [state (atom 5)]
      (swap! state inc)
      (is (= 6 @state)))

    (let [state (atom 5)]
      (swap! state + 3)
      (is (= 8 @state)))

    (let [state (atom 5)]
      (swap! state + 3 1)
      (is (= 9 @state)))

    (let [state (atom 5)]
      (swap! state + 3 1 5)
      (is (= 14 @state)))

    (let [state (atom 5)]
      (swap! state + 3 1 5 6)
      (is (= 20 @state)))))
