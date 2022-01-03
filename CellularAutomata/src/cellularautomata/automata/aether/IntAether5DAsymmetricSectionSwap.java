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
import cellularautomata.model5d.ActionableModel5D;
import cellularautomata.model5d.AnisotropicModel5DA;
import cellularautomata.model5d.AnisotropicIntModel5DSlice;
import cellularautomata.model5d.ImmutableIntModel5D;
import cellularautomata.model5d.IntModel5D;
import cellularautomata.model5d.IntSubModel5D;
import cellularautomata.model5d.IntSubModel5DWithVBounds;
import cellularautomata.model5d.SizeLimitedAnisotropicIntModel5DBlock;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 5D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class IntAether5DAsymmetricSectionSwap extends ActionableModel5D<IntModel5D> implements AnisotropicModel5DA {

	public static final int MAX_INITIAL_VALUE = Integer.MAX_VALUE;
	public static final int MIN_INITIAL_VALUE = -477218589;

	private static final String PROPERTIES_BACKUP_FILE_NAME = "properties.ser";
	private static final String GRID_FOLDER_NAME = "grid";

	private static final byte LEFT = 0;
	private static final byte CENTER = 1;
	private static final byte RIGHT = 2;

	private SizeLimitedAnisotropicIntModel5DBlock gridBlockA;
	private SizeLimitedAnisotropicIntModel5DBlock gridBlockB;
	private int initialValue;
	private long step;
	private int maxV;
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
	public IntAether5DAsymmetricSectionSwap(int initialValue, long maxGridHeapSize, String folderPath) throws Exception {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of int type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		maxGridBlockSize = maxGridHeapSize/2;
		gridBlockA = new SizeLimitedAnisotropicIntModel5DBlock(0, maxGridBlockSize);
		if (gridBlockA.maxV < 8) {
			throw new IllegalArgumentException("Passed heap space limit is too small.");
		}
		gridBlockA.setValueAtPosition(0, 0, 0, 0, 0, initialValue);
		maxV = 6;
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
	public IntAether5DAsymmetricSectionSwap(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
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
		if (maxV > gridBlockA.maxV) {
			gridBlockB = loadGridBlock(gridBlockA.maxV + 1);
		}
		readWriteGridFolder = new File(folderPath + File.separator + getSubfolderPath() + File.separator + GRID_FOLDER_NAME);
	}

	private SizeLimitedAnisotropicIntModel5DBlock loadGridBlockSafe(int minV) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedAnisotropicIntModel5DBlock gridBlock = null;
		File gridBlockFile = null;
		for (int i = 0; i < files.length && !found; i++) {
			File currentFile = files[i];
			String fileName = currentFile.getName();
			int fileMinV;
			try {
				//"minV=".length() == 5
				fileMinV = Integer.parseInt(fileName.substring(5, fileName.indexOf("_")));
				if (fileMinV == minV) {
					found = true;
					gridBlockFile = currentFile;
				}
			} catch (NumberFormatException ex) {

			}
		}
		if (found) {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(gridBlockFile));
			gridBlock = (SizeLimitedAnisotropicIntModel5DBlock) in.readObject();
			in.close();
		}
		return gridBlock;
	}

	private SizeLimitedAnisotropicIntModel5DBlock loadGridBlock(int minV) throws IOException, ClassNotFoundException {
		SizeLimitedAnisotropicIntModel5DBlock gridBlock = loadGridBlockSafe(minV);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minW=" + minV + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}

	private void saveGridBlock(SizeLimitedAnisotropicIntModel5DBlock gridBlock) throws FileNotFoundException, IOException {
		String name = "minV=" + gridBlock.minV + "_maxV=" + gridBlock.maxV + ".ser";
		String pathName = this.gridFolder.getPath() + File.separator + name;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}

	private SizeLimitedAnisotropicIntModel5DBlock loadOrBuildGridBlock(int minV) throws ClassNotFoundException, IOException {		
		SizeLimitedAnisotropicIntModel5DBlock gridBlock = loadGridBlockSafe(minV);
		if (gridBlock != null) {
			return gridBlock;
		} else {
			return new SizeLimitedAnisotropicIntModel5DBlock(minV, maxGridBlockSize);
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
		int[] relevantAsymmetricNeighborValues = new int[10];
		int[][] relevantAsymmetricNeighborCoords = new int[10][5];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[10];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[10];// to compensate for omitted symmetric positions		
		AnisotropicIntModel5DSlice[] newVSlices = 
				new AnisotropicIntModel5DSlice[] {
						null, 
						new AnisotropicIntModel5DSlice(0), 
						new AnisotropicIntModel5DSlice(1)};
		if (gridBlockA.minV > 0) {
			if (gridBlockB != null && gridBlockB.minV == 0) {
				SizeLimitedAnisotropicIntModel5DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			} else {
				saveGridBlock(gridBlockA);
				gridBlockA.free();
				gridBlockA = loadGridBlock(0);
			}
		}
		int currentMaxV = maxV;//it can change during computing
		boolean anySlicePositionToppled = toppleRangeFromZeroToFive(gridBlockA, newVSlices, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts);
		gridChanged = gridChanged || anySlicePositionToppled;
		int v = 6;
		AnisotropicIntModel5DSlice[] vSlices = new AnisotropicIntModel5DSlice[] {
				null,
				gridBlockA.getSlice(5),
				gridBlockA.getSlice(6)};
		while (v <= currentMaxV) {
			while (v <= currentMaxV && v < gridBlockA.maxV) {
				slideGridSlices(newVSlices, new AnisotropicIntModel5DSlice(v + 1));
				anySlicePositionToppled = toppleSliceBeyondFive(gridBlockA, v, vSlices, newVSlices, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(v - 1, newVSlices[LEFT]);
				v++;
			}
			if (v <= currentMaxV) {
				if (gridBlockB != null) {
					if (gridBlockB.minV != v + 1) {
						saveGridBlock(gridBlockB);
						gridBlockB.free();
						gridBlockB = loadOrBuildGridBlock(v + 1);
					}
				} else {
					gridBlockB = loadOrBuildGridBlock(v + 1);
				}
				slideGridSlices(newVSlices, new AnisotropicIntModel5DSlice(v + 1));
				anySlicePositionToppled = toppleLastSliceOfBlock(gridBlockA, gridBlockB, v, vSlices, newVSlices, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(v - 1, newVSlices[LEFT]);
				v++;
				if (v <= currentMaxV) {
					slideGridSlices(newVSlices, new AnisotropicIntModel5DSlice(v + 1));
					anySlicePositionToppled = toppleFirstSliceOfBlock(gridBlockA, gridBlockB, v, vSlices, newVSlices, 
							relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
					gridChanged = gridChanged || anySlicePositionToppled;
					gridBlockA.setSlice(v - 1, newVSlices[LEFT]);
					processGridBlock(gridBlockA);
					SizeLimitedAnisotropicIntModel5DBlock swp = gridBlockA;
					gridBlockA = gridBlockB;
					gridBlockB = swp;
					v++;
				}
			}
		}
		gridBlockA.setSlice(currentMaxV, newVSlices[CENTER]);
		if (currentMaxV == gridBlockA.maxV) {
			processGridBlock(gridBlockA);
			gridBlockB.setSlice(currentMaxV + 1, newVSlices[RIGHT]);
			processGridBlock(gridBlockB);
		} else {
			gridBlockA.setSlice(currentMaxV + 1, newVSlices[RIGHT]);
			processGridBlock(gridBlockA);
		}
		triggerAfterProcessing();
		return gridChanged;
	}

	private boolean toppleRangeFromZeroToFive(SizeLimitedAnisotropicIntModel5DBlock gridBlock,
			AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = null, 
				currentVSlice = gridBlock.getSlice(0), 
				greaterVSlice = gridBlock.getSlice(1);
		AnisotropicIntModel5DSlice newSmallerVSlice = null, 
				newCurrentVSlice = newVSlices[1], 
				newGreaterVSlice = newVSlices[2];
		// 0 | 0 | 0 | 0 | 0 | 1
		int currentValue = currentVSlice.getFromPosition(0, 0, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(0, 0, 0, 0);
		if (topplePositionType1(currentValue, greaterVNeighborValue, newCurrentVSlice, newGreaterVSlice)) {
			changed = true;
		}
		//v slice transition
		//smallerVSlice = currentVSlice; //not needed here
		currentVSlice = greaterVSlice;
		greaterVSlice = gridBlock.getSlice(2);
		newSmallerVSlice = newCurrentVSlice;
		newCurrentVSlice = newGreaterVSlice;
		newGreaterVSlice = new AnisotropicIntModel5DSlice(3);
		newVSlices[0] = newSmallerVSlice;
		newVSlices[1] = newCurrentVSlice;
		newVSlices[2] = newGreaterVSlice;
		// 1 | 0 | 0 | 0 | 0 | 2
		//reuse values obtained previously
		int smallerVNeighborValue = currentValue;
		currentValue = greaterVNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(0, 0, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(1, 0, 0, 0);
		if (topplePositionType2(currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 1 | 1 | 0 | 0 | 0 | 3
		//reuse values obtained previously
		int smallerWNeighborValue = currentValue;
		currentValue = greaterWNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 0, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(1, 1, 0, 0);
		if (topplePositionType3(currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 1 | 1 | 1 | 0 | 0 | 4
		//reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 0);
		if (topplePositionType4(currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 1 | 1 | 1 | 1 | 0 | 5
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 1, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 1);
		if (topplePositionType5(currentValue, greaterVNeighborValue, smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 1 | 1 | 1 | 1 | 1 | 6
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 1, 1);
		if (topplePositionType6(currentValue, greaterVNeighborValue, smallerZNeighborValue, newCurrentVSlice, newGreaterVSlice)) {
			changed = true;
		}
		//v slice transition
		gridBlock.setSlice(0, newSmallerVSlice);
		smallerVSlice = currentVSlice;
		currentVSlice = greaterVSlice;
		greaterVSlice = gridBlock.getSlice(3);
		AnisotropicIntModel5DSlice[] vSlices = new AnisotropicIntModel5DSlice[] { smallerVSlice, currentVSlice, greaterVSlice};
		newSmallerVSlice = newCurrentVSlice;
		newCurrentVSlice = newGreaterVSlice;
		newGreaterVSlice = new AnisotropicIntModel5DSlice(4);
		newVSlices[0] = newSmallerVSlice;
		newVSlices[1] = newCurrentVSlice;
		newVSlices[2] = newGreaterVSlice;
		// 2 | 0 | 0 | 0 | 0 | 7
		currentValue = currentVSlice.getFromPosition(0, 0, 0, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(0, 0, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(0, 0, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(1, 0, 0, 0);
		if (topplePositionType7(currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 1 | 0 | 0 | 0 | 8
		//reuse values obtained previously
		smallerWNeighborValue = currentValue;
		currentValue = greaterWNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 0, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(1, 0, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(2, 0, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(1, 1, 0, 0);
		if (topplePositionType8(1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, 
				smallerWNeighborValue, 8, greaterXNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 1 | 1 | 0 | 0 | 9
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(1, 1, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(2, 1, 0, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 0);
		if (topplePositionType9(1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 6, 
				greaterYNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 1 | 1 | 1 | 0 | 10
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(1, 1, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 1);
		if (topplePositionType10(1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 4, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 1 | 1 | 1 | 1 | 11
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(1, 1, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 1);
		if (topplePositionType11(1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 5, greaterWNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 0 | 0 | 0 | 12
		currentValue = currentVSlice.getFromPosition(2, 0, 0, 0);		
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 0, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 0, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 1, 0, 0);
		if (topplePositionType12(2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 1 | 0 | 0 | 13
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 0, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 0);
		if (topplePositionType13(2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 6, 
				greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 1 | 1 | 0 | 14
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 1);
		if (topplePositionType14(2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 1 | 1 | 1 | 15
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		if (topplePositionType15(2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 3, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 2 | 0 | 0 | 16
		currentValue = currentVSlice.getFromPosition(2, 2, 0, 0);		
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 0, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 0, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		if (topplePositionType16(2, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 2 | 1 | 0 | 17
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		if (topplePositionType17(2, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 2 | 1 | 1 | 18
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 1);
		if (topplePositionType18(2, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 4, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 2 | 2 | 0 | 19
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentVSlice.getFromPosition(2, 2, 2, 0);		
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 0);
		if (topplePositionType19(2, currentValue, greaterVNeighborValue, smallerYNeighborValue, greaterZNeighborValue,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 2 | 2 | 1 | 20
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 2);
		if (topplePositionType20(2, 1, currentValue, greaterVNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 5, smallerZNeighborValue, 2,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 2 | 2 | 2 | 2 | 2 | 21
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;		
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 2);
		if (topplePositionType21(2, currentValue, greaterVNeighborValue, smallerZNeighborValue, newVSlices[1], newVSlices[2])) {
			changed = true;
		}		
		//v slice transition
		gridBlock.setSlice(1, newSmallerVSlice);
		smallerVSlice = currentVSlice;
		currentVSlice = greaterVSlice;
		greaterVSlice = gridBlock.getSlice(4);
		vSlices[0] = smallerVSlice;
		vSlices[1] = currentVSlice;
		vSlices[2] = greaterVSlice;
		newSmallerVSlice = newCurrentVSlice;
		newCurrentVSlice = newGreaterVSlice;
		newGreaterVSlice = new AnisotropicIntModel5DSlice(5);
		newVSlices[0] = newSmallerVSlice;
		newVSlices[1] = newCurrentVSlice;
		newVSlices[2] = newGreaterVSlice;
		if (toppleRangeType1(vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// 3 | 2 | 0 | 0 | 0 | 26
		currentValue = currentVSlice.getFromPosition(2, 0, 0, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 0, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 0, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 0, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 0, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 1, 0, 0);
		if (topplePositionType8(2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, 
				smallerWNeighborValue, 1, greaterXNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 1 | 0 | 0 | 27
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 1, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 1, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 0, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 0);
		if (topplePositionType22(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 1 | 1 | 0 | 28
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 1, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 1);
		if (topplePositionType23(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 4, greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 1 | 1 | 1 | 29
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 1, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		if (topplePositionType24(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 2 | 0 | 0 | 30
		currentValue = currentVSlice.getFromPosition(2, 2, 0, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 0, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 0, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		if (topplePositionType9(2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 2 | 1 | 0 | 31
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		if (topplePositionType25(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 2 | 1 | 1 | 32
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 1);
		if (topplePositionType26(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 2 | 2 | 0 | 33
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentVSlice.getFromPosition(2, 2, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		if (topplePositionType10(2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 2 | 2 | 1 | 34
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 2);
		if (topplePositionType27(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 4,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 2 | 2 | 2 | 2 | 35
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 2);
		if (topplePositionType11(2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 5, greaterWNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType2(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// 3 | 3 | 2 | 0 | 0 | 39
		currentValue = currentVSlice.getFromPosition(3, 2, 0, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 0, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 0, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 0);
		if (topplePositionType13(3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 3 | 2 | 1 | 0 | 40
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 1);
		if (topplePositionType28(3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 3 | 2 | 1 | 1 | 41
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 1);
		if (topplePositionType29(3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 3 | 2 | 2 | 0 | 42
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		if (topplePositionType14(3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 3 | 2 | 2 | 1 | 43
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 2);		
		if (topplePositionType30(3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 3,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 3 | 2 | 2 | 2 | 44
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		if (topplePositionType15(3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 3, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType3(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// 3 | 3 | 3 | 2 | 0 | 47
		currentValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		if (topplePositionType17(3, 2, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 3 | 3 | 2 | 1 | 48
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		if (topplePositionType31(3, 2, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 3 | 3 | 3 | 2 | 2 | 49
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 2);
		if (topplePositionType18(3, 2, currentValue, greaterVNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 4, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType4(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		//v slice transition
		gridBlock.setSlice(2, newSmallerVSlice);
		smallerVSlice = currentVSlice;
		currentVSlice = greaterVSlice;
		greaterVSlice = gridBlock.getSlice(5);
		vSlices[0] = smallerVSlice;
		vSlices[1] = currentVSlice;
		vSlices[2] = greaterVSlice;
		newSmallerVSlice = newCurrentVSlice;
		newCurrentVSlice = newGreaterVSlice;
		newGreaterVSlice = new AnisotropicIntModel5DSlice(6);
		newVSlices[0] = newSmallerVSlice;
		newVSlices[1] = newCurrentVSlice;
		newVSlices[2] = newGreaterVSlice;
		if (toppleRangeType5(vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType6(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// 4 | 3 | 2 | 0 | 0 | 65
		currentValue = currentVSlice.getFromPosition(3, 2, 0, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 0, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 0, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 0);
		if (topplePositionType22(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 3 | 2 | 1 | 0 | 66
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 1);
		if (topplePositionType36(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 3 | 2 | 1 | 1 | 67
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 1);
		if (topplePositionType37(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 3 | 2 | 2 | 0 | 68
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		if (topplePositionType23(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 3 | 2 | 2 | 1 | 69
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 2);
		if (topplePositionType38(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 3 | 2 | 2 | 2 | 70
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		if (topplePositionType24(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType7(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// 4 | 3 | 3 | 2 | 0 | 73
		currentValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 3, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		if (topplePositionType25(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 3 | 3 | 2 | 1 | 74
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 3, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		if (topplePositionType39(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 3 | 3 | 2 | 2 | 75
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 3, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 2);
		if (topplePositionType26(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType8(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType9(4, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// 4 | 4 | 3 | 2 | 0 | 86
		currentValue = currentVSlice.getFromPosition(4, 3, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(4, 3, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 1);
		if (topplePositionType28(4, 3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 4 | 3 | 2 | 1 | 87
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(4, 3, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 2);
		if (topplePositionType43(4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 4 | 4 | 3 | 2 | 2 | 88
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 2);
		if (topplePositionType29(4, 3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType10(4, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		//v slice transition
		gridBlock.setSlice(3, newSmallerVSlice);
		smallerVSlice = currentVSlice;
		currentVSlice = greaterVSlice;
		greaterVSlice = gridBlock.getSlice(6);
		vSlices[0] = smallerVSlice;
		vSlices[1] = currentVSlice;
		vSlices[2] = greaterVSlice;
		newSmallerVSlice = newCurrentVSlice;
		newCurrentVSlice = newGreaterVSlice;
		newGreaterVSlice = new AnisotropicIntModel5DSlice(7);
		newVSlices[0] = newSmallerVSlice;
		newVSlices[1] = newCurrentVSlice;
		newVSlices[2] = newGreaterVSlice;
		if (toppleRangeType11(vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType12(4, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType13(4, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// 5 | 4 | 3 | 2 | 0 | 121
		currentValue = currentVSlice.getFromPosition(4, 3, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(4, 3, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(5, 3, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(4, 3, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 1);
		if (topplePositionType36(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// 5 | 4 | 3 | 2 | 1 | 122
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(4, 3, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(5, 3, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(4, 3, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 2);
		if (topplePositionType47(4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				newVSlices)) {
			changed = true;
		}
		// 5 | 4 | 3 | 2 | 2 | 123
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(4, 3, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(5, 3, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 2);
		if (topplePositionType37(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType14(4, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		gridBlock.setSlice(4, newSmallerVSlice);		
		return changed;
	}

	private boolean toppleSliceBeyondFive(SizeLimitedAnisotropicIntModel5DBlock gridBlock, int v, AnisotropicIntModel5DSlice[] vSlices, 
			AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondFive(gridBlock, gridBlock, gridBlock, v, vSlices, newVSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}

	private boolean toppleLastSliceOfBlock(SizeLimitedAnisotropicIntModel5DBlock leftGridBlock, SizeLimitedAnisotropicIntModel5DBlock rightGridBlock,
			int v, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondFive(leftGridBlock, leftGridBlock, rightGridBlock, v, vSlices, newVSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}

	private boolean toppleFirstSliceOfBlock(SizeLimitedAnisotropicIntModel5DBlock leftGridBlock, SizeLimitedAnisotropicIntModel5DBlock rightGridBlock,
			int v, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondFive(leftGridBlock, rightGridBlock, rightGridBlock, v, vSlices, newVSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}

	private boolean toppleSliceBeyondFive(SizeLimitedAnisotropicIntModel5DBlock leftGridBlock, SizeLimitedAnisotropicIntModel5DBlock centerGridBlock, 
			SizeLimitedAnisotropicIntModel5DBlock rightGridBlock, int v, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, 
			int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean changed = false;
		int vMinusOne = v - 1, vMinusTwo = v - 2, vMinusThree = v - 3, vMinusFour = v - 4, vPlusOne = v + 1;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[1], 
				currentVSlice = vSlices[2], 
				greaterVSlice = rightGridBlock.getSlice(vPlusOne);
		vSlices[0] = smallerVSlice;
		vSlices[1] = currentVSlice;
		vSlices[2] = greaterVSlice;
		if (toppleRangeType11(vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType15(4, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType16(4, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | 4 | 3 | 2 | 0 | 156
		int currentValue = currentVSlice.getFromPosition(4, 3, 2, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(4, 3, 2, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(5, 3, 2, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(4, 3, 1, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 1);
		if (topplePositionType36(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 4 | 3 | 2 | 1 | 157
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(4, 3, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(5, 3, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(4, 3, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 2);
		if (topplePositionType47(4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				newVSlices)) {
			changed = true;
		}
		// v | 4 | 3 | 2 | 2 | 158
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(4, 3, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(4, 3, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(5, 3, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(4, 4, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(4, 3, 3, 2);
		if (topplePositionType37(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType17(4, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int w = 5, wMinusOne = w - 1, wPlusOne = w + 1;
		for (int wMinusTwo = w - 2, wMinusThree = w - 3; w != vMinusOne; wMinusThree = wMinusTwo, wMinusTwo = wMinusOne, wMinusOne = w, w = wPlusOne, wPlusOne++) {
			if (toppleRangeType15(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType18(w, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// v | w | 3 | 2 | 0 | 195
			currentValue = currentVSlice.getFromPosition(w, 3, 2, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 0);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, 3, 2, 0);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 3, 2, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 1);
			if (topplePositionType36(w, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | 3 | 2 | 1 | 196
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 1);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, 3, 2, 1);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 3, 2, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 2);
			if (topplePositionType47(w, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			// v | w | 3 | 2 | 2 | 197
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 2);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, 3, 2, 2);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 3, 2, 2);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 2);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 2);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 2);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 2);
			if (topplePositionType37(w, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			if (toppleRangeType19(w, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}			
			int x = 4, xMinusOne = x - 1, xPlusOne = x + 1;
			for (int xMinusTwo = x - 2; x != wMinusOne; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
				if (toppleRangeType18(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
				// v | w | x | 2 | 0 | 223
				currentValue = currentVSlice.getFromPosition(w, x, 2, 0);
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 0);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 0);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 0);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 0);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 0);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 0);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 0);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
				if (topplePositionType58(w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
						greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				// v | w | x | 2 | 1 | 224
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 1);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 1);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 1);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 1);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 1);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 1);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 1);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 2);
				if (topplePositionType47(w, x, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
				// v | w | x | 2 | 2 | 225
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 2);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 2);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 2);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 2);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 2);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 2);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 2);
				if (topplePositionType59(w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
						smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
				for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
					// v | w | x | y | 0 | 223
					currentValue = currentVSlice.getFromPosition(w, x, y, 0);
					greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
					smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
					greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
					smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
					greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
					smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
					greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
					smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
					greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
					if (topplePositionType58(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
							greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
						changed = true;
					}
					// v | w | x | y | 1 | 238
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
					smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
					greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
					smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
					greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
					smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
					greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
					smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
					greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
					if (topplePositionType47(w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newVSlices)) {
						changed = true;
					}
					int z = 2, zPlusOne = z + 1;
					for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
						// v | w | x | y | z | 243
						//reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
						smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
						greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
						smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
						greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
						smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
						greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
						smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
						greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
						if (topplePositionType63(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
								greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, newVSlices)) {
							changed = true;
						}
					}
					// v | w | x | y | z | 239
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
					smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
					greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
					if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newVSlices)) {
						changed = true;
					}
					z = y;
					// v | w | x | y | z | 225
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
					smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
					greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
					if (topplePositionType59(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
							smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
						changed = true;
					}
				}
				// v | w | x | y | 0 | 195
				currentValue = currentVSlice.getFromPosition(w, x, y, 0);
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
				if (topplePositionType36(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				// v | w | x | y | 1 | 226
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
				if (topplePositionType47(w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
				int z = 2, zPlusOne = z + 1;
				for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
					// v | w | x | y | z | 240
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
					smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
					greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
					if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
							greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newVSlices)) {
						changed = true;
					}
				}
				// v | w | x | y | z | 227
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
				z = xMinusOne;
				// v | w | x | y | z | 197
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				if (topplePositionType37(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
						greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				if (toppleRangeType19(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
			}
			if (toppleRangeType16(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// v | w | x | 2 | 0 | 200
			currentValue = currentVSlice.getFromPosition(w, x, 2, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 0);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 0);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
			if (topplePositionType36(w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | 2 | 1 | 201
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 1);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 1);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 2);
			if (topplePositionType47(w, x, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			// v | w | x | 2 | 2 | 202
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 2);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 2);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 2);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 2);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 2);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 2);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 2);
			if (topplePositionType37(w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
			for (; y != wMinusTwo; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				// v | w | x | y | 0 | 200
				currentValue = currentVSlice.getFromPosition(w, x, y, 0);
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
				if (topplePositionType36(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				// v | w | x | y | 1 | 229
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
				if (topplePositionType47(w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
				int z = 2, zPlusOne = z + 1;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					// v | w | x | y | z | 241
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
					smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
					greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
					if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newVSlices)) {
						changed = true;
					}
				}
				// v | w | x | y | z | 230
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
				z = y;
				// v | w | x | y | z | 202
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				if (topplePositionType37(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | 0 | 156
			currentValue = currentVSlice.getFromPosition(w, x, y, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
			if (topplePositionType36(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | y | 1 | 203
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
			if (topplePositionType47(w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			int z = 2, zPlusOne = z + 1;
			for (; z != wMinusThree; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 231
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 204
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			z = wMinusTwo;
			// v | w | x | y | z | 158
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			if (topplePositionType37(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			if (toppleRangeType17(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
		}
		if (toppleRangeType12(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType20(w, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 3 | 2 | 0 | 169
		currentValue = currentVSlice.getFromPosition(w, 3, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 3, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 3, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 1);
		if (topplePositionType36(w, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 3 | 2 | 1 | 170
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 3, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 3, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 2);
		if (topplePositionType47(w, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				newVSlices)) {
			changed = true;
		}
		// v | w | 3 | 2 | 2 | 171
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 3, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 3, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 2);
		if (topplePositionType37(w, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType21(w, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int x = 4, xMinusOne = x - 1, xPlusOne = x + 1;
		for (int xMinusTwo = x - 2; x != vMinusTwo; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
			if (toppleRangeType20(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// v | w | x | 2 | 0 | 209
			currentValue = currentVSlice.getFromPosition(w, x, 2, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 0);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 0);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
			if (topplePositionType36(w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | 2 | 1 | 210
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 1);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 1);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 2);
			if (topplePositionType47(w, x, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			// v | w | x | 2 | 2 | 211
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 2);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 2);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 2);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 2);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 2);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 2);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 2);
			if (topplePositionType37(w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
			for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				// v | w | x | y | 0 | 209
				currentValue = currentVSlice.getFromPosition(w, x, y, 0);
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
				if (topplePositionType36(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				// v | w | x | y | 1 | 233
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
				if (topplePositionType47(w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
				int z = 2, zPlusOne = z + 1;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					// v | w | x | y | z | 242
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
					smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
					greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
					if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newVSlices)) {
						changed = true;
					}
				}
				// v | w | x | y | z | 234
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
				z = y;
				// v | w | x | y | z | 211
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				if (topplePositionType37(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | 0 | 169
			currentValue = currentVSlice.getFromPosition(w, x, y, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
			if (topplePositionType36(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | y | 1 | 212
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
			if (topplePositionType47(w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			int z = 2, zPlusOne = z + 1;
			for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 235
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 213
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			z = xMinusOne;
			// v | w | x | y | z | 171
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			if (topplePositionType37(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			if (toppleRangeType21(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
		}
		if (toppleRangeType13(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | x | 2 | 0 | 174
		currentValue = currentVSlice.getFromPosition(w, x, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType36(w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 2 | 1 | 175
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 2);
		if (topplePositionType47(w, x, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				newVSlices)) {
			changed = true;
		}
		// v | w | x | 2 | 2 | 176
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 2);
		if (topplePositionType37(w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
		for (; y != vMinusThree; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// v | w | x | y | 0 | 174
			currentValue = currentVSlice.getFromPosition(w, x, y, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
			if (topplePositionType36(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | y | 1 | 215
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
			if (topplePositionType47(w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			int z = 2, zPlusOne = z + 1;
			for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 236
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 216
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
			z = y;
			// v | w | x | y | z | 176
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			if (topplePositionType37(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | x | y | 0 | 121
		currentValue = currentVSlice.getFromPosition(w, x, y, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
		if (topplePositionType36(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | y | 1 | 177
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
		if (topplePositionType47(w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != vMinusFour; z = zPlusOne, zPlusOne++) {
			// v | w | x | y | z | 217
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newVSlices)) {
				changed = true;
			}
		}
		// v | w | x | y | z | 178
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
		if (topplePositionType47(w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				newVSlices)) {
			changed = true;
		}
		z = vMinusThree;
		// v | w | x | y | z | 123
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
		if (topplePositionType37(w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType14(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}		
		
		//end
		
		if (changed && v > maxV - 1) {
			maxV++;
		}
		return changed;
	}

	private static boolean toppleRangeType1(AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | 0 | 0 | 0 | 0 | 7
		int currentValue = currentVSlice.getFromPosition(0, 0, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(0, 0, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(0, 0, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(1, 0, 0, 0);
		if (topplePositionType7(currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 1 | 0 | 0 | 0 | 22
		//reuse values obtained previously
		int smallerWNeighborValue = currentValue;
		currentValue = greaterWNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 0, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(1, 0, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(2, 0, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(1, 1, 0, 0);
		if (topplePositionType8(1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, 
				smallerWNeighborValue, 8, greaterXNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 1 | 1 | 0 | 0 | 23
		//reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(1, 1, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(2, 1, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 0);
		if (topplePositionType9(1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 6, 
				greaterYNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 1 | 1 | 1 | 0 | 24
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(1, 1, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 1);
		if (topplePositionType10(1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 4, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 1 | 1 | 1 | 1 | 25
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(1, 1, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(1, 1, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 1);
		if (topplePositionType11(1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType2(int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int crdMinusOne = crd - 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// crd | crd | 0 | 0 | 0 | 12
		int currentValue = currentVSlice.getFromPosition(crd, 0, 0, 0);		
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd, 0, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(crdMinusOne, 0, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(crd, 1, 0, 0);
		if (topplePositionType12(crd, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | 1 | 0 | 0 | 36
		//reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, 1, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(crdMinusOne, 1, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(crd, 2, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(crd, 1, 1, 0);
		if (topplePositionType13(crd, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 6, 
				greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | 1 | 1 | 0 | 37
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, 1, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(crdMinusOne, 1, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(crd, 2, 1, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd, 1, 1, 1);
		if (topplePositionType14(crd, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | 1 | 1 | 1 | 38
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, 1, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(crdMinusOne, 1, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(crd, 2, 1, 1);
		if (topplePositionType15(crd, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType3(int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int crdMinusOne = crd - 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// crd | crd | crd | 0 | 0 | 16
		int currentValue = currentVSlice.getFromPosition(crd, crd, 0, 0);		
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 1, 0);
		if (topplePositionType16(crd, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | crd | 1 | 0 | 45
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, 1, 1);
		if (topplePositionType17(crd, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | crd | 1 | 1 | 46
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 2, 1);
		if (topplePositionType18(crd, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType4(int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int crdMinusOne = crd - 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// crd | crd | crd | crd | 0 | 19
		int currentValue = currentVSlice.getFromPosition(crd, crd, crd, 0);		
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, 1);
		if (topplePositionType19(crd, currentValue, greaterVNeighborValue, smallerYNeighborValue, greaterZNeighborValue,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | crd | crd | 1 | 50
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, 2);
		if (topplePositionType20(crd, 1, currentValue, greaterVNeighborValue, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// crd | crd | crd | crd | z | 96
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;		
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, zPlusOne);
			if (topplePositionType46(crd, z, currentValue, greaterVNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue,
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// crd | crd | crd | crd | z | 51
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;		
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, zPlusOne);
		if (topplePositionType20(crd, z, currentValue, greaterVNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 5, smallerZNeighborValue, 1,
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | crd | crd | crd | 21
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;		
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, crd);
		if (topplePositionType21(crd, currentValue, greaterVNeighborValue, smallerZNeighborValue, newVSlices[1], newVSlices[2])) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType5(AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		if (toppleRangeType1(vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | 2 | 0 | 0 | 0 | 52
		int currentValue = currentVSlice.getFromPosition(2, 0, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(2, 0, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(2, 0, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(3, 0, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(1, 0, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(2, 1, 0, 0);
		if (topplePositionType32(2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue,
				greaterXNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 1 | 0 | 0 | 53
		//reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 1, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 1, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 0);
		if (topplePositionType22(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 1 | 1 | 0 | 54
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 1, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 1);
		if (topplePositionType23(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 4, greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 1 | 1 | 1 | 55
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 1, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 1, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(1, 1, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		if (topplePositionType24(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 2 | 0 | 0 | 56
		currentValue = currentVSlice.getFromPosition(2, 2, 0, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 0, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 0, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		if (topplePositionType33(2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue,
				greaterYNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 2 | 1 | 0 | 57
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		if (topplePositionType25(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 2 | 1 | 1 | 58
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(2, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 1);
		if (topplePositionType26(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 2 | 2 | 0 | 59
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentVSlice.getFromPosition(2, 2, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		if (topplePositionType34(2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerYNeighborValue,
				greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 2 | 2 | 1 | 60
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 2);
		if (topplePositionType27(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 4,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 2 | 2 | 2 | 2 | 61
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(2, 2, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(2, 2, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 2);
		if (topplePositionType35(2,currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType6(int w, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		//int w = v - 1;
		int  wMinusOne = w - 1, wPlusOne = w + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | 0 | 0 | 0 | 26
		int currentValue = currentVSlice.getFromPosition(w, 0, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, 0, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, 0, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 0, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 0, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, 1, 0, 0);
		if (topplePositionType8(w, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, 
				smallerWNeighborValue, 1, greaterXNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 1 | 0 | 0 | 62
		//reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 1, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 1, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 1, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 1, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 2, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 0);
		if (topplePositionType22(w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 1 | 1 | 0 | 63
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 1, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 1, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 1, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 1, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 1);
		if (topplePositionType23(w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 1 | 1 | 1 | 64
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 1, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 1, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 1, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 1, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 1);
		if (topplePositionType24(w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType7(int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		//int crd = v - 1;
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | crd | crd | 0 | 0 | 30
		int currentValue = currentVSlice.getFromPosition(crd, crd, 0, 0);		
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 1, 0);
		if (topplePositionType9(crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | crd | crd | 1 | 0 | 71
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, 1, 1);
		if (topplePositionType25(crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | crd | crd | 1 | 1 | 72
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 2, 1);
		if (topplePositionType26(crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType8(int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		//int v = crd + 1;
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | crd | crd | crd | 0 | 33
		int currentValue = currentVSlice.getFromPosition(crd, crd, crd, 0);		
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, 1);
		if (topplePositionType10(crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | crd | crd | crd | 1 | 76
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, 2);
		if (topplePositionType27(crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | crd | crd | crd | z | 131
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, zPlusOne);
			if (topplePositionType27(crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | crd | crd | crd | z | 77
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, zPlusOne);
		if (topplePositionType27(crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 4,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | crd | crd | crd | crd | 35
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, crd);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, crd);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, crd);
		if (topplePositionType11(crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 5, greaterWNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int w = crdPlusOne, wMinusOne = crd;
		if (toppleRangeType2(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 2 | 0 | 0 | 78
		currentValue = currentVSlice.getFromPosition(w, 2, 0, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 0);
		if (topplePositionType40(w, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue,
				greaterYNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 1 | 0 | 79
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 1);
		if (topplePositionType28(w, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 1 | 1 | 80
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 1);
		if (topplePositionType29(w, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 0 | 81
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentVSlice.getFromPosition(w, 2, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 0);
		if (topplePositionType41(w, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue,
				greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 1 | 82
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 2);		
		if (topplePositionType30(w, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 2 | 83
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 2);
		if (topplePositionType42(w, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType9(int crd, int x, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		//int x = crd - 1;
		int  crdMinusOne = x, xMinusOne = x - 1, xPlusOne = crd;
		boolean changed = false;
		AnisotropicIntModel5DSlice currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// crd | crd | x | 0 | 0 | 39
		int currentValue = currentVSlice.getFromPosition(crd, x, 0, 0);		
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd, x, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(crdMinusOne, x, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(crd, xPlusOne, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(crd, xMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(crd, x, 1, 0);
		if (topplePositionType13(crd, x, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | x | 1 | 0 | 84
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, x, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(crdMinusOne, x, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(crd, xPlusOne, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd, xMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd, x, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd, x, 1, 1);
		if (topplePositionType28(crd, x, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd | crd | x | 1 | 1 | 85
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, x, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(crdMinusOne, x, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(crd, xPlusOne, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd, xMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd, x, 2, 1);
		if (topplePositionType29(crd, x, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType10(int crd1, int crd2, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		//int crd2 = crd1 - 1;
		int crd2MinusOne = crd2 - 1, crd2PlusOne = crd1, crd1MinusOne = crd2, crd1MinusTwo = crd2MinusOne;
		boolean changed = false;
		AnisotropicIntModel5DSlice currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// crd1 | crd1 | crd2 | crd2 | 0 | 42
		int currentValue = currentVSlice.getFromPosition(crd1, crd2, crd2, 0);		
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd2, crd2, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(crd1MinusOne, crd2, crd2, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(crd1, crd2PlusOne, crd2, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd2, crd2MinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd2, crd2, 1);
		if (topplePositionType14(crd1, crd2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd1 | crd1 | crd2 | crd2 | 1 | 89
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd2, crd2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(crd1MinusOne, crd2, crd2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(crd1, crd2PlusOne, crd2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd2, crd2MinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd2, crd2, 2);		
		if (topplePositionType30(crd1, crd2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crd1MinusTwo; z = zPlusOne, zPlusOne++) {
			// crd1 | crd1 | crd2 | crd2 | z | 144
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd2, crd2, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(crd1MinusOne, crd2, crd2, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(crd1, crd2PlusOne, crd2, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd2, crd2MinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd2, crd2, zPlusOne);		
			if (topplePositionType30(crd1, crd2, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// crd1 | crd1 | crd2 | crd2 | z | 90
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd2, crd2, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(crd1MinusOne, crd2, crd2, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(crd1, crd2PlusOne, crd2, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd2, crd2MinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd2, crd2, zPlusOne);		
		if (topplePositionType30(crd1, crd2, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd1 | crd1 | crd2 | crd2 | crd2 | 44
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd2, crd2, crd2);
		smallerWNeighborValue = currentVSlice.getFromPosition(crd1MinusOne, crd2, crd2, crd2);
		greaterXNeighborValue = currentVSlice.getFromPosition(crd1, crd2PlusOne, crd2, crd2);
		if (topplePositionType15(crd1, crd2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 3, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType3(crd1, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// crd1 | crd1 | crd1 | 2 | 0 | 91
		currentValue = currentVSlice.getFromPosition(crd1, crd1, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, 2, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, 2, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, 2, 1);
		if (topplePositionType44(crd1, 2, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd1 | crd1 | crd1 | 2 | 1 | 92
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, 2, 2);
		if (topplePositionType31(crd1, 2, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd1 | crd1 | crd1 | 2 | 2 | 93
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, 3, 2);
		if (topplePositionType45(crd1, 2, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
		for (; y != crd1MinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// crd1 | crd1 | crd1 | y | 0 | 91
			currentValue = currentVSlice.getFromPosition(crd1, crd1, y, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yMinusOne, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, y, 1);
			if (topplePositionType44(crd1, y, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
					greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// crd1 | crd1 | crd1 | y | 1 | 145
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yMinusOne, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, y, 2);
			if (topplePositionType31(crd1, y, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = 2;
			zPlusOne = z + 1;
			for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// crd1 | crd1 | crd1 | y | z | 192
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, y, zPlusOne);
				if (topplePositionType57(crd1, y, z, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
			}
			// crd1 | crd1 | crd1 | y | z | 146
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, y, zPlusOne);
			if (topplePositionType31(crd1, y, z, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = zPlusOne;
			// crd1 | crd1 | crd1 | y | z | 93
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, z);
			if (topplePositionType45(crd1, y, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// crd1 | crd1 | crd1 | y | 0 | 47
		currentValue = currentVSlice.getFromPosition(crd1, crd1, y, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yMinusOne, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, y, 1);
		if (topplePositionType17(crd1, y, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// crd1 | crd1 | crd1 | y | 1 | 94
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, y, 2);
		if (topplePositionType31(crd1, y, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		z = 2;
		zPlusOne = z + 1;
		for (; z != crd1MinusTwo; z = zPlusOne, zPlusOne++) {
			// crd1 | crd1 | crd1 | y | z | 147
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, y, zPlusOne);
			if (topplePositionType31(crd1, y, z, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// crd1 | crd1 | crd1 | y | z | 95
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd1, crd1, y, zPlusOne);
		if (topplePositionType31(crd1, y, z, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		z = zPlusOne;
		// crd1 | crd1 | crd1 | y | z | 49
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd1, crd1, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd1, crd1MinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd1, crd1, yPlusOne, z);
		if (topplePositionType18(crd1, y, currentValue, greaterVNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 4, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType4(crd1, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType11(AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		if (toppleRangeType5(vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType22(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | 3 | 2 | 0 | 0 | 100
		int currentValue = currentVSlice.getFromPosition(3, 2, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 0);
		if (topplePositionType22(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 3 | 2 | 1 | 0 | 101
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 1);
		if (topplePositionType36(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 3 | 2 | 1 | 1 | 102
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 1);
		if (topplePositionType37(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 3 | 2 | 2 | 0 | 103
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		if (topplePositionType23(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 3 | 2 | 2 | 1 | 104
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 2);
		if (topplePositionType38(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 3 | 2 | 2 | 2 | 105
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 2, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 2, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 2, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(2, 2, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		if (topplePositionType24(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType23(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | 3 | 3 | 2 | 0 | 108
		currentValue = currentVSlice.getFromPosition(3, 3, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 3, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 1);
		if (topplePositionType25(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 3 | 3 | 2 | 1 | 109
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 3, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(3, 3, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(3, 3, 2, 2);
		if (topplePositionType39(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | 3 | 3 | 2 | 2 | 110
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(3, 3, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(3, 3, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(4, 3, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(3, 2, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(3, 3, 3, 2);
		if (topplePositionType26(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType24(3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType12(int w, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		//int w = v - 1;
		int  wMinusOne = w - 1, wPlusOne = w + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		if (toppleRangeType6(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 2 | 0 | 0 | 113
		int currentValue = currentVSlice.getFromPosition(w, 2, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 0);
		if (topplePositionType22(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 1 | 0 | 114
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 1);
		if (topplePositionType36(w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 1 | 1 | 115
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 1);
		if (topplePositionType37(w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 0 | 116
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentVSlice.getFromPosition(w, 2, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 0);
		if (topplePositionType23(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 1 | 117
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 2);
		if (topplePositionType38(w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 2 | 118
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 2);
		if (topplePositionType24(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType13(int w, int x, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int  wMinusOne = w - 1, wPlusOne = w + 1, xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | x | 0 | 0 | 65
		int currentValue = currentVSlice.getFromPosition(w, x, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		if (topplePositionType22(w, x, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 119
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		if (topplePositionType36(w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 120
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType37(w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType14(int w, int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		//int w = v - 1;
		int wMinusOne = w - 1, wMinusTwo = w - 2, wPlusOne = w + 1, crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | crd | crd | 0 | 68
		int currentValue = currentVSlice.getFromPosition(w, crd, crd, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 1);
		if (topplePositionType23(w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 124
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 2);
		if (topplePositionType38(w, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != wMinusTwo; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 179
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);
			if (topplePositionType38(w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 125
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);
		if (topplePositionType38(w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 70
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, crd);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, crd);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, crd);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, crd);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, crd);
		if (topplePositionType24(w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType7(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int xMinusOne = crd, x = w;
		// v | w | x | 2 | 0 | 126
		currentValue = currentVSlice.getFromPosition(w, x, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType25(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 2 | 1 | 127
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 2);
		if (topplePositionType39(w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 2 | 2 | 128
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 2);
		if (topplePositionType26(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
		for (; y != wMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// v | w | x | y | 0 | 126
			currentValue = currentVSlice.getFromPosition(w, x, y, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
			if (topplePositionType25(w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | y | 1 | 180
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
			if (topplePositionType39(w, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = 2;
			zPlusOne = z + 1;
			for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 218
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType39(w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 181
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType39(w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = y;
			// v | w | x | y | z | 128
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			if (topplePositionType26(w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | x | y | 0 | 73
		currentValue = currentVSlice.getFromPosition(w, x, y, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
		if (topplePositionType25(w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | y | 1 | 129
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
		if (topplePositionType39(w, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		z = 2;
		zPlusOne = z + 1;
		for (; z != wMinusTwo; z = zPlusOne, zPlusOne++) {
			// v | w | x | y | z | 182
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType39(w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | x | y | z | 130
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
		if (topplePositionType39(w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		z = zPlusOne;
		// v | w | x | y | z | 75
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
		if (topplePositionType26(w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType8(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int wMinusThree = wMinusTwo;
		wMinusTwo = wMinusOne;
		wMinusOne = w;
		w = wPlusOne;
		wPlusOne++;
		if (toppleRangeType25(w, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 3 | 2 | 0 | 134
		currentValue = currentVSlice.getFromPosition(w, 3, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 1);
		if (topplePositionType28(w, 3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 3 | 2 | 1 | 135
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 2);
		if (topplePositionType43(w, 3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 3 | 2 | 2 | 136
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 3, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 3, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 4, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 3, 3, 2);
		if (topplePositionType29(w, 3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType26(w, 3, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		x = 4;
		xMinusOne = x - 1;
		int xPlusOne = x + 1;
		for (int xMinusTwo = x - 2; x != wMinusOne; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
			if (toppleRangeType25(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// v | w | x | 2 | 0 | 183
			currentValue = currentVSlice.getFromPosition(w, x, 2, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
			if (topplePositionType54(w, x, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | 2 | 1 | 184
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 2);
			if (topplePositionType43(w, x, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | 2 | 2 | 185
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 2);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 2);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 2);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 2);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 2);
			if (topplePositionType55(w, x, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			y = 3;
			yMinusOne = y - 1;
			yPlusOne = y + 1;
			for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				// v | w | x | y | 0 | 183
				currentValue = currentVSlice.getFromPosition(w, x, y, 0);
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
				if (topplePositionType54(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				// v | w | x | y | 1 | 219
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
				if (topplePositionType43(w, x, y, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				z = 2;
				zPlusOne = z + 1;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					// v | w | x | y | z | 237
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
					smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
					if (topplePositionType62(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue,
							smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
						changed = true;
					}
				}
				// v | w | x | y | z | 220
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
				z = y;
				// v | w | x | y | z | 185
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				if (topplePositionType55(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | 0 | 134
			currentValue = currentVSlice.getFromPosition(w, x, y, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
			if (topplePositionType28(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | y | 1 | 186
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
			if (topplePositionType43(w, x, y, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = 2;
			zPlusOne = z + 1;
			for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 221
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 187
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = xMinusOne;
			// v | w | x | y | z | 136
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			if (topplePositionType29(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			if (toppleRangeType26(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
		}
		if (toppleRangeType9(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | x | 2 | 0 | 139
		currentValue = currentVSlice.getFromPosition(w, x, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType28(w, x, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 2 | 1 | 140
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 2);
		if (topplePositionType43(w, x, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 2 | 2 | 141
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 2);
		if (topplePositionType29(w, x, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		y = 3;
		yMinusOne = y - 1;
		yPlusOne = y + 1;
		for (; y != wMinusTwo; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// v | w | x | y | 0 | 139
			currentValue = currentVSlice.getFromPosition(w, x, y, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
			if (topplePositionType28(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | y | 1 | 189
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
			if (topplePositionType43(w, x, y, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = 2;
			zPlusOne = z + 1;
			for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 222
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 190
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = y;
			// v | w | x | y | z | 141
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			if (topplePositionType29(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | x | y | 0 | 86
		currentValue = currentVSlice.getFromPosition(w, x, y, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
		if (topplePositionType28(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | y | 1 | 142
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
		if (topplePositionType43(w, x, y, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		z = 2;
		zPlusOne = z + 1;
		for (; z != wMinusThree; z = zPlusOne, zPlusOne++) {
			// v | w | x | y | z | 191
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | x | y | z | 143
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
		if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		z = wMinusTwo;
		// v | w | x | y | z | 88
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, y, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
		if (topplePositionType29(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType10(w, x, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType15(int w, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int  wMinusOne = w - 1, wPlusOne = w + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		if (toppleRangeType22(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 2 | 0 | 0 | 148
		int currentValue = currentVSlice.getFromPosition(w, 2, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 0);
		if (topplePositionType48(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerXNeighborValue, greaterYNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 1 | 0 | 149
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 1);
		if (topplePositionType36(w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 1 | 1 | 150
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 1);
		if (topplePositionType37(w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 0 | 151
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;		
		currentValue = currentVSlice.getFromPosition(w, 2, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 2, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 0);
		if (topplePositionType49(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 1 | 152
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 2, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 2);
		if (topplePositionType38(w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 2 | 2 | 2 | 153
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 2, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 2, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 2, 2, 2);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 2, 2, 2);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 3, 2, 2);
		smallerZNeighborValue = currentVSlice.getFromPosition(w, 2, 2, 1);
		if (topplePositionType50(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType16(int w, int x, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int  wMinusOne = w - 1, wPlusOne = w + 1, xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | x | 0 | 0 | 100
		int currentValue = currentVSlice.getFromPosition(w, x, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		if (topplePositionType22(w, x, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 154
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		if (topplePositionType36(w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 155
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType37(w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType17(int w, int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		//int crd = w - 1;
		int wMinusOne = w - 1, wMinusTwo = w - 2, wPlusOne = w + 1, crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | crd | crd | 0 | 103
		int currentValue = currentVSlice.getFromPosition(w, crd, crd, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 1);
		if (topplePositionType23(w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 159
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 2);
		if (topplePositionType38(w, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != wMinusTwo; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 205
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);
			if (topplePositionType38(w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 160
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);
		if (topplePositionType38(w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 105
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, crd);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, crd);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, crd);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, crd);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, crd);
		if (topplePositionType24(w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType23(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int x = w, xMinusOne = wMinusOne;
		// v | w | x | 2 | 0 | 161
		currentValue = currentVSlice.getFromPosition(w, x, 2, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, 2, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType51(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 2 | 1 | 162
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, 2, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 2, 2);
		if (topplePositionType39(w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 2 | 2 | 163
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 2, 2);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 2, 2);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 2, 2);
		smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, 2, 2);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 3, 2);
		if (topplePositionType52(w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
		for (; y != wMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// v | w | x | y | 0 | 161
			currentValue = currentVSlice.getFromPosition(w, x, y, 0);
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
			smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, 0);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
			if (topplePositionType51(w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
					greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			// v | w | x | y | 1 | 206
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
			smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, 1);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
			if (topplePositionType39(w, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			for (z = 2, zPlusOne = z + 1; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 232
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
				smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
				greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
				smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
				greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
				if (topplePositionType61(w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue,
						smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 207
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType39(w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
			z = y;
			// v | w | x | y | z | 163
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			if (topplePositionType52(w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | x | y | 0 | 108
		currentValue = currentVSlice.getFromPosition(w, x, y, 0);
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 0);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 0);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 1);
		if (topplePositionType25(w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | y | 1 | 164
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, 2);
		if (topplePositionType39(w, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		for (z = 2, zPlusOne = z + 1; z != wMinusTwo; z = zPlusOne, zPlusOne++) {
			// v | w | x | y | z | 208
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
			smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
			greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
			if (topplePositionType39(w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | x | y | z | 165
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(x, xMinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, x, yMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, x, y, zPlusOne);
		if (topplePositionType39(w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		z = wMinusOne;
		// v | w | x | y | z | 110
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, y, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, y, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, y, z);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, y, z);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, yPlusOne, z);
		if (topplePositionType26(w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		if (toppleRangeType24(w, vSlices, newVSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType18(int w, int x, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int  wMinusOne = w - 1, wPlusOne = w + 1, xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | x | 0 | 0 | 148
		int currentValue = currentVSlice.getFromPosition(w, x, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		if (topplePositionType48(w, x, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerXNeighborValue, greaterYNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 193
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		if (topplePositionType36(w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 194
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType37(w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType19(int w, int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1, wMinusOne = w - 1, wPlusOne = w + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | crd | crd | 0 | 151
		int currentValue = currentVSlice.getFromPosition(w, crd, crd, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 1);
		if (topplePositionType49(w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 198
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 2);
		if (topplePositionType38(w, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 228
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);
			if (topplePositionType60(w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, 
					greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 199
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);
		if (topplePositionType38(w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 153
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, crd);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, crd);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, crd);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, crd);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, crd);
		smallerZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, crdMinusOne);
		if (topplePositionType50(w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType20(int w, int x, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int  wMinusOne = w - 1, wPlusOne = w + 1, xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | x | 0 | 0 | 113
		int currentValue = currentVSlice.getFromPosition(w, x, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		if (topplePositionType22(w, x, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 167
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		if (topplePositionType36(w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 168
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, x, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, x, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType37(w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType21(int w, int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int  wMinusOne = w - 1, wPlusOne = w + 1, crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | crd | crd | 0 | 116
		int currentValue = currentVSlice.getFromPosition(w, crd, crd, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 1);
		if (topplePositionType23(w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 172
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 2);
		if (topplePositionType38(w, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 214
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);
			if (topplePositionType38(w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 173
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);
		if (topplePositionType38(w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 118
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, crd);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, crd, crd, crd);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, crd, crd, crd);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, crd);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, crd);
		if (topplePositionType24(w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType22(int w, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int wMinusOne = w - 1, wPlusOne = w + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | 0 | 0 | 0 | 52
		int currentValue = currentVSlice.getFromPosition(w, 0, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, 0, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(w, 0, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 0, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 0, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, 1, 0, 0);
		if (topplePositionType32(w, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue,
				greaterXNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 1 | 0 | 0 | 97
		//reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 1, 0, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 1, 0, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 1, 0, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 1, 0, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 2, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 0);
		if (topplePositionType22(w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 1 | 1 | 0 | 98
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 1, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 1, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 1, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 1, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, 1, 1, 1);
		if (topplePositionType23(w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | 1 | 1 | 1 | 99
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, 1, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(w, 1, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(wPlusOne, 1, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, 1, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, 2, 1, 1);
		if (topplePositionType24(w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType23(int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | crd | crd | 0 | 0 | 56
		int currentValue = currentVSlice.getFromPosition(crd, crd, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 0, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, 0, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 1, 0);
		if (topplePositionType33(crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue,
				greaterYNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | crd | crd | 1 | 0 | 106
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 1, 0);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, 1, 0);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, 1, 1);
		if (topplePositionType25(crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | crd | crd | 1 | 1 | 107
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, 1, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, 1, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(crd, crdMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(crd, crd, 2, 1);
		if (topplePositionType26(crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType24(int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice smallerVSlice = vSlices[0], currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | crd | crd | crd | 0 | 59
		int currentValue = currentVSlice.getFromPosition(crd, crd, crd, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, 0);
		int smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, 0);
		int greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, 1);
		if (topplePositionType34(crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerYNeighborValue,
				greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | crd | crd | crd | 1 | 111
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, 1);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, 1);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, 2);
		if (topplePositionType27(crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | crd | crd | crd | z | 166
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, z);
			smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, z);
			greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, zPlusOne);
			if (topplePositionType53(crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | crd | crd | crd | z | 112
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, z);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, z);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(crd, crd, crdMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(crd, crd, crd, zPlusOne);
		if (topplePositionType27(crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 4,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | crd | crd | crd | crd | 61
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(crd, crd, crd, crd);
		smallerVNeighborValue = smallerVSlice.getFromPosition(crd, crd, crd, crd);
		greaterWNeighborValue = currentVSlice.getFromPosition(crdPlusOne, crd, crd, crd);
		if (topplePositionType35(crd,currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType25(int w, int x, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int wMinusOne = w - 1, xPlusOne = x + 1, xMinusOne = x - 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | x | 0 | 0 | 78
		int currentValue = currentVSlice.getFromPosition(w, x, 0, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 0, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 0, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 0, 0);
		int smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 0, 0);
		int greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 1, 0);
		if (topplePositionType40(w, x, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue,
				greaterYNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 132
		//reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 0);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 0);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 0);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 0);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, x, 1, 1);
		if (topplePositionType28(w, x, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 133
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, x, 1, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, x, 1, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, xPlusOne, 1, 1);
		smallerXNeighborValue = currentVSlice.getFromPosition(w, xMinusOne, 1, 1);
		greaterYNeighborValue = currentVSlice.getFromPosition(w, x, 2, 1);
		if (topplePositionType29(w, x, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType26(int w, int crd, AnisotropicIntModel5DSlice[] vSlices, AnisotropicIntModel5DSlice[] newVSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1, wMinusOne = w - 1;
		boolean changed = false;
		AnisotropicIntModel5DSlice currentVSlice = vSlices[1], greaterVSlice = vSlices[2];		
		// v | w | crd | crd | 0 | 81
		int currentValue = currentVSlice.getFromPosition(w, crd, crd, 0);
		int greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 0);
		int smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 0);
		int greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 0);
		int smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 0);
		int greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 1);
		if (topplePositionType41(w, crd, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue,
				greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 137
		//reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, 1);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, 1);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, 1);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, 1);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, 2);		
		if (topplePositionType30(w, crd, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 188
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
			smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
			greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
			smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
			greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);		
			if (topplePositionType56(w, crd, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue,
					smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 138
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, z);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, z);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, z);
		smallerYNeighborValue = currentVSlice.getFromPosition(w, crd, crdMinusOne, z);
		greaterZNeighborValue = currentVSlice.getFromPosition(w, crd, crd, zPlusOne);		
		if (topplePositionType30(w, crd, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 83
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = greaterVSlice.getFromPosition(w, crd, crd, crd);
		smallerWNeighborValue = currentVSlice.getFromPosition(wMinusOne, crd, crd, crd);
		greaterXNeighborValue = currentVSlice.getFromPosition(w, crdPlusOne, crd, crd);
		if (topplePositionType42(w, crd, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newVSlices)) {
			changed = true;
		}
		return changed;
	}		

	private static boolean topplePositionType1(int currentValue, int gVValue, AnisotropicIntModel5DSlice newCurrentVSlice, AnisotropicIntModel5DSlice newGreaterVSlice) {
		boolean toppled = false;
		if (gVValue < currentValue) {
			int toShare = currentValue - gVValue;
			int share = toShare/11;
			if (share != 0) {
				toppled = true;
				newCurrentVSlice.addToPosition(0, 0, 0, 0, currentValue - toShare + share + toShare%11);
				newGreaterVSlice.addToPosition(0, 0, 0, 0, share);
			} else {
				newCurrentVSlice.addToPosition(0, 0, 0, 0, currentValue);
			}			
		} else {
			newCurrentVSlice.addToPosition(0, 0, 0, 0, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionType2(int currentValue, int gVValue, int sVValue, int gWValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 10;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 8;
			relevantNeighborCount += 8;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, 0, 0, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType3(int currentValue, int gVValue, int sWValue, int gXValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 8;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, 1, 0, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType4(int currentValue, int gVValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 6;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, 1, 1, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType5(int currentValue, int gVValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 1;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 5;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, 1, 1, 1, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType6(int currentValue, int gVValue, int sZValue, AnisotropicIntModel5DSlice newCurrentVSlice, AnisotropicIntModel5DSlice newGreaterVSlice) {
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gVValue < currentValue) {
				if (sZValue == gVValue) {
					//gv = sz < current
					int toShare = currentValue - gVValue; 
					int share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(1, 1, 1, 0, share + share);//one more for the symmetric position at the other side
					newCurrentVSlice.addToPosition(1, 1, 1, 1, currentValue - toShare + share + toShare%11);
					newGreaterVSlice.addToPosition(1, 1, 1, 1, share);
				} else if (sZValue < gVValue) {
					//sz < gv < current
					int toShare = currentValue - gVValue; 
					int share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(1, 1, 1, 0, share + share);
					newGreaterVSlice.addToPosition(1, 1, 1, 1, share);
					int currentRemainingValue = currentValue - 10*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/6;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(1, 1, 1, 0, share + share);
					newCurrentVSlice.addToPosition(1, 1, 1, 1, currentRemainingValue - toShare + share + toShare%6);
				} else {
					//gv < sz < current
					int toShare = currentValue - sZValue; 
					int share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(1, 1, 1, 0, share + share);
					newGreaterVSlice.addToPosition(1, 1, 1, 1, share);
					int currentRemainingValue = currentValue - 10*share;
					toShare = currentRemainingValue - gVValue; 
					share = toShare/6;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(1, 1, 1, 1, currentRemainingValue - toShare + share + toShare%6);
					newGreaterVSlice.addToPosition(1, 1, 1, 1, share);
				}
			} else {
				//sz < current <= gv
				int toShare = currentValue - sZValue; 
				int share = toShare/6;
				if (share != 0) {
					toppled = true;
				}
				newCurrentVSlice.addToPosition(1, 1, 1, 0, share + share);
				newCurrentVSlice.addToPosition(1, 1, 1, 1, currentValue - toShare + share + toShare%6);
			}
		} else {
			if (gVValue < currentValue) {
				//gv < current <= sz
				int toShare = currentValue - gVValue; 
				int share = toShare/6;
				if (share != 0) {
					toppled = true;
				}
				newCurrentVSlice.addToPosition(1, 1, 1, 1, currentValue - toShare + share + toShare%6);
				newGreaterVSlice.addToPosition(1, 1, 1, 1, share);
			} else {
				newCurrentVSlice.addToPosition(1, 1, 1, 1, currentValue);
			}
		}
		return toppled;
	}

	private static boolean topplePositionType7(int currentValue, int gVValue, int sVValue, int gWValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 8;
			relevantNeighborCount += 8;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, 0, 0, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType8(int w, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, 0, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType9(int coord, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType10(int coord, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType11(int coord, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType12(int w, int currentValue, int gVValue, int sWValue, int gXValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, 0, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType13(int w, int x, int currentValue, int gVValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType14(int w, int coord, int currentValue, int gVValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType15(int w, int coord, int currentValue, int gVValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType16(int coord, int currentValue, int gVValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType17(int coord, int y, int currentValue, int gVValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y + 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType18(int coord1, int coord2, int currentValue, int gVValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1 - 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2 + 1;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType19(int coord, int currentValue, int gVValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType20(int coord, int z, int currentValue, int gVValue, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType21(int coord, int currentValue, int gVValue, int sZValue, AnisotropicIntModel5DSlice newCurrentVSlice, AnisotropicIntModel5DSlice newGreaterVSlice) {
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gVValue < currentValue) {
				if (sZValue == gVValue) {
					//gv = sz < current
					int toShare = currentValue - gVValue; 
					int share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(coord, coord, coord, coord - 1, share);
					newCurrentVSlice.addToPosition(coord, coord, coord, coord, currentValue - toShare + share + toShare%11);
					newGreaterVSlice.addToPosition(coord, coord, coord, coord, share);
				} else if (sZValue < gVValue) {
					//sz < gv < current
					int coordMinusOne = coord - 1;
					int toShare = currentValue - gVValue; 
					int share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(coord, coord, coord, coordMinusOne, share);
					newGreaterVSlice.addToPosition(coord, coord, coord, coord, share);
					int currentRemainingValue = currentValue - 10*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/6;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(coord, coord, coord, coordMinusOne, share);
					newCurrentVSlice.addToPosition(coord, coord, coord, coord, currentRemainingValue - toShare + share + toShare%6);
				} else {
					//gv < sz < current
					int toShare = currentValue - sZValue; 
					int share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(coord, coord, coord, coord - 1, share);
					newGreaterVSlice.addToPosition(coord, coord, coord, coord, share);
					int currentRemainingValue = currentValue - 10*share;
					toShare = currentRemainingValue - gVValue; 
					share = toShare/6;
					if (share != 0) {
						toppled = true;
					}
					newCurrentVSlice.addToPosition(coord, coord, coord, coord, currentRemainingValue - toShare + share + toShare%6);
					newGreaterVSlice.addToPosition(coord, coord, coord, coord, share);
				}
			} else {
				//sz < current <= gv
				int toShare = currentValue - sZValue; 
				int share = toShare/6;
				if (share != 0) {
					toppled = true;
				}
				newCurrentVSlice.addToPosition(coord, coord, coord, coord - 1, share);
				newCurrentVSlice.addToPosition(coord, coord, coord, coord, currentValue - toShare + share + toShare%6);
			}
		} else {
			if (gVValue < currentValue) {
				//gv < current <= sz
				int toShare = currentValue - gVValue; 
				int share = toShare/6;
				if (share != 0) {
					toppled = true;
				}
				newCurrentVSlice.addToPosition(coord, coord, coord, coord, currentValue - toShare + share + toShare%6);
				newGreaterVSlice.addToPosition(coord, coord, coord, coord, share);
			} else {
				newCurrentVSlice.addToPosition(coord, coord, coord, coord, currentValue);
			}
		}
		return toppled;
	}

	private static boolean topplePositionType22(int w, int x, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType23(int w, int coord, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType24(int w, int coord, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType25(int coord, int y, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y + 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType26(int coord1, int coord2, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1 + 1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1 - 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2 + 1;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType27(int coord, int z, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType28(int w, int x, int y, int currentValue, int gVValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType29(int w, int x, int coord, int currentValue, int gVValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord + 1;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType30(int w, int coord, int z, int currentValue, int gVValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType31(int coord, int y, int z, int currentValue, int gVValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y + 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType32(int w, int currentValue, int gVValue, int sVValue, int gWValue, int sWValue, int gXValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, 0, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType33(int coord, int currentValue, int gVValue, int sVValue, int gWValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType34(int coord, int currentValue, int gVValue, int sVValue, int gWValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType35(int coord, int currentValue, int gVValue, int sVValue, int gWValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType36(int w, int x, int y, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType37(int w, int x, int coord, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord + 1;
			nc[4] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType38(int w, int coord, int z, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType39(int coord, int y, int z, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sVShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y + 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType40(int w, int x, int currentValue, int gVValue, int sWValue, int gXValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType41(int w, int coord, int currentValue, int gVValue, int sWValue, int gXValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType42(int w, int coord, int currentValue, int gVValue, int sWValue, int gXValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType43(int w, int x, int y, int z, int currentValue, int gVValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType44(int coord, int y, int currentValue, int gVValue, int sXValue, int gYValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y + 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType45(int coord1, int coord2, int currentValue, int gVValue, int sXValue, int gYValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1 - 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2 + 1;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType46(int coord, int z, int currentValue, int gVValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType47(int w, int x, int y, int z, int currentValue, int gVValue, int sVValue, int sVShareMultiplier, int gWValue, int gWShareMultiplier, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sVShareMultiplier;
			relevantNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gWShareMultiplier;
			relevantNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sWShareMultiplier;
			relevantNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gXShareMultiplier;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionType48(int w, int x, int currentValue, int gVValue, int sVValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType49(int w, int coord, int currentValue, int gVValue, int sVValue, int gWValue, int sWValue, int gXValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType50(int w, int coord, int currentValue, int gVValue, int sVValue, int gWValue, int sWValue, int gXValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType51(int coord, int y, int currentValue, int gVValue, int sVValue, int gWValue, int sXValue, int gYValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y + 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType52(int coord1, int coord2, int currentValue, int gVValue, int sVValue, int gWValue, int sXValue, int gYValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1 + 1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1 - 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2 + 1;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType53(int coord, int z, int currentValue, int gVValue, int sVValue, int gWValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType54(int w, int x, int y, int currentValue, int gVValue, int sWValue, int gXValue, int sXValue, int gYValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType55(int w, int x, int coord, int currentValue, int gVValue, int sWValue, int gXValue, int sXValue, int gYValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord + 1;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType56(int w, int coord, int z, int currentValue, int gVValue, int sWValue, int gXValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType57(int coord, int y, int z, int currentValue, int gVValue, int sXValue, int gYValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y + 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y - 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType58(int w, int x, int y, int currentValue, int gVValue, int sVValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType59(int w, int x, int coord, int currentValue, int gVValue, int sVValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = coord;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord + 1;
			nc[4] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType60(int w, int coord, int z, int currentValue, int gVValue, int sVValue, int gWValue, int sWValue, int gXValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord + 1;
			nc[3] = coord;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord - 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType61(int coord, int y, int z, int currentValue, int gVValue, int sVValue, int gWValue, int sXValue, int gYValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y + 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y - 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, coord, coord, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType62(int w, int x, int y, int z, int currentValue, int gVValue, int sWValue, int gXValue, int sXValue, int gYValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType63(int w, int x, int y, int z, int currentValue, int gVValue, int sVValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, AnisotropicIntModel5DSlice[] newVSlices) {
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z + 1;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z - 1;
			relevantNeighborCount++;
		}
		return topplePosition(newVSlices, currentValue, w, x, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantNeighborCount);
	}

	private static boolean topplePosition(AnisotropicIntModel5DSlice[] newVSlices, int value, int w, int x, int y, int z, int[] neighborValues,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords);
			toppled = topplePositionSortedNeighbors(newVSlices, value, w, x, y, z, neighborValues, 
					neighborCoords, 3);
			break;
		case 2:
			int n0Val = neighborValues[0], n1Val = neighborValues[1];
			int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
			if (n0Val == n1Val) {
				//n0Val = n1Val < value
				int toShare = value - n0Val; 
				int share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				newVSlices[1].addToPosition(w, x, y, z, value - toShare + share + toShare%3);
			} else if (n0Val < n1Val) {
				//n0Val < n1Val < value
				int toShare = value - n1Val; 
				int share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				int currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				newVSlices[1].addToPosition(w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			} else {
				//n1Val < n0Val < value
				int toShare = value - n0Val; 
				int share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				int currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				newVSlices[1].addToPosition(w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			}				
			break;
		case 1:
			int toShare = value - neighborValues[0];
			int share = toShare/2;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%2 + share;
				int[] nc = neighborCoords[0];
				newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share);
			}
			//no break
		case 0:
			newVSlices[1].addToPosition(w, x, y, z, value);
			break;
		default: //10, 9, 8, 7, 6, 5, 4
			Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords);
			toppled = topplePositionSortedNeighbors(newVSlices, value, w, x, y, z, neighborValues, neighborCoords, 
					neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(AnisotropicIntModel5DSlice[] newVSlices, int value, int w, int x, int y, int z, 
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
				newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share);
			}
		}
		int previousNeighborValue = neighborValue;
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
						newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newVSlices[1].addToPosition(w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(AnisotropicIntModel5DSlice[] newVSlices, int value, int w, int x, int y, int z, int[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
		case 3:
			Utils.sort3NeighborsByValueDesc(asymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
			toppled = topplePositionSortedNeighbors(newVSlices, value, w, x, y, z, asymmetricNeighborValues, 
					asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
			break;
		case 2:
			int n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
			int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
			int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
			int shareCount = neighborCount + 1;
			if (n0Val == n1Val) {
				//n0Val = n1Val < value
				int toShare = value - n0Val; 
				int share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				newVSlices[1].addToPosition(w, x, y, z, value - toShare + share + toShare%shareCount);
			} else if (n0Val < n1Val) {
				//n0Val < n1Val < value
				int toShare = value - n1Val; 
				int share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				int currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				newVSlices[1].addToPosition(w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
			} else {
				//n1Val < n0Val < value
				int toShare = value - n0Val; 
				int share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				int currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				newVSlices[1].addToPosition(w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
				newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share * asymmetricNeighborShareMultipliers[0]);
			}
			//no break
		case 0:
			newVSlices[1].addToPosition(w, x, y, z, value);
			break;
		default: //10, 9, 8, 7, 6, 5, 4
			Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, asymmetricNeighborValues, asymmetricNeighborCoords, 
					asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
			toppled = topplePositionSortedNeighbors(newVSlices, value, w, x, y, z, asymmetricNeighborValues, asymmetricNeighborCoords, 
					asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(AnisotropicIntModel5DSlice[] newVSlices, int value, int w, int x, int y, int z, int[] asymmetricNeighborValues,
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
				newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share * asymmetricNeighborShareMultipliers[j]);
			}
		}
		int previousNeighborValue = neighborValue;
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
						newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share * asymmetricNeighborShareMultipliers[j]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newVSlices[1].addToPosition(w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(AnisotropicIntModel5DSlice[] newVSlices, int value, int w, int x, int y, int z, int[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords, neighborShareMultipliers);
			toppled = topplePositionSortedNeighbors(newVSlices, value, w, x, y, z, neighborValues, 
					neighborCoords, neighborShareMultipliers, 3);
			break;
		case 2:
			int n0Val = neighborValues[0], n1Val = neighborValues[1];
			int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
			int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
			if (n0Val == n1Val) {
				//n0Val = n1Val < value
				int toShare = value - n0Val; 
				int share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				newVSlices[1].addToPosition(w, x, y, z, value - toShare + share + toShare%3);
			} else if (n0Val < n1Val) {
				//n0Val < n1Val < value
				int toShare = value - n1Val; 
				int share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				int currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				newVSlices[1].addToPosition(w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			} else {
				//n1Val < n0Val < value
				int toShare = value - n0Val; 
				int share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				int currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				newVSlices[1].addToPosition(w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			}				
			break;
		case 1:
			int toShare = value - neighborValues[0];
			int share = toShare/2;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%2 + share;
				int[] nc = neighborCoords[0];
				newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share * neighborShareMultipliers[0]);
			}
			//no break
		case 0:
			newVSlices[1].addToPosition(w, x, y, z, value);
			break;
		default: //10, 9, 8, 7, 6, 5, 4
			Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords, 
					neighborShareMultipliers);
			toppled = topplePositionSortedNeighbors(newVSlices, value, w, x, y, z, neighborValues, neighborCoords, 
					neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(AnisotropicIntModel5DSlice[] newVSlices, int value, int w, int x, int y, int z, int[] neighborValues,
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
				newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share * neighborShareMultipliers[j]);
			}
		}
		int previousNeighborValue = neighborValue;
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
						newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share * neighborShareMultipliers[j]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newVSlices[1].addToPosition(w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(AnisotropicIntModel5DSlice[] newVSlices, int value, int w, int x, int y, int z, int[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
		case 3:
			Utils.sort3NeighborsByValueDesc(asymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts);
			toppled = topplePositionSortedNeighbors(newVSlices, value, w, x, y, z, asymmetricNeighborValues, 
					asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
			break;
		case 2:
			int n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
			int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
			int shareCount = neighborCount + 1;
			if (n0Val == n1Val) {
				//n0Val = n1Val < value
				int toShare = value - n0Val; 
				int share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				newVSlices[1].addToPosition(w, x, y, z, value - toShare + share + toShare%shareCount);
			} else if (n0Val < n1Val) {
				//n0Val < n1Val < value
				int toShare = value - n1Val; 
				int share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				int currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				newVSlices[1].addToPosition(w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
			} else {
				//n1Val < n0Val < value
				int toShare = value - n0Val; 
				int share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				int currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newVSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				newVSlices[1].addToPosition(w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
				newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share);
			}
			//no break
		case 0:
			newVSlices[1].addToPosition(w, x, y, z, value);
			break;
		default: //10, 9, 8, 7, 6, 5, 4
			Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, asymmetricNeighborValues, asymmetricNeighborCoords, 
					asymmetricNeighborSymmetryCounts);
			toppled = topplePositionSortedNeighbors(newVSlices, value, w, x, y, z, asymmetricNeighborValues, asymmetricNeighborCoords, 
					asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(AnisotropicIntModel5DSlice[] newVSlices, int value, int w, int x, int y, int z, int[] asymmetricNeighborValues,
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
				newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share);
			}
		}
		int previousNeighborValue = neighborValue;
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
						newVSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], nc[4], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newVSlices[1].addToPosition(w, x, y, z, value);
		return toppled;
	}	

	private void processGridBlock(SizeLimitedAnisotropicIntModel5DBlock block) throws Exception {
		if (block.minV <= maxV) {
			IntModel5D subBlock = null;
			int maxW = getMaxW();
			int maxX = getMaxX();
			int maxY = getMaxY();
			int maxZ = getMaxZ();
			if (block.maxV > maxV) {
				if (block.maxV > maxW || block.maxV > maxX || block.maxV > maxY || block.maxV > maxZ) {
					subBlock = new IntSubModel5D(block, block.minV, maxV, 0, maxW, 0, maxX, 0, maxY, 0, maxZ);
				} else {
					subBlock = new IntSubModel5DWithVBounds(block, block.minV, maxV);
				}
			} else {
				if (block.maxV > maxW || block.maxV > maxX || block.maxV > maxY || block.maxV > maxZ) {
					subBlock = new IntSubModel5D(block, block.minV, block.maxV, 0, maxW, 0, maxX, 0, maxY, 0, maxZ);
				} else {
					subBlock = new ImmutableIntModel5D(block);
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
			if (gridBlockA.minV < gridBlockB.minV) {
				SizeLimitedAnisotropicIntModel5DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			}
			if (gridBlockB.minV == 0 && gridBlockA.maxV >= maxV
					&& gridBlockB.maxV == gridBlockA.minV - 1) {//two blocks
				processGridBlock(gridBlockB);
				processGridBlock(gridBlockA);
			} else { //more than two blocks
				//I keep the one closest to zero (in case it can be reused when computing the next step)
				//and save the other
				if (!readOnlyMode)
					saveGridBlock(gridBlockA);
				if (gridBlockB.minV > 0) {
					gridBlockA.free();
					gridBlockA = loadGridBlock(0);
					processGridBlock(gridBlockA);
					while (gridBlockA.maxV < gridBlockB.minV - 1) {
						int nextX = gridBlockA.maxV + 1;
						gridBlockA.free();
						gridBlockA = loadGridBlock(nextX);
						processGridBlock(gridBlockA);
					}
				}
				processGridBlock(gridBlockB);
				int previousMaxX = gridBlockB.maxV;			
				while (previousMaxX < maxV) {
					int nextX = previousMaxX + 1;
					gridBlockA.free();
					gridBlockA = loadGridBlock(nextX);
					processGridBlock(gridBlockA);
					previousMaxX = gridBlockA.maxV;
				}
			}			
		}
		triggerAfterProcessing();
	}

	private void slideGridSlices(AnisotropicIntModel5DSlice[] newGridSlices, AnisotropicIntModel5DSlice newSlice) {
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
		return getName() + "/5D/" + initialValue + "/asymmetric_section";
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
	public int getInitialValue() {
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
		properties.put("maxV", maxV);
		properties.put("maxGridBlockSize", maxGridBlockSize);
		return properties;
	}

	private void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (int) properties.get("initialValue");
		step = (long) properties.get("step");
		maxV = (int) properties.get("maxV");
		maxGridBlockSize = (long) properties.get("maxGridBlockSize");
	}

	@Override
	public int getMaxV() {
		return maxV;
	}
	
	@Override
	public int getMaxW() {
		return (int) Math.min((step + 2)/2 - 1, maxV);
	}
	
	@Override
	public int getMaxX() {
		return getMaxW();
	}
	
	@Override
	public int getMaxY() {
		return getMaxW();
	}
	
	@Override
	public int getMaxZ() {
		return getMaxW();
	}

}