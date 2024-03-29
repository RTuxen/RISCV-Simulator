package Assignment;

import java.io.*;


public class Simulator {

    // Registers
    static int reg[] = new int[32];

    // Sets memory size to 10 MB
    static int memorySize = 0x2625A0;

    // Memory array
    static int[] progr = new  int[memorySize];

    // Name of test Folder
    static String folderName = "cae-test";

    // Name of test
    static String testname;

    // If enabled, the program prints output in console
    static boolean outPutInConsole = false;


    public static void main(String[] args) throws IOException {

        testname = args[0]; // Sets name of test to input from command line argument

        int pc = 0; // Initializes Program Counter

        // values used in program decoding
        int instr,opcode,rd,rs1, rs2, funct3, funct7,shamt,remainder,val;
        int imm_B1, imm_B2,imm_S1,imm_S2,imm,imm_B,imm_J,imm_U,imm_S;
        byte byte1,byte2,byte3,byte4;

        // values used in for ecalls and 32 bit multiplication
        int bytenum,offset;
        long MulResult;
        char ch;


        int length = readBinaryFile("./" + folderName + "/" + testname + ".bin"); // loads memory with instructions


        // Decoding and execute loop
        // Runs until there are no more instructions
        // or programs ends with ecall 10 or 17
        while(pc < length) {

            instr = progr[pc];                      // Gets 32 bit instruction from memory
            opcode = instr & 0x7f;                  // Gets opcode
            rd = (instr >> 7) & 0x1f;               // Gets rd
            funct3 = (instr >> 12) & 0x07;          // Gets funct3
            rs1 = (instr >> 15) & 0x1f;             // Gets rs1
            rs2 = (instr >> 20) & 0x1f;             // Gets rs1
            imm_B1 = (instr >> 7) & 0x1f;           // Gets lower imm_B
            imm_B2 = (instr >>> 25);                // Gets upper imm_B
            imm_S1 = imm_B1;                        // Gets lower imm_S
            imm_S2 = imm_B2;                        // Gets upper imm_S
            funct7 = (instr >> 25);                 // Gets funct7
            shamt = (instr >> 20) & 0x01f;          // Gets shamt

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


            ++pc; // Program counts in 4 byte words

            switch (opcode) {

                case 0x03: // Opcode 0000011

                    switch (funct3){
                        case 0x00: // LB - Load Byte
                            reg[rd] = getByte(reg[rs1],imm,0);
                            break;
                        case 0x01: // LH - Load Halfword
                            byte1 = getByte(reg[rs1],imm,0);
                            byte2 = getByte(reg[rs1],imm,1);
                            reg[rd] = (short)(((byte2 & 0xFF) << 8) | (byte1 & 0xFF));
                            break;
                        case 0x02: // LW - Load Word
                            byte1 = getByte(reg[rs1],imm,0);
                            byte2 = getByte(reg[rs1],imm,1);
                            byte3 = getByte(reg[rs1],imm,2);
                            byte4 = getByte(reg[rs1],imm,3);
                            reg[rd] = ( ((byte4 & 0xFF) << 24) | ((byte3 & 0xFF) << 16) | ((byte2 & 0xFF) << 8) | (byte1 & 0xFF));
                            break;
                        case 0x04: // LBU - Load Byte Unsigned
                            reg[rd] = getByte(reg[rs1],imm,0) & 0xFF;
                            break;
                        case 0x05: // LHU - Load Halfword Unsigned
                            byte1 = getByte(reg[rs1],imm,0);
                            byte2 = getByte(reg[rs1],imm,1);
                            reg[rd] = (short)(((byte2 & 0xFF) << 8) | (byte1 & 0xFF)) & 0xFFFF;
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
                            remainder = (reg[rs1] + imm_S)%4;
                            val = (reg[rs1] + imm_S)/4;

                            byte1 = (byte) reg[rs2];
                            progr[val] &= ~(0xFF << 8*remainder);
                            progr[val] |= (byte1 << 8*remainder) & (0xFF << 8*remainder);
                            break;
                        case 0x01: // SH - Store Halfword
                            remainder = (reg[rs1] + imm_S)%4;
                            val = (reg[rs1] + imm_S)/4;

                            if(remainder == 0){
                                progr[val] &= 0xFFFF0000;
                                progr[val] |= (short) reg[rs2];
                            } else  if(remainder ==1){
                                progr[val] &= 0xFF0000FF;
                                progr[val] |= ((short) reg[rs2] << 8) & 0x00FFFF00;
                            } else  if(remainder ==2){
                                progr[val] &= 0x0000FFFF;
                                progr[val] |= (short) reg[rs2] << 16;
                            } else {
                                progr[val] &= 0x00FFFFFF;
                                progr[val+1] &= 0xFFFFFF00;
                                progr[val] |= (short) reg[rs2] << 24;
                                progr[val+1] |= ((short) reg[rs2] >>> 8) & 0x000000FF;
                            }
                            break;
                        case 0x02: // SW - Store Word
                            remainder = (reg[rs1] + imm_S)%4;
                            val = (reg[rs1] + imm_S)/4;


                            if(remainder == 0){
                                progr[val] = reg[rs2];
                            } else  if(remainder ==1){
                                progr[val] &= 0x000000FF;
                                progr[val+1] &= 0xFFFFFF00;
                                progr[val] |= reg[rs2] << 8;
                                progr[val+1] |= (reg[rs2] >>> 24);
                            } else  if(remainder ==2){
                                progr[val] &= 0x0000FFFF;
                                progr[val+1] &= 0xFFFF0000;
                                progr[val] |= reg[rs2] << 16;
                                progr[val+1] |= (reg[rs2] >>> 16);
                            } else {
                                progr[val] &= 0x00FFFFFF;
                                progr[val+1] &= 0xFF000000;
                                progr[val] |= reg[rs2] << 24;
                                progr[val+1] |= (reg[rs2] >>> 8);
                            }


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

                case 0x73: // Environmental Calls (ecall)

                    switch (reg[10]){ // ecall type depend on value i a0

                        case 0x01: // prints integer in a1
                            System.out.print(reg[11]);
                            break;

                        case 0x04: // Prints null-terminated string whose address is in a1
                            bytenum = 0;
                            offset = 0;
                            for(;;){
                                if (bytenum == 4){
                                    offset +=1;
                                    bytenum=0;
                                }
                                ch = (char) ((progr[(reg[11]/4)+offset] >> 8*bytenum) & 0xFF);
                                if (ch == 0){
                                    break;
                                }
                                System.out.print(ch);
                                bytenum++;
                            }
                            break;

                        case 0x09: // allocates a1 bytes on the heap, returns pointer to start in a0 - INCOMPLETE
                            break;

                        case 0x0a: // ends the program
                            printResultConsole();
                            writeOutputFile();
                            javafx.application.Application.launch(TableGen.class);  // Launches table GUI
                            System.exit(0);
                            break;

                        case 0x0b: // prints ASCII character in a1
                            System.out.println((char) reg[11]);
                            break;

                        case 0x11: // ends the program with return code in a1
                            printResultConsole();
                            writeOutputFile();
                            javafx.application.Application.launch(TableGen.class); // Launches table GUI
                            System.exit(reg[11]);
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

        }


    }

    // Reads a binary file containing 32 bit instructions
    // Instructions are read in bytes and then bitshifted into
    // place to account for the little endian placement
    private static int readBinaryFile(String input) throws IOException {

        File data = new File(input);

        int FileLength = (int) data.length()/4; // Length of file in words
        int length=0;


        // Opens a binary file.
        FileInputStream fstream =
                new FileInputStream(input);
        DataInputStream inputFile =
                new DataInputStream(fstream);

        for (int i = 0; i < FileLength; i++){
            for(int j = 0; j <= 3; j++){
                progr[i] += (inputFile.readByte() & 0xFF) << (8*j);
            }
            length++;
        }

        // Closes the file.
        inputFile.close();

        return length;

    }

    // Prints the register values from test and .res file to console
    // Used mostly for debugging
    private static void printResultConsole() throws IOException{

        if (outPutInConsole){       // Checks if output for console is disabled
            System.out.println("\nResult:");
            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }

            File resFile = new File("./" + folderName + "/" + testname + ".res");

            // Prints out register values from .res file
            if (resFile.exists()){
                int FileLength = (int) resFile.length()/4; // Length of file in words
                int resultList[]= new int[FileLength];



                // Opens a binary file.
                FileInputStream fstream =
                        new FileInputStream("./" +folderName + "/" + testname + ".res");
                DataInputStream inputFile =
                        new DataInputStream(fstream);

                for (int i = 0; i < FileLength; i++){
                    for(int j = 0; j <= 3; j++){
                        resultList[i] += (inputFile.readByte() & 0xFF) << (8*j);
                    }
                }

                // Closes the file.
                inputFile.close();
                System.out.println("\nResult from " + testname + ".res :");
                for (int i = 0; i < resultList.length; ++i) {
                    System.out.print(resultList[i] + " ");
                }
            }
        }
    }

    // Creates a binary dump of the registers in specified location
    private static void writeOutputFile () throws IOException{
        FileOutputStream out = new FileOutputStream("./" + folderName + "-results/" + testname + ".res");

        byte[] array = intArrayToByteArray(reg);
        out.write(array);
    }

    // Reverses order of byte array to little endian
    private static byte[] intArrayToByteArray(int[] data) {
        byte[]array = new byte[data.length << 2];

        for (int i=0; i < data.length; i++) {
            int j = i << 2;
            array[j++] = (byte) ((data[i] >>> 0) & 0xff);
            array[j++] = (byte) ((data[i] >>> 8) & 0xff);
            array[j++] = (byte) ((data[i] >>> 16) & 0xff);
            array[j++] = (byte) ((data[i] >>> 24) & 0xff);
        }
        return array;
    }

    // Returns byte from memory with an offset
    private static byte getByte(int regvalue, int imm, int offset){
        int remainder = (regvalue + imm + offset)%4;
        return (byte) (progr[(regvalue + imm + offset -remainder)/4] >> 8*remainder);
    }
}