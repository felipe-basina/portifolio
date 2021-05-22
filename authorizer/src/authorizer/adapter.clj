(ns authorizer.adapter
  (:require [authorizer.port :as port]
            [clojure.string :as str])
  (:import (java.io BufferedReader)))

(defn get-content-from-stdin
  "Reads input data from stdin
  and returns a vector with each of its lines"
  []
  (str/split (slurp (BufferedReader. *in*)) #"\n"))

(defn process-input
  "For each line from the stdin invokes the port and then
  prints out the response into the stdout.
  The param 'configs' provides configuration to run application
  accordingly to the environment"
  [configs]
  (loop [inputs (get-content-from-stdin)]
    (when (not-empty inputs)
      (let [input (first inputs)]
        (println (port/authorize input (:storage configs)))
        (recur (rest inputs))))))