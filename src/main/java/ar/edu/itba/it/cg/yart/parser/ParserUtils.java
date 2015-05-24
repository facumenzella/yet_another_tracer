package ar.edu.itba.it.cg.yart.parser;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
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
	
	public static Color[] parseColorArray(final String[] array) {
		if (array.length % 3 != 0) {
			throw new IllegalArgumentException("Color properties need multiple of 3 values, found " + array.length);
		}
		Color[] ret = new Color[array.length / 3];
		for (int i = 0; i < array.length; i += 3) {
			ret[i / 3] = new Color(Double.valueOf(array[i]), Double.valueOf(array[i+1]), Double.valueOf(array[i+2]));
		}
		
		return ret;
	}
	
	public static Point3d[] parsePointArray(final String[] array) {
		if (array.length % 3 != 0) {
			throw new IllegalArgumentException("Point properties need multiple of 3 values, found " + array.length);
		}
		Point3d[] ret = new Point3d[array.length / 3];
		for (int i = 0; i < array.length; i += 3) {
			ret[i / 3] = new Point3d(Double.valueOf(array[i]), Double.valueOf(array[i+1]), Double.valueOf(array[i+2]));
		}
		
		return ret;
	}
	
	public static Vector3d[] parseVectorArray(final String[] array) {
		if (array.length % 3 != 0) {
			throw new IllegalArgumentException("Vector properties need multiple of 3 values, found " + array.length);
		}
		Vector3d[] ret = new Vector3d[array.length / 3];
		for (int i = 0; i < array.length; i += 3) {
			ret[i / 3] = new Vector3d(Double.valueOf(array[i]), Double.valueOf(array[i+1]), Double.valueOf(array[i+2]));
		}
		
		return ret;
	}

}
