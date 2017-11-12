(ns glue.api
  (:refer-clojure :exclude [atom])
  (:require-macros [cljs.core :refer [exists?]])
  (:require [clojure.spec.alpha :as s]
            [clojure.string     :as string]
            [cljsjs.vue]
            [glue.gatom         :as gatom]))

(defn trace [arg] (prn arg) arg)

(s/def ::template string?)
(s/def ::gatom #(instance? gatom/GAtom %))
(s/def ::state-map (s/map-of keyword? ::gatom))
(s/def ::state fn?)
(s/def ::props (s/* keyword?))
(s/def ::methods (s/map-of keyword? fn?))
(s/def ::computed (s/map-of keyword? fn?))
(s/def ::data fn?)
(s/def ::config (s/keys :req-un [::template]
                        :opt-un [::props ::state ::computed ::methods ::data]))

(defn valid-or-explain [spec item]
  (if (s/valid? spec item)
    true
    (do (s/explain spec item)
        false)))

(def atom gatom/atom)
(def components-global-state (clojure.core/atom {}))

(defn reset-state! [] (reset! components-global-state {}))

(defn state-for [init-fn this]
  (if (and (exists? this)
           (exists? (.-_uid this)))
    (let [id (.-_uid this)
          state (get @components-global-state id)]
      (if state
        state
        (let [initial-state (init-fn)]
          (swap! components-global-state assoc id initial-state)
          initial-state)))))

(defn kebab->camel [s]
  (string/replace
    s
    #"-(\w)"
    (fn [matches] (string/upper-case (second matches)))))

(defn convert-name [k]
  (kebab->camel (name k)))

(defn convert-data [data-fn]
  (comp clj->js data-fn))

(defn convert-method [method-fn state-fn]
  (fn [& args] (this-as this (apply method-fn this (state-for state-fn this) args))))

(defn convert-computed-prop [prop-fn state-fn]
  (fn [] (this-as this (clj->js (prop-fn this (state-for state-fn this))))))

(defn convert-methods [methods state-fn]
  (into {}
        (map (fn [[k v]] [(convert-name k) (convert-method v state-fn)])
             methods)))

(defn convert-computed-props [computed-props state-fn]
  (into {}
        (map (fn [[k v]] [(convert-name k) (convert-computed-prop v state-fn)])
             computed-props)))

(defn convert-props [props]
  (map convert-name props))

(defn generate-comp-properties-for-state [state-fn]
  (let [kx (keys (state-fn))]
    (into {}
          (map (fn [k]
                 [(convert-name k)
                  (fn [] (this-as
                           this
                           (-> (state-for state-fn this)
                               (get k)
                               deref
                               clj->js)))])
               kx))))

(defn convert-component-config [{:keys [state data methods computed props]
                                :or {state {}
                                     data (fn [] {})
                                     methods {}
                                     computed {}
                                     props []}
                                :as config}]
  {:pre [(valid-or-explain ::config config)]}
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
