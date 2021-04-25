package com.demon.yu.camera.opengl

import android.opengl.GLES30
import com.example.mypractice.Logger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 1f, 0.0f,      // top
        -1f, -1f, 0.0f,    // bottom left
        1f, -1f, 0.0f      // bottom right
)
var squareCoords = floatArrayOf(
        -0.5f, 0.5f, 0.0f,      // top left
        -0.5f, -0.5f, 0.0f,      // bottom left
        0.5f, -0.5f, 0.0f,      // bottom right
        0.5f, 0.5f, 0.0f       // top right
)


fun loadShader(type: Int, shaderCode: String): Int {

    // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
    return GLES30.glCreateShader(type).also { shader ->

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)
        val status = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, status, 0)
        if (status[0] == GLES30.GL_FALSE) {
            Logger.debug("shape", "compile shader: " + type + ", error: " + GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
        }

    }
}

/**
 * 着色器脚本的基本流程
 * 1. 创建着色器脚本
 * 2. gl.glCreateShader（GL_VERTEX_SHADER｜GL_FRAGMENT_SHADER） 创建一个类型的shader
 * 3. gl.glShaderSource(shader,shaderCode) 绑定shader code
 * 4. gl.glCompileShader(shader) 编译shader
 * 5. gl.glCreateProgram 创建program
 * 6. gl.glAttachShader(program,shader) 添加shader
 * 7. gl.glLinkProgram(program) 链接程序，等待执行
 * 8. gl.glUseProgram(program) 使用program
 */
class Triangle {

    private val vertexShaderCode =
            "uniform mat4 uMVPMatrix;\n" +
                    "attribute vec4 vPosition;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * vPosition;\n" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform vec4 vColor;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = vColor;\n" +
                    "}"

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 0f)

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
    private var uMVPMatrixHandle: Int = 0
    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES30.glCreateProgram().also {

            // add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES30.glLinkProgram(it)

            GLES30.glValidateProgram(it)  // 让OpenGL来验证一下我们的shader program，并获取验证的状态

            val status = IntArray(1)

            GLES30.glGetProgramiv(it, GLES30.GL_VALIDATE_STATUS, status, 0)

            Logger.debug("shape", "mProgram status status=${GLES30.GL_TRUE == status[0]} infoLog  ${GLES30.glGetProgramInfoLog(it)}")
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment


        GLES30.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES30.glEnableVertexAttribArray(it)


            uMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")

            // Pass the projection and view transformation to the shader
            GLES30.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0)

            // Prepare the triangle coordinate data
            GLES30.glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GLES30.GL_FLOAT,
                    false,
                    vertexStride,
                    vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES30.glUniform4fv(colorHandle, 1, color, 0)
            }

            // Draw the triangle
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES30.glDisableVertexAttribArray(it)
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