package lk.ijse.eca.bookservice.exception;


public class DuplicateBookException extends RuntimeException {

    public DuplicateBookException(String id) {
        super("Book with ID '" + id + "' already exists");
    }
}
