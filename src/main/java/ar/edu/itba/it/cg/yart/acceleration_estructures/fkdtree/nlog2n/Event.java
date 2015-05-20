package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitAxis;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitPoint;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class Event implements Comparable<Event>{
	
	public final EventType type;
	public final GeometricObject object;
	public final double point;
	public final SplitAxis axis;
	public final SplitPoint splitPoint;
	
	public Event(final EventType type, final GeometricObject object, final SplitPoint splitPoint) {
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
				return type.value - o.type.value;
			}
			return axis.value - o.axis.value;
		}

		return 1;
	}
	
	@Override
	public String toString() {
		return "Event [axis=" + axis + ", type=" + type + ", position="
				+ point + "]";
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

}
