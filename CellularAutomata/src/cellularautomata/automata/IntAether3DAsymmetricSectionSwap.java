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
package cellularautomata.automata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

import cellularautomata.evolvinggrid.ActionableEvolvingIntGrid3D;
import cellularautomata.grid3d.AnisotropicIntGrid3DSlice;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid3d.IntSubGrid3DWithXBounds;

public class IntAether3DAsymmetricSectionSwap extends ActionableEvolvingIntGrid3D {

	private static final String PROPERTIES_BACKUP_FILE_NAME = "properties.ser";
	private static final String GRID_FOLDER_NAME = "grid";
	
	private static final byte LEFT = 0;
	private static final byte CENTER = 1;
	private static final byte RIGHT = 2;
	
	private SizeLimitedAnisotropicIntGrid3DBlock gridBlockA;
	private SizeLimitedAnisotropicIntGrid3DBlock gridBlockB;
	private int initialValue;
	private int currentStep;
	private int maxX;
	private File gridFolder;
	private long maxGridBlockSize;
	
	/**
	 * 
	 * @param initialValue
	 * @param maxGridHeapSize the maximum amount of heap space the grid can take up in bytes. This won't limit the total size of the grid.
	 * @param folderPath
	 * @throws Exception
	 */
	public IntAether3DAsymmetricSectionSwap(int initialValue, long maxGridHeapSize, String folderPath) throws Exception {
		if (initialValue < -858993459) {//to prevent overflow of int type
			throw new IllegalArgumentException("Initial value cannot be smaller than -858,993,459. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		maxGridBlockSize = maxGridHeapSize/2;
		gridBlockA = new SizeLimitedAnisotropicIntGrid3DBlock(0, maxGridBlockSize);
		if (gridBlockA.maxX < 6) {
			throw new IllegalArgumentException("Passed heap space limit is too small.");
		}
		gridBlockA.setValueAtPosition(0, 0, 0, initialValue);
		maxX = 4;
		currentStep = 0;
		gridFolder = new File(folderPath + File.separator + getSubFolderPath() + File.separator + GRID_FOLDER_NAME);
		if (!gridFolder.exists()) {
			gridFolder.mkdirs();
		} else {
			FileUtils.cleanDirectory(gridFolder);
		}
	}
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @param folderPath
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public IntAether3DAsymmetricSectionSwap(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		gridFolder = new File(backupPath + File.separator + GRID_FOLDER_NAME);
		if (!gridFolder.exists()) {
			throw new FileNotFoundException("Missing grid folder at '" + gridFolder.getAbsolutePath() + "'");
		}
		gridBlockA = loadGridBlock(0);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> properties = 
				(HashMap<String, Object>) Utils.deserializeFromFile(backupPath + File.separator + PROPERTIES_BACKUP_FILE_NAME);
		setPropertiesFromMap(properties);
		File gridBackupFolder = gridFolder;
		gridFolder = new File(folderPath + File.separator + getSubFolderPath() + File.separator + GRID_FOLDER_NAME);
		if (gridFolder.exists()) {
			FileUtils.cleanDirectory(gridFolder);
		}
		FileUtils.copyDirectory(gridBackupFolder, gridFolder);
		if (maxX > gridBlockA.maxX) {
			gridBlockB = loadGridBlock(gridBlockA.maxX + 1);
		}
	}

	private SizeLimitedAnisotropicIntGrid3DBlock loadGridBlockSafe(int minX) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedAnisotropicIntGrid3DBlock gridBlock = null;
		File gridBlockFile = null;
		for (int i = 0; i < files.length && !found; i++) {
			File currentFile = files[i];
			String fileName = currentFile.getName();
			int fileMinX;
			try {
				//"minX=".length() == 5
				fileMinX = Integer.parseInt(fileName.substring(5, fileName.indexOf("_")));
				if (fileMinX == minX) {
					found = true;
					gridBlockFile = currentFile;
				}
			} catch (NumberFormatException ex) {
				
			}
		}
		if (found) {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(gridBlockFile));
			gridBlock = (SizeLimitedAnisotropicIntGrid3DBlock) in.readObject();
			in.close();
		}
		return gridBlock;
	}
	
	private SizeLimitedAnisotropicIntGrid3DBlock loadGridBlock(int minX) throws IOException, ClassNotFoundException {
		SizeLimitedAnisotropicIntGrid3DBlock gridBlock = loadGridBlockSafe(minX);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minX=" + minX + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private void saveGridBlock(SizeLimitedAnisotropicIntGrid3DBlock gridBlock) throws FileNotFoundException, IOException {
		String name = "minX=" + gridBlock.minX + "_maxX=" + gridBlock.maxX + ".ser";
		String pathName = this.gridFolder.getPath() + File.separator + name;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}
	
	private SizeLimitedAnisotropicIntGrid3DBlock loadOrBuildGridBlock(int minX) throws ClassNotFoundException, IOException {		
		SizeLimitedAnisotropicIntGrid3DBlock gridBlock = loadGridBlockSafe(minX);
		if (gridBlock != null) {
			return gridBlock;
		} else {
			return new SizeLimitedAnisotropicIntGrid3DBlock(minX, maxGridBlockSize);
		}
	}

	@Override
	public boolean nextStep() throws Exception {
		triggerBeforeProcessing();
		boolean gridChanged = false;
		int[] relevantAsymmetricNeighborValues = new int[6];
		int[][] relevantAsymmetricNeighborCoords = new int[6][3];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[6];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[6];// to compensate for omitted symmetric positions		
		AnisotropicIntGrid3DSlice[] newXSlices = 
				new AnisotropicIntGrid3DSlice[] {
						null, 
						new AnisotropicIntGrid3DSlice(0), 
						new AnisotropicIntGrid3DSlice(1)};
		if (gridBlockA.minX > 0) {
			if (gridBlockB != null && gridBlockB.minX == 0) {
				SizeLimitedAnisotropicIntGrid3DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			} else {
				saveGridBlock(gridBlockA);
				gridBlockA.free();
				gridBlockA = loadGridBlock(0);
			}
		}
		int currentMaxX = maxX;//it can change during computing
		boolean anySlicePositionToppled = toppleRangeFromZeroToThree(gridBlockA, newXSlices, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts);
		gridChanged = gridChanged || anySlicePositionToppled;
		int x = 4;
		AnisotropicIntGrid3DSlice[] xSlices = new AnisotropicIntGrid3DSlice[] {
				null,
				gridBlockA.getSlice(3),
				gridBlockA.getSlice(4)};
//		newXSlices[1] = newCurrentXSlice;
//		newXSlices[2] = newGreaterXSlice;
		while (x <= currentMaxX) {
			while (x <= currentMaxX && x < gridBlockA.maxX) {
				slideGridSlices(newXSlices, new AnisotropicIntGrid3DSlice(x + 1));
				anySlicePositionToppled = toppleSliceBeyondThree(gridBlockA, x, xSlices, newXSlices, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(x - 1, newXSlices[LEFT]);
				x++;
			}
			if (x <= currentMaxX) {
				if (gridBlockB != null) {
					if (gridBlockB.minX != x + 1) {
						saveGridBlock(gridBlockB);
						gridBlockB.free();
						gridBlockB = loadOrBuildGridBlock(x + 1);
					}
				} else {
					gridBlockB = loadOrBuildGridBlock(x + 1);
				}
				slideGridSlices(newXSlices, new AnisotropicIntGrid3DSlice(x + 1));
				anySlicePositionToppled = toppleLastSliceOfBlock(gridBlockA, gridBlockB, x, xSlices, newXSlices, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(x - 1, newXSlices[LEFT]);
				x++;
				if (x <= currentMaxX) {
					slideGridSlices(newXSlices, new AnisotropicIntGrid3DSlice(x + 1));
					anySlicePositionToppled = toppleFirstSliceOfBlock(gridBlockA, gridBlockB, x, xSlices, newXSlices, 
							relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
					gridChanged = gridChanged || anySlicePositionToppled;
					gridBlockA.setSlice(x - 1, newXSlices[LEFT]);
					processGridBlock(gridBlockA);
					SizeLimitedAnisotropicIntGrid3DBlock swp = gridBlockA;
					gridBlockA = gridBlockB;
					gridBlockB = swp;
					x++;
				}
			}
		}
		gridBlockA.setSlice(currentMaxX, newXSlices[CENTER]);
		if (currentMaxX == gridBlockA.maxX) {
			processGridBlock(gridBlockA);
			gridBlockB.setSlice(currentMaxX + 1, newXSlices[RIGHT]);
			processGridBlock(gridBlockB);
		} else {
			gridBlockA.setSlice(currentMaxX + 1, newXSlices[RIGHT]);
			processGridBlock(gridBlockA);
		}
		currentStep++;
		triggerAfterProcessing();
		return gridChanged;
	}
	
	private boolean toppleRangeFromZeroToThree(SizeLimitedAnisotropicIntGrid3DBlock gridBlock,
			AnisotropicIntGrid3DSlice[] newXSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean changed = false;
		AnisotropicIntGrid3DSlice smallerXSlice = null, 
				currentXSlice = gridBlock.getSlice(0), 
				greaterXSlice = gridBlock.getSlice(1);
		AnisotropicIntGrid3DSlice newSmallerXSlice = null, 
				newCurrentXSlice = newXSlices[1], 
				newGreaterXSlice = newXSlices[2];
		// x = 0, y = 0, z = 0
		int currentValue = currentXSlice.getFromPosition(0, 0);
		int greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		if (topplePositionType1(currentValue, greaterXNeighborValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		// x = 1, y = 0, z = 0
		// smallerXSlice = currentXSlice; // not needed here
		currentXSlice = greaterXSlice;
		greaterXSlice = gridBlock.getSlice(2);
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new AnisotropicIntGrid3DSlice(3);
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		// reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		int greaterYNeighborValue = currentXSlice.getFromPosition(1, 0);
		if (topplePositionType2(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 1, y = 1, z = 0
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 0);
		int greaterZNeighborValue = currentXSlice.getFromPosition(1, 1);
		if (topplePositionType3(currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 1, y = 1, z = 1
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 1);
		if (topplePositionType4(currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		gridBlock.setSlice(0, newSmallerXSlice);
		// x = 2, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = gridBlock.getSlice(3);
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new AnisotropicIntGrid3DSlice(4);
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice.getFromPosition(0, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(0, 0);
		greaterYNeighborValue = currentXSlice.getFromPosition(1, 0);
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(1, 0);
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice.getFromPosition(2, 0);
		greaterZNeighborValue = currentXSlice.getFromPosition(1, 1);
		if (topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 1);
		smallerXNeighborValue = smallerXSlice.getFromPosition(1, 1);
		greaterYNeighborValue = currentXSlice.getFromPosition(2, 1);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 2, z = 0
		currentValue = currentXSlice.getFromPosition(2, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 0);
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		if (topplePositionType8(2, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				newXSlices)) {
			changed = true;
		}
		// x = 2, y = 2, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 1);
		smallerYNeighborValue = currentXSlice.getFromPosition(1, 1);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(2, 2);
		if (topplePositionType9(2, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 2, z = 2
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 2);
		if (topplePositionType10(2, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice)) {
			changed = true;
		}
		gridBlock.setSlice(1, newSmallerXSlice);
		// x = 3, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = gridBlock.getSlice(4);
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new AnisotropicIntGrid3DSlice(5);
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice.getFromPosition(0, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(0, 0);
		greaterYNeighborValue = currentXSlice.getFromPosition(1, 0);
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(1, 0);
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice.getFromPosition(2, 0);
		greaterZNeighborValue = currentXSlice.getFromPosition(1, 1);
		if (topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 1);
		smallerXNeighborValue = smallerXSlice.getFromPosition(1, 1);
		greaterYNeighborValue = currentXSlice.getFromPosition(2, 1);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 2, z = 0
		currentValue = currentXSlice.getFromPosition(2, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(2, 0);
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice.getFromPosition(3, 0);
		if (topplePositionType6(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 2, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 1);
		smallerXNeighborValue = smallerXSlice.getFromPosition(2, 1);
		greaterYNeighborValue = currentXSlice.getFromPosition(3, 1);
		smallerYNeighborValue = currentXSlice.getFromPosition(1, 1);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(2, 2);
		if (topplePositionType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 2, z = 2
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 2);
		smallerXNeighborValue = smallerXSlice.getFromPosition(2, 2);
		greaterYNeighborValue = currentXSlice.getFromPosition(3, 2);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 0
		currentValue = currentXSlice.getFromPosition(3, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(3, 0);
		smallerYNeighborValue = currentXSlice.getFromPosition(2, 0);
		greaterZNeighborValue = currentXSlice.getFromPosition(3, 1);
		if (topplePositionType8(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(3, 1);
		smallerYNeighborValue = currentXSlice.getFromPosition(2, 1);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(3, 2);
		if (topplePositionType9(3, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 2
		greaterXNeighborValue = greaterXSlice.getFromPosition(3, 2);
		smallerYNeighborValue = currentXSlice.getFromPosition(2, 2);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(3, 3);
		if (topplePositionType9(3, 2, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 3
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice.getFromPosition(3, 3);
		if (topplePositionType10(3, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice)) {
			changed = true;
		}
		gridBlock.setSlice(2, newSmallerXSlice);
		return changed;
	}

	private boolean toppleSliceBeyondThree(SizeLimitedAnisotropicIntGrid3DBlock gridBlock, int x, AnisotropicIntGrid3DSlice[] xSlices, 
			AnisotropicIntGrid3DSlice[] newXSlices, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondThree(gridBlock, gridBlock, gridBlock, x, xSlices, newXSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleLastSliceOfBlock(SizeLimitedAnisotropicIntGrid3DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid3DBlock rightGridBlock,
			int x, AnisotropicIntGrid3DSlice[] xSlices, AnisotropicIntGrid3DSlice[] newXSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondThree(leftGridBlock, leftGridBlock, rightGridBlock, x, xSlices, newXSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleFirstSliceOfBlock(SizeLimitedAnisotropicIntGrid3DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid3DBlock rightGridBlock,
			int x, AnisotropicIntGrid3DSlice[] xSlices, AnisotropicIntGrid3DSlice[] newXSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondThree(leftGridBlock, rightGridBlock, rightGridBlock, x, xSlices, newXSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleSliceBeyondThree(SizeLimitedAnisotropicIntGrid3DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid3DBlock centerGridBlock, 
			SizeLimitedAnisotropicIntGrid3DBlock rightGridBlock, int x, AnisotropicIntGrid3DSlice[] xSlices, AnisotropicIntGrid3DSlice[] newXSlices, 
			int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean anyToppled = false;
		int xPlusOne = x + 1;
		AnisotropicIntGrid3DSlice smallerXSlice = xSlices[1], 
				currentXSlice = xSlices[2], 
				greaterXSlice = rightGridBlock.getSlice(xPlusOne);
		AnisotropicIntGrid3DSlice newCurrentXSlice = newXSlices[1], 
				newGreaterXSlice = newXSlices[2];
		// y = 0, z = 0
		int currentValue = currentXSlice.getFromPosition(0, 0);
		int greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		int smallerXNeighborValue = smallerXSlice.getFromPosition(0, 0);
		int greaterYNeighborValue = currentXSlice.getFromPosition(1, 0);
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// y = 1, z = 0
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(1, 0);
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice.getFromPosition(2, 0);
		int greaterZNeighborValue = currentXSlice.getFromPosition(1, 1);
		if (topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// y = 1, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 1);
		smallerXNeighborValue = smallerXSlice.getFromPosition(1, 1);
		greaterYNeighborValue = currentXSlice.getFromPosition(2, 1);
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// y = 2, z = 0
		currentValue = currentXSlice.getFromPosition(2, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(2, 0);
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice.getFromPosition(3, 0);
		if (topplePositionType12(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// y = 2, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 1);
		smallerXNeighborValue = smallerXSlice.getFromPosition(2, 1);
		greaterYNeighborValue = currentXSlice.getFromPosition(3, 1);
		smallerYNeighborValue = currentXSlice.getFromPosition(1, 1);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(2, 2);
		if (topplePositionType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			anyToppled = true;
		}
		// y = 2, z = 2
		greaterXNeighborValue = greaterXSlice.getFromPosition(2, 2);
		smallerXNeighborValue = smallerXSlice.getFromPosition(2, 2);
		greaterYNeighborValue = currentXSlice.getFromPosition(3, 2);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType13(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		int y = 3, yMinusOne = 2, yPlusOne = 4;
		for (int lastY = x - 2; y <= lastY;) {
			// z = 0
			currentValue = currentXSlice.getFromPosition(y, 0);
			greaterXNeighborValue = greaterXSlice.getFromPosition(y, 0);
			smallerXNeighborValue = smallerXSlice.getFromPosition(y, 0);
			greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, 0);
			smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, 0);
			greaterZNeighborValue = currentXSlice.getFromPosition(y, 1);
			if (topplePositionType12(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// z = 1
			greaterXNeighborValue = greaterXSlice.getFromPosition(y, 1);
			smallerXNeighborValue = smallerXSlice.getFromPosition(y, 1);
			greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, 1);
			smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice.getFromPosition(y, 2);
			if (topplePositionType11(y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			int z = 2, zPlusOne = 3;
			for (int lastZ = y - 2; z <= lastZ;) {
				greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
				smallerXNeighborValue = smallerXSlice.getFromPosition(y, z);
				greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, z);
				smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, z);
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice.getFromPosition(y, zPlusOne);
				if (topplePositionType15(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 
						greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, newXSlices)) {
					anyToppled = true;
				}
				z = zPlusOne;
				zPlusOne++;
			}
			// z = y - 1
			greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
			smallerXNeighborValue = smallerXSlice.getFromPosition(y, z);
			greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, z);
			smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, z);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice.getFromPosition(y, zPlusOne);
			if (topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			// z = y
			z = zPlusOne;
			greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
			smallerXNeighborValue = smallerXSlice.getFromPosition(y, z);
			greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, z);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionType13(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}				 
			yMinusOne = y;
			y = yPlusOne;
			yPlusOne++;
		}
		// y = x - 1, z = 0
		currentValue = currentXSlice.getFromPosition(y, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(y, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(y, 0);
		greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, 0);
		smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, 0);
		greaterZNeighborValue = currentXSlice.getFromPosition(y, 1);
		if (topplePositionType6(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// y = x - 1, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(y, 1);
		smallerXNeighborValue = smallerXSlice.getFromPosition(y, 1);
		greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, 1);
		smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, 1);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(y, 2);
		if (topplePositionType11(y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			anyToppled = true;
		}
		int z = 2, zPlusOne = 3, lastZ = y - 2;
		for(; z <= lastZ;) {
			greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
			smallerXNeighborValue = smallerXSlice.getFromPosition(y, z);
			greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, z);
			smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, z);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice.getFromPosition(y, zPlusOne);
			if (topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			z = zPlusOne;
			zPlusOne++;
		}
		// y = x - 1, z = y - 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
		smallerXNeighborValue = smallerXSlice.getFromPosition(y, z);
		greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, z);
		smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, z);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(y, zPlusOne);
		if (topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			anyToppled = true;
		}
		z = zPlusOne;
		zPlusOne++;
		// y = x - 1, z = y
		greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
		smallerXNeighborValue = smallerXSlice.getFromPosition(y, z);
		greaterYNeighborValue = currentXSlice.getFromPosition(yPlusOne, z);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		yMinusOne = y;
		y = yPlusOne;
		// y = x, z = 0
		currentValue = currentXSlice.getFromPosition(y, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(y, 0);
		smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, 0);
		greaterZNeighborValue = currentXSlice.getFromPosition(y, 1);
		if (topplePositionType8(y, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// y = x, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(y, 1);
		smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, 1);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(y, 2);
		if (topplePositionType9(y, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		z = 2;
		zPlusOne = 3;
		lastZ++;
		for(; z <= lastZ; z = zPlusOne, zPlusOne++) {
			greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
			smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, z);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice.getFromPosition(y, zPlusOne);
			if (topplePositionType14(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 
					greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
		}			
		// y = x, z = y - 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
		smallerYNeighborValue = currentXSlice.getFromPosition(yMinusOne, z);
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice.getFromPosition(y, zPlusOne);
		if (topplePositionType9(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		z = zPlusOne;
		// y = x, z = y
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice.getFromPosition(y, z);
		if (topplePositionType10(y, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice)) {
			anyToppled = true;
		}
		if (anyToppled && x > maxX - 1) {
			maxX++;
		}
		xSlices[0] = smallerXSlice;
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		return anyToppled;
	}
	
	private static boolean topplePositionType1(int currentValue, int greaterXNeighborValue, AnisotropicIntGrid3DSlice newCurrentXSlice, 
			AnisotropicIntGrid3DSlice newGreaterXSlice) {
		boolean toppled = false;
		if (greaterXNeighborValue < currentValue) {
//			coverage.add(new Exception().getStackTrace()[0].getLineNumber());//debug
			int toShare = currentValue - greaterXNeighborValue;
			int share = toShare/7;
			if (share != 0) {
				toppled = true;
				newCurrentXSlice.addToPosition(0, 0, currentValue - toShare + share + toShare%7);
				newGreaterXSlice.addToPosition(0, 0, share);
			} else {
				newCurrentXSlice.addToPosition(0, 0, currentValue);
			}			
		} else {
			newCurrentXSlice.addToPosition(0, 0, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionType2(int currentValue, int greaterXNeighborValue, int smallerXNeighborValue, 
			int greaterYNeighborValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;// this is the index of the new slice: 0->newSmallerXSlice, 1->newCurrentXSlice, 2->newGreaterXSlice
			nc[1] = 0;// this is the actual y coordinate
			nc[2] = 0;// this is the actual z coordinate
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 6;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType3(int currentValue, int greaterXNeighborValue, int smallerYNeighborValue, 
			int greaterZNeighborValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 1, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType4(int currentValue, int greaterXNeighborValue, int smallerZNeighborValue, 
			AnisotropicIntGrid3DSlice newCurrentXSlice, AnisotropicIntGrid3DSlice newGreaterXSlice) {
		boolean toppled = false;
		if (smallerZNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerZNeighborValue == greaterXNeighborValue) {
					// gx = sz < current
					int toShare = currentValue - greaterXNeighborValue; 
					int share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 0, share + share);// one more for the symmetric position at the other side
					newCurrentXSlice.addToPosition(1, 1, currentValue - toShare + share + toShare%7);
					newGreaterXSlice.addToPosition(1, 1, share);
				} else if (smallerZNeighborValue < greaterXNeighborValue) {
					// sz < gx < current
					int toShare = currentValue - greaterXNeighborValue; 
					int share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 0, share + share);
					newGreaterXSlice.addToPosition(1, 1, share);
					int currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - smallerZNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 0, share + share);
					newCurrentXSlice.addToPosition(1, 1, currentRemainingValue - toShare + share + toShare%4);
				} else {
					// gx < sz < current
					int toShare = currentValue - smallerZNeighborValue; 
					int share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 0, share + share);
					newGreaterXSlice.addToPosition(1, 1, share);
					int currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 1, currentRemainingValue - toShare + share + toShare%4);
					newGreaterXSlice.addToPosition(1, 1, share);
				}
			} else {
				// sz < current <= gx
				int toShare = currentValue - smallerZNeighborValue; 
				int share = toShare/4;
				if (share != 0) {
					toppled = true;
				}
				newCurrentXSlice.addToPosition(1, 0, share + share);
				newCurrentXSlice.addToPosition(1, 1, currentValue - toShare + share + toShare%4);
			}
		} else {
			if (greaterXNeighborValue < currentValue) {
				// gx < current <= sz
				int toShare = currentValue - greaterXNeighborValue; 
				int share = toShare/4;
				if (share != 0) {
					toppled = true;
				}
				newCurrentXSlice.addToPosition(1, 1, currentValue - toShare + share + toShare%4);
				newGreaterXSlice.addToPosition(1, 1, share);
			} else {
				newCurrentXSlice.addToPosition(1, 1, currentValue);
			}
		}
		return toppled;
	}

	private static boolean topplePositionType5(int currentValue, int greaterXNeighborValue, 
			int smallerXNeighborValue, int greaterYNeighborValue, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts,
			AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	};

	private static boolean topplePositionType6(int y, int currentValue, int gXValue, int sXValue, 
			int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, 
			int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType7(int coord, int currentValue, int gXValue, int sXValue, 
			int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, 
			int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType8(int y, int currentValue, int greaterXNeighborValue, int smallerYNeighborValue, 
			int greaterZNeighborValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid3DSlice[] newXSlices ) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType9(int y, int z, int currentValue, int gXValue, int sYValue, 
			int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, 
			int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType10(int coord, int currentValue, int greaterXNeighborValue, 
			int smallerZNeighborValue, AnisotropicIntGrid3DSlice newCurrentXSlice, AnisotropicIntGrid3DSlice newGreaterXSlice) {
		boolean toppled = false;
		if (smallerZNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerZNeighborValue == greaterXNeighborValue) {
					// gx = sz < current
					int toShare = currentValue - greaterXNeighborValue; 
					int share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coord - 1, share);
					newCurrentXSlice.addToPosition(coord, coord, currentValue - toShare + share + toShare%7);
					newGreaterXSlice.addToPosition(coord, coord, share);
				} else if (smallerZNeighborValue < greaterXNeighborValue) {
					// sz < gx < current
					int coordMinusOne = coord - 1;
					int toShare = currentValue - greaterXNeighborValue; 
					int share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coordMinusOne, share);
					newGreaterXSlice.addToPosition(coord, coord, share);
					int currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - smallerZNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coordMinusOne, share);
					newCurrentXSlice.addToPosition(coord, coord, currentRemainingValue - toShare + share + toShare%4);
				} else {
					// gx < sz < current
					int toShare = currentValue - smallerZNeighborValue; 
					int share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coord - 1, share);
					newGreaterXSlice.addToPosition(coord, coord, share);
					int currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coord, currentRemainingValue - toShare + share + toShare%4);
					newGreaterXSlice.addToPosition(coord, coord, share);
				}
			} else {
				// sz < current <= gx
				int toShare = currentValue - smallerZNeighborValue; 
				int share = toShare/4;
				if (share != 0) {
					toppled = true;
				}
				newCurrentXSlice.addToPosition(coord, coord - 1, share);
				newCurrentXSlice.addToPosition(coord, coord, currentValue - toShare + share + toShare%4);
			}
		} else {
			if (greaterXNeighborValue < currentValue) {
				// gx < current <= sz
				int toShare = currentValue - greaterXNeighborValue; 
				int share = toShare/4;
				if (share != 0) {
					toppled = true;
				}
				newCurrentXSlice.addToPosition(coord, coord, currentValue - toShare + share + toShare%4);
				newGreaterXSlice.addToPosition(coord, coord, share);
			} else {
				newCurrentXSlice.addToPosition(coord, coord, currentValue);
			}
		}
		return toppled;
	}

	private static boolean topplePositionType11(int y, int z, int currentValue, int gXValue, int sXValue, 
			int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, 
			int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantNeighborValues, int[][] relevantNeighborCoords, 
			int[] relevantNeighborShareMultipliers, 
			AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantNeighborCount = 0;
		if (gXValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount ] = gXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z + 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType12(int y, int currentValue, int greaterXNeighborValue, 
			int smallerXNeighborValue, int greaterYNeighborValue, int smallerYNeighborValue, 
			int greaterZNeighborValue, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, 
			AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount ] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType13(int coord, int currentValue, int greaterXNeighborValue, 
			int smallerXNeighborValue, int greaterYNeighborValue, int smallerZNeighborValue, 
			int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType14(int y, int z, int currentValue, int greaterXNeighborValue, 
			int smallerYNeighborValue,	int greaterZNeighborValue, int smallerZNeighborValue, 
			int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType15(int y, int z, int currentValue, int greaterXNeighborValue, 
			int smallerXNeighborValue,	int greaterYNeighborValue, int smallerYNeighborValue, 
			int greaterZNeighborValue, int smallerZNeighborValue, int[] relevantNeighborValues, 
			int[][] relevantNeighborCoords, AnisotropicIntGrid3DSlice[] newXSlices) {
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z + 1;
			relevantNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, relevantNeighborCoords, 
				relevantNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean topplePosition(AnisotropicIntGrid3DSlice[] newXSlices, int value, int y, int z, int[] neighborValues,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, 
						neighborCoords, 3);
				break;
			case 2:
				int n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					newXSlices[1].addToPosition(y, z, value - toShare + share + toShare%3);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%2);
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%2);
				}				
				break;
			case 1:
				int toShare = value - neighborValues[0];
				int share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]].addToPosition(nc[1], nc[2], share);
				}
				// no break
			case 0:
				newXSlices[1].addToPosition(y, z, value);
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, neighborCoords, 
						neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicIntGrid3DSlice[] newXSlices, int value, int y, int z, 
			int[] neighborValues, int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		int neighborValue = neighborValues[0];
		int toShare = value - neighborValue;
		int share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[j];
				newXSlices[nc[0]].addToPosition(nc[1], nc[2], share);
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[j];
						newXSlices[nc[0]].addToPosition(nc[1], nc[2], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1].addToPosition(y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(AnisotropicIntGrid3DSlice[] newXSlices, int value, int y, int z, int[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(asymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				int n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
				int shareCount = neighborCount + 1;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					newXSlices[1].addToPosition(y, z, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				int toShare = value - asymmetricNeighborValues[0];
				int share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					int[] nc = asymmetricNeighborCoords[0];
					newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * asymmetricNeighborShareMultipliers[0]);
				}
				// no break
			case 0:
				newXSlices[1].addToPosition(y, z, value);
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicIntGrid3DSlice[] newXSlices, int value, int y, int z, int[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		int neighborValue = asymmetricNeighborValues[0];
		int toShare = value - neighborValue;
		int share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[j];
				newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * asymmetricNeighborShareMultipliers[j]);
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[0];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[j];
						newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * asymmetricNeighborShareMultipliers[j]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newXSlices[1].addToPosition(y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(AnisotropicIntGrid3DSlice[] newXSlices, int value, int y, int z, int[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords, neighborShareMultipliers);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, 
						neighborCoords, neighborShareMultipliers, 3);
				break;
			case 2:
				int n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					newXSlices[1].addToPosition(y, z, value - toShare + share + toShare%3);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%2);
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%2);
				}				
				break;
			case 1:
				int toShare = value - neighborValues[0];
				int share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * neighborShareMultipliers[0]);
				}
				// no break
			case 0:
				newXSlices[1].addToPosition(y, z, value);
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords, 
						neighborShareMultipliers);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicIntGrid3DSlice[] newXSlices, int value, int y, int z, int[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		int neighborValue = neighborValues[0];
		int toShare = value - neighborValue;
		int share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[j];
				newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * neighborShareMultipliers[j]);
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[j];
						newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * neighborShareMultipliers[j]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1].addToPosition(y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(AnisotropicIntGrid3DSlice[] newXSlices, int value, int y, int z, int[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(asymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				int n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int shareCount = neighborCount + 1;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					newXSlices[1].addToPosition(y, z, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				int toShare = value - asymmetricNeighborValues[0];
				int share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					int[] nc = asymmetricNeighborCoords[0];
					newXSlices[nc[0]].addToPosition(nc[1], nc[2], share);
				}
				// no break
			case 0:
				newXSlices[1].addToPosition(y, z, value);
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicIntGrid3DSlice[] newXSlices, int value, int y, int z, int[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		int neighborValue = asymmetricNeighborValues[0];
		int toShare = value - neighborValue;
		int share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[j];
				newXSlices[nc[0]].addToPosition(nc[1], nc[2], share);
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[0];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[j];
						newXSlices[nc[0]].addToPosition(nc[1], nc[2], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newXSlices[1].addToPosition(y, z, value);
		return toppled;
	}

	private void processGridBlock(SizeLimitedAnisotropicIntGrid3DBlock block) throws Exception {
		if (block.minX <= maxX) {
			IntGrid3D subBlock = null;
			if (block.maxX > maxX) {
				subBlock = new IntSubGrid3DWithXBounds(block, block.minX, maxX);
			} else {
				subBlock = block;
			}
			triggerProcessGridBlock(subBlock);
		}
	}
	
	@Override
	public void processGrid() throws Exception {
		triggerBeforeProcessing();
		if (gridBlockB == null) { //one block
			processGridBlock(gridBlockA);
		} else {
			if (gridBlockA.minX < gridBlockB.minX) {
				SizeLimitedAnisotropicIntGrid3DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			}
			if (gridBlockB.minX == 0 && gridBlockA.maxX >= maxX
					&& gridBlockB.maxX == gridBlockA.minX - 1) {//two blocks
				processGridBlock(gridBlockB);
				processGridBlock(gridBlockA);
			} else { //more than two blocks
				//I keep the one closest to zero (in case it can be reused when computing the next step)
				//and save the other
				saveGridBlock(gridBlockA);
				if (gridBlockB.minX > 0) {
					gridBlockA.free();
					gridBlockA = loadGridBlock(0);
					processGridBlock(gridBlockA);
					while (gridBlockA.maxX < gridBlockB.minX - 1) {
						int nextX = gridBlockA.maxX + 1;
						gridBlockA.free();
						gridBlockA = loadGridBlock(nextX);
						processGridBlock(gridBlockA);
					}
				}
				processGridBlock(gridBlockB);
				int previousMaxX = gridBlockB.maxX;			
				while (previousMaxX < maxX) {
					int nextX = previousMaxX + 1;
					gridBlockA.free();
					gridBlockA = loadGridBlock(nextX);
					processGridBlock(gridBlockA);
					previousMaxX = gridBlockA.maxX;
				}
			}			
		}
		triggerAfterProcessing();
	}

	private void slideGridSlices(AnisotropicIntGrid3DSlice[] newGridSlices, AnisotropicIntGrid3DSlice newSlice) {
		newGridSlices[LEFT] = newGridSlices[CENTER];
		newGridSlices[CENTER] = newGridSlices[RIGHT];
		newGridSlices[RIGHT] = newSlice;
	}

	@Override
	public String getName() {
		return "Aether3D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/asymmetric_section";
	}

	@Override
	public long getStep() {
		return currentStep;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		String backupFolderPath = backupPath + File.separator + backupName;
		File backupFolder = new File(backupFolderPath);
		if (backupFolder.exists()) {
			FileUtils.cleanDirectory(backupFolder);
		} else {
			backupFolder.mkdirs();
		}		
		if (gridBlockA != null) {
			saveGridBlock(gridBlockA);
		}
		if (gridBlockB != null) {
			saveGridBlock(gridBlockB);
		}
		File gridBackupFolder = new File(backupFolderPath + File.separator + GRID_FOLDER_NAME);
	    FileUtils.copyDirectory(gridFolder, gridBackupFolder);
		HashMap<String, Object> properties = getPropertiesMap();
		Utils.serializeToFile(properties, backupFolderPath, PROPERTIES_BACKUP_FILE_NAME);
	}
	
	private HashMap<String, Object> getPropertiesMap() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("initialValue", initialValue);
		properties.put("currentStep", currentStep);
		properties.put("maxX", maxX);
		properties.put("maxGridBlockSize", maxGridBlockSize);
		return properties;
	}
	
	private void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (int) properties.get("initialValue");
		currentStep = (int) properties.get("currentStep");
		maxX = (int) properties.get("maxX");
		maxGridBlockSize = (long) properties.get("maxGridBlockSize");
	}
	
	@Override
	public int getMinX() {
		return 0;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}
	
	@Override
	public int getMinY() {
		return 0;
	}
	
	@Override
	public int getMaxY() {
		return maxX;
	}
	
	@Override
	public int getMinZ() {
		return 0;
	}
	
	@Override
	public int getMaxZ() {
		return maxX;
	}
	
	@Override
	public int getMinXAtY(int y) {
		return y;
	}

	@Override
	public int getMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getMinX(int y, int z) {
		return Math.max(y, z);
	}

	@Override
	public int getMaxXAtY(int y) {
		return maxX;
	}

	@Override
	public int getMaxXAtZ(int z) {
		return maxX;
	}

	@Override
	public int getMaxX(int y, int z) {
		return maxX;
	}

	@Override
	public int getMinYAtX(int x) {
		return 0;
	}

	@Override
	public int getMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getMinY(int x, int z) {
		return z;
	}

	@Override
	public int getMaxYAtX(int x) {
		return x;
	}

	@Override
	public int getMaxYAtZ(int z) {
		return maxX;
	}

	@Override
	public int getMaxY(int x, int z) {
		return x;
	}

	@Override
	public int getMinZAtX(int x) {
		return 0;
	}

	@Override
	public int getMinZAtY(int y) {
		return 0;
	}

	@Override
	public int getMinZ(int x, int y) {
		return 0;
	}

	@Override
	public int getMaxZAtX(int x) {
		return x;
	}

	@Override
	public int getMaxZAtY(int y) {
		return y;
	}

	@Override
	public int getMaxZ(int x, int y) {
		return y;
	}

}