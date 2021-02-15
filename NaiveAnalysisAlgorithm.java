
/**
 * A very simple and naive implementation of the analysis algorithm. The code
 * was taken from
 * http://stackoverflow.com/questions/9625663/calculating-and-printing-the-nth-prime-number.
 * More details about the algorithm and how it could be optimized can be found
 * on that page.
 *
 * @author mpilone
 */
public class NaiveAnalysisAlgorithm implements AnalysisAlgorithm {

	@Override
	public int analyzeValue(int n) {

		int candidate, count;
		for (candidate = 2, count = 0; count < n; ++candidate) {
			if (isPrime(candidate)) {
				++count;
			}
		}
		// The candidate has been incremented once after the count reached n
		return candidate - 1;
	}

	/**
	 * Checks if the given number is a prime number.
	 *
	 * @param n the number to check
	 * @return true if prime, false otherwise
	 */
	private static boolean isPrime(int n) {
		for (int i = 2; i < n; ++i) {
			if (n % i == 0) {
				// We are naive, but not stupid, if
				// the number has a divisor other
				// than 1 or itself, we return immediately.
				return false;
			}
		}
		return true;
	}

}
