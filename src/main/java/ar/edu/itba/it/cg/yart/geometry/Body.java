package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.acceleration_estructures.Side;
import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;

public interface Body {
	public Side insideOf(final BoundingBox box);
}
