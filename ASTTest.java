package cop5556sp17;

import static cop5556sp17.Scanner.Kind;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.Tuple;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s) {
		if(doPrint){System.out.println(s);}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}

	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(Kind.PLUS, be.getOp().kind);
	}
	
	@Test
	public void testExpression0() throws SyntaxException, IllegalCharException, IllegalNumberException{
		String input = "false%true-screenwidth<5";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(Kind.LT, be.getOp().kind);
		
		BinaryExpression be1 = (BinaryExpression)be.getE0();		
		assertEquals(BinaryExpression.class, be1.getE0().getClass());
		assertEquals(ConstantExpression.class, be1.getE1().getClass());
		assertEquals(Kind.MINUS, be1.getOp().kind);
		
		BinaryExpression be2 = (BinaryExpression)be1.getE0();
		assertEquals(BooleanLitExpression.class, be2.getE0().getClass());
		assertEquals(BooleanLitExpression.class, be2.getE1().getClass());
		assertEquals(Kind.MOD, be2.getOp().kind);
		
	}
	
	@Test
	public void testFactor2() throws SyntaxException, IllegalCharException, IllegalNumberException{
		String input = "(3*5/4&6+5==4)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.factor();

		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(Kind.EQUAL, be.getOp().kind);
		
		be = (BinaryExpression) be.getE0();
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(Kind.PLUS, be.getOp().kind);
		
		be = (BinaryExpression) be.getE0();
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(Kind.AND, be.getOp().kind);
		
		be = (BinaryExpression) be.getE0();
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(Kind.DIV, be.getOp().kind);
		
		be = (BinaryExpression) be.getE0();
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(Kind.TIMES, be.getOp().kind);
	}
	
	@Test
	public void testArg0() throws SyntaxException, IllegalCharException, IllegalNumberException{
		String input = "(3*5/4&6+5==4)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.arg();
		Tuple tuple = (Tuple) ast;
		assertEquals(tuple.getExprList().size(), 1);
		
		BinaryExpression be = (BinaryExpression) tuple.getExprList().get(0);
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(Kind.EQUAL, be.getOp().kind);
	}
	
	@Test
	public void testAssign0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "$abc<-(3==5);";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.statement();
		AssignmentStatement assign = (AssignmentStatement) ast;
		assertEquals("$abc", assign.var.getText());
		BinaryExpression be = (BinaryExpression)assign.getE();
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(Kind.EQUAL, be.getOp().kind);
	}
	
	@Test
	public void testChain0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "$495 -> $27684";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chain();
		BinaryChain bc = (BinaryChain) ast;
		assertEquals(IdentChain.class, bc.getE0().getClass());
		assertEquals(IdentChain.class, bc.getE1().getClass());
	}
	
}
