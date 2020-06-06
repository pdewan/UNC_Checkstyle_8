package unc.cs.symbolTable;


public class CopyOfSymbolTableFactory {
	static SymbolTable symbolTable;
	
	public static SymbolTable getOrCreateSymbolTable() {
		if (symbolTable == null)
			symbolTable = new ASymbolTable();
		return symbolTable;
	}

	public static SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public static void setSymbolTable(SymbolTable newValue) {
		CopyOfSymbolTableFactory.symbolTable = newValue;
	}
}
