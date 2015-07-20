(ns recipi.app-test
  (:require [ring.mock.request :refer :all]
            [recipi.app :refer :all]
            [midje.sweet :refer :all]))

(defn get-content-type [response]
  "Returns the value for the Content-Type header"
  (get-in response [:headers "Content-Type"]))

(def json "application/json; charset=utf-8")
(def html "text/html; charset=utf-8")

(facts "about the web server web servers"
  (fact "A get request '/' returns the home page"
    (let [response (app (request :get "/"))]
      (:status response) => 200
      (get-content-type response) => html))

  (fact "A post request '/recipes' creates a new recipe"
    (let [response (app (request :post "/recipes"))]
      (:status response) => 200
      (get-content-type response) => json))

  (fact "A get request '/recipes' returns a list of recipes"
    (let [response (app (request :get "/recipes"))]
      (:status response) => 200
      (get-content-type response) => json))

  (fact "A get request '/recipes/:id' returns a recipe"
    (let [response (app (request :get "/recipes/1"))]
      (:status response) => 200
      (get-content-type response) => json))

  (fact "A delete request '/recipes/:id' deletes a recipe"
    (let [response (app (request :delete "/recipes/1"))]
      (:status response) => 200
      (get-content-type response) => json))

  (fact "A put request '/recipes/:id' updates a recipe"
    (let [response (app (request :put "/recipes/1"))]
      (:status response) => 200
      (get-content-type response) => json))

  (fact "Any request made to another endpoint returns a 404"
    (let [response (app (request :get "/invalid"))]
      (:status response) => 404)))
