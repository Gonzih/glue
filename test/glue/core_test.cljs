(ns ^:figwheel-always glue.core-test
  (:require [cljs.test :refer-macros [run-all-tests]]
            [glue.api-test]
            [glue.gatom-test]))

(defn exit! [code]
  (when js/phantom (.exit js/phantom code)))

(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
  (if (cljs.test/successful? m)
    (exit! 0)
    (exit! 1)))

(enable-console-print!)

(defn ^:export run []
  (run-all-tests))

(run)
