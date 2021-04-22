package com.demon.yu.camera.opengl

import android.opengl.GLES31
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 0.622008459f, 0.0f,      // top
        -0.5f, -0.311004243f, 0.0f,    // bottom left
        0.5f, -0.311004243f, 0.0f      // bottom right
)
var squareCoords = floatArrayOf(
        -0.5f, 0.5f, 0.0f,      // top left
        -0.5f, -0.5f, 0.0f,      // bottom left
        0.5f, -0.5f, 0.0f,      // bottom right
        0.5f, 0.5f, 0.0f       // top right
)


fun loadShader(type: Int, shaderCode: String): Int {

    // create a vertex shader type (GLES31.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES31.GL_FRAGMENT_SHADER)
    return GLES31.glCreateShader(type).also { shader ->

        // add the source code to the shader and compile it
        GLES31.glShaderSource(shader, shaderCode)
        GLES31.glCompileShader(shader)
    }
}

class Triangle {

    private val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var vertexBuffer: FloatBuffer =
            // (number of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(triangleCoords)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }

    private var mProgram: Int
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        val vertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES31.glCreateProgram().also {

            // add the vertex shader to program
            GLES31.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES31.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES31.glLinkProgram(it)
        }
    }

    fun draw() {
        // Add program to OpenGL ES environment
        GLES31.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES31.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES31.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES31.glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GLES31.GL_FLOAT,
                    false,
                    vertexStride,
                    vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES31.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES31.glUniform4fv(colorHandle, 1, color, 0)
            }

            // Draw the triangle
            GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES31.glDisableVertexAttribArray(it)
        }
    }
}

class Square {

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    // initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
            // (# of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(squareCoords.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(squareCoords)
                    position(0)
                }
            }

    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
            // (# of coordinate values * 2 bytes per short)
            ByteBuffer.allocateDirect(drawOrder.size * 2).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(drawOrder)
                    position(0)
                }
            }
}