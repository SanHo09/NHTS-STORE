package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Invoice;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends GenericRepository<Invoice,Long>{
    @Query("SELECT i FROM Invoice i WHERE i.createdBy = :createdBy AND i.id = :id")
    Optional<Invoice> findByIdAndCreatedBy(Long id, String createdBy);

    @Query("SELECT i FROM Invoice i WHERE i.createdBy = :createdBy")
    Page<Invoice> findAllByCreatedBy(String createdBy, Pageable pageable);
}
