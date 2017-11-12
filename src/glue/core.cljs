(ns glue.core
    (:require [glue.api :as glue]))

(enable-console-print!)

(def counter (glue/atom 0))
(def todos (glue/atom ["hello"]))

(glue/defcomponent
  :todo
  {:template "#todo"
   :state {:todos (glue/atom ["hello"])
           :counter (glue/atom 0)}
   :methods {:add-random-todo (fn [this state _]
                                (swap! (:todos state) #(conj % (str (rand-int 100)))))
             :child-clicked (fn [this state n]
                              (swap! (:counter state) inc))}})

(glue/defcomponent
  :todo-item
  {:template "#todo-item"
   :props [:label]
   :state {:counter (glue/atom 0)}
   :computed {:counter-label (fn [this state] (str @(:counter state)
                                                   " clicks"))}
   :methods {:click-me (fn [this state _]
                         (swap! (:counter state) inc)
                         (glue/emit this :todo-click 1))}})

(defn new-app [] (glue/vue {:el "#app"}))

(def app (new-app))

(defn on-js-reload [])
