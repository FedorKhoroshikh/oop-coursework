package ru.spbstu.horoshikh.coursework.task4;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Six small tasks, each solved with the Stream API only — no manual loops,
 * no collection built by hand and filled with {@code add()}.
 */
public final class StreamTasks {

    private StreamTasks() {
    }

    /** Average of the list, as a double so an empty list has a defined result. */
    public static double average(List<Integer> numbers) {
        Objects.requireNonNull(numbers, "numbers");
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    /** Every string, upper-cased and prefixed with "_new_". */
    public static List<String> upperCasedWithPrefix(List<String> words) {
        Objects.requireNonNull(words, "words");
        return words.stream()
                .map(word -> "_new_" + word.toUpperCase())
                .toList();
    }

    /**
     * Squares of the elements that occur exactly once in the list.
     * Order follows first appearance in the input.
     */
    public static List<Integer> squaresOfUnique(List<Integer> numbers) {
        Objects.requireNonNull(numbers, "numbers");
        Map<Integer, Long> occurrences = numbers.stream()
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()));

        return numbers.stream()
                .distinct()
                .filter(n -> occurrences.get(n) == 1L)
                .map(n -> n * n)
                .toList();
    }

    /** Last element of the collection, or an exception if it is empty. */
    public static <T> T lastElement(Iterable<T> elements) {
        Objects.requireNonNull(elements, "elements");
        return StreamSupport.stream(elements.spliterator(), false)
                .reduce((first, second) -> second)
                .orElseThrow(() -> new NoSuchElementException("the collection is empty"));
    }

    /** Sum of the even numbers in the array, or 0 if there are none. */
    public static long sumOfEven(int[] numbers) {
        Objects.requireNonNull(numbers, "numbers");
        return IntStream.of(numbers)
                .filter(n -> n % 2 == 0)
                .asLongStream()
                .sum();
    }

    /** First character of a non-empty word — the key used by {@link #byFirstCharacter}. */
    private static char firstCharacter(String word) {
        if (word.isEmpty()) {
            throw new IllegalArgumentException("a word must not be empty");
        }
        return word.charAt(0);
    }

    /**
     * Every string turned into a {@code first character -> rest of the string}
     * entry. Two different words sharing the first character is a genuine
     * conflict, not a bug to hide, so it is reported instead of silently
     * keeping one of the two.
     *
     * @throws IllegalStateException    two words share the same first character
     * @throws IllegalArgumentException a word is empty (it has no first character)
     */
    public static Map<Character, String> byFirstCharacter(List<String> words) {
        Objects.requireNonNull(words, "words");
        BinaryOperator<String> rejectDuplicateKeys = (a, b) -> {
            throw new IllegalStateException(
                    "two words share the first character: \"%s\" and \"%s\"".formatted(a, b));
        };
        // Keep the full word here so a collision is reported with the actual
        // words involved, not with the already-trimmed remainders.
        Map<Character, String> fullWordByFirstCharacter = words.stream()
                .collect(Collectors.toMap(
                        StreamTasks::firstCharacter,
                        word -> word,
                        rejectDuplicateKeys,
                        LinkedHashMap::new));

        return fullWordByFirstCharacter.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().substring(1),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }
}
