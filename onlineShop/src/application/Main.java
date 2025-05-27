package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Main extends Application {

	ArrayList<Product> Products = new ArrayList<Product>();
	ArrayList<Category> catList = new ArrayList<Category>();
	ArrayList<Product> prodList = new ArrayList<Product>();
	ArrayList<Product> guestCart = new ArrayList<>(); // for users that AREN'T logged in yet, so it saves their cart for
	ArrayList<Product> cartToShow = new ArrayList<>(); // when they do log in
	ArrayList<User> users = new ArrayList<User>();

	int Total = 0;
	boolean loggedIn = false;
	private static final String IMG_PATH = "file:///C:/Users/moham/eclipse-workspace/onlineShop/src/application/Images/";

	private Stage mainStage;
	private FlowPane productGrid;
	private VBox drawerMenu;
	private boolean isDrawerOpen = false;
	private Rectangle overlay;
	private StackPane root;
	private Scene scene;
	private BorderPane mainPane;
	private final Stack<Node> historyStack = new Stack<>();
	private MediaPlayer mediaPlayer;

	@Override
	public void start(Stage primaryStage) {
		this.mainStage = primaryStage;

		productsList();
		Music();
		setupMainLayout(primaryStage);
		setupScene(primaryStage);

		primaryStage.setTitle("Online Shop");
		primaryStage.setFullScreenExitHint("");
		primaryStage.setFullScreen(true);
		primaryStage.show();
	}

	private void setupMainLayout(Stage primaryStage) {
		mainPane = new BorderPane();
		HBox topBar = createTopBar(primaryStage);
		mainPane.setTop(topBar);

		drawerMenu = createDrawerMenu(primaryStage);

		productGrid = createProductGrid(getRandomProducts(20));
		productGrid.setAlignment(Pos.CENTER);
		ScrollPane scrollPane = new ScrollPane(productGrid);
		scrollPane.setBackground(Background.EMPTY);
		scrollPane.setFitToWidth(true);

		VBox centerContent = new VBox(scrollPane);
		centerContent.setAlignment(Pos.CENTER_RIGHT);
		centerContent.setPadding(new Insets(20));
		centerContent.setStyle("-fx-background-color: transparent;");
		mainPane.setCenter(centerContent);

		mainPane.setStyle("-fx-background-color: #e6e1db;");
	}

	private void setupScene(Stage primaryStage) {
		overlay = new Rectangle();
		overlay.setFill(Color.rgb(0, 0, 0, 0.2));
		overlay.setVisible(false);
		overlay.setOnMouseClicked(e -> toggleDrawer());

		root = new StackPane(mainPane, overlay, drawerMenu);
		StackPane.setAlignment(drawerMenu, Pos.CENTER_LEFT);

		scene = new Scene(root, 800, 800);
		overlay.widthProperty().bind(scene.widthProperty());
		overlay.heightProperty().bind(scene.heightProperty());

		primaryStage.setScene(scene);
	}

	private HBox createTopBar(Stage stage) {
		Button menuBtn = new Button("≡");
		applyBtnStyle(menuBtn);
		menuBtn.setOnAction(e -> toggleDrawer());

		Button muteBtn = new Button("🔇");
		applyBtnStyle(muteBtn);
		muteBtn.setOnAction(e -> {
			if (!mediaPlayer.isMute()) {
				mediaPlayer.setMute(true);
				muteBtn.setText("🔊");
			} else {
				mediaPlayer.setMute(false);
				muteBtn.setText("🔇");
			}
		});

		TextField searchField = new TextField();
		searchField.setPromptText("Search for a product...                 🔍");
		searchField.setPrefWidth(200);
		searchField.textProperty().addListener((obs, oldText, newText) -> {
			ArrayList<Product> filtered = new ArrayList<>();
			for (Product p : prodList) {
				if (p.getName().toLowerCase().contains(newText.toLowerCase())) {
					filtered.add(p);
				}
			}
			updateProductGrid(productGrid, filtered);
		});

		ImageView logoImg = new ImageView(IMG_PATH + "newLogo1noBG3.png");
		logoImg.setFitHeight(75);
		logoImg.setFitWidth(250);
		logoImg.setCursor(Cursor.HAND);
		logoImg.setOnMouseClicked(e -> showDevs(mainStage));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox topBar = new HBox(10, menuBtn, muteBtn, searchField, spacer, logoImg);
		topBar.setPadding(new Insets(10));
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.setStyle("-fx-background-color: #e6e1db;");
		return topBar;
	}

	private void Music() {
		String musicFile = "src/application/shopMusic.mp3";
		Media backgroundMusic = new Media(new File(musicFile).toURI().toString());
		mediaPlayer = new MediaPlayer(backgroundMusic);
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.setVolume(0.2);
		mediaPlayer.play();
	}

	private VBox createDrawerMenu(Stage primaryStage) {
		VBox drawer = new VBox();
		drawer.setPrefWidth(300);
		drawer.setMinWidth(300);
		drawer.setMaxWidth(300);
		drawer.setTranslateX(-300); // off-screen
		drawer.setSpacing(10);
		drawer.setPadding(new Insets(20));
		drawer.setMaxWidth(300);
		drawer.setStyle("""
				    -fx-background-color: linear-gradient(to bottom, #f9f9f9, #e6e6e6);
				    -fx-border-color: transparent;
				    -fx-background-radius: 0 15 15 0; /* top-left, top-right, bottom-right, bottom-left */
				    -fx-padding: 20;
				    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.2, 0, 2);
				""");

		Button closeBtn = new Button("✕");
		closeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #333;");
		closeBtn.setAlignment(Pos.TOP_RIGHT);
		closeBtn.setCursor(Cursor.HAND);
		closeBtn.setOnAction(e -> toggleDrawer());

		// align it to the right using an HBox wrapper
		HBox closeContainer = new HBox(closeBtn);
		closeContainer.setAlignment(Pos.TOP_RIGHT);

		drawer.getChildren().add(closeContainer);

		Button profileBtn = new Button("Profile");
		if (!loggedIn) {
			profileBtn.setShape(new Circle(30));
			profileBtn.setMinSize(90, 90);
			profileBtn.setMaxSize(90, 90);
			applyBtnStyle(profileBtn);
			profileBtn.setCursor(Cursor.HAND);
		}
		profileBtn.setOnAction(e -> {
			if (!loggedIn) {
				ProfileCreation(mainStage);
			} else {
				openProfileScene(mainStage);
			}
		});
		drawer.getChildren().add(profileBtn);

		Label title = new Label("🛒 Shop by Category");
		title.setAlignment(Pos.CENTER);
		title.setMaxWidth(Double.MAX_VALUE);
		title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
		title.setTextFill(Color.web("#222"));
		title.setStyle("""
				    -fx-padding: 5 0 15 0;
				    -fx-border-color: #ccc;
				    -fx-border-width: 0 0 1 0;
				    -fx-font-size: 16px;
				""");
		drawer.getChildren().add(title);

		Region catSpacer = new Region();
		catSpacer.setPrefHeight(10);
		drawer.getChildren().add(catSpacer);

		for (Category category : catList) {
			Rectangle divider = new Rectangle(250, 1);
			divider.setFill(Color.web("#cccccc"));
			drawer.getChildren().add(divider);

			Button btn = new Button("🛍 " + category.getCatName());
			btn.setPrefWidth(Double.MAX_VALUE);
			btn.setAlignment(Pos.CENTER_LEFT);
			btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
			btn.setStyle("""
					    -fx-background-color: #f0f0f0;
					    -fx-text-fill: #333;
					    -fx-background-radius: 10;
					    -fx-border-color: #ccc;
					    -fx-border-radius: 10;
					    -fx-padding: 10 20;
					""");
			btn.setCursor(Cursor.HAND);

			btn.setOnMouseEntered(ev -> btn.setStyle("""
					    -fx-background-color: #dcdcdc;
					    -fx-text-fill: #000;
					    -fx-background-radius: 10;
					    -fx-border-color: #bbb;
					    -fx-border-radius: 10;
					    -fx-padding: 10 20;
					"""));

			btn.setOnMouseExited(ev -> btn.setStyle("""
					    -fx-background-color: #f0f0f0;
					    -fx-text-fill: #333;
					    -fx-background-radius: 10;
					    -fx-border-color: #ccc;
					    -fx-border-radius: 10;
					    -fx-padding: 10 20;
					"""));

			btn.setOnAction(e -> {
				showProducts(mainStage, category.getCatName());
				closeDrawer();
			});

			drawer.getChildren().add(btn);
		}
		Region spacer = new Region(); // to create a space between the category buttons, and put this button at the
										// bottom
		VBox.setVgrow(spacer, Priority.ALWAYS);

		Button cartButton = new Button("Check your cart - 🛒");
		applyBtnStyle(cartButton);
		cartButton.setMaxWidth(Double.MAX_VALUE);
		cartButton.setOnAction(e -> showCart(primaryStage));

		Button logoutBtn = new Button("Log out");
		applyBtnStyle(logoutBtn);
		logoutBtn.setMaxWidth(Double.MAX_VALUE);
		logoutBtn.setOnAction(e -> {
			if (loggedIn) {
				loggedIn = false;
				Alert logoutAlert = new Alert(AlertType.INFORMATION);
				logoutAlert.setTitle("Logged Out");
				logoutAlert.setHeaderText(null);
				logoutAlert.setContentText("You have been logged out successfully.");
				logoutAlert.initOwner(primaryStage);
				logoutAlert.showAndWait();
			} else {
				Alert logoutAlert = new Alert(AlertType.ERROR);
				logoutAlert.setTitle("Logged Out");
				logoutAlert.setHeaderText(null);
				logoutAlert.setContentText("You are already logged out");
				logoutAlert.initOwner(primaryStage);
				logoutAlert.showAndWait();
			}
		});

		drawer.getChildren().addAll(spacer, cartButton, logoutBtn);

		return drawer;
	}

	private void toggleDrawer() {
		TranslateTransition transition = new TranslateTransition(Duration.millis(150), drawerMenu);
		if (isDrawerOpen) {
			transition.setToX(-drawerMenu.getWidth());
			overlay.setVisible(false);
		} else {
			transition.setToX(0);
			overlay.setVisible(true);
		}
		isDrawerOpen = !isDrawerOpen;
		transition.play();
	}

	private void closeDrawer() {
		if (isDrawerOpen) {
			TranslateTransition transition = new TranslateTransition(Duration.millis(150), drawerMenu);
			transition.setToX(-drawerMenu.getWidth());
			transition.setOnFinished(e -> overlay.setVisible(false));
			transition.play();
			isDrawerOpen = false;
		}
	}

	private FlowPane createProductGrid(ArrayList<Product> items) {
		FlowPane grid = new FlowPane();
		grid.setPadding(new Insets(30));
		grid.setHgap(30);
		grid.setVgap(30);
		updateProductGrid(grid, items);
		return grid;
	}

	private void updateProductGrid(FlowPane grid, ArrayList<Product> items) {
		grid.getChildren().clear();
		grid.setStyle("-fx-background-color: #e6e1db;");

		ArrayList<Product> printItems = new ArrayList<Product>();

		for (Product product : items) {
			if (!printItems.contains(product)) {
				printItems.add(product);
				VBox productCard = new VBox(10);
				productCard.setAlignment(Pos.CENTER);
				productCard.setPadding(new Insets(10));
				productCard.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 15;"
						+ "-fx-border-radius: 15;" + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 4);"
						+ "-fx-padding: 30 40 30 40;");
				productCard.setPrefWidth(250);

				Label prodName = new Label("Name: " + product.getName() + "\nPrice: $" + product.getPrice());
				prodName.setFont(Font.font("Century", FontWeight.BOLD, 15));
				prodName.setTextFill(Color.BLACK);

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
				img.setOnMouseClicked(e -> showProductDetails(mainStage, product));

				ComboBox<String> sizeBox = new ComboBox<>();
				sizeBox.setPromptText("Size");
				sizeBox.setStyle("""
						    -fx-background-color: white;
						    -fx-border-color: #ccc;
						    -fx-border-radius: 10;
						    -fx-background-radius: 10;
						    -fx-padding: 5 10 5 10;
						    -fx-font-family: 'Segoe UI';
						    -fx-font-size: 13;
						    -fx-cursor: hand;
						""");
				if (!product.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")
						&& !product.getCategory().getCatName().equalsIgnoreCase("MAKE UP")) {
					if (!product.getCategory().getCatName().equalsIgnoreCase("shoes")) {
						sizeBox.getItems().addAll("S", "M", "L", "XL");
					} else {
						sizeBox.getItems().addAll("34", "36", "38", "40");
					}
				} else {
					sizeBox.setDisable(true);
					sizeBox.setBlendMode(BlendMode.ADD);
				}

				ComboBox<String> colorBox = new ComboBox<String>();
				colorBox.setPromptText("Color");
				colorBox.setStyle("""
						    -fx-background-color: white;
						    -fx-border-color: #ccc;
						    -fx-border-radius: 10;
						    -fx-background-radius: 10;
						    -fx-padding: 5 10 5 10;
						    -fx-font-family: 'Segoe UI';
						    -fx-font-size: 13;
						    -fx-cursor: hand;
						""");

				if (product.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")) {
					colorBox.getItems().addAll("Gold", "Silver");
				} else if (product.getCategory().getCatName().equalsIgnoreCase("MAKE UP")) {
					colorBox.getItems().addAll("Pink", "Purple", "Red");
				} else {
					colorBox.getItems().addAll("Black", "Red", "Blue", "Green");
				}

				Text FAIL = new Text("");
				FAIL.setFill(Color.TRANSPARENT);
				FAIL.setFont(Font.font("Book Antiqua", 10));

				HBox cartStuff = new HBox();
				cartStuff.setAlignment(Pos.CENTER);

				TextField amount = new TextField("");
				amount.setPrefWidth(40);
				amount.setAlignment(Pos.CENTER);
				amount.setPromptText("Qty");

				amount.setOnAction(e -> {
					if (amount.getText().trim().isEmpty())
						return;

					try {
						int count = Integer.parseInt(amount.getText());
						if (count <= 0) {
							Alert zeroInput = new Alert(Alert.AlertType.WARNING);
							zeroInput.initOwner(mainStage);
							zeroInput.setTitle("Invalid Quantity");
							zeroInput.setHeaderText(null);
							zeroInput.setContentText("Please enter a quantity greater than 0.");
							zeroInput.showAndWait();
							return;
						}

						if (!product.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")
								&& !product.getCategory().getCatName().equalsIgnoreCase("MAKE UP")) {
							tryAddToCart(product, sizeBox.getValue(), colorBox.getValue(), count, FAIL);
						} else {
							tryAddToCart(product, "-", colorBox.getValue(), count, FAIL);
						}
						amount.clear();

					} catch (NumberFormatException ex) {
						Alert wrongInput = new Alert(Alert.AlertType.ERROR);
						wrongInput.initOwner(mainStage);
						wrongInput.setTitle("Input Error");
						wrongInput.setHeaderText(null);
						wrongInput.setContentText("Invalid input! Please enter numbers only.");
						wrongInput.showAndWait();
					}

					amount.clear();
				});

				Button cartBtn = new Button("Add to Cart");

				VBox IncDecBtns = new VBox();
				Button incBtn = new Button("+");
				Button decBtn = new Button("-");
				incBtn.setPrefSize(30, 20);
				decBtn.setPrefSize(30, 20);

				incBtn.setOnAction(e -> {
					int num;
					try {
						num = amount.getText().isEmpty() ? 0 : Integer.parseInt(amount.getText());
						amount.setText(Integer.toString(num + 1));
					} catch (NumberFormatException ex) {
						showError("Invalid input! Please enter numbers only.");
					}
				});

				decBtn.setOnAction(e -> {
					try {
						int num = Integer.parseInt(amount.getText());
						if (num > 1)
							amount.setText(Integer.toString(num - 1));
					} catch (NumberFormatException ex) {
						showError("Invalid input! Please enter numbers only.");
					}
				});

				IncDecBtns.getChildren().addAll(incBtn, decBtn);
				cartStuff.getChildren().addAll(cartBtn, amount, IncDecBtns);

				cartBtn.setOnAction(e -> {
					int count = 1;
					try {
						if (!amount.getText().isEmpty()) {
							count = Integer.parseInt(amount.getText());
							if (count <= 0)
								throw new NumberFormatException();
						}
					} catch (NumberFormatException ex) {
						showError("Invalid quantity!");
						return;
					}
					if (!product.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")
							&& !product.getCategory().getCatName().equalsIgnoreCase("MAKE UP")) {
						tryAddToCart(product, sizeBox.getValue(), colorBox.getValue(), count, FAIL);
					} else {
						tryAddToCart(product, "-", colorBox.getValue(), count, FAIL);
					}
					amount.clear();
				});

				productCard.getChildren().addAll(img, prodName, sizeBox, colorBox, cartStuff, FAIL);
				grid.getChildren().add(productCard);
			}
		}
	}

	private ArrayList<Product> getRandomProducts(int count) {
		ArrayList<Product> copy = new ArrayList<>(prodList);
		ArrayList<Product> selected = new ArrayList<>();

		int actualCount = Math.min(count, copy.size());

		while (selected.size() < actualCount) {
			int randIndex = (int) (Math.random() * copy.size());
			Product picked = copy.remove(randIndex);
			selected.add(picked);
		}

		return selected;
	}

	private void showError(String msg) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initOwner(mainStage);
		alert.setTitle("Input Error");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	private void openProfileScene(Stage primaryStage) {
		GridPane profile = new GridPane();
		profile.setStyle("-fx-background-color: #e6e1db;");

		Button backBtn = new Button("← Back");
		applyBtnStyle(backBtn);
		backBtn.setOnAction(e -> goBack());

		profile.setPadding(new Insets(20));

		profile.setAlignment(Pos.CENTER);

		profile.setStyle("-fx-background-color: linear-gradient(to bottom, #eeeeee, #cccccc);");

		VBox P = createVBox(15, 600, 400, Pos.TOP_CENTER);

		HBox topBar = new HBox(backBtn);
		topBar.setAlignment(Pos.TOP_LEFT);
		topBar.setPadding(new Insets(10));

		VBox fullLayout = new VBox(20, topBar, P);
		fullLayout.setAlignment(Pos.TOP_CENTER);

		P.setStyle("-fx-background-color: #ffffff; " + "-fx-background-radius: 12; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

		P.setPadding(new Insets(20));

		ImageView genderImg;

		String gender = Session.currentUser.getGender();
		if (gender != null)
			gender = gender.trim();
		if ("♂Male".equals(gender)) {
			genderImg = new ImageView(IMG_PATH + "male.png");
		} else if ("♀Female".equals(gender)) {
			genderImg = new ImageView(IMG_PATH + "female.png");
		} else {
			genderImg = new ImageView(IMG_PATH + "user.png"); // proper fallback
		}

		genderImg.setFitWidth(100);
		genderImg.setFitHeight(100);
		genderImg.setPreserveRatio(true);
		genderImg.setClip(new Circle(50, 50, 50)); // clip after it's definitely not null
		P.getChildren().add(genderImg);

		P.getChildren().addAll(createField("Username", Session.currentUser.getName()),
				createField("Email", Session.currentUser.getEmail()),

				createField("Phone", Session.currentUser.getPhone()),
				createField("Gender", Session.currentUser.getGender()),
				createField("Country", Session.currentUser.getCountry()));

		profile.add(fullLayout, 0, 0);

		setMainContent(profile);

	}

	private VBox createVBox(int spacing, double height, double width, Pos pos) {

		VBox box = new VBox(spacing);

		box.setPrefHeight(height);

		box.setPrefWidth(width);

		box.setAlignment(pos);

		return box;

	}

	private Label createLabel(String text, boolean isTitle) {

		Label label = new Label(text);

		label.setAlignment(Pos.CENTER_LEFT);

		if (isTitle) {
			label.setFont(Font.font("Century", FontWeight.BOLD, FontPosture.REGULAR, 18));
		} else {
			label.setFont(Font.font("Century", FontWeight.NORMAL, 16));
		}
		return label;
	}

	private VBox createField(String title, String value) {

		VBox field = new VBox(2);

		field.setAlignment(Pos.CENTER_LEFT);

		field.setPadding(new Insets(5));

		field.getChildren().addAll(

				createLabel(title, true),

				createLabel(value, false)

		);

		field.setStyle("-fx-background-color: #f9f9f9; " + "-fx-background-radius: 8;");
		return field;

	}

	private void showDevs(Stage primaryStage) {

		VBox fullScene = new VBox(10);
		fullScene.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #121212);" + "-fx-padding: 20;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0.2, 0, 4);");

		String txtStyle = "-fx-font-family: 'Verdana';" + "-fx-font-size: 18px;" + "-fx-text-fill: white;"
				+ "-fx-background-color: linear-gradient(to right, #4b6cb7, #182848);" + "-fx-padding: 10 20 10 20;"
				+ "-fx-background-radius: 15;" + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);";

		BorderPane topLayout = new BorderPane();
		topLayout.setPadding(new Insets(10, 20, 10, 20));

		Label devsTitle = new Label("Developers");
		devsTitle.setStyle("-fx-text-fill: #f5f5f5;" + "-fx-font-size: 26px;" + "-fx-font-weight: bold;"
				+ "-fx-letter-spacing: 1px;");

		devsTitle.setUnderline(true);
		devsTitle.setAlignment(Pos.CENTER);
		topLayout.setCenter(devsTitle);

		Button returnBtn = new Button("← Back");
		returnBtn.setStyle("-fx-background-color: linear-gradient(to right, #3a3a3a, #222);" + "-fx-text-fill: white;"
				+ "-fx-background-radius: 10;" + "-fx-cursor: hand;" + "-fx-font-weight: bold;");
		returnBtn.setOnAction(e -> goBack());
		topLayout.setLeft(returnBtn);

		FlowPane columns = new FlowPane();
		columns.setHgap(30);
		columns.setVgap(30);
		columns.setPadding(new Insets(20));
		columns.setAlignment(Pos.TOP_CENTER);

		fullScene.getChildren().addAll(topLayout, columns);

		VBox Yazan = new VBox(30);
		Yazan.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 4);" + "-fx-padding: 30 40 30 40;");
		Yazan.setAlignment(Pos.CENTER);

		ImageView yazanImg = new ImageView(IMG_PATH + "Yazan.jpg");
		yazanImg.setFitWidth(100);
		yazanImg.setFitHeight(100);
		yazanImg.setPreserveRatio(true);
		yazanImg.setClip(new Circle(50, 50, 50));
		Label yazanName = new Label("Yazan Sabbah");
		yazanName.setStyle(txtStyle);
		Label yazanMajor = new Label("Software Engineer");
		yazanMajor.setStyle(txtStyle);
		Label yazanNum = new Label("4779");
		yazanNum.setStyle(txtStyle);
		Label yazanEmail = new Label("202404779@bethlehem.edu");
		yazanEmail.setStyle(txtStyle);

		Yazan.getChildren().addAll(yazanImg, yazanName, yazanMajor, yazanNum, yazanEmail);

		VBox Athem = new VBox(30);
		Athem.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 4);" + "-fx-padding: 30 40 30 40;");
		Athem.setAlignment(Pos.CENTER);

		ImageView AthemImg = new ImageView(IMG_PATH + "Athem.jpg");
		AthemImg.setFitWidth(100);
		AthemImg.setFitHeight(100);
		AthemImg.setPreserveRatio(true);
		AthemImg.setClip(new Circle(50, 50, 50));
		Label AthemName = new Label("Mohammad Athem");
		AthemName.setStyle(txtStyle);
		Label AthemMajor = new Label("Software Engineer");
		AthemMajor.setStyle(txtStyle);
		Label AthemNum = new Label("4340");
		AthemNum.setStyle(txtStyle);
		Label AthemEmail = new Label("202404340@bethlehem.edu");
		AthemEmail.setStyle(txtStyle);

		Athem.getChildren().addAll(AthemImg, AthemName, AthemMajor, AthemNum, AthemEmail);

		VBox Reda = new VBox(30);
		Reda.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 4);" + "-fx-padding: 30 40 30 40;");
		Reda.setAlignment(Pos.CENTER);

		ImageView RedaImg = new ImageView(IMG_PATH + "Reda.jpg");
		RedaImg.setFitWidth(100);
		RedaImg.setFitHeight(100);
		RedaImg.setPreserveRatio(true);
		RedaImg.setClip(new Circle(50, 50, 50));
		Label RedaName = new Label("Reda Abu Ayyash");
		RedaName.setStyle(txtStyle);
		Label RedaMajor = new Label("Software Engineer");
		RedaMajor.setStyle(txtStyle);
		Label RedaNum = new Label("4874");
		RedaNum.setStyle(txtStyle);
		Label RedaEmail = new Label("202404874@bethlehem.edu");
		RedaEmail.setStyle(txtStyle);

		Reda.getChildren().addAll(RedaImg, RedaName, RedaMajor, RedaNum, RedaEmail);

		columns.getChildren().addAll(Yazan, Athem, Reda);

		setMainContent(fullScene);
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
		File file = new File("C:/Users/moham/eclipse-workspace/onlineShop/src/application/Prod_Info/USERS.txt");
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] array = line.split(" ");
				String name = array[0];
				String password = array[1];
				String email = array[2];
				String phone = array[3];
				String gender = array[4];
				String country = array[5];
				User u = new User(name, password, email, phone, gender, country);
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
		pane1.setStyle("-fx-background-color: #e6e1db");

		VBox loginCard = new VBox(30);
		loginCard.setPrefHeight(800);
		loginCard.setPrefWidth(500);
		loginCard.setAlignment(Pos.TOP_CENTER);
		loginCard.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 4);" + "-fx-padding: 30 40 30 40;");
		pane1.add(loginCard, 0, 0);

		setMainContent(pane1);
		primaryStage.setFullScreen(true);

		ImageView logoImg = new ImageView(IMG_PATH + "newLogo1noBG.png");
		logoImg.setPreserveRatio(true);
		logoImg.setFitHeight(250);

		// Register button
		Button register = new Button("\nDon't have an account?\n\t   Register");
		register.setAlignment(Pos.CENTER);
		register.setBackground(Background.EMPTY);
		register.setFont(Font.font("Century", FontWeight.BOLD, FontPosture.ITALIC, 15));
		register.setTextFill(Color.BLUE);
		register.setCursor(Cursor.HAND);

		// Log in button
		Button login = new Button("Log in");
		login.setAlignment(Pos.CENTER);
		login.setDisable(true);
		GridPane.setHalignment(login, HPos.CENTER);

		login.setStyle("-fx-background-color: #000000;" + "-fx-text-fill: white;" + "-fx-font-size: 16px;"
				+ "-fx-background-radius: 10;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #888888, 6, 0.3, 0, 1);");

		login.setOnMouseEntered(e -> login.setStyle("-fx-background-color: #222;" + "-fx-text-fill: white;"
				+ "-fx-font-size: 16px;" + "-fx-background-radius: 10;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #666666, 8, 0.4, 0, 2);"));
		login.setOnMouseExited(e -> login.setStyle("-fx-background-color: #000;" + "-fx-text-fill: white;"
				+ "-fx-font-size: 16px;" + "-fx-background-radius: 10;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #888888, 6, 0.3, 0, 1);"));

		// UserName Text Field
		TextField UNfield = new TextField();
		UNfield.setPromptText("👤  Username");
		UNfield.setAlignment(Pos.CENTER);
		UNfield.setMaxSize(250, 60);

		// PassWord Field
		PasswordField PWfield = new PasswordField();
		PWfield.setPromptText("🔒  Password");
		PWfield.setAlignment(Pos.CENTER);
		PWfield.setMaxSize(250, 60);

		TextField visiblePWfield = new TextField();
		visiblePWfield.setPromptText("🔒  Password");
		visiblePWfield.setAlignment(Pos.CENTER);
		visiblePWfield.setMaxSize(250, 60);

		visiblePWfield.textProperty().bindBidirectional(PWfield.textProperty());

		Button toggleBtn = new Button("\uD83D\uDC41");
		toggleBtn.setStyle("-fx-background-color: transparent;" + "-fx-font-size: 18px;" + "-fx-text-fill: #666;"
				+ "-fx-cursor: hand;");

		toggleBtn
				.setOnMouseEntered(e -> toggleBtn.setStyle("-fx-background-color: #ddd;" + "-fx-background-radius: 50%;"
						+ "-fx-font-size: 18px;" + "-fx-text-fill: #333;" + "-fx-cursor: hand;"));

		toggleBtn.setOnMouseExited(e -> toggleBtn.setStyle("-fx-background-color: transparent;" + "-fx-font-size: 18px;"
				+ "-fx-text-fill: #666;" + "-fx-cursor: hand;"));

		StackPane passwordFieldStack = new StackPane();
		passwordFieldStack.getChildren().addAll(PWfield, visiblePWfield);
		visiblePWfield.setVisible(false); // hide the visible one initially

		toggleBtn.setOnMousePressed(e -> { // make it visible when mouse click and hold
			visiblePWfield.setVisible(true);
			PWfield.setVisible(false);
		});
		toggleBtn.setOnMouseReleased(e -> {// make it invisible again when mouse released
			visiblePWfield.setVisible(false);
			PWfield.setVisible(true);
		});
		HBox passwordRow = new HBox(10, passwordFieldStack, toggleBtn);
		passwordRow.setAlignment(Pos.CENTER);

		String inputStyle = """
				    -fx-background-color: transparent;
				    -fx-border-color: transparent transparent #aaa transparent;
				    -fx-border-width: 0 0 2 0;
				    -fx-font-size: 16px;
				    -fx-text-fill: #333;
				    -fx-padding: 8 0 5 0;
				""";

		UNfield.setStyle(inputStyle);
		PWfield.setStyle(inputStyle);
		visiblePWfield.setStyle(inputStyle);

		// Enable login only when both fields are not empty
		ChangeListener<String> loginFieldListener = (obs, oldVal, newVal) -> {
			boolean enable = !UNfield.getText().trim().isEmpty() && !PWfield.getText().trim().isEmpty();
			login.setDisable(!enable);
		};

		UNfield.textProperty().addListener(loginFieldListener);
		PWfield.textProperty().addListener(loginFieldListener);

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
						Session.currentUser = user; // assign the matched user first
						if (!guestCart.isEmpty()) {
							Session.currentUser.getCart().addAll(guestCart);
							guestCart.clear();
						}
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
				} else {
					loggedIn = true;
					goBack();
				}

			}

		});

		loginCard.getChildren().addAll(logoImg, UNfield, passwordRow, login, register);

		VBox registerCard = new VBox(30);
		registerCard.setAlignment(Pos.TOP_CENTER);
		registerCard.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 4);" + "-fx-padding: 30 40 30 40;");
		registerCard.setPrefWidth(500);
		registerCard.setPrefHeight(800);

		ImageView logo1 = new ImageView(IMG_PATH + "newLogo1noBG3.png");
		logo1.setPreserveRatio(true);
		logo1.setFitHeight(90);

		TextField Nfield = new TextField();
		Nfield.setPromptText("👤  Username");
		Nfield.setMaxSize(250, 60);
		Nfield.setAlignment(Pos.CENTER);
		Nfield.setStyle(inputStyle); // same as login

		PasswordField pwField = new PasswordField();
		pwField.setPromptText("🔒  Password");
		pwField.setMaxSize(250, 60);
		pwField.setAlignment(Pos.CENTER);
		pwField.setStyle(inputStyle);

		PasswordField pwCField = new PasswordField();
		pwCField.setPromptText("🔁  Confirm Password");
		pwCField.setMaxSize(250, 60);
		pwCField.setAlignment(Pos.CENTER);
		pwCField.setStyle(inputStyle);

		TextField emailField = new TextField();
		emailField.setPromptText("📧  Email");
		emailField.setMaxSize(250, 60);
		emailField.setAlignment(Pos.CENTER);
		emailField.setStyle(inputStyle);

		TextField phoneField = new TextField();
		phoneField.setPromptText("📞  Phone Number");
		phoneField.setMaxSize(250, 60);
		phoneField.setAlignment(Pos.CENTER);
		phoneField.setStyle(inputStyle);

		HBox genderRadio = new HBox(10);
		genderRadio.setAlignment(Pos.CENTER);

		ToggleGroup Gender = new ToggleGroup();
		RadioButton Male = new RadioButton("♂Male");
		RadioButton Female = new RadioButton("♀Female");
		Male.setToggleGroup(Gender);
		Female.setToggleGroup(Gender);

		Male.setStyle("-fx-font-family: 'Segoe UI';" + "-fx-font-size: 14;" + "-fx-text-fill: #333;"
				+ "-fx-background-color: #f2f2f2;" + "-fx-background-radius: 10;" + "-fx-border-color: #bbb;"
				+ "-fx-border-radius: 10;" + "-fx-border-width: 1;" + "-fx-padding: 6 14;" + "-fx-cursor: hand;");

		Female.setStyle("-fx-font-family: 'Segoe UI';" + "-fx-font-size: 14;" + "-fx-text-fill: #333;"
				+ "-fx-background-color: #f2f2f2;" + "-fx-background-radius: 10;" + "-fx-border-color: #bbb;"
				+ "-fx-border-radius: 10;" + "-fx-border-width: 1;" + "-fx-padding: 6 14;" + "-fx-cursor: hand;");

		genderRadio.getChildren().addAll(Male, Female);

		ComboBox<String> countryComboBox = new ComboBox<>();
		countryComboBox.setStyle("-fx-background-color: white;" + "-fx-border-color: #ccc;" + "-fx-border-radius: 8;"
				+ "-fx-background-radius: 8;" + "-fx-padding: 5 10 5 10;" + "-fx-font-family: 'Segoe UI';"
				+ "-fx-font-size: 14;" + "-fx-cursor: hand;");

		String[] countryCodes = Locale.getISOCountries();

		for (String code : countryCodes) {
			Locale locale = new Locale("", code);
			String countryName = locale.getDisplayCountry();

			if (countryName.equalsIgnoreCase("Israel"))
				continue;

			if (countryName.equalsIgnoreCase("Palestinian Territories")) {
				if (!countryComboBox.getItems().contains("Palestine"))
					countryComboBox.getItems().add("Palestine");
			} else {
				if (!countryComboBox.getItems().contains(countryName)) // to avoid duplication
					countryComboBox.getItems().add(countryName);
			}
		}

		FXCollections.sort(countryComboBox.getItems());
		countryComboBox.setPromptText("Select your country");

		Button confirmBtn = new Button("Confirm");
		confirmBtn.setAlignment(Pos.CENTER);
		confirmBtn.setDisable(true);

		Button login1 = new Button("Login");
		login1.setAlignment(Pos.CENTER);
		login1.setBackground(Background.EMPTY);
		login1.setFont(Font.font("Century", FontWeight.BOLD, FontPosture.ITALIC, 15));
		login1.setTextFill(Color.BLUE);
		login1.setCursor(Cursor.HAND);
		login1.setOnAction(e -> setMainContent(pane1));

		confirmBtn.setStyle("-fx-background-color: #000000;" + "-fx-text-fill: white;" + "-fx-font-size: 16px;"
				+ "-fx-background-radius: 10;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #888888, 6, 0.3, 0, 1);");

		confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle("-fx-background-color: #222;" + "-fx-text-fill: white;"
				+ "-fx-font-size: 16px;" + "-fx-background-radius: 10;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #666666, 8, 0.4, 0, 2);"));
		confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle("-fx-background-color: #000;" + "-fx-text-fill: white;"
				+ "-fx-font-size: 16px;" + "-fx-background-radius: 10;" + "-fx-padding: 10 20;" + "-fx-cursor: hand;"
				+ "-fx-effect: dropshadow(gaussian, #888888, 6, 0.3, 0, 1);"));

		// Enable login only when both fields are not empty
		ChangeListener<String> registerFieldListener = (obs, oldVal, newVal) -> {
			boolean enable = !Nfield.getText().trim().isEmpty() && !pwField.getText().trim().isEmpty()
					&& !pwCField.getText().trim().isEmpty() && !emailField.getText().trim().isEmpty()
					&& !phoneField.getText().trim().isEmpty() && Gender.getSelectedToggle() != null
					&& countryComboBox.getValue() != null;
			confirmBtn.setDisable(!enable);
		};

		Nfield.textProperty().addListener(registerFieldListener);
		pwField.textProperty().addListener(registerFieldListener);
		pwCField.textProperty().addListener(registerFieldListener);
		emailField.textProperty().addListener(registerFieldListener);
		phoneField.textProperty().addListener(registerFieldListener);
		countryComboBox.valueProperty().addListener(registerFieldListener);
		Gender.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
			boolean enable = !Nfield.getText().trim().isEmpty() && !pwField.getText().trim().isEmpty()
					&& !pwCField.getText().trim().isEmpty() && !emailField.getText().trim().isEmpty()
					&& !phoneField.getText().trim().isEmpty() && Gender.getSelectedToggle() != null
					&& countryComboBox.getValue() != null;

			confirmBtn.setDisable(!enable);
		});

		registerCard.getChildren().addAll(logo1, Nfield, pwField, pwCField, emailField, phoneField, genderRadio,
				countryComboBox, confirmBtn, login1);

		GridPane wrapper = new GridPane();
		wrapper.setAlignment(Pos.CENTER);
		wrapper.setStyle("-fx-background-color: #e6e1db;");
		wrapper.setPadding(new Insets(20));
		wrapper.add(registerCard, 0, 0);

		// Register button action

		register.setOnAction(e -> setMainContent(wrapper));

		// Confirm button action
		confirmBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

				if (!(pwField.getText().trim().equals(pwCField.getText().trim()))) {
					Alert equalPassWord = new Alert(AlertType.WARNING);
					equalPassWord.initOwner(primaryStage);
					equalPassWord.setTitle("Mismatch passwords");
					equalPassWord.setHeaderText(null);
					equalPassWord.setContentText("Passwords do not match. Please try again.");
					equalPassWord.showAndWait();
					return;
				}

				if (!(Pattern.matches(emailRegex, emailField.getText()))) {
					Alert failAlert = new Alert(Alert.AlertType.ERROR);
					failAlert.initOwner(primaryStage);
					failAlert.setTitle("Login Failed");
					failAlert.setHeaderText(null);
					failAlert.setContentText("Invalid email format. Please try again.");
					failAlert.showAndWait();
					return;
				}

				for (User user : users) {
					if (user.getName().equals(Nfield.getText()) && user.getEmail().equals(emailField.getText())) {

						Alert accountExist = new Alert(AlertType.WARNING);
						accountExist.initOwner(primaryStage);
						accountExist.setTitle("Account Exists");
						accountExist.setHeaderText(null);
						accountExist.setContentText("This account already exists.");
						accountExist.showAndWait();
						return;
					}
				}

				if (Gender.getSelectedToggle() != null) {
					RadioButton selectedRadio = (RadioButton) Gender.getSelectedToggle();
					String genderText = selectedRadio.getText(); // "Male" or "Female"

					users.add(new User(Nfield.getText(), pwField.getText(), emailField.getText(), phoneField.getText(),
							genderText, countryComboBox.getValue()));
					Session.currentUser = users.get(users.size() - 1);
					Alert accountCreated = new Alert(AlertType.INFORMATION);
					accountCreated.initOwner(primaryStage);
					accountCreated.setTitle("Account Created");
					accountCreated.setHeaderText(null);
					accountCreated.setContentText("Account created successfully!");
					accountCreated.showAndWait();
				}

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
				loggedIn = true;
				Session.currentUser.setCart(guestCart);
				goBack();
				goBack();

			}

		});

	}

	private void showProducts(Stage primaryStage, String category) {

		VBox productListContainer = new VBox(20);
		productListContainer.setPadding(new Insets(20));
		productListContainer.setAlignment(Pos.TOP_CENTER);
		productListContainer.setStyle("-fx-background-color: #e6e1db;");

		Button returnBtn = new Button("← Back");
		applyBtnStyle(returnBtn);
		returnBtn.setOnAction(e -> goBack());

		Button cartButton = new Button("Check your cart - 🛒");
		applyBtnStyle(cartButton);
		cartButton.setOnAction(e -> showCart(primaryStage));

		BorderPane topLayout = new BorderPane();
		topLayout.setPadding(new Insets(10, 20, 10, 20));
		topLayout.setLeft(returnBtn);
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
			productCard.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 15;"
					+ "-fx-border-radius: 15;" + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 4);"
					+ "-fx-padding: 30 40 30 40;");
			productCard.setPrefWidth(250);

			Label prodName = new Label("Name: " + product.getName() + "\nPrice: $" + product.getPrice());
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
			sizeBox.setStyle("""
					    -fx-background-color: white;
					    -fx-border-color: #ccc;
					    -fx-border-radius: 10;
					    -fx-background-radius: 10;
					    -fx-padding: 5 10 5 10;
					    -fx-font-family: 'Segoe UI';
					    -fx-font-size: 13;
					    -fx-cursor: hand;
					""");
			if (!category.equalsIgnoreCase("Accessories") && !category.equalsIgnoreCase("MAKE UP")) {
				if (!(category.equalsIgnoreCase("shoes"))) {
					sizeBox.getItems().addAll("S", "M", "L", "XL");
				} else {
					sizeBox.getItems().addAll("34", "36", "38", "40");
				}
			} else {
				sizeBox.setDisable(true);
				sizeBox.setBlendMode(BlendMode.ADD);
			}

			ComboBox<String> colorBox = new ComboBox<String>();
			colorBox.setPromptText("Color");
			colorBox.setStyle("""
					    -fx-background-color: white;
					    -fx-border-color: #ccc;
					    -fx-border-radius: 10;
					    -fx-background-radius: 10;
					    -fx-padding: 5 10 5 10;
					    -fx-font-family: 'Segoe UI';
					    -fx-font-size: 13;
					    -fx-cursor: hand;
					""");

			if (category.equalsIgnoreCase("ACCESSORIES")) {
				colorBox.getItems().addAll("Gold", "Silver");
			} else if (category.equalsIgnoreCase("MAKE UP")) {
				colorBox.getItems().addAll("Pink", "Purple", "Red");
			} else {
				colorBox.getItems().addAll("Black", "Red", "Blue", "Green");
			}

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
						wrongInput.initOwner(primaryStage);
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
						wrongInput.initOwner(primaryStage);
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
						wrongInput.initOwner(primaryStage);
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
						zeroInput.initOwner(primaryStage);
						zeroInput.setTitle("Invalid Quantity");
						zeroInput.setHeaderText(null);
						zeroInput.setContentText("Please enter a quantity greater than 0.");
						zeroInput.showAndWait();
						return;
					}

					if (!product.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")
							&& !product.getCategory().getCatName().equalsIgnoreCase("MAKE UP")) {
						tryAddToCart(product, sizeBox.getValue(), colorBox.getValue(), count, FAIL);
					} else {
						tryAddToCart(product, "-", colorBox.getValue(), count, FAIL);
					}
					amount.clear();

				} catch (NumberFormatException ex) {
					Alert wrongInput = new Alert(Alert.AlertType.ERROR);
					wrongInput.initOwner(primaryStage);
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
						wrongInput.initOwner(primaryStage);
						wrongInput.setTitle("Input Error");
						wrongInput.setHeaderText(null);
						wrongInput.setContentText("Invalid input! Please enter numbers only.");
						wrongInput.showAndWait();
						return;
					}
				}

				if (!product.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")
						&& !product.getCategory().getCatName().equalsIgnoreCase("MAKE UP")) {
					tryAddToCart(product, sizeBox.getValue(), colorBox.getValue(), count, FAIL);
				} else {
					tryAddToCart(product, "-", colorBox.getValue(), count, FAIL);
				}
			});

			productCard.getChildren().addAll(img, prodName, sizeBox, colorBox, cartStuff, FAIL);
			productList.getChildren().add(productCard);

		}

		setMainContent(scrollPane);
		primaryStage.setFullScreen(true);

	}

	private void showProductDetails(Stage primaryStage, Product product) {

		VBox detailLayout = new VBox(30);
		detailLayout.setPadding(new Insets(20));
		detailLayout.setAlignment(Pos.TOP_CENTER);
		detailLayout.setStyle("-fx-background-color: #e6e1db;");

		BorderPane topLayout = new BorderPane();
		topLayout.setPadding(new Insets(10, 20, 10, 20));

		Button returnBtn = new Button("← Back");
		returnBtn.setOnAction(e -> goBack());
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

		Text Description = new Text(product.getDescription() + "\n\nPrice: $" + product.getPrice() + "\n");
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

		Label colorLabel = new Label("Color:");
		colorLabel.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, FontPosture.ITALIC, 15));

		String[] colorNames;
		Color[] Colors;

		if (product.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")) {
			// these are the actual names we’ll use when assigning the selected color to the
			// product
			colorNames = new String[] { "Gold", "Silver" };
			// these are the color values that match the above names
			Colors = new Color[] { Color.GOLDENROD, Color.GRAY };
		} else if (product.getCategory().getCatName().equalsIgnoreCase("MAKE UP")) {
			colorNames = new String[] { "Pink", "Purple", "Red" };
			Colors = new Color[] { Color.PINK, Color.PURPLE, Color.RED };
		} else {
			colorNames = new String[] { "Black", "Red", "Blue", "Green" };
			Colors = new Color[] { Color.BLACK, Color.RED, Color.BLUE, Color.DARKOLIVEGREEN };
		}

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

			// add the colored circle button to the row
			COLORS.getChildren().add(circlePane);
		}

		// same idea now but for sizes
		HBox SIZES = new HBox(30);
		Label sizeLabel = new Label("Sizes:");
		sizeLabel.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, FontPosture.ITALIC, 15));

		VBox SI = new VBox(10);
		SI.getChildren().addAll(sizeLabel, SIZES);

		if (!product.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")
				&& !product.getCategory().getCatName().equalsIgnoreCase("MAKE UP")) {
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
		} else {
			sizeLabel.setText("");
			product.setSelectedSize("-");
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
		ArrayList<Product> randomRecoms = getRandomProducts(15);

		for (Product p : randomRecoms) {
			VBox recomCard = new VBox();
			recomCard.setAlignment(Pos.CENTER);

			Label recomName = new Label(p.getName());
			recomName.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 10));

			ImageView recomImg = new ImageView(IMG_PATH + p.getImgFileName());
			recomImg.setFitHeight(150);
			recomImg.setPreserveRatio(true);
			recomImg.setOnMouseEntered(
					e -> recomImg.setStyle("-fx-effect: dropshadow(gaussian, #888888, 10, 0.5, 0, 0);"));
			recomImg.setOnMouseExited(e -> recomImg.setStyle(""));

			recomCard.setOnMouseClicked(e -> showProductDetails(primaryStage, p));
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

		setMainContent(detailLayout);
		primaryStage.setFullScreen(true);
	}

	private void tryAddToCart(Product baseProduct, String size, String color, int count, Text failText) {
		if (size == null || color == null) {
			if (size == null && color == null) {
				failText.setText("Please choose both a size and a color!");
			} else if (size == null) {
				if (!baseProduct.getCategory().getCatName().equalsIgnoreCase("ACCESSORIES")) {
					failText.setText("Please choose a size!");
				}
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
			if (Session.currentUser != null) {
				Session.currentUser.addtoCart(item);
			} else {
				guestCart.add(item);
			}

		}
		failText.setFill(Color.TRANSPARENT);
		System.out.println("Item(s) added successfully");
	}

	private void showCart(Stage primaryStage) {
		cartToShow = (Session.currentUser == null) ? guestCart : Session.currentUser.getCart();
		// this decides which cart to show, whether the guest's if not logged or the
		// user's

		if (cartToShow.isEmpty()) {
			Alert emptyCart = new Alert(Alert.AlertType.ERROR);
			emptyCart.initOwner(primaryStage);
			emptyCart.setTitle("Cart's Empty");
			emptyCart.setHeaderText(null);
			emptyCart.setContentText("You haven't added anything to your cart yet!");
			emptyCart.showAndWait();
			return;
		}

		Total = 0;

		VBox outerLayout = new VBox();
		outerLayout.setStyle("-fx-background-color: #e6e1db;");
		outerLayout.setAlignment(Pos.TOP_CENTER);
		outerLayout.setPrefHeight(scene.getHeight()); // Ensure it fills screen height

		FlowPane cartItemsFlow = new FlowPane();
		cartItemsFlow.setHgap(30);
		cartItemsFlow.setVgap(30);
		cartItemsFlow.setPadding(new Insets(20));
		cartItemsFlow.setAlignment(Pos.TOP_CENTER);
		cartItemsFlow.setStyle("-fx-background-color: #e6e1db;");

		VBox cartContainer = new VBox(10);
		cartContainer.setStyle("-fx-background-color: #e6e1db;");
		cartContainer.getChildren().add(cartItemsFlow);
		cartContainer.setPrefHeight(600);
		cartContainer.setAlignment(Pos.TOP_CENTER);

		ScrollPane scrollPane = new ScrollPane();
		outerLayout.getChildren().addAll(cartContainer);
		scrollPane.setContent(outerLayout);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setStyle("-fx-background-color: #e6e1db;");
		scrollPane.setFitToWidth(true);

		ArrayList<Product> printedItems = new ArrayList<Product>();

		for (Product product : cartToShow) {
			if (printedItems.contains(product))
				continue;

			int count = 0;
			for (Product dupe : cartToShow) {
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
			if (cartToShow.size() > 1) {
				removeBtn.setOnAction(e -> {
					cartToShow.remove(product);
					showCart(primaryStage);
				});
			} else {
				removeBtn.setOnAction(e -> {
					cartToShow.remove(product);
					start(primaryStage);
					Alert emptyCart2 = new Alert(Alert.AlertType.WARNING);
					emptyCart2.initOwner(primaryStage);
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

		setMainContent(scrollPane);
		primaryStage.setFullScreen(true);

		Button returnBtn = new Button("← Back");
		returnBtn.setOnAction(e -> goBack());

		Button clearBtn = new Button("Clear your cart - \uD83D\uDDD1");
		clearBtn.setOnAction(e -> {
			cartToShow.clear();
			goBack();
			Alert emptyCart2 = new Alert(Alert.AlertType.WARNING);
			emptyCart2.initOwner(primaryStage);
			emptyCart2.setTitle("Cart's Empty");
			emptyCart2.setHeaderText(null);
			emptyCart2.setContentText("You have completely emptied your cart!");
			emptyCart2.showAndWait();
		});

		Button checkoutBtn = new Button("Check out! - 🛒✅");
		checkoutBtn.setOnAction(e -> {
			if (loggedIn) {
				checkOut(primaryStage);
			} else {
				Alert loggedOut = new Alert(Alert.AlertType.WARNING);
				loggedOut.initOwner(primaryStage);
				loggedOut.setTitle("Not Logged In");
				loggedOut.setHeaderText("You need to be logged in to continue.");
				loggedOut.setContentText("""
						You're currently browsing as a guest. 🕵️‍♂️

						Would you like to log in now?
						""");

				ButtonType loginBtn = new ButtonType("Log In");
				ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
				loggedOut.getButtonTypes().setAll(loginBtn, cancelBtn);

				loggedOut.getDialogPane().setPrefSize(400, 200);

				loggedOut.getDialogPane().setStyle("""
						    -fx-font-family: 'Segoe UI';
						    -fx-font-size: 13;
						""");

				Optional<ButtonType> result = loggedOut.showAndWait();
				if (result.isPresent() && result.get() == loginBtn) {
					ProfileCreation(mainStage);
				}
			}
		});

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
		COLayoutFULL.setStyle("-fx-background-color: #e6e1db;");

		BorderPane COLayoutTitle = new BorderPane();
		COLayoutTitle.setPadding(new Insets(10, 20, 10, 20));

		Button returnBtn = new Button("← Back");
		returnBtn.setOnAction(e -> goBack());
		applyBtnStyle(returnBtn);

		Label COTitle = new Label("Check Out\n" + "Total: $" + Total);
		COTitle.setFont(Font.font("", FontWeight.EXTRA_BOLD, 25));

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

		for (Product product : Session.currentUser.getCart()) {
			if (printedItems.contains(product))
				continue;

			int count = 0;
			for (Product dupe : Session.currentUser.getCart()) {
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
				failAlert.initOwner(primaryStage);
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
			payPalStage.initOwner(primaryStage);
			payPalStage.setTitle("PayPal login");
			payPalStage.show();
		});

		VBox Payment = new VBox(10);

		paymentMethod.getChildren().addAll(creditCard, payPal);

		Payment.getChildren().addAll(COTitle, payMethodInfo);

		COLayout.getChildren().addAll(scrollPane, Payment);

		COLayoutFULL.getChildren().addAll(COLayoutTitle, COLayout);

		setMainContent(COLayoutFULL);
		primaryStage.setFullScreen(true);

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
			failAlert.initOwner(payPalStage);
			failAlert.setTitle("Login Failed");
			failAlert.setHeaderText(null);
			failAlert.setContentText("Invalid email format. Please try again.");
			failAlert.showAndWait();

			emailField.clear();
			passField.clear();
		}
	}

	private void billPrinter() {
		try {
			PrintWriter writer = new PrintWriter(
					"C:/Users/moham/eclipse-workspace/onlineShop/src/application/Prod_Info/Bill.txt");
			writer.println(Session.currentUser.getName() + "'s Receipt      " + LocalDate.now());
			writer.println("-----------------------------");

			ArrayList<Product> printedItems = new ArrayList<>();

			for (Product product : Session.currentUser.getCart()) {
				if (printedItems.contains(product))
					continue;

				int count = 0;
				for (Product dupe : Session.currentUser.getCart()) {
					if (product.equals(dupe)) {
						count++;
					}
				}

				printedItems.add(product);

				if (count > 1) {
					writer.println(count + "x " + product.getName() + " - " + product.getSelectedSize() + " - "
							+ product.getSelectedColor() + " - $" + product.getPrice() + " each");
				} else {
					writer.println(product.getName() + " - " + product.getSelectedSize() + " - "
							+ product.getSelectedColor() + " - $" + product.getPrice());
				}
			}

			writer.println("-----------------------------");
			writer.println("Total: $" + Total);

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void showConfirmation(Stage primaryStage) {

		VBox layout = new VBox(20);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(30));
		layout.setStyle("-fx-background-color: #e6e1db");

		HBox itemsRecap = new HBox(15);
		itemsRecap.setPadding(new Insets(10));
		itemsRecap.setAlignment(Pos.TOP_CENTER);
		itemsRecap.setBackground(Background.EMPTY);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(itemsRecap);
		scrollPane.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 4);" + "-fx-padding: 30 40 30 40;");
		scrollPane.setPrefHeight(300);
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

		for (Product product : Session.currentUser.getCart()) {
			if (printedItems.contains(product))
				continue;

			int count = 0;
			for (Product dupe : Session.currentUser.getCart()) {
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
			billPrinter();
			Session.currentUser.getCart().clear();
			setMainContent(mainPane);
		});

		layout.getChildren().addAll(itemRecap, thankYou, details, returnBtn);

		setMainContent(layout);
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

	private void setMainContent(Node newContent) {
		closeDrawer();
		if (!root.getChildren().isEmpty()) {
			Node current = root.getChildren().get(0);
			historyStack.push(current); // store the current screen
		}

		root.getChildren().set(0, newContent);
	}

	private void goBack() {
		if (!historyStack.isEmpty()) {
			Node previous = historyStack.pop();
			root.getChildren().set(0, previous);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
