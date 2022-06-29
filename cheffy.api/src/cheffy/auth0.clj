(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-test-token
  []
  (->> {:content-type  :json
        :cookie-policy :standard
        :body          (m/encode "application/json"
                                 {:client_id  "gUSjqIkdJVSyCkBgAg3Nmxqq2c5U5Ark"
                                  :audience   "https://dev-fansjs74.us.auth0.com/api/v2/"
                                  :grant_type "password"
                                  :username   "testing@cheffy.app"
                                  :password   "Testing#00"
                                  :scope      "openid profile email"})}
       (http/post "https://dev-fansjs74.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))

(comment
  (get-test-token))