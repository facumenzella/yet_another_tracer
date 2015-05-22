package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class ClassifiedEvents {

	public final List<Event> elo;
	public final List<Event> ero;
	public final List<Event> ebl;
	public final List<Event> ebr;

	private ClassifiedEvents(final List<Event> elo, final List<Event> ero,
			final List<Event> ebl, final List<Event> ebr) {
		this.elo = elo;
		this.ero = ero;
		this.ebl = ebl;
		this.ebr = ebr;
	}

	public static ClassifiedEvents splice(final Event[] events,
			final ClassifiedObjects tc, final AABB leftBox, final AABB rightBox) {
		final List<Event> elo = new ArrayList<Event>();
		final List<Event> ero = new ArrayList<Event>();
		final List<Event> ebl = new ArrayList<Event>();
		final List<Event> ebr = new ArrayList<Event>();

		for (int i = 0; i < events.length; i++) {
			Event e = events[i];
			switch (tc.sides.get(e.object)) {
			case 1:
				elo.add(e);
				break;
			case 2:
				ero.add(e);
				break;
			}
		}

		for (final Entry<GeometricObject, Integer> entry : tc.sides.entrySet()) {
			if (entry.getValue() == 3) {
				final GeometricObject obj = entry.getKey();
				ebl.addAll(YAFKDTree2.generateEvents(obj, leftBox));
				ebr.addAll(YAFKDTree2.generateEvents(obj, rightBox));
			}
		}

		return new ClassifiedEvents(elo, ero, ebl, ebr);
	}

}
