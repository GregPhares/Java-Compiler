
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Interpreter {
    
    private ReserveTable optable;

    public Interpreter (){
        //init reserve
        optable = new ReserveTable(17);
        initReserve(optable);
        
    }
    
public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable){
         InitSTF(stable);
         InitQTF(qtable);
         return true;
}
 
public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable){
         InitSTS(stable);
         InitQTS(qtable);    
         return true;
}

//factorial   
    public static void InitSTF(SymbolTable st) {
        st.AddSymbol("n",'v',10);
        st.AddSymbol("i",'v',0);
        st.AddSymbol("product",'v',0);
        st.AddSymbol("1",'c',1);
        st.AddSymbol("$temp",'v',0);
    }
//summation    
    public static void InitSTS(SymbolTable st) {
        st.AddSymbol("n",'v',10);
        st.AddSymbol("i",'v',0);
        st.AddSymbol("product",'v',0);
        st.AddSymbol("1",'c',1);
        st.AddSymbol("$temp",'v',0);
        st.AddSymbol("0",'c',0);
    }
    //factorial
    public void InitQTF(QuadTable qt) {
        qt.AddQuad(5,3,0,2); 
        qt.AddQuad(5,3,0,1);
        qt.AddQuad(3,1,0,4);
        qt.AddQuad(12,4,0,7);
        qt.AddQuad(2,2,1,2);
        qt.AddQuad(4,1,3,1);
        qt.AddQuad(14,0,0,2);
        qt.AddQuad(16,2,0,0);

        qt.AddQuad(0, 0, 0, 0);
    }
        //summation
    public void InitQTS(QuadTable qt) {
        qt.AddQuad(5,5,0,2); //Set product at 0 init
        qt.AddQuad(5,3,0,1);
        qt.AddQuad(3,1,0,4);
        qt.AddQuad(12,4,0,7);
        qt.AddQuad(4,2,1,2);
        qt.AddQuad(4,1,3,1); //change to ADD
        qt.AddQuad(14,0,0,2);
        qt.AddQuad(16,2,0,0);

        qt.AddQuad(0, 0, 0, 0);
    }
        

static final int STOP = 0;
static final int DIV = 1;
static final int MUL = 2;
static final int SUB = 3;
static final int ADD = 4;
static final int MOV = 5;
static final int STI = 6;
static final int LDI = 7;
static final int BNZ = 8;
static final int BNP = 9;
static final int BNN = 10;
static final int BZ = 11;
static final int BP = 12;
static final int BN = 13;
static final int BR = 14;
static final int BINDR = 15;
static final int PRINT = 16;

    private boolean Valid(int opcode){
        return (optable.LookupCode(opcode) != "");
    }
    /*
    PC = 0025: ADD 5, 8, 2PC = 0026: BNZ 12
    */
    private String makeTraceString(int pc, int opcode,int op1,int op2,int op3 ){
        String result = "";
        result = "PC = "+String.format("%04d", pc)+": "+(optable.LookupCode(opcode)+"     ").substring(0,6)+String.format("%02d",op1)+
                                ", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
        return result;
    }
    
    public int opcodeFor(String stop) {
        return optable.LookupName(stop);
    }
    
    public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename){
      int calcresult;  
      int PC; //The program counter
      int opcode, op1, op2, op3; //The current quad data
      String outString;
      // file stuff
          //Prints to the named file only the currently used contents of the Quad table in neattabular format, one row per 
   try {
            FileOutputStream outputStream = new FileOutputStream(filename);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
             
      /*****/
      PC = 0; //assumes 0-based indexing on quad table
      while (PC < Q.maxSize()) {
          //GETQUAD(PC, OPCODE, OP1, OP2, OP3);
          opcode = Q.GetQuad(PC,0); //the quad data at index PC
          op1 = Q.GetQuad(PC,1);
          op2 = Q.GetQuad(PC,2);
          op3 = Q.GetQuad(PC,3);
          if (TraceOn) {    //trace is on
              outString = makeTraceString(PC,opcode,op1,op2,op3);
              System.out.println(outString);
              bufferedWriter.write(outString);
              bufferedWriter.newLine();
          }
          if (Valid(opcode)) {//make sure case can handle it
             switch (opcode) {
             
                 case DIV: calcresult = S.GetInteger(op1) / S.GetInteger(op2);
                           S.UpdateSymbol(op3, 'v', calcresult);
                           PC++;
                           break;
            
                 case MUL: calcresult = S.GetInteger(op1) * S.GetInteger(op2);
                           S.UpdateSymbol(op3, 'v', calcresult);
                           PC++;
                           break;
            
                 case ADD: calcresult = S.GetInteger(op1) + S.GetInteger(op2);
                           S.UpdateSymbol(op3, 'v', calcresult);
                           PC++;
                           break;
            
                 case SUB: calcresult = S.GetInteger(op1) - S.GetInteger(op2);
                           S.UpdateSymbol(op3, 'v', calcresult);
                           PC++;
                           break;
            
                 case MOV: // op1 copied to op3
                           S.UpdateSymbol(op3, 'i', S.GetInteger(op1));
                           PC++;
                           break;
            
                 case BNZ: if (S.GetInteger(op1) != 0) {
                               PC = op3; 
                             }
                            else 
                              PC++;
                           break;
                 case BNN: if (S.GetInteger(op1) >= 0) {
                               PC = op3; 
                             }
                            else 
                              PC++;
                           break;
                 case BNP: if (S.GetInteger(op1) <= 0) {
                               PC = op3; 
                             }
                            else 
                              PC++;
                           break;
                           
                 case BZ: if (S.GetInteger(op1) == 0) {
                               PC = op3; 
                             }
                            else 
                              PC++;
                           break;
                 case BP: if (S.GetInteger(op1) > 0) {
                               PC = op3; 
                             }
                            else 
                              PC++;
                           break;
                 case BN: if (S.GetInteger(op1) < 0) {
                               PC = op3; 
                             }
                            else 
                              PC++;
                           break;

                 case BR: //unconditional
                           PC = op3; 
                           break;
                           
                 case BINDR: //unconditional indirect
                           PC = S.GetInteger(op3); 
                           break;

                 case STI: // store indexed- op1 val to op2 +op3 slot
                           S.UpdateSymbol(op2+op3, 'i', S.GetInteger(op1));
                           PC++;
                           break;
                 case LDI: // Load indexed- Assign the value in op1 + offset op2, into op3                           
                           S.UpdateSymbol(op3,'i', S.GetInteger(op1+op2));
                           PC++;
                           break;

                 case PRINT: if (S.GetDataType(op1) == 's') { System.out.println(S.GetSymbol(op1)); }
		                     else{ System.out.println(S.GetInteger(op1)); }
		                     PC++;
		                     break;
                                       
                case STOP: 
                           System.out.println("Execution terminated by program STOP.");
                           PC = Q.maxSize();
                           break;
      } //switch
    } //if valid 
} //while
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }    
      
} //interpret
    

  private void initReserve(ReserveTable optable){
      optable.Add("STOP", 0);
      optable.Add("DIV", 1);
      optable.Add("MUL", 2);
      optable.Add("SUB", 3);
      optable.Add("ADD", 4);
      optable.Add("MOV", 5);
      optable.Add("STI", 6);
      optable.Add("LDI", 7);
      optable.Add("BNZ", 8);
      optable.Add("BNP", 9);
      optable.Add("BNN", 10);
      optable.Add("BZ", 11);
      optable.Add("BP", 12);
      optable.Add("BN", 13);
      optable.Add("BR", 14);
      optable.Add("BINDR", 15);
      optable.Add("PRINT", 16);
 }

}
