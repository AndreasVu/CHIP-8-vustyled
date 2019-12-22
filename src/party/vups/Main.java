package party.vups;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        if (args.length < 1) {
            System.exit(-1);
        }
        cpu mycpu = new cpu(args[0]);
        ui newui = new ui(mycpu);
        newui.run();
    }


    /*KeyListener keys = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("bruh");
            switch (e.getKeyCode()) {
                case KeyEvent.VK_1:
                    key[0] = 1;
                    break;

                case KeyEvent.VK_2:
                    key[1] = 1;
                    break;

                case KeyEvent.VK_3:
                    key[2] = 1;
                    break;

                case KeyEvent.VK_4:
                    key[3] = 1;
                    break;

                case KeyEvent.VK_Q:
                    key[4] = 1;
                    break;

                case KeyEvent.VK_W:
                    key[5] = 1;
                    break;

                case KeyEvent.VK_E:
                    key[6] = 1;
                    break;

                case KeyEvent.VK_R:
                    key[7] = 1;
                    break;

                case KeyEvent.VK_A:
                    key[8] = 1;
                    break;

                case KeyEvent.VK_S:
                    key[9] = 1;
                    break;

                case KeyEvent.VK_D:
                    key[10] = 1;
                    break;

                case KeyEvent.VK_F:
                    key[11] = 1;
                    break;

                case KeyEvent.VK_Z:
                    key[12] = 1;
                    break;

                case KeyEvent.VK_X:
                    key[13] = 1;
                    break;

                case KeyEvent.VK_C:
                    key[14] = 1;
                    break;

                case KeyEvent.VK_V:
                    key[15] = 1;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_1:
                    key[0] = 0;
                    break;

                case KeyEvent.VK_2:
                    key[1] = 0;
                    break;

                case KeyEvent.VK_3:
                    key[2] = 0;
                    break;

                case KeyEvent.VK_4:
                    key[3] = 0;
                    break;

                case KeyEvent.VK_Q:
                    key[4] = 0;
                    break;

                case KeyEvent.VK_W:
                    key[5] = 0;
                    break;

                case KeyEvent.VK_E:
                    key[6] = 0;
                    break;

                case KeyEvent.VK_R:
                    key[7] = 0;
                    break;

                case KeyEvent.VK_A:
                    key[8] = 0;
                    break;

                case KeyEvent.VK_S:
                    key[9] = 0;
                    break;

                case KeyEvent.VK_D:
                    key[10] = 0;
                    break;

                case KeyEvent.VK_F:
                    key[11] = 0;
                    break;

                case KeyEvent.VK_Z:
                    key[12] = 0;
                    break;

                case KeyEvent.VK_X:
                    key[13] = 0;
                    break;

                case KeyEvent.VK_C:
                    key[14] = 0;
                    break;

                case KeyEvent.VK_V:
                    key[15] = 0;
                    break;
            }
        }
    };*/
}
