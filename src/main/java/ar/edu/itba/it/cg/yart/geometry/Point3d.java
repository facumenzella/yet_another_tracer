package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class Point3d {

	public double x;
	public double y;
	public double z;
	public double w;
	
	public Point3d(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
	}
	
	public Vector3d sub(final Point3d other) {
		return new Vector3d(x - other.x, y - other.y, z - other.z);
	}
	
	public Point3d add(final Point3d other) {
		return new Point3d(x + other.x, y + other.y, z + other.z);
	}
	
	public Point3d add(final Vector3d other) {
		return new Point3d(x + other.x, y + other.y, z + other.z);
	}
	
	public double distance(final Point3d other) {
		return this.sub(other).length;
	}
	
	public void copy(final Point3d p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
		this.w = p.w;
	}

	public Point3d transformByMatrix(final Matrix4d matrix) {
		final double dx = (matrix.m00 * this.x) + (matrix.m01 * this.y) + (matrix.m02 * this.z) + matrix.m03;
		final double dy = (matrix.m10 * this.x) + (matrix.m11 * this.y) + (matrix.m12 * this.z) + matrix.m13;
		final double dz = (matrix.m20 * this.x) + (matrix.m21 * this.y) + (matrix.m22 * this.z) + matrix.m23;
		final double dw = (matrix.m30 * this.x) + (matrix.m31 * this.y) + (matrix.m32 * this.z) + matrix.m33;
		return new Point3d(dx, dy, dz);
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
		Point3d other = (Point3d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
	
}
