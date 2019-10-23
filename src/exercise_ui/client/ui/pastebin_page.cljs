(ns exercise-ui.client.ui.pastebin-page
  (:require
    [re-frame.core :refer [dispatch subscribe]]))

(defn pastebin-page-view []
  [:div.page
   [:h1 "Pastebin"]
   [:button {:on-click (fn [_]
                         (dispatch [:fetch-pastebin!]))} "Pull"]
   [:textarea
    {:on-change (fn [e]
                  (dispatch [:set-pastebin! (.. e -target -value)]))
     :value @(subscribe [:pastebin])}]])
