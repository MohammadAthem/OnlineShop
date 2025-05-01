package application;

public class Category {
	
	private String catName;
	private String catImg;
	
	public Category() {
		
	}
	
	public Category(String catName, String catImg) {
		super();
		this.catName = catName;
		this.catImg = catImg;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getCatImg() {
		return catImg;
	}

	public void setCatImg(String catImg) {
		this.catImg = catImg;
	}

	
	
	

}
