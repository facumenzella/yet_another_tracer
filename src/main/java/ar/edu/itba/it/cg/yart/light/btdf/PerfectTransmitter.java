package ar.edu.itba.it.cg.yart.light.btdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class PerfectTransmitter extends BTDF {

	private Texture kt;
	private double ior;
		
	public void setIor(final double ior) {
		this.ior = ior;
	}

	public void setKt(final double kt) {
		final Color ktColor = new Color(kt);
		setKt(ktColor);
	}
	
	public void setKt(final Color kt) {
		final Texture ktTexture = new ConstantColor(kt);
		setKt(ktTexture);
	}
	
	public void setKt(final Texture kt) {
		this.kt = kt;
	}

	@Override
	public Color f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return Color.blackColor();
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		return Color.blackColor();
	}

	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wt) {
		MutableVector3d n = new MutableVector3d(sr.normal);
		double cosThetai = n.dot(wo);
		double eta = ior;

		if (cosThetai < 0.0) {
			cosThetai = -cosThetai;
			n.inverse();
			eta = 1.0/eta;
		}
		double etaSquared = eta*eta;
		double temp = 1.0 - (1.0 - cosThetai * cosThetai) / etaSquared;
		double cosTheta2 = Math.sqrt(temp);
		
		MutableVector3d mWo = new MutableVector3d(wo);
		
		mWo.inverse();
		mWo.scale(1.0 / eta);
		n.scale(cosTheta2 - cosThetai / eta);
		mWo.sub(n);
		wt.copy(mWo);
		
		final double aux = Math.abs(sr.normal.dot(wt));
		final Color c1 = Color.whiteColor();
		c1.multiplyEquals(kt.getColor(sr).multiply(1/etaSquared));
		c1.multiplyEquals(1/aux);
		return c1;
	}

	@Override
	public boolean tir(ShadeRec sr) {
		final double dx = -sr.ray.direction[0];
		final double dy = -sr.ray.direction[1];
		final double dz = -sr.ray.direction[2];

		final Vector3d wo = new Vector3d(dx, dy, dz);
		final double cosThetai = sr.normal.dot(wo);
		double eta = ior;
		if (cosThetai < 0.001) {
			eta = 1 / eta;	
		}
		final double aux = 1.0 - (1.0 - cosThetai * cosThetai)/(eta * eta);
		return (aux < 0.001);
	}

}
