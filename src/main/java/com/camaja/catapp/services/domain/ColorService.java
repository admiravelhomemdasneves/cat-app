package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Color;
import com.camaja.catapp.repository.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {
    @Autowired ColorRepository colorRepository;

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public Color getColorById(Long id) {
        return colorRepository.findById(id.intValue()).orElse(null);
    }

    public void saveColor(Color color) {
        colorRepository.save(color);
    }
}