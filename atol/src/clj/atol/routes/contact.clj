(ns atol.routes.contact
  (:require
    [atol.layout :as layout]
    [atol.middleware :as middleware]
    [atol.services.contact :as sc]
    [atol.tags.contact :all tg]))

(defn owner-id [request]
  (:identity (:session request)))

(defn get-contacts-by-owner [request]
  (sc/get-contacts-by-owner-id (owner-id request)))

(defn contact-init-page [request]
  (layout/render request "contacts/list.html" {:contacts (get-contacts-by-owner request)}))

(defn contact-add-handler [request]
  (layout/render request "contacts/create.html"))

(defn extract-form-param [form-params]
  (let [[name email phone] (map #(get form-params %) ["contact_name"
                                                      "email"
                                                      "phone_number"])]
    (assoc {} :contact_name name
              :email email
              :phone_number phone)))

(defn contact-create-handler
  [request]
  (let [form (extract-form-param (:form-params request))
        validate-form (sc/valid-contact? form)
        validation-errors (first validate-form)
        session (:session request)
        owner_id (:identity session)]
    (cond
      validation-errors
      (layout/render request "contacts/create.html" {:error   validation-errors
                                                     :contact (:form-params request)})
      (nil? (sc/valid-phone? form))
      (layout/render request "contacts/create.html" {:error   (assoc {} :phone_number "Should be a valid phone number!")
                                                     :contact (:form-params request)})
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
      (layout/render request "contacts/create.html" {:contact-error (str "Contact not found")}))))

(defn contact-update-handler [request]
  (let [contact-id (Integer/parseInt (get (:form-params request) "idt"))
        form (extract-form-param (:form-params request))
        validate-form (sc/valid-contact? form)
        validation-errors (first validate-form)]
    (cond
      validation-errors
      (layout/render request "contacts/create.html" {:error validation-errors :contact (:form-params request)})
      (nil? (sc/valid-phone? form))
      (layout/render request "contacts/create.html" {:error   (assoc {} :phone_number "Should be a valid phone number!")
                                                     :contact (:form-params request)})
      :else (do
              (sc/update-contact! (assoc form :idt contact-id))
              (layout/render request "contacts/list.html" {:message  "Contact updated successfully!"
                                                           :contacts (get-contacts-by-owner request)})))))

(defn contact-delete-handler [request]
  (let [session (:session request)
        owner_id (:identity session)
        contact_id (get-id-from-params (:path-params request))
        contact (sc/delete-contact! contact_id owner_id)]
    (if (sc/deleted? contact)
      (layout/render request "contacts/list.html" {:message  "Contact deleted successfully!"
                                                   :contacts (get-contacts-by-owner request)})
      (layout/render request "contacts/list.html" {:contact-error (str "Contact not found")
                                                   :contacts      (get-contacts-by-owner request)}))))

(defn contact-filter-handler [request]
  (let [filter (get (:form-params request) "filter")
        owner_id (owner-id request)]
    (if (not-empty filter)
      (let [filtered-contacts (sc/get-contacts-by-filter filter owner_id)]
        (layout/render request "contacts/list.html" {:contacts filtered-contacts}))
      (layout/render request "contacts/list.html" {:contacts (get-contacts-by-owner request)}))))

(defn contact-routes []
  ["/contact"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats
                 middleware/wrap-restricted]}
   ["/list" {:get contact-init-page}]
   ["/add" {:get contact-add-handler}]
   ["/create" {:post contact-create-handler}]
   ["/update/:idt" {:get get-for-update-handler}]
   ["/update" {:post contact-update-handler}]
   ["/delete/:idt" {:get contact-delete-handler}]
   ["/filter" {:post contact-filter-handler}]])

