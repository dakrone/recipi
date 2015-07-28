(ns recipi.app
  (:require [clojure.tools.logging :as log]
            [compojure.route :as route]
            [compojure.api.sweet :refer :all]
            [compojure.api.swagger :refer [validate]]
            [compojure.api.middleware :refer [api-middleware-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [response]]
            [ring.util.http-response :refer :all]
            [schema.core :as schema]))

(schema/defschema Ingredient
  {:item schema/Str
   :quantity schema/Int
   :measure (schema/enum :cup :ounce :pound :each)})

(schema/defschema Step
  {:description schema/Str
   :time schema/Int})

(schema/defschema Recipe
  {:ingredients [Ingredient]
   :instructions [Step]})

(defn log-on-receive [handler]
  "Logs all incoming requests to the console."
  (fn [req]
    (log/info (str "Incoming request to '" (:uri req) "' received."))
    (handler req)))

(defapi app
  (middlewares [log-on-receive]
    (swagger-ui "/swagger")
    (swagger-docs
      {:info {:version "0.1.0"
              :title "Recipi"
              :description "A web server to manage delicious recipes!"}}
      :consumes ["application/json"]
      :produces ["application/json"])
    (GET* "/" []
      :no-doc true
      "<h1>Web page!</h1>")
    (context "/recipes" []
      (GET* "/" []
        :description "Returns an unsorted list of most recently added recipes."
        (response {:message "Getting a list of recipes"}))
      (POST* "/" []
        :description "Adds a new recipe to your growing collection."
        :body [recipe Recipe]
        (response {:message "New recipe added!"}))
      (context "/:id" [id]
        (GET* "/" []
          :description "Returns the recipe with the given `id`. If no such recipe
                        is found, return a 404."
          (response {:message (str "Getting " id " recipe")}))
        (PUT* "/" []
          :description "Update an existing recipe. A whole new recipe must be
                        uploaded, not just the changes."
          :body [recipe Recipe]
          (response {:message (str "Updading " id " recipe")}))
        (DELETE* "/" []
          :description "Remove a recipe from your diminishing collection."
          (response {:message (str "Deleting " id " recipe")}))))
    (route/not-found
      (response {:message "Invalid URI"}))))

(validate app)
