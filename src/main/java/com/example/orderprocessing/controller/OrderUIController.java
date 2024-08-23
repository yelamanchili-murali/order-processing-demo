package com.example.orderprocessing.controller;

import com.example.orderprocessing.model.Order;
import com.example.orderprocessing.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderUIController {

    private final OrderService orderService;

    public OrderUIController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/new")
    public String showCreateOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "order_form";
    }

    @PostMapping
    public String createOrder(@ModelAttribute Order order) {
        orderService.createOrder(order.getCustomerId(), order.getOrderDetails());
        return "redirect:/orders/list";
    }

    @GetMapping("/list")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order_list";
    }
}
