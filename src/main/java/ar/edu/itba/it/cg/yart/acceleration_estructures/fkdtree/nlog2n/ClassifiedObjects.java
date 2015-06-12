package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.PlaneCandidate;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree.Event;
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
