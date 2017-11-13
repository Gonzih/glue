(ns glue.core
  (:refer-clojure :exclude [atom])
  (:require [glue.api :as g :refer [atom]]))

(enable-console-print!)

(g/defcomponent
  :todo
  {:template "#todo"
   :state (fn [] {:todos   (atom ["hello"])
                  :counter (atom 0)})
   :methods {:add-random-todo (fn [this state _]
                                (swap! (:todos state) #(conj % (str (rand-int 100)))))
             :child-clicked (fn [this state n]
                              (swap! (:counter state) inc))}})

(g/defcomponent
  :todo-item
  {:template "#todo-item"
   :props [:label]
   :state (fn [] {:counter (atom 0)})
   :computed {:counter-label (fn [this state]
                               (str @(:counter state) " clicks"))}
   :methods {:click-me (fn [this state _]
                         (println "Click happened on" (g/prop this :label))
                         (swap! (:counter state) inc)
                         (g/emit this :todo-click 1))}})

(defonce app (g/vue {:el "#app"}))

(defn on-js-reload []
  (g/reset-state!))
