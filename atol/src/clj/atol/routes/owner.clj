(ns atol.routes.owner
  (:require
    [atol.layout :as layout]
    [atol.middleware :as middleware]
    [atol.services.owner :as so]))

(defn owner-init-page [request]
  (layout/render request "owner.html"))

(defn extract-form-param [form-params]
  (let [name (get form-params "owner-name")
        email (get form-params "email")
        owner-pass (get form-params "owner-pass")
        owner-pass-confirm (get form-params "owner-pass-confirm")]
    (assoc {} :owner-name name
              :email email
              :owner-pass owner-pass
              :owner-pass-confirm owner-pass-confirm)))

(defn owner-create-handler [request]
  (let [form (extract-form-param (:form-params request))
        validate-form (so/valid-owner? form)
        validation-errors (first validate-form)]            ; add validation for existing email!
    (if validation-errors
      (layout/render request "owner.html" {:error validation-errors})
      (do
        (so/save-new-owner form)
        (layout/render request "login.html" {:message "User registered successfully!"})))))

(defn owner-routes []
  ["/owner"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/init" {:get owner-init-page}]
   ["" {:post owner-create-handler}]])

