
import java.io.*;


public class Simulator {


    static int pc;
    static int reg[] = new int[32];
    static String name = "random10";
    static String fileName = "InstrTest/test_" + name + ".bin";
    static String fileNameRes = "InstrTest/test_" + name + ".res";



    public static void main(String[] args) throws IOException {

        pc = 0;
        int instr,opcode,rd,rs1, rs2, funct3, funct7,shamt,remainder;
        int imm_B1, imm_B2,imm_S1,imm_S2,imm,imm_B,imm_J,imm_U,imm_S;
        int counter,offset;
        long MulResult;
        char ch;


        int[] progr = readBinaryFile(fileName);

        /*
        // Prints list of all instructions
        System.out.println("List of Instructions");
        for (int k = 0; k < progr.length; k++){
            if(progr[k] != 0){
                System.out.println(String.format("0x%08X", progr[k]));
            }
        }
        System.out.println();
        */


        while(pc < progr.length) {

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
                            remainder = (reg[rs1] + imm)%4;
                            if(progr[reg[rs1] + imm -remainder] > 127 || progr[reg[rs1] + imm -remainder] < -128){
                                reg[rd] = (byte) (progr[reg[rs1] + imm - remainder] >> 8*remainder);
                            } else {
                                reg[rd] = (byte) progr[reg[rs1] + imm];
                            }
                            break;
                        case 0x01: // LH - Load Halfword
                            remainder = (reg[rs1] + imm)%4;
                            if(progr[reg[rs1] + imm -remainder] > 32767 || progr[reg[rs1] + imm -remainder] < -32768){
                                reg[rd] = (short) (progr[reg[rs1] + imm-remainder] >> 8*remainder);
                            } else {
                                reg[rd] = (short) progr[reg[rs1] + imm];
                            }
                            break;
                        case 0x02: // LW - Load Word
                            reg[rd] = progr[reg[rs1]+imm];
                            break;
                        case 0x04: // LBU - Load Byte Unsigned
                            remainder = (reg[rs1] + imm)%4;
                            if(progr[reg[rs1] + imm -remainder] > 127 || progr[reg[rs1] + imm -remainder] < -128){
                                reg[rd] = (byte) (progr[reg[rs1] + imm - remainder] >> 8*remainder) & 0xFF;
                            } else {
                                reg[rd] = (byte) progr[reg[rs1] + imm] & 0xFF;
                            }
                            break;
                        case 0x05: // LHU - Load Halfword Unsigned
                            remainder = (reg[rs1] + imm)%4;
                            if(progr[reg[rs1] + imm -remainder] > 32767 || progr[reg[rs1] + imm -remainder] < -32768){
                                reg[rd] = (short) (progr[reg[rs1] + imm-remainder] >> 8*remainder) & 0xFFFF;
                            } else {
                                reg[rd] = (short) progr[reg[rs1] + imm] & 0xFFFF;
                            }
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
                            reg[rd] = (Integer.toUnsignedLong(reg[rs1]) < Integer.toUnsignedLong(imm)) ? 1 : 0;
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
                    reg[rd] = (pc-1)*4 + imm_U;
                    break;

                case 0x23: // Opcode 0100011
                    switch (funct3){
                        case 0x00: // SB - Store Byte
                            progr[(reg[rs1] + imm_S)] = (byte) reg[rs2];
                            break;
                        case 0x01: // SH - Store Halfword
                            progr[(reg[rs1] + imm_S)] = (short) reg[rs2];
                            break;
                        case 0x02: // SW - Store Word
                            progr[reg[rs1] + imm_S] = reg[rs2];
                            break;
                        default:
                            System.out.println("For opcode 0x23, funct3" + funct3 + " has not been implemented yet");
                    }
                    break;

                case 0x33: // Opcode 0110011

                    switch (funct3) {
                        case 0x00:
                            switch (funct7) {
                                case 0x00: //ADD
                                    reg[rd] = reg[rs1] + reg[rs2];
                                    break;
                                case 0x01: // MUL
                                    MulResult = reg[rs1] * reg[rs2];
                                    reg[rd] = (int) MulResult;
                                    break;
                                case 0x20: //SUB
                                    reg[rd] = reg[rs1] - reg[rs2];
                                    break;
                            }
                            break;
                        case 0x01:
                            switch (funct7){
                                case 0x00: // SLL - Shift Left
                                    reg[rd] = reg[rs1] << reg[rs2];
                                    break;
                                case 0x01: // MULH
                                    MulResult = (long) reg[rs1] * reg[rs2];
                                    reg[rd] = (int) (MulResult >> 32);
                                    break;
                            }
                            break;
                        case 0x02:
                            switch (funct7){
                                case 0x00: // SLT - Set Less Than
                                    reg[rd] = (reg[rs1] < reg[rs2]) ? 1 : 0;
                                    break;
                                case 0x01: // MULHSU
                                    MulResult = (long) reg[rs1] * Integer.toUnsignedLong(reg[rs2]);
                                    reg[rd] = (int) (MulResult >> 32);
                                    break;
                            }
                            break;
                        case 0x03:
                            switch (funct7){
                                case 0x00: // SLTU - Set Less Than Unsigned
                                    reg[rd] = (Integer.toUnsignedLong(reg[rs1]) < Integer.toUnsignedLong(reg[rs2])) ? 1 : 0;
                                    break;
                                case 0x01: // MULHU
                                    MulResult = Integer.toUnsignedLong(reg[rs1]) * Integer.toUnsignedLong(reg[rs2]);
                                    reg[rd] = (int) (MulResult >> 32);
                                    break;
                            }
                            break;
                        case 0x04:
                            switch (funct7){
                                case 0x00: // XOR
                                    reg[rd] = reg[rs1] ^ reg[rs2];
                                    break;
                                case 0x01: // DIV
                                    if(reg[rs2] == 0){ // Checks if divisor is equal to 0
                                        reg[rd] = -1;
                                    } else {
                                        reg[rd] = reg[rs1]/reg[rs2];
                                    }
                                    break;
                            }
                            break;
                        case 0x05:
                            switch (funct7) {
                                case 0x00://SRL - Shift Right
                                    reg[rd] = reg[rs1] >>> reg[rs2];
                                    break;
                                case 0x01: // DIVU
                                    if(reg[rs2] == 0){ // Checks if divisor is equal to 0
                                        reg[rd] = reg[rs1];
                                    } else {
                                        reg[rd] = (int) (Integer.toUnsignedLong(reg[rs1])/Integer.toUnsignedLong(reg[rs2]));
                                    }
                                    break;
                                case 0x20://SRA - Shift Right Arithmetic
                                    reg[rd] = reg[rs1] >> reg[rs2];
                                    break;
                            }
                            break;
                        case 0x06: //OR
                            switch (funct7){
                                case 00: // OR
                                    reg[rd] = reg[rs1] | reg[rs2];
                                    break;
                                case 0x01: // Rem - Remainder
                                    if(reg[rs2] == 0){ // Checks if divisor is equal to 0
                                        reg[rd] = reg[rs1];
                                    } else {
                                        reg[rd] = reg[rs1] % reg[rs2];
                                    }
                                    break;
                            }
                            break;
                        case 0x07:
                            switch (funct7){
                                case 0x01: // REMU - Remainder Unsigned
                                    if(reg[rs2] == 0){ // Checks if divisor is equal to 0
                                        reg[rd] = (int) Integer.toUnsignedLong(reg[rs1]);
                                    } else {
                                        reg[rd] = (int) (Integer.toUnsignedLong(reg[rs1]) % Integer.toUnsignedLong(reg[rs2]));
                                    }
                                    break;
                                case 0x00: // AND
                                    reg[rd] = reg[rs1] & reg[rs2];
                                    break;
                            }
                            break;

                    }
                    break;

                case 0x37: // opcode 0110111 LUI - Load Upper Immediate
                    reg[rd] = imm_U;
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
                            if (Integer.toUnsignedLong(reg[rs1]) < Integer.toUnsignedLong(reg[rs2])){
                                pc = pc + imm_B - 1;
                            }
                            break;
                        case 0x07: // BGEU - Branch >= Unsigned
                            if (Integer.toUnsignedLong(reg[rs1]) >= Integer.toUnsignedLong(reg[rs2])){
                                pc = pc + imm_B - 1;
                            }
                            break;
                    }
                    break;

                case 0x67: // Opcode 1100111 JALR - Jump and Link Register
                    reg[rd] = pc*4;
                    pc = (reg[rs1] + imm)/4;
                    break;

                case 0x6f: // Opcode 1101111 JAL - Jump and Link
                    reg[rd] = pc*4;
                    pc += imm_J -1;

                    break;

                case 0x73: // ecall
                    switch (reg[10]){
                        case 0x01:
                            System.out.print(reg[11]);
                            break;
                        case 0x04: // Prints null-terminated string whose adress is in a1
                            counter = 0;
                            offset = 0;
                            for(;;){
                                if (counter == 4){
                                    offset +=1;
                                    counter=0;
                                }
                                ch = (char) ((progr[(reg[11]/4)+offset] >> 8*counter) & 0xFF);
                                if (ch == 0){
                                    break;
                                }
                                System.out.print(ch);
                                counter++;
                            }
                            System.out.println();
                            break;
                        case 0x09: // allocates a1 bytes on the heap, returns pointer to start in a0 - INCOMPLETE
                            break;
                        case 0x0a:
                            System.out.println("Result:");
                            for (int i = 0; i < reg.length; ++i) {
                                System.out.print(reg[i] + " ");
                            }
                            System.out.println();
                            printResult(fileNameRes);
                            System.exit(0);
                            break;
                        case 0x0b:
                            System.out.println((char) reg[11]);
                            break;
                        case 0x2f:
                            System.out.println("Exited with error code " + reg[11]);
                            break;
                        default:
                            System.out.println("Invalid ecall " + reg[10]);
                    }
                    break;

                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }

            reg[0] = 0; // Forces x0 = 0;

            /*
            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            System.out.println();
            */

        }


    }
    private static int[] readBinaryFile(String input) throws IOException {

        File data = new File(input);

        int FileLength = (int) data.length()/4; // Length of file in words
        int instructionList[]= new int[0x1FFFFFF];


        // Opens a binary file.
        FileInputStream fstream =
                new FileInputStream(input);
        DataInputStream inputFile =
                new DataInputStream(fstream);

        for (int i = 0; i < FileLength; i++){
            for(int j = 0; j <= 3; j++){
                instructionList[i] += (inputFile.readByte() & 0xFF) << (8*j);
            }
        }

        // Closes the file.
        inputFile.close();

        return instructionList;

    }

    private static void printResult(String input) throws IOException{
        File data = new File(input);

        int FileLength = (int) data.length()/4; // Length of file in words
        int resultList[]= new int[FileLength];


        // Opens a binary file.
        FileInputStream fstream =
                new FileInputStream(input);
        DataInputStream inputFile =
                new DataInputStream(fstream);

        for (int i = 0; i < FileLength; i++){
            for(int j = 0; j <= 3; j++){
                resultList[i] += (inputFile.readByte() & 0xFF) << (8*j);
            }
        }
        // Closes the file.

        inputFile.close();
        System.out.println("Expected Result:");
        for (int i = 0; i < resultList.length; ++i) {
            System.out.print(resultList[i] + " ");
        }

    }


}
