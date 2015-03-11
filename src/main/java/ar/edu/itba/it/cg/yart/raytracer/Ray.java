package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class Ray {
	
	public Point3 origin;
	public Vector3d direction;
	
	public Ray(final Point3 origin, final Vector3d direction) {
		this.origin = origin;
		this.direction = direction;
	}
	
	public Ray(final Point3 origin) {
		this.origin = origin;
	}

}
