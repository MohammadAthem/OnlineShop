package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class Main extends Application {

	ArrayList<Product> Products = new ArrayList<Product>();
	ArrayList<Product> Cart = new ArrayList<Product>();
	private static final String IMG_PATH = "file:///C:/Users/moham/eclipse-workspace/onlineShop/src/application/Images/";

	@Override
	public void start(Stage primaryStage) {
		productsList();

		Font Roboto = Font.loadFont(getClass().getResourceAsStream("Roboto-SemiBold.ttf"), 16);

		VBox Categories = new VBox(15);
		Categories.setPadding(new Insets(20));
		Categories.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		Label Welcome = new Label("Welcome to our store!");
		Welcome.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 30));
		Categories.setAlignment(Pos.TOP_CENTER);

		Label label = new Label("Choose a category:");
		label.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 20));

		FlowPane categoryGrid = new FlowPane();
		categoryGrid.setHgap(40);
		categoryGrid.setVgap(40);
		categoryGrid.setPadding(new Insets(20));
		categoryGrid.setAlignment(Pos.CENTER);
		categoryGrid.setPrefWrapLength(500);

		Button shoesCategory = new Button("Shoes");
		shoesCategory.setFont(Font.font("Roboto", FontWeight.BOLD, FontPosture.ITALIC, 15));
		shoesCategory.setFont(Roboto);

		Button shirtsCategory = new Button("Shirts");
		shirtsCategory.setFont(Font.font("Roboto", FontWeight.BOLD, FontPosture.ITALIC, 15));
		shirtsCategory.setFont(Roboto);

		Button pantsCategory = new Button("Pants");
		pantsCategory.setFont(Font.font("Roboto", FontWeight.BOLD, FontPosture.ITALIC, 15));
		pantsCategory.setFont(Roboto);

		Button cartButton = new Button("Check your cart - 🛒");
		cartButton.setFont(Font.font("Century", FontWeight.BOLD, FontPosture.ITALIC, 15));

		ImageView shoeCat = new ImageView(new Image(IMG_PATH + "shoe1.png"));
		shoeCat.setFitWidth(120);
		shoeCat.setFitHeight(150);
		shoesCategory.setGraphic(shoeCat);
		shoesCategory.setBackground(Background.EMPTY);
		ImageView shirtCat = new ImageView(new Image(IMG_PATH + "shirt1.png"));
		shirtCat.setFitWidth(120);
		shirtCat.setFitHeight(150);
		shirtsCategory.setGraphic(shirtCat);
		shirtsCategory.setBackground(Background.EMPTY);
		ImageView pantsCat = new ImageView(new Image(IMG_PATH + "pants1.png"));
		pantsCat.setFitWidth(120);
		pantsCat.setFitHeight(150);
		pantsCategory.setGraphic(pantsCat);
		pantsCategory.setBackground(Background.EMPTY);

		applyBtnStyle(shoesCategory);
		applyBtnStyle(shirtsCategory);
		applyBtnStyle(pantsCategory);

		categoryGrid.getChildren().addAll(shoesCategory, shirtsCategory, pantsCategory);

		shoesCategory.setOnAction(e -> showProducts(primaryStage, "Shoes"));
		shirtsCategory.setOnAction(e -> showProducts(primaryStage, "Shirts"));
		pantsCategory.setOnAction(e -> showProducts(primaryStage, "Pants"));
		cartButton.setOnAction(e -> showCart(primaryStage));

		Categories.getChildren().addAll(Welcome, label, categoryGrid, cartButton);

		Scene scene = new Scene(Categories, 600, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Shopping site");
		primaryStage.show();

	}

	private void productsList() { // A list of all products organised by category
		if (!Products.isEmpty())
			return; // to make sure items don't get duplicated after pressing Return and going back
					// into the same products menu again

		ArrayList<Category> catList = new ArrayList<Category>();
		ArrayList<Product> prodList = new ArrayList<Product>();

		File catData = new File("C:/Users/moham/eclipse-workspace/onlineShop/src/application/Prod_Info/Categories.txt");
		try {
			Scanner catScan = new Scanner(catData);
			while (catScan.hasNextLine()) {
				String line = catScan.nextLine();
				String[] catInfo = line.split("/");
				if (catInfo.length == 2) {
					Category category = new Category(catInfo[0], catInfo[1]);
					catList.add(category);
				}
			}

		} catch (FileNotFoundException e) {
			System.err.print("File not found!");
		}

		File prodData = new File("C:/Users/moham/eclipse-workspace/onlineShop/src/application/Prod_Info/Products.txt");
		try {
			Scanner prodScan = new Scanner(prodData);
			while (prodScan.hasNextLine()) {
				String line = prodScan.nextLine();
				String[] prodInfo = line.split("/");
				if (prodInfo.length == 5) {
					String categoryName = prodInfo[0];
					String Brand = prodInfo[1];
					String Name = prodInfo[2];
					double Price = Double.parseDouble(prodInfo[3]);
					String imgFile = prodInfo[4];

					Category matchingCatName = null;
					for (Category c : catList) {
						if (c.getCatName().equalsIgnoreCase(categoryName)) {
							matchingCatName = c;
							break;
						}
					}
					if (matchingCatName != null) {
						Product product = new Product(matchingCatName, Brand, Name, Price, imgFile);
						prodList.add(product);
					} else {
						System.out.println("Category not found!");
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
		}
		Products = prodList;
	}

	private void showProducts(Stage primaryStage, String category) {

		VBox productListContainer = new VBox(20);
		productListContainer.setPadding(new Insets(20));
		productListContainer.setAlignment(Pos.TOP_CENTER);
		productListContainer.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		Label catTitle = new Label(category);
		catTitle.setFont(Font.font("", FontWeight.EXTRA_BOLD, 25));
		catTitle.setUnderline(true);

		FlowPane productList = new FlowPane();
		productList.setHgap(30);
		productList.setVgap(30);
		productList.setPadding(new Insets(20));
		productList.setAlignment(Pos.TOP_CENTER);

		productListContainer.getChildren().addAll(catTitle, productList);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(productListContainer);
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

		ArrayList<Product> chosenCategory = new ArrayList<Product>();

		for (Product product : Products) { // Check all the products within the same category as the user's choice and
											// at them to the new ArrayList
			if (product.getCategory().getCatName().equalsIgnoreCase(category)) {
				chosenCategory.add(product);
			}
		}

		for (Product product : chosenCategory) {

			VBox productCard = new VBox(10);
			productCard.setAlignment(Pos.CENTER);
			productCard.setPadding(new Insets(10));
			productCard.setStyle(
					"-fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f9f9f9;");
			productCard.setPrefWidth(250);

			Label prodName = new Label("Product Name: " + product.getName() + "\nPrice: $" + product.getPrice());
			prodName.setFont(Font.font("Century", FontWeight.BOLD, 15));

			ImageView img = new ImageView(new Image(IMG_PATH + product.getImgFileName()));
			img.setFitWidth(150);
			img.setFitHeight(150);

			ComboBox<String> sizeBox = new ComboBox<String>();
			sizeBox.setPromptText("Size");
			sizeBox.getItems().addAll("S", "M", "L", "XL");

			ComboBox<String> colorBox = new ComboBox<String>();
			colorBox.setPromptText("Color");
			colorBox.getItems().addAll("Black", "Red", "Blue", "Yellow");

			Text FAIL = new Text("");
			FAIL.setFill(Color.TRANSPARENT);
			FAIL.setFont(Font.font("Book Antiqua", 10));

			Button cartButton = new Button("Add to Cart");

			cartButton.setOnAction(e -> {
				String SelectedSize = sizeBox.getValue();
				String SelectedColor = colorBox.getValue();

				if (SelectedSize != null && SelectedColor != null) {
					Product newItem = new Product(product);
					newItem.setSelectedSize(SelectedSize);
					newItem.setSelectedColor(SelectedColor);
					Cart.add(newItem);
					System.out.println("Item added successfully");
					FAIL.setFill(Color.TRANSPARENT);
				} else if (SelectedSize != null && SelectedColor == null) {
					FAIL.setText("Please choose a color!");
					FAIL.setFill(Color.RED);
				} else if (SelectedSize == null && SelectedColor != null) {
					FAIL.setText("Please choose a size!");
					FAIL.setFill(Color.RED);
				} else {
					FAIL.setText("Please choose both a size and a color!");
					FAIL.setFill(Color.RED);
				}
			});

			productCard.getChildren().addAll(img, prodName, sizeBox, colorBox, cartButton, FAIL);
			productList.getChildren().add(productCard);

		}

		Scene productScene = new Scene(scrollPane, 600, 600);
		primaryStage.setScene(productScene);

		Button returnButton = new Button("Return");
		returnButton.setOnAction(e -> start(primaryStage));

		productListContainer.getChildren().add(returnButton);

		Button cartButton = new Button("Check your cart - 🛒");
		cartButton.setOnAction(e -> showCart(primaryStage));

		productListContainer.getChildren().add(cartButton);

	}

	private void showCart(Stage primaryStage) {
		if (Cart.isEmpty()) {
			Alert emptyCart = new Alert(Alert.AlertType.ERROR);
			emptyCart.setTitle("Cart's Empty");
			emptyCart.setHeaderText(null);
			emptyCart.setContentText("You haven't added anything to your cart yet!");
			emptyCart.showAndWait();
			return;
		}

		VBox cartItems = new VBox(10);
		cartItems.setAlignment(Pos.CENTER);
		cartItems.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(cartItems);
		scrollPane.setStyle(
				"-fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f9f9f9;");
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

		int Total = 0;

		ArrayList<Product> printedItems = new ArrayList<Product>();

		for (Product product : Cart) {
			if (printedItems.contains(product))
				continue;

			int count = 0;
			for (Product dupe : Cart) {
				if (product.equals(dupe)) {
					count++;
				}
			}
			printedItems.add(product);

			HBox cartItemsInfo = new HBox(10);
			Label itemName = new Label(count + "x " + product.getName() + "\n" + "Size: " + product.getSelectedSize()
					+ "\n" + "Color: " + product.getSelectedColor() + "\n" + "Price per item: $" + product.getPrice());

			itemName.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 15));

			ImageView img = new ImageView(new Image(IMG_PATH + product.getImgFileName()));
			img.setFitWidth(150);
			img.setFitHeight(150);

			VBox displayInfo = new VBox(10);

			Button removeBtn = new Button("Remove Item");
			if (Cart.size() > 1) {
				removeBtn.setOnAction(e -> {
					Cart.remove(product);
					showCart(primaryStage);
				});
			} else {
				removeBtn.setOnAction(e -> {
					Cart.remove(product);
					start(primaryStage);
					Alert emptyCart2 = new Alert(Alert.AlertType.WARNING);
					emptyCart2.setTitle("Cart's Empty");
					emptyCart2.setHeaderText(null);
					emptyCart2.setContentText("You have completely emptied your cart!");
					emptyCart2.showAndWait();
				});
			}
			displayInfo.getChildren().addAll(itemName, removeBtn);

			cartItemsInfo.getChildren().addAll(img, displayInfo);

			cartItems.getChildren().addAll(cartItemsInfo);

			Total += product.getPrice() * count;
		}

		Label cartTotal = new Label("Your cart's total is: $" + Total);
		cartTotal.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 20));

		cartItems.getChildren().add(cartTotal);

		Scene itemScene = new Scene(scrollPane, 600, 600);
		primaryStage.setScene(itemScene);

		Button returnBtn = new Button("Return");
		returnBtn.setOnAction(e -> showProducts(primaryStage, Cart.get(Cart.size() - 1).getCategory().getCatName()));

		Button clearBtn = new Button("Clear your cart");
		clearBtn.setOnAction(e -> {
			Cart.clear();
			start(primaryStage);
			Alert emptyCart2 = new Alert(Alert.AlertType.WARNING);
			emptyCart2.setTitle("Cart's Empty");
			emptyCart2.setHeaderText(null);
			emptyCart2.setContentText("You have completely emptied your cart!");
			emptyCart2.showAndWait();
		});

		cartItems.getChildren().addAll(returnBtn, clearBtn);
	}

	private void applyBtnStyle(Button button) {
		button.setStyle("-fx-background-color: #e0e0e0;" + "-fx-text-fill: #333333;" + "-fx-font-weight: bold;"
				+ "-fx-background-radius: 12;" + "-fx-border-radius: 12;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 4, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 4, 0, 2, 2);");

		button.setOnMouseEntered(e -> {
			button.setStyle("-fx-background-color: #d1d1d1;" + "-fx-text-fill: #333333;" + "-fx-font-weight: bold;"
					+ "-fx-background-radius: 12;" + "-fx-border-radius: 12;" + "-fx-padding: 10 20;"
					+ "-fx-cursor: hand;" + "-fx-effect: dropshadow(gaussian, #b0b0b0, 6, 0, 2, 2),"
					+ "            dropshadow(gaussian, #ffffff, 6, 0, -2, -2);");
			button.setScaleX(1.05);
			button.setScaleY(1.05);
		});

		button.setOnMouseExited(e -> {
			button.setStyle("-fx-background-color: #e0e0e0;" + "-fx-text-fill: #333333;" + "-fx-font-weight: bold;"
					+ "-fx-background-radius: 12;" + "-fx-border-radius: 12;" + "-fx-padding: 10 20;"
					+ "-fx-cursor: hand;" + "-fx-effect: dropshadow(gaussian, #ffffff, 4, 0, -2, -2),"
					+ "            dropshadow(gaussian, #c0c0c0, 4, 0, 2, 2);");
			button.setScaleX(1.0);
			button.setScaleY(1.0);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
