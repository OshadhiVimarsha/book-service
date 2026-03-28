package lk.ijse.eca.bookservice.service;

import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lk.ijse.eca.bookservice.dto.BookResponseDTO;

import java.util.List;

public interface BookService {

    BookResponseDTO createBook(BookRequestDTO dto);

    BookResponseDTO updateBook(String id, BookRequestDTO dto);

    void deleteBook(String id);

    BookResponseDTO getBook(String id);

    List<BookResponseDTO> getAllBooks();

    byte[] getBookCover(String id);
}