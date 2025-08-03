package com.crm.customer.mapper;

import com.crm.customer.dto.CustomerDto;
import com.crm.customer.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Customer Mapper
 * 
 * Maps between Customer entity and CustomerDto.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Component
public class CustomerMapper {

    /**
     * Map Customer entity to CustomerDto
     * 
     * @param customer the customer entity
     * @return the customer DTO
     */
    public CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setPhone(customer.getPhone());
        customerDto.setCompany(customer.getCompany());
        customerDto.setSource(customer.getSource());
        customerDto.setDescription(customer.getDescription());
        customerDto.setCreatedBy(customer.getCreatedBy());
        customerDto.setIsActive(customer.getIsActive());
        customerDto.setCreatedAt(customer.getCreatedAt());
        customerDto.setUpdatedAt(customer.getUpdatedAt());

        return customerDto;
    }

    /**
     * Map CustomerDto to Customer entity
     * 
     * @param customerDto the customer DTO
     * @return the customer entity
     */
    public Customer toEntity(CustomerDto customerDto) {
        if (customerDto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setId(customerDto.getId());
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setPhone(customerDto.getPhone());
        customer.setCompany(customerDto.getCompany());
        customer.setSource(customerDto.getSource());
        customer.setDescription(customerDto.getDescription());
        customer.setCreatedBy(customerDto.getCreatedBy());
        customer.setIsActive(customerDto.getIsActive());
        customer.setCreatedAt(customerDto.getCreatedAt());
        customer.setUpdatedAt(customerDto.getUpdatedAt());

        return customer;
    }

    /**
     * Map list of Customer entities to list of CustomerDto
     * 
     * @param customers the list of customer entities
     * @return the list of customer DTOs
     */
    public List<CustomerDto> toDtoList(List<Customer> customers) {
        if (customers == null) {
            return null;
        }

        return customers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Map list of CustomerDto to list of Customer entities
     * 
     * @param customerDtos the list of customer DTOs
     * @return the list of customer entities
     */
    public List<Customer> toEntityList(List<CustomerDto> customerDtos) {
        if (customerDtos == null) {
            return null;
        }

        return customerDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 