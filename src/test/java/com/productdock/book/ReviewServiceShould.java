package com.productdock.book;

import com.productdock.exception.BookReviewException;
import com.productdock.producer.JsonRecordPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceShould {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRatingCalculator calculator;

    @Mock
    private JsonRecordPublisher ratingJsonRecordPublisher;

    @Mock
    private ReviewMapper reviewMapper;

    private static final ReviewDto reviewDtoMock = mock(ReviewDto.class);
    private static final ReviewEntity reviewEntityMock = mock(ReviewEntity.class);
    private static final List reviewListMock = mock(List.class);
    private static final Rating ratingMock = mock(Rating.class);
    private static final String BOOK_REVIEW_EXCEPTION_MESSAGE = "The user cannot enter more than one comment for a particular book.";

    @Captor
    private ArgumentCaptor<BookRatingMessage> bookRatingMessageCaptor;

    @Test
    void saveReview() throws Exception {
        given(reviewMapper.toEntity(reviewDtoMock)).willReturn(reviewEntityMock);
        given(reviewRepository.existsById(reviewEntityMock.getReviewCompositeKey())).willReturn(false);
        given(reviewRepository.findByBookId(reviewDtoMock.bookId)).willReturn(reviewListMock);
        given(reviewEntityMock.getRating()).willReturn((short) 1);
        given(calculator.calculate(reviewListMock)).willReturn(ratingMock);

        reviewService.saveReview(reviewDtoMock);

        verify(reviewRepository).save(reviewEntityMock);
        verify(ratingJsonRecordPublisher).sendMessage(bookRatingMessageCaptor.capture());

        var bookRatingMessageValue = bookRatingMessageCaptor.getValue();
        assertThat(bookRatingMessageValue.getBookId()).isEqualTo(reviewDtoMock.bookId);
        assertThat(bookRatingMessageValue.getRating()).isEqualTo(ratingMock.getScore());
        assertThat(bookRatingMessageValue.getRatingsCount()).isEqualTo(ratingMock.getCount());
    }

    @Test
    void saveReview_whenRatingMissing() throws Exception {
        given(reviewMapper.toEntity(reviewDtoMock)).willReturn(reviewEntityMock);
        given(reviewEntityMock.getRating()).willReturn(null);
        given(reviewRepository.existsById(reviewEntityMock.getReviewCompositeKey())).willReturn(false);

        reviewService.saveReview(reviewDtoMock);

        verify(reviewRepository).save(reviewEntityMock);
        verify(ratingJsonRecordPublisher, times(0)).sendMessage(any());
    }

    @Test
    void saveReview_whenUserAlreadyReviewedBook() {
        given(reviewMapper.toEntity(reviewDtoMock)).willReturn(reviewEntityMock);
        given(reviewRepository.existsById(reviewEntityMock.getReviewCompositeKey())).willReturn(true);

        assertThatThrownBy(() -> reviewService.saveReview(reviewDtoMock))
                .isInstanceOf(BookReviewException.class)
                .hasMessage(BOOK_REVIEW_EXCEPTION_MESSAGE);
    }
}
