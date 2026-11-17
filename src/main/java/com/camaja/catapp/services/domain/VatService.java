package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.VatRequest;
import com.camaja.catapp.models.Vat;
import com.camaja.catapp.repository.VatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class VatService {
    private final VatRepository vatRepository;
    private final TenantAccess tenantAccess;

    /** Active VATs of the current company plus the global ones. */
    public List<Vat> getVisibleVats() {
        return vatRepository.findVisibleByCompany(tenantAccess.company());
    }

    public Vat getVatById(Long id) {
        return vatRepository.findVisible(id, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("VAT", id));
    }

    @Transactional
    public Vat saveVat(VatRequest request) {
        Vat vat;
        if (request.getIdVat() != null) {
            vat = getVatById(request.getIdVat());
            tenantAccess.assertCanModify(vat.getCompany());
        } else {
            vat = new Vat();
            vat.setCompany(tenantAccess.companyForNewCatalogRow(Boolean.TRUE.equals(request.getGlobal())));
        }
        vat.setCountry(request.getCountry());
        vat.setVatRate(request.getVatRate());
        vat.setFlagActive(request.getFlagActive() == null ? Boolean.TRUE : request.getFlagActive());
        vat.setA$date$(Timestamp.from(Instant.now()));
        return vatRepository.save(vat);
    }

    @Transactional
    public Vat inactivateVat(Long id) {
        Vat vat = getVatById(id);
        tenantAccess.assertCanModify(vat.getCompany());
        vat.setFlagActive(false);
        vat.setA$date$(Timestamp.from(Instant.now()));
        return vatRepository.save(vat);
    }
}
