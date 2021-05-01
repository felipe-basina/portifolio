(ns atol.util
  (:require [clojure.string :as str]
            [clojure.test :refer :all]))

(defn validate-domain [validation-errors key message]
  (is (not (nil? (first validation-errors))))
  (is (contains? (first validation-errors) key))
  (is (= (key (first validation-errors)) message)))

(defn validate-http-response-commons [response status]
  (is (= status (:status response)))
  (is (contains? response :body))
  (is (contains? response :headers))
  (is (str/includes? (:headers response) "text/html")))