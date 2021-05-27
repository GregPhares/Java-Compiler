
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


//import SymbolRow;


public class SymbolTable {
  SymbolRow list[];
  int size, curSize;  
  static int notFound = -1;
  
  public SymbolTable(int maxSize){
      list = new SymbolRow[maxSize];
      size = maxSize-1; //highest index
      curSize = -1; //point to nothing
      
  }
  
  public int LookupSymbol(String symbol){
      boolean found = false;
      int result = notFound;
      int index =0;
      while ((index <= curSize)&&(!found)){
          if (symbol.compareToIgnoreCase(list[index].name) == 0){
              found = true;
              result = index;
          }
          else 
              index++;
     }
  return result;    
  }
  
  public int AddSymbol(String symbol, char kind, int value){
      int result = LookupSymbol(symbol);
      if (result == notFound) { // must add it 
          if (curSize < size) {//is room
              curSize++;
              list[curSize] = new SymbolRow();
              list[curSize].name = symbol;
              list[curSize].kind = kind;
              list[curSize].dataType = 'i';
              list[curSize].integerValue = value;
              list[curSize].floatValue = 0.0;
              list[curSize].stringValue = "";
              result = curSize;
          }       
         }
      //if it was found, just return index
    return result;  
}
public int AddSymbol(String symbol, char kind, double value) {
      int result = LookupSymbol(symbol);
      if (result == notFound) { // must add it 
          if (curSize < size) {//is room
              curSize++;
              list[curSize] = new SymbolRow();
              list[curSize].name = symbol;
              list[curSize].kind = kind;
              list[curSize].dataType = 'f';
              list[curSize].integerValue = 0;
              list[curSize].floatValue = value;
              list[curSize].stringValue = "";
              result = curSize;
          }       
         }
      //if it was found, just return index
    return result;  
}
public int AddSymbol(String symbol, char kind, String value){
      int result = LookupSymbol(symbol);
      if (result == notFound) { // must add it 
          if (curSize < size) {//is room
              curSize++;
              list[curSize] = new SymbolRow();
              list[curSize].name = symbol;
              list[curSize].kind = kind;
              list[curSize].dataType = 's';
              list[curSize].integerValue = 0;
              list[curSize].floatValue = 0.0;
              list[curSize].stringValue = value;
              result = curSize;
          }       
         }
      //if it was found, just return index
    return result;  
}
private boolean indexOK(int index) {
    return (index>=0)&&(index <=curSize);
}
    
public String GetSymbol(int index) {
    if (indexOK(index))
        return list[index].name;
    else
        return "";
}

public char GetDataType(int index){
    if (indexOK(index))
        return list[index].dataType;
    else
        return ' ';
}

public char GetKind(int index){
    if (indexOK(index))
        return list[index].kind;
    else
        return ' ';
}

public String GetString(int index) {
    if (indexOK(index))
        return list[index].stringValue;
    else
        return "";
}

public int GetInteger(int index) {
    if (indexOK(index))
        return list[index].integerValue;
    else
        return 0;

}

public double GetFloat(int index) {
    if (indexOK(index))
        return list[index].floatValue;
    else
        return 0.0;
}
        
public void UpdateSymbol(int index, char kind, int value){
    if (indexOK(index)){
        list[index].kind = kind;
        list[index].dataType = 'i';
        list[index].integerValue = value;
    }
}
public void UpdateSymbol(int index, char kind, double value){
    if (indexOK(index)){
        list[index].kind = kind;
        list[index].dataType = 'f';
        list[index].floatValue = value;
    }
}

public void UpdateSymbol(int index, char kind, String value){
    if (indexOK(index)){
        list[index].kind = kind;
        list[index].dataType = 's';
        list[index].stringValue = value;
    }
}


public void PrintSymbolTable(String filename) {
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
             
//            bufferedWriter.write("Symbol Table");
//            bufferedWriter.newLine();
            for (int i=0; i<=curSize; i++) {
                bufferedWriter.write(list[i].toString());
                bufferedWriter.newLine();
            }
                
//            bufferedWriter.write("");
             
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

}
}