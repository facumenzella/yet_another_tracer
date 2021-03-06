package ar.edu.itba.it.cg.yart.light.materials;


import ar.edu.itba.it.cg.yart.YartDefaults;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.brdf.PDF;
import ar.edu.itba.it.cg.yart.light.brdf.PerfectSpecular;
import ar.edu.itba.it.cg.yart.samplers.NRooks;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.Texture;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;
import ar.edu.itba.it.cg.yart.tracer.strategy.PathTracingStrategy;
import ar.edu.itba.it.cg.yart.tracer.strategy.RayTracingStrategy;
import ar.edu.itba.it.cg.yart.tracer.strategy.TracerStrategy;

public class Reflective extends Phong implements Material {

	private final PerfectSpecular reflectiveBRDF;
	private double tMax = YartDefaults.DEFAULT_RAY_DEPTH;
	private final TracerStrategy shader = new PathTracingStrategy();
	private final TracerStrategy directs = new RayTracingStrategy();

	public Reflective() {
		this.reflectiveBRDF = new PerfectSpecular();
		this.reflectiveBRDF.setSampler(new NRooks(1, 10000));
	}

	public Reflective setKa(final double ka) {
		super.setKa(ka);
		return this;
	}

	public Reflective setKd(final double kd) {
		super.setKd(kd);
		return this;
	}

	public Reflective setKs(final double ks) {
		super.setKs(ks);
		return this;
	}

	public Reflective setCd(final Color cd) {
		super.setCd(cd);
		return this;
	}

	public Reflective setCd(final Texture cd) {
		super.setCd(cd);
		return this;
	}

	public Reflective setExp(final double exp) {
		super.setExp(exp);
		return this;
	}

	public Reflective setCr(final Texture cr) {
		reflectiveBRDF.setCr(cr);
		return this;
	}

	public Reflective setCr(final Color color) {
		final Texture texture = new ConstantColor(color);
		setCr(texture);
		return this;
	}

	public Reflective setKr(final double kr) {
		reflectiveBRDF.setKr(kr);
		return this;
	}

	public Reflective setKr(final Color kr) {
		reflectiveBRDF.setKr(kr);
		return this;
	}

	public Reflective setKr(final Texture kr) {
		reflectiveBRDF.setKr(kr);
		return this;
	}

	public Reflective setTMax(final double tMax) {
		this.tMax = tMax;
		return this;
	}

	@Override
	public Color shade(ShadeRec sr, final Stack stack) {
		Color colorL = super.shade(sr, stack);
		final double dx = -sr.ray.direction[0];
		final double dy = -sr.ray.direction[1];
		final double dz = -sr.ray.direction[2];

		final Vector3d wo = new Vector3d(dx, dy, dz);
		Vector3d wi = new Vector3d(0, 0, 0);
		PDF pdf = new PDF();
		Color fr = reflectiveBRDF.sample_f(sr, wo, wi, pdf);

		Ray reflectedRay = new Ray(sr.hitPoint, wi);
		ShadeRec sRec = new ShadeRec(sr.world);
		reflectedRay.depth = sr.ray.depth + 1;
		Color c = sr.world.getTree().traceRay(reflectedRay, sRec, tMax, stack,
				directs);

		final double factor = sr.normal.dot(wi);
		fr.r *= c.r * factor;
		fr.g *= c.g * factor;
		fr.b *= c.b * factor;

		colorL.r += fr.r;
		colorL.g += fr.g;
		colorL.b += fr.b;

		return colorL;
	}

	@Override
	public Color globalShade(ShadeRec sr, final Stack stack) {
		final double dx = -sr.ray.direction[0];
		final double dy = -sr.ray.direction[1];
		final double dz = -sr.ray.direction[2];

		final Vector3d wo = new Vector3d(dx, dy, dz);
		Vector3d wi = new Vector3d(0, 0, 0);
		PDF pdf = new PDF();
		Color colorL = super.shade(sr, stack);

		Color fr1 = reflectiveBRDF.sample_f(sr, wo, wi, pdf);
		Ray reflectedRay1 = new Ray(sr.hitPoint, wi);
		final double ndotwi1 = sr.normal.dot(wi);

		Color c;
		ShadeRec sRec1 = new ShadeRec(sr.world);
		reflectedRay1.depth = sr.ray.depth + 1;
		c = sr.world.getTree().traceRay(reflectedRay1, sRec1, tMax, stack,
				shader);

		final double gain = 1;
		final double factor1 = ndotwi1 * gain / pdf.pdf;

		colorL.r += c.r * fr1.r * factor1;
		colorL.g += c.g * fr1.g * factor1;
		colorL.b += c.b * fr1.b * factor1;

		return colorL;
	}
}
