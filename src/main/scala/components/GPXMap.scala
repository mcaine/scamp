package components

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.vdom.html_<^.^.onClick
import japgolly.scalajs.react.{Callback, ReactEvent, ReactMouseEventFromHtml, Ref, ScalaComponent}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.*
import typings.ol.coordinateMod.Coordinate
import typings.ol.geometryTypeMod.GeometryType
import typings.ol.mapBrowserEventMod.MapBrowserEvent
import typings.ol.mapEventMod.MapEvent
import typings.ol.olFeatureMod.Feature
import typings.ol.olMapMod.Map as OLMap
import typings.ol.pluggableMapMod.PluggableMap
import typings.ol.projMod.transform
import typings.ol.vectorMod.VectorLayer
import typings.ol.*

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object GPXMap {

  case class Props(gpx: String)

  class Backend($: BackendScope[Props, Unit]) {

    private val divRef = Ref[html.Div]

    def render = {
      //println(s"Rendering with props ${props}")

      <.div(
        ^.className := "map-container",
      ).withRef(divRef)
    }

    val draw = (p: Props) => {
      divRef.foreach(containerDiv => {

        println(s"Calling init ${p}")

        // Vector layer from GPX track
        val gpxLayer = typings.ol.vectorMod.default(new vectorMod.Options {
          source = new typings.ol.sourceVectorMod.default(new typings.ol.sourceVectorMod.Options {
            url = p.gpx
            format = new formatMod.GPX()
          })
          style = new styleMod.Style(new styleStyleMod.Options {
            stroke = new typings.ol.strokeMod.default(new strokeMod.Options {
              color = js.Array(255, 100, 100)
              width = 3
            })
          })
        })

        val osmLayer = new tileMod.default(new tileMod.Options {
          source = new sourceMod.OSM()
        })

        val opts = new pluggableMapMod.MapOptions {
          target = containerDiv

          layers = js.Array(
            osmLayer,
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

    def init: Callback = $.props >>= draw
  }

  val Component =
    ScalaComponent.builder[Props]
      .renderBackend[Backend]
      .componentDidMount(_.backend.init)
      .build

  def apply(gpxFile: String) = Component(Props(gpx = gpxFile))
}
