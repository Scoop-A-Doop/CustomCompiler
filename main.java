package project5;
//import ADT.SymbolTable;
//import ADT.Lexical;
import ADT.*;
/**
*
* @author abrouill SPRING 2023
*/
public class main {
	public static void main(String[] args) {
		String filePath = "BadSyntax-1-A-SP23.txt";
		System.out.println("Code Generation SP2023, by Suleyman Shouib");
		System.out.println("Parsing "+filePath);
		boolean traceon = true; //false;
		Syntactic parser = new Syntactic(filePath, traceon);
		parser.parse();
		System.out.println("Done.");
	}
}