package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.mappers.ProductParameterMapper;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Product;
import com.camaja.catapp.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock ProductParameterMapper productParameterMapper;
    @Mock SecurityUtils securityUtils;

    @InjectMocks ProductService productService;

    @Test
    void getActiveProducts_scopedByCurrentCompany() {
        Company company = new Company();
        when(securityUtils.getCurrentCompany()).thenReturn(company);

        productService.getActiveProducts();

        verify(productRepository).findByCompanyAndFlagActive(company, true);
    }

    @Test
    void saveProduct_whenCompanyIsNull_autoAssignsFromSecurityContext() {
        Company company = new Company();
        company.setName("ACME");
        when(securityUtils.getCurrentCompany()).thenReturn(company);

        Product product = new Product();
        product.setCompany(null);

        productService.saveProduct(product);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getCompany()).isSameAs(company);
    }

    @Test
    void saveProduct_whenCompanyAlreadySet_doesNotCallSecurityUtils() {
        Company existingCompany = new Company();
        existingCompany.setName("Existing");

        Product product = new Product();
        product.setCompany(existingCompany);

        productService.saveProduct(product);

        verify(securityUtils, never()).getCurrentCompany();
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getCompany()).isSameAs(existingCompany);
    }
}
