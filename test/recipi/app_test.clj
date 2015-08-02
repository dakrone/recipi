(ns recipi.app-test
  (:require [recipi.app :refer :all]
            [clj-http.client :as http]
            [midje.sweet :refer :all]
            [cheshire.core :refer :all]
            [clj-http.fake :refer :all]))

(def json "application/json;charset=UTF-8")
(def html "text/html;charset=UTF-8")
(def server "http://127.0.0.1:3000")

(def sample-recipe (generate-string
                     {:ingredients
                      [{:item "tomato" :quantity 42 :measure "each"}]
                      :instructions
                      [{:description "Throw tomato" :minutes 60}]}))

(facts "given the web server is running"
  (fact "when a get request to '/' is received"
    (with-fake-routes
      {server
       (fn [request]
         {:status 200
          :headers {:content-type html}
          :body "<h1>Mock page</h1>"})}
      (let [response (http/get server)]
        (:status response) => 200
        (:content-type (:headers response)) => html)))

  ;; FIXME use schema/validate to ensure incoming payload is correct
  (fact "when a post request to '/recipes' is received"
    (with-fake-routes
      {(apply str server "/recipes")
       (fn [request]
         {:status 200
          :headers {:content-type json}
          :body sample-recipe})}
      (let [response (http/post (apply str server "/recipes")
                       {:headers {:content-type json}
                        :body sample-recipe})]
        (:status response) => 200
        (:body response) => sample-recipe
        (:content-type (:headers response)) => json)))

  (fact "when a get request to '/recipes' is received"
    (with-fake-routes
      {(apply str server "/recipes")
       (fn [request]
         {:status 200
          :headers {:content-type json}
          :body (str [(repeat 5 sample-recipe)])})}
      (let [response (http/get (apply str server "/recipes"))]
        (:status response) => 200
        (:content-type (:headers response)) => json)))

  (fact "when a get request to '/recipes/:id' is received"
    (with-fake-routes
      {(apply str server "/recipes/1")
       (fn [request]
         {:status 200
          :headers {:content-type json}
          :body sample-recipe})}
      (let [response (http/get (apply str server "/recipes/1"))]
        (:status response) => 200
        (:content-type (:headers response)) => json
        (:body response) => sample-recipe)))

  ;; FIXME use schema/validate to ensure incoming payload is correct
  (fact "when a put request to '/recipes/:id' is received"
    (with-fake-routes
      {(apply str server "/recipes/1")
       (fn [request]
         {:status 200
          :headers {:content-type json}
          :body sample-recipe})}
      (let [response (http/put (apply str server "/recipes/1")
                       {:headers {:content-type json}
                        :body sample-recipe})]
        (:status response) => 200
        (:content-type (:headers response)) => json
        (:body response) => sample-recipe)))
  
  (fact "when a delete request to '/recipes/:id' is received"
    (with-fake-routes
      {(apply str server "/recipes/1")
       (fn [request]
         {:status 200
          :headers {:content-type json}
          :body sample-recipe})}
      (let [response (http/delete (apply str server "/recipes/1"))]
        (:status response) => 200
        (:content-type (:headers response)) => json
        (:body response) => sample-recipe))))
