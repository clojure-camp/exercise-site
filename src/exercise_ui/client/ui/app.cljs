(ns exercise-ui.client.ui.app
  (:require
    [reagent.core :as r]
    [bloom.commons.pages :refer [path-for]]
    [re-frame.core :refer [dispatch subscribe]]
    [exercise-ui.client.pages :as pages]
    [exercise-ui.utils :as utils]))

(defn main-view []
  [:div
   [:a {:href (path-for :home)} "Home"]
   [:a {:href (path-for :pastebin)} "Pastebin"]
   [:div
    @(subscribe [:user-name])
    [:button {:on-click (fn [e]
                          (dispatch [:log-out!]))}
     "Log Out"]]
   [pages/current-page-view]])

(defn login-view []
  (let [name (r/atom "")]
    (fn []
      [:form {:on-submit
              (fn [e]
                (.preventDefault e)
                (dispatch [:log-in! @name]))}
       [:input {:type "text"
                :name "name"
                :on-change (fn [e]
                             (reset! name (-> (.. e -target -value)
                                              (utils/sanitize-name))))
                :value @name}]
       [:button "Enter"]])))

(defn app-view []
  [:div
   (if @(subscribe [:logged-in?])
     [main-view]
     [login-view])])
