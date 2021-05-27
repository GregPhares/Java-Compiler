
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Lexical {
	private char currentChar;
	private boolean EOF;
	private boolean echo;
	private boolean inComment = false;
	private boolean lineNeeded;
	private int lineCount;
	private int index;
	private int lineCounter; //Not sure if this is needed
	private final int IDENT_ID = 50;
	private final int INTEGER_ID = 51;
	private final int FLOAT_ID = 52;
	private final int STRING_ID= 53;
	private String readLine;
	private String filename;
	private ReserveTable reserveWords = new ReserveTable(50);
	private ReserveTable mnemonics = new ReserveTable(55);
	private SymbolTable symbolStorage;
	private BufferedReader fileRead;
	private FileReader filereader;
	private File file;
	private final int NOTFOUND_CHAR =99;
	
	//Constructor - initializing BufferReader
	public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
		symbolStorage = symbols;
		echo = echoOn;
		index = -1;
		lineNeeded = true; 
		initReserved(reserveWords);
		initMnemonics(mnemonics);
		
		try {
			file = new File(filename);
			filereader = new FileReader(file);
			fileRead = new BufferedReader(filereader);
			EOF = false;
			currentChar = GetNextChar();
		}catch(IOException e) {
			EOF = true;
			e.printStackTrace();
		}
	}
	
	//initialize the Mnemonic Reserve Table
	private void initMnemonics(ReserveTable table) {
		table.Add("GOTO", 0);
		table.Add("INTG", 1);
		table.Add("_TO_",2);
		table.Add("_DO_", 3);
		table.Add("_IF_", 4);
		table.Add("THEN",5);
		table.Add("ELSE",6);
		table.Add("FOR_",7);
		table.Add("_OF_",8);
		table.Add("WRTN",9);
		table.Add("READ",10);
		table.Add("BGIN",11);
		table.Add("END_",12);
		table.Add("VAR_",13);
		table.Add("WHLE",14);
		table.Add("MDLE",15);
		table.Add("LABL",16);
		table.Add("REPT",17);
		table.Add("UNTL",18);
		table.Add("PROC",19);
		table.Add("DOWN",20);
		table.Add("FUNC",21);
		table.Add("RETN",22);
		table.Add("FLOT",23);
		table.Add("STRG",24);
		table.Add("ARRY",25);
		table.Add("DIVD",30);
		table.Add("MULT",31);
		table.Add("PLUS",32);
		table.Add("MINS",33);
		table.Add("LPAR",34);
		table.Add("RPAR",35);
		table.Add("SEMI",36);
		table.Add("ASGN",37);
		table.Add("GRTR",38);
		table.Add("LSTN",39);
		table.Add("GREQ",40);
		table.Add("LEEQ",41);
		table.Add("EQLS",42);
		table.Add("NEQL",43);
		table.Add("COMA",44);
		table.Add("LBRK",45);
		table.Add("RBRK",46);
		table.Add("COLN",47);
		table.Add("PERD",48);
		table.Add("IDNT", 50 );
		table.Add("ICNS", 51);
		table.Add("FCNS", 52);
		table.Add("SCNS", 53);
		table.Add("UNKN", 99);
	}
	
	//Initializing the reserved word ReserveTable
	private void initReserved(ReserveTable table) {
		table.Add("GOTO", 0);
		table.Add("INTEGER", 1);
		table.Add("TO", 2);
		table.Add("DO", 3);
		table.Add("IF", 4);
		table.Add("THEN", 5);
		table.Add("ELSE",6);
		table.Add("FOR",7);
		table.Add("OF",8);
		table.Add("WRITELN",9);
		table.Add("READLN",10);
		table.Add("BEGIN", 11);
		table.Add("END",12);
		table.Add("VAR",13);
		table.Add("WHILE", 14);
		table.Add("MODULE",15);
		table.Add("LABEL",16);
		table.Add("REPEAT",17);
		table.Add("UNTIL",18);
		table.Add("PROCEDURE", 19);
		table.Add("DOWNTO", 20);
		table.Add("FUNCTION", 21);
		table.Add("RETURN",22);
		table.Add("FLOAT",23);
		table.Add("STRING",24);
		table.Add("ARRAY",25);
		table.Add("/",30);
		table.Add("*",31);
		table.Add("+",32);
		table.Add("-",33);
		table.Add("(",34);
		table.Add(")",35);
		table.Add(";",36);
		table.Add(":=",37);
		table.Add(">",38);
		table.Add("<",39);
		table.Add(">=",40);
		table.Add("<=",41);
		table.Add("=",42);
		table.Add("<>",43);
		table.Add(",",44);
		table.Add("[",45);
		table.Add("]",46);
		table.Add(":",47);
		table.Add(".",48);
	}
	
	//Gets a new token each time call, finding out how it should be built
	public token GetNextToken() {
		token result = new token();
	
		//Skip the whitespace
		skipWhiteSpace();
		
		//Get correct token by catagory
		if(isLetter()) {
			result = getIdent();
		}else {
			if(isDigit()) {
			result = getNumber();
			}else {
				if(isStringStart()) {
					result = getString();
				}else {
					result = GetOneTwoChar();
			}
		  }
		}
		
		if((result.lexeme.equals(""))||(EOF)) {
			result= null;
		}
		if(result !=null) {
			result.mnemonic = mnemonics.LookupCode(result.code);
			truncate(result);
		}
		return result;
	}
	
	//Looks for any symbol character or combination of 2 symnbol characters from the ReserveTable
	private token GetOneTwoChar() {
        token result = new token();
        result.lexeme = "" + currentChar;
        //Looking for 2 character combos
        if (currentChar == ':' && PeekNextChar() == '=') {
                currentChar = GetNextChar();
                result.lexeme = result.lexeme + currentChar;
        }else if (currentChar == '<' && (PeekNextChar() == '>' || PeekNextChar() == '=')) {
        	currentChar = GetNextChar();
            result.lexeme = result.lexeme + currentChar;
        }
        
        else if (currentChar == '>' && PeekNextChar() == '=') {
        	currentChar = GetNextChar();
                result.lexeme = result.lexeme + currentChar;
        }

        //Gets all single character symbols
        result.code = reserveWords.LookupName(result.lexeme);
        result.mnemonic = mnemonics.LookupCode(result.code);

        if (result.code == ReserveTable.notFound) {
            result.code = NOTFOUND_CHAR;
            result.mnemonic = mnemonics.LookupCode(result.code);
        }
        
        currentChar = GetNextChar();
        return result;
    }
	
	//Gets numbers int, float, and othe forms with E
	public token getNumber() {
			token result = new token();
			boolean isfloat = false;
			result.lexeme = ""+currentChar;
			
			//Looks for Integers
			if(isDigit()) {
				currentChar = GetNextChar();
				while((isDigit())) {
				result.lexeme = result.lexeme+currentChar;
				currentChar = GetNextChar();
				}
			}
			//Looks for a decimal and adds
			if(currentChar == '.') {
				result.lexeme = result.lexeme+currentChar;
				isfloat = true;
				currentChar = GetNextChar();
				while(isDigit()) {
					result.lexeme = result.lexeme+currentChar;
					currentChar = GetNextChar();
				}
				//Looks for if ends with E or e
				if((currentChar == 'e')||(currentChar =='E')) {
					result.lexeme = result.lexeme +currentChar;
					currentChar = GetNextChar();
					if(currentChar == '+' ||currentChar =='-') {
						result.lexeme = result.lexeme + currentChar;
						currentChar = GetNextChar();
					}
					while(isDigit()) {
						result.lexeme = result.lexeme+currentChar;
						currentChar = GetNextChar();
					}
				}
			}
			
			//Double check to see if its double and if there are any errors
			if(isfloat) {
				result.code = FLOAT_ID;
				result.mnemonic = "FLOT";
				if(doubleOK(result.lexeme)) {
					symbolStorage.AddSymbol(result.lexeme,'f',Double.parseDouble(result.lexeme));
				}
			} 
			// Double checks to see if it is an integer and if there are any errors
			else {
				result.code = INTEGER_ID;
				result.mnemonic = "INTG";
				//try {
				if(integerOK(result.lexeme)) {
					symbolStorage.AddSymbol(result.lexeme, 'i', Integer.parseInt(result.lexeme));
				}
			}
			return result;
	}
	
	//Gets a string 
	public token getString() {

		token result = new token();
		currentChar = GetNextChar();
		result.lexeme = ""+currentChar;
		boolean unfound = false;
		while((currentChar !='"')&&(currentChar !='\n')&&(!EOF)) {
			currentChar = GetNextChar();
		}
		
			if(EOF ) {
				unfound =true;
				System.out.println("Error! Warning the file has ended!");
			}
			
			if(currentChar == '\n') {
				unfound = true;
				System.out.println("String cant go past this line");
			}
			currentChar = GetNextChar();
//			if(currentChar != '"') {
//				result.lexeme += currentChar;
//			}
			
		
		
		if(unfound) {
			result.mnemonic = "UNKN";
			result.code = 99;
		}else {
			result.mnemonic = "SCNS";
			result.code = 53;
			symbolStorage.AddSymbol(result.lexeme,'c',result.lexeme);
		}
		
		return result;
	}
	
	
	public token getIdent() {
		  //int lookup = IDENT;
	      token result = new token();
	      result.lexeme = "" + currentChar; //have the first char
	      currentChar = GetNextChar();
	      while (isLetter()||(isDigit()||(currentChar == '$')||(currentChar=='_'))) {
	         result.lexeme = result.lexeme + currentChar; //extend lexeme
	         currentChar = GetNextChar();
	      }
	      // end of token, lookup or IDENT                
	      result.code = reserveWords.LookupName(result.lexeme);
	      if (result.code == ReserveTable.notFound) {
	          result.code = IDENT_ID;
	          result.mnemonic = "IDNT";
	      }else {
	    	  result.mnemonic=  mnemonics.LookupCode(result.code);
	      }
	      return result;
	}
		
	//Gets the next line from the source code
	private void GetNextLine() {
	    try {
	         readLine = fileRead.readLine();
	         if ((readLine != null) && (echo)) {
	              lineCount++;
	              //System.out.println("Error! The file has ended, there are no more lines to be read"); change this location
	              System.out.println(String.format("%04d", lineCount) + " " + readLine);
	         	}
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        if (readLine == null) {
	            EOF = true;
	        }
	    index = -1;
	    lineNeeded = false;
	    //the line is ready for the next call to get a character
	}
		
	//Gets the next character from the line that scanned
	public char GetNextChar() {
		char result;
		if (lineNeeded) //ran out last time we got a char, so get a new line
		{
		     GetNextLine();
		}
		        //try to get char from line buff
		if (EOF) {
		     result = '\n';
		     lineNeeded = false;
		} else {
		     if ((index < readLine.length() - 1)) { //have a character available
		            index++;
		            result = readLine.charAt(index);
		     } else { //need a new line, but want to return eoln on this call first
		            result = '\n';
		            lineNeeded = true; //will read a new line on next GetNextChar call
		     }
		  }
		return result;
	}
	
	//Checks to see if there is white space in the file
	public boolean isWhiteSpace() {
		boolean result;
		if((currentChar ==' '|| currentChar=='\n'|| currentChar =='\t') && (!EOF)) {
			result = true;
		}else {
			result = false;
		}
		return result;
	}

	//Checks to see if the string started with "
	public boolean isStringStart() {
		return (currentChar == '"');
	}

	//Checks to see if character is a number
	public boolean isDigit() {
		boolean result;
		if(currentChar >= '0' && currentChar <= '9') {
			result = true;
		}
		else {
			result = false;
		}
		return result;
	}
	
	//Checks to see if the character is from the alphabet
	public boolean isLetter() {
		boolean result;
		if((currentChar >= 'a' && currentChar <='z') || (currentChar >= 'A' && currentChar <='Z')) {
			result = true;
		}else {
			result = false;
		}
		return result;
	}
	
	//Boolean for end of the file
	public boolean endOfFile() {
		return EOF;
	}
	
	//Checks to see if the line has ended
	public boolean endOfLine() {
		boolean result;
		if(index >= readLine.length()) {
			result = true;
		}else {
			result = false;
		}
		return result;
	}
	
	//Looks at the next character past the current scanned in
	private char PeekNextChar() {
	    char result = 'x';
	    if ((lineNeeded) || (EOF)){     
	          result = ' '; //at end of line, so nothing
	    } else{
	    		int length = readLine.length();
	           if ((index + 1) < (readLine.length())) { //have one 
	            result = readLine.charAt(index + 1);    
	          }  
	     }       
	    return result;    
	}       

	//Method that skips the white space and is not scanned in
	public void skipWhiteSpace() {
		char PeekStorage;
		if(isWhiteSpace()) {
			currentChar = GetNextChar();
			skipWhiteSpace();
		}
		
		if(currentChar=='{') {
			while(currentChar!='}') {
				currentChar = GetNextChar();
				if(EOF) {
					System.out.println("Error! The file has ended!");
					break;
				}
			}
			if(currentChar=='}') {
				currentChar=GetNextChar();
				skipWhiteSpace();
			}
		}
		
		if(currentChar=='(') {
			PeekStorage=PeekNextChar();
			if(PeekStorage=='*') {
				currentChar=GetNextChar();
				currentChar = GetNextChar();
			
			
			while((currentChar!='*')&&(PeekNextChar()!=')' && !EOF) ) {
				currentChar=GetNextChar();
				if(EOF) {
					System.out.println("Error! The file has ended!");
					break;
				}
			}
			if(currentChar=='*') {
				currentChar = GetNextChar();
				if(currentChar == ')') {
					currentChar = GetNextChar();
					skipWhiteSpace();
				}
			}
		}
	  }
	}
	
	//Checks the quality of the double 
	public boolean doubleOK(String sting) {
		boolean result;
		Double num;
		try {
			num = Double.parseDouble(sting);
			result= true;
		}
		catch(NumberFormatException e) {
			result= false;
		}
		return result;
	}
	
	//Checks the quality of the integer
	public boolean integerOK(String sting) {
		boolean result;
		int num;
		try {
			num= Integer.parseInt(sting);
			result = true;
		}catch(NumberFormatException e) {
			result = false;
		}
		return result;
	}
	
	//shorten Ident, integers and doubles
	public token truncate(token result) {
		int storeInt = 0;
		double storeDble = 0.0;
		int lexLeng = result.lexeme.length();
		String lexemetrunc= result.lexeme;
		
		switch(result.code) 
		{
		case IDENT_ID: if (lexLeng >20) {
							lexemetrunc = result.lexeme.substring(0,20);
							System.out.println("The Identifer is greater than 20 char, it is being truncated "+result.lexeme+"to "+lexemetrunc);
		
							}
							symbolStorage.AddSymbol(lexemetrunc, 'v', 0);
							break;
							
		case INTEGER_ID: if(lexLeng >6) {
							lexemetrunc = result.lexeme.substring(0,6);
							System.out.println("The Integer is greater than 6 char, it is being truncated "+result.lexeme+"to "+lexemetrunc);
							storeInt = 0;
						}else {
								if(integerOK(result.lexeme)) {
								storeInt = Integer.valueOf(lexemetrunc);
								} else  {
								System.out.println("Error! Invalid Integer value");
							}}
						symbolStorage.AddSymbol(lexemetrunc, 'c', storeInt);
						break;
							
		case FLOAT_ID: if(lexLeng > 12) {
							lexemetrunc = result.lexeme.substring(0,12);
							System.out.println("The Float is greater than 12 char, it is being truncated "+result.lexeme+"to "+lexemetrunc);
							storeDble = 0;
						}else {
								if(doubleOK(result.lexeme)) {
								storeDble = Double.valueOf(lexemetrunc);
								}else {
								System.out.println("Error! Invalid Float value");
							}}
						symbolStorage.AddSymbol(result.lexeme, 'c', storeDble);
						break;
		case STRING_ID: symbolStorage.AddSymbol(result.lexeme, 'c', result.lexeme);
						break;
		
		default: break;
							
		}
		return result;	
	}

	//Create the object of the token
	public class token{
		public String lexeme;
		public int code;
		public String mnemonic;
		token(){
			lexeme = "";
			code = 0;
			mnemonic = ""; 
		}
	}
}
