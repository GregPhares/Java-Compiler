
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class ReserveTable {
    static int notFound = -1;
    String[] words;
    int[] codes;
    int size;
    int curSize; 

private boolean indexOK(int index) {
    return (index>=0)&&(index <=curSize);
}
    
public ReserveTable(int initSize) {
    words = new String[initSize];
    codes = new int[initSize];
    size = initSize;
    curSize = -1;
}
    
public int Add(String name, int code) {
    int result = -1;
    if (curSize < size) {
        curSize++;
        words[curSize] = name;
        codes[curSize] = code;
        result = curSize;
    }   
return result;    
}

public int LookupName(String name) {
      boolean found = false;
      int result = notFound;
      int index =0;
      while ((index <= curSize)&&(!found)){
          if (name.compareToIgnoreCase(words[index]) == 0){
              found = true;
              result = codes[index];
          }
          else 
              index++;
     }
  return result;    
  }

public String LookupCode(int code) {
      boolean found = false;
      String result = "";
      int index =0;
      while ((index <= curSize)&&(!found)){
          if (codes[index] == code) {
              found = true;
              result = words[index];
          }
          else 
              index++;
     }
  return result;    
  }
        
String ReserveToString(int index) {
    return words[index].trim().toUpperCase()+'|'+Integer.toString(codes[index]);
}        


public void PrintReserveTable(String filename) {
    //Prints to the named file only the currently used contents of the Quad table in neattabular format, one row per 
   try {
            FileOutputStream outputStream = new FileOutputStream(filename);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
             
//            bufferedWriter.write("Reserve Table");
//            bufferedWriter.newLine();
            for (int i=0; i<=curSize; i++) {
                bufferedWriter.write(ReserveToString(i));
                bufferedWriter.newLine();
            }
                
//            bufferedWriter.write("");
             
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }    
}
}
    

