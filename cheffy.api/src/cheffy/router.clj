(ns cheffy.router
  (:require [reitit.ring :as ring]
            [cheffy.account.routes :as account]
            [cheffy.recipe.routes :as recipe]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.spec :as rs]))

(def swagger-docs
  ["/swagger.json"
   {:get
    {:no-doc  true
     :swagger {:basePath "/"
               :info     {:title       "Cheffy API Reference"
                          :description "The Cheffy API is organized around rest. Returns JSON, Transit (msgpack, json), or EDN encoded responses."
                          :version     "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])

;;-----------------------------------------------
;; All middleware defined below will be global
;;-----------------------------------------------
(def router-config
  {:validate  rs/validate                                   ; Checks if handlers in routes are valid functions and not strings
   :exception pretty/exception
   :data      {:coercion   coercion-spec/coercion
               :muuntaja   m/instance                       ;; This with the middleware below will allow content negotiation (conversion): e.g. from bytearray to json
               :middleware [swagger/swagger-feature
                            muuntaja/format-middleware
                            exception/exception-middleware  ;; This will send http 400 when there is an issue with parameters
                            coercion/coerce-request-middleware ;; This will convert request parameters to the values defined in :parameters {:path {:recipe-id int?}} (recipe.routes)
                            coercion/coerce-response-middleware]}}
  )

(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [swagger-docs
       ["/v1"
        (recipe/routes env)
        (account/routes env)]]
      router-config)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"}))))