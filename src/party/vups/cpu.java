package party.vups;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class cpu {
    private int hz = 100;

    private Random numbergen = new Random();

    private short opcode;

    private boolean run = true;

    private boolean copy = true;

    private char[] keycopy;

    private char[] memory = new char[4096];

    private char[] v = new char[16];

    private short I, pc;

    char[] gfx = new char[64*32];

    private char delay_timer;

    private char sound_timer;

    private short[] stack = new short[16];

    boolean drawflag;

    private short sp;

    private char[] key = new char[16];

    private char[] fontset = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };

    public cpu() { }

    //Draws the pixels in system out
    protected void draw() {
        clearScreen();
        StringBuilder display = new StringBuilder();

        if (gfx[1] == 1)
            display.append(" * ");
        else
            display.append("   ");
        for (int i = 1; i < gfx.length; i++) {
            if (i % 64 == 0) {
                if (gfx[i] == 1)
                    display.append("\n * ");
                else
                    display.append("\n   ");
            } else {
                if (gfx[i] == 1)
                    display.append(" * ");
                else
                    display.append("   ");
            }
        }

        drawflag = false;
        System.out.println(display.toString());
    }

    private static void clearScreen() {
        for (int i =  0; i < 40; i++)
            System.out.println("\n");
    }

    void setKeys(boolean a, int i) {
        if (a)
            key[i] = 1;
        else
            key[i] = 0;
    }


    //Initializes the cpu
    void init() {
        pc = 0x200;
        opcode = 0;
        I = 0;
        sp = 0;
        drawflag = false;

        //Clear the memory
        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0;
        }

        //Clear v
        for (int i = 0; i < v.length; i++) {
            v[i] = 0;
        }

        //Clear the stack
        for (int i = 0; i < stack.length; i++) {
            stack[i] = 0;
        }

        //Load fontset
        System.arraycopy(fontset, 0, memory, 80, 80);

        //Resets timers
        delay_timer = 0;
        sound_timer = 0;

        File file = new File("C:\\Users\\andreas\\IdeaProjects\\CHIP-8\\games\\Chip-8 Demos\\Maze [David Winter, 199x].ch8");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int index = 0;
            int stream;
            while ((stream = fileInputStream.read()) != -1) {
                memory[index + 512] = (char) stream;
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Runs every cycle
    void cycle() throws InterruptedException {
        opcode = (short)(memory[pc] << 8 | memory[pc +1]);
        //System.out.println(String.format("Opcode: 0x%x", opcode));

        //field
        //Tests
        long milli = System.currentTimeMillis();
        double interval = (1000 / 60);
        while (((System.currentTimeMillis() - milli)) < interval){
            Thread.sleep(1);
        }
        decode(opcode);

        if (delay_timer > 0)
            delay_timer--;

        if (sound_timer > 0)
            if(sound_timer == 1)
                System.out.println("BEEP!\n");
            sound_timer--;
    }

    //Decodes and executes opcode
    private void decode(short opcode) throws InterruptedException {
        if (!run) {
            if (copy) {
                keycopy = key.clone();
                copy = false;
            }
        }
        switch (opcode & 0xF000) {
            case 0x0000:
                opcode00(opcode);
                break;


            case 0x1000:
                //1NNN
                //Jump to address 0x0NNN
                pc = (short) (opcode & 0x0FFF);
                break;

            case 0x2000:
                //2NNN
                //Jumps to subroutine at 0x0NNN
                stack[sp] = pc;
                sp++;
                pc = (short) (opcode & 0x0FFF);
                break;

            case 0x3000:
                //3XNN
                //Skips the next instruction if VX = NN
                if (v[(opcode & 0x0F00) >> 8] == (opcode & 0x00FF))
                    pc += 4;
                else
                    pc += 2;
                break;

            case 0x4000:
                //4XNN
                //Skips the next instruction if VX != NN
                if (v[(opcode & 0x0F00) >> 8] != (opcode & 0x00FF))
                    pc += 4;
                else
                    pc += 2;
                break;

            case 0x500:
                //5XY0
                //Skips the next instruction if VX = VY
                if (v[(opcode & 0x0F00) >> 8] ==  v[(opcode & 0x00F0) >> 4])
                    pc += 4;
                else
                    pc += 2;
                break;

            case 0x6000:
                //6XNN
                //Sets VX = NN
                v[(opcode & 0x0F00) >> 8] = (char) (opcode & 0x00FF);
                pc += 2;
                break;

            case 0x7000:
                //7XNN
                //VX += NN
                v[(opcode & 0x0F00) >> 8] += (char) (opcode & 0x00FF);
                pc += 2;
                break;

            case 0x8000:
                opcode90(opcode);
                break;

            case 0x9000:
                //9XY0
                //Skips the next instruction if VX != VY
                if (v[(opcode & 0x0F00) >> 8] != v[(opcode & 0x00F0) >> 4])
                    pc += 4;
                else
                    pc += 2;
                break;

            case 0xA000:
                //ANNN
                //Sets I to the address NNN.
                I = (short) (opcode & 0x0FFF);
                pc += 2;
                break;

            case 0xB000:
                //BNNN
                //Jumps to the address NNN plus V0. PC = NNN + PC
                pc = (short) (v[0x0] + (opcode & 0x0FFF));
                pc += 2;
                break;

            case 0xC000:
                //CXNN
                //Sets VX to the result of a bitwise and operation on a random number and NN
                v[(opcode & 0x0F00) >> 8] = (char) (numbergen.nextInt(255) & (opcode & 0x00FF));
                pc += 2;
                break;

            case 0xD000:
                //DXYN
                //Draws sprite
                short x = (short) v[(opcode & 0x0F00) >> 8];
                short y = (short) v[(opcode & 0x00F0) >> 4];
                short height = (short) (opcode & 0x000F);
                short pixel;

                v[0xF] = 0;
                for (int yline = 0; yline < height; yline++)
                {
                    pixel = (short) (memory[I + yline]);
                    for(int xline = 0; xline < 8; xline++)
                    {
                        if((pixel & (0x80 >> xline)) != 0)
                        {
                            if(gfx[(x + xline + ((y + yline) * 64))] == 1)
                                v[0xF] = 1;
                            gfx[x + xline + ((y + yline) * 64)] ^= 1;
                        }
                    }
                }

                drawflag = true;
                pc += 2;

                break;

            case 0x0E000:
                opcodee0(opcode);
                break;

            case 0x0F000:
                opcodef0(opcode);
                break;
        }
    }


    private void opcode00(short opcode) {
        switch (opcode) {
            //00E0
            //Clear screen
            case 0x00E0: {
                gfx = new char[64 * 32];
                pc += 2;
                break;
            }

            //00EE
            //Returns from a subroutine
            case 0x00EE:
                sp -= 1;
                pc = (short)(stack[sp] + 2);
                stack[sp] = 0;
                break;
        }
    }

    private void opcode90(short opcode) {
        switch (opcode & 0x000F){
            case 0x0000:
                //8XY0
                //VX = VY
                v[(opcode & 0x0F00) >> 8 ] = v[(opcode & 0x00F0) >> 4];
                pc += 2;
                break;

            case 0x0001:
                //8XY1
                //Sets VX = VX | VY
                v[(opcode & 0x0F00) >> 8 ] = (char) (v[(opcode & 0x0F00) >> 8] | v[(opcode & 0x00F0) >> 4]);
                pc += 2;
                break;

            case 0x0002:
                //8XY2
                //Sets VX = VX & VY
                v[(opcode & 0x0F00) >> 8] = (char) (v[(opcode & 0x0F00) >> 8] & v[(opcode & 0x00F0) >> 4]);
                pc += 2;
                break;

            case 0x0003:
                //8XY3
                //Sets VX = VX ^ VY
                v[(opcode & 0x0F00) >> 8] = (char) (v[(opcode & 0x0F00) >> 8] ^ v[(opcode & 0x00F0) >> 4]);
                pc += 2;
                break;

            case 0x0004:
                //8XY4
                //Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
                if(v[(opcode & 0x00F0) >> 4] > (0xFF - v[(opcode & 0x0F00) >> 8]))
                    v[0xF] = 1;
                else
                    v[0xF] = 0;
                v[(opcode & 0x0F00) >> 8] += v[(opcode & 0x00F0) >> 4];
                pc += 2;
                break;

            case 0x0005:
                //8XY5
                //VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                if(v[(opcode & 0x00F0) >> 4] > (v[(opcode & 0x0F00) >> 8]))
                    v[0xF] = 1;
                else
                    v[0xF] = 0;
                v[(opcode & 0x0F00) >> 8] -= v[(opcode & 0x00F0) >> 4];
                pc += 2;
                break;

            //TODO
            case 0x0006:

                break;

            case 0x0007:
                break;

            case 0x000E:
                break;
        }
    }

    private void opcodee0(short opcode) {
        switch (opcode & 0x000F) {
            case 0x000E:
                //EX9E
                //Skips the next instruction if the key stored in VX is pressed.
                if (key[v[(opcode & 0x0F00) >> 8]] != 0)
                    pc += 4;
                else
                    pc += 2;
                break;

            case 0x0001:
                //EX91
                //Skips the next instruction if the key is not stored in VX is pressed.
                if (key[v[(opcode & 0x0F00) >> 8]] == 0)
                    pc += 4;
                else
                    pc += 2;
                break;
        }
    }

    private void opcodef0(short opcode) {
        switch (opcode & 0x000F) {
            case 0x0007:
                //FX07
                //VX = delay timer
                v[(opcode & 0x0F00) >> 8] = delay_timer;
                pc += 2;
                break;

            case 0x000A:
                //FX0A
                //A key press is awaited, and then stored in VX.
                if (!run) {
                    if (!Arrays.equals(keycopy, key)) {
                        for (int i = 0; i < key.length; i++) {
                            if (key[i] != keycopy[i]) {
                                v[(opcode & 0x0F00) >> 8] = (char) i;
                                copy = true;
                                run = true;
                                pc += 2;
                            }
                        }
                    }
                } else
                    run = false;
                break;

            case 0x0005:
                switch (opcode & 0x00FF) {
                    case 0x0015:
                        //FX15
                        //Sets the delay timer to VX.
                        delay_timer = v[(opcode & 0x0F00) >> 8];
                        pc += 2;
                        break;

                    case 0x0055:
                        //FX55
                        //Stores V0 to VX (including VX) in memory starting at address I.
                        System.arraycopy(v, 0, memory, I, ((opcode & 0x0F00) >> 8) + 1);
                        pc += 2;
                        break;

                    case 0x0065:
                        //FX65
                        //Fills V0 to VX (including VX) with values from memory starting at address I
                        System.arraycopy(memory, I, v, 0, ((opcode & 0x0F00) >> 8) + 1);
                        pc += 2;
                        break;
                }
                break;

            case 0x0008:
                //FX18
                //Sets the sound timer to VX
                sound_timer = v[(opcode & 0x0F00) >> 8];
                pc += 2;
                break;

            case 0x000E:
                //FX1E
                //I += VX
                I += v[(opcode & 0x0F00) >> 8];
                pc += 2;
                break;

            case 0x0009:
                //FX29
                //Sets I to the location of the sprite for the character in VX.
                I = (short)(fontset[v[(opcode & 0x0F00) >> 8]]);
                pc += 2;
                break;

            case 0x0003:
                //FX33
                memory[I] = (char) (v[(opcode & 0x0F00) >> 8] / 100);
                memory[I + 1] = (char) (((v[(opcode & 0x0F00) >> 8] / 10)) % 10);
                memory[I + 2] = (char) (((v[(opcode & 0x0F00) >> 8] / 100)) % 10);
                pc += 2;
                break;

            default:
                System.out.println(String.format("No opcode found for 0x%x", opcode));
        }
    }


}
