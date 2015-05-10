package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class Point3d {
	
	public double x;
	public double y;
	public double z;
	
	public Point3d(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3d sub(final Point3d other) {
		return new Vector3d(x - other.x, y - other.y, z - other.z);
	}
	
	public MutableVector3d mutableSub(final Point3d other) {
		return new MutableVector3d(x - other.x, y - other.y, z - other.z);
	}
	
	public Point3d add(final Point3d other) {
		return new Point3d(x + other.x, y + other.y, z + other.z);
	}
	
	public Point3d add(final Vector3d other) {
		return new Point3d(x + other.x, y + other.y, z + other.z);
	}
	
	public Point3d add(final MutableVector3d other) {
		return new Point3d(x + other.x, y + other.y, z + other.z);
	}
	
	public double distance(final Point3d other) {
		return this.sub(other).length;
	}
	
	public void copy(final Point3d p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}

	public Point3d transformByMatrix(final Matrix4d matrix) {
		final double dx = (matrix.m00 * this.x) + (matrix.m01 * this.y) + (matrix.m02 * this.z) + matrix.m03;
		final double dy = (matrix.m10 * this.x) + (matrix.m11 * this.y) + (matrix.m12 * this.z) + matrix.m13;
		final double dz = (matrix.m20 * this.x) + (matrix.m21 * this.y) + (matrix.m22 * this.z) + matrix.m23;
		return new Point3d(dx, dy, dz);
	}
	
}
