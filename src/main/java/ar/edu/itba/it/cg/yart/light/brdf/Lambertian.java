package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Lambertian extends BRDF {
	
	private double kd;
	private Color cd;
	private final double invPi = 1/Math.PI;

	@Override
	public Color f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return cd.multiply(kd).multiply(invPi);
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		return cd.multiply(kd);
	}

	public double getKd() {
		return kd;
	}

	public void setKd(final double kd) {
		this.kd = kd;
	}

	public Color getCd() {
		return cd;
	}

	public void setCd(final Color cd) {
		this.cd = cd;
	}

	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return Color.blackColor();
	}
	
	
}
