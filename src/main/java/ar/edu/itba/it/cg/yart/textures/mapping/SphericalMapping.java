package ar.edu.itba.it.cg.yart.textures.mapping;

import ar.edu.itba.it.cg.yart.geometry.Point3d;

public class SphericalMapping extends Mapping{
	
	@Override
	public void getTexetlCoordinates(Point3d localHitPoint, int hres, int vres) {
		final double theta = Math.acos(localHitPoint.y);
		double phi = Math.atan2(localHitPoint.x, localHitPoint.y);
		if(phi < 0.0) {
			phi += twoPI;
		}
		
		final double u = phi*invTwoPI;
		final double v = 1 - theta * invPI;
		
		int column = (int) ((hres - 1)*u);
		int row = (int) ((vres - 1)*v);
	}

}
