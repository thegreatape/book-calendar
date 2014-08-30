(ns book-calendar.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [book-calendar.core :refer :all]))

(fact "extracted books have the correct fields"
  (let [xml (xml-parse (slurp "test/resources/reviews.xml"))]
    (let [book (extract-book xml)]
      (keys book)              => (just [:title :description :image_url :publication_date :authors] :in-any-order)
      (:title book)            => "Clojure for the Brave and True"
      (:description book)      => "Clojure for the Brave and True is a work-in-progress available to read for free online."
      (:image_url book)        => "https://d.gr-assets.com/books/1392849558m/20873338.jpg"
      (:publication_date book) => (t/date-time 2012 11 6)
      (:authors book)          => '({:id "7868379" :name "Daniel Higginbotham"})
    )))

(fact "extracted books without publication dates have nil dates"
  (:publication_date (extract-book (xml-parse (slurp "test/resources/reviews-no-pub-date.xml")))) => nil)

(fact "extracted authors have the correct fields"
  (let [authors (extract-authors (xml-parse (slurp "test/resources/reviews.xml")))]

    (count authors)         => 1
    (:id (first authors))   => "7868379"
    (:name (first authors)) => "Daniel Higginbotham"
  ))

