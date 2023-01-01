/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package caimgmaker;

import java.awt.Color;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

import org.apache.commons.math3.FieldElement;

import caimgmaker.colormap.ColorMapper;
import cellularautomata.MinAndMax;
import cellularautomata.model2d.Model2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.ObjectModel2D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.NumericModel3D;

public class ImgMaker {
	
	private long imgsPerFolder = 10000;
	private long millisecondsBetweenBackups;
	private boolean saveBackupsAutomatically = true;
	private volatile boolean backupRequested = false;
	
	public ImgMaker() {
		saveBackupsAutomatically = false;
	}
	
	public ImgMaker(long millisecondsBetweenBackups) {
		this.millisecondsBetweenBackups = millisecondsBetweenBackups;
	}
	
	public void createImages(IntModel2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try { 
			long step = ca.getStep();
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			String imgPath = path + "/" + colorMapper.getColormapName() + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					int[] minAndMaxValue = ca.getMinAndMax();
					System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(ca, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, caName + "_" + step + ".png");
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}		
				step++;
				currentStepLeap++;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createImages(LongModel2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
			long step = ca.getStep();
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			String imgPath = path + "/" + colorMapper.getColormapName() + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					long[] minAndMaxValue = ca.getMinAndMax();
					System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(ca, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, caName + "_" + step + ".png");
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}		
					System.out.println();
				}
				step++;
				currentStepLeap++;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void createImages(
			NumericModel2D<Number_Type> ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			String imgPath = path + "/" + colorMapper.getColormapName() + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					MinAndMax<Number_Type> minAndMaxValue = ca.getMinAndMax();
					System.out.println("Min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
					ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(ca, minAndMaxValue.getMin(), minAndMaxValue.getMax());
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, caName + "_" + step + ".png");
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}		
					System.out.println();
				}
				step++;
				currentStepLeap++;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createEvenOddImages(IntModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
			boolean isEvenStep = step%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			String imgPath = path + "/" + colorMapper.getColormapName() + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					if (!omitEven) {
						int[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(isEvenStep);			
						if (evenMinAndMaxValue != null) {
							System.out.println("Even positions: min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + evenMinAndMaxValue[1]);
							ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(!isEvenStep);
						if (oddMinAndMaxValue != null) {
							System.out.println("Odd positions: min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + oddMinAndMaxValue[1]);
							ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}	
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createEvenOddImages(LongModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
			boolean isEvenStep = step%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			String imgPath = path + "/" + colorMapper.getColormapName() + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					if (!omitEven) {
						long[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(isEvenStep);			
						if (evenMinAndMaxValue != null) {
							System.out.println("Even positions: min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + evenMinAndMaxValue[1]);
							ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						long[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(!isEvenStep);
						if (oddMinAndMaxValue != null) {
							System.out.println("Odd positions: min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + oddMinAndMaxValue[1]);
							ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}	
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void createEvenOddImages(NumericModel2D<Number_Type> ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
			boolean isEvenStep = step%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			String imgPath = path + "/" + colorMapper.getColormapName() + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					if (!omitEven) {
						MinAndMax<Number_Type> evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(isEvenStep);			
						if (evenMinAndMaxValue != null) {
							System.out.println("Even positions: min value: " + evenMinAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + evenMinAndMaxValue.getMax());
							ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca, evenMinAndMaxValue.getMin(), evenMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						MinAndMax<Number_Type> oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(!isEvenStep);
						if (oddMinAndMaxValue != null) {
							System.out.println("Odd positions: min value: " + oddMinAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + oddMinAndMaxValue.getMax());
							ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca, oddMinAndMaxValue.getMin(), oddMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}	
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createImagesFromEvenOddX(IntModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
			boolean isEvenStep = step%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			String imgPath = path + "/" + colorMapper.getColormapName() + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					if (!omitEven) {
						int[] evenMinAndMaxValue = ca.getMinAndMaxAtEvenOddX(isEvenStep);			
						if (evenMinAndMaxValue != null) {
							System.out.println("Even " + xLabel + " positions: min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + evenMinAndMaxValue[1]);
							ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
							createImageFromEvenOrOddXPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "even_" + xLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "even_" + xLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddMinAndMaxValue = ca.getMinAndMaxAtEvenOddX(!isEvenStep);
						if (oddMinAndMaxValue != null) {
							System.out.println("Odd " + xLabel + " positions: min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + oddMinAndMaxValue[1]);
							ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
							createImageFromEvenOrOddXPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "odd_" + xLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									imgPath + "odd_" + xLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}	
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createScanningAndZCrossSectionImages(IntModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			int[] scanCoords = scanInitialCoords;
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
			path += "/" + colorMapper.getColormapName() + "/";
			String[] scanImgPaths = new String[] { 
					path + xLabel + "_scan/",
					path + yLabel + "_scan/",
					path + zLabel + "_scan/" };
			String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					//x scan
					if (scanCoords[0] < ca.getMinX()) {
						scanCoords[0] = ca.getMaxX();
					}
					System.out.println("Scan " + xLabel + ": " + scanCoords[0]);
					IntModel2D crossSection = ca.crossSectionAtX(scanCoords[0]);
					int[] minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[0]--;
					//y scan
					if (scanCoords[1] < ca.getMinY()) {
						scanCoords[1] = ca.getMaxY();
					}
					System.out.println("Scan " + yLabel + ": " + scanCoords[1]);
					crossSection = ca.crossSectionAtY(scanCoords[1]);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[1]--;
					//z scan
					if (scanCoords[2] < ca.getMinZ()) {
						scanCoords[2] = ca.getMaxZ();
					}
					System.out.println("Scan " + zLabel + ": " + scanCoords[2]);
					crossSection = ca.crossSectionAtZ(scanCoords[2]);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[2]--;
					//cross section
					crossSection = ca.crossSectionAtZ(crossSectionZ);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
							caName + "_" + step + ".png");
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}
				step++;
				currentStepLeap++;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createScanningAndZCrossSectionImages(LongModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			int[] scanCoords = scanInitialCoords;
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
			path += "/" + colorMapper.getColormapName() + "/";
			String[] scanImgPaths = new String[] { 
					path + xLabel + "_scan/",
					path + yLabel + "_scan/",
					path + zLabel + "_scan/" };
			String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					//x scan
					if (scanCoords[0] < ca.getMinX()) {
						scanCoords[0] = ca.getMaxX();
					}
					System.out.println("Scan " + xLabel + ": " + scanCoords[0]);
					LongModel2D crossSection = ca.crossSectionAtX(scanCoords[0]);
					long[] minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[0]--;
					//y scan
					if (scanCoords[1] < ca.getMinY()) {
						scanCoords[1] = ca.getMaxY();
					}
					System.out.println("Scan " + yLabel + ": " + scanCoords[1]);
					crossSection = ca.crossSectionAtY(scanCoords[1]);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[1]--;
					//z scan
					if (scanCoords[2] < ca.getMinZ()) {
						scanCoords[2] = ca.getMaxZ();
					}
					System.out.println("Scan " + zLabel + ": " + scanCoords[2]);
					crossSection = ca.crossSectionAtZ(scanCoords[2]);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[2]--;
					//cross section
					crossSection = ca.crossSectionAtZ(crossSectionZ);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
							caName + "_" + step + ".png");
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}
				step++;
				currentStepLeap++;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void createScanningAndZCrossSectionImages(NumericModel3D<Number_Type> ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			int[] scanCoords = scanInitialCoords;
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
			path += "/" + colorMapper.getColormapName() + "/";
			String[] scanImgPaths = new String[] { 
					path + xLabel + "_scan/",
					path + yLabel + "_scan/",
					path + zLabel + "_scan/" };
			String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					//x scan
					if (scanCoords[0] < ca.getMinX()) {
						scanCoords[0] = ca.getMaxX();
					}
					System.out.println("Scan " + xLabel + ": " + scanCoords[0]);
					NumericModel2D<Number_Type> crossSection = ca.crossSectionAtX(scanCoords[0]);
					MinAndMax<Number_Type> minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
					ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[0]--;
					//y scan
					if (scanCoords[1] < ca.getMinY()) {
						scanCoords[1] = ca.getMaxY();
					}
					System.out.println("Scan " + yLabel + ": " + scanCoords[1]);
					crossSection = ca.crossSectionAtY(scanCoords[1]);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[1]--;
					//z scan
					if (scanCoords[2] < ca.getMinZ()) {
						scanCoords[2] = ca.getMaxZ();
					}
					System.out.println("Scan " + zLabel + ": " + scanCoords[2]);
					crossSection = ca.crossSectionAtZ(scanCoords[2]);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Scan: min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + numberedFolder, caName + "_" + step + ".png");
					scanCoords[2]--;
					//cross section
					crossSection = ca.crossSectionAtZ(crossSectionZ);
					minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Cross section: min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
					colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
					createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
							caName + "_" + step + ".png");
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}
				step++;
				currentStepLeap++;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createScanningAndZCrossSectionEvenOddImages(IntModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			boolean isEvenStep = step%2 == 0;
			boolean isEvenCrossSectionZ = crossSectionZ%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			int[] scanCoords = scanInitialCoords;		
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
			path += "/" + colorMapper.getColormapName() + "/";
			String[] scanImgPaths = new String[] { 
					path + xLabel + "_scan/",
					path + yLabel + "_scan/",
					path + zLabel + "_scan/" };
			String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";		
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					//x scan
					if (scanCoords[0] < ca.getMinY())
						scanCoords[0] = ca.getMaxY();	
					IntModel2D crossSection = ca.crossSectionAtX(scanCoords[0]);
					System.out.println("Scan " + xLabel + ": " + scanCoords[0]);
					if (!omitEven) {
						int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[0]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[0]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[0]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[0]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[0]--;
					//y scan
					if (scanCoords[1] < ca.getMinY())
						scanCoords[1] = ca.getMaxY();	
					crossSection = ca.crossSectionAtY(scanCoords[1]);
					System.out.println("Scan " + yLabel + ": " + scanCoords[1]);
					if (!omitEven) {
						int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[1]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[1]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[1]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[1]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[1]--;
					//z scan
					if (scanCoords[2] < ca.getMinZ())
						scanCoords[2] = ca.getMaxZ();	
					crossSection = ca.crossSectionAtZ(scanCoords[2]);
					System.out.println("Scan " + zLabel + ": " + scanCoords[2]);
					if (!omitEven) {
						int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[2]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[2]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[2]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[2]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[2]--;
					//cross section
					crossSection = ca.crossSectionAtZ(crossSectionZ);
					if (!omitEven) {
						int[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep == isEvenCrossSectionZ);
						if (evenCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
							ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenCrossSectionColorModel, isEvenStep == isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep != isEvenCrossSectionZ);
						if (oddCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
							ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddCrossSectionColorModel, isEvenStep != isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
											crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createScanningAndZCrossSectionEvenOddImages(LongModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {
			long step = ca.getStep();
			boolean isEvenStep = step%2 == 0;
			boolean isEvenCrossSectionZ = crossSectionZ%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			int[] scanCoords = scanInitialCoords;		
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
			path += "/" + colorMapper.getColormapName() + "/";
			String[] scanImgPaths = new String[] { 
					path + xLabel + "_scan/",
					path + yLabel + "_scan/",
					path + zLabel + "_scan/" };
			String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";		
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					//x scan
					if (scanCoords[0] < ca.getMinY())
						scanCoords[0] = ca.getMaxY();	
					LongModel2D crossSection = ca.crossSectionAtX(scanCoords[0]);
					System.out.println("Scan " + xLabel + ": " + scanCoords[0]);
					if (!omitEven) {
						long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[0]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[0]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[0]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[0]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[0]--;
					//y scan
					if (scanCoords[1] < ca.getMinY())
						scanCoords[1] = ca.getMaxY();	
					crossSection = ca.crossSectionAtY(scanCoords[1]);
					System.out.println("Scan " + yLabel + ": " + scanCoords[1]);
					if (!omitEven) {
						long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[1]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[1]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[1]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[1]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[1]--;
					//z scan
					if (scanCoords[2] < ca.getMinZ())
						scanCoords[2] = ca.getMaxZ();	
					crossSection = ca.crossSectionAtZ(scanCoords[2]);
					System.out.println("Scan " + zLabel + ": " + scanCoords[2]);
					if (!omitEven) {
						long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[2]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[2]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[2]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[2]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[2]--;
					//cross section
					crossSection = ca.crossSectionAtZ(crossSectionZ);
					if (!omitEven) {
						long[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep == isEvenCrossSectionZ);
						if (evenCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
							ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(evenCrossSectionColorModel, isEvenStep == isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						long[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep != isEvenCrossSectionZ);
						if (oddCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
							ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(oddCrossSectionColorModel, isEvenStep != isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
											crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void createScanningAndZCrossSectionEvenOddImages(NumericModel3D<Number_Type> ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();	
		try {	
			long step = ca.getStep();
			boolean isEvenStep = step%2 == 0;
			boolean isEvenCrossSectionZ = crossSectionZ%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			int[] scanCoords = scanInitialCoords;		
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
			path += "/" + colorMapper.getColormapName() + "/";
			String[] scanImgPaths = new String[] { 
					path + xLabel + "_scan/",
					path + yLabel + "_scan/",
					path + zLabel + "_scan/" };
			String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";	
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;	
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					//x scan
					if (scanCoords[0] < ca.getMinY())
						scanCoords[0] = ca.getMaxY();	
					NumericModel2D<Number_Type> crossSection = ca.crossSectionAtX(scanCoords[0]);
					System.out.println("Scan " + xLabel + ": " + scanCoords[0]);
					if (!omitEven) {
						MinAndMax<Number_Type> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[0]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue.getMin() + ", max value: " + evenScanMinAndMaxValue.getMax());
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[0]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						MinAndMax<Number_Type> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[0]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[0]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[0]--;
					//y scan
					if (scanCoords[1] < ca.getMinY())
						scanCoords[1] = ca.getMaxY();	
					crossSection = ca.crossSectionAtY(scanCoords[1]);
					System.out.println("Scan " + yLabel + ": " + scanCoords[1]);
					if (!omitEven) {
						MinAndMax<Number_Type> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[1]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue.getMin() + ", max value: " + evenScanMinAndMaxValue.getMax());
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[1]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						MinAndMax<Number_Type> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[1]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[1]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[1]--;
					//z scan
					if (scanCoords[2] < ca.getMinZ())
						scanCoords[2] = ca.getMaxZ();	
					crossSection = ca.crossSectionAtZ(scanCoords[2]);
					System.out.println("Scan " + zLabel + ": " + scanCoords[2]);
					if (!omitEven) {
						MinAndMax<Number_Type> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[2]%2 == 0 == isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue.getMin() + ", max value: " + evenScanMinAndMaxValue.getMax());
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(evenScanColorModel, scanCoords[2]%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						MinAndMax<Number_Type> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(scanCoords[2]%2 == 0 != isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(oddScanColorModel, scanCoords[2]%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[2]--;
					//cross section
					crossSection = ca.crossSectionAtZ(crossSectionZ);
					if (!omitEven) {
						MinAndMax<Number_Type> evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep == isEvenCrossSectionZ);
						if (evenCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue.getMin() + ", max value: " + evenCrossSectionMinAndMaxValue.getMax());
							ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue.getMin(), evenCrossSectionMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(evenCrossSectionColorModel, isEvenStep == isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						MinAndMax<Number_Type> oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep != isEvenCrossSectionZ);
						if (oddCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue.getMin() + ", max value: " + oddCrossSectionMinAndMaxValue.getMax());
							ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue.getMin(), oddCrossSectionMinAndMaxValue.getMax());
							createImageFromEvenOrOddPositions(oddCrossSectionColorModel, isEvenStep != isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
											crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createScanningAndZCrossSectionImagesFromEvenOddY(LongModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();		
		try {
			long step = ca.getStep();
			boolean isEvenStep = step%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			int[] scanCoords = scanInitialCoords;
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
			path += "/" + colorMapper.getColormapName() + "/";
			String[] scanImgPaths = new String[] { 
					path + xLabel + "_scan/",
					path + yLabel + "_scan/",
					path + zLabel + "_scan/" };
			String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";		
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;	
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					//x scan
					if (scanCoords[0] < ca.getMinY())
						scanCoords[0] = ca.getMaxY();	
					LongModel2D crossSection = ca.crossSectionAtX(scanCoords[0]);
					System.out.println("Scan " + xLabel + ": " + scanCoords[0]);
					if (!omitEven) {
						long[] evenScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even " + yLabel + " positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(evenScanColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						long[] oddScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd " + yLabel + " positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(oddScanColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[0]--;
					//y scan
					if (scanCoords[1] < ca.getMinY())
						scanCoords[1] = ca.getMaxY();	
					crossSection = ca.crossSectionAtY(scanCoords[1]);
					System.out.println("Scan " + yLabel + ": " + scanCoords[1]);
					boolean isEvenYScan = scanCoords[1]%2 == 0 == isEvenStep;
					if (isEvenYScan && !omitEven || !isEvenYScan && !omitOdd) {
						long[] scanMinAndMaxValue = crossSection.getMinAndMax();
						System.out.println("Scan even " + yLabel + " positions: min value: " + scanMinAndMaxValue[0] + ", max value: " + scanMinAndMaxValue[1]);
						ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
						createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
								scanImgPaths[1] + (isEvenYScan ? "even" : "odd") + "_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
					}
					scanCoords[1]--;
					//z scan
					if (scanCoords[2] < ca.getMinZ())
						scanCoords[2] = ca.getMaxZ();	
					crossSection = ca.crossSectionAtZ(scanCoords[2]);
					System.out.println("Scan " + zLabel + ": " + scanCoords[2]);
					if (!omitEven) {
						long[] evenScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even " + yLabel + " positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(evenScanColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						long[] oddScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd " + yLabel + " positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(oddScanColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[2]--;
					//cross section
					crossSection = ca.crossSectionAtZ(crossSectionZ);
					if (!omitEven) {
						long[] evenCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
						if (evenCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section even " + yLabel + " positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
							ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(evenCrossSectionColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						long[] oddCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
						if (oddCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section odd " + yLabel + " positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
							ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(oddCrossSectionColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
											crossSectionImgPath + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public void createScanningAndZCrossSectionImagesFromEvenOddY(IntModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		try {		
			long step = ca.getStep();
			boolean isEvenStep = step%2 == 0;
			int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
			int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
			int[] scanCoords = scanInitialCoords;
			String caName = ca.getName();
			String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
			path += "/" + colorMapper.getColormapName() + "/";
			String[] scanImgPaths = new String[] { 
					path + xLabel + "_scan/",
					path + yLabel + "_scan/",
					path + zLabel + "_scan/" };
			String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";		
			int currentStepLeap = (int) (step%stepLeap);
			if (currentStepLeap == 0) {
				currentStepLeap = stepLeap;
			}
			Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;	
			long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
			do {
				System.out.println("Step: " + step);
				if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
					currentStepLeap = 0;
					if (changed != null && !changed) {
						createLastImage = false;
					}
					int minX = ca.getMinX(), maxX = ca.getMaxX(), 
							minY = ca.getMinY(), maxY = ca.getMaxY();
					System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + xLabel + ": " + maxX);
					//x scan
					if (scanCoords[0] < ca.getMinY())
						scanCoords[0] = ca.getMaxY();	
					IntModel2D crossSection = ca.crossSectionAtX(scanCoords[0]);
					System.out.println("Scan " + xLabel + ": " + scanCoords[0]);
					if (!omitEven) {
						int[] evenScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even " + yLabel + " positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(evenScanColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd " + yLabel + " positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(oddScanColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[0] + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[0]--;
					//y scan
					if (scanCoords[1] < ca.getMinY())
						scanCoords[1] = ca.getMaxY();	
					crossSection = ca.crossSectionAtY(scanCoords[1]);
					System.out.println("Scan " + yLabel + ": " + scanCoords[1]);
					boolean isEvenYScan = scanCoords[1]%2 == 0 == isEvenStep;
					if (isEvenYScan && !omitEven || !isEvenYScan && !omitOdd) {
						int[] scanMinAndMaxValue = crossSection.getMinAndMax();
						if (scanMinAndMaxValue != null) {
							System.out.println("Scan even " + yLabel + " positions: min value: " + scanMinAndMaxValue[0] + ", max value: " + scanMinAndMaxValue[1]);
							ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
							createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + (isEvenYScan ? "even" : "odd") + "_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[1] + (isEvenYScan ? "even" : "odd") + "_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[1]--;
					//z scan
					if (scanCoords[2] < ca.getMinZ())
						scanCoords[2] = ca.getMaxZ();	
					crossSection = ca.crossSectionAtZ(scanCoords[2]);
					System.out.println("Scan " + zLabel + ": " + scanCoords[2]);
					if (!omitEven) {
						int[] evenScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
						if (evenScanMinAndMaxValue != null) {
							System.out.println("Scan even " + yLabel + " positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
							ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(evenScanColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
						if (oddScanMinAndMaxValue != null) {
							System.out.println("Scan odd " + yLabel + " positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
							ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(oddScanColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									scanImgPaths[2] + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					scanCoords[2]--;
					//cross section
					crossSection = ca.crossSectionAtZ(crossSectionZ);
					if (!omitEven) {
						int[] evenCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
						if (evenCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section even " + yLabel + " positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
							ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(evenCrossSectionColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "even_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}
					if (!omitOdd) {
						int[] oddCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
						if (oddCrossSectionMinAndMaxValue != null) {
							System.out.println("Cross section odd " + yLabel + " positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
							ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
							createImageFromEvenOrOddYPositions(oddCrossSectionColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
											crossSectionImgPath + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						} else {
							createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "odd_" + yLabel + "/" + numberedFolder, caName + "_" + step + ".png");
						}
					}			
					folderImageCount++;
					if (folderImageCount == imgsPerFolder) {
						numberedFolder++;
						folderImageCount = 0;
					}		
					boolean backUp = false;
					if (saveBackupsAutomatically) {
						backUp = System.currentTimeMillis() >= nextBckTime;
						if (backUp) {
							nextBckTime += millisecondsBetweenBackups;
						}
					}
					if (backupRequested) {
						backUp = true;
						backupRequested = false;
					}
					if (backUp) {
						String backupName = ca.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
						ca.backUp(backupPath, backupName);		
						System.out.println("Backing up finished");
					}
					System.out.println();
				}
				step++;
				currentStepLeap++;
				isEvenStep = !isEvenStep;
			} while ((changed = ca.nextStep()) == null || changed || createLastImage);
			System.out.println("Finished!");
		} finally {
			stdIn.stop();
			inputThread.join();
		}
	}
	
	public static int getModelPositionSize(int minX, int maxX, int minY, int maxY, int preferredMaxWidth, int preferredMaxHeight) {
		int ySize = 1;
		int height = maxY - minY + 1;
		if (height > 0) {
			ySize = preferredMaxHeight/height;
        }
		if (ySize == 0) ySize = 1;
		int xSize = 1;
		int width = maxX - minX + 1;
		if (width > 0) {
			xSize = preferredMaxWidth/width;
        }
		if (xSize == 0) xSize = 1;
		return Math.min(xSize, ySize);
	}
	
	private static int getModelPositionSize(Model2D grid, int preferredMaxWidth, int preferredMaxHeight) {
		int ySize = 1;
		int gridHeight = grid.getMaxY() - grid.getMinY() + 1;
		if (gridHeight > 0) {
			ySize = preferredMaxHeight/gridHeight;
        }
		if (ySize == 0) ySize = 1;
		int xSize = 1;
		int gridWidth = grid.getMaxX() - grid.getMinX() + 1;
		if (gridWidth > 0) {
			xSize = preferredMaxWidth/gridWidth;
        }
		if (xSize == 0) xSize = 1;
		return Math.min(xSize, ySize);
	}
	
	public static void createImage(ObjectModel2D<Color> grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImage(grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImage(ObjectModel2D<Color> grid, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];
		int canvasTopMargin = height - dataHeight;
		int canvasRightMargin = width - dataWidth;
		int gridTopMargin = 0;
		int framedModelMinY, framedModelMaxY;
		int gridMaxY = grid.getMaxY(), gridMinY = grid.getMinY();
		if (maxY > gridMaxY) {
			gridTopMargin = (maxY - gridMaxY) * gridPositionSize;
			framedModelMaxY = gridMaxY;
		} else {
			framedModelMaxY = maxY;
		}
		if (minY < gridMinY) {
			framedModelMinY = gridMinY;
		} else {
			framedModelMinY = minY;
		}
		int dataIndex = (canvasTopMargin + gridTopMargin) * width * 3;
		for (int y = framedModelMaxY; y >= framedModelMinY; y--) {
			int gridMinXAtY = grid.getMinX(y);
			int gridMaxXAtY = grid.getMaxX(y);
			int dataLeftMargin = 0, gridRightMargin = 0;
			int framedModelMinXAtY, framedModelMaxXAtY;
			if (minX < gridMinXAtY) {
				dataLeftMargin = (gridMinXAtY - minX) * gridPositionSize * 3;
				framedModelMinXAtY = gridMinXAtY;
			} else {
				framedModelMinXAtY = minX;
			}
			if (maxX > gridMaxXAtY) {
				gridRightMargin = (maxX - gridMaxXAtY) * gridPositionSize;
				framedModelMaxXAtY = gridMaxXAtY;
			} else {
				framedModelMaxXAtY = maxX;
			}
			int dataRightMargin = (canvasRightMargin + gridRightMargin) * 3;
			dataIndex += dataLeftMargin;
			int firstDataIndexToCopyFrom = dataIndex;
			for (int x = framedModelMinXAtY; x <= framedModelMaxXAtY; x++) {
				java.awt.Color c = grid.getFromPosition(x, y);
				byte r = (byte) c.getRed(), g = (byte) c.getGreen(), b = (byte) c.getBlue();
				for (int j = 0; j < gridPositionSize; j++) {
					pixelData[dataIndex++] = r;
					pixelData[dataIndex++] = g;
					pixelData[dataIndex++] = b;
				}				
			}
			dataIndex += dataRightMargin;
			for (int i = 1; i < gridPositionSize; i++) {
				dataIndex += dataLeftMargin;
				int dataIndexToCopyFrom = firstDataIndexToCopyFrom;
				for (int x = framedModelMinXAtY; x <= framedModelMaxXAtY; x++) {
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
					}				
				}
				dataIndex += dataRightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public static void createEmptyImage(int minX, int maxX, int minY, int maxY, String path, String name) 
			throws Exception {
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		createEmptyImage(minX, maxX, minY, maxY, 1, width, height, path, name);
	}
	
	public static void createEmptyImage(int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createEmptyImage(minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createEmptyImage(int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		
		int framedModelWidth = maxX - minX + 1;
		int framedModelHeight = maxY - minY + 1;	
		int framedModelWidthInPixels = framedModelWidth * gridPositionSize;
		int framedModelHeightInPixels = framedModelHeight * gridPositionSize;	
		int imageWidth = Math.max(framedModelWidthInPixels, minWidth);
		int imageHeight = Math.max(framedModelHeightInPixels, minHeight);	
		long longByteCount = (long)imageWidth * imageHeight * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];	
		saveAsPngImage(pixelData, imageWidth, imageHeight, path, name);
	}
	
	public static void createImage(ObjectModel2D<Color> grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(grid, minWidth, minHeight);
		createImage(grid, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImage(ObjectModel2D<Color> grid, int gridPositionSize, int minWidth, int minHeight, String path, String name) throws Exception {
		int maxX = grid.getMaxX(), minX = grid.getMinX(), maxY = grid.getMaxY(), minY = grid.getMinY();
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];
		int topMargin = height - dataHeight;
		int rightMargin = width - dataWidth;
		int dataIndex = topMargin * width * 3;		
		for (int y = maxY; y >= minY; y--) {
			int localMinX = grid.getMinX(y);
			int localMaxX = grid.getMaxX(y);
			int localLeftMargin = (localMinX - minX)  * gridPositionSize * 3;
			int localRightMargin = (rightMargin + ((maxX - localMaxX) * gridPositionSize)) * 3;
			dataIndex += localLeftMargin;
			int firstDataIndexToCopyFrom = dataIndex;
			for (int x = localMinX; x <= localMaxX; x++) {
				java.awt.Color c = grid.getFromPosition(x,y);
				for (int j = 0; j < gridPositionSize; j++) {
					pixelData[dataIndex++] = (byte) c.getRed();
					pixelData[dataIndex++] = (byte) c.getGreen();
					pixelData[dataIndex++] = (byte) c.getBlue();
				}
			}
			dataIndex += localRightMargin;
			for (int i = 1; i < gridPositionSize; i++) {
				dataIndex += localLeftMargin;
				int dataIndexToCopyFrom = firstDataIndexToCopyFrom;
				for (int x = localMinX; x <= localMaxX; x++) {
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
					}
				}
				dataIndex += localRightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ObjectModel2D<Color> grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(grid, minWidth, minHeight);
		createImageFromEvenOrOddPositions(isEven, grid, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ObjectModel2D<Color> grid, int gridPositionSize, int minWidth, int minHeight, String path, String name) throws Exception {
		int maxX = grid.getMaxX(), minX = grid.getMinX(), maxY = grid.getMaxY(), minY = grid.getMinY();
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];
		int topMargin = height - dataHeight;
		int rightMargin = width - dataWidth;
		int dataIndex = topMargin * width * 3;
		int gridPositionSizeTimes3 = gridPositionSize * 3;
		for (int y = maxY; y >= minY; y--) {
			int localMinX = grid.getMinX(y);
			int localMaxX = grid.getMaxX(y);
			boolean isPositionEven = (localMinX+y)%2 == 0;
			if (isEven != isPositionEven) { 
				localMinX++;
			}
			int localLeftMargin = (localMinX - minX)  * gridPositionSizeTimes3;
			int localRightMargin = (rightMargin + ((maxX - localMaxX) * gridPositionSize)) * 3;
			dataIndex += localLeftMargin;
			int firstDataIndexToCopyFrom = dataIndex;
			int x = localMinX;
			for (; x < localMaxX; x+=2) {
				java.awt.Color c = grid.getFromPosition(x,y);
				for (int j = 0; j < gridPositionSize; j++) {
					pixelData[dataIndex++] = (byte) c.getRed();
					pixelData[dataIndex++] = (byte) c.getGreen();
					pixelData[dataIndex++] = (byte) c.getBlue();
				}
				dataIndex += gridPositionSizeTimes3;
			}
			if (x == localMaxX) {
				java.awt.Color c = grid.getFromPosition(x,y);
				for (int j = 0; j < gridPositionSize; j++) {
					pixelData[dataIndex++] = (byte) c.getRed();
					pixelData[dataIndex++] = (byte) c.getGreen();
					pixelData[dataIndex++] = (byte) c.getBlue();
				}
			}
			dataIndex += localRightMargin;
			for (int i = 1; i < gridPositionSize; i++) {
				dataIndex += localLeftMargin;
				int dataIndexToCopyFrom = firstDataIndexToCopyFrom;
				x = localMinX;
				for (; x < localMaxX; x+=2) {
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
					}
					dataIndex += gridPositionSizeTimes3;
					dataIndexToCopyFrom += gridPositionSizeTimes3;
				}
				if (x == localMaxX) {
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] =  pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] =  pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] =  pixelData[dataIndexToCopyFrom++];
					}
				}
				dataIndex += localRightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, String path, String name) 
			throws Exception {
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		createImageFromEvenOrOddPositions(grid, isEven, minX, maxX, minY, maxY, 1, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddPositions(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];
		int canvasTopMargin = height - dataHeight;
		int canvasRightMargin = width - dataWidth;
		int gridTopMargin = 0;
		int framedModelMinY, framedModelMaxY;
		int gridMaxY = grid.getMaxY(), gridMinY = grid.getMinY();
		if (maxY > gridMaxY) {
			gridTopMargin = (maxY - gridMaxY) * gridPositionSize;
			framedModelMaxY = gridMaxY;
		} else {
			framedModelMaxY = maxY;
		}
		if (minY < gridMinY) {
			framedModelMinY = gridMinY;
		} else {
			framedModelMinY = minY;
		}
		int dataIndex = (canvasTopMargin + gridTopMargin) * width * 3;
		int gridPositionSizeTimes3 = gridPositionSize * 3;
		for (int y = framedModelMaxY; y >= framedModelMinY; y--) {
			int gridMinXAtY = grid.getMinX(y);
			int gridMaxXAtY = grid.getMaxX(y);
			int gridLeftMargin = 0, gridRightMargin = 0;
			int framedModelMinXAtY, framedModelMaxXAtY;
			if (minX < gridMinXAtY) {
				gridLeftMargin = (gridMinXAtY - minX) * gridPositionSize;
				framedModelMinXAtY = gridMinXAtY;
			} else {
				framedModelMinXAtY = minX;
			}
			if (maxX > gridMaxXAtY) {
				gridRightMargin = (maxX - gridMaxXAtY) * gridPositionSize;
				framedModelMaxXAtY = gridMaxXAtY;
			} else {
				framedModelMaxXAtY = maxX;
			}
			int dataRightMargin = (canvasRightMargin + gridRightMargin) * 3;
			boolean isPositionEven = (framedModelMinXAtY+y)%2 == 0;
			if (isEven != isPositionEven) { 
				framedModelMinXAtY++;
				gridLeftMargin += gridPositionSize;
			}
			int dataLeftMargin = gridLeftMargin * 3;
			dataIndex += dataLeftMargin;
			int firstDataIndexToCopyFrom = dataIndex;
			int x = framedModelMinXAtY;
			for (; x < framedModelMaxXAtY; x+=2) {
				java.awt.Color c = grid.getFromPosition(x,y);
				for (int j = 0; j < gridPositionSize; j++) {
					pixelData[dataIndex++] = (byte) c.getRed();
					pixelData[dataIndex++] = (byte) c.getGreen();
					pixelData[dataIndex++] = (byte) c.getBlue();
				}
				dataIndex += gridPositionSizeTimes3;
			}
			if (x == framedModelMaxXAtY) {
				java.awt.Color c = grid.getFromPosition(x,y);
				for (int j = 0; j < gridPositionSize; j++) {
					pixelData[dataIndex++] = (byte) c.getRed();
					pixelData[dataIndex++] = (byte) c.getGreen();
					pixelData[dataIndex++] = (byte) c.getBlue();
				}
			}
			dataIndex += dataRightMargin;
			for (int i = 1; i < gridPositionSize; i++) {
				dataIndex += dataLeftMargin;
				int dataIndexToCopyFrom = firstDataIndexToCopyFrom;
				x = framedModelMinXAtY;
				for (; x < framedModelMaxXAtY; x+=2) {
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
					}
					dataIndex += gridPositionSizeTimes3;
					dataIndexToCopyFrom += gridPositionSizeTimes3;
				}
				if (x == framedModelMaxXAtY) {
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
					}
				}
				dataIndex += dataRightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddXPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddXPositions(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddXPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];
		int canvasTopMargin = height - dataHeight;
		int canvasRightMargin = width - dataWidth;
		int gridTopMargin = 0;
		int framedModelMinY, framedModelMaxY;
		int gridMaxY = grid.getMaxY(), gridMinY = grid.getMinY();
		if (maxY > gridMaxY) {
			gridTopMargin = (maxY - gridMaxY) * gridPositionSize;
			framedModelMaxY = gridMaxY;
		} else {
			framedModelMaxY = maxY;
		}
		if (minY < gridMinY) {
			framedModelMinY = gridMinY;
		} else {
			framedModelMinY = minY;
		}
		int dataIndex = (canvasTopMargin + gridTopMargin) * width * 3;
		int gridPositionSizeTimes3 = gridPositionSize * 3;
		for (int y = framedModelMaxY; y >= framedModelMinY; y--) {
			int gridMinXAtY = grid.getMinX(y);
			int gridMaxXAtY = grid.getMaxX(y);
			int gridLeftMargin = 0, gridRightMargin = 0;
			int framedModelMinXAtY, framedModelMaxXAtY;
			if (minX < gridMinXAtY) {
				gridLeftMargin = (gridMinXAtY - minX) * gridPositionSize;
				framedModelMinXAtY = gridMinXAtY;
			} else {
				framedModelMinXAtY = minX;
			}
			if (maxX > gridMaxXAtY) {
				gridRightMargin = (maxX - gridMaxXAtY) * gridPositionSize;
				framedModelMaxXAtY = gridMaxXAtY;
			} else {
				framedModelMaxXAtY = maxX;
			}
			int dataRightMargin = (canvasRightMargin + gridRightMargin) * 3;
			boolean isXEven = framedModelMinXAtY%2 == 0;
			if (isEven != isXEven) { 
				framedModelMinXAtY++;
				gridLeftMargin += gridPositionSize;
			}
			int dataLeftMargin = gridLeftMargin * 3;
			dataIndex += dataLeftMargin;
			int firstDataIndexToCopyFrom = dataIndex;
			int x = framedModelMinXAtY;
			for (; x < framedModelMaxXAtY; x+=2) {
				java.awt.Color c = grid.getFromPosition(x,y);
				for (int j = 0; j < gridPositionSize; j++) {
					pixelData[dataIndex++] = (byte) c.getRed();
					pixelData[dataIndex++] = (byte) c.getGreen();
					pixelData[dataIndex++] = (byte) c.getBlue();
				}
				dataIndex += gridPositionSizeTimes3;
			}
			if (x == framedModelMaxXAtY) {
				java.awt.Color c = grid.getFromPosition(x,y);
				for (int j = 0; j < gridPositionSize; j++) {
					pixelData[dataIndex++] = (byte) c.getRed();
					pixelData[dataIndex++] = (byte) c.getGreen();
					pixelData[dataIndex++] = (byte) c.getBlue();
				}
			}
			dataIndex += dataRightMargin;
			for (int i = 1; i < gridPositionSize; i++) {
				dataIndex += dataLeftMargin;
				int dataIndexToCopyFrom = firstDataIndexToCopyFrom;
				x = framedModelMinXAtY;
				for (; x < framedModelMaxXAtY; x+=2) {
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
					}
					dataIndex += gridPositionSizeTimes3;
					dataIndexToCopyFrom += gridPositionSizeTimes3;
				}
				if (x == framedModelMaxXAtY) {
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
					}
				}
				dataIndex += dataRightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddYPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddYPositions(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddYPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];
		int canvasTopMargin = height - dataHeight;
		int canvasRightMargin = width - dataWidth;
		int gridTopMargin = 0;
		int framedModelMinY, framedModelMaxY;
		int gridMaxY = grid.getMaxY(), gridMinY = grid.getMinY();
		if (maxY > gridMaxY) {
			gridTopMargin = (maxY - gridMaxY) * gridPositionSize;
			framedModelMaxY = gridMaxY;
		} else {
			framedModelMaxY = maxY;
		}
		if (minY < gridMinY) {
			framedModelMinY = gridMinY;
		} else {
			framedModelMinY = minY;
		}
		int dataIndex = (canvasTopMargin + gridTopMargin) * width * 3;
		boolean isYEven = framedModelMaxY%2 == 0;
		for (int y = framedModelMaxY; y >= framedModelMinY; y--, isYEven = !isYEven) {
			int gridMinXAtY = grid.getMinX(y);
			int gridMaxXAtY = grid.getMaxX(y);
			int dataLeftMargin = 0, gridRightMargin = 0;
			int framedModelMinXAtY, framedModelMaxXAtY;
			if (minX < gridMinXAtY) {
				dataLeftMargin = (gridMinXAtY - minX) * gridPositionSize * 3;
				framedModelMinXAtY = gridMinXAtY;
			} else {
				framedModelMinXAtY = minX;
			}
			if (maxX > gridMaxXAtY) {
				gridRightMargin = (maxX - gridMaxXAtY) * gridPositionSize;
				framedModelMaxXAtY = gridMaxXAtY;
			} else {
				framedModelMaxXAtY = maxX;
			}
			int dataRightMargin = (canvasRightMargin + gridRightMargin) * 3;
			if (isEven == isYEven) {
				dataIndex += dataLeftMargin;
				int firstDataIndexToCopyFrom = dataIndex;
				for (int x = framedModelMinXAtY; x <= framedModelMaxXAtY; x++) {
					java.awt.Color c = grid.getFromPosition(x, y);
					byte r = (byte) c.getRed(), g = (byte) c.getGreen(), b = (byte) c.getBlue();
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = r;
						pixelData[dataIndex++] = g;
						pixelData[dataIndex++] = b;
					}				
				}
				dataIndex += dataRightMargin;
				for (int i = 1; i < gridPositionSize; i++) {
					dataIndex += dataLeftMargin;
					int dataIndexToCopyFrom = firstDataIndexToCopyFrom;
					for (int x = framedModelMinXAtY; x <= framedModelMaxXAtY; x++) {
						for (int j = 0; j < gridPositionSize; j++) {
							pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
							pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
							pixelData[dataIndex++] = pixelData[dataIndexToCopyFrom++];
						}				
					}
					dataIndex += dataRightMargin;
				}
			} else {
				dataIndex += (dataLeftMargin + ((framedModelMaxXAtY - framedModelMinXAtY + 1) * gridPositionSize * 3) + dataRightMargin) * gridPositionSize;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}

	public static void saveAsPngImage(byte[] pixelData, int width, int height, String path, String name) throws IOException {
		DataBuffer buffer = new DataBufferByte(pixelData, pixelData.length);
		//3 bytes per pixel: red, green, blue
		WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[] {0, 1, 2}, (Point)null);
		ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE); 
		BufferedImage image = new BufferedImage(cm, raster, true, null);
		//BufferedImage image = new BufferedImage( 0, 0, BufferedImage.TYPE_BYTE_GRAY );//TODO grayscale png
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		String pathName = path + "/" + name;
		System.out.println("Creating image at: '" + pathName + "'");
		ImageIO.write(image, "png", new File(pathName));
	}
	
	private class StdInRunnable implements Runnable {
		
		private volatile boolean stop = false;
		
		@Override
		public void run() {
			InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
			try {
				while (!stop) {
					while (!br.ready() && !stop) {
			          Thread.sleep(200);
			        }
					if (stop) {
						br.close();
					} else {
						String line = br.readLine().trim().toLowerCase();
						if (line.equals("backup") || line.equals("save")) {
							backupRequested = true;
							System.out.println("Backup requested.");
						} else {
							System.out.println("Unknown command '" + line + "'. Use 'save' or 'backup' to request a backup.");
						}
					}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void stop() {
			stop = true;
		}
		
	}
	
}
