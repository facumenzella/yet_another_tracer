package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3;

public class Ray {
	
	public Point3 origin;
	public Vector3 direction;
	
	public Ray(final Point3 origin, final Vector3 direction) {
		this.origin = origin;
		this.direction = direction;
	}

}
