package MiniC.Scanner;

import MiniC.Scanner.SourceFile;
import MiniC.Scanner.Token;

public final class Scanner {

  private SourceFile sourceFile;

  private char currentChar;
  private boolean verbose;
  private StringBuffer currentLexeme;
  private StringBuffer temp_buffer = new StringBuffer("");
  private StringBuffer tempLexeme = new StringBuffer("");

  private boolean currentlyScanningToken;
  private int currentLineNr;
  private int currentColNr;
  // private boolean exist_error_escape = false;
  private boolean is_unterminated_STRINGLITERAL = false;

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

  // takeIt appends the current character to the current token, and gets
  // the next character from the source program (or the to-be-implemented
  // "untake" buffer in case of look-ahead characters that got 'pushed back'
  // into the input stream).

  private void takeIt() {
    if (currentlyScanningToken)
    {
      currentLexeme.append(currentChar);
    }
    currentChar = sourceFile.readChar();
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
      // System.out.println("col: "+currentColNr);
      boolean isFloat = false;
      boolean check_not_float = false;
      while (isDigit(currentChar)) {
        takeIt();
      }
      if(currentChar=='.'){
        takeIt();
        
        while(isDigit(currentChar)){
          takeIt();
          // System.out.println("col: "+currentColNr);

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
              currentColNr--;
              
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              // currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentColNr = currentColNr-2;
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
          else{                                                   //ex)2.4eq
            temp_buffer.append(currentChar);
            takeIt();
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentColNr = currentColNr-1;
                  return Token.FLOATLITERAL;
          }
        }
        // System.out.println("col: "+currentColNr);
        return Token.FLOATLITERAL;
      }
      else if(currentChar == 'E' | currentChar == 'e'){
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
              currentColNr--;
              
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              // currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentColNr = currentColNr-2;
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
          else{                                                   //ex)2.4eq
            temp_buffer.append(currentChar);
            takeIt();
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentColNr = currentColNr-1;
                  return Token.FLOATLITERAL;
          }
      }
      return Token.INTLITERAL;

    case '.':
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
            if(isDigit(currentChar)){                            //check the third char next to the digit  ex).4e+2
              while(isDigit(currentChar)){
                takeIt();
              }
              temp_buffer = new StringBuffer("");
              return Token.FLOATLITERAL;                        
            }
            else{                                                   //if thrid char is not digit -> not FLOATLITERAL ex).4e+q
              temp_buffer.append(currentChar);
              takeIt();
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentLexeme.deleteCharAt(currentLexeme.length()-1);
              currentColNr = currentColNr-4;
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
          else{                                                   //ex).4eq
            temp_buffer.append(currentChar);
            takeIt();
            currentLexeme.deleteCharAt(currentLexeme.length()-1);
            currentColNr = currentColNr-3;
                  return Token.FLOATLITERAL;
          }
        }
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
            if ("\\t".equals(temp_buffer.toString())){
              System.out.println("ERROR: illegal escape sequence");
              // exist_error_escape = true;
              temp_buffer = new StringBuffer();
              currentColNr = currentColNr+2;
            }
            else{
              temp_buffer = new StringBuffer();
            }
          }
          else if(currentChar == '\n'){
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
        return Token.DIV;
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

  private int scanToken_tempBuffer(char tempchar){
    if((tempchar >= 'A' && tempchar <= 'Z') || (tempchar >= 'a' && tempchar <= 'z')){
      tempLexeme.append(tempchar);
      // while ((tempchar >= 'A' && tempchar <= 'Z') || (tempchar >= 'a' && tempchar <= 'z') || isDigit(tempchar) || tempchar == '_')
      // {
      //   tempLexeme.append(tempchar);
      //   System.out.println(tempLexeme);
      // }
      if(tempLexeme.equals("return")){
        tempLexeme = new StringBuffer("");
        return Token.RETURN;
      }
      else if(tempLexeme.equals("if")){
        tempLexeme = new StringBuffer("");
        return Token.IF;
      }
      else if(tempLexeme.equals("while")){
        tempLexeme = new StringBuffer("");
        return Token.WHILE;
      }
      else if(tempLexeme.equals("bool")){
        tempLexeme = new StringBuffer("");
        return Token.BOOL;
      }
      else if(tempLexeme.equals("int")){
        tempLexeme = new StringBuffer("");
        return Token.INT;
      }
      else if(tempLexeme.equals("float")){
        tempLexeme = new StringBuffer("");
        return Token.FLOAT;
      }
      tempLexeme = new StringBuffer("");
      return Token.ID;
    }
    else if (tempchar == '+'){
      return Token.PLUS;
    }
    return Token.ERROR;
  }

  public Token scan() {
    Token currentToken;
    SourcePos pos;
    int kind;
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
    currentLexeme = new StringBuffer("");
    pos = new SourcePos();
    if (temp_buffer.length() != 0){
      currentColNr = currentColNr+1;
      pos.StartLine = currentLineNr;
      pos.EndLine = currentLineNr;
      pos.StartCol = currentColNr;
      if(temp_buffer.charAt(0) == ' '){
          temp_buffer = temp_buffer.delete(0,1);
          // currentColNr++;
          pos.StartCol = currentColNr+1;
      }
      try{
        kind = scanToken_tempBuffer(temp_buffer.charAt(0));
        currentToken = new Token(kind, Character.toString(temp_buffer.charAt(0)), pos);
        currentColNr = pos.StartCol;
        pos.EndCol = currentColNr;
        temp_buffer = temp_buffer.delete(0,1);
      } catch (java.lang.StringIndexOutOfBoundsException e){
        kind = scanToken();
        currentToken = new Token(kind, currentLexeme.toString(), pos);
        // currentColNr = pos.StartCol+currentLexeme.length()-1;
        pos.EndCol = currentColNr;
      }
    }
    // Note: currentLineNr and currentColNr are not maintained yet!
    else{
      pos.StartLine = currentLineNr;
      pos.EndLine = currentLineNr;
      pos.StartCol = currentColNr+1;
      kind = scanToken();
      currentToken = new Token(kind, currentLexeme.toString(), pos);
      if(kind == 18){
        if(is_unterminated_STRINGLITERAL){
          pos.EndCol = pos.StartCol+currentLexeme.length();
          is_unterminated_STRINGLITERAL = false;
        }
        else{
          pos.EndCol = pos.StartCol+currentLexeme.length()-1+2;
        }
      }
      else{
        pos.EndCol = pos.StartCol+currentLexeme.length()-1;
      // currentToken = new Token(kind, currentLexeme.toString(), pos);
      }
      // currentColNr = pos.StartCol+currentLexeme.length()-1;
      // pos.EndCol = pos.StartCol+currentLexeme.length()-1;
      
      // currentColNr = currentColNr-1;
      // System.out.println("currentColNr: " + currentColNr);
    }
    
    if (verbose)
      currentToken.print();
    return currentToken;
  }

}