package party.vups;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ui {
    private cpu mycpu;

    ui(cpu c) {
        mycpu = c;
    }
    private int sidelength = 5;

    // The window handle
    private long window;

    public void run() throws InterruptedException {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();
        mycpu.init();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(64 * 10, 32 * 10, "CHIP-8", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            //halfsize pixel

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

    }

    private void loop() throws InterruptedException {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glfwSetKeyCallback(window, keyCallback());

        glViewport(0, 0, 640, 320);
        GL11.glMatrixMode(GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 640, 320, 0, 1, -1);
        GL11.glMatrixMode(GL_MODELVIEW);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glfwPollEvents();
            mycpu.cycle();
            if (mycpu.getDrawflag()) {
                glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer
                drawCubes(mycpu.getGfx());
                mycpu.setDrawflag(false);
                glfwSwapBuffers(window); // swap the buffers
            }
        }
    }

    private void drawCubes(char[][] gfx) {
        int x = -sidelength;
        int y;
        glColor3f(1.0f,1.0f,1.0f);
        for (int i = 0; i < gfx.length; i++) {
            x += 2 * sidelength;
            y = -sidelength;
            for (int j = 0; j < gfx[0].length; j++) {
                y += 2 * sidelength;
                if (gfx[i][j] == 1) {
                    draw(x, y);
                }
            }
        }
    }

    private void draw(int x, int y) {
        glBegin(GL_QUADS);
        glVertex2f(x + sidelength,y + sidelength);
        glVertex2f(x + sidelength,y - sidelength);
        glVertex2f(x - sidelength,y - sidelength);
        glVertex2f(x - sidelength,y + sidelength);
        glEnd();
    }

    private GLFWKeyCallbackI keyCallback() {
        return new GLFWKeyCallbackI() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_1:
                            mycpu.setKeys(true, 0);
                            break;
                        case GLFW_KEY_2:
                            mycpu.setKeys(true, 1);
                            break;
                        case GLFW_KEY_3:
                            mycpu.setKeys(true, 2);
                            break;
                        case GLFW_KEY_4:
                            mycpu.setKeys(true, 3);
                            break;
                        case GLFW_KEY_Q:
                            mycpu.setKeys(true, 4);
                            break;
                        case GLFW_KEY_W:
                            mycpu.setKeys(true, 5);
                            break;
                        case GLFW_KEY_E:
                            mycpu.setKeys(true, 6);
                            break;
                        case GLFW_KEY_R:
                            mycpu.setKeys(true, 7);
                            break;
                        case GLFW_KEY_A:
                            mycpu.setKeys(true, 8);
                            break;
                        case GLFW_KEY_S:
                            mycpu.setKeys(true, 9);
                            break;
                        case GLFW_KEY_D:
                            mycpu.setKeys(true, 10);
                            break;
                        case GLFW_KEY_F:
                            mycpu.setKeys(true, 11);
                            break;
                        case GLFW_KEY_Z:
                            mycpu.setKeys(true, 12);
                            break;
                        case GLFW_KEY_X:
                            mycpu.setKeys(true, 13);
                            break;
                        case GLFW_KEY_C:
                            mycpu.setKeys(true, 14);
                            break;
                        case GLFW_KEY_V:
                            mycpu.setKeys(true, 15);
                            break;
                    }
                } else if (action == GLFW_RELEASE) {
                    switch (key) {
                        case GLFW_KEY_1:
                            mycpu.setKeys(false, 0);
                            break;
                        case GLFW_KEY_2:
                            mycpu.setKeys(false, 1);
                            break;
                        case GLFW_KEY_3:
                            mycpu.setKeys(false, 2);
                            break;
                        case GLFW_KEY_4:
                            mycpu.setKeys(false, 3);
                            break;
                        case GLFW_KEY_Q:
                            mycpu.setKeys(false, 4);
                            break;
                        case GLFW_KEY_W:
                            mycpu.setKeys(false, 5);
                            break;
                        case GLFW_KEY_E:
                            mycpu.setKeys(false, 6);
                            break;
                        case GLFW_KEY_R:
                            mycpu.setKeys(false, 7);
                            break;
                        case GLFW_KEY_A:
                            mycpu.setKeys(false, 8);
                            break;
                        case GLFW_KEY_S:
                            mycpu.setKeys(false, 9);
                            break;
                        case GLFW_KEY_D:
                            mycpu.setKeys(false, 10);
                            break;
                        case GLFW_KEY_F:
                            mycpu.setKeys(false, 11);
                            break;
                        case GLFW_KEY_Z:
                            mycpu.setKeys(false, 12);
                            break;
                        case GLFW_KEY_X:
                            mycpu.setKeys(false, 13);
                            break;
                        case GLFW_KEY_C:
                            mycpu.setKeys(false, 14);
                            break;
                        case GLFW_KEY_V:
                            mycpu.setKeys(false, 15);
                            break;
                    }
                }
            }
        };
    }
}
