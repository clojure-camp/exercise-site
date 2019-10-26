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

(defn >teachable []
  [:>.teachable

   [:&.concept
    {:font-style "italic"}]

   [:&.function
    {:font-family "monospace"}]])

(defn app []
  [

   (at-import "https://fonts.googleapis.com/css?family=Source+Code+Pro")
   (at-import
     "https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.44.0/codemirror.css")
   (at-import "https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.44.0/theme/railscasts.min.css")

   [:.CodeMirror
    (codemirror)]

   [:body
    {:margin 0
     :padding 0}

    [:.header
     {:display "flex"
      :justify-content "space-between"}

     [:nav

      [:a
       {:display "inline-block"
        :padding "0.25em 0"
        :margin "0 0.25em"}

       ["&[target]"
        {:font-style "italic"}]]]]

    [:.page

     [:&.exercises

      [:>a
       {:display "block"}]

      [:>table.exercises

       [:>tbody

        [:>tr

         ["&:nth-child(even)"
          {:background-color "#eee"}]

         [:>td

          (>teachable)]]]]]

     [:&.exercise

      [:>.functions

       (>teachable)

       [:>.function
        {:display "block"}

        [:&.taught
         {:font-weight "bold"}]

        [:&.used]]]

      [:>.related
       [:>a
        {:display "block"}]]

      [:>details.solution
       {:margin-top "1em"}

       [:>summary

        [:>h2
         {:display "inline"}]]]]]]])
