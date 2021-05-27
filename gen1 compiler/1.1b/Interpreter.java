
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Interpreter {
	
	private ReserveTable myTable; 
	
	public Interpreter(){
		myTable = new ReserveTable(17);
		initReserve(myTable);
	}
	
	//add reserve words to the reserve table
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
	
	//Specifically stated do not hard code in the switch statement for Symbolic constants
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
	
	//Preloading the SymbolTable for the Factorial
	private static void InitSTF(SymbolTable st) {
		st.AddSymbol("n",'v',10);
		st.AddSymbol("i",'v',0);
		st.AddSymbol("product",'v',0);
		st.AddSymbol("1",'c',1);
		st.AddSymbol("$temp",'v',0);	
	}
	
	//Preloading the SymbolTable for the Sum
	private static void InitSTS(SymbolTable st) {
		st.AddSymbol("n",'v',10);
		st.AddSymbol("i",'v',0);
		st.AddSymbol("sum",'i',0);
		st.AddSymbol("1",'c',1);
		st.AddSymbol("$temp",'i',0);
		st.AddSymbol("0",'c',0);
	}
	
	//Preloading the QuadTable for the Factorial
	private static void InitQTF(QuadTable qt) {
		qt.AddQuad(5, 3, 0, 2);
		qt.AddQuad(5, 3, 0, 1);
		qt.AddQuad(3, 1, 0, 4);
		qt.AddQuad(12, 4, 0, 7);
		qt.AddQuad(2, 2, 1, 2);
		qt.AddQuad(4, 1, 3, 1);
		qt.AddQuad(14, 0, 0, 2);
		qt.AddQuad(16, 2, 0, 0);
		qt.AddQuad(0, 0, 0, 0);
	}
	
	//Preloading the QuadTable for the Sum
	private static void InitQTS(QuadTable qt) {
		qt.AddQuad(5, 5, 0, 2);
		qt.AddQuad(5, 3, 0, 1);
		qt.AddQuad(3, 1, 0, 4);
		qt.AddQuad(12, 4, 0, 7);
		qt.AddQuad(4, 2, 1, 2);
		qt.AddQuad(4, 1, 3, 1);
		qt.AddQuad(14, 0, 0, 2);
		qt.AddQuad(16, 2, 0, 0);
		qt.AddQuad(0, 0, 0, 0);
	}
	
	//Initializing both tables have to do with Factorial
	public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
		InitSTF(stable);
		InitQTF(qtable);
		return true;
	}
	
	//Initializing both tables have to do with Summation
	public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable) {
		InitSTS(stable);
		InitQTS(qtable);
		return true;
	}
	
	//Format the print for the trace printout
	private String TraceStringGen(int counter, int opcode,int op1, int op2, int op3) {
		String result = "";
        result = "PC = "+String.format("%04d", counter)+": "+(myTable.LookupCode(opcode)+"     ").substring(0,6)+String.format("%02d",op1)+
                ", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
		return result;
	}
	
	//printer method 
	public static void Print(String filename, String string, boolean nl) {
		try {
			FileOutputStream stream = new FileOutputStream(filename,true);
			OutputStreamWriter writer = new OutputStreamWriter(stream);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			System.out.println(string);
			bufferedWriter.write(string);
			if(nl == true) {
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		}catch(IOException e) {
			 e.printStackTrace();
		}
	}
	
	//Method to ensure that opperation codes are not strings
	private boolean DataCheck(int opcode) {
		return (myTable.LookupCode(opcode) != "");	
	}
	
	//This will from the reserve words print and calc accordingly
	public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename) {
		int counter = 0;
		char dataType;
		
			while(counter <Q.theMaxSize ) { 
				int opcode = Q.GetQuad(counter, 0);
				int op1 = Q.GetQuad(counter, 1);
				int op2 = Q.GetQuad(counter, 2);
				int op3 = Q.GetQuad(counter, 3);
		 		String printString = TraceStringGen(counter,opcode,op1,op2,op3);
				boolean exit;
				
				if(TraceOn) {//Start the trace printing with the printer method
					Print(filename,printString,true);
				}
				
				
				if (DataCheck(opcode)){ //test to see if the case is in a valid form and not a string
					switch (opcode) {  //Checking to see which operation should be performed 
						case STOP:	//Termination Case
									System.out.println("Execution terminated by program STOP.");
									counter = Q.theMaxSize;	
									
									continue;
									
						case DIV:	dataType = S.GetDataType(op3);	
								
									switch (dataType) {
										case 'i':	S.UpdateSymbol(op3, 'i', (S.GetInteger(op1) / S.GetInteger(op2)));
													break;											
										case 'f':	S.UpdateSymbol(op3, 'f', (S.GetFloat(op1) / S.GetFloat(op2)));
													break;										
										case 's':	break;
									}
									break;
						case MUL:	dataType = S.GetDataType(op3);
									switch (dataType) {
										case 'i':	S.UpdateSymbol(op3, 'i', (S.GetInteger(op1) * S.GetInteger(op2)));
													break;									
										case 'f':	S.UpdateSymbol(op3, 'f', (S.GetFloat(op1) * S.GetFloat(op2)));
													break;			
										case 's':	break;
									}
									break;
						case SUB: 	dataType = S.GetDataType(op3);
									switch (dataType) {
										case 'i':	S.UpdateSymbol(op3, 'i', (S.GetInteger(op1) - S.GetInteger(op2)));
													break;										
										case 'f':	S.UpdateSymbol(op3, 'f', (S.GetInteger(op1) - S.GetInteger(op2)));
													break;									
										case 's':	break;
									}	
									break;
						case ADD: 	dataType = S.GetDataType(op3);
									switch (dataType) {
										case 'i':	S.UpdateSymbol(op3, 'i', (S.GetInteger(op1) + S.GetInteger(op2)));
													break;									
										case 'f':	S.UpdateSymbol(op3, 'f', (S.GetFloat(op1) + S.GetFloat(op2)));
													break;										
										case 's':	break;
									}	
									break;
						case MOV:	dataType = S.GetDataType(op3);
									switch(dataType) {
										case 'i':	S.UpdateSymbol(op3, 'i', S.GetInteger(op1));
													break;										
										case 'f':	S.UpdateSymbol(op3, 'f', S.GetFloat(op1));
													break;										
										case 's':	S.UpdateSymbol(op3, 's', S.GetString(op1));
													counter++;
													break;	
									}
									break;
						case STI:	dataType = S.GetDataType(op2);
									switch(dataType) {
										case 'i':	S.UpdateSymbol(op1, 'i', S.GetInteger(op2 + op3));
													break;													
										case 'f':	S.UpdateSymbol(op2+op3, 'f', S.GetFloat(op1));
													break;										
										case 's':	S.UpdateSymbol(op2+op3, 's', S.GetString(op1));
													break;
									}
									break;
						case LDI:	dataType = S.GetDataType(op3);
									
									switch(dataType) {
										case 'i':	S.UpdateSymbol(op3, 'i', S.GetInteger(op1+op2));
													break;												
										case 'f':	S.UpdateSymbol(op3, 'f', S.GetFloat(op1+op2));
													break;												
										case 's':	S.UpdateSymbol(op3, 's', S.GetString(op1+op2));
													break;
									}
									break;
						case BNZ:	dataType= S.GetDataType(op1);
									switch(dataType) {
										case 'i':	if(S.GetInteger(op1) != 0) {
														counter = S.GetInteger(op3);
														
														continue;
													}	
											break;
										case 'f':	if(S.GetFloat(op1)!=0) {
														counter = op3;
														continue;
													}									
										case 's':	break;
									}
									break;
						case BNP:	dataType= S.GetDataType(op1);
									switch(dataType) {
										case 'i':	if(S.GetInteger(op1) <= 0) {
														counter = op3;
														continue;
													}
											break;
										case 'f':	if(S.GetFloat(op1) <= 0) {
														counter = op3;
														continue;
													}										
										case 's':	break;
									}
									break;
						case BNN:	dataType= S.GetDataType(op1);
									switch(dataType) {
										case 'i':	if(S.GetInteger(op1) >= 0) {
														counter=op3;
														continue;
													}
												break;
										case 'f':	if(S.GetFloat(op1) >= 0) {
														counter=op3;
														continue;
													}									
										case 's':	break;
									}	
									break;
						case BZ:	dataType= S.GetDataType(op1);								
									switch(dataType) {
										case 'i':	if(S.GetInteger(op1)== 0) {
														counter = op3;
														continue;
													}
											break;
										case 'f':	if(S.GetFloat(op1) == 0) {
														counter = op3;
														continue;
													}
										case 's':	break;
									}	
									break;
						case BP:	dataType= S.GetDataType(op1);								
									switch(dataType) {
										case 'i':	if(S.GetInteger(op1) > 0) {
														counter = op3;
														continue;
													
													}
											break;
										case 'f':	if(S.GetFloat(op1) > 0) {
														counter = op3;
														continue;
													}	
												break;
										case 's':	break;
									}
									break;
						case BN:	dataType= S.GetDataType(op1);
									switch(dataType) {
										case 'i':	if(S.GetInteger(op1)< 0) {
														counter = op3;
														continue;
													}
												break;
										case 'f':	if(S.GetFloat(op1)< 0) {
														counter = op3;
														continue;
													}
										case 's':	break;
									}	
									break;
						case BR:	counter = op3;
									continue;
						
						case BINDR:	dataType = S.GetDataType(op1);	
									switch(dataType) {
											case 'i':	counter = S.GetInteger(op3);
														continue;
											case 'f':	break;
											case 's':	continue;
										}
									break;
						case PRINT:	dataType = S.GetDataType(op1);
									switch(dataType) {
										case 'i':	System.out.println("product = "+S.GetInteger(op1));
													break;
										case 'f':	System.out.println(S.GetFloat(op3));	
													break;
										case 's':	System.out.println(S.GetString(op3));	
										break;
									}
								break;
					}
				}
				counter++;
			}
	}
}