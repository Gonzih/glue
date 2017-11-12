(ns glue.api
  (:refer-clojure :exclude [atom])
  (:require [clojure.spec.alpha :as s]
            [clojure.string     :as string]
            [cljsjs.vue]
            [glue.gatom         :as gatom]))

(s/def ::template string?)
(s/def ::gatom #(instance? gatom/GAtom %))
(s/def ::state (s/map-of keyword? ::gatom))
(s/def ::props (s/* keyword?))
(s/def ::methods (s/map-of keyword? fn?))
(s/def ::computed (s/map-of keyword? fn?))
(s/def ::data fn?)
(s/def ::config (s/keys :req-un [::template]
                        :opt-un [::props ::state ::computed ::methods ::data]))

(def atom gatom/atom)

(defn trace [i]
  (prn i)
  i)

(defn kebab->camel [s]
  (string/replace
    s
    #"-(\w)"
    (fn [matches] (string/upper-case (second matches)))))

(defn convert-name [k]
  (kebab->camel (name k)))

(defn convert-data [data-fn]
  (comp clj->js data-fn))

(defn convert-method [method-fn state]
  (fn [& args] (this-as this (apply method-fn this state args))))

(defn convert-computed-prop [prop-fn state]
  (fn [] (this-as this (clj->js (prop-fn this state)))))

(defn convert-methods [methods state]
  (into {}
        (map (fn [[k v]] [(convert-name k) (convert-method v state)])
             methods)))

(defn convert-computed-props [computed-props state]
  (into {}
        (map (fn [[k v]] [(convert-name k) (convert-computed-prop v state)])
             computed-props)))

(defn convert-props [props]
  (map convert-name props))

(defn generate-comp-properties-for-state [state]
  (into {}
        (map (fn [[k a]] [(convert-name k) #(clj->js @a)])
             state)))

(defn validate-config [config]
  (if (s/valid? ::config config)
    true
    (do (s/explain ::config config)
        false)))

(defn convert-component-config [{:keys [state data methods computed props]
                                :or {state {}
                                     data (fn [] {})
                                     methods {}
                                     computed {}
                                     props []}
                                :as config}]
  {:pre [(validate-config config)]}
  (-> config
      (dissoc :state)
      (assoc :data     (convert-data data)
             :methods  (convert-methods methods state)
             :computed (merge (generate-comp-properties-for-state state)
                              (convert-computed-props computed state))
             :props    (convert-props props))))

(defn config->vue [config]
  (clj->js (convert-component-config config)))

(defn emit [this label & args]
  (apply js-invoke this "$emit" (name label) args))

(defn defcomponent [n config]
  (js/Vue.component (name n) (config->vue config)))

(defn vue [config]
  (js/Vue. (clj->js config)))

(defn deffilter [n f]
  (js/Vue.filter (name n) f))
