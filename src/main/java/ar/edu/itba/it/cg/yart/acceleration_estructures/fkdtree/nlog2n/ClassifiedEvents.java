package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.List;

public class ClassifiedEvents {

	public final List<Event> elo;
	public final List<Event> ero;
	public final List<Event> ebl;
	public final List<Event> ebr;

	ClassifiedEvents(final List<Event> elo, final List<Event> ero,
			final List<Event> ebl, final List<Event> ebr) {
		this.elo = elo;
		this.ero = ero;
		this.ebl = ebl;
		this.ebr = ebr;
	}
}
