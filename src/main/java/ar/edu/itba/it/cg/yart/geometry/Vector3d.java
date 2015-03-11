package ar.edu.itba.it.cg.yart.geometry;

public final class Vector3d {

	public final double x;
	public final double y;
	public final double z;
	public final double length;
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = this.length();
	}
	
	public static Vector3d xAxis() {
		return new Vector3d(1, 0, 0);
	}
	
	public static Vector3d yAxis() {
		return new Vector3d(0, 1, 0);
	}
	
	public static Vector3d zAxis() {
		return new Vector3d(0, 0, 1);
	}
	
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector3d direction() {
		return scale(1/length());
	}
	
	public Vector3d inverse() {
		return new Vector3d(-x, -y, -z);
	}
	
	public Vector3d scale(final double factor) {
		return new Vector3d(x * factor, y * factor, z * factor);
	}
	
	public Vector3d add(final Vector3d other) {
		return new Vector3d(x + other.x, y + other.y, z + other.z);
	}
	
	public Vector3d sub(final Vector3d other) {
		return new Vector3d(x - other.x, y - other.y, z - other.z);
	}
	
	public double dot(final Vector3d other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public Vector3d cross(final Vector3d other) {
		return new Vector3d(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
	}
	
	public double angleWith(final Vector3d other) {
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
		Vector3d other = (Vector3d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
}
