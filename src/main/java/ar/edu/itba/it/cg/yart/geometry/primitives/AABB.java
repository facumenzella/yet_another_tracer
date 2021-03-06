package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;
import ar.edu.itba.it.cg.yart.transforms.Transformable;

// Chapter 19 - RTFTGU
public class AABB implements Transformable {

	private static final double EPSILON = 0.0001;
	public Point3d p0;
	public Point3d p1;
	public double surfaceArea;
	
	public AABB(final Point3d p0, final Point3d p1) {
		this.p0 = p0;
		this.p1 = p1;

		refreshArea();
	}

	public boolean hit(final Ray ray) {
		final double ox = ray.origin.x;
		final double dx = ray.direction[0];
		final double oy = ray.origin.y;
		final double dy = ray.direction[1];
		final double oz = ray.origin.z;
		final double dz = ray.direction[2];
		
		double tx_min, ty_min, tz_min;
		double tx_max, ty_max, tz_max;
		
		double a = 1.0 / dx;
		if (a >= 0) {
			tx_min = (p0.x - ox) * a;
			tx_max = (p1.x - ox) * a;
		} else {
			tx_min = (p1.x - ox) * a;
			tx_max = (p0.x - ox) * a;
		}
		
		double b = 1.0 / dy;
		if (b < 0) {
			ty_min = (p0.y - oy) * b;
			ty_max = (p1.y - oy) * b;
		} else {
			ty_min = (p1.y - oy) * b;
			ty_max = (p0.y - oy) * b;
		}
		
		double c = 1.0 / dz;
		if (c >= 0) {
			tz_min = (p0.z - oz) * c;
			tz_max = (p1.z - oz) * c;
		} else {
			tz_min = (p1.z - oz) * c;
			tz_max = (p0.z - oz) * c;
		}
		
		double t0, t1;
		if (tx_min > ty_min) {
			t0 = tx_min;
		} else {
			t0 = ty_min;
		}
		if (tz_min > t0) {
			t0 = tz_min;
		}
		
		if (tx_max < ty_max) {
			t1 = tx_max;
		} else {
			t1 = ty_max;
		}
		
		if (tz_max < t1) {
			t1 = tz_max;
		}
		return t0 < t1 && t1 > EPSILON;
	}

	public boolean intersectsBox(final AABB box) {
		if (this.p0.x > box.p1.x) { return false; }
		if (this.p1.x < box.p0.x) { return false; }
		if (this.p1.z < box.p0.z) { return false; }
		if (this.p0.z > box.p1.z) { return false; }
		if (this.p0.y < box.p1.y) { return false; }
		if (this.p1.y > box.p0.y) { return false; }
		return true;
	}

	public boolean pointIsInside(final Point3d point) {
		if (point.x >= this.p0.x && point.x <= this.p1.x && point.y >= this.p1.y
				&& point.y <= this.p0.y && point.z >= this.p0.z
				&& point.z <= this.p1.z) {
			return true;
		}
		return false;
	}

	public AABB clip(final AABB box) {
		double minX, minY, minZ, maxX, maxY, maxZ;
		minX = Math.max(this.p0.x, box.p0.x);
		minY = Math.max(this.p1.y, box.p1.y);
		minZ = Math.max(this.p0.z, box.p0.z);
		maxX = Math.min(this.p1.x, box.p1.x);
		maxY = Math.min(this.p0.y, box.p0.y);
		maxZ = Math.min(this.p1.z, box.p1.z);

		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY,
				maxZ));
	}

	@Override
	public void applyTransformation(Matrix4d matrix) {
		p0 = p0.transformByMatrix(matrix);
		p1 = p1.transformByMatrix(matrix);
		
		double minX, minY, minZ, maxX, maxY, maxZ;
		minX = Math.min(p0.x, p1.x);
		minY = Math.min(p0.y, p1.y);
		minZ = Math.min(p0.z, p1.z);
		maxX = Math.max(p0.x, p1.x);
		maxY = Math.max(p0.y, p1.y);
		maxZ = Math.max(p0.z, p1.z);
		this.p0.x = minX;
		this.p0.y = maxY;
		this.p0.z = minZ;
		this.p1.x = maxX;
		this.p1.y = minY;
		this.p1.z = maxZ;
		
		refreshArea();
	}
	
	private void refreshArea() {
		final double bottomAndTopArea = Math.abs(p1.x - p0.x) * Math.abs(p0.y - p1.y)
				* 2;
		final double side1 = Math.abs(p0.y - p1.y) * Math.abs(p1.z - p0.z) * 2;
		final double side2 = Math.abs(p0.x - p1.x) * Math.abs(p1.z - p0.z) * 2;
		this.surfaceArea = bottomAndTopArea + side1 + side2;
	}
	
	@Override
	public String toString() {
		return "[" + "x:" + this.p0.x + " y: " + this.p0.y + " z: " + this.p0.z + "] \n" +
				"[" + "x:" + this.p1.x + " y: " + this.p1.y + " z: " + this.p1.z + "]";
	}

}
