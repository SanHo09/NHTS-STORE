package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.GenericEntity;
import java.util.List;

/**
 *
 * @author NamDang
 */
public interface GenericService<T extends GenericEntity> {
    List<T> findAll();
    T findById(Long id);
    T save(T entity);
    void deleteById(Long id);
    void deleteMany(List<T> entities);
}
