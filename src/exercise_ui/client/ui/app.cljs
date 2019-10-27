(ns exercise-ui.client.ui.app
  (:require
    [reagent.core :as r]
    [bloom.commons.pages :refer [path-for]]
    [re-frame.core :refer [dispatch subscribe]]
    [exercise-ui.client.pages :as pages]
    [exercise-ui.utils :as utils]))

(defn main-view []
  [:div
   [:div.header
    [:nav
     [:a {:href (path-for :exercises)} "exercises"]
     [:a {:href (path-for :setup)} "setup"]
     [:a {:href (path-for :shortcuts)} "shortcuts"]
     [:a {:href (path-for :pastebin)} "pastebin"]
     [:a {:href "https://www.clojuredocs.org"
          :target "_blank"
          :rel "noopener noreferrer"}
      "clojuredocs"]
     [:a {:href "https://cognitory.github.io/clojure-cheatsheet/"
          :target "_blank"
          :rel "noopener noreferrer"}
      "clj-cheatsheet"]]

    [:div.user
     @(subscribe [:user-id])
     [:button {:on-click (fn [e]
                           (dispatch [:log-out!]))}
      "Log Out"]]]
   [pages/current-page-view]])

(defn login-view []
  (let [user-id (r/atom "")]
    (fn []
      [:form {:on-submit
              (fn [e]
                (.preventDefault e)
                (dispatch [:log-in! @user-id]))}
       [:input {:type "text"
                :on-change (fn [e]
                             (reset! user-id (-> (.. e -target -value)
                                                 (utils/sanitize-user-id))))
                :value @user-id}]
       [:button "Enter"]])))

(defn app-view []
  [:div
   (if @(subscribe [:logged-in?])
     [main-view]
     [login-view])])
