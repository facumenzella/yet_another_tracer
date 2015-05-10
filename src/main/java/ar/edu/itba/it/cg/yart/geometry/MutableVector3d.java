package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class MutableVector3d {
	public double x;
	public double y;
	public double z;
	public double length;
	
	public MutableVector3d(final Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.length = Math.sqrt(x * x + y * y + z * z);
	}
	
	public MutableVector3d(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3d inmutableCopy() {
		return new Vector3d(this.x, this.y, this.z);
	}
	
	public void copy(final Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public void normalize() {
		this.length = Math.sqrt(x * x + y * y + z * z);
		this.x = this.x / length;
		this.y = this.y / length;
		this.z = this.z / length;
	}
	
	public double dot(final Vector3d other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public double dot(final MutableVector3d other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public void inverse() {
		this.x = -x;
		this.y = -y;
		this.z = -z;
	}
	
	public void scale(final double factor) {
		this.x = this.x * factor;
		this.y = this.y * factor;
		this.z = this.z * factor;
	}
	
	public void cross(final Vector3d other) {
		this.x = y * other.z - this.z * other.y;
		this.y = z * other.x - this.x * other.z;
		this.z = x * other.y - this.y * other.x;
	}
	
	public void sub(final Point3d other) {
		this.x = this.x - other.x;
		this.y = this.y - other.y;
		this.z = this.z - other.z;
	}
	
	public void sub(final MutableVector3d other) {
		this.x = x - other.x;
		this.y = y - other.y;
		this.z = z - other.z;
	}
	
	public void add(final double n) {
		this.x += n;
		this.y += n;
		this.z += n;
	}
	
	public void add(final MutableVector3d other) {
		this.x = x + other.x;
		this.y = y + other.y;
		this.z = z + other.z;
	}
	
	public void add(final Vector3d other) {
		this.x = x + other.x;
		this.y = y + other.y;
		this.z = z + other.z;
	}
	
	public void transformByMatrix(final Matrix4d matrix) {
		final double dx = (matrix.m00 * this.x) + (matrix.m01 * this.y) + (matrix.m02 * this.z);
		final double dy = (matrix.m10 * this.x) + (matrix.m11 * this.y) + (matrix.m12 * this.z);
		final double dz = (matrix.m20 * this.x) + (matrix.m21 * this.y) + (matrix.m22 * this.z);
		this.x = dx;
		this.y = dy;
		this.z = dz;
	}
}
