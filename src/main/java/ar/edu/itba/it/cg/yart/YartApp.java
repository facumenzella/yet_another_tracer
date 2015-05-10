package ar.edu.itba.it.cg.yart;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.parser.SceneBuilder;
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
		final int bucketSize = 128;
		final double tMax = 1000;
		final double distance = 500;
		final int zoom = 1;
		final int numSamples = 16;
		
		RenderResult renderResult = new RenderResult();
		
		boolean guiRender = false;
		String sceneFile = null;
		String outputFile = null;
		String inputFile = null;
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
				final String output = cmd.getOptionValue('o');
				imageName = StringUtils.substringBeforeLast(output, ".");
				imageExtension = StringUtils.substringAfterLast(output, ".");
			}
			
			if (cmd.hasOption('i')) {
				inputFile = cmd.getOptionValue('i');
			}
			
			guiRender = cmd.hasOption('g');
			
			// Apply command line parameters
			RayTracer raytracer = new SimpleRayTracer(renderResult, bucketSize, tMax, distance, zoom, numSamples, cores);
			if (StringUtils.isEmpty(inputFile)) {
				World w = new World();
				w.buildTestWorld();
				raytracer.setWorld(w);
			}
			else {
				renderResult.startSceneLoading();
				SceneBuilder builder = new SceneBuilder();
				builder.buildRayTracer(raytracer, new SceneParser(inputFile));
				renderResult.finishSceneLoading();
			}

			if (guiRender) {
				new RenderWindow(raytracer);
			}

			renderResult = raytracer.render();
			System.out.println(renderResult.getRenderTime());
		
			ImageSaver imageSaver = new ImageSaver();
			imageSaver.saveImage(renderResult.getPixels(), imageName, imageExtension);
		} catch (org.apache.commons.cli.ParseException ex) {
			System.out.println(ex.getMessage());
			printHelp(options);
		} catch (java.lang.NumberFormatException ex) {
			printHelp(options);
		}
	}
	
	private static void printHelp(final Options options) {
		new HelpFormatter().printHelp("Main", options);
		System.exit(0);
	}

}
