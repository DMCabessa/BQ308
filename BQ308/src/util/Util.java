package util;
import java.util.Random;

public class Util {

	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	/**
	 * Returns terminal number of n recursively.
	 * <code>terminal(n) = n + (n-1) + (n-2) + ... + 1</code>
	 * 
	 * @param n value
	 * @return Terminal integer
	 */
	public static int terminal(int n){
		if(n <= 1) return 1;
		else return n + terminal(n-1);
	}

	/**
	 * Returns the equivalent char to an integer i.
	 * If the character is out of [A-Z] range, then return '?'.
	 * 
	 * @param i integer
	 * @return char in [A-Z]
	 */
	public static char toChar(int i){
		int converted = i+65;
		return converted <= 90 && converted >= 65 ? (char) (i + 65) : '?';
	}
}
