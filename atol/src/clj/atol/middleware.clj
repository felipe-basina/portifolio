(ns atol.middleware
  (:require
    [atol.env :refer [defaults]]
    [clojure.tools.logging :as log]
    [atol.layout :refer [error-page]]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [atol.middleware.formats :as formats]
    [muuntaja.middleware :refer [wrap-format wrap-params]]
    [atol.config :refer [env]]
    [ring.middleware.flash :refer [wrap-flash]]
    [ring.adapter.undertow.middleware.session :refer [wrap-session]]
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
    [buddy.auth.accessrules :refer [restrict wrap-access-rules]]
    [buddy.auth :refer [authenticated?]]
    [buddy.auth.backends.session :refer [session-backend]]
    [ring.util.response :refer [response redirect content-type]]
    ;[ring.middleware.session :refer [wrap-session]]
    ;[ring.middleware.params :refer [wrap-params]]
    [clojure.java.io :as io]
    [atol.layout :as layout])
  )

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status  500
                     :title   "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title  "Invalid anti-forgery token"})}))

(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn on-error [request response]
  (comment (error-page
    {:status 403
     :title  (str "Access to " (:uri request) " is not authorized")}))
  (layout/render request "login.html" {:error "Permission denied. You must login!"}))

(defn wrap-restricted [handler]
  (println "authenticated?" authenticated?)
  (restrict handler {:handler  authenticated?
                     :on-error on-error}))

(def rules
  [{:uri "/restricted"
    :handler authenticated?}])

(defn wrap-auth [handler]
  (let [backend (session-backend)]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend))))

(defn unauthorized-handler
  [request metadata]
  (cond
    ;; If request is authenticated, raise 403 instead
    ;; of 401 (because user is authenticated but permission
    ;; denied is raised).
    (authenticated? request)
    (-> (layout/render request "error.html")
        (assoc :status 403))
    ;; In other cases, redirect the user to login page.
    :else
    (let [current-url (:uri request)]
      (redirect (format "/login?next=%s" current-url)))))

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      ;(wrap-access-rules {:rules rules :on-error on-error})
      ;wrap-auth
      ;(wrap-authentication (session-backend))
      (wrap-authorization auth-backend)
      (wrap-authentication auth-backend)
      wrap-params
      wrap-flash
      (wrap-session {:cookie-attrs {:http-only true}})
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)))
      wrap-internal-error))
