(ns book-calendar.web
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring])
  (:use [book-calendar.core]))

(defroutes routes
  (GET "/" [] "<h1>Hello Compojure Reloaded World</h1>"))

(defn -main []
  (ring/run-jetty #'routes {:port 8080 :join? false}))
