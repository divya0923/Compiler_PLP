package cop5556sp17.AST;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

public class IdentChain extends ChainElem {

	public IdentChain(Token firstToken) {
		super(firstToken);
	}

	public Dec dec;
	public boolean isLeft;
	public Kind arrowOp;

	/**
	 * @return the arrowOp
	 */
	public Kind getArrowOp() {
		return arrowOp;
	}


	/**
	 * @param arrowOp the arrowOp to set
	 */
	public void setArrowOp(Kind arrowOp) {
		this.arrowOp = arrowOp;
	}


	/**
	 * @return the isLeft
	 */
	public boolean isLeft() {
		return isLeft;
	}


	/**
	 * @param isLeft the isLeft to set
	 */
	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
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


	@Override
	public String toString() {
		return "IdentChain [firstToken=" + firstToken + "]";
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentChain(this, arg);
	}



}
