(ns exercise-ui.client.ui.styles
  (:require
    [garden.stylesheet :refer [at-import at-media]]))

(def color-accent "#2E4057")
(def color-accent-light "#048BA8")
(def color-text-light "#FFF")

(defn codemirror []
  {:white-space "pre"
   :height "inherit"
   :font-family "Source Code Pro"
   :font-size "0.8em"
   :width "100%"
   :box-sizing "border-box"
   :padding "1em"
   :overflow "auto"})

(defn >status []
  [:>.status
   {:display "inline-block"
    :width "1em"
    :color color-accent}

   [:&.started]
   [:&.completed]
   [:&.reviewed]])

(defn >teachable []
  [:>.teachable

   [:&.concept
    {:font-style "italic"}]

   [:&.function
    {:font-family "monospace"}]

   {:color "black"}

   ["&[href]"
    {:text-decoration "none"}

    [:&:hover
     {:text-decoration "underline"}]]])

(defn app []
  [

   (at-import "https://fonts.googleapis.com/css?family=Montserrat:600|Source+Code+Pro&display=swap")
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
     :padding 0
     :font-family "Arial"}

    [:.log-in
     {:display "flex"
      :flex-direction "column"
      :width "100%"
      :height "100vh"
      :align-items "center"
      :justify-content "center"}

     [:>h1
      {:font-family "Montserrat"}]

     [:>form

      [:>input
       :>button
       {:font-size "1.5em"
        :padding "0.5em"}]]]

    [:.header
     {:display "flex"
      :justify-content "space-between"
      :align-items "center"
      :background color-accent
      :font-family "Montserrat"}

     [:>h1
      {:color color-text-light
       :font-family "Source Code Pro"
       :font-size "1em"
       :padding "0.5em"
       :margin 0}]

     [:>nav

      [:>a
       {:display "inline-block"
        :padding "0.5em"
        :margin "0 0.25em"
        :color color-text-light
        :text-decoration "none"}

       {:opacity "0.7"}

       [:&.main
        {:opacity 1}]

       [:&:hover
        :&.active
        {:background-color color-accent-light}]

       ["&[target]"
        {:font-style "italic"}]]]

     [:>.user
      {:color color-text-light}

      [:>button
       {:background "none"
        :border "none"
        :color color-text-light
        :padding "0.5em"}

       [:&:hover
        {:background-color color-accent-light
         :cursor "pointer"}]

       [:>svg
        {:width "1em"}]]]]

    [:.gap
     {:flex-grow 2}]

    [:.page
     {:padding "1em 2em"}

     [:h1
      {:font-family "Montserrat"}]

     ;; exercises

     [:&.exercises

      [:>a
       {:display "block"}]

      [:>section

       [:>table.exercises
        {:border-collapse "collapse"}

        [:>thead

         [:>tr

          [:>th
           {:text-align "left"}]]]

        [:>tbody

         [:>tr

          ["&:nth-child(even)"
           {:background-color "#eee"}]

          [:>td
           {:padding "0.25em"}

           (>teachable)

           (>status)]]]]]]

     ;; exercise

     [:&.exercise

      {:max-width "40em"}

      [:>header
       {:display "flex"
        :align-items "center"
        :justify-content "space-between"
        :margin "2em 0 1em"}

       (>status)

       [:>.status
        {:font-size "1.5em"
         :margin-right "0.25em"}]

       [:>h1
        {:margin 0}]

       [:>.gap
        {:flex-grow 2}]

       [:>button.action
        {:background "#3cd1e3"
         :font-size "1em"
         :border-radius "0.2em"
         :padding "0.35em 0.5em"
         :font-family "Montserrat"
         :display "flex"
         :cursor "pointer"
         :align-items "center"
         :border-top "none"
         :border-left "none"
         :border-bottom "2px solid #106670"
         :border-right "2px solid #106670"}

        [:>svg
         {:width "1em"
          :height "1em"
          :margin-left "0.25em"}]]]

      [:>section
       {:margin "0 0 2em"
        :box-shadow "1px 1px 2px #b5b5b5"}

       [:>header
        {:background color-accent
         :color color-text-light
         :padding "0.75em 1em"}

        [:>h2
         {:margin 0
          :font-family "Montserrat"
          :font-size "1em"}]]]

      [:>section.instructions
       {:background "#eee"
        :padding "1em"
        :border-left [["0.5em" "solid" color-accent]]}

       [:p
        {:margin "0 0 1em"}]

       ["p:last-child"
        {:margin-bottom 0}]]

      [:>.related
       {:display "flex"
        :justify-content "flex-start"
        :align-items "top"}

       [:>h2
        {:display "inline"
         :font-size "1em"
         :font-family "Montserrat"
         :margin "0"}]

       [:>.exercises
        {:display "inline-block"
         :margin-left "0.5em"}

        [:>.exercise
         (>status)
         [:>.status
          {:vertical-align "middle"
           :margin-right "0.25em"}]]]]

      [:>section.functions

       [:>.body
        {:padding "1em"
         :background "#2b2b2b"}

        (>teachable)

        [:>.teachable
         {:color "white"}]

        [:>.function

         [:&.teaches
          {:font-weight "bold"}]

         [:&.uses
          {:color "#CCC"}]]]]

      [:>section.solution

       [:>header
        {:display "flex"
         :align-items "center"
         :justify-content "space-between"
         :cursor "pointer"}

        [:>svg
         {:height "1.25em"}]]]]

     ;; shortcuts

     [:&.shortcuts

      [:header

       [:>h1
        {:display "inline-block"}]

       [:>a>svg
        {:margin-left "0.5em"
         :width "1em"}]]]

     ;; pastebin

     [:&.pastebin

      [:>textarea
       {:width "50%"
        :height "50vh"
        :padding "1em"
        :color "#fff"
        :background "#2b2b2b"}]

      [:>button
       {:display "block"}

       [:>svg
        {:width "2em"
         :height "2em"}]]]

     ;; admin progress page

     [:&.admin-progress
      [:table.user-progress
       [:>tbody
        [:>tr
         ["&:nth-child(even)"
          {:background-color "#eee"}]]]]]]]])
