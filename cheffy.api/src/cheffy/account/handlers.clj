(ns cheffy.account.handlers
  (:require [ring.util.response :as rr]
            [clj-http.client :as http]
            [cheffy.account.db :as account-db]
            [cheffy.auth0 :as auth0]
            [muuntaja.core :as m]))

(defn create-account!
  [db]
  (fn [request]
    (let [{:keys [sub name picture]} (-> request :claims)]
      (account-db/create-account! db {:uid     sub
                                      :name    name
                                      :picture picture})
      (rr/status 201))))

(defn update-role-to-cook!
  []
  (fn [request]
    (let [uid (-> request :claims :sub)
          token (auth0/get-management-token)]
      (->> {:headers          {"Authorization" (str "Bearer " token)}
            :cookie-policy    :standard
            :content-type     :json
            :throw-exceptions false
            :body             (m/encode
                                "application/json"
                                {:roles [(auth0/get-role-id token)]})}
           (http/post (str "https://dev-fansjs74.us.auth0.com/api/v2/users/" uid "/roles"))))))

(defn delete-account!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          delete-auth0-account! (http/delete
                                  (str "https://dev-fansjs74.us.auth0.com/api/v2/users/" uid)
                                  {:headers {"Authorization" (str "Bearer " (auth0/get-management-token))}})]
      (when (= 204 (:status delete-auth0-account!))
        (account-db/delete-account! db {:uid uid})
        (rr/status 204)))))