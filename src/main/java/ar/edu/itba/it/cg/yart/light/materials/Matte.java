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
import ar.edu.itba.it.cg.yart.raytracer.shade.PathTracerShader;
import ar.edu.itba.it.cg.yart.raytracer.shade.Shader;
import ar.edu.itba.it.cg.yart.samplers.NRooks;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Matte extends MaterialAbstract {

	private final Lambertian ambientBRDF;
	private Lambertian diffuseBRDF;
	private final double tMax = YartConstants.DEFAULT_TMAX;
	private final Shader shader = new PathTracerShader();

	public Matte() {
		this.ambientBRDF = new Lambertian();
		this.diffuseBRDF = new Lambertian();
		this.diffuseBRDF.setSampler(new NRooks(1, 1000));
	}

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

	@Override
	public Color globalShade(final ShadeRec sr, final Stack stack) {
		final double dx = -sr.ray.direction[0];
		final double dy = -sr.ray.direction[1];
		final double dz = -sr.ray.direction[2];
		
		Vector3d wi = new Vector3d(0, 0, 0);
		PDF pdf = new PDF();
		
		Color colorL = this.shade(sr, stack);
		final Vector3d wo = new Vector3d(dx, dy, dz);
		ShadeRec sRec1 = new ShadeRec(sr.world);
		final Color f1 = diffuseBRDF.sample_f(sr, wo, wi, pdf);
		final double ndotwi1 = sr.normal.dot(wi);

		final Ray reflectedRay1 = new Ray(sr.hitPoint, wi);
		reflectedRay1.depth = sr.ray.depth + 1;
		Color reflectedColor1 = sr.world.getTree().traceRay(reflectedRay1,
				sRec1, tMax, stack, shader);
		
		final double gain = 1;
		final double factor1 = ndotwi1 * gain / pdf.pdf;
		
		colorL.r += reflectedColor1.r * f1.r * factor1;
		colorL.g += reflectedColor1.g * f1.g * factor1;
		colorL.b += reflectedColor1.b * f1.b * factor1;
		
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
