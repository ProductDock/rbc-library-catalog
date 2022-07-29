package com.productdock.adapter.in.web;

import com.productdock.adapter.in.web.mapper.ReviewDtoMapper;
import com.productdock.application.port.in.SaveBookReviewUseCase;
import com.productdock.library.jwt.validator.UserTokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/catalog/books")
record CreateBookReviewApi(SaveBookReviewUseCase saveBookReviewUseCase, ReviewDtoMapper reviewMapper) {
    public static final String USER_EMAIL = "email";
    public static final String USER_NAME = "name";

    @PostMapping("/{bookId}/reviews")
    public void createReviewForBook(
            @PathVariable("bookId") final Long bookId,
            @Valid @RequestBody ReviewDto reviewDto,
            Authentication authentication) {
        log.debug("POST request received - api/catalog/books/{}/reviews, Payload: {}", bookId, reviewDto);
        reviewDto.bookId = bookId;
        reviewDto.userId = ((UserTokenInfo)authentication.getPrincipal()).getEmail();
        reviewDto.userFullName = ((UserTokenInfo)authentication.getPrincipal()).getFullName();
        var review = reviewMapper.toDomain(reviewDto);
        saveBookReviewUseCase.saveReview(review);
    }
}
