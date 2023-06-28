(ns exercise-ui.client.ui.partials.teachable)

(defn docs-link
  [fn-symbol]
  (str "https://clojuredocs.org/"
       (if-let [fn-ns (namespace fn-symbol)]
         fn-ns
         "clojure.core")
       "/" (name fn-symbol)))

(defn teachable-view
  [teachable class]
  (cond
    (keyword? teachable)
    [:span.teachable.concept
     {:class class}
     (name teachable)]

    (symbol? teachable)
    [:a.teachable.function
     {:class class
      :href (docs-link teachable)
      :target "_blank"
      :rel "noopener noreferrer"}
     (str teachable)]

    :else
    [:span.teachable.function
     {:class class}
     (str teachable)]))
