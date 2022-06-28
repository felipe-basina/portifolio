(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(ig-repl/set-prep!
  (fn [] (-> "resources/config.edn"
             slurp
             ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)                                   ; Reloads file changed
(def reset-all ig-repl/reset-all)                           ; Reloads all files

(def app (-> state/system :cheffy/app))
(def db (-> state/system :db/postgres))

(comment
  ;; To show content on repl as clojure map (namespace keywords)
  (set! *print-namespace-maps* false)

  (-> (app {:request-method :get
            :uri            "/v1/recipes/93f36ba4-036c-4140-9928-421cb3c987a8"})
      :body
      (slurp))

  (-> (app {:request-method :post
            :uri            "/v1/recipes"
            :body-params    {:name      "my recipe"
                             :prep-time 49
                             :img       "image-url"}})
      :body
      (slurp))

  (sql/update! db :recipe {:name "MyRecipey"} {:recipe-id "69472ea0-b494-43f7-b4b6-544e2a4607f2"})

  (-> (app {:request-method :put
            :uri            "/v1/recipes/69472ea0-b494-43f7-b4b6-544e2a4607f2"
            :body-params    {:name      "my-recipe"
                             :public    false
                             :prep-time 50
                             :img       "image-url/img"}})
      :body
      (slurp))

  (jdbc/execute! db ["SELECT * FROM recipe WHERE public = true"])
  (jdbc/execute! db ["SELECT * FROM recipe"])
  (jdbc/execute! db ["SELECT * FROM recipe where recipe_id = '58d1c24f-d0c6-4ad5-a1ee-f92e714d2f6c'"])
  (jdbc/execute! db ["DELETE FROM recipe WHERE name like '%my%recipe%'"])
  (jdbc/execute! db ["select * FROM recipe WHERE name = 'my recipe'"])
  (jdbc/execute! db ["select count(*) FROM recipe"])

  (time (sql/find-by-keys db :recipe {:public false}))
  (time
    (with-open [conn (jdbc/get-connection db)]
      {:public (sql/find-by-keys conn :recipe {:public false})
       :drafts (sql/find-by-keys conn :recipe {:public false :uid "auth0|5ef440986e8fbb001355fd9c"})}))

  (with-open [conn (jdbc/get-connection db)]
    (let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"]
      (sql/get-by-id conn :recipe recipe-id :recipe_id {})))

  (with-open [conn (jdbc/get-connection db)]
    (let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"
          [recipe] (sql/find-by-keys conn :recipe {:recipe_id recipe-id})
          steps (sql/find-by-keys conn :step {:recipe_id recipe-id})
          ingredients (sql/find-by-keys conn :ingredient {:recipe_id recipe-id})]
      (when (seq recipe)
        (assoc recipe
          :recipe/steps steps
          :recipe/ingredients ingredients))))

  (go)
  (halt)
  (reset))