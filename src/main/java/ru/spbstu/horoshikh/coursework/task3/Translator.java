package ru.spbstu.horoshikh.coursework.task3;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Replaces every dictionary phrase found in a text with its translation,
 * leaving words that are not in the dictionary — and all whitespace and
 * punctuation — exactly as they were.
 */
public final class Translator {

    private final Dictionary dictionary;

    public Translator(Dictionary dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary, "dictionary");
    }

    public String translate(String text) {
        Objects.requireNonNull(text, "text");
        List<Token> tokens = Tokenizer.tokenize(text);
        StringBuilder result = new StringBuilder(text.length());

        int i = 0;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.type() != TokenType.WORD) {
                result.append(token.text());
                i++;
                continue;
            }

            Optional<Dictionary.Match> match = dictionary.longestMatch(tokens, i);
            if (match.isPresent()) {
                result.append(match.get().translation());
                i = match.get().endTokenIndex() + 1;
            } else {
                result.append(token.text());   // not in the dictionary — kept as is
                i++;
            }
        }
        return result.toString();
    }
}
