package ar.edu.itba.it.cg.yart.textures.mapping;

import java.awt.Point;

import ar.edu.itba.it.cg.yart.geometry.Normal3d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class SphericalMapping extends Mapping{
	
	@Override
	public void getTexetlCoordinates(final Point3d localHitPoint,
			final int hres, final int vres, Point coordinates) {
		
		Vector3d point = new Vector3d(localHitPoint.x,localHitPoint.y, localHitPoint.z);
		Normal3d versor = point.normalizedVector();
		final double theta = Math.acos(versor.y);
		double phi = Math.atan2(versor.x, versor.z);
		if(phi < 0.0) {
			phi += twoPI;
		}
		
		final double u = phi*invTwoPI;
		final double v = 1 - theta * invPI;
		
		int X = (int) ((hres - 1)*u);
		int Y = (int) ((vres - 1)*v);
		coordinates.setLocation(X, Y);
	}

}
