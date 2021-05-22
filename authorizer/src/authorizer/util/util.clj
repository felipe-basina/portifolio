(ns authorizer.util.util
  (:require [clojure.data.json :as json]
            [clj-time.local :as l]))

(defn convert-to-map
  "Converts from a string like {\"account\": {\"active-card\": true, \"available-limit\": 100}}
  into a map like {:account {:active-card true, :available-limit 100}}"
  [input]
  (json/read-str input :key-fn keyword))

(defn convert-to-json
  "Converts from a map like {:account {:active-card true, :available-limit 100}}
  into a json like {\"account\": {\"active-card\": true, \"available-limit\": 100}}"
  [model]
  (json/write-str model))

(defn convert-str-datetime
  "Converts from a datetime string like \"2019-02-13T10:00:00.000Z\"
  into a clojure date-time like #clj-time/date-time\"2019-02-13T10:00:00.000Z\""
  [datetime]
  (l/to-local-date-time datetime))

(defn convert-to-output-model
  "Converts from a map like {:active true, :available-limit 100 :violations []}
  into a map like {:account {:active-card true, :available-limit 100}, :violations []}"
  [model]
  (assoc {} :account {:active-card     (if (contains? model :active) (:active model) false)
                      :available-limit (if (contains? model :available-limit) (:available-limit model) 0)}
            :violations (:violations model)))