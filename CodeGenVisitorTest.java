
package cop5556sp17;

import java.io.FileOutputStream;
import java.io.OutputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;

public class CodeGenVisitorTest {

	static final boolean doPrint = true;
	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	boolean devel = false;
	boolean grade = true;


	@Test
	public void emptyProg() throws Exception {
		//scan, parse, and type check the program
		String progname = "emptyProg";
		String input = progname + "  {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show(program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel,grade,null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		System.out.println("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[0]; //create command line argument array to initialize params, none in this case
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void testProg1() throws Exception {
		//scan, parse, and type check the program
		String progname = "TestClass";
		String input = progname + " integer x, boolean y {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show(program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel,grade,null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		System.out.println("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[0]; //create command line argument array to initialize params, none in this case
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void testProg2() throws Exception {
		//scan, parse, and type check the program
		String progname = "TestClass1";
		String input = progname + " integer x, integer y { integer z boolean a x <- 10; y <- 20; z <- 10; a <- true; if(a) { integer bb }}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show(program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel,grade,null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		System.out.println("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[0]; //create command line argument array to initialize params, none in this case
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}


}
