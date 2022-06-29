(ns cheffy.middleware
  (:require [ring.middleware.jwt :as jwt]))

(def wrap-auth0
  {:name        ::auth0                                     ; Equivalent to cheffy.middleware/auth0
   :description "Middleware for auth0 authentication and authorization"
   :wrap        (fn [handler]
                  (jwt/wrap-jwt
                    handler
                    {:alg          :RS256
                     :jwk-endpoint "https://dev-fansjs74.us.auth0.com/.well-known/jwks.json"}))})
