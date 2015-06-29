package ar.edu.itba.it.cg.yart.light.materials;

import java.util.List;

import ar.edu.itba.it.cg.yart.YartConstants;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.AreaLight;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.brdf.Lambertian;
import ar.edu.itba.it.cg.yart.light.brdf.PDF;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Matte extends MaterialAbstract {

	private Lambertian ambientBRDF = new Lambertian();
	private Lambertian diffuseBRDF = new Lambertian();
	private double tMax = YartConstants.DEFAULT_TMAX;

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
		
		Color areaColors = this.shadeAreaLights(sr, stack, wo);
		colorL.r += areaColors.r;
		colorL.g += areaColors.g;
		colorL.b += areaColors.b;
		
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
	
	private Color shadeAreaLights(final ShadeRec sr, final Stack stack, final Vector3d wo) {
		Color colorL = new Color(0);
		for (final AreaLight light : sr.world.getAreaLights()) {
			double pdfAndSamples = light.pdf(sr) * light.getSamplesNumber();
			for (int i = 0; i < light.getSamplesNumber(); i++) {
				final Vector3d wi = light.getDirection(sr);
				double ndotwi = sr.normal.dot(wi);

				if (ndotwi > 0.0) {
					boolean inShadow = false;
					Ray shadowRay = new Ray(sr.hitPoint, wi);
					inShadow = light.inShadow(shadowRay, sr, stack);
					if (!inShadow) {
						final Color aux = diffuseBRDF.f(sr, wo, wi);
						final Color li = light.L(sr);
						final double g = light.G(sr);
						final double factor = ndotwi * g / pdfAndSamples;
						
						aux.r *= li.r * factor;
						aux.g *= li.g * factor;
						aux.b *= li.b * factor;

						colorL.r += aux.r;
						colorL.g += aux.g;
						colorL.b += aux.b;
					}
				}
			}
		}
		return colorL;
	}
	
	@Override
	public Color globalShade(final ShadeRec sr, final Stack stack) {
		final double dx = -sr.ray.direction[0];
		final double dy = -sr.ray.direction[1];
		final double dz = -sr.ray.direction[2];

		final Vector3d wo = new Vector3d(dx, dy, dz);
		Color L = null;
		if (sr.depth == 0) {
			L = this.shadeAreaLights(sr, stack, wo);
		}
		Vector3d wi = null;
		PDF pdf = new PDF();
		final Color f = diffuseBRDF.sample_f(sr, wo, wi, pdf);
		final double ndotwi = sr.normal.dot(wi);
		
		final Ray reflectedRay = new Ray(sr.hitPoint, wi);
		reflectedRay.depth = sr.ray.depth + 1;
		Color reflectedColor = sr.world.getTree().traceRay(reflectedRay, new ShadeRec(sr.world), tMax, stack);
		
		f.r *= ndotwi / pdf.pdf;
		f.g *= ndotwi / pdf.pdf;
		f.b *= ndotwi / pdf.pdf;
		
		L.r += reflectedColor.r * f.r;
		L.g += reflectedColor.g * f.g;
		L.b += reflectedColor.b * f.b;
		return L;
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
