package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Color;
import com.camaja.catapp.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ColorService {
    private final ColorRepository colorRepository;

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public Color getColorById(Long id) {
        return colorRepository.findById(id).orElse(null);
    }

    public void saveColor(Color color) {
        colorRepository.save(color);
    }
}