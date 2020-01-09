(ns exercise-ui.server.routes
  (:require
   [bloom.commons.env :as env]
   [bloom.omni.auth.token :as token]
   [clojure.java.io :as io]
   [exercise-ui.server.db :as db]
   [exercise-ui.server.email :as email]
   [exercise-ui.utils :as utils]
   [org.httpkit.client :as http]
   [clojure.string :as string]))

(defonce pastebin (atom ""))

(def routes
  [[[:get "/css/codemirror.css"]
    (fn [request]
      {:headers {"Content-Type" "text/css"}
       :body (slurp (io/resource "cljsjs/codemirror/production/codemirror.min.css"))})]

   [[:get "/css/railscasts.css"]
    (fn [request]
      {:headers {"Content-Type" "text/css"}
       :body (slurp (io/resource "cljsjs/codemirror/common/theme/railscasts.css"))})]

   [[:get "/api/exercises"]
    (fn [request]
      {:status 200
       :body {:exercises (db/get-exercises)
              :order (db/get-exercise-order)}})]

   [[:get "/api/example"]
    (fn [request]
      {:status 200
       :body {:example (:body @(http/get "https://raw.githubusercontent.com/cognitory/clojure-exercises/master/examples/example_app_state.clj"))}})]

   [[:put "/api/progress"]
    (fn [request]
      (if (get-in request [:session :user-id])
        (do
          (db/set-exercise-status! (get-in request [:session :user-id])
                                   (get-in request [:params :exercise-id])
                                   (get-in request [:params :status]))
          {:status 200})
        {:status 401}))]

   [[:get "/api/session"]
    (fn [request]
      (if-let [user-id (get-in request [:session :user-id])]
        {:status 200
         :body {:user (db/get-user user-id)}}
        {:status 401}))]

   [[:delete "/api/session"]
    (fn [request]
      {:status 200
       :session nil})]

   [[:get "/api/pastebin"]
    (fn [request]
      {:status 200
       :body {:value @pastebin}})]

   [[:put "/api/pastebin"]
    (fn [request]
      (reset! pastebin (get-in request [:params :value]))
      {:status 200})]

   [[:get "/api/admin/progress"]
    (fn [request]
      {:status 200
       :body (db/users-progress)})]

   [[:post "/api/admin/progress"]
    (fn [{{:keys [user-id exercise-id]} :params :as request}]
      (db/set-exercise-status! user-id exercise-id :reviewed)
      {:status 200
       :body (db/users-progress)})]

   [[:post "/api/request-email"]
    (fn [{{:keys [email code]} :params :as request}]
      (if (and (not (string/blank? email))
               (= code (env/get :login-code)))
        (let [user-id (java.util.UUID/nameUUIDFromBytes (.getBytes email "UTF-8"))
              secret (get-in (env/get :omni/auth) [:token :secret])
              link (str (env/get :site-base-url)
                        "?"
                        (token/login-query-string user-id secret))]
          ;; send email
          (email/send-login-email! {:to email
                                    :login-link link})
          ;; middleware will add :user-id to session
          {:status 200
           :body {:ok true}})
        {:status 400
         :body {:error "Invalid parameters"}}))]
   ])
