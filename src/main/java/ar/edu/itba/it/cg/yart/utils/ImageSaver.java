package ar.edu.itba.it.cg.yart.utils;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.matrix.ArrayColorMatrix;
import ar.edu.itba.it.cg.yart.tracer.RenderResult;
import ar.edu.itba.it.cg.yart.tracer.tonemapper.ToneMapper;

public class ImageSaver {
	
	private static final Path BASE_PATH = Paths.get(".").normalize();
	
	public static void saveImage(final ArrayColorMatrix pixels, final String imageName, final String imageExtension, final ToneMapper toneMapper, final RenderResult results) throws IOException {
		int w = pixels.cols();
		int h = pixels.rows();
		
		Path path = Paths.get(imageName + "." + imageExtension);
		
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Color color = pixels.get(x, y);
				if (toneMapper != null) {
					color = toneMapper.map(color);
				}
				image.setRGB(x, y, color.toInt());
			}
		}

		File outputFile = BASE_PATH.resolve(path).toFile();
		outputFile.mkdirs();

		if (results != null) {
			printRenderTime(image, results);
		}

		ImageIO.write(image, imageExtension, outputFile);
	}
	
	public static void saveImage(final ArrayColorMatrix pixels, final String imageName, final ToneMapper toneMapper, final String imageExtension) throws IOException {
		saveImage(pixels, imageName, imageExtension, toneMapper, null);
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

			// FIXME AWT Color conflicts with our Color
			Graphics graphics = image.getGraphics();
			graphics.setColor(new java.awt.Color(0.1f, 0.1f, 0.1f, 0.75f));
			graphics.fillRect(0, h - 30, w, 30);
	        graphics.setColor(java.awt.Color.WHITE);
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
