(ns book-calendar.core
  (:require [clj-http.client :as http]
            [clojure.zip :as zip]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as xml-zip]
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
  (zip/xml-zip
    (xml/parse
      (java.io.ByteArrayInputStream.
        (.getBytes (:body (fetch url params)))))))

(def book-attrs
  [:title :description])

(defn extract-book
  [book-element]
  (let [extract-attribute (fn [book-hash attr]
                            (let [value (xml-zip/xml-> book-element zf/descendants attr xml-zip/text)]
                              (assoc book-hash attr (string/join " " (seq value)))))]
    (reduce extract-attribute {} book-attrs)))

(defn extract-books
  [parsed-xml]
  (let [book-list (xml-zip/xml-> parsed-xml zf/descendants :book)]
    (map extract-book book-list))
)
(books-on-shelf 2003928 "currently-reading")

(defn books-on-shelf
  [user-id shelf-name]
  (extract-books
    (get-response "/review/list" {:v 2
                                  :id user-id
                                  :shelf shelf-name
                                  :key api-key })))

;(books-on-shelf 2003928 "currently-reading")
