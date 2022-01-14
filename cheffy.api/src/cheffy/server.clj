(ns cheffy.server
  (:require [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [environ.core :refer [env]]
            [cheffy.router :as router]))

(defn app
  [env]
  (router/routes env))

;------------------------------------------------------------------;
; INIT                                                             ;
; Settings to integrate the integrant component (app auto reload)  ;
;------------------------------------------------------------------;
(defmethod ig/prep-key :server/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :db/postgres
  [_ config]
  (merge config {:jdbc-url (env :jdbc-database-url)}))

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]                                ; Equivalent to {:server/jetty {:handler (ig/ref :cheffy/app) :port 3000}
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))      ; join? false = do not block thread

(defmethod ig/init-key :cheffy/app
  [_ config]
  (println "\nStarted app")
  (app config))

(defmethod ig/init-key :db/postgres
  [_ config]
  (println "\nConfigured db")
  (:jdbc-url config))

(defmethod ig/halt-key! :server/jetty
  [_ jetty]
  (.stop jetty))
;------------------------------------------------------------------;
; END                                                              ;
;------------------------------------------------------------------;

(defn -main
  [config-file]
  (let [config (-> config-file
                   slurp
                   ig/read-string)]
    (-> config
        ig/prep
        ig/init)))

(comment
  (app {:request-method :get
        :uri            "/"})
  (-main))