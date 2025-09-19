package com.vanix.easygl.learnopengl.c4_advancedopengl;

import com.vanix.easygl.application.g3d.ControllableCamera;
import com.vanix.easygl.commons.bufferio.BufferStruct;
import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.input.Mouse;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.*;
import lombok.Data;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

public class C8_AdvancedGlslUbo {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenglForwardCompat.set(true);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var programRed = Program.of();
             var programGreen = Program.of();
             var programBlue = Program.of();
             var programYellow = Program.of();
             var ubo = Buffer.of(DataType.Byte);
             var cubeVAO = VertexArray.of();
             var cubeVBO = Buffer.of(DataType.Float)) {

            window
                    .inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);

            graphics.depthTest().enable();

            programRed.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/8.advanced_glsl.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/8.red.fs")
                    .link();
            programGreen.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/8.advanced_glsl.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/8.green.fs")
                    .link();
            programBlue.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/8.advanced_glsl.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/8.blue.fs")
                    .link();
            programYellow.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/8.advanced_glsl.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/8.yellow.fs")
                    .link();

            cubeVBO.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    // positions
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,

                    -0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,

                    -0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,

                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,

                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,

                    -0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
            });
            cubeVAO.bind().enableAttributePointers(3f);

            var matricesUniformBlock = programRed.getUniformBlock("Matrices");
            var bindingPoint = ubo.bind(Buffer.Target.Uniform)
                    .realloc(Buffer.DataUsage.StaticDraw, matricesUniformBlock.getBufferDataSize())
                    .bindAt(0);
            matricesUniformBlock.bind(bindingPoint);
            var matricesMapping = bindingPoint.createMapping(new Matrices(), matricesUniformBlock);
            programGreen.getUniformBlock("Matrices").bind(bindingPoint);
            programBlue.getUniformBlock("Matrices").bind(bindingPoint);

            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);

            var cubeDrawable = cubeVAO.drawingArrays(DrawMode.Triangles, cubeVBO.count() / 3).build();
            while (!window.shouldClose()) {
                graphics.depthTest().enable().then()
                        .defaultFrameBuffer().setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);
                var view = camera.update().view();
                matricesMapping.getBean().setProjection(projection);
                matricesMapping.getBean().setView(new Matrix4f(view));
                matricesMapping.flush();

                programRed.bind()
                        .getUniform("model").setMatrix4(new Matrix4f().translate(-0.75f, 0.75f, 0.0f).get(mat4f));
                cubeDrawable.draw();

                programGreen.bind()
                        .getUniform("model").setMatrix4(new Matrix4f().translate(0.75f, 0.75f, 0.0f).get(mat4f));
                cubeDrawable.draw();

                programBlue.bind()
                        .getUniform("model").setMatrix4(new Matrix4f().translate(-0.75f, -0.75f, 0.0f).get(mat4f));
                cubeDrawable.draw();

                programYellow.bind()
                        .getUniform("model").setMatrix4(new Matrix4f().translate(0.75f, -0.75f, 0.0f).get(mat4f));
                cubeDrawable.draw();

                window.swapBuffers().pollEvents();
            }
            matricesMapping.close();
        }
    }

    @Data
    public static class Matrices {
        private Matrix4f projection;
        private Matrix4f view;

        public Matrices() {
        }

        @BufferStruct
        public Matrices(Matrix4f projection, Matrix4f view) {
            this.projection = projection;
            this.view = view;
        }
    }
}
