package com.company;

import com.sun.org.apache.bcel.internal.generic.RET;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.company.TokenType.*;

class Scanner {
    private final String source;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final HashMap<String, TokenType> reservedKeywords;

    static {
        reservedKeywords = new HashMap<>();
        reservedKeywords.put("if", IF);
        reservedKeywords.put("else", ELSE);
        reservedKeywords.put("for", FOR);
        reservedKeywords.put("while", WHILE);
        reservedKeywords.put("fun", FUN);
        reservedKeywords.put("return", RETURN);
        reservedKeywords.put("var", VAR);
        reservedKeywords.put("print", PRINT);
    }

    Scanner(String source) {
        this.source = source;
    }

    ArrayList<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char consumedChar = advance();
        switch(consumedChar) {
            case '(' : addToken(L_PAREN); break;
            case ')' : addToken(R_PAREN); break;
            case '{' : addToken(L_BRACE); break;
            case '}' : addToken(R_BRACE); break;
            case ',' : addToken(COMMA); break;
            case ';' : addToken(SEMICOLON); break;
            case '+' : addToken(PLUS); break;
            case '-' : addToken(MINUS); break;
            //TODO: Divide
            case '*' : addToken(MULT); break;
            case '!' : addToken(match('=') ? NOT_EQUAL : NOT); break;
            case '=' : addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<' : addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>' : addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/' : if(matchDivision()) addToken(DIVIDE); break;
            case ' ' : break;
            case '\r' : break;
            case '\t' : break;
            case '\n' : line++; break;
            case '"' : string(); break;
            default : if(isDigit(consumedChar)) {
                //messy way of handling number literals.
                number();
            } else if(isAlpha(consumedChar)) {
                identifier();
            } else {
                BBE.error(line, "Unexpected charcter.");
                break;
            }
        }
    }

    private void identifier() {
        while(isAlphaNumeric(peek())) advance();

        String indentifierText = source.substring(start, current);
        TokenType type = reservedKeywords.get(indentifierText);
        if(type == null) type = IDENTIFIER;

        addToken(type);
    }

    private void number() {
        while(isDigit(peek())) advance();

        if(peek() == '.' && isDigit(peekNext())) {
            advance();

            while(isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }

        if(isAtEnd()) {
            BBE.error(line, "Unterminated string.");
            return;
        }

        //The closing "
        advance();

        String stringLiteral = source.substring(start + 1, current - 1);
        addToken(STRING, stringLiteral);
    }

    private boolean matchDivision() {
        if(match('/')) {
            while(peek() != '\n' && !isAtEnd()) advance();
            return false;
        }
        return true;
    }

    private char peekNext() {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char peek() {
        if(isAtEnd()) return '\n';
        return source.charAt(current);
    }

    private boolean match(char toBeMatched) {
        if(isAtEnd()) return false;
        if(source.charAt(current) != toBeMatched) return false;

        //If it matches consume it.
        current++;
        return true;
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String tokenText = source.substring(start, current);
        tokens.add(new Token(type, tokenText, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return  (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
