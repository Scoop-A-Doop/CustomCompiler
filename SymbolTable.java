/* Name: Suleyman Shouib
 * Course: CS 4100
 * Term: Spring 2023
 */
package ADT;

import java.io.FileWriter;
import java.io.IOException;

public class SymbolTable {
	private int nextAvailable = 0;	// Keeps track of the next available row of the Quad Table
	Symbol[] symbolTable;	// The Symbol Table
	
	// Constructor sets the size of symbolTable
	public SymbolTable(int maxSize) {
		symbolTable = new Symbol[maxSize];
	}
	
	/* Overloaded Method adds an INTEGER symbol. If a symbol of the same name already exists, 
	 * do not add that symbol to the table, instead return the currently existing symbols index.
	 * If the symbolTable is at maximum capacity, return -1. Otherwise return the index of where the new symbol has been added
	 */
	public int AddSymbol(String symbol, char usage, int value) {
		if(nextAvailable < symbolTable.length) {
			for(int i = 0; i < nextAvailable; i++) {
				if(symbolTable[i].getName().equalsIgnoreCase(symbol)) {
					return i;
				}
			}
			symbolTable[nextAvailable] = new Symbol(symbol, usage, value);
			nextAvailable++;
		}
		else {
			return -1;
		}
		return nextAvailable;
	}

	/* Overloaded Method adds a FLOAT symbol. If a symbol of the same name already exists, 
	 * do not add that symbol to the table, instead return the currently existing symbols index.
	 * If the symbolTable is at maximum capacity, return -1. Otherwise return the index of where the new symbol has been added
	 */
	public int AddSymbol(String symbol, char usage, double value) {
		if(nextAvailable < symbolTable.length) {
			for(int i = 0; i < nextAvailable; i++) {
				if(symbolTable[i].getName().equalsIgnoreCase(symbol)) {
					return i;
				}
			}
			symbolTable[nextAvailable] = new Symbol(symbol, usage, value);
			nextAvailable++;
		}
		else {
			return -1;
		}
		return nextAvailable;
	}

	/* Overloaded Method adds a STRING symbol. If a symbol of the same name already exists, 
	 * do not add that symbol to the table, instead return the currently existing symbols index.
	 * If the symbolTable is at maximum capacity, return -1. Otherwise return the index of where the new symbol has been added
	 */
	public int AddSymbol(String symbol, char usage, String value) {
		if(nextAvailable < symbolTable.length) {
			for(int i = 0; i < nextAvailable; i++) {
				if(symbolTable[i].getName().equalsIgnoreCase(symbol)) {
					return i;
				}
			}
			symbolTable[nextAvailable] = new Symbol(symbol, usage, value);
			nextAvailable++;
		}
		else {
			return -1;
		}
		return nextAvailable;
	}
	
	/* Given the name of a symbol, search through the table and return the index of the symbols placement.
	 * Else, symbol is not in the symbol table so return -1
	 */
	public int LookupSymbol(String symbol) {
		for(int i = 0; i < nextAvailable; i++) {
			if(symbolTable[i].getName().equalsIgnoreCase(symbol)) {
				return i;
			}
		}
		return -1;
	}
	
	// Returns the name of a symbol
	public String GetSymbol(int index) {
		return symbolTable[index].getName();
	}
	
	// Returns the usage type of a symbol
	public char GetUsage(int index) {
		return symbolTable[index].getUsage();
	}
	
	// Returns the data type of a symbol
	public char GetDataType(int index) {
		return symbolTable[index].getDataType();
	}
	
	// Returns the STRING value of a symbol
	public String GetString(int index) {
		return symbolTable[index].getStringValue();
	}
	
	// Returns the INTEGER value of a symbol
	public int GetInteger(int index) {
		return symbolTable[index].getIntValue();
	}
	
	// Returns the FLOAT value of a symbol
	public double GetFloat(int index) {
		return symbolTable[index].getDoubleValue();
	}
	
	// Overloaded method updates the USAGE and INTEGER fields
	public void UpdateSymbol(int index, char usage, int value){
		symbolTable[index].setUsage(usage);
		symbolTable[index].setIntValue(value);
	}
	
	// Overloaded method updates the USAGE and DOUBLE fields
	public void UpdateSymbol(int index, char usage, double value){
		symbolTable[index].setUsage(usage);
		symbolTable[index].setDoubleValue(value);
	}
	
	// Overloaded method updates the USAGE and STRING fields
	public void UpdateSymbol(int index, char usage, String value){
		symbolTable[index].setUsage(usage);
		symbolTable[index].setStringValue(value);
	}
	
	// Prints the symbol table to the given file name
	public void PrintSymbolTable(String filename) {
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.write(pad(pad("Index",3,true),8,false)+pad(pad("Name",3,true),27,false)
			+pad(pad("Use",4,true),10,false)+pad(pad("Typ",1,true),8,false)+pad(pad("Value",1,true),7,false)+"\n");
			
			for(int i = 0; i < nextAvailable; i++) {
				myWriter.write(pad(pad(Integer.toString(i),3,true),8,false)+"|"
						+pad(pad(symbolTable[i].getName(),3,true),25,false)+"|"
						+pad(pad(Character.toString(symbolTable[i].getUsage()),3,true),8,false)+"|"
						+pad(pad(Character.toString(symbolTable[i].getDataType()),3,true),8,false)+"|"
						+pad(pad(symbolTable[i].getAssignedValue(),3,true),25,false)+"\n");
			}
			myWriter.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Function provided by Professor Al Brouillette, applies padding format around the string "input". 
	 * Padding size is determined by len and padding direction determined by left (true = left, false = right). Returns the padded string
	 */
	public String pad(String input, int len, boolean left) {
	    while (input.length() < len){
	        if (left)
	          input = " " +input ;
	        else
	          input = input + " ";
	    }
	    return input;
	}
}

class Symbol{
	String name;
	char usage;
	char dataType;
	int intValue;
	double doubleValue;
	String stringValue;
	
	// Overloaded constructor sets name, usage, and INTEGER data type/value
	public Symbol(String n, char u, int v) {
		name = n;
		usage = u;
		dataType = 'I';
		intValue = v;
	}
	
	// Overloaded constructor sets name, usage, and FLOAT data type/value
	public Symbol(String n, char u, double v) {
		name = n;
		usage = u;
		dataType = 'F';
		doubleValue = v;
	}
	
	// Overloaded constructor sets name, usage, and STRING data type/value
	public Symbol(String n, char u, String v) {
		name = n;
		usage = u;
		dataType = 'S';
		stringValue = v;
	}
	
	// Getter for symbol name
	public String getName() {
		return name;
	}
	
	// Getter for symbol data type
	public char getDataType() {
		return dataType;
	}
	
	// Getter for symbol usage type
	public char getUsage() {
		return usage;
	}
	
	// Setter for symbol usage type
	public void setUsage(char u) {
		usage = u;
	}
	
	// Getter for symbol INTEGER value
	public int getIntValue() {
		return intValue;
	}
	
	// Setter for symbol INTEGER value
	public void setIntValue(int v) {
		intValue = v;
	}
	
	// Getter for symbol FLOAT value
	public double getDoubleValue() {
		return doubleValue;
	}
	
	// Setter for symbol FLOAT value
	public void setDoubleValue(double v) {
		doubleValue = v;
	}
	
	// Getter for symbol STRING value
	public String getStringValue() {
		return stringValue;
	}
	
	// Setter for symbol STRING value
	public void setStringValue(String v) {
		stringValue = v;
	}
	
	// Checks the assigned data type and returns the respective value of said data type as a string 
	// Used for printing the table
	public String getAssignedValue() {
		if(getDataType() == 'I') {
			return Integer.toString(getIntValue());
		}
		else if(getDataType() == 'F') {
			return Double.toString(getDoubleValue());
		}
		else{
			return getStringValue();
		}
	}

}