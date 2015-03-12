package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public abstract class BRDF {
	
	protected Vector3d normal;
	
	public abstract double f(final ShadeRec sr, final Vector3d wi, final Vector3d wo);
	public abstract double rho(final ShadeRec sr, final Vector3d wi, final Vector3d wo);
}
