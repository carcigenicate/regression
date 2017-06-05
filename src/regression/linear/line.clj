(ns regression.linear.line
  (:require [quil.core :as q]))

(defrecord Line [m b]
  Object
  (toString [self] (str "y = (" m ")x + (" b ")")))

(defn y= [line x]
  (let [{:keys [m b]} line]
    (+ (* m x) b)))

