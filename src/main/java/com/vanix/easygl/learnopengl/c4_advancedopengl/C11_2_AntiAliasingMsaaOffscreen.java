package com.vanix.easygl.learnopengl.c4_advancedopengl;

import com.vanix.easygl.application.g3d.ControllableCamera;
import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.graphics.InternalPixelFormat;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.input.Mouse;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.learnopengl.Uniforms;
import com.vanix.easygl.graphics.*;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

public class C11_2_AntiAliasingMsaaOffscreen {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var frameBuffer = FrameBuffer.of();
             var renderBuffer = RenderBuffer.of();
             var intermediateFBO = FrameBuffer.of();
             var screenTexture = Texture2D.of();
             var textureColorBufferMultiSampled = Texture2DMultiSample.of();
             var program = Program.of();
             var screenProgram = Program.of();
             var cubeVao = VertexArray.of();
             var cubeVbo = Buffer.of(DataType.Float);
             var quadVao = VertexArray.of();
             var quadVbo = Buffer.of(DataType.Float)) {
            window.bind().inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE)
                    .subscribe((event) -> event.source().window().shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);
            graphics.depthTest().enable();
            graphics.enable(Capability.Multisample);

            program.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/11.2.anti_aliasing.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/11.2.anti_aliasing.fs")
                    .link();
            var uniforms = program.bindResources(new Uniforms<>());
            screenProgram.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/11.2.aa_post.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/11.2.aa_post.fs")
                    .link();

            cubeVbo.bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{
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
                            -0.5f, 0.5f, -0.5f
                    });
            var cubeTriangleCount = cubeVao.bind().enableAttributePointers(3f).countOfStride();

            quadVbo.bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{// vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
                            // positions   // texCoords
                            -1.0f, 1.0f, 0.0f, 1.0f,
                            -1.0f, -1.0f, 0.0f, 0.0f,
                            1.0f, -1.0f, 1.0f, 0.0f,

                            -1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, -1.0f, 1.0f, 0.0f,
                            1.0f, 1.0f, 1.0f, 1.0f
                    });
            var quadTriangleCount = quadVao.bind().enableAttributePointers(2f, 2f).countOfStride();

            renderBuffer.bind()
                    .storageMultiSample(4, InternalPixelFormat.Base.DEPTH24_STENCIL8, window.frameBufferWidth(), window.frameBufferHeight())
                    .unbind();
            textureColorBufferMultiSampled.bind()
                    .establish(4, InternalPixelFormat.Base.RGB, window.frameBufferWidth(), window.frameBufferHeight())
                    .unbind();
            frameBuffer.bindFrame()
                    .attach(FrameInnerBuffer.Attachment.ofColor(0), textureColorBufferMultiSampled)
                    .attach(FrameInnerBuffer.Attachment.DepthStencil, renderBuffer)
                    .checkStatus()
                    .unbind();

            screenTexture.bind()
                    .allocate(InternalPixelFormat.Base.RGB, window.frameBufferWidth(), window.frameBufferHeight())
                    .minFilter(MinFilter.Linear)
                    .magFilter(MagFilter.Linear);
            intermediateFBO.bindFrame()
                    .attach(FrameInnerBuffer.Attachment.ofColor(0), screenTexture)
                    .checkStatus()
                    .unbind();

            screenProgram.bind()
                    .getUniform("screenTexture").setTextureUnit(TextureUnit.U0);

            var cubeDrawable = cubeVao.drawingArrays(DrawMode.Triangles, cubeTriangleCount).build();
            var quadDrawable = quadVao.drawingArrays(DrawMode.Triangles, quadTriangleCount).build();
            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer()
                        .setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);
                frameBuffer.bindFrame()
                        .setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);
                graphics.depthTest().enable();

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);

                program.bind();
                uniforms
                        .projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(camera.update().view().get(mat4f))
                        .model.setMatrix4(new Matrix4f().get(mat4f));
                cubeDrawable.draw();

                frameBuffer.bindRead();
                intermediateFBO.bindDraw()
                        .blit(0, 0, window.frameBufferWidth(), window.frameBufferHeight(), FrameInnerBuffer.Mask.Color, MagFilter.Nearest)
                        .unbind();
                graphics.defaultFrameBuffer()
                        .setClearColor(1.0f, 1.0f, 1.0f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.Color);
                graphics.depthTest().disable();

                screenProgram.bind();
                screenTexture.bind();
                quadDrawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}