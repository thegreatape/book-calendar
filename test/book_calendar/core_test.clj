(ns book-calendar.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [book-calendar.core :refer :all]))

(let [xml (xml-parse (slurp "test/resources/reviews.xml"))
      actual-book (extract-book xml)]

  (fact "extracted books have the expected keys"
        (keys actual-book) => (just [:title :description :image_url :publication_date] :in-any-order))

  (fact "extracted books have the correct title"
        (:title actual-book) => "Clojure for the Brave and True" )

  (fact "extracted books have the correct description"
    (:description actual-book) => "Clojure for the Brave and True is a work-in-progress available to read for free online.")

  (fact "extracted books have the correct image_url"
        (:image_url actual-book) => "https://d.gr-assets.com/books/1392849558m/20873338.jpg" )

  (fact "extracted books have the publication date as a clj-time/date-time"
        (:publication_date actual-book) => (t/date-time 2012 11 6))
)
