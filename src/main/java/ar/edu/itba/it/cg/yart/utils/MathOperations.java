package ar.edu.itba.it.cg.yart.utils;

public final class MathOperations {

	public static int lerp(final int a, final int b, final int t) {
		return (1 - t) * a + (t * b);
	}
	
}
