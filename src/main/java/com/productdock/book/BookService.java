package com.productdock.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record BookService(BookRepository bookRepository,
                          BookMapper bookMapper) {

    private static final int PAGE_SIZE = 18;

    public SearchBooksResponse getBooks(Optional<List<String>> topics, int page) {
        var pageTemplate = PageRequest.of(page, PAGE_SIZE);

        Page<BookEntity> booksPage = bookRepository
                .findByTopicsName(topics, pageTemplate);

        List<BookDto> books = booksPage
                .stream()
                .map(bookMapper::toDto)
                .toList();
        return new SearchBooksResponse(booksPage.getTotalElements(), books);
    }

}
