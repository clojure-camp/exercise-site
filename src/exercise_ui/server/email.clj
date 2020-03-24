(ns exercise-ui.server.email
  (:require
   [bloom.commons.env :as env]
   [bloom.omni.auth.token :as token]
   [postal.core :as postal]))

(defn send-login-email!
  [{:keys [to login-link]}]
  (prn "Sending login link"
       (postal/send-message
         (env/get :mail-creds)
         {:from (:from (env/get :mail-creds))
          :to to
          :subject "Your Login Link"
          :body [:alternative
                 {:type "text/plain; charset=utf-8"
                  :content (str "Go to this link to log in to the Clojure Workshop site:\n"
                                login-link)}
                 {:type "text/html; charset=utf-8"
                  :content (str "<p>Click <a href=\"" login-link "\">here</a> to log in to the Clojure Workshop site")} ]})))
