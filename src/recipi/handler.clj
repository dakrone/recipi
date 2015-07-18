(ns recipi.handler
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]))

(defroutes app-routes
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
