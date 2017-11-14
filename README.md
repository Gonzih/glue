# glue

Trying to glue cljs and vue together.

## Overview

So far main idea is just to configure components via clojurescript.
This is a small demo with figwheel so far.
Might release a library later.

## Example

```html
    <template id="sample-template">
        <div>
            <span>{{ label }} | </span>
            <span>{{ counterLabel }}</span>
            <button @click="clickMe">Click</button>
            <hr/>
        </div>
    </template>

    <div id="app">
        <sample-template label="sample button"></sample-template>
    </div>
```

```clojure
(ns app.core
  (:refer-clojure :exclude [atom])
  (:require [glue.api :as g :refer [atom]]))

(g/defcomponent
  :samle-component
  {:template "#sample-template"
   :props [:label]
   :state (fn [] {:counter (atom 0)})
   :computed {:counter-label (fn [this state]
                               (str @(:counter state) " clicks"))}
   :methods {:click-me (fn [this state _]
                         (println "Click happened on" (g/prop this :label))
                         (swap! (:counter state) inc))}})

(g/vue {:el "#app"})
```

## Figwheel setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).

## License

Copyright © 2017 Max Gonzih gonzih @ gmail.com

Distributed under the MIT license.
