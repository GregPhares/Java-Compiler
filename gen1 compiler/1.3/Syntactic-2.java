
public class Syntactic {

    private String filein;              //The full file path to input file
    private SymbolTable symbolList;     //Symbol table storing ident/const
    private Lexical lex;                //Lexical analyzer 
    private Lexical.token token;        //Next Token retrieved 
    private boolean traceon;            //Controls tracing mode 
    private int level = 0;              //Controls indent for trace mode
    private boolean anyErrors;          //Set TRUE if an error happens 

    private final int symbolSize = 250;

    public Syntactic(String filename, boolean traceOn) {
        filein = filename;
        traceon = traceOn;
        symbolList = new SymbolTable(symbolSize);
        lex = new Lexical(filein, symbolList, true);
        lex.setPrintToken(traceOn);
        anyErrors = false;
    }

//Interface to the syntax analyzer, initiates parsing    
    public void parse() {
        int recur = 0;
// prime the pump
        token = lex.GetNextToken();
// csll PROGRAM
        recur = Program();
    }

//NT     
    private int ProgIdentifier() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        // This non-term is used to uniquely mark the program identifier
        if (token.code == lex.codeFor("IDNT")) {
            // Because this is the progIdentifier, it will get a 'p' type to prevent re-use as a var
            symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'p', 0);
            //move on
            recur= Identifier();
        }
        return recur;
    }

//NT
    private int Program() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Program", true);
        if (token.code == lex.codeFor("MODL")) {
            token = lex.GetNextToken();
            recur = ProgIdentifier();
            if (token.code == lex.codeFor("SEMI")) {
                token = lex.GetNextToken();
                recur = Block();
                if (token.code == lex.codeFor("PERD")) {
                    if (!anyErrors) {
                        System.out.println("Success.");
                    } else {
                        System.out.println("Compilation failed.");
                    }
                } else {
                    error(lex.reserveFor("PERD"), token.lexeme);
                }
            } else {
                error(lex.reserveFor("SEMI"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("MODL"), token.lexeme);
        }
        trace("Program", false);
        return recur;
    }

//NT    
    private int Block() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Block", true);

        if (token.code == lex.codeFor("BGIN")) {
            token = lex.GetNextToken();
            recur = Statement();
            while ((token.code == lex.codeFor("SEMI")) 
            		&& (!lex.EOF()) 
            		&& (!anyErrors)) {
                token = lex.GetNextToken();
                recur = Statement();
            }
            if (token.code == lex.codeFor("END_")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("END_"), token.lexeme);
            }

        } else {
            error(lex.reserveFor("BGIN"), token.lexeme);
        }

        trace("Block", false);
        return recur;
    }
    
    
    private int Variable() {
    	int recur = 0;
        if (anyErrors) {
            return -1;
        }
    	trace("Variable",true);
    	recur = Identifier();
    	trace("Variable",false);
    	return recur;
    }

//Not a NT, but used to shorten Statement code body   
    //<variable> $COLON-EQUALS <simple expression>
    private int handleAssignment() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleAssignment", true);
        //have ident already in order to get to here, handle as Variable
        recur = Variable();
       // token = lex.GetNextToken();
        if (token.code == lex.codeFor("ASGN")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
        } 
        else {
            error(lex.reserveFor("ASGN"), token.lexeme);
        }

        trace("handleAssignment", false);
        return recur;
    }

// NT This is dummied in to only work for an identifier.  MUST BE 
//  COMPLETED TO IMPLEMENT CFG <simple expression>
    private int SimpleExpression() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("SimpleExpression", true);

        if ((token.code == lex.codeFor("PLUS"))
        		||(token.code == lex.codeFor("MINS"))) {
        	recur=Sign();
        }
        
        recur = Term();
        if (token.code == lex.codeFor("IDNT")) {
            token = lex.GetNextToken();
        }
        
        while((token.code == lex.codeFor("PLUS"))
        		||(token.code == lex.codeFor("MINS"))
        		&&(!lex.EOF())
        		&&(!anyErrors)) {
        	recur = Addop();
        	recur = Term();
        }
        trace("SimpleExpression", false);
        return recur;
    }

  
    private int Statement() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Statement", true);

        if (token.code == lex.codeFor("IDNT")) {  //must be an ASSUGNMENT
            recur = handleAssignment();
        } else {
            if (token.code == lex.codeFor("_IF_")) {  //must be an ASSUGNMENT
                // this would handle the rest of the IF statment IN PART B
            } else // if/elses should look for the other possible statement starts...  
            //  but not until PART B
            {
                error("Statement start", token.lexeme);
            }
        }

        trace("Statement", false);
        return recur;
    }

// error provides a simple way to print an error statement to standard output
//     and avoid reduncancy
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
        } else {
            if (level > 0) {
                level--;
            }
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("<-- Exiting " + proc);
        }
    }

// repeatChar returns a string containing x repetitions of string s; 
//    nice for making a varying indent format
    private String repeatChar(String s, int x) {
        int i;
        String result = "";
        for (i = 1; i <= x; i++) {
            result = result + s;
        }
        return result;
    }


    private int Term() {
    	int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Term", true);
            recur = Factor();
            while((token.code == lex.codeFor("MULT"))
            		||(token.code == lex.codeFor("DIVD"))
            		&&(!lex.EOF())
            		&&(!anyErrors)) {
            	recur = Mulop();
            	recur = Factor();
            }
        trace("Term", false);
        return recur;
    }
    
    private int Factor() {
       	int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Factor", true);
        //token = lex.GetNextToken();
        if ((token.code == lex.codeFor("ICNS"))
        		||(token.code == lex.codeFor("FCNS"))) {
            recur=unsignedConstant();
        }else if ((token.code == lex.codeFor("IDNT"))) {
        	recur= Variable();
        }else if((token.code == lex.codeFor("LPAR"))) {
        	token = lex.GetNextToken();
        	recur= SimpleExpression();
        	
        	if((token.code != lex.codeFor("RPAR"))) {
        		error(lex.reserveFor("RPAR"), token.lexeme);
        		return -1;
        	}
        	token = lex.GetNextToken();
        }else {
        	error("Number, Variable or '('", token.lexeme);
        }
        trace("Factor", false);
        
        return recur;
    }
    
    private int Sign() {
    	int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Sign", true);
        
            token = lex.GetNextToken();
        trace("Sign", false);
        return recur;
    }
    
    private int Addop() {
    	int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Addop", true);
            token = lex.GetNextToken();
        trace("Addop", false);
        return recur;
    }
    
    private int Mulop() {
    	int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Mulop", true);
        // Add a cases in theses see if it fixes them....
            token = lex.GetNextToken();
        trace("Mulop", false);
        return recur;
    }
    
   private int unsignedConstant() {
	   int recur = 0;
	   if(anyErrors) {
		   return -1;
	   }
	   trace("UnsignedConstant", true);
	   recur = unsignedNumber();
	   trace("UnsignedConstant", false);
	   return recur;
   }
   
   private int unsignedNumber() {
	   int recur = 0;
	   if(anyErrors) {
		   return -1;
	   }
	   trace("UnsignedNumber", true);
	   token = lex.GetNextToken();
	   trace("UnsignedNumber", false);
	   return 0;
   }
   
   private int Identifier() {
	   int incur = 0;
	   if(anyErrors) {
		   return -1;
	   }
	   token = lex.GetNextToken();
	   return incur;
   }
    



}
