(ns tmx-walls.core
  (:require [play-clj.core :refer [tiled-map tiled-map-layer
                                   map-objects
                                   shape color]]
            [play-clj.g2d :refer [texture!]]
            [play-clj.g2d-physics :refer [body! add-body!
                                          body-position!
                                          chain-shape
                                          fixture-def]]
            [tmx-walls.map-object :as mo]
            [tmx-walls.body :as b]
            [tmx-walls.utils :as u]))

(defn map-layer-walls
  "Returns all objects from the MapLayer which name
  is 'layer-name' as properties maps represented by
  MapWall.
  <clear?> defines whether the properties in the MapObject
  object must be cleared after the block is created.
  <clear?> is true by default."
  ([screen layer-name]
   (map-layer-walls screen layer-name true))
  ([screen layer-name clean-properties?]
   (let [ppm (u/get-tile-size screen)
         objects (->> layer-name
                      (tiled-map-layer screen ,,,)
                      map-objects
                      .iterator
                      iterator-seq)]
     (map #(mo/object->map-wall % ppm clean-properties?) objects))))

(defn walls!
  "Return a vector of wall (that contain box2d
  bodies) entities."
  [screen layer-name]
  (->> layer-name
       (map-layer-walls screen ,,,)
       (map (fn [map-wall]
              (assoc map-wall :body
                     (b/wall-body! screen map-wall))))))
