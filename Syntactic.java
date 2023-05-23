/* Name: Suleyman Shouib
 * Course: CS 4100
 * Term: Spring 2023
 */
package ADT;

import java.util.ArrayList;

/**
*
* @author abrouill
*/
public class Syntactic {
	private String filein; //The full file path to input file
	private SymbolTable symbolList; //Symbol table storing ident/const
	private QuadTable quads;
	private Interpreter interp;
	private Lexical lex; //Lexical analyzer
	private Lexical.token token; //Next Token retrieved
	private boolean traceon; //Controls tracing mode
	private int level = 0; //Controls indent for trace mode
	private boolean anyErrors; //Set TRUE if an error happens
	private boolean globalErrors = false;
	
	private final int symbolSize = 250;
	private final int quadSize = 1000;
	private int Minus1Index;
	private int Plus1Index;
	private ArrayList<String> declaredIdentifiers = new ArrayList<String>();	//Keeps track of all identifiers that were declared
	private int factorIndex = 0;	//Keeps track of the number of the custom variables
	
	/* CFG PART B
	 * <program>			---> $UNIT <identifier> $SEMIColon <block> $PERIOD
	 * <block> 				---> {<variable-dec-sec>}* <block-body>
	 * <block-body> 		---> $BEGIN <statement> {$SCOLN <statement>} $END
	 * <variable-dec-sec> 	---> $VAR {<identifier> {$COMMA <identifier>}* $COLON <simple type> $SEMICOLON}+
	 * <statement> 			---> [<handleAssignment> | <block-body> | <handleIf> | <handleDoWhile> | <handleRepeat> | <handleFor> | <handleWriteLn> | <handleReadLn>]+
	 * <handleAssignment> 	---> <variable> $COLON-EQUALS <simple expression>
	 * <handleIf> 			---> $IF <relexpression> $THEN <statement> [$ELSE <statement>]
	 * <handleDoWhile> 		---> $DOWHILE <relexpression> <statement>
	 * <handleRepeat> 		---> $REPEAT <statement> $UNTIL <relexpression>
	 * <handleFor> 			---> $FOR <variable> $ASSIGN <simple expression> $TO <simple expression $DO <statement>
	 * <handleWriteLn> 		---> $WRITELN $LPAR (<identfier> | <stringconst>) $RPAR
	 * <handleReadLn> 		---> $READLN $LPAR <identifier> $RPAR
	 * <variable> 			---> <identifier>
	 * <relexpression> 		---> <simple expression> <relop> <simple expression>
	 * <relop> 				---> $EQ | $ LSS | $GTR | $NEQ | $LEQ | $GEQ 
	 * <simple expression> 	---> [<sign>] <term> { $PLUS | $MINUS <term>}*
	 * <sign> 				---> $PLUS | $MINUS
	 * <term> 				---> <factor> { $MULT | $DIVIDE <factor> }*
	 * <factor>  			---> <unsigned constant> | <variable> | $LPAR <simple expression> $RPAR
	 * <simple type> 		---> $INTEGER | $FLOAT | $STRING
	 * <constant> 			---> [<sign>] <unsigned constant>
	 * <unsigned constant> 	---> <unsigned number>
	 * <unsigned number> 	---> $FLOATTYPE | $INTTYPE
	 * <identifier> 		---> $IDENTIFIER
	 * <stringconst> 		---> $STRINGTYPE
	 */
	
	public Syntactic(String filename, boolean traceOn) {
		filein = filename;
		traceon = traceOn;
		symbolList = new SymbolTable(symbolSize);
		// Add these to symbol table to accommodate sign flips
		//Minus1Index = symbolList.AddSymbol("-1", symbolList.constantkind, -1);
		//Plus1Index = symbolList.AddSymbol("1", symbolList.constantkind, 1);
		Minus1Index = symbolList.AddSymbol("-1", 'c', -1);
		Plus1Index = symbolList.AddSymbol("1", 'c', 1);
		quads = new QuadTable(quadSize);
		interp = new Interpreter();
		lex = new Lexical(filein, symbolList, true);
		lex.setPrintToken(traceOn);
		anyErrors = false;
	}
	
	//The interface to the syntax analyzer, initiates parsing
	// Uses variable RECUR to get return values throughout the non-terminal methods
	//Interface to the syntax analyzer, initiates parsing
	public void parse() {
		//Use source filename as pattern for symbol table and quad table output later
		String filenameBase = filein.substring(0, filein.length() - 4);
		System.out.println(filenameBase);
		int recur = 0;
		
		//Prime the pump, get first token
		token = lex.GetNextToken();
		
		//Call PROGRAM
		recur = Program();
		
		//Done with recursion, so add the final STOP quad
		//quads.AddQuad(interp.opcodeFor("STOP"), 0, 0, 0);
		quads.AddQuad(interp.optable.LookupName("STOP"), 0, 0, 0);
		
		//Print SymbolTable, QuadTable before execute
		symbolList.PrintSymbolTable(filenameBase + "ST-before.txt");
		quads.PrintQuadTable(filenameBase + "QUADS.txt");
		
		//interpret
		if (!anyErrors) {
			interp.InterpretQuads(quads, symbolList, true, filenameBase + "TRACE.txt");
		} 
		else {
			System.out.println("Errors, unable to run program.");
		}
		
		symbolList.PrintSymbolTable(filenameBase + "ST-after.txt");
	}
	
	//Non Terminal PROGRAM is fully implemented here.
	// $UNIT <identifier> $SEMIColon <block> $PERIOD
	private int Program() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		
		trace("Program", true);
		
		if (token.code == lex.codeFor("UNIT_")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			recur = ProgIdentifier();
			
			if (token.code == lex.codeFor("SEMIC")) {
				token = lex.GetNextToken();
				checkDeclaredToken(token);
				recur = Block();
				
				if (token.code == lex.codeFor("PERID")) {
					if (!globalErrors) {
						System.out.println("Success.");
					} 
					else {
						System.out.println("Compilation failed.");
					}
				} 
				else {
					error(lex.reserveFor("PERID"), token.lexeme);
				}
			} 
			else {
				error(lex.reserveFor("SEMIC"), token.lexeme);
			}
		} 
		else {
			error(lex.reserveFor("UNIT_"), token.lexeme);
		}
		trace("Program", false);
		return recur;
	}
	
	// Non Terminal BLOCK is fully implemented here.
	// {<variable-dec-sec>}* <block-body>
	private int Block() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		trace("Block", true);
		
		while(token.code == lex.codeFor("VAR__")) {
			recur = VariableDecSec();
		}
		
		recur = BlockBody();
		
		trace("Block", false);
		return recur;
	}
	
	// Non Terminal BLOCKBODY is fully implemented here.
	// $BEGIN <statement> {$SCOLN <statement>} $END
	private int BlockBody() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		trace("BlockBody", true);

		if (token.code == lex.codeFor("BEGIN")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			recur = Statement();

			while ((token.code == lex.codeFor("SEMIC")) && (!lex.EOF()) && (!anyErrors)) {
				token = lex.GetNextToken();
				checkDeclaredToken(token);
				recur = Statement();
			}

			if(anyErrors) {
				globalErrors = true;
				// Re-synch. First looks for the end of the line denoted by a semicolon
				
				while(token.code != lex.codeFor("SEMIC")) {
					token = lex.GetNextToken();
					checkDeclaredToken(token);
				}
				
				// Looks for the next Statement Start token
				while(!(token.code == lex.codeFor("IF___") || token.code == lex.codeFor("DOWHI") || token.code == lex.codeFor("REPET") || 
						token.code == lex.codeFor("FOR__") || token.code == lex.codeFor("REPET") || token.code == lex.codeFor("WRTLN") || 
						token.code == lex.codeFor("REDLN") || token.code == lex.codeFor("BEGIN")  || token.code == lex.codeFor("IDTOK"))) {
					
					token = lex.GetNextToken();
					checkDeclaredToken(token);
				}
				
				anyErrors = false;	// Set anyErrors to true so that statement level parsing can occur

				// Begin statement level parsing. The rest of the program will be parsed now
				recur = Statement();
				
				while ((token.code == lex.codeFor("SEMIC")) && (!lex.EOF()) && (!anyErrors)) {
					token = lex.GetNextToken();
					checkDeclaredToken(token);
					recur = Statement();
					anyErrors = false;	//anyErrors is set to false so that the loop can run continuously until the end of the code
				}
				
				
			}
			
			if (token.code == lex.codeFor("END__")) {
				token = lex.GetNextToken();
				checkDeclaredToken(token);
			} 
			else {
				error(lex.reserveFor("END__"), token.lexeme);
			}
		} 
		else {
			error(lex.reserveFor("BEGIN"), token.lexeme);
		}
		
		trace("BlockBody", false);
		return recur;
	}
	
	// Non Terminal VARIABLEDECSEC is fully implemented here.
	// $VAR {<identifier> {$COMMA <identifier>}* $COLON <simple type> $SEMICOLON}+
	private int VariableDecSec() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		
		trace("VariableDecSec", true);
		token = lex.GetNextToken();
		
		do {
			if(token.code == lex.codeFor("IDTOK")) {
				declaredIdentifiers.add(token.lexeme);
				token = lex.GetNextToken();
				
				while(token.code == lex.codeFor("COMMA")) {
					token = lex.GetNextToken();
					
					if(token.code == lex.codeFor("IDTOK")) {
						declaredIdentifiers.add(token.lexeme);
						token = lex.GetNextToken();
						
					}
					else {
						error(lex.reserveFor("IDTOK"), token.lexeme);
					}
					
					recur = ProgIdentifier();
				}
				
				if(token.code == lex.codeFor("COLON")) {
					token = lex.GetNextToken();
					
					recur = SimpleType();
					
					if(token.code == lex.codeFor("SEMIC")) {
						token = lex.GetNextToken();
						
					}
					else {
						error(lex.reserveFor("SEMIC"), token.lexeme);
					}
				}
				else {
					error(lex.reserveFor("COLON"), token.lexeme);
				}
			}
			
			else {
				error(lex.reserveFor("IDTOK"), token.lexeme);
			}
			
			
		}while(token.code == lex.codeFor("IDTOK"));
		
		trace("VariableDecSec", false);
		return recur;
	}
	
	// Non Terminal statement is fully implemented here.
	// [<handleAssignment> | <block-body> | <handleIf> | <handleDoWhile> | <handleRepeat> | <handleFor> | <handleWriteLn> | <handleReadLn>]+
	private int Statement() {
		int recur = 0;

		if (anyErrors) {
			return -1;
		}
			
		trace("Statement", true);
		if (token.code == lex.codeFor("IDTOK")) { //must be an ASSIGNMENT
			recur = handleAssignment();
		} 
		else {
			// IF
			if (token.code == lex.codeFor("IF___")) { 
				recur = handleIf();
			} 
			
			// DO WHILE
			else if(token.code == lex.codeFor("DOWHI")) {
				recur = handleDoWhile();
			}
				
			// REPEAT
			else if(token.code == lex.codeFor("REPET")) {
				recur = handleRepeat();
			}
				
			// FOR
			else if(token.code == lex.codeFor("FOR__")) {
				recur = handleFor();
			}
				
			// WRITE LINE
			else if(token.code == lex.codeFor("WRTLN")) {
				recur = handleWriteLn();
			}
			
			// READ LINE
			else if(token.code == lex.codeFor("REDLN")) {
				recur = handleReadLn();
			}
			
			// BLOCK BODY BEGIN
			else if(token.code == lex.codeFor("BEGIN")) {
				recur = BlockBody();
			}
			
			// ERROR
			else {
				error("Statement start", token.lexeme);
			}
		}
		
		trace("Statement", false);
		return recur;
	}
	
	// Non Terminal HANDLEIF is fully implemented here.
	// $IF <relexpression> $THEN <statement> [$ELSE <statement>]
	private int handleIf() {
		int branchQuad, patchElse;
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
			
		trace("handleIf", true);
		
		token = lex.GetNextToken();
		checkDeclaredToken(token);
		branchQuad = Relexpression();	// Tells where branch target to be set to jump around TRUE part
		
		if(token.code == lex.codeFor("THEN_")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			recur = Statement();
			
			if(token.code == lex.codeFor("ELSE_")) {
				token = lex.GetNextToken();
				checkDeclaredToken(token);
				
				patchElse = quads.NextQuad();	// Save backfill quad to jump around else body
				
				quads.AddQuad(interp.optable.LookupName("JMP"), 0, 0, 0);	
				
				quads.UpdateJump(branchQuad, quads.NextQuad());	// Conditional jump
				recur = Statement();
				
				quads.UpdateJump(patchElse, quads.NextQuad());	
			}
			else {
				quads.UpdateJump(branchQuad, quads.NextQuad());	// No ELSE found, fix IF branch
			}
		}
		else {
			error(lex.reserveFor("THEN_"), token.lexeme);
		}
			
		
		trace("handleIf", false);
		
		return branchQuad;
	}
	
	// Non Terminal HANDLEDOWHILE is fully implemented here.
	// $DOWHILE <relexpression> <statement>
	private int handleDoWhile() {
		int recur = 0;	
		int branchQuad;	
		int saveTop = 0;
		
		if (anyErrors) {
			return -1;
		}
			
		trace("handleDoWhile", true);
		
		token = lex.GetNextToken();
		checkDeclaredToken(token);
		
		saveTop = quads.NextQuad();	// Save the top of the loop
		
		branchQuad = Relexpression();	
		
		recur = Statement();
		
		// Jump to the top of the loop
		quads.AddQuad(interp.optable.LookupName("JMP"), 0, 0, saveTop);	

		// Conditional jumps the next quad
		quads.UpdateJump(branchQuad, quads.NextQuad());
		
		trace("handleDoWhile", false);
		
		return recur;
	}
	
	// Non Terminal HANDLEREPEAT is fully implemented here.
	// $REPEAT <statement> $UNTIL <relexpression>
	private int handleRepeat() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
			
		trace("handleRepeat", true);
		token = lex.GetNextToken();
		checkDeclaredToken(token);
		recur = Statement();
			
		if(token.code == lex.codeFor("UNTIL")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			recur = Relexpression();
		}
		else {
			error(lex.reserveFor("UNTIL_"), token.lexeme);
		}
		trace("handleRepeat", false);
		return recur;
	}
	
	// Non Terminal HANDLEFOR is fully implemented here.
	// $FOR <variable> $ASSIGN <simple expression> $TO <simple expression $DO <statement>
	private int handleFor() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
			
		trace("handleFor", true);
		token = lex.GetNextToken();
		checkDeclaredToken(token);
		recur = Variable();
		
		if(token.code == lex.codeFor("ASIGN")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			recur = SimpleExpression();
			
			if(token.code == lex.codeFor("TO___")) {
				token = lex.GetNextToken();
				checkDeclaredToken(token);
				recur = SimpleExpression();
					
				if(token.code == lex.codeFor("DO___")) {
					token = lex.GetNextToken();
					checkDeclaredToken(token);
					recur = Statement();
				}
				else {
					error(lex.reserveFor("DO___"), token.lexeme);
				}
			}
			else {
				error(lex.reserveFor("TO___"), token.lexeme);
			}
		}
		else {
			error(lex.reserveFor("ASIGN"), token.lexeme);
		}
		trace("handleFor", false);
		return recur;
	}
	
	// Non Terminal HANDLEWRITELN is fully implemented here.
	// $WRITELN $LPAR (<identfier> | <stringconst>) $RPAR
	private int handleWriteLn() {
		int recur = 0;
		int toprint = 0;
		
		if (anyErrors) {
			return -1;
		}
			
		trace("handleWriteLn", true);
		token = lex.GetNextToken();
		checkDeclaredToken(token);
		
		//look for ( stringconst, ident, or simpleexp )
		if(token.code == lex.codeFor("LPARE")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			
			if ((token.code == lex.codeFor("STRIN")) || (token.code == lex.codeFor("IDTOK"))){
				toprint = symbolList.LookupSymbol(token.lexeme);
				token = lex.GetNextToken();
				checkDeclaredToken(token);
			}
			else if(token.code == lex.codeFor("STTOK")) {
				toprint = Stringconst();
			}
			else{
				toprint = SimpleExpression();
			}

			quads.AddQuad(interp.optable.LookupName("PRINT"), 0, 0, toprint);
			
			if(token.code == lex.codeFor("RPARE")) {
				token = lex.GetNextToken();
				checkDeclaredToken(token);
			}
			else {
				error(lex.reserveFor("RPARE"), token.lexeme);
			}
		}
		else {
			error(lex.reserveFor("LPARE"), token.lexeme);
		}
		trace("handleWriteLn", false);
		return recur;
	}

	// Non Terminal HANDLEREADLN is fully implemented here.
	// $READLN $LPAR <identifier> $RPAR
	private int handleReadLn() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
			
		trace("handleReadLn", true);
		token = lex.GetNextToken();
		checkDeclaredToken(token);
		
		if(token.code == lex.codeFor("LPARE")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			recur = Variable();

			quads.AddQuad(interp.optable.LookupName("READ"), 0, 0, recur);
			
			if(token.code == lex.codeFor("RPARE")) {
				token = lex.GetNextToken();
				checkDeclaredToken(token);
			}
			else {
				error(lex.reserveFor("RPARE"), token.lexeme);
			}
			
		}
		else {
			error(lex.reserveFor("LPARE"), token.lexeme);
		}
		trace("handleReadLn", false);
		return recur;
	}
	
	// Non-terminal VARIABLE just looks for an IDENTIFIER. Later, a
	// type-check can verify compatible math ops, or if casting is required.
	// <identifier>
	private int Variable() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("Variable", true);
		
		if ((token.code == lex.codeFor("IDTOK"))) {
			//return the location of this variable for Quad use
			recur = symbolList.LookupSymbol(token.lexeme);
			// bookkeeping and move on
			token = lex.GetNextToken();
			checkDeclaredToken(token);
		} 
		else {
			error("Variable", token.lexeme);
		}
		
		trace("Variable", false);
		return recur;
	}
	
	// Non Terminal RELEXPRESSION is fully implemented here.
	// <simple expression> <relop> <simple expression>
	private int Relexpression(){
		int left, right, saveRelop, result, temp;
		
		if (anyErrors) {
			return -1;
		}
		
		trace("Relexpression", true);
		
		left = SimpleExpression();
		saveRelop = Relop();
		right = SimpleExpression();
		
		temp = symbolList.AddSymbol("@"+factorIndex, 'v', 0) -1;	// Adds new symbol to calculate the relexpression
		factorIndex++;
		
		quads.AddQuad(interp.optable.LookupName("SUB"), left, right, temp);	// Checks to see if the relexpression is true or false
		
		result = quads.NextQuad();
		
		quads.AddQuad(relopToOpcode(saveRelop), temp, 0, 0);
		
		trace("Relexpression", false);
		return result;
	}

	// Returns the appropriate opcode given a specific relop
	private int relopToOpcode(int rel) {
		int result = 0;

		if(rel == lex.codeFor("EQUAL")) {
			result = interp.optable.LookupName("JNZ");
		}
		else if(rel == lex.codeFor("LETHN")) {
			result = interp.optable.LookupName("JNN");
		}
		else if(rel == lex.codeFor("GRTHN")) {
			result = interp.optable.LookupName("JNP");
		}
		else if(rel == lex.codeFor("NEQUL")) {
			result = interp.optable.LookupName("JZ");
		}
		else if(rel == lex.codeFor("GREQU")) {
			result = interp.optable.LookupName("JN");
		}
		else if(rel == lex.codeFor("LEEQU")) {
			result = interp.optable.LookupName("JP");
		}

		return result;
	}
	
	// Non Terminal RELOP is fully implemented here.
	// $EQ | $ LSS | $GTR | $NEQ | $LEQ | $GEQ 
	private int Relop(){
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		
		trace("Relop", true);
		
		if(token.code == lex.codeFor("EQUAL") || token.code == lex.codeFor("LETHN") || token.code == lex.codeFor("GRTHN") || 
				token.code == lex.codeFor("NEQUL") || token.code == lex.codeFor("GREQU") || token.code == lex.codeFor("LEEQU")) {

			recur = token.code;
			
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			
		}
		else {
			error(lex.reserveFor("EQUAL")+" "+lex.reserveFor("LETHN")+" "+lex.reserveFor("GRTHN")+" "+
					lex.reserveFor("NEQUL")+" "+lex.reserveFor("GREQU")+" "+lex.reserveFor("LEEQU")+" ", token.lexeme);
		}
		
		trace("Relop", false);
		return recur;
	}
	
	// Non Terminal SIMPLEEXPRESSION is fully implemented here.
	// [<sign>] <term> { $PLUS | $MINUS <term>}*
	private int SimpleExpression() {
		int left, right, signval, temp, opcode;
		signval = 0;
		if (anyErrors) {
			return -1;
		}
		
		trace("SimpleExpression", true);
		
		//If the token is a + or -, get the sign by calling Sign()
		if(token.code == lex.codeFor("ADD__") || token.code == lex.codeFor("SUB__")) {
			signval = Sign();
		}
		
		//There MUST be a Term so call that function
		left = Term();

		
		if(signval == -1) {
			quads.AddQuad(interp.optable.LookupName("MULT"), left, Minus1Index, left);
		}
		
		
		//If there is an Add or Sub, call AddOp(). After an AddOp, there MUST be a Term, so call Term()
		//Keep looping until the token is no longer a + or -
		while((token.code == lex.codeFor("ADD__") || token.code == lex.codeFor("SUB__"))){
			
			if(token.code == lex.codeFor("ADD__")) {
				opcode = interp.optable.LookupName("ADD");
				
			}
			else {	//Else subtract
				opcode = interp.optable.LookupName("SUB");
			}
			
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			
			right = Term();
			
			// Add a temporary symbol used to calculate this specific simple expression. This helps the code follow PEMDAS
			temp = symbolList.AddSymbol("@"+factorIndex, 'v', 0) -1;
			factorIndex++;
			
			// Add quad then set left equal to temp. Return left
			quads.AddQuad(opcode, left, right, temp);
			left = temp;
		}

			
		trace("SimpleExpression", false);
		
		return left;
	}
	
	// Non terminal gets a Sign
	// $PLUS | $MINUS
	private int Sign() {
		int result = 0;
		
		if (anyErrors) {
			return -1;
		}
		
		trace("Sign", true);
		
		//Check to see if the sign is a plus or minus. Set result accordingly then get next token and return
		if(token.code == lex.codeFor("ADD__")) {
			result = 1;
		}
		else if(token.code == lex.codeFor("SUB__")) {
			result = -1;
		}
		
		token = lex.GetNextToken();
		checkDeclaredToken(token);
			
		trace("Sign", false);
		return result;
	}
	
	// Non terminal gets a Term
	// <factor> { $MULT | $DIVIDE <factor> }*
	private int Term(){
		int left, right, temp, opcode;
		
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Term", true);
		
		//In a term, there MUST be a Factor so call that function
		left = Factor();
		
		//There is an optional loop if the next token is a multiply or divide. After that, there MUST be a Factor, so call Factor().
		//Keep iterating until the token is NOT a * or /
		while((token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID"))){
			
			if(token.code == lex.codeFor("MULTI")) {
				opcode = interp.optable.LookupName("MUL");
				
			}
			else {	//Else divide
				opcode = interp.optable.LookupName("DIV");
			}
			
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			
			right = Factor();
			
			// Add a temporary symbol used to calculate this specific term. This helps the code follow PEMDAS
			temp = symbolList.AddSymbol("@"+factorIndex, 'v', 0) -1;
			factorIndex++;
			
			// Add quad then set left to equal to temp. Return left
			quads.AddQuad(opcode, left, right, temp);
			left = temp;
		}
		
		return left;
	}

	// Non terminal gets the appropriate Factor variable
	// <unsigned constant> | <variable> | $LPAR <simple expression> $RPAR
	private int Factor(){
		int recur = 0; //Return value used later
		
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Factor", true);
		// The unique non-terminal stuff goes here, assigning to "recur" based on recursive calls that were made
		
		//If the token is a '(', get the next token which WILL be a simple expression, so call that function. After that there MUST be a ')' or else raise error
		if(token.code == lex.codeFor("LPARE")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			recur = SimpleExpression();
			
			if(token.code == lex.codeFor("RPARE")) {
				token = lex.GetNextToken();
				checkDeclaredToken(token);
			}
			else {
				error(lex.reserveFor("RPARE"), token.lexeme);
			}
		}
		
		//Else if, the token is an unsigned constant (which would then be an unsigned number) so make sure it is a int or float then call that function
		else if(token.code == lex.codeFor("INTOK") || token.code == lex.codeFor("FLTOK")) {
			recur = UnsignedConstant();
		}
		
		//Else if, the token is an identifier so it is a variable so call that function
		else if(token.code == lex.codeFor("IDTOK") || token.code == lex.codeFor("STTOK")){
			recur = Variable();
		}
		
		//Else something invalid has come up, so raise an error
		else {
			error("Number, Variable or "+lex.reserveFor("LPARE"), token.lexeme);
		}
		
		trace("Factor", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}	
	
	// Non Terminal SIMPLETYPE is fully implemented here.
	// $INTEGER | $FLOAT | $STRING
	private int SimpleType() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		trace("SimpleType", true);
		
		if(token.code == lex.codeFor("INTEG") || token.code == lex.codeFor("FLOAT") || token.code == lex.codeFor("STRIN")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
		}
		else {
			error(lex.reserveFor("INTEG") +" "+lex.reserveFor("FLOAT")+" "+lex.reserveFor("STRIN"), token.lexeme);
		}
		
		trace("SimpleType", false);
		return recur;
	}
	
	// Non Terminal CONSTANT is fully implemented here.
	// [<sign>] <unsigned constant>
	@SuppressWarnings("unused")
	private int Constant() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		
		trace("Constant", true);
		
		//Optional
		if(token.code == lex.codeFor("ADD__") || token.code == lex.codeFor("SUB__")) {
			recur = Sign();
		}
		
		UnsignedConstant();
			
		trace("Constant", false);
		return recur;
	}
	
	// Non terminal gets an UnsignedConstant
	// <unsigned number>
	private int UnsignedConstant() {
		int recur = 0; //Return value used later
		
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("UnsignedConstant", true);
		
		recur = UnsignedNumber();	
		
		trace("UnsignedConstant", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}
	
	// Non terminal gets an UnsignedNumber
	// $FLOATTYPE | $INTTYPE
	private int UnsignedNumber() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		trace("UnsignedNumber", true);
		// float or int or ERROR
		// unsigned constant starts with integer or float number
		if ((token.code == lex.codeFor("INTOK") || (token.code == lex.codeFor("FLTOK")))) {
		// return the s.t. index
		recur = symbolList.LookupSymbol(token.lexeme);
		token = lex.GetNextToken();
		checkDeclaredToken(token);
		} 
		else {
			error("Integer or Floating Point Number", token.lexeme);
		}
		
		trace("UnsignedNumber", false);
		return recur;
	}
	
	// Non Terminal STRINGCONST is fully implemented here.
	// $STRINGTYPE
	private int Stringconst() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		
		trace("Constant", true);
		if(token.code == lex.codeFor("STTOK")) {
			recur = symbolList.LookupSymbol(token.lexeme);
			token = lex.GetNextToken();
			checkDeclaredToken(token);
		}
		else {
			error(lex.reserveFor("STTOK"), token.lexeme);
		}
			
		trace("Constant", false);
		return recur;
	}	
	
	// Non Terminal PROGIDENTIFIER is fully implemented here, leave it as-is.
	private int ProgIdentifier() {
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}

		// This non-term is used to uniquely mark the program identifier
		if (token.code == lex.codeFor("IDTOK")) {
			// Because this is the progIdentifier, it will get a 'P' type to prevent re-use as a var
			symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'P', 0);
			//move on
			token = lex.GetNextToken();
			checkDeclaredToken(token);
		}
		
		return recur;
	}
	
	// Not a NT, but used to shorten Statement code body for readability.
	// <variable> $COLON-EQUALS <simple expression>
	private int handleAssignment() {
		int left, right;
		int recur = 0;
		
		if (anyErrors) {
			return -1;
		}
		
		trace("handleAssignment", true);
		//have ident already in order to get to here, handle as Variable
		left = Variable(); //Variable moves ahead, next token ready
		if (token.code == lex.codeFor("ASIGN")) {
			token = lex.GetNextToken();
			checkDeclaredToken(token);
			right = SimpleExpression();
			quads.AddQuad(interp.optable.LookupName("MOV"), right, 0, left);
		} 
		else {
			error(lex.reserveFor("ASIGN"), token.lexeme);
		}
		trace("handleAssignment", false);
		return recur;
	}
	

/****************************************************/
	
	/* UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
	// error provides a simple way to print an error statement to standard output
	// and avoid reduncancy
	private void error(String wanted, String got) {
		anyErrors = true;
		System.out.println("ERROR: Expected " + wanted + " but found " + got);
	}

	// trace simply RETURNs if traceon is false; otherwise, it prints an
	// ENTERING or EXITING message using the proc string
	private void trace(String proc, boolean enter) {
		String tabs = "";
		
		if (!traceon) {
			return;
		}
		if (enter) {
			tabs = repeatChar(" ", level);
			System.out.print(tabs);
			System.out.println("--> Entering " + proc);
			level++;
		} 
		else {
			if (level > 0) {
				level--;
			}
			tabs = repeatChar(" ", level);
			System.out.print(tabs);
			System.out.println("<-- Exiting " + proc);
		}
	}
	
	// repeatChar returns a string containing x repetitions of string s;
	// nice for making a varying indent format
	private String repeatChar(String s, int x) {
		int i;
		String result = "";
		for (i = 1; i <= x; i++) {
			result = result + s;
		}
		return result;
	}
	
	// Whenever the next token is received, this function will check if it's an appropriate identifier token whether it was declared prior to usage or not
	private boolean checkDeclaredToken(Lexical.token token) {
		
		//If the token is an identifier AND the usage of that identifier is a 'v' AND it is not the first symbol (program name)
		if (token.code == lex.codeFor("IDTOK") && (symbolList.GetUsage(symbolList.LookupSymbol(token.lexeme)) == 'v') && (symbolList.LookupSymbol(token.lexeme) != 2)) {

			//Check all declared identifiers. If there is a match, then the identifier was properly declared. Return true
			for(int i = 0; i < declaredIdentifiers.size(); i++) {
				if(declaredIdentifiers.get(i).equals(token.lexeme)) {
					return true;
				}
			}
			
			//Otherwise, it was not in the table so print an error and return false
			System.out.println("ERROR: "+token.lexeme+" is undeclared!");
			declaredIdentifiers.add(token.lexeme);	//Add it to the declared identifiers list ONLY so that the error doesn't show up again
		}
		
		return false;
	}
	
	
}