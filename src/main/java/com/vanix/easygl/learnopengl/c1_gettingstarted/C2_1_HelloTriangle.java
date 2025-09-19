package com.vanix.easygl.learnopengl.c1_gettingstarted;

import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.*;

public class C2_1_HelloTriangle {
    public static void main(String[] args) {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var program = Program.of();
             var vao = VertexArray.of();
             var vbo = Buffer.of(DataType.Float)) {
            window.bind().inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE)
                    .subscribe((event) -> event.source().window().shouldClose(true));

            program.attachVertex("""
                            #version 330 core
                            layout (location = 0) in vec3 aPos;
                            void main() {
                                gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
                            }
                            """)
                    .attachFragment("""
                            #version 330 core
                            out vec4 FragColor;
                            void main(){
                                FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                            }
                            """)
                    .link();
            vbo.bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{
                            -0.5f, -0.5f, 0.0f, // left
                            0.5f, -0.5f, 0.0f, // right
                            0.0f, 0.5f, 0.0f  // top
                    });
            var triangleCount = vao.bind().enableAttributePointers(3f).countOfStride();

            var drawable = vao.drawingArrays(DrawMode.Triangles, triangleCount).build();
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
