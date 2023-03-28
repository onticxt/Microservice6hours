package com.example.productservice;

import com.example.productservice.dtos.ProductRequest;
import com.example.productservice.models.Product;
import com.example.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {


	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	static MockMvc mockMvc;
	@Autowired
	private	ProductRepository productRepository;
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProducts() throws Exception{

	ProductRequest productRequest= getProductRequests();
String productRequestString= objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString)

		).andExpect(MockMvcResultMatchers.status().isCreated());
		Assertions.assertTrue(productRepository.findAll().size()==1);
	}

	private ProductRequest getProductRequests(){
		return ProductRequest.builder()
				.name("iphone 13")
				.description("iphone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}


}
