package ar.edu.itba.it.cg.yart.parser;

public class ParserUtils {
	
	public static double[] parseDoubleArray(final String[] array) {
		double[] ret = new double[array.length];
		int i = 0;
		
		try {
			for (i = 0; i < array.length; i++) {
				ret[i] = Double.valueOf(array[i]);
			}
		}
		catch (NumberFormatException e) {
			throw new NumberFormatException("Invalid number \"" + array[i] + "\"");
		}
		
		return ret;
	}
	
	public static int[] parseIntegerArray(final String[] array) {
		int[] ret = new int[array.length];
		int i = 0;
		
		try {
			for (i = 0; i < array.length; i++) {
				ret[i] = Integer.valueOf(array[i]);
			}
		}
		catch (NumberFormatException e) {
			throw new NumberFormatException("Invalid number \"" + array[i] + "\"");
		}
		
		return ret;
	}
	
	public static boolean[] parseBooleanArray(final String[] array) {
		boolean[] ret = new boolean[array.length];
		int i = 0;
		
		try {
			for (i = 0; i < array.length; i++) {
				ret[i] = Boolean.valueOf(array[i]);
			}
		}
		catch (NumberFormatException e) {
			throw new NumberFormatException("Invalid boolean \"" + array[i] + "\"");
		}
		
		return ret;
	}

}
