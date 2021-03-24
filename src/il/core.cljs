(ns il.core
  (:require
   [il.calc :as calc]
   [goog.string :as gstring]
   [reagent.core :as r]
   [reagent.dom :as rdom]))

(def nbsp (gstring/unescapeEntities "&nbsp;"))

(def ^:const default-proportion 50)

(def ^:const default-price-change 0)

(def ^:const default-pool-value 1000)

(def proportion (r/atom default-proportion))

(def pool-value (r/atom default-pool-value))

(def price-change-a (r/atom default-price-change))

(def price-change-b (r/atom 0))

(def result (r/atom {}))

(defn- format-num [number]
  (if (.-toLocaleString number)
    (.toLocaleString number js/undefined {:minimumFractionDigits 2 :maximumSignificantDigits 2})
    number))

(defn- value->atom [e atom negatives?]
  (let [new-value (.. e -target -value)]
    (if (and negatives? (= new-value "-"))
      (reset! atom new-value)
      (let [parsed (js/parseInt new-value)]
        (reset! atom (if (= parsed parsed) parsed ""))))))

(defn- change-proportion [e]
  (value->atom e proportion false))

(defn- change-pool-value [e]
  (value->atom e pool-value false))

(defn- change-price-change-a [e]
  (value->atom e price-change-a true))

(defn- change-price-change-b [e]
  (value->atom e price-change-b true))

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
    [:table.table.result
     [:tbody
      [:tr
       [:td "Impermanent loss"]
       [:td (str (.toFixed (get @result :il) 2) nbsp "%")]]
      [:tr
       [:td "HODL value"]
       [:td (str "$" nbsp (format-num (get @result :hodl-value 0.0)))]
       ;;
       ]
      [:tr
       [:td "Pool value"]
       [:td (str "$" nbsp (format-num (get @result :pool-value 0.0)))]]]]))

(defn app []
  [:div.root
   [:h1 "Impremanent" [:br] "Loss" [:br] "Calculator"]
   [:form
    [:label "Pool proportion"]
    [:section.proportion
     [:span
      (str @proportion "/" (- 100 @proportion))]
     [:input.slider {:type "range"
                     :min 0
                     :max 100
                     :step 10
                     :value @proportion
                     :onChange change-proportion}]]

    [:label "Initial pool value"]
    [:section.pool-value
     [:span.unit "$"]
     [:input {:type "number" :value @pool-value :onChange change-pool-value}]]

    [:label "Price changes"]
    [:section.price-changes
     [:div
      [:input {:type "number"
               :autoComplete "off"
               :autoCorrect "off"
               :value @price-change-a
               :onChange change-price-change-a}]
      [:span.unit "%"]]
     [:div
      [:input {:type "number"
               :autoComplete "off"
               :autoCorrect "off"
               :value @price-change-b
               :onChange change-price-change-b}]
      [:span.unit "%"]]]

    [:section.submit
     [:button {:type "button" :onClick calculate} "Calculate"]]]

   [:hr]
   [render-result]])

(defn- render-app []
  (rdom/render [app] (js/document.getElementById "root")))

(defn main []
  (render-app))

(defn ^:dev/after-load after-load []
  (render-app))
