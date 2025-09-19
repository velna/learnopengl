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

public class C5_1_LightCastersDirectional {
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
             var vbo = Buffer.of(DataType.Float);
             var diffuseMap = Texture2D.of();
             var specularMap = Texture2D.of()) {

            window
                    .attributes().Resizable.disable().then()
                    .inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            graphics.depthTest().enable();

            lightingProgram.attachResource(Shader.Type.Vertex, "shaders/2_lighting/5.1.light_casters.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/2_lighting/5.1.light_casters.fs")
                    .link();
            var lightingUniforms = lightingProgram.bindResources(new Uniforms<>());

            lightCubeProgram.attachResource(Shader.Type.Vertex, "shaders/2_lighting/5.1.light_cube.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/2_lighting/5.1.light_cube.fs")
                    .link();
            var lightingCubeUniforms = lightCubeProgram.bindResources(new Uniforms<>());

            vbo.bind(Buffer.Target.Array).realloc(Buffer.DataUsage.StaticDraw, new float[]{
                    // positions          // normals           // texture coords
                    -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                    0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
                    0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
                    0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
                    -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,

                    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                    0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                    0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                    -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

                    -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                    -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                    -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                    -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

                    0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                    0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                    0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                    0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                    0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

                    -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
                    0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
                    0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
                    0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
                    -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
                    -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,

                    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                    0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                    0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                    -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
            });

            var cubePositions = new Vector3f[]{
                    new Vector3f(0.0f, 0.0f, 0.0f),
                    new Vector3f(2.0f, 5.0f, -15.0f),
                    new Vector3f(-1.5f, -2.2f, -2.5f),
                    new Vector3f(-3.8f, -2.0f, -12.3f),
                    new Vector3f(2.4f, -0.4f, -3.5f),
                    new Vector3f(-1.7f, 3.0f, -7.5f),
                    new Vector3f(1.3f, -2.0f, -2.5f),
                    new Vector3f(1.5f, 2.0f, -2.5f),
                    new Vector3f(1.5f, 0.2f, -1.5f),
                    new Vector3f(-1.3f, 1.0f, -1.5f)};

            var cubeTriangleCount = cubeVAO.bind().enableAttributePointers(3f, 3f, 2f).countOfStride();
            lightCubeVAO.bind().enableAttributePointers(3f, -3f, -2f);
            var lightTriangleCount = lightCubeVAO.attribute(0).countOfStride();

            diffuseMap.bind()
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/container2.png")
                    .generateMipmap();
            specularMap.bind()
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .load("textures/container2_specular.png")
                    .generateMipmap();

            lightingProgram.bind();
            lightingUniforms.material
                    .diffuse.setTextureUnit(TextureUnit.U0)
                    .specular.setTextureUnit(TextureUnit.U1);

            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            var lightPos = new Vector3f(1.2f, 1.0f, 2.0f);
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);

            var cubeDrawable = cubeVAO.drawingArrays(DrawMode.Triangles, cubeTriangleCount).build();
            var lightCubeDrawable = lightCubeVAO.drawingArrays(DrawMode.Triangles, lightTriangleCount).build();
            long start = System.currentTimeMillis();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                float time = (System.currentTimeMillis() - start) / 1000.0f;

                lightPos.x = 1.0f + Math.sin(time) * 2.0f;
                lightPos.y = Math.sin(time / 2.0f);

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);
                var view = camera.update().view();

                lightingProgram.bind();
                lightingUniforms.light
                        .ambient.setVec3(0.2f, 0.2f, 0.2f)
                        .diffuse.setVec3(0.5f, 0.5f, 0.5f)
                        .specular.setVec3(1.0f, 1.0f, 1.0f)
                        // material properties
                        .then().material
                        .shininess.setFloat(32.0f)
                        .then()
                        .viewPos.setVec3(camera.position())
                        .projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(view.get(mat4f));

                diffuseMap.bind(TextureUnit.U0);
                specularMap.bind(TextureUnit.U1);

//                cubeVAO.bind().drawArray(DrawMode.Triangles, vbo);

                for (var i = 0; i < cubePositions.length; i++) {
                    lightingUniforms.model.setMatrix4(new Matrix4f()
                                    .translate(cubePositions[i])
                                    .rotate(Math.toRadians(20.0f * i), new Vector3f(1.0f, 0.3f, 0.5f))
                                    .get(mat4f))
                            .light.direction.setVec3(lightPos);
                    cubeDrawable.draw();
                }

                lightCubeProgram.bind();
                lightingCubeUniforms
                        .projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(view.get(mat4f))
                        .model.setMatrix4(new Matrix4f().translate(lightPos).scale(0.2f));
                lightCubeDrawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
