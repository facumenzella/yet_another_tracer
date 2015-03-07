package ar.edu.itba.it.cg.yart.color;

public class Color {
	
	public float r;
	public float g;
	public float b;
	public float a;
	
	public Color(final float r, final float g, final float b) {
		this(r, g, b, 1.0f);
	}
	
	public Color(final float r, final float g, final float b, final float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public int toInt() {
		int ret = 0;
		int alpha = (int) (a * 255);
		int red = (int) (r * 255);
		int green = (int) (g * 255);
		int blue = (int) (b * 255);
		
		ret = alpha << 24 | red << 16 | green << 8 | blue;
		
		return ret;
	}
	
	@Override
	public String toString() {
		return "(r: " + r + ", g: " + g + ", b: " + b + ", a: " + a + ")";
	}

}
