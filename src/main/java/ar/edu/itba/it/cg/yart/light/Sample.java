package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class Sample {

	public Point3d point;
	public Vector3d normal;
	
	public Sample(final Point3d point, final Vector3d normal) {
		this.point = point;
		this.normal = normal;
	}
}
