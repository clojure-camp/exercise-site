(ns exercise-ui.exercises.schema)

(def Code
  :any
  #_[:or [:vector :any] :string])

(defn I18N [val-schema]
  [:map-of :keyword val-schema])

(def ExerciseInput
  ;; raw exercise files, pre-normalization
  [:map {:closed true}
   [:title (I18N :string)]
   [:category [:enum :starter :learning-functions :synthesis]]
   [:difficulty [:enum :high :mid :low]]
   [:instructions
    [:or
     :string
     [:vector Code]
     [:map-of :keyword [:vector Code]]]]
   [:uses [:set [:or :keyword :symbol :string]]]
   ;; optional:
   [:related {:optional true} [:set :string]]
   [:function-template {:optional true} Code]
   [:source {:optional true} :string]
   [:test-cases {:optional true} [:vector [:map
                                           [:input Code]
                                           [:output Code]]]]
   [:solution {:optional true} [:or Code [:vector Code]]]
   [:teaches {:optional true} [:set [:or :keyword :symbol :string]]]])

(def Exercise
  ;; parsed, normalized exercise
  [:map {:closed true}
   [:exercise/id :string]
   [:exercise/title (I18N :string)]
   [:exercise/category [:enum
                        :exercise.category/starter
                        :exercise.category/learning-functions
                        :exercise.category/synthesis]]
   [:exercise/difficulty [:enum
                          :exercise.difficulty/high
                          :exercise.difficulty/mid
                          :exercise.difficulty/low]]
   [:exercise/instructions
    [:or
     :string
     [:vector Code]
     [:map-of :keyword [:vector Code]]]]
   [:exercise/uses [:set [:or :keyword :symbol :string]]]
   [:exercise/related [:set :string]]
   [:exercise/function-template [:maybe [:vector Code]]]
   [:exercise/test-cases [:vector [:map
                                   [:input Code]
                                   [:output Code]]]]
   [:exercise/solution [:maybe [:vector Code]]]
   [:exercise/teaches [:set [:or :keyword :symbol :string]]]])
