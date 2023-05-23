package ADT;

import java.io.FileWriter;
import java.io.IOException;

public class QuadTable {
	private int nextAvailable = 0;	// Keeps track of the next available row of the Quad Table
	int[][] quadTable;	// The Quad Table
	
	// Constructor creates array given maxSize
	public QuadTable(int maxSize){
		quadTable = new int[maxSize][4];
	}
	
	// Returns the next available index (i.e an empty array will return 0 since 0 is the next available index)
	public int NextQuad() {
		
		return nextAvailable;
	}
	
	// Adds opcode, op1, op2, op3 into the next available row of quadTable. Then update nextAvailable
	public void AddQuad(int opcode, int op1, int op2, int op3) {
		quadTable[nextAvailable][0] = opcode;
		quadTable[nextAvailable][1] = op1;
		quadTable[nextAvailable][2] = op2;
		quadTable[nextAvailable][3] = op3;
		nextAvailable++;
	}
	
	// Returns an row given an index of the quad table
	public int[] GetQuad(int index) {
		int[] quadRow = new int[4];
		quadRow[0] = quadTable[index][0];
		quadRow[1] = quadTable[index][1];
		quadRow[2] = quadTable[index][2];
		quadRow[3] = quadTable[index][3];
		
		return quadRow;
	}
	
	// Given an index, update the 3rd operand op3
	public void UpdateJump(int index, int op3) {
		quadTable[index][3] = op3;
	}
	
	// Prints the quad table into the given file name
	public void PrintQuadTable(String filename){
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.write((pad(pad("Index",3,true),8,false)+pad(pad("Opcode",3,true),8,false)
			+pad(pad("Op1",4,true),8,false)+pad(pad("Op2",4,true),8,false)+pad(pad("Op3",4,true),8,false))+"\n");
			
			for(int i = 0; i < nextAvailable; i++) {
				myWriter.write(pad(pad(Integer.toString(i),3,true),7,false)+"|");
				for(int j = 0; j < quadTable[0].length; j++) {
					myWriter.write(pad(pad(Integer.toString(quadTable[i][j]),3,true),7,false)+"|");
				}
				myWriter.write("\n");
			}
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Padding size is determined by len and padding direction determined by left (true = left, false = right). Returns the padded string 
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
