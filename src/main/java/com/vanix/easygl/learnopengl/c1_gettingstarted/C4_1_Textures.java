package com.vanix.easygl.learnopengl.c1_gettingstarted;

import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.core.media.Image;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.*;

import java.io.IOException;

public class C4_1_Textures {
    public static void main(String[] args) throws IOException {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var program = Program.of();
             var vao = VertexArray.of();
             var vbo = Buffer.of(DataType.Float);
             var ebo = Buffer.of(DataType.UnsignedInt);
             var texture = Texture2D.of()) {
            window.bind().inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE)
                    .subscribe((event) -> event.source().window().shouldClose(true));

            program.attachResource(Shader.Type.Vertex, "shaders/1_getting_started/4.1.texture.vs")
                    .attachResource(Shader.Type.Fragment, "shaders/1_getting_started/4.1.texture.fs")
                    .link();
            vbo.bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{
                            // positions          // colors           // texture coords
                            0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
                            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
                            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom left
                            -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f  // top left
                    });
            vao.bind().enableAttributePointers(3f, 3f, 2f);
            ebo.bind(Buffer.Target.ElementArray).realloc(Buffer.DataUsage.StaticDraw, new int[]{
                    0, 1, 3, // first triangle
                    1, 2, 3  // second triangle
            });

            texture.bind()
                    .wrapS(Texture.Wrap.Repeat)
                    .wrapT(Texture.Wrap.Repeat)
                    .minFilter(MinFilter.LinearMipmapLinear)
                    .magFilter(MagFilter.Linear);
            try (var image = Image.load("textures/container.jpg")) {
                texture.load(image).generateMipmap();
            }

            var drawable = vao.drawingElements(DrawMode.Triangles, ebo).build();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.2f, 0.3f, 0.3f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.Color);

                texture.bind();
                program.bind();
                drawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
