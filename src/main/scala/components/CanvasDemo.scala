package components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.Ref
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom.*
import org.scalajs.dom.WebGLRenderingContext.{ARRAY_BUFFER, COLOR_BUFFER_BIT, COMPILE_STATUS, FLOAT, FRAGMENT_SHADER, STATIC_DRAW, TRIANGLES, VERTEX_SHADER}
import org.w3c.dom.html.HTMLStyleElement

object CanvasDemo {

  object CanvasUtils {
    def webGLRenderContext(canvas: html.Canvas): WebGLRenderingContext =
      canvas.getContext("webgl2").asInstanceOf[WebGLRenderingContext]
  }

  val theHeight = "800"
  val theWidth = "1700"

  class Backend($: BackendScope[Unit, String]) {
    import CanvasUtils.*

    val canvasRef = Ref[html.Canvas]

    val setClicked =
      $.setState("CLICKED") >>
        canvasRef.foreach(canvas => {
          canvas.asInstanceOf[html.Canvas].height = theHeight.toInt
          canvas.asInstanceOf[html.Canvas].width = theWidth.toInt
          //canvas.width = theWidth.toInt

          val gl = webGLRenderContext(canvas)

          gl.clearColor(0.9, 0.9, 0.9, 1.0)
          gl.clear(COLOR_BUFFER_BIT)

          val vShader = gl.createShader(VERTEX_SHADER)
          val vertText = "attribute vec2 position; void main() {gl_Position = vec4(position, 0.0, 1.0);}"
          gl.shaderSource(vShader, vertText)
          gl.compileShader(vShader)

          // Fragment Shader variables
          // in vec4 gl_FragCoord;
          // in bool gl_FrontFacing;
          // in vec2 gl_PointCoord;

          val fShader = gl.createShader(FRAGMENT_SHADER)

          val fragText = """
          precision highp float;
          uniform vec4 color;
          void main() {
            vec4 s;
            s.r = gl_FragCoord.x / 300.0;
            s.g = 0.0;
            s.b = gl_FragCoord.y / 150.0;
            s.a = 1.0;

            if (gl_FragCoord.x < 5.0) {
             s.g = 1.0;
            }

            if (gl_FragCoord.x > 295.0) {
             s.g = 1.0;
            }

            if (gl_FragCoord.y < 5.0) {
             s.b = 1.0;
            }

            if (gl_FragCoord.y > 145.0) {
             s.b = 0.0;
            }

            gl_FragColor = s;
          }
        """

          val rayMarchText = """
precision highp float;
uniform vec4 color;

const int MAX_MARCHING_STEPS = 255;
const float MIN_DIST = 0.0;
const float MAX_DIST = 100.0;
const float EPSILON = 0.0001;

/**
 * Signed distance function for a cube centered at the origin
 * with width = height = length = 2.0
 */
float cubeSDF(vec3 p) {
    // If d.x < 0, then -1 < p.x < 1, and same logic applies to p.y, p.z
    // So if all components of d are negative, then p is inside the unit cube
    vec3 d = abs(p) - vec3(1.0, 1.0, 1.0);

    // Assuming p is inside the cube, how far is it from the surface?
    // Result will be negative or zero.
    float insideDistance = min(max(d.x, max(d.y, d.z)), 0.0);

    // Assuming p is outside the cube, how far is it from the surface?
    // Result will be positive or zero.
    float outsideDistance = length(max(d, 0.0));

    return insideDistance + outsideDistance;
}

/**
 * Signed distance function for a sphere centered at the origin with radius 1.0;
 */
float sphereSDF(vec3 p) {
    return length(p) - 1.0;
}

/**
 * Signed distance function describing the scene.
 *
 * Absolute value of the return value indicates the distance to the surface.
 * Sign indicates whether the point is inside or outside the surface,
 * negative indicating inside.
 */
float sceneSDF(vec3 samplePoint) {
    return cubeSDF(samplePoint);
}

/**
 * Return the shortest distance from the eyepoint to the scene surface along
 * the marching direction. If no part of the surface is found between start and end,
 * return end.
 *
 * eye: the eye point, acting as the origin of the ray
 * marchingDirection: the normalized direction to march in
 * start: the starting distance away from the eye
 * end: the max distance away from the ey to march before giving up
 */
float shortestDistanceToSurface(vec3 eye, vec3 marchingDirection, float start, float end) {
    float depth = start;
    for (int i = 0; i < MAX_MARCHING_STEPS; i++) {
        float dist = sceneSDF(eye + depth * marchingDirection);
        if (dist < EPSILON) {
			return depth;
        }
        depth += dist;
        if (depth >= end) {
            return end;
        }
    }
    return end;
}


/**
 * Return the normalized direction to march in from the eye point for a single pixel.
 *
 * fieldOfView: vertical field of view in degrees
 * size: resolution of the output image
 * fragCoord: the x,y coordinate of the pixel in the output image
 */
vec3 rayDirection(float fieldOfView, vec2 size, vec2 fragCoord) {
    vec2 xy = fragCoord - size / 2.0;
    float z = size.y / tan(radians(fieldOfView) / 2.0);
    return normalize(vec3(xy, -z));
}

/**
 * Using the gradient of the SDF, estimate the normal on the surface at point p.
 */
vec3 estimateNormal(vec3 p) {
    return normalize(vec3(
        sceneSDF(vec3(p.x + EPSILON, p.y, p.z)) - sceneSDF(vec3(p.x - EPSILON, p.y, p.z)),
        sceneSDF(vec3(p.x, p.y + EPSILON, p.z)) - sceneSDF(vec3(p.x, p.y - EPSILON, p.z)),
        sceneSDF(vec3(p.x, p.y, p.z  + EPSILON)) - sceneSDF(vec3(p.x, p.y, p.z - EPSILON))
    ));
}

/**
 * Lighting contribution of a single point light source via Phong illumination.
 *
 * The vec3 returned is the RGB color of the light's contribution.
 *
 * k_a: Ambient color
 * k_d: Diffuse color
 * k_s: Specular color
 * alpha: Shininess coefficient
 * p: position of point being lit
 * eye: the position of the camera
 * lightPos: the position of the light
 * lightIntensity: color/intensity of the light
 *
 * See https://en.wikipedia.org/wiki/Phong_reflection_model#Description
 */
vec3 phongContribForLight(vec3 k_d, vec3 k_s, float alpha, vec3 p, vec3 eye,
                          vec3 lightPos, vec3 lightIntensity) {
    vec3 N = estimateNormal(p);
    vec3 L = normalize(lightPos - p);
    vec3 V = normalize(eye - p);
    vec3 R = normalize(reflect(-L, N));

    float dotLN = dot(L, N);
    float dotRV = dot(R, V);

    if (dotLN < 0.0) {
        // Light not visible from this point on the surface
        return vec3(0.0, 0.0, 0.0);
    }

    if (dotRV < 0.0) {
        // Light reflection in opposite direction as viewer, apply only diffuse
        // component
        return lightIntensity * (k_d * dotLN);
    }
    return lightIntensity * (k_d * dotLN + k_s * pow(dotRV, alpha));
}

/**
 * Lighting via Phong illumination.
 *
 * The vec3 returned is the RGB color of that point after lighting is applied.
 * k_a: Ambient color
 * k_d: Diffuse color
 * k_s: Specular color
 * alpha: Shininess coefficient
 * p: position of point being lit
 * eye: the position of the camera
 *
 * See https://en.wikipedia.org/wiki/Phong_reflection_model#Description
 */
vec3 phongIllumination(vec3 k_a, vec3 k_d, vec3 k_s, float alpha, vec3 p, vec3 eye) {
    float iTime = 12.0;
    const vec3 ambientLight = 0.5 * vec3(1.0, 1.0, 1.0);
    vec3 color = ambientLight * k_a;

    vec3 light1Pos = vec3(4.0 * sin(iTime),
                          2.0,
                          4.0 * cos(iTime));
    vec3 light1Intensity = vec3(0.4, 0.4, 0.4);

    color += phongContribForLight(k_d, k_s, alpha, p, eye,
                                  light1Pos,
                                  light1Intensity);

    vec3 light2Pos = vec3(2.0 * sin(0.37 * iTime),
                          2.0 * cos(0.37 * iTime),
                          2.0);
    vec3 light2Intensity = vec3(0.4, 0.4, 0.4);

    color += phongContribForLight(k_d, k_s, alpha, p, eye,
                                  light2Pos,
                                  light2Intensity);
    return color;
}

/**
 * Return a transform matrix that will transform a ray from view space
 * to world coordinates, given the eye point, the camera target, and an up vector.
 *
 * This assumes that the center of the camera is aligned with the negative z axis in
 * view space when calculating the ray marching direction. See rayDirection.
 */
mat4 viewMatrix(vec3 eye, vec3 center, vec3 up) {
    // Based on gluLookAt man page
    vec3 f = normalize(center - eye);
    vec3 s = normalize(cross(f, up));
    vec3 u = cross(s, f);
    return mat4(
        vec4(s, 0.0),
        vec4(u, 0.0),
        vec4(-f, 0.0),
        vec4(0.0, 0.0, 0.0, 1)
    );
}

void main() {
    vec2 iResolution = vec2(1700.0, 800.0);
	  vec3 viewDir = rayDirection(45.0, iResolution, gl_FragCoord.xy);
    vec3 eye = vec3(8.0, 5.0, 7.0);

    mat4 viewToWorld = viewMatrix(eye, vec3(0.0, 0.0, 0.0), vec3(0.0, 1.0, 0.0));
    vec3 worldDir = (viewToWorld * vec4(viewDir, 0.0)).xyz;

    float dist = shortestDistanceToSurface(eye, worldDir, MIN_DIST, MAX_DIST);
    if (dist > MAX_DIST - EPSILON) {
      gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
  		return;
    }

    // The closest point on the surface to the eyepoint along the view ray
    vec3 p = eye + dist * worldDir;

    vec3 K_a = vec3(0.2, 0.2, 0.2);
    vec3 K_d = vec3(0.7, 0.2, 0.2);
    vec3 K_s = vec3(1.0, 1.0, 1.0);

    float shininess = 10.0;

    vec3 color = phongIllumination(K_a, K_d, K_s, shininess, p, eye);

    gl_FragColor = vec4(color, 1.0);
}

"""

          //gl.shaderSource(fShader, fragText)
          gl.shaderSource(fShader, rayMarchText)
          gl.compileShader(fShader)

          val program = gl.createProgram()
          gl.attachShader(program, vShader)
          gl.attachShader(program, fShader)
          gl.linkProgram(program)

          val tempVertices: scalajs.js.Array[Float] = scalajs.js.Array[Float]()
          tempVertices.push(-1f,-1f,   1f, -1f,  -1f, 1f,  -1f,1f,   1f, 1f,   1f, -1f)
          import scalajs.js.typedarray.Float32Array
          val vertices: Float32Array = new Float32Array(tempVertices)

          val buffer = gl.createBuffer()
          gl.bindBuffer(ARRAY_BUFFER, buffer)
          gl.bufferData(ARRAY_BUFFER, vertices, STATIC_DRAW)

          gl.useProgram(program)
          val progDyn = program.asInstanceOf[scalajs.js.Dynamic]
          progDyn.color = gl.getUniformLocation(program, "color")
          val temp2 = scalajs.js.Array[Double]()
          temp2.push(0.0f, 0.0f, 1f, 1f)
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
        <.div(<.canvas(
          ^.height := theHeight,
          ^.width := theWidth
        ).withRef(canvasRef))
      )
  }

  val Component =
    ScalaComponent.builder[Unit]
      .initialState("Click Me")
      .renderBackend[Backend]
      .build

  def apply() = Component()
}