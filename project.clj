(defproject clojure-exercise-ui "0.0.1"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [io.bloomventures/omni "0.21.6"]
                 [io.bloomventures/commons "0.5.1"]
                 [io.bloomventures/decant "3.0.0"]
                 [cljsjs/codemirror "5.24.0-1"]
                 [re-frame "0.10.9"]
                 [rewrite-clj "0.6.1"]
                 [reagent "0.8.1"]
                 [garden "1.3.5"]
                 [zprint "0.4.10"]]

  :main exercise-ui.core

  :plugins [[io.bloomventures/omni "0.18.1"]]

  :omni-config exercise-ui.config/config

  :profiles {:uberjar
             {:aot :all
              :prep-tasks [["omni" "compile"]
                           "compile"]}})
