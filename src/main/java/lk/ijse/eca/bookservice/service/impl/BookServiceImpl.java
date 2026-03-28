package lk.ijse.eca.bookservice.service.impl;

import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lk.ijse.eca.bookservice.dto.BookResponseDTO;
import lk.ijse.eca.bookservice.entity.Book;
import lk.ijse.eca.bookservice.exception.BookNotFoundException;
import lk.ijse.eca.bookservice.exception.DuplicateBookException;
import lk.ijse.eca.bookservice.exception.FileOperationException;
import lk.ijse.eca.bookservice.mapper.BookMapper;
import lk.ijse.eca.bookservice.repository.BookRepository;
import lk.ijse.eca.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Value("${app.storage.path}")
    private String storagePathStr;

    private Path storagePath;

    // ------------------- CREATE -------------------
    @Override
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO dto) {
        log.debug("Creating book with ID: {}", dto.getId());

        if (bookRepository.existsById(dto.getId())) {
            log.warn("Duplicate Book ID detected: {}", dto.getId());
            throw new DuplicateBookException(dto.getId());
        }

        String coverId = UUID.randomUUID().toString();

        Book book = bookMapper.toEntity(dto);
        book.setCoverImage(coverId);

        bookRepository.save(book);
        saveCoverImage(coverId, dto.getCoverImage());

        log.info("Book created successfully: {}", dto.getId());
        return bookMapper.toResponseDto(book);
    }

    // ------------------- UPDATE -------------------
    @Override
    @Transactional
    public BookResponseDTO updateBook(String id, BookRequestDTO dto) {
        log.debug("Updating book with ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found for update: {}", id);
                    return new BookNotFoundException(id);
                });

        String oldCoverId = book.getCoverImage();
        boolean coverChanged = dto.getCoverImage() != null && !dto.getCoverImage().isEmpty();
        String newCoverId = coverChanged ? UUID.randomUUID().toString() : oldCoverId;

        bookMapper.updateEntity(dto, book);
        book.setCoverImage(newCoverId);

        bookRepository.save(book);

        if (coverChanged) {
            saveCoverImage(newCoverId, dto.getCoverImage());
            tryDeleteCoverImage(oldCoverId);
        }

        log.info("Book updated successfully: {}", id);
        return bookMapper.toResponseDto(book);
    }

    // ------------------- DELETE -------------------
    @Override
    @Transactional
    public void deleteBook(String id) {
        log.debug("Deleting book with ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found for deletion: {}", id);
                    return new BookNotFoundException(id);
                });

        String coverId = book.getCoverImage();

        bookRepository.delete(book);
        deleteCoverImage(coverId);

        log.info("Book deleted successfully: {}", id);
    }

    // ------------------- GET -------------------
    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBook(String id) {
        log.debug("Fetching book with ID: {}", id);
        return bookRepository.findById(id)
                .map(bookMapper::toResponseDto)
                .orElseThrow(() -> {
                    log.warn("Book not found: {}", id);
                    return new BookNotFoundException(id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks() {
        log.debug("Fetching all books");
        List<BookResponseDTO> books = bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
        log.debug("Fetched {} books", books.size());
        return books;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getBookCover(String id) {
        log.debug("Fetching cover for book ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found: {}", id);
                    return new BookNotFoundException(id);
                });
        Path filePath = storagePath().resolve(book.getCoverImage());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read cover for book: {}", id, e);
            throw new FileOperationException("Failed to read cover image for book: " + id, e);
        }
    }

    // ------------------- PRIVATE FILE METHODS -------------------
    private Path storagePath() {
        if (storagePath == null) {
            storagePath = Paths.get(storagePathStr);
        }
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new FileOperationException(
                    "Failed to create storage directory: " + storagePath.toAbsolutePath(), e);
        }
        return storagePath;
    }

    private void saveCoverImage(String coverId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileOperationException("Cover image file must not be empty");
        }
        Path filePath = storagePath().resolve(coverId);
        try {
            Files.write(filePath, file.getBytes());
            log.debug("Cover image saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save cover image: {}", filePath, e);
            throw new FileOperationException("Failed to save cover image file: " + coverId, e);
        }
    }

    private void deleteCoverImage(String coverId) {
        Path filePath = storagePath().resolve(coverId);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.debug("Cover image deleted: {}", filePath);
            } else {
                log.warn("Cover image file not found (already removed?): {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete cover image: {}", filePath, e);
            throw new FileOperationException("Failed to delete cover image file: " + coverId, e);
        }
    }

    private void tryDeleteCoverImage(String coverId) {
        try {
            deleteCoverImage(coverId);
        } catch (FileOperationException e) {
            log.warn("Could not delete old cover image '{}'. Manual cleanup may be required.", coverId);
        }
    }
}