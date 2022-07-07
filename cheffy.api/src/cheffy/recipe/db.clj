(ns cheffy.recipe.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

(defn find-all-recipes
  [db uid]
  (with-open [conn (jdbc/get-connection db)]
    (let [public (sql/find-by-keys conn :recipe {:public true})]
      (if uid
        (let [drafts (sql/find-by-keys conn :recipe {:public false :uid uid})]
          {:public public
           :drafts drafts})
        {:public public}))))

(defn insert-recipe!
  [db recipe]
  (sql/insert! db :recipe (assoc recipe :public false
                                        :favorite-count 0)))

(defn find-recipe-by-id
  [db recipe-id]
  (with-open [conn (jdbc/get-connection db)]
    (let [[recipe] (sql/find-by-keys conn :recipe {:recipe_id recipe-id})
          steps (sql/find-by-keys conn :step {:recipe_id recipe-id})
          ingredients (sql/find-by-keys conn :ingredient {:recipe_id recipe-id})]
      (when (seq recipe)
        (assoc recipe
          :recipe/steps steps
          :recipe/ingredients ingredients)))))

(defn update-recipe!
  [db recipe]
  (-> (sql/update! db :recipe recipe (select-keys recipe [:recipe-id]))
      ::jdbc/update-count
      (pos?)))

(defn delete-recipe!
  [db recipe]
  (-> (sql/delete! db :recipe recipe)
      ::jdbc/update-count
      (pos?)))

(defn insert-step!
  [db step]
  (sql/insert! db :step step))

(defn update-step!
  [db step]
  (-> (sql/update! db :step step (select-keys step [:step-id]))
      ::jdbc/update-count
      (pos?)))

(defn delete-step!
  [db step]
  (-> (sql/delete! db :step step)
      ::jdbc/update-count
      (pos?)))

(defn insert-ingredient!
  [db ingredient]
  (sql/insert! db :ingredient ingredient))

(defn update-ingredient!
  [db ingredient]
  (-> (sql/update! db :ingredient ingredient (select-keys ingredient [:ingredient-id]))
      ::jdbc/update-count
      (pos?)))

(defn delete-ingredient!
  [db ingredient]
  (-> (sql/delete! db :ingredient ingredient)
      ::jdbc/update-count
      (pos?)))

(defn favorite-recipe!
  [db {:keys [recipe-id] :as data}]
  (-> (jdbc/with-transaction [tx db]                        ; Using transaction to guarantee all or nothing
                             (sql/insert! tx :recipe-favorite data (:options db))
                             (jdbc/execute-one! tx ["UPDATE recipe
                                                     SET favorite_count = favorite_count + 1
                                                     WHERE recipe_id = ?" recipe-id]))
      ::jdbc/update-count
      (pos?)))

(defn unfavorite-recipe!
  [db {:keys [recipe-id] :as data}]
  (-> (jdbc/with-transaction [tx db]
                             (sql/delete! tx :recipe-favorite data (:options db))
                             (jdbc/execute-one! tx ["UPDATE recipe
                                                     SET favorite_count = favorite_count - 1
                                                     WHERE recipe_id = ?" recipe-id]))
      ::jdbc/update-count
      (pos?)))