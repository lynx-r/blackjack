package com.workingbit.repository;

import com.workingbit.entity.Model;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
public interface ModelRepository extends CrudRepository<Model, Long> {

    List<Model> findAll();

    Model findByName(String name);

}