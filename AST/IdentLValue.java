package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

public class IdentLValue extends ASTNode {
	public Dec dec;
	
	public IdentLValue(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public String toString() {
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}

	/**
	 * @return the dec
	 */
	public Dec getDec() {
		return dec;
	}

	/**
	 * @param dec the dec to set
	 */
	public void setDec(Dec dec) {
		this.dec = dec;
	}

}
