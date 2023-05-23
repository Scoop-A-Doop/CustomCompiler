package ADT;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class ReserveTable {
	int maxSize;
	int elementsInUse = -1;	// Keeps track of the number of elements that are in reserveTable
	ArrayList<Command> reserveTable = new ArrayList<Command>();
	
	// Constructor sets the max size of the Reserve Table
	public ReserveTable(int i) {
		maxSize = i;
	}
	
	/* Given a name and code number, adds a new Command object to the ArrayList reserveTable if the reserve table has not reached maximum capacity.
	 * Returns the index of the added command, however if there is an Index Out Of Bounds error, return -1 
	 */
	public int Add(String name, int code) {
		if(elementsInUse < maxSize) {
			elementsInUse++;
			Command command = new Command(name.toUpperCase(), code);
			reserveTable.add(command);
		}
		else {
			System.out.println("IndexOutOfBounds Error.");
			return -1;
		}
		
		return elementsInUse;
	}

	/* Given the name of a command in reserveTable, look through every iteration until a match has been found while ignoring case sensitivity
	 * Once the command has been found, return that commands code number. If the command name does not exist in reserveTable, return -1
	 */
	public int LookupName(String name) {
		for(int i = 0; i <= elementsInUse; i++) {
			if(reserveTable.get(i).GetName().compareToIgnoreCase(name) == 0) {
				return reserveTable.get(i).GetCode();
			}
		}
		
		return -1;
	}

	/* Given the code number of a command in reserveTable, look through every commands code number in reserveTable until a match has been found
	 * Once the command has been found, return that commands name. If the code does not exist in reserveTable, return an empty string ""
	 */
	public String LookupCode(int code) {
		for(int i = 0; i <= elementsInUse; i++) {
			if(reserveTable.get(i).GetCode() == code) {
				return reserveTable.get(i).GetName();
			}
		}
		
		return "";
	}
	
	/* Writes all of the data from reserveTable into a file "filename".
	 * All contents are formatted via the pad function.
	 */
	public void PrintReserveTable(String filename) {
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.write(pad(pad("Index",3,true),8,false)+pad(pad("Name",3,true),8,false)+pad(pad("Code",3,true),8,false)+"\n");
			for(int i = 0; i <= elementsInUse; i++) {
				myWriter.write(pad(pad(Integer.toString(i),3,true),8,false)+pad(pad(reserveTable.get(i).GetName(),3,true),8,false)+pad(pad(Integer.toString(reserveTable.get(i).GetCode()),3,true),8,false)+"\n");
				
			}
			
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 
	//Padding size is determined by len and padding direction determined by left (true = left, false = right). Returns the padded string
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

/* Command class object contains string name and int code. Constructor saves passed in data to name and code respectively. 
 * Getter methods are used to return their respective data (GetName and GetCode)
 */
class Command{
	String name;
	int code;
	
	public Command(String s, int i) {
		name = s;
		code = i;
	}
	
	public String GetName() {
		return name;
	}
	
	public int GetCode() {
		return code;
	}
}
