import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ExecutorDataProcessor.java CMIS 335 Project 4 Instructor: Mike Pilone
 * 
 * Purpose: Implements the DataProcessor interface by performing all processing
 * using the ExecutorService and the constructor will set the number of threads
 * to use. .
 *
 * @author Dakin T. Werneburg
 * @version 1.0 7/9/2016
 */
public class ExecutorDataProcessor implements DataProcessor {

	private AnalysisAlgorithm algorithm;
	private File outputDir, outputFile;
	private int nThreads;
	private ExecutorService ex;

	/**
	 * Constructor copies provided parameters to class variables and constructs two
	 * threads and starts them, and constructs an ArrayList that will hold pending
	 * futures.
	 * 
	 * @param outputDir
	 * @param algorithm
	 */
	public ExecutorDataProcessor(File outputDir, AnalysisAlgorithm algorithm, int n) {
		this.outputDir = outputDir;
		this.algorithm = algorithm;
		nThreads = n;
		ex = Executors.newFixedThreadPool(nThreads);

	}

	/**
	 * Takes a file, reads a number then performs the analysis on each number per
	 * line, then writes the result to another file.
	 * 
	 * @param future dataFile the data file to process
	 * @return a future representing a potentially incomplete task that will
	 *         complete eventually
	 */
	@Override
	public Future<File> processDataFile(File dataFile) {
		outputFile = new File(outputDir + "/" + dataFile.getName());
		Runnable processor = new Runnable() {
			@Override
			public void run() {
				try {
					final Scanner input = new Scanner(dataFile);
					outputFile = new File(outputDir + "/" + dataFile.getName());
					PrintWriter pw = new PrintWriter(outputFile);
					while (input.hasNext()) {
						int number = input.nextInt();
						int result = algorithm.analyzeValue(number);
						pw.println(result);
					}
					input.close();
					pw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		};

		return ex.submit(processor, outputFile);
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void shutdown() {
		ex.shutdown();
	}
}
