package com.workingbit.service;

import com.workingbit.entity.Model;

import java.util.List;

public interface ModelService {

    Model save(Model contact);

    List<Model> findAll();

    Model findByName(String name);

    String getDefaultParamsStringForModel(List<Double> model);
}