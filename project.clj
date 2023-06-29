(defproject clojure-exercise-ui "0.0.1"
  :dependencies [[io.bloomventures/omni "0.32.2"
                  :exclusions [borkdude/edamame]]
                 [cljsjs/codemirror "5.44.0-1"]
                 [re-frame "1.3.0"]
                 [rewrite-clj "1.1.47"]
                 [zprint "1.2.7"]
                 ;; [org.clojure/clojure "1.10.0"] from omni
                 ;; [org.clojure/clojurescript "1.10.520"] from omni
                 ;; [io.bloomventures/commons "0.5.1"] from omni
                 ;; [reagent "1.1.1"] from omni
                 ;; [cljsjs/react "17.0.2-0"] from omni
                 ;; [cljsjs/react-dom "17.0.2-0"] from omni
                 ;; [garden "1.3.5"] from omni
                 ]

  :main exercise-ui.core

  :plugins [[io.bloomventures/omni "0.32.2"]]

  :omni-config exercise-ui.omni-config/omni-config

  :profiles {:uberjar
             {:aot :all
              :prep-tasks [["omni" "compile"]
                           "compile"]}})
