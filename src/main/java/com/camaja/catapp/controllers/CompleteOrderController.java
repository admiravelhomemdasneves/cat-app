package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.CompleteOrderDTO;
import com.camaja.catapp.dto.CompleteOrderProductDTO;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import com.camaja.catapp.repository.OrderProductRepository;
import com.camaja.catapp.repository.OrderProductSpecsRepository;
import com.camaja.catapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order/complete")
@CrossOrigin
public class CompleteOrderController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderProductRepository orderProductRepository;
    @Autowired
    OrderProductSpecsRepository orderProductSpecsRepository;

    @GetMapping("/{id}")
    CompleteOrderDTO readCompleteOrder(@PathVariable int id) {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            Iterable<OrderProduct> orderProducts = orderProductRepository.findByOrder(order);
            List<CompleteOrderProductDTO> completeOrderProducts = new ArrayList<>();

            //Fetch complete info for each OrderProduct
            for (OrderProduct orderProduct : orderProducts) {
                Iterable<OrderProductSpecs> orderProductSpecs = orderProductSpecsRepository.findByOrderProduct(Optional.ofNullable(orderProduct));
                CompleteOrderProductDTO completeOrderProductDTO = new CompleteOrderProductDTO(orderProduct, orderProductSpecs);
                completeOrderProducts.add(completeOrderProductDTO);
            }

            CompleteOrderDTO completeOrderDTO = new CompleteOrderDTO(order.get(), completeOrderProducts);

            return completeOrderDTO;
        } else {
            return null;
        }
    }
}
