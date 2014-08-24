(ns book-calendar.core-test
  (:require [clojure.test :refer :all]
            [book-calendar.core :refer :all]))


(deftest parse-single-book
  (testing "parsing a single book from goodreads review xml"
    (let [xml (xml-parse (slurp "test/resources/reviews.xml"))
          expected-book {:title       "Clojure for the Brave and True"
                         :description "Clojure for the Brave and True is a work-in-progress available to read for free online."
                         :image_url   "https://d.gr-assets.com/books/1392849558m/20873338.jpg" 
                         }
          actual-book (extract-book xml)]
    (is (= expected-book actual-book)))))
