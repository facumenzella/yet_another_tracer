package ar.edu.itba.it.cg.yart.utils;

public final class MathOperations {

	public static double lerp(final double a, final double b, final double t) {
		return (1 - t) * a + (t * b);
	}
	
}
