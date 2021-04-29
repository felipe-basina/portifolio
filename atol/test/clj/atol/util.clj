(ns atol.util
  (:require [clojure.test :refer :all]))

(defn validate-domain [validation-errors key message]
  (is (not (nil? (first validation-errors))))
  (is (contains? (first validation-errors) key))
  (is (= (key (first validation-errors)) message)))