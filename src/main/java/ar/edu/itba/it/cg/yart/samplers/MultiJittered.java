package ar.edu.itba.it.cg.yart.samplers;

import java.util.concurrent.ThreadLocalRandom;
import ar.edu.itba.it.cg.yart.geometry.Point2d;

public class MultiJittered extends SamplerAbstract {

	public MultiJittered(int num_samples, int num_sets) {
		super(num_samples, num_sets);
		generateSamples();
	}

	@Override
	public void generateSamples() {
		int n = (int) Math.sqrt((double) num_samples);
		double subcell_width = 1.0 / num_samples;

		// fill the samples array with dummy points to allow us to use the [ ]
		// notation when we set the
		// initial patterns

		for (int j = 0; j < num_samples * num_sets; j++) {
			samples[j] = new Point2d(0, 0);
		}
		// distribute points in the initial patterns
		for (int p = 0; p < num_sets; p++)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++) {
					samples[i * n + j + p * num_samples].x = (i * n + j)
							* subcell_width + (ThreadLocalRandom.current().nextDouble() * subcell_width);
					samples[i * n + j + p * num_samples].y = (j * n + i)
							* subcell_width + (ThreadLocalRandom.current().nextDouble() * subcell_width);
				}

		this.shuffleXCoordinates();
		this.shuffleYCoordinates();
	}

}
