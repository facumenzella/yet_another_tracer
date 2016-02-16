package ar.edu.itba.it.cg.yart.color;

public class Color {
	
	public double r;
	public double g;
	public double b;
	public double a;
	
	public static final Color BLACK =	new Color(0.0, 0.0, 0.0);
	public static final Color RED =		new Color(1.0, 0.0, 0.0);
	public static final Color GREEN =	new Color(0.0, 1.0, 0.0);
	public static final Color BLUE =	new Color(0.0, 0.0, 1.0);
	public static final Color YELLOW =	new Color(1.0, 1.0, 0.0);
	public static final Color WHITE =	new Color(1.0, 1.0, 1.0);
	
	public Color(final double c) {
		this(c,c,c);
	}
	
	public Color(final double r, final double g, final double b) {
		this(r, g, b, 1.0);
	}
	
	public Color(final Color c) {
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
		this.a = c.a;
	}
	
	public Color(final double r, final double g, final double b, final double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void set(final Color other) {
		this.r = other.r;
		this.g = other.g;
		this.b = other.b;
		this.a = other.a;
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
	
	public Color multiply(final double f) {
		return new Color(this.r*f, this.g*f, this.b*f);
	}
	
	public Color multiply(final Color f) {
		return new Color(this.r*f.r, this.g*f.g, this.b*f.b);
	}
	
	public Color multiplyEquals(final Color f) {
		if (f != null) {
			this.r = this.r * f.r;
			this.g = this.g * f.g;
			this.b = this.b * f.b;
		}
		return this;
	}
	
	public Color multiplyEquals(final double f) {
		this.r*=f;
		this.g*=f;
		this.b*=f;
		return this;
	}
	
	public Color add(final Color c) {
		if(c != null){
			return new Color(this.r + c.r, this.g + c.g, this.b + c.b);
		}
		return this;
	}
	
	public void addEquals(final Color color) {
		if (color != null) {
			this.r+= color.r;
			this.g+= color.g;
			this.b+= color.b;
		}
	}
	
	public void copy(final Color color) {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	}
	
	public static Color whiteColor() {
		return new Color(1.0,1.0,1.0,1.0);
	}
	
	public static Color blackColor() {
		return new Color(0,0,0,1.0);
	}
	
	public static Color redColor() {
		return new Color(1.0,0,0,1.0);
	}
	
	public static Color greenColor() {
		return new Color(0,1.0,0,1.0);
	}
	
	public static Color blueColor() {
		return new Color(0,0,1.0,1.0);
	}
	
	public static Color yellowColor() {
		return new Color(1.0,1.0,0,1.0);
	}
	
	public Color complementMe() {
		this.r = 1 - r;
		this.g= 1 - g;
		this.b = 1 - b;
		return this;
	}
	
	public Color complement() {
		return new Color(1-r, 1-g, 1- b);
	}
	
	public void correctColor() {
		double max = Math.max(r, Math.max(g, b));
		if (max > 1.0) {
			r /= max;
			g /= max;
			b /= max;
		}
	}
	
	@Override
	public String toString() {
		return "(r: " + r + ", g: " + g + ", b: " + b + ", a: " + a + ")";
	}

}
