

import SymbolTable;
import Lexical;

public class main{

    public static void main(String[] args) {
        String filePath = "C:\\WorkSpace\\project1a\\"; 
        SymbolTable symbolList;
        symbolList = new SymbolTable(100);
        Lexical myLexer = new Lexical(filePath+"LexicalTestSP21.txt", symbolList, true);  
        //Lexical myLexer = new Lexical(filePath+"tester.txt", symbolList, true);   
        //Lexical myLexer = new Lexical(filePath+"longnum.txt", symbolList, true); 
        Lexical.token t;
        t = myLexer.GetNextToken();
        while (t != null) {
          System.out.println("\t"+t.mnemonic+" | \t"+String.format("%04d",t.code)+" | \t"+t.lexeme);
          t = myLexer.GetNextToken();      
        }
        symbolList.PrintSymbolTable(filePath+"symbolTable2.txt");
        System.out.println("Done.");
    }
    
}