package com.spring.hibernate.axcel.repositories;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.spring.hibernate.axcel.models.User;

@Transactional
@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	public User findByName(String name);
	public User findByEmail(String email);
}
