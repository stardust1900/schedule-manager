package com.demonisles.schedulemanager.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demonisles.schedulemanager.domain.Customer;
import com.demonisles.schedulemanager.repository.CustomerRepository;
import com.demonisles.schedulemanager.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public List<Customer> queryAll() {
		Iterable<Customer> customers = customerRepository.findAll();
		List<Customer> result = new ArrayList<Customer>();
		for(Customer customer : customers) {
			result.add(customer);
		}
		return result;
	}

}
