(ns book-calendar.core
  (:require [clj-http.client :as http]
            [clojure.xml :as xml]
            [clojure.string :as string]
            ))

(def api-key
  (System/getenv "GOODREADS_API_KEY"))

(defn api-url
  [url]
  (str "https://goodreads.com" url))

(defn fetch
  [url params]
  (http/get (api-url url)
            {:query-params params}))
(defn parse
  [xml-str]
  (xml/parse xml-str))

(defn get-response
  [url params]
  (xml/parse (java.io.ByteArrayInputStream.
               (.getBytes (:body (fetch url params))))))

(defn extract-books
  [parsed-xml]
)

(defn books-on-shelf
  [user-id shelf-name]
  (get-response "/review/list" {:v 2
                                :id user-id
                                :shelf shelf-name
                                :key api-key }))

(books-on-shelf 2003928 "currently-reading")
