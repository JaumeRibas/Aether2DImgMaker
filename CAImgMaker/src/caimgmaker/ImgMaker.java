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
import cellularautomata.automata.Utils;
import cellularautomata.evolvinggrid.EvolvingIntGrid2D;
import cellularautomata.evolvinggrid.EvolvingIntGrid3D;
import cellularautomata.evolvinggrid.EvolvingIntGrid4D;
import cellularautomata.evolvinggrid.EvolvingLongGrid2D;
import cellularautomata.evolvinggrid.EvolvingLongGrid3D;
import cellularautomata.evolvinggrid.EvolvingLongGrid4D;
import cellularautomata.evolvinggrid.EvolvingShortGrid2D;
import cellularautomata.evolvinggrid.EvolvingShortGrid3D;
import cellularautomata.evolvinggrid.EvolvingShortGrid4D;
import cellularautomata.evolvinggrid.SymmetricActionableEvolvingIntGrid3D;
import cellularautomata.evolvinggrid.SymmetricActionableEvolvingLongGrid3D;
import cellularautomata.grid.IntGridMinAndMaxProcessor;
import cellularautomata.grid.LongGridEvenOddMinAndMaxProcessor;
import cellularautomata.grid.LongGridMinAndMaxProcessor;
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
import cellularautomata.grid3d.IsotropicIntGrid3DZCrossSectionCopierProcessor;
import cellularautomata.grid.IntGridEvenOddMinAndMaxProcessor;

public class ImgMaker {
	
	private long imgsPerFolder = 10000;
	private long backupLeap;
	private boolean saveBackupsAutomatically = true;
	private volatile boolean backupRequested = false;
	
	public ImgMaker() {
		saveBackupsAutomatically = false;
	}
	
	public ImgMaker(long backupLeap) {
		this.backupLeap = backupLeap;
	}
	
	public void createImages(EvolvingLongGrid2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getClass().getSimpleName() + "/";
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			long[] minAndMaxValue = ca.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
	}
	
	public void createImages(EvolvingIntGrid2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getClass().getSimpleName() + "/";
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			int[] minAndMaxValue = ca.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
	}
	
	public void createEvenOddImages(EvolvingLongGrid2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		boolean isEvenStep = currentStep%2 == 0;
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/" + colorMapper.getClass().getSimpleName() + "/";
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			long[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMaxValue(true);
			long[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMaxValue(false);
			System.out.println("even positions: min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "max value: " + evenMinAndMaxValue[1]);
			System.out.println("odd positions: min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "max value: " + oddMinAndMaxValue[1]);
			ColorGrid2D evenColorGrid = colorMapper.getMappedGrid(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
			ColorGrid2D oddColorGrid = colorMapper.getMappedGrid(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
			String evenFolder, oddFolder;
			if (isEvenStep) {
				evenFolder = "even";
				oddFolder = "odd";
			} else {
				evenFolder = "odd";
				oddFolder = "even";
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
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
	}
	
	public void createCrossSectionImages(EvolvingLongGrid3D ca, int z, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/z=" + z + "/";
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			LongGrid2D xSection = ca.crossSectionAtZ(z);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
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
		System.out.println("finished!");
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
		String imgPath = path + "/cross_section_value_scan/z=" + z + "/value_range=" + valueRange + "/scan_speed=" + scanSpeed + "/";
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			LongGrid2D xSection = ca.crossSectionAtZ(z);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			if (scanSpeed > 0 && (rangeMax > minAndMaxValue[1] || rangeMax < minAndMaxValue[0] || firstIteration)) {
				rangeMin = minAndMaxValue[0];
				rangeMax = rangeMin + valueRange - 1;
			} else if (scanSpeed < 0 && (rangeMin < minAndMaxValue[0] || rangeMin > minAndMaxValue[1] || firstIteration)) {
				rangeMax = minAndMaxValue[1];
				rangeMin = rangeMax - valueRange + 1;
			}
			System.out.println("range min value: " + rangeMin + System.lineSeparator() + "range max value: " + rangeMax);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, rangeMin, rangeMax);
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
		System.out.println("finished!");
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
		String imgPath = path + "/cross_section_value_ranges/z=" + z + "/";
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			LongGrid2D xSection = ca.crossSectionAtZ(z);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
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
				System.out.println("range " + rangeId + System.lineSeparator() + "min value: " + rangeMin + System.lineSeparator() + "max value: " + rangeMax);
				ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, rangeMin, rangeMax);
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
		System.out.println("finished!");
	}
	
	public void createSurfaceMaxXImages(EvolvingLongGrid3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/surface/";
		do {
			System.out.println("step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			LongGrid2D surface = ca.projectedSurfaceMaxX();
			long[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
	}
	
	public void createScanningImages(EvolvingLongGrid3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		String imgPath = path + "/scan/";
		int scanZ = ca.getMinZ();
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			System.out.println("scan z: " + scanZ);
			LongGrid2D xSection = ca.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
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
					nextBckTime += backupLeap;
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
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}

	public void createAether3DLastStepsCrossSectionImages(ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		int i = 0;
		String imgPath = path + "/" + new Aether3D(0).getName() 
				+ "/img/lats_steps/cross_section_asymetric/" + colorMapper.getClass().getSimpleName();
		while (true) {
			long initialValue = i;
			System.out.println("initial value: " + initialValue);
			Aether3D ae = new Aether3D(initialValue);
			while (ae.nextStep());
			long lastStep = ae.getStep() - 1;
			System.out.println("last step: " + lastStep);
			LongGrid3D asymmetricSection = ae.asymmetricSection();
			LongGrid2D xSection = asymmetricSection.crossSectionAtZ(asymmetricSection.getMinZ());
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			int minX = asymmetricSection.getMinX(), maxX = asymmetricSection.getMaxX(), 
					minY = asymmetricSection.getMinY(), maxY = asymmetricSection.getMaxY();
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath, 
					ae.getName() + "_x_section_" + initialValue + ".png");
			i++;
		}
	}
	
	public void createScanningAndCrossSectionImages(EvolvingIntGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String path) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, path);	
	}
	
	public void createScanningAndCrossSectionImages(EvolvingIntGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
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
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section/" + "z=" + crossSectionZ + "/";
		boolean caFinished = false;
		boolean lastPassFinished = false;
		boolean lastPassStarted = false;
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
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
			System.out.println("scan z: " + scanZ);
			IntGrid2D scan = ca.crossSectionAtZ(scanZ);
			int[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			scanZ++;
			if (!caFinished) {
				IntGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = xSection.getMinAndMaxValue();
				System.out.println("cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
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
					nextBckTime += backupLeap;
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
			if (!caFinished) {
				caFinished = !ca.nextStep();
			}
			System.out.println();
		} while (!caFinished || !lastPassFinished);
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingIntGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
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
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = scanInitialZIndex;
		
		String caName = ca.getName();
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section/" + "z=" + crossSectionZ + "/";
		
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			System.out.println("scan z: " + scanZ);
			EvolvingIntGrid2D scan = ca.crossSectionAtZ(scanZ);
			EvolvingIntGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
			
			int[] evenScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMaxValue(true);
			int[] evenXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMaxValue(true);
			int[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMaxValue(false);
			int[] oddXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMaxValue(false);
			System.out.println("scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
			System.out.println("scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
			System.out.println("cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
			System.out.println("cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);
			
			ColorGrid2D evenScanColorGrid = scanningColorMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
			ColorGrid2D oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
			ColorGrid2D evenCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(xSection, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
			ColorGrid2D oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(xSection, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				evenXSectionFolder = "even";
				oddXSectionFolder = "odd";
				if (scanZ%2 == 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			} else {
				evenXSectionFolder = "odd";
				oddXSectionFolder = "even";
				if (scanZ%2 != 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			}
			createEvenOddImageLeftToRight(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			createEvenOddImageLeftToRight(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			createEvenOddImageLeftToRight(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			createEvenOddImageLeftToRight(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				backUp = System.currentTimeMillis() >= nextBckTime;
				if (backUp) {
					nextBckTime += backupLeap;
				}
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingShortGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionEvenOddImages(EvolvingShortGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, String backupPath) throws Exception {
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
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section/" + "z=" + crossSectionZ + "/";
		
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			System.out.println("scan z: " + scanZ);
			EvolvingShortGrid2D scan = ca.crossSectionAtZ(scanZ);
			EvolvingShortGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
			
			short[] evenScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMaxValue(true);
			short[] evenXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMaxValue(true);
			short[] oddScanMinAndMaxValue = scan.getEvenOddPositionsMinAndMaxValue(false);
			short[] oddXSectionMinAndMaxValue = xSection.getEvenOddPositionsMinAndMaxValue(false);
			System.out.println("scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
			System.out.println("scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
			System.out.println("cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
			System.out.println("cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);
			
			ColorGrid2D evenScanColorGrid = scanningColorMapper.getMappedGrid(scan, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
			ColorGrid2D oddScanColorGrid = scanningColorMapper.getMappedGrid(scan, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
			ColorGrid2D evenCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(xSection, evenXSectionMinAndMaxValue[0], evenXSectionMinAndMaxValue[1]);
			ColorGrid2D oddCrossSectionColorGrid = crossSectionColorMapper.getMappedGrid(xSection, oddXSectionMinAndMaxValue[0], oddXSectionMinAndMaxValue[1]);
			
			String evenXSectionFolder, oddXSectionFolder, evenScanFolder, oddScanFolder;
			if (isEvenStep) {
				evenXSectionFolder = "even";
				oddXSectionFolder = "odd";
				if (scanZ%2 == 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			} else {
				evenXSectionFolder = "odd";
				oddXSectionFolder = "even";
				if (scanZ%2 != 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			}
			createEvenOddImageLeftToRight(evenScanColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + evenScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			createEvenOddImageLeftToRight(oddScanColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							scanImgPath + oddScanFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			createEvenOddImageLeftToRight(evenCrossSectionColorGrid, true, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + evenXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			createEvenOddImageLeftToRight(oddCrossSectionColorGrid, false, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + oddXSectionFolder + "/" + numberedFolder, caName + "_" + currentStep + ".png");
			
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				backUp = System.currentTimeMillis() >= nextBckTime;
				if (backUp) {
					nextBckTime += backupLeap;
				}
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
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
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = scanInitialZIndex;
		String caName = ca.getName();
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section/" + "z=" + crossSectionZ + "/";
		boolean caFinished = false;
		boolean lastPassFinished = false;
		boolean lastPassStarted = false;
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
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
			System.out.println("scan z: " + scanZ);
			LongGrid2D scan = ca.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			scanZ++;
			if (!caFinished) {
				LongGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = xSection.getMinAndMaxValue();
				System.out.println("cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
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
					nextBckTime += backupLeap;
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
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImages(EvolvingShortGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String path) throws Exception {
				
		int scanInitialZIndex = ca.getMinZ();
		createScanningAndCrossSectionImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, path);	
	}
	
	public void createScanningAndCrossSectionImages(EvolvingShortGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
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
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section/" + "z=" + crossSectionZ + "/";
		boolean caFinished = false;
		boolean lastPassFinished = false;
		boolean lastPassStarted = false;
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
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
			System.out.println("scan z: " + scanZ);
			ShortGrid2D scan = ca.crossSectionAtZ(scanZ);
			short[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			scanZ++;
			if (!caFinished) {
				ShortGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = xSection.getMinAndMaxValue();
				System.out.println("cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
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
					nextBckTime += backupLeap;
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
			if (!caFinished) {
				caFinished = !ca.nextStep();
			}
			System.out.println();
		} while (!caFinished || !lastPassFinished);
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	/*//TODO use general inclined plane
	public void createBisectingPlanesImages(IntCellularAutomaton3D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		String caName = ca.getName();
		String bisectingPlaneImgPath = path + "/" + colorMapper.getClass().getSimpleName() + "/bisecting_plane/";
		IntGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			IntGrid2D scan = ca.crossSectionAtZ(scanZ);
			int[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, 
					bisectingPlaneImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
					caName + "_x_section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				backUp = System.currentTimeMillis() >= nextBckTime;
				if (backUp) {
					nextBckTime += backupLeap;
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
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	*/
	
	public void createScanningAndCrossSectionImagesFromAsymmetricSection(SymmetricActionableEvolvingIntGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.asymmetricSection().getMinZ();
		createScanningAndCrossSectionImagesFromAsymmetricSection(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionImagesFromAsymmetricSection(SymmetricActionableEvolvingIntGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
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
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan_slice/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section_slice/" + "z=" + crossSectionZ + "/";
		
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
			System.out.println("step: " + currentStep);
			int minX = ca.getAsymmetricMinX(), maxX = ca.getAsymmetricMaxX(), 
					minY = ca.getAsymmetricMinY(), maxY = ca.getAsymmetricMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			System.out.println("scan z: " + scanZ);
			int[] scanMinAndMaxValue = scanMinAndMaxProcessor.getMinAndMaxValue();
			int[] xSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getMinAndMaxValue();
			System.out.println("scan: min value: " + scanMinAndMaxValue[0] + ", max value: " + scanMinAndMaxValue[1]);
			System.out.println("cross section: min value: " + xSectionMinAndMaxValue[0] + ", max value: " + xSectionMinAndMaxValue[1]);
			
			ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D> scanColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D>(scan, scanningColorMapper, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D> crossSectionColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<IntGrid2D, ColorGrid2D>(xSection, crossSectionColorMapper, xSectionMinAndMaxValue[0], xSectionMinAndMaxValue[1]);
			
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
					nextBckTime += backupLeap;
				}
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getAsymmetricMaxZ())
				scanZ = ca.getAsymmetricMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionImagesFromAsymmetricSection(SymmetricActionableEvolvingLongGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.asymmetricSection().getMinZ();
		createScanningAndCrossSectionImagesFromAsymmetricSection(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionImagesFromAsymmetricSection(SymmetricActionableEvolvingLongGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
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
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan_slice/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section_slice/" + "z=" + crossSectionZ + "/";
		
		ActionableSymmetricLongGrid3DZCrossSectionProcessor scan = new ActionableSymmetricLongGrid3DZCrossSectionProcessor(ca, scanZ);
		ActionableSymmetricLongGrid3DZCrossSectionProcessor xSection = new ActionableSymmetricLongGrid3DZCrossSectionProcessor(ca, crossSectionZ);
		
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
			System.out.println("step: " + currentStep);
			int minX = ca.getAsymmetricMinX(), maxX = ca.getAsymmetricMaxX(), 
					minY = ca.getAsymmetricMinY(), maxY = ca.getAsymmetricMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			System.out.println("scan z: " + scanZ);
			long[] scanMinAndMaxValue = scanMinAndMaxProcessor.getMinAndMaxValue();
			long[] xSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getMinAndMaxValue();
			System.out.println("scan: min value: " + scanMinAndMaxValue[0] + ", max value: " + scanMinAndMaxValue[1]);
			System.out.println("cross section: min value: " + xSectionMinAndMaxValue[0] + ", max value: " + xSectionMinAndMaxValue[1]);
			
			ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D> scanColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D>(scan, scanningColorMapper, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
			ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D> crossSectionColorMapperProcessor = 
					new ActionableGrid2DColorMapperProcessor<LongGrid2D, ColorGrid2D>(xSection, crossSectionColorMapper, xSectionMinAndMaxValue[0], xSectionMinAndMaxValue[1]);
			
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
					nextBckTime += backupLeap;
				}
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getAsymmetricMaxZ())
				scanZ = ca.getAsymmetricMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionAsymmetricEvenOddImages(SymmetricActionableEvolvingIntGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.asymmetricSection().getMinZ();
		createScanningAndCrossSectionAsymmetricEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}

	public void createScanningAndCrossSectionAsymmetricEvenOddImages(SymmetricActionableEvolvingIntGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String imagesPath, 
			String backupPath) throws Exception {
		
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
		String scanCopiesPath =  copiesPath + "/scan_slice/";
		String crossSectionCopiesPath = copiesPath + "/cross_section_slice/" + "z=" + crossSectionZ + "/";
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan_slice/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section_slice/" + "z=" + crossSectionZ + "/";
		
		IsotropicIntGrid3DZCrossSectionCopierProcessor copier = new IsotropicIntGrid3DZCrossSectionCopierProcessor();
		
		IntGridEvenOddMinAndMaxProcessor<IntGrid2D> scanMinAndMaxProcessor = new IntGridEvenOddMinAndMaxProcessor<IntGrid2D>();
		IntGridEvenOddMinAndMaxProcessor<IntGrid2D> xSectionMinAndMaxProcessor = new IntGridEvenOddMinAndMaxProcessor<IntGrid2D>();
		
		ca.addProcessor(copier);
		
		copier.requestCopy(scanZ);
		copier.requestCopy(crossSectionZ);

		//copy current step cross sections
		ca.processGrid();
		
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getAsymmetricMinX(), maxX = ca.getAsymmetricMaxX(), 
					minY = ca.getAsymmetricMinY(), maxY = ca.getAsymmetricMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			System.out.println("scan z: " + scanZ);
			ActionableSymmetricIntGrid3DZCrossSectionCopy scan = copier.getCopy(scanZ);
			ActionableSymmetricIntGrid3DZCrossSectionCopy xSection = copier.getCopy(crossSectionZ);
			
			if (currentStep%10 == 0) {
				System.out.println("saving cross sections copies at '" + copiesPath + "'");
				Utils.serializeToFile(scan, scanCopiesPath + "/" + numberedFolder, caName + "_x_section_" + currentStep + ".ser");
				Utils.serializeToFile(xSection, crossSectionCopiesPath + "/" + numberedFolder, caName + "_x_section_" + currentStep + ".ser");
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
			System.out.println("scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
			System.out.println("scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
			System.out.println("cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
			System.out.println("cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);
			
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
				evenXSectionFolder = "even";
				oddXSectionFolder = "odd";
				if (scanZ%2 == 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			} else {
				evenXSectionFolder = "odd";
				oddXSectionFolder = "even";
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
			scan.processGrid();
			xSection.processGrid();
			
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				backUp = System.currentTimeMillis() >= nextBckTime;
				if (backUp) {
					nextBckTime += backupLeap;
				}
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getAsymmetricMaxZ())
				scanZ = ca.getAsymmetricMinZ();
			
			copier.requestCopy(scanZ);
			copier.requestCopy(crossSectionZ);
			
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionAsymmetricEvenOddImages(SymmetricActionableEvolvingLongGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, 
			String imagesPath, String backupPath) throws Exception {
				
		int scanInitialZIndex = ca.asymmetricSection().getMinZ();
		createScanningAndCrossSectionAsymmetricEvenOddImages(ca, scanInitialZIndex, crossSectionZ, scanningColorMapper, 
			crossSectionColorMapper, minWidth, minHeight, imagesPath, backupPath);	
	}
	
	public void createScanningAndCrossSectionAsymmetricEvenOddImages(SymmetricActionableEvolvingLongGrid3D ca, int scanInitialZIndex, int crossSectionZ, 
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
		String scanImgPath = imagesPath + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan_slice/";
		String crossSectionImgPath = imagesPath + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section_slice/" + "z=" + crossSectionZ + "/";
		
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
			System.out.println("step: " + currentStep);
			int minX = ca.getAsymmetricMinX(), maxX = ca.getAsymmetricMaxX(), 
					minY = ca.getAsymmetricMinY(), maxY = ca.getAsymmetricMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			System.out.println("scan z: " + scanZ);
			long[] evenScanMinAndMaxValue = scanMinAndMaxProcessor.getEvenMinAndMaxValue();
			long[] evenXSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getEvenMinAndMaxValue();
			long[] oddScanMinAndMaxValue = scanMinAndMaxProcessor.getOddMinAndMaxValue();
			long[] oddXSectionMinAndMaxValue = xSectionMinAndMaxProcessor.getOddMinAndMaxValue();
			System.out.println("scan even positions: min value: " + evenScanMinAndMaxValue[0] + ", max value: " + evenScanMinAndMaxValue[1]);
			System.out.println("scan odd positions: min value: " + oddScanMinAndMaxValue[0] + ", max value: " + oddScanMinAndMaxValue[1]);
			System.out.println("cross section even positions: min value: " + evenXSectionMinAndMaxValue[0] + ", max value: " + evenXSectionMinAndMaxValue[1]);
			System.out.println("cross section odd positions: min value: " + oddXSectionMinAndMaxValue[0] + ", max value: " + oddXSectionMinAndMaxValue[1]);
			
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
				evenXSectionFolder = "even";
				oddXSectionFolder = "odd";
				if (scanZ%2 == 0) {
					evenScanFolder = "even";
					oddScanFolder = "odd";
				} else {
					evenScanFolder = "odd";
					oddScanFolder = "even";
				}
			} else {
				evenXSectionFolder = "odd";
				oddXSectionFolder = "even";
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
					nextBckTime += backupLeap;
				}
			}
			if (backupRequested) {
				backUp = true;
				backupRequested = false;
			}
			if (backUp) {
				String backupName = ca.getClass().getSimpleName() + "_" + currentStep;
				System.out.println("backing up instance at '" + backupPath + "/" + backupName + "'");
				ca.backUp(backupPath, backupName);		
				System.out.println("backing up finished");
			}
			scanZ++;
			if (scanZ >= ca.getAsymmetricMaxZ())
				scanZ = ca.getAsymmetricMinZ();
			scan.setZ(scanZ);
			scan.addProcessor(scanMinAndMaxProcessor);
			xSection.addProcessor(xSectionMinAndMaxProcessor);
			currentStep++;
			isEvenStep = !isEvenStep;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	
	public void createScanningAndCrossSectionImagesNoBackUp(EvolvingLongGrid3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path) throws Exception {
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String caName = ca.getName();
		String scanImgPath = path + "/" + scanningColorMapper.getClass().getSimpleName() + "/scan/";
		String crossSectionImgPath = path + "/" + crossSectionColorMapper.getClass().getSimpleName() 
				+ "/cross_section/" + "z=" + crossSectionZ + "/";
		int scanZ = ca.getMinZ();
		do {
			System.out.println("step: " + currentStep);
			int minX = ca.getMinX(), maxX = ca.getMaxX(), 
					minY = ca.getMinY(), maxY = ca.getMaxY();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			if (scanZ >= ca.getMaxZ())
				scanZ = ca.getMinZ();
			System.out.println("scan z: " + scanZ);
			LongGrid2D scan = ca.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("scan: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = scanningColorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minX, maxX, minY, maxY, minWidth, minHeight, scanImgPath + numberedFolder, caName + "_x_section_" + currentStep + ".png");
			scanZ++;
			LongGrid2D xSection = ca.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
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
		System.out.println("finished!");
	}
	
	public void createScanning3DEdgeMaxWImages(EvolvingLongGrid4D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String backupPath) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		LongGrid3D edge = ca.projected3DEdgeMaxW();
		int scanZ = edge.getMinZ();
		String imgPath = path + "/scan_3D_edge/";
		do {
			edge = ca.projected3DEdgeMaxW();
			System.out.println("step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("scan z: " + scanZ);
			LongGrid2D xSection = edge.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");			
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
					nextBckTime += backupLeap;
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
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSection3DEdgeMaxWImages(EvolvingLongGrid4D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		LongGrid3D edge = ca.projected3DEdgeMaxW();
		int scanZ = edge.getMinZ();
		do {
			edge = ca.projected3DEdgeMaxW();
			System.out.println("step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			String imgPath = path + "/";
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("scan z: " + scanZ);
			LongGrid2D scan = edge.crossSectionAtZ(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("3D edge scan: min value: " + minAndMaxValue[0] + ", max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/scan_3D_edge/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");			
			scanZ++;
			LongGrid2D xSection = edge.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3D edge cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/3D_edge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");
			LongGrid2D xSection2 = ca.crossSectionAtYZ(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("cross section: min value " + minAndMaxValue[0] + ", max value: " + minAndMaxValue[1]);
			ColorGrid2D symmColorGrid = colorMapper.getMappedGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = ca.getMinX(); maxX = ca.getMaxX();
			minY = ca.getMinY(); maxY = ca.getMaxY();
			createImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross_section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				backUp = System.currentTimeMillis() >= nextBckTime;
				if (backUp) {
					nextBckTime += backupLeap;
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
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSection3DEdgeImages(EvolvingIntGrid4D ca, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		IntGrid3D edge = ca.projected3DEdgeMaxW();
		int scanZ = edge.getMinZ();
		do {
			edge = ca.projected3DEdgeMaxW();
			System.out.println("step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			String imgPath = path;
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("scan z: " + scanZ);
			IntGrid2D scan = edge.crossSectionAtZ(scanZ);
			int[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("3D edge scan: min value: " + minAndMaxValue[0] + ", max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/scan_3D_edge/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");			
			scanZ++;
			IntGrid2D xSection = edge.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3D edge cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath
					+ "/cross_section_3D_edge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");
			IntGrid2D xSection2 = ca.crossSectionAtYZ(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D symmColorGrid = colorMapper.getMappedGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = ca.getMinX(); maxX = ca.getMaxX();
			minY = ca.getMinY(); maxY = ca.getMaxY();
			createImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath
					+ "/cross_section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				backUp = System.currentTimeMillis() >= nextBckTime;
				if (backUp) {
					nextBckTime += backupLeap;
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
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSection3DEdgeMaxWImages(EvolvingShortGrid4D ca, 
			int crossSectionZ, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		ShortGrid3D edge = ca.projected3DEdgeMaxW();
		int scanZ = edge.getMinZ();
		do {
			edge = ca.projected3DEdgeMaxW();
			System.out.println("step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX(), minX, minY;
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			String imgPath = path;
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("scan z: " + scanZ);
			ShortGrid2D scan = edge.crossSectionAtZ(scanZ);
			short[] minAndMaxValue = scan.getMinAndMaxValue();
			System.out.println("3D edge scan: min value: " + minAndMaxValue[0] + ", max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/scan_3D_edge/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");			
			scanZ++;
			ShortGrid2D xSection = edge.crossSectionAtZ(crossSectionZ);
			minAndMaxValue = xSection.getMinAndMaxValue();
			System.out.println("3D edge cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath 
					+ "/cross_section_3D_edge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");
			ShortGrid2D xSection2 = ca.crossSectionAtYZ(crossSectionZ, crossSectionZ);
			minAndMaxValue = xSection2.getMinAndMaxValue();
			System.out.println("cross section: min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D symmColorGrid = colorMapper.getMappedGrid(xSection2, minAndMaxValue[0], minAndMaxValue[1]);
			minX = ca.getMinX(); maxX = ca.getMaxX();
			minY = ca.getMinY(); maxY = ca.getMaxY();
			createImage(symmColorGrid, minX, maxX, minY, maxY, minWidth, minHeight, imgPath
					+ "/cross_section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3D_edge_x_section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				backUp = System.currentTimeMillis() >= nextBckTime;
				if (backUp) {
					nextBckTime += backupLeap;
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
		System.out.println("finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void create3DEdgeSurfaceImages(EvolvingIntGrid4D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/surface_3D_egde/";
		do {
			System.out.println("step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			IntGrid2D surface = ca.projected3DEdgeMaxW().projectedSurfaceMaxX();
			int[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
	}
	
	public void create3DEdgeSurfaceImages(EvolvingShortGrid4D ca, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		String imgPath = path + "/surface_3D_egde/";
		do {
			System.out.println("step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("max y: " + maxY + System.lineSeparator() + "max x: " + maxX);
			ShortGrid2D surface = ca.projected3DEdgeMaxW().projectedSurfaceMaxX();
			short[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
			System.out.println();
		} while (ca.nextStep());
		System.out.println("finished!");
	}
	
	public void createAsymmetricSectionXScanningImages(SymmetricActionableEvolvingIntGrid3D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getClass().getSimpleName() + "/scans/step=" + currentStep + "/x_scan/";
		System.out.println("scanning grid at step " + currentStep + " along the x axis.");
		
		ca.addProcessor(new SymmetricGridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX(), maxX = gridBlock.getMaxX();
				System.out.println("new block: [" + minX + "," + maxX + "]");
				for (int scanX = minX; scanX <= maxX; scanX++) {
					System.out.println("x: " + scanX);
					IntGrid2D xSection = gridBlock.crossSectionAtX(scanX);
					int[] minAndMaxValue = xSection.getMinAndMaxValue();
					System.out.println("min value: " + minAndMaxValue[0] + System.lineSeparator() + "max value: " + minAndMaxValue[1]);
					ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, minAndMaxValue[0], minAndMaxValue[1]);
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
	
		System.out.println("finished!");
	}
	
	public void createXScanningEvenOddImagesFromAsymmetricSection(SymmetricActionableEvolvingIntGrid3D ca, ColorMapper colorMapper, 
			int imageWidth, int imageHeight, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getClass().getSimpleName() + "/scans/step=" 
				+ currentStep + "/x_scan/" + imageWidth + "x" + imageHeight + "/";
		System.out.println("scanning grid at step " + currentStep + " along the x axis.");
		
		ca.addProcessor(new SymmetricGridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX();
				int maxX = gridBlock.getMaxX();
				System.out.println("new block: min x: " + minX + System.lineSeparator() + "max x: " + maxX);
				boolean isEven = (minX + currentStep)%2 == 0;
				for (int scanX = minX; scanX <= maxX; scanX++, isEven = !isEven) {
					System.out.println("x: " + scanX);
					IntGrid2D xSection = gridBlock.crossSectionAtX(scanX);
					SubareaGrid<IntGrid2D> xSectionSubareaGrid = new SubareaGrid<IntGrid2D>(xSection, imageWidth, imageHeight);
					int subareasGridMinX = xSectionSubareaGrid.getMinX();
					int subareasGridMaxX = xSectionSubareaGrid.getMaxX();
					for (int subareasX = subareasGridMinX; subareasX <= subareasGridMaxX; subareasX++) {
						int subareasGridLocalMinY = xSectionSubareaGrid.getMinY(subareasX);
						int subareasGridLocalMaxY = xSectionSubareaGrid.getMaxY(subareasX);
						for (int subareasY = subareasGridLocalMinY; subareasY <= subareasGridLocalMaxY; subareasY++) {
							System.out.println("subarea: (" + subareasX + "," + subareasY + ")");
							int framedGridMinX = subareasX * imageWidth;
							int framedGridMaxX = framedGridMinX + imageWidth - 1;
							int framedGridMinY = subareasY * imageHeight;
							int framedGridMaxY = framedGridMinY + imageHeight - 1;
							IntGrid2D xSectionSubarea = xSectionSubareaGrid.getSubareaAtPosition(subareasX, subareasY);
							int[] evenMinAndMaxValue = xSectionSubarea.getEvenOddPositionsMinAndMaxValue(isEven);
							System.out.println("even positions min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "even positions max value: " + evenMinAndMaxValue[1]);
							ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSectionSubarea, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(isEven, colorGrid, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
									imageWidth, imageHeight, imgsPath + "even/" 
									+ subareasX + "," + subareasY, caName + "_x_section_" + scanX + ".png");
							int[] oddMinAndMaxValue = xSectionSubarea.getEvenOddPositionsMinAndMaxValue(!isEven);
							System.out.println("odd positions min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "odd positions max value: " + oddMinAndMaxValue[1]);
							colorGrid = colorMapper.getMappedGrid(xSectionSubarea, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
							createImageFromEvenOrOddPositions(!isEven, colorGrid, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
									imageWidth, imageHeight, imgsPath + "odd/" 
									+ subareasX + "," + subareasY, caName + "_x_section_" + scanX + ".png");
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
	
		System.out.println("finished!");
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
	
	public void createZScanningEvenOddImagesFromAsymmetricSection(SymmetricActionableEvolvingIntGrid3D ca,
			ColorMapper colorMapper, String imagesPath) throws Exception {
		long currentStep = ca.getStep();
		String caName = ca.getName();
		String imgsPath = imagesPath + "/" + colorMapper.getClass().getSimpleName() + "/scans/step=" 
				+ currentStep + "/z_scan/";
		System.out.println("scanning grid at step " + currentStep + " along the z axis.");
		
		ca.addProcessor(new SymmetricGridProcessor<IntGrid3D>() {
			
			@Override
			public void processGridBlock(IntGrid3D gridBlock) throws Exception {
				int minX = gridBlock.getMinX();
				int maxX = gridBlock.getMaxX();
				int minY = gridBlock.getMinY();
				int maxY = gridBlock.getMaxY();
				int minZ = gridBlock.getMinZ();
				int maxZ = gridBlock.getMaxZ();
				System.out.println("new block: [" + minX + "," + maxX + "]");
				boolean isEven = (minZ + currentStep)%2 == 0;
				for (int scanZ = minZ; scanZ <= maxZ; scanZ++, isEven = !isEven) {
					System.out.println("z: " + scanZ);
					IntGrid2D xSection = gridBlock.crossSectionAtZ(scanZ);
					int[] evenMinAndMaxValue = xSection.getEvenOddPositionsMinAndMaxValue(isEven);
					System.out.println("even positions min value: " + evenMinAndMaxValue[0] + System.lineSeparator() + "even positions max value: " + evenMinAndMaxValue[1]);
					ColorGrid2D colorGrid = colorMapper.getMappedGrid(xSection, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(isEven, colorGrid, minX, maxX, minY, maxY,
							imgsPath + "minX=" + minX + "_maxX=" + maxX + "/even/", caName + "_x_section_" + scanZ + ".png");
					int[] oddMinAndMaxValue = xSection.getEvenOddPositionsMinAndMaxValue(!isEven);
					System.out.println("odd positions min value: " + oddMinAndMaxValue[0] + System.lineSeparator() + "odd positions max value: " + oddMinAndMaxValue[1]);
					colorGrid = colorMapper.getMappedGrid(xSection, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
					createImageFromEvenOrOddPositions(!isEven, colorGrid, minX, maxX, minY, maxY,
							imgsPath + "minX=" + minX + "_maxX=" + maxX + "/odd/", caName + "_x_section_" + scanZ + ".png");				
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
	
		System.out.println("finished!");
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
		System.out.println("creating image at: '" + pathName + "'");
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
					System.out.println("backup requested");
				}
			}
		}
		
		public void stop() {
			stop = true;
			s.close();
		}
		
	}
	
}
