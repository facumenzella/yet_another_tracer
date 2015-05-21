package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Lambertian extends BRDF {

	private Texture kd;
	private Texture cd;
	private final double invPi = 1 / Math.PI;

	private Color sample_f = Color.blackColor();
	
	@Override
	public Color f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return rho(sr, wo).multiply(invPi);
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		return cd.getColor(sr).multiply(kd.getColor(sr));
	}

	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return this.sample_f;
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

}
