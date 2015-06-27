package ar.edu.itba.it.cg.yart.samplers;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.Point2d;

/*
 * For more information check out chapter 5 in RTFTGU
 */

public abstract class SamplerAbstract implements Sampler{
	
	final protected int num_samples; // the number of points in a pattern
	final protected int num_sets; // the number of sample sets (patterns) stored
	final protected List<Point2d> samples;  // sample points  on a unit square
	final protected int[] shuffled_indices; // shuffled samples array indices
	final protected long count; // the current number of sample points used
	final protected int jump; // random index jump
	
	public SamplerAbstract(final int num_samples, final int num_sets,
			final List<Point2d> samples, final int[] shuffled_indices, final long count, final int jump) {
		this.num_samples = num_samples;
		this.num_sets = num_sets;
		this.samples = samples;
		this.shuffled_indices = shuffled_indices;
		this.count = count;
		this.jump = jump;
	}
	public abstract void generateSamples();
	public abstract void setupShuffledIndices();
	public abstract void shuffleSamples();
	public abstract Point2d sampleUnitSquare();
}
