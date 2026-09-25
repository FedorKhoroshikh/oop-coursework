package ru.spbstu.horoshikh.coursework.task3;

/**
 * Thrown when a dictionary line does not match the required
 * {@code phrase | translation} format.
 */
public class InvalidFileFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidFileFormatException(String message) {
        super(message);
    }
}
