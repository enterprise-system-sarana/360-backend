package com.saranaresturantsystem.repository.quote;

import com.saranaresturantsystem.entities.quote.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuoteRepository extends JpaRepository<Quote,Long>, JpaSpecificationExecutor<Quote> {
}
