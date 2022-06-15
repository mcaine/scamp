package components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.Ref
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom.*
import org.scalajs.dom.WebGLRenderingContext.{COLOR_BUFFER_BIT, VERTEX_SHADER, FRAGMENT_SHADER, ARRAY_BUFFER, STATIC_DRAW, FLOAT, TRIANGLES}

object CanvasDemo {

  class Backend($: BackendScope[Unit, String]) {

    val canvasRef = Ref[html.Canvas]

    val setClicked =
      $.setState("CLICKED") >>
      canvasRef.foreach(canvas => {
        val gl: WebGLRenderingContext = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]
        gl.clearColor(0.4, 0.5, 0.5, 0.8)
        gl.clear(COLOR_BUFFER_BIT)

        val vShader = gl.createShader(VERTEX_SHADER)
        val vertText = "attribute vec2 position; void main() {gl_Position = vec4(position, 0, 1);}"
        gl.shaderSource(vShader, vertText)

        gl.shaderSource(vShader, vertText)
        gl.compileShader(vShader)

        val fShader = gl.createShader(FRAGMENT_SHADER)
        val fragText = "precision highp float; uniform vec4 color; void main() {gl_FragColor = vec4(0, 1, 0, 1);}"
        gl.shaderSource(fShader, fragText)
        gl.compileShader(fShader)

        val program = gl.createProgram()
        gl.attachShader(program, vShader)
        gl.attachShader(program, fShader)
        gl.linkProgram(program)

        val tempVertices: scalajs.js.Array[Float] = scalajs.js.Array[Float]()
        tempVertices.push(-0.3f,-0.3f,   0.3f,-0.3f,  0.0f,0.3f,  0.2f,0.2f,   0.6f, 0.6f,   0.4f, -0.4f)
        import scalajs.js.typedarray.Float32Array
        val vertices: Float32Array = new Float32Array(tempVertices)

        val buffer = gl.createBuffer()
        gl.bindBuffer(ARRAY_BUFFER, buffer)
        gl.bufferData(ARRAY_BUFFER, vertices, STATIC_DRAW)

        gl.useProgram(program)
        val progDyn = program.asInstanceOf[scalajs.js.Dynamic]
        progDyn.color = gl.getUniformLocation(program, "color")
        val temp2 = scalajs.js.Array[Double]()
        temp2.push(0f, 1f, 0.5f, 1.0f)
        gl.uniform4fv(progDyn.color.asInstanceOf[WebGLUniformLocation], temp2)

        progDyn.position = gl.getAttribLocation(program, "position")
        gl.enableVertexAttribArray(progDyn.position.asInstanceOf[Int])
        gl.vertexAttribPointer(progDyn.position.asInstanceOf[Int], 2, FLOAT, false, 0, 0)
        gl.drawArrays(TRIANGLES, 0, vertices.length / 2)
      })

    def render(state: String) =
      <.div(
        <.button(
          ^.onClick --> setClicked,
          state),
        <.div(<.canvas().withRef(canvasRef))
      )
  }

  val Component =
    ScalaComponent.builder[Unit]
      .initialState("Click Me")
      .renderBackend[Backend]
      .build

  def apply() = Component()
}
