package ar.edu.itba.it.cg.yart.parser;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.light.materials.Matte;

public class ParserUtils {
	
	public static Material defaultMaterial = new Matte().setCd(new Color(0.75, 0.75, 0.75)).setKd(0.5).setKa(0.15);
	
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
