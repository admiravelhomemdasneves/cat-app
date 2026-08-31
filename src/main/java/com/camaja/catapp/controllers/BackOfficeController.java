package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.dto.PermissionRequestDTO;
import com.camaja.catapp.dto.ProductParameterDTO;
import com.camaja.catapp.dto.RegisterUserDTO;
import com.camaja.catapp.dto.UserDTO;
import com.camaja.catapp.models.*;
import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.services.auth.AuthenticationService;
import com.camaja.catapp.services.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/bo")
@RestController
@CrossOrigin
public class BackOfficeController {
    private final ContactService contactService;
    private final ProductService productService;
    private final ProductParametersService productParametersService;
    private final VatService vatService;
    private final StoreService storeService;
    private final OrderService orderService;
    private final BillingService billingService;
    private final CompanyService companyService;
    private final AccessLevelService accessLevelService;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final SecurityUtils securityUtils;
    private final BackofficePageService backofficePageService;
    private final RolePagePermissionService rolePagePermissionService;

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
        ContactDTO contactDTO = contactService.getContactById(id);
        return ResponseEntity.ok(contactDTO);
    }

    @GetMapping("/contact/inactivate/{id}")
    public ResponseEntity<ContactDTO> inactivateContactById(@PathVariable Long id) {
        ContactDTO contactDTO = contactService.getContactById(id);
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

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/product/inactivate/{id}")
    public ResponseEntity<Product> inactivateProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
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
        Product product = productService.getProductById(idProduct);
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

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDTOById(id));
    }

    @GetMapping("/orders/{id}/recalculate")
    public ResponseEntity<OrderDTO> recalculateOrder(@PathVariable Long id) {
        billingService.recalculateForOrder(id);
        return ResponseEntity.ok(orderService.getOrderDTOById(id));
    }

    @PostMapping("/orders/save")
    public ResponseEntity<OrderDTO> saveOrderDTO(@RequestBody OrderDTO orderDTO) {
        orderService.saveOrderDTO(orderDTO);
        return ResponseEntity.ok(orderDTO);
    }

    // ─── ORDER ITEMS ──────────────────────────────────────────────────────────────
    @GetMapping("/orderitems/inactivate/{id}")
    public ResponseEntity<OrderItem> inactivateOrderItemById(@PathVariable Long id) {
        OrderItem orderItem = orderService.getOrderItemById(id);

        if (orderItem != null) {
            orderItem.setFlagActive(false);
            orderService.saveOrderItem(orderItem);
        }

        return ResponseEntity.ok(orderItem);
    }

    @PostMapping("/orderitems/save")
    public ResponseEntity<OrderItemDTO> saveOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        orderService.saveOrderItemDTO(orderItemDTO);
        return ResponseEntity.ok(orderItemDTO);
    }

    // ─── COMPANIES ────────────────────────────────────────────────────────────────

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/companies/mine")
    public ResponseEntity<Company> getMyCompany() {
        return ResponseEntity.ok(securityUtils.getCurrentCompany());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/companies/save")
    public ResponseEntity<Company> saveCompany(@RequestBody Company company) {
        return ResponseEntity.ok(companyService.saveCompany(company));
    }

    // ─── ACCESS LEVELS ────────────────────────────────────────────────────────────

    @GetMapping("/accessLevels")
    public ResponseEntity<List<AccessLevel>> getAccessLevels() {
        return ResponseEntity.ok(accessLevelService.getAllAccessLevels());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/accessLevels/save")
    public ResponseEntity<AccessLevel> saveAccessLevel(@RequestBody AccessLevel level) {
        return ResponseEntity.ok(accessLevelService.saveAccessLevel(level));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/accessLevels/{id}")
    public ResponseEntity<Void> deleteAccessLevel(@PathVariable Long id) {
        accessLevelService.deleteAccessLevel(id);
        return ResponseEntity.noContent().build();
    }

    // ─── USERS ────────────────────────────────────────────────────────────────────

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsersInCompany() {
        return ResponseEntity.ok(userService.getUsersInCurrentCompany());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users/save")
    public ResponseEntity<UserDTO> createUser(@RequestBody RegisterUserDTO registerUserDTO) {
        User created = authenticationService.signup(registerUserDTO);
        return ResponseEntity.ok(UserDTO.from(created));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}/access-level")
    public ResponseEntity<Void> updateUserAccessLevel(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        userService.updateAccessLevel(id, body.get("accessLevelId"));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/inactivate/{id}")
    public ResponseEntity<Void> inactivateUser(@PathVariable Long id) {
        userService.inactivateUser(id);
        return ResponseEntity.ok().build();
    }

    // ─── BACKOFFICE PAGES ─────────────────────────────────────────────────────────

    @GetMapping("/pages")
    public ResponseEntity<List<BackofficePage>> getPages() {
        return ResponseEntity.ok(backofficePageService.getAllPages());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/pages/save")
    public ResponseEntity<BackofficePage> savePage(@RequestBody BackofficePage page) {
        return ResponseEntity.ok(backofficePageService.savePage(page));
    }

    // ─── ROLE PAGE PERMISSIONS ────────────────────────────────────────────────────

    @GetMapping("/permissions/mine")
    public ResponseEntity<List<RolePagePermission>> getMyPermissions() {
        return ResponseEntity.ok(rolePagePermissionService.getPermissionsForCurrentRole());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/permissions")
    public ResponseEntity<List<RolePagePermission>> getAllPermissions() {
        return ResponseEntity.ok(rolePagePermissionService.getAllPermissions());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/permissions/save")
    public ResponseEntity<RolePagePermission> savePermission(@RequestBody PermissionRequestDTO dto) {
        return ResponseEntity.ok(rolePagePermissionService.savePermission(dto));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        rolePagePermissionService.deletePermission(id);
        return ResponseEntity.ok().build();
    }
}