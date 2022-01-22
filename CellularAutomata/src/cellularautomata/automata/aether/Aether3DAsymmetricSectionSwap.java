/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
package cellularautomata.automata.aether;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

import cellularautomata.Utils;
import cellularautomata.model3d.ActionableModel3D;
import cellularautomata.model3d.AnisotropicModel3DA;
import cellularautomata.model3d.AnisotropicLongModel3DSlice;
import cellularautomata.model3d.ImmutableLongModel3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.LongSubModel3D;
import cellularautomata.model3d.LongSubModel3DWithXBounds;
import cellularautomata.model3d.SizeLimitedAnisotropicLongModel3DBlock;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class Aether3DAsymmetricSectionSwap extends ActionableModel3D<LongModel3D> implements AnisotropicModel3DA {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -3689348814741910323L;

	private static final String PROPERTIES_BACKUP_FILE_NAME = "properties.ser";
	private static final String GRID_FOLDER_NAME = "grid";
	
	private static final byte LEFT = 0;
	private static final byte CENTER = 1;
	private static final byte RIGHT = 2;
	
	private SizeLimitedAnisotropicLongModel3DBlock gridBlockA;
	private SizeLimitedAnisotropicLongModel3DBlock gridBlockB;
	private long initialValue;
	private long step;
	private int maxX;
	private File gridFolder;
	private File readWriteGridFolder;
	private long maxGridBlockSize;
	private boolean readOnlyMode = false;
	
	/**
	 * 
	 * @param initialValue
	 * @param maxGridHeapSize the maximum amount of heap space the grid can take up in bytes. This won't limit the total size of the grid.
	 * @param folderPath
	 * @throws Exception
	 */
	public Aether3DAsymmetricSectionSwap(long initialValue, long maxGridHeapSize, String folderPath) throws Exception {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		maxGridBlockSize = maxGridHeapSize/2;
		gridBlockA = new SizeLimitedAnisotropicLongModel3DBlock(0, maxGridBlockSize);
		if (gridBlockA.maxX < 6) {
			throw new IllegalArgumentException("Passed heap space limit is too small.");
		}
		gridBlockA.setValueAtPosition(0, 0, 0, initialValue);
		maxX = 4;
		step = 0;
		gridFolder = new File(folderPath + File.separator + getSubfolderPath() + File.separator + GRID_FOLDER_NAME);
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
	public Aether3DAsymmetricSectionSwap(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		readOnlyMode = true;
		gridFolder = new File(backupPath + File.separator + GRID_FOLDER_NAME);
		if (!gridFolder.exists()) {
			throw new FileNotFoundException("Missing grid folder at '" + gridFolder.getAbsolutePath() + "'");
		}
		gridBlockA = loadGridBlock(0);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> properties = 
				(HashMap<String, Object>) Utils.deserializeFromFile(backupPath + File.separator + PROPERTIES_BACKUP_FILE_NAME);
		setPropertiesFromMap(properties);
		if (maxX > gridBlockA.maxX) {
			gridBlockB = loadGridBlock(gridBlockA.maxX + 1);
		}
		readWriteGridFolder = new File(folderPath + File.separator + getSubfolderPath() + File.separator + GRID_FOLDER_NAME);
	}

	private SizeLimitedAnisotropicLongModel3DBlock loadGridBlockSafe(int minX) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedAnisotropicLongModel3DBlock gridBlock = null;
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
			gridBlock = (SizeLimitedAnisotropicLongModel3DBlock) in.readObject();
			in.close();
		}
		return gridBlock;
	}
	
	private SizeLimitedAnisotropicLongModel3DBlock loadGridBlock(int minX) throws IOException, ClassNotFoundException {
		SizeLimitedAnisotropicLongModel3DBlock gridBlock = loadGridBlockSafe(minX);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minX=" + minX + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private void saveGridBlock(SizeLimitedAnisotropicLongModel3DBlock gridBlock) throws FileNotFoundException, IOException {
		String name = "minX=" + gridBlock.minX + "_maxX=" + gridBlock.maxX + ".ser";
		String pathName = this.gridFolder.getPath() + File.separator + name;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}
	
	private SizeLimitedAnisotropicLongModel3DBlock loadOrBuildGridBlock(int minX) throws ClassNotFoundException, IOException {		
		SizeLimitedAnisotropicLongModel3DBlock gridBlock = loadGridBlockSafe(minX);
		if (gridBlock != null) {
			return gridBlock;
		} else {
			return new SizeLimitedAnisotropicLongModel3DBlock(minX, maxGridBlockSize);
		}
	}

	@Override
	public boolean nextStep() throws Exception {
		step++;
		if (readOnlyMode) {
			readOnlyMode = false;
			if (readWriteGridFolder.exists()) {
				FileUtils.cleanDirectory(readWriteGridFolder);
			}
			FileUtils.copyDirectory(gridFolder, readWriteGridFolder);
			gridFolder = readWriteGridFolder;
		}
		triggerBeforeProcessing();
		boolean gridChanged = false;
		long[] relevantAsymmetricNeighborValues = new long[6];
		int[] sortedNeighborsIndexes = new int[6];
		int[][] relevantAsymmetricNeighborCoords = new int[6][3];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[6];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[6];// to compensate for omitted symmetric positions		
		AnisotropicLongModel3DSlice[] newXSlices = 
				new AnisotropicLongModel3DSlice[] {
						null, 
						new AnisotropicLongModel3DSlice(0), 
						new AnisotropicLongModel3DSlice(1)};
		if (gridBlockA.minX > 0) {
			if (gridBlockB != null && gridBlockB.minX == 0) {
				SizeLimitedAnisotropicLongModel3DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			} else {
				saveGridBlock(gridBlockA);
				gridBlockA.free();
				gridBlockA = loadGridBlock(0);
			}
		}
		int currentMaxX = maxX;//it can change during computing
		boolean anySlicePositionToppled = toppleRangeFromZeroToThree(gridBlockA, newXSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts);
		gridChanged = gridChanged || anySlicePositionToppled;
		int x = 4;
		AnisotropicLongModel3DSlice[] xSlices = new AnisotropicLongModel3DSlice[] {
				null,
				gridBlockA.getSlice(3),
				gridBlockA.getSlice(4)};
		while (x <= currentMaxX) {
			while (x <= currentMaxX && x < gridBlockA.maxX) {
				slideGridSlices(newXSlices, new AnisotropicLongModel3DSlice(x + 1));
				anySlicePositionToppled = toppleSliceBeyondThree(gridBlockA, x, xSlices, newXSlices, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				slideGridSlices(newXSlices, new AnisotropicLongModel3DSlice(x + 1));
				anySlicePositionToppled = toppleLastSliceOfBlock(gridBlockA, gridBlockB, x, xSlices, newXSlices, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(x - 1, newXSlices[LEFT]);
				x++;
				if (x <= currentMaxX) {
					slideGridSlices(newXSlices, new AnisotropicLongModel3DSlice(x + 1));
					anySlicePositionToppled = toppleFirstSliceOfBlock(gridBlockA, gridBlockB, x, xSlices, newXSlices, 
							relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
					gridChanged = gridChanged || anySlicePositionToppled;
					gridBlockA.setSlice(x - 1, newXSlices[LEFT]);
					processGridBlock(gridBlockA);
					SizeLimitedAnisotropicLongModel3DBlock swp = gridBlockA;
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
		triggerAfterProcessing();
		return gridChanged;
	}
	
	private boolean toppleRangeFromZeroToThree(SizeLimitedAnisotropicLongModel3DBlock gridBlock,
			AnisotropicLongModel3DSlice[] newXSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean changed = false;
		AnisotropicLongModel3DSlice smallerXSlice = null, 
				currentXSlice = gridBlock.getSlice(0), 
				greaterXSlice = gridBlock.getSlice(1);
		AnisotropicLongModel3DSlice newSmallerXSlice = null, 
				newCurrentXSlice = newXSlices[1], 
				newGreaterXSlice = newXSlices[2];
		// x = 0, y = 0, z = 0
		long currentValue = currentXSlice.getFromPosition(0, 0);
		long greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		if (topplePositionType1(currentValue, greaterXNeighborValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		// x = 1, y = 0, z = 0
		// smallerXSlice = currentXSlice; // not needed here
		currentXSlice = greaterXSlice;
		greaterXSlice = gridBlock.getSlice(2);
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new AnisotropicLongModel3DSlice(3);
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		// reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		long greaterYNeighborValue = currentXSlice.getFromPosition(1, 0);
		if (topplePositionType2(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 1, y = 1, z = 0
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 0);
		long greaterZNeighborValue = currentXSlice.getFromPosition(1, 1);
		if (topplePositionType3(currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 1, y = 1, z = 1
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
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
		newGreaterXSlice = new AnisotropicLongModel3DSlice(4);
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice.getFromPosition(0, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(0, 0);
		greaterYNeighborValue = currentXSlice.getFromPosition(1, 0);
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
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
				greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
		newGreaterXSlice = new AnisotropicLongModel3DSlice(5);
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice.getFromPosition(0, 0);
		greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(0, 0);
		greaterYNeighborValue = currentXSlice.getFromPosition(1, 0);
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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

	private boolean toppleSliceBeyondThree(SizeLimitedAnisotropicLongModel3DBlock gridBlock, int x, AnisotropicLongModel3DSlice[] xSlices, 
			AnisotropicLongModel3DSlice[] newXSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondThree(gridBlock, gridBlock, gridBlock, x, xSlices, newXSlices, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleLastSliceOfBlock(SizeLimitedAnisotropicLongModel3DBlock leftGridBlock, SizeLimitedAnisotropicLongModel3DBlock rightGridBlock,
			int x, AnisotropicLongModel3DSlice[] xSlices, AnisotropicLongModel3DSlice[] newXSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondThree(leftGridBlock, leftGridBlock, rightGridBlock, x, xSlices, newXSlices, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleFirstSliceOfBlock(SizeLimitedAnisotropicLongModel3DBlock leftGridBlock, SizeLimitedAnisotropicLongModel3DBlock rightGridBlock,
			int x, AnisotropicLongModel3DSlice[] xSlices, AnisotropicLongModel3DSlice[] newXSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondThree(leftGridBlock, rightGridBlock, rightGridBlock, x, xSlices, newXSlices, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleSliceBeyondThree(SizeLimitedAnisotropicLongModel3DBlock leftGridBlock, SizeLimitedAnisotropicLongModel3DBlock centerGridBlock, 
			SizeLimitedAnisotropicLongModel3DBlock rightGridBlock, int x, AnisotropicLongModel3DSlice[] xSlices, AnisotropicLongModel3DSlice[] newXSlices, 
			long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean anyToppled = false;
		int xPlusOne = x + 1;
		AnisotropicLongModel3DSlice smallerXSlice = xSlices[1], 
				currentXSlice = xSlices[2], 
				greaterXSlice = rightGridBlock.getSlice(xPlusOne);
		AnisotropicLongModel3DSlice newCurrentXSlice = newXSlices[1], 
				newGreaterXSlice = newXSlices[2];
		// y = 0, z = 0
		long currentValue = currentXSlice.getFromPosition(0, 0);
		long greaterXNeighborValue = greaterXSlice.getFromPosition(0, 0);
		long smallerXNeighborValue = smallerXSlice.getFromPosition(0, 0);
		long greaterYNeighborValue = currentXSlice.getFromPosition(1, 0);
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// y = 1, z = 0
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 0);
		smallerXNeighborValue = smallerXSlice.getFromPosition(1, 0);
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice.getFromPosition(2, 0);
		long greaterZNeighborValue = currentXSlice.getFromPosition(1, 1);
		if (topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// y = 1, z = 1
		greaterXNeighborValue = greaterXSlice.getFromPosition(1, 1);
		smallerXNeighborValue = smallerXSlice.getFromPosition(1, 1);
		greaterYNeighborValue = currentXSlice.getFromPosition(2, 1);
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newXSlices)) {
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
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
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
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
					greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
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
	
	private static boolean topplePositionType1(long currentValue, long greaterXNeighborValue, AnisotropicLongModel3DSlice newCurrentXSlice, 
			AnisotropicLongModel3DSlice newGreaterXSlice) {
		boolean toppled = false;
		if (greaterXNeighborValue < currentValue) {
//			coverage.add(new Exception().getStackTrace()[0].getLineNumber());//debug
			long toShare = currentValue - greaterXNeighborValue;
			long share = toShare/7;
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

	private static boolean topplePositionType2(long currentValue, long greaterXNeighborValue, long smallerXNeighborValue, 
			long greaterYNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType3(long currentValue, long greaterXNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, 1, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType4(long currentValue, long greaterXNeighborValue, long smallerZNeighborValue, 
			AnisotropicLongModel3DSlice newCurrentXSlice, AnisotropicLongModel3DSlice newGreaterXSlice) {
		boolean toppled = false;
		if (smallerZNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerZNeighborValue == greaterXNeighborValue) {
					// gx = sz < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 0, share + share);// one more for the symmetric position at the other side
					newCurrentXSlice.addToPosition(1, 1, currentValue - toShare + share + toShare%7);
					newGreaterXSlice.addToPosition(1, 1, share);
				} else if (smallerZNeighborValue < greaterXNeighborValue) {
					// sz < gx < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 0, share + share);
					newGreaterXSlice.addToPosition(1, 1, share);
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - smallerZNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 0, share + share);
					newCurrentXSlice.addToPosition(1, 1, currentRemainingValue - toShare + share + toShare%4);
				} else {
					// gx < sz < current
					long toShare = currentValue - smallerZNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(1, 0, share + share);
					newGreaterXSlice.addToPosition(1, 1, share);
					long currentRemainingValue = currentValue - 6*share;
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
				long toShare = currentValue - smallerZNeighborValue; 
				long share = toShare/4;
				if (share != 0) {
					toppled = true;
				}
				newCurrentXSlice.addToPosition(1, 0, share + share);
				newCurrentXSlice.addToPosition(1, 1, currentValue - toShare + share + toShare%4);
			}
		} else {
			if (greaterXNeighborValue < currentValue) {
				// gx < current <= sz
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/4;
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

	private static boolean topplePositionType5(long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue, long greaterYNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts,
			AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	};

	private static boolean topplePositionType6(int y, long currentValue, long gXValue, long sXValue, 
			int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, 
			long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType7(int coord, long currentValue, long gXValue, long sXValue, 
			int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, 
			int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType8(int y, long currentValue, long greaterXNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicLongModel3DSlice[] newXSlices ) {
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
		return topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType9(int y, int z, long currentValue, long gXValue, long sYValue, 
			int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, 
			long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType10(int coord, long currentValue, long greaterXNeighborValue, 
			long smallerZNeighborValue, AnisotropicLongModel3DSlice newCurrentXSlice, AnisotropicLongModel3DSlice newGreaterXSlice) {
		boolean toppled = false;
		if (smallerZNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerZNeighborValue == greaterXNeighborValue) {
					// gx = sz < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coord - 1, share);
					newCurrentXSlice.addToPosition(coord, coord, currentValue - toShare + share + toShare%7);
					newGreaterXSlice.addToPosition(coord, coord, share);
				} else if (smallerZNeighborValue < greaterXNeighborValue) {
					// sz < gx < current
					int coordMinusOne = coord - 1;
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coordMinusOne, share);
					newGreaterXSlice.addToPosition(coord, coord, share);
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - smallerZNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coordMinusOne, share);
					newCurrentXSlice.addToPosition(coord, coord, currentRemainingValue - toShare + share + toShare%4);
				} else {
					// gx < sz < current
					long toShare = currentValue - smallerZNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					newCurrentXSlice.addToPosition(coord, coord - 1, share);
					newGreaterXSlice.addToPosition(coord, coord, share);
					long currentRemainingValue = currentValue - 6*share;
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
				long toShare = currentValue - smallerZNeighborValue; 
				long share = toShare/4;
				if (share != 0) {
					toppled = true;
				}
				newCurrentXSlice.addToPosition(coord, coord - 1, share);
				newCurrentXSlice.addToPosition(coord, coord, currentValue - toShare + share + toShare%4);
			}
		} else {
			if (greaterXNeighborValue < currentValue) {
				// gx < current <= sz
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/4;
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

	private static boolean topplePositionType11(int y, int z, long currentValue, long gXValue, long sXValue, 
			int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, 
			long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantNeighborCoords, 
			int[] relevantNeighborShareMultipliers, 
			AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionType12(int y, long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue, long greaterYNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, 
			AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType13(int coord, long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue, long greaterYNeighborValue, long smallerZNeighborValue, 
			long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType14(int y, int z, long currentValue, long greaterXNeighborValue, 
			long smallerYNeighborValue,	long greaterZNeighborValue, long smallerZNeighborValue, 
			long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType15(int y, int z, long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue,	long greaterYNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long smallerZNeighborValue, long[] relevantNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantNeighborCoords, AnisotropicLongModel3DSlice[] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static boolean topplePosition(AnisotropicLongModel3DSlice[] newXSlices, long value, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, 3);
				break;
			case 2:
				long n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					newXSlices[1].addToPosition(y, z, value - toShare + share + toShare%3);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%2);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					long currentRemainingValue = value - neighborCount*share;
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
				long toShare = value - neighborValues[0];
				long share = toShare/2;
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
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicLongModel3DSlice[] newXSlices, long value, int y, int z, 
			long[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		long neighborValue = neighborValues[0];
		long toShare = value - neighborValue;
		long share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
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
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
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
	
	private static boolean topplePosition(AnisotropicLongModel3DSlice[] newXSlices, long value, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				long n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
				int shareCount = neighborCount + 1;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					newXSlices[1].addToPosition(y, z, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
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
				long toShare = value - asymmetricNeighborValues[0];
				long share = toShare/shareCount;
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
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicLongModel3DSlice[] newXSlices, long value, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		long neighborValue = asymmetricNeighborValues[0];
		long toShare = value - neighborValue;
		long share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
				newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
						newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1].addToPosition(y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(AnisotropicLongModel3DSlice[] newXSlices, long value, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, neighborShareMultipliers, 3);
				break;
			case 2:
				long n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					newXSlices[1].addToPosition(y, z, value - toShare + share + toShare%3);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%2);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share*n0Mult);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share*n1Mult);
					long currentRemainingValue = value - neighborCount*share;
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
				long toShare = value - neighborValues[0];
				long share = toShare/2;
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
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicLongModel3DSlice[] newXSlices, long value, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		long neighborValue = neighborValues[0];
		long toShare = value - neighborValue;
		long share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
				newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
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
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
						newXSlices[nc[0]].addToPosition(nc[1], nc[2], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1].addToPosition(y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(AnisotropicLongModel3DSlice[] newXSlices, long value, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				long n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int shareCount = neighborCount + 1;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					newXSlices[1].addToPosition(y, z, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[1].addToPosition(y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], share);
					newXSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
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
				long toShare = value - asymmetricNeighborValues[0];
				long share = toShare/shareCount;
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
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicLongModel3DSlice[] newXSlices, long value, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		long neighborValue = asymmetricNeighborValues[0];
		long toShare = value - neighborValue;
		long share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
				newXSlices[nc[0]].addToPosition(nc[1], nc[2], share);
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
						newXSlices[nc[0]].addToPosition(nc[1], nc[2], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1].addToPosition(y, z, value);
		return toppled;
	}

	private void processGridBlock(SizeLimitedAnisotropicLongModel3DBlock block) throws Exception {
		if (block.minX <= maxX) {
			LongModel3D subBlock = null;
			int maxY = getMaxY();
			int maxZ = getMaxZ();
			if (block.maxX > maxX) {
				if (block.maxX > maxY || block.maxX > maxZ) {
					subBlock = new LongSubModel3D<LongModel3D>(block, block.minX, maxX, 0, maxY, 0, maxZ);
				} else {
					subBlock = new LongSubModel3DWithXBounds(block, block.minX, maxX);
				}
			} else {
				if (block.maxX > maxY || block.maxX > maxZ) {
					subBlock = new LongSubModel3D<LongModel3D>(block, block.minX, block.maxX, 0, maxY, 0, maxZ);
				} else {
					subBlock = new ImmutableLongModel3D(block);
				}
			}
			triggerProcessModelBlock(subBlock);
		}
	}
	
	@Override
	public void processModel() throws Exception {
		triggerBeforeProcessing();
		if (gridBlockB == null) { //one block
			processGridBlock(gridBlockA);
		} else {
			if (gridBlockA.minX < gridBlockB.minX) {
				SizeLimitedAnisotropicLongModel3DBlock swp = gridBlockA;
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
				if (!readOnlyMode)
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

	private void slideGridSlices(AnisotropicLongModel3DSlice[] newGridSlices, AnisotropicLongModel3DSlice newSlice) {
		newGridSlices[LEFT] = newGridSlices[CENTER];
		newGridSlices[CENTER] = newGridSlices[RIGHT];
		newGridSlices[RIGHT] = newSlice;
	}

	@Override
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/" + initialValue + "/asymmetric_section";
	}

	@Override
	public long getStep() {
		return step;
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
		if (!readOnlyMode) {
			if (gridBlockA != null) {
				saveGridBlock(gridBlockA);
			}
			if (gridBlockB != null) {
				saveGridBlock(gridBlockB);
			}			
		}
		File gridBackupFolder = new File(backupFolderPath + File.separator + GRID_FOLDER_NAME);
	    FileUtils.copyDirectory(gridFolder, gridBackupFolder);
		HashMap<String, Object> properties = getPropertiesMap();
		Utils.serializeToFile(properties, backupFolderPath, PROPERTIES_BACKUP_FILE_NAME);
	}
	
	private HashMap<String, Object> getPropertiesMap() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("initialValue", initialValue);
		properties.put("step", step);
		properties.put("maxX", maxX);
		properties.put("maxGridBlockSize", maxGridBlockSize);
		return properties;
	}
	
	private void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (long) properties.get("initialValue");
		step = (long) properties.get("step");
		maxX = (int) properties.get("maxX");
		maxGridBlockSize = (long) properties.get("maxGridBlockSize");
	}

	@Override
	public int getMaxX() {
		return maxX;
	}
	
	@Override
	public int getMaxY() {
		return (int) Math.min((step + 2)/2 - 1, maxX);
	}
	
	@Override
	public int getMaxZ() {
		return getMaxY();
	}

}