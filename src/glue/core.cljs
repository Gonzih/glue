(ns glue.core
    (:require [glue.api :as g]))

(enable-console-print!)

(def counter (g/atom 0))
(def todos (g/atom ["hello"]))

(g/defcomponent
  :todo
  {:template "#todo"
   :state (fn [] {:todos (g/atom ["hello"])
                  :counter (g/atom 0)})
   :methods {:add-random-todo (fn [this state _]
                                (swap! (:todos state) #(conj % (str (rand-int 100)))))
             :child-clicked (fn [this state n]
                              (swap! (:counter state) inc))}})

(g/defcomponent
  :todo-item
  {:template "#todo-item"
   :props [:label]
   :state (fn [] {:counter (g/atom 0)})
   :computed {:counter-label (fn [this state]
                               (str @(:counter state) " clicks"))}
   :methods {:click-me (fn [this state _]
                         (prn (g/prop this :label))
                         (swap! (:counter state) inc)
                         (g/emit this :todo-click 1))}})

(defonce app (g/vue {:el "#app"}))

(defn on-js-reload []
  (g/reset-state!))
