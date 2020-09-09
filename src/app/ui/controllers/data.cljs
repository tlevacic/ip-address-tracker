(ns app.ui.controllers.data
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.next.controllers.dataloader :as dl]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [app.api :as api]
            [app.settings :as settings]))

(def ip-regex #"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")

(defn parse-ip [s]
  (when (re-matches ip-regex s)
    (mapv js/parseInt (clojure.string/split s #"\."))))

(derive :data ::pipelines/controller)

(def pipelines
  {:get-address (pipeline! [value {:keys [state* deps-state*] :as ctrl}]
                           (let [input (.. value -current -value)
                                 is-ip? (parse-ip input)
                                 search (if is-ip?
                                          (str "ipAddress=" input)
                                          (str "domain=" input))]
                             (-> (pipeline! [value ctrl]
                                            (dl/req ctrl :dataloader api/get-address search)
                                            (edb/insert-named! ctrl :entitydb :address :address/current value)
                                            (reset! state* value))
                                 (pp/set-queue :load-address)
                                 pp/restartable)))})

(defmethod ctrl/prep :data [ctrl]
  (pipelines/register ctrl pipelines))

(defmethod ctrl/derive-state :data [_ state {:keys [entitydb]}]
  state)