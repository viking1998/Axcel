package com.spring.hibernate.axcel.services;

import com.spring.hibernate.axcel.models.User;

public interface UserService {
	
	public void save(User user);
	
	public User getByEmail(String email);
	
	public User getByName(String username);
}
