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
package cellularautomata.automata.aether;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import cellularautomata.Utils;
import cellularautomata.model.FileBackedModel;
import cellularautomata.model4d.IsotropicHypercubicModel4DA;
import cellularautomata.model4d.SymmetricLongModel4D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class FileBackedLongAether4D extends FileBackedModel implements SymmetricLongModel4D, IsotropicHypercubicModel4DA {

	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -2635249153387078803L;
	public static final int POSITION_BYTES = Long.BYTES;
	
	private long initialValue;
	private long step;
	private int maxW;
	private Boolean changed = null;

	public FileBackedLongAether4D(long initialValue, String folderPath) throws IOException {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		createGridFolder(folderPath);
		currentFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step));
		grid = new RandomAccessFile(currentFile, "rw");
		grid.setLength(Utils.getAnisotropic4DGridPositionCount(8)*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
		grid.writeLong(initialValue);
		maxW = 5;
		step = 0;
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
	public FileBackedLongAether4D(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		super(backupPath, folderPath);
	}

	@Override
	public Boolean nextStep() throws IOException {
		RandomAccessFile newGrid = null;
		try {
			boolean changed = false;
			// w = 0, x = 0, y = 0, z = 0
			long currentValue = getFromAsymmetricPosition(0, 0, 0, 0);
			long greaterWNeighborValue = getFromAsymmetricPosition(1, 0, 0, 0);
			File newFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step + 1));
			newGrid = new RandomAccessFile(newFile, "rw");
			newGrid.setLength(Utils.getAnisotropic4DGridPositionCount(maxW + 4)*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
			if (topplePositionOfType1(currentValue, greaterWNeighborValue, newGrid)) {
				changed = true;
			}
			long[] relevantAsymmetricNeighborValues = new long[8];
			int[] sortedNeighborsIndexes = new int[8];
			int[][] relevantAsymmetricNeighborCoords = new int[8][4];
			int[] relevantAsymmetricNeighborShareMultipliers = new int[8];// to compensate for omitted symmetric positions
			int[] relevantAsymmetricNeighborSymmetryCounts = new int[8];// to compensate for omitted symmetric positions
			// w = 1, x = 0, y = 0, z = 0
			// reuse values obtained previously
			long smallerWNeighborValue = currentValue;
			currentValue = greaterWNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(2, 0, 0, 0);
			long greaterXNeighborValue = getFromAsymmetricPosition(1, 1, 0, 0);
			if (topplePositionOfType2(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 1, x = 1, y = 0, z = 0
			// reuse values obtained previously
			long smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(2, 1, 0, 0);
			long greaterYNeighborValue = getFromAsymmetricPosition(1, 1, 1, 0);
			if (topplePositionOfType3(currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 1, x = 1, y = 1, z = 0
			// reuse values obtained previously
			long smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(2, 1, 1, 0);
			long greaterZNeighborValue = getFromAsymmetricPosition(1, 1, 1, 1);
			if (topplePositionOfType4(currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 1, x = 1, y = 1, z = 1
			// reuse values obtained previously
			long smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1);
			if (topplePositionOfType5(currentValue, greaterWNeighborValue, smallerZNeighborValue, newGrid)) {
				changed = true;
			}
			// w = 2, x = 0, y = 0, z = 0
			currentValue = getFromAsymmetricPosition(2, 0, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 0, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(1, 0, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 1, 0, 0);
			if (topplePositionOfType6(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 2, x = 1, y = 0, z = 0
			// reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(3, 1, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(1, 1, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 2, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 1, 1, 0);
			if (topplePositionOfType7(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, 
					smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 2, x = 1, y = 1, z = 0
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(3, 1, 1, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(1, 1, 1, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 2, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1);
			if (topplePositionOfType8(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 4, 
					greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 2, x = 1, y = 1, z = 1
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(3, 1, 1, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(1, 1, 1, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1);
			if (topplePositionOfType9(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, 
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 2, x = 2, y = 0, z = 0
			currentValue = getFromAsymmetricPosition(2, 2, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 2, 0, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 1, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 1, 0);
			if (topplePositionOfType10(2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 2, x = 2, y = 1, z = 0
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(3, 2, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 1, 1, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1);
			if (topplePositionOfType11(2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 4, 
					greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 2, x = 2, y = 1, z = 1
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1);
			if (topplePositionOfType12(2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, 
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 2, x = 2, y = 2, z = 0
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;		
			currentValue = getFromAsymmetricPosition(2, 2, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 0);
			if (topplePositionOfType13(2, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// w = 2, x = 2, y = 2, z = 1
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(2, 2, 2, 2);
			if (topplePositionOfType14(2, 1, currentValue, greaterWNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 4, 
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 02 | 02 | 02 | 02 | 15
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2);
			if (topplePositionOfType15(2, currentValue, greaterWNeighborValue, smallerZNeighborValue, newGrid)) {
				changed = true;
			}
			if (toppleRangeOfType1(3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 03 | 02 | 00 | 00 | 19
			// reuse values obtained previously
			currentValue = getFromAsymmetricPosition(3, 2, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 2, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 2, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 0, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 1, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 1, 0);
			if (topplePositionOfType7(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, 
					smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 03 | 02 | 01 | 00 | 20
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(4, 2, 1, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 2, 1, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 1, 1, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1);
			if (topplePositionOfType16(3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 03 | 02 | 01 | 01 | 21
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(4, 2, 1, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 1, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1);
			if (topplePositionOfType17(3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 03 | 02 | 02 | 00 | 22
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			currentValue = getFromAsymmetricPosition(3, 2, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 2, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 2, 0);
			if (topplePositionOfType8(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 03 | 02 | 02 | 01 | 23
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2);
			if (topplePositionOfType18(3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 03 | 02 | 02 | 02 | 24
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 2, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2);
			if (topplePositionOfType9(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeOfType2(3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 03 | 03 | 02 | 00 | 27
			currentValue = getFromAsymmetricPosition(3, 3, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 3, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 2, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 3, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1);
			if (topplePositionOfType11(3, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 03 | 03 | 02 | 01 | 28
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(4, 3, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2);
			if (topplePositionOfType19(3, 2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 03 | 03 | 02 | 02 | 29
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2);
			if (topplePositionOfType12(3, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeOfType3(3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeOfType4(4, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeOfType5(4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 04 | 03 | 02 | 00 | 40
			currentValue = getFromAsymmetricPosition(4, 3, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(5, 3, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 4, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 2, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(4, 3, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(4, 3, 2, 1);
			if (topplePositionOfType16(4, 3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 04 | 03 | 02 | 01 | 41
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(5, 3, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 4, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(4, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2);
			if (topplePositionOfType23(4, 3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				changed = true;
			}
			// 04 | 03 | 02 | 02 | 42
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(5, 3, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 4, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2);
			if (topplePositionOfType17(4, 3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeOfType6(4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeOfType7(4, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}		
			// 5 <= w < edge - 2
			int edge = maxW + 2;
			int edgeMinusTwo = edge - 2;
			if (toppleRangeBeyondW4(newGrid, 5, edgeMinusTwo, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) { // is it faster to reuse these arrays?
				changed = true;
			}
			//edge - 2 <= w < edge
			if (toppleRangeBeyondW4(newGrid, edgeMinusTwo, edge, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) {
				changed = true;
				maxW++;
			}
			grid.close();
			if (readingBackup) {
				readingBackup = false;
			} else {
				currentFile.delete();				
			}
			currentFile = newFile;
			grid = newGrid;
			step++;
			this.changed = changed;
			return changed;
		} catch (Exception ex) {
			if (newGrid != null)
				newGrid.close();
			close();
			throw ex;
		}
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}

	private boolean toppleRangeBeyondW4(RandomAccessFile newGrid, int minW,
			int maxW, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords,
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) throws IOException {
		boolean changed = false;
		int w = minW, wMinusOne = w - 1, wMinusTwo = w - 2, wMinusThree = w - 3, wPlusOne = w + 1, wPlusTwo = w + 2;
		for (; w != maxW; wMinusThree = wMinusTwo, wMinusTwo = wMinusOne, wMinusOne = w, w = wPlusOne, wPlusOne = wPlusTwo, wPlusTwo++) {
			if (toppleRangeOfType4(w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeOfType8(w, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			//  w | 03 | 02 | 00 | 53
			long currentValue = getFromAsymmetricPosition(w, 3, 2, 0);
			long greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 3, 2, 0);
			long smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 3, 2, 0);
			long greaterXNeighborValue = getFromAsymmetricPosition(w, 4, 2, 0);
			long smallerXNeighborValue = getFromAsymmetricPosition(w, 2, 2, 0);
			long greaterYNeighborValue = getFromAsymmetricPosition(w, 3, 3, 0);
			long smallerYNeighborValue = getFromAsymmetricPosition(w, 3, 1, 0);
			long greaterZNeighborValue = getFromAsymmetricPosition(w, 3, 2, 1);
			if (topplePositionOfType16(w, 3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			//  w | 03 | 02 | 01 | 54
			// reuse values obtained previously
			long smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 3, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 3, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(w, 4, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(w, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(w, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(w, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(w, 3, 2, 2);
			if (topplePositionOfType23(w, 3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				changed = true;
			}
			//  w | 03 | 02 | 02 | 55
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 3, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 3, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(w, 4, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(w, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(w, 3, 3, 2);
			if (topplePositionOfType17(w, 3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeOfType9(w, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			int x = 4, xPlusOne = 5, xMinusOne = 3;
			for (int xMinusTwo = 2; x != wMinusOne; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
				if (toppleRangeOfType8(w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
				//  w |  x | 02 | 00 | 67
				currentValue = getFromAsymmetricPosition(w, x, 2, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 2, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 2, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 2, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 2, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, 3, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, 1, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, 2, 1);
				if (topplePositionOfType27(w, x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				//  w |  x | 02 | 01 | 68
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 2, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 2, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 2, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 2, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, 3, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, 1, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, 2, 2);
				if (topplePositionOfType23(w, x, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					changed = true;
				}
				//  w |  x | 02 | 02 | 69
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 2, 2);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 2, 2);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 2, 2);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 2, 2);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, 3, 2);
				if (topplePositionOfType28(w, x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				int y = 3, yMinusOne = 2, yPlusOne = 4;
				for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
					//  w |  x |  y | 00 | 67
					currentValue = getFromAsymmetricPosition(w, x, y, 0);
					greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 0);
					smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, 0);
					greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, 0);
					smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 0);
					greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 0);
					smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 0);
					greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 1);
					if (topplePositionOfType27(w, x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
							smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
					//  w |  x |  y | 01 | 77
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 1);
					smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, 1);
					greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, 1);
					smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 1);
					greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 1);
					smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 1);
					greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 2);
					if (topplePositionOfType23(w, x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
							smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers, newGrid)) {
						changed = true;
					}
					int z = 2, zPlusOne = 3;
					for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
						//  w |  x |  y |  z | 81
						// reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
						smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
						greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
						smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
						greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
						smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
						greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
						if (topplePositionOfType31(w, x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
								smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newGrid)) {
							changed = true;
						}
					}
					//  w |  x |  y |  z | 78
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
					if (topplePositionOfType23(w, x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
							smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers, newGrid)) {
						changed = true;
					}
					//  w |  x |  y |++z | 69
					z = zPlusOne;
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
					if (topplePositionOfType28(w, x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
							smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
				}
				//  w |  x |  y | 00 | 53
				currentValue = getFromAsymmetricPosition(w, x, y, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 1);
				if (topplePositionOfType16(w, x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				//  w |  x |  y | 01 | 70
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 2);
				if (topplePositionOfType23(w, x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					changed = true;
				}
				int z = 2, zPlusOne = 3;
				for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
					//  w |  x |  y |  z | 79
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
					if (topplePositionOfType23(w, x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
							smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers, newGrid)) {
						changed = true;
					}
				}
				//  w |  x |  y |  z | 71
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
				if (topplePositionOfType23(w, x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					changed = true;
				}
				//  w |  x |  y |++z | 55
				z = zPlusOne;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
				if (topplePositionOfType17(w, x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
						smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				if (toppleRangeOfType9(w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
			}
			if (toppleRangeOfType5(w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			//  w |  x | 02 | 00 | 58
			currentValue = getFromAsymmetricPosition(w, x, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(w, x, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(w, x, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(w, x, 2, 1);
			if (topplePositionOfType16(w, x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			//  w |  x | 02 | 01 | 59
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(w, x, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(w, x, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(w, x, 2, 2);
			if (topplePositionOfType23(w, x, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				changed = true;
			}
			//  w |  x | 02 | 02 | 60
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(w, x, 3, 2);
			if (topplePositionOfType17(w, x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			int y = 3, yPlusOne = 4, yMinusOne = 2;
			for (; y != wMinusTwo; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				//  w |  x |  y | 00 | 58
				currentValue = getFromAsymmetricPosition(w, x, y, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 1);
				if (topplePositionOfType16(w, x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				//  w |  x |  y | 01 | 73
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 2);
				if (topplePositionOfType23(w, x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					changed = true;
				}
				int z = 2, zPlusOne = 3;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					//  w |  x |  y |  z | 80
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
					if (topplePositionOfType23(w, x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
							smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers, newGrid)) {
						changed = true;
					}
				}
				//  w |  x |  y |  z | 74
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
				if (topplePositionOfType23(w, x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					changed = true;
				}
				//  w |  x |  y |++z | 60
				z = zPlusOne;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
				if (topplePositionOfType17(w, x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			//  w |  x |  y | 00 | 40
			currentValue = getFromAsymmetricPosition(w, x, y, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 1);
			if (topplePositionOfType16(w, x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			//  w |  x |  y | 01 | 61
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 2);
			if (topplePositionOfType23(w, x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				changed = true;
			}
			int z = 2, zPlusOne = 3;
			for (; z != wMinusThree; z = zPlusOne, zPlusOne++) {
				//  w |  x |  y |  z | 75
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
				if (topplePositionOfType23(w, x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					changed = true;
				}
			}
			//  w |  x |  y |  z | 62
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
			if (topplePositionOfType23(w, x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				changed = true;
			}
			//  w |  x |  y |++z | 42
			z = zPlusOne;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
			if (topplePositionOfType17(w, x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}			
			if (toppleRangeOfType6(w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			xMinusOne = x;
			x = xPlusOne;
			for (y = 3, yMinusOne = 2, yPlusOne = 4; y != wMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				//  w |  x |  y | 00 | 45
				currentValue = getFromAsymmetricPosition(w, x, y, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 1);
				if (topplePositionOfType24(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				//  w |  x |  y | 01 | 64
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, 2);
				if (topplePositionOfType19(x, y, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
						smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				for (z = 2, zPlusOne = 3; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					//  w |  x |  y |  z | 76
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
					if (topplePositionOfType30(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
							smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
				}
				//  w |  x |  y |  z | 65
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(w, x, y, zPlusOne);
				if (topplePositionOfType19(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
						smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				//  w |  x |  y |++z | 47
				z = zPlusOne;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(w, x, yPlusOne, z);
				if (topplePositionOfType25(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			if (toppleRangeOfType7(x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
		}
		return changed;
	}

	private boolean toppleRangeOfType1(int w, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		boolean changed = false;
		int wPlusOne = w + 1, wMinusOne = w - 1;
		//  w | 00 | 00 | 00 | 06
		long currentValue = getFromAsymmetricPosition(w, 0, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 0, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 0, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(w, 1, 0, 0);
		if (topplePositionOfType6(w, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}		
		//  w | 01 | 00 | 00 | 16
		// reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 1, 0, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 1, 0, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(w, 2, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(w, 1, 1, 0);
		if (topplePositionOfType7(w, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 6, greaterYNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w | 01 | 01 | 00 | 17
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 1, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 1, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(w, 2, 1, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(w, 1, 1, 1);
		if (topplePositionOfType8(w, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 3, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w | 01 | 01 | 01 | 18
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 1, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 1, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(w, 2, 1, 1);
		if (topplePositionOfType9(w, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeOfType2(int coord, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		boolean changed = false;
		//  w |  x | 00 | 00 | 10
		long currentValue = getFromAsymmetricPosition(coord, coord, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(coord, coordMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(coord, coord, 1, 0);
		if (topplePositionOfType10(coord, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x | 01 | 00 | 25
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(coord, coordMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(coord, coord, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, 1, 1);
		if (topplePositionOfType11(coord, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x | 01 | 01 | 26
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(coord, coordMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(coord, coord, 2, 1);
		if (topplePositionOfType12(coord, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeOfType3(int coord, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		boolean changed = false;
		//  w |  x |  y | 00 | 13
		long currentValue = getFromAsymmetricPosition(coord, coord, coord, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, coord, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(coord, coord, coordMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, coord, 1);
		if (topplePositionOfType13(coord, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x |  y | 01 | 30
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, coord, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(coord, coord, coordMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, coord, 2);
		if (topplePositionOfType14(coord, 1, currentValue, greaterWNeighborValue, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
			//  w |  x |  y |  z | 50
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, coord, z);
			smallerYNeighborValue = getFromAsymmetricPosition(coord, coord, coordMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, coord, zPlusOne);
			if (topplePositionOfType26(coord, z, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		//  w |  x |  y |  z | 31
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, coord, z);
		smallerYNeighborValue = getFromAsymmetricPosition(coord, coord, coordMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, coord, zPlusOne);
		if (topplePositionOfType14(coord, z, currentValue, greaterWNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 4, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x |  y |++z | 15
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, coord, z);
		if (topplePositionOfType15(coord, currentValue, greaterWNeighborValue, smallerZNeighborValue, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeOfType4(int w, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		boolean changed = false;
		int wPlusOne = w + 1, wMinusOne = w - 1;
		if (toppleRangeOfType1(w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		//  w | 02 | 00 | 00 | 32
		long currentValue = getFromAsymmetricPosition(w, 2, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 2, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 2, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(w, 3, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(w, 1, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(w, 2, 1, 0);
		if (topplePositionOfType20(w, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w | 02 | 01 | 00 | 33
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 2, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 2, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(w, 3, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(w, 1, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(w, 2, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(w, 2, 1, 1);
		if (topplePositionOfType16(w, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w | 02 | 01 | 01 | 34
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 2, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 2, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(w, 3, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(w, 1, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(w, 2, 2, 1);
		if (topplePositionOfType17(w, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w | 02 | 02 | 00 | 35
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;		
		currentValue = getFromAsymmetricPosition(w, 2, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 2, 2, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 2, 2, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(w, 3, 2, 0);
		if (topplePositionOfType21(w, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w | 02 | 02 | 01 | 36
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 2, 2, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 2, 2, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(w, 3, 2, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(w, 2, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(w, 2, 2, 2);
		if (topplePositionOfType18(w, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w | 02 | 02 | 02 | 37
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, 2, 2, 2);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, 2, 2, 2);
		greaterXNeighborValue = getFromAsymmetricPosition(w, 3, 2, 2);
		if (topplePositionOfType22(w, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeOfType5(int w, int x, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int xMinusOne = x - 1, xPlusOne = x + 1, wPlusOne = w + 1, wMinusOne = w - 1;
		boolean changed = false;
		//  w |  x | 00 | 00 | 19
		long currentValue = getFromAsymmetricPosition(w, x, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(w, x, 1, 0);
		if (topplePositionOfType7(w, x, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x | 01 | 00 | 38
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(w, x, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(w, x, 1, 1);
		if (topplePositionOfType16(w, x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x | 01 | 01 | 39
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(w, x, 2, 1);
		if (topplePositionOfType17(w, x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeOfType6(int w, int coord, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1, wPlusOne = w + 1, wMinusOne = w - 1;
		boolean changed = false;		
		//  w |  x |  y | 00 | 22
		long currentValue = getFromAsymmetricPosition(w, coord, coord, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(w, coord, coordMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(w, coord, coord, 1);
		if (topplePositionOfType8(w, coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x |  y | 01 | 43
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(w, coord, coordMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(w, coord, coord, 2);
		if (topplePositionOfType18(w, coord, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
			//  w |  x |  y |  z | 63
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, z);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, z);
			greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, z);
			smallerYNeighborValue = getFromAsymmetricPosition(w, coord, coordMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(w, coord, coord, zPlusOne);
			if (topplePositionOfType18(w, coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		//  w |  x |  y |  z | 44
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, z);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, z);
		greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, z);
		smallerYNeighborValue = getFromAsymmetricPosition(w, coord, coordMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(w, coord, coord, zPlusOne);
		if (topplePositionOfType18(w, coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x |  y |++z | 24
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, z);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, z);
		greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, z);
		if (topplePositionOfType9(w, coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		coordMinusOne = coord;
		coord++;
		if (toppleRangeOfType2(coord, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}		
		//  w |  x | 02 | 00 | 45
		currentValue = getFromAsymmetricPosition(w, coord, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, 2, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(w, coordMinusOne, 2, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(w, coord, 3, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(w, coord, 1, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(w, coord, 2, 1);
		if (topplePositionOfType24(coord, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x | 02 | 01 | 46
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, 2, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(w, coordMinusOne, 2, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(w, coord, 3, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(w, coord, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(w, coord, 2, 2);
		if (topplePositionOfType19(coord, 2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x | 02 | 02 | 47
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, 2, 2);
		smallerXNeighborValue = getFromAsymmetricPosition(w, coordMinusOne, 2, 2);
		greaterYNeighborValue = getFromAsymmetricPosition(w, coord, 3, 2);
		if (topplePositionOfType25(coord, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	//this range could be merged into type 6
	private boolean toppleRangeOfType7(int coord, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {		
		int y = coord - 1, coordMinusOne = coord - 1, coordMinusTwo = coord - 2, coordPlusOne = coord + 1, yPlusOne = y + 1, yMinusOne = y - 1;
		boolean changed = false;
		//  w |  x |  y | 00 | 27
		long currentValue = getFromAsymmetricPosition(coord, coord, y, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, y, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(coord, coordMinusOne, y, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(coord, coord, yPlusOne, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(coord, coord, yMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, y, 1);
		if (topplePositionOfType11(coord, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x |  y | 01 | 48
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, y, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(coord, coordMinusOne, y, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(coord, coord, yPlusOne, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(coord, coord, yMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, y, 2);
		if (topplePositionOfType19(coord, y, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusTwo; z = zPlusOne, zPlusOne++) {
			//  w |  x |  y |  z | 66
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(coord, coordMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(coord, coord, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(coord, coord, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, y, zPlusOne);
			if (topplePositionOfType19(coord, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		//  w |  x |  y |  z | 49
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(coord, coordMinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(coord, coord, yPlusOne, z);
		smallerYNeighborValue = getFromAsymmetricPosition(coord, coord, yMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(coord, coord, y, zPlusOne);
		if (topplePositionOfType19(coord, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x |  y |++z | 29
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(coordPlusOne, coord, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(coord, coordMinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(coord, coord, yPlusOne, z);
		if (topplePositionOfType12(coord, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}		
		if (toppleRangeOfType3(coord, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeOfType8(int w, int x, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int xMinusOne = x - 1, xPlusOne = x + 1, wPlusOne = w + 1, wMinusOne = w - 1;
		boolean changed = false;
		//  w |  x | 00 | 00 | 32
		long currentValue = getFromAsymmetricPosition(w, x, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(w, x, 1, 0);
		if (topplePositionOfType20(w, x, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
				greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x | 01 | 00 | 51
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(w, x, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(w, x, 1, 1);
		if (topplePositionOfType16(w, x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x | 01 | 01 | 52
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, x, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, x, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(w, xPlusOne, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(w, xMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(w, x, 2, 1);
		if (topplePositionOfType17(w, x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeOfType9(int w, int coord, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		int wPlusOne = w + 1, wMinusOne = w - 1;
		boolean changed = false;
		//  w |  x |  y | 00 | 35
		long currentValue = getFromAsymmetricPosition(w, coord, coord, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(w, coord, coordMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(w, coord, coord, 1);
		if (topplePositionOfType21(w, coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x |  y | 01 | 56
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(w, coord, coordMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(w, coord, coord, 2);
		if (topplePositionOfType18(w, coord, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
			//  w |  x |  y |  z | 72
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, z);
			smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, z);
			greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, z);
			smallerYNeighborValue = getFromAsymmetricPosition(w, coord, coordMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(w, coord, coord, zPlusOne);
			if (topplePositionOfType29(w, coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		//  w |  x |  y |  z | 57
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, z);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, z);
		greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, z);
		smallerYNeighborValue = getFromAsymmetricPosition(w, coord, coordMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(w, coord, coord, zPlusOne);
		if (topplePositionOfType18(w, coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		//  w |  x |  y |++z | 37
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = getFromAsymmetricPosition(wPlusOne, coord, coord, z);
		smallerWNeighborValue = getFromAsymmetricPosition(wMinusOne, coord, coord, z);
		greaterXNeighborValue = getFromAsymmetricPosition(w, coordPlusOne, coord, z);
		if (topplePositionOfType22(w, coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private static boolean topplePositionOfType1(long currentValue, long gWValue, RandomAccessFile newGrid) throws IOException {
		boolean toppled = false;
		if (gWValue < currentValue) {
			long toShare = currentValue - gWValue;
			long share = toShare/9;
			if (share != 0) {
				toppled = true;
				addToPosition(newGrid, 0, 0, 0, 0, currentValue - toShare + share + toShare%9);
				addToPosition(newGrid, 1, 0, 0, 0, share);
			} else {
				addToPosition(newGrid, 0, 0, 0, 0, currentValue);
			}			
		} else {
			addToPosition(newGrid, 0, 0, 0, 0, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType2(long currentValue, long gWValue, long sWValue, long gXValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
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
		return topplePosition(newGrid, currentValue, 1, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType3(long currentValue, long gWValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
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
		return topplePosition(newGrid, currentValue, 1, 1, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType4(long currentValue, long gWValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
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
		return topplePosition(newGrid, currentValue, 1, 1, 1, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType5(long currentValue, long gWValue, long sZValue, RandomAccessFile newGrid) throws IOException {
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gWValue < currentValue) {
				if (sZValue == gWValue) {
					// gw = sz < current
					long toShare = currentValue - gWValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 0, share + share);// one more for the symmetric position at the other side
					addToPosition(newGrid, 1, 1, 1, 1, currentValue - toShare + share + toShare%9);
					addToPosition(newGrid, 2, 1, 1, 1, share);
				} else if (sZValue < gWValue) {
					// sz < gw < current
					long toShare = currentValue - gWValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 0, share + share);
					addToPosition(newGrid, 2, 1, 1, 1, share);
					long currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 0, share + share);
					addToPosition(newGrid, 1, 1, 1, 1, currentRemainingValue - toShare + share + toShare%5);
				} else {
					// gw < sz < current
					long toShare = currentValue - sZValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 0, share + share);
					addToPosition(newGrid, 2, 1, 1, 1, share);
					long currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - gWValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 1, currentRemainingValue - toShare + share + toShare%5);
					addToPosition(newGrid, 2, 1, 1, 1, share);
				}
			} else {
				// sz < current <= gw
				long toShare = currentValue - sZValue; 
				long share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, 1, 1, 1, 0, share + share);
				addToPosition(newGrid, 1, 1, 1, 1, currentValue - toShare + share + toShare%5);
			}
		} else if (gWValue < currentValue) {
			// gw < current <= sz
			long toShare = currentValue - gWValue; 
			long share = toShare/5;
			if (share != 0) {
				toppled = true;
			}
			addToPosition(newGrid, 1, 1, 1, 1, currentValue - toShare + share + toShare%5);
			addToPosition(newGrid, 2, 1, 1, 1, share);
		} else {
			// gw >= current <= sz
			addToPosition(newGrid, 1, 1, 1, 1, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType6(int w, long currentValue, long gWValue, long sWValue, long gXValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType7(int w, int x, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = x;
			nc[2] = 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType8(int w, int coord, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType9(int w, int coord, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, coord, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType10(int coord, long currentValue, long gWValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord - 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType11(int coord, int y, long currentValue, long gWValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y;
			nc[3] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType12(int coord1, int coord2, long currentValue, long gWValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = coord2;
			nc[3] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2 + 1;
			nc[3] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2 - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType13(int coord, long currentValue, long gWValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType14(int coord, int z, long currentValue, long gWValue, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType15(int coord, long currentValue, long gWValue, long sZValue, RandomAccessFile newGrid) throws IOException {
		// 4*GW, 4*SZ | 15 | 15
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gWValue < currentValue) {
				if (sZValue == gWValue) {
					// gw = sz < current
					long toShare = currentValue - gWValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coord - 1, share);
					addToPosition(newGrid, coord, coord, coord, coord, currentValue - toShare + share + toShare%9);
					addToPosition(newGrid, coord + 1, coord, coord, coord, share);
				} else if (sZValue < gWValue) {
					// sz < gw < current
					int coordMinusOne = coord - 1;
					long toShare = currentValue - gWValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coordMinusOne, share);
					addToPosition(newGrid, coord + 1, coord, coord, coord, share);
					long currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coordMinusOne, share);
					addToPosition(newGrid, coord, coord, coord, coord, currentRemainingValue - toShare + share + toShare%5);
				} else {
					// gw < sz < current
					int coordPlusOne = coord + 1;
					long toShare = currentValue - sZValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coord - 1, share);
					addToPosition(newGrid, coordPlusOne, coord, coord, coord, share);
					long currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - gWValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coord, currentRemainingValue - toShare + share + toShare%5);
					addToPosition(newGrid, coordPlusOne, coord, coord, coord, share);
				}
			} else {
				// sz < current <= gw
				long toShare = currentValue - sZValue; 
				long share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, coord, coord, coord, coord - 1, share);
				addToPosition(newGrid, coord, coord, coord, coord, currentValue - toShare + share + toShare%5);
			}
		} else if (gWValue < currentValue) {
			// gw < current <= sz
			long toShare = currentValue - gWValue; 
			long share = toShare/5;
			if (share != 0) {
				toppled = true;
			}
			addToPosition(newGrid, coord, coord, coord, coord, currentValue - toShare + share + toShare%5);
			addToPosition(newGrid, coord + 1, coord, coord, coord, share);
		} else {
			// gw >= current <= sz
			addToPosition(newGrid, coord, coord, coord, coord, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType16(int w, int x, int y, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType17(int w, int x, int coord, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType18(int w, int coord, int z, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType19(int coord, int y, int z, long currentValue, long gWValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y;
			nc[3] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType20(int w, int x, long currentValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = x;
			nc[2] = 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType21(int w, int coord, long currentValue, long gWValue, long sWValue, long gXValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType22(int w, int coord, long currentValue, long gWValue, long sWValue, long gXValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, coord, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType23(int w, int x, int y, int z, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, RandomAccessFile newGrid) throws IOException {
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w + 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w - 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sWShareMultiplier;
			relevantNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x + 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gXShareMultiplier;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionOfType24(int coord, int y, long currentValue, long gWValue, long sXValue, long gYValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord - 1;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y + 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y - 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y;
			nc[3] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType25(int coord1, int coord2, long currentValue, long gWValue, long sXValue, long gYValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = coord2;
			nc[3] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2 + 1;
			nc[3] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2 - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType26(int coord, int z, long currentValue, long gWValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType27(int w, int x, int y, long currentValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType28(int w, int x, int coord, long currentValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType29(int w, int coord, int z, long currentValue, long gWValue, long sWValue, long gXValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = w + 1;
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
			nc[0] = w - 1;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
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
			nc[0] = w;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType30(int coord, int y, int z, long currentValue, long gWValue, long sXValue, long gYValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord - 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y + 1;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y - 1;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y;
			nc[3] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = y;
			nc[3] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType31(int w, int x, int y, int z, long currentValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, RandomAccessFile newGrid) throws IOException {
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w + 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w - 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x + 1;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z + 1;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = w;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z - 1;
			relevantNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, w, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantNeighborCount);
	}

	private static boolean topplePosition(RandomAccessFile newGrid, long value, int w, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) throws IOException {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, w, x, y, z, neighborValues, sortedNeighborsIndexes, 
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
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share);
				addToPosition(newGrid, w, x, y, z, value - toShare + share + toShare%3);
			} else if (n0Val < n1Val) {
				// n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share);
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share);
				addToPosition(newGrid, w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			} else {
				// n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share);
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share);
				addToPosition(newGrid, w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			}				
			break;
		case 1:
			long toShare = value - neighborValues[0];
			long share = toShare/2;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%2 + share;
				int[] nc = neighborCoords[0];
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share);
			}
			// no break
		case 0:
			addToPosition(newGrid, w, x, y, z, value);
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, w, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
					neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int w, int x, int y, int z, 
			long[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) throws IOException {
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		addToPosition(newGrid, w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(RandomAccessFile newGrid, long value, int w, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) throws IOException {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
		case 3:
			Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, w, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
				addToPosition(newGrid, w, x, y, z, value - toShare + share + toShare%shareCount);
			} else if (n0Val < n1Val) {
				// n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
				addToPosition(newGrid, w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
			} else {
				// n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
				addToPosition(newGrid, w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share * asymmetricNeighborShareMultipliers[0]);
			}
			// no break
		case 0:
			addToPosition(newGrid, w, x, y, z, value);
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, w, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
					asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int w, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) throws IOException {
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		addToPosition(newGrid, w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(RandomAccessFile newGrid, long value, int w, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) throws IOException {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, w, x, y, z, neighborValues, sortedNeighborsIndexes, 
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
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
				addToPosition(newGrid, w, x, y, z, value - toShare + share + toShare%3);
			} else if (n0Val < n1Val) {
				// n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
				addToPosition(newGrid, w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			} else {
				// n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share*n1Mult);
				addToPosition(newGrid, w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			}				
			break;
		case 1:
			long toShare = value - neighborValues[0];
			long share = toShare/2;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%2 + share;
				int[] nc = neighborCoords[0];
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share * neighborShareMultipliers[0]);
			}
			// no break
		case 0:
			addToPosition(newGrid, w, x, y, z, value);
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, w, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
					neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int w, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) throws IOException {
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		addToPosition(newGrid, w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(RandomAccessFile newGrid, long value, int w, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) throws IOException {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
		case 3:
			Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, w, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share);
				addToPosition(newGrid, w, x, y, z, value - toShare + share + toShare%shareCount);
			} else if (n0Val < n1Val) {
				// n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share);
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share);
				addToPosition(newGrid, w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
			} else {
				// n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share);
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], share);
				addToPosition(newGrid, w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share);
			}
			// no break
		case 0:
			addToPosition(newGrid, w, x, y, z, value);
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, w, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
					asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int w, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) throws IOException {
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		addToPosition(newGrid, w, x, y, z, value);
		return toppled;
	}

	@Override
	public long getFromPosition(int w, int x, int y, int z) throws IOException {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (w < 0) w = -w;
		//sort coordinates
		//TODO faster sorting?
		boolean sorted;
		do {
			sorted = true;
			if (z > y) {
				sorted = false;
				int swp = z;
				z = y;
				y = swp;
			}
			if (y > x) {
				sorted = false;
				int swp = y;
				y = x;
				x = swp;
			}
			if (x > w) {
				sorted = false;
				int swp = x;
				x = w;
				w = swp;
			}
		} while (!sorted);
		return getFromAsymmetricPosition(w, x, y, z);
	}

	@Override
	public long getFromAsymmetricPosition(int w, int x, int y, int z) throws IOException {	
		long pos = (Utils.getAnisotropic4DGridPositionCount(w)
				+ Utils.getAnisotropic3DGridPositionCount(x)
				+ ((y*y-y)/2+y)+z)
				*POSITION_BYTES;
		grid.seek(pos);
		return grid.readLong();
	}
	
	private static void addToPosition(RandomAccessFile grid, int w, int x, int y, int z, long value) throws IOException {
		long pos = (Utils.getAnisotropic4DGridPositionCount(w)
				+ Utils.getAnisotropic3DGridPositionCount(x)
				+ ((y*y-y)/2+y)+z)
				*POSITION_BYTES;
		grid.seek(pos);
		long previousValue = grid.readLong();
		grid.seek(pos);
		grid.writeLong(previousValue + value);
	}

	@Override
	public int getAsymmetricMaxW() {
		return maxW;
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
	public long getIntialValue() {
		return initialValue;
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/4D/" + initialValue;
	}
	
	@Override
	protected HashMap<String, Object> getPropertiesMap() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("initialValue", initialValue);
		properties.put("step", step);
		properties.put("maxW", maxW);
		properties.put("changed", changed);
		return properties;
	}
	
	@Override
	protected void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (long) properties.get("initialValue");
		step = (long) properties.get("step");
		maxW = (int) properties.get("maxW");
		changed = (boolean) properties.get("changed");
	}

}