(ns atol.routes.home
  (:require
    [atol.layout :as layout]
    [atol.db.core :as db]
    [clojure.java.io :as io]
    [atol.middleware :as middleware]
    [ring.util.response]
    [ring.util.http-response :as response]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn test-page [request]
  (let [_ (db/create-sample! {:description "Sample from app"
                              :created_on  (java.util.Date.)})
        samples (db/get-samples)]
    (layout/render request "test.html" {:samples samples})))

(defn say-hi [request]
  (let [someone (get (:form-params request) "name")
        someone (if (not-empty someone) someone "there!")]
    (layout/render request "test.html" {:name someone})))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]
   ["/test" {:get test-page}]
   ["/sayHi" {:post say-hi}]])

