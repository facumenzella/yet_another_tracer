package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.PlaneCandidate;
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
	
	public static ClassifiedObjects classify(List<GeometricObject> gObjects,
			final Event[] events, final PlaneCandidate candidate) {
		// 1: Left
		// 2: Right
		// 3: Both
		Map<GeometricObject, Integer> sides = new HashMap<GeometricObject, Integer>();

		for (final GeometricObject o : gObjects) {
			sides.put(o, 3);
		}

		// END(0), PLANAR(1), START(2);
		for (final Event e : events) {
			if (e.type == 0 && e.axis == candidate.splitPoint.axis
					&& e.point <= candidate.splitPoint.point) {
				sides.put(e.object, 1);
			} else if (e.type == 2 && e.axis == candidate.splitPoint.axis
					&& e.point >= candidate.splitPoint.point) {
				sides.put(e.object, 2);
			} else if (e.type == 1 && e.axis == candidate.splitPoint.axis) {
				if (e.point < candidate.splitPoint.point
						|| (e.point == candidate.splitPoint.point && candidate.left)) {
					sides.put(e.object, 1);
				} else if (e.point > candidate.splitPoint.point
						|| (e.point == candidate.splitPoint.point && !candidate.left)) {
					sides.put(e.object, 2);
				}

			}
		}

		final List<GeometricObject> tl = new ArrayList<GeometricObject>();
		final List<GeometricObject> tr = new ArrayList<GeometricObject>();

		for (final Entry<GeometricObject, Integer> e : sides.entrySet()) {
			switch (e.getValue()) {
			case 1:
				tl.add(e.getKey());
				break;
			case 2:
				tr.add(e.getKey());
				break;
			case 3:
				tl.add(e.getKey());
				tr.add(e.getKey());
				break;
			}
		}

		return new ClassifiedObjects(tl, tr, sides);
	}
}
