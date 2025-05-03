package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
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
		Categories.setAlignment(Pos.TOP_CENTER);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(Categories);
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

		Label Welcome = new Label("Welcome to our store!");
		Welcome.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 30));

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
		applyBtnStyle(cartButton);
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

		Scene scene = new Scene(scrollPane, 800, 800);
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
		Products = prodList; // add all the products from the read file into the Products ArrayList
	}

	private void showProducts(Stage primaryStage, String category) {

		VBox productListContainer = new VBox(20);
		productListContainer.setPadding(new Insets(20));
		productListContainer.setAlignment(Pos.TOP_CENTER);
		productListContainer.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		Button returnButton = new Button("Return");
		applyBtnStyle(returnButton);
		returnButton.setOnAction(e -> start(primaryStage));

		Button cartButton = new Button("Check your cart - 🛒");
		applyBtnStyle(cartButton);
		cartButton.setOnAction(e -> showCart(primaryStage));

		BorderPane btnLayout = new BorderPane();
		btnLayout.setPadding(new Insets(10, 20, 10, 20));
		btnLayout.setLeft(returnButton);
		btnLayout.setRight(cartButton);

		Label catTitle = new Label(category);
		catTitle.setFont(Font.font("", FontWeight.EXTRA_BOLD, 25));
		catTitle.setUnderline(true);
		btnLayout.setCenter(catTitle);

		productListContainer.getChildren().add(btnLayout);

		FlowPane productList = new FlowPane();
		productList.setHgap(30);
		productList.setVgap(30);
		productList.setPadding(new Insets(20));
		productList.setAlignment(Pos.TOP_CENTER);

		productListContainer.getChildren().addAll(productList);

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
			if (!(category.equalsIgnoreCase("shoes"))) {
				sizeBox.getItems().addAll("S", "M", "L", "XL");
			} else {
				sizeBox.getItems().addAll("34", "36", "38", "40");
			}

			ComboBox<String> colorBox = new ComboBox<String>();
			colorBox.setPromptText("Color");
			colorBox.getItems().addAll("Black", "Red", "Blue", "Yellow");

			Text FAIL = new Text("");
			FAIL.setFill(Color.TRANSPARENT);
			FAIL.setFont(Font.font("Book Antiqua", 10));

			HBox cartStuff = new HBox();
			cartStuff.setAlignment(Pos.CENTER);

			TextField amount = new TextField("");
			amount.setPrefWidth(40);
			amount.setAlignment(Pos.CENTER);
			amount.setPromptText("Qty");

			Button cartBtn = new Button("Add to Cart");

			VBox IncDecBtns = new VBox();
			Button incBtn = new Button("+");
			Button decBtn = new Button("-");
			incBtn.setPrefSize(30, 20);
			decBtn.setPrefSize(30, 20);

			incBtn.setOnAction(e -> {
				int num;
				if (!(amount.getText().trim().isEmpty())) {
					try {
						num = Integer.parseInt(amount.getText());
						num++;
						amount.setText(Integer.toString(num));
					} catch (NumberFormatException ex) {
						Alert wrongInput = new Alert(Alert.AlertType.ERROR);
						wrongInput.setTitle("Input Error");
						wrongInput.setHeaderText(null);
						wrongInput.setContentText("Invalid input! Please enter numbers only.");
						wrongInput.showAndWait();
					}
				} else {
					try {
						num = 0;
						num++;
						amount.setText(Integer.toString(num));
					} catch (NumberFormatException ex) {
						Alert wrongInput = new Alert(Alert.AlertType.ERROR);
						wrongInput.setTitle("Input Error");
						wrongInput.setHeaderText(null);
						wrongInput.setContentText("Invalid input! Please enter numbers only.");
						wrongInput.showAndWait();
					}
				}

			});

			decBtn.setOnAction(e -> {
				int num = 0;
				String text = amount.getText().trim();

				if (!text.isEmpty()) {
					try {
						num = Integer.parseInt(text);
						if (num > 1) {
							num--;
							amount.setText(Integer.toString(num));
						}
					} catch (NumberFormatException ex) {
						Alert wrongInput = new Alert(Alert.AlertType.ERROR);
						wrongInput.setTitle("Input Error");
						wrongInput.setHeaderText(null);
						wrongInput.setContentText("Invalid input! Please enter numbers only.");
						wrongInput.showAndWait();
					}
				}
			});

			IncDecBtns.getChildren().addAll(incBtn, decBtn);

			cartStuff.getChildren().addAll(cartBtn, amount, IncDecBtns);

			amount.setOnAction(e -> {

				int count = 0;

				if (!(amount.getText().trim().isEmpty())) {
					try {
						count = Integer.parseInt(amount.getText());
						if (count <= 0) {
							Alert zeroInput = new Alert(Alert.AlertType.WARNING);
							zeroInput.setTitle("Invalid Quantity");
							zeroInput.setHeaderText(null);
							zeroInput.setContentText("Please enter a quantity greater than 0.");
							zeroInput.showAndWait();
							return;
						}
					} catch (NumberFormatException ex) {
						Alert wrongInput = new Alert(Alert.AlertType.ERROR);
						wrongInput.setTitle("Input Error");
						wrongInput.setHeaderText(null);
						wrongInput.setContentText("Invalid input! Please enter numbers only.");
						wrongInput.showAndWait();
					}

					String SelectedSize = sizeBox.getValue();
					String SelectedColor = colorBox.getValue();
					for (int i = 0; i < count; i++) {
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
					}
				}
				amount.clear();
			});

			cartBtn.setOnAction(e -> {
				int count = 0;

				if (!(amount.getText().trim().isEmpty())) {
					try {
						count = Integer.parseInt(amount.getText());
						if (count <= 0) {
							Alert zeroInput = new Alert(Alert.AlertType.WARNING);
							zeroInput.setTitle("Invalid Quantity");
							zeroInput.setHeaderText(null);
							zeroInput.setContentText("Please enter a quantity greater than 0.");
							zeroInput.showAndWait();
							return;
						}
					} catch (NumberFormatException ex) {
						Alert wrongInput = new Alert(Alert.AlertType.ERROR);
						wrongInput.setTitle("Input Error");
						wrongInput.setHeaderText(null);
						wrongInput.setContentText("Invalid input! Please enter numbers only.");
						wrongInput.showAndWait();
					}

					String SelectedSize = sizeBox.getValue();
					String SelectedColor = colorBox.getValue();
					for (int i = 0; i < count; i++) {
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
					}
				} else {
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
				}
				amount.clear();
			});

			productCard.getChildren().addAll(img, prodName, sizeBox, colorBox, cartStuff, FAIL);
			productList.getChildren().add(productCard);

		}

		Scene productScene = new Scene(scrollPane, 800, 800);
		primaryStage.setScene(productScene);

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

		FlowPane cartItemsFlow = new FlowPane();
		cartItemsFlow.setHgap(30);
		cartItemsFlow.setVgap(30);
		cartItemsFlow.setPadding(new Insets(20));
		cartItemsFlow.setAlignment(Pos.TOP_CENTER);
		cartItemsFlow.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		VBox cartContainer = new VBox(10);
		cartContainer.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");
		cartContainer.getChildren().add(cartItemsFlow);
		cartContainer.setPrefHeight(600);
		cartContainer.setAlignment(Pos.TOP_CENTER);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(cartContainer);
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

			Label itemName = new Label();
			if (count > 1) {
				itemName.setText(count + "x " + product.getName() + "\n" + "Size: " + product.getSelectedSize() + "\n"
						+ "Color: " + product.getSelectedColor() + "\n" + "Price per item: $" + product.getPrice());
			} else {
				itemName.setText(product.getName() + "\n" + "Size: " + product.getSelectedSize() + "\n" + "Color: "
						+ product.getSelectedColor() + "\n" + "Price per item: $" + product.getPrice());
			}
			itemName.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 15));

			ImageView img = new ImageView(new Image(IMG_PATH + product.getImgFileName()));
			img.setFitWidth(150);
			img.setFitHeight(150);

			VBox displayInfo = new VBox(10);

			Button removeBtn = new Button("Remove Item - \uD83D\uDDD1");
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

			cartItemsFlow.getChildren().addAll(cartItemsInfo);

			Total += product.getPrice() * count;
		}

		Label cartTotal = new Label("Your cart's total is: $" + Total);
		cartTotal.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 20));

		cartContainer.getChildren().add(cartTotal);

		Scene itemScene = new Scene(scrollPane, 800, 800);
		primaryStage.setScene(itemScene);

		Button returnBtn = new Button("Return");
		returnBtn.setOnAction(e -> showProducts(primaryStage, Cart.get(Cart.size() - 1).getCategory().getCatName()));

		Button clearBtn = new Button("Clear your cart - \uD83D\uDDD1");
		clearBtn.setOnAction(e -> {
			Cart.clear();
			start(primaryStage);
			Alert emptyCart2 = new Alert(Alert.AlertType.WARNING);
			emptyCart2.setTitle("Cart's Empty");
			emptyCart2.setHeaderText(null);
			emptyCart2.setContentText("You have completely emptied your cart!");
			emptyCart2.showAndWait();
		});

		Button checkoutBtn = new Button("Check out! - 🛒✅");
		checkoutBtn.setOnAction(e -> checkOut(primaryStage));

		BorderPane btnLayout = new BorderPane();
		btnLayout.setPadding(new Insets(10, 20, 10, 20));
		btnLayout.setLeft(returnBtn);
		applyBtnStyle(returnBtn);
		btnLayout.setRight(clearBtn);
		applyBtnStyle(clearBtn);
		btnLayout.setCenter(checkoutBtn);
		applyBtnStyle(checkoutBtn);
		cartContainer.getChildren().add(btnLayout);

	}

	private void checkOut(Stage primaryStage) {

		VBox COLayoutFULL = new VBox(10);
		COLayoutFULL.setPadding(new Insets(20));
		COLayoutFULL.setAlignment(Pos.TOP_CENTER);
		COLayoutFULL.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		BorderPane COLayoutTitle = new BorderPane();
		COLayoutTitle.setPadding(new Insets(10, 20, 10, 20));

		Button returnBtn = new Button("Return");
		returnBtn.setOnAction(e -> showCart(primaryStage));
		applyBtnStyle(returnBtn);

		Label COTitle = new Label("Check Out              ");
		COTitle.setFont(Font.font("", FontWeight.EXTRA_BOLD, 25));

		COLayoutTitle.setRight(COTitle);
		COLayoutTitle.setLeft(returnBtn);

		HBox COLayout = new HBox(30);
		COLayout.setPadding(new Insets(20));
		COLayout.setAlignment(Pos.TOP_CENTER);

		VBox itemsRecap = new VBox(15);
		itemsRecap.setPadding(new Insets(10));
		itemsRecap.setAlignment(Pos.TOP_CENTER);
		itemsRecap.setBackground(Background.EMPTY);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(itemsRecap);
		scrollPane.setStyle(
				"-fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f9f9f9;");
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setBackground(Background.EMPTY);

		ArrayList<Product> printedItems = new ArrayList<Product>();

		int orderNumber = (int) (Math.random() * 100000000);

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

			ImageView img = new ImageView(IMG_PATH + product.getImgFileName());
			img.setFitWidth(150);
			img.setFitHeight(150);

			Label itemInfo = new Label();
			if (count > 1) {
				itemInfo.setText(count + "x " + product.getName() + "\n" + "$" + product.getPrice());
			} else {
				itemInfo.setText(product.getName() + "\n" + "$" + product.getPrice());
			}
			itemInfo.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 15));

			Label orderNum = new Label("Order Number\n00" + (orderNumber * 100000000));
			Label orderSize = new Label("Size\n" + product.getSelectedSize());
			Label orderColor = new Label("Color\n" + product.getSelectedColor());
			Label orderPrice = new Label("Total\n$" + (count * product.getPrice()));

			HBox orderInfo = new HBox(10);
			orderInfo.getChildren().addAll(orderNum, orderSize, orderColor, orderPrice);

			VBox itemCard = new VBox(10);
			itemCard.getChildren().addAll(itemInfo, orderInfo);

			HBox itemCardFULL = new HBox(10);
			itemCardFULL.setAlignment(Pos.CENTER);
			itemCardFULL.setBackground(Background.EMPTY);
			itemCardFULL.getChildren().addAll(img, itemCard);

			itemsRecap.getChildren().addAll(itemCardFULL);
		}

		VBox userInfo = new VBox(15);
		userInfo.setPadding(new Insets(10));

		HBox paymentMethod = new HBox(10);

		TextField creditNum = new TextField("");
		creditNum.setPromptText("Credit Card Number");
		TextField creditDate = new TextField("");
		creditDate.setPromptText("MM/YY");
		TextField creditCVC = new TextField("");
		creditCVC.setPromptText("CVC");

		Button creditCard = new Button("Credit Card");
		applyBtnStyle(creditCard);

		creditCard.setOnAction(e -> {
			userInfo.getChildren().addAll(creditNum, creditDate, creditCVC);
		});

		VBox payMethodInfo = new VBox(10);
		payMethodInfo.getChildren().addAll(paymentMethod, userInfo);

		Button payPal = new Button("PayPal");
		applyBtnStyle(payPal);

		payPal.setOnAction(e -> {
			Stage payPalStage = new Stage();
			userInfo.getChildren().clear();

			FlowPane PayPalPane = new FlowPane();
			PayPalPane.setAlignment(Pos.CENTER);
			ImageView PayPalPic = new ImageView(IMG_PATH + "PayPal2.png");
			PayPalPic.setFitWidth(150);
			PayPalPic.setFitHeight(150);

			VBox payPalLogin = new VBox(10);

			TextField payPalEmail = new TextField();
			payPalEmail.setPromptText("CoolPerson@gmail.com");
			payPalEmail.setPrefWidth(100);

			PasswordField payPalPass = new PasswordField();
			payPalPass.setPromptText("Password");

			Button Confirm = new Button("Confirm");

			Confirm.setOnAction(b -> {
				payPalCheck(payPalStage, payPalEmail, payPalPass);
			});

			payPalLogin.getChildren().addAll(payPalEmail, payPalPass, Confirm);

			PayPalPane.getChildren().addAll(PayPalPic, payPalLogin);
			PayPalPane.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
					+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
					+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

			Scene paypalScene = new Scene(PayPalPane, 350, 300);
			payPalStage.setScene(paypalScene);
			payPalStage.setTitle("PayPal login");
			payPalStage.show();
		});

		paymentMethod.getChildren().addAll(creditCard, payPal);

		COLayout.getChildren().addAll(scrollPane, payMethodInfo);

		COLayoutFULL.getChildren().addAll(COLayoutTitle, COLayout);

		Scene COscene = new Scene(COLayoutFULL, 800, 800);
		primaryStage.setScene(COscene);

	}

	private void payPalCheck(Stage payPalStage, TextField emailField, PasswordField passField) {
		String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

		if (Pattern.matches(emailRegex, emailField.getText())) {
			StackPane loadingPane = new StackPane(new ProgressIndicator());
			StackPane root = new StackPane(loadingPane);

			Scene scene = new Scene(root, 350, 350);
			payPalStage.setScene(scene);
			payPalStage.setTitle("Checking...");
			payPalStage.show();

			PauseTransition pause = new PauseTransition(Duration.seconds(3));
			pause.setOnFinished(p -> {
				root.getChildren().setAll(new Label("Success!"));
			});
			pause.play();
		} else {
			// Just show an alert, clear the fields, and let user retry
			Alert failAlert = new Alert(Alert.AlertType.ERROR);
			failAlert.setTitle("Login Failed");
			failAlert.setHeaderText(null);
			failAlert.setContentText("Invalid email format. Please try again.");
			failAlert.showAndWait();

			emailField.clear();
			passField.clear();
			emailField.requestFocus(); // Bring cursor back to email
		}
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
