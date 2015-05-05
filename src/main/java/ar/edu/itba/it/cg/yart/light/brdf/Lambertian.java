package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Lambertian extends BRDF {

	private double kd = 0;
	private Color cd;
	private final double invPi = 1 / Math.PI;

	private Color f;
	private Color rho;
	private Color sample_f = Color.blackColor();
	
	@Override
	public Color f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return f;
	}

	public Color mF(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return this.rho.multiply(invPi);
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		return this.rho;
	}

	public Color mRho(ShadeRec sr, Vector3d wo) {
		return cd.multiply(kd);
	}

	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return this.sample_f;
	}

	public Color mSample_f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return new Color(this.sample_f);
	}

	public double getKd() {
		return kd;
	}

	public void setKd(final double kd) {
		this.kd = kd;
		if (cd != null) {
			this.rho = this.mRho(null, null);
			this.f = this.mF(null, null, null);
		}
	}

	public Color getCd() {
		return cd;
	}

	public void setCd(final Color cd) {
		this.cd = cd;
		this.rho = this.mRho(null, null);
		this.f = this.mF(null, null, null);
	}

}
