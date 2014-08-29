(ns book-calendar.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [book-calendar.core :refer :all]))

(let [xml (xml-parse (slurp "test/resources/reviews.xml"))]

  (let [book (extract-book xml)]

    (fact "extracted books have the expected keys"
          (keys book) => (just [:title :description :image_url :publication_date :authors] :in-any-order))

    (fact "extracted books have the correct title"
          (:title book) => "Clojure for the Brave and True" )

    (fact "extracted books have the correct description"
          (:description book) => "Clojure for the Brave and True is a work-in-progress available to read for free online.")

    (fact "extracted books have the correct image_url"
          (:image_url book) => "https://d.gr-assets.com/books/1392849558m/20873338.jpg" )

    (fact "extracted books have the publication date as a clj-time/date-time"
          (:publication_date book) => (t/date-time 2012 11 6))

    (fact "extracted books have the authors"
          (:authors book) => '({:id "7868379" :name "Daniel Higginbotham"}))
  )

  (let [authors (extract-authors xml)]

    (fact "a single author is extracted"
          (count authors) => 1)

    (fact "the extracted author has the correct id"
          (:id (first authors)) => "7868379")

    (fact "the extracted author has the correct name"
          (:name (first authors)) => "Daniel Higginbotham")
  )
)
