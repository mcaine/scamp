package components

import japgolly.scalajs.react.{Callback, ReactEvent, ReactMouseEventFromHtml, Ref, ScalaComponent}
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.vdom.html_<^.^.onClick
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.*
import typings.ol.coordinateMod.Coordinate
import typings.ol.geometryTypeMod.GeometryType
import typings.ol.olMapMod.Map as OLMap
import typings.ol.vectorMod.VectorLayer
import typings.ol.pluggableMapMod.PluggableMap
import typings.ol.mapEventMod.MapEvent
import typings.ol.mapBrowserEventMod.MapBrowserEvent
import typings.ol.olFeatureMod.Feature
import typings.ol.{circleMod, eventMod, formatMod, geometryMod, geometryTypeMod, gpxMod, olFeatureMod, olMapMod, olTileMod, pixelMod, pluggableMapMod, sourceMod, strokeMod, styleMod, styleStyleMod, tileLayerMod, tileMod, vectorLayerMod, vectorMod, viewMod}
import typings.ol.projMod.transform

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object OpenLayersDemo {

  class Backend($: BackendScope[Unit, Unit]) {

    private val divRef = Ref[html.Div]

    def render =
      <.div(
        ^.className := "map-container",
      ).withRef(divRef)

    def init: Callback = divRef.foreach(containerDiv => {

      // Vector layer from GPX track
      val gpxLayer = typings.ol.vectorMod.default(new vectorMod.Options {
        source = new typings.ol.sourceVectorMod.default(new typings.ol.sourceVectorMod.Options {
          url = "gpx/southernuplandway.gpx"
          format = new formatMod.GPX()
        })
        style = new styleMod.Style(new styleStyleMod.Options {
          stroke = new typings.ol.strokeMod.default(new strokeMod.Options {
            color = js.Array(255, 100, 100)
            width = 3
          })
        })
      })

      // Vector Features
      val feature = new olFeatureMod.default(new circleMod.default(js.Array(0, 6706150), 100))
      val feature2 = olFeatureMod.default(new circleMod.default(js.Array(0, 6706150), 150))
      val vectorLayer: VectorLayer = typings.ol.vectorMod.default(new vectorMod.Options {
        source = new typings.ol.sourceVectorMod.default(new typings.ol.sourceVectorMod.Options {
          features = js.Array(feature, feature2)
        })
        style = new styleMod.Style(new typings.ol.styleStyleMod.Options {
          stroke = new typings.ol.strokeMod.default(new strokeMod.Options {
            color = js.Array(100, 100, 255)
            width = 2
          })
        })
      })

      val opts = new pluggableMapMod.MapOptions {
        target = containerDiv

        layers = js.Array(

          new tileMod.default(new tileMod.Options {
            source = new sourceMod.OSM()
          }),
          vectorLayer,
          gpxLayer
        )

        view = new viewMod.default(new viewMod.ViewOptions {
          center = js.Array(0, 6706150)
          zoom = 18
        })
      }

      val theMap = new olMapMod.default(opts)

      theMap.on("click", (event) => {
        val mapBrowserEvent: MapBrowserEvent = event.asInstanceOf[MapBrowserEvent]
        val pluggableMap: PluggableMap = mapBrowserEvent.map
        val coordinates: Coordinate = pluggableMap.getCoordinateFromPixel(mapBrowserEvent.pixel)
        val transformedCoord = transform(coordinates, "EPSG:3857", "EPSG:4326")
        println(s"Clicked at: ${transformedCoord(0)} ${transformedCoord(1)}")
      })
    })
  }

  val Component =
    ScalaComponent.builder[Unit]
      //.initialState(State(None, None))
      .renderBackend[Backend]
      .componentDidMount(_.backend.init)
      .build

  def apply() = Component()
}
