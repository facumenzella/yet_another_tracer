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

	public double getKd() {
		return kd;
	}

	public void setKd(double kd) {
		this.kd = kd;
	}

	public int getCd() {
		return cd;
	}

	public void setCd(int cd) {
		this.cd = cd;
	}
	
	
}
