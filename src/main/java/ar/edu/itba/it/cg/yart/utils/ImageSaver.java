package ar.edu.itba.it.cg.yart.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.RenderResult;

public class ImageSaver {
	
	private static final Path BASE_PATH = Paths.get(".").normalize();
	
	public static void saveImage(final ArrayIntegerMatrix pixels, final String imageName, final String imageExtension, final RenderResult results) throws IOException {
		int w = pixels.cols();
		int h = pixels.rows();
		
		Path path = Paths.get(imageName + "." + imageExtension);
		
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				image.setRGB(x, y, pixels.get(x, y));
			}
		}

		File outputFile = BASE_PATH.resolve(path).toFile();
		outputFile.mkdirs();
		
		printRenderTime(image, results);
		
		ImageIO.write(image, imageExtension, outputFile);
	}
	
	public static void saveImage(final ArrayIntegerMatrix pixels, final String imageName, final String imageExtension) throws IOException {
		saveImage(pixels, imageName, imageExtension, null);
	}
	
	public static void printRenderTime(final BufferedImage image, final RenderResult results) {
		int w = image.getWidth();
		int h = image.getHeight();
		
		if (results != null && results.isDisplayRenderTime()) {
			String timeString = null;
			if (results.getBenchmarkRuns() > 1) {
				timeString = "Average time: " + getTimeString(results.getAverageTime());
			}
			else {
				timeString = "Render time: " + getTimeString(results.getRenderTime());
			}
			Graphics graphics = image.getGraphics();
			graphics.setColor(new Color(0.1f, 0.1f, 0.1f, 0.75f));
			graphics.fillRect(0, h - 30, w, 30);
	        graphics.setColor(Color.WHITE);
	        graphics.setFont(new Font("Arial", Font.PLAIN, 16));
	        graphics.drawString(timeString + " | Tris: " + results.getTriangles(), 10, h - 10);
		}
	}
	
	public static String getTimeString(long millis) {
		if (millis <= 5000) {
			return millis + "ms";
		}
		
		float seconds = millis / 1000.0f;
		long minutes = (long) seconds / 60;
		long hours = minutes / 60;
		
		String ret = "";
		
		if (hours > 0) {
			ret += hours + "h";
		}
		if (minutes > 0) {
			ret += (minutes % 60) + "m";
		}
		if (seconds > 0) {
			ret += new DecimalFormat("#.###").format((seconds % 60)) + "s";
		}
		
		return ret;
	}
}
