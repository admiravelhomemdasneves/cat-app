package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Color;
import com.camaja.catapp.repository.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/color")
@CrossOrigin
public class ColorController {
    @Autowired
    ColorRepository colorRepository;

    @GetMapping
    Iterable<Color> getColors() {
        List<Color> colors = (List<Color>) colorRepository.findAll();
        return colors;
    }

    @GetMapping("/{id}")
    Optional<Color> read(@PathVariable int id) {
        return colorRepository.findById(id);
    }

    @PostMapping
    Color save(@RequestBody Color color) {
        colorRepository.save(color);
        return color;
    }

    @PutMapping
    Color update(@RequestBody Color color) {
        colorRepository.save(color);
        return color;
    }
}
