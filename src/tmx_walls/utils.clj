(ns tmx-walls.utils
  (:require [play-clj.core :refer [tiled-map!]]))

(defmacro get-tile-size
  [screen]
  `(.get (tiled-map! ~screen :get-properties) "tilewidth"))

(defn vertices->vertices
  "Converts a shape's vertices from map coordiantes
  to world coordinates."
  [ppm vertices]
  (->> (vec vertices)
       (map #(/ % ppm) ,,,)
       float-array))

(defmacro rectangle-vertices
  [x y width height]
  `(float-array [~x ~y
                 ~x (+ ~y ~height)
                 (+ ~x ~width) (+ ~y ~height)
                 (+ ~x ~width) ~y
                 ~x ~y]))

