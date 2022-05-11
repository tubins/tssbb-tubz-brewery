package com.tubz.brewery.web.controllers;

import com.tubz.brewery.services.BeerService;
import com.tubz.brewery.web.model.BeerDto;
import com.tubz.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BeerControllerTest {

    @InjectMocks
    BeerController beerController;

    @Mock
    BeerService beerService;

    MockMvc mockMvc;

    BeerDto validBeerDto;

    @BeforeEach
    void setup() {
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

        mockMvc = MockMvcBuilders.standaloneSetup(beerController).build();
    }

    @Test
    void listBeers() {

    }


    @Test
    void getBeerById() throws Exception {
        given(beerService.findBeerById(any(UUID.class))).willReturn(validBeerDto);
        mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(validBeerDto.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(validBeerDto.getBeerName())));
    }
}