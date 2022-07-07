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

(defn get-role-id
  [token]
  (->> {:headers          {"Authorization" (str "Bearer " token)}
        :throw-exceptions false                             ; To show errors in a body response fashion
        :content-type     :json
        :cookie-policy    :standard}
       (http/get "https://dev-fansjs74.us.auth0.com/api/v2/roles")
       (m/decode-response-body)
       (filter #(= (:name %1) "manage-recipes"))
       (reduce +)
       :id))

(comment
  (let [token (get-management-token)
        role-id (get-role-id token)
        uid "auth0|62bcacf353cb0e073428f840"]
    (->> {:headers          {"Authorization" (str "Bearer " token)}
          :cookie-policy    :standard
          :content-type     :json
          :throw-exceptions false
          #_#_:body             (m/encode
                              "application/json"
                              {:roles [role-id]})}
         (http/get (str "https://dev-fansjs74.us.auth0.com/api/v2/users/" uid "/roles"))))

  (get-test-token))