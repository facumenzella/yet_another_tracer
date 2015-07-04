package ar.edu.itba.it.cg.yart.samplers;

import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;

public interface Sampler {

	public Point2d sampleUnitSquare(); // get the next sample on unit square
	public Point3d sampleHemisphere();
	public Point3d sampleSphere();
	public void mapSamples2Hemisphere(final double exp);
	public Point3d oneHemisphereSample(final double e);
}
