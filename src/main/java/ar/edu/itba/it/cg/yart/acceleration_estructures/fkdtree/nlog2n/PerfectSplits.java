package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;


public class PerfectSplits {
	
	public final double[] perfectXs;
	public final double[] perfectYs;
	public final double[] perfectZs;

	public PerfectSplits(final double[] perfectXs, final double[] perfectYs, final double[] perfectZs) {
		this.perfectXs = perfectXs;
		this.perfectYs = perfectYs;
		this.perfectZs = perfectZs;
	}
	
	public static PerfectSplits perfectSplits(final GeometricObject object,
			final AABB box) {
		double[] xs = new double[2];
		double[] ys = new double[2];
		double[] zs = new double[2];
		AABB b = object.getBoundingBox();
		if (b != null) {
			b = b.clip(box);
			// we first find the perfect xs
			xs[0] = b.p0.x;
			xs[1] = b.p1.x;
			// then the ys
			ys[0] = b.p1.y;
			ys[1] = b.p0.y;
			// finally the zs
			zs[0] = b.p0.z;
			zs[1] = b.p1.z;
		}
		return new PerfectSplits(xs, ys, zs);
	}

}
