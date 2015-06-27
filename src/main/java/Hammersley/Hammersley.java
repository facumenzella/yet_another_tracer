package Hammersley;

import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.samplers.SamplerAbstract;

public class Hammersley extends SamplerAbstract {

	public Hammersley(int num_samples, int num_sets) {
		super(num_samples, num_sets);
		generateSamples();
	}

	@Override
	public void generateSamples() {
		int i = 0;
		for (int p = 0; p < num_sets; p++)		
			for (int j = 0; j < num_samples; j++) {
				final double x = (double) j / (double) num_samples;
				final double y = phi(j);
				final Point2d point = new Point2d(x,y);
				samples[i++] = point;
			}	
	}
	
	private double phi(final int j) {
		double x = 0.0;
		double f = 0.5; 
		
		int iterator = j;
		while (iterator != 0) {
			x += f * (double) (j % 2);
			iterator /= 2;
			f *= 0.5; 
		}
		
		return x;
	}
}
