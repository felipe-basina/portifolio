(ns authorizer.integration-test.core-integration-test
  (:require [authorizer.core :as core]
            [authorizer.adapter :as adapter]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.test :refer :all]))

(deftest test-start-application
  (testing "integration test : start running application"
    (with-redefs [adapter/get-content-from-stdin (fn [] (str/split (slurp (io/resource "operations")) #"\n"))]
      (let [response (core/-main)]
        (is (nil? response))))))