package com.productdock.adapter.in.web;


import com.productdock.adapter.in.web.dto.GetBookDto;
import com.productdock.adapter.in.web.mapper.GetBookDtoMapper;
import com.productdock.application.port.in.GetBookQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping({"/api/catalog/books", "/api/catalog/internal/books"})
record GetBookApi(GetBookQuery getBookQuery, GetBookDtoMapper getBookDtoMapper) {

    @GetMapping("/{bookId}")
    public GetBookDto getBook(@PathVariable("bookId") Long bookId) {
        log.debug("GET request received - api/catalog/books/{}", bookId);
        var book = getBookQuery.getById(bookId);
        return getBookDtoMapper.toDto(book);
    }

    @GetMapping
    public GetBookDto getBook(@RequestParam String title, @RequestParam String author) {
        log.debug("GET request received - api/catalog/books?title={}&author={}", title, author);
        var book = getBookQuery.getByTitleAndAuthor(title, author);
        return getBookDtoMapper.toDto(book);
    }
}
