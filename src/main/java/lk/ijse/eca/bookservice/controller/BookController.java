package lk.ijse.eca.bookservice.controller;

import jakarta.validation.Valid;
import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lk.ijse.eca.bookservice.dto.BookResponseDTO;
import lk.ijse.eca.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookController {

    private final BookService bookService;

    // ------------------- CREATE -------------------
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponseDTO> createBook(
            @Valid @ModelAttribute BookRequestDTO dto) {
        log.info("POST /api/v1/books - ID: {}", dto.getId());
        BookResponseDTO response = bookService.createBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ------------------- UPDATE -------------------
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable String id,
            @Valid @ModelAttribute BookRequestDTO dto) {
        log.info("PUT /api/v1/books/{}", id);
        BookResponseDTO response = bookService.updateBook(id, dto);
        return ResponseEntity.ok(response);
    }

    // ------------------- DELETE -------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        log.info("DELETE /api/v1/books/{}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------- GET SINGLE -------------------
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBook(@PathVariable String id) {
        log.info("GET /api/v1/books/{}", id);
        BookResponseDTO response = bookService.getBook(id);
        return ResponseEntity.ok(response);
    }

    // ------------------- GET ALL -------------------
    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        log.info("GET /api/v1/books");
        List<BookResponseDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    // ------------------- GET COVER IMAGE -------------------
    @GetMapping("/{id}/cover-image")
    public ResponseEntity<byte[]> getBookCover(@PathVariable String id) {
        log.info("GET /api/v1/books/{}/cover-image", id);
        byte[] cover = bookService.getBookCover(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(cover);
    }
}