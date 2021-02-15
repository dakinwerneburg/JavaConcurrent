
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

/**
 * HttpDataRetriever.java
 * CMIS 335 Project 4
 * Instructor: Mike Pilone
 * 
 * Purpose: Implements the DataRetriever interface which 
 * retrieves 1 of 10 files randomly that contains 25 randomly 
 * generates numbers between 1 and 10,000 from the Internet,then returns
 * the data file.
 *
 * @author Dakin T. Werneburg
 * @version 1.0 7/9/2016
 */
public class HttpDataRetriever implements DataRetriever{
	
	/**
	* Retrieves 1 of 10 possible data files on the Internet
	* by randomly generating a number 1-10 of the file name
	* and then writes to a file using the PrintWriter class
	* 
	* @return File of random numbers
	*/
	@Override
	public File retrieveDataFile() {	
		File dataFile = null;
		InputStream in = null;
		FileOutputStream out = null;
			
			try {

				dataFile = File.createTempFile("output_",".txt");
				int fileIndex = ThreadLocalRandom.current().nextInt(1,11); 
				URL dataUrl = new URL("http://cmsc335.s3-website-us-east-1.amazonaws.com/"
						+ fileIndex + ".dat");
				in = dataUrl.openStream();
				byte[] buffer = new byte[1024];
				int length;
				out = new FileOutputStream(dataFile);
				while((length = in.read(buffer)) != -1){
					out.write(buffer,0,length);					
				}				
			}			
			catch (IOException e) {
				e.printStackTrace();
			}			
			finally{
				try {
					if(out != null)
						out.flush();
						out.close();
					if(in != null) 
						in.close();
				}				
				catch (IOException e) {
					e.printStackTrace();
				}				
			}			
		 return dataFile;
	}
}
