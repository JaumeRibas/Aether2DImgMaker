/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
import cellularautomata.automata.Aether3D;
import cellularautomata.automata.CellularAutomaton;
import cellularautomata.automata.IntCellularAutomaton2D;
import cellularautomata.automata.IntCellularAutomaton3D;
import cellularautomata.automata.LongCellularAutomaton2D;
import cellularautomata.automata.SymmetricIntActionableCellularAutomaton3D;
import cellularautomata.automata.SymmetricIntCellularAutomaton2D;
import cellularautomata.automata.SymmetricIntCellularAutomaton3D;
import cellularautomata.automata.SymmetricIntCellularAutomaton4D;
import cellularautomata.automata.SymmetricLongActionableCellularAutomaton3D;
import cellularautomata.automata.SymmetricLongCellularAutomaton2D;
import cellularautomata.automata.SymmetricLongCellularAutomaton3D;
import cellularautomata.automata.SymmetricLongCellularAutomaton4D;
import cellularautomata.automata.SymmetricShortCellularAutomaton4D;
import cellularautomata.automata.Utils;
import cellularautomata.grid.IntGridMinAndMaxProcessor;
import cellularautomata.grid.LongGridEvenOddMinAndMaxProcessor;
import cellularautomata.grid.SymmetricGridProcessor;
import cellularautomata.grid2d.Grid2D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;
import cellularautomata.grid2d.SubareaGrid;
import cellularautomata.grid3d.ActionableSymmetricIntGrid3DZCrossSectionCopy;
import cellularautomata.grid3d.ActionableSymmetricIntGrid3DZCrossSectionProcessor;
import cellularautomata.grid3d.ActionableSymmetricLongGrid3DZCrossSectionProcessor;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid3d.LongGrid3D;
import cellularautomata.grid3d.ShortGrid3D;
import cellularautomata.grid3d.SymmetricIntGrid3DZCrossSectionCopierProcessor;
import cellularautomata.grid4d.IntGrid4D;
import cellularautomata.grid4d.LongGrid4D;
import cellularautomata.grid4d.ShortGrid4D;
import cellularautomata.grid.IntGridEvenOddMinAndMaxProcessor;

public class CAImgMaker {
	
	private long imgsPerFolder = 10000;
	private long backupLeap = 1000 * 60 * 60 * 12;
//	private long backupLeap = 1000 * 30;//debug
	private volatile boolean backupRequested = false;
	
	
	public CAImgMaker() {}
	
	public CAImgMaker(long backupLeap) {
		this.backupLeap = backupLeap;
	}
	
	public void createNonsymmetricImages(SymmetricLongCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + colorMapper.getClass().getSimpleName() + "/slice/";
		LongGrid2D nonsymmetricSection = ca.nonsymmetricSection();
		do {
			System.out.println("Current step: " + currentStep);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			long[] minAndMaxValue = nonsymmetricSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(nonsymmetricSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createNonsymmetricImages(SymmetricIntCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + colorMapper.getClass().getSimpleName() + "/slice/";
		IntGrid2D nonsymmetricSection = ca.nonsymmetricSection();
		do {
			System.out.println("Current step: " + currentStep);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			int[] minAndMaxValue = nonsymmetricSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(nonsymmetricSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createNonsymmetricEvenOddImages(SymmetricLongCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + colorMapper.getClass().getSimpleName() + "/slice/";
		LongGrid2D nonsymmetricSection = ca.nonsymmetricSection();
		do {
			System.out.println("Current step: " + currentStep);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			long[] evenMinAndMaxValue = nonsymmetricSection.getEvenOddPositionsMinAndMaxValue(true);
			long[] oddMinAndMaxValue = nonsymmetricSection.getEvenOddPositionsMinAndMaxValue(false);
			System.out.println("Even positions: Min value " + evenMinAndMaxValue[0] + " Max value " + evenMinAndMaxValue[1]);
			System.out.println("Odd positions: Min value " + oddMinAndMaxValue[0] + " Max value " + oddMinAndMaxValue[1]);
			ColorGrid2D evenColorGrid = colorMapper.getMappedGrid(nonsymmetricSection, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
			ColorGrid2D oddColorGrid = colorMapper.getMappedGrid(nonsymmetricSection, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
			String evenFolder, oddFolder;
			if (isEvenStep) {
				evenFolder = "even-odd";
				oddFolder = "odd-even";
			} else {
				evenFolder = "odd-even";
				oddFolder = "even-odd";
			}
			createEvenOddImageLeftToRight(evenColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
					imgPath + evenFolder + "/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			createEvenOddImageLeftToRight(oddColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
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
	
	public void createCrossSectionNonsymmetricImages(SymmetricLongCellularAutomaton3D ca, int z, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/cross-section-slice/z=" + z + "/";
		LongGrid3D nonsymmetricSection = ca.nonsymmetricSection();
		do {
			System.out.println("Current step: " + currentStep);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			LongGrid2D xSection = nonsymmetricSection.crossSectionAtZ(z);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
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
	
	public void createValueScanCrossSectionNonsymmetricImages(SymmetricLongCellularAutomaton3D ca, int z, int valueRange, long scanSpeed, ColorMapper colorMapper, 
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
		LongGrid3D nonsymmetricSection = ca.nonsymmetricSection();
		do {
			System.out.println("Current step: " + currentStep);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			LongGrid2D xSection = nonsymmetricSection.crossSectionAtZ(z);
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
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, rangeMin, rangeMax);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_x-section_" + currentStep + ".png");
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
	
	public void createValueRangesCrossSectionNonsymmetricImages(SymmetricLongCellularAutomaton3D ca, int z, int valueRange, long rangeLeap, ColorMapper colorMapper, 
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
		LongGrid3D nonsymmetricSection = ca.nonsymmetricSection();
		do {
			System.out.println("Current step: " + currentStep);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			LongGrid2D xSection = ca.crossSectionAtZ(z);
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
				ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, rangeMin, rangeMax);
				createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
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
	
	public void createNonsymmetricSurfaceImages(SymmetricLongCellularAutomaton3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/surface/";
		LongGrid3D nonsymmetricSection = ca.nonsymmetricSection();
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = nonsymmetricSection.getMaxY(), maxX = nonsymmetricSection.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			LongGrid2D surface = nonsymmetricSection.projectedSurfaceMaxX();
			long[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedLongGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
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
	
	public void createImages(IntCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			int[] minAndMaxValue = ca.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedIntGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
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
	
	public void createScanningNonsymmetricImages(SymmetricLongCellularAutomaton3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		String imgPath = path + "/scan-slice/";
		LongGrid3D nonsymmetricSection = ca.nonsymmetricSection();
		int scanZ = nonsymmetricSection.getMinZ();
		do {
			System.out.println("Current step: " + currentStep);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			if (scanZ >= nonsymmetricSection.getMaxZ())
				scanZ = nonsymmetricSection.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D xSection = nonsymmetricSection.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
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

	public void createAether3DLastStepsCrossSectionImages(ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		int i = 0;
		String imgPath = path + new Aether3D(0).getName() 
				+ "/img/lats-steps/cross-section-slice/" + colorMapper.getClass().getSimpleName();
		while (true) {
			long initialValue = i;
			System.out.println("Current initial value: " + initialValue);
			SymmetricLongCellularAutomaton3D ae = new Aether3D(initialValue);
			while (ae.nextStep());
			long lastStep = ae.getStep() - 1;
			System.out.println("Last step: " + lastStep);
			LongGrid3D nonsymmetricSection = ae.nonsymmetricSection();
			LongGrid2D xSection = nonsymmetricSection.crossSectionAtZ(nonsymmetricSection.getMinZ());
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath, 
					ae.getName() + "_x-section_" + initialValue + ".png");
			i++;
		}
	}
	
	public void createScanningAndCrossSectionNonsymmetricImages(SymmetricIntCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String path) throws Exception {
				
		createScanningAndCrossSectionImages(ca, ca.nonsymmetricSection(), crossSectionZ, 
				crossSectionColorMapper, crossSectionColorMapper, minHeight, minHeight, path);	
	}
	
	public void createScanningAndCrossSectionNonsymmetricImages(SymmetricIntCellularAutomaton3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path) throws Exception {
		createScanningAndCrossSectionImages(ca, ca.nonsymmetricSection(), scanInitialZIndex, crossSectionZ, 
				crossSectionColorMapper, crossSectionColorMapper, minHeight, minHeight, path);
	}
	
	public void createScanningAndCrossSectionImages(IntCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String path) throws Exception {
		createScanningAndCrossSectionImages(ca, ca, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, path);	
	}
	
	public void createScanningAndCrossSectionImages(CellularAutomaton ca, IntGrid3D grid, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String path) throws Exception {
				
		int scanInitialZIndex = grid.getMinZ();
		createScanningAndCrossSectionImages(ca, grid, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, path);	
	}
	
	public void createScanningAndCrossSectionImages(CellularAutomaton ca, IntGrid3D grid, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = scanInitialZIndex;
		String caName = ca.getName();
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		do {
			System.out.println("Current step: " + currentStep);
			int minX = grid.getMinX(), maxX = grid.getMaxX(), 
					minY = grid.getMinY(), maxY = grid.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			if (scanZ >= grid.getMaxZ())
				scanZ = grid.getMinZ();
			System.out.println("Scan z: " + scanZ);
			IntGrid2D scan = grid.crossSectionAtZ(scanZ);
			int[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("Scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPath + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			scanZ++;
			IntGrid2D xSection = grid.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = crossSectionColorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
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
	
	public void createScanningAndCrossSectionNonsymmetricImages(SymmetricIntActionableCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.nonsymmetricSection().getMinZ();
		createScanningAndCrossSectionNonsymmetricImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionNonsymmetricImages(SymmetricIntActionableCellularAutomaton3D ca, int scanInitialZIndex, int crossSectionZ, 
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
		
		ActionableSymmetricIntGrid3DZCrossSectionProcessor scan = new ActionableSymmetricIntGrid3DZCrossSectionProcessor(ca, scanZ);
		ActionableSymmetricIntGrid3DZCrossSectionProcessor xSection = new ActionableSymmetricIntGrid3DZCrossSectionProcessor(ca, crossSectionZ);
		
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
			int minX = ca.getNonsymmetricMinX(), maxX = ca.getNonsymmetricMaxX(), 
					minY = ca.getNonsymmetricMinY(), maxY = ca.getNonsymmetricMaxY();
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
			if (scanZ >= ca.getNonsymmetricMaxZ())
				scanZ = ca.getNonsymmetricMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionNonsymmetricEvenOddImages(SymmetricIntActionableCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.nonsymmetricSection().getMinZ();
		createScanningAndCrossSectionNonsymmetricEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}

	public void createScanningAndCrossSectionNonsymmetricEvenOddImages(SymmetricIntActionableCellularAutomaton3D ca, int scanInitialZIndex, int crossSectionZ, 
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
		String copiesPath = imagesPath + "copies";
		String scanCopiesPath =  copiesPath + "/scan-slice/";
		String crossSectionCopiesPath = copiesPath + "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		String scanImgPath = imagesPath + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = imagesPath + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		
		SymmetricIntGrid3DZCrossSectionCopierProcessor copier = new SymmetricIntGrid3DZCrossSectionCopierProcessor();
		
		IntGridEvenOddMinAndMaxProcessor<IntGrid2D> scanMinAndMaxProcessor = new IntGridEvenOddMinAndMaxProcessor<IntGrid2D>();
		IntGridEvenOddMinAndMaxProcessor<IntGrid2D> xSectionMinAndMaxProcessor = new IntGridEvenOddMinAndMaxProcessor<IntGrid2D>();
		
		ca.addProcessor(copier);
		
		copier.requestCopy(scanZ);
		copier.requestCopy(crossSectionZ);

		//copy current step cross sections
		ca.processGrid();
		
		do {
			System.out.println("Current step: " + currentStep);
			int minX = ca.getNonsymmetricMinX(), maxX = ca.getNonsymmetricMaxX(), 
					minY = ca.getNonsymmetricMinY(), maxY = ca.getNonsymmetricMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			System.out.println("Scan z: " + scanZ);
			ActionableSymmetricIntGrid3DZCrossSectionCopy scan = copier.getCopy(scanZ);
			ActionableSymmetricIntGrid3DZCrossSectionCopy xSection = copier.getCopy(crossSectionZ);
			
			if (currentStep%10 == 0) {
				System.out.println("Saving cross sections copies at '" + copiesPath + "'");
				Utils.serializeToFile(scan, scanCopiesPath + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".ser");
				Utils.serializeToFile(xSection, crossSectionCopiesPath + "/" + numberedFolder, caName + "_x-section_" + currentStep + ".ser");
			}
			
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			
			//get min and max for cross sections
			scan.processGrid();
			xSection.processGrid();
			
			scan.removeProcessor(scanMinAndMaxProcessor);
			xSection.removeProcessor(xSectionMinAndMaxProcessor);
			
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
			scan.processGrid();
			xSection.processGrid();
			
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
			if (scanZ >= ca.getNonsymmetricMaxZ())
				scanZ = ca.getNonsymmetricMinZ();
			
			copier.requestCopy(scanZ);
			copier.requestCopy(crossSectionZ);
			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionNonsymmetricEvenOddImages(SymmetricLongActionableCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.nonsymmetricSection().getMinZ();
		createScanningAndCrossSectionNonsymmetricEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionNonsymmetricEvenOddImages(SymmetricLongActionableCellularAutomaton3D ca, int scanInitialZIndex, int crossSectionZ, 
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
		
		ActionableSymmetricLongGrid3DZCrossSectionProcessor scan = new ActionableSymmetricLongGrid3DZCrossSectionProcessor(ca, scanZ);
		ActionableSymmetricLongGrid3DZCrossSectionProcessor xSection = new ActionableSymmetricLongGrid3DZCrossSectionProcessor(ca, crossSectionZ);
		
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
			int minX = ca.getNonsymmetricMinX(), maxX = ca.getNonsymmetricMaxX(), 
					minY = ca.getNonsymmetricMinY(), maxY = ca.getNonsymmetricMaxY();
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
			if (scanZ >= ca.getNonsymmetricMaxZ())
				scanZ = ca.getNonsymmetricMinZ();
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
	
	
	public void createScanningAndCrossSectionNonsymmetricImagesNoBackUp(SymmetricLongCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path) throws Exception {
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String caName = ca.getName();
		String scanImgPath = path + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = path + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross-section-slice/" + "z=" + crossSectionZ + "/";
		LongGrid3D nonsymmetricSection = ca.nonsymmetricSection();
		int scanZ = nonsymmetricSection.getMinZ();
		do {
			System.out.println("Current step: " + currentStep);
			int minX = nonsymmetricSection.getMinX(), maxX = nonsymmetricSection.getMaxX(), 
					minY = nonsymmetricSection.getMinY(), maxY = nonsymmetricSection.getMaxY();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			if (scanZ >= nonsymmetricSection.getMaxZ())
				scanZ = nonsymmetricSection.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D scan = nonsymmetricSection.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("Scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, scanImgPath + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			scanZ++;
			LongGrid2D xSection = nonsymmetricSection.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = crossSectionColorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
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
	
	public void createScanningNonsymmetric3DEdgeImages(SymmetricLongCellularAutomaton4D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		LongGrid4D nonsymmetricSection = ca.nonsymmetricSection();
		LongGrid3D edge = nonsymmetricSection.projected3DEdgeMaxW();
		int scanZ = edge.getMinZ();
		String imgPath = path + "/scan-3DEdge/";
		do {
			edge = nonsymmetricSection.projected3DEdgeMaxW();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D xSection = edge.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedLongGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
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
	
	public void createScanningAndCrossSectionNonsymmetric3DEdgeImages(SymmetricLongCellularAutomaton4D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		LongGrid4D nonsymmetricSection = ca.nonsymmetricSection();
		LongGrid3D edge = nonsymmetricSection.projected3DEdgeMaxW();
		int scanZ = edge.getMinZ();
		do {
			edge = nonsymmetricSection.projected3DEdgeMaxW();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/";
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D scan = edge.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("3DEdge scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedLongGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			LongGrid2D xSection = edge.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedLongGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			LongGrid2D xSection2 = nonsymmetricSection.crossSectionAtYZ(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D symmColorGrid = colorMapper.getMappedGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = nonsymmetricSection.getMinX(); maxX = nonsymmetricSection.getMaxX();
			minY = nonsymmetricSection.getMinY(); maxY = nonsymmetricSection.getMaxY();
			createImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
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
	
	public void createScanningAndCrossSectionNonsymmetric3DEdgeImages(SymmetricIntCellularAutomaton4D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		IntGrid4D nonsymmetricSection = ca.nonsymmetricSection();
		IntGrid3D edge = nonsymmetricSection.projected3DEdgeMaxW();
		int scanZ = edge.getMinZ();
		do {
			edge = nonsymmetricSection.projected3DEdgeMaxW();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path;
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			IntGrid2D scan = edge.crossSectionAtZ(scanZ);
			int[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("3DEdge scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedIntGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			IntGrid2D xSection = edge.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedIntGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			IntGrid2D xSection2 = nonsymmetricSection.crossSectionAtYZ(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D symmColorGrid = colorMapper.getMappedGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = nonsymmetricSection.getMinX(); maxX = nonsymmetricSection.getMaxX();
			minY = nonsymmetricSection.getMinY(); maxY = nonsymmetricSection.getMaxY();
			createImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath
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
	
	public void createScanningAndCrossSectionNonsymmetric3DEdgeImages(SymmetricShortCellularAutomaton4D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		ShortGrid4D nonsymmetricSection = ca.nonsymmetricSection();
		ShortGrid3D edge = nonsymmetricSection.projected3DEdgeMaxW();
		int scanZ = edge.getMinZ();
		do {
			edge = nonsymmetricSection.projected3DEdgeMaxW();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path;
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			ShortGrid2D scan = edge.crossSectionAtZ(scanZ);
			short[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("3DEdge scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedShortGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			ShortGrid2D xSection = edge.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedShortGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			ShortGrid2D xSection2 = nonsymmetricSection.crossSectionAtYZ(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("Cross section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D symmColorGrid = colorMapper.getMappedGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = nonsymmetricSection.getMinX(); maxX = nonsymmetricSection.getMaxX();
			minY = nonsymmetricSection.getMinY(); maxY = nonsymmetricSection.getMaxY();
			createImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath
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
			IntGrid2D surface = ca.projected3DEdgeMaxW().projectedSurfaceMaxX();
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
	
	public void createNonsymmetric3DEdgeSurfaceImages(SymmetricShortCellularAutomaton4D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/surface-3DEgde/";
		ShortGrid4D nonsymmetricSection = ca.nonsymmetricSection();
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = nonsymmetricSection.getMaxY(), maxX = nonsymmetricSection.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			ShortGrid2D surface = nonsymmetricSection.projected3DEdgeMaxW().projectedSurfaceMaxX();
			short[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedShortGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
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
	
	public void createXScanningNonsymmetricImages(SymmetricIntActionableCellularAutomaton3D ca,	ColorMapper colorMapper, 
			int minWidth, int minHeight, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + colorMapper.getClass().getSimpleName() + "/scans/step=" + currentStep + "/x_scan/";
		System.out.println("Scanning grid at step " + currentStep + " along the x axis.");
		
		ca.addProcessor(new SymmetricGridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX(), maxX = gridBlock.getMaxX();
				System.out.println("New block: minX=" + minX + ", maxX=" + maxX);
				for (int scanX = minX; scanX <= maxX; scanX++) {
					System.out.println("Current x: " + scanX);
					IntGrid2D xSection = gridBlock.crossSectionAtX(scanX);
					int[] minAndMaxValue = xSection.getMinAndMaxValue();
					System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
					ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
					createImage(colorGrid, minWidth, minHeight, imgsPath, caName + "_x-section_" + scanX + ".png");
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
	
	public void createXScanningNonsymmetricEvenOddImages(SymmetricIntActionableCellularAutomaton3D ca, ColorMapper colorMapper, 
			int imageWidth, int imageHeight, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + colorMapper.getClass().getSimpleName() + "/scans/step=" 
				+ currentStep + "/x_scan/" + imageWidth + "x" + imageHeight + "/";
		System.out.println("Scanning grid at step " + currentStep + " along the x axis.");
		
		ca.addProcessor(new SymmetricGridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX();
				int maxX = gridBlock.getMaxX();
				System.out.println("New block: minX=" + minX + ", maxX=" + maxX);
				boolean isEven = (minX + currentStep)%2 == 0;
				for (int scanX = minX; scanX <= maxX; scanX++, isEven = !isEven) {
					System.out.println("Current x: " + scanX);
					IntGrid2D xSection = gridBlock.crossSectionAtX(scanX);
					SubareaGrid<IntGrid2D> xSectionSubareaGrid = new SubareaGrid<IntGrid2D>(xSection, imageWidth, imageHeight);
					int subareasGridMinX = xSectionSubareaGrid.getMinX();
					int subareasGridMaxX = xSectionSubareaGrid.getMaxX();
					for (int subareasX = subareasGridMinX; subareasX <= subareasGridMaxX; subareasX++) {
						int subareasGridLocalMinY = xSectionSubareaGrid.getMinY(subareasX);
						int subareasGridLocalMaxY = xSectionSubareaGrid.getMaxY(subareasX);
						for (int subareasY = subareasGridLocalMinY; subareasY <= subareasGridLocalMaxY; subareasY++) {
							System.out.println("Subarea " + subareasX + "," + subareasY);
							int framedGridMinX = subareasX * imageWidth;
							int framedGridMaxX = framedGridMinX + imageWidth - 1;
							int framedGridMinY = subareasY * imageHeight;
							int framedGridMaxY = framedGridMinY + imageHeight - 1;
							IntGrid2D xSectionSubarea = xSectionSubareaGrid.getSubareaAtPosition(subareasX, subareasY);
							int[] evenMinAndMaxValue = xSectionSubarea.getEvenOddPositionsMinAndMaxValue(isEven);
							System.out.println("Even positions min value " + evenMinAndMaxValue[0] + ", Even positions max value " + evenMinAndMaxValue[1]);
							ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSectionSubarea, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(isEven, colorGrid, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
									imageWidth, imageHeight, imgsPath + "even/" 
									+ subareasX + "," + subareasY, caName + "_x-section_" + scanX + ".png");
							int[] oddMinAndMaxValue = xSectionSubarea.getEvenOddPositionsMinAndMaxValue(!isEven);
							System.out.println("Odd positions min value " + oddMinAndMaxValue[0] + ", Odd positions max value " + oddMinAndMaxValue[1]);
							colorGrid = colorMapper.getMappedGrid(xSectionSubarea, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(!isEven, colorGrid, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
									imageWidth, imageHeight, imgsPath + "odd/" 
									+ subareasX + "," + subareasY, caName + "_x-section_" + scanX + ".png");
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
	
	public static void createImage(ColorGrid2D grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImage(grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImage(ColorGrid2D grid, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
					java.awt.Color c = grid.getColorAtPosition(x, y);
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
	
	public static void createImageLeftToRight(ColorGrid2D grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageLeftToRight(grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageLeftToRight(ColorGrid2D gridRegion, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
					java.awt.Color c = gridRegion.getColorAtPosition(x, y);
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
	
	public static void createEvenOddImageLeftToRight(ColorGrid2D grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createEvenOddImageLeftToRight(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createEvenOddImageLeftToRight(ColorGrid2D gridRegion, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
					java.awt.Color c = gridRegion.getColorAtPosition(x, y);
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
	
	public void createZScanningNonsymmetricEvenOddImages(SymmetricIntActionableCellularAutomaton3D ca,
			ColorMapper colorMapper, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + colorMapper.getClass().getSimpleName() + "/scans/step=" 
				+ currentStep + "/z_scan/";
		System.out.println("Scanning grid at step " + currentStep + " along the z axis.");
		
		ca.addProcessor(new SymmetricGridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX();
				int maxX = gridBlock.getMaxX();
				int minY = gridBlock.getMinY();
				int maxY = gridBlock.getMaxY();
				int minZ = gridBlock.getMinZ();
				int maxZ = gridBlock.getMaxZ();
				System.out.println("New block: minX=" + minX + ", maxX=" + maxX);
				boolean isEven = (minZ + currentStep)%2 == 0;
				for (int scanZ = minZ; scanZ <= maxZ; scanZ++, isEven = !isEven) {
					System.out.println("Current z: " + scanZ);
					IntGrid2D xSection = gridBlock.crossSectionAtZ(scanZ);
					int[] evenMinAndMaxValue = xSection.getEvenOddPositionsMinAndMaxValue(isEven);
					System.out.println("Even positions min value " + evenMinAndMaxValue[0] + ", Even positions max value " + evenMinAndMaxValue[1]);
					ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(isEven, colorGrid, minX, maxX, minY, maxY,
							imgsPath + "minX=" + minX + "_maxX=" + maxX + "/even/", caName + "_x-section_" + scanZ + ".png");
					int[] oddMinAndMaxValue = xSection.getEvenOddPositionsMinAndMaxValue(!isEven);
					System.out.println("Odd positions min value " + oddMinAndMaxValue[0] + ", Odd positions max value " + oddMinAndMaxValue[1]);
					colorGrid = colorMapper.getMappedGrid(xSection, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(!isEven, colorGrid, minX, maxX, minY, maxY,
							imgsPath + "minX=" + minX + "_maxX=" + maxX + "/odd/", caName + "_x-section_" + scanZ + ".png");				
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
	
	public static void createImage(ColorGrid2D grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(grid, minWidth, minHeight);
		createImage(grid, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImage(ColorGrid2D grid, int gridPositionSize, int minWidth, int minHeight, String path, String name) throws Exception {
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
					java.awt.Color c = grid.getColorAtPosition(x,y);
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
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ColorGrid2D grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(grid, minWidth, minHeight);
		createImageFromEvenOrOddPositions(isEven, grid, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ColorGrid2D grid, int gridPositionSize, int minWidth, int minHeight, String path, String name) throws Exception {
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
			if (isEven) { 
				if (!isPositionEven) {
					localMinX++;
				}
			} else {
				if (isPositionEven) {
					localMinX++;
				}
			}
			int localLeftMargin = (localMinX - minX)  * gridPositionSize;
			int localRightMargin = rightMargin + ((maxX - localMaxX) * gridPositionSize);
			for (int i = 0; i < gridPositionSize; i++) {
				dataIndex += 3 * localLeftMargin;
				int x = localMinX;
				for (; x < localMaxX; x+=2) {
					java.awt.Color c = grid.getColorAtPosition(x,y);
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
					dataIndex += 3 * gridPositionSize;
				}
				if (x == localMaxX) {
					java.awt.Color c = grid.getColorAtPosition(x,y);
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
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ColorGrid2D grid, int minX, int maxX, int minY, int maxY, String path, String name) 
			throws Exception {
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		createImageFromEvenOrOddPositions(isEven, grid, minX, maxX, minY, maxY, 1, width, height, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ColorGrid2D grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddPositions(isEven, grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	public static void createImageFromEvenOrOddPositions(boolean isEven, ColorGrid2D grid, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
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
			if (isEven) { 
				if (!isPositionEven) {
					framedGridMinXAtY++;
					gridLeftMargin += gridPositionSize;
				}
			} else {
				if (isPositionEven) {
					framedGridMinXAtY++;
					gridLeftMargin += gridPositionSize;
				}
			}
			for (int i = 0; i < gridPositionSize; i++) {
				dataIndex += gridLeftMargin * 3;
				int x = framedGridMinXAtY;
				for (; x < framedGridMaxXAtY; x+=2) {
					java.awt.Color c = grid.getColorAtPosition(x,y);
					for (int j = 0; j < gridPositionSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
					dataIndex += 3 * gridPositionSize;
				}
				if (x == framedGridMaxXAtY) {
					java.awt.Color c = grid.getColorAtPosition(x,y);
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
