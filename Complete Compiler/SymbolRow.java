
public class SymbolRow {
    public String name;
    public char kind;
    public char dataType;
    public int integerValue;
    public double floatValue;
    public String stringValue;
    
    public SymbolRow() {
    name = "";
    kind = ' ';
    dataType = ' ';
    integerValue = 0;
    floatValue = 0.0;
    stringValue = "";
}
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
}
