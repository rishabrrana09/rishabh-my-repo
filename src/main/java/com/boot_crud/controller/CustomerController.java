package com.boot_crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.boot_crud.exception.UserNotFoundException;
import com.boot_crud.model.Customer;
import com.boot_crud.service.CustomerService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping("/list")
	public String showCustomerList(Model model) {
		List<Customer> listCustomers = customerService.fetchAllCustomers();
		model.addAttribute("listCustomers", listCustomers);
		return "customer_listing";
	}

	@GetMapping("/new")
	public String showNewForm(Model model) {
		model.addAttribute("user", new Customer());
		model.addAttribute("pageTitle", "Add New User");
		return "customer_form";
	}

	@PostMapping("/save")
	public String saveUser(Customer user, RedirectAttributes attributes) {
		customerService.saveCustomer(user);
		attributes.addFlashAttribute("message", "The customer has been saved successfully.");
		return "redirect:/customer/list";
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Customer user = customerService.getCustomerDetailById(id);
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit Customer Details (ID: " + id + ")");
			return "customer_form";
		} catch (UserNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/customer/list";
		}
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes attributes) {
		try {
			customerService.deleteCustomerById(id);
			attributes.addFlashAttribute("message", "The customer ID " + id + " has been deleted.");
		} catch (UserNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/customer/list";
	}

	@GetMapping("/fetch/sunbase/db")
	public String fetchCustomerDetails(RedirectAttributes attributes) {
		try {
			customerService.fetchAllCustomersFromSunDB();
			attributes.addFlashAttribute("message", "The customer data synced successfully");
		} catch (Exception e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/customer/list";
	}
	

}
