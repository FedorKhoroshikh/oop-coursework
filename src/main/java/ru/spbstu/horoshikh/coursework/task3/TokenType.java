package ru.spbstu.horoshikh.coursework.task3;

/** What a {@link Token} represents. */
public enum TokenType {
    /** A run of letters, digits or apostrophes — a candidate for translation. */
    WORD,
    /** A run of whitespace, kept as-is between words. */
    WHITESPACE,
    /** Everything else: punctuation, symbols. Kept as-is. */
    OTHER
}
