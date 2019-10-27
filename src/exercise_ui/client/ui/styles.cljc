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
   (at-import "/css/codemirror.css")
   (at-import "/css/railscasts.css")

   [:kbd.chord
    {:margin "0 0.5em"}

    [:kbd.key
     {:display "inline-block"
      :border-top "1px solid black"
      :border-right "2px solid black"
      :border-bottom "2px solid black"
      :border-left "1px solid black"
      :border-radius "3px"
      :padding "0.15em 0.25em"
      :box-sizing "border-box"
      :min-width "1.25em"
      :text-align "center"}]

    [:.plus
     {:display "inline-block"
      :padding "0 0.15em"}]]

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

     ;; exercises

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

     ;; exercise

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
         {:display "inline"}]]]]

     ;; shortcuts

     [:&.shortcuts
      [:header
       [:>h1
        {:display "inline-block"}]
       [:>a>svg
        {:margin-left "0.5em"
         :width "1em"}]]]

     ;; admin progress page

     [:&.admin-progress
      [:table.user-progress
       [:>tbody
        [:>tr
         ["&:nth-child(even)"
          {:background-color "#eee"}]]]]]]]])
