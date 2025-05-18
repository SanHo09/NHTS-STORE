package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.GenericEntity;
import com.nhom4.nhtsstore.repositories.GenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface GenericService<T extends GenericEntity, ID, R extends GenericRepository<T, ID>> {

    List<T> findAll();
    T findById(ID id);
    T save(T entity);
    void deleteById(ID id);
    void deleteMany(List<T> entities);
    Page<T> findAll(Pageable pageable);

    // Get the repository instance
    R getRepository();
    //default method to find all entities with pagination by fields and keyword
    // you can override this method to add more conditions if needed
    default Page<T> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<T> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<T> keywordSpec = Specification.where(null);
            for (String field : searchFields) {
                keywordSpec = keywordSpec.or((root, query, cb) ->
                        cb.like(cb.lower(root.get(field)), "%" + keyword.toLowerCase() + "%"))
                //add more conditions from here if you override this method
                //example: .or((root, query, cb) -> cb.like(cb.lower(root.join("otherTable").get(field)), "%" + keyword.toLowerCase() + "%"))
                ;
            }
            spec = spec.and(keywordSpec);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return getRepository().findAll(spec, pageable);
    }
}