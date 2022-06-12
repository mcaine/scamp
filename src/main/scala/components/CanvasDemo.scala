package components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^.*

object CanvasDemo {

  val Component =
    ScalaComponent.builder[Unit]
      .renderStatic(
        <.div(
          <.div("CANVAS DEMO"),
          <.div(<.canvas())
        )
      )
      .build

  def apply() = Component()

}
