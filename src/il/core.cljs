(ns il.core
  (:require
   [il.calc :as calc]
   [reagent.core :as r]
   [reagent.dom :as rdom]))

(def ^:const default-proportion 50)

(def ^:const default-price-change -80)

(def ^:const default-pool-value 0)

(def proportion (r/atom default-proportion))

(def pool-value (r/atom default-pool-value))

(def price-change-a (r/atom default-price-change))

(def price-change-b (r/atom 0))

(def result (r/atom {}))

(defn- value->atom [e atom]
  (let [new-value (js/parseInt (.. e -target -value))]
    (reset! atom new-value)))

(defn- change-proportion [e]
  (value->atom e proportion))

(defn- change-pool-value [e]
  (value->atom e pool-value))

(defn- change-price-change-a [e]
  (value->atom e price-change-a))

(defn- change-price-change-b [e]
  (value->atom e price-change-b))

(defn- calculate [_]
  (let [il (calc/il @price-change-a @price-change-b @proportion)
        hodl-value (* (calc/hodl-value @price-change-a @price-change-b @proportion)
                      @pool-value)
        pool-value (* hodl-value
                      (/ (- 100.0 il) 100.0))]
    (swap! result
           assoc
           :il il
           :hodl-value hodl-value
           :pool-value pool-value)))

(defn- render-result []
  (when (seq @result)
    [:div.result
     [:table.table
      [:tbody
       [:tr
        [:td "Impermanent loss"]
        [:td (str (.toFixed (get @result :il) 2) "%")]]
       [:tr
        [:td "HODL value"]
        [:td (str (.toFixed (get @result :hodl-value 0.0) 2) "$")]]
       [:tr
        [:td "Pool value"]
        [:td (str (.toFixed (get @result :pool-value 0.0) 2) "$")]]]]]))

(defn app []
  [:div.root
   [:h1 "Impremanent Loss Calculator"]
   [:form
    [:h2 "Pool proportion"]
    [:div.proportion
     [:input.w-full {:type "range"
                     :min 0
                     :max 100
                     :step 10
                     :defaultValue default-proportion
                     :onChange change-proportion}]
     [:span-center
      (str @proportion "/" (- 100 @proportion))]]

    [:h2 "Initial pool value"]
    [:div.pool-value
     [:div
      [:input {:type "number" :defaultValue default-pool-value :onChange change-pool-value}]
      "$"]]

    [:h2 "Price changes"]
    [:div.price-changes
     [:div
      [:input {:type "number" :defaultValue default-price-change :onChange change-price-change-a}]
      "%"]
     [:div
      [:input {:type "number" :defaultValue default-price-change :onChange change-price-change-b}]
      "%"]]

    [:button {:type "button" :onClick calculate} "Calculate"]]

   [:hr]
   [render-result]])

(defn- render-app []
  (rdom/render [app] (js/document.getElementById "root")))

(defn main []
  (render-app))

(defn ^:dev/after-load after-load [] (render-app))
