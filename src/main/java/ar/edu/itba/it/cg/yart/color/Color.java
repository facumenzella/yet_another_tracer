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
	
	public static Color blackColor() {
		return new Color(255,255,255,0);
	}
	
	public static Color whiteColor() {
		return new Color(0,0,0,0);
	}
	
	public static Color redColor() {
		return new Color(255,0,0,0);
	}
	
	public static Color greenColor() {
		return new Color(0,255,0,0);
	}
	
	public static Color blueColor() {
		return new Color(0,0,255,0);
	}
	
	public static Color yellowColor() {
		return new Color(255,255,0,0);
	}
	
	@Override
	public String toString() {
		return "(r: " + r + ", g: " + g + ", b: " + b + ", a: " + a + ")";
	}

}
