package com.productdock.application.service;

import com.productdock.application.port.in.PublishNewRatingUseCase;
import com.productdock.application.port.out.persistence.ReviewPersistenceOutPort;
import com.productdock.domain.Book;
import com.productdock.exception.BookReviewException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaveBookReviewServiceShould {

    private static final Book.Review.ReviewCompositeKey REVIEW_COMPOSITE_KEY = mock(Book.Review.ReviewCompositeKey.class);
    private static final Book.Review REVIEW = mock(Book.Review.class);
    private static final Long BOOK_ID = 1L;
    private static final String USER_LEFT_REVIEW_FOR_BOOK = "The user cannot enter more than one comment for a particular book.";

    @InjectMocks
    private SaveBookReviewService service;

    @Mock
    private ReviewPersistenceOutPort reviewRepository;

    @Mock
    private PublishNewRatingUseCase newRatingPublisher;

    @Test
    void saveReview() {
        given(REVIEW.getReviewCompositeKey()).willReturn(REVIEW_COMPOSITE_KEY);
        given(reviewRepository.existsById(REVIEW_COMPOSITE_KEY)).willReturn(false);
        given(REVIEW_COMPOSITE_KEY.getBookId()).willReturn(BOOK_ID);

        service.saveReview(REVIEW);

        verify(reviewRepository).save(REVIEW);
    }

    @Test
    void throwExceptionWhenReviewWithCompositeKeyExist() {
        given(REVIEW.getReviewCompositeKey()).willReturn(REVIEW_COMPOSITE_KEY);
        given(reviewRepository.existsById(REVIEW_COMPOSITE_KEY)).willReturn(true);

        assertThatThrownBy(() -> service.saveReview(REVIEW))
                .isInstanceOf(BookReviewException.class)
                .hasMessage(USER_LEFT_REVIEW_FOR_BOOK);
    }

}
