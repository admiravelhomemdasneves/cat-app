package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock CompanyRepository companyRepository;

    @InjectMocks CompanyService companyService;

    @Test
    void getCompanyById_whenNotFound_throwsIllegalArgumentException() {
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getCompanyById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Company not found");
    }

    @Test
    void saveCompany_stampsADateBeforeSaving() {
        Company company = new Company();
        company.setName("ACME");
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        companyService.saveCompany(company);

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());
        assertThat(captor.getValue().getA$date$()).isNotNull();
    }
}
