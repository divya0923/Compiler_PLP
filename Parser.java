package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTNode;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.WhileStatement;

public class Parser {	
	HashMap<String,Kind> strongOp = new HashMap<String,Kind>();
	HashMap<String,Kind> weakOp = new HashMap<String,Kind>();
	HashMap<String,Kind> relOp = new HashMap<String,Kind>();
	HashMap<String,Kind> arrowOp = new HashMap<String,Kind>();	
	HashMap<String,Kind> filter_op_keyword1 = new HashMap<String,Kind>();
	HashMap<String,Kind> image_op_keyword1 = new HashMap<String,Kind>();
	HashMap<String,Kind> frame_op_keyword1 = new HashMap<String,Kind>();
	
	
	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	Scanner scanner;
	Token t;
	Parser(Scanner scanner) {
		initDataStructures1();
		this.scanner = scanner;
		t = scanner.nextToken();
	}
	
	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {	
		Program prog = program();
		matchEOF();
		return prog;
	}

	Expression expression() throws SyntaxException {
		Token firstToken = t;
		Expression expr1 = null, expr2 = null;
		expr1 = term();
		while(relOp.containsKey(t.getText())) {	
			Token op = t;
			consume();
			expr2 = term();
			expr1 = new BinaryExpression(firstToken, expr1, op, expr2);		
		}
		return expr1;
	}

	Expression term() throws SyntaxException {
		Token firstToken = t;
		Expression expr1 = null, expr2 = null;
		expr1 = elem();
		while(weakOp.containsKey(t.getText())) {
			Token op = t;
			consume();
			expr2 = elem();
			expr1 = new BinaryExpression(firstToken, expr1, op, expr2);		
		}	
		return expr1;
	}

	Expression elem() throws SyntaxException {
		Token firstToken = t;
		Expression expr1 = null, expr2 = null;
		expr1 = factor();
		while(strongOp.containsKey(t.getText())){
			Token op = t;
			consume();
			expr2 = factor();
			expr1 = new BinaryExpression(firstToken, expr1, op, expr2);
			
		}
		return expr1;
	}

	Expression factor() throws SyntaxException {	
		Expression expr = null; 
		Kind kind = t.kind;
		Token firstToken = t;
		switch (kind) {
		case IDENT: {
			expr = new IdentExpression(firstToken);
			consume();
		}
			break;
		case INT_LIT: {
			expr = new IntLitExpression(firstToken);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			expr = new BooleanLitExpression(firstToken);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			expr = new ConstantExpression(firstToken);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			expr = expression();
			match(RPAREN);
		}
			break;
		default :
			//you will want to provide a more useful error message
			throw new SyntaxException("The token at line " + t.getLinePos().line + "at position " + t.pos + " is of kind " + t.kind + " which is not a factor");
		}
		
		return expr;
	}

	Block block() throws SyntaxException {
		ArrayList<Dec> decList = new ArrayList<Dec>();
		ArrayList<Statement> statementList = new ArrayList<Statement>();
		Token firstToken = t;
		if(t.kind.equals(LBRACE))
		{
			consume();
			while(t.kind.equals(KW_INTEGER)||t.kind.equals(KW_BOOLEAN)||t.kind.equals(KW_IMAGE)||
					t.kind.equals(KW_FRAME) ||t.kind.equals(KW_WHILE)||t.kind.equals(KW_IF) ||t.kind.equals(OP_SLEEP)
					||t.kind.equals(IDENT)||filter_op_keyword1.containsKey(t.kind.getText())
					||frame_op_keyword1.containsKey(t.kind.getText())||image_op_keyword1.containsKey(t.kind.getText()))				
			{
				switch(t.kind)
				{
				case KW_INTEGER:
				case KW_BOOLEAN:
				case KW_IMAGE:
				case KW_FRAME: decList.add(dec());
								break;
								
				case OP_SLEEP:
				case KW_WHILE:
				case KW_IF:
				case IDENT:
				case OP_BLUR:
				case OP_GRAY:
				case OP_CONVOLVE:
				case KW_SHOW:
				case KW_HIDE:	
				case KW_MOVE:
				case KW_XLOC:
				case KW_YLOC:
				case OP_WIDTH:
				case OP_HEIGHT:
				case KW_SCALE: statementList.add(statement());
								break;
				
				default:
					throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a BLOCK");
					
				}
			}
			if(t.kind.equals(RBRACE))
				consume();
			else
				throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a RBRACE");			
		}
		else
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a LBRACE");		
		return new Block(firstToken, decList, statementList);
	}

	Program program() throws SyntaxException {
		Token firstToken = t; 
		Block block = null;
		ArrayList<ParamDec> paramDecList= new ArrayList<ParamDec>();
		if(t.kind.equals(IDENT)) {
			consume();	
			if(t.kind.equals(LBRACE)) {
				block = block();
			}
			else if(t.kind.equals(KW_URL)||t.kind.equals(KW_FILE)||t.kind.equals(KW_INTEGER)||t.kind.equals(KW_BOOLEAN)) {				
				paramDecList.add(paramDec());
				while(t.kind.equals(COMMA)) {
					consume();
					paramDecList.add(paramDec());
				}
				block = block();
			}
			else
				throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a PARAMDEC");			
		}
		else
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");
		
		return new Program(firstToken, paramDecList, block);
	}

	ParamDec paramDec() throws SyntaxException {
		Token firstToken, ident;
		firstToken = t;
		switch(t.kind) {
			case KW_URL: {
				consume();
				if(t.kind.equals(IDENT)){
					ident = t;
					consume();
				}
					
				else
					throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");					
			}
			break;
			case KW_FILE: {
				consume();
				if(t.kind.equals(IDENT)){
					ident = t;
					consume();
				}
				else
					throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");			
			}
			break;
			case KW_INTEGER: {
				consume();
				if(t.kind.equals(IDENT)){
					ident = t;
					consume();
				}
				else
					throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");			
			}
			break;
			case KW_BOOLEAN: {
				consume();
				if(t.kind.equals(IDENT)){
					ident = t;
					consume();
				}
				else
					throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");			
			}
			break;
			
			default:
				throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not one of KW_URL,KW_FILE,KW_INTEGER or KW_BOOLEAN");
		}
		
		return new ParamDec(firstToken, ident);
	}

	Dec dec() throws SyntaxException {
		Token firstToken, ident;
		if(t.kind.equals(KW_INTEGER))
		{
			firstToken = t;
			consume();	
			if(t.kind.equals(IDENT)) {
				ident = t;
				consume();
			}
			else
				throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");			
		}
		else if(t.kind.equals(KW_BOOLEAN))
		{
			firstToken = t;
			consume();
			if(t.kind.equals(IDENT)) {
				ident = t;
				consume();
			}
			else
				throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");			
		}
		else if(t.kind.equals(KW_IMAGE))
		{
			firstToken = t;
			consume();
			if(t.kind.equals(IDENT)) {
				ident = t;
				consume();
			}
			else
				throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");			
		}
		else if(t.kind.equals(KW_FRAME))
		{
			firstToken = t;
			consume();
			if(t.kind.equals(IDENT)) {
				ident = t;
				consume();
			}
			else
				throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IDENT");			
		}
		else
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a DEC");
		
		return new Dec(firstToken, ident);		
	}

	Statement statement() throws SyntaxException {
		Statement statement = null;
		Token firstToken = null;
		
		if(t.kind.equals(KW_WHILE)||t.kind.equals(KW_IF) ||t.kind.equals(OP_SLEEP)) {
			switch(t.kind) {
			case OP_SLEEP:{
				        	firstToken = t;
							consume();
							Expression expr = expression();
							if(t.kind.equals(SEMI))
							{
								consume();
								statement = new SleepStatement(firstToken, expr);
							}
							else
								throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a SEMI");
						   }
						break;

			case KW_WHILE:{
						statement = whileStatement();
						  }
						break;

			case KW_IF:{
						statement = ifStatement();
	
						}
						break;
			default:
				break;
			}
		}
		
		else if(t.kind.equals(IDENT)||filter_op_keyword1.containsKey(t.kind.getText())||frame_op_keyword1.containsKey(t.kind.getText())||image_op_keyword1.containsKey(t.kind.getText())) {		
			if(t.kind.equals(IDENT) && scanner.peek().kind.equals(ASSIGN)) {
				firstToken = t;
				consume();
				if(t.kind.equals(ASSIGN)) {
					consume();
					Expression expr = expression();
					if(t.kind.equals(SEMI)){
						consume();
						statement = new AssignmentStatement(firstToken, new IdentLValue(firstToken), expr);
					}					
					else 
						throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a SEMI");
				}
				
			}
			//else if(arrowOp.containsKey(scanner.peek().getText())) {
			else {	
				Chain chain = chain();
				if(t.kind.equals(SEMI)){
					consume();
					statement = chain;
				}					
				else 
					throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a SEMI");
			}
			//else
				//throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a STATEMENT");
		}
		else
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a STATEMENT");
		
		return statement;
	}

	Chain chain() throws SyntaxException {
		Chain chain = null;
		Token firstToken = t;
		boolean chain_flag=false;
		ChainElem chainElem = chainElem(); // convolve
		if(arrowOp.containsKey(t.kind.getText())) {
			Token arrow = t;
			consume();
			chain_flag=true;
			ChainElem chainElem2 = chainElem(); // blur 
			chain = new BinaryChain(firstToken,chainElem, arrow, chainElem2); // convolve + blur 
			
			while(arrowOp.containsKey(t.kind.getText())) {
				arrow = t;
				consume();
				chainElem2 = chainElem(); // gray 
				chain = new BinaryChain(firstToken, chain, arrow, chainElem2); // convolve + blur + gray + width 
				chain_flag=true;
			}
		}
		else
			chain_flag = false;
		if(chain_flag)
			return chain;
		else
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a CHAIN");
	}
	
	void assign() throws SyntaxException
	{
		boolean assign_flag = false;
		if(t.kind.equals(IDENT)) {
			consume();
			assign_flag = true;
			if(t.kind.equals(ASSIGN)) {
				consume();	
				expression();
			}
			else
				assign_flag = false;			
		}
		if(assign_flag)
			return;
		else
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a ASSIGN");
	}
	
	WhileStatement whileStatement() throws SyntaxException {
		Expression expr = null;
		Block block = null;
		Token firstToken = null; 
		boolean while_flag=false;
		if(t.kind.equals(KW_WHILE)) {	
			while_flag=true;
			firstToken = t;
			consume();
			if(t.kind.equals(LPAREN)) {	
				while_flag=true;
				consume();
				expr = expression();				
				if(t.kind.equals(RPAREN)) {
					while_flag = true;
					consume();
					block = block();
				}
				else while_flag = false;
			}
			else while_flag = false;
			
		}
		if(while_flag)
			return new WhileStatement(firstToken, expr, block);
		else
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a WHILE");
	}

	IfStatement ifStatement() throws SyntaxException {
		Expression expr = null;
		Block block = null;
		Token firstToken = null; 
		boolean if_flag=false;
		if(t.kind.equals(KW_IF)) {	
			firstToken = t;
			if_flag=true;
			consume();
			if(t.kind.equals(LPAREN)) {
				if_flag=true;
				consume();
				expr = expression();
				if(t.kind.equals(RPAREN)){
					if_flag = true;
					consume();
					block = block();
				}
				else if_flag = false;
			}
			else 
				if_flag = false;
			
		}
		if(if_flag)
			return new IfStatement(firstToken, expr, block);
		else
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a IF");		
		
	}

	ChainElem chainElem() throws SyntaxException {
		Token firstToken = t; 
		Tuple tuple = null;
		if(t.kind.equals(IDENT)) {	
			consume();
			return new IdentChain(firstToken);
		}
		else if(filter_op_keyword1.containsKey(t.getText())) {
			consume();
			tuple = arg();
			return new FilterOpChain(firstToken, tuple);
		}
		else if(frame_op_keyword1.containsKey(t.getText()))	{
			consume();
			tuple = arg();
			return new FrameOpChain(firstToken, tuple);
		} 
		else if(image_op_keyword1.containsKey(t.getText()))	{
			consume();
			tuple = arg();
			return new ImageOpChain(firstToken, tuple);
		}
		else {			
			throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a CHAINELEM");		}
		
	}

	Tuple arg() throws SyntaxException {
		List<Expression> argList = new ArrayList<Expression>();
		boolean arg_flag=false;	
		Token firstToken = t;
		if(t.kind.equals(LPAREN)) {
			consume();
			argList.add(expression());
			while(t.kind.equals(COMMA)) {
				consume();
				arg_flag = true;
				argList.add(expression());
				
			}
			if(t.kind.equals(RPAREN)) {
				consume();
				arg_flag=true;
			}
			else
				arg_flag = false;
			
			if(arg_flag)
				return new Tuple(firstToken, argList);
			else
				throw new SyntaxException("The token at line " + t.getLinePos().line + " at position " + t.pos + " is of kind " + t.kind + " which is not a ARG");			
		}
		else
			return new Tuple(firstToken, argList);
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if(t.kind.equals(EOF))
			return t;
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		//CHANGING THIS TO OUR KIND
		//if (t.isKind(kind)) {
		if (t.kind.equals(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}
	
	public void initDataStructures1()
	{
		arrowOp.put("->", Kind.ARROW);
		arrowOp.put("|->", Kind.BARARROW);
		
		strongOp.put("*", Kind.TIMES);
		strongOp.put("/", Kind.DIV);
		strongOp.put("&", Kind.AND);
		strongOp.put("%", Kind.MOD);
		
		weakOp.put("+",Kind.PLUS);
		weakOp.put("-",Kind.MINUS);
		weakOp.put("|",Kind.OR);
		
		relOp.put("<", Kind.LT);
		relOp.put("<=", Kind.LE);
		relOp.put(">=", Kind.GE);
		relOp.put(">", Kind.GT);
		relOp.put("==", Kind.EQUAL);
		relOp.put("!=", Kind.NOTEQUAL);
		
		filter_op_keyword1.put("gray",Kind.OP_GRAY );
		filter_op_keyword1.put("convolve", Kind.OP_CONVOLVE);
		filter_op_keyword1.put("blur", Kind.OP_BLUR);
		//filter_op_keyword1.put("scale", Kind.KW_SCALE);

		image_op_keyword1.put("width", Kind.OP_WIDTH);
		image_op_keyword1.put("height", Kind.OP_HEIGHT);
		image_op_keyword1.put("scale", Kind.KW_SCALE);
		
		frame_op_keyword1.put("xloc",Kind.KW_XLOC);
		frame_op_keyword1.put("yloc",Kind.KW_YLOC);
		frame_op_keyword1.put("hide",Kind.KW_HIDE);
		frame_op_keyword1.put("show",Kind.KW_SHOW);
		frame_op_keyword1.put("move",Kind.KW_MOVE);

		
		
		
	}
	

}
