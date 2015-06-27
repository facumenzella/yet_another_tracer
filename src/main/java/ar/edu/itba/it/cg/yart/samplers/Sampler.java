package ar.edu.itba.it.cg.yart.samplers;

import ar.edu.itba.it.cg.yart.geometry.Point2d;

public interface Sampler {

	public void generateSamples(); // generate sample patterns in a unit square
	public Point2d sampleUnitSquare(); // get the next sample on unit square
	
}
