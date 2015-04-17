package ar.edu.itba.it.cg.yart.light.btdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class PerfectTransmitter extends BTDF {

	private double kt;
	private double ior;

	public void setIor(final double ior) {
		this.ior = ior;
	}

	public void setKt(final double kt) {
		this.kt = kt;
	}

	@Override
	public Color f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		// TODO Auto-generated method stub
		return Color.blackColor();
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		// TODO Auto-generated method stub
		return Color.blackColor();
	}

	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wt) {
		Vector3d n = sr.normal;
		double cosThetai = n.dot(wo);
		double eta = ior;

		if (cosThetai < 0.0) {
			cosThetai = -cosThetai;
			n = n.inverse();
			eta = 1.0/eta;
		}
		double etaSquared = eta*eta;
		double temp = 1.0 - (1.0 - cosThetai * cosThetai)/etaSquared;
		double cosTheta2 = Math.sqrt(temp);
		wt.copy(wo.inverse().scale(1.0 / eta)
				.sub(n.scale(cosTheta2 - cosThetai / eta)));
		final double aux = Math.abs(sr.normal.dot(wt));
		return (Color.whiteColor().multiply(kt/etaSquared))
				.multiply(1.0/aux);
	}

	@Override
	public boolean tir(ShadeRec sr) {
		final Vector3d wo = sr.ray.direction.inverse();
		final double cosThetai = sr.normal.dot(wo);
		double eta = ior;
		if (cosThetai < 0.0) {
			eta = 1.0 / eta;
		}
		final double aux = 1.0 - (1.0 - cosThetai * cosThetai)/(eta * eta);
		return (aux < 0.0);
	}

}
