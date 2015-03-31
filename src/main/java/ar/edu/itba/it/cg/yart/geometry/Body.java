package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.acceleration_estructures.Side;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;

public interface Body {
	public Side sideOfPlane(final Plane plane);
}
