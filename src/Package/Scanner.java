package Package;

import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

/**
 * Created by Lenovo on 3/10/2015.
 */
public class Scanner {

    //result area content string reference
    Label resultArea;

    //token max length to keep the identifier at a manageable length
    private static final int MAX_TOKEN_LENGTH = 41;

    //Source program
    private BufferedReader source;

    //token string which is currently being processed
    private char [] tokenString;

    //source program line which is currently being processed
    private char [] buffer;

    //position of the buffer inside the line being processed
    private int bufferPos;

    //size of the line being processed
    private int bufferSize;

    //line number being processed in the source program
    private int lineNo;

    //Reserved Words table
    private HashMap<String, TokenType> reservedTable;



    public Scanner(String sourceProgram, Label resultArea) {
        this.resultArea = resultArea;
        //getting the source program into buffered reader
        StringReader reader = new StringReader(sourceProgram);
        source = new BufferedReader(reader);
        //init the position variables
        bufferPos = lineNo = bufferSize = 0;

        //init Reserved Words Table
        reservedTable = new HashMap<String, TokenType>();
        reservedTable.put("if", TokenType.IF);
        reservedTable.put("then", TokenType.THEN);
        reservedTable.put("else", TokenType.ELSE);
        reservedTable.put("end", TokenType.END);
        reservedTable.put("repeat", TokenType.REPEAT);
        reservedTable.put("until", TokenType.UNTIL);
        reservedTable.put("read", TokenType.READ);
        reservedTable.put("write", TokenType.WRITE);


        tokenString = null;
    }


    /*
    * getNextChar
    * returns next non-blank character
    * if you reached the end of the buffer content
    * then get a new line and return the first character
    * if there is not any additional characters
    * then return EOF
    */
    private char getNextChar() throws IOException {
        bufferPos++;
        //reached the end of the buffer get another line
        if(bufferPos >= bufferSize){
            lineNo++;
            buffer = null;
            String temp = source.readLine();
            //a new line is fetched
            if(temp != null){
                buffer = temp.toCharArray();
                bufferSize = buffer.length;
                bufferPos = 0;
                return buffer[bufferPos];
            }
            //end of file reached
            else{
               //return '';
                throw new EOFException();
            }

        }else{
            return buffer[bufferPos];
        }
    }

    private void ungetChar(){bufferPos--;}

    private TokenType reservedWordsLookup(String tokenString){
        if(reservedTable.containsKey(tokenString)){
            return reservedTable.get(tokenString);
        }else{
            return null;
        }
    }

    private boolean isDigit(char c){
        if(c >= 48 && c <= 57)
            return true;
        return false;
    }
    private boolean isLetter(char c){
        if( (c >= 65 && c <= 90) || (c >= 97 && c <= 122) )
            return true;
        return false;
    }

    public TokenType getToken() {
        //tokenString character array index
        int tokenIndex = 0;
        //init tokenString Character array
        tokenString = null;
        tokenString = new char[MAX_TOKEN_LENGTH+1];
        //state of the scanner
        StateType state = StateType.START;
        //to decide which tokens to keep track of
        //like do not keep track of comments
        boolean save;
        //character to be inserted into DFA to determine token type
        char c;
        //Current Token to be returned
        TokenType currentToken = null;

        //Done means accepting state
        while(state != StateType.DONE){
            try {
                c = getNextChar();
            } catch (IOException e) {
                System.out.println("state: "+state+" Token: "+currentToken+" string: "+new String(tokenString, 0, tokenIndex)+".");
                resultArea.setText(resultArea.getText()+"state: "+state+" Token: EOF"+" string: "+new String(tokenString, 0, tokenIndex)+".\n");
                state = StateType.DONE;
                if(e instanceof EOFException)
                    return TokenType.EOF;
                else
                    return TokenType.ERROR;
            }
            save = true;

            switch (state) {
                case START:
                    //found digit
                    if (isDigit(c)) {
                        state = StateType.INNUM;
                    }
                    //found letter
                    else if (isLetter(c)) {
                        state = StateType.INID;
                    }
                    //found right curly brackets
                    else if (c == '{') {
                        save = false;
                        state = StateType.INCOMMENT;
                    }
                    //found colon start of assign operator
                    else if (c == ':') {
                        state = StateType.INASSIGN;
                    }
                    //found white space
                    else if (c == ' ' || c == '\t' || c == '\n') {
                        save = false;
                    }
                    else {
                        state = StateType.DONE;
                        switch (c) {
                            case '+':
                                currentToken = TokenType.PLUS;
                                break;
                            case '-':
                                currentToken = TokenType.MINUS;
                                break;
                            case '*':
                                currentToken = TokenType.TIMES;
                                break;
                            case '/':
                                currentToken = TokenType.DIVISION;
                                break;
                            case '=':
                                currentToken = TokenType.EQ;
                                break;
                            case '<':
                                currentToken = TokenType.LT;
                                break;
                            case '>':
                                currentToken = TokenType.GT;
                                break;
                            case '(':
                                currentToken = TokenType.RPAREN;
                                break;
                            case ')':
                                currentToken = TokenType.LPAREN;
                                break;
                            case ';':
                                currentToken = TokenType.SEMI;
                                break;
                            default:
                                currentToken = TokenType.ERROR;
                        }
                    }
                    break;
                case INCOMMENT:
                    save = false;
                    if (c == '}') state = StateType.START;
                    break;
                case INNUM:
                    save = true;
                    if (!isDigit(c)){
                        ungetChar();
                        save = false;
                        state = StateType.DONE;
                        currentToken = TokenType.NUM;
                    }
                    break;
                case INID:
                    save = true;
                    if(!isLetter(c)){
                        ungetChar();
                        save = false;
                        state = StateType.DONE;
                        currentToken = TokenType.ID;
                    }
                    break;
                case INASSIGN:
                    save = true;
                    state = StateType.DONE;
                    if(c == '='){
                        currentToken = TokenType.ASSIGN;
                    }else{
                        ungetChar();
                        save = false;
                        currentToken = TokenType.ERROR;
                    }
                    break;
                case DONE:
                default:
                    currentToken = TokenType.ERROR;
                    state = StateType.DONE;
                    break;
            }

            if(save && tokenIndex < MAX_TOKEN_LENGTH){
                tokenString[tokenIndex++] = c;
            }

            if(state == StateType.DONE){
                System.out.println("state: "+state+" Token: "+currentToken+" string: "+new String(tokenString, 0, tokenIndex)+".");
                resultArea.setText(resultArea.getText()+"state: "+state+" Token: "+currentToken+" string: "+new String(tokenString, 0, tokenIndex)+".\n");
                if(currentToken == TokenType.ID){
                    TokenType temp = reservedWordsLookup(new String(tokenString, 0, tokenIndex));
                    currentToken = temp == null ? currentToken:temp ;
                }
            }
        }


        return currentToken;
    }
}
