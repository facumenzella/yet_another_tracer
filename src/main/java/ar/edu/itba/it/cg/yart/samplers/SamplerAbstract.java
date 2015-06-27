package ar.edu.itba.it.cg.yart.samplers;

import java.util.Arrays;
import java.util.Random;

import ar.edu.itba.it.cg.yart.geometry.Point2d;

/*
 * For more information check out chapter 5 in RTFTGU
 */

public abstract class SamplerAbstract implements Sampler{
	
	protected final Random random;
	final protected int num_samples; // the number of points in a pattern
	final protected int num_sets; // the number of sample sets (patterns) stored
	final protected Point2d[] samples;  // sample points  on a unit square
	final protected int[] shuffled_indices; // shuffled samples array indices
	protected long count; // the current number of sample points used
	protected int jump; // random index jump
	
	public SamplerAbstract(final int num_samples, final int num_sets) {
		this.num_samples = num_samples;
		this.num_sets = num_sets;
		this.samples = new Point2d[num_samples*num_sets];
		this.shuffled_indices = setupShuffledIndices();
		this.random = new Random();
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
			Arrays.sort(indices);
			
			for (int j = 0; j < num_samples; j++){
				shuffled_indices[j] = indices[j];
			}
		}
		return shuffled_indices;
	}
	
	protected void shuffle_x_coordinates() {
		for (int p = 0; p < num_sets; p++)
			for (int i = 0; i <  num_samples - 1; i++) {
				int target = random.nextInt() % num_samples + p * num_samples;
				double temp = samples[i + p * num_samples + 1].x;
				samples[i + p * num_samples + 1].x = samples[target].x;
				samples[target].x = temp;
			}
	}
	
	protected void shuffle_y_coordinates() {
		for (int p = 0; p < num_sets; p++)
			for (int i = 0; i <  num_samples - 1; i++) {
				int target = random.nextInt() % num_samples + p * num_samples;
				double temp = samples[i + p * num_samples + 1].y;
				samples[i + p * num_samples + 1].y = samples[target].y;
				samples[target].y = temp;
			}	
	}
	
	
	public Point2d sampleUnitSquare() {
		if (count % num_samples == 0) { // start of a new pixel
			jump = (random.nextInt() % num_sets) * num_samples;
		}
		return this.samples[((int) (jump + count++ % num_samples))];
	}
}
