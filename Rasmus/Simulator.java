
import java.io.*;


public class Simulator {


    static int pc;
    static int reg[] = new int[31];
    static String fileName = "branchcnt.bin";



    public static void main(String[] args) throws IOException {

        pc = 0;
        int instr,opcode,rd,rs1, rs2, funct3, funct7, imm1, imm2, imm,shamt,Bimm;


        int[] progr = readBinaryFile(fileName);
        /*
        for (int k = 0; k<progr.length; k++){
            System.out.println(String.format("0x%08X", progr[k]));
        }

        instr = progr[3];
        imm1 = (instr >>> 7) & 0x1f;
        imm2 = (instr >>> 25) ;
        Bimm = ((imm2 << 5) & 0x800) | ((imm1 << 11) & 0x400) | ((imm2 << 5) & 0x3E0) | (imm1 & 0x1E);
        Bimm = (((((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0xFFFFF7FE) | ((   (((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0x00000001) << 11))/4;
        System.out.println("imm1 = " + Integer.toBinaryString(imm1) + " imm2 = "+ Integer.toBinaryString(imm2));
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
            //Bimm = ((imm2 << 5) & 0x800) + ((imm1 << 11) & 0x400) + ((imm2 << 5) & 0x3E0) + (imm1 & 0x1E);
            Bimm = (((((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0xFFFFF7FE) | (((((instr >> 20) & 0xFFFFFFE0) | ((instr >>> 7) & 0x0000001F)) & 0x00000001) << 11))/4;

            switch (opcode) {

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
                            System.out.println("funct3" + funct3 + " has not been implemented yet");
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
                case 0x63: //Opcode 1100011

                    switch (funct3) {
                        case 0x00: // BEQ - Branch Equal
                            if (reg[rs1] == reg[rs2]){
                                pc = pc + Bimm - 1;
                            }
                            break;
                        case 0x01: // BNE - Branch Not Equal
                            if (reg[rs1] != reg[rs2]){
                                pc = pc + Bimm - 1;
                            }
                            break;
                        case 0x04: // BLT - Branch Less Than
                            if (reg[rs1] < reg[rs2]){
                                pc = pc + Bimm - 1;
                            }
                            break;
                        case 0x05: // BGE - Branch Greater Than or Equal
                            if (reg[rs1] >= reg[rs2]){
                                pc = pc + Bimm - 1;
                            }
                            break;
                        case 0x06: // BLTU - Branch Less Than Unsigned
                            if (reg[rs1] < reg[rs2]){
                                pc = pc + Bimm - 1;
                            }
                            break;
                        case 0x07: // BGEU - Branch >= Unsigned
                            if (reg[rs1] >= reg[rs2]){
                                pc = pc + Bimm - 1;
                            }
                            break;
                    }
                    break;
                case 0x37: // lui

                    rd = (instr >> 7) & 0x01f;
                    rs1 = (instr >> 15) & 0x01f;
                    imm = (instr >> 20);
                    reg[rd] = reg[rs1] + (imm << 20);
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

            ++pc; // We count in 4 byte words
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
