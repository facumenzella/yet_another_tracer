package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.List;
import java.util.Map;

import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class ClassifiedObjects {
	final List<GeometricObject> tl, tr;
	final Map<GeometricObject, Integer> sides;

	public ClassifiedObjects(final List<GeometricObject> tl,
			final List<GeometricObject> tr,
			final Map<GeometricObject, Integer> sides) {
		this.tl = tl;
		this.tr = tr;
		this.sides = sides;
	}
}
