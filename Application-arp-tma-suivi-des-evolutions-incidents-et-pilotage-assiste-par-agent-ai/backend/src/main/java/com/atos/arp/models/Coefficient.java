package com.atos.arp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coefficient {
    private int id;
    private double coef;
    private double valeurMin;
    private String operationMin;
    private double valeurMax;
    private String operationMax;
}