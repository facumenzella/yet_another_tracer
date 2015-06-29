package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.samplers.Sampler;

public class GlossySpecular extends BRDF{
	
	private double ks;
	private double cs;
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
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi, final PDF pdf) {
		double ndotwo = sr.normal.dot(wo);
		
		final double srx = sr.normal.x * ndotwo * 2.0;
		final double sry = sr.normal.y * ndotwo * 2.0;
		final double srz = sr.normal.z * ndotwo * 2.0;
		double wox = -wo.x;
		double woy = -wo.y;
		double woz = -wo.z;
		wox += srx;
		woy += sry;
		woz += srz;
		Vector3d r = new Vector3d(wox, woy, woz); // direction of mirror reflection
		
		Vector3d w = r;								
		Vector3d u = new Vector3d(0.00424, 1, 0.00764).cross(w); 
		u.normalizeMe();
		Vector3d v = u.cross(w);
			
		Point3d sp = sampler.sampleHemisphere();
		wi.copy(u.scale(sp.x).add(v.scale(sp.y)).add(w.scale(sp.z))); // reflected ray direction
		
		if (sr.normal.dot(wi) < 0.0) { // reflected ray is below tangent plane
			wi.copy(u.scale(-sp.x).add(v.scale(-sp.y)).add(w.scale(-sp.z)));
		}
			
		final double phong_lobe = Math.pow(r.dot(wi), exp);
		pdf.pdf = phong_lobe * (sr.normal.dot(wi));

		final double aux = ks * cs * phong_lobe;
		return new Color(aux);
	}
	
	public void setExp(final double exp) {
		this.exp = exp;
	}
	
	public void setCs(final double cs) {
		this.cs = cs;
	}
	
	public void setKs(final double ks) {
		this.ks = ks;
	}
	
	public void setSampler(final Sampler sampler) {
		super.setSampler(sampler);
		sampler.mapSamples2Hemisphere(exp);
	}

}
