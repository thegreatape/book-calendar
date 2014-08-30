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
  (string/trim (xml-zip/xml1-> value xml-zip/text)))

(defn extract-attribute
  [element]
  (fn [element-hash [attr converter]]
    (let [value (xml-zip/xml1-> element zf/descendants attr)]
      (assoc element-hash attr (converter value)))))

(declare author-attrs)

(defn extract-author
  [author-element]
  (reduce (extract-attribute author-element) {} author-attrs))

(defn extract-authors
  [element]
  (map extract-author (xml-zip/xml-> element zf/descendants :author)))

(def book-attrs
  {:title             to-string
   :description       to-string
   :image_url         to-string
   :publication_year  to-string
   :publication_month to-string
   :publication_day   to-string
   :authors           extract-authors
   })

(def author-attrs
  {:id   to-string
   :name to-string
   })


(defn raw-book-hash
  [book-element]
  (reduce (extract-attribute book-element) {} book-attrs))

(defn add-publication-date
  [book-hash]
  (let [publication-date (if (or (empty? (:publication_year book-hash))
                                (empty? (:publication_month book-hash))
                                (empty? (:publication_day book-hash)))
                           nil
                           (t/date-time
                             (bigdec (:publication_year book-hash))
                             (bigdec (:publication_month book-hash))
                             (bigdec (:publication_day book-hash))))]
        (dissoc
          (assoc book-hash :publication_date publication-date)
          :publication_year :publication_month :publication_day)))

(def extract-book (comp add-publication-date raw-book-hash))

(defn extract-books
  [parsed-xml]
  (map extract-book (xml-zip/xml-> parsed-xml zf/descendants :book)))

(defn books-on-shelf
  [user-id shelf-name]
  (extract-books
    (get-response "/review/list" {:v 2
                                  :id user-id
                                  :shelf shelf-name
                                  :key api-key })))
