/* TO STRING CODE
1) FOR SymbolTable rows:
 public String toString(){
        String result;
        String val = "";
        result = name.trim()+'|'+kind+'|'+dataType+'|';
           switch (dataType) {
             case 'i': val = Integer.toString(integerValue);
                   break;
             case 'f': val = Double.toString(floatValue);
                   break;
             case 's': val = stringValue.trim();
                   break;
           }
                   
        return result + val;
    }

2) FOR QuadTable rows:
private String RowToString(int row){
    return Integer.toString(table[row][0]) + '|'+
           Integer.toString(table[row][1]) + '|'+
           Integer.toString(table[row][2]) + '|'+
           Integer.toString(table[row][3]) + '|'; 
}

3) FOR Reserve Table rows:
String ReserveToString(int index) {
    return words[index].trim().toUpperCase()+'|'+Integer.toString(codes[index]);
}        
*/

import SymbolTable;
import QuadTable;
import ReserveTable;


public class main {

    public static void main(String[] args) {
        String filePath = "d:\\"; 
        int indexA, indexB, idx, nameindex, tooMany, testInt;
        int opcode, op1, op2, op3, rcode;
        double testFloat;
        String testString, testSymbol;
        char testKind, testType;
        SymbolTable st = new SymbolTable(6);
        QuadTable qt = new QuadTable(10);
        ReserveTable rt = new ReserveTable(5);
        
// Symbol table tests  will try adding the same thing twice, compare placement indexes        
        idx = st.AddSymbol("count",'v',2345);    //0
        idx = st.AddSymbol("sum",'v',88.6);      //1
// this is the one which will attempt a duplicate   
// the following should match the first MyInt added, and not add again or change
        indexA = st.AddSymbol("MyInt", 'v',25);  //2
        idx = st.AddSymbol("ToTal",'v',"my string");  //3
// here try different capitalization, should come back with same as indexA
        indexB = st.AddSymbol("myint", 'c',25.0);
        if (indexA != indexB) 
            System.out.println("Symbol Table duplicate add check failed");
        nameindex = st.AddSymbol("Name",'v',"Paul Williams");    //4
        idx = st.AddSymbol("realCount",'v',922.65);  //5 last one there is room for
        tooMany = st.AddSymbol("noRoom",'v',16);  //6 should fail, return -1
        if (tooMany != -1) 
            System.out.println("Symbol Table size exceeded check failed");

// Look for 'name' case insensitive
        idx = st.LookupSymbol("NaMe");
        if (idx != nameindex)
            System.out.println("Symbol Table lookup of NaMe failed");
        testSymbol = st.GetSymbol(idx);
        testKind = st.GetKind(idx);
        testType = st.GetDataType(idx);
        testString = st.GetString(idx);
        testFloat = st.GetFloat(idx);
        testInt = st.GetInteger(idx);
                
        System.out.println(testSymbol+"|"+testKind+"|"+testType+"|"+testString+"|"+testFloat+"|"+testInt+"|");

        st.UpdateSymbol(idx, 'c', "James Roberts" );
        testSymbol = st.GetSymbol(idx);
        testKind = st.GetKind(idx);
        testType = st.GetDataType(idx);
        testString = st.GetString(idx);
        testFloat = st.GetFloat(idx);
        testInt = st.GetInteger(idx);
                
        System.out.println(testSymbol+"|"+testKind+"|"+testType+"|"+testString+"|"+testFloat+"|"+testInt+"|");
        
// Print the symbol table contents, only the valid data should print, lines 0 through 5        
st.PrintSymbolTable(filePath+"symbolTable.txt");


// Quad table testing fill it to capacity
        for (idx = 0; idx < 5; idx++ ) {
            qt.AddQuad(idx*2, idx+2, idx*3, idx+12);
        }   
        
        if (qt.NextQuad() != 5) 
            System.out.println("NextQuad was not the expected value 5");

        for (idx = 5; idx < 10; idx++ ) {
            qt.AddQuad(idx*2, idx+2, idx*3, idx+12);
        }   

        qt.UpdateQuad(9, 1, 2, 3, 4);

        opcode = qt.GetQuad(9, 0);
        op1 = qt.GetQuad(9, 1);
        op2 = qt.GetQuad(9, 2);
        op3 = qt.GetQuad(9, 3);
        if ((opcode !=1)||(op1 !=2)||(op2 !=3)||(op3 != 4))
            System.out.println("Update Quad Failed on slot 9");
          
        qt.PrintQuadTable(filePath+"quadTable.txt");
        
//Reserve Table tests 5 elements       
        rt.Add("for", 20);
        rt.Add("NEXT", 21);
        rt.Add("Begin", 22);
        rt.Add("END", 23);
        rt.Add("program", 24);
        
        rcode = rt.LookupName("FOR");
        if (rcode != 20) System.out.println("Lookup FOR failed");
        rcode = rt.LookupName("ProGram");
        if (rcode != 24) System.out.println("Lookup ProGram failed");
        rcode = rt.LookupName("NotThere");
        if (rcode != -1) System.out.println("Lookup NotThere failed");
        testString = rt.LookupCode(20);
        if (testString.compareToIgnoreCase("FOR") != 0) System.out.println("Lookup 20 failed");
        testString = rt.LookupCode(24);
        if (testString.compareToIgnoreCase("program") != 0) System.out.println("Lookup 24 failed");
        testString = rt.LookupCode(44);
        if (testString.compareToIgnoreCase("") != 0) System.out.println("Lookup 44 failed");

        rt.PrintReserveTable(filePath+"reserveTable.txt");
        
    }
    
}

