(ns atol.routes.contact
  (:require
    [atol.layout :as layout]
    [atol.middleware :as middleware]
    [atol.services.contact :as sc]))

(defn contact-init-page [request]
  (let [session (:session request)
        owner_id (:identity session)
        contacts (sc/get-contacts-by-owner-id owner_id)]
    (layout/render request "contacts/list.html" {:contacts contacts})))

(defn contact-add-handler [request]
  (layout/render request "contacts/create.html"))

(defn extract-form-param [form-params]
  (let [[name email phone] (map #(get form-params %) ["contact-name"
                                                      "contact-email"
                                                      "contact-phone"])]
    (assoc {} :contact-name name
              :contact-email email
              :contact-phone phone)))

(defn contact-create-handler
  [request]
  (let [form (extract-form-param (:form-params request))
        validate-form (sc/valid-contact? form)
        validation-errors (first validate-form)
        session (:session request)
        owner_id (:identity session)]
    (cond
      validation-errors
      (layout/render request "contacts/create.html" {:error validation-errors})
      (nil? (sc/valid-phone? form))
      (layout/render request "contacts/create.html" {:error (assoc {} :contact-phone "Should be a valid phone number!")})
      :else (do
              (sc/create-contact! form owner_id)
              (layout/render request "contacts/create.html" {:message "Contact created successfully!"})))))

(defn contact-routes []
  ["/contact"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats
                 middleware/wrap-restricted]}
   ["/list" {:get contact-init-page}]
   ["/add" {:get contact-add-handler}]
   ["/create" {:post contact-create-handler}]])

