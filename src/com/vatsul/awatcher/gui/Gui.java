package com.vatsul.awatcher.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import com.vatsul.awatcher.Main;
import com.vatsul.awatcher.MalApi;
import com.vatsul.awatcher.Utils;
import com.vatsul.awatcher.database.Indexer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Gui extends Application {
	
	private Stage primaryStage;
	
	private ImageView thumbnail;
	private Hyperlink titleLbl;
	private ComboBox<String> myScoreBox;
	private ComboBox<String> myStatusBox;
	private TextArea descriptionArea;
	private Label seasonTypeEpLbl;
	private HBox malBtnBox;
	private Button scanAnimeBtn;
	private Button scanVariousBtn;
	private TabPane animeTableTabs;
	
	private ProgressBar topProgressBar;
	private Label progressLbl;
	
	// Table data
	private ObservableList<MalRow> malTableDataStatus1;		// Watching
	private ObservableList<MalRow> malTableDataStatus2;		// Completed
	private ObservableList<MalRow> malTableDataStatus3;		// On Hold
	private ObservableList<MalRow> malTableDataStatus4;		// Dropped
	private ObservableList<MalRow> malTableDataStatus6;		// Plan to Watch
	
	private FilteredList<MalRow> malTableDataStatus1Filtered;
	private FilteredList<MalRow> malTableDataStatus2Filtered;
	private FilteredList<MalRow> malTableDataStatus3Filtered;
	private FilteredList<MalRow> malTableDataStatus4Filtered;
	private FilteredList<MalRow> malTableDataStatus6Filtered;
	
	private SortedList<MalRow> malTableDataStatus1Sorted;
	private SortedList<MalRow> malTableDataStatus2Sorted;
	private SortedList<MalRow> malTableDataStatus3Sorted;
	private SortedList<MalRow> malTableDataStatus4Sorted;
	private SortedList<MalRow> malTableDataStatus6Sorted;
	
	private int selectedMalID;
	private int selectedWatchedEps;
	
	private ArrayList<TableView> tableViews = new ArrayList<TableView>();
	
	public static boolean scanning = false;

	public Gui() {
		initializeMalRowData();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		setUserAgentStylesheet(STYLESHEET_MODENA);
		VBox mainHBox = new VBox();
		mainHBox.getChildren().addAll(topToolBar(), animeBox());
		
		StackPane root = new StackPane();
		root.getChildren().addAll(mainHBox);
		Scene scene = new Scene(root, 1200, 800);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
		primaryStage = stage;
		primaryStage.setTitle("Animuwatcher");
		primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
		primaryStage.setScene(scene);
		primaryStage.show();
		
		if(Main.config.getDisableWelcome()==false) {
			welcomeDialog();
			//Main.config.setDisableWelcome(true);
		}
	}
	
	private void welcomeDialog() {
		Dialog welcomeDialog = new Dialog();
		welcomeDialog.setWidth(300);
		welcomeDialog.setTitle("Animuwatch - Welcome");
		welcomeDialog.setHeaderText("Welcome to Animuwatch!");
		welcomeDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		Label welcomeLbl = new Label();
		welcomeLbl.setText("Thank you for downloading and running Animuwatch!\n"
				+ "To get started, please configure the application by clicking\n"
				+ "the gear like icon on top header of the application.\n"
				+ "Click the icon with folder and arrows to scan anime from selected folders.\n"
				+ "Click the icon left of it to sync everything else.");
		welcomeLbl.setWrapText(true);
		welcomeDialog.getDialogPane().setContent(welcomeLbl);
		
		welcomeDialog.showAndWait();
	}
	
	private ToolBar topToolBar() {
		ToolBar topToolBar = new ToolBar();
		
		scanVariousBtn = new Button();
		scanVariousBtn.setTooltip(new Tooltip("Update database data"));
		scanVariousBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(scanning) {
					System.out.println("Already scanning!");
				} else {
					System.out.println("Starting a new thread to update database data...");
					new Thread(() -> {
						updateDatabaseData();
					}).start();
				}
			}
		});
		ImageView scanVariousIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("scanIcon.png")));
		scanVariousIcon.setSmooth(true);
		scanVariousBtn.setGraphic(scanVariousIcon);
		topToolBar.getItems().add(scanVariousBtn);
		
		scanAnimeBtn = new Button();
		scanAnimeBtn.setTooltip(new Tooltip("Scan anime from folder (Warning: Takes a long time!)"));
		scanAnimeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				/*actionsProgressIndicator.setProgress(0);
				actionsProgressIndicator.setVisible(true);*/
				if(scanning) {
					System.out.println("Already scanning!");
				} else {
					System.out.println("Starting a new thread to scan anime from directories...");
					new Thread(() -> {
						scanAnimeFromDirectories();
					}).start();
				}
			}
		});
		ImageView scanAnimeIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("scanFolderIcon.png")));
		scanAnimeBtn.setGraphic(scanAnimeIcon);
		topToolBar.getItems().add(scanAnimeBtn);
		
		Button optionsBtn = new Button();
		optionsBtn.setTooltip(new Tooltip("Settings, click to set things"));
		optionsBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
			//	TODO: optionsWindow();
			optionsDialog();
			}
		});
		ImageView optionsIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("optionsIcon.png")));
		optionsBtn.setGraphic(optionsIcon);
		topToolBar.getItems().add(optionsBtn);
		
		Button playBtn = new Button();
		playBtn.setTooltip(new Tooltip("Click to play next episode from currently selected anime (If it exists on disk)"));
		playBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(selectedMalID>0) {
					int aid = Main.database.getAid(selectedMalID);
					int epNum = selectedWatchedEps;
					File nextEpisode = Main.database.getFileByAidEp(aid, epNum+1);
					if(nextEpisode!=null) {
						new Thread(() -> {
							try {
								Desktop.getDesktop().open(nextEpisode);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}).start();
					}
				}
			}
		});
		ImageView playIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("playIcon.png")));
		playBtn.setGraphic(playIcon);
		topToolBar.getItems().add(playBtn);
		
		ToggleButton listenMediaplayers = new ToggleButton("Toggle autoview");
		listenMediaplayers.setTooltip(new Tooltip("Toggle to listen running mediaplayer instances for running episodes"));
		listenMediaplayers.setSelected(Main.config.getListenMediaplayers());
		listenMediaplayers.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
					Main.config.setListenMediaplayers(listenMediaplayers.isSelected());
			}
		});
		topToolBar.getItems().add(listenMediaplayers);
		
		// Used to fill excess space between left and right side widgets
		Pane spaceFillerPane = new Pane();
		HBox.setHgrow(spaceFillerPane, Priority.ALWAYS);
		topToolBar.getItems().add(spaceFillerPane);
		
		VBox progressVBox = new VBox();
		topToolBar.getItems().add(progressVBox);
		
		topProgressBar = new ProgressBar();
		topProgressBar.setProgress(1);
		topProgressBar.setPrefWidth(250);
		progressVBox.getChildren().add(topProgressBar);
		
		progressLbl = new Label("Ready");
		progressLbl.setMaxWidth(Double.MAX_VALUE);
		progressLbl.setAlignment(Pos.CENTER);
		progressVBox.getChildren().add(progressLbl);
		
		Separator separator = new Separator(Orientation.VERTICAL);
		topToolBar.getItems().add(separator);
		
		TextField searchField = new TextField();
		searchField.setPromptText("Search");
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			malTableDataStatus1Filtered.setPredicate(MalRow -> {
				if(newValue == null || newValue.isEmpty()) {
					return true;
				} else if(MalRow.titleProperty().getValue().toLowerCase().contains(newValue.toLowerCase())) {
					return true;
				}
				return false;
			});
			
			malTableDataStatus2Filtered.setPredicate(MalRow -> {
				if(newValue == null || newValue.isEmpty()) {
					return true;
				} else if(MalRow.titleProperty().getValue().toLowerCase().contains(newValue.toLowerCase())) {
					return true;
				}
				return false;
			});
			
			malTableDataStatus3Filtered.setPredicate(MalRow -> {
				if(newValue == null || newValue.isEmpty()) {
					return true;
				} else if(MalRow.titleProperty().getValue().toLowerCase().contains(newValue.toLowerCase())) {
					return true;
				}
				return false;
			});
			
			malTableDataStatus4Filtered.setPredicate(MalRow -> {
				if(newValue == null || newValue.isEmpty()) {
					return true;
				} else if(MalRow.titleProperty().getValue().toLowerCase().contains(newValue.toLowerCase())) {
					return true;
				}
				return false;
			});
			
			malTableDataStatus6Filtered.setPredicate(MalRow -> {
				if(newValue == null || newValue.isEmpty()) {
					return true;
				} else if(MalRow.titleProperty().getValue().toLowerCase().contains(newValue.toLowerCase())) {
					return true;
				}
				return false;
			});
			refreshAnimeTables();
		});
		searchField.setPrefWidth(200);
		topToolBar.getItems().add(searchField);
		
		return topToolBar;
	}
	
	private void optionsDialog() {
		Dialog optionsDialog = new Dialog();
		optionsDialog.setTitle("Animuwatcher - Options");
		
		ButtonType saveBtn = new ButtonType("Save");
		
		optionsDialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
		
		GridPane optionsGrid = new GridPane();
		optionsDialog.getDialogPane().setContent(optionsGrid);
		
		Label animeDirectoryLbl = new Label("Anime Directory:");
		GridPane.setConstraints(animeDirectoryLbl, 0, 0);
		optionsGrid.getChildren().add(animeDirectoryLbl);
		DirectoryChooser animeDirectoryChooser = new DirectoryChooser();
		animeDirectoryChooser.setTitle("Titletest");
		animeDirectoryChooser.setInitialDirectory(Main.config.getAnimeDirectory());
		Button animeDirectoryBtn = new Button("Choose directory");
		animeDirectoryBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				File selectedDir = animeDirectoryChooser.showDialog(primaryStage);
				if(selectedDir!=null) {
					animeDirectoryChooser.setInitialDirectory(selectedDir);
				}
			}
		});
		GridPane.setConstraints(animeDirectoryBtn, 1, 0);
		optionsGrid.getChildren().add(animeDirectoryBtn);
		
		Label anidbUsernameLbl = new Label("AniDB Username:");
		GridPane.setConstraints(anidbUsernameLbl, 0, 1);
		optionsGrid.getChildren().add(anidbUsernameLbl);
		TextField anidbUsernameField = new TextField(Main.config.getAnidbUsername());
		GridPane.setConstraints(anidbUsernameField, 1, 1);
		optionsGrid.getChildren().add(anidbUsernameField);
		anidbUsernameField.setPrefWidth(200);
		
		Label anidbPasswordLbl = new Label("AniDB Password: ");
		GridPane.setConstraints(anidbPasswordLbl, 0, 2);
		optionsGrid.getChildren().add(anidbPasswordLbl);
		PasswordField anidbPasswordField = new PasswordField();
		anidbPasswordField.setText(Main.config.getAnidbPassword());
		GridPane.setConstraints(anidbPasswordField, 1, 2);
		optionsGrid.getChildren().add(anidbPasswordField);
		
		Label malUsernameLbl = new Label("MAL Username:");
		GridPane.setConstraints(malUsernameLbl, 0, 3);
		optionsGrid.getChildren().add(malUsernameLbl);
		TextField malUsernameField = new TextField(Main.config.getMalUsername());
		GridPane.setConstraints(malUsernameField, 1, 3);
		optionsGrid.getChildren().add(malUsernameField);
		
		Label malPasswordLbl = new Label("MAL Password:");
		GridPane.setConstraints(malPasswordLbl, 0, 4);
		optionsGrid.getChildren().add(malPasswordLbl);
		PasswordField malPasswordField = new PasswordField();
		malPasswordField.setText(Main.config.getMalPassword());
		GridPane.setConstraints(malPasswordField, 1, 4);
		optionsGrid.getChildren().add(malPasswordField);
		
		Optional result = optionsDialog.showAndWait();
		if(result.get() == saveBtn) {
			Main.config.setAnimeDirectory(animeDirectoryChooser.getInitialDirectory());
			Main.config.setAnidbUsername(anidbUsernameField.getText());
			Main.config.setAnidbPassword(anidbPasswordField.getText());
			Main.config.setMalUsername(malUsernameField.getText());
			Main.config.setMalPassword(malPasswordField.getText());
			System.out.println("Updated config with options");
		}
	}
	
	private HBox animeBox() {
		initializeMalRowData();
		HBox animeHBox = new HBox();
		
		// LEFT BOX
		VBox leftVBox = new VBox();
		leftVBox.setAlignment(Pos.CENTER);
		animeHBox.getChildren().add(leftVBox);
		
		// Image of anime
		thumbnail = new ImageView();
		thumbnail.setImage(new Image("file:placeholder.png"));
		thumbnail.setPreserveRatio(true);
		thumbnail.setFitWidth(300);
		leftVBox.getChildren().add(thumbnail);
		
		titleLbl = new Hyperlink("Animuwatcher");
		titleLbl.getStyleClass().add("titleLbl");
		titleLbl.setUnderline(false);
		titleLbl.setVisited(true);
		titleLbl.setWrapText(true);
		titleLbl.setPrefWidth(300);
		titleLbl.setTextAlignment(TextAlignment.CENTER);
		titleLbl.getStyleClass().add(("titleLabel"));
		leftVBox.getChildren().add(titleLbl);
		
		seasonTypeEpLbl = new Label("Developed by: Purus Cor");
		leftVBox.getChildren().add(seasonTypeEpLbl);
		
		malBtnBox = new HBox();
		malBtnBox.setDisable(true);
		leftVBox.getChildren().add(malBtnBox);

		myStatusBox = new ComboBox<String>();
		myStatusBox.getItems().addAll(
				"Watching",
				"Completed",
				"On Hold",
				"Dropped",
				"Plan To Watch"
				);
		myStatusBox.setTooltip(new Tooltip("Status on MyAnimeList"));
		myStatusBox.getSelectionModel().selectFirst();
		malBtnBox.getChildren().add(myStatusBox);
		
		myScoreBox = new ComboBox<String>();
		myScoreBox.getItems().addAll(
				"10",
				"9",
				"8",
				"7",
				"6",
				"5",
				"4",
				"3",
				"2",
				"1",
				"-"
				);
		myScoreBox.setTooltip(new Tooltip("My Score"));
		malBtnBox.getChildren().add(myScoreBox);
		
		Button updateMalBtn = new Button("Update");
		updateMalBtn.setTooltip(new Tooltip("Update the given data to MyAnimeList"));
		updateMalBtn.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent event) {
				int myStatusToUpdateTo = 0;
				if(myStatusBox.getValue().equals("Watching")) {
					myStatusToUpdateTo = 1;
				} else if(myStatusBox.getValue().equals("Completed")) {
					myStatusToUpdateTo = 2;
				} else if(myStatusBox.getValue().equals("On Hold")) {
					myStatusToUpdateTo = 3;
				} else if(myStatusBox.getValue().equals("Dropped")) {
					myStatusToUpdateTo = 4;
				} else if(myStatusBox.getValue().equals("Plan To Watch")) {
					myStatusToUpdateTo = 6;
				}
				int score;
				if(myScoreBox.getValue().equals("-")) {
					score = 0;
				} else {
					score = Integer.parseInt(myScoreBox.getValue());
				}
				final int myStatusToUpdateToFinal = myStatusToUpdateTo;
				new Thread(() -> {
					MalApi.updateAnimeListStatusScore(selectedMalID, myStatusToUpdateToFinal, score);
					MalApi.updateMyAnimeList();
					updateMalRowData();
				}).start();
			}
		});
		malBtnBox.getChildren().add(updateMalBtn);
		
		Button watchedBtn = new Button("+");
		watchedBtn.setTooltip(new Tooltip("Mark next episode as watched"));
		watchedBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new Thread(() -> {
					malBtnBox.setDisable(true);
					MalApi.updateAnimeListWatchedEpisodes(selectedMalID, selectedWatchedEps+1);
					MalApi.updateMyAnimeList();
					updateMalRowData();
					malBtnBox.setDisable(false);
				}).start();
			}
		});
		malBtnBox.getChildren().add(watchedBtn);
		
		Button notWatchedBtn = new Button("-");
		notWatchedBtn.setTooltip(new Tooltip("Mark previous episode as not watched"));
		notWatchedBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new Thread(() -> {
					malBtnBox.setDisable(true);
					MalApi.updateAnimeListWatchedEpisodes(selectedMalID, selectedWatchedEps-1);
					MalApi.updateMyAnimeList();
					updateMalRowData();
					malBtnBox.setDisable(false);
				}).start();
			}
		});
		malBtnBox.getChildren().add(notWatchedBtn);
		
		Separator separator = new Separator(Orientation.HORIZONTAL);
		leftVBox.getChildren().add(separator);
		
		descriptionArea = new TextArea();
		leftVBox.getChildren().add(descriptionArea);
		descriptionArea.getStyleClass().add("descriptionArea");
		descriptionArea.setEditable(false);
		descriptionArea.setWrapText(true);
		descriptionArea.setPrefWidth(300);
		descriptionArea.setPrefRowCount(Integer.MAX_VALUE);
		descriptionArea.setText("Animuwatcher is currently under development.");;
		// END OF LEFT BOX
		
		// Tabs which contain MAL info
		animeTableTabs = new TabPane();
		animeHBox.getChildren().add(animeTableTabs);
		HBox.setHgrow(animeTableTabs, Priority.ALWAYS);
		animeTableTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		// MAL Tabs
		Tab malWatchingTab = new Tab("Watching(0)");
		animeTableTabs.getTabs().add(malWatchingTab);
		TableView tableViewWatching = malTableByStatus();
		tableViews.add(tableViewWatching);
		malTableDataStatus1Sorted.comparatorProperty().bind(tableViewWatching.comparatorProperty());
		tableViewWatching.setItems(malTableDataStatus1Sorted);
		malWatchingTab.setContent(tableViewWatching);

		Tab malCompletedTab = new Tab("Completed(0)");
		animeTableTabs.getTabs().add(malCompletedTab);
		TableView tableViewCompleted = malTableByStatus();
		tableViews.add(tableViewCompleted);
		malTableDataStatus2Sorted.comparatorProperty().bind(tableViewCompleted.comparatorProperty());
		tableViewCompleted.setItems(malTableDataStatus2Sorted);
		malCompletedTab.setContent(tableViewCompleted);
		
		Tab malOnHoldTab = new Tab("On Hold(0)");
		animeTableTabs.getTabs().add(malOnHoldTab);
		TableView tableViewOnHold = malTableByStatus();
		tableViews.add(tableViewOnHold);
		malTableDataStatus3Sorted.comparatorProperty().bind(tableViewOnHold.comparatorProperty());
		tableViewOnHold.setItems(malTableDataStatus3Sorted);
		malOnHoldTab.setContent(tableViewOnHold);
		
		Tab malDroppedTab = new Tab("Dropped(0)");
		animeTableTabs.getTabs().add(malDroppedTab);
		TableView tableViewDropped = malTableByStatus();
		tableViews.add(tableViewDropped);
		malTableDataStatus4Sorted.comparatorProperty().bind(tableViewDropped.comparatorProperty());
		tableViewDropped.setItems(malTableDataStatus4Sorted);
		malDroppedTab.setContent(tableViewDropped);
		
		Tab malPlanToWatchTab = new Tab("Plan to Watch(0)");
		animeTableTabs.getTabs().add(malPlanToWatchTab);
		TableView tableViewPTW = malTableByStatus();
		tableViews.add(tableViewPTW);
		malTableDataStatus6Sorted.comparatorProperty().bind(tableViewPTW.comparatorProperty());
		tableViewPTW.setItems(malTableDataStatus6Sorted);
		malPlanToWatchTab.setContent(tableViewPTW);
		
		updateMalRowData();
		
		return animeHBox;
	}
	
	@SuppressWarnings("unchecked")
	private TableView malTableByStatus() {
		TableView malTable = new TableView();
		
		TableColumn<String, String> titleColumn = new TableColumn<String, String>("Title");
		titleColumn.setPrefWidth(500);
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		
		TableColumn<MalRow, Long> yearColumn = new TableColumn<MalRow, Long>("Year");
		yearColumn.getStyleClass().add("malTableColumnTitle");
		yearColumn.setPrefWidth(45);
		yearColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
		yearColumn.setCellFactory(new Callback<TableColumn<MalRow,Long>, TableCell<MalRow,Long>>() {
			@Override
			public TableCell<MalRow, Long> call(TableColumn<MalRow, Long> param) {
				TableCell<MalRow, Long> cell = new TableCell<MalRow, Long>() {
					@Override
					protected void updateItem(Long item, boolean empty) {
						if(item!=null && item==0) {
							setText("-");
						} else if(item!=null) {
							setText(new SimpleDateFormat("YYYY").format(new Date(item)));
						}
					}
				};
				return cell;
			}
		});
		
		TableColumn<MalRow, String> seasonColumn = new TableColumn<MalRow, String>("Season");
		seasonColumn.getStyleClass().add("malTableColumnTitle");
		seasonColumn.setPrefWidth(60);
		seasonColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MalRow,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<MalRow, String> param) {
				if(param.getValue().getStartDate()==0) {
					return new SimpleStringProperty("-");
				} else {
					return new SimpleStringProperty(getAnimeSeason(new Date(param.getValue().getStartDate())));
				}
			}
		});
		
		TableColumn<MalRow, Integer> seenColumn = new TableColumn<MalRow, Integer>("Seen");
		seenColumn.getStyleClass().add("malTableColumnTitle");
		seenColumn.setPrefWidth(45);
		seenColumn.setCellValueFactory(new PropertyValueFactory<>("seenEps"));
		seenColumn.setCellFactory(new Callback<TableColumn<MalRow,Integer>, TableCell<MalRow,Integer>>() {
			@Override
			public TableCell<MalRow, Integer> call(TableColumn<MalRow, Integer> param) {
				TableCell<MalRow, Integer> cell = new TableCell<MalRow, Integer>() {
					@Override
					protected void updateItem(Integer item, boolean empty) {
						if(item!=null && item==0) {
							setText("-");
						} else if(item!=null) {
							setText(item.toString());
						}
					}
				};
				return cell;
			}
		});
		
		TableColumn<MalRow, Integer> epsColumn = new TableColumn<MalRow, Integer>("Eps");
		epsColumn.getStyleClass().add("malTableColumnTitle");
		epsColumn.setPrefWidth(40);
		epsColumn.setCellValueFactory(new PropertyValueFactory<>("totalEps"));
		epsColumn.setCellFactory(new Callback<TableColumn<MalRow,Integer>, TableCell<MalRow,Integer>>() {
			@Override
			public TableCell<MalRow, Integer> call(TableColumn<MalRow, Integer> param) {
				TableCell<MalRow, Integer> cell = new TableCell<MalRow, Integer>() {
					@Override
					protected void updateItem(Integer item, boolean empty) {
						if(item!=null && item==0) {
							setText("-");
						} else if(item!=null) {
							setText(item.toString());
						}
					}
				};
				return cell;
			}
		});
		
		TableColumn<MalRow, Integer> scoreColumn = new TableColumn<MalRow, Integer>("Score");
		scoreColumn.getStyleClass().add("malTableColumnTitle");
		scoreColumn.setPrefWidth(50);
		scoreColumn.setCellValueFactory(new PropertyValueFactory<>("myScore"));
		scoreColumn.setCellFactory(new Callback<TableColumn<MalRow,Integer>, TableCell<MalRow,Integer>>() {
			@Override
			public TableCell<MalRow, Integer> call(TableColumn<MalRow, Integer> param) {
				TableCell<MalRow, Integer> cell = new TableCell<MalRow, Integer>() {
					@Override
					protected void updateItem(Integer item, boolean empty) {
						if(item!=null && item==0) {
							setText("-");
						} else if(item!=null) {
							setText(item.toString());
						}
					}
				};
				return cell;
			}
		});
		
		TableColumn<String, String> typeColumn = new TableColumn<String, String>("Type");
		typeColumn.getStyleClass().add("malTableColumnTitle");
		typeColumn.setPrefWidth(55);
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		
		TableColumn<MalRow, Integer> updatedColumn = new TableColumn<MalRow, Integer>("Updated");
		updatedColumn.getStyleClass().add("malTableColumnTitle");
		updatedColumn.setPrefWidth(75);
		updatedColumn.setCellValueFactory(new PropertyValueFactory<>("updatedDate"));
		updatedColumn.setCellFactory(new Callback<TableColumn<MalRow,Integer>, TableCell<MalRow,Integer>>() {
			@Override
			public TableCell<MalRow, Integer> call(TableColumn<MalRow, Integer> param) {
				TableCell<MalRow, Integer> cell = new TableCell<MalRow, Integer>() {
					@Override
					protected void updateItem(Integer item, boolean empty) {
						if(item!=null && item==0) {
							setText("-");
						} else if(item!=null) {
							setText(new SimpleDateFormat("YYYY-MM-dd").format(new Date(new Long(item)*1000)));
						}
					}
				};
				return cell;
			}
		});
		malTable.getColumns().addAll(titleColumn, yearColumn, seasonColumn, seenColumn,
				epsColumn, scoreColumn, typeColumn, updatedColumn);
		
		malTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MalRow>() {

			@Override
			public void changed(ObservableValue<? extends MalRow> observable, MalRow oldValue, MalRow newValue) {
				if(newValue!=null) {
					int malID = newValue.getMalID();
					int myStatus = Main.database.MyAnimeList.getMyStatusMalID(malID);
					File thumbnailFile = Main.database.MyAnimeList.getThumbnail(malID);
					thumbnail.setImage(new Image("file:cache/malthumbnails/"+thumbnailFile.getName()));
					String description = Main.database.MyAnimeList.getSynopsisByMalID(malID);
					description = description.replaceAll("<.*?>|\\[.*?]", "");
					description = org.unbescape.html.HtmlEscape.unescapeHtml(description);
					descriptionArea.setText(description);
					if(myStatus==1) {
						myStatusBox.setValue("Watching");
					} else if(myStatus==2) {
						myStatusBox.setValue("Completed");
					} else if(myStatus==3) {
						myStatusBox.setValue("On Hold");
					} else if(myStatus==4) {
						myStatusBox.setValue("Dropped");
					} else if(myStatus==6) {
						myStatusBox.setValue("Plan To Watch");
					}
					if(newValue.myScoreProperty().getValue()==0) {
						myScoreBox.setValue("-");
					} else {
						myScoreBox.setValue(newValue.myScoreProperty().intValue()+"");
					}
					titleLbl.setText(newValue.titleProperty().getValue());
					titleLbl.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
								System.out.println(".");
								new Thread(() -> {
									try {
										Desktop.getDesktop().browse(new URI("https://myanimelist.net/anime/"+malID));
									} catch (IOException | URISyntaxException e) {
										e.printStackTrace();
									}
								}).start();
							}
						}
					});
					String year = "";
					String season = "";
					if(newValue.getStartDate()>0) {
						season = getAnimeSeason(new Date(newValue.getStartDate()));
						year = new SimpleDateFormat("YYYY").format(new Date(newValue.getStartDate()));
					}
					seasonTypeEpLbl.setText(season + " - " 
					+ year + " | " +
							newValue.typeProperty().getValue() + " | Episodes: " + newValue.totalEpsProperty().getValue());
					
					selectedMalID = malID;
					selectedWatchedEps = newValue.seenEpsProperty().getValue();
					malBtnBox.setDisable(false);
				}
			}
		});
		malTable.getSortOrder().add(titleColumn);
		malTable.sort();
		return malTable;
	}
	
	// Called once to initialize mal row data arrays before updating them
	private void initializeMalRowData() {
		malTableDataStatus1 = FXCollections.observableArrayList();
		malTableDataStatus2 = FXCollections.observableArrayList();
		malTableDataStatus3 = FXCollections.observableArrayList();
		malTableDataStatus4 = FXCollections.observableArrayList();
		malTableDataStatus6 = FXCollections.observableArrayList();
		
		malTableDataStatus1Filtered = new FilteredList<MalRow>(malTableDataStatus1, p->true);
		malTableDataStatus2Filtered = new FilteredList<MalRow>(malTableDataStatus2, p->true);
		malTableDataStatus3Filtered = new FilteredList<MalRow>(malTableDataStatus3, p->true);
		malTableDataStatus4Filtered = new FilteredList<MalRow>(malTableDataStatus4, p->true);
		malTableDataStatus6Filtered = new FilteredList<MalRow>(malTableDataStatus6, p->true);
		
		malTableDataStatus1Sorted = new SortedList<MalRow>(malTableDataStatus1Filtered);
		malTableDataStatus2Sorted = new SortedList<MalRow>(malTableDataStatus2Filtered);
		malTableDataStatus3Sorted = new SortedList<MalRow>(malTableDataStatus3Filtered);
		malTableDataStatus4Sorted = new SortedList<MalRow>(malTableDataStatus4Filtered);
		malTableDataStatus6Sorted = new SortedList<MalRow>(malTableDataStatus6Filtered);
	}
	
	// Tableviews must be refreshed after each change in order to prevent data ghosting
	private void refreshAnimeTables() {
		for(int i=0; i<tableViews.size(); i++) {
			TableView tw = tableViews.get(i);
			tw.refresh();
			
			Tab tab = animeTableTabs.getTabs().get(i);
			Platform.runLater(() -> tab.setText(tab.getText().substring(
					0,tab.getText().indexOf("("))+"("+tw.getItems().size()+")"));
		}
	}
	
	public void updateMalRowData() {
		malTableDataStatus1.clear();
		malTableDataStatus1.addAll(getMalRowsByMyStatus(1));
		
		malTableDataStatus2.clear();
		malTableDataStatus2.addAll(getMalRowsByMyStatus(2));
		
		malTableDataStatus3.clear();
		malTableDataStatus3.addAll(getMalRowsByMyStatus(3));
		
		malTableDataStatus4.clear();
		malTableDataStatus4.addAll(getMalRowsByMyStatus(4));
		
		malTableDataStatus6.clear();
		malTableDataStatus6.addAll(getMalRowsByMyStatus(6));
		
		refreshAnimeTables();
	}
	
	private static ObservableList<MalRow> getMalRowsByMyStatus(int myStatus) {
		ArrayList<ArrayList> data = Main.database.MyAnimeList.getAnimeDataForTableByMyStatus(myStatus);
		ArrayList<Integer> malIDs = data.get(0);
		ArrayList<String> titles = data.get(1);
		ArrayList<Integer> types = data.get(2);
		ArrayList<Integer> totalEpisodes = data.get(3);
		ArrayList<Integer> watchedEpisodes = data.get(4);
		ArrayList<Integer> myScores = data.get(5);
		ArrayList<String> startDates = data.get(6);
		ArrayList<Long> startDatesMilliseconds = new ArrayList<Long>();
		ArrayList<Integer> lastUpdated = data.get(7);
		final ObservableList<MalRow> malRows = FXCollections.observableArrayList();
		for(int i=0; i<malIDs.size(); i++) {
			String startDate = startDates.get(i);
			try {
				if(startDate.equals("0000-00-00")) {
					startDatesMilliseconds.add(new Long(0));
				} else {
					startDatesMilliseconds.add(new SimpleDateFormat("yyyy-MM-dd").parse(startDate).getTime());
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			String type = "-";
			if(types.get(i)==1) {
				type = "TV";
			} else if(types.get(i)==2) {
				type = "OVA";
			} else if(types.get(i)==3) {
				type = "Movie";
			} else if(types.get(i)==4) {
				type = "Special";
			} else if(types.get(i)==5) {
				type = "ONA";
			}
			malRows.add(new MalRow(malIDs.get(i), titles.get(i), startDatesMilliseconds.get(i), watchedEpisodes.get(i),
					totalEpisodes.get(i), myScores.get(i), type, lastUpdated.get(i)));
		}
		return malRows;
	}
	
	// Input format YYYY-MM-DD
	private String getAnimeSeason(Date date) {
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
		String season;
		if(month<=3) {
			season = "Winter";
		} else if(month<=6) {
			season = "Spring";
		} else if(month<=9) {
			season = "Summer";
		} else {
			season = "Autumn";
		}
		return season;
	}
	
	private void scanAnimeFromDirectories() {
		Platform.runLater(() -> scanAnimeBtn.setDisable(true));
		Platform.runLater(() -> scanVariousBtn.setDisable(true));
		scanning = true;
		Platform.runLater(() -> topProgressBar.setProgress(-1));
		Platform.runLater(() -> progressLbl.setText("Scanning for valid files..."));
		ArrayList<File> videos = Utils.getVideoFiles(Main.config.getAnimeDirectory());
		for(int i=0; i<videos.size(); i++) {
			final int iFinal = i;
			Platform.runLater(() -> progressLbl.setText("Checking anime " + iFinal + "/" + videos.size()));
			final double progress = new Double(i)/(videos.size()-1);
			Platform.runLater(() -> topProgressBar.setProgress(progress));
			Indexer.checkAnime(videos.get(i));
		}
		Platform.runLater(() -> topProgressBar.setProgress(1));
		Platform.runLater(() -> progressLbl.setText("Done"));
		scanning = false;
		Platform.runLater(() -> scanAnimeBtn.setDisable(false));
		Platform.runLater(() -> scanVariousBtn.setDisable(false));
	}
	
	private void updateDatabaseData() {
		Platform.runLater(() -> scanAnimeBtn.setDisable(true));
		Platform.runLater(() -> scanVariousBtn.setDisable(true));
		scanning = true;
		Platform.runLater(() -> topProgressBar.setProgress(-1));
		Platform.runLater(() -> progressLbl.setText("Updating AID data..."));
		Indexer.updateAidData();
		Platform.runLater(() -> progressLbl.setText("Updating MALIDs..."));
		Indexer.updateMalids();
		Platform.runLater(() -> progressLbl.setText("Updating MyAnimeList..."));
		MalApi.updateMyAnimeList();
		Platform.runLater(() -> progressLbl.setText("Caching Synopses..."));
		MalApi.cacheSynopsesToDB();
		Platform.runLater(() -> progressLbl.setText("Caching Thumbnails..."));
		Indexer.cacheThumbnails();
		Platform.runLater(() -> topProgressBar.setProgress(1));
		Platform.runLater(() -> progressLbl.setText("Done"));
		updateMalRowData();
		scanning = false;
		Platform.runLater(() -> scanAnimeBtn.setDisable(false));
		Platform.runLater(() -> scanVariousBtn.setDisable(false));
	}
}
