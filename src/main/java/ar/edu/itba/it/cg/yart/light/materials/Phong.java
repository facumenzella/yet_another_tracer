package ar.edu.itba.it.cg.yart.light.materials;

import java.util.List;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.brdf.GlossySpecular;
import ar.edu.itba.it.cg.yart.light.brdf.Lambertian;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Phong extends MaterialAbstract {

	private final Lambertian ambientBRDF = new Lambertian();
	private final Lambertian diffuseBRDF = new Lambertian();
	private final GlossySpecular specularBRDF = new GlossySpecular();

	@Override
	public Color shade(ShadeRec sr, final Stack stack) {

		final double dx = -sr.ray.direction[0];
		final double dy = -sr.ray.direction[1];
		final double dz = -sr.ray.direction[2];

		final Vector3d wo = new Vector3d(dx, dy, dz);

		final Color colorL = ambientBRDF.getCd(sr);
		colorL.multiplyEquals(sr.world.getAmbientLight().L(sr));
		
		final List<Light> castShadowLights = sr.world.getCastShadowLights();
		final List<Light> doNotCastShadowLights = sr.world
				.getDoNotCastShadowLights();

		for (Light light : doNotCastShadowLights) {
			final Vector3d wi = light.getDirection(sr);
			double ndotwi = sr.normal.dot(wi);

			if (ndotwi > 0.0) {
				Color aux = diffuseBRDF.f(sr, wo, wi);
				aux.addEquals(specularBRDF.f(sr, wo, wi));
				aux.multiplyEquals(light.L(sr));
				aux.multiplyEquals(ndotwi);
				colorL.addEquals(aux);
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
					Color aux = diffuseBRDF.f(sr, wo, wi);
					aux.addEquals(specularBRDF.f(sr, wo, wi));
					aux.multiplyEquals(light.L(sr));
					aux.multiplyEquals(ndotwi);
					colorL.addEquals(aux);
				}
			}
		}

		return colorL;
	}

	public Phong setKa(final double ka) {
		ambientBRDF.setKd(ka);
		return this;
	}

	public Phong setKd(final double kd) {
		diffuseBRDF.setKd(kd);
		return this;
	}

	public Phong setKs(final double ks) {
		specularBRDF.setKs(ks);
		return this;
	}

	public Phong setCd(final Color cd) {
		final Texture texture = new ConstantColor(cd);
		setCd(texture);
		return this;
	}

	public Phong setCd(final Texture cd) {
		ambientBRDF.setCd(cd);
		diffuseBRDF.setCd(cd);
		return this;
	}

	public Phong setExp(final double exp) {
		specularBRDF.setExp(exp);
		return this;
	}
}
