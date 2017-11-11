(ns ^:figwheel-always glue.core-test
  (:require [cljs.test :refer-macros [run-all-tests]]
            [glue.api-test]
            [glue.gatom-test]))

(enable-console-print!)
(run-all-tests)
