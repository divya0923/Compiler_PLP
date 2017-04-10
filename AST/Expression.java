package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public abstract class Expression extends ASTNode {
	
	private TypeName typeName; 
	
	protected Expression(Token firstToken) {
		super(firstToken);
	}
	
	/**
	 * @param type the type to set
	 */
	public void setTypeName(TypeName typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return the type
	 */
	public TypeName getTypeName() {
		return this.typeName;
	}
	
	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
