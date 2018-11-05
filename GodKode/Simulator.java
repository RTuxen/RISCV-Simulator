import java.io.*;


public class Simulator {


    static int pc;
    static int reg[] = new int[31];
    static String fileName = "addpos.bin";



    public static void main(String[] args) throws IOException {

        pc = 0;
        int instr,opcode,rd,rs1, rs2, funct3, funct7, imm1, imm2, imm;

        int[] progr = readBinaryFile(fileName);


        for (;;) {

            instr = progr[pc];
            opcode = instr & 0x7f;
            rd = (instr >> 7) & 0x1f;
            imm1 = (instr >> 7) & 0x01f;
            funct3 = (instr >> 12) & 0x07;
            rs1 = (instr >> 15) & 0x1f;
            rs2 = (instr >> 20) & 0x1f;
            imm = (instr >> 20);
            imm2 = (instr >> 25);
            funct7 = (instr >> 25);

            switch (opcode) {

                case 0x13: // Opcode 0010011
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
                                    reg[rd] = reg[rs1] >> reg[rs2];
                                    break;
                                case 0x20://SRA - Shift Right Arithmetic
                                    reg[rd] = reg[rs1] >> reg[rs2];
                                    break;
                            }
                            break;
                        case 0x06: //OR
                            reg[rd] = reg[rs1] | reg[rs2];
                            break;
                        case 0x07://AND
                            reg[rd] = reg[rs1] & reg[rs2];
                            break;
                    }
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
                    if (number[i] < 0){
                        number[i] = number[i] & 0xFF;
                    }
                }
                System.out.println("\n" + number[0] + " " + number[1] + " "+ number[2] + " " + number[3] + " ");
                instruction[counter] = number[0] + (number[1] << 8) + (number[2] << 16) + (number[3] << 24);
                counter++;

            } catch (Exception e) {
                endOfFile = true;
            }

        }
        // Closes the file.
        inputFile.close();
        System.out.println("   ***Done with reading from a binary file.");

        int[] instructionList = new int[counter]; // Loads instructions into array of correct size
        for (int j = 0; j<counter; j++){
            instructionList[j] = instruction[j];
        }

        return instructionList;

    }

}