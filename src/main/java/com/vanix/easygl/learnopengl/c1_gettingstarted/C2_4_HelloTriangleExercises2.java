package com.vanix.easygl.learnopengl.c1_gettingstarted;

import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.*;
import com.vanix.easygl.graphics.draw.Drawing;

public class C2_4_HelloTriangleExercises2 {
    public static void main(String[] args) {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();
        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight());
             var vertex = Shader.vertex();
             var fragment = Shader.fragment();
             var program = Program.of();
             var vaos = VertexArray.of(2);
             var vbos = Buffer.of(2, DataType.Float)) {
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
            vbos.getFirst().bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{
                            // first triangle
                            -0.9f, -0.45f, 0.0f,  // left
                            -0.0f, -0.45f, 0.0f,  // right
                            -0.45f, 0.45f, 0.0f
                    });
            vaos.getFirst().bind().enableAttributePointers(3f);

            vbos.getLast().bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{
                            // second triangle
                            0.0f, -0.45f, 0.0f,  // left
                            0.9f, -0.45f, 0.0f,  // right
                            0.45f, 0.45f, 0.0f   // top
                    });
            vaos.getLast().bind().enableAttributePointers(3f).countOfStride();

            var drawables = vaos.stream().map(vao -> vao.drawingArrays(DrawMode.Triangles, 3).build()).toList();
            while (!window.shouldClose()) {
                graphics.defaultFrameBuffer().setClearColor(0.2f, 0.3f, 0.3f, 1.0f)
                        .clear(FrameInnerBuffer.Mask.Color);

                program.bind();
                drawables.forEach(Drawing::draw);
                window.swapBuffers().pollEvents();
            }
        }
    }

}
