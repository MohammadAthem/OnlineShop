package application;

public class Product {
	private Category Category;
	private String Brand;
	private String Name;
	private double Price;
	private String imgFileName;
	private String selectedSize = null;
	private String selectedColor = null;

	public Product() {
	}

	public Product(Category category, String brand, String name, double price) {
		this.Category = category;
		this.Brand = brand;
		this.Name = name;
		this.Price = price;
	}

	public Product(Category category, String brand, String name, double price, String imgFileName) {
		this.Category = category;
		this.Brand = brand;
		this.Name = name;
		this.Price = price;
		this.imgFileName = imgFileName;
	}

	public Category getCategory() {
		return Category;
	}

	public void setCategory(Category category) {
		Category = category;
	}

	public String getBrand() {
		return Brand;
	}

	public void setBrand(String brand) {
		Brand = brand;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public double getPrice() {
		return Price;
	}

	public void setPrice(double price) {
		Price = price;
	}

	public String getSelectedSize() {
		return selectedSize;
	}

	public void setSelectedSize(String selectedSize) {
		this.selectedSize = selectedSize;
	}

	public String getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(String selectedColor) {
		this.selectedColor = selectedColor;
	}

	public String getImgFileName() {
		return imgFileName;
	}

	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}

	@Override
	public boolean equals(Object o) { // equals method in order to check for duplication in the cart
		if (!(o instanceof Product)) {
			return false;
		} else {
			Product p = (Product) o;

			return this.getCategory().equals(p.getCategory()) && this.getBrand().equals(p.getBrand())
					&& this.getName().equals(p.getName()) && this.getPrice() == p.getPrice()
					&& this.getImgFileName().equals(p.getImgFileName())
					&& this.getSelectedColor().equals(p.getSelectedColor())
					&& this.getSelectedSize().equals(p.getSelectedSize());
		}
	}

	public Product(Product original) { // clone method to make sure items of different sizes and colors don't get
										// duplicated together (see Product newItem = new Product(product);)
		this.Category = original.Category;
		this.Brand = original.Brand;
		this.Name = original.Name;
		this.Price = original.Price;
		this.imgFileName = original.imgFileName;
	}

}
