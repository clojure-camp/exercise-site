(ns exercise-ui.server.routes
  (:require
    [exercise-ui.server.db :as db]
    [exercise-ui.utils :as utils]))

(defonce pastebin (atom ""))

(def routes
  [[[:get "/api/exercises"]
    (fn [request]
      {:status 200
       :body (db/get-exercises)})]

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

   [[:put "/api/session"]
    (fn [request]
      (let [user-id (-> (get-in request [:params :user-id])
                        (utils/sanitize-user-id))]
        {:status 200
         :session {:user-id user-id}
         :body {:user (db/get-user user-id)}}))]

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
      {:status 200})]])
