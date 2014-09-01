(ns book-calendar.web
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring])
  (:use [book-calendar.core]
        [hiccup.core])
  (:gen-class))

(defroutes routes
  (GET "/" [] 
       (html [:h1 "Hello Hiccup World"])))

(def port
  (bigdec (or (System/getenv "PORT") 8080)))

(defn -main []
  (ring/run-jetty #'routes {:port port :join? false}))
