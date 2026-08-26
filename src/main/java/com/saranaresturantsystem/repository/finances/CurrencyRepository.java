package com.saranaresturantsystem.repository.finances;

import com.saranaresturantsystem.entities.finances.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CurrencyRepository extends JpaRepository<Currency , Long> , JpaSpecificationExecutor<Currency> {
}
