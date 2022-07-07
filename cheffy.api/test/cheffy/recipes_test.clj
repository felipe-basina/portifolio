(ns cheffy.recipes-test
  (:require [clojure.test :refer :all]
            [cheffy.server :refer :all]
            [cheffy.test-system :as ts]))

(defn recipe-fixture
  [f]
  (let [email "account-tests@cheffy.app"
        password "Testing#00"]
    (ts/create-auth0-user
      {:connection "Username-Password-Authentication"
       :email      email
       :password   password})
    (reset! ts/token (ts/get-test-token email))
    (ts/test-endpoint :post "/v1/accounts" {:auth true})
    (ts/test-endpoint :put "/v1/accounts" {:auth true})
    (reset! ts/token (ts/get-test-token email))
    (f)
    (ts/test-endpoint :delete "/v1/accounts" {:auth true})
    (reset! ts/token nil)))

(use-fixtures :once recipe-fixture)

(def recipe-id (atom nil))

(def step-id (atom nil))

(def ingredient-id (atom nil))

(def recipe {:img       ""
             :prep-time 30
             :name      "My Test Recipe"})

(def step {:description "My Test Step"
           :sort        1})

(def ingredient {:name    "New Ingredient"
                 :amount  5
                 :measure "unit"
                 :sort    1})

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

  (testing "Create step"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/steps")
                                                  {:auth true :body step})]
      (reset! step-id (:step-id body))
      (is (= status 201))))

  (testing "Update step"
    (let [{:keys [status body]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/steps")
                                                  {:auth true :body {:step-id     @step-id
                                                                     :sort        2
                                                                     :description "Updated Step"}})]
      (is (= status 204))
      (is (nil? body))))

  (testing "Delete step"
    (let [{:keys [status body]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/steps")
                                                  {:auth true :body {:step-id @step-id}})]
      (reset! step-id (:step-id body))
      (is (= status 204))
      (is (nil? body))))

  (testing "Create ingredient"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/ingredients")
                                                  {:auth true :body ingredient})]
      (reset! ingredient-id (:ingredient-id body))
      (is (= status 201))))

  (testing "Update ingredient"
    (let [{:keys [status body]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/ingredients")
                                                  {:auth true :body {:ingredient-id @ingredient-id
                                                                     :sort          2
                                                                     :amount        6
                                                                     :measure       "unit"
                                                                     :name          "Updated Ingredient"}})]
      (is (= status 204))
      (is (nil? body))))

  (testing "Delete ingredient"
    (let [{:keys [status body]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/ingredients")
                                                  {:auth true :body {:ingredient-id @ingredient-id}})]
      (reset! ingredient-id (:ingredient-id body))
      (is (= status 204))
      (is (nil? body))))

  (testing "Delete recipe"
    (let [{:keys [status body]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
      (reset! recipe-id (:recipe-id body))
      (is (= status 204))
      (is (nil? body)))))

(comment
  (ts/test-endpoint :get "/v1/recipes" {:auth true})
  (ts/test-endpoint :get "/v1/recipes/e8fc982e-c0f2-4359-899d-902af3b3ee49" {:auth true})

  (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})
  (let [{:keys [body]} (ts/test-endpoint :get "/v1/recipes/7ad6ae9d-e6b6-4142-8924-c98da78032ab" {:auth false})]
    (:recipe body))

  (ts/test-endpoint :put "/v1/recipes/8737fd6a-1a45-44ea-97ed-005d79fff89f" {:auth true :body update-recipe})
  (ts/test-endpoint :delete "/v1/recipes/a3c5ec2d-e1a1-463f-aeb7-4fa6bb6e8ac6" {:auth true})

  (ts/test-endpoint :post "/v1/recipes/7ad6ae9d-e6b6-4142-8924-c98da78032ab/favorite" {:auth true})
  (ts/test-endpoint :delete "/v1/recipes/7ad6ae9d-e6b6-4142-8924-c98da78032ab/favorite" {:auth true})
  )