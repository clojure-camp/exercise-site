(ns exercise-ui.client.ui.app
  (:require
    [reagent.core :as r]
    [bloom.commons.pages :refer [path-for]]
    [bloom.commons.fontawesome :as fa]
    [re-frame.core :refer [dispatch subscribe]]
    [exercise-ui.client.pages :as pages]
    [exercise-ui.utils :as utils]))

(defn header-view []
  [:div.header
   [:h1 "{}"]
   [:nav
    [:a {:href (path-for :exercises)
         :class (when (= @pages/current-page-id
                         :exercises)
                  "active")}
     "exercises"]
    [:a {:href (path-for :setup)
         :class (when (= @pages/current-page-id
                         :setup)
                  "active")}
     "setup"]
    [:a {:href (path-for :shortcuts)
         :class (when (= @pages/current-page-id
                         :shortcuts)
                  "active")}
     "shortcuts"]
    [:a {:href (path-for :pastebin)
         :class (when (= @pages/current-page-id
                         :pastebin)
                  "active")}
     "pastebin"]
    [:a {:href (path-for :reference-example)
         :class (when (= @pages/current-page-id
                         :reference-example)
                  "active")}
     "reference example"]
    [:a {:href "https://www.clojuredocs.org"
         :target "_blank"
         :rel "noopener noreferrer"}
     "clojuredocs"]
    [:a {:href "https://cognitory.github.io/clojure-cheatsheet/"
         :target "_blank"
         :rel "noopener noreferrer"}
     "clj-cheatsheet"]]

   [:div.gap]

   [:div.user
    @(subscribe [:user-id])
    [:button {:on-click (fn [e]
                          (dispatch [:log-out!]))}
     [fa/fa-sign-out-alt-solid]]]])

(defn main-view []
  [:div
   [header-view]
   [pages/current-page-view]])

(defn login-view []
  (let [user-id (r/atom "")]
    (fn []
      [:div.log-in
       [:h1 "Bell Media Clojure Training"]
       [:form
        {:on-submit
         (fn [e]
           (.preventDefault e)
           (dispatch [:log-in! @user-id]))}
        [:input {:type "text"
                 :on-change (fn [e]
                              (reset! user-id (-> (.. e -target -value)
                                                  (utils/sanitize-user-id))))
                 :value @user-id}]
        [:button "Log In"]]])))

(defn app-view []
  [:div
   (if @(subscribe [:logged-in?])
     [main-view]
     [login-view])])
