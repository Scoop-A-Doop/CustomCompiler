/* Name: Suleyman Shouib
 * Course: CS 4100
 * Term: Spring 2023
 */
package ADT;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/*
*
* @author abrouill
*/
import java.io.*;
@SuppressWarnings("unused")
public class Lexical {
	private File file;                        //File to be read for input
	private FileReader filereader;            //Reader, Java reqd
	private BufferedReader bufferedreader;    //Buffered, Java reqd
	private String line;                      //Current line of input from file
	private int linePos;                      //Current character position in the current line
	private SymbolTable saveSymbols;          //SymbolTable used in Lexical sent as parameter to construct
	private boolean EOF;                      //End Of File indicator
	private boolean echo;                     //true means echo each input line
	private boolean printToken;               //true to print found tokens here
	private int lineCount;                    //line #in file, for echo-ing
	private boolean needLine;                 //track when to read a new line
	char currCh;
	
	//Tables to hold the reserve words and the mnemonics for token codes
	private final int sizeReserveTable = 50;
	private ReserveTable reserveWords = new ReserveTable(sizeReserveTable); //a few more than # reserves
	private ReserveTable mnemonics = new ReserveTable(sizeReserveTable);  //a few more than # reserves constructor
	
	public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
		saveSymbols = symbols;  //map the initialized parameter to the local ST 
		echo = echoOn;          //store echo status
		lineCount = 0;          //start the line number count
		line = "";              //line starts empty
		needLine = true;        //need to read a line
		printToken = false;     //default OFF, do not print tokesn here within GetNextToken; call setPrintToken to change it publicly.
		linePos = -1;      	 	//no chars read yet call 
		//initializations of tables
		initReserveWords(reserveWords);
		initMnemonics(mnemonics);
		//set up the file access, get first character, line retrieved 1st time
		try {
			file = new File(filename);    //creates a new file instance
			filereader = new FileReader(file);   //reads the file
			bufferedreader = new BufferedReader(filereader);  //creates a buffering character input stream
			EOF = false;
			currCh = GetNextChar();	//I think this needs to be and return a char
		} catch (IOException e) {
			EOF = true;
			e.printStackTrace();
		}
		
	}

	//Initializes Mnemonic reserve table with the SHORTENED name of the reserved word, WHILE MATCHING ReserveWords codes
	private void initMnemonics(ReserveTable mnemonics) {
		//Student must create their own 5-char mnemonics
		mnemonics.Add("GOTO_", 0);
		mnemonics.Add("INTEG", 1);
		mnemonics.Add("TO___", 2);
		mnemonics.Add("DO___", 3);
		mnemonics.Add("IF___", 4);
		mnemonics.Add("THEN_", 5);
		mnemonics.Add("ELSE_", 6);
		mnemonics.Add("FOR__", 7);
		mnemonics.Add("OF___", 8);
		mnemonics.Add("WRTLN", 9);
		mnemonics.Add("REDLN", 10);
		mnemonics.Add("BEGIN", 11);
		mnemonics.Add("END__", 12);
		mnemonics.Add("VAR__", 13);
		mnemonics.Add("DOWHI", 14);
		mnemonics.Add("UNIT_", 15);
		mnemonics.Add("LABEL", 16);
		mnemonics.Add("REPET", 17);
		mnemonics.Add("UNTIL", 18);
		mnemonics.Add("PROCE", 19);
		mnemonics.Add("DWNTO", 20);
		mnemonics.Add("FUNCT", 21);
		mnemonics.Add("RETRN", 22);
		mnemonics.Add("FLOAT", 23);
		mnemonics.Add("STRIN", 24);
		mnemonics.Add("ARRAY", 25);
		//1 and 2-char
		mnemonics.Add("DIVID", 30);
		mnemonics.Add("MULTI", 31);
		mnemonics.Add("ADD__", 32);
		mnemonics.Add("SUB__", 33);
		mnemonics.Add("LPARE", 34);
		mnemonics.Add("RPARE", 35);
		mnemonics.Add("SEMIC", 36);
		mnemonics.Add("ASIGN", 37);
		mnemonics.Add("GRTHN", 38);
		mnemonics.Add("LETHN", 39);
		mnemonics.Add("GREQU", 40);
		mnemonics.Add("LEEQU", 41);
		mnemonics.Add("EQUAL", 42);
		mnemonics.Add("NEQUL", 43);
		mnemonics.Add("COMMA", 44);
		mnemonics.Add("LBRAC", 45);
		mnemonics.Add("RBRAC", 46);
		mnemonics.Add("COLON", 47);
		mnemonics.Add("PERID", 48);
		mnemonics.Add("OTHER", 99);
		//Tokens
		mnemonics.Add("IDTOK", 50);
		mnemonics.Add("INTOK", 51);
		mnemonics.Add("FLTOK", 52);
		mnemonics.Add("STTOK", 53);
	}

	//Initializes Reserve Words table with the "actual" name of the reserved word
	private void initReserveWords(ReserveTable reserveWords) {
		//Student must provide the rest
		reserveWords.Add("GOTO", 0);
		reserveWords.Add("INTEGER", 1);
		reserveWords.Add("TO", 2);
		reserveWords.Add("DO", 3);
		reserveWords.Add("IF", 4);
		reserveWords.Add("THEN", 5);
		reserveWords.Add("ELSE", 6);
		reserveWords.Add("FOR", 7);
		reserveWords.Add("OF", 8);
		reserveWords.Add("WRITELN", 9);
		reserveWords.Add("READLN", 10);
		reserveWords.Add("BEGIN", 11);
		reserveWords.Add("END", 12);
		reserveWords.Add("VAR", 13);
		reserveWords.Add("DOWHILE", 14);
		reserveWords.Add("UNIT", 15);
		reserveWords.Add("LABEL", 16);
		reserveWords.Add("REPEAT", 17);
		reserveWords.Add("UNTIL", 18);
		reserveWords.Add("PROCEDURE", 19);
		reserveWords.Add("DOWNTO", 20);
		reserveWords.Add("FUNCTION", 21);
		reserveWords.Add("RETURN", 22);
		reserveWords.Add("FLOAT", 23);
		reserveWords.Add("STRING", 24);
		reserveWords.Add("ARRAY", 25);
		//1 and 2-char
		reserveWords.Add("/", 30);
		reserveWords.Add("*", 31);
		reserveWords.Add("+", 32);
		reserveWords.Add("-", 33);
		reserveWords.Add("(", 34);
		reserveWords.Add(")", 35);
		reserveWords.Add(";", 36);
		reserveWords.Add(":=", 37);
		reserveWords.Add(">", 38);
		reserveWords.Add("<", 39);
		reserveWords.Add(">=", 40);
		reserveWords.Add("<=", 41);
		reserveWords.Add("=", 42);
		reserveWords.Add("<>", 43);
		reserveWords.Add(",", 44);
		reserveWords.Add("[", 45);
		reserveWords.Add("]", 46);
		reserveWords.Add(":", 47);
		reserveWords.Add(".", 48);
		reserveWords.Add("OTHER", 99);
		//Tokens
		reserveWords.Add("IDENTIFIER", 50);
		reserveWords.Add("INTTOKEN", 51);
		reserveWords.Add("FLOATTOKEN", 52);
		reserveWords.Add("STRINGTOKEN", 53);
	}

	//Depending on what the current char value is, call the appropriate function to get the next token
	public token GetNextToken() {
		token result = new token();
		currCh = skipWhiteSpace();
		if (isLetter(currCh)) { //is identifier
			result = getIdentifier();
		} 
		else if (isDigit(currCh)) { //is numeric
			result = getNumber();
		} 
		else if (isStringStart(currCh)) { //string literal
			result = getString();
		} 
		else { //default char checks
			result = getOtherToken();
		}
		if ((result.lexeme.equals("")) || (EOF)) {
			result = null;
		}
		//set the mnemonic
		if (result != null) {
			// THIS LINE REMOVED-- PUT BACK IN TO USE LOOKUP
			result.mnemonic = mnemonics.LookupCode(result.code);
			if (printToken) {
				System.out.println("\t" + result.mnemonic + " | \t" + 
				String.format("%04d", result.code) + " | \t" + result.lexeme);
			}
		}
		return result;
	}

	//Looks up the code of the mnemonic via the mnemonics name
	public int codeFor(String mnemonic) {
		return mnemonics.LookupName(mnemonic);
	}
	
	//Looks up the name of the mnemonic code via the mnemonics name
	public String reserveFor(String mnemonic) {
		return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
	}
	
	//Returns if we have reached the end of file
	public boolean EOF() {
		return EOF;
	}
	
	//Toggles printToken to on or off (it is off by default, so this should only be called to turn it on)
	public void setPrintToken(boolean on) {
		printToken = on;
	}
	
	/* Utility Functions! */
	
	//Prints an error to the console
	private void consoleShowError(String message) {
		System.out.println("**** ERROR FOUND: " + message);
	}
	
	// Character category for alphabetic chars
	private boolean isLetter(char ch) {
		return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
	}
	
	// Character category for 0..9
	private boolean isDigit(char ch) {
		return ((ch >= '0') && (ch <= '9'));
	}
	
	// Category for any whitespace to be skipped over
	private boolean isWhitespace(char ch) {
		// SPACE, TAB, NEWLINE are white space
		return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
	}
	
	// Returns the VALUE of the next character without removing it from the input line.  
	// Useful for checking 2-character tokens that start with a 1-character token.
	private char PeekNextChar() {
		char result = ' ';
		if ((needLine) || (EOF)) {
			result = ' '; //at end of line, so nothing
		} 
		else {
			if ((linePos + 1) < line.length()) { //have a char to peek
				result = line.charAt(linePos + 1);
			}
		}
		return result;
	}
	
	// Called by GetNextChar when the cahracters in the current line are used up.
	// STUDENT CODE SHOULD NOT EVER CALL THIS!
	private void GetNextLine() {
		try {
			line = bufferedreader.readLine();
			if ((line != null) && (echo)) {
				lineCount++;
				System.out.println(String.format("%04d", lineCount) + " " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		if (line == null) {    // The readLine returns null at EOF, set flag
			EOF = true;
		}
		linePos = -1;      // reset vars for new line if we have one
		needLine = false;  // we have one, no need. the line is ready for the next call to get a character
	}
	
	// Called to get the next character from file, automatically gets a new
	// line when needed. CALL THIS TO GET CHARACTERS FOR GETIDENT etc.
	private char GetNextChar() {
		char result;
		
		if (needLine){ //ran out last time we got a char, so get a new line
			GetNextLine();
		}
		
		//try to get char from line buff
		if (EOF) {
			result = '\n';
			needLine = false;
		} 
		else {
			if ((linePos < line.length() - 1)) { //have a character available
				linePos++;
				result = line.charAt(linePos);
			} 
			else { //need a new line, but want to return eoln on this call first
				result = '\n';
				needLine = true; //will read a new line on next GetNextChar call
			}
		}
		return result;
	}
	
	// The constants below allow flexible comment start/end characters
	final char commentStart_1 = '{';
	final char commentEnd_1 = '}';
	final char commentStart_2 = '(';
	final char commentPairChar = '*';
	final char commentEnd_2 = ')';
	
	// Skips past single and multi-line comments, and outputs UNTERMINATED
	//  COMMENT when end of line is reached before terminating
	String unterminatedComment = "Comment not terminated before End Of File";
	public char skipComment(char curr) {
		if (curr == commentStart_1) {
			curr = GetNextChar();
			while ((curr != commentEnd_1) && (!EOF)) {
				curr = GetNextChar();
			}
			if (EOF) {
				consoleShowError(unterminatedComment);
			} 
			else {
				curr = GetNextChar();
			}
		} 
		else {
			if ((curr == commentStart_2) && (PeekNextChar() == commentPairChar)) {
			curr = GetNextChar(); // get the second
			curr = GetNextChar(); // into comment or end of comment
			//while ((curr != commentPairChar) && (PeekNextChar() != commentEnd_2) &&(!EOF)) {
				while ((!((curr == commentPairChar) && (PeekNextChar() == commentEnd_2))) && (!EOF)) {
					//if (lineCount >=4) {
						//System.out.println("In Comment, curr, peek: "+curr+", "+PeekNextChar());
					//}
					curr = GetNextChar();
				}
				if (EOF) {
					consoleShowError(unterminatedComment);
				} 
				else {
					curr = GetNextChar();          //must move past close
					curr = GetNextChar();          //must get following
				}
			}
		}

		return (curr);
	}
	
	// Reads past all whitespace as defined by isWhiteSpace
	// NOTE THAT COMMENTS ARE SKIPPED AS WHITESPACE AS WELL!
	public char skipWhiteSpace() {
		do {
			while ((isWhitespace(currCh)) && (!EOF)) {
				currCh = GetNextChar();
			}
			currCh = skipComment(currCh);
		} while (isWhitespace(currCh) && (!EOF));
		return currCh;
	}
	
	//Returns true of char is a :, <, or >
	private boolean isPrefix(char ch) {
		return ((ch == ':') || (ch == '<') || (ch == '>'));
	}
	
	//Returns true or false if the start of a string is an open quotation mark "
	private boolean isStringStart(char ch) {
		return ch == '"';
	}
	
	//Finds and returns an identifier token 
	//Upon creating a valid token, it is added to the symbol table IF it is not a reserved word
	private token getIdentifier() {
		token identifierToken = new token();
		String lexeme = "";
		int maxLength = 20;
		int lengthCounter = 0;
		boolean maxReached = false;	//Flag that keeps track of if identifier length has reached maxLength. It is used to print only one warning and for other checks
		
		//Append starting character. Increment length counter and get next character
		lexeme += currCh; 	
		lengthCounter++;	
		currCh = GetNextChar();
		  
		// While currCh is a letter, digit or underscore AND it has NOT reached the max length, append to lexeme and increment counter
		while ((isLetter(currCh) || isDigit(currCh) || currCh == '_')) {
			//Only add onto currCh if identifier isnt at max length
			if(!maxReached) {
				lexeme += currCh;
				lengthCounter++;
				currCh = GetNextChar();
			}
			
			// If after adding currCh the length has met/surpassed maxLength, print a warning
			if(!maxReached && lengthCounter >= maxLength) {
				consoleShowError("Identifer has reached max length.");
				maxReached = true;
			}
			
			//If Identifier has reached max length, go through the rest of the characters WITHOUT appending to lexeme
			if(maxReached) {
				currCh = GetNextChar();
			}
		}
		
		// If identifier is NOT in reservedWords then add it to symbol table and return token
		if (reserveWords.LookupName(lexeme) == -1) {
			saveSymbols.AddSymbol(lexeme, 'v', 0);
			identifierToken.lexeme = lexeme;
			identifierToken.code = reserveWords.LookupName("IDENTIFIER");
			identifierToken.mnemonic = reserveFor(lexeme);
		}
		else {	//Else it IS a reserved word, so just return the token
			identifierToken.lexeme = lexeme;
			identifierToken.code = reserveWords.LookupName(lexeme);
			identifierToken.mnemonic = reserveFor(lexeme);
		}
		
		return identifierToken;	//If it reaches here, it is a reserved word
	}
	
	//Finds and returns a number token 
	//Upon creating a valid token, it is added to the symbol table 
	private token getNumber() {
		/* a number is:   <digit>+[.<digit>*[E<digit>+]] */
		token numberToken = new token();
		String lexeme = "";	
		int maxLength = 6;	//Max length for an integer. If the token is a float, it'll change to 12
		int lengthCounter = 0;	
		boolean floatE = false;	//Flags whether or not the float uses the E operator or not
		boolean maxReached = false;		
		boolean isInteger = true;	//Flags whether the token is an integer or a float
		
		// While currCh is a digit and it has not reached maximum length, append to lexeme and increment counter
		while (isDigit(currCh)) {
						
			//Only add onto lexeme if Integer IS NOT at max length
			if(!maxReached) {
				lexeme += currCh;
				lengthCounter++;
				currCh = GetNextChar();
			}
						
			// If after adding currCh the length has met/surpassed maxLength, print a warning, and flag maxReached to true
			if(!maxReached && lengthCounter >= maxLength) {
				consoleShowError("Integer has reached max length.");
				maxReached = true;
			}
						
			//If the integer has reached its maxLength, iterate through the rest of the number chars WITHOUT appending to lexeme
			if(maxReached) {
				currCh = GetNextChar();
			}
		}
		
		//The number is now a float
		if(currCh == '.') {
			isInteger = false;	//Token is now a float
			lexeme += currCh;
			lengthCounter++;
			maxLength = 12;		//Max length is now the max length of a float
			currCh = GetNextChar();
			
			// While currCh is a digit, append to lexeme and increment counter
			while (isDigit(currCh)) {
				
				//Only add onto lexeme if Float isnt at max length
				if(!maxReached) {
					lexeme += currCh;
					lengthCounter++;
					currCh = GetNextChar();
				}
				
				// If after adding currCh the length has met/surpassed maxLength, print a warning, and flag maxReached to true
				if(!maxReached && lengthCounter >= maxLength) {
					consoleShowError("Float has reached max length.");
					maxReached = true;
				}
				
				//If float has reached max length, go through the rest of the numbers without appending to lexeme
				if(maxReached) {
					currCh = GetNextChar();
				}
			}
			
			//If currCh is an E, append it to the lexeme
			if(currCh == 'E') {
				floatE = true;	//Flag floatE as true. This will be used when calculating its value for the symbol table
				lexeme += currCh;
				currCh = GetNextChar();
				
				// While currCh is a digit, append to lexeme and increment counter
				if(isDigit(currCh)) {
					while(isDigit(currCh)) {
						//Only add onto lexeme if Float isnt at max length
						if(!maxReached) {
							lexeme += currCh;
							lengthCounter++;
							currCh = GetNextChar();
						}
						
						// If after adding currCh the length has met/surpassed maxLength, print a warning and flag maxReached to true
						if(!maxReached && lengthCounter >= maxLength) {
							consoleShowError("Float has reached max length.");
							maxReached = true;
						}
						
						//If float has reached max length, go through the rest of the numbers without appending to lexeme
						if(maxReached) {
							currCh = GetNextChar();
						}	
					}
				}
			}
		}
		
		//Token has been fully read. Set the tokens lexeme and mnemonic
		numberToken.lexeme = lexeme;
		numberToken.mnemonic = reserveFor(lexeme);
		
		//If the token is an integer, save code as integer and add it to the symbol table
		if(isInteger) {
			numberToken.code = reserveWords.LookupName("INTTOKEN");
			//If the code reached max, add it to the symbol with a value of zero. 
			if(maxReached) {
				saveSymbols.AddSymbol(numberToken.lexeme, 'c', 0);
			}
			else {	//Else, add it with its appropriate value that is the same as its lexeme
				saveSymbols.AddSymbol(numberToken.lexeme, 'c', Integer.parseInt(numberToken.lexeme));
				
			}
		}
		else {	//Else the token is a float, so save code as float and add it to the symbol table
			numberToken.code = reserveWords.LookupName("FLOATTOKEN");
			//If the code reached max, add it to the symbol table with a value of zero. 
			if(maxReached) {
				saveSymbols.AddSymbol(numberToken.lexeme, 'c', 0.0);
			}
			else {	//Else, add it with its appropriate value after properly calculating the value
				//If the float has an E in it, convert the E into a numerical operation (10^<# after E>)
				if(floatE) {
					int locationOfE = lexeme.indexOf('E');	//Find the index of the lexeme with the E
					double firstPart = Double.parseDouble(lexeme.substring(0, locationOfE));	//Saves the part of the float before E
					
					//If there is NO NUMBER after the E, then the number is INVALID so the code should be OTHER
					if(lexeme.substring(locationOfE+1).equals("")) {	
						numberToken.code = reserveWords.LookupName("OTHER");
					}
					else {	//Else, extract the part of the float after the E and calculate the floats value. Then add it to the symbol table with its numeric value
						double power = Double.parseDouble(lexeme.substring(locationOfE+1));
						double value = firstPart * Math.pow(10, power);
						saveSymbols.AddSymbol(numberToken.lexeme, 'c', value);
					}
				}
				else {	//Else the float DOES NOT contain an E in it, so just add it to the symbol table with its lexeme as the value
					saveSymbols.AddSymbol(numberToken.lexeme, 'c', Double.parseDouble(numberToken.lexeme));
				}
			}
		}
		
		return numberToken;
	}
	
	//Finds and returns a string token
	//Upon creating a valid token, it is added to the symbol table
	private token getString() {
		token stringToken = new token();
		String lexeme = "";	
		boolean unterminatedString = false;	//Records whether or not the string was unterminated with an end quote character (")
		currCh = GetNextChar();
		
		//Strings must be completed within the line it is initialized. So if the line does not end with a ", it is going to be an unterminated string
		//Print a warning and flag unterminatedString to true
		/*
		if(!line.endsWith("\"")) {
			consoleShowError("Unterminated String");
			unterminatedString = true;
		}
		*/
		
		//While currCh is not an end quote character, append it to the lexeme 
		while(currCh != '"') {
			lexeme += currCh;
			currCh = GetNextChar();
			
			//If boolean needLine is true, then we have reached the end of the line without an end quote.
			//Set currCh to " to exit the while loop. The " will NOT be appended to lexeme this way
			if(needLine) {
				currCh = '"';
			}
		}
		
		//If currCh was falsely set to " to terminate the while loop, call GetNextChar() so that it will be the correct char for the next token
		if(currCh == '"') {
			currCh = GetNextChar();
		}
		
		//Set the lexeme and mnemonic for the stringToken
		stringToken.lexeme = lexeme;
		stringToken.mnemonic = reserveFor(lexeme);
		
		//If the string was unterminated, it is INVALID so make the code OTHER
		if(unterminatedString) {
			stringToken.code = reserveWords.LookupName("OTHER");
		}
		else {	//Else the token is valid, so set the code to the appropriate value and add it to the symbol table where the value is the lexeme
			stringToken.code = reserveWords.LookupName("STRINGTOKEN");
			saveSymbols.AddSymbol(stringToken.lexeme, 'c', stringToken.lexeme);
		}
		
		return stringToken;
	}
	
	//Finds and returns an "Other" token (1 char tokens & 2 char tokens)
	private token getOtherToken() {
		token otherToken = new token();
		String lexeme = "";
		
		//If the char is a prefix (:, >, <), then check to see if it might be a 2 char token.
		if(isPrefix(currCh)) {
			char peek = PeekNextChar();	//Peeks to see the next char value to confirm that it is indeed a 2char token (should be = or >)
			
			if(currCh == ':'){
				if(peek == '=') {
					// Token is :=
					lexeme = "" + currCh + peek;
					otherToken.lexeme = lexeme;
					otherToken.code = reserveWords.LookupName(lexeme);
					otherToken.mnemonic = reserveFor(lexeme);
					currCh = GetNextChar();	//Get it to the second char token
					currCh = GetNextChar();	//Move onto the next NEW token
					return otherToken;
				}
			}
			else if(currCh == '>') {
				if(peek == '=') {
					// Token is >=
					lexeme = "" + currCh + peek;
					otherToken.lexeme = lexeme;
					otherToken.code = reserveWords.LookupName(lexeme);
					otherToken.mnemonic = reserveFor(lexeme);
					currCh = GetNextChar();	//Get it to the second char token
					currCh = GetNextChar();	//Move onto the next NEW token
					return otherToken;
				}
			}
			else if(currCh == '<') {
				if(peek == '=') {
					// Token is <=
					lexeme = "" + currCh + peek;
					otherToken.lexeme = lexeme;
					otherToken.code = reserveWords.LookupName(lexeme);
					otherToken.mnemonic = reserveFor(lexeme);
					currCh = GetNextChar();	//Get it to the second char token
					currCh = GetNextChar();	//Move onto the next NEW token
					return otherToken;
				}
				else if(peek == '>') {
					// Token is <>
					lexeme = "" + currCh + peek;
					otherToken.lexeme = lexeme;
					otherToken.code = reserveWords.LookupName(lexeme);
					otherToken.mnemonic = reserveFor(lexeme);
					currCh = GetNextChar();	//Get it to the second char token
					currCh = GetNextChar();	//Move onto the next NEW token
					return otherToken;
				}
			}
			
			//At this point, if the above if statements have not been triggered, the token is a single char token
			//Create the lexeme, set the otherToken lexeme, code (from reserve table) and mnemonic and set char to be the next value for the next token.
			
			//For some reason the code doesn't recognize : as a prefix single char token, so this if statement is here to prevent that bug
			if(currCh == ':') {
				lexeme = "" + currCh;
				otherToken.lexeme = lexeme;
				otherToken.code = reserveWords.LookupName(lexeme);
				otherToken.mnemonic = reserveFor(lexeme);
				currCh = GetNextChar();
				return otherToken;
			}
			else { //Else it is a 1 char token that IS a prefix (> or < since : is taken care of above)
				lexeme = "" + currCh;
				otherToken.lexeme = lexeme;
				otherToken.code = reserveWords.LookupName(lexeme);
				otherToken.mnemonic = reserveFor(lexeme);
				currCh = GetNextChar();
				return otherToken;
			}
		}
		
		//Else if, it is a single value char token. Check to make sure the char is not a number or a letter just in case
		else if(!isDigit(currCh) && !isLetter(currCh)) {
			lexeme = "" + currCh;	//Append the lexeme to the single token value and save to the token lexeme
			otherToken.lexeme = lexeme;
			
			//If there is no code currently in the reserve table for the given token, then it is an "Other" otherToken (!, ?, %, etc.)
			if(reserveWords.LookupName(lexeme) == -1) {
				otherToken.code = reserveWords.LookupName("OTHER");
				otherToken.mnemonic = reserveFor(lexeme);
			}
			else {	//Else it is in the reserve table so get the appropriate code
				otherToken.code = reserveWords.LookupName(lexeme);
				otherToken.mnemonic = reserveFor(lexeme);
			}
		}
		
		currCh = GetNextChar();
		return otherToken;	//If code reaches here, it is returning a 1 char token
	}	
	
	// Checks to see if a string contains a valid DOUBLE
	public boolean doubleOK(String stin) {
		boolean result;
		Double x;
		try {
			x = Double.parseDouble(stin);
			result = true;
		} catch (NumberFormatException ex) {
			result = false;
		}
		return result;
	}
	
	// Checks the input string for a valid INTEGER
	public boolean integerOK(String stin) {
		boolean result;
		int x;
		try {
			x = Integer.parseInt(stin);
			result = true;
		} catch (NumberFormatException ex) {
			result = false;
		}
		return result;
	}
	
	// inner class "token" is declared here, no accessors needed
	public class token {
		public String lexeme;
		public int code;
		public String mnemonic;
		
		token() {
			lexeme = "";
			code = 0;
			mnemonic = "";
		}
	}
	
}