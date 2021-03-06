package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitPoint;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
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
	
	public static List<Event> mergeEvents(final Collection<Event> e1,
			final Collection<Event> elo) {
		final Iterator<Event> it1 = e1.iterator();
		final Iterator<Event> it2 = elo.iterator();
		final List<Event> merged = new ArrayList<>();

		if (!it1.hasNext()) {
			merged.addAll(elo);
			return merged;
		}

		if (!it2.hasNext()) {
			merged.addAll(e1);
			return merged;
		}

		Event ev1 = it1.next();
		Event ev2 = it2.next();

		do {
			if (ev1.compareTo(ev2) < 0) {
				merged.add(ev1);
				if (it1.hasNext()) {
					ev1 = it1.next();
				} else {
					merged.add(ev2);
					break;
				}
			} else {
				merged.add(ev2);
				if (it2.hasNext()) {
					ev2 = it2.next();
				} else {
					merged.add(ev1);
					break;
				}
			}
		} while (it1.hasNext() && it2.hasNext());

		while (it1.hasNext()) {
			merged.add(it1.next());
		}

		while (it2.hasNext()) {
			merged.add(it2.next());
		}

		return merged;
	}
	
	// Here comes the shit
		public static List<Event> generateEvents(final GeometricObject object,
				final AABB box) {
			final List<Event> eventList = new ArrayList<Event>();
			PerfectSplits perfectSplits = PerfectSplits.perfectSplits(object, box);

			// 3 because we have 3 dimensions
			// first x
			int axis = 0; // x;
			double[] perfects = null;
			perfects = perfectSplits.perfectXs;
			double min = perfects[0];
			double max = perfects[1];

			// if they are the same, they are planar
			// END(0), PLANAR(1), START(2);
			if (max - min < YAFKDTree.kEPSILON) {
				SplitPoint splitPoint = new SplitPoint();
				splitPoint.axis = axis;
				splitPoint.point = min;
				eventList.add(new Event(1, object, splitPoint));
			} else if (object.isFinite()) {
				SplitPoint splitPointStart = new SplitPoint();
				splitPointStart.axis = axis;
				splitPointStart.point = min;
				SplitPoint splitPointEnd = new SplitPoint();
				splitPointEnd.axis = axis;
				splitPointEnd.point = max;

				eventList.add(new Event(2, object, splitPointStart));
				eventList.add(new Event(0, object, splitPointEnd));
			}

			// then y
			perfects = null;
			axis = 1; // y
			perfects = perfectSplits.perfectYs;

			min = perfects[0];
			max = perfects[1];

			// if they are the same, they are planar
			// END(0), PLANAR(1), START(2);
			if (max - min < YAFKDTree.kEPSILON) {
				SplitPoint splitPoint = new SplitPoint();
				splitPoint.axis = axis;
				splitPoint.point = min;
				eventList.add(new Event(1, object, splitPoint));
			} else if (object.isFinite()) {
				SplitPoint splitPointStart = new SplitPoint();
				splitPointStart.axis = axis;
				splitPointStart.point = min;
				SplitPoint splitPointEnd = new SplitPoint();
				splitPointEnd.axis = axis;
				splitPointEnd.point = max;

				eventList.add(new Event(2, object, splitPointStart));
				eventList.add(new Event(0, object, splitPointEnd));
			}

			// finally z
			perfects = null;
			axis = 2; // z
			perfects = perfectSplits.perfectZs;

			min = perfects[0];
			max = perfects[1];

			// if they are the same, they are planar
			// END(0), PLANAR(1), START(2);
			if (max - min < YAFKDTree.kEPSILON) {
				SplitPoint splitPoint = new SplitPoint();
				splitPoint.axis = axis;
				splitPoint.point = min;
				eventList.add(new Event(1, object, splitPoint));
			} else if (object.isFinite()) {
				SplitPoint splitPointStart = new SplitPoint();
				splitPointStart.axis = axis;
				splitPointStart.point = min;
				SplitPoint splitPointEnd = new SplitPoint();
				splitPointEnd.axis = axis;
				splitPointEnd.point = max;

				eventList.add(new Event(2, object, splitPointStart));
				eventList.add(new Event(0, object, splitPointEnd));
			}

			return eventList;

		}
}
