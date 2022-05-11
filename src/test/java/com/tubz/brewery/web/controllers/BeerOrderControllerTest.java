package com.tubz.brewery.web.controllers;

import com.tubz.brewery.services.BeerOrderService;
import com.tubz.brewery.web.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {
    @MockBean
    private BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    BeerDto validBeerDto;
    BeerOrderDto beerOrderDto;

    BeerOrderPagedList beerOrderPagedList;

    @BeforeEach
    void setUp() {
        validBeerDto = BeerDto
                .builder()
                .id(UUID.randomUUID())
                .beerName("Test beer")
                .beerStyle(BeerStyleEnum.IPA)
                .price(new BigDecimal("2.5"))
                .upc(123456789l)
                .quantityOnHand(4)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();

        beerOrderDto = BeerOrderDto.builder()
                .customerId(UUID.randomUUID())
                .id(UUID.randomUUID())
                .createdDate(OffsetDateTime.now())
                .version(2)
                .customerRef("ref/123")
                .orderStatusCallbackUrl("http://localhost:8080/testCalbackUrl")
                .orderStatus(OrderStatusEnum.READY)
                .beerOrderLines(List.of(BeerOrderLineDto.builder().beerId(validBeerDto.getId()).build()))
                .build();

        beerOrderPagedList = new BeerOrderPagedList(List.of(beerOrderDto), PageRequest.of(1, 1), 1L);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(beerOrderService);
    }

    @Test
    void listOrders() throws Exception {
        given(beerOrderService.listOrders(any(), any())).willReturn(beerOrderPagedList);
        mockMvc.perform(get("/api/v1/customers/" + beerOrderDto.getCustomerId().toString() + "/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }



    @Test
    void getOrder() throws Exception {
        given(beerOrderService.getOrderById(any(), any())).willReturn(beerOrderDto);
        mockMvc.perform(get("/api/v1/customers/" + beerOrderDto.getCustomerId().toString() + "/orders/" + beerOrderDto.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }
}