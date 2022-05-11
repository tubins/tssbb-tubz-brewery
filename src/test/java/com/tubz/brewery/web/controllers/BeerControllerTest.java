package com.tubz.brewery.web.controllers;

import com.tubz.brewery.services.BeerService;
import com.tubz.brewery.web.model.BeerDto;
import com.tubz.brewery.web.model.BeerPagedList;
import com.tubz.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @MockBean
    BeerService beerService;

    @Autowired
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

    }

    @AfterEach
    void tearDown() {
        reset(beerService);
    }

    @Test
    void testGetBeerById() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        given(beerService.findBeerById(any(UUID.class))).willReturn(validBeerDto);
        mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(validBeerDto.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(validBeerDto.getBeerName())))
                .andExpect(jsonPath("$.createdDate", is(formatter.format(validBeerDto.getCreatedDate()))));
    }

    @DisplayName("List Ops -")
    @Nested
    public class TestListOperations {

        @Captor
        ArgumentCaptor<String> beerNameCaptor;

        @Captor
        ArgumentCaptor<BeerStyleEnum> beerStyleEnumArgumentCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor;

        BeerPagedList beerPagedList;

        @BeforeEach
        void setup() {
            List<BeerDto> beerDtoList = new ArrayList<>();
            beerDtoList.add(validBeerDto);
            beerDtoList.add(
                    BeerDto
                            .builder()
                            .beerName("Beer 2")
                            .beerStyle(BeerStyleEnum.PALE_ALE)
                            .price(new BigDecimal("12.5"))
                            .createdDate(OffsetDateTime.now())
                            .lastModifiedDate(OffsetDateTime.now())
                            .id(UUID.randomUUID())
                            .upc(789456l)
                            .build());
            beerPagedList = new BeerPagedList(beerDtoList, PageRequest.of(1, 1), 2L);
            given(beerService.listBeers(beerNameCaptor.capture(), beerStyleEnumArgumentCaptor.capture(), pageRequestArgumentCaptor.capture()))
                    .willReturn(beerPagedList);

        }

        @Test
        void testListBeers() throws Exception {

            mockMvc.perform(get("/api/v1/beer")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(validBeerDto.getId().toString())))
                    .andExpect(jsonPath("$.content[0].beerName", is(validBeerDto.getBeerName())));
        }
    }
}