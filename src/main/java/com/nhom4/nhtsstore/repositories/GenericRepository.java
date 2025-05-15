package com.nhom4.nhtsstore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface GenericRepository<E,ID> extends JpaRepository<E,ID>, JpaSpecificationExecutor<E> {

}
