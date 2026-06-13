package com.example.bookstore.service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.exception.IsbnAlreadyExistsException;
import com.example.bookstore.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldCreateBook() {
        BookRequest request = createRequest();

        when(bookRepository.existsByIsbn(request.getIsbn()))
                .thenReturn(false);

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.createBook(request);

        assertEquals("Clean Code", response.getTitle());
        assertEquals("9780132350884", response.getIsbn());
        assertEquals(5, response.getAvailableCopies());

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldRejectDuplicateIsbn() {
        BookRequest request = createRequest();

        when(bookRepository.existsByIsbn(request.getIsbn()))
                .thenReturn(true);

        assertThrows(
                IsbnAlreadyExistsException.class,
                () -> bookService.createBook(request)
        );

        verify(bookRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllBooks() {
        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "programming",
                "Clean code guide",
                5
        );

        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookResponse> books = bookService.getAllBooks();

        assertEquals(1, books.size());
        assertEquals("Clean Code", books.get(0).getTitle());
    }

    @Test
    void shouldThrowExceptionWhenBookDoesNotExist() {
        when(bookRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookNotFoundException.class,
                () -> bookService.getBookById(99L)
        );
    }

    private BookRequest createRequest() {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code");
        request.setAuthor("Robert C. Martin");
        request.setIsbn("9780132350884");
        request.setCategory("programming");
        request.setDescription("Clean code guide");
        request.setAvailableCopies(5);

        return request;
    }

    @Test
    void shouldReturnBookById() {
        Book book = createBook();

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        BookResponse response = bookService.getBookById(1L);

        assertEquals("Clean Code", response.getTitle());
        assertEquals("Robert C. Martin", response.getAuthor());
    }

    @Test
    void shouldSearchBooksByTitle() {
        Book book = createBook();

        when(bookRepository.findByTitleContainingIgnoreCase("clean"))
                .thenReturn(List.of(book));

        List<BookResponse> result = bookService.searchByTitle("clean");

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void shouldSearchBooksByAuthor() {
        Book book = createBook();

        when(bookRepository.findByAuthorContainingIgnoreCase("martin"))
                .thenReturn(List.of(book));

        List<BookResponse> result = bookService.searchByAuthor("martin");

        assertEquals(1, result.size());
        assertEquals("Robert C. Martin", result.get(0).getAuthor());
    }

    @Test
    void shouldSearchBooksByCategory() {
        Book book = createBook();

        when(bookRepository.findByCategoryIgnoreCase("programming"))
                .thenReturn(List.of(book));

        List<BookResponse> result =
                bookService.searchByCategory("programming");

        assertEquals(1, result.size());
        assertEquals("programming", result.get(0).getCategory());
    }

    @Test
    void shouldUpdateBook() {
        Book book = createBook();
        BookRequest request = createRequest();

        request.setTitle("Effective Java");
        request.setAuthor("Joshua Bloch");
        request.setIsbn("9780134685991");
        request.setAvailableCopies(4);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(bookRepository.findByIsbn(request.getIsbn()))
                .thenReturn(Optional.empty());

        when(bookRepository.save(book))
                .thenReturn(book);

        BookResponse response = bookService.updateBook(1L, request);

        assertEquals("Effective Java", response.getTitle());
        assertEquals("Joshua Bloch", response.getAuthor());
        assertEquals(4, response.getAvailableCopies());
    }

    @Test
    void shouldRejectUpdateWhenIsbnBelongsToAnotherBook() {
        Book book = createBook();
        Book otherBook = createBook();

        BookRequest request = createRequest();
        request.setIsbn("different-isbn");

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(bookRepository.findByIsbn("different-isbn"))
                .thenReturn(Optional.of(otherBook));

        assertThrows(
                IsbnAlreadyExistsException.class,
                () -> bookService.updateBook(1L, request)
        );

        verify(bookRepository, never()).save(book);
    }

    @Test
    void shouldDeleteBook() {
        Book book = createBook();

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    private Book createBook() {
        return new Book(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "programming",
                "Clean code guide",
                5
        );
    }

    @Test
    void shouldDeleteExistingBook() {
        Book book = createBook();

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }
}