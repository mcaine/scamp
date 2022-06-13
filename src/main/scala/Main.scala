import org.scalajs.dom
import dom.{document, window}
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

object Main {

  @JSExportTopLevel("main")
  def main(): Unit = {
    //println("Starting 'SCAMP' app...")

    val rootElement = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    App().renderIntoDOM(rootElement)
  }
}
