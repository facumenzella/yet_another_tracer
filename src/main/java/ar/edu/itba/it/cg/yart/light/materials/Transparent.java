package ar.edu.itba.it.cg.yart.light.materials;

import org.w3c.dom.css.RGBColor;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.brdf.PerfectSpecular;
import ar.edu.itba.it.cg.yart.light.btdf.PerfectTransmitter;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class Transparent extends Phong implements Material{

	private final PerfectSpecular reflectiveBRDF = new PerfectSpecular();
	private final PerfectTransmitter specularBTDF = new PerfectTransmitter();

	public Transparent setKa(final double ka) {
		super.setKa(ka);
		return this;
	}

	public Transparent setKd(final double kd) {
		super.setKd(kd);
		return this;
	}

	public Transparent setKs(final double ks) {
		super.setKs(ks);
		return this;
	}

	public Transparent setCd(final Color cd) {
		super.setCd(cd);
		return this;
	}

	public Transparent setCd(final Texture cd) {
		super.setCd(cd);
		return this;
	}

	public Transparent setExp(final double exp) {
		super.setExp(exp);
		return this;
	}
	
	public Transparent setCr(final Color color) {
		final Texture cr = new ConstantColor(color);
		setCr(cr);
		return this;
	}
	
	public Transparent setCr(final Texture cr) {
		reflectiveBRDF.setCr(cr);
		return this;
	}

	public Transparent setKr(final double kr) {
		reflectiveBRDF.setKr(kr);
		return this;
	}
	
	public Transparent setKr(final Color kr) {
		reflectiveBRDF.setKr(kr);
		return this;
	}
	
	public Transparent setKr(final Texture kr) {
		reflectiveBRDF.setKr(kr);
		return this;
	}

	public Transparent setIor(final double ior) {
		specularBTDF.setIor(ior);
		return this;
	}
	
	public Transparent setKt(final Color kt) {
		specularBTDF.setKt(kt);
		return this;
	}
	
	public Transparent setKt(final Texture kt) {
		specularBTDF.setKt(kt);
		return this;
	}

	public Transparent setKt(final double kt) {
		specularBTDF.setKt(kt);
		return this;
	}

	@Override
	public Color shade(ShadeRec sr, final Stack stack) {
		Color colorL = super.shade(sr, stack);
		final Vector3d wo = sr.ray.direction.inverse();
		final Vector3d wi = new Vector3d(0, 0, 0);

		//final Color fr = reflectiveBRDF.sample_f(sr, wo, wi);
		final Ray reflectedRay = new Ray(sr.hitPoint, wi);
		reflectedRay.depth = sr.depth + 1;
		if (specularBTDF.tir(sr)) {
			colorL.addEquals(sr.world.getTree().traceRay(reflectedRay, new ShadeRec(sr.world), stack));
		} else {
			final Vector3d wt = new Vector3d(0, 0, 0);
			final Color ft = specularBTDF.sample_f(sr, wo, wt);
			final Ray transmittedRay = new Ray(sr.hitPoint, wt);
			transmittedRay.depth = sr.depth + 1;
			
			//double srdotwi = Math.abs(sr.normal.dot(wi));
			double srdotwt = Math.abs(sr.normal.dot(wt));
			
			//colorL.addEquals(sr.world.getTree().traceRay(reflectedRay, new ShadeRec(sr.world), stack).multiply(fr).multiply(srdotwi));
			colorL.addEquals(sr.world.getTree().traceRay(transmittedRay, new ShadeRec(sr.world), stack).multiply(ft).multiply(srdotwt));
		}
		
		return colorL;
	}

}
