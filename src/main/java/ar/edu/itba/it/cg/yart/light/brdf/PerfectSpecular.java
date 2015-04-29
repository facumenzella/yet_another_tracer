package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class PerfectSpecular extends BRDF {
	
	double kr;
	Color cr;

	@Override
	public Color f(final ShadeRec sr, final Vector3d wo, final Vector3d wi) {
		return Color.blackColor();
	}

	@Override
	public Color rho(final ShadeRec sr, final Vector3d wo) {
		return Color.blackColor();
	}

	@Override
	public Color sample_f(final ShadeRec sr, final Vector3d wo, Vector3d wi) {
		final double ndotwo = sr.normal.dot(wo);
		MutableVector3d mSR = new MutableVector3d(sr.normal);
		MutableVector3d mWo = new MutableVector3d(wo);

		mSR.scale(ndotwo);
		mSR.scale(2.0);
		
		mWo.inverse();
		mWo.add(mSR);
		
		wi.copy(mWo); 
		final double aux = sr.normal.dot(wi);
		return (cr.multiply(kr).multiply(1/aux));
	}
	
	public void setCr(final Color color) {
		this.cr = color;
	}
	
	public void setKr(final double kr) {
		this.kr = kr;
	}

}
