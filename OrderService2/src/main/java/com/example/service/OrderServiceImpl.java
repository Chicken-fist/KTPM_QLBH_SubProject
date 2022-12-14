package com.example.service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.dto.UserDTO;
import com.example.dto.OrderDTO;
import com.example.dto.OrderDTORefProductDTO;
import com.example.dto.OrderRefAll;
import com.example.dto.ProductDTO;
import com.example.entity.Order;
import com.example.repository.OrderRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
    OAuth2RestTemplate oauth2RestTemplate;
	
	
	@Override
	public Order addOrder(Order order) {
		// TODO Auto-generated method stub
		return orderRepository.save(order);
	}

	@Override
	public String deleteOrder(int orderId) {
		// TODO Auto-generated method stub
		 orderRepository.deleteById(orderId);
		 return "Deleted " + orderId;
	}

	@Override
	public Order updateOrder(Order order) {
		// TODO Auto-generated method stub
		Order Order2 = orderRepository.saveAndFlush(order);
		return order;
	}

	@Override
	public OrderDTO getOrderById(int id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<String> entity = new HttpEntity<String>(headers);
	    Order order = orderRepository.findById(id).get();
		// goi service customer
		UserDTO customerDto = oauth2RestTemplate.exchange("http://localhost:9002/api/user"
	    		  .concat("/")
	    		  .concat(String.valueOf(order.getUserId())),
	    		  	HttpMethod.GET, 
					 entity, 
					 UserDTO.class
					).getBody();

		OrderDTO result = new OrderDTO(order.getOrderId(), order.getPrice(), customerDto, order.getProductId());
		return result;
	}
	@Override
	public OrderDTORefProductDTO getOrderByIdProduct(int id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<String> entity = new HttpEntity<String>(headers);
	    Order order = orderRepository.findById(id).get();
		// goi service customer
		ProductDTO customerDto = oauth2RestTemplate.exchange("http://localhost:9003/api/product"
	    		  .concat("/")
	    		  .concat(String.valueOf(order.getUserId())),
	    		  	HttpMethod.GET, 
					 entity, 
					 ProductDTO.class
					).getBody();
//		customerDt
		
		OrderDTORefProductDTO result = new OrderDTORefProductDTO(order.getOrderId(),  
				order.getUserId(), customerDto);
		return result;
//		return null;
	}
	
	@Override
	public OrderRefAll getOrderByIdGetAll(int id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<String> entity = new HttpEntity<String>(headers);
	    Order order = orderRepository.findById(id).get();
		// goi service customer
		ProductDTO productDto = oauth2RestTemplate.exchange("http://localhost:9003/api/product"
	    		  .concat("/")
	    		  .concat(String.valueOf(order.getUserId())),
	    		  	HttpMethod.GET, 
					 entity, 
					 ProductDTO.class
					).getBody();
		UserDTO customerDto = oauth2RestTemplate.exchange("http://localhost:9002/api/user"
	    		  .concat("/")
	    		  .concat(String.valueOf(order.getUserId())),
	    		  	HttpMethod.GET, 
					 entity, 
					 UserDTO.class
					).getBody();
		
		OrderRefAll result = new OrderRefAll(order.getOrderId(),  
				customerDto, productDto);
		return result;
//		return null;
	}

	@Override
	public List<Order> getListOrder() {
		// TODO Auto-generated method stub
		return orderRepository.findAll();
	}

	@Override
	public ProductDTO getProductById(int id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<String> entity = new HttpEntity<String>(headers);
//	    Order order = orderRepository.findById(id).get();
	    ProductDTO productDTO = oauth2RestTemplate.exchange("http://localhost:9003/api/product"
	    		  .concat("/")
	    		  .concat(String.valueOf(id)),
	    		  	HttpMethod.GET, 
					 entity, 
					 ProductDTO.class
					).getBody();
		return productDTO;
	}

}
