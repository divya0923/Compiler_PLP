package cop5556sp17;

import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		Chain chain = binaryChain.getE0();
		chain.visit(this, arg);
		TypeName chainType = chain.getTypeName();
		
		ChainElem chainElem = binaryChain.getE1();
		chainElem.visit(this, arg);
		TypeName chainElemType = chainElem.getTypeName();
		
		Token arrow = binaryChain.getArrow();
		
		if(chainType.isType(TypeName.URL)  && arrow.kind.equals(Kind.ARROW) && chainElemType.isType(TypeName.IMAGE))
			binaryChain.setTypeName(TypeName.IMAGE);
		
		else if(chainType.isType(TypeName.FILE)  && arrow.kind.equals(Kind.ARROW) && chainElemType.isType(TypeName.IMAGE))
			binaryChain.setTypeName(TypeName.IMAGE);
		
		else if(chainType.isType(TypeName.FRAME)  && arrow.kind.equals(Kind.ARROW) && chainElem instanceof FrameOpChain){
			if(chainElem.getFirstToken().kind.equals(KW_XLOC) || chainElem.getFirstToken().kind.equals(KW_YLOC)) {
				binaryChain.setTypeName(TypeName.INTEGER);
			}
			else if(chainElem.getFirstToken().kind.equals(KW_SHOW) || chainElem.getFirstToken().kind.equals(KW_HIDE) || chainElem.getFirstToken().kind.equals(KW_MOVE))
				binaryChain.setTypeName(TypeName.FRAME);
			else 
				throw new TypeCheckException("");
		}
		
		else if(chainType.isType(TypeName.IMAGE)  && arrow.kind.equals(Kind.ARROW) && chainElem instanceof ImageOpChain){
			if(chainElem.getFirstToken().kind.equals(OP_WIDTH) || chainElem.getFirstToken().kind.equals(OP_HEIGHT)) {
				binaryChain.setTypeName(TypeName.INTEGER);
			}
			else if(chainElem.getFirstToken().kind.equals(KW_SCALE)) {
				binaryChain.setTypeName(TypeName.IMAGE);
			}
			else 
				throw new TypeCheckException("ChainElement is not an instance of FrameOpChain");
		}
		
		else if(chainType.isType(TypeName.IMAGE)  && arrow.kind.equals(Kind.ARROW) && chainElemType.isType(TypeName.FRAME)){
			binaryChain.setTypeName(TypeName.FRAME);
		}
		
		else if(chainType.isType(TypeName.IMAGE)  && arrow.kind.equals(Kind.ARROW) && chainElemType.isType(TypeName.FILE)){
			binaryChain.setTypeName(TypeName.NONE);
		}
		
		else if(chainType.isType(TypeName.IMAGE)  && (arrow.kind.equals(Kind.ARROW) || arrow.kind.equals(Kind.BARARROW)) && chainElem instanceof FilterOpChain){
			if(chainElem.getFirstToken().kind.equals(OP_GRAY) || chainElem.getFirstToken().kind.equals(OP_BLUR) || chainElem.getFirstToken().kind.equals(OP_CONVOLVE)) {
				binaryChain.setTypeName(TypeName.IMAGE);
			}
			else 
				throw new TypeCheckException("ChainElement is not an instance of FrameOpChain");
		}
		
		else if(chainType.isType(TypeName.IMAGE)  && arrow.kind.equals(Kind.ARROW) && chainElem instanceof IdentChain){
			binaryChain.setTypeName(TypeName.IMAGE);
		}
		
		else 
			throw new TypeCheckException("Operator is not arrow or bararrow in Binary Chain");
	
		return null;
	}
	
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		Expression e0 = binaryExpression.getE0();
		Expression e1 = binaryExpression.getE1();
		Token op = binaryExpression.getOp();
		
		e0.visit(this, arg);
		TypeName e0type = e0.getTypeName();
		
		e1.visit(this, arg);
		TypeName e1type = e1.getTypeName();
		
		if (e0type.isType(TypeName.INTEGER) && e1type.isType(TypeName.INTEGER)){
			if(op.kind.equals(TIMES) || op.kind.equals(PLUS) || op.kind.equals(MINUS) || op.kind.equals(DIV)){
				binaryExpression.setTypeName(TypeName.INTEGER);
			}
			else if (op.kind.equals(LT) || op.kind.equals(GT) || op.kind.equals(LE) || op.kind.equals(GE)){
				binaryExpression.setTypeName(TypeName.BOOLEAN);
			}
			else if(op.kind.equals(EQUAL) || op.kind.equals(NOTEQUAL)){
				binaryExpression.setTypeName(TypeName.BOOLEAN);
			}
			else 
				throw new TypeCheckException("Operation is not allowed between two integers");
		}
		else if(e0type.isType(TypeName.BOOLEAN) && e1type.isType(TypeName.BOOLEAN)){
			if (op.kind.equals(LT) || op.kind.equals(GT) || op.kind.equals(LE) || op.kind.equals(GE)){
				binaryExpression.setTypeName(TypeName.BOOLEAN);
			}
			else if(op.kind.equals(EQUAL) || op.kind.equals(NOTEQUAL)){
				binaryExpression.setTypeName(TypeName.BOOLEAN);
			}
			else 
				throw new TypeCheckException("Operation is not allowed between two booleans");
		}
		else if(e0type.isType(TypeName.IMAGE) && e1type.isType(TypeName.IMAGE)){
			if(op.kind.equals(PLUS) || op.kind.equals(MINUS)){
				binaryExpression.setTypeName(TypeName.IMAGE);
			}
			else if(op.kind.equals(EQUAL) || op.kind.equals(NOTEQUAL)){
				binaryExpression.setTypeName(TypeName.BOOLEAN);
			}
			else 
				throw new TypeCheckException("Operation is not allowed between two images");
		}
		else if((e0type.isType(TypeName.IMAGE) && e1type.isType(TypeName.INTEGER)) || (e0type.isType(TypeName.INTEGER) && e1type.isType(TypeName.IMAGE))){
			if(op.kind.equals(TIMES))
				binaryExpression.setTypeName(TypeName.IMAGE);
			else 
				throw new TypeCheckException("Operation is not allowed between two image and integer");
		}	
		else if (e0type.isType(e1type)){
			if(op.kind.equals(EQUAL) || op.kind.equals(NOTEQUAL)){
				binaryExpression.setTypeName(TypeName.BOOLEAN);
			}
			else 
				throw new TypeCheckException("Operation is not allowed between two expressions of same type");
		}
		
		else 
			throw new TypeCheckException("Operation is not allowed between two expressions"); 
					
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symtab.enterScope();
		// visit decs
		ArrayList<Dec> decs = block.getDecs();
		for(Dec dec : decs)
			dec.visit(this, arg);
		
		//visit statement
		ArrayList<Statement> statements = block.getStatements();
		for(Statement statement : statements)
			statement.visit(this, arg);
		
		symtab.leaveScope();
		
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		booleanLitExpression.setTypeName(TypeName.BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		int tupleLength = filterOpChain.getArg().getExprList().size();
		if(tupleLength != 0)
			throw new TypeCheckException("Tuple length is not zero for FilterOpChain");
		else 
			filterOpChain.setTypeName(TypeName.IMAGE);
			
		filterOpChain.getArg().visit(this, arg);
		
		//visitTuple(filterOpChain.getArg(), arg);
		
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		Kind kind = frameOpChain.firstToken.kind;
		int tupleLength = frameOpChain.getArg().getExprList().size();
		
		frameOpChain.getArg().visit(this, arg);
		
		if(kind == Kind.KW_SHOW || kind == Kind.KW_HIDE){
			if(tupleLength != 0)
				throw new TypeCheckException("Tuple length is not zero for FrameOpChain");
			else 
				frameOpChain.setTypeName(Type.TypeName.NONE);
		}
		else if(kind == Kind.KW_XLOC || kind == Kind.KW_YLOC){
			if(tupleLength != 0)
				throw new TypeCheckException("Tuple length is not zero for FrameOpChain");
			else 
				frameOpChain.setTypeName(Type.TypeName.INTEGER);
		}
		else if(kind == Kind.KW_MOVE){
			if(tupleLength != 2)
				throw new TypeCheckException("Tuple length is not two for FrameOpChain");
			else 
				frameOpChain.setTypeName(Type.TypeName.NONE);
		}
		
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		Dec dec = symtab.lookup(identChain.getFirstToken().getText());
		if(dec == null)
			throw new TypeCheckException("Ident has not been declared and is visible in the current scope");
		else {
			// FIXME - validate this 
			identChain.setTypeName(Type.getTypeName(dec.getFirstToken()));
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Dec dec = symtab.lookup(identExpression.getFirstToken().getText());
		TypeName typeName;
		if(dec == null)
			throw new TypeCheckException("ident has not been declared and is not visible in the current scope");
		else {
			typeName = Type.getTypeName(dec.getFirstToken());
			identExpression.setTypeName(typeName);
			dec.setTypeName(typeName);
			identExpression.setDec(dec);
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Expression e = ifStatement.getE();
		e.visit(this, arg);
		TypeName typeName =  e.getTypeName();
		if(!typeName.isType(TypeName.BOOLEAN))
			throw new TypeCheckException("Expression is not a Boolean Expression for ifStatement");
		ifStatement.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		intLitExpression.setTypeName(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		Expression e = sleepStatement.getE();
		e.visit(this, arg);
		if(!e.getTypeName().equals(INTEGER))
			throw new TypeCheckException("Expression type is not INTEGER for SleepStatement");
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Expression e = whileStatement.getE();
		e.visit(this, arg);
		TypeName typeName = e.getTypeName();
		// TODO - validate isType
		if(!typeName.isType(TypeName.BOOLEAN))
			throw new TypeCheckException("Expression is not a Boolean Expression for whileStatement");
		whileStatement.getB().visit(this, arg);
		return null;
	}
	
	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		boolean insert = symtab.insert(declaration.getIdent().getText(), declaration);
		if(!insert)
			throw new TypeCheckException("Ident is declared twice");
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		ArrayList<ParamDec> params = program.getParams();
		Block b = program.getB();
		for(ParamDec paramDec : params)
			paramDec.visit(this, arg);
		b.visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {	
		IdentLValue var = assignStatement.getVar();
		var.visit(this, arg);
		Expression e = assignStatement.getE();
		e.visit(this, arg);
		if(!e.getTypeName().isType(var.getDec().getTypeName()))
			throw new TypeCheckException("IdentLValue and Expression are not of the same type");
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		Dec dec = symtab.lookup(identX.firstToken.getText());
		TypeName typeName;
		if(dec == null)
			throw new TypeCheckException("Ident is not declared or visible in the current scope");
		else {
			typeName = Type.getTypeName(dec.getFirstToken());
			dec.setTypeName(typeName);
			identX.setDec(dec);
		}	
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		boolean insert = symtab.insert(paramDec.getIdent().getText(), paramDec);
		if(!insert)
			throw new TypeCheckException("Symbol table insert failed");
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		constantExpression.setTypeName(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		Kind kind = imageOpChain.firstToken.kind;
		int tupleLength = imageOpChain.getArg().getExprList().size();
		imageOpChain.getArg().visit(this, arg);
		
		if (kind == Kind.OP_WIDTH || kind == Kind.OP_HEIGHT) {
			if(tupleLength != 0)
				throw new TypeCheckException("Tuple length is not zero for ImageOpChain");
			else 
				imageOpChain.setTypeName(Type.TypeName.INTEGER);
		}
		else if (kind == Kind.KW_SCALE) {
			if (tupleLength != 1)
				throw new TypeCheckException("Tuple length is not one for ImageOpChain");
			else 
				imageOpChain.setTypeName(Type.TypeName.IMAGE);
		}
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		List<Expression> exprList = tuple.getExprList();
		for (Expression expr : exprList) {
			expr.visit(this, arg);
			if (!expr.getTypeName().isType(TypeName.INTEGER))
				throw new TypeCheckException("Expression in tuple are not integer");
		}
		return null;
	}


}
