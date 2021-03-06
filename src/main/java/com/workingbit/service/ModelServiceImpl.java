package com.workingbit.service;

import com.workingbit.entity.Model;
import com.workingbit.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Transactional
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ModelRepository repository;

    /**
     * Метод добавляет парочку записей в БД после запуска приложения,
     * чтобы не было совсем пусто.
     *
     * Из-за того, что подключена H2 (in-memory) БД.
     */
    @PostConstruct
    public void generateTestData() {
    }

    @Override
    public Model save(Model contact) {
        return repository.save(contact);
    }

    @Override
    public List<Model> findAll() {
        return repository.findAll();
    }

    @Override
    public Model findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public String getDefaultParamsStringForModel(List<Double> params) {
        return params.stream()
                .map(Object::toString)
                .reduce((aDouble, aDouble2) -> aDouble + "," + aDouble2)
                .orElse(null);
    }
}