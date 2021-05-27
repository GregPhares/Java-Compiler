package project1a.ADT;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class QuadTable {
	int[][] quad;
	int count;
	
	public QuadTable(int maxSize) {
		quad = new int[maxSize][4];
		count = -1;
	}
	
	//My question is can we just use the 0 to determine if the line is empty or does the op code 0 mean exit? Is this just looking for the next 0 to know when the row is empty??
	public int NextQuad() {
		
		return count+1;		
	}
	
	//Is it correct to assume that all that will be passed in are ints
	public void AddQuad(int opcode, int op1, int op2, int op3) {
			count++;
			quad[count][0] = opcode;
			quad[count][1]= op1;
			quad[count][2]= op2;
			quad[count][3]= op3;
	}
	
	public int GetQuad(int index, int column) {
		return quad[index][column];
	}
	
	public void UpdateQuad(int index, int opcode, int op1, int op2, int op3) {
		quad[index][0] = opcode;
		quad[index][1]= op1;
		quad[index][2]= op2;
		quad[index][3]= op3;
	}
	
	public void PrintQuadTable(String filename) {
		try {
			FileOutputStream stream = new FileOutputStream(filename);
			OutputStreamWriter writer = new OutputStreamWriter(stream);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			
			for(int row = 0; row<quad.length; row++) {
				for( int column =0; column <4; column++) {
					
					bufferedWriter.write((String.format("%d",quad[row][column])+"|"));
				}
				if(row == quad.length-1) {
					break;
				}
				bufferedWriter.newLine();
			}
			
			bufferedWriter.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
