(ns atol.routes.owner
  (:require
    [atol.layout :as layout]
    [atol.middleware :as middleware]
    [atol.services.owner :as so]))

(defn owner-init-page [request]
  (layout/render request "owner.html"))

(defn form-param-value [form-params param]
  (get form-params param))

(defn extract-form-param [form-params]
  (let [[name email owner-pass owner-pass-confirm]
        (map #(form-param-value form-params %) ["owner-name"
                                                "email"
                                                "owner-pass"
                                                "owner-pass-confirm"])]
    (assoc {} :owner-name name
              :email email
              :owner-pass owner-pass
              :owner-pass-confirm owner-pass-confirm)))

(defn owner-create-handler [request]
  (let [form (extract-form-param (:form-params request))
        validate-form (so/valid-owner? form)
        validation-errors (first validate-form)]
    (cond
      validation-errors
      (layout/render request "owner.html" {:error validation-errors})
      (not-empty (so/get-owner-by-email (:email form)))
      (layout/render request "owner.html" {:owner-error (str "User '" (:email form) "' already exists")})
      :else (do
              (so/save-new-owner form)
              (layout/render request "login.html" {:message "User registered successfully!"})))))

(comment
  (if validation-errors
    (layout/render request "owner.html" {:error validation-errors})
    (do
      (so/save-new-owner form)
      (layout/render request "login.html" {:message "User registered successfully!"}))))

(defn owner-routes []
  ["/owner"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/init" {:get owner-init-page}]
   ["" {:post owner-create-handler}]])

