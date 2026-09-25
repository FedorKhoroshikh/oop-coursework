package ru.spbstu.horoshikh.coursework.task3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A word/phrase dictionary loaded from a {@code phrase | translation} text
 * file and organised as a trie over the words of every phrase, so that the
 * longest phrase starting at a given word can be found directly instead of
 * scanning every entry.
 */
public final class Dictionary {

    private final TrieNode root = new TrieNode();
    private int entryCount;

    private Dictionary() {
    }

    /**
     * Reads and parses a dictionary file.
     *
     * @throws FileReadException          the file does not exist, is not
     *                                     accessible, or an I/O error happens
     *                                     while reading it
     * @throws InvalidFileFormatException a non-blank line does not match the
     *                                     {@code phrase | translation} format,
     *                                     or the file has no entries at all
     */
    public static Dictionary loadFrom(Path path) throws FileReadException, InvalidFileFormatException {
        Objects.requireNonNull(path, "dictionary path");

        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileReadException("Cannot read dictionary file: " + path, e);
        }

        Dictionary dictionary = new Dictionary();
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            String trimmed = line.strip();
            if (!trimmed.isEmpty()) {
                dictionary.addLine(trimmed, lineNumber);
            }
        }
        if (dictionary.entryCount == 0) {
            throw new InvalidFileFormatException("Dictionary file has no entries: " + path);
        }
        return dictionary;
    }

    private void addLine(String line, int lineNumber) throws InvalidFileFormatException {
        int separator = line.indexOf('|');
        if (separator < 0) {
            throw new InvalidFileFormatException(
                    "Line %d is missing the '|' separator: \"%s\"".formatted(lineNumber, line));
        }
        String phrase = line.substring(0, separator).strip();
        String translation = line.substring(separator + 1).strip();
        if (phrase.isEmpty() || translation.isEmpty()) {
            throw new InvalidFileFormatException(
                    "Line %d has an empty phrase or translation: \"%s\"".formatted(lineNumber, line));
        }
        insert(phrase, translation);
    }

    /** A later entry for the same phrase overwrites an earlier one. */
    private void insert(String phrase, String translation) {
        TrieNode node = root;
        for (String word : phrase.split("\\s+")) {
            node = node.children.computeIfAbsent(word.toLowerCase(Locale.ROOT), key -> new TrieNode());
        }
        node.translation = translation;
        entryCount++;
    }

    /**
     * The longest dictionary phrase that starts at {@code tokens.get(wordIndex)}.
     * A multi-word phrase only matches while consecutive words in {@code tokens}
     * are joined by a single whitespace token — see
     * {@link Tokenizer#nextWordIndex}.
     */
    Optional<Match> longestMatch(List<Token> tokens, int wordIndex) {
        TrieNode node = root;
        Match best = null;
        int cur = wordIndex;

        while (true) {
            String word = tokens.get(cur).text().toLowerCase(Locale.ROOT);
            TrieNode child = node.children.get(word);
            if (child == null) {
                break;
            }
            node = child;
            if (node.translation != null) {
                best = new Match(cur, node.translation);   // keep the deepest match seen so far
            }
            int next = Tokenizer.nextWordIndex(tokens, cur);
            if (next < 0) {
                break;
            }
            cur = next;
        }
        return Optional.ofNullable(best);
    }

    /** One trie node: children keyed by lowercase word, translation at phrase ends. */
    private static final class TrieNode {
        private final Map<String, TrieNode> children = new HashMap<>();
        private String translation;
    }

    /** A match found by {@link #longestMatch}: where the phrase ends and what to output. */
    record Match(int endTokenIndex, String translation) {
    }
}
