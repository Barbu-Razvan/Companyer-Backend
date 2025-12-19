package com.companyer.controller;

import com.companyer.entity.Company;
import com.companyer.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/get-companies")
    public Map<String, List<Company>> getAllCompanies() {
        return Map.of("companies", companyRepository.findAll());
    }

    @PostMapping("/edit-company")
    @PreAuthorize("hasRole('ROLE_EDITOR') or hasRole('ROLE_ADMIN')")
    public Map<String, Object> editCompany(@RequestBody Map<String, Map<String, String>> payload) {
        try {
            Map<String, String> updatedCompany = payload.get("company");
            if (updatedCompany == null || !updatedCompany.containsKey("idCompany")) {
                return Map.of("error", true, "message", "Invalid company data.");
            }

            int idCompany;
            try {
                idCompany = Integer.parseInt(updatedCompany.get("idCompany"));
            } catch (NumberFormatException e) {
                return Map.of("error", true, "message", "Invalid company ID.");
            }

            Company existing = companyRepository.findById(idCompany).orElse(null);
            if (existing == null) {
                return Map.of("error", true, "message", "Company not found.");
            }

            String name = updatedCompany.get("name");
            if (name != null && !name.isEmpty()) existing.setName(name);

            String type = updatedCompany.get("type");
            if (type != null && !type.isEmpty()) existing.setType(type);

            String domain = updatedCompany.get("domain");
            if (domain != null && !domain.isEmpty()) existing.setDomain(domain);

            String country = updatedCompany.get("country");
            if (country != null && !country.isEmpty()) existing.setCountry(country);

            String city = updatedCompany.get("city");
            if (city != null && !city.isEmpty()) existing.setCity(city);

            String yearStr = updatedCompany.get("year");
            if (yearStr != null && !yearStr.isEmpty()) {
                existing.setYear(Integer.parseInt(yearStr));
            }

            String employeesStr = updatedCompany.get("employees");
            if (employeesStr != null && !employeesStr.isEmpty()) {
                existing.setEmployees(Integer.parseInt(employeesStr));
            }

            String revenueStr = updatedCompany.get("annualRevenue");
            if (revenueStr != null && !revenueStr.isEmpty()) {
                existing.setAnnualRevenue(Integer.parseInt(revenueStr));
            }

            String profitStr = updatedCompany.get("annualProfit");
            if (profitStr != null && !profitStr.isEmpty()) {
                existing.setAnnualProfit(Integer.parseInt(profitStr));
            }

            companyRepository.save(existing);

            return Map.of("error", false, "company", existing);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", true, "message", "Server error.");
        }
    }

    @PostMapping("/delete-company")
    @PreAuthorize("hasRole('ROLE_EDITOR') or hasRole('ROLE_ADMIN')")
    public Map<String, Object> deleteCompany(@RequestBody Map<String, String> payload) {
        try {
            if (payload == null || !payload.containsKey("id")) {
                return Map.of("error", true, "message", "Missing company ID.");
            }

            int id;
            try {
                id = Integer.parseInt(payload.get("id"));
            } catch (NumberFormatException e) {
                return Map.of("error", true, "message", "Invalid company ID.");
            }

            Company existing = companyRepository.findById(id).orElse(null);
            if (existing == null) {
                return Map.of("error", true, "message", "Company not found.");
            }

            companyRepository.deleteById(id);

            return Map.of("error", false, "message", "Company deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", true, "message", "Server error.");
        }
    }

    @PostMapping("/add-company")
    @PreAuthorize("hasRole('ROLE_EDITOR') or hasRole('ROLE_ADMIN')")
    public Map<String, Object> addCompany(@RequestBody Map<String, Map<String, String>> payload) {
        try {
            Map<String, String> companyData = payload.get("company");
            if (companyData == null) {
                return Map.of("error", true, "message", "Missing company data.");
            }

            String[] requiredFields = {"name", "type", "domain", "country", "city", "year", "employees", "annualRevenue", "annualProfit"};
            for (String field : requiredFields) {
                if (!companyData.containsKey(field) || companyData.get(field).trim().isEmpty()) {
                    return Map.of("error", true, "message", "Missing required field: " + field);
                }
            }

            Company company = new Company();
            company.setName(companyData.get("name"));
            company.setType(companyData.get("type"));
            company.setDomain(companyData.get("domain"));
            company.setCountry(companyData.get("country"));
            company.setCity(companyData.get("city"));

            company.setYear(Integer.parseInt(companyData.get("year")));
            company.setEmployees(Integer.parseInt(companyData.get("employees")));
            company.setAnnualRevenue(Integer.parseInt(companyData.get("annualRevenue")));
            company.setAnnualProfit(Integer.parseInt(companyData.get("annualProfit")));

            // Save to database
            companyRepository.save(company);

            return Map.of("error", false, "company", company);
        } catch (NumberFormatException e) {
            return Map.of("error", true, "message", "Invalid number format in numeric fields.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", true, "message", "Server error.");
        }
    }


}
