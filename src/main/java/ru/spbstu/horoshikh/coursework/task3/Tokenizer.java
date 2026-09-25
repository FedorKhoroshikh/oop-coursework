package ru.spbstu.horoshikh.coursework.task3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits raw text into words, whitespace runs and everything else, without
 * losing a single character: concatenating every token's text reproduces the
 * original string exactly.
 */
public final class Tokenizer {

    private static final Pattern TOKEN = Pattern.compile(
            "(?<word>[\\p{L}\\p{Nd}']+)|(?<ws>\\s+)|(?<other>[^\\s\\p{L}\\p{Nd}']+)");

    private Tokenizer() {
    }

    public static List<Token> tokenize(String text) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN.matcher(text);
        while (matcher.find()) {
            if (matcher.group("word") != null) {
                tokens.add(new Token(TokenType.WORD, matcher.group("word")));
            } else if (matcher.group("ws") != null) {
                tokens.add(new Token(TokenType.WHITESPACE, matcher.group("ws")));
            } else {
                tokens.add(new Token(TokenType.OTHER, matcher.group("other")));
            }
        }
        return tokens;
    }

    /**
     * Index of the next WORD token that continues the current one — i.e. is
     * separated from it by exactly one whitespace token and nothing else.
     * Punctuation between two words (a comma, for example) breaks the run,
     * so a dictionary phrase never matches across it. Returns -1 if the run
     * does not continue.
     */
    static int nextWordIndex(List<Token> tokens, int wordIndex) {
        int whitespace = wordIndex + 1;
        if (whitespace < tokens.size() && tokens.get(whitespace).type() == TokenType.WHITESPACE) {
            int next = whitespace + 1;
            if (next < tokens.size() && tokens.get(next).type() == TokenType.WORD) {
                return next;
            }
        }
        return -1;
    }
}
