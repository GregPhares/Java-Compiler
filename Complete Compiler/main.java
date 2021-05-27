
import ADT.SymbolTable;
import ADT.Lexical;
import ADT.*;

public class main {

    public static void main(String[] args) {
    	// String filePath = "C:\\WorkSpace\\Compiler2.0\\SyntaxTestSP21.txt";
    	// String filePath = "C:\\WorkSpace\\Compiler2.0\\SyntaxMinimumTestSP21.txt";
        // String filePath = "C:\\WorkSpace\\Compiler2.0\\CodeGen1-Assign.txt";
        // String filePath = "C:\\WorkSpace\\Compiler2.0\\BadSyntax-1-ASP21.txt";
    	//String filePath = "C:\\WorkSpace\\Compiler2.0\\CodeGen1-Assign.txt";
    	String filePath = "C:\\WorkSpace\\Compiler2.0\\CodeGen4-All.txt";
        System.out.println("Parsing "+filePath);
        boolean traceon = false; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        
        System.out.println("Done.");
    }

}
