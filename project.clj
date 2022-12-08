(defproject clojure-exercise-ui "0.0.1"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.10.764" :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [io.bloomventures/omni "0.27.0"]
                 [io.bloomventures/commons "0.13.2"]
                 [io.bloomventures/decant "3.0.0"]
                 [cljsjs/codemirror "5.44.0-1"]
                 [re-frame "1.3.0"]
                 [rewrite-clj "1.1.45"]
                 [reagent "1.1.1"]
                 [cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]
                 [garden "1.3.5"]
                 [com.draines/postal "2.0.5"]
                 [zprint "0.4.16"]]

  :main exercise-ui.core

  :plugins [[io.bloomventures/omni "0.27.0"]]

  :omni-config exercise-ui.omni-config/omni-config

  :profiles {:uberjar
             {:aot :all
              :prep-tasks [["omni" "compile"]
                           "compile"]}})
