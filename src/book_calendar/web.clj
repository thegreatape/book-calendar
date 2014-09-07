(ns book-calendar.web
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.handler :as ch]
            [clj-time.core :as t]
            [clojurewerkz.support.json :as support-json]
            [ring.middleware.json :as ring-json]
            [ring.middleware.params :as ring-params]
            [ring.adapter.jetty :as ring])
  (:use [book-calendar.core]
        [hiccup.core]
        [ring.util.response :only [response]])
  (:gen-class))

(defroutes routes

  (GET "/" []
       (html [:h1 "Hello Hiccup World"]))

  (GET "/user/:id/books" [id & params]
     (response
       (published-between
         (books-by-authors-on-shelf id (get params "shelf"))
         (t/date-time 2014 1 1)
         (t/date-time 2015 6 1))))
)

(def port
  (bigdec (or (System/getenv "PORT") 8080)))

(def handler
  (-> routes
      ring-params/wrap-params
      ring-json/wrap-json-response
      ring-json/wrap-json-body))

(defn -main []
  (ring/run-jetty #'handler {:port port :join? false}))
