package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public class CookTorrance extends BRDF {

	private Color ks;
	private Color kd;
	private double r;
	private final double pi = Math.PI;
	private final double invPi = 1 / pi;

	@Override
	public Color f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		/*
		 * wi is light direction wo is eye direction;
		 */
		Vector3d normal = sr.normal;

		double rSquared = r * r;

		Vector3d halfVector = wi.add(wo).normalizedVector();
		double NdotH = halfVector.dot(normal);
		double VdotH = wo.dot(halfVector);
		double NdotV = wo.dot(normal);
		double NdotL = wi.dot(normal);

		/* We calculate D (microfacet distribution, Beckmanns */
		double NdotHsquared = NdotH * NdotH;
		double NdotHfourth = NdotHsquared * NdotHsquared;

		double r1 = (NdotHsquared - 1) / (rSquared * NdotHsquared);
		double r2 = 1 / (pi * rSquared * NdotHfourth);
		double D = r2 * Math.exp(r1);

		/*
		 * We calculate G, geometrical attenuation
		 */
		double NH2 = 2.0 * NdotH;
		double g1 = (NH2 * NdotV) / VdotH;
		double g2 = (NH2 * NdotL) / VdotH;
		double G = Math.min(1.0, Math.min(g1, g2));

		/* We calculate fresnel Schlick aproximation */
		double aux = Math.pow(1.0 - VdotH, 5.0);
		Color F = ks.complement();
		F.multiplyEquals(aux);
		F.addEquals(ks);

		double aux2 = (G * D) / (NdotV * NdotL * pi);
		F.multiplyEquals(aux2).multiplyEquals(ks).multiplyEquals(NdotL);

		kd.multiplyEquals(NdotL);
		return kd.add(F);
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		return Color.blackColor();
	}

	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi, PDF pdf) {
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
		Vector3d r = new Vector3d(wox, woy, woz); // direction of mirror
													// reflection

		Vector3d w = r;
		Vector3d u = new Vector3d(0.00424, 1, 0.00764).cross(w);
		u.normalizeMe();
		Vector3d v = u.cross(w);

		Point3d sp = sampler.sampleHemisphere();
		wi.copy(u.scale(sp.x).add(v.scale(sp.y)).add(w.scale(sp.z))); // reflected
																		// ray
																		// direction
		if (sr.normal.dot(wi) < 0.0) { // reflected ray is below tangent plane
			wi.copy(u.scale(-sp.x).add(v.scale(-sp.y)).add(w.scale(-sp.z)));
		}

		wi.normalizeMe();

		return null;
	}

	public void setRoughness(final double roughness) {
		this.r = roughness;
	}

	public void setFresnel(final Color fresnel) {
		this.ks = fresnel;
		this.kd = fresnel.complement();
	}
}
