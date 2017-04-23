package cop5556sp17;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
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

	@Before
	public void initLog(){
	if (devel || grade) PLPRuntimeLog.initLog(); }

	@After
	public void printLog(){
	System.out.println(PLPRuntimeLog.getString());
	}

	@Test
	public void emptyProg() throws Exception {
        //initLog();
        //scan, parse, and type check the program
        //String progname = "emptProg";
       //String input = "subImage url u {image i image j image k frame f \nu -> i; \nu -> j; \n k <- i-j; k -> f -> show;\n}"; //FAIL
       // String input = "readFromURLandWriteToFile2 url u, \nfile out \n{image i frame f \nu -> gray  -> i;\n i -> f -> show; \n i -> out;\n}"; //FAIL
      // String input = "readFromURLandWriteToFile3 url u, \nfile out \n{\nu -> gray  -> out;\n}"; //PASS
        //String input = "sleepImg url u {image i frame f \nu -> i -> convolve -> f -> show;sleep 5;integer j j <- 42;\n}"; //FAIL
        //TODO check if add is working
       //String input = "addImage url u, url u1 {image i image j image k frame f \nu -> i; \nu1 -> j; \n k <- i-j; k -> f -> show;  sleep 3000; k <- k+i; k -> f -> show; \n}";//FAIL
        //String input = "convolveImg url u {image i frame f \nu -> i -> convolve -> f -> show;\n}";//fAIL
      // String input = "readFromURLandDisplay url u {image i frame f \nu -> i;i -> f -> show;\n}";//FAIL
       // String input = "imgMove2 url u {image i frame f \nu -> i;i -> f -> show; \nsleep 5000; \ninteger x \ninteger y \nx <- screenwidth;\ny <-  screenheight; \nf -> move (x*3/4,y/4) -> show;\n}";//FAIL
     // String input = "subImage2 url u {image i image j  frame f \nu -> i; \nu -> j; \n i <- i-j; i -> f -> show;\n}";//FAIL major
      //  String input = "modImage url u {image i image j  frame f \nu -> j; \n i <- j%128; i -> f -> show;\n}"; //FAIL MAJOR
        //String input = "allTheOps file u {image i frame f \nu -> i -> gray -> convolve -> blur -> i -> f -> show;\n}";//FAIL MAJOR
       // String input = "readFromURLandDisplayDisplay2 url u {image i frame f \nu -> i -> f->hide->show->xloc;\n}";//FAIL
       // String input = "readFromURLandWriteToFile url u, \nfile out \n{image i frame f \nu -> i;i -> f -> show; \n i -> out;\n}";
        // String input = "blurImg url u {image i frame f \nu -> i -> blur -> f -> show;\n}";
      // String input = "grayImg url u {image i frame f \nu -> i -> gray -> f -> show;\n}";
       // String input = "divImage url u {image i image j frame f \nu -> j; \n i <- j/2; i -> f -> show;\n}";
        //String input = "readFromURLandWriteScaledImageToFile url u, \nfile out \n{image i frame f \nu -> scale (3) -> i;i -> f -> show; \n i -> out;\n}";
      //String input = "barArrowGray url u {image i frame f \nu -> i |-> gray -> f -> show;\n}";
      //  String input = "assignImage url u {image i image j frame f \nu -> i; j <- i;j -> f -> show;\n}";
       //  String input = "scaleImage url u {image i frame f \nu -> i -> f -> show; sleep 3000; frame f2 \ni -> scale (3) -> f2 -> show;\n}";
        //String input = "assignImageAndFrame url u {image i image i1 frame f frame f1\nu -> i -> f -> show; frame f2 \ni -> scale (3) -> f2 -> show; \n i1 <- i; \n f2 <- f;\n}";
       // String input =  "imgMove url u {image i frame f \nu -> i;i -> f -> show; \nsleep 5; \ninteger x \ninteger y \nf -> xloc -> x; \nf -> yloc -> y; \nf -> move (x+100,y-100) -> show;\n}";
		//String input = "readFromURLandWriteToFile url u, \nfile out \n{image i frame f \nu -> i;i -> f -> show; \n i -> out;\n}";
		// String input = "readFromFile file u {image i frame f \nu -> i;i -> f -> show; \n}";
//		String input = "addImage url u {image i image j image k frame f \nu -> i; \nu -> j; \n k <- i-j; k -> f -> show; sleep 5; k <- k + i; k -> f -> show; \n}";
	//	String input = "emptyProg file f, url u {image i image j frame fr u->i; j<-i; i->gray->fr; fr->show; sleep(3000);fr->hide; sleep(3000);j->fr; fr->show; sleep(3000); i->fr; fr->show; sleep(3000);}";
		//String input = "identExprBooleanLocal  {boolean i boolean j i<-false; j <- i;";
		//String input = "emptyProg file f, url u {image i image j frame fr u->i; j<-i; i|->gray->fr; fr->show; sleep(3000);fr->hide; sleep(3000);j->fr; fr->show; sleep(3000); i->fr; fr->show; sleep(3000);}";
		// String input = "prog1 file file1, integer itx, boolean b1{ integer ii1 boolean bi1 \n image IMAGE1 frame fram1 sleep (1000) itx+ii1; while (b1){if(bi1)\n{sleep ii1+itx*2;}}\nfile1->blur |->gray;fram1 ->yloc;\n IMAGE1->blur->scale (ii1+1)|-> gray;\nii1 <- 12345+54321;}";
		//String input = "compProg0 { integer a0 a0<-0;if(a0 == 0){integer a00 integer b00 integer c00 integer d00 integer e00 e00 <- 5; d00 <- 4; c00 <- 3; b00 <- 2; a00 <- 1; if(a00 == 1){integer a01 integer b01 integer c01 integer d01 integer e01 e01 <- 55; d01 <- 44; c01 <- 33; b01 <- 22; a01 <- 11; }}} ";
		//String input = "compProg1 integer a, integer b, integer c, boolean bool0 { a <- 4;  b <- 5; boolean boolA  boolean boolB  boolA <- true;  boolB <- false;  if(boolA == true)  {boolean a a <- boolA; bool0 <- false;while(a != boolB){integer d  integer e c <- 3 + 5; d <- 10 - 1; c <- c * d; e <- d / 3; a <- boolB;if(c > d) {     c <- d;     if(c <= d)     {        boolA <- false;    }    if(boolA < boolB)     {        c <- 0;    }}} } if(c >= 1) {     /*boolB <- bool0 | true;*/} a <- 7;}";

		//String input = "compProg2 integer x, integer y, integer z, boolean bool_1, boolean bool_2 { \nx <- 100; \ny <- x / 3 * 2; \nz <- y; \nbool_1 <- false; \nbool_2 <- true; \ninteger y \ny <- z + 20; \nz <- y; \nif(bool_2){ \nboolean bool_1 \nbool_1 <- bool_2; \n} \nif(bool_1) { \ninteger err \nerr <- 2333; \n} \ninteger pass_token \npass_token <- 0; \nwhile(pass_token != 4) { \ninteger local_1 \ninteger local_2 \nlocal_1 <- 45; \nlocal_2 <- 46; \nif(local_1 != local_2) {pass_token <- pass_token + 1;} \nif(local_1 == local_2) {pass_token <- pass_token + 1;} \nif(local_1 > local_2) {pass_token <- pass_token + 1;} \nif(local_1 >= 45) {pass_token <- pass_token + 1;} \nif(local_1 < local_2) {pass_token <- pass_token + 1;} \nif(46 <= local_2) {pass_token <- pass_token + 1;} \nif((local_1 > local_2)) {pass_token <- pass_token + 1;} \n} \n} ";
		//String input = "whileifwhileStatement0{\ninteger i \ninteger j \ninteger t \ni <-10; \nj <-1; \nt <-2; \nwhile (i > 0) {\ninteger k \nk <-i/j; \nif (k > 1) {\nwhile (t > 0) {t <- t-1;} \nj <- j+1;} \ni<-i-1;} \ni<-t;}";
		//String input = "booleanComp2 { boolean a boolean b boolean c a<-true;b<-false; c<-a<b;c<-a<=b;c<-a>b;c<-a>=b;c<-a==b;c<-a!=b;a<-false; b<-true;c<-a<b;c<-a<=b;c<-a>b;c<-a>=b;c<-a==b;c<-a!=b;a<-true; b<-true;c<-a<b;c<-a<=b;c<-a>b;c<-a>=b;c<-a==b;c<-a!=b;a<-false; b<-false;c<-a<b;c<-a<=b;c<-a>b;c<-a>=b;c<-a==b;c<-a!=b;}";
		//String input = "ifStatement2{\ninteger i \ninteger j \ni <-10; \nj <-0; \nif (i > 5) {j <- j+1; \nif (i > 7){ j <- j + 1; \nif (i > 8){ j <- j + 1;}\n}\n}\n}";
		//String input = "ifStatement4 {integer local_int0\ninteger local_int1\nlocal_int0 <- 42;local_int1 <- 43;if(local_int0 == local_int1){integer local_int11 \n local_int11 <- 44;} if(local_int0 != local_int1){integer local_int22 \n local_int22 <- 45;}if(local_int0 != local_int1){integer local_int33 \n local_int33 <- 46;integer local_int44 \n local_int44 <- 47;}}";
		//String input = "ifwhileifStatement0{\ninteger i \ninteger j \ninteger k \nboolean b \ni <-10; \nj <-1; \nb <-true; \nif (b) {\nwhile (i > 0) {\nk <-i/j; \nif (k > 1) {j <- j+1;} \ni<-i-1;} \ni<-0;}\n}";
		//String input = "assignParam integer i, boolean b {i<-33; b<-false;}";
		String input = "ifStatement4 {integer local_int0\ninteger local_int1\nlocal_int0 <- 42;local_int1 <- 43;if(local_int0 == local_int1){integer local_int11 \n local_int11 <- 44;} if(local_int0 != local_int1){integer local_int22 \n local_int22 <- 45;}if(local_int0 != local_int1){integer local_int33 \n local_int33 <- 46;integer local_int44 \n local_int44 <- 47;}}";
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
	//String[] args = new String[0]; //create command line argument array to initialize params, none in this case
		//String[] args = new String[]{"/Users/dmahendran/Desktop/test2.jpg", "https://upload.wikimedia.org/wikipedia/commons/3/39/C_Hello_World_Program.png", "https://upload.wikimedia.org/wikipedia/commons/3/39/C_Hello_World_Program.png",  "test.png"}; //create command line argument array to initialize params, none in this case"/images/imgin-2.jpg"
		// String[] args = new String[]{"https://upload.wikimedia.org/wikipedia/commons/3/39/C_Hello_World_Program.png", "test.png"}; //create command line argument array to initialize params, none in this case"/images/imgin-2.jpg"
		String[] args = new String[] {"45", "43", "27", "false", "false"};
		//String[] args = new String[]{"images/imgin-2.jpg"}; //create command line argument array to initialize params, none in this case
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog1() throws Exception {
		//scan, parse, and type check the program
		String input ="identExprBooleanParam boolean i { boolean j j <- i;}";
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
		 //create command line argument array to initialize params, none in this case
		String[] args = new String[]{"true"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog2() throws Exception {
		//scan, parse, and type check the program
		String input ="exprComp { integer a  a <- 1 - 3 * (2 + 1 -6) +2 + 5/2;} ";
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
		 //create command line argument array to initialize params, none in this case
		String[] args = new String[0];
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog3() throws Exception {
		//scan, parse, and type check the program
		String input ="compProg1 integer a, integer b, integer c, boolean bool0 { a <- 4;  b <- 5; boolean boolA  boolean boolB  boolA <- true;  boolB <- false;  if(boolA == true)  {boolean a a <- boolA; bool0 <- false;while(a != boolB){integer d  integer e c <- 3 + 5; d <- 10 - 1; c <- c * d; e <- d / 3; a <- boolB;if(c > d) {     c <- d;     if(c <= d)     {        boolA <- false;    }    if(boolA < boolB)     {        c <- 0;    }}} } if(c >= 1) {     /*boolB <- bool0 | true;*/} a <- 7;}";
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
		 //create command line argument array to initialize params, none in this case
		String[] args = new String[]{"2", "3", "4", "true"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog4() throws Exception {
		//scan, parse, and type check the program
		String input ="checkConstantExpr { integer x integer y x <- screenwidth; y <- screenheight; sleep 10;}";
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
		 //create command line argument array to initialize params, none in this case
		String[] args = new String[0];
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}



	@Test
	public void prog5() throws Exception {
		//scan, parse, and type check the program
		String input ="prog file in { integer s integer h s <- screenwidth; h <- screenheight; image i integer x integer y image j x <- 3; y <- 10; in -> i; in -> j; i <- 3 * i; y <- y % x;  }";
		//String input ="prog url u { image i in -> i;}";
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
		 //create command line argument array to initialize params, none in this case
		String[] args = new String[]{"/Users/dmahendran/Desktop/test.jpg"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog6() throws Exception {
		//scan, parse, and type check the program
		//String input ="testImg url u, file f { image i u -> gray -> i -> f; }";
		String input ="testImg url u { image i integer x u -> i; x <- 3; i <- 3 * i;}";
		//String input ="prog url u { image i in -> i;}";
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
		 //create command line argument array to initialize params, none in this case
		String[] args = new String[]{"http://www.avajava.com/images/avajavalogo.jpg" , "/Users/dmahendran/Desktop/test23.jpg"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog7() throws Exception {
		String name = "prog7";
		String input = name + " {boolean x boolean y x <- true; y <- false; x <- x | y;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("/Users/dmahendran/Desktop/test2.jpg");
		args[1] = new String("/Users/dmahendran/Desktop/test4.jpg");
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog8() throws Exception {
		String name = "prog8";
		String input = name + " file f1, file f2 {\n image i image j f1->i; j <- i; j -> f2;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("/Users/dmahendran/Desktop/test2.jpg");
		args[1] = new String("/Users/dmahendran/Desktop/test22.jpg");
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog9() throws Exception {
		String name = "prog9";
		String input = name + " file f1 {\n image i integer j f1 -> i -> height -> j; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("/Users/dmahendran/Desktop/test2.jpg");
		args[1] = new String("/Users/dmahendran/Desktop/test22.jpg");
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog10() throws Exception {
		String name = "prog10";
		String input = name + " file in {\n image i frame fr in -> i -> fr -> show; sleep(3000);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("/Users/dmahendran/Desktop/test2.jpg");
		args[1] = new String("/Users/dmahendran/Desktop/test22.jpg");
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog11() throws Exception {
		String name = "prog10";
		String input = name + " file in {\n image i frame fr in -> i -> fr -> show; sleep(3000);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("/Users/dmahendran/Desktop/test2.jpg");
		args[1] = new String("/Users/dmahendran/Desktop/test22.jpg");
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog12() throws Exception {
		String name = "prog12";
		String input =  name + "file u { image i frame j u -> i; i -> j; image l l <- i; frame z l -> z; z -> show; sleep(2000);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("/Users/dmahendran/Desktop/test2.jpg");
		args[1] = new String("/Users/dmahendran/Desktop/test22.jpg");
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void prog13() throws Exception {
		String name = "prog13";
		String input =  name + " file u { image i u -> i -> gray; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("/Users/dmahendran/Desktop/test1.jpg");
		//args[1] = new String("/Users/dmahendran/Desktop/test22.jpg");
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	// readFromURLandWriteToFile2 url u, \nfile out \n{image i frame f \nu -> gray -> i;\n i -> f -> show; \n i -> out;\n}

	@Test
	public void failedTC1() throws Exception {
		String name = "readFromURLandWriteToFile2";
		String input =  name + " url u, \n file out \n {image i frame f \nu -> gray -> i;\n i -> f -> show; \n i -> out;\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("https://s-media-cache-ak0.pinimg.com/736x/61/22/cb/6122cb371a319afa82c5d4e8077ebbdc.jpg");
		args[1] = new String("/Users/dmahendran/Desktop/testcase1.jpg");

		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

	@Test
	public void failedTC2() throws Exception {
		String name = "addImage";
		String input =  name + " url u { image i image j image k frame f \nu -> i; \nu -> j; \n k <- i-j; k -> f -> show; sleep 5; k <- k + i; k -> f -> show; \n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show("\n\n" + program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		//write byte code to file
		String classFileName = "bin/" + ((Program) program).getName() + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		show("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[2]; //create String[] array to initialize params

		args[0] = new String("https://s-media-cache-ak0.pinimg.com/736x/61/22/cb/6122cb371a319afa82c5d4e8077ebbdc.jpg");
		args[1] = new String("/Users/dmahendran/Desktop/testcase1.jpg");

		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}
}
