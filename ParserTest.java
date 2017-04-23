package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	@Test
	public void testExpression0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "false%true-screenwidth<5";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.expression();
	}

	@Test
	public void testExpression1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.expression();
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(Token t : scanner.tokens)
			System.out.println(t.kind + " " + t.getText());
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
	}

	@Test
	public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(3*5/4&6+5==4)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(3*5/4&6+5==4)";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}

	@Test
	public void testArg1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(3*5/4&6+5==4 , false%true-screenwidth<5)";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}

	@Test
	public void testArg2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = " ";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}


	@Test
	public void testArg3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "screenwidth";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
		parser.factor();
	}

	@Test
	public void testArg4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(screenwidth screenwidth)";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}

	@Test
	public void testAssign0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "$abc<-(3==5)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.assign();
	}

	@Test
	public void testAssign1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.assign();
	}

	@Test
	public void testChain0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "$495 -> blur";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}

	@Test
	public void testChain1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "$abc->gray(true,false)->blur(3)->convolve5->show->hide->move->xloc->yloc->width->height->scale";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(Token t : scanner.tokens)
			System.out.println(t.kind + " " + t.getText());
		Parser parser = new Parser(scanner);
		parser.chain();
	}

	@Test
	public void testParamDec0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url a4657676";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
	}

	@Test
	public void testDec0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url a4657676";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}

	@Test
	public void testDec1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "frame a4657676";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}

	@Test
	public void testStatement0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "sleep false%true-screenwidth<5 ;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}

	@Test
	public void testStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "while (false%true-screenwidth<5) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}

	@Test
	public void testStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "if (false%true-screenwidth<5) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}

	@Test
	public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "$abc|->gray(true,false)->blur(3)->convolve->show->hide->move->xloc->yloc->width->height->scale;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}

	@Test
	public void testStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "$abc<-(3==5);";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}

	@Test
	public void testStatement5() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}

	@Test
	public void testBlock0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "{}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.block();
	}

	@Test
	public void testBlock1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "{$abc<-(3==5);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();

		Parser parser = new Parser(scanner);
		parser.block();
	}

	@Test
	public void testBlock2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "{$abc<-(3==5)}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}

	@Test
	public void testBlock3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "{while (false%true-screenwidth<5) {$abc<-(3==5);}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.block();
	}

	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "abc url sleep5 , file jd78 {while (false%true-screenwidth<5) {$abc<-(3==5);}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.program();
	}

	@Test
	public void testProgram2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "abc url sleep5 , file jd78 {while (false%true-screenwidth<5) {$abc<-(3==5);}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.program();
	}

	@Test
	public void testProgram3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "_ boolean $ {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.program();
	}

	@Test
	public void testProgram4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "abc \n \n { ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}


	@Test
	public void testParse0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "abc {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.parse();
	}

	@Test
	public void testFTC1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "__ {__->_|->$0|-> show (__/_%($$TAT$T_T%$)|true*screenwidth&$|_*$!=_==_>=(z_z),_&$|_$+_0); while (__==$$!=$_/_){sleep z$_2+_3z%$;} blur -> width ($);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.parse();
	}


}
