/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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

import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import caimgmaker.colormap.ActionableGrid2DColorMapperProcessor;
import caimgmaker.colormap.ColorGrid2D;
import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.SymmetricColorGrid2D;
import cellularautomata.automata.Aether3D;
import cellularautomata.automata.LongCellularAutomaton2D;
import cellularautomata.automata.SymmetricIntActionableCellularAutomaton3D;
import cellularautomata.automata.SymmetricIntCellularAutomaton4D;
import cellularautomata.automata.SymmetricLongActionableCellularAutomaton3D;
import cellularautomata.automata.SymmetricLongCellularAutomaton2D;
import cellularautomata.automata.SymmetricLongCellularAutomaton3D;
import cellularautomata.automata.SymmetricLongCellularAutomaton4D;
import cellularautomata.automata.SymmetricShortCellularAutomaton4D;
import cellularautomata.grid.IntGridMinAndMaxProcessor;
import cellularautomata.grid.LongGridEvenOddMinAndMaxProcessor;
import cellularautomata.grid.IntGridEvenOddMinAndMaxProcessor;
import cellularautomata.grid2D.Grid2D;
import cellularautomata.grid2D.IntGrid2D;
import cellularautomata.grid3D.ActionableIntGrid3DCrossSectionProcessor;
import cellularautomata.grid3D.ActionableLongGrid3DCrossSectionProcessor;
import cellularautomata.grid3D.IntGrid3D;
import cellularautomata.grid2D.LongGrid2D;
import cellularautomata.grid3D.LongGrid3D;
import cellularautomata.grid2D.ShortGrid2D;
import cellularautomata.grid3D.ShortGrid3D;
import cellularautomata.grid2D.SymmetricIntGrid2D;
import cellularautomata.grid2D.SymmetricLongGrid2D;
import cellularautomata.grid2D.SymmetricShortGrid2D;

public class CAImgMaker {
	
	private long imgsPerFolder = 10000;
	private long backupLeap = 1000 * 60 * 60 * 12;
//	private long backupLeap = 1000 * 30;//debug
	private volatile boolean backupRequested = false;
	
	
	public CAImgMaker() {}
	
	public CAImgMaker(long backupLeap) {
		this.backupLeap = backupLeap;
	}
	
	public void createNonSymmetricImages(SymmetricLongCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + colorMapper.getClass().getSimpleName() + "/slice/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			long[] minAndMaxValue = ca.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createNonSymmetricEvenOddImages(SymmetricLongCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + colorMapper.getClass().getSimpleName() + "/slice/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			long[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMaxValue(true);
			long[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMaxValue(false);
			System.out.println("Even positions: Min value " + evenMinAndMaxValue[0] + " Max value " + evenMinAndMaxValue[1]);
			System.out.println("Odd positions: Min value " + oddMinAndMaxValue[0] + " Max value " + oddMinAndMaxValue[1]);
			SymmetricColorGrid2D evenColorGrid = colorMapper.getMappedSymmetricGrid(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
			SymmetricColorGrid2D oddColorGrid = colorMapper.getMappedSymmetricGrid(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
			String evenFolder, oddFolder;
			if (isEvenStep) {
				evenFolder = "even-odd";
				oddFolder = "odd-even";
			} else {
				evenFolder = "odd-even";
				oddFolder = "even-odd";
			}
			createNonSymmetricEvenOddImage(evenColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
					imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			createNonSymmetricEvenOddImage(oddColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
					imgPath + oddFolder + "/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}
			currentStep++;
			isEvenStep = !isEvenStep;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createCrossSectionNonSymmetricImages(SymmetricLongCellularAutomaton3D ca, int z, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/cross-section-slice/z=" + z + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			SymmetricLongGrid2D xSection = ca.crossSection(z);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					imgPath + numberedFolder, ca.getName() + "_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createValueScanCrossSectionNonSymmetricImages(SymmetricLongCellularAutomaton3D ca, int z, int valueRange, long scanSpeed, ColorMapper colorMapper, 
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
		String imgPath = path + "/cross-section-slice-value-scan/z=" + z + "/value-range=" + valueRange + "/scan-speed=" + scanSpeed + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			SymmetricLongGrid2D xSection = ca.crossSection(z);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			if (scanSpeed > 0 && (rangeMax > minAndMaxValue[1] || rangeMax < minAndMaxValue[0] || firstIteration)) {
				rangeMin = minAndMaxValue[0];
				rangeMax = rangeMin + valueRange - 1;
			} else if (scanSpeed < 0 && (rangeMin < minAndMaxValue[0] || rangeMin > minAndMaxValue[1] || firstIteration)) {
				rangeMax = minAndMaxValue[1];
				rangeMin = rangeMax - valueRange + 1;
			}
			System.out.println("Range min value " + rangeMin + " Range max value " + rangeMax);
			SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(xSection, rangeMin, rangeMax);
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			rangeMin += scanSpeed;
			rangeMax += scanSpeed;
			firstIteration = false;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createValueRangesCrossSectionNonSymmetricImages(SymmetricLongCellularAutomaton3D ca, int z, int valueRange, long rangeLeap, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {	
		if (valueRange < 1)
			throw new IllegalArgumentException("Value range must be bigger than 0.");
		if (rangeLeap <= 0)
			throw new IllegalArgumentException("Range leap must be bigger than zero.");
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long firstRangeMin = 0, rangeMin = 0;
		boolean firstIteration = true;
		String imgPath = path + "/cross-section-slice-value-ranges/z=" + z + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			SymmetricLongGrid2D xSection = ca.crossSection(z);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
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
				System.out.println("Range " + rangeId + " min value " + rangeMin + " Range max value " + rangeMax);
				SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(xSection, rangeMin, rangeMax);
				createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + "/" + rangeId + "(minVal=" + rangeMin + "_maxVal=" + rangeMax + ")/" 
						+ numberedFolder, ca.getName() + "_x-section_" + currentStep + ".png");
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
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createSurfaceImages(SymmetricLongCellularAutomaton3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/surface/";
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			LongGrid2D surface = ca.projectedSurface();
			long[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedLongGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createImages(SymmetricLongCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			long[] minAndMaxValue = ca.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedLongGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createImages(LongCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			long[] minAndMaxValue = ca.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedLongGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createScanningNonSymmetricImages(SymmetricLongCellularAutomaton3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = ca.getNonSymmetricMinZ();
		String imgPath = path + "/scan-slice/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			if (scanZ >= ca.getNonSymmetricMaxZ())
				scanZ = ca.getNonSymmetricMinZ();
			System.out.println("Scan z: " + scanZ);
			SymmetricLongGrid2D xSection = ca.crossSection(scanZ);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					imgPath + numberedFolder, ca.getName() + "_x-section_" + currentStep + ".png");
			scanZ++;
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				ca.backUp(path + "/backups", ca.getClass().getSimpleName() + "_" + currentStep);					
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
//	public void createAetherZeroLastStepsScanningSliceImages(ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
//		int i = 1;
//		while (true) {
//			long initialValue = i*6;
//			System.out.println("Current initial value: " + initialValue);
//			SymmetricLongCellularAutomaton3D az = new AetherZero3D(initialValue);
//			String imgPath = path + az.getSubFolderPath() + "/img/scan-slice/" 
//					+ colorMapper.getClass().getSimpleName();
//			while (az.nextStep());
//			long step = az.getCurrentStep();
//			System.out.println("Scanning last step: " + step);
//			int maxZ = az.getNonSymmetricMaxZ();
//			for (int scanZ = az.getNonSymmetricMinZ(); scanZ <= maxZ; scanZ++) {
//				SymmetricLongGrid2D xSection = az.crossSection(scanZ);
//				long[] minAndMaxValue = xSection.getMinAndMaxValue();
//				System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
//				SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
//				createSliceImage(colorGrid, minWidth, minHeight, imgPath, az.getName() + "_x-section_" + step + "_" + scanZ + ".png");
//			}
//			i *= 10;
//		}
//	}
//	
	public void createAetherLastStepsCrossSectionImages(ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		int i = 0;
		String imgPath = path + new Aether3D(0).getName() 
				+ "/img/lats-steps/cross-section-slice/" + colorMapper.getClass().getSimpleName();
		while (true) {
			long initialValue = i;
			System.out.println("Current initial value: " + initialValue);
			SymmetricLongCellularAutomaton3D ae = new Aether3D(initialValue);
			while (ae.nextStep());
			long step = ae.getStep();
			System.out.println("Last step: " + step);
			SymmetricLongGrid2D xSection = ae.crossSection(ae.getNonSymmetricMinZ());
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			int minX = ae.getNonSymmetricMinX(), maxX = ae.getNonSymmetricMaxX(), 
					minY = ae.getNonSymmetricMinY(), maxY = ae.getNonSymmetricMaxY();
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath, 
					ae.getName() + "_x-section_" + initialValue + ".png");
			i++;
		}
	}
	
	public void createScanningAndCrossSectionNonSymmetricImages(SymmetricLongCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = ca.getNonSymmetricMinZ();
		String caName = ca.getName();
		String scanImgPath = path + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = path + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
//			long[] minAndMaxValue = ca.getMinAndMaxValue();
//			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			if (scanZ >= ca.getNonSymmetricMaxZ())
				scanZ = ca.getNonSymmetricMinZ();
			System.out.println("Scan z: " + scanZ);
			SymmetricLongGrid2D scan = ca.crossSection(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("Scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = scanningColorMapper.getMappedSymmetricGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPath + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			scanZ++;
			SymmetricLongGrid2D xSection = ca.crossSection(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = crossSectionColorMapper.getMappedSymmetricGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
					caName + "_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				ca.backUp(path + "/backups", ca.getClass().getSimpleName() + "_" + currentStep);					
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionNonSymmetricImages(SymmetricIntActionableCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getNonSymmetricMinZ();
		createScanningAndCrossSectionNonSymmetricImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionNonSymmetricImages(SymmetricIntActionableCellularAutomaton3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = imagesPath + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		
		ActionableIntGrid3DCrossSectionProcessor scan = new ActionableIntGrid3DCrossSectionProcessor(ca, scanZ);
		ActionableIntGrid3DCrossSectionProcessor xSection = new ActionableIntGrid3DCrossSectionProcessor(ca, crossSectionZ);
		
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
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			System.out.println("Scan z: " + scanZ);
			int[] scanMinAndMaxValue = scanMinAndMaxProcessor.getMinAndMaxValue();
			int[] xSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getMinAndMaxValue();
			System.out.println("Scan: Min value " + scanMinAndMaxValue[0] + " Max value " + scanMinAndMaxValue[1]);
			System.out.println("Cross section: Min value " + xSectionMinAndMaxValue[0] + " Max value " + xSectionMinAndMaxValue[1]);
			
			ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D> scanColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D>(scan, scanningColorMapper, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D> crossSectionColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D>(xSection, crossSectionColorMapper, xSectionMinAndMaxValue[0], xSectionMinAndMaxValue[1]);
			
			ImageRenderingProcessor scanImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			ImageRenderingProcessor xSectionImageRenderer = 
					new ImageRenderingProcessor(minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
							caName + "_x-section_" + currentStep + ".png");
			
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
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
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
			if (scanZ >= ca.getNonSymmetricMaxZ())
				scanZ = ca.getNonSymmetricMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionNonSymmetricEvenOddImages(SymmetricIntActionableCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getNonSymmetricMinZ();
		createScanningAndCrossSectionNonSymmetricEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}

	public void createScanningAndCrossSectionNonSymmetricEvenOddImages(SymmetricIntActionableCellularAutomaton3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = imagesPath + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		
		ActionableIntGrid3DCrossSectionProcessor scan = new ActionableIntGrid3DCrossSectionProcessor(ca, scanZ);
		ActionableIntGrid3DCrossSectionProcessor xSection = new ActionableIntGrid3DCrossSectionProcessor(ca, crossSectionZ);
		
		IntGridEvenOddMinAndMaxProcessor<IntGrid2D> scanMinAndMaxProcessor = new IntGridEvenOddMinAndMaxProcessor<IntGrid2D>();
		IntGridEvenOddMinAndMaxProcessor<IntGrid2D> xSectionMinAndMaxProcessor = new IntGridEvenOddMinAndMaxProcessor<IntGrid2D>();
		
		scan.addProcessor(scanMinAndMaxProcessor);
		xSection.addProcessor(xSectionMinAndMaxProcessor);
		
		ca.addProcessor(scan);
		ca.addProcessor(xSection);
		//get min and max for current step
		ca.processGrid();
		
		do {
			scan.removeProcessor(scanMinAndMaxProcessor);
			xSection.removeProcessor(xSectionMinAndMaxProcessor);
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			System.out.println("Scan z: " + scanZ);
			int[] evenScanMinAndMaxValue = scanMinAndMaxProcessor.getEvenMinAndMaxValue();
			int[] evenXSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getEvenMinAndMaxValue();
			int[] oddScanMinAndMaxValue = scanMinAndMaxProcessor.getOddMinAndMaxValue();
			int[] oddXSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getOddMinAndMaxValue();
			System.out.println("Scan even positions: Min value " + evenScanMinAndMaxValue[0] + " Max value " + evenScanMinAndMaxValue[1]);
			System.out.println("Scan odd positions: Min value " + oddScanMinAndMaxValue[0] + " Max value " + oddScanMinAndMaxValue[1]);
			System.out.println("Cross section even positions: Min value " + evenXSectionMinAndMaxValue[0] + " Max value " + evenXSectionMinAndMaxValue[1]);
			System.out.println("Cross section odd positions: Min value " + oddXSectionMinAndMaxValue[0] + " Max value " + oddXSectionMinAndMaxValue[1]);
			
			ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D> evenScanColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D>(scan, scanningColorMapper, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D> oddScanColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D>(scan, scanningColorMapper, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D> evenCrossSectionColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D>(xSection, crossSectionColorMapper, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D> oddCrossSectionColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D>(xSection, crossSectionColorMapper, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				evenXSectionFolder = "even-odd";
				oddXSectionFolder = "odd-even";
				if (scanZ%2 == 0) {
					evenScanFolder = "even-odd";
					oddScanFolder = "odd-even";
				} else {
					evenScanFolder = "odd-even";
					oddScanFolder = "even-odd";
				}
			} else {
				evenXSectionFolder = "odd-even";
				oddXSectionFolder = "even-odd";
				if (scanZ%2 != 0) {
					evenScanFolder = "even-odd";
					oddScanFolder = "odd-even";
				} else {
					evenScanFolder = "odd-even";
					oddScanFolder = "even-odd";
				}
			}
			EvenOddImageRenderingProcessor evenScanImageRenderer = 
					new EvenOddImageRenderingProcessor(true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor oddScanImageRenderer = 
					new EvenOddImageRenderingProcessor(false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor evenXSectionImageRenderer = 
					new EvenOddImageRenderingProcessor(true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor oddXSectionImageRenderer = 
					new EvenOddImageRenderingProcessor(false, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			
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
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
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
			if (scanZ >= ca.getNonSymmetricMaxZ())
				scanZ = ca.getNonSymmetricMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
			isEvenStep = !isEvenStep;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionNonSymmetricEvenOddImages(SymmetricLongActionableCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getNonSymmetricMinZ();
		createScanningAndCrossSectionNonSymmetricEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionNonSymmetricEvenOddImages(SymmetricLongActionableCellularAutomaton3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
		
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = imagesPath + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		
		ActionableLongGrid3DCrossSectionProcessor scan = new ActionableLongGrid3DCrossSectionProcessor(ca, scanZ);
		ActionableLongGrid3DCrossSectionProcessor xSection = new ActionableLongGrid3DCrossSectionProcessor(ca, crossSectionZ);
		
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
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			System.out.println("Scan z: " + scanZ);
			long[] evenScanMinAndMaxValue = scanMinAndMaxProcessor.getEvenMinAndMaxValue();
			long[] evenXSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getEvenMinAndMaxValue();
			long[] oddScanMinAndMaxValue = scanMinAndMaxProcessor.getOddMinAndMaxValue();
			long[] oddXSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getOddMinAndMaxValue();
			System.out.println("Scan even positions: Min value " + evenScanMinAndMaxValue[0] + " Max value " + evenScanMinAndMaxValue[1]);
			System.out.println("Scan odd positions: Min value " + oddScanMinAndMaxValue[0] + " Max value " + oddScanMinAndMaxValue[1]);
			System.out.println("Cross section even positions: Min value " + evenXSectionMinAndMaxValue[0] + " Max value " + evenXSectionMinAndMaxValue[1]);
			System.out.println("Cross section odd positions: Min value " + oddXSectionMinAndMaxValue[0] + " Max value " + oddXSectionMinAndMaxValue[1]);
			
			ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D> evenScanColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D>(scan, scanningColorMapper, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D> oddScanColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D>(scan, scanningColorMapper, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D> evenCrossSectionColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D>(xSection, crossSectionColorMapper, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D> oddCrossSectionColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D>(xSection, crossSectionColorMapper, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				evenXSectionFolder = "even-odd";
				oddXSectionFolder = "odd-even";
				if (scanZ%2 == 0) {
					evenScanFolder = "even-odd";
					oddScanFolder = "odd-even";
				} else {
					evenScanFolder = "odd-even";
					oddScanFolder = "even-odd";
				}
			} else {
				evenXSectionFolder = "odd-even";
				oddXSectionFolder = "even-odd";
				if (scanZ%2 != 0) {
					evenScanFolder = "even-odd";
					oddScanFolder = "odd-even";
				} else {
					evenScanFolder = "odd-even";
					oddScanFolder = "even-odd";
				}
			}
			EvenOddImageRenderingProcessor evenScanImageRenderer = 
					new EvenOddImageRenderingProcessor(true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor oddScanImageRenderer = 
					new EvenOddImageRenderingProcessor(false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor evenXSectionImageRenderer = 
					new EvenOddImageRenderingProcessor(true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			EvenOddImageRenderingProcessor oddXSectionImageRenderer = 
					new EvenOddImageRenderingProcessor(false, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			
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
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
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
			if (scanZ >= ca.getNonSymmetricMaxZ())
				scanZ = ca.getNonSymmetricMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
			isEvenStep = !isEvenStep;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	
	public void createScanningAndCrossSectionNonSymmetricImagesNoBackUp(SymmetricLongCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path) throws Exception {
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		int scanZ = ca.getNonSymmetricMinZ();
		String caName = ca.getName();
		String scanImgPath = path + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = path + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonSymmetricMinX(), maxX = ca.getNonSymmetricMaxX(), 
					minY = ca.getNonSymmetricMinY(), maxY = ca.getNonSymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			if (scanZ >= ca.getNonSymmetricMaxZ())
				scanZ = ca.getNonSymmetricMinZ();
			System.out.println("Scan z: " + scanZ);
			SymmetricLongGrid2D scan = ca.crossSection(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("Scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = scanningColorMapper.getMappedSymmetricGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, scanImgPath + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			scanZ++;
			SymmetricLongGrid2D xSection = ca.crossSection(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = crossSectionColorMapper.getMappedSymmetricGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createNonSymmetricImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
					caName + "_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createScanning3DEdgeImages(SymmetricLongCellularAutomaton4D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		LongGrid3D edge = ca.projected3DEdge();
		int scanZ = edge.getMinZ();
		String imgPath = path + "/scan-3DEdge/";
		do {
			edge = ca.projected3DEdge();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D xSection = edge.crossSection(scanZ);
			long[] minAndMaxValue = xSection.getMinAndMaxValueExcluding(ca.getBackgroundValue());
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedLongGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				ca.backUp(path + "/backups", ca.getClass().getSimpleName() + "_" + currentStep);				
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSection3DEdgeImages(SymmetricLongCellularAutomaton4D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		LongGrid3D edge = ca.projected3DEdge();
		int scanZ = edge.getMinZ();
		do {
			edge = ca.projected3DEdge();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/";
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D scan = edge.crossSection(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValueExcluding(ca.getBackgroundValue());
			System.out.println("3DEdge scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedLongGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			LongGrid2D xSection = edge.crossSection(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedLongGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			SymmetricLongGrid2D xSection2 = ca.crossSection(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D symmColorGrid = colorMapper.getMappedSymmetricGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = ca.getNonSymmetricMinX(); maxX = ca.getNonSymmetricMaxX();
			minY = ca.getNonSymmetricMinY(); maxY = ca.getNonSymmetricMaxY();
			createNonSymmetricImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				ca.backUp(path + "/backups", ca.getClass().getSimpleName() + "_" + currentStep);				
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSection3DEdgeImages(SymmetricIntCellularAutomaton4D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		IntGrid3D edge = ca.projected3DEdge();
		int scanZ = edge.getMinZ();
		do {
			edge = ca.projected3DEdge();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path;
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			IntGrid2D scan = edge.crossSection(scanZ);
			int[] minAndMaxValue = scan.getMinAndMaxValueExcluding(ca.getBackgroundValue());
			System.out.println("3DEdge scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedIntGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			IntGrid2D xSection = edge.crossSection(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedIntGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			SymmetricIntGrid2D xSection2 = ca.crossSection(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D symmColorGrid = colorMapper.getMappedSymmetricGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = ca.getNonSymmetricMinX(); maxX = ca.getNonSymmetricMaxX();
			minY = ca.getNonSymmetricMinY(); maxY = ca.getNonSymmetricMaxY();
			createNonSymmetricImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath
					+ "/cross-section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				ca.backUp(path + "/backups", ca.getClass().getSimpleName() + "_" + currentStep);				
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSection3DEdgeImages(SymmetricShortCellularAutomaton4D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		ShortGrid3D edge = ca.projected3DEdge();
		int scanZ = edge.getMinZ();
		short[] historicMinAndMaxValue = new short[] {ca.getBackgroundValue(), 0};
		do {
			edge = ca.projected3DEdge();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path;
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			ShortGrid2D scan = edge.crossSection(scanZ);
			short[] minAndMaxValue = scan.getMinAndMaxValueExcluding(ca.getBackgroundValue());
			System.out.println("3DEdge scan: Min value " + historicMinAndMaxValue[0] + " Max value " + historicMinAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedShortGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			ShortGrid2D xSection = edge.crossSection(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedShortGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			SymmetricShortGrid2D xSection2 = ca.crossSection(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D symmColorGrid = colorMapper.getMappedSymmetricGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = ca.getNonSymmetricMinX(); maxX = ca.getNonSymmetricMaxX();
			minY = ca.getNonSymmetricMinY(); maxY = ca.getNonSymmetricMaxY();
			createNonSymmetricImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath
					+ "/cross-section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = System.currentTimeMillis() >= nextBckTime;
			if (backUp) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				ca.backUp(path + "/backups", ca.getClass().getSimpleName() + "_" + currentStep);				
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void create3DEdgeSurfaceImages(SymmetricIntCellularAutomaton4D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/surface-3DEgde/";
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			IntGrid2D surface = ca.projected3DEdge().projectedSurfaceMaxX(ca.getBackgroundValue());
			int[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedIntGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void create3DEdgeSurfaceImages(SymmetricShortCellularAutomaton4D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/surface-3DEgde/";
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			ShortGrid2D surface = ca.projected3DEdge().projectedSurfaceMaxX(ca.getBackgroundValue());
			short[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedShortGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
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
	
	private int getGridPositionSize(Grid2D grid, int preferredMaxWidth, int preferredMaxHeight) {
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
	
	public void createNonSymmetricImage(SymmetricColorGrid2D grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createNonSymmetricImageLeftToRight(grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public void createNonSymmetricImage(SymmetricColorGrid2D grid, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth); //TODO: make separate images when grid overflows
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
		int gridMaxY = grid.getNonSymmetricMaxY(), gridMinY = grid.getNonSymmetricMinY();
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
			int gridMinXAtY = grid.getNonSymmetricMinX(y);
			int gridMaxXAtY = grid.getNonSymmetricMaxX(y);
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
					java.awt.Color c = grid.getColorAtNonSymmetricPosition(x, y);
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
	
	public void createNonSymmetricEvenOddImage(SymmetricColorGrid2D grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createNonSymmetricEvenOddImageLeftToRight(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public void createNonSymmetricImageLeftToRight(SymmetricColorGrid2D gridRegion, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		
		int framedGridWidth = maxX - minX + 1;
		int framedGridHeight = maxY - minY + 1;
		
		int framedGridWidthInPixels = framedGridWidth * gridPositionSize;
		int framedGridHeightInPixels = framedGridHeight * gridPositionSize;
		
		int imageWidth = Math.max(framedGridWidthInPixels, minWidth); //TODO: make separate images when grid overflows
		int imageHeight = Math.max(framedGridHeightInPixels, minHeight);	
		
		
		long longByteCount = (long)imageWidth * imageHeight * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];	
		
		
		int canvasTopMargin = imageHeight - framedGridHeightInPixels;
//		int canvasRightMargin = imageWidth - framedGridWidthInPixels;
		
		
		int regionMinX = gridRegion.getNonSymmetricMinX();
		int regionMaxX = gridRegion.getNonSymmetricMaxX();
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
			
			int regionMaxY = gridRegion.getNonSymmetricMaxY(x);
			int regionMinY = gridRegion.getNonSymmetricMinY(x);
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
					java.awt.Color c = gridRegion.getColorAtNonSymmetricPosition(x, y);
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
	
	public void createNonSymmetricEvenOddImageLeftToRight(SymmetricColorGrid2D gridRegion, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		
		int framedGridWidth = maxX - minX + 1;
		int framedGridHeight = maxY - minY + 1;
		
		int framedGridWidthInPixels = framedGridWidth * gridPositionSize;
		int framedGridHeightInPixels = framedGridHeight * gridPositionSize;
		
		int imageWidth = Math.max(framedGridWidthInPixels, minWidth); //TODO: make separate images when grid overflows
		int imageHeight = Math.max(framedGridHeightInPixels, minHeight);	
		
		
		long longByteCount = (long)imageWidth * imageHeight * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];	
		
		
		int canvasTopMargin = imageHeight - framedGridHeightInPixels;
//		int canvasRightMargin = imageWidth - framedGridWidthInPixels;
		
		
		int regionMinX = gridRegion.getNonSymmetricMinX();
		int regionMaxX = gridRegion.getNonSymmetricMaxX();
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
			
			int regionMaxY = gridRegion.getNonSymmetricMaxY(x);
			int regionMinY = gridRegion.getNonSymmetricMinY(x);
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
			if (isEven) { 
				if (!isPositionEven) {
					framedRegionMinYAtX++;
				}
			} else {
				if (isPositionEven) {
					framedRegionMinYAtX++;
				}
			}
			for (int hBandIndex = 0; hBandIndex < gridPositionSize; hBandIndex++) {			
				for (int y = framedRegionMinYAtX, yy = y - minY; y <= framedRegionMaxYAtX; y+=2, yy+=2) {
					java.awt.Color c = gridRegion.getColorAtNonSymmetricPosition(x, y);
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

	public void createSliceImage(ColorGrid2D grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(grid, minWidth, minHeight);
		createSliceImage(grid, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public void createSliceImage(ColorGrid2D grid, int gridPositionSize, int minWidth, int minHeight, String path, String name) throws Exception {
		int maxX = grid.getMaxX(), minX = grid.getMinX(),
				maxY = grid.getMaxY(), minY = grid.getMinY();
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
		for (int y = maxY, yy = maxY - minY; y >= minY; y--, yy--) {
			for (int i = 0; i < gridPositionSize; i++) {
				for (int x = minX, xx = 0; x <= maxX; x++, xx++) {
					if (xx >= yy) {
						java.awt.Color c = grid.getColorAtPosition(x, y);
						for (int j = 0; j < gridPositionSize; j++) {
							pixelData[dataIndex++] = (byte) c.getRed();
							pixelData[dataIndex++] = (byte) c.getGreen();
							pixelData[dataIndex++] = (byte) c.getBlue();
						}
					} else
						dataIndex += 3 * gridPositionSize;					
				}
				dataIndex += 3 * rightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public void createImage(ColorGrid2D grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(grid, minWidth, minHeight);
		createImage(grid, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public void createImage(ColorGrid2D grid, int gridPositionSize, int minWidth, int minHeight, String path, String name) throws Exception {
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
			for (int i = 0; i < gridPositionSize; i++) {
				for (int x = minX; x <= maxX; x++) {
					java.awt.Color c = grid.getColorAtPosition(x,y);
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
				}
				dataIndex += 3 * rightMargin;
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
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		String pathName = path + "/" + name;
		System.out.println("Creating image at '" + pathName + "'");
		ImageIO.write(image, "png", new File(pathName));
	}
	
	private class StdInRunnable implements Runnable {
		private Scanner s;
		private volatile boolean stop = false;
		
		@Override
		public void run() {
			s = new Scanner(System.in);
			while (!stop) {
				String line = s.nextLine().trim().toLowerCase();
				if (line.equals("backup") || line.equals("back") || line.equals("save")) {
					backupRequested = true;
					System.out.println("Backup requested");
				}
			}
		}
		
		public void stop() {
			stop = true;
			s.close();
		}
		
	}
	
}
