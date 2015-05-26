package ar.edu.itba.it.cg.yart.light.materials;

import java.util.List;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.brdf.Lambertian;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Matte extends MaterialAbstract {

	private Lambertian ambientBRDF = new Lambertian();
	private Lambertian diffuseBRDF = new Lambertian();

	@Override
	public Color shade(ShadeRec sr, final Stack stack) {
		final double dx = -sr.ray.direction[0];
		final double dy = -sr.ray.direction[1];
		final double dz = -sr.ray.direction[2];

		final Vector3d wo = new Vector3d(dx, dy, dz);
		final Color colorL = ambientBRDF.getCd(sr);
		final Color a = sr.world.getAmbientLight().L(sr);

		colorL.r *= a.r;
		colorL.g *= a.g;
		colorL.b *= a.b;

		final List<Light> castShadowLights = sr.world.getCastShadowLights();
		final List<Light> doNotCastShadowLights = sr.world
				.getDoNotCastShadowLights();

		for (final Light light : doNotCastShadowLights) {
			final Vector3d wi = light.getDirection(sr);
			double ndotwi = sr.normal.dot(wi);

			if (ndotwi > 0.0) {
				final Color aux = diffuseBRDF.f(sr, wo, wi);
				final Color li = light.L(sr);
				aux.r *= li.r * ndotwi;
				aux.g *= li.g * ndotwi;
				aux.b *= li.b * ndotwi;

				colorL.r += aux.r;
				colorL.g += aux.g;
				colorL.b += aux.b;

			}
		}

		for (final Light light : castShadowLights) {
			final Vector3d wi = light.getDirection(sr);
			double ndotwi = sr.normal.dot(wi);

			if (ndotwi > 0.0) {
				boolean inShadow = false;
				Ray shadowRay = new Ray(sr.hitPoint, wi);
				inShadow = light.inShadow(shadowRay, sr, stack);
				if (!inShadow) {
					final Color aux = diffuseBRDF.f(sr, wo, wi);
					final Color li = light.L(sr);
					aux.r *= li.r * ndotwi;
					aux.g *= li.g * ndotwi;
					aux.b *= li.b * ndotwi;

					colorL.r += aux.r;
					colorL.g += aux.g;
					colorL.b += aux.b;
				}
			}
		}

		return colorL;
	}

	public Matte setKa(final double ka) {
		ambientBRDF.setKd(ka);
		return this;
	}

	public Matte setKd(final double kd) {
		diffuseBRDF.setKd(kd);
		return this;
	}

	public Matte setKd(final Color kd) {
		diffuseBRDF.setKd(kd);
		return this;
	}

	public Matte setKd(final Texture kd) {
		diffuseBRDF.setKd(kd);
		return this;
	}

	public Matte setCd(final double cd) {
		final Color color = new Color(cd);
		return setCd(color);
	}

	public Matte setCd(final Color cd) {
		final Texture texture = new ConstantColor(cd);
		return setCd(texture);
	}

	public Matte setCd(final Texture cd) {
		ambientBRDF.setCd(cd);
		diffuseBRDF.setCd(cd);
		return this;
	}
}