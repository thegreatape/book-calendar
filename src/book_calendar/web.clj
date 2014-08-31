(ns book-calendar.web
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring])
  (:use [book-calendar.core]
        [hiccup.core]))

(defroutes routes
  (GET "/" [] 
       (html [:h1 "Hello Hiccup World"])))

(defn -main []
  (ring/run-jetty #'routes {:port 8080 :join? false}))
