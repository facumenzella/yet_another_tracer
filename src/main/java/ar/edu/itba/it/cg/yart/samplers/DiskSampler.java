package ar.edu.itba.it.cg.yart.samplers;

import ar.edu.itba.it.cg.yart.geometry.Point2d;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates N samples uniformly across a disk with R radius.
 * The samples are pre-generated. This class is intended to be used when samples must be obtained efficiently, but
 * there is no time limit when generating them.
 * Since this is intended to be used in multi-threaded environments, to prevent race conditions and avoid
 * synchronization some extra samples are generated.
 */
public class DiskSampler {

	private static final double EXTRA_SAMPLES_PERCENTAGE = 0.1;

	private final int samplesNumber;
	private final double radius;
	private final Point2d[] samples;

	private int nextSample = 0;

	public DiskSampler(final int samplesNumber, final double radius) throws IllegalArgumentException {
		if (samplesNumber <= 0) {
			throw new IllegalArgumentException("Number of samples must be a positive number");
		}
		else if (radius <= 0) {
			throw new IllegalArgumentException("Disk radius must be a positive number");
		}
		this.samplesNumber = samplesNumber;
		this.radius = radius;
		this.samples = new Point2d[(int) Math.ceil((1 + EXTRA_SAMPLES_PERCENTAGE) * samplesNumber)];
		generateSamples();
	}

	public Point2d getSample() {
		final Point2d p = samples[nextSample++];
		if (nextSample > samplesNumber) {
			nextSample = 0;
		}
		return p;
	}

	private void generateSamples() {
		for (int i = 0; i < samples.length; i++) {
			final double rSquareRoot = Math.sqrt(ThreadLocalRandom.current().nextDouble()) * radius;
			final double theta = ThreadLocalRandom.current().nextDouble(2 * Math.PI);
			final Point2d p = new Point2d(rSquareRoot * Math.cos(theta), rSquareRoot * Math.sin(theta));
			samples[i] = p;
		}
	}
}
