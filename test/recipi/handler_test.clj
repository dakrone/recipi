(ns recipi.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [recipi.handler :refer :all]))

(defn get-content-type [response]
  "Returns the value for the Content-Type header"
  (get-in response [:headers "Content-Type"]))

(deftest test-app
  (testing "A GET request to '/' returns the home page"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (= (get-content-type response) "text/html"))))
  
  (testing "A POST request to '/recipes' creates a new recipe"
    (let [response (app (request :post "/recipes"))]
      (is (= (:status response) 201))
      (is (= (get-content-type response) "application/json"))))
  
  (testing "A GET request to '/recipes/:id' returns a recipe"
    (let [response (app (request :get "/recipes/1"))]
      (is (= (:status response) 200))
      (is (= (get-content-type response) "application/json"))))

  (testing "A GET request to '/recipes' returns a list of recipes"
    (let [response (app (request :get "/recipes"))]
      (is (= (:status response) 200))
      (is (= (get-content-type response) "application/json"))))
  
  (testing "A DELETE request to '/recipes/:id' deletes a recipe"
    (let [response (app (request :get "/recipes/1"))]
      (is (= (:status response) 200))
      (is (= (get-content-type response) "application/json"))))
  
  (testing "A PUT request to '/recipes/:id' updates a recipe"
    (let [response (app (request :get "/recipes/1"))]
      (is (= (:status response) 200))
      (is (= (get-content-type response) "application/json"))))
  
  (testing "Any request made to other URIs returns a 404"
    (let [response (app (request :get "/baduri"))]
      (is (= (:status response) 404)))))
