package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class PerfectSpecular extends BRDF {
	
	Texture kr;
	Texture cr;

	@Override
	public Color f(final ShadeRec sr, final Vector3d wo, final Vector3d wi) {
		return Color.blackColor();
	}

	@Override
	public Color rho(final ShadeRec sr, final Vector3d wo) {
		return Color.blackColor();
	}

	@Override
	public Color sample_f(final ShadeRec sr, final Vector3d wo, Vector3d wi, final PDF pdf) {
		final double ndotwo = sr.normal.dot(wo);

		final double srx = sr.normal.x * ndotwo * 2.0;
		final double sry = sr.normal.y * ndotwo * 2.0;
		final double srz = sr.normal.z * ndotwo * 2.0;

		double wox = -wo.x;
		double woy = -wo.y;
		double woz = -wo.z;

		wox += srx;
		woy += sry;
		woz += srz;
		
		wi.x = wox;
		wi.y = woy;
		wi.z = woz;
		
		pdf.pdf = sr.normal.dot(wi);
		
		final double aux = Math.abs(sr.normal.dot(wi));
		
		final Color c = cr.getColor(sr);
		final Color k = kr.getColor(sr);
		
		final double factor = 1.0 / aux;
		final double r = c.r * k.r * factor;
		final double g = c.g * k.g * factor;
		final double b = c.b * k.b * factor;

		return new Color(r, g, b, c.a);
	}
	
	public void setCr(final Color cr) {
		final Texture texture = new ConstantColor(cr);
		setCr(texture);
	}
	
	public void setCr(final Texture cr) {
		this.cr = cr;
	}
	
	public void setKr(final double kr) {
		final Color krColor = new Color(kr);
		setKr(krColor);
	}
	
	public void setKr(final Color kr) {
		final Texture krTexture = new ConstantColor(kr);
		setKr(krTexture);
	}
	
	public void setKr(final Texture kr) {
		this.kr = kr;
	}

}
