(ns atol.routes.login
  (:require
    [atol.layout :as layout]
    [clojure.java.io :as io]
    [atol.middleware :as middleware]
    [atol.services.sample :as ss]
    [ring.util.response :refer [response redirect content-type]]
    [buddy.hashers :as hashers]))

(defn init-page [request]
  (layout/render request "login.html"))

(def user {:id "bob" :pass "bcrypt+sha512$6a97cbaa33d1476b8342d4f7146fc8a8$12$9e2366ead1b45135f12ccd4674f8b907bf6fb2e84a6de6d3"})

(comment
  (defn login-handler [{:keys [params session] :as request}]
  (let [user-req (assoc {} :id (:id params)
                           :pass (:pass params))]
    (if (= user user-req)
      (do
        (assoc response :session (assoc session :identity "foo"))
        (layout/render request "test.html" {:name "Ok login!"}))
      (layout/render request "login.html" {:error "Invalid user/password"})))))

(defn login-handler
  "Check request username and password against authdata
  username and passwords.
  On successful authentication, set appropriate user
  into the session and redirect to the value of
  (:next (:query-params request)). On failed
  authentication, renders the login page."
  [request]
  (println (get-in request [:form-params "id"]))
  (println (get-in request [:form-params "pass"]))
  (let [username (get-in request [:form-params "id"])
        password (get-in request [:form-params "pass"])
        session (:session request)
        found-password (get user (keyword "pass"))]
    (if (and found-password (hashers/check password found-password))
      (let [next-url (get-in request [:query-params :next] "/test")
            updated-session (assoc session :identity (keyword username))]
        (-> (redirect next-url)
            (assoc :session updated-session)))
      (layout/render request "login.html" {:error "Invalid user/password"}))))

(defn logout-handler
  [request]
  (-> (redirect "/")
      (assoc :session {})))

(defn login-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get init-page}]
   ["/login" {:post login-handler}]
   ["/logout" {:get logout-handler}]])

