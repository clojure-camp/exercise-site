(ns exercise-ui.client.ui.login
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [dispatch]]
    [reagent.core :as r]))

(defn login-view []
  (let [user-email (r/atom "")
        code (r/atom "")
        error (r/atom nil)
        state (r/atom :log-in)]
    (fn []
      (case @state
        :waiting
        [:div.log-in "Requesting..."]

        :sent
        [:div.log-in
         "Check your email for a log-in link"
         [:button {:on-click (fn [] (reset! state :log-in))}
          "Try Again"]]

        :log-in
        [:div.log-in
         [:h1 "Log in to Exercises"]
         (when @error
           [:div.error "Invalid Email or Code"])
         [:form
          {:on-submit
           (fn [e]
             (.preventDefault e)
             (reset! error nil)
             (when (not (string/blank? @user-email))
               (dispatch [:request-email! @user-email @code
                          (fn [] (reset! state :sent))
                          (fn []
                            (reset! state :log-in)
                            (reset! error true))])
               (reset! state :waiting)))}
          [:label
           [:span "Email:"]
           [:input {:type "email"
                    :placeholder "foo@example.com"
                    :on-change (fn [e]
                                 (->> (.. e -target -value)
                                      (reset! user-email)))
                    :value @user-email}]]
          [:label
           [:span "Access Code:"]
           [:input {:type "text"
                    :placeholder ""
                    :value @code
                    :on-change (fn [e]
                                 (->> (.. e -target -value)
                                      (reset! code)))}]]
          [:button "Log In"]]]))))
