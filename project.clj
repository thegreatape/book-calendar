(defproject book-calendar "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :ring {:handler book-calendar.web/routes}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.zip "0.1.1"]
                 [clj-time "0.8.0"]
                 [midje "1.6.3"]
                 [throttler "1.0.0"]
                 [clojurewerkz/spyglass "1.0.0"]
                 [clj-http "1.0.0"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 ])
