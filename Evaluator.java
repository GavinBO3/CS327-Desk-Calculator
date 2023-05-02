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

      // YOUR CODE GOES HERE

      return new Double(0.0);

   } // evaluate

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
          while(text == " ") // This loop effectively skips past any whitespace chars
          {
              text = "" + nextChar();
          }
          switch(text)
          {
              case '+':
                  return ADD_OP;
                  break;
              
              case '-':
                  return SUBTRACT_OP;
                  break;
                  
              case '*':
                  return MULTIPLY_OP;
                  break;
                 
              case '/':
                  return DIVIDE_OP;
                  break;
              
              case '~':
                  return MINUS_OP;
                  break;
                  
              case '=':
                  return ASSIGN_OP;
                  break;
                  
              case '\n':
                  return EOL;
                  break;
          }
          
          if (text.charAt(0) >= '0' && text.charAt(0) <= '9')
          {
              char nxt = nextChar();
              bool decimalUsed = false; // There can only be one decimal point
              // Iterates through the whole number
              while((nxt >= '0' && text.charAt(0) <= '9') || (nxt == '.' && !decimalUsed))
              {
                  text += nxt;
                  if (nxt == '.')
                  {
                      decimalUsed = true;
                  }
                  nxt = nextChar();
              }
              return NUMBER;
          }
          // TODO: implement letters/variables and "other"
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
