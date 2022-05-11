package com.tubz.brewery.web.controllers;

import com.tubz.brewery.domain.Customer;
import com.tubz.brewery.repositories.CustomerRepository;
import com.tubz.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerTestIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    CustomerRepository customerRepository;

    Customer customer;

    @BeforeEach
    void setup() {
     customer = customerRepository.findAll().get(0);
    }

    @Test
    void listOrders() {
        BeerOrderPagedList beerOrderPagedList = restTemplate
                .getForObject("/api/v1/customers/" + customer.getId()+ "/orders", BeerOrderPagedList.class);
        assertEquals(1, beerOrderPagedList.getContent().size());

    }
}