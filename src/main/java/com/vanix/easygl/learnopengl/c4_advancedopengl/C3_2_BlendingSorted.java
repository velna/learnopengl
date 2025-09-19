package com.vanix.easygl.learnopengl.c4_advancedopengl;

import com.vanix.easygl.application.g3d.ControllableCamera;
import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.input.Mouse;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.learnopengl.Uniforms;
import com.vanix.easygl.graphics.*;
import com.vanix.easygl.graphics.feature.Blending;
import org.eclipse.collections.api.factory.Lists;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

public class C3_2_BlendingSorted {
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
             var transparentVAO = VertexArray.of();
             var transparentVBO = Buffer.of(DataType.Float);
             var cubeTexture = Texture2D.of();
             var floorTexture = Texture2D.of();
             var transparentTexture = Texture2D.of()) {

            window
                    .attributes().Resizable.disable().then()
                    .inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);

            graphics.depthTest().enable().then()
                    .blending().enable().setFunction(Blending.Function.SrcAlpha, Blending.Function.OneMinusSrcAlpha);

            program.attachResource(Shader.Type.Vertex, "shaders/4_advanced_opengl/3.2.blending.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/4_advanced_opengl/3.2.blending.fs")
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
                    // positions          // texture Coords
                    5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
                    -5.0f, -0.5f, 5.0f, 0.0f, 0.0f,
                    -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,

                    5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
                    -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,
                    5.0f, -0.5f, -5.0f, 2.0f, 2.0f
            });
            var planeTriangleCount = planeVAO.bind().enableAttributePointers(3f, 2f).countOfStride();


            transparentVBO.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    // positions         // texture Coords (swapped y coordinates because texture is flipped upside down)
                    0.0f, 0.5f, 0.0f, 0.0f, 0.0f,
                    0.0f, -0.5f, 0.0f, 0.0f, 1.0f,
                    1.0f, -0.5f, 0.0f, 1.0f, 1.0f,

                    0.0f, 0.5f, 0.0f, 0.0f, 0.0f,
                    1.0f, -0.5f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.5f, 0.0f, 1.0f, 0.0f
            });
            var transparentTriangleCount = transparentVAO.bind().enableAttributePointers(3f, 2f).countOfStride();

            cubeTexture.bind()
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/marble.jpg")
                    .generateMipmap();
            floorTexture.bind()
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/metal.png")
                    .generateMipmap();
            transparentTexture.bind()
                    .wrapS(Texture.Wrap.ClampToEdge)
                    .wrapT(Texture.Wrap.ClampToEdge)
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/window.png")
                    .generateMipmap();

            var vegetation = Lists.mutable.of(
                    new Vector3f(-1.5f, 0.0f, -0.48f),
                    new Vector3f(1.5f, 0.0f, 0.51f),
                    new Vector3f(0.0f, 0.0f, 0.7f),
                    new Vector3f(-0.3f, 0.0f, -2.3f),
                    new Vector3f(0.5f, 0.0f, -0.6f));

            program.bind()
                    .getUniform("texture1").setTextureUnit(TextureUnit.U0);

            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);

            var cubeDrawable = cubeVAO.drawingArrays(DrawMode.Triangles, cubeTriangleCount).build();
            var planeDrawable = planeVAO.drawingArrays(DrawMode.Triangles, planeTriangleCount).build();
            var transparentDrawable = transparentVAO.drawingArrays(DrawMode.Triangles, transparentTriangleCount).build();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);
                var view = camera.update().view();

                vegetation.sort((a, b) -> Float.compare(b.distance(camera.position()), a.distance(camera.position())));

                program.bind();
                uniforms.projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(view.get(mat4f));

                // cubes
                cubeVAO.bind();
                cubeTexture.bind();
                uniforms.model.setMatrix4(new Matrix4f().translate(-1.0f, 0.0f, -1.0f).get(mat4f));
                cubeDrawable.draw();
                uniforms.model.setMatrix4(new Matrix4f().translate(2.0f, 0.0f, 0.0f).get(mat4f));
                cubeDrawable.draw();

                //floor
                floorTexture.bind();
                uniforms.model.setMatrix4(new Matrix4f().get(mat4f));
                planeDrawable.draw();

                //
                transparentTexture.bind();
                for (var vec : vegetation) {
                    uniforms.model.setMatrix4(new Matrix4f().translate(vec).get(mat4f));
                    transparentDrawable.draw();
                }

                window.swapBuffers().pollEvents();
            }
        }
    }

}
