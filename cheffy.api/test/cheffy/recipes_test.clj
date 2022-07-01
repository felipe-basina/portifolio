(ns cheffy.recipes-test
  (:require [clojure.test :refer :all]
            [cheffy.server :refer :all]
            [cheffy.test-system :as ts]
            [muuntaja.core :as m]))

(use-fixtures :once ts/token-fixture)

(def recipe-id (atom nil))

(def recipe {:img       ""
             :prep-time 30
             :name      "My Test Recipe"})

(def update-recipe (assoc recipe :public true))

(deftest recipes-tests
  (testing "List recipes"
    (testing "with auth -- public and drafts"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes" {:auth true})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (vector? (:drafts body)))))

    (testing "without auth -- public"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes" {:auth false})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (nil? (:drafts body)))))))

(deftest recipe-tests
  (testing "Create recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})]
      (reset! recipe-id (:recipe-id body))
      (is (= status 201))
      (is (not (empty? body)))))

  (testing "Update recipe"
    (let [{:keys [status body]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id) {:auth true :body update-recipe})]
      (is (= status 204))
      (is (nil? body))))

  (testing "Favorite recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/favorite") {:auth true})]
      (is (= status 201))
      (is (nil? body))))

  (testing "Unfavorite recipe"
    (let [{:keys [status body]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/favorite") {:auth false})]
      (is (= status 204))
      (is (nil? body))))

  (testing "Delete recipe"
    (let [{:keys [status body]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
      (reset! recipe-id (:recipe-id body))
      (is (= status 204))
      (is (nil? body)))))

(comment
  (ts/test-endpoint :get "/v1/recipes" {:auth false})

  (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})
  (let [{:keys [body]} (ts/test-endpoint :get "/v1/recipes/7ad6ae9d-e6b6-4142-8924-c98da78032ab" {:auth false})]
    (:recipe body))

  (ts/test-endpoint :put "/v1/recipes/8737fd6a-1a45-44ea-97ed-005d79fff89f" {:auth true :body update-recipe})
  (ts/test-endpoint :delete "/v1/recipes/a3c5ec2d-e1a1-463f-aeb7-4fa6bb6e8ac6" {:auth true})

  (ts/test-endpoint :post "/v1/recipes/7ad6ae9d-e6b6-4142-8924-c98da78032ab/favorite" {:auth true})
  (ts/test-endpoint :delete "/v1/recipes/7ad6ae9d-e6b6-4142-8924-c98da78032ab/favorite" {:auth true})
  )