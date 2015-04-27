package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class GlossySpecular extends BRDF{
	
	private double ks;
	private Color cs;
	private double exp;
	
	private Color rho = Color.blackColor();
	private Color sample_f = Color.blackColor();
	
	@Override
	public Color f(final ShadeRec sr, final Vector3d wo, final Vector3d wi) {
		MutableVector3d mNormalSR = new MutableVector3d(sr.normal);
		MutableVector3d mWi = new MutableVector3d(wi);
		mWi.inverse();
		
		Color L = null;
		final double NdotWi = sr.normal.dot(wi);
		mNormalSR.scale(2.0);
		mNormalSR.scale(NdotWi);
		mWi.add(mNormalSR);
		final double rdotWo = mWi.dot(wo);
		
		if(rdotWo >	0.0) {
			final double aux = ks* Math.pow(rdotWo, exp);
			L = new Color(aux,aux,aux);
		}
		return L;
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		return this.rho;
	}
	
	public Color mRho(ShadeRec sr, Vector3d wo) {
		return Color.blackColor();
	}
	
	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return this.sample_f;
	}
	
	public Color mSample_f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return Color.blackColor();
	}
	
	public void setExp(final double exp) {
		this.exp = exp;
	}
	
	public void setCs(final Color color) {
		this.cs = color;
	}
	
	public void setKs(final double ks) {
		this.ks = ks;
	}

}
