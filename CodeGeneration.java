import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class CodeGeneration {

	static LinkedHashMap<String, ArrayList<String>> fcnTable;
	static LinkedHashMap<String, ArrayList<String>> paramTable;
	static LinkedHashMap<String, ArrayList<String>> varTable;
	static ArrayList<String> arrays = new ArrayList<>();
	static int index = 1;
	static String placeHolder = "_t";
	static int blockCounter = 0;
	static int end;
	static int begin;
	static String relOP;
	static int arraySize;
	static String counter = "0";
	static String fcnBlockName;
	static String fcnName1;
	static boolean ayy = false;
	static String numberr;
	static String aryDisplacement;
	static ArrayList<String> paramArrays = new ArrayList<>();
	static boolean callParam = false;
	static boolean created = false;
	String assigned;

	static ArrayList<String> bigTable = new ArrayList<>();

	public CodeGeneration() {

		if (created == false) {
			bigTable.add("INDEX");
			bigTable.add("OP");
			bigTable.add("OPND");
			bigTable.add("OPND");
			bigTable.add("RESULT");
			created = true;
		}
		// System.out.format("%-7s%-10s%-7s%-7s%-7s", "INDEX", "OP", "OPND",
		// "OPND", "RESULT\n");

	}

	void assignFcn(String name, LinkedHashMap<String,ArrayList<String>> fcnTable) {
		this.fcnTable = fcnTable;
		printAssignFcn(name);

	}

	void printAssignFcn(String name) {
		String fcnName = name;
		String type;
		String numOfParams;
		fcnBlockName = name;
		type = fcnTable.get(fcnName).get(1);
		if (type.equals("void"))
			numOfParams = "0";
		else {

			numOfParams = fcnTable.get(fcnName).get(2);
			if (!fcnName.equals("main"))
				numberr = numOfParams;
		}

		bigTable.add(String.valueOf(index));
		bigTable.add("func");
		bigTable.add(fcnName);
		bigTable.add(type);
		bigTable.add(numOfParams);
		/*
		 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "func", fcnName,
		 * type, numOfParams); System.out.println();
		 */
		index++;

		callParam = true;

	}

	void assignParam(String name, LinkedHashMap<String,ArrayList<String>> paramTable) {
		this.paramTable = paramTable;
		printAssignParam(name);
	}

	void printAssignParam(String name) {
		String paramName;
		String type;
		String numOfBytes = "0";

		paramName = name;
		type = paramTable.get(paramName).get(1);
		if (type.equals("int") || type.equals("float"))
			numOfBytes = "4";

		paramArrays.add("param");
		paramArrays.add(" ");
		paramArrays.add(" ");
		paramArrays.add(" ");
		/*
		 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "param", "", "",
		 * ""); System.out.println();
		 */

		paramArrays.add("alloc");
		paramArrays.add(numOfBytes);
		paramArrays.add(" ");
		paramArrays.add(paramName);
		/*
		 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "alloc",
		 * numOfBytes, paramName); System.out.println();
		 */
		if (callParam == true) {
			for (int i = 0; i < paramArrays.size(); i++) {
				if (i % 4 == 0) {
					bigTable.add(String.valueOf(index));
					bigTable.add(paramArrays.get(i));
					index++;
				} else
					bigTable.add(paramArrays.get(i));

			}
			callParam = false;
		}
	}

	void assignVar(String name, LinkedHashMap<String,ArrayList<String>> varTable) {
		this.varTable = varTable;
		printAssignVar(name);
	}

	void printAssignVar(String name) {
		String varName;
		String type;
		String numOfBytes = "0";

		varName = name;
		type = varTable.get(varName).get(1);
		if (type.equals("int") || type.equals("float"))
			numOfBytes = "4";
		else if (type.equals("intAry") || type.equals("floatAry")) {
			arrays.add(varName);
			numOfBytes = String.valueOf(Integer.parseInt("4") * arraySize);
		}

		/*
		 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "alloc",
		 * numOfBytes, " ", varName); System.out.println();
		 */
		bigTable.add(String.valueOf(index));
		bigTable.add("alloc");
		bigTable.add(numOfBytes);
		bigTable.add(" ");
		bigTable.add(varName);
		index++;
	}

	public void setArraySize(int size) {
		arraySize = size;
	}

	public void doExpr(Queue<String> exprInputs) {
		Queue<String> inputs = new LinkedList<>();
		if (ayy == true) {
			while (!exprInputs.isEmpty()) {
				if (exprInputs.peek().equals(fcnName1))
					exprInputs.remove();
				else
					inputs.add(exprInputs.remove());
			}
		}
		String output;
		InToPost theTrans;
		if (ayy == true)
			theTrans = new InToPost(inputs);
		else
			theTrans = new InToPost(exprInputs);
		output = theTrans.doTrans();
		// output = output.replaceAll("\\s+", "");
		// char ary[] = output.toCharArray();

		Stack<String> s = new Stack<String>();
		Queue<String> tokens = new LinkedList<String>();
		boolean dis = true;
		// for (int i = 0; i < ary.length; i++) {
		// if (ary[i] == '[')
		// dis = false;
		// if (ary[i] == ']') {
		// i++;
		// dis = true;
		// }
		// if (dis == true)
		// if (i < ary.length){
		// tokens.add(Character.toString(ary[i]));
		// }
		//
		// }

		String[] ary = output.split(" ");

		for (int i = 0; i < ary.length; i++) {
			if (ary[i].equals("["))
				dis = false;
			if (ary[i].equals("]")) {
				i++;
				dis = true;
			}
			if (i < ary.length) {
				if (dis == true && !ary[i].equals(""))
					tokens.add(ary[i]);

			}
		}
		while (!tokens.isEmpty()) {
			if (checkOPs(tokens.peek()) == false) {
				s.push(tokens.remove());
			} else {
				String a = s.pop();
				String b = s.pop();
				String op = tokens.remove();
				if (isArray(a)) {
					checkForArray(output, a);
					bigTable.add(String.valueOf(index));
					bigTable.add("disp");
					bigTable.add(a);
					bigTable.add(String.valueOf(Integer
							.valueOf(aryDisplacement) * 4));
					bigTable.add(placeHolder + counter);
					a = placeHolder + counter;
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
				}
				if (isArray(b)) {
					checkForArray(output, b);
					bigTable.add(String.valueOf(index));
					bigTable.add("disp");
					bigTable.add(b);
					if (isNumeric(aryDisplacement)) {
						bigTable.add(String.valueOf(Integer
								.valueOf(aryDisplacement) * 4));
						bigTable.add(placeHolder + counter);
					} else {
						bigTable.add(aryDisplacement);
						bigTable.add(placeHolder + counter);
					}
					b = placeHolder + counter;
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
				}

				if (op.equals("+")) {
					/*
					 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "add",
					 * b, a, placeHolder + counter); System.out.println();
					 */
					bigTable.add(String.valueOf(index));
					bigTable.add("add");
					bigTable.add(b);
					bigTable.add(a);
					bigTable.add(placeHolder + counter);
					s.push(placeHolder + counter);
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
				} else if (op.equals("-")) {
					/*
					 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "sub",
					 * b, a, placeHolder + counter); System.out.println();
					 */
					bigTable.add(String.valueOf(index));
					bigTable.add("sub");
					bigTable.add(b);
					bigTable.add(a);
					bigTable.add(placeHolder + counter);
					s.push(placeHolder + counter);
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
				} else if (op.equals("*")) {
					/*
					 * (System.out.format("%-7s%-10s%-7s%-7s%-7s", index,
					 * "times", b, a, placeHolder + counter);
					 * System.out.println();
					 */
					bigTable.add(String.valueOf(index));
					bigTable.add("times");
					bigTable.add(b);
					bigTable.add(a);
					bigTable.add(placeHolder + counter);
					s.push(placeHolder + counter);
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
				} else {
					/*
					 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "div",
					 * b, a, placeHolder + counter); System.out.println();
					 */
					bigTable.add(String.valueOf(index));
					bigTable.add("div");
					bigTable.add(b);
					bigTable.add(a);
					bigTable.add(placeHolder + counter);
					s.push(placeHolder + counter);
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
				}
			}
		}
		int temp = Integer.valueOf(counter);
		temp = temp - 1;
		/*
		 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "assign",
		 * placeHolder + temp, " ", assigned); System.out.println();
		 */
		if (ayy == true) {
			String num;
			ayy = false;
			if (containsOPS(output) == false) {
				if (output.length() > 1) {
					String[] ary1 = output.split(" ");
					for (int i = 0; i < ary1.length; i++) {
						if (!ary1[i].equals("")) {
							bigTable.add(String.valueOf(index));
							bigTable.add("arg");
							bigTable.add(" ");
							bigTable.add(" ");
							bigTable.add(String.valueOf(ary[i]));
							index++;
						}
					}
				} else {
					bigTable.add(String.valueOf(index));
					bigTable.add("arg");
					bigTable.add(" ");
					bigTable.add(" ");
					bigTable.add(output);
					index++;
				}
			} else {
				bigTable.add(String.valueOf(index));
				bigTable.add("arg");
				bigTable.add(" ");
				bigTable.add(" ");
				bigTable.add(placeHolder + temp);
				index++;
			}

			bigTable.add(String.valueOf(index));
			bigTable.add("call");
			bigTable.add(fcnName1);
			bigTable.add(numberr);
			bigTable.add(placeHolder + temp);
			index++;

		}
		if (assigned.equals("return")) {
			bigTable.add(String.valueOf(index));
			bigTable.add(assigned);
			bigTable.add(" ");
			bigTable.add(" ");
			if (output.length() == 2) {
				bigTable.add(output);
			} else {
				bigTable.add(placeHolder + (temp));
			}
			temp = Integer.valueOf(counter);
			temp++;
			counter = String.valueOf(temp);
			index++;
		} else {
			if (!s.isEmpty()) {
				String item = s.pop();
				if (isArray(item) == true) {
					checkForArray(output, item);
					bigTable.add(String.valueOf(index));
					bigTable.add("disp");
					bigTable.add(item);
					bigTable.add(String.valueOf(Integer
							.valueOf(aryDisplacement) * 4));
					bigTable.add(placeHolder + counter);
					temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
					bigTable.add(String.valueOf(index));
					bigTable.add("assign");
					bigTable.add(placeHolder + Integer.valueOf(temp - 1));
					bigTable.add(" ");
					bigTable.add(assigned);
					index++;
				} else {
					bigTable.add(String.valueOf(index));
					bigTable.add("assign");
					bigTable.add(placeHolder + temp);
					bigTable.add(" ");
					bigTable.add(assigned);
					index++;
				}
			}

		}
	}

	public boolean checkOPs(String x) {
		char ary[] = { '-', '+', '/', '*' };

		for (int i = 0; i < ary.length; i++) {
			if (x.equals(Character.toString(ary[i]))) {
				return true;
			}
		}
		return false;
	}

	public void setAssigned(Queue<String> exprInputs) {
		assigned = exprInputs.toString();

		assigned = assigned.replaceAll("\\s+", "");
		assigned = assigned.replaceAll(",", "");
		assigned = assigned.substring(1, assigned.length() - 2);
		int index = assigned.indexOf('=');
		String input = assigned.substring(index + 1);
		assigned = assigned.substring(0, index);
		while (!exprInputs.peek().equals("=")) {
			exprInputs.remove();
		}
		if (exprInputs.peek().equals("="))
			exprInputs.remove();
		doExpr(exprInputs);
	}

	public void setKeywordExpr(Queue<String> exprInputs) {
		String expr = exprInputs.toString();
		expr = expr.replaceAll("\\s+", "");
		expr = expr.replaceAll(",", "");
		expr = expr.substring(2, expr.lastIndexOf(")"));
		int index = expr.indexOf(String.valueOf(findOP(expr)));
		String input = expr.substring(index + 1);
		expr = expr.substring(0, index);
		compExpr(exprInputs, input);

	}

	boolean OP(String expr) {

		switch (expr) {
		case ">":
			return true;
		case ">=":
			return true;
		case "<":
			return true;
		case "<=":
			return true;
		case "==":
			return true;
		}
		return false;
	}

	String findOP(String expr) {

		char ary[] = expr.toCharArray();
		for (int i = 0; i < expr.length(); i++) {

			switch (Character.toString(ary[i])) {
			case ">":
				relOP = "BRLT";
				return ">";
			case ">=":
				relOP = "BRLEQ";
				return ">=";
			case "<":
				relOP = "BRGT";
				return "<";
			case "<=":
				relOP = "BRGEQ";
				return "<=";
			case "==":
				relOP = "BREQ";
				return "==";
			}
		}
		return "Didn't find symbol";
	}

	void compExpr(Queue<String> leftSideInput, String rightSide) {
		int controller = 0;
		String output;
		Queue<String> leftSide = new LinkedList<>();
		while (!leftSideInput.isEmpty()) {
			if (OP(leftSideInput.peek()) == true)
				break;
			else
				leftSide.add(leftSideInput.poll());
		}
		InToPost theTrans = new InToPost(leftSide);
		output = theTrans.doTrans();
		output = output.substring(1);
		Stack<String> s = new Stack<String>();
		Queue<String> tokens = new LinkedList<String>();
		String[] ary = output.split(" ");

		for (int i = 0; i < ary.length; i++) {
			if (!ary[i].equals(""))
				tokens.add(ary[i]);
		}

		while (!tokens.isEmpty()) {
			if (checkOPs(tokens.peek()) == false) {
				s.push(tokens.remove());
			} else {
				String a = s.pop();
				String b = s.pop();
				String op = tokens.remove();

				if (op.equals("+")) {
					/*
					 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "add",
					 * a, b, placeHolder + counter); System.out.println();
					 */

					bigTable.add(String.valueOf(index));
					bigTable.add("add");
					bigTable.add(b);
					bigTable.add(a);
					bigTable.add(placeHolder + counter);
					s.push(placeHolder + counter);
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
					controller++;
				} else if (op.equals("-")) {
					/*
					 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "sub",
					 * b, a, placeHolder + counter); System.out.println();
					 */
					bigTable.add(String.valueOf(index));
					bigTable.add("sub");
					bigTable.add(b);
					bigTable.add(a);
					bigTable.add(placeHolder + counter);
					s.push(placeHolder + counter);
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
					controller++;
				} else if (op.equals("*")) {
					/*
					 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index,
					 * "times", b, a, placeHolder + counter);
					 * System.out.println();
					 */
					bigTable.add(String.valueOf(index));
					bigTable.add("times");
					bigTable.add(b);
					bigTable.add(a);
					bigTable.add(placeHolder + counter);
					s.push(placeHolder + counter);
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
					controller++;
				} else {
					/*
					 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "div",
					 * b, a, placeHolder + counter); System.out.println();
					 */
					bigTable.add(String.valueOf(index));
					bigTable.add("div");
					bigTable.add(b);
					bigTable.add(a);
					bigTable.add(placeHolder + counter);
					s.push(placeHolder + counter);
					int temp = Integer.valueOf(counter);
					temp++;
					counter = String.valueOf(temp);
					index++;
					controller++;
				}
				if (controller == 1)
					begin = index - 1;
			}
		}
		/*
		 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "comp", placeHolder
		 * + String.valueOf((Integer.valueOf(counter)-1)), rightSide,
		 * placeHolder + counter); System.out.println();
		 */
		bigTable.add(String.valueOf(index));
		bigTable.add("comp");
		if (containsOPS(output) == false) {
			leftSide.remove();
			bigTable.add(leftSide.remove());
			bigTable.add(rightSide);
			bigTable.add(placeHolder + counter);
			int temp = Integer.valueOf(counter);
			temp++;
			counter = String.valueOf(temp);
			index++;
		} else {
			bigTable.add(placeHolder
					+ String.valueOf((Integer.valueOf(counter) - 1)));
			bigTable.add(rightSide);
			bigTable.add(placeHolder + counter);
			int temp = Integer.valueOf(counter);
			temp++;
			counter = String.valueOf(temp);
			index++;
		}

		int temp = Integer.valueOf(counter);
		temp = temp - 1;
		/*
		 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, relOP, placeHolder
		 * + temp, " ", " "); System.out.println();
		 */
		bigTable.add(String.valueOf(index));
		bigTable.add(relOP);
		bigTable.add(placeHolder + temp);
		bigTable.add(" ");
		bigTable.add("endV");

		counter = String.valueOf(temp + 1);
		index++;

	}

	public void blocks() {
		/*
		 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "block", " ", " ",
		 * " "); System.out.println();
		 */
		bigTable.add(String.valueOf(index));
		bigTable.add("block");
		bigTable.add(" ");
		bigTable.add(" ");
		bigTable.add(" ");
		index++;
		blockCounter++;
	}

	public void endBlocks() {
		if (blockCounter > 0) {
			/*
			 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "end", "block",
			 * " ", " "); System.out.println();
			 */
			bigTable.add(String.valueOf(index));
			bigTable.add("end");
			bigTable.add("block");
			bigTable.add(" ");
			bigTable.add(" ");
			index++;
			/*
			 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "BR", " ", " ",
			 * " "); System.out.println();
			 */
			bigTable.add(String.valueOf(index));
			bigTable.add("BR");
			bigTable.add(" ");
			bigTable.add(" ");
			bigTable.add("beginV");
			index++;

			blockCounter--;

			end = index;
		} else if (blockCounter == 0) {
			/*
			 * System.out.format("%-7s%-10s%-7s%-7s%-7s", index, "end",
			 * "func",fcnBlockName, " "); System.out.println();
			 */
			bigTable.add(String.valueOf(index));
			bigTable.add("end");
			bigTable.add("func");
			bigTable.add(fcnBlockName);
			bigTable.add(" ");
			index++;
			for (int i = 0; i < bigTable.size(); i++) {
				if (i % 5 == 0 && i > 0)
					System.out.println();
				else if (bigTable.get(i).equals("endV"))
					bigTable.set(i, String.valueOf(end));
				else if (bigTable.get(i).equals("beginV"))
					bigTable.set(i, String.valueOf(begin));

				System.out.format("%-8s", bigTable.get(i));

			}
			System.out.println();
			bigTable.clear();
		}
	}

	public void setReturn(Queue<String> input) {
		try {
			assigned = "return";
			doExpr(input);
		} catch (Exception ex) {
		}

	}

	public void setFcnNameForExpr(String input) {
		fcnName1 = input;
		ayy = true;
	}

	public void checkForArray(String output, String var) {
		output = output.replaceAll("\\s+", "");
		int begin = output.indexOf(var);
		int end = output.indexOf("]");
		aryDisplacement = output.substring(begin + 2, end);

	}

	public boolean isArray(String a) {
		for (int i = 0; i < arrays.size(); i++) {
			if (a.equals(arrays.get(i)))
				return true;
		}

		return false;
	}

	public boolean isNumeric(String s) {
		return s.matches("[-+]?\\d*\\.?\\d+");
	}

	boolean containsOPS(String output) {
		if (output.contains("+") || output.contains("-")
				|| output.contains("*") || output.contains("/")) {
			return true;
		}
		return false;
	}

	public void soloReturn() {
		bigTable.add(String.valueOf(index));
		bigTable.add("return");
		bigTable.add(" ");
		bigTable.add(" ");
		bigTable.add(" ");
		index++;

	}
}