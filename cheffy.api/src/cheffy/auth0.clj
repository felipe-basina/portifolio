(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

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
  (create-auth0-user {:connection "Username-Password-Authentication"
                      :email      "account-tests@cheffy.app"
                      :password   "s#m3R4nd0m-pass"})

  (->> {:headers          {"Authorization" (str "Bearer " (get-management-token))}
        :throw-exceptions false                             ; To show errors in a body response fashion
        :content-type     :json
        :cookie-policy    :standard}
       (http/get "https://dev-fansjs74.us.auth0.com/api/v2/users/auth0|62c73facfecd320827fc8a58")
       (m/decode-response-body))

  (->> {:headers          {"Authorization" (str "Bearer " (get-management-token))}
        :throw-exceptions false                             ; To show errors in a body response fashion
        :content-type     :json
        :cookie-policy    :standard}
       (http/get "https://dev-fansjs74.us.auth0.com/api/v2/users")
       (m/decode-response-body))

  (->> {:headers          {"Authorization" (str "Bearer " (get-management-token))}
        :throw-exceptions false                             ; To show errors in a body response fashion
        :content-type     :json
        :cookie-policy    :standard}
       (http/delete "https://dev-fansjs74.us.auth0.com/api/v2/users/auth0|62c74318fecd320827fc8afb"))

  (->> {:headers          {"Authorization" (str "Bearer " (get-management-token))}
        :throw-exceptions false                             ; To show errors in a body response fashion
        :content-type     :json
        :cookie-policy    :standard}
       (http/get "https://dev-fansjs74.us.auth0.com/api/v2/roles")
       (m/decode-response-body))

  (let [token (get-management-token)
        role-id (get-role-id token)
        uid "auth0|62bcacf353cb0e073428f840"]
    (->> {:headers          {"Authorization" (str "Bearer " token)}
          :cookie-policy    :standard
          :content-type     :json
          :throw-exceptions false
          #_#_:body (m/encode
                      "application/json"
                      {:roles [role-id]})}
         (http/get (str "https://dev-fansjs74.us.auth0.com/api/v2/users/" uid "/roles"))))

  (get-test-token "testing@cheffy.app")
  (get-role-id "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik9OdTltRmE3RzdpTTV5WGt5bG44byJ9.eyJpc3MiOiJodHRwczovL2Rldi1mYW5zanM3NC51cy5hdXRoMC5jb20vIiwic3ViIjoiM2Q1d3JhNHAyYWlzb054S0pMSUFTajZodVZKSkVJcDZAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vZGV2LWZhbnNqczc0LnVzLmF1dGgwLmNvbS9hcGkvdjIvIiwiaWF0IjoxNjU3MjI5MTk1LCJleHAiOjE2NTczMTU1OTUsImF6cCI6IjNkNXdyYTRwMmFpc29OeEtKTElBU2o2aHVWSkpFSXA2Iiwic2NvcGUiOiJyZWFkOnVzZXJzIHVwZGF0ZTp1c2VycyBkZWxldGU6dXNlcnMgY3JlYXRlOnVzZXJzIHJlYWQ6cm9sZXMgY3JlYXRlOnJvbGVfbWVtYmVycyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.N9ythhD1vCppQtgb4mX8HdzBiA1kiHXIb3ncg8OfW1X99m2mcQSwhZS0bBNU7i2Q3ps_qpzlQenPF3ccRHFf5SR5d17uidO52jhdYu-lvPRYgD8EN_2QawKIrsZ8b2lz3fxUH4vEw59R7Y3s2dcfJM0rhIbY3axCiFjwO8AG8NkLBmfOnNz3K7Q2V5z4WIZ3ycej30vuiN6KgQPcF_d97qp66TD4e2dhAbcIzOY9rkJU1avM4g6mvwvkfsPvudOBbIYJ4VmwKdP3hEZMJ6G_uyhFXnirV3keRoVWqqdmvPhD_EIhJJTYdVfLAp11SAr1JBwoFD_0hufB3SH9GqMRdQ")
  (get-test-token "account-tests@cheffy.app"))