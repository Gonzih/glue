(ns glue.core
    (:require [glue.api :as glue]))

(enable-console-print!)

(glue/defcomponent
  :todo
  {:template "#todo"
   :data (fn [] {:todos []
                 :counter 0})
   :methods {:add-random-todo (fn [this _] (glue/jsupdate this :todos #(conj % (str (rand-int 100)))))
             :child-clicked (fn [this n] (glue/jsupdate-raw this :counter #(+ n %)))}})

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
