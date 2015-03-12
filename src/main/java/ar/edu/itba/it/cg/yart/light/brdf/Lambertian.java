package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Lambertian extends BRDF {
	
	private double kd;
	private int cd;
	private final double invPi = 1/Math.PI;

	@Override
	public double f(ShadeRec sr, Vector3d wi, Vector3d wo) {
		return kd*cd*invPi;
	}

	@Override
	public double rho(ShadeRec sr, Vector3d wi, Vector3d wo) {
		return kd*cd;
	}
	
}
