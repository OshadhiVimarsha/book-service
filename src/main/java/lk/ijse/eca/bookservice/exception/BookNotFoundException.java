package lk.ijse.eca.bookservice.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String id) {
        super("Book with ID '" + id + "' not found");
    }
}
