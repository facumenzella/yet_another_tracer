package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class Ray {
	
	public Point3d origin;
	public double[] direction;
	public int depth = 0;
	
	public Ray() {};
	
	public Ray(final Point3d origin, final Vector3d direction) {
		this.origin = origin;
		this.direction = new double[3];
		this.direction[0] = direction.x;
		this.direction[1] = direction.y;
		this.direction[2] = direction.z;
	}
	
	public Ray(final Point3d origin) {
		this.origin = origin;
		this.direction = new double[3];
	}

}