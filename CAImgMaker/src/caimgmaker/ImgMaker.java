/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
import java.io.IOException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.math3.FieldElement;

import caimgmaker.colormap.ColorMapper;
import cellularautomata.MinAndMax;
import cellularautomata.Utils;
import cellularautomata.model.Model;
import cellularautomata.model2d.BooleanModel2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.ObjectModel2D;
import cellularautomata.model3d.BooleanModel3D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.NumericModel3D;

public class ImgMaker {
	
	private long imgsPerFolder = 10000;
	private long millisecondsBetweenBackups;
	private boolean saveBackupsAutomatically = true;
	private InputReaderTask inputReader;
	//translated strings
	private static String backingUpInstanceMessageFormat;
	private static String backingUpFinishedMessage;
	private static String stepNameAndEquals;
	private static String lessThanOrEqualToValueNameLessThanOrEqualTo;
	private static String lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo;
	private static String lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo;
	private static String lessThanOrEqualToEvenCoordValueNameLessThanOrEqualToFormat;
	private static String lessThanOrEqualToOddCoordValueNameLessThanOrEqualToFormat;
	private static String scanCoordAndEqualsFormat;
	private static String imagePathNameAndEquals;
	private static String maxImageSizeExceededMessage;
	private static final String LINE_SEPARATOR = System.lineSeparator();
	
	public ImgMaker(ResourceBundle messages, InputReaderTask inputReader) {
		saveBackupsAutomatically = false;
		this.inputReader = inputReader;
		getTranslatedStrings(messages);
	}
	
	public ImgMaker(ResourceBundle messages, InputReaderTask inputReader, long millisecondsBetweenBackups) {
		this.inputReader = inputReader;
		this.millisecondsBetweenBackups = millisecondsBetweenBackups;
		getTranslatedStrings(messages);
	}
	
	private void getTranslatedStrings(ResourceBundle messages) {
		backingUpInstanceMessageFormat = messages.getString("backing-up-instance-format");
		backingUpFinishedMessage = messages.getString("backing-up-finished");
		stepNameAndEquals = messages.getString("step") + " = ";
		lessThanOrEqualToValueNameLessThanOrEqualTo = " <= " + messages.getString("value") + " <= ";
		lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo = " <= " + messages.getString("even-coords-value-no-spaces") + " <= ";
		lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo = " <= " + messages.getString("odd-coords-value-no-spaces") + " <= ";
		lessThanOrEqualToEvenCoordValueNameLessThanOrEqualToFormat = " <= " + messages.getString("even-coord-value-no-spaces-format") + " <= ";
		lessThanOrEqualToOddCoordValueNameLessThanOrEqualToFormat = " <= " + messages.getString("odd-coord-value-no-spaces-format") + " <= ";
		scanCoordAndEqualsFormat = LINE_SEPARATOR + messages.getString("scan-coord-no-spaces-format") + " = ";
		imagePathNameAndEquals = messages.getString("image-path-no-spaces") + " = ";
		maxImageSizeExceededMessage = messages.getString("max-img-size-exceeded");
	}
	
	private void backUp(Model model, long step, String backupPath) throws Exception {
		String backupName = model.getName() + "_" + step + "_" + Utils.getFileNameSafeTimeStamp();
		System.out.printf(backingUpInstanceMessageFormat, backupPath + "/" + backupName);
		model.backUp(backupPath, backupName);		
		System.out.println(backingUpFinishedMessage);
	}
	
	public void createImages(BooleanModel2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap) throws Exception {	
		long step = ca.getStep();
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(ca);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, name + step + ".png");
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println();
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}	
			step++;
			currentStepLeap++;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createImages(IntModel2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap) throws Exception {	
		long step = ca.getStep();
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				int[] minAndMaxValue = ca.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(ca, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, name + step + ".png");
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println();
			}	
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}		
			step++;
			currentStepLeap++;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createImages(LongModel2D ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap) throws Exception {	
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		long step = ca.getStep();
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				long[] minAndMaxValue = ca.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(ca, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, name + step + ".png");
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}	
				System.out.println();
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}	
			step++;
			currentStepLeap++;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void createImages(
			NumericModel2D<Number_Type> ca, ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap) throws Exception {	
		long step = ca.getStep();
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				MinAndMax<Number_Type> minAndMaxValue = ca.getMinAndMax();
				System.out.println(minAndMaxValue.getMin() + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue.getMax());
				ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(ca, minAndMaxValue.getMin(), minAndMaxValue.getMax());
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, imgPath + numberedFolder, name + step + ".png");
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}	
				System.out.println();
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createEvenOddImages(BooleanModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		long step = ca.getStep();
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				if (!omitEven) {
					ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca);
					createImageFromEvenOrOddPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
						imgPath + "even/" + numberedFolder, name + step + ".png");
				}
				if (!omitOdd) {
					ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca);
					createImageFromEvenOrOddPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + "odd/" + numberedFolder, name + step + ".png");
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println();
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createEvenOddImages(IntModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		long step = ca.getStep();
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				if (!omitEven) {
					int[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(isEvenStep);			
					if (evenMinAndMaxValue != null) {
						System.out.println(evenMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenMinAndMaxValue[1]);
						ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(!isEvenStep);
					if (oddMinAndMaxValue != null) {
						System.out.println(oddMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddMinAndMaxValue[1]);
						ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println();
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createEvenOddImages(LongModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		long step = ca.getStep();
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				if (!omitEven) {
					long[] evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(isEvenStep);			
					if (evenMinAndMaxValue != null) {
						System.out.println(evenMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenMinAndMaxValue[1]);
						ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					long[] oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(!isEvenStep);
					if (oddMinAndMaxValue != null) {
						System.out.println(oddMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddMinAndMaxValue[1]);
						ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println();
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void createEvenOddImages(NumericModel2D<Number_Type> ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		long step = ca.getStep();
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				if (!omitEven) {
					MinAndMax<Number_Type> evenMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(isEvenStep);			
					if (evenMinAndMaxValue != null) {
						System.out.println(evenMinAndMaxValue.getMin() + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenMinAndMaxValue.getMax());
						ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca, evenMinAndMaxValue.getMin(), evenMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					MinAndMax<Number_Type> oddMinAndMaxValue = ca.getEvenOddPositionsMinAndMax(!isEvenStep);
					if (oddMinAndMaxValue != null) {
						System.out.println(oddMinAndMaxValue.getMin() + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddMinAndMaxValue.getMax());
						ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca, oddMinAndMaxValue.getMin(), oddMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println();
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createImagesFromEvenOddX(IntModel2D ca, ColorMapper colorMapper, 
			int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {	
		long step = ca.getStep();
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel();
		String lessThanOrEqualToEvenXValueNameLessThanOrEqualTo = String.format(lessThanOrEqualToEvenCoordValueNameLessThanOrEqualToFormat, xLabel);
		String lessThanOrEqualToOddXValueNameLessThanOrEqualTo = String.format(lessThanOrEqualToOddCoordValueNameLessThanOrEqualToFormat, xLabel);
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		String imgPath = path + "/";
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY);
				if (!omitEven) {
					int[] evenMinAndMaxValue = ca.getMinAndMaxAtEvenOddX(isEvenStep);			
					if (evenMinAndMaxValue != null) {
						System.out.println(evenMinAndMaxValue[0] + lessThanOrEqualToEvenXValueNameLessThanOrEqualTo + evenMinAndMaxValue[1]);
						ObjectModel2D<Color> evenColorModel = colorMapper.getMappedModel(ca, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
						createImageFromEvenOrOddXPositions(evenColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
							imgPath + "even_" + xLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "even_" + xLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddMinAndMaxValue = ca.getMinAndMaxAtEvenOddX(!isEvenStep);
					if (oddMinAndMaxValue != null) {
						System.out.println(oddMinAndMaxValue[0] + lessThanOrEqualToOddXValueNameLessThanOrEqualTo + oddMinAndMaxValue[1]);
						ObjectModel2D<Color> oddColorModel = colorMapper.getMappedModel(ca, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
						createImageFromEvenOrOddXPositions(oddColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "odd_" + xLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								imgPath + "odd_" + xLabel + "/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}	
				System.out.println();
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createScanningAndZCrossSectionImages(BooleanModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap) throws Exception {
		long step = ca.getStep();
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinX()) {
					xScanCoord = ca.getMaxX();
				}
				System.out.println(xScanCoordAndEquals + xScanCoord);
				BooleanModel2D crossSection = ca.crossSectionAtX(xScanCoord);
				ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection);
				createImage(colorModel, minY, maxY, minZ, maxZ, minWidth, minHeight, 
						xScanImgPath + numberedFolder, name + step + ".png");
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY()) {
					yScanCoord = ca.getMaxY();
				}
				System.out.println(yScanCoordAndEquals + yScanCoord);
				crossSection = ca.crossSectionAtY(yScanCoord);
				colorModel = colorMapper.getMappedModel(crossSection);
				createImage(colorModel, minX, maxX, minZ, maxZ, minWidth, minHeight, 
						yScanImgPath + numberedFolder, name + step + ".png");
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ()) {
					zScanCoord = ca.getMaxZ();
				}
				System.out.println(zScanCoordAndEquals + zScanCoord);
				crossSection = ca.crossSectionAtZ(zScanCoord);
				colorModel = colorMapper.getMappedModel(crossSection);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + numberedFolder, name + step + ".png");
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				colorModel = colorMapper.getMappedModel(crossSection);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
						name + step + ".png");
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createScanningAndZCrossSectionImages(IntModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap) throws Exception {
		long step = ca.getStep();
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinX()) {
					xScanCoord = ca.getMaxX();
				}
				System.out.println(xScanCoordAndEquals + xScanCoord);
				IntModel2D crossSection = ca.crossSectionAtX(xScanCoord);
				int[] minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minY, maxY, minZ, maxZ, minWidth, minHeight, 
						xScanImgPath + numberedFolder, name + step + ".png");
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY()) {
					yScanCoord = ca.getMaxY();
				}
				System.out.println(yScanCoordAndEquals + yScanCoord);
				crossSection = ca.crossSectionAtY(yScanCoord);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minX, maxX, minZ, maxZ, minWidth, minHeight, 
						yScanImgPath + numberedFolder, name + step + ".png");
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ()) {
					zScanCoord = ca.getMaxZ();
				}
				System.out.println(zScanCoordAndEquals + zScanCoord);
				crossSection = ca.crossSectionAtZ(zScanCoord);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + numberedFolder, name + step + ".png");
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
						name + step + ".png");
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createScanningAndZCrossSectionImages(LongModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap) throws Exception {
		long step = ca.getStep();
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinX()) {
					xScanCoord = ca.getMaxX();
				}
				System.out.println(xScanCoordAndEquals + xScanCoord);
				LongModel2D crossSection = ca.crossSectionAtX(xScanCoord);
				long[] minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minY, maxY, minZ, maxZ, minWidth, minHeight, 
						xScanImgPath + numberedFolder, name + step + ".png");
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY()) {
					yScanCoord = ca.getMaxY();
				}
				System.out.println(yScanCoordAndEquals + yScanCoord);
				crossSection = ca.crossSectionAtY(yScanCoord);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minX, maxX, minZ, maxZ, minWidth, minHeight, 
						yScanImgPath + numberedFolder, name + step + ".png");
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ()) {
					zScanCoord = ca.getMaxZ();
				}
				System.out.println(zScanCoordAndEquals + zScanCoord);
				crossSection = ca.crossSectionAtZ(zScanCoord);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + numberedFolder, name + step + ".png");
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue[1]);
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue[0], minAndMaxValue[1]);
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
						name + step + ".png");
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void createScanningAndZCrossSectionImages(NumericModel3D<Number_Type> ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap) throws Exception {
		long step = ca.getStep();
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinX()) {
					xScanCoord = ca.getMaxX();
				}
				System.out.println(xScanCoordAndEquals + xScanCoord);
				NumericModel2D<Number_Type> crossSection = ca.crossSectionAtX(xScanCoord);
				MinAndMax<Number_Type> minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue.getMin() + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue.getMax());
				ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
				createImage(colorModel, minY, maxY, minZ, maxZ, minWidth, minHeight, 
						xScanImgPath + numberedFolder, name + step + ".png");
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY()) {
					yScanCoord = ca.getMaxY();
				}
				System.out.println(yScanCoordAndEquals + yScanCoord);
				crossSection = ca.crossSectionAtY(yScanCoord);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue.getMin() + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue.getMax());
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
				createImage(colorModel, minX, maxX, minZ, maxZ, minWidth, minHeight, 
						yScanImgPath + numberedFolder, name + step + ".png");
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ()) {
					zScanCoord = ca.getMaxZ();
				}
				System.out.println(zScanCoordAndEquals + zScanCoord);
				crossSection = ca.crossSectionAtZ(zScanCoord);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue.getMin() + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue.getMax());
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, 
						zScanImgPath + numberedFolder, name + step + ".png");
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				minAndMaxValue = crossSection.getMinAndMax();
				System.out.println(minAndMaxValue.getMin() + lessThanOrEqualToValueNameLessThanOrEqualTo + minAndMaxValue.getMax());
				colorModel = colorMapper.getMappedModel(crossSection, minAndMaxValue.getMin(), minAndMaxValue.getMax());
				createImage(colorModel, minX, maxX, minY, maxY, minWidth, minHeight, crossSectionImgPath + numberedFolder, 
						name + step + ".png");
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createScanningAndZCrossSectionEvenOddImages(BooleanModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		boolean isEvenCrossSectionZ = crossSectionZ%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];		
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinY())
					xScanCoord = ca.getMaxY();	
				BooleanModel2D crossSection = ca.crossSectionAtX(xScanCoord);
				System.out.println(xScanCoordAndEquals + xScanCoord);
				if (!omitEven) {
					ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection);
					createImageFromEvenOrOddPositions(evenScanColorModel, xScanCoord%2 == 0 == isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
							xScanImgPath + "even/" + numberedFolder, name + step + ".png");
				}
				if (!omitOdd) {
					ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection);
					createImageFromEvenOrOddPositions(oddScanColorModel, xScanCoord%2 == 0 != isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
							xScanImgPath + "odd/" + numberedFolder, name + step + ".png");
				}
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY())
					yScanCoord = ca.getMaxY();	
				crossSection = ca.crossSectionAtY(yScanCoord);
				System.out.println(yScanCoordAndEquals + yScanCoord);
				if (!omitEven) {
					ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection);
					createImageFromEvenOrOddPositions(evenScanColorModel, yScanCoord%2 == 0 == isEvenStep, minX, maxX, minZ, maxZ, minWidth, minHeight, 
							yScanImgPath + "even/" + numberedFolder, name + step + ".png");
				}
				if (!omitOdd) {
					ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection);
					createImageFromEvenOrOddPositions(oddScanColorModel, yScanCoord%2 == 0 != isEvenStep, minX, maxX, minZ, maxZ, minWidth, minHeight, 
							yScanImgPath + "odd/" + numberedFolder, name + step + ".png");
				}
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ())
					zScanCoord = ca.getMaxZ();	
				crossSection = ca.crossSectionAtZ(zScanCoord);
				System.out.println(zScanCoordAndEquals + zScanCoord);
				if (!omitEven) {
					ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection);
					createImageFromEvenOrOddPositions(evenScanColorModel, zScanCoord%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
							zScanImgPath + "even/" + numberedFolder, name + step + ".png");
				}
				if (!omitOdd) {
					ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection);
					createImageFromEvenOrOddPositions(oddScanColorModel, zScanCoord%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
							zScanImgPath + "odd/" + numberedFolder, name + step + ".png");
				}
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				if (!omitEven) {
					ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection);
					createImageFromEvenOrOddPositions(evenCrossSectionColorModel, isEvenStep == isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
							crossSectionImgPath + "even/" + numberedFolder, name + step + ".png");
				}
				if (!omitOdd) {
					ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection);
					createImageFromEvenOrOddPositions(oddCrossSectionColorModel, isEvenStep != isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
									crossSectionImgPath + "odd/" + numberedFolder, name + step + ".png");
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createScanningAndZCrossSectionEvenOddImages(IntModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		boolean isEvenCrossSectionZ = crossSectionZ%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];		
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinY())
					xScanCoord = ca.getMaxY();	
				IntModel2D crossSection = ca.crossSectionAtX(xScanCoord);
				System.out.println(xScanCoordAndEquals + xScanCoord);
				if (!omitEven) {
					int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(xScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenScanColorModel, xScanCoord%2 == 0 == isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(xScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddScanColorModel, xScanCoord%2 == 0 != isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY())
					yScanCoord = ca.getMaxY();	
				crossSection = ca.crossSectionAtY(yScanCoord);
				System.out.println(yScanCoordAndEquals + yScanCoord);
				if (!omitEven) {
					int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(yScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenScanColorModel, yScanCoord%2 == 0 == isEvenStep, minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(yScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddScanColorModel, yScanCoord%2 == 0 != isEvenStep, minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ())
					zScanCoord = ca.getMaxZ();	
				crossSection = ca.crossSectionAtZ(zScanCoord);
				System.out.println(zScanCoordAndEquals + zScanCoord);
				if (!omitEven) {
					int[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(zScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenScanColorModel, zScanCoord%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(zScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddScanColorModel, zScanCoord%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				if (!omitEven) {
					int[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep == isEvenCrossSectionZ);
					if (evenCrossSectionMinAndMaxValue != null) {
						System.out.println(evenCrossSectionMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenCrossSectionMinAndMaxValue[1]);
						ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenCrossSectionColorModel, isEvenStep == isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep != isEvenCrossSectionZ);
					if (oddCrossSectionMinAndMaxValue != null) {
						System.out.println(oddCrossSectionMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddCrossSectionMinAndMaxValue[1]);
						ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddCrossSectionColorModel, isEvenStep != isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
										crossSectionImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public void createScanningAndZCrossSectionEvenOddImages(LongModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		boolean isEvenCrossSectionZ = crossSectionZ%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];		
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinY())
					xScanCoord = ca.getMaxY();	
				LongModel2D crossSection = ca.crossSectionAtX(xScanCoord);
				System.out.println(xScanCoordAndEquals + xScanCoord);
				if (!omitEven) {
					long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(xScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenScanColorModel, xScanCoord%2 == 0 == isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(xScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddScanColorModel, xScanCoord%2 == 0 != isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY())
					yScanCoord = ca.getMaxY();	
				crossSection = ca.crossSectionAtY(yScanCoord);
				System.out.println(yScanCoordAndEquals + yScanCoord);
				if (!omitEven) {
					long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(yScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenScanColorModel, yScanCoord%2 == 0 == isEvenStep, minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(yScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddScanColorModel, yScanCoord%2 == 0 != isEvenStep, minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ())
					zScanCoord = ca.getMaxZ();	
				crossSection = ca.crossSectionAtZ(zScanCoord);
				System.out.println(zScanCoordAndEquals + zScanCoord);
				if (!omitEven) {
					long[] evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(zScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenScanColorModel, zScanCoord%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					long[] oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(zScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddScanColorModel, zScanCoord%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				if (!omitEven) {
					long[] evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep == isEvenCrossSectionZ);
					if (evenCrossSectionMinAndMaxValue != null) {
						System.out.println(evenCrossSectionMinAndMaxValue[0] + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenCrossSectionMinAndMaxValue[1]);
						ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(evenCrossSectionColorModel, isEvenStep == isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					long[] oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep != isEvenCrossSectionZ);
					if (oddCrossSectionMinAndMaxValue != null) {
						System.out.println(oddCrossSectionMinAndMaxValue[0] + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddCrossSectionMinAndMaxValue[1]);
						ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
						createImageFromEvenOrOddPositions(oddCrossSectionColorModel, isEvenStep != isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
										crossSectionImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void createScanningAndZCrossSectionEvenOddImages(NumericModel3D<Number_Type> ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		boolean isEvenCrossSectionZ = crossSectionZ%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];		
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;	
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinY())
					xScanCoord = ca.getMaxY();	
				NumericModel2D<Number_Type> crossSection = ca.crossSectionAtX(xScanCoord);
				System.out.println(xScanCoordAndEquals + xScanCoord);
				if (!omitEven) {
					MinAndMax<Number_Type> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(xScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue.getMin() + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue.getMax());
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(evenScanColorModel, xScanCoord%2 == 0 == isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					MinAndMax<Number_Type> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(xScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue.getMin() + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue.getMax());
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(oddScanColorModel, xScanCoord%2 == 0 != isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY())
					yScanCoord = ca.getMaxY();	
				crossSection = ca.crossSectionAtY(yScanCoord);
				System.out.println(yScanCoordAndEquals + yScanCoord);
				if (!omitEven) {
					MinAndMax<Number_Type> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(yScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue.getMin() + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue.getMax());
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(evenScanColorModel, yScanCoord%2 == 0 == isEvenStep, minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					MinAndMax<Number_Type> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(yScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue.getMin() + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue.getMax());
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(oddScanColorModel, yScanCoord%2 == 0 != isEvenStep, minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ())
					zScanCoord = ca.getMaxZ();	
				crossSection = ca.crossSectionAtZ(zScanCoord);
				System.out.println(zScanCoordAndEquals + zScanCoord);
				if (!omitEven) {
					MinAndMax<Number_Type> evenScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(zScanCoord%2 == 0 == isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue.getMin() + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenScanMinAndMaxValue.getMax());
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue.getMin(), evenScanMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(evenScanColorModel, zScanCoord%2 == 0 == isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					MinAndMax<Number_Type> oddScanMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(zScanCoord%2 == 0 != isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue.getMin() + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddScanMinAndMaxValue.getMax());
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue.getMin(), oddScanMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(oddScanColorModel, zScanCoord%2 == 0 != isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				if (!omitEven) {
					MinAndMax<Number_Type> evenCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep == isEvenCrossSectionZ);
					if (evenCrossSectionMinAndMaxValue != null) {
						System.out.println(evenCrossSectionMinAndMaxValue.getMin() + lessThanOrEqualToEvenCoordsValueNameLessThanOrEqualTo + evenCrossSectionMinAndMaxValue.getMax());
						ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue.getMin(), evenCrossSectionMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(evenCrossSectionColorModel, isEvenStep == isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					MinAndMax<Number_Type> oddCrossSectionMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEvenStep != isEvenCrossSectionZ);
					if (oddCrossSectionMinAndMaxValue != null) {
						System.out.println(oddCrossSectionMinAndMaxValue.getMin() + lessThanOrEqualToOddCoordsValueNameLessThanOrEqualTo + oddCrossSectionMinAndMaxValue.getMax());
						ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue.getMin(), oddCrossSectionMinAndMaxValue.getMax());
						createImageFromEvenOrOddPositions(oddCrossSectionColorModel, isEvenStep != isEvenCrossSectionZ, minX, maxX, minY, maxY, minWidth, minHeight, 
										crossSectionImgPath + "odd/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	//TODO review after changing cross section coordinate mapping
	public void createScanningAndZCrossSectionImagesFromEvenOddY(LongModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		String lessThanOrEqualToEvenYValueNameLessThanOrEqualTo = String.format(lessThanOrEqualToEvenCoordValueNameLessThanOrEqualToFormat, yLabel);
		String lessThanOrEqualToOddYValueNameLessThanOrEqualTo = String.format(lessThanOrEqualToOddCoordValueNameLessThanOrEqualToFormat, yLabel);
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;	
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(), 
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinY())
					xScanCoord = ca.getMaxY();	
				LongModel2D crossSection = ca.crossSectionAtX(xScanCoord);
				System.out.println(xScanCoordAndEquals + xScanCoord);
				if (!omitEven) {
					long[] evenScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenYValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(evenScanColorModel, isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					long[] oddScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddYValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(oddScanColorModel, !isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY())
					yScanCoord = ca.getMaxY();	
				crossSection = ca.crossSectionAtY(yScanCoord);
				System.out.println(yScanCoordAndEquals + yScanCoord);
				boolean isEvenYScan = yScanCoord%2 == 0 == isEvenStep;
				if (isEvenYScan && !omitEven || !isEvenYScan && !omitOdd) {
					long[] scanMinAndMaxValue = crossSection.getMinAndMax();
					System.out.println(scanMinAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + scanMinAndMaxValue[1]);
					ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
					createImage(colorModel, minX, maxX, minZ, maxZ, minWidth, minHeight, 
							yScanImgPath + (isEvenYScan ? "even" : "odd") + "_" + yLabel + "/" + numberedFolder, name + step + ".png");
				}
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ())
					zScanCoord = ca.getMaxZ();	
				crossSection = ca.crossSectionAtZ(zScanCoord);
				System.out.println(zScanCoordAndEquals + zScanCoord);
				if (!omitEven) {
					long[] evenScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenYValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(evenScanColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					long[] oddScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddYValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(oddScanColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				if (!omitEven) {
					long[] evenCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
					if (evenCrossSectionMinAndMaxValue != null) {
						System.out.println(evenCrossSectionMinAndMaxValue[0] + lessThanOrEqualToEvenYValueNameLessThanOrEqualTo + evenCrossSectionMinAndMaxValue[1]);
						ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(evenCrossSectionColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					long[] oddCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
					if (oddCrossSectionMinAndMaxValue != null) {
						System.out.println(oddCrossSectionMinAndMaxValue[0] + lessThanOrEqualToOddYValueNameLessThanOrEqualTo + oddCrossSectionMinAndMaxValue[1]);
						ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(oddCrossSectionColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
										crossSectionImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	//TODO review after changing cross section coordinate mapping
	public void createScanningAndZCrossSectionImagesFromEvenOddY(IntModel3D ca, int[] scanInitialCoords, int crossSectionZ, 
			ColorMapper colorMapper, int minWidth, int minHeight, String path, String name, String backupPath, int stepLeap, boolean omitEven, boolean omitOdd) throws Exception {
		long step = ca.getStep();
		boolean isEvenStep = step%2 == 0;
		int numberedFolder = (int) ((step/stepLeap)/imgsPerFolder);
		int folderImageCount = (int) ((step/stepLeap)%imgsPerFolder);
		int xScanCoord = scanInitialCoords[0], yScanCoord = scanInitialCoords[1], zScanCoord = scanInitialCoords[2];
		String xLabel = ca.getXLabel(), yLabel = ca.getYLabel(), zLabel = ca.getZLabel();
		String lessThanOrEqualToEvenYValueNameLessThanOrEqualTo = String.format(lessThanOrEqualToEvenCoordValueNameLessThanOrEqualToFormat, yLabel);
		String lessThanOrEqualToOddYValueNameLessThanOrEqualTo = String.format(lessThanOrEqualToOddCoordValueNameLessThanOrEqualToFormat, yLabel);
		path += "/";
		String xScanImgPath = path + xLabel + "_scan/";
		String yScanImgPath = path + yLabel + "_scan/";
		String zScanImgPath = path + zLabel + "_scan/";
		String xScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, xLabel);
		String yScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, yLabel);
		String zScanCoordAndEquals = String.format(scanCoordAndEqualsFormat, zLabel);
		String crossSectionImgPath = path + zLabel + "=" + crossSectionZ + "/";
		String crossSectionLog = LINE_SEPARATOR + zLabel + " = " + crossSectionZ;
		int currentStepLeap = (int) (step%stepLeap);
		if (currentStepLeap == 0) {
			currentStepLeap = stepLeap;
		}
		Boolean changed = ca.isChanged(), createLastImage = stepLeap > 1;	
		long nextBckTime = System.currentTimeMillis() + millisecondsBetweenBackups;
		do {
			System.out.println(stepNameAndEquals + step);
			if (currentStepLeap == stepLeap || changed != null && !changed && createLastImage) {
				currentStepLeap = 0;
				if (changed != null && !changed) {
					createLastImage = false;
				}
				int minX = ca.getMinX(), maxX = ca.getMaxX(),
						minY = ca.getMinY(), maxY = ca.getMaxY(), 
						minZ = ca.getMinZ(), maxZ = ca.getMaxZ();
				System.out.println(minX + " <= " + xLabel + " <= " + maxX + LINE_SEPARATOR + minY + " <= " + yLabel + " <= " + maxY 
						+ LINE_SEPARATOR + minZ + " <= " + zLabel + " <= " + maxZ);
				//x scan
				if (xScanCoord < ca.getMinY())
					xScanCoord = ca.getMaxY();	
				IntModel2D crossSection = ca.crossSectionAtX(xScanCoord);
				System.out.println(xScanCoordAndEquals + xScanCoord);
				if (!omitEven) {
					int[] evenScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenYValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(evenScanColorModel, isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddYValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(oddScanColorModel, !isEvenStep, minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minY, maxY, minZ, maxZ, minWidth, minHeight, 
								xScanImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				xScanCoord--;
				//y scan
				if (yScanCoord < ca.getMinY())
					yScanCoord = ca.getMaxY();	
				crossSection = ca.crossSectionAtY(yScanCoord);
				System.out.println(yScanCoordAndEquals + yScanCoord);
				boolean isEvenYScan = yScanCoord%2 == 0 == isEvenStep;
				if (isEvenYScan && !omitEven || !isEvenYScan && !omitOdd) {
					int[] scanMinAndMaxValue = crossSection.getMinAndMax();
					if (scanMinAndMaxValue != null) {
						System.out.println(scanMinAndMaxValue[0] + lessThanOrEqualToValueNameLessThanOrEqualTo + scanMinAndMaxValue[1]);
						ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(crossSection, scanMinAndMaxValue[0], scanMinAndMaxValue[1]);
						createImage(colorModel, minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + (isEvenYScan ? "even" : "odd") + "_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minZ, maxZ, minWidth, minHeight, 
								yScanImgPath + (isEvenYScan ? "even" : "odd") + "_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				yScanCoord--;
				//z scan
				if (zScanCoord < ca.getMinZ())
					zScanCoord = ca.getMaxZ();	
				crossSection = ca.crossSectionAtZ(zScanCoord);
				System.out.println(zScanCoordAndEquals + zScanCoord);
				if (!omitEven) {
					int[] evenScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
					if (evenScanMinAndMaxValue != null) {
						System.out.println(evenScanMinAndMaxValue[0] + lessThanOrEqualToEvenYValueNameLessThanOrEqualTo + evenScanMinAndMaxValue[1]);
						ObjectModel2D<Color> evenScanColorModel = colorMapper.getMappedModel(crossSection, evenScanMinAndMaxValue[0], evenScanMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(evenScanColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddScanMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
					if (oddScanMinAndMaxValue != null) {
						System.out.println(oddScanMinAndMaxValue[0] + lessThanOrEqualToOddYValueNameLessThanOrEqualTo + oddScanMinAndMaxValue[1]);
						ObjectModel2D<Color> oddScanColorModel = colorMapper.getMappedModel(crossSection, oddScanMinAndMaxValue[0], oddScanMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(oddScanColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								zScanImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				zScanCoord--;
				//cross section
				System.out.println(crossSectionLog);
				crossSection = ca.crossSectionAtZ(crossSectionZ);
				if (!omitEven) {
					int[] evenCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(isEvenStep);
					if (evenCrossSectionMinAndMaxValue != null) {
						System.out.println(evenCrossSectionMinAndMaxValue[0] + lessThanOrEqualToEvenYValueNameLessThanOrEqualTo + evenCrossSectionMinAndMaxValue[1]);
						ObjectModel2D<Color> evenCrossSectionColorModel = colorMapper.getMappedModel(crossSection, evenCrossSectionMinAndMaxValue[0], evenCrossSectionMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(evenCrossSectionColorModel, isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "even_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}
				if (!omitOdd) {
					int[] oddCrossSectionMinAndMaxValue = crossSection.getMinAndMaxAtEvenOddY(!isEvenStep);
					if (oddCrossSectionMinAndMaxValue != null) {
						System.out.println(oddCrossSectionMinAndMaxValue[0] + lessThanOrEqualToOddYValueNameLessThanOrEqualTo + oddCrossSectionMinAndMaxValue[1]);
						ObjectModel2D<Color> oddCrossSectionColorModel = colorMapper.getMappedModel(crossSection, oddCrossSectionMinAndMaxValue[0], oddCrossSectionMinAndMaxValue[1]);
						createImageFromEvenOrOddYPositions(oddCrossSectionColorModel, !isEvenStep, minX, maxX, minY, maxY, minWidth, minHeight, 
										crossSectionImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					} else {
						createEmptyImage(minX, maxX, minY, maxY, minWidth, minHeight, 
								crossSectionImgPath + "odd_" + yLabel + "/" + numberedFolder, name + step + ".png");
					}
				}			
				folderImageCount++;
				if (folderImageCount == imgsPerFolder) {
					numberedFolder++;
					folderImageCount = 0;
				}
				System.out.println(LINE_SEPARATOR);
			}
			boolean backUp = false;
			if (saveBackupsAutomatically) {
				if (System.currentTimeMillis() >= nextBckTime) {
					backUp = true;
					nextBckTime += millisecondsBetweenBackups;
				}
			}
			if (inputReader.backupRequested) {
				backUp = true;
				inputReader.backupRequested = false;
			}
			if (backUp) {
				backUp(ca, step, backupPath);
			}
			step++;
			currentStepLeap++;
			isEvenStep = !isEvenStep;
		} while ((changed = ca.nextStep()) == null || changed || createLastImage);
	}
	
	//TODO missing methods? EvenOddY(Model2D), EvenOddX(Model3D), EvenOddZ(Model3D)...
	
	private static int getModelPositionSize(int minX, int maxX, int minY, int maxY, int preferredMaxWidth, int preferredMaxHeight) {
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
	
	private static void createImage(ObjectModel2D<Color> grid, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImage(grid, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	private static void createImage(ObjectModel2D<Color> grid, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception(maxImageSizeExceededMessage);
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
	
	private static void createEmptyImage(int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createEmptyImage(minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	private static void createEmptyImage(int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		
		int framedModelWidth = maxX - minX + 1;
		int framedModelHeight = maxY - minY + 1;	
		int framedModelWidthInPixels = framedModelWidth * gridPositionSize;
		int framedModelHeightInPixels = framedModelHeight * gridPositionSize;	
		int imageWidth = Math.max(framedModelWidthInPixels, minWidth);
		int imageHeight = Math.max(framedModelHeightInPixels, minHeight);	
		long longByteCount = (long)imageWidth * imageHeight * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception(maxImageSizeExceededMessage);
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];	
		saveAsPngImage(pixelData, imageWidth, imageHeight, path, name);
	}
	
	private static void createImageFromEvenOrOddPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddPositions(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	private static void createImageFromEvenOrOddPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception(maxImageSizeExceededMessage);
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
	
	private static void createImageFromEvenOrOddXPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddXPositions(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	private static void createImageFromEvenOrOddXPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception(maxImageSizeExceededMessage);
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
	
	private static void createImageFromEvenOrOddYPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) 
			throws Exception {
		int gridPositionSize = getModelPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		createImageFromEvenOrOddYPositions(grid, isEven, minX, maxX, minY, maxY, gridPositionSize, minWidth, minHeight, path, name);
	}
	
	private static void createImageFromEvenOrOddYPositions(ObjectModel2D<Color> grid, boolean isEven, int minX, int maxX, int minY, int maxY, int gridPositionSize, 
			int minWidth, int minHeight, String path, String name) throws Exception {
		int dataWidth = (maxX - minX + 1) * gridPositionSize;
		int dataHeight = (maxY - minY + 1) * gridPositionSize;
		int width = Math.max(dataWidth, minWidth);
		int height = Math.max(dataHeight, minHeight);
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception(maxImageSizeExceededMessage);
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

	private static void saveAsPngImage(byte[] pixelData, int width, int height, String path, String name) throws IOException {
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
		System.out.println(imagePathNameAndEquals + "'" + pathName + "'");
		ImageIO.write(image, "png", new File(pathName));
	}
	
}
