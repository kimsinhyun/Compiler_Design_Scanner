package MiniC.Scanner;

import MiniC.Scanner.SourceFile;
import MiniC.Scanner.Token;
import java.io.*;

public final class Scanner {

  private SourceFile sourceFile;

  private char currentChar;
  private boolean verbose;
  private StringBuffer currentLexeme;
  private StringBuffer temp_buffer = new StringBuffer("");
  private StringBuffer tempLexeme = new StringBuffer("");
  private int lengthOfTempBuffer; 

  private boolean scanningTempBuffer = false;
  private int afterCommentStartCol = -1;

  private boolean currentlyScanningToken;


  private int currentLineNr;
  private int currentColNr;
  private boolean is_unterminated_STRINGLITERAL = false;

  private java.io.File tempFile;
  private java.io.FileInputStream tempSource;


  private boolean isDigit(char c) {
    return (c >= '0' && c <= '9');
  }


///////////////////////////////////////////////////////////////////////////////

  public Scanner(SourceFile source) {
    sourceFile = source;
    currentChar = sourceFile.readChar();
    verbose = false;
    currentLineNr = 1;
    currentColNr= 0;
  }

  public void enableDebugging() {
    verbose = true;
  }

  private void startScanningTempBuffer(){
    try {
      tempFile = new java.io.File("tempBuffer.txt");
      tempSource = new java.io.FileInputStream(tempFile);
   } catch (java.io.IOException e) {
      System.err.println("Error opening file " + "tempBuffer.txt");
      System.err.println("Exiting...");
      System.exit(1);
   }
   currentChar = readTempBufferChar();
   scanningTempBuffer = true;
  }
  
  private char readTempBufferChar(){
    try {
      int c = tempSource.read();
      if (c == -1) {
        c = '\u0000';
     }
      return (char) c;
   } catch (java.io.IOException e) {
     System.out.println("Sth Wrong....");
     return (char)-1;
   }
  }

  // takeIt appends the current character to the current token, and gets
  // the next character from the source program (or the to-be-implemented
  // "untake" buffer in case of look-ahead characters that got 'pushed back'
  // into the input stream).

  private void takeIt() {
    if (currentlyScanningToken)
    {
      currentLexeme.append(currentChar);
    }
    if(scanningTempBuffer){
      currentChar = readTempBufferChar();
    }
    else{
      currentChar = sourceFile.readChar();
    }
    currentColNr++;
  }


  private int scanToken() {
    //----------------------------check ID-------------------------------------
    if( (currentChar == '_') || (currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z')){
      takeIt();
      while ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z') || isDigit(currentChar) || currentChar == '_')
      {
        takeIt();
      }
      if(currentLexeme.equals("return")){
        return Token.RETURN;
      }
      else if(currentLexeme.equals("if")){
        return Token.IF;
      }
      else if(currentLexeme.equals("else")){
        return Token.ELSE;
      }
      else if(currentLexeme.equals("for")){
        return Token.FOR;
      }
      else if(currentLexeme.equals("void")){
        return Token.VOID;
      }
      else if(currentLexeme.equals("while")){
        return Token.WHILE;
      }
      else if(currentLexeme.equals("bool")){
        return Token.BOOL;
      }
      else if(currentLexeme.equals("int")){
        return Token.INT;
      }
      else if(currentLexeme.equals("float")){
        return Token.FLOAT;
      }
      else if("true".equals(currentLexeme.toString()) || "false".equals(currentLexeme.toString())){
        return Token.BOOLLITERAL;
      }
      return Token.ID;
    }
    

    switch (currentChar) {

    case '0':  case '1':  case '2':  case '3':  case '4':     //floatLITERAL has some problem
    case '5':  case '6':  case '7':  case '8':  case '9':
      takeIt();
      boolean isFloat = false;
      boolean check_not_float = false;
      while (isDigit(currentChar)) {
        takeIt();
      }
      if(currentChar=='.'){
        takeIt();
        
        while(isDigit(currentChar)){
          takeIt();
        }
        if(currentChar == 'E' | currentChar == 'e'){   //check the first char next to the digit
          temp_buffer.append(currentChar);
          takeIt();
          if(currentChar == '+' | currentChar == '-'){      //check the second char next to the digit
            temp_buffer.append(currentChar);
            takeIt();  
            if(isDigit(currentChar)){                            //check the third char next to the digit  ex)2.4e+2
              while(isDigit(currentChar)){
                takeIt();
              }
              temp_buffer = new StringBuffer("");
              return Token.FLOATLITERAL;                        
            }
            else{                                                   //if thrid char is not digit -> not FLOATLITERAL ex)2.4e+q
              temp_buffer.append(currentChar);
              takeIt();
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentColNr = currentColNr-3;
              return Token.FLOATLITERAL;
            }
          }
          else if(isDigit(currentChar)){                         //check the second char next to the digit ex)2.4e232
            while(isDigit(currentChar)){
              takeIt();
              temp_buffer = new StringBuffer("");
              return Token.FLOATLITERAL;
            }
          }
          else if(currentChar=='.'){
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentColNr --;
            return Token.FLOATLITERAL;
          }
          else{                                                   //ex)2.4eq
            temp_buffer.append(currentChar);
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentColNr = currentColNr-1;
            return Token.FLOATLITERAL;
          }
        }
        return Token.FLOATLITERAL;
      }
      else if(currentChar == 'E' | currentChar == 'e'){   //check the first char next to the digit
        temp_buffer.append(currentChar);
        takeIt();
        if(currentChar == '+' | currentChar == '-'){      //check the second char next to the digit
          temp_buffer.append(currentChar);
          takeIt();  
          if(isDigit(currentChar)){                            //check the third char next to the digit  ex)2.4e+2
            while(isDigit(currentChar)){
              takeIt();
            }
            temp_buffer = new StringBuffer("");
            return Token.FLOATLITERAL;                        
          }
          else{                                                   //if thrid char is not digit -> not FLOATLITERAL ex)2.4e+q
            temp_buffer.append(currentChar);
            takeIt();
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentColNr = currentColNr-3;
            return Token.FLOATLITERAL;
          }
        }
        else if(isDigit(currentChar)){                         //check the second char next to the digit ex)2.4e232
          while(isDigit(currentChar)){
            takeIt();
            temp_buffer = new StringBuffer("");
            return Token.FLOATLITERAL;
          }
        }
        else if(currentChar=='.'){
          currentLexeme.deleteCharAt(currentLexeme.length()-1);
          currentColNr --;
          return Token.FLOATLITERAL;
        }
        else{                                                   //ex)2.4eq
          temp_buffer.append(currentChar);
          currentLexeme.deleteCharAt(currentLexeme.length()-1);
          currentLexeme.deleteCharAt(currentLexeme.length()-1);
          currentColNr = currentColNr-1;
          return Token.FLOATLITERAL;
        }
      }
      return Token.INTLITERAL;

    case '.':
    takeIt();
    boolean existOneDigit = false;
    while(isDigit(currentChar)){
      existOneDigit = true;
      takeIt();
    }
    if(currentChar == 'E' | currentChar == 'e'){   //check the first char next to the digit
      temp_buffer.append(currentChar);
      takeIt();
      if(currentChar == '+' | currentChar == '-'){      //check the second char next to the digit
        temp_buffer.append(currentChar);
        takeIt();  
        if(isDigit(currentChar)){                            //check the third char next to the digit  ex)2.4e+2
          while(isDigit(currentChar)){
            takeIt();
          }
          temp_buffer = new StringBuffer("");
          return Token.FLOATLITERAL;                        
        }
        else{                                                   //if thrid char is not digit -> not FLOATLITERAL ex)2.4e+q
          temp_buffer.append(currentChar);
          takeIt();
          currentLexeme.deleteCharAt(currentLexeme.length()-1);
          currentLexeme.deleteCharAt(currentLexeme.length()-1);
          currentLexeme.deleteCharAt(currentLexeme.length()-1);
          currentColNr = currentColNr-3;
          return Token.FLOATLITERAL;
        }
      }
      else if(isDigit(currentChar)){                         //check the second char next to the digit ex)2.4e232
        while(isDigit(currentChar)){
          takeIt();
          temp_buffer = new StringBuffer("");
          return Token.FLOATLITERAL;
        }
      }
      else if(currentChar=='.'){
        currentLexeme.deleteCharAt(currentLexeme.length()-1);
        currentColNr --;
        return Token.FLOATLITERAL;
      }
      else{                                                   //ex)2.4eq
        temp_buffer.append(currentChar);
        // takeIt();
        currentLexeme.deleteCharAt(currentLexeme.length()-1);
        currentLexeme.deleteCharAt(currentLexeme.length()-1);
        currentColNr = currentColNr-1;
        return Token.FLOATLITERAL;
      }
    }
    else if(!existOneDigit){
      return Token.ERROR;
    }
    // System.out.println("col: "+currentColNr);
    return Token.FLOATLITERAL;
    
    case '\"':
        currentChar = sourceFile.readChar();
        // takeIt();
        while(true){
          if(temp_buffer.length() == 1){
            temp_buffer.append(currentChar);
            takeIt();
          }
          if(temp_buffer.length() == 2){
            if (!"\\n".equals(temp_buffer.toString())){
              System.out.println("ERROR: illegal escape sequence");
              // exist_error_escape = true;
              temp_buffer = new StringBuffer();
              currentColNr = currentColNr+2;
            }
            else{
              temp_buffer = new StringBuffer();
            }
          }


          if(currentChar == '\n'){
            System.out.println("ERROR: unterminated string literal");
            currentLineNr++;
            currentColNr = 0;
            is_unterminated_STRINGLITERAL = true;
            currentChar = sourceFile.readChar();
            return Token.STRINGLITERAL;
          }
          if(currentChar != '\"'){
            if(currentChar == '\\'){
              temp_buffer.append(currentChar);
            }
            takeIt();
          }
          
          else{
            currentChar = sourceFile.readChar();
            return Token.STRINGLITERAL;
          }
        }

    case '+':
        takeIt();
        return Token.PLUS;
    case '-':
        takeIt();
        return Token.MINUS;
    case '*':
        takeIt();
        return Token.TIMES;
        
    case '/':
        takeIt();
        //-------------------start scan multi line comment----------------------
        if(currentChar=='*'){
          currentlyScanningToken=false;
          takeIt();
          while(true){
            if(currentChar=='\u0000'){
              currentLexeme=new StringBuffer();
              currentLexeme.append('$');
              currentLineNr++;
              System.out.println("ERROR: unterminated multi-line comment.");
              return Token.EOF;
            }
            while(currentChar!='*'){
              if (currentChar == '\f'
              || currentChar == '\n'
              || currentChar == '\r'
              || currentChar == '\t'
              || currentChar == ' ')
              {
                if(currentChar == '\n'){
                  takeIt();
                  currentLineNr++;
                  currentColNr = 1;
                }
                else{
                  takeIt();
                }
                continue;
              }
              takeIt();
              if(currentChar=='\u0000'){
                currentLexeme=new StringBuffer();
                currentLexeme.append('$');
                System.out.println("ERROR: unterminated multi-line comment.");
                return Token.EOF;
              } 
            }

            takeIt();         //if got '*' then check the next char is '/'
            
            if(currentChar!='/'){         //if '/' is not followed next '*', continue read comment
              if (currentChar == '\f'
              || currentChar == '\n'
              || currentChar == '\r'
              || currentChar == '\t'
              || currentChar == ' ')
              {
                if(currentChar == '\n'){
                  takeIt();
                  currentLineNr++;
                  currentColNr = 1;  
                }
                else{
                  takeIt();
                }
                continue;
              }
              continue;// takeIt();
            }
            //----------terminated multi line comment------------
            else{                                             //if '*/'  ('/'is currentChar), it's terminated comment
                takeIt();
                
                currentLexeme = new StringBuffer();
                currentlyScanningToken = false;
                while (currentChar == ' '
                      || currentChar == '\f'
                      || currentChar == '\n'
                      || currentChar == '\r'
                      || currentChar == '\t')
                {
                  if(currentChar == '\n'){
                    currentLineNr ++;
                    currentColNr = -1;
                    }
                  else if(currentChar =='\t'){
                    currentColNr = currentColNr + 4;
                    }
                  else if(currentChar == ' '){
                    }
                  takeIt();
                } 
                currentlyScanningToken = true;
                int kind;
                afterCommentStartCol = currentColNr;

                kind = scanToken();
                return kind;
            }
            //----------terminated multi line comment------------
          }

        }
        //-------------------end scan multi line comment----------------------

        //-------------------start scan single line comment----------------------
        else if(currentChar=='/'){
          currentlyScanningToken=false;
          takeIt();
          while(true){
          if(currentChar=='\n'){
            currentLineNr++;
            takeIt();
                currentLexeme = new StringBuffer();
                currentlyScanningToken = false;
                while (currentChar == ' '
                      || currentChar == '\f'
                      || currentChar == '\n'
                      || currentChar == '\r'
                      || currentChar == '\t')
                {
                  if(currentChar == '\n'){
                    currentLineNr ++;
                    currentColNr = -1;
                    }
                  else if(currentChar =='\t'){
                    currentColNr = currentColNr + 4;
                    }
                  else if(currentChar == ' '){
                    // currentColNr++;
                    }
                  takeIt();
                } 
                currentlyScanningToken = true;
                int kind;
                kind = scanToken();
                return kind;
          }
          takeIt();
          if(currentChar=='\n'){
            currentLineNr++;
            takeIt();
                currentLexeme = new StringBuffer();
                currentlyScanningToken = false;
                while (currentChar == ' '
                      || currentChar == '\f'
                      || currentChar == '\n'
                      || currentChar == '\r'
                      || currentChar == '\t')
                {
                  if(currentChar == '\n'){
                    currentLineNr ++;
                    currentColNr = -1;
                    }
                  else if(currentChar =='\t'){
                    currentColNr = currentColNr + 4;
                    }
                  else if(currentChar == ' '){
                    // currentColNr++;
                    }
                  takeIt();
                } 
                currentlyScanningToken = true;
                int kind;
                kind = scanToken();
                return kind;
          }
        }
      }
      //-------------------start scan single line comment----------------------
        
      else{
        return Token.DIV;
      }
    case '{':
        takeIt();
        return Token.LEFTBRACE;
    case '}':
        takeIt();
        return Token.RIGHTBRACE;
    case '[':
        takeIt();
        return Token.LEFTBRACKET;
    case ']':
        takeIt();
        return Token.RIGHTBRACKET;
    case '(':
        takeIt();
        return Token.LEFTPAREN;
    case ')':
        takeIt();
        return Token.RIGHTPAREN;
    case ',':
        takeIt();
        return Token.COMMA;
    case ';':
        takeIt();
        return Token.SEMICOLON;
    

    case '=':
        takeIt();
        if(currentChar == '='){
          takeIt();
          return Token.EQ;
        }
        return Token.ASSIGN;
    case '|':
        takeIt();
        if(currentChar == '|'){
          takeIt();
          return Token.OR;
        }
        else{
          return Token.ERROR;
        }
    case '&':
        takeIt();
        if(currentChar == '&'){
          takeIt();
          return Token.AND;
        }
        else{
          return Token.ERROR;
        }
    case '!':
      takeIt();
      if(currentChar == '='){
        takeIt();
        return Token.NOTEQ;
      }
      return Token.NOT;
    case '<':
      takeIt();
      if(currentChar == '='){
        takeIt();
        return Token.LESSEQ;
      }
      return Token.LESS;
    case '>':
      takeIt();
      if(currentChar == '='){
        takeIt();
        return Token.GREATEREQ;
      }
      return Token.GREATER;
           
    case '\u0000': // sourceFile.eot:
      currentLexeme.append('$');
      return Token.EOF;
    // Add code here for the remaining MiniC tokens...
    
    
    default:
      takeIt();
      return Token.ERROR;
    }
  }


  public Token scan() {
    Token currentToken;
    SourcePos pos;
    int kind;
    //---------------------------find the start of File----------------------------
    currentlyScanningToken = false;
    while (currentChar == ' '
           || currentChar == '\f'
           || currentChar == '\n'
           || currentChar == '\r'
           || currentChar == '\t')
    {
      if(currentChar == '\n'){
        currentLineNr ++;
        currentColNr = -1;
        }
      else if(currentChar =='\t'){
        currentColNr = currentColNr + 4;
        }
      else if(currentChar == ' '){
        // currentColNr++;
        }
      takeIt();
    } 
    currentlyScanningToken = true;
    //---------------------------find the start of File----------------------------


    currentLexeme = new StringBuffer("");
    pos = new SourcePos();
    //-------------------------test temp buffer------------------
    if(temp_buffer.length() != 0){
      File file = new File("tempBuffer.txt");
      // temp_buffer.append('`');
      while(currentChar != '\u0000'){
        takeIt();
        currentColNr--;
      }
      temp_buffer.append(currentLexeme);
      currentLexeme = new StringBuffer();
      String str = temp_buffer.toString();
      lengthOfTempBuffer = str.length();
      try{
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(str);
        writer.close();
      } catch (IOException e){
        e.printStackTrace();
      }
      startScanningTempBuffer();
      temp_buffer = new StringBuffer("");

      char test = sourceFile.readChar();
      StringBuffer testtest = new StringBuffer("");
      
    }
    
    // Note: currentLineNr and currentColNr are not maintained yet!
      pos.StartCol = currentColNr+1;
      
      kind = scanToken();
      currentToken = new Token(kind, currentLexeme.toString(), pos);
      pos.StartLine = currentLineNr;
      pos.EndLine = currentLineNr;
      if(afterCommentStartCol != -1){
        pos.StartCol = afterCommentStartCol+1;
        afterCommentStartCol = -1;
      }
      if(kind == 18){               //if String Literal
        if(is_unterminated_STRINGLITERAL){
          pos.EndCol = pos.StartCol+currentLexeme.length();
          pos.StartLine = currentLineNr-1;
          pos.EndLine = currentLineNr-1;
          is_unterminated_STRINGLITERAL = false;
        }
        else{
          pos.EndCol = pos.StartCol+currentLexeme.length()-1+2;     //because of "" should not be printed
          currentColNr = currentColNr+2;
        }
      }
      else{
        pos.EndCol = pos.StartCol+currentLexeme.length()-1;
      }
    if (verbose)
      currentToken.print();
    else
      enableDebugging();
    return currentToken;
  }

}