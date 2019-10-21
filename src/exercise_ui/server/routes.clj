(ns exercise-ui.server.routes
  (:require
    [exercise-ui.server.db :as db]))

(defonce pastebin (atom ""))

(def routes
  [[[:get "/api/exercises"]
    (fn [request]
      {:status 200
       :body (db/get-exercises)})]

   [[:get "/api/pastebin"]
    (fn [request]
      {:status 200
       :body {:value @pastebin}})]

   [[:put "/api/pastebin"]
    (fn [request]
      (reset! pastebin (get-in request [:params :value]))
      {:status 200})]])
