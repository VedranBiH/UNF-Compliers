
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.io.IOException;

public class InToPost {
   private Stack<String> theStack;
   private ArrayList<String> input;
   private String output = "";
   public InToPost(Queue<String> in) {
      input = new ArrayList<String>(in);
      int stackSize = 10;
      theStack = new Stack<String>();
   }
   
  public String doTrans() {
      for (int j = 0; j < input.size(); j++) {
         String ch = input.get(j);
         switch (ch) {
            case "+": 
            case "-":
            gotOper(ch, 1); 
            break; 
            case "*": 
            case "/":
            gotOper(ch, 2); 
            break; 
            case "(": 
            theStack.push(ch);
            break;
            case ")": 
            gotParen(ch); 
            break;
            default: 
            output = output + " " + ch; 
            break;
         }
      }
      while (!theStack.isEmpty()) {
         output = output + " " + theStack.pop();
      }
      return output; 
   }
   public void gotOper(String opThis, int prec1) {
      while (!theStack.isEmpty()) {
         String opTop = theStack.pop();
         if (opTop.equals("(")) {
            theStack.push(opTop);
            break;
         }
         else {
            int prec2;
            if (opTop.equals("+") || opTop.equals("-"))
            prec2 = 1;
            else
            prec2 = 2;
            if (prec2 < prec1) { 
               theStack.push(opTop);
               break;
            }
		    else
            output = output + " " + opTop;
         }
      }
      theStack.push(opThis);
   }
   public void gotParen(String ch){ 
      while (!theStack.isEmpty()) {
         String chx = theStack.pop();
         if (chx.equals("(")) 
         break; 
         else
         output = output + " "+ chx; 
      }
   }
}