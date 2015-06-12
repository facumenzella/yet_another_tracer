package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitPoint;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class Event implements Comparable<Event> {
	public final int type; // END(0), PLANAR(1), START(2);
	public final GeometricObject object;
	public final double point;
	public final int axis; // x=0, y=1, z=2
	public final SplitPoint splitPoint;

	public Event(final int type, final GeometricObject object,
			final SplitPoint splitPoint) {
		this.type = type;
		this.object = object;
		this.point = splitPoint.point;
		this.axis = splitPoint.axis;
		this.splitPoint = splitPoint;
	}

	@Override
	public int compareTo(Event o) {
		final double first = point - o.point;
		if (first < 0) {
			return -1;
		}
		if (first == 0) {
			if (axis == o.axis) {
				return type - o.type;
			}
			return axis - o.axis;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "Event [axis=" + axis + ", type=" + type + ", position="
				+ point + object + "]";
	}
}
