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
import cellularautomata.grid4d.AnisotropicGrid4DA;
import cellularautomata.grid4d.AnisotropicIntGrid4DSlice;
import cellularautomata.grid4d.ImmutableIntGrid4D;
import cellularautomata.grid4d.IntGrid4D;
import cellularautomata.grid4d.IntSubGrid4DWithWBounds;
import cellularautomata.grid4d.SizeLimitedAnisotropicIntGrid4DBlock;
import cellularautomata.model4d.ActionableModel4D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class IntAether4DAsymmetricSectionSwap extends ActionableModel4D<IntGrid4D> implements AnisotropicGrid4DA {
	
	public static final int MAX_INITIAL_VALUE = Integer.MAX_VALUE;
	public static final int MIN_INITIAL_VALUE = -613566757;

	private static final String PROPERTIES_BACKUP_FILE_NAME = "properties.ser";
	private static final String GRID_FOLDER_NAME = "grid";
	
	private static final byte LEFT = 0;
	private static final byte CENTER = 1;
	private static final byte RIGHT = 2;
	
	private SizeLimitedAnisotropicIntGrid4DBlock gridBlockA;
	private SizeLimitedAnisotropicIntGrid4DBlock gridBlockB;
	private int initialValue;
	private long step;
	private int maxW;
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
	public IntAether4DAsymmetricSectionSwap(int initialValue, long maxGridHeapSize, String folderPath) throws Exception {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of int type
			throw new IllegalArgumentException("Initial value cannot be smaller than -2,635,249,153,387,078,803. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		maxGridBlockSize = maxGridHeapSize/2;
		gridBlockA = new SizeLimitedAnisotropicIntGrid4DBlock(0, maxGridBlockSize);
		if (gridBlockA.maxW < 7) {
			throw new IllegalArgumentException("Passed heap space limit is too small.");
		}
		gridBlockA.setValueAtPosition(0, 0, 0, 0, initialValue);
		maxW = 5;
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
	public IntAether4DAsymmetricSectionSwap(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
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
		if (maxW > gridBlockA.maxW) {
			gridBlockB = loadGridBlock(gridBlockA.maxW + 1);
		}
		readWriteGridFolder = new File(folderPath + File.separator + getSubfolderPath() + File.separator + GRID_FOLDER_NAME);
	}

	private SizeLimitedAnisotropicIntGrid4DBlock loadGridBlockSafe(int minW) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedAnisotropicIntGrid4DBlock gridBlock = null;
		File gridBlockFile = null;
		for (int i = 0; i < files.length && !found; i++) {
			File currentFile = files[i];
			String fileName = currentFile.getName();
			int fileMinW;
			try {
				//"minW=".length() == 5
				fileMinW = Integer.parseInt(fileName.substring(5, fileName.indexOf("_")));
				if (fileMinW == minW) {
					found = true;
					gridBlockFile = currentFile;
				}
			} catch (NumberFormatException ex) {
				
			}
		}
		if (found) {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(gridBlockFile));
			gridBlock = (SizeLimitedAnisotropicIntGrid4DBlock) in.readObject();
			in.close();
		}
		return gridBlock;
	}
	
	private SizeLimitedAnisotropicIntGrid4DBlock loadGridBlock(int minW) throws IOException, ClassNotFoundException {
		SizeLimitedAnisotropicIntGrid4DBlock gridBlock = loadGridBlockSafe(minW);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minW=" + minW + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private void saveGridBlock(SizeLimitedAnisotropicIntGrid4DBlock gridBlock) throws FileNotFoundException, IOException {
		String name = "minW=" + gridBlock.minW + "_maxW=" + gridBlock.maxW + ".ser";
		String pathName = this.gridFolder.getPath() + File.separator + name;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}
	
	private SizeLimitedAnisotropicIntGrid4DBlock loadOrBuildGridBlock(int minW) throws ClassNotFoundException, IOException {		
		SizeLimitedAnisotropicIntGrid4DBlock gridBlock = loadGridBlockSafe(minW);
		if (gridBlock != null) {
			return gridBlock;
		} else {
			return new SizeLimitedAnisotropicIntGrid4DBlock(minW, maxGridBlockSize);
		}
	}

	@Override
	public boolean nextStep() throws Exception {
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
		int[] relevantAsymmetricNeighborValues = new int[8];
		int[][] relevantAsymmetricNeighborCoords = new int[8][4];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[8];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[8];// to compensate for omitted symmetric positions		
		AnisotropicIntGrid4DSlice[] newWSlices = 
				new AnisotropicIntGrid4DSlice[] {
						null, 
						new AnisotropicIntGrid4DSlice(0), 
						new AnisotropicIntGrid4DSlice(1)};
		if (gridBlockA.minW > 0) {
			if (gridBlockB != null && gridBlockB.minW == 0) {
				SizeLimitedAnisotropicIntGrid4DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			} else {
				saveGridBlock(gridBlockA);
				gridBlockA.free();
				gridBlockA = loadGridBlock(0);
			}
		}
		int currentMaxW = maxW;//it can change during computing
		boolean anySlicePositionToppled = toppleRangeFromZeroToFour(gridBlockA, newWSlices, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts);
		gridChanged = gridChanged || anySlicePositionToppled;
		int w = 5;
		AnisotropicIntGrid4DSlice[] wSlices = new AnisotropicIntGrid4DSlice[] {
				null,
				gridBlockA.getSlice(4),
				gridBlockA.getSlice(5)};
		while (w <= currentMaxW) {
			while (w <= currentMaxW && w < gridBlockA.maxW) {
				slideGridSlices(newWSlices, new AnisotropicIntGrid4DSlice(w + 1));
				anySlicePositionToppled = toppleSliceBeyondFour(gridBlockA, w, wSlices, newWSlices, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(w - 1, newWSlices[LEFT]);
				w++;
			}
			if (w <= currentMaxW) {
				if (gridBlockB != null) {
					if (gridBlockB.minW != w + 1) {
						saveGridBlock(gridBlockB);
						gridBlockB.free();
						gridBlockB = loadOrBuildGridBlock(w + 1);
					}
				} else {
					gridBlockB = loadOrBuildGridBlock(w + 1);
				}
				slideGridSlices(newWSlices, new AnisotropicIntGrid4DSlice(w + 1));
				anySlicePositionToppled = toppleLastSliceOfBlock(gridBlockA, gridBlockB, w, wSlices, newWSlices, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(w - 1, newWSlices[LEFT]);
				w++;
				if (w <= currentMaxW) {
					slideGridSlices(newWSlices, new AnisotropicIntGrid4DSlice(w + 1));
					anySlicePositionToppled = toppleFirstSliceOfBlock(gridBlockA, gridBlockB, w, wSlices, newWSlices, 
							relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
					gridChanged = gridChanged || anySlicePositionToppled;
					gridBlockA.setSlice(w - 1, newWSlices[LEFT]);
					processGridBlock(gridBlockA);
					SizeLimitedAnisotropicIntGrid4DBlock swp = gridBlockA;
					gridBlockA = gridBlockB;
					gridBlockB = swp;
					w++;
				}
			}
		}
		gridBlockA.setSlice(currentMaxW, newWSlices[CENTER]);
		if (currentMaxW == gridBlockA.maxW) {
			processGridBlock(gridBlockA);
			gridBlockB.setSlice(currentMaxW + 1, newWSlices[RIGHT]);
			processGridBlock(gridBlockB);
		} else {
			gridBlockA.setSlice(currentMaxW + 1, newWSlices[RIGHT]);
			processGridBlock(gridBlockA);
		}
		step++;
		triggerAfterProcessing();
		return gridChanged;
	}
	
	private boolean toppleRangeFromZeroToFour(SizeLimitedAnisotropicIntGrid4DBlock gridBlock,
			AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean changed = false;
		AnisotropicIntGrid4DSlice smallerWSlice = null, 
				currentWSlice = gridBlock.getSlice(0), 
				greaterWSlice = gridBlock.getSlice(1);
		AnisotropicIntGrid4DSlice newSmallerWSlice = null, 
				newCurrentWSlice = newWSlices[1], 
				newGreaterWSlice = newWSlices[2];
		// w = 0, x = 0, y = 0, z = 0
		int currentValue = currentWSlice.getFromPosition(0, 0, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(0, 0, 0);
		if (topplePositionType1(currentValue, greaterWNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
		}
		//w slice transition
		// smallerWSlice = currentWSlice; // not needed here
		currentWSlice = greaterWSlice;
		greaterWSlice = gridBlock.getSlice(2);
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = new AnisotropicIntGrid4DSlice(3);
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		// w = 1, x = 0, y = 0, z = 0
		// reuse values obtained previously
		int smallerWNeighborValue = currentValue;
		currentValue = greaterWNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(0, 0, 0);
		int greaterXNeighborValue = currentWSlice.getFromPosition(1, 0, 0);
		if (topplePositionType2(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 1, x = 1, y = 0, z = 0
		// reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 0, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(1, 1, 0);
		if (topplePositionType3(currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 1, x = 1, y = 1, z = 0
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 1, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(1, 1, 1);
		if (topplePositionType4(currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 1, x = 1, y = 1, z = 1
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 1, 1);
		if (topplePositionType5(currentValue, greaterWNeighborValue, smallerZNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
		}
		//w slice transition
		gridBlock.setSlice(0, newSmallerWSlice);// free old grid progressively to save memory
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = gridBlock.getSlice(3);
		AnisotropicIntGrid4DSlice[] wSlices = new AnisotropicIntGrid4DSlice[] { smallerWSlice, currentWSlice, greaterWSlice};
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = new AnisotropicIntGrid4DSlice(4);
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		// w = 2, x = 0, y = 0, z = 0
		currentValue = currentWSlice.getFromPosition(0, 0, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(0, 0, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(0, 0, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(1, 0, 0);
		if (topplePositionType6(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 1, y = 0, z = 0
		// reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 0, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(1, 0, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(2, 0, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(1, 1, 0);
		if (topplePositionType7(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, 
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 1, y = 1, z = 0
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 1, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(1, 1, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(2, 1, 0);
		greaterZNeighborValue = currentWSlice.getFromPosition(1, 1, 1);
		if (topplePositionType8(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 1, y = 1, z = 1
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 1, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(1, 1, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(2, 1, 1);
		if (topplePositionType9(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 2, y = 0, z = 0
		currentValue = currentWSlice.getFromPosition(2, 0, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 0, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(1, 0, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(2, 1, 0);
		if (topplePositionType10(2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 2, y = 1, z = 0
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 1, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(1, 1, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(2, 2, 0);
		greaterZNeighborValue = currentWSlice.getFromPosition(2, 1, 1);
		if (topplePositionType11(2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 2, y = 1, z = 1
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 1, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(1, 1, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(2, 2, 1);
		if (topplePositionType12(2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 2, y = 2, z = 0
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;		
		currentValue = currentWSlice.getFromPosition(2, 2, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 0);
		if (topplePositionType13(2, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 2, y = 2, z = 1
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(2, 1, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(2, 2, 2);
		if (topplePositionType14(2, 1, currentValue, greaterWNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 4, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// 02 | 02 | 02 | 02 | 15
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 2);
		if (topplePositionType15(2, currentValue, greaterWNeighborValue, smallerZNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
		}		
		//w slice transition
		gridBlock.setSlice(1, newSmallerWSlice);
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = gridBlock.getSlice(4);
		wSlices[0] = smallerWSlice;
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = new AnisotropicIntGrid4DSlice(5);
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		if (toppleRangeType1(wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
	// 03 | 02 | 00 | 00 | 19
		// reuse values obtained previously
		currentValue = currentWSlice.getFromPosition(2, 0, 0);;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 0, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 0, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 0, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(1, 0, 0);;
		greaterYNeighborValue = currentWSlice.getFromPosition(2, 1, 0);
		if (topplePositionType7(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, 
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	// 03 | 02 | 01 | 00 | 20
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 1, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 1, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 1, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(1, 1, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(2, 2, 0);
		greaterZNeighborValue = currentWSlice.getFromPosition(2, 1, 1);
		if (topplePositionType16(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	// 03 | 02 | 01 | 01 | 21
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 1, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 1, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 1, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(1, 1, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(2, 2, 1);
		if (topplePositionType17(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	// 03 | 02 | 02 | 00 | 22
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentWSlice.getFromPosition(2, 2, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 2, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 2, 0);
		if (topplePositionType8(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	// 03 | 02 | 02 | 01 | 23
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 2, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 2, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(2, 1, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(2, 2, 2);
		if (topplePositionType18(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	// 03 | 02 | 02 | 02 | 24
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 2);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 2, 2);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 2, 2);
		if (topplePositionType9(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		if (toppleRangeType2(3, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
	// 03 | 03 | 02 | 00 | 27
		currentValue = currentWSlice.getFromPosition(3, 2, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 0);
		smallerYNeighborValue = currentWSlice.getFromPosition(3, 1, 0);
		greaterZNeighborValue = currentWSlice.getFromPosition(3, 2, 1);
		if (topplePositionType11(3, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	// 03 | 03 | 02 | 01 | 28
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(3, 1, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(3, 2, 2);
		if (topplePositionType19(3, 2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	// 03 | 03 | 02 | 02 | 29
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 2);
		smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 2);
		greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 2);
		if (topplePositionType12(3, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		if (toppleRangeType3(3, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		//w slice transition
		gridBlock.setSlice(2, newSmallerWSlice);
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = gridBlock.getSlice(5);
		wSlices[0] = smallerWSlice;
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = new AnisotropicIntGrid4DSlice(6);
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		if (toppleRangeType4(wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType5(3, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
	// 04 | 03 | 02 | 00 | 40
		currentValue = currentWSlice.getFromPosition(3, 2, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(3, 2, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(4, 2, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 0);
		smallerYNeighborValue = currentWSlice.getFromPosition(3, 1, 0);
		greaterZNeighborValue = currentWSlice.getFromPosition(3, 2, 1);
		if (topplePositionType16(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	// 04 | 03 | 02 | 01 | 41
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(3, 2, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(4, 2, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(3, 1, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(3, 2, 2);
		if (topplePositionType23(3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
			changed = true;
		}
	// 04 | 03 | 02 | 02 | 42
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 2);
		smallerWNeighborValue = smallerWSlice.getFromPosition(3, 2, 2);
		greaterXNeighborValue = currentWSlice.getFromPosition(4, 2, 2);
		smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 2);
		greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 2);
		if (topplePositionType17(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		if (toppleRangeType6(3, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType7(4, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		gridBlock.setSlice(3, newSmallerWSlice);	
		return changed;
	}

	private boolean toppleSliceBeyondFour(SizeLimitedAnisotropicIntGrid4DBlock gridBlock, int w, AnisotropicIntGrid4DSlice[] wSlices, 
			AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondFour(gridBlock, gridBlock, gridBlock, w, wSlices, newWSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleLastSliceOfBlock(SizeLimitedAnisotropicIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid4DBlock rightGridBlock,
			int w, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondFour(leftGridBlock, leftGridBlock, rightGridBlock, w, wSlices, newWSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleFirstSliceOfBlock(SizeLimitedAnisotropicIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid4DBlock rightGridBlock,
			int w, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		return toppleSliceBeyondFour(leftGridBlock, rightGridBlock, rightGridBlock, w, wSlices, newWSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers,	relevantAsymmetricNeighborSymmetryCounts);
	}
	
	private boolean toppleSliceBeyondFour(SizeLimitedAnisotropicIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid4DBlock centerGridBlock, 
			SizeLimitedAnisotropicIntGrid4DBlock rightGridBlock, int w, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, 
			int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean anyToppled = false;
		int wMinusOne = w - 1, wMinusTwo = w - 2, wMinusThree = w - 3, wPlusOne = w + 1;
		AnisotropicIntGrid4DSlice smallerWSlice = wSlices[1], 
				currentWSlice = wSlices[2], 
				greaterWSlice = rightGridBlock.getSlice(wPlusOne);
		wSlices[0] = smallerWSlice;
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		if (toppleRangeType4(wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			anyToppled = true;
		}
		if (toppleRangeType8(3, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			anyToppled = true;
		}
//  w | 03 | 02 | 00 | 53
		int currentValue = currentWSlice.getFromPosition(3, 2, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 0);
		int smallerWNeighborValue = smallerWSlice.getFromPosition(3, 2, 0);
		int greaterXNeighborValue = currentWSlice.getFromPosition(4, 2, 0);
		int smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 0);
		int smallerYNeighborValue = currentWSlice.getFromPosition(3, 1, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(3, 2, 1);
		if (topplePositionType16(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			anyToppled = true;
		}
//  w | 03 | 02 | 01 | 54
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(3, 2, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(4, 2, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(3, 1, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(3, 2, 2);
		if (topplePositionType23(3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
			anyToppled = true;
		}
//  w | 03 | 02 | 02 | 55
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(3, 2, 2);
		smallerWNeighborValue = smallerWSlice.getFromPosition(3, 2, 2);
		greaterXNeighborValue = currentWSlice.getFromPosition(4, 2, 2);
		smallerXNeighborValue = currentWSlice.getFromPosition(2, 2, 2);
		greaterYNeighborValue = currentWSlice.getFromPosition(3, 3, 2);
		if (topplePositionType17(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			anyToppled = true;
		}
		if (toppleRangeType9(3, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			anyToppled = true;
		}
		int x = 4, xPlusOne = x + 1, xMinusOne = x - 1;
		for (int xMinusTwo = x - 2; x != wMinusOne; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
			if (toppleRangeType8(x, wSlices, newWSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				anyToppled = true;
			}
//  w |  x | 02 | 00 | 67
			currentValue = currentWSlice.getFromPosition(x, 2, 0);
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, 2, 0);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, 2, 0);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 2, 0);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 2, 0);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, 3, 0);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, 1, 0);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, 2, 1);
			if (topplePositionType27(x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
//  w |  x | 02 | 01 | 68
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, 2, 1);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, 2, 1);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 2, 1);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 2, 1);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, 3, 1);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, 1, 1);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, 2, 2);
			if (topplePositionType23(x, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				anyToppled = true;
			}
//  w |  x | 02 | 02 | 69
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, 2, 2);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, 2, 2);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 2, 2);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 2, 2);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, 3, 2);
			if (topplePositionType28(x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
			int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
			for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
//  w |  x |  y | 00 | 67
				currentValue = currentWSlice.getFromPosition(x, y, 0);
				greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 0);
				smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, 0);
				greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, 0);
				smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 0);
				greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 0);
				smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 0);
				greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 1);
				if (topplePositionType27(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					anyToppled = true;
				}
//  w |  x |  y | 01 | 77
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 1);
				smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, 1);
				greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, 1);
				smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 1);
				greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 1);
				smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 1);
				greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 2);
				if (topplePositionType23(x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					anyToppled = true;
				}
				int z = 2, zPlusOne = z + 1;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
//  w |  x |  y |  z | 81
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
					smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
					greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
					smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
					greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
					smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
					greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
					if (topplePositionType31(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
							smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, newWSlices)) {
						anyToppled = true;
					}
				}
//  w |  x |  y |  z | 78
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
				smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
				greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
				smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
				greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
				smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
				greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
				if (topplePositionType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					anyToppled = true;
				}
//  w |  x |  y |++z | 69
				z = zPlusOne;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
				smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
				greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
				smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
				greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
				if (topplePositionType28(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					anyToppled = true;
				}
			}
//  w |  x |  y | 00 | 53
			currentValue = currentWSlice.getFromPosition(x, y, 0);
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 0);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, 0);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, 0);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 0);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 0);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 0);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 1);
			if (topplePositionType16(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
//  w |  x |  y | 01 | 70
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 1);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, 1);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, 1);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 1);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 1);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 1);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 2);
			if (topplePositionType23(x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				anyToppled = true;
			}
			int z = 2, zPlusOne = z + 1;
			for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
//  w |  x |  y |  z | 79
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
				smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
				greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
				smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
				greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
				smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
				greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
				if (topplePositionType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					anyToppled = true;
				}
			}
//  w |  x |  y |  z | 71
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
			if (topplePositionType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				anyToppled = true;
			}
//  w |  x |  y |++z | 55
			z = zPlusOne;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
			if (topplePositionType17(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
			if (toppleRangeType9(x, wSlices, newWSlices, relevantAsymmetricNeighborValues,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				anyToppled = true;
			}
		}
		if (toppleRangeType5(x, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			anyToppled = true;
		}
//  w |  x | 02 | 00 | 58
		currentValue = currentWSlice.getFromPosition(x, 2, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 2, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, 2, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 2, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 2, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 3, 0);
		smallerYNeighborValue = currentWSlice.getFromPosition(x, 1, 0);
		greaterZNeighborValue = currentWSlice.getFromPosition(x, 2, 1);
		if (topplePositionType16(x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			anyToppled = true;
		}
//  w |  x | 02 | 01 | 59
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 2, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, 2, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 2, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 2, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 3, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(x, 1, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(x, 2, 2);
		if (topplePositionType23(x, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
			anyToppled = true;
		}
//  w |  x | 02 | 02 | 60
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 2, 2);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, 2, 2);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 2, 2);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 2, 2);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 3, 2);
		if (topplePositionType17(x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			anyToppled = true;
		}
		int y = 3, yPlusOne = y + 1, yMinusOne = y - 1;
		for (; y != wMinusTwo; yMinusOne = y, y = yPlusOne, yPlusOne++) {
//  w |  x |  y | 00 | 58
			currentValue = currentWSlice.getFromPosition(x, y, 0);
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 0);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, 0);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, 0);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 0);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 0);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 0);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 1);
			if (topplePositionType16(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
//  w |  x |  y | 01 | 73
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 1);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, 1);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, 1);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 1);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 1);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 1);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 2);
			if (topplePositionType23(x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				anyToppled = true;
			}
			int z = 2, zPlusOne = z + 1;
			for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
//  w |  x |  y |  z | 80
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
				smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
				greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
				smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
				greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
				smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
				greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
				if (topplePositionType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					anyToppled = true;
				}
			}
//  w |  x |  y |  z | 74
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
			if (topplePositionType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				anyToppled = true;
			}
//  w |  x |  y |++z | 60
			z = zPlusOne;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
			if (topplePositionType17(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
		}
//  w |  x |  y | 00 | 40
		currentValue = currentWSlice.getFromPosition(x, y, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 0);
		smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 0);
		greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 1);
		if (topplePositionType16(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			anyToppled = true;
		}
//  w |  x |  y | 01 | 61
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 2);
		if (topplePositionType23(x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
			anyToppled = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != wMinusThree; z = zPlusOne, zPlusOne++) {
//  w |  x |  y |  z | 75
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
			smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
			greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
			if (topplePositionType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				anyToppled = true;
			}
		}
//  w |  x |  y |  z | 62
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
		smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
		greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
		if (topplePositionType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
			anyToppled = true;
		}
//  w |  x |  y |++z | 42
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, y, z);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, y, z);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
		if (topplePositionType17(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			anyToppled = true;
		}			
		if (toppleRangeType6(x, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			anyToppled = true;
		}
		xMinusOne = x;
		x = xPlusOne;
		for (y = 3, yMinusOne = y - 1, yPlusOne = y + 1; y != wMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
//  w |  x |  y | 00 | 45
			currentValue = currentWSlice.getFromPosition(x, y, 0);
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 0);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 0);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 0);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 0);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 1);
			if (topplePositionType24(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
					greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
//  w |  x |  y | 01 | 64
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 1);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 1);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 1);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 1);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 2);
			if (topplePositionType19(x, y, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
			for (z = 2, zPlusOne = z + 1; z != yMinusOne; z = zPlusOne, zPlusOne++) {
//  w |  x |  y |  z | 76
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
				smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
				greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
				smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
				greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
				if (topplePositionType30(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords,
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					anyToppled = true;
				}
			}
//  w |  x |  y |  z | 65
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
			if (topplePositionType19(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
//  w |  x |  y |++z | 47
			z = zPlusOne;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
			if (topplePositionType25(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				anyToppled = true;
			}
		}
		if (toppleRangeType7(x, wSlices, newWSlices, relevantAsymmetricNeighborValues,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			anyToppled = true;
		}
		if (anyToppled && w > maxW - 1) {
			maxW++;
		}
		return anyToppled;
	}
	
	private static boolean toppleRangeType1(AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		boolean changed = false;
		AnisotropicIntGrid4DSlice smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
	//  w | 00 | 00 | 00 | 06
		int currentValue = currentWSlice.getFromPosition(0, 0, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(0, 0, 0);
		int smallerWNeighborValue = smallerWSlice.getFromPosition(0, 0, 0);
		int greaterXNeighborValue = currentWSlice.getFromPosition(1, 0, 0);
		if (topplePositionType6(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}		
	//  w | 01 | 00 | 00 | 16
		// reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 0, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(1, 0, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(2, 0, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(1, 1, 0);
		if (topplePositionType7(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 6, greaterYNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 01 | 01 | 00 | 17
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 1, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(1, 1, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(2, 1, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(1, 1, 1);
		if (topplePositionType8(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 3, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 01 | 01 | 01 | 18
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(1, 1, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(1, 1, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(2, 1, 1);
		if (topplePositionType9(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType2(int x, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int xMinusOne = x - 1;
		boolean changed = false;
		AnisotropicIntGrid4DSlice currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x | 00 | 00 | 10
		int currentValue = currentWSlice.getFromPosition(x, 0, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(x, 0, 0);
		int smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 0, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(x, 1, 0);
		if (topplePositionType10(x, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 00 | 25
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 1, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 1, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 2, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(x, 1, 1);
		if (topplePositionType11(x, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 01 | 26
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 1, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 1, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 2, 1);
		if (topplePositionType12(x, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType3(int coord, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int coordMinusOne = coord - 1;
		boolean changed = false;
		AnisotropicIntGrid4DSlice currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x |  y | 00 | 13
		int currentValue = currentWSlice.getFromPosition(coord, coord, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, 0);
		int smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, 1);
		if (topplePositionType13(coord, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y | 01 | 30
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, 2);
		if (topplePositionType14(coord, 1, currentValue, greaterWNeighborValue, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = z + 1;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 50
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
			smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, z);
			greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, zPlusOne);
			if (topplePositionType26(coord, z, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
			}
		}
	//  w |  x |  y |  z | 31
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
		smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, z);
		greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, zPlusOne);
		if (topplePositionType14(coord, z, currentValue, greaterWNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 4, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y |++z | 15
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
		if (topplePositionType15(coord, currentValue, greaterWNeighborValue, smallerZNeighborValue, newWSlices[1], newWSlices[2])) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType4(AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		boolean changed = false;
		if (toppleRangeType1(wSlices, newWSlices, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		AnisotropicIntGrid4DSlice smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
	//  w | 02 | 00 | 00 | 32
		int currentValue = currentWSlice.getFromPosition(2, 0, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(2, 0, 0);
		int smallerWNeighborValue = smallerWSlice.getFromPosition(2, 0, 0);
		int greaterXNeighborValue = currentWSlice.getFromPosition(3, 0, 0);
		int smallerXNeighborValue = currentWSlice.getFromPosition(1, 0, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(2, 1, 0);
		if (topplePositionType20(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 02 | 01 | 00 | 33
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 1, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 1, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 1, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(1, 1, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(2, 2, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(2, 1, 1);
		if (topplePositionType16(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 02 | 01 | 01 | 34
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 1, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 1, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 1, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(1, 1, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(2, 2, 1);
		if (topplePositionType17(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 02 | 02 | 00 | 35
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;		
		currentValue = currentWSlice.getFromPosition(2, 2, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 2, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 2, 0);
		if (topplePositionType21(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 02 | 02 | 01 | 36
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 2, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 2, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(2, 1, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(2, 2, 2);
		if (topplePositionType18(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 02 | 02 | 02 | 37
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(2, 2, 2);
		smallerWNeighborValue = smallerWSlice.getFromPosition(2, 2, 2);
		greaterXNeighborValue = currentWSlice.getFromPosition(3, 2, 2);
		if (topplePositionType22(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType5(int x, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		AnisotropicIntGrid4DSlice smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x | 00 | 00 | 19
		int currentValue = currentWSlice.getFromPosition(x, 0, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(x, 0, 0);
		int smallerWNeighborValue = smallerWSlice.getFromPosition(x, 0, 0);
		int greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 0, 0);
		int smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 0, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(x, 1, 0);
		if (topplePositionType7(x, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 00 | 38
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 1, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, 1, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 1, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 1, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 2, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(x, 1, 1);
		if (topplePositionType16(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 01 | 39
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 1, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, 1, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 1, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 1, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 2, 1);
		if (topplePositionType17(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType6(int coord, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		AnisotropicIntGrid4DSlice smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];	
		boolean changed = false;		
	//  w |  x |  y | 00 | 22
		int currentValue = currentWSlice.getFromPosition(coord, coord, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, 0);
		int smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, 0);
		int greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, 0);
		int smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, 1);
		if (topplePositionType8(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y | 01 | 43
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, 2);
		if (topplePositionType18(coord, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = z + 1;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 63
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
			smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, z);
			greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, z);
			smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, z);
			greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, zPlusOne);
			if (topplePositionType18(coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
			}
		}
	//  w |  x |  y |  z | 44
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
		smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, z);
		greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, z);
		smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, z);
		greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, zPlusOne);
		if (topplePositionType18(coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y |++z | 24
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
		smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, z);
		greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, z);
		if (topplePositionType9(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		coordMinusOne = coord;
		coord++;
		if (toppleRangeType2(coord, wSlices, newWSlices, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}		
	//  w |  x | 02 | 00 | 45
		currentValue = currentWSlice.getFromPosition(coord, 2, 0);
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, 2, 0);
		int smallerXNeighborValue = currentWSlice.getFromPosition(coordMinusOne, 2, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(coord, 3, 0);
		smallerYNeighborValue = currentWSlice.getFromPosition(coord, 1, 0);
		greaterZNeighborValue = currentWSlice.getFromPosition(coord, 2, 1);
		if (topplePositionType24(coord, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 02 | 01 | 46
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, 2, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(coordMinusOne, 2, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(coord, 3, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(coord, 1, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(coord, 2, 2);
		if (topplePositionType19(coord, 2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 02 | 02 | 47
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, 2, 2);
		smallerXNeighborValue = currentWSlice.getFromPosition(coordMinusOne, 2, 2);
		greaterYNeighborValue = currentWSlice.getFromPosition(coord, 3, 2);
		if (topplePositionType25(coord, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType7(int x, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {		
		int y = x - 1, xMinusOne = x - 1, xMinusTwo = x - 2, yPlusOne = y + 1, yMinusOne = y - 1;
		boolean changed = false;
		AnisotropicIntGrid4DSlice currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x |  y | 00 | 27
		int currentValue = currentWSlice.getFromPosition(x, y, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 0);
		int smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 0);
		int smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 1);
		if (topplePositionType11(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y | 01 | 48
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(x, y, 2);
		if (topplePositionType19(x, y, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = z + 1;
		for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 66
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
			smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
			greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
			smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
			greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
			if (topplePositionType19(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
			}
		}
	//  w |  x |  y |  z | 49
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
		smallerYNeighborValue = currentWSlice.getFromPosition(x, yMinusOne, z);
		greaterZNeighborValue = currentWSlice.getFromPosition(x, y, zPlusOne);
		if (topplePositionType19(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y |++z | 29
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, y, z);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, y, z);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, yPlusOne, z);
		if (topplePositionType12(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}		
		if (toppleRangeType3(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType8(int x, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		AnisotropicIntGrid4DSlice smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x | 00 | 00 | 32
		int currentValue = currentWSlice.getFromPosition(x, 0, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(x, 0, 0);
		int smallerWNeighborValue = smallerWSlice.getFromPosition(x, 0, 0);
		int greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 0, 0);
		int smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 0, 0);
		int greaterYNeighborValue = currentWSlice.getFromPosition(x, 1, 0);
		if (topplePositionType20(x, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
				greaterYNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 00 | 51
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 1, 0);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, 1, 0);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 1, 0);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 1, 0);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 2, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(x, 1, 1);
		if (topplePositionType16(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 01 | 52
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(x, 1, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(x, 1, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(xPlusOne, 1, 1);
		smallerXNeighborValue = currentWSlice.getFromPosition(xMinusOne, 1, 1);
		greaterYNeighborValue = currentWSlice.getFromPosition(x, 2, 1);
		if (topplePositionType17(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeType9(int coord, AnisotropicIntGrid4DSlice[] wSlices, AnisotropicIntGrid4DSlice[] newWSlices, int[] relevantAsymmetricNeighborValues,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		boolean changed = false;
		AnisotropicIntGrid4DSlice smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
	//  w |  x |  y | 00 | 35
		int currentValue = currentWSlice.getFromPosition(coord, coord, 0);
		int greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, 0);
		int smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, 0);
		int greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, 0);
		int smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, 0);
		int greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, 1);
		if (topplePositionType21(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y | 01 | 56
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, 1);
		smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, 1);
		greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, 1);
		smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, 1);
		greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, 2);
		if (topplePositionType18(coord, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = z + 1;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 72
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
			smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, z);
			greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, z);
			smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, z);
			greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, zPlusOne);
			if (topplePositionType29(coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
			}
		}
	//  w |  x |  y |  z | 57
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
		smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, z);
		greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, z);
		smallerYNeighborValue = currentWSlice.getFromPosition(coord, coordMinusOne, z);
		greaterZNeighborValue = currentWSlice.getFromPosition(coord, coord, zPlusOne);
		if (topplePositionType18(coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y |++z | 37
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice.getFromPosition(coord, coord, z);
		smallerWNeighborValue = smallerWSlice.getFromPosition(coord, coord, z);
		greaterXNeighborValue = currentWSlice.getFromPosition(coordPlusOne, coord, z);
		if (topplePositionType22(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}
	
	private static boolean topplePositionType1(int currentValue, int gWValue, AnisotropicIntGrid4DSlice newCurrentWSlice, AnisotropicIntGrid4DSlice newGreaterWSlice) {
		boolean toppled = false;
		if (gWValue < currentValue) {
			int toShare = currentValue - gWValue;
			int share = toShare/9;
			if (share != 0) {
				toppled = true;
				newCurrentWSlice.addToPosition(0, 0, 0, currentValue - toShare + share + toShare%9);
				newGreaterWSlice.addToPosition(0, 0, 0, share);
			} else {
				newCurrentWSlice.addToPosition(0, 0, 0, currentValue);
			}			
		} else {
			newCurrentWSlice.addToPosition(0, 0, 0, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionType2(int currentValue, int gWValue, int sWValue, int gXValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = 0;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = 0;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 8;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
	        relevantNeighborCount += 6;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, 0, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType3(int currentValue, int gWValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = 0;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 6;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = 1;
	        nc[2] = 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
	        relevantNeighborCount += 4;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, 1, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType4(int currentValue, int gWValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = 1;
	        nc[2] = 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = 1;
	        nc[2] = 1;
	        nc[3] = 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, 1, 1, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType5(int currentValue, int gWValue, int sZValue, AnisotropicIntGrid4DSlice newCurrentWSlice, AnisotropicIntGrid4DSlice newGreaterWSlice) {
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gWValue < currentValue) {
				if (sZValue == gWValue) {
					// gw = sz < current
					int toShare = currentValue - gWValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(1, 1, 0, share + share);// one more for the symmetric position at the other side
					newCurrentWSlice.addToPosition(1, 1, 1, currentValue - toShare + share + toShare%9);
					newGreaterWSlice.addToPosition(1, 1, 1, share);
				} else if (sZValue < gWValue) {
					// sz < gw < current
					int toShare = currentValue - gWValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(1, 1, 0, share + share);
					newGreaterWSlice.addToPosition(1, 1, 1, share);
					int currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(1, 1, 0, share + share);
					newCurrentWSlice.addToPosition(1, 1, 1, currentRemainingValue - toShare + share + toShare%5);
				} else {
					// gw < sz < current
					int toShare = currentValue - sZValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(1, 1, 0, share + share);
					newGreaterWSlice.addToPosition(1, 1, 1, share);
					int currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - gWValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(1, 1, 1, currentRemainingValue - toShare + share + toShare%5);
					newGreaterWSlice.addToPosition(1, 1, 1, share);
				}
			} else {
				// sz < current <= gw
				int toShare = currentValue - sZValue; 
				int share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				newCurrentWSlice.addToPosition(1, 1, 0, share + share);
				newCurrentWSlice.addToPosition(1, 1, 1, currentValue - toShare + share + toShare%5);
			}
		} else {
			if (gWValue < currentValue) {
				// gw < current <= sz
				int toShare = currentValue - gWValue; 
				int share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				newCurrentWSlice.addToPosition(1, 1, 1, currentValue - toShare + share + toShare%5);
				newGreaterWSlice.addToPosition(1, 1, 1, share);
			} else {
				newCurrentWSlice.addToPosition(1, 1, 1, currentValue);
			}
		}
		return toppled;
	}

	private static boolean topplePositionType6(int currentValue, int gWValue, int sWValue, int gXValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = 0;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = 0;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
	        relevantNeighborCount += 6;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, 0, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType7(int x, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = x;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x + 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
	        relevantNeighborCount += 4;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType8(int coord, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord + 1;
	        nc[2] = coord;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord - 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType9(int coord, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord + 1;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = coord - 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType10(int x, int currentValue, int gWValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
	        relevantNeighborCount += 4;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType11(int x, int y, int currentValue, int gWValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y + 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y - 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType12(int x, int coord, int currentValue, int gWValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = coord + 1;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord - 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType13(int coord, int currentValue, int gWValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord - 1;
	        nc[3] = 0;
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
	        nc[3] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType14(int coord, int z, int currentValue, int gWValue, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord - 1;
	        nc[3] = z;
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
	        nc[3] = z + 1;
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
	        nc[3] = z - 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType15(int coord, int currentValue, int gWValue, int sZValue, AnisotropicIntGrid4DSlice newCurrentWSlice, AnisotropicIntGrid4DSlice newGreaterWSlice) {
		 // 4*GW, 4*SZ | 15 | 15
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gWValue < currentValue) {
				if (sZValue == gWValue) {
					// gw = sz < current
					int toShare = currentValue - gWValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(coord, coord, coord - 1, share);
					newCurrentWSlice.addToPosition(coord, coord, coord, currentValue - toShare + share + toShare%9);
					newGreaterWSlice.addToPosition(coord, coord, coord, share);
				} else if (sZValue < gWValue) {
					// sz < gw < current
					int coordMinusOne = coord - 1;
					int toShare = currentValue - gWValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(coord, coord, coordMinusOne, share);
					newGreaterWSlice.addToPosition(coord, coord, coord, share);
					int currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(coord, coord, coordMinusOne, share);
					newCurrentWSlice.addToPosition(coord, coord, coord, currentRemainingValue - toShare + share + toShare%5);
				} else {
					// gw < sz < current
					int toShare = currentValue - sZValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(coord, coord, coord - 1, share);
					newGreaterWSlice.addToPosition(coord, coord, coord, share);
					int currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - gWValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice.addToPosition(coord, coord, coord, currentRemainingValue - toShare + share + toShare%5);
					newGreaterWSlice.addToPosition(coord, coord, coord, share);
				}
			} else {
				// sz < current <= gw
				int toShare = currentValue - sZValue; 
				int share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				newCurrentWSlice.addToPosition(coord, coord, coord - 1, share);
				newCurrentWSlice.addToPosition(coord, coord, coord, currentValue - toShare + share + toShare%5);
			}
		} else {
			if (gWValue < currentValue) {
				// gw < current <= sz
				int toShare = currentValue - gWValue; 
				int share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				newCurrentWSlice.addToPosition(coord, coord, coord, currentValue - toShare + share + toShare%5);
				newGreaterWSlice.addToPosition(coord, coord, coord, share);
			} else {
				newCurrentWSlice.addToPosition(coord, coord, coord, currentValue);
			}
		}
		return toppled;
	}

	private static boolean topplePositionType16(int x, int y, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x + 1;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y + 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y - 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType17(int x, int coord, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x + 1;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = coord + 1;
	        nc[3] = coord;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord - 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType18(int coord, int z, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord + 1;
	        nc[2] = coord;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord - 1;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = z + 1;
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
	        nc[3] = z - 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType19(int x, int y, int z, int currentValue, int gWValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = y;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y + 1;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y - 1;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z + 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z - 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType20(int x, int currentValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = x;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x + 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = 0;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
	        relevantNeighborCount += 4;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType21(int coord, int currentValue, int gWValue, int sWValue, int gXValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord + 1;
	        nc[2] = coord;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord - 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType22(int coord, int currentValue, int gWValue, int sWValue, int gXValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord + 1;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = coord - 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType23(int x, int y, int z, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = 1;
	        relevantNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 0;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sWShareMultiplier;
	        relevantNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x + 1;
	        nc[2] = y;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gXShareMultiplier;
	        relevantNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = y;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
	        relevantNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y + 1;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
	        relevantNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y - 1;
	        nc[3] = z;
	        relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
	        relevantNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z + 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
	        relevantNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z - 1;
	        relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
	        relevantNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionType24(int x, int y, int currentValue, int gWValue, int sXValue, int gYValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y + 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y - 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType25(int x, int coord, int currentValue, int gWValue, int sXValue, int gYValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = coord + 1;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord - 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType26(int coord, int z, int currentValue, int gWValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
	        relevantNeighborCount += 3;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord - 1;
	        nc[3] = z;
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
	        nc[3] = z + 1;
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
	        nc[3] = z - 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType27(int x, int y, int currentValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x + 1;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = y;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y + 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y - 1;
	        nc[3] = 0;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType28(int x, int coord, int currentValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x + 1;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = coord;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = coord + 1;
	        nc[3] = coord;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = coord;
	        nc[3] = coord - 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType29(int coord, int z, int currentValue, int gWValue, int sWValue, int gXValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 0;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord + 1;
	        nc[2] = coord;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord - 1;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = coord;
	        nc[2] = coord;
	        nc[3] = z + 1;
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
	        nc[3] = z - 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType30(int x, int y, int z, int currentValue, int gWValue, int sXValue, int gYValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantAsymmetricNeighborCount = 0;
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = y;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
	        relevantNeighborCount += 2;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y + 1;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y - 1;
	        nc[3] = z;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z + 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z - 1;
	        relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
	        relevantNeighborCount += 1;
	        relevantAsymmetricNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType31(int x, int y, int z, int currentValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, AnisotropicIntGrid4DSlice[] newWSlices) {
	    int relevantNeighborCount = 0;
	    if (gWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 2;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z;
	        relevantNeighborCount++;
	    }
	    if (sWValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 0;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z;
	        relevantNeighborCount++;
	    }
	    if (gXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x + 1;
	        nc[2] = y;
	        nc[3] = z;
	        relevantNeighborCount++;
	    }
	    if (sXValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x - 1;
	        nc[2] = y;
	        nc[3] = z;
	        relevantNeighborCount++;
	    }
	    if (gYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y + 1;
	        nc[3] = z;
	        relevantNeighborCount++;
	    }
	    if (sYValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y - 1;
	        nc[3] = z;
	        relevantNeighborCount++;
	    }
	    if (gZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z + 1;
	        relevantNeighborCount++;
	    }
	    if (sZValue < currentValue) {
	        relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
	        int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
	        nc[0] = 1;
	        nc[1] = x;
	        nc[2] = y;
	        nc[3] = z - 1;
	        relevantNeighborCount++;
	    }
	    return topplePosition(newWSlices, currentValue, x, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantNeighborCount);
	}
	
	private static boolean topplePosition(AnisotropicIntGrid4DSlice[] newWSlices, int value, int x, int y, int z, int[] neighborValues,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, 
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
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share);
					newWSlices[1].addToPosition(x, y, z, value - toShare + share + toShare%3);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share);
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share);
					newWSlices[1].addToPosition(x, y, z, currentRemainingValue - toShare + share + toShare%2);
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share);
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share);
					newWSlices[1].addToPosition(x, y, z, currentRemainingValue - toShare + share + toShare%2);
				}				
				break;
			case 1:
				int toShare = value - neighborValues[0];
				int share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share);
				}
				// no break
			case 0:
				newWSlices[1].addToPosition(x, y, z, value);
				break;
			default: // 8, 7, 6, 5, 4
				Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, neighborCoords, 
						neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicIntGrid4DSlice[] newWSlices, int value, int x, int y, int z, 
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
				newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share);
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
						newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newWSlices[1].addToPosition(x, y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(AnisotropicIntGrid4DSlice[] newWSlices, int value, int x, int y, int z, int[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(asymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, 
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
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
					newWSlices[1].addToPosition(x, y, z, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
					newWSlices[1].addToPosition(x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
					newWSlices[1].addToPosition(x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
					newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share * asymmetricNeighborShareMultipliers[0]);
				}
				// no break
			case 0:
				newWSlices[1].addToPosition(x, y, z, value);
				break;
			default: // 8, 7, 6, 5, 4
				Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicIntGrid4DSlice[] newWSlices, int value, int x, int y, int z, int[] asymmetricNeighborValues,
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
				newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share * asymmetricNeighborShareMultipliers[j]);
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
						newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share * asymmetricNeighborShareMultipliers[j]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newWSlices[1].addToPosition(x, y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(AnisotropicIntGrid4DSlice[] newWSlices, int value, int x, int y, int z, int[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords, neighborShareMultipliers);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, 
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
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
					newWSlices[1].addToPosition(x, y, z, value - toShare + share + toShare%3);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
					newWSlices[1].addToPosition(x, y, z, currentRemainingValue - toShare + share + toShare%2);
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
					newWSlices[1].addToPosition(x, y, z, currentRemainingValue - toShare + share + toShare%2);
				}				
				break;
			case 1:
				int toShare = value - neighborValues[0];
				int share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share * neighborShareMultipliers[0]);
				}
				// no break
			case 0:
				newWSlices[1].addToPosition(x, y, z, value);
				break;
			default: // 8, 7, 6, 5, 4
				Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords, 
						neighborShareMultipliers);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicIntGrid4DSlice[] newWSlices, int value, int x, int y, int z, int[] neighborValues,
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
				newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share * neighborShareMultipliers[j]);
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
						newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share * neighborShareMultipliers[j]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newWSlices[1].addToPosition(x, y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(AnisotropicIntGrid4DSlice[] newWSlices, int value, int x, int y, int z, int[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(asymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, 
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
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share);
					newWSlices[1].addToPosition(x, y, z, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share);
					newWSlices[1].addToPosition(x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]].addToPosition(n0Coords[1], n0Coords[2], n0Coords[3], share);
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n1Coords[0]].addToPosition(n1Coords[1], n1Coords[2], n1Coords[3], share);
					newWSlices[1].addToPosition(x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
					newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share);
				}
				// no break
			case 0:
				newWSlices[1].addToPosition(x, y, z, value);
				break;
			default: // 8, 7, 6, 5, 4
				Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(AnisotropicIntGrid4DSlice[] newWSlices, int value, int x, int y, int z, int[] asymmetricNeighborValues,
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
				newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share);
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
						newWSlices[nc[0]].addToPosition(nc[1], nc[2], nc[3], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newWSlices[1].addToPosition(x, y, z, value);
		return toppled;
	}	

	private void processGridBlock(SizeLimitedAnisotropicIntGrid4DBlock block) throws Exception {
		if (block.minW <= maxW) {
			IntGrid4D subBlock = null;
			if (block.maxW > maxW) {
				subBlock = new IntSubGrid4DWithWBounds(block, block.minW, maxW);
			} else {
				subBlock = new ImmutableIntGrid4D(block);
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
			if (gridBlockA.minW < gridBlockB.minW) {
				SizeLimitedAnisotropicIntGrid4DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			}
			if (gridBlockB.minW == 0 && gridBlockA.maxW >= maxW
					&& gridBlockB.maxW == gridBlockA.minW - 1) {//two blocks
				processGridBlock(gridBlockB);
				processGridBlock(gridBlockA);
			} else { //more than two blocks
				//I keep the one closest to zero (in case it can be reused when computing the next step)
				//and save the other
				if (!readOnlyMode)
					saveGridBlock(gridBlockA);
				if (gridBlockB.minW > 0) {
					gridBlockA.free();
					gridBlockA = loadGridBlock(0);
					processGridBlock(gridBlockA);
					while (gridBlockA.maxW < gridBlockB.minW - 1) {
						int nextX = gridBlockA.maxW + 1;
						gridBlockA.free();
						gridBlockA = loadGridBlock(nextX);
						processGridBlock(gridBlockA);
					}
				}
				processGridBlock(gridBlockB);
				int previousMaxX = gridBlockB.maxW;			
				while (previousMaxX < maxW) {
					int nextX = previousMaxX + 1;
					gridBlockA.free();
					gridBlockA = loadGridBlock(nextX);
					processGridBlock(gridBlockA);
					previousMaxX = gridBlockA.maxW;
				}
			}			
		}
		triggerAfterProcessing();
	}

	private void slideGridSlices(AnisotropicIntGrid4DSlice[] newGridSlices, AnisotropicIntGrid4DSlice newSlice) {
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
		return getName() + "/4D/" + initialValue + "/asymmetric_section";
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
		properties.put("maxW", maxW);
		properties.put("maxGridBlockSize", maxGridBlockSize);
		return properties;
	}
	
	private void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (int) properties.get("initialValue");
		step = (long) properties.get("step");
		maxW = (int) properties.get("maxW");
		maxGridBlockSize = (long) properties.get("maxGridBlockSize");
	}
	
	@Override
	public int getMaxW() {
		return maxW;
	}

	@Override
	public int getMaxX() {
		return maxW;
	}
	
	@Override
	public int getMaxY() {
		return maxW;
	}
	
	@Override
	public int getMaxZ() {
		return maxW;
	}

}