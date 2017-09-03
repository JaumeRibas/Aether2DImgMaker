/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017 Jaume Ribas

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

import caimgmaker.colormap.ColorGrid2D;
import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.HueWithBackgroundMapper;
import caimgmaker.colormap.SymmetricColorGrid2D;
import cellularautomata.automata.Aether2D;
import cellularautomata.automata.SymmetricIntCellularAutomaton4D;
import cellularautomata.automata.SymmetricLongCellularAutomaton2D;
import cellularautomata.automata.SymmetricLongCellularAutomaton3D;
import cellularautomata.automata.SymmetricLongCellularAutomaton4D;
import cellularautomata.automata.SymmetricShortCellularAutomaton4D;
import cellularautomata.grid.Grid2D;
import cellularautomata.grid.IntGrid2D;
import cellularautomata.grid.IntGrid3D;
import cellularautomata.grid.LongGrid2D;
import cellularautomata.grid.LongGrid3D;
import cellularautomata.grid.ShortGrid2D;
import cellularautomata.grid.ShortGrid3D;
import cellularautomata.grid.SymmetricGrid2D;
import cellularautomata.grid.SymmetricIntGrid2D;
import cellularautomata.grid.SymmetricLongGrid2D;
import cellularautomata.grid.SymmetricShortGrid2D;

public class CAImgMaker {
	
	private static final int HD_WIDTH = 1920;
	private static final int HD_HEIGHT = 1080;
	
	private long imgsPerFolder = 10000;	
	private long backupLeap = 1000 * 60 * 60 * 3;
	private volatile boolean backupRequested = false;
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			long initialValue = Long.parseLong(args[0]);
			SymmetricLongCellularAutomaton2D ca = new Aether2D(initialValue);
			String path;
			int initialStep = 0;
			if (args.length > 1) {
				path = args[1];
				char lastCharacter = path.charAt(path.length() - 1); 
				if (lastCharacter != '/' && lastCharacter != '\\') {
					path += "/";
				}
				if (args.length > 2) {
					initialStep = Integer.parseInt(args[2]);
				}
			} else {
				path = "./";
			}
			boolean finished = false;
			while (ca.getCurrentStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("Current step: " + ca.getCurrentStep());
			}
			ColorMapper colorMapper = new HueWithBackgroundMapper(ca.getBackgroundValue(), Color.BLACK);
			path += ca.getSubFolderPath();	
			CAImgMaker imgMaker = new CAImgMaker();
			imgMaker.createSliceImages(ca, colorMapper, HD_WIDTH/2, HD_HEIGHT/2, path);
		}		
	}
	
	public void createSliceImages(SymmetricLongCellularAutomaton2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			long[] minAndMaxValue = ca.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/slice/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createCrossSectionSliceImages(SymmetricLongCellularAutomaton3D ca, int z, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			SymmetricLongGrid2D cSection = ca.crossSection(z);
			long[] minAndMaxValue = cSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(cSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section-slice/z=" + z + "/" + numberedFolder, ca.getName() + "_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createSurfaceImages(SymmetricLongCellularAutomaton3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			LongGrid2D surface = ca.projectedSurface();
			long[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/surface/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
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
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			long[] minAndMaxValue = ca.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(ca, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	public void createScanningSliceImages(SymmetricLongCellularAutomaton3D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = ca.getNonSymmetricMinZ();
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			if (scanZ >= ca.getNonSymmetricMaxZ())
				scanZ = ca.getNonSymmetricMinZ();
			System.out.println("Scan z: " + scanZ);
			SymmetricLongGrid2D cSection = ca.crossSection(scanZ);
			long[] minAndMaxValue = cSection.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(cSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/scan-slice/" + numberedFolder, ca.getName() + "_x-section_" + currentStep + ".png");
			scanZ++;
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backup = System.currentTimeMillis() >= nextBckTime;
			if (backup) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backup = true;
				backupRequested = false;
			}
			if (backup) {
				backup(ca.getData(), path + "/serialized", ca.getClass().getSimpleName() + "_" + currentStep + ".ser");					
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanningAndCrossSectionSliceImages(SymmetricLongCellularAutomaton3D ca, int crossSectionZ, 
			ColorMapper scanningColorMapper, ColorMapper crossSectionColorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		int scanZ = ca.getNonSymmetricMinZ();
		String imgPath = path + "/img/";
		String caName = ca.getName();
		String scanImgPath = imgPath + scanningColorMapper.getClass().getSimpleName() + "/scan-slice/";
		String crossSectionImgPath = imgPath + crossSectionColorMapper.getClass().getSimpleName() + "/cross-section-slice/";
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
//			long[] minAndMaxValue = ca.getMinAndMaxValue();
//			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			if (scanZ >= ca.getNonSymmetricMaxZ())
				scanZ = ca.getNonSymmetricMinZ();
			System.out.println("Scan z: " + scanZ);
			SymmetricLongGrid2D scan = ca.crossSection(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue(ca.getBackgroundValue());
			System.out.println("Scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D colorGrid = scanningColorMapper.getMappedSymmetricGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, scanImgPath + numberedFolder, caName + "_x-section_" + currentStep + ".png");
			scanZ++;
			SymmetricLongGrid2D cSection = ca.crossSection(crossSectionZ);
			minAndMaxValue = cSection.getMinAndMaxValue();
			System.out.println("C-Section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = crossSectionColorMapper.getMappedSymmetricGrid(cSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, crossSectionImgPath + "z=" + crossSectionZ + "/" + numberedFolder, 
					caName + "_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backup = System.currentTimeMillis() >= nextBckTime;
			if (backup) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backup = true;
				backupRequested = false;
			}
			if (backup) {
				backup(ca.getData(), path + "/serialized", ca.getClass().getSimpleName() + "_" + currentStep + ".ser");					
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void createScanning3DEdgeImages(SymmetricLongCellularAutomaton4D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {
		StdInRunnable stdIn = new StdInRunnable();
		Thread inputThread = new Thread(stdIn);
		inputThread.start();
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		LongGrid3D edge = ca.projected3DEdge();
		int scanZ = edge.getMinZ();
		do {
			edge = ca.projected3DEdge();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D cSection = edge.crossSection(scanZ);
			long[] minAndMaxValue = cSection.getMinAndMaxValue(ca.getBackgroundValue());
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(cSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backup = System.currentTimeMillis() >= nextBckTime;
			if (backup) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backup = true;
				backupRequested = false;
			}
			if (backup) {
				backup(ca.getData(), path + "/serialized", ca.getClass().getSimpleName() + "_" + currentStep + ".ser");					
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
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		LongGrid3D edge = ca.projected3DEdge();
		int scanZ = edge.getMinZ();
		do {
			edge = ca.projected3DEdge();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			LongGrid2D scan = edge.crossSection(scanZ);
			long[] minAndMaxValue = scan.getMinAndMaxValue(ca.getBackgroundValue());
			System.out.println("3DEdge scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			LongGrid2D cSection = edge.crossSection(crossSectionZ);
			minAndMaxValue = cSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(cSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			SymmetricLongGrid2D cSection2 = ca.crossSection(crossSectionZ, crossSectionZ);
			minAndMaxValue = cSection2.getMinAndMaxValue();
			System.out.println("C-Section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D symmColorGrid = colorMapper.getMappedSymmetricGrid(cSection2, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(symmColorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backup = System.currentTimeMillis() >= nextBckTime;
			if (backup) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backup = true;
				backupRequested = false;
			}
			if (backup) {
				backup(ca.getData(), path + "/serialized", ca.getClass().getSimpleName() + "_" + currentStep + ".ser");					
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
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		IntGrid3D edge = ca.projected3DEdge();
		int scanZ = edge.getMinZ();
		do {
			edge = ca.projected3DEdge();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			IntGrid2D scan = edge.crossSection(scanZ);
			int[] minAndMaxValue = scan.getMinAndMaxValue(ca.getBackgroundValue());
			System.out.println("3DEdge scan: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			IntGrid2D cSection = edge.crossSection(crossSectionZ);
			minAndMaxValue = cSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(cSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			SymmetricIntGrid2D cSection2 = ca.crossSection(crossSectionZ, crossSectionZ);
			minAndMaxValue = cSection2.getMinAndMaxValue();
			System.out.println("C-Section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D symmColorGrid = colorMapper.getMappedSymmetricGrid(cSection2, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(symmColorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backup = System.currentTimeMillis() >= nextBckTime;
			if (backup) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backup = true;
				backupRequested = false;
			}
			if (backup) {
				backup(ca.getData(), path + "/serialized", ca.getClass().getSimpleName() + "_" + currentStep + ".ser");					
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
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		long nextBckTime = System.currentTimeMillis() + backupLeap;
		ShortGrid3D edge = ca.projected3DEdge();
		int scanZ = edge.getMinZ();
		short[] historicMinAndMaxValue = new short[] {ca.getBackgroundValue(), 0};
		do {
			edge = ca.projected3DEdge();
			System.out.println("Current step: " + currentStep);
			int maxY = edge.getMaxY(), maxX = edge.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			if (scanZ >= edge.getMaxZ())
				scanZ = edge.getMinZ();
			System.out.println("Scan z: " + scanZ);
			ShortGrid2D scan = edge.crossSection(scanZ);
			short[] minAndMaxValue = scan.getMinAndMaxValue(ca.getBackgroundValue());
			System.out.println("3DEdge scan: Min value " + historicMinAndMaxValue[0] + " Max value " + historicMinAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(scan, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/scan-3DEdge/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");			
			scanZ++;
			ShortGrid2D cSection = edge.crossSection(crossSectionZ);
			minAndMaxValue = cSection.getMinAndMaxValue();
			System.out.println("3DEdge c-section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			colorGrid = colorMapper.getMappedGrid(cSection, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section-3DEdge/z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			SymmetricShortGrid2D cSection2 = ca.crossSection(crossSectionZ, crossSectionZ);
			minAndMaxValue = cSection2.getMinAndMaxValue();
			System.out.println("C-Section: Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			SymmetricColorGrid2D symmColorGrid = colorMapper.getMappedSymmetricGrid(cSection2, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(symmColorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/cross-section/y=" + crossSectionZ + "_z=" + crossSectionZ + "/" + numberedFolder, ca.getName() + "_3DEdge_x-section_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}		
			boolean backup = System.currentTimeMillis() >= nextBckTime;
			if (backup) {
				nextBckTime += backupLeap;
			}
			if (backupRequested) {
				backup = true;
				backupRequested = false;
			}
			if (backup) {
				backup(ca.getData(), path + "/serialized", ca.getClass().getSimpleName() + "_" + currentStep + ".ser");					
			}
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
		stdIn.stop();
		inputThread.join();
	}
	
	public void create3DEdgeSurfaceImages(SymmetricIntCellularAutomaton4D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path) throws Exception {	
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			IntGrid2D surface = ca.projected3DEdge().projectedSurfaceMaxX(ca.getBackgroundValue());
			int[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/surface-3DEgde/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
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
		long currentStep = ca.getCurrentStep();
		int numberedFolder = (int) (currentStep/imgsPerFolder);
		int folderImageCount = (int) (currentStep%imgsPerFolder);
		do {
			System.out.println("Current step: " + currentStep);
			int maxY = ca.getMaxY(), maxX = ca.getMaxX();
			System.out.println("maxY=" + maxY + ", maxX=" + maxX);
			String imgPath = path + "/img/";
			ShortGrid2D surface = ca.projected3DEdge().projectedSurfaceMaxX(ca.getBackgroundValue());
			short[] minAndMaxValue = surface.getMinAndMaxValue();
			System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
			ColorGrid2D colorGrid = colorMapper.getMappedGrid(surface, minAndMaxValue[0], minAndMaxValue[1]);
			createSliceImage(colorGrid, minWidth, minHeight, imgPath + colorMapper.getClass().getSimpleName() 
					+ "/surface-3DEgde/" + numberedFolder, ca.getName() + "_" + currentStep + ".png");
			folderImageCount++;
			if (folderImageCount == imgsPerFolder) {
				numberedFolder++;
				folderImageCount = 0;
			}			
			currentStep++;
		} while (ca.nextStep());
		System.out.println("Finished!");
	}
	
	private int getDotSize(SymmetricGrid2D grid, int preferredMaxWidth, int preferredMaxHeight) {
		int yDotSize = 1;
		int dataHeight = grid.getNonSymmetricMaxY() - grid.getNonSymmetricMinY() + 1;
		if (dataHeight > 0) {
			yDotSize = preferredMaxHeight/dataHeight;
        }
		if (yDotSize == 0) yDotSize = 1;
		int xDotSize = 1;
		int dataWidth = grid.getNonSymmetricMaxX() - grid.getNonSymmetricMinX() + 1;
		if (dataWidth > 0) {
			xDotSize = preferredMaxWidth/dataWidth;
        }
		if (xDotSize == 0) xDotSize = 1;
		return Math.min(xDotSize, yDotSize);
	}
	
	private int getDotSize(Grid2D grid, int preferredMaxWidth, int preferredMaxHeight) {
		int yDotSize = 1;
		int dataHeight = grid.getMaxY() - grid.getMinY() + 1;
		if (dataHeight > 0) {
			yDotSize = preferredMaxHeight/dataHeight;
        }
		if (yDotSize == 0) yDotSize = 1;
		int xDotSize = 1;
		int dataWidth = grid.getMaxX() - grid.getMinX() + 1;
		if (dataWidth > 0) {
			xDotSize = preferredMaxWidth/dataWidth;
        }
		if (xDotSize == 0) xDotSize = 1;
		return Math.min(xDotSize, yDotSize);
	}
	
	public void createSliceImage(SymmetricColorGrid2D grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int dotSize = getDotSize(grid, minWidth, minHeight);
		createSliceImage(grid, dotSize, minWidth, minHeight, path, name);
	}
	
	public void createSliceImage(SymmetricColorGrid2D grid, int dotSize, int minWidth, int minHeight, String path, String name) throws Exception {
		int maxX = grid.getNonSymmetricMaxX(), minX = grid.getNonSymmetricMinX(),
				maxY = grid.getNonSymmetricMaxY(), minY = grid.getNonSymmetricMinY();
		int dataWidth = (maxX - minX + 1) * dotSize;
		int dataHeight = (maxY - minY + 1) * dotSize;
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
			for (int i = 0; i < dotSize; i++) {
				for (int x = minX; x <= maxX; x++) {
					if (x >= y) {
						java.awt.Color c = grid.getNonSymmetricColorAt(x, y);
						for (int j = 0; j < dotSize; j++) {
							pixelData[dataIndex++] = (byte) c.getRed();
							pixelData[dataIndex++] = (byte) c.getGreen();
							pixelData[dataIndex++] = (byte) c.getBlue();
						}
					} else
						dataIndex += 3 * dotSize;					
				}
				dataIndex += 3 * rightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public void createSliceImage(ColorGrid2D grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int dotSize = getDotSize(grid, minWidth, minHeight);
		createSliceImage(grid, dotSize, minWidth, minHeight, path, name);
	}
	
	public void createSliceImage(ColorGrid2D grid, int dotSize, int minWidth, int minHeight, String path, String name) throws Exception {
		int maxX = grid.getMaxX(), minX = grid.getMinX(),
				maxY = grid.getMaxY(), minY = grid.getMinY();
		int dataWidth = (maxX - minX + 1) * dotSize;
		int dataHeight = (maxY - minY + 1) * dotSize;
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
			for (int i = 0; i < dotSize; i++) {
				for (int x = minX, xx = 0; x <= maxX; x++, xx++) {
					if (xx >= yy) {
						java.awt.Color c = grid.getColorAt(x, y);
						for (int j = 0; j < dotSize; j++) {
							pixelData[dataIndex++] = (byte) c.getRed();
							pixelData[dataIndex++] = (byte) c.getGreen();
							pixelData[dataIndex++] = (byte) c.getBlue();
						}
					} else
						dataIndex += 3 * dotSize;					
				}
				dataIndex += 3 * rightMargin;
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public void createImage(ColorGrid2D grid, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int dotSize = getDotSize(grid, minWidth, minHeight);
		createImage(grid, dotSize, minWidth, minHeight, path, name);
	}
	
	public void createImage(ColorGrid2D grid, int dotSize, int minWidth, int minHeight, String path, String name) throws Exception {
		int maxX = grid.getMaxX(), minX = grid.getMinX(), maxY = grid.getMaxY(), minY = grid.getMinY();
		int dataWidth = (maxX - minX + 1) * dotSize;
		int dataHeight = (maxY - minY + 1) * dotSize;
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
			for (int i = 0; i < dotSize; i++) {
				for (int x = minX; x <= maxX; x++) {
					java.awt.Color c = grid.getColorAt(x,y);
					for (int j = 0; j < dotSize; j++) {
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
	
	public void saveAsPngImage(byte[] pixelData, int width, int height, String path, String name) throws IOException {
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
	
	private void backup(Object obj, String path, String name) throws FileNotFoundException, IOException {
		String pathName = path + "/" + name;
		System.out.println("Backing up instance at '" + pathName + "'");
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(obj);
		out.flush();
		out.close();
	}
	
	private static Object restore(String pathName) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(pathName));
		Object obj = in.readObject();
		in.close();
		return obj;
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
