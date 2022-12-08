(ns exercise-ui.server.email
  (:require
   [bloom.omni.auth.token :as token]
   [postal.core :as postal]
   [exercise-ui.config :refer [config]]))

(defn send-login-email!
  [{:keys [to login-link]}]
  (prn "Sending login link"
       (postal/send-message
         (:mail-creds config)
         {:from (:from (:mail-creds config))
          :to to
          :subject "Your Login Link"
          :body
          [:alternative
           {:type "text/plain; charset=utf-8"
            :content (str "Go to this link to log in to the Clojure Workshop site:\n"
                          login-link)}
           {:type "text/html; charset=utf-8"
            :content (str "<html><body><p>Click <a href=\"" login-link "\">here</a> to log in to the Clojure Workshop site:</p><p><a href=\"" login-link "\">" login-link "</a></p></body></html>")}]})))
