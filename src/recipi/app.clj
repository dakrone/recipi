(ns recipi.app
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]))

(defroutes app-routes
  (GET "/" [] "<h1>Web page!</h1>")
  (context "/recipes" []
    (GET "/" [] (response {:message "Stub for getting a list of recipes"}))
    (POST "/" [] (response {:message "Stub for creating a new recipe"}))
    (context "/:id" [id]
      (GET "/" [] (response {:message (str "Getting recipe, id " id)}))
      (PUT "/" [] (response {:message (str "Updating recipe, id " id)}))
      (DELETE "/" [] (response {:message (str "Deleting recipe, id " id)}))))
  (route/not-found 
    (response {:message "Invalid URI."})))

(defn wrap-log-response [handler]
  (fn [req]
    (log/info (str "Incoming request to '" (:uri req) "' received."))
    (handler req)))

(def app 
  (-> app-routes
    wrap-log-response
    wrap-json-response))
