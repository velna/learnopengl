package com.vanix.easygl.learnopengl.c3_modelloading;

import com.vanix.easygl.application.g3d.ControllableCamera;
import com.vanix.easygl.application.g3d.Mesh;
import com.vanix.easygl.application.g3d.Model;
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

public class ModelLoading {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenglForwardCompat.enable();
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var program = Program.of();
             var model = Model.of("objects/backpack/backpack.obj")) {

            window.inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE).subscribe(event -> window.shouldClose(true));
            window.inputs().mouse().cursorMode(Mouse.CursorMode.CURSOR_DISABLED);
            graphics.depthTest().enable();

            program.attachResource(Shader.Type.Vertex, "shaders/3_model_loading/1.model_loading.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/3_model_loading/1.model_loading.fs")
                    .link();
            var uniforms = program.bindResources(new Uniforms<>());

            var camera = new ControllableCamera(window.inputs().keyboard(), window.inputs().mouse());
            FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);
            var meshes = model.getMeshes();

            var textureDiffuse1 = program.getUniform("texture_diffuse1");
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.2f, 0.3f, 0.3f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.ColorAndDepth);

                var projection = new Matrix4f()
                        .perspective(Math.toRadians(camera.fov().get()), window.getAspect(), 0.1f, 100.0f);
                var view = camera.update().view();

                program.bind();
                uniforms.projection.setMatrix4(projection.get(mat4f))
                        .view.setMatrix4(view.get(mat4f))
                        .model.setMatrix4(new Matrix4f()
                                .translate(0.0f, 0.0f, 0.0f)
                                .scale(1.0f, 1.0f, 1.0f)
                                .get(mat4f));

                for (Mesh mesh : meshes) {
                    var textures = mesh.getTextures(Model.TextureType.Diffuse);
                    if (!textures.isEmpty()) {
                        mesh.getTextures(Model.TextureType.Diffuse).getFirst().getTexture().bind(TextureUnit.U0);
                        textureDiffuse1.setTextureUnit(TextureUnit.U0);
                    }
                    mesh.draw();
                }

                window.swapBuffers().pollEvents();
            }
        }
    }


}
