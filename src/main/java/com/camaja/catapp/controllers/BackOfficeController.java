package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.models.Product;
import com.camaja.catapp.models.ProductParameters;
import com.camaja.catapp.services.domain.ContactService;
import com.camaja.catapp.services.domain.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/bo")
@RestController
@CrossOrigin
public class BackOfficeController {
    @Autowired ContactService contactService;
    @Autowired ProductService productService;

    //CONTACTS
    @GetMapping("/contact")
    public ResponseEntity<List<ContactDTO>> getActiveContacts() {
        List<ContactDTO> contactDTO = contactService.getAllContacts();
        List<ContactDTO> activeContacts = contactDTO.stream()
                .filter(contact -> !Boolean.FALSE.equals(contact.getFlagActive()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeContacts);
    }

    @GetMapping("/contact/{id}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        ContactDTO contactDTO = contactService.getContactById(id.intValue());
        return ResponseEntity.ok(contactDTO);
    }

    @GetMapping("/contact/inactivate/{id}")
    public ResponseEntity<ContactDTO> inactivateContactById(@PathVariable Long id) {
        ContactDTO contactDTO = contactService.getContactById(id.intValue());
        contactDTO.setFlagActive(false);
        contactService.saveContactDTO(contactDTO);
        return ResponseEntity.ok(contactDTO);
    }

    @PostMapping("/contact/save")
    public ResponseEntity<ContactDTO> saveContact(@RequestBody ContactDTO contactDTO) {
        contactService.saveContactDTO(contactDTO);
        return ResponseEntity.ok(contactDTO);
    }

    //PRODUCTS
    @GetMapping("/product")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products = productService.getAllProducts();
        List<Product> activeProducts = products.stream().filter(product -> !Boolean.FALSE.equals(product.getFlagActive())).toList();

        products.forEach(product -> {
            List<ProductParameters> activeProductParameters = product.getParameters().stream()
                    .filter(productParameters -> !Boolean.FALSE.equals(productParameters.getFlagActive()))
                    .toList();
            product.setParameters(activeProductParameters);
        });

        return ResponseEntity.ok(activeProducts);
    }

    @GetMapping("/product/inactivate/{id}")
    public ResponseEntity<Product> inactivateProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id.intValue());
        product.setFlagActive(false);
        productService.saveProduct(product);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/product/save")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        productService.saveProduct(product);
        return ResponseEntity.ok(product);
    }
}
