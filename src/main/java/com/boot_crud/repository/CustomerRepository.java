package com.boot_crud.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.boot_crud.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	public Long countById(Integer id);
	
	@Query(value = "Select * from customer where email = ?1")
    Customer findByEmail(String email);
	
}
