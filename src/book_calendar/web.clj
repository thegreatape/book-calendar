(ns book-calendar.web
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.handler :as ch]
            [clj-time.core :as t]
            [pusher :as pusher]
            [clojurewerkz.support.json :as support-json]
            [ring.middleware.json :as ring-json]
            [ring.middleware.params :as ring-params]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.core.async :refer [go <! <!! chan]]
            [ring.adapter.jetty :as ring])
  (:use [book-calendar.core]
        [hiccup.core]
        [ring.util.response :only [response]])
  (:gen-class))

(def pusher-key
  (System/getenv "PUSHER_KEY"))

(def pusher-secret
  (System/getenv "PUSHER_SECRET"))

(def pusher-app-id
  (System/getenv "PUSHER_APP_ID"))

(def start-date
  (t/date-time 2014 1 1))

(def end-date
  (t/date-time 2015 6 1))

(defn notify-book
  [user-id book]
  (pusher/with-pusher-auth [pusher-app-id pusher-key pusher-secret]
    (pusher/with-pusher-channel (str "user-" user-id)
      (pusher/trigger "book" book))))

(defn fetch-books
  [user-id shelf]
    (books-by-authors-on-shelf
      user-id
      shelf
      (fn [book]
        (if (published-between? book start-date end-date)
          (notify-book user-id book)))))

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
  (wrap-cors (-> routes
                 ring-params/wrap-params
                 ring-json/wrap-json-response
                 ring-json/wrap-json-body)
             :access-control-allow-origin #"http://localhost:9000"
             :access-control-allow-methods [:get :put :post :delete]))

(defn -main []
  (ring/run-jetty #'handler {:port port :join? false}))

;(let [ch (chan)]
  ;(go (while true
        ;(let [v (<! ch)]
          ;(println "Read: " v))))
  ;(go (>! ch "hi")
      ;(<! (timeout 5000))
      ;(>! ch "there")))



