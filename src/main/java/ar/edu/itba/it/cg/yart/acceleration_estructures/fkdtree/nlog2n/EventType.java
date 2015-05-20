package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

public enum EventType {
	END(0), PLANAR(1), START(2);
	
	EventType(final int value) {
		this.value = value;
	}
	
	public int value;
}