package com.workingbit.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
public class Model implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Enumerated
    private EnumModel name;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Double> params;

    public Model() {
    }

    public Model(EnumModel name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumModel getName() {
        return name;
    }

    public void setName(EnumModel name) {
        this.name = name;
    }

    public List<Double> getParams() {
        return params;
    }

    public void setParams(List<Double> params) {
        this.params = params;
    }
}