package com.vanix.easygl.learnopengl.c1_gettingstarted;

import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.*;

public class C2_2_HelloTriangleIndexed {
    public static void main(String[] args) {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var vertex = Shader.vertex();
             var fragment = Shader.fragment();
             var program = Program.of();
             var vao = VertexArray.of();
             var vbo = Buffer.of(DataType.Float);
             var ebo = Buffer.of(DataType.UnsignedInt)) {
            window.bind().inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE)
                    .subscribe((event) -> event.source().window().shouldClose(true));

            program.attach(vertex.source("""
                                    #version 330 core
                                    layout (location = 0) in vec3 aPos;
                                    void main() {
                                        gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
                                    }
                                    """)
                            .compile())
                    .attach(fragment.source("""
                                    #version 330 core
                                    out vec4 FragColor;
                                    void main(){
                                        FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                                    }
                                    """)
                            .compile())
                    .link();
            vbo.bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{
                            0.5f, 0.5f, 0.0f,  // top right
                            0.5f, -0.5f, 0.0f,  // bottom right
                            -0.5f, -0.5f, 0.0f,  // bottom left
                            -0.5f, 0.5f, 0.0f   // top left
                    });
            vao.bind().enableAttributePointers(3f);
            ebo.bind(Buffer.Target.ElementArray)
                    .realloc(Buffer.DataUsage.StaticDraw, new int[]{
                            // note that we start from 0!
                            0, 1, 3,  // first Triangle
                            1, 2, 3   // second Triangle
                    });

            var drawable = vao.drawingElements(DrawMode.Triangles, ebo).build();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.2f, 0.3f, 0.3f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.Color);

                program.bind();
                drawable.draw();

                window.swapBuffers().pollEvents();
            }
        }
    }

}
