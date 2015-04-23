package ar.edu.itba.it.cg.yart.geometry;

public class MutableVector3d {
	public double x;
	public double y;
	public double z;
	public double length;
	public Vector3d normalized;
	
	public MutableVector3d(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3d inmutableCopy() {
		return new Vector3d(this.x, this.y, this.z);
	}
	
	public double dot(final Vector3d other) {
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
	
	public void sub(final Point3 other) {
		this.x = this.x - other.x;
		this.y = this.y - other.y;
		this.z = this.z - other.z;
	}
	
	public void add(final double n) {
		this.x += n;
		this.y += n;
		this.z += n;
	}
	
	public void add(final Vector3d other) {
		this.x = x + other.x;
		this.y = y + other.y;
		this.z = z + other.z;
	}
	
}
