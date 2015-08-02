(ns recipi.app
  (:require [clojure.tools.logging :as log]
            [compojure.route :as route]
            [compojure.api.sweet :refer :all]
            [compojure.api.swagger :refer [validate]]
            [compojure.api.middleware :refer [api-middleware-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [response]]
            [ring.util.http-response :refer :all]
            [schema.core :as schema]
            [clj-http.client :as http]
            [cheshire.core :as json]))

(schema/defschema Ingredient
  {:item schema/Str
   :quantity schema/Int
   :measure schema/Str})                ; FIXME should be an enum

(schema/defschema Step
  {:description schema/Str
   :minutes schema/Int})

(schema/defschema Recipe
  {:ingredients [Ingredient]
   :instructions [Step]
   (schema/optional-key :id) schema/Str})

(defn es-url
  "Returns the URL to use for a given index, type and ID"
  [index type id]
  (str "http://localhost:9200/" index "/" type "/" id))

(defn create-recipe 
  "Store a recipe to the server."
  [recipe]
  (let [resp (-> (http/post (es-url "recipes" "recipe" nil)
                            {:as :json
                             :throw-exceptions false
                             :body (json/encode recipe)})
                 :body)]
    (if (nil? (:error resp))
      (created {:id (:id resp) :recipe recipe})
      (internal-server-error {:error (:error resp) :recipe recipe}))))

(defn get-recipe-list
  "Returns a list of recipes"
  []
  (let [query {:query {:match_all {}}}
        resp (-> (http/post (es-url "recipes" "recipe" "_search")
                            {:as :json
                             :throw-exceptions false
                             :body (json/encode query)})
                 :body)]
    (if (nil? (:error resp))
      (ok (map :_source (:hits (:hits resp))))
      (not-found {:error "Recipe list could not be retrieved."}))))

(defn get-recipe
  "Returns a specific recipe"
  [id]
  (let [resp (-> (http/get (es-url "recipes" "recipe" id)
                           {:as :json
                            :throw-exceptions false})
                 :body)]
    (if (:found resp)
      (ok (:_source resp))
      (not-found {:error (str "Recipe with id " id " not found!")}))))

(defn log-on-receive
  "Logs all incoming requests to the console."
  [handler]
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
      (-> 
        (ok "<h1>Web page!</h1>")
        (content-type "text/html; charset=utf-8")))

    (context "/recipes" []
      (GET* "/" []
        :description "Returns an unsorted list of most recently added recipes."
        :responses {404 {:schema {:error schema/Str}
                         :description "Either no recipes are loaded, or a 
                                       connection to the elasticsearch server
                                       was not established."}}
        :return [Recipe]
        (get-recipe-list))

      (POST* "/" []
        :description "Adds a new recipe to your growing collection."
        :body [recipe Recipe]
        :responses {201 {:schema {:id schema/Str :recipe Recipe}
                         :description "The recipe was successfully added to
                                       the database."}
                    500 {:schema {:error schema/Str :recipe Recipe}
                         :description "An error occurred while trying to save 
                                       the recipe to the server."}}
        (create-recipe recipe))

      (context "/:id" [id]
        (GET* "/" []
          :description "Returns the recipe for the given `id`"
          :responses {404 {:schema {:error schema/Str}
                           :description "The recipe for that given id
                                         could not be found."}}
          :return Recipe
          (get-recipe id))

        (PUT* "/" []
          :description "Update an existing recipe."
          :body [recipe Recipe]
          :responses {201 {:schema {:id schema/Str :recipe Recipe}
                           :description "The recipe was successfully added to
                                         the database."}
                      500 {:schema {:error schema/Str :recipe Recipe}
                           :description "An error occurred while trying to save 
                                         the recipe to the server."}}
          (ok {:message (str "Updading " id " recipe")}))

        (DELETE* "/" []
          :description "Remove a recipe from the server."
          (ok {:message (str "Deleting " id " recipe")}))))

    (route/not-found
      (not-found {:message "Invalid URI"}))))

(validate app)
