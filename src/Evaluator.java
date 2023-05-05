import java.io.InputStream;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Simulate a PDA to evaluate a series of postfix expressions provided by a lexer.
 * The constructor argument is the lexer of type Lexer. A single line is evaluated and its
 * value is printed. Expression values can also be assigned to variables for later use.
 * If no variable is explicitly assigned, then the default variable "it" is assigned
 * the value of the most recently evaluated expression.
 *
 * @author Gavin Boland
 */
public class Evaluator {

   // State transition variables
   private static final int Q1 = 1;
   private static final int Q2 = 2;
   private static final int Q3 = 3;
   private static final int Q4 = 4;
   private static final int ACCEPTANCE = 5;
   private static final int REJECT = -1;
    
    
   /**
    * Run the desk calculator.
    */
   public static void main(String[] args) {
      Evaluator evaluator = new Evaluator(new Lexer(System.in));
      evaluator.run();
   }

   private Lexer lexer; // providing a stream of tokens
   private LinkedList<Double> stack; // operands
   private HashMap<String, Double> symbols; // symbol table for variables
   private String target; // variable assigned the latest expression value
   private int currState;

   public Evaluator(Lexer lexer) {
      this.lexer = lexer;
      stack = new LinkedList<>();
      symbols = new HashMap<>();
      target = "it";
   }

   /**
    * Evaluate a single line of input, which should be a complete expression
    * optionally assigned to a variable; if no variable is assigned to, then
    * the result is assigned to "it". In any case, return the value of the
    * expression, or "no value" if there was some sort of error.
    */
   public Double evaluate() {
      stack = new LinkedList<>(); // Resets the stack in case of an error
      target = "it";
      currState = 1;
      // YOUR CODE GOES HERE

      do
      {
          switch(currState)
          {
              case Q1: // State q1
                  currState = q1();
                  break;
               
              case Q2:
                  currState = q2();
                  break;
                  
              case Q3:
                  currState = q3();
                  break;
                  
              case Q4:
                  symbols.put(target, stack.pop());
                  currState = ACCEPTANCE;
                  break;
                  

                  
          }
      } while(currState != ACCEPTANCE && currState != REJECT);
      
      if (currState == ACCEPTANCE && stack.isEmpty())
      {
          return symbols.get(target);   
      }
      else
      {
          error(lexer.getCurrentLine());
          return null;
      }
   } // evaluate
   
   
   /**
    * Represents state q1, the initial state.
    * @return state to transition to.
    */
   private int q1()
   {      
       int currToken = lexer.nextToken();
       switch(currToken)
       {
           case Lexer.NUMBER:
               stack.push(Double.parseDouble(lexer.getText()));
               return Q3;
               
           case Lexer.VARIABLE:
               if (!symbols.containsKey(lexer.getText()))
               {
                   symbols.put(lexer.getText(), 0.0);
               }
               stack.push(symbols.get(lexer.getText()));
               return Q2;
               
           default:
               return REJECT;
       }
   }
   
   /**
    * Represents state q2, which is if the first token is a number.
    * @return state to transition to.
    */
   private int q2()
   {

       String varName = lexer.getText(); // Used for assign_op
       int currToken = lexer.nextToken();
       switch(currToken)
       {
           case Lexer.NUMBER:
               stack.push(Double.parseDouble(lexer.getText()));
               return Q3;
               
           case Lexer.VARIABLE:
               if (!symbols.containsKey(lexer.getText()))
               {
                   symbols.put(lexer.getText(), 0.0);
               }
               stack.push(symbols.get(lexer.getText()));
               return Q3;
               
               
           case Lexer.ASSIGN_OP:
               stack.pop();
               target = varName;
               return Q3;
               
           case Lexer.MINUS_OP:
               stack.push(stack.pop() * -1);
               return Q3;
               
           case Lexer.EOL:
               return Q4;
               
           default:
               return REJECT;
       }
   }
   
   
   /**
    * Represents state q3, which is if the first token is a variable.
    * @return state to transition to.
    */
   private int q3()
   {      
       int currToken = lexer.nextToken();
       
       switch(currToken)
       {
           case Lexer.MINUS_OP:
               if (!stack.isEmpty())
               {
                   Double temp = stack.pop();
                   temp *= -1;
                   stack.push(temp);
               }
               else
               {
                   return REJECT;
               }
               
               return Q3;
               
           
           case Lexer.NUMBER:
               stack.push(Double.parseDouble(lexer.getText()));
               return Q3;
              
          
           case Lexer.VARIABLE:
               if (!symbols.containsKey(lexer.getText()))
               {
                   symbols.put(lexer.getText(), 0.0);
               }
               stack.push(symbols.get(lexer.getText()));
               return Q3;
               
           case Lexer.ADD_OP:
               if (stack.size() >= 2) // Stack must have 2 elements
               {
                   stack.push(stack.pop() + stack.pop());
               }
               else
               {
                   return REJECT;
               }

               return Q3;
               
           case Lexer.SUBTRACT_OP:
               if (stack.size() >= 2) // Stack must have 2 elements
               {
                   // The top of the stack is what to subtract
                   double second = stack.pop(); 
                   stack.push(stack.pop() - second);
               }
               else
               {
                   return REJECT;
               }

               return Q3;
               
           case Lexer.MULTIPLY_OP:
               if (stack.size() >= 2) // Stack must have 2 elements
               {
                   
                   stack.push(stack.pop() * stack.pop());
               }
               else
               {
                   return REJECT;
               }

               return Q3;
               
           case Lexer.DIVIDE_OP:
               if (stack.size() >= 2) // Stack must have 2 elements
               {
                   // The top of the stack is the denominator
                   double second = stack.pop();
                   stack.push(stack.pop() / second);
               }
               else
               {
                   return REJECT;
               }

               return Q3;
               
           case Lexer.EOL:
               return Q4;
               
           default:
               return REJECT;
       }
   }

   /**
    * Run evaluate on each line of input and print the result forever.
    */
   public void run() {
      while (true) {
         Double value = evaluate();
         if (value == null)
            System.out.println("no value");
         else
            System.out.println(value);
      }
   }

   /**
    * Print an error message, display the offending line with the current
    * location marked, and flush the lexer in preparation for the next line.
    *
    * @param msg what to print as an error indication
    */
   private void error(String msg) {
      System.out.println(msg);
      String line = lexer.getCurrentLine();
      int index = lexer.getCurrentChar();
      System.out.print(line);
      for (int i = 1; i < index; i++) System.out.print(' ');
      System.out.println("^");
      lexer.flush();
   }

   ////////////////////////////////
   ///////// Lexer Class //////////

   /**
   * Read terminal input and convert it to a token type, and also record the text
   * of each token. Whitespace is skipped. The input comes from stdin, and each line
   * is prompted for.
   */
   public static class Lexer {

      // language token codes
      public static final int ADD_OP      = 3;
      public static final int SUBTRACT_OP = 4;
      public static final int MULTIPLY_OP = 5;
      public static final int DIVIDE_OP   = 6;
      public static final int MINUS_OP    = 7;
      public static final int ASSIGN_OP   = 8;
      public static final int EOL         = 9;
      public static final int NUMBER      = 11;
      public static final int VARIABLE    = 12;
      public static final int BAD_TOKEN   = 100;

      private Scanner input;     // for reading lines from stdin
      private String line;       // next input line
      private int index;         // current character in this line
      private String text;       // text of the current token

      public Lexer(InputStream in) {
         input = new Scanner(in);
         line = "";
         index = 0;
         text = "";
      }

      /**
       * Fetch the next character from the terminal. If the current line is
       * exhausted, then prompt the user and wait for input. If end-of-file occurs,
       * then exit the program.
       */
      private char nextChar() {
         if (index == line.length()) {
            System.out.print(">> ");
            if (input.hasNextLine()) {
               line = input.nextLine() + "\n";
               if (line.equals("exit\n"))
               {
                   System.out.println("\nBye");
                   System.exit(0);
               }
               index = 0;
            } else {
               System.out.println("\nBye");
               System.exit(0);
            }
         }
         char ch = line.charAt(index);
         index++;
         return ch;
      }

      /**
       * Put the last character back on the input line.
       */
      private void unread() { index -= 1; }

      /**
       * Return the next token from the terminal.
       */
      public int nextToken() {

          text = "" + nextChar();
          
          while(text.equals(" ")) // This loop effectively skips past any whitespace chars
          {
              text = "" + nextChar();
          }
          switch(text.charAt(0))
          {
              case '+':
                  return ADD_OP;
                  
              
              case '-':
                  return SUBTRACT_OP;
                  
                  
              case '*':
                  return MULTIPLY_OP;
                  
                 
              case '/':
                  return DIVIDE_OP;
                  
              
              case '~':
                  return MINUS_OP;
                  
                  
              case '=':
                  return ASSIGN_OP;
                  
                  
              case '\n':
                  return EOL;
                  
          }
          
          if (text.charAt(0) >= '0' && text.charAt(0) <= '9')
          {
              char nxt = nextChar();
              boolean decimalUsed = false; // There can only be one decimal point
              // Iterates through the whole number
              // State q1
              while((nxt >= '0' && nxt <= '9') || (nxt == '.' && !decimalUsed)) 
              {
                  text += nxt;
                  if (nxt == '.')
                  {
                      decimalUsed = true; // Transition to state q10
                  }
                  nxt = nextChar();
              }
              unread(); // This way we won't lose the next input if there's no spaces.
              return NUMBER; // State q11
          }
          else if (Character.isLetter(text.charAt(0))) 
          {
              char nxt = nextChar();
              // Iterates through the whole variable
              // State q2
              while((nxt >= '0' && nxt <= '9') || (Character.isLetter(nxt)))
              {
                  text += nxt;
                  
                  nxt = nextChar();
              }
              unread(); // This way we won't lose the next input if there's no spaces.
              return VARIABLE; // State q12
          }


          return BAD_TOKEN;

      } // nextToken

      /**
       * Return the current line for error messages.
       */
      public String getCurrentLine() { return line; }

      /**
       * Return the current character index for error messages.
       */
      public int getCurrentChar() { return index; }

      /**
       * /** Return the text of the current token.
       */
      public String getText() { return text; }

      /**
       * Clear the current line after an error
       */
      public void flush() { index = line.length(); }

   } // Lexer

} // Evaluator
