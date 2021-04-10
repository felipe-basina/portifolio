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

(defn contact-routes []
  ["/contact"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats
                 middleware/wrap-restricted]}
   ["/list" {:get contact-init-page}]])

