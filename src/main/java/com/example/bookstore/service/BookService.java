package com.example.bookstore.service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.exception.IsbnAlreadyExistsException;
import com.example.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IsbnAlreadyExistsException(request.getIsbn());
        }

        Book book = new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                request.getCategory(),
                request.getDescription(),
                request.getAvailableCopies()
        );

        Book savedBook = bookRepository.save(book);

        return mapToResponse(savedBook);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BookResponse getBookById(Long id) {
        Book book = findBookById(id);
        return mapToResponse(book);
    }

    public List<BookResponse> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BookResponse> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BookResponse> searchByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBookById(id);

        bookRepository.findByIsbn(request.getIsbn())
                .filter(existingBook -> !existingBook.getId().equals(id))
                .ifPresent(existingBook -> {
                    throw new IsbnAlreadyExistsException(request.getIsbn());
                });

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        book.setDescription(request.getDescription());
        book.setAvailableCopies(request.getAvailableCopies());

        Book updatedBook = bookRepository.save(book);

        return mapToResponse(updatedBook);
    }

    public void deleteBook(Long id) {
        Book book = findBookById(id);
        bookRepository.delete(book);
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    private BookResponse mapToResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getDescription(),
                book.getAvailableCopies(),
                book.getCreatedAt()
        );
    }
}