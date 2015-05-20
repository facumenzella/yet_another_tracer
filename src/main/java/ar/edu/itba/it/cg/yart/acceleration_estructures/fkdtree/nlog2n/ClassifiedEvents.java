package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class ClassifiedEvents {
	
	public final Set<Event> elo;
	public final Set<Event> ero;
	public final Set<Event> ebl;
	public final Set<Event> ebr;
	
	private ClassifiedEvents(final Set<Event> elo,
			final Set<Event> ero, final Set<Event> ebl,
			final Set<Event> ebr) {
		this.elo = elo;
		this.ero = ero;
		this.ebl = ebl;
		this.ebr = ebr;
	}
	
	public static ClassifiedEvents splice(final Event[] events,
			final ClassifiedObjects tc, final AABB leftBox, final AABB rightBox) {
		final Set<Event> elo = new TreeSet<Event>();
		final Set<Event> ero = new TreeSet<Event>();
		final Set<Event> ebl = new TreeSet<Event>();
		final Set<Event> ebr = new TreeSet<Event>();
		
		try {
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
		} catch (Exception exc) {
			exc.printStackTrace();
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
