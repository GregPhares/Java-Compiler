
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ReserveTable {
	public MyObject[] table;
	int count;
	
	public ReserveTable(int maxSize) {
		table = new MyObject[maxSize];
		count = -1;
	}
	
	public int Add(String name, int code) {
		int index = LookupName(name);
		if(index == -1) {
			count++;
			table[count] = new MyObject(name, code);
			index = count;
		}
		return index;		
	}
	

	public int LookupName(String name) {
		int result =-1;
		for(int n=0; n<=count; n++) {
			if(table[n].name.compareToIgnoreCase(name)==0) {
				//this will return the table[n].code
				result = table[n].code;
			}
		}
		return result;
	}
	
	public String LookupCode(int code) {
			String storage = "";
			for(int n = 0; n<=count; n++) {
				if(table[n].code == code){
					storage = table[n].name;
					break;
				}
			}
		return storage;
	}
	
	public void PrintReserveTable(String filename){
		try {
			FileOutputStream stream = new FileOutputStream(filename);
			OutputStreamWriter writer = new OutputStreamWriter(stream);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			
			for( int row = 0; row<=count; row++) {
				bufferedWriter.write(table[row].name.toUpperCase() + "|"+String.format("%d", table[row].code));
				if(row == count) {
					break;
				}
				bufferedWriter.newLine();
			}
	
			bufferedWriter.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	private class MyObject{
		private String name;
		private int code;
		
		public MyObject(String name, int code) {
			this.name= name;
			this.code= code;
		}
	}
}
