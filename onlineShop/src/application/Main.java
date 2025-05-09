package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Main extends Application {

	ArrayList<Product> Products = new ArrayList<Product>();
	ArrayList<Product> Cart = new ArrayList<Product>();
	ArrayList<Category> catList = new ArrayList<Category>();
	ArrayList<Product> prodList = new ArrayList<Product>();
	int Total = 0;
	private static final String IMG_PATH = "file:///C:/Users/moham/eclipse-workspace/onlineShop/src/application/Images/";

	private Stage mainStage;


	@Override
	public void start(Stage primaryStage) {
		productsList();

		this.mainStage = primaryStage;

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

		Button cartButton = new Button("Check your cart - 🛒");
		applyBtnStyle(cartButton);
		cartButton.setFont(Font.font("Century", FontWeight.BOLD, FontPosture.ITALIC, 15));

		for (Category category : catList) {
			ImageView catimg = new ImageView(IMG_PATH + category.getCatImg());
			catimg.setFitHeight(150);
			catimg.setFitWidth(120);
			Button catBtn = new Button(category.getCatName());
			catBtn.setGraphic(catimg);
			applyBtnStyle(catBtn);
			categoryGrid.getChildren().add(catBtn);

			catBtn.setOnAction(e -> showProducts(primaryStage, category.getCatName()));
		}
		cartButton.setOnAction(e -> showCart(primaryStage));

		Button login = new Button("Log In");
		login.setOnAction(e -> ProfileCreation(primaryStage));

		Categories.getChildren().addAll(Welcome, label, categoryGrid, cartButton, login);

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		Scene scene = new Scene(scrollPane, screenBounds.getWidth(), screenBounds.getHeight());
		primaryStage.setScene(scene);
		primaryStage.setFullScreenExitHint(""); // disables the "Press ESC to exit fullscreen" message
		primaryStage.setTitle("Shopping site");
		primaryStage.setFullScreen(true);
		primaryStage.show();

	}

	private void productsList() { // A list of all products organised by category
		if (!Products.isEmpty())
			return; // to make sure items don't get duplicated after pressing Return and going back
					// into the same products menu again

		File catData = new File("C:/Users/moham/eclipse-workspace/onlineShop/src/application/Prod_Info/Categories.txt");
		try (Scanner catScan = new Scanner(catData)) {
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
		try (Scanner prodScan = new Scanner(prodData)) {
			while (prodScan.hasNextLine()) {
				String line = prodScan.nextLine();
				String[] prodInfo = line.split("/");
				if (prodInfo.length == 7) {
					String categoryName = prodInfo[0];
					String Brand = prodInfo[1];
					String Name = prodInfo[2];
					double Price = Double.parseDouble(prodInfo[3]);
					String imgFile = prodInfo[4];
					String Desc = prodInfo[5];
					int Stars = Integer.parseInt(prodInfo[6]);

					Category matchingCatName = null;
					for (Category c : catList) {
						if (c.getCatName().equalsIgnoreCase(categoryName)) {
							matchingCatName = c;
							break;
						}
					}
					if (matchingCatName != null) {
						Product product = new Product(matchingCatName, Brand, Name, Price, imgFile, Desc, Stars);
						prodList.add(product);
					} else {
						System.out.println("Category not found!");
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		Products = prodList; // add all the products from the read file into the Products ArrayList
	}

	private void ProfileCreation(Stage primaryStage) {
		ArrayList<User> users = new ArrayList<User>();

		File file = new File("C:/Users/moham/eclipse-workspace/onlineShop/src/application/Prod_Info/USERS.txt");
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] array = line.split(" ");
				String name = array[0];
				String password = array[1];
				User u = new User(name, password);
				users.add(u);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// pane of log in and register
		GridPane pane1 = new GridPane();
		pane1.setHgap(5);
		pane1.setVgap(5);
		pane1.setPadding(new Insets(10, 10, 10, 10));
		pane1.setAlignment(Pos.CENTER);
		pane1.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #cceeff)");

//		Image backgroundImage = new Image(getClass().getResource("/background_image.png").toExternalForm());
//
//		// إعداد خلفية الصورة
//		BackgroundImage background = new BackgroundImage(
//		    backgroundImage,
//		    BackgroundRepeat.NO_REPEAT,   
//		    BackgroundRepeat.NO_REPEAT,
//		    BackgroundPosition.CENTER,     
//		    BackgroundSize.DEFAULT         
//		);
//		pane1.setBackground(new Background(background));
//		

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		Scene scene = new Scene(pane1, screenBounds.getWidth(), screenBounds.getHeight());
		// UserName Label
		Label userName = new Label("Username");
		userName.setAlignment(Pos.CENTER);
		GridPane.setHalignment(userName, HPos.CENTER);
		userName.setMinWidth(300);
		userName.setMinHeight(50);
		userName.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 20), 30), "px; "

		));
		// PassWord label
		Label passWord = new Label("Password");
		passWord.setAlignment(Pos.CENTER);
		GridPane.setHalignment(passWord, HPos.CENTER);
		passWord.setMinWidth(300);
		passWord.setMinHeight(50);
		passWord.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 20), 30), "px; "

		));
		// Register button
		Button register = new Button("Don't have an account?\n\t   Register");
		register.setAlignment(Pos.CENTER);
		register.setBackground(Background.EMPTY);
		GridPane.setHalignment(register, HPos.CENTER);
		register.styleProperty()
				.bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
						"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 10), 30),
						"px; ", "-fx-text-fill: blue;"));

		// Log in button
		Button login = new Button("Log in");
		login.setAlignment(Pos.CENTER);
		GridPane.setHalignment(login, HPos.CENTER);
		login.styleProperty()
				.bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
						"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 10), 30),
						"px; "));

		// UserName Text Field
		TextField UNfield = new TextField();
		UNfield.setPromptText("Enter your username");
		UNfield.setAlignment(Pos.CENTER);
		GridPane.setHalignment(UNfield, HPos.CENTER);
		UNfield.styleProperty()
				.bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
						"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 20), 30),
						"px; "));

		// PassWord Field
		PasswordField PWfield = new PasswordField();
		PWfield.setPromptText("Enter your password");
		PWfield.setAlignment(Pos.CENTER);
		GridPane.setHalignment(PWfield, HPos.CENTER);
		PWfield.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 20), 30), "px; "

		));

		// Log in button action
		login.setOnAction(e -> {
			if (UNfield.getText().trim().isEmpty()) {
				Alert UNnull = new Alert(AlertType.WARNING);
				UNnull.initOwner(primaryStage);
				UNnull.setTitle("Username empty");
				UNnull.setHeaderText(null);
				UNnull.setContentText("Username cannot be empty.");
				UNnull.showAndWait();
				return;
			} else if (PWfield.getText().trim().isEmpty()) {
				Alert PWnull = new Alert(AlertType.WARNING);
				PWnull.initOwner(primaryStage);
				PWnull.setTitle("Password empty");
				PWnull.setHeaderText(null);
				PWnull.setContentText("Password cannot be empty.");
				PWnull.showAndWait();
				return;
			}

			else {
				boolean flage = false;
				for (User user : users) {
					if (user.getName().equals(UNfield.getText()) && user.getPassword().equals(PWfield.getText())) {
						flage = true;
						break;
					}
				}
				if (!flage) {
					Alert loginError = new Alert(AlertType.WARNING);
					loginError.initOwner(primaryStage);
					loginError.setTitle("Login Error");
					loginError.setHeaderText(null);
					loginError.setContentText("Incorrect username or password.");
					loginError.showAndWait();
					return;
				}else {
					start(primaryStage);
				}

			}

		});

		/*
		 * Add to pane1 UserName Label PassWord label Register button UserName Text
		 * Field PassWord Field
		 */

		pane1.add(userName, 1, 1);
		pane1.add(UNfield, 2, 1);
		pane1.add(passWord, 1, 3);
		pane1.add(PWfield, 2, 3);
		pane1.add(login, 2, 5);
		pane1.add(register, 2, 6);

		// Name label
		Label Name = new Label("Username");
		Name.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(Name, HPos.CENTER);
		Name.setMinWidth(300);
		Name.setMinHeight(50);
		Name.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// PassWord label
		Label PassWord = new Label("Password");
		PassWord.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(PassWord, HPos.CENTER);
		PassWord.setMinWidth(300);
		PassWord.setMinHeight(50);
		PassWord.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// Email label
		Label email = new Label("Email");
		email.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(email, HPos.CENTER);
		email.setMinWidth(300);
		email.setMinHeight(50);
		email.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// PhoneNumber label
		Label phoneNumber = new Label("Phone Number");
		phoneNumber.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(phoneNumber, HPos.CENTER);
		phoneNumber.setMinWidth(300);
		phoneNumber.setMinHeight(50);
		phoneNumber.styleProperty()
				.bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
						"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20),
						"px; "

				));

		// Name Text Field
		TextField Nfield = new TextField();
		Nfield.setPromptText("Enter your username");
		Nfield.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(Nfield, HPos.CENTER);
		Nfield.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// PassWord Field
		PasswordField pwField = new PasswordField();
		pwField.setPromptText("Enter your password");
		pwField.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(pwField, HPos.CENTER);
		pwField.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// Password confirmation field
		PasswordField pwCField = new PasswordField();
		pwCField.setPromptText("Confirm your password");
		pwCField.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(pwCField, HPos.CENTER);
		pwCField.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// Email Text Field
		TextField Efield = new TextField();
		Efield.setPromptText("Enter your email");
		Efield.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(Efield, HPos.CENTER);
		Efield.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// Phone Text Field
		TextField PHfield = new TextField();
		PHfield.setPromptText("Enter your phone number");
		PHfield.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(PHfield, HPos.CENTER);
		PHfield.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// Confirm button
		Button confirm = new Button("Confirm");
		confirm.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(confirm, HPos.CENTER);
		confirm.styleProperty().bind(Bindings.concat("-fx-font-family: \"Book Antiqua\"; ", "-fx-font-style: italic; ",
				"-fx-font-size: ", Bindings.min(Bindings.max(primaryStage.widthProperty().divide(40), 15), 20), "px; "

		));

		// Logo Image
		ImageView logo = new ImageView(IMG_PATH + "Logo.png");
		logo.setPreserveRatio(false);
		logo.setSmooth(true);
		logo.setCache(true);

		StackPane pane3 = new StackPane(logo);

		// pane2
		GridPane pane2 = new GridPane();
		pane2.setHgap(10);
		pane2.setVgap(10);
		pane2.setPadding(new Insets(20, 20, 20, 20));
		pane2.setAlignment(Pos.CENTER_LEFT);
		pane2.setStyle("-fx-background-color:  #ddceb8");// #ddceb8
		pane2.add(Name, 3, 1);
		pane2.add(Nfield, 4, 1);
		pane2.add(PassWord, 3, 2);
		pane2.add(pwField, 4, 2);
		pane2.add(pwCField, 4, 3);
		pane2.add(email, 3, 4);
		pane2.add(Efield, 4, 4);
		pane2.add(phoneNumber, 3, 5);
		pane2.add(PHfield, 4, 5);
		pane2.add(confirm, 4, 6);

		HBox box1 = new HBox(pane3, pane2);
		HBox.setHgrow(logo, Priority.ALWAYS);
		HBox.setHgrow(pane2, Priority.ALWAYS);

		logo.fitWidthProperty().bind(box1.widthProperty().divide(2));
		logo.fitHeightProperty().bind(box1.heightProperty());

		// Register button action

		register.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				scene.setRoot(box1);

			}

		});

		// Confirm button action
		confirm.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (Nfield.getText().trim().isEmpty()) {
					Alert UNnull = new Alert(AlertType.WARNING);
					UNnull.initOwner(primaryStage);
					UNnull.setTitle("Username empty");
					UNnull.setHeaderText(null);
					UNnull.setContentText("Username cannot be empty.");
					UNnull.showAndWait();
					return;
				}

				if (pwField.getText().trim().isEmpty()) {
					Alert PWnull = new Alert(AlertType.WARNING);
					PWnull.initOwner(primaryStage);
					PWnull.setTitle("Password empty");
					PWnull.setHeaderText(null);
					PWnull.setContentText("Password cannot be empty.");
					PWnull.showAndWait();
					return;
				}
				if (pwCField.getText().trim().isEmpty()) {
					Alert PWnull = new Alert(AlertType.WARNING);
					PWnull.initOwner(primaryStage);
					PWnull.setTitle("Confirm Password empty");
					PWnull.setHeaderText(null);
					PWnull.setContentText("Please confirm your password");
					PWnull.showAndWait();
					return;
				}
				if (Efield.getText().trim().isEmpty()) {
					Alert emailNull = new Alert(AlertType.WARNING);
					emailNull.initOwner(primaryStage);
					emailNull.setTitle("Email empty");
					emailNull.setHeaderText(null);
					emailNull.setContentText("Email cannot be empty.");
					emailNull.showAndWait();
					return;
				}
				if (PHfield.getText().trim().isEmpty()) {
					Alert PHnull = new Alert(AlertType.WARNING);
					PHnull.initOwner(primaryStage);
					PHnull.setTitle("Phone Number empty");
					PHnull.setHeaderText(null);
					PHnull.setContentText("Phone Number cannot be empty.");
					PHnull.showAndWait();
					return;
				}
				if (!(pwField.getText().trim().equals(pwCField.getText().trim()))) {
					Alert equalPassWord = new Alert(AlertType.WARNING);
					equalPassWord.initOwner(primaryStage);
					equalPassWord.setTitle("Mismatch passwords");
					equalPassWord.setHeaderText(null);
					equalPassWord.setContentText("Passwords do not match. Please try again.");
					equalPassWord.showAndWait();
					return;
				}

				for (User user : users) {
					if (user.getName().equals(Nfield.getText()) && user.getPassword().equals(pwField.getText())) {

						Alert accountExist = new Alert(AlertType.WARNING);
						accountExist.initOwner(primaryStage);
						accountExist.setTitle("Account Exists");
						accountExist.setHeaderText(null);
						accountExist.setContentText("This account already exists.");
						accountExist.showAndWait();
						return;
					}
				}

				users.add(new User(Nfield.getText(), pwField.getText()));
				Alert accountCreated = new Alert(AlertType.INFORMATION);
				accountCreated.initOwner(primaryStage);
				accountCreated.setTitle("Account Created");
				accountCreated.setHeaderText(null);
				accountCreated.setContentText("Account created successfully!");
				accountCreated.showAndWait();

				try {
					PrintWriter writer = new PrintWriter(
							"C:/Users/moham/eclipse-workspace/onlineShop/src/application/Prod_Info/USERS.txt");
					for (int i = 0; i < users.size(); i++) {

						writer.println(users.get(i).toString());
					}
					writer.close();
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
			}

		});

		primaryStage.setScene(scene);
		primaryStage.setFullScreenExitHint("");
		primaryStage.setFullScreen(true);
		primaryStage.show();
	}

	private void showProducts(Stage primaryStage, String category) {

		VBox productListContainer = new VBox(20);
		productListContainer.setPadding(new Insets(20));
		productListContainer.setAlignment(Pos.TOP_CENTER);
		productListContainer.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		Button returnButton = new Button("<- Back");
		applyBtnStyle(returnButton);
		returnButton.setOnAction(e -> start(primaryStage));

		Button cartButton = new Button("Check your cart - 🛒");
		applyBtnStyle(cartButton);
		cartButton.setOnAction(e -> showCart(primaryStage));

		BorderPane topLayout = new BorderPane();
		topLayout.setPadding(new Insets(10, 20, 10, 20));
		topLayout.setLeft(returnButton);
		topLayout.setRight(cartButton);

		Label catTitle = new Label(category);
		catTitle.setFont(Font.font("Trebuchet MS", FontWeight.EXTRA_BOLD, 25));
		catTitle.setUnderline(true);
		topLayout.setCenter(catTitle);

		productListContainer.getChildren().add(topLayout);

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

			ImageView img = new ImageView(IMG_PATH + product.getImgFileName());
			img.setFitWidth(150);
			img.setFitHeight(150);
			img.setOnMouseEntered(e -> {
				img.setScaleX(1.05);
				img.setScaleY(1.05);
			});
			img.setOnMouseExited(e -> {
				img.setScaleX(1.0);
				img.setScaleY(1.0);
			});
			img.setOnMouseClicked(e -> showProductDetails(primaryStage, product));

			ComboBox<String> sizeBox = new ComboBox<String>();
			sizeBox.setPromptText("Size");
			if (!(category.equalsIgnoreCase("shoes"))) {
				sizeBox.getItems().addAll("S", "M", "L", "XL");
			} else {
				sizeBox.getItems().addAll("34", "36", "38", "40");
			}

			ComboBox<String> colorBox = new ComboBox<String>();
			colorBox.setPromptText("Color");
			colorBox.getItems().addAll("Black", "Red", "Blue", "Green");

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
				if (amount.getText().trim().isEmpty())
					return;

				try {
					int count = Integer.parseInt(amount.getText());
					if (count <= 0) {
						Alert zeroInput = new Alert(Alert.AlertType.WARNING);
						zeroInput.setTitle("Invalid Quantity");
						zeroInput.setHeaderText(null);
						zeroInput.setContentText("Please enter a quantity greater than 0.");
						zeroInput.showAndWait();
						return;
					}

					tryAddToCart(product, sizeBox.getValue(), colorBox.getValue(), count, FAIL);
				} catch (NumberFormatException ex) {
					Alert wrongInput = new Alert(Alert.AlertType.ERROR);
					wrongInput.setTitle("Input Error");
					wrongInput.setHeaderText(null);
					wrongInput.setContentText("Invalid input! Please enter numbers only.");
					wrongInput.showAndWait();
				}

				amount.clear();
			});

			cartBtn.setOnAction(e -> {
				int count = 1;

				if (!amount.getText().trim().isEmpty()) {
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
						return;
					}
				}

				tryAddToCart(product, sizeBox.getValue(), colorBox.getValue(), count, FAIL);
				amount.clear();
			});

			productCard.getChildren().addAll(img, prodName, sizeBox, colorBox, cartStuff, FAIL);
			productList.getChildren().add(productCard);

		}

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		Scene productScene = new Scene(scrollPane, screenBounds.getWidth(), screenBounds.getHeight());
		primaryStage.setScene(productScene);
		primaryStage.setFullScreenExitHint("");
		primaryStage.setFullScreen(true);

	}

	private void showProductDetails(Stage primaryStage, Product product) {

		VBox detailLayout = new VBox(30);
		detailLayout.setPadding(new Insets(20));
		detailLayout.setAlignment(Pos.TOP_CENTER);
		detailLayout.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		BorderPane topLayout = new BorderPane();
		topLayout.setPadding(new Insets(10, 20, 10, 20));

		Button returnBtn = new Button("<- Back");
		returnBtn.setOnAction(e -> showProducts(primaryStage, product.getCategory().getCatName()));
		applyBtnStyle(returnBtn);

		Label itemTitle = new Label(product.getName());
		itemTitle.setFont(Font.font("Trebuchet MS", FontWeight.EXTRA_BOLD, 25));
		itemTitle.setUnderline(true);

		topLayout.setCenter(itemTitle);
		topLayout.setLeft(returnBtn);

		HBox itemLayout = new HBox(10);

		ImageView itemImg = new ImageView(IMG_PATH + product.getImgFileName());

		itemImg.setFitWidth(250);
		itemImg.setFitHeight(250);

		VBox DescRev = new VBox(20);

		Text Description = new Text("Description:\n" + product.getDescription());
		Description.setWrappingWidth(400);
		Description.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 15));
		Text Stars = new Text("\t ");
		for (int i = 0; i < product.getStars(); i++) {
			Stars.setText(Stars.getText() + "★");
		}
		for (int i = 0; i < 5 - product.getStars(); i++) {
			Stars.setText(Stars.getText() + "☆");
		}
		Stars.setText(Stars.getText() + "  (1000+ reviews)\n\n\n");
		Stars.setFill(Color.GOLDENROD);
		Stars.setScaleX(1.5);
		Stars.setScaleY(1.5);

		// this HBox will hold all the color options horizontally
		HBox COLORS = new HBox(30); // spacing between each color circle is 30px

		Label colorLabel = new Label("Colors:");
		colorLabel.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, FontPosture.ITALIC, 15));

		// these are the actual names we’ll use when assigning the selected color to the
		// product
		String[] colorNames = { "Black", "Red", "Blue", "Green" };

		// these are the real JavaFX color values that match the above names
		Color[] Colors = { Color.BLACK, Color.RED, Color.BLUE, Color.DARKOLIVEGREEN };

		VBox C = new VBox(10);
		C.getChildren().addAll(colorLabel, COLORS);

		// this lil array keeps track of the currently selected circle (only one at a
		// time)
		final Circle[] selectedCircle = new Circle[1];

		// loop through the color options to create a colored circle for each one
		for (int i = 0; i < colorNames.length; i++) {
			Circle circle = new Circle(15);
			circle.setFill(Colors[i]); // fill it with the corresponding color
			circle.setStrokeWidth(0); // no border at first

			// putting the circle inside a StackPane so we can easily click it and style it
			StackPane circlePane = new StackPane(circle);
			circlePane.setCursor(Cursor.HAND); // make it feel clickable (more like a button object and not just a
												// circle)

			int finalI = i; // needed to use inside the lambda below

			// when user clicks a circle
			circlePane.setOnMouseClicked(e -> {
				// remove the border from the previously selected circle if there was one
				if (selectedCircle[0] != null) {
					selectedCircle[0].setStrokeWidth(0);
				}

				// now mark this circle as the selected one and add a visible border to it
				selectedCircle[0] = circle;
				circle.setStroke(Color.DARKSLATEGREY);
				circle.setStrokeWidth(3.2);

				// actually set the product's selectedColor property to the one the user clicked
				product.setSelectedColor(colorNames[finalI]);
			});

			// finally, add the colored circle button to the row
			COLORS.getChildren().add(circlePane);
		}

		// same idea now but for sizes
		HBox SIZES = new HBox(30); // holds the size buttons with spacing
		Label sizeLabel = new Label("Sizes:");
		sizeLabel.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, FontPosture.ITALIC, 15));

		VBox SI = new VBox(10);
		SI.getChildren().addAll(sizeLabel, SIZES);

		// check if it's a shoe, if yes show numbers, if not show S, M, L, XL
		String[] sizes;
		if (!product.getCategory().getCatName().equalsIgnoreCase("shoes")) {
			sizes = new String[] { "S", "M", "L", "XL" };
		} else {
			sizes = new String[] { "34", "36", "38", "40" };
		}

		// use this to keep track of which size button is currently selected
		Button[] sizeButtons = new Button[sizes.length];
		final Button[] selectedSizeBtn = new Button[1];

		// loop through the sizes and create a button for each one
		for (int i = 0; i < sizes.length; i++) {
			String size = sizes[i];
			Button sizeBtn = new Button(size);
			applyBtnStyle(sizeBtn);
			sizeButtons[i] = sizeBtn;

			// when user clicks on a size
			sizeBtn.setOnAction(e -> {
				// update the product’s selected size
				product.setSelectedSize(size);

				// remove the "selected" look from the previously selected button
				if (selectedSizeBtn[0] != null) {
					selectedSizeBtn[0].getStyleClass().remove("selected-btn");
					applyBtnStyle(selectedSizeBtn[0]); // reset it to normal
				}

				// apply a special style to show this button is now selected
				sizeBtn.getStyleClass().add("selected-btn");
				sizeBtn.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff; "
						+ "-fx-background-radius: 12; -fx-border-radius: 12; -fx-padding: 10 20; -fx-cursor: hand;");
				selectedSizeBtn[0] = sizeBtn;
			});

			// add this button to the size row
			SIZES.getChildren().add(sizeBtn);
		}

		Button addtoCart = new Button("Add to cart - 🛒");
		addtoCart.setStyle("-fx-background-color: #000000;" + "-fx-text-fill: #FFFFFF;" + "-fx-background-radius: 12;"
				+ "-fx-border-radius: 12;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #b0b0b0, 6, 0, 2, 2),"
				+ "            dropshadow(gaussian, #ffffff, 6, 0, -2, -2);");
		addtoCart.setPrefSize(400, 75);
		addtoCart.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 20));
		addtoCart.setOnMouseEntered(e -> {
			addtoCart.setScaleX(1.03);
			addtoCart.setScaleY(1.03);
		});
		addtoCart.setOnMouseExited(e -> {
			addtoCart.setScaleX(1);
			addtoCart.setScaleY(1);
		});

		Text FAIL = new Text("");
		FAIL.setFill(Color.TRANSPARENT);
		FAIL.setFont(Font.font("Book Antiqua", 15));

		VBox recommendedTab = new VBox(10);

		Label recomTitle = new Label("Recommended Items:");
		recomTitle.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 15));

		HBox recomItems = new HBox(20);
		ArrayList<Integer> usedItems = new ArrayList<>();

		int maxItems = Math.min(10, prodList.size());
		while (usedItems.size() < maxItems) {
			int rand = (int) (Math.random() * prodList.size());

			boolean alreadyUsed = false;
			for (int used : usedItems) {
				if (used == rand) {
					alreadyUsed = true;
					break;
				}
			}

			if (alreadyUsed)
				continue;
			usedItems.add(rand);

			VBox recomCard = new VBox();
			recomCard.setAlignment(Pos.CENTER);

			Label recomName = new Label(prodList.get(rand).getName());
			recomName.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 10));

			ImageView recomImg = new ImageView(IMG_PATH + prodList.get(rand).getImgFileName());
			recomImg.setFitHeight(150);
			recomImg.setPreserveRatio(true);
			recomImg.setOnMouseEntered(
					e -> recomImg.setStyle("-fx-effect: dropshadow(gaussian, #888888, 10, 0.5, 0, 0);"));
			recomImg.setOnMouseExited(e -> recomImg.setStyle(""));

			recomCard.setOnMouseClicked(e -> showProductDetails(primaryStage, prodList.get(rand)));

			recomCard.getChildren().addAll(recomImg, recomName);

			recomItems.getChildren().add(recomCard);
		}

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(recomItems);
		scrollPane.setStyle("-fx-background: #e0e0e0; -fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setPrefHeight(200);

		addtoCart.setOnAction(
				e -> tryAddToCart(product, product.getSelectedSize(), product.getSelectedColor(), 1, FAIL));

		recommendedTab.getChildren().addAll(recomTitle, scrollPane);

		DescRev.getChildren().addAll(Description, Stars, C, SI, addtoCart, FAIL);

		itemLayout.getChildren().addAll(itemImg, DescRev);

		detailLayout.getChildren().addAll(topLayout, itemLayout, recommendedTab);

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		Scene detailScene = new Scene(detailLayout, screenBounds.getWidth(), screenBounds.getHeight());
		primaryStage.setScene(detailScene);
		primaryStage.setFullScreen(true);
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
		Total = 0;

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
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setStyle("-fx-background: #e0e0e0; -fx-border-color: transparent;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

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

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		Scene itemScene = new Scene(scrollPane, screenBounds.getWidth(), screenBounds.getHeight());
		primaryStage.setScene(itemScene);
		primaryStage.setFullScreen(true);

		Button returnBtn = new Button("<- Back");
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

		Button returnBtn = new Button("<- Back");
		returnBtn.setOnAction(e -> showCart(primaryStage));
		applyBtnStyle(returnBtn);

		Label COTitle = new Label("Check Out\n" + "Total: $" + Total + "                 ");
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
		scrollPane.setPrefWidth(390);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
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
		Button creditConfirm = new Button("Confirm");
		applyBtnStyle(creditConfirm);

		Button creditCard = new Button("Credit Card");
		applyBtnStyle(creditCard);

		creditCard.setOnAction(e -> {
			userInfo.getChildren().addAll(creditNum, creditDate, creditCVC, creditConfirm);
		});

		creditConfirm.setOnAction(e -> {
			String creditDateRegex = "^(0[1-9]|1[0-2])/\\d{2}$";
			if (creditNum.getText().length() == 16 && creditDate.getText().matches(creditDateRegex)
					&& creditCVC.getText().length() == 3) {
				String[] parts = creditDate.getText().split("/");
				int month = Integer.parseInt(parts[0]);
				int year = Integer.parseInt(parts[1]);

				if (month >= 5 && year >= 25 || month >= 0 && year > 25) {
					showConfirmation(primaryStage);
				} else {
					Alert failAlert = new Alert(Alert.AlertType.ERROR);
					failAlert.setTitle("Error");
					failAlert.setHeaderText(null);
					failAlert.setContentText("Credit card expired!");
					failAlert.showAndWait();
				}
			} else {
				Alert failAlert = new Alert(Alert.AlertType.ERROR);
				failAlert.setTitle("Error");
				failAlert.setHeaderText(null);
				failAlert.setContentText("There's an error in your information. Please try again.");
				failAlert.showAndWait();
			}
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

			payPalPass.setOnAction(b -> payPalCheck(payPalStage, payPalEmail, payPalPass));

			Button Confirm = new Button("Confirm");

			Confirm.setOnAction(b -> payPalCheck(payPalStage, payPalEmail, payPalPass));

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

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		Scene COscene = new Scene(COLayoutFULL, screenBounds.getWidth(), screenBounds.getHeight());
		primaryStage.setScene(COscene);
		primaryStage.setFullScreen(true);

	}

	private void tryAddToCart(Product baseProduct, String size, String color, int count, Text failText) {
		if (size == null || color == null) {
			if (size == null && color == null) {
				failText.setText("Please choose both a size and a color!");
			} else if (size == null) {
				failText.setText("Please choose a size!");
			} else {
				failText.setText("Please choose a color!");
			}
			failText.setFill(Color.RED);
			return;
		}

		for (int i = 0; i < count; i++) {
			Product item = new Product(baseProduct);
			item.setSelectedSize(size);
			item.setSelectedColor(color);
			Cart.add(item);
		}
		failText.setFill(Color.TRANSPARENT);
		System.out.println("Item(s) added successfully");
	}

	private void payPalCheck(Stage payPalStage, TextField emailField, PasswordField passField) {
		String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

		if (Pattern.matches(emailRegex, emailField.getText())) {
			StackPane loadingPane = new StackPane(new ProgressIndicator());
			StackPane root = new StackPane(loadingPane);
			root.setStyle("-fx-background-color: #e0e0e0;" + "-fx-border-color: transparent;"
					+ "-fx-effect: dropshadow(gaussian, #ffffff, 3, 0, -2, -2),"
					+ "            dropshadow(gaussian, #c0c0c0, 3, 0, 2, 2);");

			Scene scene = new Scene(root, 350, 300);
			payPalStage.setScene(scene);
			payPalStage.setTitle("Checking...");
			payPalStage.show();

			PauseTransition pause = new PauseTransition(Duration.seconds(3));
			pause.setOnFinished(p -> {
				ImageView check = new ImageView(IMG_PATH + "checkmark2.png");
				check.setFitWidth(300);
				check.setFitHeight(300);
				root.getChildren().setAll(check);

				PauseTransition pause1 = new PauseTransition(Duration.seconds(1.5));
				pause1.setOnFinished(c -> {
					payPalStage.close();
					showConfirmation(mainStage);
				});
				pause1.play();
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
		}
	}

	private void showConfirmation(Stage primaryStage) {

		VBox layout = new VBox(20);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(30));
		layout.setStyle("-fx-background-color: #e0f7fa;");

		HBox itemsRecap = new HBox(15);
		itemsRecap.setPadding(new Insets(10));
		itemsRecap.setAlignment(Pos.TOP_CENTER);
		itemsRecap.setBackground(Background.EMPTY);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(itemsRecap);
		scrollPane.setStyle(
				"-fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f9f9f9;");
		scrollPane.setPrefHeight(220);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setBackground(Background.EMPTY);

		ImageView checkMark = new ImageView(new Image(IMG_PATH + "check.png"));
		checkMark.setFitWidth(150);
		checkMark.setFitHeight(150);

		HBox itemRecap = new HBox(10);
		itemRecap.setAlignment(Pos.CENTER);
		itemRecap.getChildren().addAll(checkMark, scrollPane);

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

			VBox itemCardFULL = new VBox(1);
			itemCardFULL.setAlignment(Pos.CENTER);
			itemCardFULL.setBackground(Background.EMPTY);
			itemCardFULL.getChildren().addAll(img, itemInfo);

			itemsRecap.getChildren().addAll(itemCardFULL);
		}

		Label thankYou = new Label("Thank you for your purchase!");
		thankYou.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 24));

		Label details = new Label("Your order will arive in 3 - 5 business days. We appreciate your business!");
		details.setFont(Font.font("Book Antiqua", 16));

		Button returnBtn = new Button("Return to Home");
		applyBtnStyle(returnBtn);
		returnBtn.setOnAction(e -> {
			Cart.clear();
			start(primaryStage);
		});

		layout.getChildren().addAll(itemRecap, thankYou, details, returnBtn);

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		Scene confirmScene = new Scene(layout, screenBounds.getWidth(), screenBounds.getHeight());
		primaryStage.setScene(confirmScene);
		primaryStage.setFullScreen(true);
	}

	private void applyBtnStyle(Button button) {
		if (button.getStyleClass().contains("selected-btn"))
			return; // don't override selected

		button.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, FontPosture.ITALIC, 15));
		button.setStyle("-fx-background-color: #e0e0e0;" + "-fx-text-fill: #333333;" + "-fx-background-radius: 12;"
				+ "-fx-border-radius: 12;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #ffffff, 4, 0, -2, -2),"
				+ "            dropshadow(gaussian, #c0c0c0, 4, 0, 2, 2);");

		button.setOnMouseEntered(e -> {
			if (button.getStyleClass().contains("selected-btn"))
				return;

			button.setStyle("-fx-background-color: #d1d1d1;" + "-fx-text-fill: #333333;" + "-fx-background-radius: 12;"
					+ "-fx-border-radius: 12;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
					+ "-fx-effect: dropshadow(gaussian, #b0b0b0, 6, 0, 2, 2),"
					+ "            dropshadow(gaussian, #ffffff, 6, 0, -2, -2);");
			button.setScaleX(1.05);
			button.setScaleY(1.05);
		});

		button.setOnMouseExited(e -> {
			if (button.getStyleClass().contains("selected-btn"))
				return;

			button.setStyle("-fx-background-color: #e0e0e0;" + "-fx-text-fill: #333333;" + "-fx-background-radius: 12;"
					+ "-fx-border-radius: 12;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
					+ "-fx-effect: dropshadow(gaussian, #ffffff, 4, 0, -2, -2),"
					+ "            dropshadow(gaussian, #c0c0c0, 4, 0, 2, 2);");
			button.setScaleX(1.0);
			button.setScaleY(1.0);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
