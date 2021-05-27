package project1a.ADT;
 import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

class SymbolTable{
	public MyObject[] table;
	int count;
	
	public SymbolTable(int maxSize) {
		table = new MyObject[maxSize];
		count = -1;
	}
	
	public int AddSymbol(String symbol, char kind, int value) {
		int index = LookupSymbol(symbol);
		
		//
		if (index== -1) {
			if(count < (table.length -1)) {
				count++;
				table[count] = new MyObject(symbol, kind, 'i', value, 0.0, "");
				index = count;
			}else {
					//Is this right for the max?
					return -1;
			}
		}
			return index;
	}
		
	public int AddSymbol(String symbol, char kind, double value) {
		int index = LookupSymbol(symbol);
		if (index== -1) {
			if(count < (table.length -1)) {
				count++;
				table[count] = new MyObject(symbol, kind, 'f', 0 , value, "");
				index = count;
			}else {
					//Is this right for the max?
					return -1;
				}
			}
			return index;
	}
	
	//helper for this that would create the object, direct the myobject to create and the helper would be called by 
	//move douplicate code to a helper fucntion
	// Something think about for this 
	
	public int AddSymbol(String symbol, char kind, String value) {
		int index = LookupSymbol(symbol);
		if (index== -1) {
				if(count< (table.length -1)) {
					count++;
					table[count] = new MyObject(symbol, kind, 's', 0 , 0.0, value);
					index = count;
				}else {
					//Is this right for the max?
					return -1;
				}
			}
			
			return index;
	}	

	public int LookupSymbol(String symbol) {
		int result = -1;
		for (int n=0; n <= count; n++) {
			if (table[n].name.toLowerCase().equals(symbol.toLowerCase())){
				result = n;	
			}
		}
		return result;
	}
	
	public String GetSymbol(int index) {
		return table[index].name;
	}
	
	public char GetKind(int index) {
		return table[index].kind;
	}
	
	public char GetDataType(int index) {
		return table[index].data_type;
	}
	
	public String GetString(int index) {
		return table[index].stringValue;
	}
	
	public int GetInteger(int index) {
		return table[index].integerValue;
	}
	
	public double GetFloat(int index) {
		return table[index].floatValue;
	}
	
	//Is this right? if so then the one above is incorrect....?
	public void UpdateSymbol(int index, char kind, int value) {
		table[index].kind = kind;
		table[index].integerValue = value;
	}
	public  void UpdateSymbol(int index, char kind, double value) {
		table[index].kind = kind;
		table[index].floatValue = value;
	}
	public void UpdateSymbol(int index, char kind, String value) {
		table[index].kind = kind;
		table[index].stringValue = value;
	}
	
	//
	public void PrintSymbolTable(String filename) {
		
			try {
					FileOutputStream stream = new FileOutputStream(filename);
					OutputStreamWriter writer = new OutputStreamWriter(stream);
					BufferedWriter bufferedWriter = new BufferedWriter(writer);
					
					
					for(int each = 0; each < table.length; each++) {
						bufferedWriter.write(table[each].name + "|"+ table[each].kind+"|"+ table[each].data_type +"|");
						if(table[each].data_type =='i' && table[each].integerValue != 0) {
							bufferedWriter.write(String.format("%d",table[each].integerValue));
						}else if(table[each].data_type =='s' && table[each].stringValue != null) {
							bufferedWriter.write(table[each].stringValue);
						}else if (table[each].data_type =='f' && table[each].floatValue != 0.0) {
							bufferedWriter.write(String.format("%.02f", table[each].floatValue));
							}
						if(each == table.length-1) {
							break;
						}
						bufferedWriter.newLine();
						}
					bufferedWriter.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
	}
	private class MyObject{
		private String name;
		private char kind;
		private char data_type;
		private int integerValue;
		private double floatValue;
		private String stringValue;
		
		public MyObject(String name, char kind, char data_type, int integerValue, double floatValue, String stringValue) {
			this.name = name;
			this.kind = kind;
			this.data_type = data_type;
			this.integerValue = integerValue;
			this.floatValue  = floatValue;
			this.stringValue = stringValue;
			}

		public boolean compareToIgnoreCase(String symbol) {
			return false;
		}
	}
}