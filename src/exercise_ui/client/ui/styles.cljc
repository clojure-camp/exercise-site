(ns exercise-ui.client.ui.styles
  (:require
    [garden.stylesheet :refer [at-import at-media]]))

(defn codemirror []
  {:white-space "pre"
   :height "inherit"
   :font-family "Source Code Pro"
   :font-size "0.8em"
   :width "100%"
   :box-sizing "border-box"
   :padding "0.5em"})

(defn app []
  [

   (at-import "https://fonts.googleapis.com/css?family=Source+Code+Pro")
   (at-import
     "https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.20.0/codemirror.css")
   (at-import "https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.20.0/theme/railscasts.min.css")

   [:.CodeMirror
    (codemirror)]

   [:body
    [:.page
     [:&.home
      [:>a
       {:display "block"}]

      [:table.exercises
       ["tr:nth-child(even)"
        {:background-color "#eee"}]]]

     [:.teachable
      [:&.concept
       {:font-style "italic"}]
      [:&.function
       {:font-family "monospace"}]]]]])
