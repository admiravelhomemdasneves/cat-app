package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.CompleteOrderDTO;
import com.camaja.catapp.dto.CompleteOrderProductDTO;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import com.camaja.catapp.services.OrderProductService;
import com.camaja.catapp.services.OrderProductSpecsService;
import com.camaja.catapp.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderProductService orderProductService;
    @Autowired
    OrderProductSpecsService orderProductSpecsService;

    @GetMapping
    Iterable<Order> read() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    Optional<Order> read(@PathVariable int id) {
        return orderService.findById(id);
    }

    @GetMapping("/complete/{id}")
    CompleteOrderDTO readCompleteOrder(@PathVariable int id) {
        Optional<Order> order = orderService.findById(id);

        if (order.isPresent()) {
            Iterable<OrderProduct> orderProducts = orderProductService.findByOrder(order);
            List<CompleteOrderProductDTO> completeOrderProducts = new ArrayList<>();

            //Fetch complete info for each OrderProduct
            for (OrderProduct orderProduct : orderProducts) {
                Iterable<OrderProductSpecs> orderProductSpecs = orderProductSpecsService.findByOrderProduct(Optional.ofNullable(orderProduct));
                CompleteOrderProductDTO completeOrderProductDTO = new CompleteOrderProductDTO(orderProduct, orderProductSpecs);
                completeOrderProducts.add(completeOrderProductDTO);
            }

            CompleteOrderDTO completeOrderDTO = new CompleteOrderDTO(order.get(), completeOrderProducts);

            return completeOrderDTO;
        } else {
            return null;
        }
    }

    @PostMapping
    Order save(@RequestBody Order order) {
        if (order.getDate_requested() == null) {order.setDate_requested(new Timestamp(System.currentTimeMillis()));}
        order.setA$date$(new Timestamp(System.currentTimeMillis()));
        orderService.save(order);
        return order;
    }

    @PutMapping
    Order update(@RequestBody Order order) {
        if (order.getDate_requested() == null) {order.setDate_requested(new Timestamp(System.currentTimeMillis()));}
        order.setA$date$(new Timestamp(System.currentTimeMillis()));
        orderService.save(order);
        return order;
    }

    @DeleteMapping("/{id}")
    Optional<Order> delete(@PathVariable int id) {
        orderService.deleteById(id);
        return read(id);
    }
}
