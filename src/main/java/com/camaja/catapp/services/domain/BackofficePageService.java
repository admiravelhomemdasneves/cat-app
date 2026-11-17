package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.BackofficePage;
import com.camaja.catapp.repository.BackofficePageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BackofficePageService {

    private final BackofficePageRepository backofficePageRepository;

    public List<BackofficePage> getAllPages() {
        return backofficePageRepository.findAll();
    }

    public BackofficePage getPageById(Long id) {
        return backofficePageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Backoffice page not found: " + id));
    }

    public BackofficePage savePage(BackofficePage page) {
        page.setA$date$(Timestamp.from(Instant.now()));
        return backofficePageRepository.save(page);
    }
}
