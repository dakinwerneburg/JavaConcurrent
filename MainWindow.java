



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Future;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * MainWindow.java CMIS 335 Project 2 Instructor: Mike Pilone
 * 
 * Purpose: GUI interface to get the output directory and number of data files
 * to process and then begins executing the analysis that returns the number of
 * prime numbers based on the 25 randomly generated numbers. This code uses
 * JavaFX controls and lambda expressions.
 *
 * @author Dakin T. Werneburg
 * @version 1.0 5/28/2016
 */
public class MainWindow extends Application {
	private BorderPane rootBrdP;
	private GridPane layoutGrdP;
	private Label outputDirLabel, fileCountLabel, workerCountLabel, logLabel;
	private CheckBox cBox;
	private TextField directoryTextF;
	private TextArea logTextA;
	private Slider numFilesSldr, numWorkerSldr;
	private DataRetriever retriever;
	private AnalysisAlgorithm algorithm;
	private DataProcessor processor;
	private ArrayList<Future<File>> list;
	private Timeline timeline;
	private int nFilesToProcess, nFilesCompleted, nWorkers;
	private File selectedDir;

	/**
	 * Generates the user interface
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize UI properties and layout
		primaryStage.setTitle("Data Analyzer");
		rootBrdP = new BorderPane();
		rootBrdP.setPadding(new Insets(10, 10, 10, 10));
		layoutGrdP = new GridPane();
		layoutGrdP.setHgap(10);
		layoutGrdP.setVgap(10);
		rootBrdP.setTop(layoutGrdP);
		Scene scene = new Scene(rootBrdP, 480, 400);

		// output directory label and text field
		outputDirLabel = new Label("Output Directory:");
		directoryTextF = new TextField();
		GridPane.setHgrow(directoryTextF, Priority.ALWAYS);

		// browse button
		Button browseBtn = new Button("Browse...");
		browseBtn.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			String currDir = System.getProperty("user.dir");
			selectedDir = directoryChooser.showDialog(primaryStage);
			if (selectedDir == null) {
				directoryTextF.setText(currDir);
				selectedDir = new File(currDir);
			} else {
				directoryTextF.setText(selectedDir.getAbsolutePath());
			}
		});

		// file count slider
		fileCountLabel = new Label("File count:");
		numFilesSldr = new Slider();
		numFilesSldr.setMin(1);
		numFilesSldr.setMax(20);
		numFilesSldr.setShowTickLabels(true);
		numFilesSldr.setShowTickMarks(true);
		numFilesSldr.setMajorTickUnit(5);
		numFilesSldr.setMinorTickCount(4);
		numFilesSldr.setSnapToTicks(true);
		numFilesSldr.setBlockIncrement(1);

		// worker count slider
		workerCountLabel = new Label("Worker count:");
		numWorkerSldr = new Slider();
		numWorkerSldr.setMin(1);
		numWorkerSldr.setMax(5);
		numWorkerSldr.setShowTickLabels(true);
		numWorkerSldr.setShowTickMarks(true);
		numWorkerSldr.setMajorTickUnit(1);
		numWorkerSldr.setMinorTickCount(0);
		numWorkerSldr.setSnapToTicks(true);
		numWorkerSldr.setBlockIncrement(0);

		// Optimizer checkbox
		cBox = new CheckBox();
		cBox.setText("Enable optimized algorithm");

		// Go button and event handler
		Button go = new Button("Go");
		GridPane.setMargin(go, new Insets(0, 0, 10, 40));
		go.setOnAction(e -> {

			if (selectedDir == null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Directory Not Found");
				alert.setHeaderText(null);
				alert.setContentText("Directory Was Not Selected!");
				alert.showAndWait();
			} else {
				nFilesToProcess = (int) numFilesSldr.getValue();
				submitForProcessing(nFilesToProcess);
				getResults();
			}

		});

		// Log label and text area
		logLabel = new Label("Log:");
		logTextA = new TextArea();
		logTextA.setEditable(false);
		rootBrdP.setCenter(logTextA);

		// add all components
		layoutGrdP.add(outputDirLabel, 0, 0);
		layoutGrdP.add(directoryTextF, 1, 0);
		layoutGrdP.add(browseBtn, 2, 0);
		layoutGrdP.add(fileCountLabel, 0, 1);
		layoutGrdP.add(workerCountLabel, 0, 2);
		layoutGrdP.add(numFilesSldr, 1, 1);
		layoutGrdP.add(numWorkerSldr, 1, 2);
		layoutGrdP.add(cBox, 1, 3);
		layoutGrdP.add(go, 2, 4);
		layoutGrdP.add(logLabel, 0, 4);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Retrieves number of data files from requested, processes the data files that
	 * will return a future and adds it to an array that will be iterated through to
	 * check if that future is done, then deletes temporary file.
	 */
	private void submitForProcessing(int n) {
		try {
			retriever = new HttpDataRetriever();
			if (cBox.isSelected()) {
				algorithm = new OptimizedAnalysisAlgorithm();
			} else {
				algorithm = new NaiveAnalysisAlgorithm();
			}
			nWorkers = (int) numWorkerSldr.getValue();
			processor = new ExecutorDataProcessor(selectedDir, algorithm, nWorkers);
			list = new ArrayList<Future<File>>(n);
			File tempFile = null;
			for (int i = 0; i < n; i++) {
				logTextA.appendText("Retrieving Data File" + (i + 1) + "\n");
				tempFile = retriever.retrieveDataFile();
				logTextA.appendText("Initiating processing on data file" + (i + 1) + "\n");
				list.add(i, processor.processDataFile(tempFile));
			}
			tempFile.deleteOnExit();
			logTextA.appendText("Submitted " + n + " data files for processing\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Iterates through ArrayList of futures and checks whether the future is done.
	 * If done it will get the file and display that its completed and get the file
	 * path. Utilizes a timer to periodically update UI components.
	 */
	public void getResults() {
		nFilesCompleted = 0;
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
			if (nFilesToProcess == nFilesCompleted) {
				processor.shutdown();
				timeline.stop();
			} else {
				logTextA.appendText("Checking if any pending files are complete.\n");
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i) != null && list.get(i).isDone()) {
						try {
							String path = list.get(i).get().getAbsolutePath();
							logTextA.appendText(
									"Completed data file " + (i + 1) + ".  Results are available at " + path + "\n");
							list.set(i, null);
							nFilesCompleted++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
