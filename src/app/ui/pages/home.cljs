(ns app.ui.pages.home
  "Example homepage 2 3"
  (:require [helix.dom :as d]
            [helix.core :as hx :refer [$]]
            [keechma.next.helix.core :refer [with-keechma use-sub dispatch]]
            [keechma.next.helix.lib :refer [defnc]]
            [keechma.next.helix.classified :refer [defclassified]]
            [app.ui.components.main :refer [Main]]
            [app.assets :refer [favicon icon-arrow icon-location pattern-bg]]
            [dv.cljs-emotion :refer [defstyled]]
            [oops.core :refer [ocall oset! oget]]
            [app.api :as api]
            [helix.hooks :as hooks]
            [promesa.core :as p]
            [app.ui.components.inputs :refer [wrapped-input]]
            [app.ui.components.hello :refer [Hello]]
            ["react-leaflet" :refer (Map Marker Popup TileLayer)]))

(def position {:lat "51.505" :lng "-0.09"})
(def zoom "13")

(defclassified HomepageWrapper :div "h-screen w-screen flex bg-gray-200 flex-col")

(defclassified ImagePatternWrapper :div "w-screen h-48 max-h-48 relative")

(def InputStyle "rounded-lg absolute top-0 left-0 rounded-tr-none rounded-br-none text-sm outline-none pl-4 py-2 border-1 focus:border-blue-500 text-gray-700")

(defstyled pattern-content-wrapper :div
  {:position "absolute"
   :top 0
   :left 0
   :right 0
   :bottom 0})

(defclassified PatternContentWrapper pattern-content-wrapper
  "flex flex-col mt-4")

(defstyled data-wrapper :div
  {:position "absolute"
   :left "50%"
   :z-index 999
   :transform "translateX(-50%)"
   :top "120px"
   :grid-template-columns "1fr 1fr 1fr 1fr"})

(defclassified DataWrapper data-wrapper "grid shadow-lg rounded-lg bg-white sm:w-11/12 md:w-9/12 lg:w-8/12 h-32")

(defclassified GridItem :div "relative px-4 py-4")

(defclassified Title :div "w-full flex justify-start text-xs text-gray-600")

(defclassified Content :div "w-full flex justify-start text-sm font-bold mt-4 h-full")

(defnc LeafletMap [data]
  (let [address (:address data)
        props (:props data)
        lat (if (:lat address) (js/parseFloat (:lat address)) 43.5081)
        lng (if (:lng address) (js/parseFloat (:lng address)) 16.4402)]
    (d/div {:class "w-screen h-full"}
           ($ Map {:center          (clj->js [lat lng])
                   :style           #js {:minHeight "465px"
                                         :zIndex    "0"}
                   :zoom            12
                   :minZoom         1
                   :zoomSnap        0
                   :zoomDelta       0.20
                   :scrollWheelZoom false
                   :className       "rounded-lg h-full"}
              ($ TileLayer {:url         "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            :attribution "OOGIS RL, OpenStreetMap &copy;"})))))

(defnc HomeRenderer [props]
  (let [ref (hooks/use-ref nil)
        address (use-sub props :data)
        ip (or (:ip address) "102.123.2.1")
        location (or (:location address) "Zagreb")
        country (or (:country address) "Croatia")
        timezone (or (:timezone address) "+2")]
    ($ HomepageWrapper
       ($ ImagePatternWrapper
          (d/img {:src pattern-bg :style {:width "100%" :height "100%"}})
          ($ PatternContentWrapper
             (d/div {:class "flex justify-center text-white text-2xl"} "IP Address Tracker")
             (d/div {:class "flex justify-center mt-4 relative mx-auto"
                     :style {:width  "500px"
                             :height "38px"}}
                    (d/input {:type        "text"
                              :class       InputStyle
                              :ref ref
                              :style       {:width "450px"}
                              :placeholder "Search for any IP address or domain"})
                    (d/button
                     {:class    "bg-black absolute top-0 bottom-0 w-10 flex justify-center items-center rounded-lg rounded-tl-none rounded-bl-none"
                      :style    {:left "450px"}
                      :on-click #(dispatch props :data :get-address ref)}
                     (d/img {:src icon-arrow})))
             ($ DataWrapper
                ($ GridItem
                   (d/div {:class "absolute right-0 bg-gray-400"
                           :style {:width     "1px"
                                   :height    "50%"
                                   :top       "50%"
                                   :transform "translateY(-50%)"}})
                   ($ Title "IP ADDRESS")
                   ($ Content ip))
                ($ GridItem (d/div {:class "absolute right-0 bg-gray-400"
                                    :style {:width     "1px"
                                            :height    "50%"
                                            :top       "50%"
                                            :transform "translateY(-50%)"}})
                   ($ Title "LOCATION")
                   ($ Content location))
                ($ GridItem (d/div {:class "absolute right-0 bg-gray-400"
                                    :style {:width     "1px"
                                            :height    "50%"
                                            :top       "50%"
                                            :transform "translateY(-50%)"}})
                   ($ Title "COUNTRY")
                   ($ Content country))
                ($ GridItem
                   ($ Title "TIMEZONE")
                   ($ Content (str "UTC " timezone))))))
       ($ LeafletMap {:address address & props}))))

(def Home (with-keechma HomeRenderer))