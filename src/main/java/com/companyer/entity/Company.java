package com.companyer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_company")
    private int idCompany;
    @Column(name = "id_owner")
    private int idOwner;
    private String name;
    private String type;
    private String domain;
    private String country;
    private String city;
    private int year;
    private int employees;
    @Column(name = "annual_revenue")
    private int annualRevenue;
    @Column(name = "annual_profit")
    private int annualProfit;
}
