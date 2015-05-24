package ar.edu.itba.it.cg.yart;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import ar.edu.itba.it.cg.yart.parser.SceneParseException;
import ar.edu.itba.it.cg.yart.parser.SceneParser;
import ar.edu.itba.it.cg.yart.raytracer.RenderResult;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.ui.RenderWindow;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;
import ar.edu.itba.it.cg.yart.utils.config.YartConfigProvider;

public class YartApp {

	private final static YartConfigProvider configs = new YartConfigProvider();
	
	public static void main(String[] args) {
		
		int cores = configs.getCoresQty();
		final int bucketSize = configs.getBucketSize();
		final double tMax = configs.getMaxT();
		final double distance = configs.getDistance();
		final int zoom = 2;
		
		RenderResult renderResult = new RenderResult();
		
		int numSamples = 4;
		int benchmarkRuns = 0;
		boolean guiRender = false;
		String sceneFile = null;
		String outputFile = null;
		String imageName = null;
		String imageExtension = "png";
		CommandLineParser parser = null;
		CommandLine cmd = null;
		
		Options options = new Options();
		options.addOption("o", "output", true, "Output file's name");
		options.addOption("i", "input", true, "Input scene file");
		options.addOption("t", "time", false, "");
		options.addOption("aa", "antialiasing", true, "");
		options.addOption("b", "benchmark", true, "");
		options.addOption("d", "raydepth", true, "");
		options.addOption("g", "gui", false, "");
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
			
			if (cmd.hasOption('b')) {
				benchmarkRuns = Integer.valueOf(cmd.getOptionValue('b'));
				
				if (benchmarkRuns <= 0) {
					throw new ParseException("Number of benchmark runs must be a positive integer");
				}
			}
			
			guiRender = cmd.hasOption('g');
			
			if (guiRender && benchmarkRuns >= 1) {
				throw new ParseException("The GUI renderer can't be used in a benchmark");
			}
			
			// Apply command line parameters
			RayTracer raytracer = new SimpleRayTracer(renderResult, bucketSize, tMax, distance, zoom, numSamples, cores);
			if (StringUtils.isEmpty(sceneFile)) {
				World w = new World();
				w.buildTestWorld();
				raytracer.setWorld(w);
			}
			else {
				renderResult.startSceneLoading();
				SceneParser sceneParser = new SceneParser(sceneFile, raytracer);
				sceneParser.parse();
				renderResult.finishSceneLoading();
				System.out.println("Scene loading time: " + renderResult.getSceneLoadingTime());
			}

			if (guiRender) {
				new RenderWindow(raytracer);
			}

			if (benchmarkRuns >= 1) {
				benchmark(raytracer, benchmarkRuns);
			}
			else {
				renderResult = raytracer.render();
				System.out.println("Render finished!");
				System.out.println("Preprocessing time: " + renderResult.getPreprocessingTime());
				System.out.println("Render time: " + renderResult.getRenderTime());
				System.out.println("-------------------");
				System.out.println("Total time: " + renderResult.getTotalTime());
			}
			
			if (!StringUtils.isEmpty(imageName)) {
				ImageSaver imageSaver = new ImageSaver();
				imageSaver.saveImage(renderResult.getPixels(), imageName, imageExtension, renderResult);
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
	
	private static void benchmark(final RayTracer raytracer, final int runs) {
		if (raytracer == null || runs < 1) {
			return;
		}
		
		long times[] = new long[runs];
		long totalTime = 0;
		for (int i = 0; i < runs; i++) {
			long currentTime = 0;
			System.out.print("Render " + (i+1) + "/" + runs + "... ");
			RenderResult result = raytracer.render();
			currentTime = result.getRenderTime();
			times[i] = currentTime;
			totalTime += currentTime;
			System.out.println(currentTime + "ms");
		}
		
		System.out.println("Benchmark finished!");
		System.out.println("Total runs: " + runs);
		System.out.println("Average time: " + (long) Math.ceil(totalTime / times.length) + " ms");
		System.out.println("Renders per second: " + runs / (totalTime / 1000.0f));
		System.out.println("Total time: " + totalTime + " ms");
	}
	
	private static void printHelp(final Options options) {
		new HelpFormatter().printHelp("Main", options);
		System.exit(0);
	}

}
