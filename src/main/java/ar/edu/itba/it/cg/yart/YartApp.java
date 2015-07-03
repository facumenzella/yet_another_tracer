package ar.edu.itba.it.cg.yart;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import ar.edu.itba.it.cg.yart.parser.SceneParseException;
import ar.edu.itba.it.cg.yart.parser.SceneParser;
import ar.edu.itba.it.cg.yart.parser.SceneParser.TracerType;
import ar.edu.itba.it.cg.yart.tracer.AbstractTracer;
import ar.edu.itba.it.cg.yart.tracer.RenderResult;
import ar.edu.itba.it.cg.yart.tracer.YATracer;
import ar.edu.itba.it.cg.yart.tracer.strategy.PathTracingStrategy;
import ar.edu.itba.it.cg.yart.ui.RenderWindow;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;
import ar.edu.itba.it.cg.yart.utils.config.YartConfigProvider;

public class YartApp {

	private final static YartConfigProvider configs = YartConfigProvider.getInstance();
	
	public static void main(String[] args) {
		
		final double distance = configs.getDistance();
		final int zoom = 2;
		
		RenderResult renderResult = new RenderResult();
		
		double rayDepth = YartDefaults.DEFAULT_RAY_DEPTH;
		int maxRayHops = YartDefaults.DEFAULT_MAX_HOPS;
		int numSamples = YartDefaults.DEFAULT_NUM_SAMPLES;
		int benchmarkRuns = 0;
		int cores = configs.getCoresQty();
		int bucketSize = configs.getBucketSize();
		boolean guiRender = false;
		String sceneFile = null;
		String outputFile = null;
		String imageName = null;
		String imageExtension = "png";
		CommandLineParser parser = null;
		CommandLine cmd = null;
		
		// We make the ray tracing the default type
		TracerType tracerType = TracerType.RAY_TRACER;
		
		Options options = new Options();
		options.addOption("th", "threads", true, "Number of threads to be used");
		options.addOption("bs", "bucketsize", true, "Bucket size to be used");
		options.addOption("pathtracer", false, "Enable Path Tracing");
		options.addOption("o", "output", true, "Output file's name");
		options.addOption("i", "input", true, "Input scene file");
		options.addOption("t", "time", false, "Print render time and triangle count in output image");
		options.addOption("s", "samples", true, "Number of samples for pathtracing. Must be a positive number");
		options.addOption("aa", "antialiasing", true, "Number of antialiasing samples. Must be a positive number");
		options.addOption("b", "benchmark", true, "Number of benchmark runs. Must be a positive number");
		options.addOption("d", "raydepth", true, "Ray depth. Must be a positive number");
		options.addOption("tr", "trace-depth", true, "Ray hops. Must be a positive integer number");
		options.addOption("g", "gui", false, "Display render progress in a window");
		options.addOption("h", "help", false, "Prints this help");
		
		parser = new BasicParser();
		try {
			// Parse command line parameters
			cmd = parser.parse(options, args);
			if (cmd.hasOption('h')) {
				printHelp(options);
			}
			
			if (cmd.hasOption('o')) {
				outputFile = cmd.getOptionValue('o');
				imageName = StringUtils.substringBeforeLast(outputFile, ".");
				imageExtension = StringUtils.substringAfterLast(outputFile, ".");
			}
			
			if (cmd.hasOption('i')) {
				sceneFile = cmd.getOptionValue('i');
			}
			
			if (cmd.hasOption('t')) {
				renderResult.setDisplayRenderTime(true);
			}
			
			if (cmd.hasOption("aa")) {
				numSamples = Integer.valueOf(cmd.getOptionValue("aa"));
				
				if (numSamples <= 0) {
					throw new ParseException("Number of antialiasing samples must be a positive integer");
				}
			}
			
			if (cmd.hasOption("d")) {
				rayDepth = Double.valueOf(cmd.getOptionValue("d"));
				
				if (rayDepth <= 0) {
					throw new ParseException("Ray depth must be a positive integer");
				}
			}
			
			if (cmd.hasOption('b')) {
				benchmarkRuns = Integer.valueOf(cmd.getOptionValue('b'));
				renderResult.setBenchmarkRuns(benchmarkRuns);
				
				if (benchmarkRuns <= 0) {
					throw new ParseException("Number of benchmark runs must be a positive integer");
				}
			}
			
			if (cmd.hasOption("bs")) {
				bucketSize = Integer.valueOf(cmd.getOptionValue("bs"));
				
				if (bucketSize <= 0) {
					throw new ParseException("Bucket size must be a positive integer");
				}
			}
			
			if (cmd.hasOption("th")) {
				cores = Integer.valueOf(cmd.getOptionValue("th"));
				
				if (cores <= 0) {
					throw new ParseException("Number of threads must be a positive integer");
				}
			}
			
			if (cmd.hasOption("pathtracer")) {
				tracerType = TracerType.PATH_TRACER;
				// no aa if using pathtracer
				if (cmd.hasOption("aa")) {
					throw new ParseException("You must not use antialiasing with pathtracing");
				}
				
				if (cmd.hasOption("s")) {
					numSamples = Integer.valueOf(cmd.getOptionValue("s"));
					if (numSamples < 1) {
						throw new ParseException("Number of samples must be a positive integer");
					}
				}
				
				if (cmd.hasOption("tr")) {
					maxRayHops = Integer.valueOf(cmd.getOptionValue("tr"));
					if (maxRayHops < 1) {
						throw new ParseException("Trace depth must be a positive integer");
					}
				}
			}
			
			guiRender = cmd.hasOption('g');
			
			if (guiRender && benchmarkRuns >= 1) {
				throw new ParseException("The GUI renderer can't be used in a benchmark");
			}
			
			// Apply command line parameters
			AbstractTracer.HOPS = maxRayHops;
			YATracer raytracer = new YATracer(renderResult, bucketSize, rayDepth, distance, zoom, numSamples, cores, new PathTracingStrategy());
			if (StringUtils.isEmpty(sceneFile)) {
				throw new ParseException("You must specify an input scene file");
			}
			else {
				renderResult.startSceneLoading();
				SceneParser sceneParser = new SceneParser(sceneFile, raytracer, tracerType);
				sceneParser.parse();
				renderResult.finishSceneLoading();
				System.out.println("Scene loading time: " + ImageSaver.getTimeString(renderResult.getSceneLoadingTime()));
			}

			if (guiRender) {
				new RenderWindow(raytracer);
			}

			if (benchmarkRuns >= 1) {
				benchmark(raytracer, benchmarkRuns);
			}
			else {
				renderResult = raytracer.render();
				raytracer.finishRaytracer();
				System.out.println("Render finished!");
				System.out.println("Preprocessing time: " + ImageSaver.getTimeString(renderResult.getPreprocessingTime()));
				System.out.println("Render time: " + ImageSaver.getTimeString(renderResult.getRenderTime()));
				System.out.println("-------------------");
				System.out.println("Total time: " + ImageSaver.getTimeString(renderResult.getTotalTime()));
			}
			
			if (!StringUtils.isEmpty(imageName)) {
				try {
					ImageSaver.saveImage(renderResult.getPixels(), imageName, imageExtension, renderResult);
				} catch (IOException e) {
					System.out.println("Failed to save render output: " + e.getMessage());
				}
			}
		} catch (ParseException ex) {
			System.out.println(ex.getMessage());
			printHelp(options);
		} catch (NumberFormatException ex) {
			printHelp(options);
		} catch (SceneParseException ex) {
			System.out.println("Failed to load scene file \"" + sceneFile + "\": " + ex.getMessage());
		}
	}
	
	private static void benchmark(final YATracer raytracer, final int runs) {
		if (raytracer == null || runs < 1) {
			return;
		}
		
		RenderResult result = null;
		long times[] = new long[runs];
		long totalTime = 0;
		for (int i = 0; i < runs; i++) {
			long currentTime = 0;
			System.out.print("Render " + (i+1) + "/" + runs + "... ");
			result = raytracer.render();
			currentTime = result.getRenderTime();
			times[i] = currentTime;
			totalTime += currentTime;
			System.out.println(ImageSaver.getTimeString(currentTime));
		}
		raytracer.finishRaytracer();
		result.setAverageTime((long) Math.ceil(totalTime / times.length));
		
		System.out.println("Benchmark finished!");
		System.out.println("Total runs: " + runs);
		System.out.println("Average time: " + ImageSaver.getTimeString(result.getAverageTime()));
		System.out.println("Renders per second: " + runs / (totalTime / 1000.0f));
		System.out.println("Total time: " + ImageSaver.getTimeString(totalTime));
	}
	
	private static void printHelp(final Options options) {
		new HelpFormatter().printHelp("Main", options);
		System.exit(0);
	}

}
