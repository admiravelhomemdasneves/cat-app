package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.CompanyRequest;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + id));
    }

    public Company saveCompany(Company company) {
        company.setA$date$(Timestamp.from(Instant.now()));
        return companyRepository.save(company);
    }

    /**
     * Updates the current user's own company. The request has no ID, so a company can never
     * edit another one; flagActive is not client-editable.
     */
    public Company updateCompany(Long currentCompanyId, CompanyRequest input) {
        Company company = getCompanyById(currentCompanyId);
        company.setName(input.getName());
        company.setEmail(input.getEmail());
        company.setVatNumber(input.getVatNumber());
        company.setAddress(input.getAddress());
        return saveCompany(company);
    }
}
