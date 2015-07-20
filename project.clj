(defproject recipi "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-json "0.3.1"]
                 [clojurewerkz/elastisch "2.1.0"]
                 [compojure "1.3.1"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler recipi.app/app}
  :profiles 
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [midje "1.7.0"]]
         :plugins [[lein-midje "3.0.0"]]}})
