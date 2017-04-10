package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Token t = scanner.nextToken();
		assertEquals(Kind.EOF,t.kind);
	}
	
	@Test 
	public void zeroNumLitStart() throws IllegalCharException, IllegalNumberException {
		String input = "099";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		assertEquals(Kind.INT_LIT, scanner.nextToken().kind);
		assertEquals(Kind.INT_LIT, scanner.nextToken().kind);
	}
	
	@Test 
	public void testNewLine() throws IllegalCharException, IllegalNumberException {
		String input = "\n_aa\n43f;!=|->/*\n\n\n*/\n_afvdf99\n+==\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Token t = scanner.nextToken();
		assertEquals(1, scanner.getLinePos(t).line);
		Token t1 = scanner.nextToken();
		assertEquals(0, scanner.getLinePos(t1).posInLine);	
	}
	
	@Test 
	public void testKeyword() throws IllegalCharException, IllegalNumberException {
		String input = "aaagray->screenheight<-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Token t = scanner.nextToken();
		assertEquals(0, scanner.getLinePos(t).line);
		Token t1 = scanner.nextToken();
		assertEquals(Kind.ARROW, t1.kind);
		Token t2 = scanner.nextToken();
		assertEquals(Kind.KW_SCREENHEIGHT, t2.kind);	
		Token t3 = scanner.nextToken();
		assertEquals(Kind.ASSIGN, t3.kind);
	}
	
	@Test 
	public void testNewLineAppend() throws IllegalCharException, IllegalNumberException {
		String input = "\n\n\n\n/a/";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		assertEquals(4, scanner.tokens.size());
	}
	
	@Test 
	public void testString() throws IllegalCharException, IllegalNumberException {
		String input = "/abcd wonderful + & * %$ $$__hihg /* 46546467";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		assertEquals(Kind.IDENT, scanner.tokens.get(2).kind);
		assertEquals(Kind.TIMES, scanner.tokens.get(5).kind);
		assertEquals(Kind.MOD, scanner.tokens.get(6).kind);
		assertEquals(Kind.IDENT, scanner.tokens.get(7).kind);
		assertEquals(Kind.IDENT, scanner.tokens.get(8).kind);
		assertEquals(Kind.EOF, scanner.tokens.get(9).kind);
	}
	
	@Test 
	public void testOperators() throws IllegalCharException, IllegalNumberException {
		String input = ";,(){}|&==!=<><=>=+-*/%!->|-><-";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		for(Token t  : scanner.tokens){
			System.out.println("Kind :" + t.kind + "pos " + t.pos);
		}
	}
	
	@Test 
	public void testComment() throws IllegalCharException, IllegalNumberException {
		String input = "/*jhdf$787;";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		assertEquals(Kind.EOF, scanner.tokens.get(0).kind);
	}
	
	@Test 
	public void testStringKeyword() throws IllegalCharException, IllegalNumberException {
		String input = "If+*}%>=whilesleep)";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		assertEquals(Kind.IDENT, scanner.tokens.get(0).kind);
		assertEquals(Kind.MOD, scanner.tokens.get(4).kind);
		assertEquals(Kind.GE.text, scanner.tokens.get(5).kind.text);
		assertEquals(Kind.IDENT, scanner.tokens.get(6).kind);
	}
		
	@Test 
	public void testStringLit() throws IllegalCharException, IllegalNumberException {
		String input = "123)&;{!=$ab_";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		assertEquals(Kind.INT_LIT, scanner.tokens.get(0).kind);
		assertEquals(Kind.RPAREN, scanner.tokens.get(1).kind);
		assertEquals(Kind.NOTEQUAL, scanner.tokens.get(5).kind);
		assertEquals(Kind.IDENT, scanner.tokens.get(6).kind);
	}
	
	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		String input= ";;;";		
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "999999999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}	
	
	@Test
	public void testFailedTC1() throws IllegalCharException, IllegalNumberException{
		String input = "ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(Token t  : scanner.tokens){
			System.out.println("Kind :" + t.kind + "pos " + t.pos);
		}	
	}
	
	@Test
	public void testFailedTC2() throws IllegalCharException, IllegalNumberException{
		String input = "|;|--->->-|->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(Token t  : scanner.tokens){
			System.out.println("Kind :" + t.kind + "pos " + t.pos);
		}	
	}
	
	@Test
	public void testFailedTC3() throws IllegalCharException, IllegalNumberException{
		String input = "false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(Token t  : scanner.tokens){
			System.out.println("Kind :" + t.kind + "pos " + t.pos);
		}	
	}
	
	@Test
	public void testBarrow() throws IllegalCharException, IllegalNumberException{
		String input = "|->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(Token t  : scanner.tokens){
			System.out.println("Kind :" + t.kind + "pos " + t.pos);
		}	
	}
}
