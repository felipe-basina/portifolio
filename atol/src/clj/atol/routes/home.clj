(ns atol.routes.home
  (:require
    [atol.layout :as layout]
    [clojure.java.io :as io]
    [atol.middleware :as middleware]
    [atol.services.sample :as ss]
    [ring.util.response]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn test-page [request]
  (ss/create-sample! {:description "Sample from app"})
  (layout/render request "test.html" {:samples (ss/get-samples)}))

(defn say-hi [request]
  (println "request full\n" request)
  (let [someone (get (:form-params request) "name")
        someone (if (not-empty someone) someone "there!")]
    (layout/render request "test.html" {:name someone :samples (ss/get-samples)})))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats
                 middleware/wrap-restricted]}
   ;["/" {:get home-page}]
   ["/about" {:get about-page}]
   ["/test" {:get test-page}]
   ["/sayHi" {:post say-hi}]])

