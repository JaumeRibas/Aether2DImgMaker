/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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

import caimgmaker.colormap.ActionableIntGrid2DColorMapperProcessor;
import caimgmaker.colormap.ActionableLongGrid2DColorMapperProcessor;
import caimgmaker.colormap.ColorMapper;
import cellularautomata.grid.IntGridMinAndMaxProcessor;
import cellularautomata.grid.LongGridEvenOddMinAndMaxProcessor;
import cellularautomata.grid.LongGridMinAndMaxProcessor;
import cellularautomata.grid.MinAndMax;
import cellularautomata.grid2d.NumberGrid2D;
import cellularautomata.grid2d.ObjectGrid2D;
import cellularautomata.grid2d.Grid2D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.SubareaGrid;
import cellularautomata.grid3d.ActionableGrid3DZCrossSectionProcessor;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid3d.IntGrid3DXCrossSectionCopierProcessor;
import cellularautomata.grid3d.IntGrid3DZCrossSectionCopierProcessor;
import cellularautomata.grid3d.LongGrid3D;
import cellularautomata.grid3d.LongGrid3DXCrossSectionCopierProcessor;
import cellularautomata.grid3d.LongGrid3DZCrossSectionCopierProcessor;
import cellularautomata.grid3d.NumberGrid3D;
import cellularautomata.grid3d.NumberGrid3DXCrossSectionCopierProcessor;
import cellularautomata.grid3d.NumberGrid3DZCrossSectionCopierProcessor;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model3d.ActionableModel3D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.NumericModel3D;
import cellularautomata.grid.GridProcessor;

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
	
	public void createImages(LongModel2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		String imgPath = path + "/" + colorMapper.getColormapName() + "/";
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			long[] minAndMaxValue = ca.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + step + ".png");
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
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createImages(IntModel2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		String imgPath = path + "/" + colorMapper.getColormapName() + "/";
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			int[] minAndMaxValue = ca.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + step + ".png");
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
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createImages(
			NumericModel2D<T> ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		String imgPath = path + "/" + colorMapper.getColormapName() + "/";
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			MinAndMax<T> minAndMaxValue = ca.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(ca, minAndMaxValue.getMin(), minAndMaxValue.getMax());
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + step + ".png");
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
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createEvenOddImages(IntModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String backupPath, boolean omitEven, boolean omitOdd) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getColormapName() + "/";
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			String evenFolder, oddFolder;
			if (isEvenStep) {
				evenFolder = "even";
				oddFolder = "odd";
			} else {
				evenFolder = "odd";
				oddFolder = "even";
			}
			if (!omitEven) {
				int[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(true);			
				if (evenMinAndMaxValue != null) {
					System.out.println("Even positions: min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + evenMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenColorGrid = colorMapper.getMappedGrid(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				int[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(false);
				if (oddMinAndMaxValue != null) {
					System.out.println("odd positions: min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + oddMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddColorGrid = colorMapper.getMappedGrid(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
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
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createEvenOddImages(LongModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String backupPath, boolean omitEven, boolean omitOdd) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getColormapName() + "/";
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			String evenFolder, oddFolder;
			if (isEvenStep) {
				evenFolder = "even";
				oddFolder = "odd";
			} else {
				evenFolder = "odd";
				oddFolder = "even";
			}
			if (!omitEven) {
				long[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(true);			
				if (evenMinAndMaxValue != null) {
					System.out.println("Even positions: min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + evenMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenColorGrid = colorMapper.getMappedGrid(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				long[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(false);
				if (oddMinAndMaxValue != null) {
					System.out.println("odd positions: min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + oddMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddColorGrid = colorMapper.getMappedGrid(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
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
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createEvenOddImages(NumericModel2D<T> ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String backupPath, boolean omitEven, boolean omitOdd) throws Exception {	
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getColormapName() + "/";
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			String evenFolder, oddFolder;
			if (isEvenStep) {
				evenFolder = "even";
				oddFolder = "odd";
			} else {
				evenFolder = "odd";
				oddFolder = "even";
			}
			if (!omitEven) {
				MinAndMax<T> evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(true);			
				if (evenMinAndMaxValue != null) {
					System.out.println("Even positions: min value: " + evenMinAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + evenMinAndMaxValue.getMax());
					ObjectGrid2D<Color> evenColorGrid = colorMapper.getMappedGrid(ca, evenMinAndMaxValue.getMin(), evenMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(evenColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				MinAndMax<T> oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(false);
				if (oddMinAndMaxValue != null) {
					System.out.println("odd positions: min value: " + oddMinAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + oddMinAndMaxValue.getMax());
					ObjectGrid2D<Color> oddColorGrid = colorMapper.getMappedGrid(ca, oddMinAndMaxValue.getMin(), oddMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(oddColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + step + ".png");
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
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createScanningAndZCrossSectionImages(IntModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int[] scanCoords = scanInitialCoords;
		String caName = ca.getName();
		path += "/" + colorMapper.getColormapName() + "/";
		String[] scanImgPaths = new String[] { 
				path + ca.getXLabel() + "_scan/",
				path + ca.getYLabel() + "_scan/",
				path + ca.getZLabel() + "_scan/" };
		String crossSectionImgPath = path + ca.getZLabel() + "=" + crossSectionZ + "/";
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			//x scan
			if (scanCoords[0] < ca.getMinX()) {
				scanCoords[0] = ca.getMaxX();
			}
			System.out.println("Scan " + ca.getXLabel() + ": " + scanCoords[0]);
			IntGrid2D crossSection = ca.crossSectionAtX(scanCoords[0]);
			int[] minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[0] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[0]--;
			//y scan
			if (scanCoords[1] < ca.getMinY()) {
				scanCoords[1] = ca.getMaxY();
			}
			System.out.println("Scan " + ca.getYLabel() + ": " + scanCoords[1]);
			crossSection = ca.crossSectionAtY(scanCoords[1]);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[1] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[1]--;
			//z scan
			if (scanCoords[2] < ca.getMinZ()) {
				scanCoords[2] = ca.getMaxZ();
			}
			System.out.println("Scan " + ca.getZLabel() + ": " + scanCoords[2]);
			crossSection = ca.crossSectionAtZ(scanCoords[2]);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[2] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[2]--;
			//cross section
			crossSection = ca.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
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
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndZCrossSectionImages(LongModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int[] scanCoords = scanInitialCoords;
		String caName = ca.getName();
		path += "/" + colorMapper.getColormapName() + "/";
		String[] scanImgPaths = new String[] { 
				path + ca.getXLabel() + "_scan/",
				path + ca.getYLabel() + "_scan/",
				path + ca.getZLabel() + "_scan/" };
		String crossSectionImgPath = path + ca.getZLabel() + "=" + crossSectionZ + "/";
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			//x scan
			if (scanCoords[0] < ca.getMinX()) {
				scanCoords[0] = ca.getMaxX();
			}
			System.out.println("Scan " + ca.getXLabel() + ": " + scanCoords[0]);
			LongGrid2D crossSection = ca.crossSectionAtX(scanCoords[0]);
			long[] minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[0] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[0]--;
			//y scan
			if (scanCoords[1] < ca.getMinY()) {
				scanCoords[1] = ca.getMaxY();
			}
			System.out.println("Scan " + ca.getYLabel() + ": " + scanCoords[1]);
			crossSection = ca.crossSectionAtY(scanCoords[1]);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[1] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[1]--;
			//z scan
			if (scanCoords[2] < ca.getMinZ()) {
				scanCoords[2] = ca.getMaxZ();
			}
			System.out.println("Scan " + ca.getZLabel() + ": " + scanCoords[2]);
			crossSection = ca.crossSectionAtZ(scanCoords[2]);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[2] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[2]--;
			//cross section
			crossSection = ca.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
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
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createScanningAndZCrossSectionImages(NumericModel3D<T> ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int[] scanCoords = scanInitialCoords;
		String caName = ca.getName();
		path += "/" + colorMapper.getColormapName() + "/";
		String[] scanImgPaths = new String[] { 
				path + ca.getXLabel() + "_scan/",
				path + ca.getYLabel() + "_scan/",
				path + ca.getZLabel() + "_scan/" };
		String crossSectionImgPath = path + ca.getZLabel() + "=" + crossSectionZ + "/";
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			//x scan
			if (scanCoords[0] < ca.getMinX()) {
				scanCoords[0] = ca.getMaxX();
			}
			System.out.println("Scan " + ca.getXLabel() + ": " + scanCoords[0]);
			NumberGrid2D<T> crossSection = ca.crossSectionAtX(scanCoords[0]);
			MinAndMax<T> minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[0] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[0]--;
			//y scan
			if (scanCoords[1] < ca.getMinY()) {
				scanCoords[1] = ca.getMaxY();
			}
			System.out.println("Scan " + ca.getYLabel() + ": " + scanCoords[1]);
			crossSection = ca.crossSectionAtY(scanCoords[1]);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[1] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[1]--;
			//z scan
			if (scanCoords[2] < ca.getMinZ()) {
				scanCoords[2] = ca.getMaxZ();
			}
			System.out.println("Scan " + ca.getZLabel() + ": " + scanCoords[2]);
			crossSection = ca.crossSectionAtZ(scanCoords[2]);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPaths[2] + numberedFolder, caName + "_" + step + ".png");
			scanCoords[2]--;
			//cross section
			crossSection = ca.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Cross section: min value: " + minAndMaxValue.getMin() + System.lineSeparator() + "Max value: " + minAndMaxValue.getMax());
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
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
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndZCrossSectionEvenOddImages(IntModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, boolean omitEven, boolean omitOdd) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int[] scanCoords = scanInitialCoords;
		
		String caName = ca.getName();
		path += "/" + colorMapper.getColormapName() + "/";
		String[] scanImgPaths = new String[] { 
				path + ca.getXLabel() + "_scan/",
				path + ca.getYLabel() + "_scan/",
				path + ca.getZLabel() + "_scan/" };
		String crossSectionImgPath = path + ca.getZLabel() + "=" + crossSectionZ + "/";		
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			String[] evenScanFolders = new String[3];
			String[] oddScanFolders = new String[3];
			String evenCrossSectionFolder, oddCrossSectionFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				for (int i = 0; i < 3; i++) {
					if (scanCoords[i]%2 == 0) {
						evenScanFolders[i] = "even";
						oddScanFolders[i] = "odd";
					} else {
						evenScanFolders[i] = "odd";
						oddScanFolders[i] = "even";
					}					
				}
			} else {
				if (crossSectionZ%2 != 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				for (int i = 0; i < 3; i++) {
					if (scanCoords[i]%2 != 0) {
						evenScanFolders[i] = "even";
						oddScanFolders[i] = "odd";
					} else {
						evenScanFolders[i] = "odd";
						oddScanFolders[i] = "even";
					}					
				}
			}
			//x scan
			if (scanCoords[0] < ca.getMinY())
				scanCoords[0] = ca.getMaxY();	
			IntGrid2D crossSection = ca.crossSectionAtX(scanCoords[0]);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanCoords[0]);
			if (!omitEven) {
				int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + evenScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + evenScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + oddScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + oddScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[0]--;
			//y scan
			if (scanCoords[1] < ca.getMinY())
				scanCoords[1] = ca.getMaxY();	
			crossSection = ca.crossSectionAtY(scanCoords[1]);
			System.out.println("Scan " + ca.getYLabel() + ": " + scanCoords[1]);
			if (!omitEven) {
				int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + evenScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + evenScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + oddScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + oddScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[1]--;
			//z scan
			if (scanCoords[2] < ca.getMinZ())
				scanCoords[2] = ca.getMaxZ();	
			crossSection = ca.crossSectionAtZ(scanCoords[2]);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanCoords[2]);
			if (!omitEven) {
				int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + evenScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + evenScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + oddScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + oddScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[2]--;
			//cross section
			crossSection = ca.crossSectionAtZ(crossSectionZ);
			if (!omitEven) {
				int[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenCrossSectionMinAndMaxValue != null) {
					System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				int[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddCrossSectionMinAndMaxValue != null) {
					System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
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
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndZCrossSectionEvenOddImages(LongModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, boolean omitEven, boolean omitOdd) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int[] scanCoords = scanInitialCoords;
		
		String caName = ca.getName();
		path += "/" + colorMapper.getColormapName() + "/";
		String[] scanImgPaths = new String[] { 
				path + ca.getXLabel() + "_scan/",
				path + ca.getYLabel() + "_scan/",
				path + ca.getZLabel() + "_scan/" };
		String crossSectionImgPath = path + ca.getZLabel() + "=" + crossSectionZ + "/";		
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			String[] evenScanFolders = new String[3];
			String[] oddScanFolders = new String[3];
			String evenCrossSectionFolder, oddCrossSectionFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				for (int i = 0; i < 3; i++) {
					if (scanCoords[i]%2 == 0) {
						evenScanFolders[i] = "even";
						oddScanFolders[i] = "odd";
					} else {
						evenScanFolders[i] = "odd";
						oddScanFolders[i] = "even";
					}					
				}
			} else {
				if (crossSectionZ%2 != 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				for (int i = 0; i < 3; i++) {
					if (scanCoords[i]%2 != 0) {
						evenScanFolders[i] = "even";
						oddScanFolders[i] = "odd";
					} else {
						evenScanFolders[i] = "odd";
						oddScanFolders[i] = "even";
					}					
				}
			}
			//x scan
			if (scanCoords[0] < ca.getMinY())
				scanCoords[0] = ca.getMaxY();	
			LongGrid2D crossSection = ca.crossSectionAtX(scanCoords[0]);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanCoords[0]);
			if (!omitEven) {
				long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + evenScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + evenScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + oddScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + oddScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[0]--;
			//y scan
			if (scanCoords[1] < ca.getMinY())
				scanCoords[1] = ca.getMaxY();	
			crossSection = ca.crossSectionAtY(scanCoords[1]);
			System.out.println("Scan " + ca.getYLabel() + ": " + scanCoords[1]);
			if (!omitEven) {
				long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + evenScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + evenScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + oddScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + oddScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[1]--;
			//z scan
			if (scanCoords[2] < ca.getMinZ())
				scanCoords[2] = ca.getMaxZ();	
			crossSection = ca.crossSectionAtZ(scanCoords[2]);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanCoords[2]);
			if (!omitEven) {
				long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + evenScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + evenScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + oddScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + oddScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[2]--;
			//cross section
			crossSection = ca.crossSectionAtZ(crossSectionZ);
			if (!omitEven) {
				long[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenCrossSectionMinAndMaxValue != null) {
					System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
					ObjectGrid2D<Color> evenCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				long[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddCrossSectionMinAndMaxValue != null) {
					System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
					ObjectGrid2D<Color> oddCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
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
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createScanningAndZCrossSectionEvenOddImages(NumericModel3D<T> ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String backupPath, boolean omitEven, boolean omitOdd) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int[] scanCoords = scanInitialCoords;
		
		String caName = ca.getName();
		path += "/" + colorMapper.getColormapName() + "/";
		String[] scanImgPaths = new String[] { 
				path + ca.getXLabel() + "_scan/",
				path + ca.getYLabel() + "_scan/",
				path + ca.getZLabel() + "_scan/" };
		String crossSectionImgPath = path + ca.getZLabel() + "=" + crossSectionZ + "/";		
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			String[] evenScanFolders = new String[3];
			String[] oddScanFolders = new String[3];
			String evenCrossSectionFolder, oddCrossSectionFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				for (int i = 0; i < 3; i++) {
					if (scanCoords[i]%2 == 0) {
						evenScanFolders[i] = "even";
						oddScanFolders[i] = "odd";
					} else {
						evenScanFolders[i] = "odd";
						oddScanFolders[i] = "even";
					}					
				}
			} else {
				if (crossSectionZ%2 != 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				for (int i = 0; i < 3; i++) {
					if (scanCoords[i]%2 != 0) {
						evenScanFolders[i] = "even";
						oddScanFolders[i] = "odd";
					} else {
						evenScanFolders[i] = "odd";
						oddScanFolders[i] = "even";
					}					
				}
			}
			//x scan
			if (scanCoords[0] < ca.getMinY())
				scanCoords[0] = ca.getMaxY();	
			NumericModel2D<T> crossSection = ca.crossSectionAtX(scanCoords[0]);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanCoords[0]);
			if (!omitEven) {
				MinAndMax<T> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue.getMin() + ", max value: " + evenScanMinAndMaxValue.getMax());
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + evenScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + evenScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				MinAndMax<T> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + oddScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[0] + oddScanFolders[0] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[0]--;
			//y scan
			if (scanCoords[1] < ca.getMinY())
				scanCoords[1] = ca.getMaxY();	
			crossSection = ca.crossSectionAtY(scanCoords[1]);
			System.out.println("Scan " + ca.getYLabel() + ": " + scanCoords[1]);
			if (!omitEven) {
				MinAndMax<T> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue.getMin() + ", max value: " + evenScanMinAndMaxValue.getMax());
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + evenScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + evenScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				MinAndMax<T> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + oddScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[1] + oddScanFolders[1] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[1]--;
			//z scan
			if (scanCoords[2] < ca.getMinZ())
				scanCoords[2] = ca.getMaxZ();	
			crossSection = ca.crossSectionAtZ(scanCoords[2]);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanCoords[2]);
			if (!omitEven) {
				MinAndMax<T> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenScanMinAndMaxValue != null) {
					System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue.getMin() + ", max value: " + evenScanMinAndMaxValue.getMax());
					ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + evenScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + evenScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				MinAndMax<T> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddScanMinAndMaxValue != null) {
					System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
					ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + oddScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPaths[2] + oddScanFolders[2] + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			scanCoords[2]--;
			//cross section
			crossSection = ca.crossSectionAtZ(crossSectionZ);
			if (!omitEven) {
				MinAndMax<T> evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
				if (evenCrossSectionMinAndMaxValue != null) {
					System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue.getMin() + ", max value: " + evenCrossSectionMinAndMaxValue.getMax());
					ObjectGrid2D<Color> evenCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, evenCrossSectionMinAndMaxValue.getMin(), evenCrossSectionMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				}
			}
			if (!omitOdd) {
				MinAndMax<T> oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
				if (oddCrossSectionMinAndMaxValue != null) {
					System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue.getMin() + ", max value: " + oddCrossSectionMinAndMaxValue.getMax());
					ObjectGrid2D<Color> oddCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue.getMin(), oddCrossSectionMinAndMaxValue.getMax());
					createImageFromEvenOrOddPositions(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
				} else {
					createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
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
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXZScanningAndZCrossSectionOddImagesFromLongGrid3D(ActionableModel3D<LongGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		int zScanInitialIndex = ca.getMaxZ();
		createXZScanningAndZCrossSectionOddImagesFromLongGrid3D(ca, xScanInitialIndex, zScanInitialIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXZScanningAndZCrossSectionOddImagesFromLongGrid3D(ActionableModel3D<LongGrid3D> ca, int xScanInitialIndex, int zScanInitialIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenCrossSectionZ = (step + crossSectionZ)%2 != 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		int scanZ = zScanInitialIndex;
		
		String caName = ca.getName();
		String xScanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getXLabel() + "_scan/";
		String zScanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getZLabel() + "_scan/";
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		
		LongGrid3DZCrossSectionCopierProcessor zCopier = new LongGrid3DZCrossSectionCopierProcessor();
		LongGrid3DXCrossSectionCopierProcessor xCopier = new LongGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		zCopier.requestCopy(scanZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY(),
					minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanX);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanZ);
			boolean isEvenScanX = (step + scanX)%2 != 0;
			boolean isEvenScanZ = (step + scanZ)%2 != 0;
			LongGrid2D xScan = xCopier.getCopy(scanX);
			LongGrid2D zScan = zCopier.getCopy(scanZ);
			LongGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			long[] oddXScanMinAndMaxValue = xScan.getEvenOddPositionsMinAndMax(isEvenScanX);
			long[] oddZScanMinAndMaxValue = zScan.getEvenOddPositionsMinAndMax(isEvenScanZ);
			long[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenCrossSectionZ);
			
			if (oddXScanMinAndMaxValue != null) {
				System.out.println("X scan odd positions: min value: " + oddXScanMinAndMaxValue[0] + ", max value: " + oddXScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(xScan, oddXScanMinAndMaxValue[0], oddXScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddScanColorGrid, isEvenScanX, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddZScanMinAndMaxValue != null) {
				System.out.println("Z scan odd positions: min value: " + oddZScanMinAndMaxValue[0] + ", max value: " + oddZScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(zScan, oddZScanMinAndMaxValue[0], oddZScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddScanColorGrid, isEvenScanZ, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddCrossSectionColorGrid, isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
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
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();
			scanZ--;
			if (scanZ < ca.getMinZ())
				scanZ = ca.getMaxZ();	
			step++;
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(scanZ);
			zCopier.requestCopy(crossSectionZ);
			isEvenCrossSectionZ = !isEvenCrossSectionZ;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXZScanningAndZCrossSectionOddImagesFromIntGrid3D(ActionableModel3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		int zScanInitialIndex = ca.getMaxZ();
		createXZScanningAndZCrossSectionOddImagesFromIntGrid3D(ca, xScanInitialIndex, zScanInitialIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXZScanningAndZCrossSectionOddImagesFromIntGrid3D(ActionableModel3D<IntGrid3D> ca, int xScanInitialIndex, int zScanInitialIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenCrossSectionZ = (step + crossSectionZ)%2 != 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		int scanZ = zScanInitialIndex;
		
		String caName = ca.getName();
		String xScanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getXLabel() + "_scan/" ;
		String zScanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getZLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		
		IntGrid3DZCrossSectionCopierProcessor zCopier = new IntGrid3DZCrossSectionCopierProcessor();
		IntGrid3DXCrossSectionCopierProcessor xCopier = new IntGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		zCopier.requestCopy(scanZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY(),
					minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanX);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanZ);
			boolean isEvenScanX = (step + scanX)%2 != 0;
			boolean isEvenScanZ = (step + scanZ)%2 != 0;
			IntGrid2D xScan = xCopier.getCopy(scanX);
			IntGrid2D zScan = zCopier.getCopy(scanZ);
			IntGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			int[] oddXScanMinAndMaxValue = xScan.getEvenOddPositionsMinAndMax(isEvenScanX);
			int[] oddZScanMinAndMaxValue = zScan.getEvenOddPositionsMinAndMax(isEvenScanZ);
			int[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenCrossSectionZ);
			
			if (oddXScanMinAndMaxValue != null) {
				System.out.println("X scan odd positions: min value: " + oddXScanMinAndMaxValue[0] + ", max value: " + oddXScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(xScan, oddXScanMinAndMaxValue[0], oddXScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddScanColorGrid, isEvenScanX, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddZScanMinAndMaxValue != null) {
				System.out.println("Z scan odd positions: min value: " + oddZScanMinAndMaxValue[0] + ", max value: " + oddZScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(zScan, oddZScanMinAndMaxValue[0], oddZScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddScanColorGrid, isEvenScanZ, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddCrossSectionColorGrid, isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
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
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();
			scanZ--;
			if (scanZ < ca.getMinZ())
				scanZ = ca.getMaxZ();	
			step++;
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(scanZ);
			zCopier.requestCopy(crossSectionZ);
			isEvenCrossSectionZ = !isEvenCrossSectionZ;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXZScanningAndZCrossSectionEvenImagesFromIntGrid3D(ActionableModel3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		int zScanInitialIndex = ca.getMaxZ();
		createXZScanningAndZCrossSectionEvenImagesFromIntGrid3D(ca, xScanInitialIndex, zScanInitialIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXZScanningAndZCrossSectionEvenImagesFromIntGrid3D(ActionableModel3D<IntGrid3D> ca, int xScanInitialIndex, int zScanInitialIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenCrossSectionZ = (step + crossSectionZ)%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		int scanZ = zScanInitialIndex;
		
		String caName = ca.getName();
		String xScanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getXLabel() + "_scan/" ;
		String zScanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getZLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		
		IntGrid3DZCrossSectionCopierProcessor zCopier = new IntGrid3DZCrossSectionCopierProcessor();
		IntGrid3DXCrossSectionCopierProcessor xCopier = new IntGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		zCopier.requestCopy(scanZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY(),
					minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanX);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanZ);
			boolean isEvenScanX = (step + scanX)%2 == 0;
			boolean isEvenScanZ = (step + scanZ)%2 == 0;
			IntGrid2D xScan = xCopier.getCopy(scanX);
			IntGrid2D zScan = zCopier.getCopy(scanZ);
			IntGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			int[] evenXScanMinAndMaxValue = xScan.getEvenOddPositionsMinAndMax(isEvenScanX);
			int[] evenZScanMinAndMaxValue = zScan.getEvenOddPositionsMinAndMax(isEvenScanZ);
			int[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenCrossSectionZ);
			
			if (evenXScanMinAndMaxValue != null) {
				System.out.println("X scan even positions: min value: " + evenXScanMinAndMaxValue[0] + ", max value: " + evenXScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(xScan, evenXScanMinAndMaxValue[0], evenXScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(evenScanColorGrid, isEvenScanX, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (evenZScanMinAndMaxValue != null) {
				System.out.println("Z scan even positions: min value: " + evenZScanMinAndMaxValue[0] + ", max value: " + evenZScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(zScan, evenZScanMinAndMaxValue[0], evenZScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(evenScanColorGrid, isEvenScanZ, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (evenCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(evenCrossSectionColorGrid, isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + "even/" + numberedFolder, caName + "_" + step + ".png");
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
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();
			scanZ--;
			if (scanZ < ca.getMinZ())
				scanZ = ca.getMaxZ();	
			step++;
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(scanZ);
			zCopier.requestCopy(crossSectionZ);
			isEvenCrossSectionZ = !isEvenCrossSectionZ;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}	
	
	public void createScanningAndCrossSectionImagesFromIntGrid3D(ActionableModel3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionImagesFromIntGrid3D(ca, scanInitialZIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionImagesFromIntGrid3D(ActionableModel3D<IntGrid3D> ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getZLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		
		ActionableGrid3DZCrossSectionProcessor<IntGrid3D, IntGrid2D> scan = 
				new ActionableGrid3DZCrossSectionProcessor<IntGrid3D, IntGrid2D>(scanZ);
		ActionableGrid3DZCrossSectionProcessor<IntGrid3D, IntGrid2D> crossSection = 
				new ActionableGrid3DZCrossSectionProcessor<IntGrid3D, IntGrid2D>(crossSectionZ);
		
		IntGridMinAndMaxProcessor<IntGrid2D> scanMinAndMaxProcessor = new IntGridMinAndMaxProcessor<IntGrid2D>();
		IntGridMinAndMaxProcessor<IntGrid2D> crossSectionMinAndMaxProcessor = new IntGridMinAndMaxProcessor<IntGrid2D>();
		
		scan.addProcessor(scanMinAndMaxProcessor);
		crossSection.addProcessor(crossSectionMinAndMaxProcessor);
		
		ca.addProcessor(scan);
		ca.addProcessor(crossSection);
		//get min and max for current step
		ca.processGrid();
		
		do {
			scan.removeProcessor(scanMinAndMaxProcessor);
			crossSection.removeProcessor(crossSectionMinAndMaxProcessor);
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanZ);
			int[] scanMinAndMaxValue = scanMinAndMaxProcessor.getMinAndMaxValue();
			int[] crossSectionMinAndMaxValue = crossSectionMinAndMaxProcessor.getMinAndMaxValue();
			System.out.println("Scan: min value: " + scanMinAndMaxValue[0] + ", max value: " + scanMinAndMaxValue[1]);
			System.out.println("Cross section: min value: " + crossSectionMinAndMaxValue[0] + ", max value: " + crossSectionMinAndMaxValue[1]);
			
			ActionableIntGrid2DColorMapperProcessor scanColorMapperProcessor = 
					new ActionableIntGrid2DColorMapperProcessor(colorMapper, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
			ActionableIntGrid2DColorMapperProcessor colorMapperProcessor = 
					new ActionableIntGrid2DColorMapperProcessor(colorMapper, crossSectionMinAndMaxValue[0], crossSectionMinAndMaxValue[1]);
			
			ImageRenderingProcessor scanImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + numberedFolder, caName + "_" + step + ".png");
			ImageRenderingProcessor crossSectionImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
							caName + "_" + step + ".png");
			
			scanColorMapperProcessor.addProcessor(scanImageRenderer);
			colorMapperProcessor.addProcessor(crossSectionImageRenderer);
			
			scan.addProcessor(scanColorMapperProcessor);
			crossSection.addProcessor(colorMapperProcessor);
			
			//generate images
			ca.processGrid();
			
			scan.removeProcessor(scanColorMapperProcessor);
			crossSection.removeProcessor(colorMapperProcessor);
			
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
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			crossSection.addProcessor(crossSectionMinAndMaxProcessor);
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImagesFromLongGrid3D(ActionableModel3D<LongGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionImagesFromLongGrid3D(ca, scanInitialZIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionImagesFromLongGrid3D(ActionableModel3D<LongGrid3D> ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getZLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		
		ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D> scan = 
				new ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D>(scanZ);
		ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D> crossSection = 
				new ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D>(crossSectionZ);
		
		LongGridMinAndMaxProcessor<LongGrid2D> scanMinAndMaxProcessor = new LongGridMinAndMaxProcessor<LongGrid2D>();
		LongGridMinAndMaxProcessor<LongGrid2D> crossSectionMinAndMaxProcessor = new LongGridMinAndMaxProcessor<LongGrid2D>();
		
		scan.addProcessor(scanMinAndMaxProcessor);
		crossSection.addProcessor(crossSectionMinAndMaxProcessor);
		
		ca.addProcessor(scan);
		ca.addProcessor(crossSection);
		//get min and max for current step
		ca.processGrid();
		
		do {
			scan.removeProcessor(scanMinAndMaxProcessor);
			crossSection.removeProcessor(crossSectionMinAndMaxProcessor);
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanZ);
			long[] scanMinAndMaxValue = scanMinAndMaxProcessor.getMinAndMaxValue();
			long[] crossSectionMinAndMaxValue = crossSectionMinAndMaxProcessor.getMinAndMaxValue();
			System.out.println("Scan: min value: " + scanMinAndMaxValue[0] + ", max value: " + scanMinAndMaxValue[1]);
			System.out.println("Cross section: min value: " + crossSectionMinAndMaxValue[0] + ", max value: " + crossSectionMinAndMaxValue[1]);
			
			ActionableLongGrid2DColorMapperProcessor scanColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(colorMapper, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
			ActionableLongGrid2DColorMapperProcessor colorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(colorMapper, crossSectionMinAndMaxValue[0], crossSectionMinAndMaxValue[1]);
			
			ImageRenderingProcessor scanImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + numberedFolder, caName + "_" + step + ".png");
			ImageRenderingProcessor crossSectionImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
							caName + "_" + step + ".png");
			
			scanColorMapperProcessor.addProcessor(scanImageRenderer);
			colorMapperProcessor.addProcessor(crossSectionImageRenderer);
			
			scan.addProcessor(scanColorMapperProcessor);
			crossSection.addProcessor(colorMapperProcessor);
			
			//generate images
			ca.processGrid();
			
			scan.removeProcessor(scanColorMapperProcessor);
			crossSection.removeProcessor(colorMapperProcessor);
			
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
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			crossSection.addProcessor(crossSectionMinAndMaxProcessor);
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXScanningAndZCrossSectionImagesFromEvenOddYZ(ActionableModel3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionImagesFromEvenOddYZ(ca, xScanInitialIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXScanningAndZCrossSectionImagesFromEvenOddYZ(ActionableModel3D<IntGrid3D> ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getXLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		String yLabel = ca.getYLabel();
		IntGrid3DZCrossSectionCopierProcessor zCopier = new IntGrid3DZCrossSectionCopierProcessor();
		IntGrid3DXCrossSectionCopierProcessor xCopier = new IntGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY(),
					minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
			System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanX);
			IntGrid2D scan = xCopier.getCopy(scanX);
			IntGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			String evenYCrossSectionFolder, oddYCrossSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenYCrossSectionFolder = "even_" + yLabel;
					oddYCrossSectionFolder = "odd_" + yLabel;
				} else {
					evenYCrossSectionFolder = "odd_" + yLabel;
					oddYCrossSectionFolder = "even_" + yLabel;
				}
				evenScanFolder = "even";
				oddScanFolder = "odd";
			} else {
				if (crossSectionZ%2 != 0) {
					evenYCrossSectionFolder = "even_" + yLabel;
					oddYCrossSectionFolder = "odd_" + yLabel;
				} else {
					evenYCrossSectionFolder = "odd_" + yLabel;
					oddYCrossSectionFolder = "even_" + yLabel;
				}
				evenScanFolder = "odd";
				oddScanFolder = "even";
			}
			
			int[] evenScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(true);
			int[] evenYCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(true);
			int[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(false);
			int[] oddYCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(false);
			
			if (evenScanMinAndMaxValue != null) {
				System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(evenScanColorGrid, true, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddScanColorGrid, false, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (evenYCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section even " + yLabel + " positions: min value: " + evenYCrossSectionMinAndMaxValue[0] + ", max value: " + evenYCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenYCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, evenYCrossSectionMinAndMaxValue[0], evenYCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddYPositions(evenYCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenYCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenYCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddYCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd " + yLabel + " positions: min value: " + oddYCrossSectionMinAndMaxValue[0] + ", max value: " + oddYCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddYCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddYCrossSectionMinAndMaxValue[0], oddYCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddYPositions(oddYCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + oddYCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + oddYCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
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
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(crossSectionZ);
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXScanningAndZCrossSectionImagesFromEvenOddXY(ActionableModel3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionImagesFromEvenOddXY(ca, xScanInitialIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXScanningAndZCrossSectionImagesFromEvenOddXY(ActionableModel3D<IntGrid3D> ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getXLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		String yLabel = ca.getYLabel();
		IntGrid3DZCrossSectionCopierProcessor zCopier = new IntGrid3DZCrossSectionCopierProcessor();
		IntGrid3DXCrossSectionCopierProcessor xCopier = new IntGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY(),
					minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
			System.out.println("Max " + yLabel + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanX);
			IntGrid2D scan = xCopier.getCopy(scanX);
			IntGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			String evenCrossSectionFolder, oddCrossSectionFolder, evenYScanFolder, oddYScanFolder;
			if (isEvenStep) {
				evenCrossSectionFolder = "even";
				oddCrossSectionFolder = "odd";
				if (scanX%2 == 0) {
					evenYScanFolder = "even_" + yLabel;
					oddYScanFolder = "odd_" + yLabel;
				} else {
					evenYScanFolder = "odd_" + yLabel;
					oddYScanFolder = "even_" + yLabel;
				}
			} else {
				evenCrossSectionFolder = "odd";
				oddCrossSectionFolder = "even";
				if (scanX%2 != 0) {
					evenYScanFolder = "even_" + yLabel;
					oddYScanFolder = "odd_" + yLabel;
				} else {
					evenYScanFolder = "odd_" + yLabel;
					oddYScanFolder = "even_" + yLabel;
				}
			}
			
			int[] evenYScanMinAndMaxValue = scan.getMinAndMaxAtEvenOddY(true);
			int[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
			int[] oddYScanMinAndMaxValue = scan.getMinAndMaxAtEvenOddY(false);
			int[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
			
			if (evenYScanMinAndMaxValue != null) {
				System.out.println("Scan even " + yLabel + " positions: min value: " + evenYScanMinAndMaxValue[0] + ", max value: " + evenYScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenYScanColorGrid = colorMapper.getMappedGrid(scan, evenYScanMinAndMaxValue[0], evenYScanMinAndMaxValue[1]);
				createImageFromEvenOrOddYPositions(evenYScanColorGrid, true, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenYScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenYScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddYScanMinAndMaxValue != null) {
				System.out.println("Scan odd " + yLabel + " positions: min value: " + oddYScanMinAndMaxValue[0] + ", max value: " + oddYScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddYScanColorGrid = colorMapper.getMappedGrid(scan, oddYScanMinAndMaxValue[0], oddYScanMinAndMaxValue[1]);
				createImageFromEvenOrOddYPositions(oddYScanColorGrid, false, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddYScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddYScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (evenCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
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
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(crossSectionZ);
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXScanningAndZCrossSectionEvenOddImages(ActionableModel3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionEvenOddImages(ca, xScanInitialIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXScanningAndZCrossSectionEvenOddImages(ActionableModel3D<IntGrid3D> ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getXLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		IntGrid3DZCrossSectionCopierProcessor zCopier = new IntGrid3DZCrossSectionCopierProcessor();
		IntGrid3DXCrossSectionCopierProcessor xCopier = new IntGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY(),
					minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanX);
			IntGrid2D scan = xCopier.getCopy(scanX);
			IntGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			String evenCrossSectionFolder, oddCrossSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				if (scanX%2 == 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			} else {
				if (crossSectionZ%2 != 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				if (scanX%2 != 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			}
			
			int[] evenScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(true);
			int[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
			int[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(false);
			int[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
			
			if (evenScanMinAndMaxValue != null) {
				System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = colorMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(evenScanColorGrid, true, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddScanColorGrid, false, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (evenCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
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
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(crossSectionZ);
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createXScanningAndZCrossSectionOddImages(ActionableModel3D<NumberGrid3D<T>> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionOddImages(ca, xScanInitialIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createXScanningAndZCrossSectionOddImages(ActionableModel3D<NumberGrid3D<T>> ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenCrossSectionZ = (step + crossSectionZ)%2 != 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getXLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		NumberGrid3DZCrossSectionCopierProcessor<T> zCopier = new NumberGrid3DZCrossSectionCopierProcessor<T>();
		NumberGrid3DXCrossSectionCopierProcessor<T> xCopier = new NumberGrid3DXCrossSectionCopierProcessor<T>();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY(),
					minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getXLabel() + ": " + scanX);
			boolean isEvenScanX = (step + scanX)%2 != 0;
			NumberGrid2D<T> scan = xCopier.getCopy(scanX);
			NumberGrid2D<T> crossSection = zCopier.getCopy(crossSectionZ);
			
			MinAndMax<T> oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(isEvenScanX);
			MinAndMax<T> oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenCrossSectionZ);
			
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
				ObjectGrid2D<Color> oddScanColorGrid = colorMapper.getMappedGrid(scan, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
				createImageFromEvenOrOddPositions(oddScanColorGrid, isEvenScanX, minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minZ, maxZ, minY, maxY, minWidth, minHeight, 
						scanImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			}
			if (oddCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue.getMin() + ", max value: " + oddCrossSectionMinAndMaxValue.getMax());
				ObjectGrid2D<Color> oddCrossSectionColorGrid = colorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue.getMin(), oddCrossSectionMinAndMaxValue.getMax());
				createImageFromEvenOrOddPositions(oddCrossSectionColorGrid, isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + step + ".png");
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
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			step++;
			isEvenCrossSectionZ = !isEvenCrossSectionZ;
			System.out.println();
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(crossSectionZ);
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createZScanningAndCrossSectionEvenOddImages(ActionableModel3D<LongGrid3D> ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createZScanningAndCrossSectionEvenOddImages(ca, scanInitialZIndex, crossSectionZ, 
			colorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createZScanningAndCrossSectionEvenOddImages(ActionableModel3D<LongGrid3D> ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + colorMapper.getColormapName() + "/" + ca.getZLabel() + "_scan/" ;
		String crossSectionImgPath = imagesPath + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		
		ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D> scan = 
				new ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D>(scanZ);
		ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D> crossSection = 
				new ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D>(crossSectionZ);
		
		LongGridEvenOddMinAndMaxProcessor<LongGrid2D> scanMinAndMaxProcessor = new LongGridEvenOddMinAndMaxProcessor<LongGrid2D>();
		LongGridEvenOddMinAndMaxProcessor<LongGrid2D> crossSectionMinAndMaxProcessor = new LongGridEvenOddMinAndMaxProcessor<LongGrid2D>();
		
		scan.addProcessor(scanMinAndMaxProcessor);
		crossSection.addProcessor(crossSectionMinAndMaxProcessor);
		
		ca.addProcessor(scan);
		ca.addProcessor(crossSection);
		//get min and max for current step
		ca.processGrid();
		
		do {
			scan.removeProcessor(scanMinAndMaxProcessor);
			crossSection.removeProcessor(crossSectionMinAndMaxProcessor);
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			System.out.println("Scan " + ca.getZLabel() + ": " + scanZ);
			long[] evenScanMinAndMaxValue = scanMinAndMaxProcessor.getEvenMinAndMaxValue();
			long[] evenCrossSectionMinAndMaxValue = crossSectionMinAndMaxProcessor.getEvenMinAndMaxValue();
			long[] oddScanMinAndMaxValue = scanMinAndMaxProcessor.getOddMinAndMaxValue();
			long[] oddCrossSectionMinAndMaxValue = crossSectionMinAndMaxProcessor.getOddMinAndMaxValue();
			System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
			System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
			System.out.println("Cross section even positions: min value: " + evenCrossSectionMinAndMaxValue[0] + ", max value: " + evenCrossSectionMinAndMaxValue[1]);
			System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
			
			ActionableLongGrid2DColorMapperProcessor evenScanColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(colorMapper, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
			ActionableLongGrid2DColorMapperProcessor oddScanColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(colorMapper, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
			ActionableLongGrid2DColorMapperProcessor evenColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(colorMapper, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
			ActionableLongGrid2DColorMapperProcessor oddColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(colorMapper, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
			
			String evenCrossSectionFolder, oddCrossSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				if (scanZ%2 == 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			} else {
				if (crossSectionZ%2 != 0) {
					evenCrossSectionFolder = "even";
					oddCrossSectionFolder = "odd";
				} else {
					evenCrossSectionFolder = "odd";
					oddCrossSectionFolder = "even";
				}
				if (scanZ%2 != 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			}
			EvenOddImageRenderingProcessor evenScanImageRenderer = 
					new EvenOddImageRenderingProcessor(true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			EvenOddImageRenderingProcessor oddScanImageRenderer = 
					new EvenOddImageRenderingProcessor(false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			EvenOddImageRenderingProcessor evenCrossSectionImageRenderer = 
					new EvenOddImageRenderingProcessor(true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			EvenOddImageRenderingProcessor oddCrossSectionImageRenderer = 
					new EvenOddImageRenderingProcessor(false, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddCrossSectionFolder + "/" + numberedFolder, caName + "_" + step + ".png");
			
			evenScanColorMapperProcessor.addProcessor(evenScanImageRenderer);
			oddScanColorMapperProcessor.addProcessor(oddScanImageRenderer);
			evenColorMapperProcessor.addProcessor(evenCrossSectionImageRenderer);
			oddColorMapperProcessor.addProcessor(oddCrossSectionImageRenderer);
			
			scan.addProcessor(evenScanColorMapperProcessor);
			scan.addProcessor(oddScanColorMapperProcessor);
			crossSection.addProcessor(evenColorMapperProcessor);
			crossSection.addProcessor(oddColorMapperProcessor);
			
			//generate images
			ca.processGrid();
			
			scan.removeProcessor(evenScanColorMapperProcessor);
			scan.removeProcessor(oddScanColorMapperProcessor);
			crossSection.removeProcessor(evenColorMapperProcessor);
			crossSection.removeProcessor(oddColorMapperProcessor);
			
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
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			crossSection.addProcessor(crossSectionMinAndMaxProcessor);
			step++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImagesNoBackUp(LongModel3D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		long step = ca.getStep();
		int numberedFolder = (int) (step/imgsPerFolder);
		int folderImageCount = (int) (step%imgsPerFolder);
		String caName = ca.getName();
		String scanImgPath = path + "/" + colorMapper.getColormapName() + "/" + ca.getZLabel() + "_scan/" ;
		String crossSectionImgPath = path + "/" + colorMapper.getColormapName() 
				+ "/" + ca.getZLabel() + "=" + crossSectionZ + "/";
		int scanZ = ca.getMinZ();
		do {
			System.out.println("Step: " + step);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max " + ca.getYLabel() + ": " + maxY + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			System.out.println("Scan " + ca.getZLabel() + ": " + scanZ);
			LongGrid2D scan = ca.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = scan.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, scanImgPath + numberedFolder, caName + "_" + step + ".png");
			scanZ++;
			LongGrid2D crossSection = ca.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = crossSection.getMinAndMax();
			System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
					caName + "_" + step + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			step++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createXScanningImages(ActionableModel3D<IntGrid3D> ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String imagesPath) throws Exception {
		long step = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getColormapName() + "/scans/step=" + step + "/" + ca.getXLabel() + "_scan/" ;
		System.out.println("Scanning grid at step " + step + " along the " + ca.getXLabel() + " axis.");
		
		ca.addProcessor(new GridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX(), maxX = gridBlock.getMaxX();
				System.out.println("New block: [" + minX + "," + maxX + "]");
				for (int scanX = minX; scanX <= maxX; scanX++) {
					System.out.println(ca.getXLabel() + ": " + scanX);
					IntGrid2D crossSection = gridBlock.crossSectionAtX(scanX);
					int[] minAndMaxValue = crossSection.getMinAndMax();
					System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorGrid, minWidth, minHeight, imgsPath, caName + "_" + scanX + ".png");
				}
			}
			
			@Override
			public void beforeProcessing() throws Exception {
				//do nothing		
			}
			
			@Override
			public void afterProcessing() throws Exception {
				//do nothing
			}
		});

		//generate images
		ca.processGrid();
	
		System.out.println("Finished!");
	}
	
	public void createXScanningEvenOddImages(ActionableModel3D<IntGrid3D> ca, ColorMapper colorMapper, 
			int imageWidth, int imageHeight, String imagesPath) throws Exception {
		long step = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getColormapName() + "/scans/step=" 
				+ step + "/" + ca.getXLabel() + "_scan/"  + imageWidth + "x" + imageHeight + "/";
		System.out.println("Scanning grid at step " + step + " along the " + ca.getXLabel() + " axis.");
		
		ca.addProcessor(new GridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX();
				int maxX = gridBlock.getMaxX();
				System.out.println("New block: Min " + ca.getXLabel() + ": " + minX + System.lineSeparator() + "Max " + ca.getXLabel() + ": " + maxX);
				boolean isEven = (minX + step)%2 == 0;
				for (int scanX = minX; scanX <= maxX; scanX++, isEven = !isEven) {
					System.out.println(ca.getXLabel() + ": " + scanX);
					IntGrid2D crossSection = gridBlock.crossSectionAtX(scanX);
					SubareaGrid<IntGrid2D> crossSectionSubareaGrid = new SubareaGrid<IntGrid2D>(crossSection, imageWidth, imageHeight);
					int subareasGridMinX = crossSectionSubareaGrid.getMinX();
					int subareasGridMaxX = crossSectionSubareaGrid.getMaxX();
					for (int subareasX = subareasGridMinX; subareasX <= subareasGridMaxX; subareasX++) {
						int subareasGridLocalMinY = crossSectionSubareaGrid.getMinY(subareasX);
						int subareasGridLocalMaxY = crossSectionSubareaGrid.getMaxY(subareasX);
						for (int subareasY = subareasGridLocalMinY; subareasY <= subareasGridLocalMaxY; subareasY++) {
							System.out.println("Subarea: (" + subareasX + "," + subareasY + ")");
							int framedGridMinX = subareasX * imageWidth;
							int framedGridMaxX = framedGridMinX + imageWidth - 1;
							int framedGridMinY = subareasY * imageHeight;
							int framedGridMaxY = framedGridMinY + imageHeight - 1;
							IntGrid2D crossSectionSubarea = crossSectionSubareaGrid.getSubareaAtPosition(subareasX, subareasY);
							int[] evenMinAndMaxValue = crossSectionSubarea.getEvenOddPositionsMinAndMax(isEven);
							if (evenMinAndMaxValue != null) {
								System.out.println("Even positions min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Even positions max value: " + evenMinAndMaxValue[1]);
								ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSectionSubarea, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
								createImageFromEvenOrOddPositions(colorGrid, isEven, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
										imageWidth, imageHeight, imgsPath + "even/" 
										+ subareasX + "," + subareasY, caName + "_" + scanX + ".png");
							} else {
								createEmptyImage(framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
										imageWidth, imageHeight, imgsPath + "even/" 
										+ subareasX + "," + subareasY, caName + "_" + scanX + ".png");
							}							
							int[] oddMinAndMaxValue = crossSectionSubarea.getEvenOddPositionsMinAndMax(!isEven);
							if (oddMinAndMaxValue != null) {
								System.out.println("Odd positions min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Odd positions max value: " + oddMinAndMaxValue[1]);
								ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSectionSubarea, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
								createImageFromEvenOrOddPositions(colorGrid, !isEven, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
										imageWidth, imageHeight, imgsPath + "odd/" 
										+ subareasX + "," + subareasY, caName + "_" + scanX + ".png");
							} else {
								createEmptyImage(framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
										imageWidth, imageHeight, imgsPath + "odd/" 
										+ subareasX + "," + subareasY, caName + "_" + scanX + ".png");
							}							
						}						
					}					
				}
			}
			
			@Override
			public void beforeProcessing() throws Exception {
				//do nothing		
			}
			
			@Override
			public void afterProcessing() throws Exception {
				//do nothing
			}
		});

		//generate images
		ca.processGrid();
	
		System.out.println("Finished!");
	}
	
	public void createZScanningEvenOddImages(ActionableModel3D<IntGrid3D> ca,
			ColorMapper colorMapper, String imagesPath) throws Exception {
		long step = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getColormapName() + "/scans/step=" 
				+ step + "/" + ca.getZLabel() + "_scan/" ;
		System.out.println("Scanning grid at step " + step + " along the " + ca.getZLabel() + " axis.");
		
		ca.addProcessor(new GridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX();
				int maxX = gridBlock.getMaxX();
				int minY = gridBlock.getMinY();
				int maxY = gridBlock.getMaxY();
				int minZ = gridBlock.getMinZ();
				int maxZ = gridBlock.getMaxZ();
				System.out.println("New block: [" + minX + "," + maxX + "]");
				boolean isEven = (minZ + step)%2 == 0;
				for (int scanZ = minZ; scanZ <= maxZ; scanZ++, isEven = !isEven) {
					System.out.println(ca.getZLabel() + ": " + scanZ);
					IntGrid2D crossSection = gridBlock.crossSectionAtZ(scanZ);
					int[] evenMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEven);
					if (evenMinAndMaxValue != null) {
						System.out.println("Even positions min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Even positions max value: " + evenMinAndMaxValue[1]);
						ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSection, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(colorGrid, isEven, minX, maxX, minY, maxY,
								imgsPath + minX + "<=" + ca.getXLabel() + "<=" + maxX + "/even/", caName + "_" + scanZ + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY,
								imgsPath + minX + "<=" + ca.getXLabel() + "<=" + maxX + "/even/", caName + "_" + scanZ + ".png");
					}					
					int[] oddMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(!isEven);
					ObjectGrid2D<Color> colorGrid;
					if (oddMinAndMaxValue != null) {
						System.out.println("Odd positions min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Odd positions max value: " + oddMinAndMaxValue[1]);
						colorGrid = colorMapper.getMappedGrid(crossSection, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(colorGrid, !isEven, minX, maxX, minY, maxY,
								imgsPath + minX + "<=" + ca.getXLabel() + "<=" + maxX + "/odd/", caName + "_" + scanZ + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY,
								imgsPath + minX + "<=" + ca.getXLabel() + "<=" + maxX + "/odd/", caName + "_" + scanZ + ".png");
					}
				}
			}
			
			@Override
			public void beforeProcessing() throws Exception {
				//do nothing		
			}
			
			@Override
			public void afterProcessing() throws Exception {
				//do nothing
			}
		});

		//generate images
		ca.processGrid();
	
		System.out.println("Finished!");
	}
	
	public static int getGridPositionSize(int minX, int maxX, int minY, int maxY, int preferredMaxWidth, int preferredMaxHeight) {
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
	
	private static int getGridPositionSize(Grid2D grid, int preferredMaxWidth, int preferredMaxHeight) {
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
	
	public static void createImage(ObjectGrid2D<Color> grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImage(grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImage(ObjectGrid2D<Color> grid, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
		int framedGridMinY, framedGridMaxY;
		int gridMaxY = grid.getMaxY(), gridMinY = grid.getMinY();
		if (maxY > gridMaxY) {
			gridTopMargin = (maxY - gridMaxY) * gridPositionSize;
			framedGridMaxY = gridMaxY;
		} else {
			framedGridMaxY = maxY;
		}
		if (minY < gridMinY) {
			framedGridMinY = gridMinY;
		} else {
			framedGridMinY = minY;
		}
		int dataIndex = (canvasTopMargin + gridTopMargin) * width * 3;
		for (int y = framedGridMaxY; y >= framedGridMinY; y--) {
			int gridMinXAtY = grid.getMinX(y);
			int gridMaxXAtY = grid.getMaxX(y);
			int gridLeftMargin = 0, gridRightMargin = 0;
			int framedGridMinXAtY, framedGridMaxXAtY;
			if (minX < gridMinXAtY) {
				gridLeftMargin = (gridMinXAtY - minX) * gridPositionSize;
				framedGridMinXAtY = gridMinXAtY;
			} else {
				framedGridMinXAtY = minX;
			}
			if (maxX > gridMaxXAtY) {
				gridRightMargin = (maxX - gridMaxXAtY) * gridPositionSize;
				framedGridMaxXAtY = gridMaxXAtY;
			} else {
				framedGridMaxXAtY = maxX;
			}
			for (int i = 0; i < gridPositionSize; i++) {
				dataIndex += gridLeftMargin * 3;
				for (int x = framedGridMinXAtY; x <= framedGridMaxXAtY; x++) {
					java.awt.Color c = grid.getFromPosition(x, y);
					byte r = (byte) c.getRed(), g = (byte) c.getGreen(), b = (byte) c.getBlue();
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = r;
						pixelData[dataIndex++] = g;
						pixelData[dataIndex++] = b;
					}				
				}
				dataIndex += (canvasRightMargin + gridRightMargin) * 3;
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
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createEmptyImage(minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createEmptyImage(int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		
		int framedGridWidth = maxX - minX + 1;
		int framedGridHeight = maxY - minY + 1;	
		int framedGridWidthInPixels = framedGridWidth * gridPositionSize;
		int framedGridHeightInPixels = framedGridHeight * gridPositionSize;	
		int imageWidth = Math.max(framedGridWidthInPixels, minWidth);
		int imageHeight = Math.max(framedGridHeightInPixels, minHeight);	
		long longByteCount = (long)imageWidth * imageHeight * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];	
		saveAsPngImage(pixelData, imageWidth, imageHeight, path, name);
	}
	
	public static void createImage(ObjectGrid2D<Color> grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(grid, minWidth, minHeight);
		createImage(grid, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImage(ObjectGrid2D<Color> grid, int gridPositionSize, int minWidth, int minHeight, String path, String name) throws Exception {
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
			int localLeftMargin = (localMinX - minX)  * gridPositionSize;
			int localRightMargin = rightMargin + ((maxX - localMaxX) * gridPositionSize);
			for (int i = 0; i < gridPositionSize; i++) {
				dataIndex += 3 * localLeftMargin;
				for (int x = localMinX; x <= localMaxX; x++) {
					java.awt.Color c = grid.getFromPosition(x,y);
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
				}
				dataIndex += 3 * localRightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ObjectGrid2D<Color> grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(grid, minWidth, minHeight);
		createImageFromEvenOrOddPositions(isEven, grid, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ObjectGrid2D<Color> grid, int gridPositionSize, int minWidth, int minHeight, String path, String name) throws Exception {
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
			boolean isPositionEven = (localMinX+y)%2 == 0;
			if (isEven != isPositionEven) { 
				localMinX++;
			}
			int localLeftMargin = (localMinX - minX)  * gridPositionSize;
			int localRightMargin = rightMargin + ((maxX - localMaxX) * gridPositionSize);
			for (int i = 0; i < gridPositionSize; i++) {
				dataIndex += 3 * localLeftMargin;
				int x = localMinX;
				for (; x < localMaxX; x+=2) {
					java.awt.Color c = grid.getFromPosition(x,y);
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
					dataIndex += 3 * gridPositionSize;
				}
				if (x == localMaxX) {
					java.awt.Color c = grid.getFromPosition(x,y);
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
				}
				dataIndex += 3 * localRightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(ObjectGrid2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, String path, String name) 
			throws Exception {
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		createImageFromEvenOrOddPositions(grid, isEven, minX, maxX, minY, maxY, 1, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(ObjectGrid2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddPositions(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(ObjectGrid2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
		int framedGridMinY, framedGridMaxY;
		int gridMaxY = grid.getMaxY(), gridMinY = grid.getMinY();
		if (maxY > gridMaxY) {
			gridTopMargin = (maxY - gridMaxY) * gridPositionSize;
			framedGridMaxY = gridMaxY;
		} else {
			framedGridMaxY = maxY;
		}
		if (minY < gridMinY) {
			framedGridMinY = gridMinY;
		} else {
			framedGridMinY = minY;
		}
		int dataIndex = (canvasTopMargin + gridTopMargin) * width * 3;
		for (int y = framedGridMaxY; y >= framedGridMinY; y--) {
			int gridMinXAtY = grid.getMinX(y);
			int gridMaxXAtY = grid.getMaxX(y);
			int gridLeftMargin = 0, gridRightMargin = 0;
			int framedGridMinXAtY, framedGridMaxXAtY;
			if (minX < gridMinXAtY) {
				gridLeftMargin = (gridMinXAtY - minX) * gridPositionSize;
				framedGridMinXAtY = gridMinXAtY;
			} else {
				framedGridMinXAtY = minX;
			}
			if (maxX > gridMaxXAtY) {
				gridRightMargin = (maxX - gridMaxXAtY) * gridPositionSize;
				framedGridMaxXAtY = gridMaxXAtY;
			} else {
				framedGridMaxXAtY = maxX;
			}
			boolean isPositionEven = (framedGridMinXAtY+y)%2 == 0;
			if (isEven != isPositionEven) { 
				framedGridMinXAtY++;
				gridLeftMargin += gridPositionSize;
			}
			for (int i = 0; i < gridPositionSize; i++) {
				dataIndex += gridLeftMargin * 3;
				int x = framedGridMinXAtY;
				for (; x < framedGridMaxXAtY; x+=2) {
					java.awt.Color c = grid.getFromPosition(x,y);
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
					dataIndex += 3 * gridPositionSize;
				}
				if (x == framedGridMaxXAtY) {
					java.awt.Color c = grid.getFromPosition(x,y);
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
				}
				dataIndex += (canvasRightMargin + gridRightMargin) * 3;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddYPositions(ObjectGrid2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddYPositions(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddYPositions(ObjectGrid2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
		int framedGridMinY, framedGridMaxY;
		int gridMaxY = grid.getMaxY(), gridMinY = grid.getMinY();
		if (maxY > gridMaxY) {
			gridTopMargin = (maxY - gridMaxY) * gridPositionSize;
			framedGridMaxY = gridMaxY;
		} else {
			framedGridMaxY = maxY;
		}
		if (minY < gridMinY) {
			framedGridMinY = gridMinY;
		} else {
			framedGridMinY = minY;
		}
		int dataIndex = (canvasTopMargin + gridTopMargin) * width * 3;
		boolean isYEven = framedGridMaxY%2 == 0;
		for (int y = framedGridMaxY; y >= framedGridMinY; y--, isYEven = !isYEven) {
			int gridMinXAtY = grid.getMinX(y);
			int gridMaxXAtY = grid.getMaxX(y);
			int gridLeftMargin = 0, gridRightMargin = 0;
			int framedGridMinXAtY, framedGridMaxXAtY;
			if (minX < gridMinXAtY) {
				gridLeftMargin = (gridMinXAtY - minX) * gridPositionSize;
				framedGridMinXAtY = gridMinXAtY;
			} else {
				framedGridMinXAtY = minX;
			}
			if (maxX > gridMaxXAtY) {
				gridRightMargin = (maxX - gridMaxXAtY) * gridPositionSize;
				framedGridMaxXAtY = gridMaxXAtY;
			} else {
				framedGridMaxXAtY = maxX;
			}
			if (isEven == isYEven) {
				for (int i = 0; i < gridPositionSize; i++) {
					dataIndex += gridLeftMargin * 3;
					for (int x = framedGridMinXAtY; x <= framedGridMaxXAtY; x++) {
						java.awt.Color c = grid.getFromPosition(x, y);
						byte r = (byte) c.getRed(), g = (byte) c.getGreen(), b = (byte) c.getBlue();
						for (int j = 0; j < gridPositionSize; j++) {
							pixelData[dataIndex++] = r;
							pixelData[dataIndex++] = g;
							pixelData[dataIndex++] = b;
						}				
					}
					dataIndex += (canvasRightMargin + gridRightMargin) * 3;
				}
			} else {
				dataIndex += ((gridLeftMargin * 3) + ((framedGridMaxXAtY - framedGridMinXAtY + 1) * gridPositionSize * 3) + ((canvasRightMargin + gridRightMargin) * 3)) * gridPositionSize;
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
