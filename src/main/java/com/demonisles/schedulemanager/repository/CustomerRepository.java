package com.demonisles.schedulemanager.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.demonisles.schedulemanager.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

  List<Customer> findByLastName(String lastName);

  Customer findById(long id);
}