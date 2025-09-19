package com.vanix.easygl.learnopengl.c2_lighting;

import com.vanix.easygl.application.g3d.ControllableCamera;
import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
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

public class C3_1_Materials {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var lightingProgram = Program.of();
             var lightCubeProgram = Program.of();
             var cubeVAO = VertexArray.of();
             var lightCubeVAO = VertexArray.of();
             var vbo = Buffer.of(DataType.Float)) {

            window.inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            graphics.depthTest().enable();

            lightingProgram.attachResource(Shader.Type.Vertex, "shaders/2_lighting/3.1.materials.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/2_lighting/3.1.materials.fs")
                    .link();
            var lightingUniforms = lightingProgram.bindResources(new Uniforms<>());

            lightCubeProgram.attachResource(Shader.Type.Vertex, "shaders/2_lighting/3.1.light_cube.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/2_lighting/3.1.light_cube.fs")
                    .link();
            var lightCubeUniforms = lightCubeProgram.bindResources(new Uniforms<>());

            vbo.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
                    0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
                    0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
                    0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
                    -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
                    -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,

                    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
                    0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
                    0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
                    0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
                    -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
                    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,

                    -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
                    -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
                    -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
                    -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
                    -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
                    -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,

                    0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
                    0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
                    0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
                    0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
                    0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,

                    -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
                    0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
                    0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
                    0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
                    -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
                    -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,

                    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                    0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
                    -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
                    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f
            });
            var cubeTriangleCount = cubeVAO.bind().enableAttributePointers(3f, 3f).countOfStride();
            var lightTriangleCount = lightCubeVAO.bind().enableAttributePointers(3f, -3f).prevAttribute().countOfStride();


            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            var lightPos = new Vector3f(1.2f, 1.0f, 2.0f);
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);

            var cubeDrawable = cubeVAO.drawingArrays(DrawMode.Triangles, cubeTriangleCount).build();
            var lightCubeDrawable = lightCubeVAO.drawingArrays(DrawMode.Triangles, lightTriangleCount).build();
            long start = System.currentTimeMillis();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.2f, 0.3f, 0.3f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                float time = (System.currentTimeMillis() - start) / 1000.0f;

                lightPos.x = 1.0f + Math.sin(time) * 2.0f;
                lightPos.y = Math.sin(time / 2.0f);

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);
                var view = camera.update().view();

                // light properties
                float sin = Math.sin(time);
                Vector3f lightColor = new Vector3f(sin * 2.0f, sin * 0.7f, sin * 1.3f);
                Vector3f diffuseColor = lightColor.mul(new Vector3f(0.5f), new Vector3f()); // decrease the influence
                Vector3f ambientColor = diffuseColor.mul(new Vector3f(0.2f), new Vector3f()); // low influence

                lightingProgram.bind();
                lightingUniforms.light
                        .position.setVec3(lightPos)
                        .ambient.setVec3(ambientColor)
                        .diffuse.setVec3(diffuseColor)
                        .specular.setVec3(1.0f, 1.0f, 1.0f)
                        .then()
                        .material
                        .ambient.setVec3(1.0f, 0.5f, 0.31f)
                        .diffuse.setVec3(1.0f, 0.5f, 0.31f)
                        .specular.setVec3(0.5f, 0.5f, 0.5f)
                        .shininess.setFloat(32.0f)
                        .then()
                        .viewPos.setVec3(camera.position())
                        .projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(view.get(mat4f))
                        .model.setMatrix4(new Matrix4f());
                cubeDrawable.draw();

                lightCubeProgram.bind().getUniform("lightColor").setVec3(lightColor);
                lightCubeUniforms.projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(view.get(mat4f))
                        .model.setMatrix4(new Matrix4f().translate(lightPos).scale(0.2f));
                lightCubeDrawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
