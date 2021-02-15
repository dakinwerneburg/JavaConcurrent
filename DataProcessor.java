import java.io.File;
import java.util.concurrent.Future;

/**
 * Processes a data file by applying an analysis algorithm to each data item in
 * the file. Due to the fact that data processing may be slow, the data
 * processor implementation may use background threads to complete the work and
 * make the results available via the returned future.
 *
 * @author mpilone
 */
public interface DataProcessor {

	/**
	 * Processes the given data file using a configured analysis algorithm. The
	 * results of the analysis will be available from the returned future.
	 *
	 * @param dataFile the data file to process
	 * @return a future representing a potentially incomplete task that will
	 *         complete eventually
	 */
	Future<File> processDataFile(File dataFile);

	/**
	 * Shuts down the processor. Shutdown should always be called when the processor
	 * is no longer needed in order to release resources such as threads and files.
	 */
	void shutdown();
}
