(ns atol.routes.login
  (:require
    [atol.layout :as layout]
    [atol.middleware :as middleware]
    [atol.services.login :as sl]
    [atol.services.owner :as so]
    [ring.util.response :refer [redirect]]))

(defn login-init-page [request]
  (layout/render request "login.html"))

(defn extract-form-param [form-params]
  (let [[username password] (map #(get form-params %) ["id" "pass"])]
    (assoc {} :username username
              :password password)))

(defn login-handler
  [request]
  (let [form (extract-form-param (:form-params request))
        validate-form (sl/valid-login? form)
        validation-errors (first validate-form)
        found-owner (so/get-owner-by-email (:username form))
        session (:session request)]
    (cond
      validation-errors
      (layout/render request "login.html" {:error validation-errors})
      (or (empty? found-owner)
          (not (sl/valid-pass? (:password form) found-owner)))
      (layout/render request "login.html" {:login-error "User/Password invalid!"})
      :else (let [next-url (get-in request [:query-params :next] "/test")
                  updated-session (assoc session :identity (:username form))]
              (-> (redirect next-url)
                  (assoc :session updated-session))))))

(defn logout-handler
  [_]
  (-> (redirect "/")
      (assoc :session {})))

(defn login-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get login-init-page}]
   ["/login" {:post login-handler}]
   ["/logout" {:get logout-handler}]])

