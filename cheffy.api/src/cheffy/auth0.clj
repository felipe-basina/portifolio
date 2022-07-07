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

(defn get-management-token
  []
  (->> {:throw-exceptions false                             ; To show errors in a body response fashion
        :content-type     :json
        :cookie-policy    :standard
        :body             (m/encode "application/json"
                                    {:client_id     "3d5wra4p2aisoNxKJLIASj6huVJJEIp6"
                                     :client_secret "LBlgmhL8VrEhwhESwYWp9ySs2Hi6dvppb3VUYtIITkcbnxJMBnDPo9QsnTK6JGaJ"
                                     :audience      "https://dev-fansjs74.us.auth0.com/api/v2/"
                                     :grant_type    "client_credentials"})}
       (http/post "https://dev-fansjs74.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))

(comment
  (get-management-token)
  (get-test-token))