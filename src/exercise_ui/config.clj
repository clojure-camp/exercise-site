(ns exercise-ui.config
  (:require
    [bloom.commons.config :as config]))

(def config
  (config/read
    "config.edn"
    [:map
     [:http-port integer?]
     [:auth-token-secret string?]
     [:auth-cookie-secret string?]
     [:environment keyword?]
     [:exercise-data-path string?]
     [:user-data-path string?]
     [:site-base-url string?]
     [:login-code string?]
     [:mail-creds {:optional true}
      [:map
       [:from string?]
       [:user string?]
       [:pass string?]
       [:host string?]
       [:ssl boolean?]
       [:port {:optional true} integer?]]]]))




