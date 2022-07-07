(ns cheffy.test-system
  (:require [clojure.test :refer :all]
            [integrant.repl.state :as state]
            [muuntaja.core :as m]
            [ring.mock.request :as mock]
            [cheffy.auth0 :as auth0]
            [clj-http.client :as http]))

(defn get-test-token
  [email]
  (->> {:content-type  :json
        :cookie-policy :standard
        :body          (m/encode "application/json"
                                 {:client_id  "gUSjqIkdJVSyCkBgAg3Nmxqq2c5U5Ark"
                                  :audience   "https://dev-fansjs74.us.auth0.com/api/v2/"
                                  :grant_type "password"
                                  :username   email
                                  :password   "Testing#00"
                                  :scope      "openid profile email"})}
       (http/post "https://dev-fansjs74.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))

(defn create-auth0-user
  [{:keys [connection email password]}]
  (->> {:headers          {"Authorization" (str "Bearer " (auth0/get-management-token))}
        :throw-exceptions false                             ; To show errors in a body response fashion
        :content-type     :json
        :cookie-policy    :standard
        :body             (m/encode "application/json" {:connection connection
                                                        :email      email
                                                        :password   password})}
       (http/post "https://dev-fansjs74.us.auth0.com/api/v2/users")
       (m/decode-response-body)))

(def token (atom nil))

(defn test-endpoint
  ([method uri]
   (test-endpoint method uri nil))
  ([method uri opts]
   (let [app (-> state/system :cheffy/app)
         email "testing@cheffy.app"
         request (app (-> (mock/request method uri)
                          (cond-> (:auth opts) (mock/header :authorization (str "Bearer " (or @token (get-test-token email))))
                                  (:body opts) (mock/json-body (:body opts)))))]
     (update request :body (partial m/decode "application/json"))))) ;; Equivalent to (fn [data] (m/decode "application/json" data))

(comment
  (let [request (test-endpoint :get "/v1/recipes")
        decoded-request (m/decode-response-body request)]   ;; Convert the byte array from the response into a json format
    (assoc request :body decoded-request))
  (test-endpoint :post "/v1/recipes" {:img       "string"
                                      :name      "my name"
                                      :prep-time 30}))