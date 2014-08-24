(ns book-calendar.core
  (:require [clj-http.client :as http]
            [clj-time.core :as t]
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

(defn xml-parse
  [xml-string]
  (zip/xml-zip
    (xml/parse
      (java.io.ByteArrayInputStream.
        (.getBytes xml-string)))))

(defn get-response
  [url params]
  (xml-parse (:body (fetch url params))))

(defn to-string [value]
  (string/trim value))

(def book-attrs
  {:title             to-string
   :description       to-string
   :image_url         to-string
   :publication_year  to-string
   :publication_month to-string
   :publication_day   to-string
   ;:authors           to-string
   })

(defn raw-book-hash
  [book-element]
  (let [extract-attribute (fn [book-hash [attr converter]]
                            (let [value (xml-zip/xml1-> book-element zf/descendants attr xml-zip/text)]
                              (assoc book-hash attr (converter value))))]
    (reduce extract-attribute {} book-attrs)))

(defn add-publication-date
  [book-hash]
  (let [year (bigdec (:publication_year book-hash))
        month (bigdec (:publication_month book-hash))
        day (bigdec (:publication_day book-hash))]
    (dissoc
      (assoc book-hash :publication_date (t/date-time year month day) )
      :publication_year :publication_month :publication_day)))

(def extract-book (comp add-publication-date raw-book-hash))

(defn extract-books
  [parsed-xml]
  (let [book-list (xml-zip/xml-> parsed-xml zf/descendants :book)]
    (map extract-book book-list)))

(defn books-on-shelf
  [user-id shelf-name]
  (extract-books
    (get-response "/review/list" {:v 2
                                  :id user-id
                                  :shelf shelf-name
                                  :key api-key })))
