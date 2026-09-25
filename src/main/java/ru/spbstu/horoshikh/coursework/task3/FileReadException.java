package ru.spbstu.horoshikh.coursework.task3;

/**
 * Thrown when the dictionary file cannot be read: it does not exist, is not
 * accessible, or an I/O error happens while reading it.
 */
public class FileReadException extends Exception {

    private static final long serialVersionUID = 1L;

    public FileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
