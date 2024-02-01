package com.boot_crud.service;

import java.util.List;

import com.boot_crud.exception.UserNotFoundException;
import com.boot_crud.model.Customer;

public interface CustomerService {

	List<Customer> fetchAllCustomers();

	void saveCustomer(Customer user);

	Customer getCustomerDetailById(Integer id) throws UserNotFoundException;

	void deleteCustomerById(Integer id) throws UserNotFoundException;

	void fetchAllCustomersFromSunDB();

}
