package com.saranaresturantsystem.repository.common;

import com.saranaresturantsystem.entities.common.InvoiceSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, String> {

    @Query(value = """
            INSERT INTO tbl_invoice_sequences (sequence_key, last_value)
            VALUES (:key, 1)
            ON CONFLICT (sequence_key)
            DO UPDATE SET last_value = tbl_invoice_sequences.last_value + 1
            RETURNING last_value
            """, nativeQuery = true)
    Long getNextValue(@Param("key") String key);
}
