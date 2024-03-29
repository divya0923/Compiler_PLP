package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Type;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	public static class BinaryChainNode {
		boolean isLeft;
		Kind arrowOp;
		public BinaryChainNode(boolean left, Kind op) {
			this.isLeft = left;
			this.arrowOp = op;
		}
	}

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	public int slotNumber = 1;
	public int argCount = 0;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		//cw = new ClassWriter(0);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);

		//TODO visit the local variables
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, startRun, endRun, 0);

		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method

		cw.visitEnd();//end of class

		//generate classfile and return it
		return cw.toByteArray();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		Chain e0 = binaryChain.getE0();
		ChainElem e1 = binaryChain.getE1();

		/*if(e0 instanceof IdentChain) {
			((IdentChain) e0).setLeft(true);
			((IdentChain) e0).setArrowOp(binaryChain.getArrow().kind);
		}
		if(e1 instanceof IdentChain) {
			((IdentChain) e1).setLeft(false);
			((IdentChain) e1).setArrowOp(binaryChain.getArrow().kind);
		}*/

		e0.visit(this, new BinaryChainNode(true, binaryChain.getArrow().kind));

		if(e0.getTypeName().equals(TypeName.FILE)) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		}
		else if(e0.getTypeName().equals(TypeName.URL)) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
		}

		e1.visit(this, new BinaryChainNode(false, binaryChain.getArrow().kind));

		//mv.visitInsn(DUP);

		/*if(e1.getTypeName().equals(TypeName.FILE)) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		}
		else if(e1.getTypeName().equals(TypeName.URL)) {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
		}*/

		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		Expression e0 = binaryExpression.getE0();
		Expression e1 = binaryExpression.getE1();

		e0.visit(this, arg);
		e1.visit(this, arg);

		// to handle integer * image
		/*if(e0 instanceof IntLitExpression && e1 instanceof IdentExpression) {
			e1.visit(this, arg);
			e0.visit(this, arg);
		}
		else {
			e0.visit(this, arg);
			e1.visit(this, arg);
		} */

		Label startLabel = new Label();
		Label endLabel = new Label();
		switch(binaryExpression.getOp().kind) {
		case PLUS:
			if(binaryExpression.getE0().getTypeName().equals(TypeName.IMAGE) && binaryExpression.getE1().getTypeName().equals(TypeName.IMAGE))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
			else
				mv.visitInsn(IADD);
			break;
		case MINUS:
			if(binaryExpression.getE0().getTypeName().equals(TypeName.IMAGE) && binaryExpression.getE1().getTypeName().equals(TypeName.IMAGE))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
			else
				mv.visitInsn(ISUB);
			break;
		case TIMES:
			if(e0.getTypeName().isType(TypeName.INTEGER))
				mv.visitInsn(SWAP);
			if(binaryExpression.getE0().getTypeName().equals(TypeName.IMAGE) && binaryExpression.getE1().getTypeName().equals(TypeName.INTEGER) ||
					binaryExpression.getE0().getTypeName().equals(TypeName.INTEGER) && binaryExpression.getE1().getTypeName().equals(TypeName.IMAGE))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			else
				mv.visitInsn(IMUL);
			break;
		case DIV:
			if(binaryExpression.getE0().getTypeName().equals(TypeName.IMAGE) && binaryExpression.getE1().getTypeName().equals(TypeName.INTEGER))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
			else
				mv.visitInsn(IDIV);
			break;
		case MOD:
			if(binaryExpression.getE0().getTypeName().equals(TypeName.IMAGE) && binaryExpression.getE1().getTypeName().equals(TypeName.INTEGER))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			else
				mv.visitInsn(IREM);
			break;
		case LT:
			mv.visitJumpInsn(IF_ICMPLT, startLabel);
			mv.visitInsn(ICONST_0);
			break;
		case LE:
			mv.visitJumpInsn(IF_ICMPLE, startLabel);
			mv.visitInsn(ICONST_0);
			break;
		case GT:
			mv.visitJumpInsn(IF_ICMPGT, startLabel);
			mv.visitInsn(ICONST_0);
			break;
		case GE:
			mv.visitJumpInsn(IF_ICMPGE, startLabel);
			mv.visitInsn(ICONST_0);
			break;
		case EQUAL:
			if(e0.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN) ||  e1.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN))
				mv.visitJumpInsn(IF_ICMPEQ, startLabel);
			else
				mv.visitJumpInsn(IF_ACMPEQ, startLabel);
			mv.visitInsn(ICONST_0);
			break;
		case NOTEQUAL:
			if(e0.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN) ||  e1.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN))
				mv.visitJumpInsn(IF_ICMPNE, startLabel);
			else
				mv.visitJumpInsn(IF_ACMPNE, startLabel);
			mv.visitInsn(ICONST_0);
			break;
		case AND:
			mv.visitInsn(IAND);
			break;
		case OR:
			mv.visitInsn(IOR);
			break;
		default:
			break;
		}

		mv.visitJumpInsn(GOTO, endLabel);
		mv.visitLabel(startLabel);
		mv.visitInsn(ICONST_1);
		mv.visitLabel(endLabel);

		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Label startBlock = new Label();
		mv.visitLabel(startBlock);

		for(Dec dec : block.getDecs())
			dec.visit(this, arg);

		for(Statement statement : block.getStatements()){
			if(statement instanceof AssignmentStatement){
				if(((AssignmentStatement)statement).getVar().getDec() instanceof ParamDec)
					mv.visitVarInsn(ALOAD, 0);
			}
			statement.visit(this, arg);

			// TODO - validate this
			if(statement instanceof Chain)
				mv.visitInsn(POP);
		}

		Label endBlock = new Label();
		mv.visitLabel(endBlock);

		for(Dec dec: block.getDecs()) {
			mv.visitLocalVariable(dec.getIdent().getText(), dec.getTypeName().getJVMTypeDesc(), null, startBlock, endBlock, dec.getSlotNo());
		}

		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(booleanLitExpression.getValue());
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		if(constantExpression.getFirstToken().kind.equals(KW_SCREENWIDTH))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", "()I", false);
		if(constantExpression.getFirstToken().kind.equals(KW_SCREENHEIGHT))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", "()I", false);
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		declaration.setSlotNo(slotNumber++);
		if(declaration.getTypeName().isType(TypeName.IMAGE, TypeName.FRAME)) {
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getSlotNo());
		}

		/*	mv.visitTypeInsn(NEW, "java/wt/image/BufferedImage");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, PLPRuntimeImageIO.BufferedImageClassName, "<init>", "(III)V", false);
		}
		else if(declaration.getTypeName().equals(TypeName.FRAME)) {
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, 1);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
		}*/
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// handle arrow and bararrow
		if(((BinaryChainNode)arg).arrowOp.equals(Kind.ARROW))
			mv.visitInsn(ACONST_NULL);
		else {
			mv.visitInsn(DUP);
			mv.visitInsn(SWAP);
		}

		if (filterOpChain.getFirstToken().kind.equals(OP_BLUR))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);
		else if (filterOpChain.getFirstToken().kind.equals(OP_GRAY))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig , false);
		else if (filterOpChain.getFirstToken().kind.equals(OP_CONVOLVE))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig , false);

		//mv.visitInsn(DUP);
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		frameOpChain.getArg().visit(this, arg);
		if(frameOpChain.getFirstToken().kind.equals(KW_SHOW))
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc , false);
		else if(frameOpChain.getFirstToken().kind.equals(KW_HIDE))
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc , false);
		else if (frameOpChain.getFirstToken().kind.equals(KW_MOVE))
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc , false);
		else if (frameOpChain.getFirstToken().kind.equals(KW_XLOC))
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc, false);
		else if (frameOpChain.getFirstToken().kind.equals(KW_YLOC))
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc, false);
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		BinaryChainNode node = (BinaryChainNode)arg;
		//System.out.println("identChain " + identChain.getDec().getIdent().getText());
		//System.out.println("isLeft " + identChain.isLeft());
		//System.out.println("arrowOp " + identChain.getArrowOp().text);

		boolean isLeft = node.isLeft;

		//System.out.println("slotNo: " + identChain.getDec().getSlotNo());

		// LHS of binaryChain
		if(isLeft) {
			// paramDec
			if(identChain.getDec() instanceof ParamDec) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), identChain.getDec().getTypeName().getJVMTypeDesc());
			}
			// dec
			else {
				if(identChain.getTypeName().isType(TypeName.INTEGER))
					mv.visitVarInsn(ILOAD, identChain.getDec().getSlotNo());
				else
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlotNo());
			}
		}

		// RHS of binaryChain
		else {
			// paramDec
			/*if(identChain.getDec() instanceof ParamDec) {
				System.out.println("!isLeft getfield");
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), identChain.getDec().getTypeName().getJVMTypeDesc());
				// file or url
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
			}
			// dec
			else {
				System.out.println("!isLeft istore"); */
				switch(identChain.getTypeName()) {
					case INTEGER :
					case BOOLEAN:
						mv.visitInsn(DUP);
						if(identChain.getDec() instanceof ParamDec) {
							mv.visitVarInsn(ALOAD, 0);
							//mv.visitInsn(SWAP);
							mv.visitFieldInsn(PUTFIELD, className, identChain.getFirstToken().getText(), identChain.getTypeName().getJVMTypeDesc());
						}
						else
								mv.visitVarInsn(ISTORE, identChain.getDec().getSlotNo());

						break;
					case FILE:
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), identChain.getDec().getTypeName().getJVMTypeDesc());
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);

						// FIXME - validate if this is needed
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), identChain.getDec().getTypeName().getJVMTypeDesc());
						//mv.visitInsn(DUP);
						break;

					case IMAGE :
						mv.visitInsn(DUP);
						mv.visitVarInsn(ASTORE, identChain.getDec().getSlotNo());
						break;

					case FRAME:
						//mv.visitInsn(ACONST_NULL);
						mv.visitVarInsn(ALOAD, identChain.getDec().getSlotNo());
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
						mv.visitInsn(DUP);
						mv.visitVarInsn(ASTORE, identChain.getDec().getSlotNo());
						//mv.visitInsn(DUP);
					default:
						break;
				}
			//}
		}

		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Dec dec = identExpression.getDec();
		if(dec instanceof ParamDec) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.getFirstToken().getText(), identExpression.getTypeName().getJVMTypeDesc());
		}
		else {
			if(dec.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN))
				mv.visitVarInsn(ILOAD, dec.getSlotNo());
			else
				mv.visitVarInsn(ALOAD, dec.getSlotNo());
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		Dec dec = identX.getDec();
		if(dec instanceof ParamDec) {
			//mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(PUTFIELD, className, identX.getFirstToken().getText(), dec.getTypeName().getJVMTypeDesc());
		}
		else {
			if(dec.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN))
				mv.visitVarInsn(ISTORE, ((Dec) dec).getSlotNo());
			else if(dec.getTypeName().isType(TypeName.IMAGE)) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
				mv.visitVarInsn(ASTORE, dec.getSlotNo());
			}
			else {
				mv.visitVarInsn(ASTORE, dec.getSlotNo());
			}
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		ifStatement.getE().visit(this, arg);
		Label endIf = new Label();
		mv.visitJumpInsn(IFEQ, endIf);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(endIf);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		if(imageOpChain.getFirstToken().kind.equals(KW_SCALE)){
			imageOpChain.getArg().visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale",  PLPRuntimeImageOps.scaleSig, false);
		}
		else if(imageOpChain.getFirstToken().kind.equals(OP_WIDTH))
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getWidth",  PLPRuntimeImageOps.getWidthSig, false);
		else if(imageOpChain.getFirstToken().kind.equals(OP_HEIGHT))
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getHeight",  PLPRuntimeImageOps.getHeightSig, false);

		//mv.visitInsn(DUP);
		//mv.visitInsn(SWAP);

		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(intLitExpression.getValue());
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		FieldVisitor visitField = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), paramDec.getTypeName().getJVMTypeDesc(), null, null);
		visitField.visitEnd();

		mv.visitVarInsn(ALOAD, 0);

		if(paramDec.getTypeName().equals(TypeName.INTEGER)){
			mv.visitVarInsn(ALOAD, 1); // args
			mv.visitLdcInsn(argCount++); // 0
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "I");
		}

		else if(paramDec.getTypeName().equals(TypeName.BOOLEAN)){
			mv.visitVarInsn(ALOAD, 1); // args
			mv.visitLdcInsn(argCount++); // 0
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Z");
		}

		else if(paramDec.getTypeName().equals(TypeName.FILE)) {
			mv.visitTypeInsn(NEW, "java/io/File"); // ref to file
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1); // args
			mv.visitLdcInsn(argCount++); // 0
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), PLPRuntimeImageIO.FileDesc);
		}

		else if(paramDec.getTypeName().equals(TypeName.URL)) {
			mv.visitVarInsn(ALOAD, 1); // args
			mv.visitLdcInsn(argCount++); // 0
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), PLPRuntimeImageIO.URLDesc);
		}

		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false); // void sleep(long) TypeDesc of long is J
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		for(Expression e : tuple.getExprList())
			e.visit(this, arg);
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this
		Label guardLabel = new Label();
		mv.visitJumpInsn(GOTO, guardLabel);
		Label bodyLabel = new Label();
		mv.visitLabel(bodyLabel);
		whileStatement.getB().visit(this, arg);
		mv.visitLabel(guardLabel);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFNE, bodyLabel);
		return null;
	}

}
