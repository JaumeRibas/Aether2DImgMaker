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
import cellularautomata.automata.Aether3D;
import cellularautomata.evolvinggrid.EvolvingIntGrid2D;
import cellularautomata.evolvinggrid.EvolvingIntGrid3D;
import cellularautomata.evolvinggrid.EvolvingLongGrid2D;
import cellularautomata.evolvinggrid.EvolvingLongGrid3D;
import cellularautomata.evolvinggrid.EvolvingShortGrid2D;
import cellularautomata.evolvinggrid.EvolvingShortGrid3D;
import cellularautomata.evolvinggrid.ActionableEvolvingGrid3D;
import cellularautomata.evolvinggrid.EvolvingNumberGrid3D;
import cellularautomata.grid.IntGridMinAndMaxProcessor;
import cellularautomata.grid.LongGridEvenOddMinAndMaxProcessor;
import cellularautomata.grid.LongGridMinAndMaxProcessor;
import cellularautomata.grid.MinAndMax;
import cellularautomata.grid2d.NumberGrid2D;
import cellularautomata.grid2d.ObjectGrid2D;
import cellularautomata.grid2d.Grid2D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;
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
	
	public void createImages(EvolvingLongGrid2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getClass().getSimpleName() + "/";
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			long[] minAndMaxValue = ca.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createImages(EvolvingIntGrid2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getClass().getSimpleName() + "/";
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			int[] minAndMaxValue = ca.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createEvenOddImages(EvolvingLongGrid2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getClass().getSimpleName() + "/";
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			String evenFolder, oddFolder;
			if (isEvenStep) {
				evenFolder = "even";
				oddFolder = "odd";
			} else {
				evenFolder = "odd";
				oddFolder = "even";
			}
			long[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(true);
			long[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(false);
			
			if (evenMinAndMaxValue != null) {
				System.out.println("Even positions: min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + evenMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenColorGrid = colorMapper.getMappedGrid(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
					imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			}
			if (oddMinAndMaxValue != null) {
				System.out.println("odd positions: min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Max value: " + oddMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddColorGrid = colorMapper.getMappedGrid(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			}
			
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createCrossSectionImages(EvolvingLongGrid3D ca, int z, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/z=" + z + "/";
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			LongGrid2D xSection = ca.crossSectionAtZ(z);
			long[] minAndMaxValue = xSection.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					imgPath + numberedFolder, ca.getName() + "_x_section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createValueScanCrossSectionImages(EvolvingLongGrid3D ca, int z, int valueRange, long scanSpeed, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {	
		if (valueRange < 1)
			throw new IllegalArgumentException("Value range must be bigger than 0.");
		if (scanSpeed == 0)
			throw new IllegalArgumentException("Scan speed must be different from zero.");
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long rangeMin = 0, rangeMax = 0;
		boolean firstIteration = true;
		String imgPath = path + "/z=" + z + "/value_scan/value_range=" + valueRange + "/scan_speed=" + scanSpeed + "/";
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			LongGrid2D xSection = ca.crossSectionAtZ(z);
			long[] minAndMaxValue = xSection.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			if (scanSpeed > 0 && (rangeMax > minAndMaxValue[1] || rangeMax < minAndMaxValue[0] || firstIteration)) {
				rangeMin = minAndMaxValue[0];
				rangeMax = rangeMin + valueRange - 1;
			} else if (scanSpeed < 0 && (rangeMin < minAndMaxValue[0] || rangeMin > minAndMaxValue[1] || firstIteration)) {
				rangeMax = minAndMaxValue[1];
				rangeMin = rangeMax - valueRange + 1;
			}
			System.out.println("Range min value: " + rangeMin + System.lineSeparator() + "Range max value: " + rangeMax);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSection, rangeMin, rangeMax);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_x_section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			rangeMin += scanSpeed;
			rangeMax += scanSpeed;
			firstIteration = false;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createValueRangesCrossSectionImages(EvolvingLongGrid3D ca, int z, int valueRange, long rangeLeap, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		if (valueRange < 1)
			throw new IllegalArgumentException("Value range must be bigger than 0.");
		if (rangeLeap <= 0)
			throw new IllegalArgumentException("Range leap must be bigger than zero.");
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long firstRangeMin = 0, rangeMin = 0;
		boolean firstIteration = true;
		String imgPath = path + "/z=" + z + "/value_ranges/";
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			LongGrid2D xSection = ca.crossSectionAtZ(z);
			long[] minAndMaxValue = xSection.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			if (firstIteration) {
				firstRangeMin = minAndMaxValue[0];
			}
			rangeMin = firstRangeMin;
			int rangeId = 0;
			while (rangeMin < minAndMaxValue[0]) {
				rangeMin += rangeLeap;
				rangeId++;
			}
			while (rangeMin <= minAndMaxValue[1]) {
				long rangeMax = rangeMin + valueRange - 1;
				System.out.println("Range " + rangeId + System.lineSeparator() + "Min value: " + rangeMin + System.lineSeparator() + "Max value: " + rangeMax);
				ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSection, rangeMin, rangeMax);
				createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + "/" + rangeId + "(minVal=" + rangeMin + "_maxVal=" + rangeMax + ")/" 
						+ numberedFolder, ca.getName() + "_x_section_" + currentStep + ".png");
				rangeMin += rangeLeap;
				rangeId++;
			}
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			firstIteration = false;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createScanningImages(EvolvingLongGrid3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		String imgPath = path + "/z_scan/";
		int scanZ = ca.getMinZ();
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D xSection = ca.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = xSection.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					imgPath + numberedFolder, ca.getName() + "_x_section_" + currentStep + ".png");
			scanZ++;
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
				ca.backUp(path + "/backups", ca.getClass().getSimpleName() + "_" + currentStep);					
			}
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}

	public void createAether3DLastStepsCrossSectionImages(ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		int i = 0;
		String imgPath = path + "/" + new Aether3D(0).getName() 
				+ "/img/lats_steps/asymmetric_section/" + colorMapper.getClass().getSimpleName();
		while (true) {
			long initialValue = i;
			System.out.println("Initial value: " + initialValue);
			Aether3D ae = new Aether3D(initialValue);
			while (ae.nextStep());
			long lastStep = ae.getStep() - 1;
			System.out.println("Last step: " + lastStep);
			LongGrid3D asymmetricSection = ae.asymmetricSection();
			LongGrid2D xSection = asymmetricSection.crossSectionAtZ(asymmetricSection.getMinZ());
			long[] minAndMaxValue = xSection.getMinAndMax();
			System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			int minX = asymmetricSection.getMinX(), maxX = asymmetricSection.getMaxX(), 
					minY = asymmetricSection.getMinY(), maxY = asymmetricSection.getMaxY();
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath, 
					ae.getName() + "_x_section_" + initialValue + ".png");
			i++;
		}
	}
	
	public void createScanningAndCrossSectionImages(EvolvingIntGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String path, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, path, backupPath);	
	}
	
	public void createScanningAndCrossSectionImages(EvolvingIntGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		String caName = ca.getName();
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		boolean caFinished = false;
		boolean lastPassFinished = false;
		boolean lastPassStarted = false;
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			if (scanZ >= ca.getMaxZ()) {
				scanZ = ca.getMinZ();
				if (caFinished) {
					if (lastPassStarted) {
						lastPassFinished = true;
					} else {
						lastPassStarted = true;
					}
				}
			}
			System.out.println("Scan z: " + scanZ);
			IntGrid2D scan = ca.crossSectionAtZ(scanZ);
			int[] minAndMaxValue = scan.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			scanZ++;
			if (!caFinished) {
				IntGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = xSection.getMinAndMax();
				System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
				colorGrid = crossSectionColorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
						caName + "_x_section_" + currentStep + ".png");
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
				ca.backUp(backupPath, ca.getClass().getSimpleName() + "_" + currentStep);					
			}
			currentStep++;
			if (!caFinished) {
				caFinished = !ca.nextStep();
			}
			System.out.println();
		} while (!caFinished || !lastPassFinished);
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingIntGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMaxZ();
		createScanningAndCrossSectionEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingIntGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan z: " + scanZ);
			EvolvingIntGrid2D scan = ca.crossSectionAtZ(scanZ);
			EvolvingIntGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
				}
				if (scanZ%2 != 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			}
			
			int[] evenScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(true);
			int[] evenXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMax(true);
			int[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(false);
			int[] oddXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMax(false);
			
			if (evenScanMinAndMaxValue != null) {
				System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = scanningColorMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (evenXSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(xSection, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddXSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(xSection, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanZ--;
			if (scanZ < ca.getMinZ())
				scanZ = ca.getMaxZ();			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXScanningAndZCrossSectionEvenOddImages(EvolvingIntGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionEvenOddImages(ca, xScanInitialIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXScanningAndZCrossSectionEvenOddImages(EvolvingIntGrid3D ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/x_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan x: " + scanX);
			IntGrid2D scan = ca.crossSectionAtX(scanX);
			IntGrid2D crossSection = ca.crossSectionAtZ(crossSectionZ);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
			int[] evenXSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
			int[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(false);
			int[] oddXSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
			
			if (evenScanMinAndMaxValue != null) {
				System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = scanningColorMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (evenXSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddXSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXZScanningAndZCrossSectionOddImagesFromLongGrid3D(ActionableEvolvingGrid3D<LongGrid3D> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		int zScanInitialIndex = ca.getMaxZ();
		createXZScanningAndZCrossSectionOddImagesFromLongGrid3D(ca, xScanInitialIndex, zScanInitialIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXZScanningAndZCrossSectionOddImagesFromLongGrid3D(ActionableEvolvingGrid3D<LongGrid3D> ca, int xScanInitialIndex, int zScanInitialIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenCrossSectionZ = (currentStep + crossSectionZ)%2 != 0;
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		int scanZ = zScanInitialIndex;
		
		String caName = ca.getName();
		String xScanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/x_scan/";
		String zScanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		LongGrid3DZCrossSectionCopierProcessor zCopier = new LongGrid3DZCrossSectionCopierProcessor();
		LongGrid3DXCrossSectionCopierProcessor xCopier = new LongGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		zCopier.requestCopy(scanZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan x: " + scanX);
			System.out.println("Scan z: " + scanZ);
			boolean isEvenScanX = (currentStep + scanX)%2 != 0;
			boolean isEvenScanZ = (currentStep + scanZ)%2 != 0;
			LongGrid2D xScan = xCopier.getCopy(scanX);
			LongGrid2D zScan = zCopier.getCopy(scanZ);
			LongGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			long[] oddXScanMinAndMaxValue = xScan.getEvenOddPositionsMinAndMax(isEvenScanX);
			long[] oddZScanMinAndMaxValue = zScan.getEvenOddPositionsMinAndMax(isEvenScanZ);
			long[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenCrossSectionZ);
			
			if (oddXScanMinAndMaxValue != null) {
				System.out.println("X scan odd positions: min value: " + oddXScanMinAndMaxValue[0] + ", max value: " + oddXScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(xScan, oddXScanMinAndMaxValue[0], oddXScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(isEvenScanX, oddScanColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddZScanMinAndMaxValue != null) {
				System.out.println("Z scan odd positions: min value: " + oddZScanMinAndMaxValue[0] + ", max value: " + oddZScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(zScan, oddZScanMinAndMaxValue[0], oddZScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(isEvenScanZ, oddScanColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(isEvenCrossSectionZ, oddCrossSectionColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
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
			currentStep++;
			isEvenStep = !isEvenStep;
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
	
	public void createXZScanningAndZCrossSectionOddImagesFromIntGrid3D(ActionableEvolvingGrid3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		int zScanInitialIndex = ca.getMaxZ();
		createXZScanningAndZCrossSectionOddImagesFromIntGrid3D(ca, xScanInitialIndex, zScanInitialIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXZScanningAndZCrossSectionOddImagesFromIntGrid3D(ActionableEvolvingGrid3D<IntGrid3D> ca, int xScanInitialIndex, int zScanInitialIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenCrossSectionZ = (currentStep + crossSectionZ)%2 != 0;
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		int scanZ = zScanInitialIndex;
		
		String caName = ca.getName();
		String xScanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/x_scan/";
		String zScanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		IntGrid3DZCrossSectionCopierProcessor zCopier = new IntGrid3DZCrossSectionCopierProcessor();
		IntGrid3DXCrossSectionCopierProcessor xCopier = new IntGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		zCopier.requestCopy(scanZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan x: " + scanX);
			System.out.println("Scan z: " + scanZ);
			boolean isEvenScanX = (currentStep + scanX)%2 != 0;
			boolean isEvenScanZ = (currentStep + scanZ)%2 != 0;
			IntGrid2D xScan = xCopier.getCopy(scanX);
			IntGrid2D zScan = zCopier.getCopy(scanZ);
			IntGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			int[] oddXScanMinAndMaxValue = xScan.getEvenOddPositionsMinAndMax(isEvenScanX);
			int[] oddZScanMinAndMaxValue = zScan.getEvenOddPositionsMinAndMax(isEvenScanZ);
			int[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenCrossSectionZ);
			
			if (oddXScanMinAndMaxValue != null) {
				System.out.println("X scan odd positions: min value: " + oddXScanMinAndMaxValue[0] + ", max value: " + oddXScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(xScan, oddXScanMinAndMaxValue[0], oddXScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(isEvenScanX, oddScanColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						xScanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddZScanMinAndMaxValue != null) {
				System.out.println("Z scan odd positions: min value: " + oddZScanMinAndMaxValue[0] + ", max value: " + oddZScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(zScan, oddZScanMinAndMaxValue[0], oddZScanMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(isEvenScanZ, oddScanColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddCrossSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddCrossSectionMinAndMaxValue[0] + ", max value: " + oddCrossSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
				createImageFromEvenOrOddPositions(isEvenCrossSectionZ, oddCrossSectionColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
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
			currentStep++;
			isEvenStep = !isEvenStep;
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
	
	public <T extends FieldElement<T> & Comparable<T>> void createXScanningAndZCrossSectionEvenOddImages(
			EvolvingNumberGrid3D<T> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionEvenOddImages(ca, xScanInitialIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createXScanningAndZCrossSectionEvenOddImages(
			EvolvingNumberGrid3D<T> ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/x_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan x: " + scanX);
			NumberGrid2D<T> scan = ca.crossSectionAtX(scanX);
			NumberGrid2D<T> crossSection = ca.crossSectionAtZ(crossSectionZ);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
				}
				if (scanX%2 != 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			}
			
			MinAndMax<T> evenScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(true);
			MinAndMax<T> evenXSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
			MinAndMax<T> oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(false);
			MinAndMax<T> oddXSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
			
			if (evenScanMinAndMaxValue != null) {
				System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue.getMin() + ", max value: " + evenScanMinAndMaxValue.getMax());
				ObjectGrid2D<Color> evenScanColorGrid = scanningColorMapper.getMappedGrid(scan, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
				createEvenOddImageLeftToRight(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
				createEvenOddImageLeftToRight(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (evenXSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenXSectionMinAndMaxValue.getMin() + ", max value: " + evenXSectionMinAndMaxValue.getMax());
				ObjectGrid2D<Color> evenCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, evenXSectionMinAndMaxValue.getMin(), evenXSectionMinAndMaxValue.getMax());
				createEvenOddImageLeftToRight(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddXSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue.getMin() + ", max value: " + oddXSectionMinAndMaxValue.getMax());
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, oddXSectionMinAndMaxValue.getMin(), oddXSectionMinAndMaxValue.getMax());
				createEvenOddImageLeftToRight(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createXScanningAndZCrossSectionOddImages(
			EvolvingNumberGrid3D<T> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionOddImages(ca, xScanInitialIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createXScanningAndZCrossSectionOddImages(
			EvolvingNumberGrid3D<T> ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenCrossSectionZ = (currentStep + crossSectionZ)%2 != 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/x_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan x: " + scanX);
			boolean isEvenScanX = (currentStep + scanX)%2 != 0;
			NumberGrid2D<T> scan = ca.crossSectionAtX(scanX);
			NumberGrid2D<T> crossSection = ca.crossSectionAtZ(crossSectionZ);
			
			MinAndMax<T> oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(isEvenScanX);
			MinAndMax<T> oddXSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenCrossSectionZ);
			
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
				createEvenOddImageLeftToRight(oddScanColorGrid, isEvenScanX, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddXSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue.getMin() + ", max value: " + oddXSectionMinAndMaxValue.getMax());
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, oddXSectionMinAndMaxValue.getMin(), oddXSectionMinAndMaxValue.getMax());
				createEvenOddImageLeftToRight(oddCrossSectionColorGrid, isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			currentStep++;
			isEvenCrossSectionZ = !isEvenCrossSectionZ;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingLongGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingLongGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan z: " + scanZ);
			EvolvingLongGrid2D scan = ca.crossSectionAtZ(scanZ);
			EvolvingLongGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
				}
				if (scanZ%2 != 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			}
			
			long[] evenScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(true);
			long[] evenXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMax(true);
			long[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(false);
			long[] oddXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMax(false);
			
			if (evenScanMinAndMaxValue != null) {
				System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = scanningColorMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage( minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (evenXSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(xSection, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddXSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);		
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(xSection, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);	
				createEvenOddImageLeftToRight(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingShortGrid3D ca, int crossSectionZ, 
			ColorMapper evenColorMapper, ColorMapper oddColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionEvenOddImages(ca, scanInitialZIndex, crossSectionZ, evenColorMapper, 
			oddColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingShortGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper evenColorMapper, ColorMapper oddColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String evenImgPath = imagesPath + "/" + evenColorMapper.getClass().getSimpleName();
		String oddImgPath = imagesPath + "/" + oddColorMapper.getClass().getSimpleName();
		String crossSectionFolder = "/z=" + crossSectionZ + "/";
		
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan z: " + scanZ);
			EvolvingShortGrid2D scan = ca.crossSectionAtZ(scanZ);
			EvolvingShortGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
			
			String evenXSectionPath, oddXSectionPath, evenScanPath, oddScanPath;
			ColorMapper evenXSectionMapper, oddXSectionMapper, evenScanMapper, oddScanMapper;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenXSectionMapper = evenColorMapper;
					oddXSectionMapper = oddColorMapper;	
					evenXSectionPath = evenImgPath + crossSectionFolder + "even";
					oddXSectionPath = oddImgPath + crossSectionFolder + "odd";
				} else {
					evenXSectionMapper = oddColorMapper;
					oddXSectionMapper = evenColorMapper;
					evenXSectionPath = oddImgPath + crossSectionFolder + "odd";
					oddXSectionPath = evenImgPath + crossSectionFolder + "even";
				}
				if (scanZ%2 == 0) {
					evenScanMapper = evenColorMapper;
					oddScanMapper = oddColorMapper;
					evenScanPath = evenImgPath + "/z_scan/even";
					oddScanPath = oddImgPath + "/z_scan/odd";
				} else {
					evenScanMapper = oddColorMapper;
					oddScanMapper = evenColorMapper;
					evenScanPath = oddImgPath + "/z_scan/odd";
					oddScanPath = evenImgPath + "/z_scan/even";
				}
			} else {
				if (crossSectionZ%2 != 0) {
					evenXSectionMapper = evenColorMapper;
					oddXSectionMapper = oddColorMapper;	
					evenXSectionPath = evenImgPath + crossSectionFolder + "even";
					oddXSectionPath = oddImgPath + crossSectionFolder + "odd";
				} else {
					evenXSectionMapper = oddColorMapper;
					oddXSectionMapper = evenColorMapper;
					evenXSectionPath = oddImgPath + crossSectionFolder + "odd";
					oddXSectionPath = evenImgPath + crossSectionFolder + "even";
				}
				if (scanZ%2 != 0) {
					evenScanMapper = evenColorMapper;
					oddScanMapper = oddColorMapper;
					evenScanPath = evenImgPath + "/z_scan/even";
					oddScanPath = oddImgPath + "/z_scan/odd";
				} else {
					evenScanMapper = oddColorMapper;
					oddScanMapper = evenColorMapper;
					evenScanPath = oddImgPath + "/z_scan/odd";
					oddScanPath = evenImgPath + "/z_scan/even";
				}
			}
			
			short[] evenScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(true);
			short[] evenXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMax(true);
			short[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(false);
			short[] oddXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMax(false);
			
			if (evenScanMinAndMaxValue != null) {
				System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = evenScanMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						evenScanPath + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						evenScanPath + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = oddScanMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
						oddScanPath + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						oddScanPath + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (evenXSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenCrossSectionColorGrid = evenXSectionMapper.getMappedGrid(xSection, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						evenXSectionPath + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						evenXSectionPath + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddXSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);		
				ObjectGrid2D<Color> oddCrossSectionColorGrid = oddXSectionMapper.getMappedGrid(xSection, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);	
				createEvenOddImageLeftToRight(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
						oddXSectionPath + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						oddXSectionPath + "/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImages(EvolvingLongGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionImages(EvolvingLongGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		String caName = ca.getName();
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		boolean caFinished = false;
		boolean lastPassFinished = false;
		boolean lastPassStarted = false;
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			if (scanZ >= ca.getMaxZ()) {
				scanZ = ca.getMinZ();
				if (caFinished) {
					if (lastPassStarted) {
						lastPassFinished = true;
					} else {
						lastPassStarted = true;
					}
				}
			}
			System.out.println("Scan z: " + scanZ);
			LongGrid2D scan = ca.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = scan.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			scanZ++;
			if (!caFinished) {
				LongGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = xSection.getMinAndMax();
				System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
				colorGrid = crossSectionColorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
						caName + "_x_section_" + currentStep + ".png");
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
				ca.backUp(backupPath, ca.getClass().getSimpleName() + "_" + currentStep);					
			}
			currentStep++;
			if (!caFinished) {
				caFinished = !ca.nextStep();
			}
			System.out.println();
		} while (!caFinished || !lastPassFinished);
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImages(EvolvingShortGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String path, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMaxZ();
		createScanningAndCrossSectionImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, path, backupPath);	
	}
	
	public void createScanningAndCrossSectionImages(EvolvingShortGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		String caName = ca.getName();
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		boolean caFinished = false;
		boolean lastPassFinished = false;
		boolean lastPassStarted = false;
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			if (scanZ < ca.getMinZ()) {
				scanZ = ca.getMaxZ();
				if (caFinished) {
					if (lastPassStarted) {
						lastPassFinished = true;
					} else {
						lastPassStarted = true;
					}
				}
			}
			System.out.println("Scan z: " + scanZ);
			ShortGrid2D scan = ca.crossSectionAtZ(scanZ);
			short[] minAndMaxValue = scan.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			scanZ--;
			if (!caFinished) {
				ShortGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = xSection.getMinAndMax();
				System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
				colorGrid = crossSectionColorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
						caName + "_x_section_" + currentStep + ".png");
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
				ca.backUp(backupPath, ca.getClass().getSimpleName() + "_" + currentStep);					
			}
			currentStep++;
			if (!caFinished) {
				caFinished = !ca.nextStep();
			}
			System.out.println();
		} while (!caFinished || !lastPassFinished);
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImagesFromIntGrid3D(ActionableEvolvingGrid3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionImagesFromIntGrid3D(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionImagesFromIntGrid3D(ActionableEvolvingGrid3D<IntGrid3D> ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/" + "z=" + crossSectionZ + "/";
		
		ActionableGrid3DZCrossSectionProcessor<IntGrid3D, IntGrid2D> scan = 
				new ActionableGrid3DZCrossSectionProcessor<IntGrid3D, IntGrid2D>(scanZ);
		ActionableGrid3DZCrossSectionProcessor<IntGrid3D, IntGrid2D> xSection = 
				new ActionableGrid3DZCrossSectionProcessor<IntGrid3D, IntGrid2D>(crossSectionZ);
		
		IntGridMinAndMaxProcessor<IntGrid2D> scanMinAndMaxProcessor = new IntGridMinAndMaxProcessor<IntGrid2D>();
		IntGridMinAndMaxProcessor<IntGrid2D> xSectionMinAndMaxProcessor = new IntGridMinAndMaxProcessor<IntGrid2D>();
		
		scan.addProcessor(scanMinAndMaxProcessor);
		xSection.addProcessor(xSectionMinAndMaxProcessor);
		
		ca.addProcessor(scan);
		ca.addProcessor(xSection);
		//get min and max for current step
		ca.processGrid();
		
		do {
			scan.removeProcessor(scanMinAndMaxProcessor);
			xSection.removeProcessor(xSectionMinAndMaxProcessor);
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan z: " + scanZ);
			int[] scanMinAndMaxValue = scanMinAndMaxProcessor.getMinAndMaxValue();
			int[] xSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getMinAndMaxValue();
			System.out.println("Scan: min value: " + scanMinAndMaxValue[0] + ", max value: " + scanMinAndMaxValue[1]);
			System.out.println("Cross section: min value: " + xSectionMinAndMaxValue[0] + ", max value: " + xSectionMinAndMaxValue[1]);
			
			ActionableIntGrid2DColorMapperProcessor scanColorMapperProcessor = 
					new ActionableIntGrid2DColorMapperProcessor(scanningColorMapper, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
			ActionableIntGrid2DColorMapperProcessor crossSectionColorMapperProcessor = 
					new ActionableIntGrid2DColorMapperProcessor(crossSectionColorMapper, xSectionMinAndMaxValue[0], xSectionMinAndMaxValue[1]);
			
			ImageRenderingProcessor scanImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			ImageRenderingProcessor xSectionImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
							caName + "_x_section_" + currentStep + ".png");
			
			scanColorMapperProcessor.addProcessor(scanImageRenderer);
			crossSectionColorMapperProcessor.addProcessor(xSectionImageRenderer);
			
			scan.addProcessor(scanColorMapperProcessor);
			xSection.addProcessor(crossSectionColorMapperProcessor);
			
			//generate images
			ca.processGrid();
			
			scan.removeProcessor(scanColorMapperProcessor);
			xSection.removeProcessor(crossSectionColorMapperProcessor);
			
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImagesFromLongGrid3D(ActionableEvolvingGrid3D<LongGrid3D> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionImagesFromLongGrid3D(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionImagesFromLongGrid3D(ActionableEvolvingGrid3D<LongGrid3D> ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D> scan = 
				new ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D>(scanZ);
		ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D> xSection = 
				new ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D>(crossSectionZ);
		
		LongGridMinAndMaxProcessor<LongGrid2D> scanMinAndMaxProcessor = new LongGridMinAndMaxProcessor<LongGrid2D>();
		LongGridMinAndMaxProcessor<LongGrid2D> xSectionMinAndMaxProcessor = new LongGridMinAndMaxProcessor<LongGrid2D>();
		
		scan.addProcessor(scanMinAndMaxProcessor);
		xSection.addProcessor(xSectionMinAndMaxProcessor);
		
		ca.addProcessor(scan);
		ca.addProcessor(xSection);
		//get min and max for current step
		ca.processGrid();
		
		do {
			scan.removeProcessor(scanMinAndMaxProcessor);
			xSection.removeProcessor(xSectionMinAndMaxProcessor);
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan z: " + scanZ);
			long[] scanMinAndMaxValue = scanMinAndMaxProcessor.getMinAndMaxValue();
			long[] xSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getMinAndMaxValue();
			System.out.println("Scan: min value: " + scanMinAndMaxValue[0] + ", max value: " + scanMinAndMaxValue[1]);
			System.out.println("Cross section: min value: " + xSectionMinAndMaxValue[0] + ", max value: " + xSectionMinAndMaxValue[1]);
			
			ActionableLongGrid2DColorMapperProcessor scanColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(scanningColorMapper, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
			ActionableLongGrid2DColorMapperProcessor crossSectionColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(crossSectionColorMapper, xSectionMinAndMaxValue[0], xSectionMinAndMaxValue[1]);
			
			ImageRenderingProcessor scanImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			ImageRenderingProcessor xSectionImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
							caName + "_x_section_" + currentStep + ".png");
			
			scanColorMapperProcessor.addProcessor(scanImageRenderer);
			crossSectionColorMapperProcessor.addProcessor(xSectionImageRenderer);
			
			scan.addProcessor(scanColorMapperProcessor);
			xSection.addProcessor(crossSectionColorMapperProcessor);
			
			//generate images
			ca.processGrid();
			
			scan.removeProcessor(scanColorMapperProcessor);
			xSection.removeProcessor(crossSectionColorMapperProcessor);
			
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createXScanningAndZCrossSectionEvenOddImages(ActionableEvolvingGrid3D<IntGrid3D> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionEvenOddImages(ca, xScanInitialIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createXScanningAndZCrossSectionEvenOddImages(ActionableEvolvingGrid3D<IntGrid3D> ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/x_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		IntGrid3DZCrossSectionCopierProcessor zCopier = new IntGrid3DZCrossSectionCopierProcessor();
		IntGrid3DXCrossSectionCopierProcessor xCopier = new IntGrid3DXCrossSectionCopierProcessor();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan x: " + scanX);
			IntGrid2D scan = xCopier.getCopy(scanX);
			IntGrid2D crossSection = zCopier.getCopy(crossSectionZ);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
			int[] evenXSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(true);
			int[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(false);
			int[] oddXSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(false);
			
			if (evenScanMinAndMaxValue != null) {
				System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenScanColorGrid = scanningColorMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (evenXSectionMinAndMaxValue != null) {
				System.out.println("Cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> evenCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddXSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);
				createEvenOddImageLeftToRight(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(crossSectionZ);
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createXScanningAndZCrossSectionOddImages(ActionableEvolvingGrid3D<NumberGrid3D<T>> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int xScanInitialIndex = ca.getMaxX();
		createXScanningAndZCrossSectionOddImages(ca, xScanInitialIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public <T extends FieldElement<T> & Comparable<T>> void createXScanningAndZCrossSectionOddImages(ActionableEvolvingGrid3D<NumberGrid3D<T>> ca, int xScanInitialIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenCrossSectionZ = (currentStep + crossSectionZ)%2 != 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanX = xScanInitialIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/x_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		NumberGrid3DZCrossSectionCopierProcessor<T> zCopier = new NumberGrid3DZCrossSectionCopierProcessor<T>();
		NumberGrid3DXCrossSectionCopierProcessor<T> xCopier = new NumberGrid3DXCrossSectionCopierProcessor<T>();
		ca.addProcessor(zCopier);
		ca.addProcessor(xCopier);
		zCopier.requestCopy(crossSectionZ);
		xCopier.requestCopy(scanX);
		ca.processGrid();
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan x: " + scanX);
			boolean isEvenScanX = (currentStep + scanX)%2 != 0;
			NumberGrid2D<T> scan = xCopier.getCopy(scanX);
			NumberGrid2D<T> crossSection = zCopier.getCopy(crossSectionZ);
			
			MinAndMax<T> oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMax(isEvenScanX);
			MinAndMax<T> oddXSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenCrossSectionZ);
			
			if (oddScanMinAndMaxValue != null) {
				System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue.getMin() + ", max value: " + oddScanMinAndMaxValue.getMax());
				ObjectGrid2D<Color> oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
				createEvenOddImageLeftToRight(oddScanColorGrid, isEvenScanX, minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						scanImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			}
			if (oddXSectionMinAndMaxValue != null) {
				System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue.getMin() + ", max value: " + oddXSectionMinAndMaxValue.getMax());
				ObjectGrid2D<Color> oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(crossSection, oddXSectionMinAndMaxValue.getMin(), oddXSectionMinAndMaxValue.getMax());
				createEvenOddImageLeftToRight(oddCrossSectionColorGrid, isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
			} else {
				createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
						crossSectionImgPath + "odd/" + numberedFolder, caName + "_" + currentStep + ".png");
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanX--;
			if (scanX < ca.getMinX())
				scanX = ca.getMaxX();			
			currentStep++;
			isEvenCrossSectionZ = !isEvenCrossSectionZ;
			System.out.println();
			xCopier.requestCopy(scanX);
			zCopier.requestCopy(crossSectionZ);
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createZScanningAndCrossSectionEvenOddImages(ActionableEvolvingGrid3D<LongGrid3D> ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createZScanningAndCrossSectionEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createZScanningAndCrossSectionEvenOddImages(ActionableEvolvingGrid3D<LongGrid3D> ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		
		ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D> scan = 
				new ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D>(scanZ);
		ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D> xSection = 
				new ActionableGrid3DZCrossSectionProcessor<LongGrid3D, LongGrid2D>(crossSectionZ);
		
		LongGridEvenOddMinAndMaxProcessor<LongGrid2D> scanMinAndMaxProcessor = new LongGridEvenOddMinAndMaxProcessor<LongGrid2D>();
		LongGridEvenOddMinAndMaxProcessor<LongGrid2D> xSectionMinAndMaxProcessor = new LongGridEvenOddMinAndMaxProcessor<LongGrid2D>();
		
		scan.addProcessor(scanMinAndMaxProcessor);
		xSection.addProcessor(xSectionMinAndMaxProcessor);
		
		ca.addProcessor(scan);
		ca.addProcessor(xSection);
		//get min and max for current step
		ca.processGrid();
		
		do {
			scan.removeProcessor(scanMinAndMaxProcessor);
			xSection.removeProcessor(xSectionMinAndMaxProcessor);
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			System.out.println("Scan z: " + scanZ);
			long[] evenScanMinAndMaxValue = scanMinAndMaxProcessor.getEvenMinAndMaxValue();
			long[] evenXSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getEvenMinAndMaxValue();
			long[] oddScanMinAndMaxValue = scanMinAndMaxProcessor.getOddMinAndMaxValue();
			long[] oddXSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getOddMinAndMaxValue();
			System.out.println("Scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
			System.out.println("Scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
			System.out.println("Cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
			System.out.println("Cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);
			
			ActionableLongGrid2DColorMapperProcessor evenScanColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(scanningColorMapper, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
			ActionableLongGrid2DColorMapperProcessor oddScanColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(scanningColorMapper, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
			ActionableLongGrid2DColorMapperProcessor evenCrossSectionColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(crossSectionColorMapper, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
			ActionableLongGrid2DColorMapperProcessor oddCrossSectionColorMapperProcessor = 
					new ActionableLongGrid2DColorMapperProcessor(crossSectionColorMapper, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				if (crossSectionZ%2 == 0) {
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
					evenXSectionFolder = "even";
					oddXSectionFolder = "odd";
				} else {
					evenXSectionFolder = "odd";
					oddXSectionFolder = "even";
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
							scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor oddScanImageRenderer = 
					new EvenOddImageRenderingProcessor(false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor evenXSectionImageRenderer = 
					new EvenOddImageRenderingProcessor(true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor oddXSectionImageRenderer = 
					new EvenOddImageRenderingProcessor(false, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			
			evenScanColorMapperProcessor.addProcessor(evenScanImageRenderer);
			oddScanColorMapperProcessor.addProcessor(oddScanImageRenderer);
			evenCrossSectionColorMapperProcessor.addProcessor(evenXSectionImageRenderer);
			oddCrossSectionColorMapperProcessor.addProcessor(oddXSectionImageRenderer);
			
			scan.addProcessor(evenScanColorMapperProcessor);
			scan.addProcessor(oddScanColorMapperProcessor);
			xSection.addProcessor(evenCrossSectionColorMapperProcessor);
			xSection.addProcessor(oddCrossSectionColorMapperProcessor);
			
			//generate images
			ca.processGrid();
			
			scan.removeProcessor(evenScanColorMapperProcessor);
			scan.removeProcessor(oddScanColorMapperProcessor);
			xSection.removeProcessor(evenCrossSectionColorMapperProcessor);
			xSection.removeProcessor(oddCrossSectionColorMapperProcessor);
			
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
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("Backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("Backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImagesNoBackUp(EvolvingLongGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path) throws Exception {
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String caName = ca.getName();
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/z_scan/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/z=" + crossSectionZ + "/";
		int scanZ = ca.getMinZ();
		do {
			System.out.println("Step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("Max y: " + maxY + System.lineSeparator() + "Max x: " + maxX);
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D scan = ca.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = scan.getMinAndMax();
			System.out.println("Scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			ObjectGrid2D<Color> colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			scanZ++;
			LongGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMax();
			System.out.println("Cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
			colorGrid = crossSectionColorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
					caName + "_x_section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createXScanningImages(ActionableEvolvingGrid3D<IntGrid3D> ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getClass().getSimpleName() + "/scans/step=" + currentStep + "/x_scan/";
		System.out.println("Scanning grid at step " + currentStep + " along the x axis.");
		
		ca.addProcessor(new GridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX(), maxX = gridBlock.getMaxX();
				System.out.println("New block: [" + minX + "," + maxX + "]");
				for (int scanX = minX; scanX <= maxX; scanX++) {
					System.out.println("X: " + scanX);
					IntGrid2D xSection = gridBlock.crossSectionAtX(scanX);
					int[] minAndMaxValue = xSection.getMinAndMax();
					System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorGrid, minWidth, minHeight, imgsPath, caName + "_x_section_" + scanX + ".png");
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
	
	public void createXScanningEvenOddImages(ActionableEvolvingGrid3D<IntGrid3D> ca, ColorMapper colorMapper, 
			int imageWidth, int imageHeight, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getClass().getSimpleName() + "/scans/step=" 
				+ currentStep + "/x_scan/" + imageWidth + "x" + imageHeight + "/";
		System.out.println("Scanning grid at step " + currentStep + " along the x axis.");
		
		ca.addProcessor(new GridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX();
				int maxX = gridBlock.getMaxX();
				System.out.println("New block: min x: " + minX + System.lineSeparator() + "Max x: " + maxX);
				boolean isEven = (minX + currentStep)%2 == 0;
				for (int scanX = minX; scanX <= maxX; scanX++, isEven = !isEven) {
					System.out.println("X: " + scanX);
					IntGrid2D xSection = gridBlock.crossSectionAtX(scanX);
					SubareaGrid<IntGrid2D> xSectionSubareaGrid = new SubareaGrid<IntGrid2D>(xSection, imageWidth, imageHeight);
					int subareasGridMinX = xSectionSubareaGrid.getMinX();
					int subareasGridMaxX = xSectionSubareaGrid.getMaxX();
					for (int subareasX = subareasGridMinX; subareasX <= subareasGridMaxX; subareasX++) {
						int subareasGridLocalMinY = xSectionSubareaGrid.getMinY(subareasX);
						int subareasGridLocalMaxY = xSectionSubareaGrid.getMaxY(subareasX);
						for (int subareasY = subareasGridLocalMinY; subareasY <= subareasGridLocalMaxY; subareasY++) {
							System.out.println("Subarea: (" + subareasX + "," + subareasY + ")");
							int framedGridMinX = subareasX * imageWidth;
							int framedGridMaxX = framedGridMinX + imageWidth - 1;
							int framedGridMinY = subareasY * imageHeight;
							int framedGridMaxY = framedGridMinY + imageHeight - 1;
							IntGrid2D xSectionSubarea = xSectionSubareaGrid.getSubareaAtPosition(subareasX, subareasY);
							int[] evenMinAndMaxValue = xSectionSubarea.getEvenOddPositionsMinAndMax(isEven);
							if (evenMinAndMaxValue != null) {
								System.out.println("Even positions min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Even positions max value: " + evenMinAndMaxValue[1]);
								ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSectionSubarea, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
								createImageFromEvenOrOddPositions(isEven, colorGrid, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
										imageWidth, imageHeight, imgsPath + "even/" 
										+ subareasX + "," + subareasY, caName + "_x_section_" + scanX + ".png");
							} else {
								createEmptyImage(framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
										imageWidth, imageHeight, imgsPath + "even/" 
										+ subareasX + "," + subareasY, caName + "_x_section_" + scanX + ".png");
							}							
							int[] oddMinAndMaxValue = xSectionSubarea.getEvenOddPositionsMinAndMax(!isEven);
							if (oddMinAndMaxValue != null) {
								System.out.println("Odd positions min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Odd positions max value: " + oddMinAndMaxValue[1]);
								ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSectionSubarea, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
								createImageFromEvenOrOddPositions(!isEven, colorGrid, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
										imageWidth, imageHeight, imgsPath + "odd/" 
										+ subareasX + "," + subareasY, caName + "_x_section_" + scanX + ".png");
							} else {
								createEmptyImage(framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
										imageWidth, imageHeight, imgsPath + "odd/" 
										+ subareasX + "," + subareasY, caName + "_x_section_" + scanX + ".png");
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
	
	public static void createImageLeftToRight(ObjectGrid2D<Color> grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageLeftToRight(grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageLeftToRight(ObjectGrid2D<Color> gridRegion, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
		
		int canvasTopMargin = imageHeight - framedGridHeightInPixels;
		
		int regionMinX = gridRegion.getMinX();
		int regionMaxX = gridRegion.getMaxX();
		int framedRegionMinX, framedRegionMaxX;
		if (minX < regionMinX) {
			framedRegionMinX = regionMinX;
		} else {
			framedRegionMinX = minX;
		}
		if (maxX > regionMaxX) {
			framedRegionMaxX = regionMaxX;
		} else {
			framedRegionMaxX = maxX;
		}
		
		for (int x = framedRegionMinX, xx = x - minX; x <= framedRegionMaxX; x++, xx++) {
			
			int regionMaxY = gridRegion.getMaxY(x);
			int regionMinY = gridRegion.getMinY(x);
			int framedRegionMinYAtX, framedRegionMaxYAtX;
			if (maxY > regionMaxY) {
				framedRegionMaxYAtX = regionMaxY;
			} else {
				framedRegionMaxYAtX = maxY;
			}
			if (minY < regionMinY) {
				framedRegionMinYAtX = regionMinY;
			} else {
				framedRegionMinYAtX = minY;
			}
					
			for (int hBandIndex = 0; hBandIndex < gridPositionSize; hBandIndex++) {			
				for (int y = framedRegionMinYAtX, yy = y - minY; y <= framedRegionMaxYAtX; y++, yy++) {
					java.awt.Color c = gridRegion.getFromPosition(x, y);
					byte r = (byte) c.getRed(), g = (byte) c.getGreen(), b = (byte) c.getBlue();
					
					int framedGridSquentialIndex = (framedGridHeight - yy - 1) * framedGridWidth + xx;
					int dataIndex = (((framedGridSquentialIndex / framedGridWidth) * gridPositionSize * imageWidth)
							+ ((framedGridSquentialIndex % framedGridWidth) * gridPositionSize) 
							+ (hBandIndex * imageWidth)
							+ (canvasTopMargin * imageWidth)) * 3;
					
					for (int vBandIndex = 0; vBandIndex < gridPositionSize; vBandIndex++) {
						pixelData[dataIndex++] = r;
						pixelData[dataIndex++] = g;
						pixelData[dataIndex++] = b;
					}				
				}
			}
			
		}
		saveAsPngImage(pixelData, imageWidth, imageHeight, path, name);
	}
	
	public static void createEvenOddImageLeftToRight(ObjectGrid2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createEvenOddImageLeftToRight(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createEvenOddImageLeftToRight(ObjectGrid2D<Color> gridRegion, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
		
		
		int canvasTopMargin = imageHeight - framedGridHeightInPixels;
		
		
		int regionMinX = gridRegion.getMinX();
		int regionMaxX = gridRegion.getMaxX();
		int framedRegionMinX, framedRegionMaxX;
		if (minX < regionMinX) {
			framedRegionMinX = regionMinX;
		} else {
			framedRegionMinX = minX;
		}
		if (maxX > regionMaxX) {
			framedRegionMaxX = regionMaxX;
		} else {
			framedRegionMaxX = maxX;
		}
		
		for (int x = framedRegionMinX, xx = x - minX; x <= framedRegionMaxX; x++, xx++) {
			
			int regionMaxY = gridRegion.getMaxY(x);
			int regionMinY = gridRegion.getMinY(x);
			int framedRegionMinYAtX, framedRegionMaxYAtX;
			if (maxY > regionMaxY) {
				framedRegionMaxYAtX = regionMaxY;
			} else {
				framedRegionMaxYAtX = maxY;
			}
			if (minY < regionMinY) {
				framedRegionMinYAtX = regionMinY;
			} else {
				framedRegionMinYAtX = minY;
			}
			boolean isPositionEven = (framedRegionMinYAtX+x)%2 == 0;
			if (isEven != isPositionEven) { 
				framedRegionMinYAtX++;
			}
			for (int hBandIndex = 0; hBandIndex < gridPositionSize; hBandIndex++) {			
				for (int y = framedRegionMinYAtX, yy = y - minY; y <= framedRegionMaxYAtX; y+=2, yy+=2) {
					java.awt.Color c = gridRegion.getFromPosition(x, y);
					byte r = (byte) c.getRed(), g = (byte) c.getGreen(), b = (byte) c.getBlue();
					
					int framedGridSquentialIndex = (framedGridHeight - yy - 1) * framedGridWidth + xx;
					int dataIndex = (((framedGridSquentialIndex / framedGridWidth) * gridPositionSize * imageWidth)
							+ ((framedGridSquentialIndex % framedGridWidth) * gridPositionSize) 
							+ (hBandIndex * imageWidth)
							+ (canvasTopMargin * imageWidth)) * 3;
					
					for (int vBandIndex = 0; vBandIndex < gridPositionSize; vBandIndex++) {
						pixelData[dataIndex++] = r;
						pixelData[dataIndex++] = g;
						pixelData[dataIndex++] = b;
					}				
				}
			}
			
		}
		saveAsPngImage(pixelData, imageWidth, imageHeight, path, name);
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
	
	public void createZScanningEvenOddImages(ActionableEvolvingGrid3D<IntGrid3D> ca,
			ColorMapper colorMapper, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getClass().getSimpleName() + "/scans/step=" 
				+ currentStep + "/z_scan/";
		System.out.println("Scanning grid at step " + currentStep + " along the z axis.");
		
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
				boolean isEven = (minZ + currentStep)%2 == 0;
				for (int scanZ = minZ; scanZ <= maxZ; scanZ++, isEven = !isEven) {
					System.out.println("Z: " + scanZ);
					IntGrid2D xSection = gridBlock.crossSectionAtZ(scanZ);
					int[] evenMinAndMaxValue = xSection.getEvenOddPositionsMinAndMax(isEven);
					if (evenMinAndMaxValue != null) {
						System.out.println("Even positions min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "Even positions max value: " + evenMinAndMaxValue[1]);
						ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(xSection, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(isEven, colorGrid, minX, maxX, minY, maxY,
								imgsPath + "minX=" + minX + "_maxX=" + maxX + "/even/", caName + "_x_section_" + scanZ + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY,
								imgsPath + "minX=" + minX + "_maxX=" + maxX + "/even/", caName + "_x_section_" + scanZ + ".png");
					}					
					int[] oddMinAndMaxValue = xSection.getEvenOddPositionsMinAndMax(!isEven);
					ObjectGrid2D<Color> colorGrid;
					if (oddMinAndMaxValue != null) {
						System.out.println("Odd positions min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "Odd positions max value: " + oddMinAndMaxValue[1]);
						colorGrid = colorMapper.getMappedGrid(xSection, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(!isEven, colorGrid, minX, maxX, minY, maxY,
								imgsPath + "minX=" + minX + "_maxX=" + maxX + "/odd/", caName + "_x_section_" + scanZ + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY,
								imgsPath + "minX=" + minX + "_maxX=" + maxX + "/odd/", caName + "_x_section_" + scanZ + ".png");
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
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ObjectGrid2D<Color> grid, int minX, int maxX, int minY, int maxY, String path, String name) 
			throws Exception {
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		createImageFromEvenOrOddPositions(isEven, grid, minX, maxX, minY, maxY, 1, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ObjectGrid2D<Color> grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddPositions(isEven, grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ObjectGrid2D<Color> grid, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
