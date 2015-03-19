package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class GlossySpecular extends BRDF{
	
	private double ks;
	private Color cs;
	private double exp;
	
	@Override
	public Color f(final ShadeRec sr, final Vector3d wo, final Vector3d wi) {
		Color L = null;
		final double NdotWi = sr.normal.dot(wi);
		final Vector3d r = wi.inverse().add(sr.normal.scale(2.0).scale(NdotWi));
		final double rdotWo = r.dot(wo);
		
		if(rdotWo >	0.0) {
			final double aux = ks* Math.pow(rdotWo, exp);
			L = new Color(aux,aux,aux);
		}
		return L;
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
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
