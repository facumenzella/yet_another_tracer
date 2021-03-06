package ar.edu.itba.it.cg.yart.samplers;

import java.util.concurrent.ThreadLocalRandom;
import ar.edu.itba.it.cg.yart.geometry.Point2d;

public class NRooks extends SamplerAbstract {

	public NRooks(int num_samples, int num_sets) {
		super(num_samples, num_sets);
		generateSamples();
	}

	@Override
	public void generateSamples() {
		int i = 0;
		for (int p = 0; p < num_sets; p++)          			
			for (int j = 0; j < num_samples; j++) {
				final double x = j + ThreadLocalRandom.current().nextDouble() / num_samples;
				final double y = j + ThreadLocalRandom.current().nextDouble() / num_samples;
				final Point2d point = new Point2d(x,y);
				samples[i++] = point;
			}		

		shuffleXCoordinates();
		shuffleYCoordinates();
	}

}
