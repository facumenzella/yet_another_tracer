package ar.edu.itba.it.cg.yart.samplers;

import java.util.concurrent.ThreadLocalRandom;

import ar.edu.itba.it.cg.yart.geometry.Point2d;

public class Jittered extends SamplerAbstract {

	public Jittered(int num_samples, int num_sets) {
		super(num_samples, num_sets);
		generateSamples();
	}

	@Override
	public void generateSamples() {
		int index = 0;
		final int n = (int) Math.sqrt(num_samples);
		for (int p = 0; p < num_sets; p++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					final double x = k + ThreadLocalRandom.current().nextDouble() / n;
					final double y = j + ThreadLocalRandom.current().nextDouble() / n;
					final Point2d point = new Point2d(x, y);
					this.samples[index++] = point;
				}
			}
		}

	}
}
