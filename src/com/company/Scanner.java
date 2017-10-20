package com.company;

import java.util.ArrayList;

import static com.company.TokenType.*;

class Scanner {
    private final String source;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

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
            case '!' : addToken(match('=') ? NOT_EQUAL : NOT);
            case '=' : addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' : addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' : addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' : if(matchDivision()) addToken(DIVIDE); break;
            case ' ' : break;
            case '\r' : break;
            case '\t' : break;
            case '\n' : line++; break;
            default : BBE.error(line, "Unexpected charcter."); break;

        }
    }

    private boolean matchDivision() {
        if(match('/')) {
            while(peek() != '\n' && !isAtEnd()) advance();
            return false;
        }
        return true;
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
}
