package com.vanix.easygl.learnopengl.c4_advancedopengl;

import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.input.Mouse;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.*;
import org.joml.Vector2f;

import java.io.IOException;

public class C10_1_InstancingQuads {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenglForwardCompat.enable();
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var instanceVbo = Buffer.of(DataType.Float);
             var quadVao = VertexArray.of();
             var quadVbo = Buffer.of(DataType.Float);
             var program = Program.of()) {

            window.inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);
            graphics.depthTest().enable();

            program.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/10.1.instancing.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/10.1.instancing.fs")
                    .link();

            // set up vertex data (and buffer(s)) and configure vertex attributes
            // ------------------------------------------------------------------
            quadVbo.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    // positions     // colors
                    -0.05f, 0.05f, 1.0f, 0.0f, 0.0f,
                    0.05f, -0.05f, 0.0f, 1.0f, 0.0f,
                    -0.05f, -0.05f, 0.0f, 0.0f, 1.0f,

                    -0.05f, 0.05f, 1.0f, 0.0f, 0.0f,
                    0.05f, -0.05f, 0.0f, 1.0f, 0.0f,
                    0.05f, 0.05f, 0.0f, 1.0f, 1.0f
            });
            var vertexAttr = quadVao.bind().enableAttributePointers(2f, 3f);

            var translations = new Vector2f[100];
            var index = 0;
            var offset = 0.1f;
            for (int y = -10; y < 10; y += 2) {
                for (int x = -10; x < 10; x += 2) {
                    translations[index++] = new Vector2f(x / 10.0f + offset, y / 10.0f + offset);
                }
            }
            instanceVbo.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, translations);
            vertexAttr.nextAttribute().enablePointers(2f).setDivisor(1);

            var drawable = quadVao.drawingArrays(DrawMode.Triangles, 0, 6).instanced(translations.length).build();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.2f, 0.3f, 0.3f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                program.bind();
                drawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }


}
