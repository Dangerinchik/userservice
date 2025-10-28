package com.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.CardInfoDTO;
import com.userservice.repository.CardInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CardInfoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Container
    private static final GenericContainer<?> genericContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.data.redis.host", genericContainer::getHost);
        registry.add("spring.data.redis.port", genericContainer::getFirstMappedPort);

    }

    private static CardInfoDTO cardInfoDTO;

    @BeforeEach
    public void setUp() {
        cardInfoRepository.deleteAll();
        cardInfoDTO = new CardInfoDTO();
        cardInfoDTO.setNumber("1111 1111 1111 1111");
        cardInfoDTO.setHolder("DANILA RAINCHYK");
        cardInfoDTO.setExpirationDate("(11/29)");
    }

    @Test
    public void testCreateCardInfo() throws Exception {
        mockMvc.perform(post("/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInfoDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holder").value("DANILA RAINCHYK"))
                .andExpect(jsonPath("$.number").value("1111 1111 1111 1111"))
                .andExpect(jsonPath("$.expirationDate").value("(11/29)"));

    }

    @Test
    public void testCreateCardInfo__WhenDuplicate() throws Exception {
        mockMvc.perform(post("/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInfoDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInfoDTO)))
                .andDo(print())
                .andExpect(status().isConflict());

    }

    @Test
    public void testGetCardInfo() throws Exception {

        String response = mockMvc.perform(post("/card/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardInfoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CardInfoDTO created = objectMapper.readValue(response, CardInfoDTO.class);

        var result = mockMvc.perform(get("/card/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("DANILA RAINCHYK"))
                .andExpect(jsonPath("$.number").value("1111 1111 1111 1111"))
                .andExpect(jsonPath("$.expirationDate").value("(11/29)"));
    }

    @Test
    public void testGetCardInfo__WhenCardInfoDoesNotExists() throws Exception {
        mockMvc.perform(get("/card/80"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGelAllCardsInfo() throws Exception {
        mockMvc.perform(post("/card/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardInfoDTO)))
                .andExpect(status().isCreated());

        CardInfoDTO toCreate = new CardInfoDTO();
        toCreate.setNumber("2222 2222 2222 2222");
        toCreate.setHolder("IVAN IVANOV");
        toCreate.setExpirationDate("(12/28)");

        mockMvc.perform(post("/card/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/card/all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].holder").value("IVAN IVANOV"));

    }

    @Test
    public void testGetAllCardsInfo__WhenCardInfoDoesNotExists() throws Exception {
        mockMvc.perform(get("/card/all"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCardInfo() throws Exception {
        mockMvc.perform(post("/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInfoDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/card/1/delete"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/card/1"))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    public void testDeleteCardInfo__WhenCardInfoDoesNotExists() throws Exception {
        mockMvc.perform(delete("/card/1/delete"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCardInfo() throws Exception {
        mockMvc.perform(post("/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInfoDTO)))
                .andExpect(status().isCreated());

        CardInfoDTO toUpdate = new CardInfoDTO();
        toUpdate.setNumber("3333 3333 3333 3333");
        toUpdate.setHolder("IVAN IVANOV");
        toUpdate.setExpirationDate("(12/28)");

        mockMvc.perform(put("/card/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.holder").value("IVAN IVANOV"))
        .andExpect(jsonPath("$.number").value("3333 3333 3333"))
        .andExpect(jsonPath("$.expirationDate").value("(12/28)"));


    }

    @Test
    public void testUpdateCardInfo__WhenCardInfoDoesNotExists() throws Exception {
        mockMvc.perform(put("/card/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInfoDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
