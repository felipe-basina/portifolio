(ns authorizer.integration-test.adapter-integration-test
  (:require [authorizer.adapter :refer :all]
            [authorizer.storage.storage-client :as storage-client]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.test :refer :all]))

(deftest test-process-input
  (testing "integration test : process input with resources/operations"
    (with-redefs [get-content-from-stdin (fn [] (str/split (slurp (io/resource "operations")) #"\n"))]
      (let [configs {:storage (storage-client/storage-client)}]
        (is (nil? (process-input configs)))))))

(deftest test-get-content-from-stdin
  (testing "integration test : get content from stdin (file)"
    (with-redefs [get-content-from-stdin (fn [] (str/split (slurp (io/resource "operations")) #"\n"))]
      (let [input (get-content-from-stdin)]
        (is (not-empty input))
        (is (vector? input))
        (is (= 10 (count input)))
        (is (str/includes? input "account"))
        (is (str/includes? input "transaction"))))))