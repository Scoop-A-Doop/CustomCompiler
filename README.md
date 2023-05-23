# Custom Compiler
This project uses concepts of computer automata to compile and execute simple programs written by the user.

**CLASS EXPLANATIONS**

This project is made up of 6 classes, each class simulating a specific aspect in compiler design. 

**Reserve Table:** Contains a list of reserved words in the language. 
**Lexical:** Using a DFA, the user's code is broken up into several tokens. Tokens are either reserved words, identifier tokens, number tokens, string tokens, or 1-2 char tokens. 
**Symbol Table:** Contains a list of symbols. A symbol can be an identifier token, number token or string token. Once one of these 3 tokens are found in Lexical, they are added to the symbol table. A symbol has a lexeme, data type, usage type and a value.
**Syntactic:** Using a CFG, the code looks through the sequence of tokens to check if the code follows the programs language. If the language is incorrect, appropriate error handling will be conducted to give the user a detailed explanation of what is wrong. 
**Quad Table:** Quad tables are used to take an appropriately defined sequence of tokens, and create instructions for execution. Quad tables are made up of 4 operands. The first operand is the action to be done (i.e Dowhile, If statement, Assignment, Addition etc.). The remaining operands are references to indexes within the symbol table. 
**Interpreter:** The interpreter takes all of the created quad tables and using a program counter, is able to sequentially execute each quad table, moving down and jumping to different instructions when necessary.

**FEATURES**

This compiler is able to compute simple arithmetic relating to parenthesis, multiplication, division, addition, and subtraction. The arithmetic is computed with priority in mind, statements within parenthesis are executed first, then multiplication and/or division is computed next and finally addition and subtraction is performed.

Statements currently implemented are Write Lines, Read Lines, DoWhile statements and If/Else conditional statements.

Relational Expressions include Assignment (:=), Less Than Equal (<=), Greater Than Equal (>=), Less Than (<), Greater Than (>), Not Equal (<>).

Comments are identified using curly braces {like this} or a combination of parenthesis and asteriks (* like this \*). Comments can be multilines.

**HOW TO RUN**

Using the provided executable jar file titled "SHOUIB_COMPILER.jar", you are able to run the provided example program text file titled "Example Program.txt". To do this you must download both files, and in the Windows command line, navigate to the location that contains both the jar file and text file and run "java -jar SHOUIB_COMPILER.jar Example Program.txt". This will compile the program and upon a successful build, execute it. It will then output the functioning code and it will also create 3 files. The first file will be the symbol table prior the execution of all statements, the symbol table after the program executes, and all quad tables generated.
