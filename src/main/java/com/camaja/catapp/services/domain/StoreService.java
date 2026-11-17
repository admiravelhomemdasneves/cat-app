package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.StoreRequest;
import com.camaja.catapp.models.Store;
import com.camaja.catapp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final TenantAccess tenantAccess;

    /** Active stores of the current company plus the global ones. */
    public List<Store> getVisibleStores() {
        return storeRepository.findVisibleByCompany(tenantAccess.company());
    }

    public Store getStoreById(Long id) {
        return storeRepository.findVisible(id, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Store", id));
    }

    @Transactional
    public Store saveStore(StoreRequest request) {
        Store store;
        if (request.getIdStore() != null) {
            store = getStoreById(request.getIdStore());
            tenantAccess.assertCanModify(store.getCompany());
        } else {
            store = new Store();
            store.setCompany(tenantAccess.companyForNewCatalogRow(Boolean.TRUE.equals(request.getGlobal())));
        }
        store.setStoreName(request.getStoreName());
        store.setRequestName(request.getRequestName());
        store.setRequestDescription(request.getRequestDescription());
        store.setRequestMethod(request.getRequestMethod());
        store.setUrl(request.getUrl());
        store.setFlagActive(request.getFlagActive() == null ? Boolean.TRUE : request.getFlagActive());
        store.setA$date$(Timestamp.from(Instant.now()));
        return storeRepository.save(store);
    }

    @Transactional
    public Store inactivateStore(Long id) {
        Store store = getStoreById(id);
        tenantAccess.assertCanModify(store.getCompany());
        store.setFlagActive(false);
        store.setA$date$(Timestamp.from(Instant.now()));
        return storeRepository.save(store);
    }
}
