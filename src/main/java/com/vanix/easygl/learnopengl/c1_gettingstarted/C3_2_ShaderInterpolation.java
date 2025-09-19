package com.vanix.easygl.learnopengl.c1_gettingstarted;

import com.vanix.easygl.graphics.DataType;
import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.*;

public class C3_2_ShaderInterpolation {
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
             var vbo = Buffer.of(DataType.Float)) {
            window.bind().inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE)
                    .subscribe((event) -> event.source().window().shouldClose(true));

            program.attach(vertex.source("""
                                    #version 330 core
                                    layout (location = 0) in vec3 aPos;
                                    layout (location = 1) in vec3 aColor;
                                    out vec3 ourColor;
                                    void main() {
                                        gl_Position = vec4(aPos, 1.0);
                                        ourColor = aColor;
                                    }
                                    """)
                            .compile())
                    .attach(fragment.source("""
                                    #version 330 core
                                    out vec4 FragColor;
                                    in vec3 ourColor;
                                    void main(){
                                        FragColor = vec4(ourColor, 1.0f);;
                                    }
                                    """)
                            .compile())
                    .link();
            vbo.bind(Buffer.Target.Array)
                    .realloc(Buffer.DataUsage.StaticDraw, new float[]{
                            // positions         // colors
                            0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,  // bottom right
                            -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,  // bottom left
                            0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f   // top
                    });
            vao.bind().enableAttributePointers(3f, 3f);

            var drawable = vao.drawingArrays(DrawMode.Triangles, 3).build();
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