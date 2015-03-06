package ar.edu.itba.it.cg.yart.math;

public class Vector3 {

	public double x;
	public double y;
	public double z;
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Vector3 xAxis() {
		return new Vector3(1, 0, 0);
	}
	
	public static Vector3 yAxis() {
		return new Vector3(0, 1, 0);
	}
	
	public static Vector3 zAxis() {
		return new Vector3(0, 0, 1);
	}
	
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector3 direction() {
		return scale(1/length());
	}
	
	public Vector3 inverse() {
		return new Vector3(-x, -y, -z);
	}
	
	public Vector3 scale(final double factor) {
		return new Vector3(x * factor, y * factor, z * factor);
	}
	
	public Vector3 add(final Vector3 other) {
		return new Vector3(x + other.x, y + other.y, z + other.z);
	}
	
	public Vector3 sub(final Vector3 other) {
		return new Vector3(x - other.x, y - other.y, z - other.z);
	}
	
	public double dot(final Vector3 other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public Vector3 cross(final Vector3 other) {
		return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
	}
	
	public double angleWith(final Vector3 other) {
		return Math.acos(this.dot(other) / (this.length() * other.length()));
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3 other = (Vector3) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
}
