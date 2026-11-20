package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.BackofficePageRequest;
import com.camaja.catapp.models.BackofficePage;
import com.camaja.catapp.repository.BackofficePageRepository;
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
class BackofficePageServiceTest {

    @Mock BackofficePageRepository backofficePageRepository;

    @InjectMocks BackofficePageService backofficePageService;

    @Test
    void getPageById_whenNotFound_throwsIllegalArgumentException() {
        when(backofficePageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> backofficePageService.getPageById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Backoffice page not found");
    }

    @Test
    void savePage_stampsADateBeforeSaving() {
        BackofficePageRequest page = new BackofficePageRequest();
        page.setName("Orders");
        page.setPath("/orders");
        when(backofficePageRepository.save(any(BackofficePage.class))).thenAnswer(inv -> inv.getArgument(0));

        backofficePageService.savePage(page);

        ArgumentCaptor<BackofficePage> captor = ArgumentCaptor.forClass(BackofficePage.class);
        verify(backofficePageRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Orders");
        assertThat(captor.getValue().getPath()).isEqualTo("/orders");
        assertThat(captor.getValue().getA$date$()).isNotNull();
    }

    @Test
    void savePage_withId_updatesTheExistingPage() {
        BackofficePage existing = new BackofficePage();
        existing.setId(3L);
        existing.setName("Old");
        existing.setPath("/old");
        when(backofficePageRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(backofficePageRepository.save(any(BackofficePage.class))).thenAnswer(inv -> inv.getArgument(0));

        BackofficePageRequest page = new BackofficePageRequest();
        page.setId(3L);
        page.setName("Orders");
        page.setPath("/orders");

        BackofficePage saved = backofficePageService.savePage(page);

        assertThat(saved).isSameAs(existing);
        assertThat(saved.getName()).isEqualTo("Orders");
        assertThat(saved.getPath()).isEqualTo("/orders");
    }
}
