package com.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.UserDTO;
import com.userservice.repository.UserRepository;
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
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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

    private static UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        userDTO = new UserDTO();
        userDTO.setEmail("danik@gmail.com");
        userDTO.setName("Danila");
        userDTO.setSurname("Rainchik");
    }

    @Test
    public void testCreateUser() throws Exception {
        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("danik@gmail.com"))
                .andExpect(jsonPath("$.name").value("Danila"))
                .andExpect(jsonPath("$.surname").value("Rainchik"));

    }

    @Test
    public void testCreateUser__WhenDuplicate() throws Exception {
        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isConflict());

    }

    @Test
    public void testGetUser() throws Exception {

        String response = mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO created = objectMapper.readValue(response, UserDTO.class);

        var result = mockMvc.perform(get("/user/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("danik@gmail.com"))
                .andExpect(jsonPath("$.name").value("Danila"))
                .andExpect(jsonPath("$.surname").value("Rainchik"));
    }

    @Test
    public void testGetUser__WhenUserDoesNotExists() throws Exception {
        mockMvc.perform(get("/user/80"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/user/email/danik@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("danik@gmail.com"))
                .andExpect(jsonPath("$.name").value("Danila"))
                .andExpect(jsonPath("$.surname").value("Rainchik"));

    }

    @Test
    public void testGetUserByEmail__WhenUserDoesNotExists() throws Exception {
        mockMvc.perform(get("/user/email/danik@gmail.com"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGelAllUsers() throws Exception {
        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        UserDTO toCreate = new UserDTO();
        toCreate.setEmail("ivan@gmail.com");
        toCreate.setName("Ivan");
        toCreate.setSurname("Ivanov");

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/user/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].email").value("ivan@gmail.com"));

    }

    @Test
    public void testGetAllUsers__WhenUserDoesNotExists() throws Exception {
        mockMvc.perform(get("/user/all"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/user/1/delete"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/user/1"))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    public void testDeleteUser__WhenUserDoesNotExists() throws Exception {
        mockMvc.perform(delete("/user/1/delete"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateUser() throws Exception {
        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        UserDTO toUpdate = new UserDTO();
        toUpdate.setEmail("ivan@gmail.com");
        toUpdate.setName("Ivan");
        toUpdate.setSurname("Ivanov");

        mockMvc.perform(put("/user/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ivan@gmail.com"))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.surname").value("Ivanov"));


    }

    @Test
    public void testUpdateUser__WhenUserDoesNotExists() throws Exception {
        mockMvc.perform(put("/user/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
