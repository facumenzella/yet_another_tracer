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
		int red = (int) (r * 255);
		int green = (int) (g * 255);
		int blue = (int) (b * 255);
		int alpha = (int) (a * 255);
		
		ret = red << 24 | green << 16 | blue << 8 | alpha;
		
		return ret;
	}
	
	@Override
	public String toString() {
		return "(r: " + r + ", g: " + g + ", b: " + b + ", a: " + a + ")";
	}

}
