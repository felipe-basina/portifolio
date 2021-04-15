(ns atol.routes.contact
  (:require
    [atol.layout :as layout]
    [atol.middleware :as middleware]
    [atol.services.contact :as sc]))

(defn get-contacts-by-owner [request]
  (let [session (:session request)
        owner_id (:identity session)]
    (sc/get-contacts-by-owner-id owner_id)))

(defn contact-init-page [request]
  (layout/render request "contacts/list.html" {:contacts (get-contacts-by-owner request)}))

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

(defn get-id-from-params [path-params]
  (try
    (Integer/parseInt (:idt path-params))
    (catch Exception _ 0)))

(defn get-for-update-handler [request]
  (let [session (:session request)
        owner_id (:identity session)
        contact_id (get-id-from-params (:path-params request))
        contact (sc/get-contact-by-id contact_id owner_id)]
    (if contact
      (layout/render request "contacts/create.html" {:contact contact})
      (layout/render request "contacts/create.html" {:error (str "Contact not found")}))))

(defn contact-update-handler [request]
  (let [contact-id (Integer/parseInt (get (:form-params request) "idt"))
        form (extract-form-param (:form-params request))
        validate-form (sc/valid-contact? form)
        validation-errors (first validate-form)]
    (cond
      validation-errors
      (layout/render request "contacts/create.html" {:error validation-errors :contact (:form-params request)})
      (nil? (sc/valid-phone? form))
      (layout/render request "contacts/create.html" {:error (assoc {} :contact-phone "Should be a valid phone number!")
                                                     :contact (:form-params request)})
      :else (do
              (sc/update-contact! (assoc form :idt contact-id))
              (layout/render request "contacts/list.html" {:message "Contact updated successfully!"
                                                           :contacts (get-contacts-by-owner request)})))))

(defn contact-routes []
  ["/contact"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats
                 middleware/wrap-restricted]}
   ["/list" {:get contact-init-page}]
   ["/add" {:get contact-add-handler}]
   ["/create" {:post contact-create-handler}]
   ["/update/:idt" {:get get-for-update-handler}]
   ["/update" {:post contact-update-handler}]])

