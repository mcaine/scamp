import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^.*

object App {
  val Component =
    ScalaComponent.builder[Unit]
      .renderStatic(
        <.div(
          "RARA"
        )
      )
      .build

  def apply() = Component()
}

