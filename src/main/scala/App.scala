import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^.*
import components.{CallbackOptionDemo, CanvasDemo, GPXMap, OpenLayersDemo, StateSnapshotDemo, ThreeJSDemo}

object App {
  val Component =
    ScalaComponent.builder[Unit]
      .renderStatic(
        <.div(
//          ThreeJSDemo(),
          GPXMap("gpx/pennineway.gpx"),
//          OpenLayersDemo()
//          CanvasDemo(),
//          StateSnapshotDemo(),
//          CallbackOptionDemo(),
        )
      )
      .build

  def apply() = Component()
}

