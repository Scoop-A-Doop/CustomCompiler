package ADT;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Interpreter {
	ReserveTable optable;
	
	//Constructor initializes/populates reserve table size
	public Interpreter() {
		optable = new ReserveTable(15);
		initReserve(optable);
	}
	
	//Following 3 methods used for initializing factorial
	public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
        InitSTF(stable);
        InitQTF(qtable);
        return true;
    }
	
	//Adds Symbols for factorial
    public void InitSTF(SymbolTable st) {
        st.AddSymbol("n", 'V', 10);
        st.AddSymbol("i", 'V', 0);
        st.AddSymbol("product", 'V', 0);
        st.AddSymbol("1", 'C', 1);
        st.AddSymbol("$temp", 'V', 0);  
    }
    
    //Adds Quads for factorial
    public void InitQTF(QuadTable qt) {
        qt.AddQuad(5, 3, 0, 2); //MOV
        qt.AddQuad(5, 3, 0, 1); //MOV
        qt.AddQuad(3, 1, 0, 4); //SUB
        qt.AddQuad(10, 4, 0, 7); //JP
        qt.AddQuad(2, 2, 1, 2); //MUL
        qt.AddQuad(4, 1, 3, 1); //ADD
        qt.AddQuad(8, 0, 0, 2); //JMP
        qt.AddQuad(6, 0, 0, 2); //PRINT  
        qt.AddQuad(0, 0, 0, 0); //STOP
    }
	
    //Following 3 methods are used to initialize summation
    public boolean initializeSummationTest(SymbolTable st, QuadTable qt) {
		InitSTS(st);
        InitQTS(qt);
        return true;
	}
	
	//Adds Symbols for summation
    public void InitSTS(SymbolTable st) {
    	st.AddSymbol("n", 'V', 10);
        st.AddSymbol("i", 'V', 0);
        st.AddSymbol("sum", 'V', 0);
        st.AddSymbol("1", 'C', 1);
        st.AddSymbol("$temp", 'V', 0);  
        st.AddSymbol("?", 'V', 0);  
    }
    
    //Adds Quads for summation
    public void InitQTS(QuadTable qt) {
    	qt.AddQuad(5, 5, 0, 2); //MOV
        qt.AddQuad(5, 3, 0, 1); //MOV
        qt.AddQuad(3, 1, 0, 4); //SUB
        qt.AddQuad(10, 4, 0, 7); //JP
        qt.AddQuad(4, 2, 1, 2); //ADD
        qt.AddQuad(4, 1, 3, 1); //ADD
        qt.AddQuad(8, 0, 0, 2); //JMP
        qt.AddQuad(6, 0, 0, 2); //PRINT  
        qt.AddQuad(0, 0, 0, 0); //STOP
    }
    
    /* Quads are interpreted constantly until a STOP command is executed.
     * Each quad is retrieved using the GetQuad function at the start of the loop.
     * The quad index is kept track via programCounter.
     * Using a switch statement, each case handles the respective quad table operator.
     * Depending on whether TraceOn is true or false, the "history" of each quad execution is
     * recorded in the system console and in a file. 
     */
	public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String FileName) {
		boolean keepGoing = true;
		Scanner myScanner = new Scanner(System.in);
		int programCounter = 0;
		
		while(keepGoing) {
			int[] quad = Q.GetQuad(programCounter);
			
			if(TraceOn) {
				System.out.println(makeTraceString(programCounter, quad[0], quad[1], quad[2], quad[3]));
				try {
					FileWriter myWriter = new FileWriter(FileName, true);
					myWriter.write(makeTraceString(programCounter, quad[0], quad[1], quad[2], quad[3])+"\n");
					myWriter.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			switch(quad[0]) {
				case 0:		//Stop
					keepGoing = false;
					System.out.println("Execution terminated by program STOP");
					break;
				case 1:		//Divide
					//If Int or Float, divide data value respectively
					if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("I")) {
						int division = S.GetInteger(quad[1]) / S.GetInteger(quad[2]);
						S.UpdateSymbol(quad[3], S.GetUsage(quad[3]), division);
					}
					else if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("F")) {
						double division = S.GetFloat(quad[1]) / S.GetFloat(quad[2]);
						S.UpdateSymbol(quad[3], S.GetUsage(quad[3]), division);
					}
					programCounter++;
					break;
				case 2:		//Multiply
					//If Int or Float, multiply data value respectively
					if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("I")) {
						int multiply = S.GetInteger(quad[1]) * S.GetInteger(quad[2]);
						S.UpdateSymbol(quad[3], S.GetUsage(quad[3]), multiply);
					}
					else if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("F")) {
						double multiply = S.GetFloat(quad[1]) * S.GetFloat(quad[2]);
						S.UpdateSymbol(quad[3], S.GetUsage(quad[3]), multiply);
					}
					programCounter++;
					break;
				case 3:		//Subtract
					//If Int or Float, subtract data value respectively
					if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("I")) {
						int subtract = S.GetInteger(quad[1]) - S.GetInteger(quad[2]);
						S.UpdateSymbol(quad[3], S.GetUsage(quad[3]), subtract);
					}
					else if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("F")) {
						double subtract = S.GetFloat(quad[1]) - S.GetFloat(quad[2]);
						S.UpdateSymbol(quad[3], S.GetUsage(quad[3]), subtract);
					}
					programCounter++;
					break;
				case 4:		//Add
					//If Int or Float, add data value respectively
					if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("I")) {
						int add = S.GetInteger(quad[1]) + S.GetInteger(quad[2]);
						S.UpdateSymbol(quad[3], S.GetUsage(quad[3]), add);
					}
					else if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("F")) {
						double add = S.GetFloat(quad[1]) + S.GetFloat(quad[2]);
						S.UpdateSymbol(quad[3], S.GetUsage(quad[3]), add);
					}
					programCounter++;
					break;	
				case 5:		//Move
					//If Int, String or Float, print data value respectively
					if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("I")) {
						S.UpdateSymbol(quad[3], S.GetUsage(quad[1]), S.GetInteger(quad[1]));
					}
					else if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("S")) {
						S.UpdateSymbol(quad[3], S.GetUsage(quad[1]), S.GetString(quad[1]));
					}
					else if(Character.toString(S.GetDataType(quad[1])).equalsIgnoreCase("F")) {
						S.UpdateSymbol(quad[3], S.GetUsage(quad[1]), S.GetFloat(quad[1]));
					}
					programCounter++;
					break;
				case 6:		//Print
					//If Int, String or Float, print data value respectively
					
					if(Character.toString(S.GetDataType(quad[3])).equalsIgnoreCase("I")) {
						System.out.println(S.GetSymbol(quad[3])+" = "+S.GetInteger(quad[3]));
					}
					else if(Character.toString(S.GetDataType(quad[3])).equalsIgnoreCase("S")) {
						System.out.println(S.GetString(quad[3]));
					}
					else if(Character.toString(S.GetDataType(quad[3])).equalsIgnoreCase("F")) {
						System.out.println(S.GetSymbol(quad[3])+" = "+S.GetFloat(quad[3]));
					}
					
					//System.out.println(S.GetSymbol(quad[3]));
					programCounter++;
					break;
				case 7:		//Read
					Scanner sc = new Scanner(System.in);
					System.out.print('>');
					int readval = sc.nextInt();
					S.UpdateSymbol(quad[3],'i',readval);
					sc = null;
					programCounter++;
					break;
				case 8:		//JMP
					programCounter = quad[3];
					break;
				case 9:		//JZ
					if(S.GetInteger(quad[1]) == 0) {
						programCounter = quad[3];
					}
					else {
						programCounter++;
					}
					break;
				case 10:	//JP
					if(S.GetInteger(quad[1]) > 0) {
						programCounter = quad[3];
					}
					else {
						programCounter++;
					}
					break;
				case 11:	//JN
					if(S.GetInteger(quad[1]) < 0) {
						programCounter = quad[3];
					}
					else {
						programCounter++;
					}
					break;
				case 12:	//JNZ
					if(S.GetInteger(quad[1]) != 0) {
						programCounter = quad[3];
					}
					else {
						programCounter++;
					}
					break;
				case 13:	//JNP
					if(S.GetInteger(quad[1]) <= 0) {
						programCounter = quad[3];
					}
					else {
						programCounter++;
					}
					break;
				case 14:	//JNN
					if(S.GetInteger(quad[1]) >= 0) {
						programCounter = quad[3];
					}
					else {
						programCounter++;
					}
					break;
				case 15:	//JINDR
					programCounter = S.GetInteger(quad[3]);
					break;
			}
		}
		System.out.println("---");
		myScanner.close();
	}
    
    //Formats each quad line so that all the trace outputs match the intended result 
  	private String makeTraceString(int pc, int opcode, int op1, int op2, int op3){
  		String result = "";
  		result = "PC = "+String.format("%04d", pc)+": "+(optable.LookupCode(opcode)
  				+"     ").substring(0,6)+String.format("%02d",op1)
  				+", "+String.format("%02d",op2)
  				+", "+String.format("%02d",op3);
  	        return result;
  	}
  	
  	//Adds each operand to the reserve table
  	private void initReserve(ReserveTable optable){
        optable.Add("STOP", 0);
        optable.Add("DIV", 1);
        optable.Add("MUL", 2);
        optable.Add("SUB", 3);
        optable.Add("ADD", 4);
        optable.Add("MOV", 5);
        optable.Add("PRINT", 6);
        optable.Add("READ", 7);
        optable.Add("JMP", 8);
        optable.Add("JZ", 9);
        optable.Add("JP", 10);
        optable.Add("JN", 11);
        optable.Add("JNZ", 12);
        optable.Add("JNP", 13);
        optable.Add("JNN", 14);
        optable.Add("JINDR", 15);
   }
    
}
