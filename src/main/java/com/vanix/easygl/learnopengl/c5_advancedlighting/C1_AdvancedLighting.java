package com.vanix.easygl.learnopengl.c5_advancedlighting;

import com.vanix.easygl.application.g3d.ControllableCamera;
import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.input.Mouse;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.learnopengl.Uniforms;
import com.vanix.easygl.graphics.*;
import com.vanix.easygl.graphics.feature.Blending;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

public class C1_AdvancedLighting {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var program = Program.of();
             var planeVao = VertexArray.of();
             var planeVbo = Buffer.of(DataType.Float);
             var floorTexture = Texture2D.of()) {

            window.inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);
            graphics.depthTest().enable().then()
                    .blending().enable().setFunction(Blending.Function.SrcAlpha, Blending.Function.OneMinusSrcAlpha);

            program.attachResource(Shader.Type.Vertex, "shaders/5_advanced_lighting/1.advanced_lighting.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/5_advanced_lighting/1.advanced_lighting.fs")
                    .link();
            var uniforms = program.bindResources(new Uniforms<>());

            planeVbo.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    // positions            // normals         // texcoords
                    10.0f, -0.5f, 10.0f, 0.0f, 1.0f, 0.0f, 10.0f, 0.0f,
                    -10.0f, -0.5f, 10.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                    -10.0f, -0.5f, -10.0f, 0.0f, 1.0f, 0.0f, 0.0f, 10.0f,

                    10.0f, -0.5f, 10.0f, 0.0f, 1.0f, 0.0f, 10.0f, 0.0f,
                    -10.0f, -0.5f, -10.0f, 0.0f, 1.0f, 0.0f, 0.0f, 10.0f,
                    10.0f, -0.5f, -10.0f, 0.0f, 1.0f, 0.0f, 10.0f, 10.0f
            });
            var planeTriangleCount = planeVao.bind().enableAttributePointers(3f, 3f, 2f).countOfStride();

            floorTexture.bind()
                    .wrapS(Texture.Wrap.Repeat)
                    .wrapT(Texture.Wrap.Repeat)
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .magFilter(MagFilter.Linear)
                    .load("textures/wood.png")
                    .generateMipmap();

//            program.bind().setInt("texture1", 0);

            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            var lightPos = new Vector3f(0.0f, 0.0f, 0.0f);
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);

            long start = System.currentTimeMillis();

            var planeDrawable = planeVao.drawingArrays(DrawMode.Triangles, planeTriangleCount).build();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                float time = (System.currentTimeMillis() - start) / 1000.0f;

                lightPos.x = 1.0f + Math.sin(time) * 2.0f;
                lightPos.y = Math.sin(time / 2.0f);

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);
                var view = camera.update().view();

                program.bind().getUniform("blinn").setBoolean(false);
                uniforms.projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(view.get(mat4f))
                        .viewPos.setVec3(camera.position())
                        .lightPos.setVec3(lightPos);

                floorTexture.bind();

                planeDrawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
