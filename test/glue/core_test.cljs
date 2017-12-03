(ns ^:figwheel-always glue.core-test
  (:require [cljs.test :refer-macros [run-all-tests]]
            [glue.gatom-test]))

(enable-console-print!)

(defn ^:export run []
  (run-all-tests))

(run)
