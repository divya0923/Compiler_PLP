package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {
	HashMap<String,Kind> separator = new HashMap<String,Kind>();
	HashMap<String,Kind> operator = new HashMap<String,Kind>();
	HashMap<String,Kind> keyword = new HashMap<String,Kind>();
	HashMap<String,Kind> boolean_literal = new HashMap<String,Kind>();
	HashMap<String,Kind> filter_op_keyword = new HashMap<String,Kind>();
	HashMap<String,Kind> image_op_keyword = new HashMap<String,Kind>();
	HashMap<String,Kind> frame_op_keyword = new HashMap<String,Kind>();
	String intLitPattern = ".*^\\d.*";
	String startPattern = "[a-zA-Z|\\$|\\_]";
	String partPattern = "[a-zA-Z|\\$|\\_|\\d]+";
	Pattern startComp = Pattern.compile(startPattern);
	Pattern partComp = Pattern.compile(partPattern);
	Pattern intComp = Pattern.compile(intLitPattern);
	ArrayList<Integer> lines_start = new ArrayList<Integer>();

	/**
	 * Kind enum
	 */
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"),
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"),
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"),
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"),
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"),
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="),
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"),
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"),
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"),
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"),
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"),
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}

	/**
	 * Thrown by Scanner when an illegal character is encountered
	 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}

	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;

		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;

		//returns the text of this Token
		public String getText() {
			return chars.substring(pos,  Math.min(pos + length, chars.length()));
		}

		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			LinePos obj;
			int lineNo = 0, posInLine = 0;
			boolean flag = false;
			int i =0;
			for(i=0;i<lines_start.size();i++)
			{
				if(this.pos==lines_start.get(i))
					{lineNo = i;
					flag = true;}
				else if (this.pos < lines_start.get(i))
					{lineNo = i - 1;
				    i = lines_start.size();
				    flag = true;}
			}
			if(flag==false)
				lineNo = i-1;
			posInLine = this.pos - lines_start.get(lineNo);
			obj = new LinePos(lineNo, posInLine);

			return obj;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/**
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 *
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			return Integer.parseInt(chars.substring(pos,  Math.min(pos + length, chars.length())));
		}

		@Override
		public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		}

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }

	}

	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}

	public void initDataStructures()
	{
		separator.put(";", Kind.SEMI);
		separator.put(",", Kind.COMMA);
		separator.put("(", Kind.LPAREN);
		separator.put(")", Kind.RPAREN);
		separator.put("{", Kind.LBRACE);
		separator.put("}", Kind.RBRACE);

		operator.put("|", Kind.OR);
		operator.put("&", Kind.AND);
		operator.put("==", Kind.EQUAL);
		operator.put("!=", Kind.NOTEQUAL);
		operator.put("<", Kind.LT);
		operator.put(">", Kind.GT);
		operator.put("<=", Kind.LE);
		operator.put(">=", Kind.GE);
		operator.put("+", Kind.PLUS);
		operator.put("-", Kind.MINUS);
		operator.put("*", Kind.TIMES);
		operator.put("/", Kind.DIV);
		operator.put("%", Kind.MOD);
		operator.put("!", Kind.NOT);
		operator.put("->", Kind.ARROW);
		operator.put("|->", Kind.BARARROW);
		operator.put("<-", Kind.ASSIGN);

		boolean_literal.put("true", Kind.KW_TRUE);
		boolean_literal.put("false", Kind.KW_FALSE);

		keyword.put("integer", Kind.KW_INTEGER);
		keyword.put("boolean", Kind.KW_BOOLEAN);
		keyword.put("image", Kind.KW_IMAGE);
		keyword.put("url",Kind.KW_URL );
		keyword.put("file", Kind.KW_FILE);
		keyword.put("frame", Kind.KW_FRAME);
		keyword.put("while", Kind.KW_WHILE);
		keyword.put("if", Kind.KW_IF);
		keyword.put("sleep", Kind.OP_SLEEP);
		keyword.put("screenheight", Kind.KW_SCREENHEIGHT);
		keyword.put("screenwidth", Kind.KW_SCREENWIDTH);

		filter_op_keyword.put("gray",Kind.OP_GRAY );
		filter_op_keyword.put("convolve", Kind.OP_CONVOLVE);
		filter_op_keyword.put("blur", Kind.OP_BLUR);
		filter_op_keyword.put("scale", Kind.KW_SCALE);

		image_op_keyword.put("width", Kind.OP_WIDTH);
		image_op_keyword.put("height", Kind.OP_HEIGHT);

		frame_op_keyword.put("xloc",Kind.KW_XLOC);
		frame_op_keyword.put("yloc",Kind.KW_YLOC);
		frame_op_keyword.put("hide",Kind.KW_HIDE);
		frame_op_keyword.put("show",Kind.KW_SHOW);
		frame_op_keyword.put("move",Kind.KW_MOVE);

		lines_start.add(0);
	}


	public Kind findEnum(String str) throws IllegalCharException
	{
		Matcher matcher = partComp.matcher(str);
		Matcher intmatcher = intComp.matcher(str);

		if(operator.containsKey(str))
		{
			return operator.get(str);
		}
		else if(separator.containsKey(str))
		{
			return separator.get(str);
		}
		else if (keyword.containsKey(str))
		{
			return keyword.get(str);
		}
		else if (filter_op_keyword.containsKey(str))
		{
			return filter_op_keyword.get(str);
		}

		else if (image_op_keyword.containsKey(str))
		{
			return image_op_keyword.get(str);
		}
		else if (boolean_literal.containsKey(str))
		{
			return boolean_literal.get(str);
		}

		else if (frame_op_keyword.containsKey(str))
		{
			return frame_op_keyword.get(str);
		}
		else if (intmatcher.matches()){
			return Kind.INT_LIT;
		}
		else if (matcher.matches()){
			return Kind.IDENT;
		}
		else {
			throw new IllegalCharException("IllegalCharException - Illegal character " + str + " encountered");
		}
	}
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 *
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		initDataStructures();
		StringBuilder str = new StringBuilder();
		str.append("");
		int i=0;
		for(i=0;i<chars.length();i++)
		{
			// Condition for separator.
			Character ch = chars.charAt(i);
			String check = ch.toString();

			//System.out.println("$"+ch);

			//"show\r\n hide \n move \n file"

			if(str.length()>0 && str.toString().startsWith("/*"))
			{
			 if(check.equals("/"))
				{
					if(str.toString().endsWith("*"))
					{
						str.setLength(0);
					}
					else
					{
						str.append(check);
					}

				}
			 else if(ch=='\n')
			 {
					lines_start.add(i+1);
					//System.out.println(lines_start);

			 }
				else
				str.append(check);
			}
			else if(Character.isWhitespace(ch))
			{
				if(ch=='\n')
				{
					lines_start.add(i+1);

					//System.out.println(lines_start);

				}

				if(str.length()>0)
					{createToken(str.toString(), i - str.length(), str.length());
					str.setLength(0);
					}
			}
			else if(separator.containsKey(check))
			{
				if(str.length()>0){
					//TO IMPLEMENT CHECK METHOD
					tokens.add(new Token(findEnum(str.toString()),i - str.length(),str.length()));
					str.setLength(0);
				}
				 tokens.add(new Token(separator.get(check),i,1));
			}

			else if(operator.containsKey(check) || check.equals("=")  )
			{
				if(str.length()>0)
				{
					Kind findEnum = null;
					if(!(str.toString().equals("=") || str.toString().equals("|-")))
						findEnum = findEnum(str.toString());
					if(str.toString().equals("|-") || str.toString().equals("=") || operator.containsKey(findEnum.getText()) )
					{
						switch(check)
						{
								case "-" : {
									if(str.toString().equals("<"))
										{tokens.add(new Token(Kind.ASSIGN,i-1,2));str.setLength(0);}
									else if(str.toString().equals("|"))
										{
												if(i+1<chars.length()&&!(chars.charAt(i+1)=='>'))
												{
												createToken(str.toString(),i-str.length(), str.length());
												str.setLength(0);
												str.append(check);
												}
												else if(i==chars.length()-1)
												{
													createToken(str.toString(),i-str.length(), str.length());
													str.setLength(0);
													str.append(check);
												}
												else
												str.append(check);
										}
									else
									{
										createToken(str.toString(),i-str.length(), str.length());
										str.setLength(0);
										str.append(check);
									}
									break;
							   }

								case ">":{
									if(str.toString().equals("-"))
										{
										tokens.add(new Token(Kind.ARROW,i-1,2));str.setLength(0);}
									else if(str.toString().equals("|-"))
									{
										tokens.add(new Token(Kind.BARARROW,i-2,3));str.setLength(0);}
									else
									{
										createToken(str.toString(), i-str.length(), str.length());
										str.setLength(0);
										str.append(check);
									}
									break;
								}


								case "=":
								{
									if(str.toString().equals("="))
									{
										tokens.add(new Token(Kind.EQUAL,i-1,2));str.setLength(0);

									}
									else if (str.toString().equals(">"))
									{
										tokens.add(new Token(Kind.GE,i-1,2));str.setLength(0);

									}
									else if (str.toString().equals("<"))
									{
										tokens.add(new Token(Kind.LE,i-1,2));str.setLength(0);

									}
									else if (str.toString().equals("!"))
									{
										tokens.add(new Token(Kind.NOTEQUAL,i-1,2));str.setLength(0);

									}
									else{
										createToken(str.toString(), i-str.length(), str.length());
										str.setLength(0);
										str.append(check);
									}
									break;
								}

								case "|":{
									if(str.length() > 0)
									{
										createToken(str.toString(), i-str.length(), str.length());
										//tokens.add(new Token(findEnum,i,0));
										str.setLength(0);
									}
									str.append("|");
									break;
								}
								case "!":{
									if(str.length() > 0)
									{
										createToken(str.toString(), i-str.length(), str.length());
										str.setLength(0);
									}
									str.append("!");
									break;
								}

								case "*":{
									if(str.toString().equals("/"))//||str.toString().startsWith("/*"))
										str.append(check);
									else
									{
										tokens.add(new Token(findEnum,i-1,str.length()));str.setLength(0);
										Kind findEnum1 = findEnum(check.toString());
										tokens.add(new Token(findEnum1,i,1));str.setLength(0);
									}
									break;
								}
								case "<":{
									tokens.add(new Token(findEnum,i-1,str.length()));str.setLength(0);
									str.append(check);
									break;
								}
								default:
								{
									tokens.add(new Token(findEnum,i-1,str.length()));str.setLength(0);
									Kind findEnum1 = findEnum(check.toString());
									tokens.add(new Token(findEnum1,i,1));str.setLength(0);
								}

						}
					}
					else{
					 tokens.add(new Token(findEnum,i-str.length(),str.length()));
					 str.setLength(0);
					 str.append(check);
					}
				}
				else{ // else of if(str!= null)
					str.append(check);
				}

			} // else of operator if

			else if (Character.isDigit(ch)){
				Kind findEnum = null;
				Matcher partMatcher = null;
				if(str.length() > 0){
					partMatcher = partComp.matcher(str);
					findEnum = findEnum(str.toString());
				}
				if(str.length() == 0){
					str.append(ch.toString());
				}
				else if(str.toString().equals("0")) {
					createToken(str.toString(), i-str.length(), str.length());
					str.setLength(0);
					str.append(ch.toString());
					//throw new IllegalCharException("IllegalCharException - NumLit cannot start with 0");
				}
				else if(operator.containsKey(findEnum.getText())){
					createToken(str.toString(), i-str.length(), str.length());
					str.setLength(0);
					str.append(ch.toString());
				}
				else if(partMatcher.find()){
					str.append(ch);
				}
				else {
					throw new IllegalCharException("IllegalCharException - Illegal character" + check +  " in NumLit");

				}
			}

			else if(Character.isLetter(ch) || check.equals("$") || check.equals("_")){
				Kind findEnum = null;
				Matcher numLitMatcher = null;
				//Matcher partMatcher = null;
				if(str.length() > 0){
					numLitMatcher = intComp.matcher(str);
					//partMatcher = partComp.matcher(str);
					findEnum = findEnum(str.toString());
				}
				if(str.length() == 0 ){
					Matcher matches = startComp.matcher(check);
					if(matches.find()){
						str.append(check);
					}
					else {
						throw new IllegalCharException("IllegalCharException - Illegal start of IDENT " + check);
					}
				}
				else if(operator.containsKey(findEnum.getText())) {
					createToken(str.toString(), i-str.length(), str.length());
					str.setLength(0);
					Matcher matches = startComp.matcher(check);
					if(matches.find()){
						str.append(check);
					}
					else {
						throw new IllegalCharException("IllegalCharException - Illegal start of IDENT " + check);
					}
				}
				else if(numLitMatcher.find()){
					createToken(str.toString(), i-str.length(), str.length());
					str.setLength(0);
					str.append(ch);
				}
				else {
					str.append(check);
				}
			}
			else {
				throw new IllegalCharException("IllegalCharException - Illegal character encountered " + check );
			}

		} // end for

		if(str.length() > 0){
			if(str.toString().startsWith("/*"))
				{str.setLength(0);}
			else{
					Kind findEnum = findEnum(str.toString());
					checkIntRange(str.toString(), findEnum);
					if(findEnum == null){
						throw new IllegalCharException("IllegalCharException - Illegal character encountered  " + str);
					}
					else {
						tokens.add(new Token(findEnum, i - str.length() , str.length()));
					}
			}
		}
		tokens.add(new Token(Kind.EOF, i, 1));
	//	System.out.println("AEFFSDFSFSFD"+lines_start.size());
		return this;
	}

	void createToken(String str, int pos, int line) throws IllegalNumberException, IllegalCharException{
		Kind findEnum = findEnum(str);
		checkIntRange(str.toString(), findEnum);
		if(findEnum == null){
			//System.out.println("Error in create token");
			throw new IllegalCharException("IllegalCharException");
		}
		else {
			tokens.add(new Token(findEnum, pos, line));
		}
	}

	void checkIntRange(String str, Kind kind) throws IllegalNumberException{
		if(kind.equals(Kind.INT_LIT)){
			try{
				Integer.parseInt(str);
			}
			catch(NumberFormatException ex){
				throw new IllegalNumberException("NumberFormatException - Integer value out of range");
			}
		}
	}

	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum=0;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}

	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);
	}

	/**
	 * Returns a LinePos object containing the line and position in line of the
	 * given token.
	 *
	 * Line numbers start counting at 0
	 *
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		return t.getLinePos();
	}

}


