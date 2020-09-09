(ns app.api
  (:require [keechma.next.toolbox.ajax :refer [GET POST DELETE PUT]]
            [app.settings :as settings]
            [promesa.core :as p]))

(def default-request-config
  {:response-format :json
   :keywords? true
   :format :json})

(defn add-auth-header [req-params jwt]
  (if jwt
    (assoc-in req-params [:headers :authorization] (str "Token " jwt))
    req-params))

(defn add-params [req-params params]
  (if params
    (assoc req-params :params params)
    req-params))

(defn req-params [& {:keys [jwt data]}]
  (-> default-request-config
      (add-auth-header jwt)
      (add-params data)))


(defn process-data [data]
  {:ip (:ip data)
   :location (:city (:location data))
   :timezone (:timezone (:location data))
   :country (:country (:location data))
   :lat (:lat (:location data))
   :lng (:lng (:location data))})

(defn get-address [search]
  (->> (GET (str "https://geo.ipify.org/api/v1?apiKey=at_2NmnZxS2KNpmoOcPfGzllRZnoVKXp&" search)
            (req-params))
       (p/map process-data)))