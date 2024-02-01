package com.boot_crud.service.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.boot_crud.dao.CustomerResponse;
import com.boot_crud.exception.UserNotFoundException;
import com.boot_crud.model.Customer;
import com.boot_crud.repository.CustomerRepository;
import com.boot_crud.service.CustomerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Value("${sunbase.customer.data.api.url}")
	private String sunbaseApiUrl;
	@Value("${sunbase.api.token}")
	private String sunbaseApiToken;

	@Override
	public List<Customer> fetchAllCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public void saveCustomer(Customer customer) {
		customerRepository.save(customer);
	}

	@Override
	public Customer getCustomerDetailById(Integer id) throws UserNotFoundException {
		Optional<Customer> result = customerRepository.findById(id);
		if (result.isPresent()) {
			return result.get();
		}
		throw new UserNotFoundException("Could not find any customer with ID " + id);
	}

	@Override
	public void deleteCustomerById(Integer id) throws UserNotFoundException {
		Long count = customerRepository.countById(id);
		if (count == null || count == 0) {
			throw new UserNotFoundException("Could not find any customer with ID " + id);
		}
		customerRepository.deleteById(id);
	}

	/*
	 * this API will fetch the customer data from the Sunbase database and will save
	 * it and for the existing customer it will update the same on our database
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void fetchAllCustomersFromSunDB() {
		OkHttpClient client = null;
		try {
			Customer customer = null;
			List<Customer> customerList = customerRepository.findAll();
			String existingCustomer = customerList.stream().map(x -> x.getEmail().toString())
					.collect(Collectors.joining(","));
			client = new OkHttpClient();
			Request request = new Request.Builder().url(sunbaseApiUrl).get()
					.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + sunbaseApiToken)
					.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();
			Response response = client.newCall(request).execute();
			Type collectionType = new TypeToken<List<CustomerResponse>>() {
			}.getType();
			List<CustomerResponse> customerResponse = (List<CustomerResponse>) new Gson()
					.fromJson(response.body().string(), collectionType);
			for (CustomerResponse user : customerResponse) {
				customer = new Customer();
				if (!existingCustomer.contains(user.getEmail())) {
					customer.setFirstName(user.getFirst_name());
					customer.setLastName(user.getLast_name());
					customer.setStreet(user.getStreet());
					customer.setAddress(user.getAddress());
					customer.setCity(user.getCity());
					customer.setState(user.getState());
					customer.setEmail(user.getEmail());
					customer.setPhone(user.getPhone());
					customerRepository.save(customer);
				} else {
					Customer existingUser = customerRepository.findByEmail(user.getEmail());
					existingUser.setFirstName(user.getFirst_name());
					existingUser.setLastName(user.getLast_name());
					existingUser.setStreet(user.getStreet());
					existingUser.setAddress(user.getAddress());
					existingUser.setCity(user.getCity());
					existingUser.setState(user.getState());
					existingUser.setPhone(user.getPhone());
					customerRepository.save(existingUser);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
