import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

class SymbolTable {
	static ArrayList<String> varDec;
	static ArrayList<String> fcnDec;
	static ArrayList<String> paramDec;
	static Hashtable<String, ArrayList<String>> fcnhashTable = new Hashtable<>();
	static Queue<String> tokens = new LinkedList<>();
	static Hashtable<String, ArrayList<String>> hashtable;
	static ArrayList<Hashtable<String, ArrayList<String>>> storage = new ArrayList<>();
	private String leftSide;
	private ArrayList<String> rightSide = new ArrayList<>();
	static boolean callFcn = false;
	static boolean checkParams = false;
	static boolean keywordExpr = false;
	static boolean foundMain = false;
	static boolean voidCallFcn = false;
	public String fcnNameCheck;
	static Queue<String> exprInput;

	CodeGeneration codeGen = new CodeGeneration();
	static LinkedHashMap<String, ArrayList<String>> output;

	public SymbolTable() {

	}

	public void setSize(int size) {

		codeGen.setArraySize(size);
	}

	void createSymTable() {
		hashtable = new Hashtable<String, ArrayList<String>>();
		storage.add(hashtable);
	}

	void setVarDec(String name, String type) {
		if (hashtable.containsKey(name) == false) {
			varDec = new ArrayList<String>();
			varDec.add("var");
			varDec.add(type);
			hashtable.put(name, varDec);

			output = new LinkedHashMap<>();
			output.put(name, varDec);
			codeGen.assignVar(name, output);
		} else {
			reject();
		}

	}

	void setFcnDec(String name, String returnType, String numOfParams,
			ArrayList<String> paramType) {

		if (name.equals("main")) {
			if (!returnType.equals("void"))
				reject();
			if (!paramType.get(0).equals("void"))
				reject();
		}

		if (fcnhashTable.containsKey(name) == false) {
			if (foundMain == true) {
				reject();
			}
			fcnDec = new ArrayList<String>();
			fcnDec.add("fcn");
			fcnDec.add(returnType);
			fcnDec.add(numOfParams);
			fcnDec.addAll(paramType);
			fcnhashTable.put(name, fcnDec);

			output = new LinkedHashMap<>();
			output.put(name, fcnDec);
			codeGen.assignFcn(name, output);
			if (name.equals("main"))
				foundMain = true;
		} else {
			reject();
		}
	}

	void setParamDec(String name, String type, String nameOfFcn) {

		if (hashtable.containsKey(name) == false) {
			paramDec = new ArrayList<String>();
			paramDec.add("param");
			paramDec.add(type);
			paramDec.add(nameOfFcn);
			hashtable.put(name, paramDec);

			output = new LinkedHashMap<>();
			output.put(name, paramDec);
			codeGen.assignParam(name, output);

		} else {
			reject();
		}
	}

	void printTables() {
		for (int i = 0; i < storage.size(); i++) {
			System.out.println(storage.get(i).toString());
		}
	}

	void removeTable() {
		storage.remove(storage.size() - 1);
	}

	String lookUp(String input) {

		for (int i = storage.size() - 1; i >= 0; i--) {

			if (storage.get(i).containsKey(input) == true) {
				return storage.get(i).get(input).get(1);
			}
			if (fcnhashTable.containsKey(input) == true) {
				fcnNameCheck = input;
				return fcnhashTable.get(input).get(1);
			}
		}

		if (input.equals("int") || input.equals("float"))
			return input;

		reject();
		return null;
	}

	void setRightSide(String input) {

		checkDec(input);

		if (callFcn == true) {
			codeGen.setFcnNameForExpr(input);
			typeCheckFcn(lookUp(input));
		} else {
			rightSide.add(lookUp(input));
		}

	}

	void setLeftSide(String input) {

		leftSide = (lookUp(input));
		if (leftSide.equals("void")) {
			voidCallFcn = true;
			fcnNameCheck = input;

		}
	}

	void typeChecking() {

		if (voidCallFcn == true) {
			voidCallFcnCheck();
		} else if (checkParams == true) {

			if (rightSide.size() == findSize()) {
				int y = 3;
				for (int i = 0; i < rightSide.size(); i++) {
					String type = findFcn(y);
					if (!type.equals(rightSide.get(i)))
						reject();
					y++;
				}
			}

			else {
				reject();
			}

		}

		else {
			if (keywordExpr == true && rightSide.size() == 0) {
				keywordExpr = false;
				return;
			} else if (leftSide.equals("void") && rightSide.isEmpty()) {

			} else if (leftSide != null && rightSide.size() == 0) {
				reject();
			}

			for (int i = 0; i < rightSide.size(); i++) {
				if (leftSide.contains("Ary")) {
					checkArrayTypes();
				}
				if (rightSide.get(i).contains("Ary"))
					rightSide.set(i, rightSide.get(i).replace("Ary", ""));
				if (!rightSide.get(i).equals(leftSide)) {
					reject();
				}
			}
		}

		rightSide.clear();
		leftSide = null;
		checkParams = false;

	}

	void checkDec(String input) {

		if (fcnhashTable.containsKey(input) == true) {

			callFcn = true;
			fcnNameCheck = input;
			return;
		} else
			callFcn = false;

	}

	void typeCheckFcn(String input) {

		if (!input.equals(leftSide))
			reject();
		else
			checkParams = true;
	}

	void reject() {
	}

	public boolean getCheckParams() {
		return checkParams;
	}

	int findSize() {

		String size = null;

		if (fcnhashTable.containsKey(fcnNameCheck) == true)
			size = fcnhashTable.get(fcnNameCheck).get(2);
		else
			reject();

		return Integer.valueOf(size);
	}

	String findFcn(int y) {

		String blah = null;

		if (fcnhashTable.containsKey(fcnNameCheck) == true)
			return fcnhashTable.get(fcnNameCheck).get(y);

		return blah;
	}

	public void returnDec(String input, String codeGenInput) {
		
		try {
			if (fcnhashTable.containsKey(input) == true) {
				leftSide = (fcnhashTable.get(input).get(1));
			}
		} catch (Exception ex) {

		}
	}

	public void checkForMain() {

		boolean control = false;
		if (fcnhashTable.containsKey("main") == true) {
			control = true;
		}

		if (control == false) {
			reject();
		}
	}

	public void setKeywordExpr(String input) {
		keywordExpr = true;
	}
	
	void voidCallFcnCheck() {

		int y = 3;
		int size = findSize();
		if (fcnhashTable.get(fcnNameCheck).get(3).equals("void")
				&& rightSide.isEmpty()) {

		} else {
			if (size != rightSide.size()) {
				reject();
			}

			for (int i = 0; i < rightSide.size(); i++)
				if (fcnhashTable.containsKey(fcnNameCheck) == true) {
					String type = fcnhashTable.get(fcnNameCheck).get(y);
					if (!type.equals(rightSide.get(i))) {
						reject();
					}
					y++;
				}
		}

	}

	public void fcnCallNoExpr(ArrayList<String> input) {

		if (fcnhashTable.containsKey(fcnNameCheck) == true) {

			if (fcnhashTable.get(fcnNameCheck).get(3).equals("void")
					&& input.isEmpty()) {

			}

			else {
				int size = findSize();
				int y = 3;
				ArrayList<String> fcnParamTypes = input;

				if (fcnParamTypes.size() != size)
					reject();
				else {
					for (int i = 0; i < fcnParamTypes.size(); i++) {

						if (!lookUp(fcnParamTypes.get(i)).equals(
								fcnhashTable.get(fcnNameCheck).get(y)))
							reject();
						y++;
					}
				}
			}
		} else
			reject();
	}

	void checkArrayTypes() {
		String ary = "Ary";
		leftSide = leftSide.replace(ary, "");

	}

	public void setExpr(Queue<String> exprInput) {
		this.exprInput = exprInput;
		// codeGen.setAssigned(input);
	}

	public void sendBlock() {
		codeGen.blocks();
	}

	public void sendEndBlock() {
		codeGen.endBlocks();
	}

}