package components

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.vdom.html_<^.^.onClick
import japgolly.scalajs.react.{Callback, ReactEvent, ReactMouseEventFromHtml, Ref, ScalaComponent}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.*
import typings.ol.mapBrowserEventMod.MapBrowserEvent
import typings.ol.projMod.transform
import typings.ol.vectorMod.VectorLayer
import typings.ol.*

import scala.scalajs.js

object GPXMap {

  case class Props(gpx: String)

  class Backend($: BackendScope[Props, Unit]) {

    private val divRef = Ref[html.Div]

    def render =
      <.div(
        ^.className := "map-container",
      ).withRef(divRef)

    val draw = (p: Props) =>
      divRef.foreach(containerDiv => {

        val gpxSource = new sourceVectorMod.default[geometryMod.default](new sourceVectorMod.Options {
          url = p.gpx
          format = new formatMod.GPX()
        })

        // Vector layer from GPX track
        val gpxLayer = vectorMod.default(new baseVectorMod.Options {
          source = gpxSource
          style = new styleMod.Style(new styleStyleMod.Options {
            stroke = new strokeMod.default(new strokeMod.Options {
              color = js.Array(255, 100, 100)
              width = 3
            })
          })
        })

        val osmLayer = new tileMod.default(new baseTileMod.Options {
          source = new sourceMod.OSM()
        })

        val ordnanceSurveyAPIKey = "cOWdIrp8IhFA3VXfp0NC0GO0FTDwNwX5"

        val ordnanceSurveyLayer = new tileMod.default(new baseTileMod.Options {
          source = new sourceMod.XYZ(new xyzMod.Options {
            url = s"https://api.os.uk/maps/raster/v1/zxy/Outdoor_3857/{z}/{x}/{y}.png?key=${ordnanceSurveyAPIKey}"
          })
          //opacity = 0.5
        })

        val opts = new pluggableMapMod.MapOptions {
          target = containerDiv

          layers = js.Array(
            osmLayer,
            ordnanceSurveyLayer,
            gpxLayer
          )

          view = new viewMod.default(new viewMod.ViewOptions {
            center = js.Array(0, 0)
            zoom = 5
          })
        }

        val theMap = new olMapMod.default(opts)

        gpxSource.on("addfeature", (feature) => {
          //println(s"added feature ${feature}")
          theMap.getView().fit(gpxSource.getExtent())
        })

        theMap.on("click", (event) => {
          val mapBrowserEvent: MapBrowserEvent[UIEvent] = event.asInstanceOf[MapBrowserEvent[UIEvent]]
          val coords = mapBrowserEvent.coordinate
          val transformedCoord = transform(coords, "EPSG:3857", "EPSG:4326")
          println(s"Clicked at: ${transformedCoord(0)} ${transformedCoord(1)}")
        })
      })

    def init = $.props >>= draw
  }

  val Component =
    ScalaComponent.builder[Props]
      .renderBackend[Backend]
      .componentDidMount(_.backend.init)
      .build

  def apply(gpxFile: String) = Component(Props(gpx = gpxFile))
}
