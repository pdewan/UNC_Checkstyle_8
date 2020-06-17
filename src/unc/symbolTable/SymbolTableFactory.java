package unc.symbolTable;

import java.util.HashMap;
import java.util.Map;

import unc.tools.checkstyle.ProjectDirectoryHolder;


public class SymbolTableFactory {
	static Map<String, SymbolTable> projectToSymbolTable = new HashMap<>();
	protected static boolean linkSymbolTables = false;
	protected static SymbolTable lastSymbolTable = null;


  public static SymbolTable getLastSymbolTable() {
    return lastSymbolTable;
  }

  public static Map<String, SymbolTable>  getProjectSymbolTables() {
		return projectToSymbolTable;
	}
	
	public static SymbolTable getOrCreateSymbolTable() {
		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
		SymbolTable aSymbolTable = projectToSymbolTable.get(aProjectDirectory);
		if (aSymbolTable == null) {
			aSymbolTable = new ASymbolTable();
			if (isLinkSymbolTables()) {
			  aSymbolTable.setPreviousSymbolTable(lastSymbolTable);
			}
			lastSymbolTable = aSymbolTable;
			projectToSymbolTable.put(aProjectDirectory, aSymbolTable);
		}
		return aSymbolTable;
	}

	public static SymbolTable getSymbolTable() {
		return projectToSymbolTable.get(ProjectDirectoryHolder.getCurrentProjectDirectory());
	}
	public static void reset() {
		projectToSymbolTable.clear();
	}

	public static void setSymbolTable(SymbolTable newValue) {
		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
		projectToSymbolTable.put(aProjectDirectory, newValue);
	}
	 public static boolean isLinkSymbolTables() {
	    return linkSymbolTables;
	  }

	  public static void setLinkSymbolTables(boolean linkSymbolTables) {
	    SymbolTableFactory.linkSymbolTables = linkSymbolTables;
	  }
}
