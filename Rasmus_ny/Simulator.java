
import java.io.*;


public class Simulator {


    static int pc;
    static int reg[] = new int[31];
    static String fileName = "branchmany.bin";
    static int[] mem = new int[0x100000];



    public static void main(String[] args) throws IOException {

        pc = 0;
        int instr,opcode,rd,rs1, rs2, funct3, funct7,shamt;
        int imm_B1, imm_B2,imm_S1,imm_S2,imm,imm_B,imm_J,imm_U,imm_S;


        int[] progr = readBinaryFile(fileName);

        // Prints list of all instructions
        System.out.println("List of Instructions");
        for (int k = 0; k < progr.length; k++){
            System.out.println(String.format("0x%08X", progr[k]));
        }
        System.out.println();





        // Selv Lavet tests
        /*
        int[] progr = new int[9];
        progr[0] = 0x014000ef;
        progr[1] = 0xffd00593;
        progr[2] = 0x01c00613;
        progr[3] = 0x00b60633;
        progr[4] = 0x00c000ef;
        progr[5] = 0x30900593;
        progr[6] = 0x00008067;
        progr[7] = 0x00a00513;
        progr[8] = 0x00000073;
        for (int k = 0; k < progr.length; k++){
            System.out.println(String.format("0x%08X", progr[k]));
        }
        */




        // Test af imm_J
        /*
        instr = progr[5];
        imm = (instr >> 12) & 0xFFFFF;
        imm_J = ((imm & 0x80000) <<  8) | ((imm & 0x7FE00) >> 8) | ((imm & 100) << 3) | ((imm & 0xFF) << 12);
        imm_J = ((imm > 524287) ? (-(((~imm_J)+1)& 0xFFFFFFFF))/4 : imm_J/4);
        System.out.println("imm   = " + Integer.toBinaryString(imm));
        System.out.println("imm_J = " + Integer.toBinaryString(imm_J));
        System.out.println("imm_J = " + imm_J);

        imm_B = (((((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0xFFFFF7FE) | (((((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0x00000001) << 11))/4;
        imm_J = (((instr >> 20) & 0xFFF007FE) | ((instr >>> 9) & 0x00000800) | (instr & 0x000FF000))/4;

        System.out.println("Deres metode imm_J = " +imm_J);
        System.out.println("Deres metode imm_J =" + Integer.toBinaryString(imm_J));
        System.out.println();
        */


        // Test af imm_S

        /*
        instr = progr[1];
        imm_S1 = (instr >> 7) & 0x1f;
        imm_S2 = (instr >>> 25) ;
        imm_S = (imm_S2 << 5) | imm_S1;
        imm_S = ((imm_S2 >63) ? (-(((~imm_S)+1)& 0xFFF)) : imm_S);
        System.out.println("imm2 = "+ Integer.toBinaryString(imm_S2) + " imm1 = " + Integer.toBinaryString(imm_S1));
        System.out.println(Integer.toBinaryString(imm_S));
        System.out.println("imm_S = " + imm_S);

        imm_S = (((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F));
        System.out.println("Deres metode imm_S = " +imm_S);
        System.out.println("Deres metode imm_S =" + Integer.toBinaryString(imm_S));
        System.out.println();
        */



        for (;;) {

            instr = progr[pc];
            opcode = instr & 0x7f;
            rd = (instr >> 7) & 0x1f;
            funct3 = (instr >> 12) & 0x07;
            rs1 = (instr >> 15) & 0x1f;
            rs2 = (instr >> 20) & 0x1f;
            imm_B1 = (instr >> 7) & 0x1f;
            imm_B2 = (instr >>> 25);
            imm_S1 = imm_B1;
            imm_S2 = imm_B2;
            funct7 = (instr >> 25);
            shamt = (instr >> 20) & 0x01f;

            // Immediate value for I-type
            imm = (instr >> 20);

            // Immediate value for S-type
            imm_S = (imm_S2 << 5) | imm_S1;
            imm_S = ((imm_S2 > 63) ? (-(((~imm_S)+1)& 0xFFF)) : imm_S);

            // Immediate value for B-type
            imm_B = ((imm_B2 << 5) & 0x3E0) | ((imm_B1 << 10) & 0x400) | ((imm_B2 << 5) & 0x800) | (imm_B1 & 0x01E);
            imm_B = ((imm_B2 >63) ? (-(((~imm_B)+1)& 0xFFF))/4 : imm_B/4);

            // Immediate value for U-type
            imm_U = (instr >> 12) << 12;

            // Immediate value for J-type
            imm_J = (((instr >> 20) & 0xFFF007FE) | ((instr >>> 9) & 0x00000800) | (instr & 0x000FF000))/4;


            ++pc; // We count in 4 byte words

            switch (opcode) {
                case 0x03: // Opcode 0000011
                    switch (funct3){
                        case 0x00: // LB - Load Byte
                            reg[rd] = (byte) mem[reg[rs1]] + imm;
                            break;
                        case 0x01: // LH - Load Halfword
                            System.out.println("LH - Load Halfword has not been implemented yet");
                            System.out.println("rd = " + rd + " reg[rd] = " + reg[rd] + " reg[rs1] = " + reg[rs1] + " imm = " + imm);
                            reg[rd] = (short) mem[reg[rs1]] + imm;
                            break;
                        case 0x02: // LW - Load Word
                            reg[rd] = mem[reg[rs1]] + imm;
                            break;
                        case 0x04: // LBU - Load Byte Unsigned
                            reg[rd] = (byte) (mem[reg[rs1]] + imm) & 0xFF;
                            break;
                        case 0x05: // LHU - Load Halfword Unsigned
                            System.out.println("Load Halfword Unsigned has not been implemented yet");
                            reg[rd] = (short) (mem[reg[rs1]] + imm) & 0xFFFF;
                            break;
                        default:
                            System.out.println("For opcode 0x03, funct3" + funct3 + " has not been implemented yet");
                    }
                    break;

                case 0x13: // Opcode 0010011

                    switch (funct3){
                        case 0x00: // Addi
                            reg[rd] = reg[rs1] + imm;
                            break;
                        case  0x01: // SLLI
                            reg[rd] = reg[rs1] << shamt;
                            break;
                        case 0x02: // SLTI
                            reg[rd] = (reg[rs1] < imm) ? 1 : 0;
                            break;
                        case 0x03: // SLTIU
                            reg[rd] = (reg[rs1] < imm) ? 1 : 0;
                            break;
                        case 0x04: // XORI
                            reg[rd] = reg[rs1] ^imm;
                            break;
                        case 0x05: // funct3 = 101
                            switch (funct7){
                                case 0x00: // SRLI
                                    reg[rd] = reg[rs1] >>> shamt;
                                    break;
                                case 0x20: // SRAI
                                    reg[rd] = reg[rs1] >> shamt;
                                    break;
                            }
                            break;
                        case 0x06: // ORI
                            reg[rd] = reg[rs1] | imm;
                            break;
                        case 0x07: // ANDI
                            reg[rd] = reg[rs1] & imm;
                            break;
                        default:
                            System.out.println("For opcode 0x13, funct3" + funct3 + " has not been implemented yet");
                    }
                    break;

                case 0x17: // opcode 0010111 AUIPC - Add Upper Immediate to PC
                    reg[rd] = pc + imm_U;
                    break;

                case 0x23: // Opcode 0100011
                    switch (funct3){
                        case 0x00: // SB - Store Byte
                            mem[(reg[rs1] + imm_S)] = (byte) reg[rs2];
                            break;
                        case 0x01: // SH - Store Halfword
                            mem[(reg[rs1] + imm_S)] = (short) reg[rs2];
                            break;
                        case 0x02: // SW - Store Word
                            System.out.println("SW. rs1 = " + rs1 + " reg[rs1] = " + reg[rs1] + " imm = " + imm_S + " rs2 = " + rs2 +" reg[rs2] = " + reg[rs2]);
                            mem[reg[rs1] + imm_S] = reg[rs2];
                            break;
                        default:
                            System.out.println("For opcode 0x23, funct3" + funct3 + " has not been implemented yet");
                    }
                    break;

                case 0x33: // Opcode 0110011

                    switch (funct3) {
                        case 0x00: //ADD or SUB
                            switch (funct7) {
                                case 0x00: //ADD
                                    reg[rd] = reg[rs1] + reg[rs2];
                                    break;
                                case 0x20: //SUB
                                    reg[rd] = reg[rs1] - reg[rs2];
                                    break;
                            }
                            break;
                        case 0x01: //SLL - Shift Left
                            reg[rd] = reg[rs1] << reg[rs2];
                            break;
                        case 0x02: //SLT - Set Less Than
                            reg[rd] = (reg[rs1] < reg[rs2]) ? 1 : 0;
                            break;
                        case 0x03: //SLTU - Set Less Than Unsigned
                            reg[rd] = (reg[rs1] < reg[rs2]) ? 1 : 0;
                            break;
                        case 0x04: //XOR
                            reg[rd] = reg[rs1] ^ reg[rs2];
                            break;
                        case 0x05: //SRL or SRA
                            switch (funct7) {
                                case 0x00://SRL - Shift Right
                                    reg[rd] = reg[rs1] >>> reg[rs2];
                                    break;
                                case 0x20://SRA - Shift Right Arithmetic
                                    reg[rd] = reg[rs1] >> reg[rs2];
                                    break;
                            }
                            break;
                        case 0x06: //OR
                            reg[rd] = reg[rs1] | reg[rs2];
                            break;
                        case 0x07: //AND
                            reg[rd] = reg[rs1] & reg[rs2];
                            break;

                    }
                    break;

                case 0x37: // opcode 0110111 LUI - Load Upper Immediate
                    reg[rd] = reg[rs1] + imm_U;
                    break;

                case 0x63: //Opcode 1100011

                    switch (funct3) {
                        case 0x00: // BEQ - Branch Equal
                            if (reg[rs1] == reg[rs2]){
                                pc = pc + imm_B - 1;
                            }
                            break;
                        case 0x01: // BNE - Branch Not Equal
                            if (reg[rs1] != reg[rs2]){
                                pc = pc + imm_B - 1;
                            }
                            break;
                        case 0x04: // BLT - Branch Less Than
                            if (reg[rs1] < reg[rs2]){
                                pc = pc + imm_B - 1;
                            }
                            break;
                        case 0x05: // BGE - Branch Greater Than or Equal
                            if (reg[rs1] >= reg[rs2]){
                                pc = pc + imm_B - 1;
                            }
                            break;
                        case 0x06: // BLTU - Branch Less Than Unsigned
                            if (reg[rs1] < reg[rs2]){
                                pc = pc + imm_B - 1;
                            }
                            break;
                        case 0x07: // BGEU - Branch >= Unsigned
                            if (reg[rs1] >= reg[rs2]){
                                pc = pc + imm_B - 1;
                            }
                            break;
                    }
                    break;

                case 0x67: // Opcode 1100111 JALR - Jump and Link Register
                    reg[rd] = pc;
                    pc = reg[rs1] + imm;
                    break;

                case 0x6f: // Opcode 1101111 JAL - Jump and Link
                    reg[rd] = pc;
                    pc += imm_J -1;

                    break;

                case 0x73: // ecall
                    switch (reg[10]){
                        case 0x01:
                            System.out.print(reg[11]);
                            break;
                        case 0x04:
                            System.out.println("prints the null-terminated string whose address is in a1 - INCOMPLETE");
                            break;
                        case 0x09:
                            System.out.println("allocates a1 bytes on the heap, returns pointer to start in a0 - INCOMPLETE");
                            break;
                        case 0x0a:
                            System.out.println("DONE");
                            System.exit(0);
                            break;
                        case 0x0b:
                            System.out.println((char) reg[11]);
                            break;
                        case 0x2f:
                            break;
                        default:
                            System.out.println("Environmental Call " + reg[10] + " not yet implemented");
                    }
                    break;

                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }

            reg[0] = 0; // Sets x0 = 0;

            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            System.out.println();

            if (pc >= progr.length) {
                break;
            }

        }


    }
    private static int[] readBinaryFile(String input) throws IOException {
        int[] number = new int[4]; // Holds a number
        int counter = 0;
        int instruction[]= new int[1000];

        boolean endOfFile = false; // End of file flag

        // Opens a binary file.
        FileInputStream fstream =
                new FileInputStream(input);
        DataInputStream inputFile =
                new DataInputStream(fstream);

        System.out.print("   ***Reading numbers from the binary file.");

        // Read data from the file.
        while (!endOfFile) {
            try {
                for (int i = 0; i<=3; i++){
                    number[i]=inputFile.readByte();
                    number[i] = number[i] & 0xFF;
                }
                //System.out.println("\n" + number[0] + " " + number[1] + " "+ number[2] + " " + number[3] + " ");
                instruction[counter] = number[0] + (number[1] << 8) + (number[2] << 16) + (number[3] << 24);
                counter++;

            } catch (Exception e) {
                endOfFile = true;
            }

        }
        // Closes the file.
        inputFile.close();

        int[] instructionList = new int[counter]; // Loads instructions into array of correct size
        for (int j = 0; j<counter; j++){
            instructionList[j] = instruction[j];
        }

        System.out.println("\n   ***Done with reading from a binary file.");
        return instructionList;

    }

}
