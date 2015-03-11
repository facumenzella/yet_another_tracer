package ar.edu.itba.it.cg.yart.geometry;

public class Point3 {
	
	public double x;
	public double y;
	public double z;
	
	public Point3(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3d sub(final Point3 other) {
		return new Vector3d(x - other.x, y - other.y, z - other.z);
	}

}
