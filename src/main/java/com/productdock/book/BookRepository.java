package com.productdock.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends PagingAndSortingRepository<BookEntity, Long> {

    @Query("""
        select b from BookEntity b
        left join b.topics t
        where t.name in :topics or concat(:topics, '') is null
        """)
    Page<BookEntity> findByTopicsName(Optional<List<String>> topics, Pageable pageable);
}


