package ar.edu.itba.it.cg.yart.textures.mapping;

import ar.edu.itba.it.cg.yart.geometry.Point3d;


public abstract class Mapping {
	
	protected final double invPI = 1/Math.PI;
	protected final double twoPI = 2*Math.PI;
	protected final double invTwoPI = 1/twoPI;
	
	public abstract void getTexetlCoordinates(final Point3d localHitPoint, final int hres,
			final int vres);
}
