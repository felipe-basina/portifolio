(ns atol.services.login
  (:require [buddy.hashers :as hashers]
            [struct.core :as st]))

(def login-schema
  [[:username st/required st/email]
   [:password st/required st/string]])

(defn valid-pass? [raw-pass owner]
  (hashers/check raw-pass (:owner_pass (first owner))))

(defn valid-login? [login]
  (st/validate login login-schema))