

public class Syntactic {
    
    private String filein;              //The full file path to input file
    private SymbolTable symbolList;     //Symbol table storing ident/const
    private Lexical lex;                //Lexical analyzer 
    private Lexical.token token;        //Next Token retrieved 
    private boolean traceon;            //Controls tracing mode
    private boolean anyErrors;          //Set TRUE if an error happens
    private int level = 0;				//Controls indent for trace mode
    
    // Allows to shift the index up or down on the symbol table
    private int indexUp;             
    private int indexDown;    
    
    // Creating an instance of quad table for updating and Interpreter of opcode lookups
    private QuadTable myQuads;            
    private Interpreter myInterpreter;         

    //Initializing sizes for the 
    private final int symbolSize = 250; 
    private final int myQuadsize = 1000;  
    
    //Increment Counter
    private int increment = 0;          
    
    public Syntactic(String filename, boolean traceOn) {
        //Given Stuff from file
    	filein = filename;
        traceon = traceOn;
        symbolList = new SymbolTable(symbolSize);
        lex = new Lexical(filein, symbolList, true);
        lex.setPrintToken(traceOn);
        anyErrors = false;
        
        // Allows to shift the index up or down on the symbol table
        indexUp = symbolList.AddSymbol("1", 'c',1);    
        indexDown = symbolList.AddSymbol("-1", 'c',-1); 
        
        // Initializing the quad table and interpreter
        myQuads = new QuadTable(myQuadsize);                 
        myInterpreter = new Interpreter();                      
        

    }

//Interface to the syntax analyzer, initiates parsing    
public void parse() {
    String filenameBase = filein.substring(0, filein.length() - 4);
    System.out.println(filenameBase);
    int recur = 0;
    // prime the pump
    token = lex.GetNextToken();
    // call PROGRAM
    recur = Program();

    // This will put the final stop in the quad
    myQuads.AddQuad(myInterpreter.opcodeFor("STOP"), 0, 0, 0);

    //Prints out the st and quad prior to exe
    symbolList.PrintSymbolTable(filenameBase + "ST-before.txt");
    myQuads.PrintQuadTable(filenameBase + "myQuads.txt");
    
    //Print out for interpret
    myInterpreter.InterpretQuads(myQuads, symbolList, false, filenameBase + "TRACE.txt");
    symbolList.PrintSymbolTable(filenameBase + "ST-after.txt");

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


    private int Block() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Block", true);
        //Label declaration section
        if (token.code == lex.codeFor("LABL")) {
            //don't advance, call label declaration section
            recur = LabelDeclaration();
        }
        // Variable declaration section
        while (token.code == lex.codeFor("VAR_")) {
            //don't advance, call var declaration section
            recur = Variabledecsec();
        }
        
        recur = Blockbody();
        trace("Block", false);
        return recur;
    }

    private int Blockbody() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Blockbody", true);
        
        if (token.code == lex.codeFor("BGIN")) {
            token = lex.GetNextToken();
            recur = Statement();
            while ((token.code == lex.codeFor("SEMI")) && (!lex.EOF()) && (!anyErrors)) {
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
        
        trace("Blockbody", false);
        return recur;
    }
    
    private int LabelDeclaration() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("LabelDeclaration", true);
        if (token.code == lex.codeFor("LABL")) {
            //advance, get series of idents
            token = lex.GetNextToken();
            if (token.code == lex.codeFor("IDNT")) {
                //set the type as LABEL
                symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'l', 0);
                //move on
                token = lex.GetNextToken();
                while ((token.code == lex.codeFor("COMA")) && (!lex.EOF()) && (!anyErrors)) {
                    //move past comma
                    token = lex.GetNextToken();
                    if (token.code == lex.codeFor("IDNT")) {
                        //set the type as LABEL
                        symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'l', 0);
                        //move on
                        token = lex.GetNextToken();
                        
                    } else {
                        error(lex.reserveFor("IDNT"), token.lexeme);
                    }
                    // end of the sequence, so take the semi
                } // while
                if (token.code == lex.codeFor("SEMI")) {
                    //move on
                    token = lex.GetNextToken();
                } else {
                    error(lex.reserveFor("SEMI"), token.lexeme);
                }
                
            } else {
                error(lex.reserveFor("IDNT"), token.lexeme);
            }
        }

        trace("LabelDeclaration", false);
        return recur;
        
    }

    
    private int Variabledecsec() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Variabledecsec", true);
        if (token.code == lex.codeFor("VAR_")) {
            //move on
            token = lex.GetNextToken();
            //get ident,ident
            while (token.code == lex.codeFor("IDNT")) {
                //              recur = Variable();

                //move on
                token = lex.GetNextToken();
                while ((token.code == lex.codeFor("COMA")) && (!lex.EOF()) && (!anyErrors)) {
                    //move past comma
                    token = lex.GetNextToken();
                    if (token.code == lex.codeFor("IDNT")) {
                        //set the type as LABEL
                        //symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'l', 0);
                        //move on
                        token = lex.GetNextToken();
                    } else {
                        error(lex.reserveFor("IDNT"), token.lexeme);
                    }
                } // while
                //should be : type ;
                if (token.code == lex.codeFor("COLN")) {
                    //move on
                    token = lex.GetNextToken();
                    recur = Simpletype();
                    // end with ;
                    if (token.code == lex.codeFor("SEMI")) {
                        //move on
                        token = lex.GetNextToken();
                        
                    } else {
                        error(lex.reserveFor("SEMI"), token.lexeme);
                    }
                } else {
                    error(lex.reserveFor("COLN"), token.lexeme);
                }
            } //while
        } // while ident
        else {
            
        }

        trace("variabledecsec", false);
        return recur;
        
    }

    // Checks to see if token is IDNT
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
            token = lex.GetNextToken();
        }
        return recur;
    }
    //Checks for data types of integer and float then gets the token. Will handle errors
    private int Simpletype() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Simpletype", true);
        if ((token.code == lex.codeFor("INTG")) || (token.code == lex.codeFor("FLOT"))) {
            recur = token.code;
            token = lex.GetNextToken();
        } else {
            error(lex.reserveFor("INTG") + " or " + lex.reserveFor("FLOT"), token.lexeme);
        }
        
        trace("Simpletype", false);
        return recur;
    }

//Not a NT, but used to shorten Statement code body   
    //<variable> $COLON-EQUALS <simple expression>
    private int handleAssignment() {
        int recur = 0;
        int storage = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleAssignment", true);
        //have ident already in order to get to here, handle as Variable
        recur = Variable();
        storage = recur; // placement for variable storing
        // token = lex.GetNextToken();
        if (token.code == lex.codeFor("ASGN")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
        } else {
            error(lex.reserveFor("ASGN"), token.lexeme);
        }
        
        trace("handleAssignment", false);
        myQuads.AddQuad(myInterpreter.opcodeFor("MOV"),recur,0, storage);
        return recur;
    }

    /*
    $WHILE <relexpression> $DO <statement> |
     */
    private int handleWhile() {
        int recur = 0;
        int condition = 0;
        int nextQuad = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleWhile", true);
        //got here from a WHILE token, move past it...
        token = lex.GetNextToken();
        nextQuad = myQuads.NextQuad();
        recur = Relexpression();
        condition = recur;
        if (token.code == lex.codeFor("_DO_")) {
            //move on to update the quad table
            token = lex.GetNextToken();
            recur = Statement();
            
            //Adds and updates the quad table
            myQuads.AddQuad(myInterpreter.opcodeFor("BR"),0,0,nextQuad);
            myQuads.UpdateQuad(condition,myQuads.GetQuad(condition,0),myQuads.GetQuad(condition,1),myQuads.GetQuad(condition,2),myQuads.NextQuad());
        } else {
            error(lex.reserveFor("_DO_"), token.lexeme);
        }
        trace("handleWhile", false);
        return recur;
    }

    /*
    $IF <relexpression> $THEN <statement>
[$ELSE <statement>] |
     */
    private int handleIf() {
        int recur = 0;
        int quadBrancher = 0;
        int quadMover = 0;
        if (anyErrors) {
            return -1;
        }

        trace("handleIf", true);
        //got here from an IF token, move past it...
        token = lex.GetNextToken();
        recur = Relexpression();
        quadBrancher = recur;
        if (token.code == lex.codeFor("THEN")) {
            //move on
            token = lex.GetNextToken();
            recur = Statement();
            //check for else part
            if (token.code == lex.codeFor("ELSE")) {
                //move on
                token = lex.GetNextToken();
                quadMover= myQuads.NextQuad();
                
                //Add and update quad table and get next statement
                myQuads.AddQuad(myInterpreter.opcodeFor("BR"),0,0,0);
                myQuads.UpdateQuad(quadBrancher,myQuads.GetQuad(quadBrancher,0),myQuads.GetQuad(quadBrancher,1),myQuads.GetQuad(quadBrancher,2),myQuads.NextQuad());
                recur = Statement();
                myQuads.UpdateQuad(quadMover, myQuads.GetQuad(quadMover, 0), myQuads.GetQuad(quadMover, 1), myQuads.GetQuad(quadMover, 2), myQuads.NextQuad());
            } //missing ELSE is ok, do nothing
            else {
                myQuads.UpdateQuad(quadBrancher, myQuads.GetQuad(quadBrancher, 0), myQuads.GetQuad(quadBrancher, 1), myQuads.GetQuad(quadBrancher, 2), myQuads.NextQuad());
            }
        } else {
            error(lex.reserveFor("THEN"), token.lexeme);
        }

        trace("handleIf", false);
        return recur;
    }

    /*
    $FOR <variable> $ASSIGN <simple expression>
$TO <simple expression> $DO <statement> |
     */
    private int handleFor() {
        int recur = 0;
        int index = 0;
        int symbStorage = 0;
        int jump = 0;
        int var;
        int exp1;
        int exp2;
        if (anyErrors) {
            return -1;
        }
        
        trace("handleFor", true);
        //got here from an FOR token, move past it...
        token = lex.GetNextToken();
        var = Variable();
        //handling the assign :=
        if (token.code == lex.codeFor("ASGN")) {
            //move on
            token = lex.GetNextToken();
            exp1 = SimpleExpression();
            myQuads.AddQuad(myInterpreter.opcodeFor("MOV"),exp1,0,var);
            
            // handling TO, will add and update quads and move the index
            if (token.code == lex.codeFor("_TO_")) {
                //move on
                token = lex.GetNextToken();
                exp2 = SimpleExpression();
                symbStorage = symbolList.AddSymbol("@" + increment++, 'v',0);
                jump = myQuads.NextQuad();
                myQuads.AddQuad(myInterpreter.opcodeFor("SUB"),var,exp2,symbStorage);
                index = myQuads.NextQuad();
                myQuads.AddQuad(myInterpreter.opcodeFor("BP"),symbStorage,0,0);
                
                // handling DO, calling recursion and adding to the quad table
                if (token.code == lex.codeFor("_DO_")) {
                    //move on
                    token = lex.GetNextToken();
                    recur = Statement();
                    myQuads.AddQuad(myInterpreter.opcodeFor("ADD"),var,indexUp,var);
                    myQuads.AddQuad(myInterpreter.opcodeFor("BR"),0,0,jump);

                } else {
                    error(lex.reserveFor("_DO_"), token.lexeme);
                }
            } else {
                error(lex.reserveFor("_TO_"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("ASGN"), token.lexeme);
        }
        myQuads.UpdateQuad(index,myQuads.GetQuad(index,0),myQuads.GetQuad(index,1),myQuads.GetQuad(index,2),myQuads.NextQuad());

        trace("handleFor", false);
        return recur;
        
    }
    
    private int handleGoto() {
        int recur = 0;
        int labelindex = -1;
        if (anyErrors) {
            return -1;
        }
        
        trace("handleGoto", true);
        //got here from a GOTO token, move past it...
        token = lex.GetNextToken();
        if (token.code == lex.codeFor("IDNT")) {
            //check if target is a label
            if (symbolList.GetKind(symbolList.LookupSymbol(token.lexeme)) == 'l') {
                //generate jump in code gen
                labelindex = symbolList.LookupSymbol(token.lexeme);
                myQuads.AddQuad(myInterpreter.opcodeFor("BINDR"),0,0,labelindex);
            } else {
                error("Declared GOTO Label", token.lexeme);
            }

            //move on
            token = lex.GetNextToken();
        } else {
            error("Declared GOTO Label", token.lexeme);
        }
        
        trace("handleGoto", false);
        return recur;
        
    }

    private int handleWriteln() {
        int recur = 0;
        int toprint = 0;
        if (anyErrors) {
            return -1;
        }

        trace("handleWriteln", true);
        //got here from a WRITELN token, move past it...
        token = lex.GetNextToken();
        //look for ( stringconst, ident, simpleexp )
        if (token.code == lex.codeFor("LPAR")) {
            //move on
            token = lex.GetNextToken();
            if ((token.code == lex.codeFor("SCNS")))
            {
                toprint = symbolList.LookupSymbol(token.lexeme);
                //move on
                token = lex.GetNextToken();
            } else {
                toprint = SimpleExpression();
            }
            myQuads.AddQuad(myInterpreter.opcodeFor("PRINT"), toprint, 0, 0);
            //now need right ")"
            if (token.code == lex.codeFor("RPAR")) {
                //move on
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("RPAR"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("LPAR"), token.lexeme);
        }
        // end lpar group

        trace("handleWriteln", false);
        return recur;

    }

//NT    
    private int Statement() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Statement", true);
        //Collecting labels which precede a statement
        while ((token.code == lex.codeFor("IDNT"))
                && (symbolList.GetKind(symbolList.LookupSymbol(token.lexeme)) == 'l')) {  //must be a LABEL)
            // Codegen will assign nextQuad to the value of ident
        	
            symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme),'l',myQuads.NextQuad());
            token = lex.GetNextToken();
            if (token.code == lex.codeFor("COLN")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("COLN" + " after LABEL"), token.lexeme);
            }
            
        } //while labels found
        if (token.code == lex.codeFor("IDNT")) {  //must be an ASSUGNMENT
            recur = handleAssignment();
        } else {
            if (token.code == lex.codeFor("WHLE")) {  //must be a WHILE
                // this will handle the rest of the WHILE statment 
                handleWhile();
            } else {
                if (token.code == lex.codeFor("_IF_")) {  //must be an IF
                    // this will handle the rest of the IF statment 
                    handleIf();
                } else {
                    if (token.code == lex.codeFor("BGIN")) {  //must be a BLOCK 
                        // this will handle the rest of the WHILE statment 
                        Blockbody();
                    } else {
                        if (token.code == lex.codeFor("FOR_")) {  //must be FOR
                            // this will handle the rest of the FOR statment 
                            handleFor();
                        } else {
                            if (token.code == lex.codeFor("GOTO")) {  //must be FOR
                                // this will handle the rest of the GOTO statment 
                                handleGoto();
                            } else {
                                if (token.code == lex.codeFor("WRTN")) {  //must be FOR
                                    // this will handle the rest of the WRITELN statment 
                                    handleWriteln();
                                } else {
                                    error("Statement start", token.lexeme);
                                    
                                }
                            }
                        }
                    }
                }
            }
        }
        
        trace("Statement", false);
        return recur;
    }

    /*
    <relexpression> -> <simple expression> <relop> <simple expression>
<relop> -> $EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
     */
    private int Relexpression() {
        int left, right, operator, recur = 0;
        int symbStorage = 0;
        int nextQuad = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Relexpression", true);
        left = SimpleExpression();
        operator = Relop();
        right = SimpleExpression();
        symbStorage = symbolList.AddSymbol("@" + increment++, 'v', 0);
        myQuads.AddQuad(myInterpreter.opcodeFor("SUB"),left, right, symbStorage);
        nextQuad = myQuads.NextQuad();
        myQuads.AddQuad(relopToOpCode(operator), symbStorage,0,0);
        trace("Relexpression", false);
        return nextQuad;
        
    }

    private int relopToOpCode(int op) {
        if(op == lex.codeFor("EQLS")) {
            return myInterpreter.opcodeFor("BNZ");
        }
        else if(op == lex.codeFor("NEQL")) {
            return myInterpreter.opcodeFor("BZ");
        }
        else if(op == lex.codeFor("LESS")) {
            return myInterpreter.opcodeFor("BNN");
        }
        else if(op == lex.codeFor("GRTR")) {
            return myInterpreter.opcodeFor("BNP");
        }
        else if(op == lex.codeFor("LEEQ")) {
            return myInterpreter.opcodeFor("BP");
        }
        else if(op == lex.codeFor("GREQ")) {
            return myInterpreter.opcodeFor("BN");
        }
        return -1;
    }

    private int Relop() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Relop", true);
        // looking for =, <, >, <>, <=, >=
        if ((token.code == lex.codeFor("GRTR"))
                || (token.code == lex.codeFor("LESS"))
                || (token.code == lex.codeFor("GREQ"))
                || (token.code == lex.codeFor("LEEQ"))
                || (token.code == lex.codeFor("EQLS"))
                || (token.code == lex.codeFor("NEQL"))) {
            recur = token.code;
            token = lex.GetNextToken();
        } else {
            error("Relational operator: <, >, >=, <=, <>, = ", token.lexeme);
        }
        trace("Relop", false);
        return recur;
        
    }

//  COMPLETED TO IMPLEMENT CFG <simple expression>
//<simple expression> -> [<sign>] <term> {<addop> <term>}*
    private int SimpleExpression() {
        int recur = 0;
        int left, right, operator, negate = 0;
        int code = 0;
        int symbStorage = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("SimpleExpression", true);
        // check optional sign  
        if (isAddop(token.code)) {
            //eat the sign, will negate in code generation
            negate = Sign();
        }
        left = Term();
        if (negate == -1){
            myQuads.AddQuad(myInterpreter.opcodeFor("MUL"),left,indexDown,left);
        }
        while (isAddop(token.code) && (!lex.EOF()) && (!anyErrors)) {
            operator = Addop();
            right = Term();
            if (operator == lex.codeFor("MINS")){
                code = myInterpreter.opcodeFor("SUB");
            }

            else if (operator == lex.codeFor("PLUS")){
                code = myInterpreter.opcodeFor("ADD");
            }
            symbStorage = symbolList.AddSymbol("@" + increment++,'v',0);
            myQuads.AddQuad(code,left,right,symbStorage);
            left = symbStorage;
        }
        // code gen will set recur to the cumulative result index
        trace("SimpleExpression", false);
        return left;
    }

//<term> -> <factor> {<mulop> <factor> }*
    private int Term() {
        int recur = 0;
        int left, right, operator;
        int code = 0;
        int symbStorage = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Term", true);
        left = Factor();
        while ((isMulop(token.code) && (!lex.EOF()) && (!anyErrors))) {
            operator = Mulop();
            right = Factor();
            if (operator == lex.codeFor("MULT")){
                code = myInterpreter.opcodeFor("MUL");
            }

            else if (operator == lex.codeFor("DIVD")){
                code = myInterpreter.opcodeFor("DIV");
            }
            symbStorage = symbolList.AddSymbol("@" + increment++,'v',0);
            myQuads.AddQuad(code,left,right,symbStorage);
            left = symbStorage;
        }
        trace("Term", false);
        // code gen will set recur to the cumulative result index
        return left;
        
    }
    
    private boolean isMulop(int code) {
        return ((token.code == lex.codeFor("MULT")
                || (token.code == lex.codeFor("DIVD"))));
    }
    
    private boolean isAddop(int code) {
        return ((token.code == lex.codeFor("PLUS")
                || (token.code == lex.codeFor("MINS"))));
    }

//<mulop>    
    private int Mulop() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Mulop", true);
        recur = token.code;
        // move past
        token = lex.GetNextToken();
        
        trace("Mulop", false);
        return recur;
        
    }
//<addop>    

    private int Addop() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Addop", true);
        recur = token.code;
        // move past
        token = lex.GetNextToken();
        
        trace("Addop", false);
        return recur;
    }
//<Sign>    

    private int Sign() {
        int recur = 1;
        if (anyErrors) {
            return -1;
        }
        
        trace("Sign", true);

        // move past
        if (token.code == lex.codeFor("MINS")) {
            recur = -1;
        }
        
        token = lex.GetNextToken();
        
        trace("Sign", false);
        return recur;
    }

//<factor> -> <unsigned constant> | <variable> | $LPAR <simple expression> $RPAR    
    private int Factor() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Factor", true);
        // unsigned constant starts with integer or float number
        if ((token.code == lex.codeFor("ICNS")
                || (token.code == lex.codeFor("FCNS")))) {
            recur = UnsignedConstant();
        } else if ((token.code == lex.codeFor("IDNT"))) {
            recur = Variable();
        } else if ((token.code == lex.codeFor("LPAR"))) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
            if ((token.code == lex.codeFor("RPAR"))) {
                token = lex.GetNextToken();
            } else {
                error("')'", token.lexeme);
            }
        } else {
            error("Number, Variable or '('", token.lexeme);
        }
        
        trace("Factor", false);
        return recur;
        
    }
    
    private int UnsignedConstant() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("UnsignedConstant", true);
        // only accepting a number
        recur = UnsignedNumber();
        trace("UnsignedConstant", false);
        return recur;
    }
    
    private int UnsignedNumber() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("UnsignedNumber", true);
        // float or int or ERROR
        // unsigned constant starts with integer or float number
        if ((token.code == lex.codeFor("ICNS")
                || (token.code == lex.codeFor("FCNS")))) {
            // return the s.t. index 
            recur = symbolList.LookupSymbol(token.lexeme);
            token = lex.GetNextToken();
        } else {
            error("Integer or Floating Point Number", token.lexeme);
        }
        trace("UnsignedNumber", false);
        return recur;
        
    }
    
    private int Variable() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Variable", true);
        if ((token.code == lex.codeFor("IDNT"))) {
            //return the location of this variable for Quad use
            recur = symbolList.LookupSymbol(token.lexeme);
            // bookkeeping and move on
            token = lex.GetNextToken();
        } else {
            error("Variable", token.lexeme);
        }
        
        trace("Variable", false);
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


}
