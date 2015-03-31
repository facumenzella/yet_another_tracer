package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

public class BoundingBox {
	
	private static final double EPSILON = 0.0001;
	public Point3 p0;
	public Point3 p1;
	
	public BoundingBox(final Point3 p0, final Point3 p1) {
		this.p0 = p0;
		this.p1 = p1;
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

	public boolean boxIsInside(final BoundingBox box) {
		if ( (box.p0.x >= this.p0.x && box.p0.z <= this.p0.z) || (box.p1.x <= this.p1.x && box.p1.y <= this.p1.y) ) {
			return true;
		}
		return false;
	}
	
}
