package com.vanix.easygl.learnopengl.c1_gettingstarted;

import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.graphics.InternalPixelFormat;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.core.media.Image;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.learnopengl.Uniforms;
import com.vanix.easygl.graphics.*;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

public class C6_2_CoordinateSystemsDepth {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var program = Program.of();
             var vao = VertexArray.of();
             var vbo = Buffer.of(DataType.Float);
             var texture1 = Texture2D.of();
             var texture2 = Texture2D.of()) {
            window.bind().inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE)
                    .subscribe((event) -> event.source().window().shouldClose(true));

            graphics.depthTest().enable();

            program.attachResource(Shader.Type.Vertex, "shaders/1_getting_started/6.2.coordinate_systems.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/1_getting_started/6.2.coordinate_systems.fs")
                    .link();
            vbo.bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{
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
            var triangleCount = vao.bind().enableAttributePointers(3f, 2f).countOfStride();

            texture1.bind()
                    .wrapS(Texture.Wrap.Repeat)
                    .wrapT(Texture.Wrap.Repeat)
                    .minFilter(MinFilter.Linear)
                    .magFilter(MagFilter.Linear);
            try (var image = Image.load("textures/container.jpg")) {
                texture1.load(image).generateMipmap();
            }

            texture2.bind()
                    .wrapS(Texture.Wrap.Repeat)
                    .wrapT(Texture.Wrap.Repeat)
                    .minFilter(MinFilter.Linear)
                    .magFilter(MagFilter.Linear);
            try (var image = Image.load("textures/awesomeface.png")) {
                texture2.load(0, InternalPixelFormat.Base.RGB, image).generateMipmap();
            }

            program.bind();
            program.getUniform("texture1").setTextureUnit(TextureUnit.U0);
            program.getUniform("texture2").setTextureUnit(TextureUnit.U1);
            var uniforms = program.bindResources(new Uniforms<>());

            long start = System.currentTimeMillis();
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);
            var drawable = vao.drawingArrays(DrawMode.Triangles, triangleCount).build();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.2f, 0.3f, 0.3f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                TextureUnit.U0.bind();
                texture1.bind();
                TextureUnit.U1.bind();
                texture2.bind();

                float time = (System.currentTimeMillis() - start) / 1000.0f;

                program.bind();
                uniforms.model.setMatrix4(new Matrix4f()
                                .rotate(time, new Vector3f(0.5f, 1.0f, 0.0f))
                                .get(mat4f))
                        .view.setMatrix4(new Matrix4f()
                                .translate(new Vector3f(0.0f, 0.0f, -3.0f))
                                .get(mat4f))
                        .projection.setMatrix4(new Matrix4f()
                                .perspective(Math.toRadians(45.0f), window.getAspect(), 0.1f, 100.0f)
                                .get(mat4f));
                drawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
