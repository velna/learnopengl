package com.vanix.easygl.learnopengl.c4_advancedopengl;

import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.input.Mouse;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.*;

import java.io.IOException;

public class C9_1_GeometryShaderHouses {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenglForwardCompat.set(true);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var program = Program.of();
             var vao = VertexArray.of();
             var vbo = Buffer.of(DataType.Float)) {

            window
                    .inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);

            graphics.depthTest().enable();

            program.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/9.1.geometry_shader.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/9.1.geometry_shader.fs")
                    .attachResource(Shader.Type.Geometry, "shaders/4_advanced_opengl/9.1.geometry_shader.gs")
                    .link();

            vbo.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, // top-left
                    0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // top-right
                    0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // bottom-right
                    -0.5f, -0.5f, 1.0f, 1.0f, 0.0f  // bottom-left
            });
            var triangleCount = vao.bind().enableAttributePointers(2f, 3f).countOfStride();

            var cubeDrawable = vao.drawingArrays(DrawMode.Points, triangleCount).build();
            while (!window.shouldClose()) {
                graphics.depthTest().enable().then()
                        .defaultFrameBuffer().setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                program.bind();
                cubeDrawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
