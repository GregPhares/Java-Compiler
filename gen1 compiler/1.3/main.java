

import SymbolTable;
import Lexical;
import *;

public class main {

    public static void main(String[] args) {
       //String filePath = "C:\\WorkSpace\\project1a\\LexicalTestSP21.txt";
        //String filePath = "C:\\WorkSpace\\project1a\\GoodSyntaxASP21.txt";
        //String filePath = "C:\\WorkSpace\\project1a\\BadSyntax-1-ASP21.txt";
        String filePath = "C:\\WorkSpace\\project1a\\BadSyntax-2-ASP21.txt";
        boolean traceon = true;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        System.out.println("Done.");
    }

}
