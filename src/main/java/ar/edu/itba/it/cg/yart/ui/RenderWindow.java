package ar.edu.itba.it.cg.yart.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ar.edu.itba.it.cg.yart.raytracer.RenderResult;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer.RaytracerCallbacks;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;

public class RenderWindow extends JFrame implements RaytracerCallbacks {

	private static final long serialVersionUID = 1L;
	
	private static final int MIN_WIDTH = 320;
	private static final int MIN_HEIGHT = 240;
	private static final int MAX_WIDTH = 800;
	private static final int MAX_HEIGHT = 600;
	
	private final BufferedImage bi;
	private final RayTracer raytracer;
	
	RenderImageResult resultPanel;
	JScrollPane scrollPane;
	
	public RenderWindow(RayTracer raytracer) {
		int width = raytracer.getHorizontalRes();
		int height = raytracer.getVerticalRes();
		
		this.raytracer = raytracer;
		
		raytracer.setCallbacks(this);
		
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(0);
		borderLayout.setVgap(0);
		
		setLayout(borderLayout);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		resultPanel = new RenderImageResult(bi);
		
		panel.add(resultPanel);
		
		scrollPane = new JScrollPane(panel);
		scrollPane.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		
		int w = width;
		int h = height;
		
		if (width > MAX_WIDTH) {
			w = MAX_WIDTH;
		}
		else if (width < MIN_WIDTH) {
			w = MIN_WIDTH;
		}
		
		if (height > MAX_HEIGHT) {
			h = MAX_HEIGHT;
		}
		else if (height < MIN_HEIGHT) {
			h = MIN_HEIGHT;
		}
		
		w += 10;
		h += 10;
		
		scrollPane.setPreferredSize(new Dimension(w, h));
		
		add(scrollPane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		
		pack();
		setLocationRelativeTo(null);
		
		setVisible(true);
	}

	@Override
	public void onBucketFinished(final Bucket bucket, final RenderResult result) {
		int xStart = bucket.getX();
		int xFinish = bucket.getX() + bucket.getWidth();
		int yStart = bucket.getY();
		int yFinish = bucket.getY() + bucket.getHeight();
		
		for (int y = yStart; y < yFinish; y++) {
			for (int x = xStart; x < xFinish; x++) {
				bi.setRGB(x, y, result.getPixels().get(x, y));
			}
		}
		
		resultPanel.repaint();
	}

	@Override
	public void onRenderFinished(RenderResult result) {
		ImageSaver.printRenderTime(bi, result);
		resultPanel.repaint();
	}

}