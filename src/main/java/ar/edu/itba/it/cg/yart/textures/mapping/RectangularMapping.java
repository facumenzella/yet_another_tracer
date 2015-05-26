package ar.edu.itba.it.cg.yart.textures.mapping;

import java.awt.Point;

import ar.edu.itba.it.cg.yart.geometry.Point3d;

public class RectangularMapping extends Mapping{

	@Override
	public void getTexetlCoordinates(Point3d localHitPoint, int hres, int vres,
			Point coordinates) {
		
		int column, row;

		final double u = localHitPoint.y;
		final double v = localHitPoint.x;
		
		column = (int) ((vres - 1)*u);
		row = (int) ((hres - 1)*v);
		coordinates.setLocation(row, column);
	}
	
}	
