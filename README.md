# glue

[![Downloads](https://jarkeeper.com/Gonzih/glue/downloads.svg)](https://jarkeeper.com/Gonzih/glue)
[![Dependencies Status](http://jarkeeper.com/Gonzih/glue/status.svg)](https://jarkeeper.com/Gonzih/glue)
[![Build Status](https://travis-ci.org/Gonzih/glue.svg?branch=master)](https://travis-ci.org/Gonzih/glue)
[![License](http://img.shields.io/:license-mit-blue.svg)](https://github.com/Gonzih/glue/blob/master/LICENSE.md)
[![Clojars Project](https://img.shields.io/clojars/v/glue.svg)](http://clojars.org/glue)

Trying to glue cljs and vue together.

## Overview

So far main idea is just to configure components via clojurescript.

## Demo

Check out [the glue cljs demo app](https://github.com/Gonzih/glue-demo) for the usage example.

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
  :sample-component
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

## License

Copyright © 2017-2019 Max Gonzih gonzih @ gmail.com

Distributed under the MIT license.
