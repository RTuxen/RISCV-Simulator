
import java.io.*;


public class Simulator {


    static int pc;
    static int reg[] = new int[31];
    static String fileName = "branchmany.bin";



    public static void main(String[] args) throws IOException {

        pc = 0;
        int instr,opcode,rd,rs1, rs2, funct3, funct7, imm1, imm2, imm,shamt,imm_B,imm_J,imm_U,imm_S;


        int[] progr = readBinaryFile(fileName);

        // Prints list of all instructions
        System.out.println("List of Instructions");
        for (int k = 0; k < progr.length; k++){
            System.out.println(String.format("0x%08X", progr[k]));
        }

        /*
        instr = progr[3];
        imm1 = (instr >> 7) & 0x1f;
        imm2 = (instr >> 25) ;
        Bimm = ((imm2 << 5) & 0x800) | ((imm1 << 11) & 0x400) | ((imm2 << 5) & 0x3E0) | (imm1 & 0x1E);
        Bimm = ((imm2 << 5) & 0x800) + ((imm1 << 10) & 0x400) | ((imm2 << 5) & 0x3E0) | (imm1 & 0x1E);
        //Bimm = (((((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0xFFFFF7FE) | ((   (((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0x00000001) << 11))/4;
        System.out.println("imm2 = "+ Integer.toBinaryString(imm2) + " imm1 = " + Integer.toBinaryString(imm1));
        System.out.println(imm1+" "+imm2);
        System.out.println(Bimm);
        System.out.println(Integer.toBinaryString(Bimm));
        */



        for (;;) {

            instr = progr[pc];
            opcode = instr & 0x7f;
            rd = (instr >> 7) & 0x1f;
            funct3 = (instr >> 12) & 0x07;
            rs1 = (instr >> 15) & 0x1f;
            rs2 = (instr >> 20) & 0x1f;
            imm = (instr >> 20);
            imm1 = (instr >> 7) & 0x1f;
            imm2 = (instr >> 25);
            funct7 = (instr >> 25);
            shamt = (instr >> 20) & 0x01f;
            //Bimm = ((imm2 << 5) & 0x800) + ((imm1 << 10) & 0x400) | ((imm2 << 5) & 0x3E0) | (imm1 & 0x1E);
            imm_B = (((((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0xFFFFF7FE) | (((((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0x00000001) << 11))/4;
            imm_J = (((instr >> 20) & 0xFFF007FE) | ((instr >>> 9) & 0x00000800) | (instr & 0x000FF000));
            imm_S = (((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F));
            imm_U = (instr >> 20) << 20;

            ++pc; // We count in 4 byte words

            switch (opcode) {
                case 0x03: // Opcode 0000011
                    switch (funct3){
                        case 0x00: // LB - Load Byte
                            System.out.println("LB - Load Byte has not been implemented yet");
                            break;
                        case 0x01: // LH - Load Halfword
                            System.out.println("LH - Load Halfword has not been implemented yet");
                            break;
                        case 0x02: // LW - Load Word
                            System.out.println("LW - Load Word has not been implemented yet");
                            break;
                        case 0x04: // LBU - Load Byte Unsigned
                            System.out.println("LBU - Load Byte Unsigned has not been implemented yet");
                            break;
                        case 0x05: // LHU - Load Halfword Unsigned
                            System.out.println("Load Halfword Unsigned has not been implemented yet");
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
                            System.out.println("SB - Store Byte has not been implemented yet");
                            break;
                        case 0x01: // SH - Store Halfword
                            System.out.println("SH - Store Halfword has not been implemented yet");
                            break;
                        case 0x02: // SW - Store Word
                            System.out.println("SW - Store Word has not been implemented yet");
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
                    pc = imm_J;
                    break;

                case 0x73: // ecall
                    if (reg[10] == 10){
                        System.out.println("DONE");
                        System.exit(0);
                    }
                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }


            if (pc >= progr.length) {
                break;
            }
            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            System.out.println();
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
