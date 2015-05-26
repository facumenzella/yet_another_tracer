package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class GlossySpecular extends BRDF{
	
	private double ks;
	private double exp;
	
	@Override
	public Color f(final ShadeRec sr, final Vector3d wo, final Vector3d wi) {		
		double wix = -wi.x;
		double wiy = -wi.y;
		double wiz = -wi.z;
		
		Color L = null;
		final double NdotWi = sr.normal.dot(wi);
		
		final double nx = sr.normal.x * 2 * NdotWi;
		final double ny = sr.normal.y * 2 * NdotWi;
		final double nz = sr.normal.z * 2 * NdotWi;
		
		wix += nx;
		wiy += ny;
		wiz += nz;

		final double rdoWox = wix * wo.x;
		final double rdoWoy = wiy * wo.y;
		final double rdoWoz = wiz * wo.z;

		final double rdotWo = rdoWox + rdoWoy + rdoWoz;
		
		if(rdotWo >	0.0) {
			final double aux = ks* Math.pow(rdotWo, exp);
			L = new Color(aux,aux,aux);
		} else {
			return new Color(0, 0, 0);
		}
		return L;
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		return Color.blackColor();
	}
	
	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return Color.blackColor();
	}
	
	public void setExp(final double exp) {
		this.exp = exp;
	}
	
	public void setKs(final double ks) {
		this.ks = ks;
	}

}
