(ns cheffy.server
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]))

(def app
  (ring/ring-handler
    (ring/router
      [["/"
        {:get {:handler (fn [req] {:status 200
                                   :body   "Hello Reitit"})}}]])))

(defn start
  []
  (let [port 3000]
    (jetty/run-jetty app {:port port :join? false})         ; join? false = do not block thread
    (println (str "\n Server running on port " port))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(comment
  (start)
  (app {:request-method :get
        :uri            "/"})
  (-main))