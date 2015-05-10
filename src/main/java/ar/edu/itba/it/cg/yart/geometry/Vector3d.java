package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class Vector3d {


	public double x;
	public double y;
	public double z;
	public double length;
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector3d(double x, double y, double z, double length) {
		this.length = length;
		this.x = x / length;
		this.y = y / length;
		this.z = z / length;
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
	
	public Vector3d direction() {
		return scale(1/length);
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
	
	public Vector3d add(final double n) {
		this.x += n;
		this.y += n;
		this.z += n;
		return this;
	}
	
	public Vector3d sub(final Vector3d other) {
		return new Vector3d(x - other.x, y - other.y, z - other.z);
	}
	
	public Vector3d sub(final Point3d other) {
		return new Vector3d(x - other.x, y - other.y, z - other.z);
	}
	
	public double dot(final Vector3d other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public Vector3d cross(final Vector3d other) {
		return new Vector3d(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
	}
	
	public double angleWith(final Vector3d other) {
		return Math.acos(this.dot(other) / (this.length * other.length));
	}
	
	public Normal3d normalizedVector() {
		Normal3d normal = new Normal3d();
		normal.x = this.x / this.length;
		normal.y = this.y / this.length;
		normal.z = this.z / this.length;
		return normal;
	}
	
	public void copy(final Vector3d other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.length = other.length;
	}
	
	public void copy(final MutableVector3d other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.length = other.length;
	}
	
	public Vector3d transformByMatrix(final Matrix4d matrix) {
		final double dx = (matrix.m00 * this.x) + (matrix.m01 * this.y) + (matrix.m02 * this.z);
		final double dy = (matrix.m10 * this.x) + (matrix.m11 * this.y) + (matrix.m12 * this.z);
		final double dz = (matrix.m20 * this.x) + (matrix.m21 * this.y) + (matrix.m22 * this.z);
		return new Vector3d(dx, dy, dz);
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
