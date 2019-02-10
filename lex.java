import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/*
 * Author:Vedran Pehlivanovic
 * Date:1-26-2016
 * Please see DOC file for description
 */
public class lex {

	static String checker = "";

	static int commentCounter = 0;
	static String typeDec;
	static String name;
	static String type;
	static String fcnName;
	static String fcnType;
	static String numOfParams;
	static String nameOfFcn;
	static boolean number = false;
	static Boolean fcnDec = false;
	static Boolean checkForReturn = false;
	static Boolean foundReturn = false;
	static Integer bracketCounter;
	static Boolean createTable = true;
	static Queue<String> tokens = new LinkedList<String>();
	static SymbolTable symTable = new SymbolTable();
	static ArrayList<String> paramDec = new ArrayList<>();
	static ArrayList<String> paramType = new ArrayList<>();
	static ArrayList<String> paramPass = new ArrayList<>();
	static Queue<String> expTerms = new LinkedList<String>();
	static boolean doExpr = false;
	static boolean doExprComp = false;
	static boolean callReturn = false;
	static boolean soloReturn = false;

	public static void main(String[] args) throws FileNotFoundException {

		symTable.createSymTable();

		File file = new File(args[0]);

		Scanner input = new Scanner(file);

		Queue<Character> q = new LinkedList<Character>();

		while (input.hasNextLine()) {

			String line = input.nextLine();

			if (!line.equals("")) {
				// System.out.println("Input: " + line);
			}

			char[] ary = line.toCharArray();

			for (int i = 0; i < ary.length; i++) {

				q.add(ary[i]);

			}// end for

			if (!line.equals("")) {
				typeDec = checkDec(line);
				startState(q);
			}

		}// end while
		tokens.add("$");
		// parse parser = new parse(tokens);
		symTable.checkForMain();
		// symTable.printTables();
	}// end main

	static void startState(Queue<Character> q) {
		/*
		 * This section of code checks if the first character in the queue is a
		 * letter,number, or symbol. If it's white space will remove from queue
		 * until it finds one of the three.
		 */
		if (typeDec.equals("fcnDec")) {
			paramType.clear();
			checkForReturn = true;
			symTable.createSymTable();
			createTable = false;
		}

		if (typeDec.equals("expr")) {
			doExpr = true;
		}

		while (!q.isEmpty()) {

			if (commentCounter > 0)
				blockComments(q);

			else if (Character.isLowerCase((Character) q.peek())
					&& number == false) {
				words(q);
			} else if (Character.isDigit((Character) q.peek())
					|| number == true) {

				numbers(q);
			} else if ((Character) q.peek() == ','
					&& typeDec.equals("paramDec")) {
				q.remove();
				symTable.setParamDec(name, type, fcnName);
				name = null;
				type = null;
			} else if (checkIfSymbol((Character) q.peek()) == true) {

				symbols(q);
			}

			else if (!q.isEmpty()
					&& ((Character) q.peek() == ' '
							|| (Character) q.peek() == '\t' || (Character) q
							.peek() == '\n'))
				q.remove();

			else {

				while (!q.isEmpty() && (Character) q.peek() != ' ') {

					checker += q.remove();
				}

				System.out.println("Error: " + checker);
				checker = "";
			}
		}

		if (typeDec.equals("varDec")) {
			if (type.equals("void")) {
				symTable.reject();
			}
			symTable.setVarDec(name, type);
			name = null;
			type = null;
		} else if (fcnDec == true) {
			if (type.equals("void"))
				paramType.add(type);
			symTable.setFcnDec(fcnName, fcnType, numOfParams, paramType);
			fcnDec = false;
		}

		if (typeDec.equals("paramDec")) {
			if (type.equals("void") && name == null) {

			} else {
				symTable.setParamDec(name, type, fcnName);
				name = null;
				type = null;
			}
		}

		if (typeDec.equals("rightSideExpr")) {
			symTable.typeChecking();
		}

		if (typeDec.equals("fcnPassParam")) {
			symTable.fcnCallNoExpr(paramPass);
		}

		if (bracketCounter != null) {
			if (bracketCounter == 0) {
				if (fcnType.equals("void"))
					checkForReturn = false;
				else if (checkForReturn == true
						&& (typeDec.equals("rightSideExpr") || typeDec
								.equals("unDefined"))) {
					if (foundReturn == false) {
						symTable.reject();
					}
				}
			}
		}
		if (!expTerms.isEmpty()) {
			CodeGeneration gen = new CodeGeneration();
			if (doExpr == true) {
				gen.setAssigned(expTerms);
				expTerms.clear();
				doExpr = false;
			} else if (doExprComp == true) {
				gen.setKeywordExpr(expTerms);
				expTerms.clear();
				doExpr = false;
				doExprComp = false;
			} else if (callReturn == true) {
				gen.setReturn(expTerms);
				expTerms.clear();
				doExpr = false;
				doExprComp = false;
				callReturn = false;
			}
		} else if (soloReturn == true) {
			CodeGeneration gen = new CodeGeneration();
			gen.soloReturn();
			doExpr = false;
			doExprComp = false;
			callReturn = false;
			soloReturn = false;
		}
		// System.out.println();
	}// end startState

	/*
	 * Takes in the queue and removes lower case letters until it finds a
	 * delimeter defined in the if statement
	 */
	static void words(Queue<Character> q) {

		if (typeDec.equals("unDefined")) {
			String formatedString = "";
			while (!q.isEmpty()) {
				if ((Character) q.peek() == ';' || (Character) q.peek() == '[')
					break;
				formatedString += q.remove();
			}
			typeDec = checkDec(formatedString);
			String ary[] = formatedString.split(" ");
			checker = ary[0];
			keywordStuff(q);
			checker = ary[1];
			idStuff(q);
			checker = "";
			return;
		}
		char y = '\0';

		while (!q.isEmpty() && Character.isLetter((Character) q.peek())) {
			checker += q.remove();
		}

		if (!q.isEmpty())
			y = (Character) q.peek();

		if (y == '\n' || y == '\t' || y == ' ' || Character.isDigit(y)
				|| q.isEmpty() || !Character.isLetter(y)) {
			if (doExpr == true || callReturn == true || doExprComp == true) {
				expTerms.add(checker);
			}
			if (keywords(checker) == false) {
				// System.out.println("ID: " + checker);
				tokens.add("id");
				idStuff(q);
			} else {
				tokens.add(checker);
				// System.out.println("Keyword: " + checker);

				if (checker.equals("if") || checker.equals("while")) {
					doExprComp = true;
					symTable.setKeywordExpr(q.toString());
					// symTable.createSymTable();
					typeDec = "expr";
					fcnDec = false;
				}
				keywordStuff(q);

			}

			checker = "";
		}

	}// end words

	/*
	 * reads numbers or +- or an E sets number to true so you won't call a the
	 * words method on letter E
	 */
	static void numbers(Queue<Character> q) {

		char y = '\0';
		number = true;
		checker += q.remove();
		boolean floatChecker = false;

		if (!q.isEmpty())
			y = (Character) q.peek();

		if (y == '+' || y == '-') {
			char x = checker.charAt(checker.length() - 1);
			if (x == 'E')
				floatChecker = true;
		}
		if (y == '\n' || y == '\t' || y == ' ' || q.isEmpty()
				|| (Character.isLetter(y) && y != 'E') || checkIfSymbol(y)
				&& floatChecker == false) {

			if (numberRE(checker) == true) {
				tokens.add("numInt");
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add(checker);
				if (typeDec.equals("rightSideExpr"))
					symTable.setRightSide("int");
			}
			// System.out.println("Num: " + checker);
			else if (floatRE(checker) == true) {
				tokens.add("numFloat");
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add(checker);
				if (typeDec.equals("rightSideExpr"))
					symTable.setRightSide("float");
			}
			// System.out.println("Float: " + checker);
			else
				System.out.println("Error: " + checker);
			checker = "";
			number = false;
		}

	}// end Numbers

	/*
	 * Takes care of symbols,line comments, and multi line comment
	 */
	static void symbols(Queue<Character> q) {
		String aryDis = "";
		char x = (Character) q.peek();

		switch (x) {
		case '+':
			// System.out.println(x + " Addition operator");
			tokens.add("+");
			if (doExpr == true || callReturn == true || doExprComp == true)
				expTerms.add("+");
			q.remove();
			break;
		case '-':
			// System.out.println(x + " Subtraction operator");
			tokens.add("-");
			if (doExpr == true || callReturn == true || doExprComp == true)
				expTerms.add("-");
			q.remove();
			break;
		case '*':
			// System.out.println(x + " Multiplication operator");
			tokens.add("*");
			if (doExpr == true || callReturn == true || doExprComp == true)
				expTerms.add("*");
			q.remove();
			break;
		case '/':
			q.remove();
			if (!q.isEmpty() && (Character) q.peek() == '*') {
				commentCounter++;
				q.remove();
				blockComments(q);
			} else if (!q.isEmpty() && (Character) q.peek() == '/')
				q.clear();
			else {
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add("/");
				tokens.add("/");
			}
			// System.out.println(x + " Division operator");
			break;
		case ';':
			tokens.add(";");
			if (callReturn == true && expTerms.isEmpty())
				soloReturn = true;
			q.remove();
			// System.out.println(x + " Semicolon operator");
			if (!q.isEmpty()) {
				try {
					if (type.equals("void")) {
						symTable.reject();
					}
					symTable.setVarDec(name, type);
					typeDec = checkDec(q.toString());
					name = null;
					type = null;
				} catch (Exception ex) {

				}
			}

			break;
		case ',':
			tokens.add(",");
			// System.out.println(x + " Comma operator");
			q.remove();
			break;
		case '(':
			tokens.add("(");
			if (typeDec.equals("fcnDec"))
				typeDec = "paramDec";
			if (doExpr == true || callReturn == true || doExprComp == true)
				expTerms.add("(");
			// System.out.println(x + " Left parenthesis operator");
			q.remove();
			break;
		case ')':
			tokens.add(")");
			if (doExpr == true || callReturn == true || doExprComp == true)
				expTerms.add(")");
			// System.out.println(x + " Right parenthesis operator");
			q.remove();
			break;
		case '[':
			tokens.add("[");
			if (doExpr == true || callReturn == true || doExprComp == true)
				expTerms.add("[");
			// System.out.println(x + " Left bracket operator");
			q.remove();
			while ((Character) q.peek() != ']') {
				if (doExpr == true || callReturn == true || doExprComp == true)
					aryDis += String.valueOf(q.remove());
			}
			if (aryDis != null) {
				expTerms.add(aryDis);
			}
			break;
		case ']':
			if (doExpr == true || callReturn == true || doExprComp == true)
				expTerms.add("]");
			tokens.add("]");
			// System.out.println(x + " Right bracket operator");
			q.remove();
			break;
		case '{':
			tokens.add("{");
			// System.out.println(x + " Left curly bracket operator");
			if (bracketCounter == null)
				bracketCounter = 0;
			bracketCounter++;
			if (createTable == true) {
				symTable.sendBlock();
				symTable.createSymTable();
			}
			createTable = true;
			q.remove();
			break;
		case '}':
			tokens.add("}");
			// System.out.println(x + " Right curly operator");
			q.remove();
			symTable.sendEndBlock();
			symTable.removeTable();
			bracketCounter--;

			break;
		case '<':
			q.remove();
			if (!q.isEmpty() && (Character) q.peek() == '=') {
				q.remove();
				tokens.add("<=");
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add("<=");
			}
			// System.out.println(x + "" + q.remove() +
			// " Less than or equal operator");
			else {
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add("<");
				tokens.add("<");
			}
			// System.out.println(x + "Less than operator");
			break;
		case '>':
			q.remove();
			if (!q.isEmpty() && (Character) q.peek() == '=') {
				q.remove();
				tokens.add(">=");
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add(">=");
			}
			// System.out.println(x + "" + q.remove() +
			// " Greater than or equal operator");
			else {
				tokens.add(">");
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add(">");
			}
			// System.out.println(x + "Greater than operator");
			break;
		case '=':
			q.remove();
			if (!q.isEmpty() && (Character) q.peek() == '=') {
				q.remove();
				tokens.add("==");
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add("==");
			}
			// System.out.println(x + "" + q.remove() + " Equals operator");
			else {
				tokens.add("=");
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add("=");
			}
			// System.out.println(x + " Equals assigner");
			break;
		case '!':
			q.remove();
			if (!q.isEmpty() && (Character) q.peek() == '=') {
				tokens.add("!=");
				q.remove();
				if (doExpr == true || callReturn == true || doExprComp == true)
					expTerms.add("!=");
			}
			// System.out.println(x + "" + q.remove() + " Not equal operator");
			else
				System.out.println("Error: " + x);
		default:

		}
	}

	static void blockComments(Queue<Character> q) {
		while (!q.isEmpty()) {

			char x = (Character) q.remove();

			if ((!q.isEmpty()) && (x == '/' && (Character) q.peek() == '*')) {
				q.remove();
				commentCounter++;
			} else if ((!q.isEmpty())
					&& (x == '*' && (Character) q.peek() == '/')) {
				q.remove();
				commentCounter--;
			}

			if (commentCounter == 0)
				break;
		}

	}

	static boolean keywords(String input) {

		String[] keywords = { "while", "if", "int", "float", "else", "return",
				"void" };

		for (int i = 0; i < keywords.length; i++) {

			if (input.equals(keywords[i]))
				return true;
		}

		return false;
	}// end keywords

	static boolean floatRE(String input) {

		// String patternFloatExp = "[0-9]+(.[0-9]+)?(E[+-]?[0-9]+)?";
		String patternFloatExp = "([1-9][0-9]*)[.][0-9]+([E][+-]?[0-9]+)?";
		//String patternFloat = "([1-9][0-9]*)|[0-9][.][0-9]+";

		if (input.matches(patternFloatExp))
			return true;
		return false;

	}

	static boolean numberRE(String input) {
		String pattern = "[0-9]+";
		if (input.matches(pattern))
			return true;
		return false;
	}

	static boolean checkIfSymbol(char x) {

		char[] symbols = { '+', '-', '*', '/', ';', ',', '(', ')', '[', ']',
				'{', '}', '<', '>', '=', '!' };

		for (int i = 0; i < symbols.length; i++) {

			if (x == symbols[i])
				return true;
		}

		return false;
	}

	static String checkDec(String input) {
		String line = "unDefined";
		if (input.trim().contains("(") && !input.trim().startsWith("if")
				&& !input.trim().startsWith("while")
				&& !input.trim().contains("=") && !input.trim().contains(";")) {

			line = "fcnDec";
			fcnDec = true;
			countNumOfOccurances(input);
		} else if (input.trim().startsWith("int")
				|| input.trim().startsWith("float")
				|| input.trim().startsWith("void"))
			line = "varDec";
		else if (input.trim().contains("=")) {
			line = "expr";
		} else if (input.trim().startsWith("return")) {
			line = "returnDec";
		} else if (input.trim().contains("("))
			line = "fcnCall";
		else if (input.trim().contains("else"))
			line = "else";

		return line;

	}

	static void countNumOfOccurances(String line) {
		int count = 0;
		char ary[] = line.toCharArray();

		for (int i = 0; i < ary.length; i++) {
			if (ary[i] == ',')
				count++;
		}

		numOfParams = String.valueOf(count + 1);
	}

	static void idStuff(Queue<Character> q) {
		String numberParams = "";
		if (typeDec.equals("varDec") || typeDec.equals("paramDec")) {
			if (!q.isEmpty() && (Character) q.peek() == '[') {

				q.remove();
				while (!q.isEmpty() && (Character) q.peek() != ']') {
					numberParams += q.remove();
				}
				if (numberRE(numberParams) == true) {
					if (!type.equals("int"))
						symTable.reject();
					type = type + "Ary";
					name = checker;
					symTable.setSize(Integer.parseInt(numberParams));
					if (typeDec.equals("paramDec"))
						paramType.add(type);
				} else if (floatRE(numberParams) == true) {
					if (!type.equals("float"))
						symTable.reject();
					type = type + "Ary";
					name = checker;
					symTable.setSize(Integer.parseInt(numberParams));
					if (typeDec.equals("paramDec"))
						paramType.add(type);

				} else if (numberParams.equals("")) {
					type = type + "Ary";
					name = checker;
					if (typeDec.equals("paramDec"))
						paramType.add(type);
				}
			} else {
				name = checker;
				if (typeDec.equals("paramDec"))
					paramType.add(type);
			}
		} else if (typeDec.equals("fcnDec"))
			fcnName = checker;
		else if (typeDec.equals("expr")) {
			symTable.setLeftSide(checker);
			typeDec = "rightSideExpr";
		} else if (typeDec.equals("rightSideExpr")) {
			symTable.setRightSide(checker);
		} else if (typeDec.equals("fcnCall")) {
			symTable.fcnNameCheck = checker;
			typeDec = "fcnPassParam";
		} else if (typeDec.equals("fcnPassParam")) {
			if (!q.isEmpty() && (Character) q.peek() == '[') {
				q.remove();
				while (!q.isEmpty() && (Character) q.peek() != ']') {
					numberParams += q.remove();
				}
				if (numberRE(numberParams) == true) {
					paramPass.add("int");
				} else if (floatRE(numberParams) == true) {
					paramPass.add("float");
				}
			} else {
				paramPass.add(checker);
			}
		}
	}

	static void keywordStuff(Queue<Character> q) {
		if (typeDec.equals("varDec")) {
			type = checker;
		} else if (typeDec.equals("paramDec")) {
			type = checker;
		} else if (typeDec.equals("fcnDec"))
			fcnType = checker;
		else if (typeDec.equals("returnDec")) {
			symTable.returnDec(fcnName, q.toString());
			typeDec = "rightSideExpr";
			foundReturn = true;
			callReturn = true;
		}
	}

}// end lex class