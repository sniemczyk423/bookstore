package com.example.bookstore.controller;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;
import com.example.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(
        name = "Books",
        description = "Book management and search operations"
)
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Create a new book")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(
            @Valid @RequestBody BookRequest request
    ) {
        return bookService.createBook(request);
    }

    @Operation(summary = "Get all books")
    @GetMapping
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(summary = "Get a book by id")
    @GetMapping("/{id}")
    public BookResponse getBookById(
            @PathVariable Long id
    ) {
        return bookService.getBookById(id);
    }

    @Operation(summary = "Search books by title")
    @GetMapping("/search/title")
    public List<BookResponse> searchByTitle(
            @RequestParam String title
    ) {
        return bookService.searchByTitle(title);
    }

    @Operation(summary = "Search books by author")
    @GetMapping("/search/author")
    public List<BookResponse> searchByAuthor(
            @RequestParam String author
    ) {
        return bookService.searchByAuthor(author);
    }

    @Operation(summary = "Search books by category")
    @GetMapping("/search/category")
    public List<BookResponse> searchByCategory(
            @RequestParam String category
    ) {
        return bookService.searchByCategory(category);
    }

    @Operation(summary = "Update an existing book")
    @PutMapping("/{id}")
    public BookResponse updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {
        return bookService.updateBook(id, request);
    }

    @Operation(summary = "Delete a book")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(
            @PathVariable Long id
    ) {
        bookService.deleteBook(id);
    }
}