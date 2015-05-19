package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;
import ar.edu.itba.it.cg.yart.transforms.Transformable;

public class AABB implements Transformable{
	
	private static final double EPSILON = 0.0001;
	public Point3d p0;
	public Point3d p1;
	public final double surfaceArea;
	
	public AABB(final Point3d p0, final Point3d p1) {
		this.p0 = p0;
		this.p1 = p1;
		
		final double bottomAndTopArea = (p1.x - p0.x) * Math.abs(p0.z - p1.z) * 2;
		final double sidesArea = Math.abs(p0.z - p1.z) * (p1.y - p0.y) * 4;
		this.surfaceArea = bottomAndTopArea + sidesArea;
	}
	
	public boolean hit(final Ray ray) {
		double ox = ray.origin.x;
		double oy = ray.origin.y;
		double oz = ray.origin.z;
		double dx = ray.direction.x;
		double dy = ray.direction.y;
		double dz = ray.direction.z;

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
		if (b >= 0) {
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

		// Find largest entering t value

		if (tx_min > ty_min)
			t0 = tx_min;
		else
			t0 = ty_min;

		if (tz_min > t0)
			t0 = tz_min;

		// Find smallest exiting t value

		if (tx_max < ty_max)
			t1 = tx_max;
		else
			t1 = ty_max;

		if (tz_max < t1)
			t1 = tz_max;

		return (t0 < t1 && t1 > EPSILON);
	}

	public boolean boxIsInside(final AABB box) {
		if ( (box.p0.x >= this.p0.x && box.p0.z <= this.p0.z) || (box.p1.x <= this.p1.x && box.p1.y <= this.p1.y) ) {
			return true;
		}
		return false;
	}
	
	public double getSurfaceArea() {
		return this.surfaceArea;
	}

	@Override
	public void applyTransformation(Matrix4d matrix) {
		p0 = p0.transformByMatrix(matrix);
		p1 = p1.transformByMatrix(matrix);
	}
	
}
