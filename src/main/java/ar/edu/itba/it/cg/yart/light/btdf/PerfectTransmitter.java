package ar.edu.itba.it.cg.yart.light.btdf;

import ar.edu.itba.it.cg.yart.color.Color;
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
		double nx = sr.normal.x;
		double ny = sr.normal.y;
		double nz = sr.normal.z;
		
		double cosThetai = sr.normal.dot(wo);
		double eta = ior;

		if (cosThetai < 0.0) {
			cosThetai = -cosThetai;
			nx = -nx;
			ny = -ny;
			nz = -nz;
			eta = 1.0/eta;
		}
		double etaSquared = eta*eta;
		double temp = 1.0 - (1.0 - cosThetai * cosThetai) / etaSquared;
		double cosTheta2 = Math.sqrt(temp);
		
		final double factor = 1.0 / eta;
		double wox = -wo.x * factor;
		double woy = -wo.y * factor;
		double woz = -wo.z * factor;
		
		
		final double factor2 = cosTheta2 - cosThetai / eta;
		nx *= factor2;
		ny *= factor2;
		nz *= factor2;
	
		wox -= nx;
		woy -= ny;
		woz -= nz;

		wt.x = wox;
		wt.y = woy;
		wt.z = woz;
		
		final double aux = Math.abs(sr.normal.dot(wt));
		
		final double factor3 = 1.0/etaSquared;
		final double factor4 = 1.0/aux;

		final Color k = kt.getColor(sr);
		final Color w = Color.WHITE;
		
		final double r = k.r * factor3 * w.r * factor4;
		final double g = k.g * factor3 * w.g * factor4;
		final double b = k.b * factor3 * w.b * factor4;

		return new Color(r, g, b, k.a);
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
