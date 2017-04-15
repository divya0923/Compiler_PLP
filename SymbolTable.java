package cop5556sp17;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import cop5556sp17.AST.Dec;

public class SymbolTable {

	public class SymbolTableEntry {
		int scope;
		Dec dec;

		public SymbolTableEntry(int scope, Dec dec) {
			this.scope = scope;
			this.dec = dec;
		}

		@Override
		public String toString() {
			return Integer.toString(this.scope);
		}
	}

	int currentScope, nextScope;
	Map<String, LinkedList<SymbolTableEntry>> symbolTableMap;
	Stack<Integer> scopeStack;

	/**
	 * to be called when block entered
	 */
	public void enterScope() {
		currentScope = ++ nextScope;
		scopeStack.push(currentScope);
	}

	/**
	 * leaves scope
	 */
	public void leaveScope(){
		scopeStack.pop();
		currentScope = scopeStack.peek();
	}

	public boolean insert(String ident, Dec dec) {
		// list empty for this ident - add a new entry to the list
		if(symbolTableMap.get(ident) == null) {
			LinkedList<SymbolTableEntry> symbolTableEntries = new LinkedList<SymbolTableEntry>();
			symbolTableEntries.addFirst(new SymbolTableEntry(currentScope, dec));
			symbolTableMap.put(ident, symbolTableEntries);
		}
		// list not empty - iterate through the list to see if variable redeclared
		else {
			LinkedList<SymbolTableEntry> symbolTableEntries = symbolTableMap.get(ident);
			for(SymbolTableEntry entry : symbolTableEntries) {
				if(entry.scope == currentScope)
					return false;
			}
			symbolTableEntries.addFirst(new SymbolTableEntry(currentScope, dec));
			symbolTableMap.put(ident, symbolTableEntries);
		}
		return true;
	}

	public Dec lookup(String ident) {
		if(symbolTableMap.get(ident) != null){
			LinkedList<SymbolTableEntry> symbolTableEntries = symbolTableMap.get(ident);
			for(SymbolTableEntry entry : symbolTableEntries){
				for (ListIterator<Integer> iterator = scopeStack.listIterator(scopeStack.size()); iterator.hasPrevious();) {
					if(entry.scope == iterator.previous()) {
				    	return entry.dec;
				    }
				}
			}
		}
		return null;
	}

	public SymbolTable() {
		scopeStack = new Stack<Integer>();
		scopeStack.push(0);
		symbolTableMap = new HashMap<String, LinkedList<SymbolTableEntry>>();
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "";
	}



}
