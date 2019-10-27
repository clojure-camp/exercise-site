(ns exercise-ui.client.ui.pastebin-page
  (:require
    [bloom.commons.fontawesome :as fa]
    [re-frame.core :refer [dispatch subscribe]]))

(defn pastebin-page-view []
  [:div.page.pastebin

   [:textarea
    {:on-change (fn [e]
                  (dispatch [:set-pastebin! (.. e -target -value)]))
     :value @(subscribe [:pastebin])
     :placeholder "Paste code to share here"}]

   [:button {:on-click (fn [_]
                         (dispatch [:fetch-pastebin!]))}
    [fa/fa-sync-alt-solid]]])
