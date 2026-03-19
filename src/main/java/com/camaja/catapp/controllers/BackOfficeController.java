package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.dto.ProductParameterDTO;
import com.camaja.catapp.models.*;
import com.camaja.catapp.services.domain.*;
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
    @Autowired ProductParametersService productParametersService;
    @Autowired VatService vatService;
    @Autowired ColorService colorService;
    @Autowired StoreService storeService;
    @Autowired OrderService orderService;

    // ─── CONTACTS ────────────────────────────────────────────────────────────────

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

    // ─── PRODUCTS ─────────────────────────────────────────────────────────────────

    @GetMapping("/product")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> activeProducts = productService.getActiveProducts();
        return ResponseEntity.ok(activeProducts);
    }

    @GetMapping("/product/parameters")
    public ResponseEntity<List<ProductParameterDTO>> getProductParameters() {
        List<ProductParameterDTO> productParameters = productService.getActiveProductsParametersDTO();
        return ResponseEntity.ok(productParameters);
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
        product.getParameters().forEach(productParameter -> productParameter.setProduct(product));
        productService.saveProduct(product);
        return ResponseEntity.ok(product);
    }

    // ─── PRODUCT PARAMETERS ───────────────────────────────────────────────────────

    @GetMapping("/product-parameter")
    public ResponseEntity<List<ProductParameters>> getActiveProductParameters() {
        List<ProductParameters> parameters = productParametersService.getAllProductParameters();
        List<ProductParameters> activeParameters = parameters.stream()
                .filter(p -> !Boolean.FALSE.equals(p.getFlagActive()))
                .toList();
        return ResponseEntity.ok(activeParameters);
    }

    @GetMapping("/product-parameter/product/{idProduct}")
    public ResponseEntity<List<ProductParameters>> getProductParametersByProduct(@PathVariable Long idProduct) {
        List<ProductParameters> parameters = productParametersService.getProductParametersByProduct(idProduct);
        List<ProductParameters> activeParameters = parameters.stream()
                .filter(p -> !Boolean.FALSE.equals(p.getFlagActive()))
                .toList();
        return ResponseEntity.ok(activeParameters);
    }

    @PostMapping("/product-parameter/save/{idProduct}")
    public ResponseEntity<ProductParameters> saveProductParameter(@PathVariable Long idProduct, @RequestBody ProductParameters productParameter) {
        Product product = productService.getProductById(idProduct.intValue());
        productParameter.setProduct(product);
        productParametersService.saveProductParameter(productParameter);
        return ResponseEntity.ok(productParameter);
    }

    @GetMapping("/product-parameter/inactivate/{id}")
    public ResponseEntity<ProductParameters> inactivateProductParameterById(@PathVariable Long id) {
        ProductParameters parameter = productParametersService.getProductParameterById(id);
        parameter.setFlagActive(false);
        productParametersService.saveProductParameter(parameter);
        return ResponseEntity.ok(parameter);
    }

    // ─── VATS ─────────────────────────────────────────────────────────────────────

    @GetMapping("/vat")
    public ResponseEntity<List<Vat>> getActiveVats() {
        List<Vat> vats = vatService.getAllVats();
        List<Vat> activeVats = vats.stream()
                .filter(v -> !Boolean.FALSE.equals(v.getFlagActive()))
                .toList();
        return ResponseEntity.ok(activeVats);
    }

    @PostMapping("/vat/save")
    public ResponseEntity<Vat> saveVat(@RequestBody Vat vat) {
        vatService.saveVat(vat);
        return ResponseEntity.ok(vat);
    }

    @GetMapping("/vat/inactivate/{id}")
    public ResponseEntity<Vat> inactivateVatById(@PathVariable Long id) {
        Vat vat = vatService.getVatById(id);
        vat.setFlagActive(false);
        vatService.saveVat(vat);
        return ResponseEntity.ok(vat);
    }

    // ─── COLORS ───────────────────────────────────────────────────────────────────

    @GetMapping("/color")
    public ResponseEntity<List<Color>> getActiveColors() {
        List<Color> colors = colorService.getAllColors();
        List<Color> activeColors = colors.stream()
                .filter(c -> !Boolean.FALSE.equals(c.getFlagActive()))
                .toList();
        return ResponseEntity.ok(activeColors);
    }

    @PostMapping("/color/save")
    public ResponseEntity<Color> saveColor(@RequestBody Color color) {
        colorService.saveColor(color);
        return ResponseEntity.ok(color);
    }

    @GetMapping("/color/inactivate/{id}")
    public ResponseEntity<Color> inactivateColorById(@PathVariable Long id) {
        Color color = colorService.getColorById(id);
        color.setFlagActive(false);
        colorService.saveColor(color);
        return ResponseEntity.ok(color);
    }

    // ─── STORES ───────────────────────────────────────────────────────────────────

    @GetMapping("/store")
    public ResponseEntity<List<Store>> getActiveStores() {
        List<Store> stores = storeService.getAllStores();
        List<Store> activeStores = stores.stream()
                .filter(s -> !Boolean.FALSE.equals(s.getFlagActive()))
                .toList();
        return ResponseEntity.ok(activeStores);
    }

    @PostMapping("/store/save")
    public ResponseEntity<Store> saveStore(@RequestBody Store store) {
        storeService.saveStore(store);
        return ResponseEntity.ok(store);
    }

    @GetMapping("/store/inactivate/{id}")
    public ResponseEntity<Store> inactivateStoreById(@PathVariable Long id) {
        Store store = storeService.getStoreById(id);
        store.setFlagActive(false);
        storeService.saveStore(store);
        return ResponseEntity.ok(store);
    }

    // ─── ORDERS ───────────────────────────────────────────────────────────────────
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getActiveOrdersDTO() {
        List<OrderDTO> activeDTOs = orderService.getActiveOrdersDTO();
        return ResponseEntity.ok(activeDTOs);
    }

    @PostMapping("/orders/save")
    public ResponseEntity<OrderDTO> saveOrderDTO(@RequestBody OrderDTO orderDTO) {
        orderService.saveOrderDTO(orderDTO);
        return ResponseEntity.ok(orderDTO);
    }

    // ─── ORDER ITEMS ──────────────────────────────────────────────────────────────
    @PostMapping("/orderitems/save")
    public ResponseEntity<OrderItemDTO> saveOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        orderService.saveOrderItemDTO(orderItemDTO);
        return ResponseEntity.ok(orderItemDTO);
    }
}