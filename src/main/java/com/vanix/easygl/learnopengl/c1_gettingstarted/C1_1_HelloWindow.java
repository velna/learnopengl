package com.vanix.easygl.learnopengl.c1_gettingstarted;

import com.vanix.easygl.window.input.Keyboard;
import com.vanix.easygl.window.Window;
import com.vanix.easygl.window.WindowHints;
import com.vanix.easygl.graphics.Graphics;

public class C1_1_HelloWindow {
    public static void main(String[] args) {
        WindowHints.ContextVersionMajor.set(3);
        WindowHints.ContextVersionMinor.set(3);
        WindowHints.OpenGlProfile.Core.set();

        try (var window = Window.of(800, 600, "LearnOpenGL").bind();
             var graphics = Graphics.of().viewport(window.frameBufferWidth(), window.frameBufferHeight())) {
            window.bind().inputs().keyboard().onKey(Keyboard.FunctionKey.ESCAPE)
                    .subscribe((event) -> event.source().window().shouldClose(true));
            graphics.viewport(0, 0, window.frameBufferWidth(), window.frameBufferHeight());

            while (!window.shouldClose()) {
                window.swapBuffers().pollEvents();
            }
        }
    }

}
