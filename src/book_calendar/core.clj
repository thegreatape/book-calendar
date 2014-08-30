(ns book-calendar.core
  (:require [clj-http.client :as http]
            [clj-time.core :as t]
            [clojure.zip :as zip]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as xml-zip]
            [clojure.xml :as xml]
            [clojure.string :as string]
            [clojurewerkz.spyglass.client :as memcache]
            [throttler.core :refer [throttle-chan throttle-fn]]
  ))

(def api-key
  (System/getenv "GOODREADS_API_KEY"))

(defn api-url
  [url]
  (str "https://goodreads.com" url))

(def memcache-url "127.0.0.1:11211")

(defn to-cache-key
  [url params]
  (let [query (map (fn [[k v]] (str k "=" v)) params)]
    (str url "?" (string/join "&" query))))

(defn fetch
  [url params]
  (if (empty? api-key)
    (throw (Exception. "GOODREADS_API_KEY not set in environment"))

    (let [cache (memcache/text-connection memcache-url)
          url-key (to-cache-key url params)
          cached-value (memcache/get cache url-key)]

      (if-not (empty? cached-value)
        cached-value
        (let [fetched-value (:body (http/get (api-url url) {:query-params params}))
              _  (memcache/set cache url-key 86400 fetched-value)]
          fetched-value)))))

(def goodreads-fetch
  (throttle-fn fetch 1 :second))

(defn xml-parse
  [xml-string]
  (zip/xml-zip
    (xml/parse
      (java.io.ByteArrayInputStream.
        (.getBytes xml-string)))))

(defn get-response
  [url params]
  (xml-parse (goodreads-fetch url params)))

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

(defn more-pages?
  [xml]
  (let [pagination-element (xml-zip/xml1->
                             xml
                             zf/descendants
                             #(contains? #{:books :reviews} (:tag (zip/node %))))]
    (<
     (bigdec (xml-zip/attr pagination-element :end))
     (bigdec (xml-zip/attr pagination-element :total)))))

(defn get-paginated
  [fetcher extractor]
  (loop [page 1
         results #{}]
    (let [response (fetcher page)
          new-results (into results (extractor response))]
      (if (more-pages? response)
        (recur (inc page) new-results)
        new-results
      ))))

(defn get-shelf
  [user-id shelf-name extractor]
  (let [fetcher (fn [page] (get-response "/review/list" {:v 2
                                                         :id user-id
                                                         :shelf shelf-name
                                                         :page page
                                                         :per_page 200
                                                         :key api-key }))]
    (get-paginated fetcher extractor)))

(defn extract-books
  [parsed-xml]
  (map extract-book (xml-zip/xml-> parsed-xml zf/descendants :book)))

(defn books-by-author
  [author]
  (let [fetcher (fn [page] (get-response
                             (str "/author/list/" (:id author) ".xml")
                             {:page page :key api-key }))]
    (get-paginated fetcher extract-books)))

(defn books-on-shelf
  [user-id shelf-name]
  (get-shelf user-id shelf-name extract-books))

(defn authors-on-shelf
  [user-id shelf-name]
  (get-shelf user-id shelf-name extract-authors))

