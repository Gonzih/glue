(ns glue.core
    (:require [glue.api :as glue]))

(enable-console-print!)

(def counter (glue/atom 0))
(def todos (glue/atom ["hello"]))

(glue/defcomponent
  :todo
  {:template "#todo"
   :computed {:counter (fn [] @counter)
              :todos (fn [] @todos)}
   :methods {:add-random-todo (fn [this _] (swap! todos #(conj % (str (rand-int 100)))))
             :child-clicked (fn [this n] (swap! counter inc))}})

(glue/defcomponent
  :todo-item
  {:template "#todo-item"
   :props [:label]
   :data (fn [] {:counter 0})
   :computed {:counter-label (fn [this] (str (glue/jsget this :counter)
                                             " clicks"))}
   :methods {:click-me (fn [this _]
                         (glue/jsupdate-raw this :counter inc)
                         (glue/emit this :todo-click 1))}})

(defn new-app [] (glue/vue {:el "#app"}))

(def app (new-app))

(defn on-js-reload [])
