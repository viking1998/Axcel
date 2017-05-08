package com.spring.hibernate.axcel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.hibernate.axcel.models.User;
import com.spring.hibernate.axcel.services.UserService;
import com.spring.hibernate.axcel.validators.UserRegValidator;

@Controller
@EnableAutoConfiguration
public class UserLogRegController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRegValidator userValidator;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "redirect:/login";
	}

	@RequestMapping(value={"/login"}, method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String home(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getByName(auth.getName());
		if(user == null) {
			return "redirect:/login";
		}
		model.addAttribute("user", user);
		return "home";
	}
	
	@RequestMapping(value="/registration", method=RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("user", new User());
		return "registration";
	}

	@RequestMapping(value="/registration", method=RequestMethod.POST)
	public String register(@ModelAttribute("user") User user, BindingResult bindingRes, RedirectAttributes redirAttr) {
		userValidator.validate(user, bindingRes);
		if(bindingRes.hasErrors()) {
			return "registration";
		}
		userService.save(user);
		redirAttr.addFlashAttribute("message", "Account successfully created!");
		return "redirect:/login";
	}
}
