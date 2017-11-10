(ns glue.api
    (:require [cljsjs.vue]
              [clojure.string :as string]))

(defn trace [i]
  (prn i)
  i)

(defn kebab->camel [s]
  (string/replace
    s
    #"-(\w)"
    (fn [matches] (string/upper-case (second matches)))))

(defn adjust-name [k]
  (kebab->camel (name k)))

(defn adjust-data [data-fn]
  (comp clj->js data-fn))

(defn adjust-method [method-fn]
  (fn [& args] (this-as dis (apply method-fn dis args))))

(defn adjust-computed-prop [prop-fn]
  (fn [] (this-as dis (clj->js (prop-fn dis)))))

(defn adjust-methods [methods]
  (into {} (map (fn [[k v]] [(adjust-name k) (adjust-method v)]) methods)))

(defn adjust-computed-props [computed-props]
  (into {} (map (fn [[k v]] [(adjust-name k) (adjust-computed-prop v)]) computed-props)))

(defn adjust-props [props]
  (map name props))

(defn adjust-component-config [config]
  (-> config
      (update :data adjust-data)
      (update :methods adjust-methods)
      (update :computed adjust-computed-props)
      (update :props adjust-props)
      clj->js))

(defn jsget [target & keys]
  (apply aget target (map name keys)))

(defn jsset [target & keys]
  (let [value (last keys)
        kx (->> keys drop-last (map name))]
    (apply aset target (concat kx [value]))))

(defn jsupdate-raw [& args]
  (let [f (last args)
        get-args (drop-last args)
        current-value (apply jsget get-args)
        new-value (f current-value)
        set-args (concat get-args [new-value])]
    (apply jsset set-args)))

(defn jsupdate [& args]
  (let [f (last args)
        conv-f (fn [input] (clj->js (f (js->clj input))))
        keys (drop-last args)]
    (apply jsupdate-raw (concat keys [conv-f]))))

(defn emit [this label & args]
  (apply js-invoke this "$emit" (name label) args))

(defn defcomponent [n config]
  (js/Vue.component (name n) (adjust-component-config config)))

(defn vue [config]
  (js/Vue. (clj->js config)))

(defn deffilter [n f]
  (js/Vue.filter (name n) f))
