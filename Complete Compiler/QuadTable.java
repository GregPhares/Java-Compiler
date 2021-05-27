

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class QuadTable {
    int[][] table;
    int size;
    int curSize; 

private boolean indexOK(int index) {
    return (index>=0)&&(index <=curSize);
}
private boolean columnOK(int column) {
    return ((column>=0)&&(column<=3));
}
    
public QuadTable(int initSize) {
    table = new int[initSize][4];
    size = initSize;
    curSize = -1;
}
public int maxSize(){
    return size;
}
public int NextQuad(){
    return curSize + 1;
}

public void AddQuad(int opcode, int op1, int op2, int op3){
    if (curSize < (size))
        curSize++;
    table[curSize][0] = opcode;
    table[curSize][1] = op1;
    table[curSize][2] = op2;
    table[curSize][3] = op3;
}

public int GetQuad(int index, int column) {
    if ((indexOK(index))&&(columnOK(column))) {
        return table[index][column];
        }
    else
        return -1;
}
public void UpdateQuad(int index, int opcode, int op1, int op2, int op3){
    if (indexOK(index)) {
        table[index][0] = opcode;
        table[index][1] = op1;
        table[index][2] = op2;
        table[index][3] = op3;
        }
    
}

private String RowToString(int row){
    return Integer.toString(table[row][0]) + '|'+
           Integer.toString(table[row][1]) + '|'+
           Integer.toString(table[row][2]) + '|'+
           Integer.toString(table[row][3]) + '|'; 
}
public void PrintQuadTable(String filename){
    //Prints to the named file only the currently used contents of the Quad table in neattabular format, one row per 
   try {
            FileOutputStream outputStream = new FileOutputStream(filename);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
             
            for (int i=0; i<=curSize; i++) {
                bufferedWriter.write(RowToString(i));
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }    
}
}
