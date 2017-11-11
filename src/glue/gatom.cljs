(ns glue.gatom
  (:refer-clojure :exclude [atom])
  (:require [cljsjs.vue]))

(defprotocol VueState
  (-get-state! [this])
  (-set-state! [this value]))

(deftype GAtom [^:mutable obj]
  IAtom

  VueState
  (-get-state! [this]
    (aget obj "state"))
  (-set-state! [this value]
    (aset obj "state" value))

  IDeref
  (-deref [a]
    (-get-state! a))

  IReset
  (-reset! [a new-value]
    (-set-state! a new-value))

  ISwap
  (-swap! [a f]          (-reset! a (f (-get-state! a))))
  (-swap! [a f x]        (-reset! a (f (-get-state! a) x)))
  (-swap! [a f x y]      (-reset! a (f (-get-state! a) x y)))
  (-swap! [a f x y more] (-reset! a (apply f (-get-state! a) x y more))))

(defn atom [x]
  (let [obj (js-obj)]
    (js/Vue.util.defineReactive obj "state" x)
    (GAtom. obj)))
