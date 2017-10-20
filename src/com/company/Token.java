package com.company;

class Token {
    final TokenType type;
    final String lexic;
    final Object literal;
    final int line;

    Token(TokenType type, String lexic, Object literal, int line) {
        this.type = type;
        this.lexic = lexic;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexic + " " + literal;
    }
}
