package ar.edu.itba.it.cg.yart.samplers;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;

/*
 * For more information check out chapter 5 in RTFTGU
 */

public abstract class SamplerAbstract implements Sampler{
	
	protected final Random random;
	final protected int num_samples; // the number of points in a pattern
	final protected int num_sets; // the number of sample sets (patterns) stored
	final protected Point2d[] samples;  // sample points  on a unit square
	final protected Point3d[] hemisphereSamples; // sample points on a unit hemisphere
	final protected Point3d[] sphereSamples; // sample points on a unit sphere
	final protected int[] shuffled_indices; // shuffled samples array indices
	protected long count; // the current number of sample points used
	protected int jump; // random index jump
	
	public SamplerAbstract(final int num_samples, final int num_sets) {
		this.random = ThreadLocalRandom.current();
		this.jump = 0;
		this.count = 0;
		this.num_samples = num_samples;
		this.num_sets = num_sets;
		this.samples = new Point2d[num_samples * num_sets];
		this.hemisphereSamples = new Point3d[num_samples * num_sets];
		this.sphereSamples = new Point3d[num_samples * num_sets];
		this.shuffled_indices = setupShuffledIndices();
	}
	
	public abstract void generateSamples();
	 // randomly shuffle  the samples in each pattern
	
	 // set up the randomly shuffled indices
	private int[] setupShuffledIndices() { 
		final int shuffled_indices[] = new int[num_samples*num_sets];
		final int[] indices = new int[num_samples*num_sets];
		
		for (int i = 0; i < num_samples; i++) {
			indices[i]= i;
		}
		for (int p = 0; p < num_sets; p++) { 
			int index = random.nextInt(p + 1);
		      // Simple swap
		      int a = indices[index];
		      indices[index] = indices[p];
		      indices[p] = a;
			
			for (int j = 0; j < num_samples; j++){
				shuffled_indices[j] = indices[j];
			}
		}
		return shuffled_indices;
	}
		
	public Point2d sampleUnitSquare() {
		if (count % num_samples == 0) { // start of a new pixel
			jump = (random.nextInt(num_sets)) * num_samples;
		}
		return this.samples[((int) (jump + count++ % num_samples))];
	}
	
	public Point3d sampleHemisphere() {
		if (count % num_samples == 0) {
			jump = (random.nextInt(num_sets)) * num_samples; // start of a new pixel
		}
		return (this.hemisphereSamples[jump + shuffled_indices[(int) (jump + count++ % num_samples)]]);		
	}
	
	public Point3d sampleSphere() {
		if (count % num_samples == 0) {
			jump = (random.nextInt() % num_sets) * num_samples; // start of a new pixel
		}
		return (this.sphereSamples[jump + shuffled_indices[(int) (jump + count++ % num_samples)]]);		
	}
	
	public void mapSamples2Hemisphere(final double exp) {
		int size = samples.length;
		
		for (int j = 0; j < size; j++) {
			final double cos_phi = Math.cos(2.0 * Math.PI * samples[j].x);
			final double sin_phi = Math.sin(2.0 * Math.PI * samples[j].x);	
			final double cos_theta = Math.pow((1.0 - samples[j].y), 1.0 / (exp + 1.0));
			final double sin_theta = Math.sqrt(1.0 - cos_theta * cos_theta);
			final double pu = sin_theta * cos_phi;
			final double pv = sin_theta * sin_phi;
			final double pw = cos_theta;
			this.hemisphereSamples[j] = new Point3d(pu, pv, pw); 
		}
	}
	
	protected void mapSamples2Sphere() {
		double r1, r2;
		double x, y, z;
		double r, phi;
			
		for (int j = 0; j < num_samples * num_sets; j++) {
			r1 	= samples[j].x;
	    	r2 	= samples[j].y;
	    	z 	= 1.0 - 2.0 * r1;
	    	r 	= Math.sqrt(1.0 - z * z);
	    	phi = Math.PI * 2 * r2;
	    	x 	= r * Math.cos(phi);
	    	y 	= r * Math.sin(phi);
			this.sphereSamples[j] = new Point3d(x, y, z); 
		}
	}
	
	protected void shuffleXCoordinates() {
		for (int p = 0; p < num_sets; p++)
			for (int i = 0; i <  num_samples - 1; i++) {
				int target = random.nextInt(num_samples) + p * num_samples;
				double temp = samples[i + p * num_samples + 1].x;
				samples[i + p * num_samples + 1].x = samples[target].x;
				samples[target].x = temp;
			}
	}
	
	protected void shuffleYCoordinates() {
		for (int p = 0; p < num_sets; p++)
			for (int i = 0; i <  num_samples - 1; i++) {
				int target = random.nextInt(num_samples) + p * num_samples;
				double temp = samples[i + p * num_samples + 1].y;
				samples[i + p * num_samples + 1].y = samples[target].y;
				samples[target].y = temp;
			}	
	}
}
