package application;

import java.util.ArrayList;

public class User {
	private String name;
	private String password;
	private String Email;
	private String Phone;
	private ArrayList<Product> cart;
	private static int numberofUsers = 0;

	public User() {
		numberofUsers++;
	}

	public User(String name, String password) {
		this.name = name;
		this.password = password;
		this.cart = new ArrayList<>();
		numberofUsers++;
	}

	public User(String name, String password, String email, String phone) {
		super();
		this.name = name;
		this.password = password;
		this.Email = email;
		this.Phone = phone;
		this.cart = new ArrayList<>();
		numberofUsers++;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static int getNumberofUsers() {
		return numberofUsers;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public ArrayList<Product> getCart() {
		return cart;
	}

	public void setCart(ArrayList<Product> cart) {
		this.cart = cart;
	}
	
	public void addtoCart(Product p) {
		cart.add(p);
	}
	
	public void clearCart() {
		cart.clear();
	}

	@Override
	public String toString() {
		return name + " " + password + " " + Email + " " + Phone;
	}

}
