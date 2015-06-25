package ar.edu.itba.it.cg.yart.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;

import ar.edu.itba.it.cg.yart.utils.ImageSaver;

public class StatusPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final JLabel lblLoadingTime;
	private final JLabel lblPreprocessingTime;
	private final JLabel lblRenderTime;
	
	private final JLabel txtLoadingTime;
	private final JLabel txtPreprocessingTime;
	private final JLabel txtRenderTime;
	
	public StatusPanel() {
		setLayout(new SpringLayout());
		
		lblLoadingTime = new JLabel("Loading time", JLabel.LEADING);
		lblPreprocessingTime = new JLabel("Pre-processing time", JLabel.LEADING);
		lblRenderTime = new JLabel("Render time", JLabel.LEADING);
		
		txtLoadingTime = new JLabel("processing...", JLabel.TRAILING);
		txtPreprocessingTime = new JLabel("processing...", JLabel.TRAILING);
		txtRenderTime = new JLabel("processing...", JLabel.TRAILING);
		
		lblLoadingTime.setLabelFor(txtLoadingTime);
		lblPreprocessingTime.setLabelFor(txtPreprocessingTime);
		lblRenderTime.setLabelFor(txtRenderTime);

		add(lblLoadingTime);
		add(txtLoadingTime);
		
		add(lblPreprocessingTime);
		add(txtPreprocessingTime);
		
		add(lblRenderTime);
		add(txtRenderTime);
		
		SpringUtilities.makeCompactGrid(this, 3, 2, 15, 15, 15, 15);
	}
	
	public void clearResults() {
		txtLoadingTime.setText("");
		txtPreprocessingTime.setText("");
		txtRenderTime.setText("");
	}
	
	public void setLoadingTime(final long loadingTimeMillis) {
		if (loadingTimeMillis <= 0) {
			txtLoadingTime.setText("processing...");
		}
		else {
			txtLoadingTime.setText(ImageSaver.getTimeString(loadingTimeMillis));
		}
	}
	
	public void setPreprocessingTime(final long preprocessingTimeMillis) {
		if (preprocessingTimeMillis <= 0) {
			txtPreprocessingTime.setText("processing...");
		}
		else {
			txtPreprocessingTime.setText(ImageSaver.getTimeString(preprocessingTimeMillis));
		}
	}
	
	public void setRenderTime(final long renderTimeMillis) {
		if (renderTimeMillis <= 0) {
			txtRenderTime.setText("processing...");
		}
		else {
			txtRenderTime.setText(ImageSaver.getTimeString(renderTimeMillis));
		}
	}
}
