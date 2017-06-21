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
    private String name;

    @Column
    private String displayName;

    @Column
    private String formula;
    
    @Column
    private Double trustRank;
    
    @Column
    private String labelText;
    
    @Column
    private String defaultValue;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Double> params;

    public Model() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Double getTrustRank() {
        return trustRank;
    }

    public void setTrustRank(Double trustRank) {
        this.trustRank = trustRank;
    }

    public List<Double> getParams() {
        return params;
    }

    public void setParams(List<Double> params) {
        this.params = params;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}