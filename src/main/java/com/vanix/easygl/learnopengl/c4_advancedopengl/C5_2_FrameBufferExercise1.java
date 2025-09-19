package com.vanix.easygl.learnopengl.c4_advancedopengl;

import com.vanix.easygl.application.g3d.ControllableCamera;
import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.graphics.InternalPixelFormat;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.input.Mouse;
import com.vanix.easygl.core.media.Image;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.learnopengl.Uniforms;
import com.vanix.easygl.graphics.*;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

public class C5_2_FrameBufferExercise1 {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenglForwardCompat.set(true);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var program = Program.of();
             var screenProgram = Program.of();
             var cubeVAO = VertexArray.of();
             var cubeVBO = Buffer.of(DataType.Float);
             var planeVAO = VertexArray.of();
             var planeVBO = Buffer.of(DataType.Float);
             var quadVAO = VertexArray.of();
             var quadVBO = Buffer.of(DataType.Float);
             var frameBuffer = FrameBuffer.of();
             var textureColor = Texture2D.of();
             var cubeTexture = Texture2D.of();
             var renderBuffer = RenderBuffer.of();
             var floorTexture = Texture2D.of()) {

            window
                    .inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);

            graphics.depthTest().enable();

            program.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/5.2.framebuffers.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/5.2.framebuffers.fs")
                    .link();
            var uniforms = program.bindResources(new Uniforms<>());
            screenProgram.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/5.2.framebuffers_screen.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/5.2.framebuffers_screen.fs")
                    .link();

            cubeVBO.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    // positions          // texture Coords
                    -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
                    0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
                    0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                    0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

                    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                    0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                    0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                    -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

                    -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                    -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                    -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

                    0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                    0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                    0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                    0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                    0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

                    -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                    0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
                    0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                    0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                    -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

                    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                    0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                    0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                    -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
                    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
            });

            var cubeTriangleCount = cubeVAO.bind().enableAttributePointers(3f, 2f).countOfStride();

            planeVBO.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    // positions          // texture Coords (note we set these higher than 1 (together with GL_REPEAT as texture wrapping mode). this will cause the floor texture to repeat)
                    5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
                    -5.0f, -0.5f, 5.0f, 0.0f, 0.0f,
                    -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,

                    5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
                    -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,
                    5.0f, -0.5f, -5.0f, 2.0f, 2.0f
            });
            var planeTriangleCount = planeVAO.bind().enableAttributePointers(3f, 2f).countOfStride();

            quadVBO.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
                    // positions   // texCoords
                    -0.3f, 1.0f, 0.0f, 1.0f,
                    -0.3f, 0.7f, 0.0f, 0.0f,
                    0.3f, 0.7f, 1.0f, 0.0f,

                    -0.3f, 1.0f, 0.0f, 1.0f,
                    0.3f, 0.7f, 1.0f, 0.0f,
                    0.3f, 1.0f, 1.0f, 1.0f
            });
            var quadTriangleCount = quadVAO.bind().enableAttributePointers(2f, 2f).countOfStride();

            cubeTexture.bind()
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/container.jpg")
                    .generateMipmap();
            floorTexture.bind()
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/metal.png")
                    .generateMipmap();

            program.bind()
                    .getUniform("texture1").setTextureUnit(TextureUnit.U0);
            screenProgram.bind()
                    .getUniform("screenTexture").setTextureUnit(TextureUnit.U0);

            frameBuffer.bindDraw()
                    .attach(FrameInnerBuffer.Attachment.ofColor(0),
                            textureColor.bind()
                                    .minFilter(MinFilter.Linear)
                                    .magFilter(MagFilter.Linear)
                                    .load(Image.empty(Image.Format.RGB, window.frameBufferWidth(), window.frameBufferHeight())))
                    .attach(FrameInnerBuffer.Attachment.DepthStencil,
                            renderBuffer.bind().storage(InternalPixelFormat.Base.DEPTH24_STENCIL8, window.frameBufferWidth(), window.frameBufferHeight()))
                    .checkStatus();

            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);

            var cubeDrawable = cubeVAO.drawingArrays(DrawMode.Triangles, cubeTriangleCount).build();
            var planeDrawable = planeVAO.drawingArrays(DrawMode.Triangles, planeTriangleCount).build();
            var quadDrawable = quadVAO.drawingArrays(DrawMode.Triangles, quadTriangleCount).build();
            while (!window.shouldClose()) {
                graphics.depthTest().enable();
                frameBuffer.bind().setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);
                var view = camera.update().view();

                Runnable scene = () -> {
                    program.bind();
                    uniforms.projection.setMatrix4(projection.get(mat4f))
                            .view.setMatrix4(view.get(mat4f));

                    cubeTexture.bind(TextureUnit.U0);
                    uniforms.model.setMatrix4(new Matrix4f().translate(-1.0f, 0.0f, -1.0f).get(mat4f));
                    cubeDrawable.draw();
                    uniforms.model.setMatrix4(new Matrix4f().translate(2.0f, 0.0f, 0.0f).get(mat4f));
                    cubeDrawable.draw();

                    floorTexture.bind();
                    uniforms.model.setMatrix4(new Matrix4f().get(mat4f));
                    planeDrawable.draw();
                };
                camera.yaw().incr(180f);
                scene.run();
                graphics.defaultFrameBuffer().bind()
                        .setClearColor(0.0f, 0.0f, 1.0f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);
                camera.yaw().incr(-180f);
                scene.run();

                graphics.depthTest().disable();
                screenProgram.bind();
                textureColor.bind();
                quadDrawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
