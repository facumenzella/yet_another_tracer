package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Lambertian extends BRDF {

	private Texture kd;
	private Texture cd;
	private final double invPi = 1 / Math.PI;
	
	@Override
	public Color f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		final Color c = cd.getColor(sr);
		final Color k = kd.getColor(sr);
		final double r = c.r * k.r * invPi;
		final double g = c.g * k.g * invPi;
		final double b = c.b * k.b * invPi;

		return new Color(r, g, b, c.a);
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		final Color c = cd.getColor(sr);
		final Color k = kd.getColor(sr);
		final double r = c.r * k.r;
		final double g = c.r * k.g;
		final double b = c.r * k.b;

		return new Color(r, g, b, c.a);
	}

	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi, final PDF pdf) {
		final Vector3d w = sr.normal;
		final Vector3d v = new Vector3d(0.034, 1.0, 0.0071).cross(w);
		v.normalizeMe();
		final Vector3d u = v.cross(w);
		Point3d sp = sampler.sampleHemisphere();
		wi.copy(u.scale(sp.x).add(v.scale(sp.y)).add(w.scale(sp.z)));
		
		wi.normalizeMe();
		pdf.pdf = sr.normal.dot(wi) * invPi;
		return 	kd.getColor(sr).multiply(cd.getColor(sr)).multiply(invPi);
	}

	public Texture getKd() {
		return kd;
	}

	public void setKd(final double kd) {
		final Color kdColor = new Color(kd);
		setKd(kdColor);
	}
	
	public void setKd(final Color kd) {
		final Texture kdTexture = new ConstantColor(kd);
		setKd(kdTexture);
	}
	
	public void setKd(final Texture kd) {
		this.kd = kd;
	}
	
	public void setCd(final Texture cd) {
		this.cd = cd;
	}
	
	public Color getCd(ShadeRec sr) {
		return new Color(cd.getColor(sr));
	}

}
