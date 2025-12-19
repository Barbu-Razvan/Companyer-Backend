package com.companyer.repository;

import com.companyer.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer>{
    Company findByIdCompany(int id_company);
}