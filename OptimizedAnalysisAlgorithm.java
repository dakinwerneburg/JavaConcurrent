
/**
 * A slightly optimized version of the analysis algorithm that checks for a few
 * simple cases and reduces the search space by half. The code was taken from
 * http://stackoverflow.com/questions/9625663/calculating-and-printing-the-nth-prime-number.
 * More details about the algorithm and how it could be optimized can be found
 * on that page.
 *
 * @author mpilone
 */
public class OptimizedAnalysisAlgorithm implements AnalysisAlgorithm {

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
		if (n % 2 == 0) {
			return n == 2;
		}
		if (n % 3 == 0) {
			return n == 3;
		}
		int step = 4, m = (int) Math.sqrt(n) + 1;
		for (int i = 5; i < m; step = 6 - step, i += step) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

}
