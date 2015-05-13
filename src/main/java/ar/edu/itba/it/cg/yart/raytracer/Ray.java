package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class Ray {
	
	public Point3d origin;
	public Vector3d direction;
	public int depth = 0;
	
	public Ray(final Point3d origin, final Vector3d direction) {
		this.origin = origin;
		this.direction = direction;
	}
	
	public Ray(final Point3d origin) {
		this.origin = origin;
	}

}
