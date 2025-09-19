package com.vanix.easygl.learnopengl.c4_advancedopengl;

import com.vanix.easygl.application.g3d.ControllableCamera;
import com.vanix.easygl.graphics.DataType;
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

public class C1_1_DepthTesting {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var program = Program.of();
             var cubeVAO = VertexArray.of();
             var cubeVBO = Buffer.of(DataType.Float);
             var planeVAO = VertexArray.of();
             var planeVBO = Buffer.of(DataType.Float);
             var cubeTexture = Texture2D.of();
             var floorTexture = Texture2D.of()) {

            window
                    .attributes().Resizable.disable().then()
                    .inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);

            graphics.depthTest().enable().setFunction(CompareFunction.Always);

            program.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/1.1.depth_testing.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/1.1.depth_testing.fs")
                    .link();
            var uniforms = program.bindResources(new Uniforms<>());

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

            cubeTexture.bind()
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/marble.jpg")
                    .generateMipmap();
            floorTexture.bind()
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/metal.png")
                    .generateMipmap();

            program.bind()
                    .getUniform("texture1").setTextureUnit(TextureUnit.U0);

            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);

            var cubeDrawable = cubeVAO.drawingArrays(DrawMode.Triangles, cubeTriangleCount).build();
            var planeDrawable = planeVAO.drawingArrays(DrawMode.Triangles, planeTriangleCount).build();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);
                var view = camera.update().view();

                program.bind();
                uniforms.projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(view.get(mat4f));

                cubeTexture.bind(TextureUnit.U0);
                uniforms.model.setMatrix4(new Matrix4f().translate(-1.0f, 0.0f, -1.0f).get(mat4f));
                cubeDrawable.draw();
                uniforms.model.setMatrix4(new Matrix4f().translate(2.0f, 0.0f, 0.0f).get(mat4f));
                cubeDrawable.draw();

                floorTexture.bind(TextureUnit.U0);
                uniforms.model.setMatrix4(new Matrix4f().get(mat4f));
                planeDrawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
