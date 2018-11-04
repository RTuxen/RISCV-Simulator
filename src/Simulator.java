
import java.io.*;


public class Simulator {


    static int pc;
    static int reg[] = new int[31];
    static String fileName = "addpos.bin";



    public static void main(String[] args) throws IOException {

        pc = 0;
        int instr,opcode,rd,rs1,imm;
        int funct7,funct3,rs2;

        int[] progr = readBinaryFile(fileName);


        for (;;) {

            instr = progr[pc];
            opcode = instr & 0x7f;

            switch (opcode) {

                case 0x13: // addi
                    rd = (instr >> 7) & 0x01f;
                    rs1 = (instr >> 15) & 0x01f;
                    imm = (instr >> 20);
                    reg[rd] = reg[rs1] + imm;
                    break;
                case 0x33: // add
                    rd = (instr >> 7) & 0x01f;
                    rs1 = (instr >> 15) & 0x01f;
                    rs2 = (instr >> 20) & 0x01f;
                    reg[rd] = reg[rs1] + reg[rs2];
                    break;
                case 0x73: // ecall
                    rd = (instr >> 7) & 0x01f;
                    funct3 = (instr >> 12) & 0x3;
                    rs1 = (instr >> 15) & 0x01f;
                    imm = (instr >> 20);
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
