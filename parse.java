import java.util.Queue;

/*
 * Author:Vedran Pehlivanovic
 * Date: 02/23/2016
 * 
 * Please see included documentation for details.
 */
public class parse {

	private Queue<String> tokens;

	public parse(Queue<String> tokens) {

		this.tokens = tokens;
		System.out.println(tokens);
		initalize();
	}

	private void initalize() {

		switch (tokens.peek()) {
		case "int":
		case "void":
		case "float":
			one();
		case "$":
			System.out.print("ACCEPT");
			break;
		default:
			reject();
		}
	}

	private void one() {

		switch (tokens.peek()) {
		case "int":
		case "void":
		case "float":
			three();
			two();
			break;
		default:
			reject();

		}
	}

	private void two() {

		switch (tokens.peek()) {
		case "int":
		case "void":
		case "float":
			three();
			two();
			break;
		case "$":
			return;
		default:
			reject();

		}
	}

	private void three() {

		switch (tokens.peek()) {
		case "int":
		case "void":
		case "float":
			seven();
		case "id":
			tokens.remove();
			four();
			break;
		default:
			reject();

		}
	}

	private void four() {

		switch (tokens.peek()) {
		case ";":
		case "[":
			six();
			break;
		case "(":
			tokens.remove();
			eight();
		case ")":
			tokens.remove();
			thirteen();
			break;
		default:
			reject();

		}
	}

	private void five() {

		switch (tokens.peek()) {
		case "int":
		case "void":
		case "float":
			seven();
		case "id":
			tokens.remove();
			six();
			break;
		default:
			reject();

		}
	}

	private void six() {

		switch (tokens.peek()) {
		case "[":
			tokens.remove();
		case "numInt":
			tokens.remove();
		case "]":
			tokens.remove();
		case ";":
			tokens.remove();
			break;
		default:
			reject();

		}
	}

	private void seven() {

		switch (tokens.peek()) {
		case "int":
		case "float":
		case "void":
			tokens.remove();
			break;
		default:
			reject();

		}
	}

	private void eight() {

		switch (tokens.peek()) {
		case "void":
			tokens.remove();
			nine();
			break;
		case "int":
			tokens.remove();
			nine();
			break;
		case "float":
			tokens.remove();
			nine();
			break;
		default:
			reject();

		}
	}

	private void nine() {

		switch (tokens.peek()) {
		case "id":
			tokens.remove();
			twelve();
			ten();
			break;
		case ")":
			return;
		default:
			reject();

		}

	}

	private void ten() {

		switch (tokens.peek()) {
		case ",":
			tokens.remove();
			eleven();
			ten();
			break;
		case ")":
			return;
		default:
			reject();

		}
	}

	private void eleven() {

		switch (tokens.peek()) {
		case "int":
		case "float":
		case "void":
			seven();
		case "id":
			tokens.remove();
			twelve();
			break;
		default:
			reject();

		}
	}

	private void twelve() {

		switch (tokens.peek()) {
		case "[":
			tokens.remove();
			if (tokens.peek().equals("]"))
				tokens.remove();
			break;
		case ",":
		case ")":
			return;
		default:
			reject();

		}
	}

	private void thirteen() {

		switch (tokens.peek()) {
		case "{":
			tokens.remove();
			fourteen();
			fifteen();
		case "}":
			tokens.remove();
			break;
		default:
			reject();

		}

	}

	private void fourteen() {

		switch (tokens.peek()) {
		case "int":
		case "void":
		case "float":
			five();
			fourteen();
			break;
		case "}":
		case "(":
		case "numInt":
		case "numFloat":
		case "id":
		case ";":
		case "{":
		case "if":
		case "while":
		case "return":
			return;
		default:
			reject();

		}
	}

	private void fifteen() {

		switch (tokens.peek()) {
		case "(":
		case "numInt":
		case "numFloat":
		case "id":
		case ";":
		case "{":
		case "if":
		case "while":
		case "return":
			sixteen();
			fifteen();
			break;
		case "}":
			return;
		default:
			reject();

		}
	}

	private void sixteen() {

		// here
		switch (tokens.peek()) {
		case "(":
		case "numInt":
		case "numFloat":
		case "id":
		case ";":
			seventeen();
			break;
		case "{":
			thirteen();
			break;
		case "if":
			eighteen();
			break;
		case "while":
			twenty();
			break;
		case "return":
			twentyOne();
			break;
		default:
			reject();

		}

	}

	private void seventeen() {

		switch (tokens.peek()) {
		case "(":
		case "numInt":
		case "numFloat":
		case "id":
			twentyTwo();
			if (tokens.peek().equals(";"))
				tokens.remove();
			break;
		case ";":
			tokens.remove();
			break;
		default:
			reject();

		}
	}

	private void eighteen() {

		switch (tokens.peek()) {
		case "if":
			tokens.remove();
		case "(":
			tokens.remove();
			twentyTwo();
		case ")":
			tokens.remove();
			sixteen();
			nineteen();
			break;
		default:
			reject();

		}
	}

	private void nineteen() {

		switch (tokens.peek()) {
		case "else":
			tokens.remove();
			sixteen();
			break;
		case "(":
		case "numInt":
		case "numFloat":
		case "id":
		case ";":
		case "{":
		case "if":
		case "while":
		case "return":
		case "}":
			return;
		default:
			reject();

		}
	}

	private void twenty() {

		switch (tokens.peek()) {
		case "while":
			tokens.remove();
		case "(":
			tokens.remove();
			twentyTwo();
		case ")":
			tokens.remove();
			sixteen();
			break;
		default:
			reject();

		}
	}

	private void twentyOne() {

		switch (tokens.peek()) {
		case "return":
			tokens.remove();
			seventeen();
			break;
		default:
			reject();

		}
	}

	private void twentyTwo() {

		switch (tokens.peek()) {
		case "(":
			tokens.remove();
			twentyTwo();
		case ")":
			tokens.remove();
			twentyThree();
			break;
		case "numInt":
		case "numFloat":
			thirtyNine();
			twentyThree();
			break;
		case "id":
			tokens.remove();
			twentyFour();
			break;
		default:
			reject();

		}
	}

	private void twentyThree() {

		switch (tokens.peek()) {
		case "*":
		case "/":
			thirtyTwo();
			twentyNine();
			twentySeven();
			break;
		case "+":
		case "-":
			twentyNine();
			twentySeven();
			break;
		case ">=":
		case "<":
		case "<=":
		case ">":
		case "==":
		case "!=":
			twentySeven();
			break;
		case ";":
		case ")":
		case "]":
		case ",":
			return;
		default:
			reject();

		}
	}

	private void twentyFour() {

		switch (tokens.peek()) {
		case "[":
			twentySix();
		case "*":
		case "/":
		case ">=":
		case "<=":
		case "<":
		case ">":
		case "!=":
		case "+":
		case "-":
		case "(":
		case "=":
		case "==":
			twentyFive();
			break;
		case ";":
		case ")":
		case "]":
		case ",":
			return;
		default:
			reject();

		}
	}

	private void twentyFive() {

		switch (tokens.peek()) {
		case "=":
			tokens.remove();
			twentyTwo();
			break;
		case "(":
			tokens.remove();
			thirtySix();
		case ")":
			tokens.remove();
			twentyThree();
			break;
		case "*":
		case "/":
		case ">=":
		case "<=":
		case "<":
		case ">":
		case "!=":
		case "+":
		case "-":
		case "==":
			twentyThree();
			break;
		case ";":
		case ",":
		case "[":
			return;
		default:
			reject();

		}
	}

	private void twentySix() {

		switch (tokens.peek()) {
		case "[":
			tokens.remove();
			twentyTwo();
		case "]":
			tokens.remove();
			break;
		case "=":
		case "(":
		case "*":
		case "/":
		case ">=":
		case "<=":
		case "<":
		case ">":
		case "==":
		case "!=":
			return;
		default:
			reject();

		}
	}

	private void twentySeven() {

		switch (tokens.peek()) {
		case "<=":
		case ">=":
		case "<":
		case ">":
		case "==":
		case "!=":
			twentyEight();
			thirtyOne();
			twentyNine();
			break;
		case ";":
		case ")":
		case "]":
		case ",":
			return;
		default:
			reject();

		}
	}

	private void twentyEight() {

		switch (tokens.peek()) {
		case ">=":
		case "<=":
		case "<":
		case ">":
		case "==":
		case "!=":
			tokens.remove();
			break;
		default:
			reject();

		}
	}

	private void twentyNine() {

		switch (tokens.peek()) {
		case "+":
		case "-":
			thirty();
			thirtyOne();
			twentyNine();
			break;
		case ">=":
		case "<=":
		case "<":
		case ">":
		case "==":
		case "!=":
		case ";":
		case ")":
		case "]":
		case ",":
			return;
		default:
			reject();

		}
	}

	private void thirty() {

		switch (tokens.peek()) {
		case "+":
		case "-":
			tokens.remove();
			break;
		default:
			reject();

		}
	}

	private void thirtyOne() {

		switch (tokens.peek()) {
		case "(":
		case "numInt":
		case "numFloat":
		case "id":
			thirtyFour();
			thirtyTwo();
			break;
		default:
			reject();

		}
	}

	private void thirtyTwo() {

		switch (tokens.peek()) {
		case "*":
		case "/":
			thirtyThree();
			thirtyFour();
			thirtyTwo();
		case "+":
		case "-":
		case ";":
		case ")":
		case "}":
		case ",":
		case ">=":
		case "<=":
		case "<":
		case ">":
		case "==":
		case "!=":
			return;
		default:
			reject();

		}
	}

	private void thirtyThree() {

		switch (tokens.peek()) {
		case "/":
		case "*":
			tokens.remove();
			break;
		default:
			reject();

		}
	}

	private void thirtyFour() {

		switch (tokens.peek()) {
		case "(":
			tokens.remove();
			twentyTwo();
		case ")":
			tokens.remove();
			break;
		case "numInt":
		case "numFloat":
			thirtyNine();
			break;
		case "id":
			tokens.remove();
			thirtyFive();
			break;
		default:
			reject();

		}
	}

	private void thirtyFive() {

		switch (tokens.peek()) {
		case "[":
			twentySix();
			break;
		case "(":
			tokens.remove();
			thirtySix();
		case ")":
			tokens.remove();
			break;
		case "*":
		case "/":
		case "+":
		case "-":
		case ";":
		case "]":
		case ",":
		case ">=":
		case "<=":
			return;
		default:
			reject();

		}
	}

	private void thirtySix() {

		switch (tokens.peek()) {
		case "(":
		case "numInt":
		case "numFloat":
		case "id":
			thirtySeven();
			break;
		case ")":
			return;
		default:
			reject();

		}
	}

	private void thirtySeven() {

		switch (tokens.peek()) {
		case "(":
		case "numInt":
		case "numFloat":
		case "id":
			twentyTwo();
			thirtyEight();
			break;
		default:
			reject();

		}

	}

	private void thirtyEight() {

		switch (tokens.peek()) {
		case ",":
			tokens.remove();
			twentyTwo();
			thirtyEight();
			break;
		case ")":
			return;
		default:
			reject();

		}

	}

	private void thirtyNine() {

		switch (tokens.peek()) {
		case "numInt":
		case "numFloat":
			tokens.remove();
			break;
		default:
			reject();

		}

	}

	private void reject() {
		System.out.println(tokens.toString());
		System.out.print("REJECT");
		System.exit(0);
	}

}