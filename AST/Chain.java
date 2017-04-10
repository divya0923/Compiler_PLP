package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	
	private TypeName typeName; 
	
	public Chain(Token firstToken) {
		super(firstToken);
	}

	/**
	 * @return the typeName
	 */
	public TypeName getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName the typeName to set
	 */
	public void setTypeName(TypeName typeName) {
		this.typeName = typeName;
	}

}
