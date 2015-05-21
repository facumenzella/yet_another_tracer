package ar.edu.itba.it.cg.yart.color;

public class Color {
	
	public double r;
	public double g;
	public double b;
	public double a;
	
	private static Color BLACK = new Color(0,0,0,1.0);
	private static Color RED = new Color(1.0,0,0,1.0);
	private static Color GREEN = new Color(0,1.0,0,1.0);
	private static Color BLUE = new Color(0,0,1.0,1.0);
	private static Color YELLOW = new Color(1.0,1.0,0,1.0);
	private static Color WHITE = new Color(1.0,1.0,1.0,1.0);
	
	public Color(final double r, final double g, final double b) {
		this(r, g, b, 1.0);
	}
	
	public Color(final Color c) {
		this.r = c.r;
		this.g = c.g;
		this.b = c.g;
		this.a = c.a;
	}
	
	public Color(final double r, final double g, final double b, final double a) {
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
	
	public Color multiply(final double f) {
		return new Color(this.r*f, this.g*f, this.b*f);
	}
	
	public Color multiply(final Color f) {
		return new Color(this.r*f.r, this.g*f.g, this.b*f.b);
	}
	
	public void multiplyEquals(final Color f) {
		if (f != null) {
			this.r = this.r * f.r;
			this.g = this.g * f.g;
			this.b = this.b * f.b;
		}
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
		return WHITE;
	}
	
	public static Color blackColor() {
		return BLACK;
	}
	
	public static Color redColor() {
		return RED;
	}
	
	public static Color greenColor() {
		return GREEN;
	}
	
	public static Color blueColor() {
		return BLUE;
	}
	
	public static Color yellowColor() {
		return YELLOW;
	}
	
	@Override
	public String toString() {
		return "(r: " + r + ", g: " + g + ", b: " + b + ", a: " + a + ")";
	}

}
