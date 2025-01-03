/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricLongModel3D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class FileBackedLongAether3D extends FileBackedModel implements SymmetricLongModel3D, IsotropicCubicModelA {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -3689348814741910323L;
	public static final int POSITION_BYTES = Long.BYTES;

	private long initialValue;
	private long step;
	private int maxX;
	private Boolean changed = null;
	
	public FileBackedLongAether3D(long initialValue, String folderPath) throws IOException {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		createGridFolder(folderPath);
		currentFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step));
		grid = new RandomAccessFile(currentFile, "rw");
		grid.setLength(Utils.getAnisotropic3DGridPositionCount(7)*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
		grid.writeLong(initialValue);
		step = 0;
		maxX = 4;
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
	public FileBackedLongAether3D(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		super(backupPath, folderPath);
	}
	
	@Override
	public Boolean nextStep() throws IOException {
		RandomAccessFile newGrid = null;
		try {
			boolean changed = false;
			// x = 0, y = 0, z = 0
			long currentValue = getFromAsymmetricPosition(0, 0, 0);
			long greaterXNeighborValue = getFromAsymmetricPosition(1, 0, 0);
			File newFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step + 1));
			newGrid = new RandomAccessFile(newFile, "rw");
			newGrid.setLength(Utils.getAnisotropic3DGridPositionCount(maxX + 4)*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
			if (topplePositionOfType1(currentValue, greaterXNeighborValue, newGrid)) {
				changed = true;
			}
			// x = 1, y = 0, z = 0
			long[] relevantAsymmetricNeighborValues = new long[6];
			int[] sortedNeighborsIndexes = new int[6];
			int[][] relevantAsymmetricNeighborCoords = new int[6][3];
			int[] relevantAsymmetricNeighborShareMultipliers = new int[6];// to compensate for omitted symmetric positions
			int[] relevantAsymmetricNeighborSymmetryCounts = new int[6];// to compensate for omitted symmetric positions
			// reuse values obtained previously
			long smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(2, 0, 0);
			long greaterYNeighborValue = getFromAsymmetricPosition(1, 1, 0);
			if (topplePositionOfType2(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 1, y = 1, z = 0
			// reuse values obtained previously
			long smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(2, 1, 0);
			long greaterZNeighborValue = getFromAsymmetricPosition(1, 1, 1);
			if (topplePositionOfType3(currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 1, y = 1, z = 1
			// reuse values obtained previously
			long smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(2, 1, 1);
			if (topplePositionOfType4(currentValue, greaterXNeighborValue, smallerZNeighborValue, newGrid)) {
				changed = true;
			}
			// x = 2, y = 0, z = 0
			currentValue = getFromAsymmetricPosition(2, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 0, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(1, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 1, 0);
			if (topplePositionOfType5(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 2, y = 1, z = 0
			greaterXNeighborValue = getFromAsymmetricPosition(3, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(1, 1, 0);
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(2, 1, 1);
			if (topplePositionOfType6(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 2, y = 1, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(3, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(1, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType7(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 2, y = 2, z = 0
			currentValue = getFromAsymmetricPosition(2, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 2, 0);
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			if (topplePositionOfType8(2, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
					newGrid)) {
				changed = true;
			}
			// x = 2, y = 2, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(3, 2, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(2, 1, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(2, 2, 2);
			if (topplePositionOfType9(2, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 2, y = 2, z = 2
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(3, 2, 2);
			if (topplePositionOfType10(2, currentValue, greaterXNeighborValue, smallerZNeighborValue, newGrid)) {
				changed = true;
			}
			// x = 3, y = 0, z = 0
			currentValue = getFromAsymmetricPosition(3, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 0, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 1, 0);
			if (topplePositionOfType5(3, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 3, y = 1, z = 0
			greaterXNeighborValue = getFromAsymmetricPosition(4, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 1, 0);
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 1, 1);
			if (topplePositionOfType6(3, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 3, y = 1, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(4, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType7(3, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 3, y = 2, z = 0
			currentValue = getFromAsymmetricPosition(3, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 2, 0);
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 0);
			if (topplePositionOfType6(3, 2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 3, y = 2, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(4, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 1, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(3, 2, 2);
			if (topplePositionOfType11(3, 2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				changed = true;
			}
			// x = 3, y = 2, z = 2
			greaterXNeighborValue = getFromAsymmetricPosition(4, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 2);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType7(3, 2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 3, y = 3, z = 0
			currentValue = getFromAsymmetricPosition(3, 3, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 1);
			if (topplePositionOfType8(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 3, y = 3, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 2, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 2);
			if (topplePositionOfType9(3, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 3, y = 3, z = 2
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 2);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 2, 2);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 3);
			if (topplePositionOfType9(3, 2, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// x = 3, y = 3, z = 3
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 3);
			if (topplePositionOfType10(3, currentValue, greaterXNeighborValue, smallerZNeighborValue, newGrid)) {
				changed = true;
			}
			// 4 <= x < edge - 2
			int edge = maxX + 2;
			int edgeMinusTwo = edge - 2;
			if (toppleRangeBeyondX3(newGrid, 4, edgeMinusTwo, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) { // is it faster to reuse these arrays?
				changed = true;
			}
			//edge - 2 <= x < edge
			if (toppleRangeBeyondX3(newGrid, edgeMinusTwo, edge, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) {
				changed = true;
				maxX++;
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
	
	private boolean toppleRangeBeyondX3(RandomAccessFile newGrid, int minX, int maxX, 
			long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) throws IOException {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++) {
			// y = 0, z = 0
			long currentValue = getFromAsymmetricPosition(x, 0, 0);
			long greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, 0, 0);
			long smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, 0, 0);
			long greaterYNeighborValue = getFromAsymmetricPosition(x, 1, 0);
			if (topplePositionOfType5(x, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			// y = 1, z = 0
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, 1, 0);
			// reuse values obtained previously
			long smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(x, 2, 0);
			long greaterZNeighborValue = getFromAsymmetricPosition(x, 1, 1);
			if (topplePositionOfType6(x, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			// y = 1, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(x, 2, 1);
			// reuse values obtained previously
			long smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType7(x, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			// y = 2, z = 0
			currentValue = getFromAsymmetricPosition(x, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, 2, 0);
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(x, 3, 0);
			if (topplePositionOfType12(x, 2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			// y = 2, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(x, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(x, 1, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(x, 2, 2);
			if (topplePositionOfType11(x, 2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				anyToppled = true;
			}
			// y = 2, z = 2
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(x, 3, 2);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType13(x, 2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			int y = 3, yMinusOne = 2, yPlusOne = 4;
			for (int lastY = x - 2; y <= lastY;) {
				// z = 0
				currentValue = getFromAsymmetricPosition(x, y, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(x, y, 1);
				if (topplePositionOfType12(x, y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					anyToppled = true;
				}
				// z = 1
				greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, 1);
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = getFromAsymmetricPosition(x, y, 2);
				if (topplePositionOfType11(x, y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					anyToppled = true;
				}
				int z = 2, zPlusOne = 3;
				for (int lastZ = y - 2; z <= lastZ;) {
					greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, z);
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterZNeighborValue = getFromAsymmetricPosition(x, y, zPlusOne);
					if (topplePositionOfType15(x, y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 
							greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
							relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newGrid)) {
						anyToppled = true;
					}
					z = zPlusOne;
					zPlusOne++;
				}
				// z = y - 1
				greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, z);
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = getFromAsymmetricPosition(x, y, zPlusOne);
				if (topplePositionOfType11(x, y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					anyToppled = true;
				}
				// z = y
				z = zPlusOne;
				greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, z);
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				if (topplePositionOfType13(x, y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					anyToppled = true;
				}				 
				yMinusOne = y;
				y = yPlusOne;
				yPlusOne++;
			}
			// y = x - 1, z = 0
			currentValue = getFromAsymmetricPosition(x, y, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(x, y, 1);
			if (topplePositionOfType6(x, y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			// y = x - 1, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(x, y, 2);
			if (topplePositionOfType11(x, y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				anyToppled = true;
			}
			int z = 2, zPlusOne = 3, lastZ = y - 2;
			for(; z <= lastZ;) {
				greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, z);
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = getFromAsymmetricPosition(x, y, zPlusOne);
				if (topplePositionOfType11(x, y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newGrid)) {
					anyToppled = true;
				}
				z = zPlusOne;
				zPlusOne++;
			}
			// y = x - 1, z = y - 1
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, z);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(x, y, zPlusOne);
			if (topplePositionOfType11(x, y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newGrid)) {
				anyToppled = true;
			}
			z = zPlusOne;
			zPlusOne++;
			// y = x - 1, z = y
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne, z);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType7(x, y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			yMinusOne = y;
			y = yPlusOne;
			// y = x, z = 0
			currentValue = getFromAsymmetricPosition(x, y, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(x, y, 1);
			if (topplePositionOfType8(y, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			// y = x, z = 1
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, 1);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(x, y, 2);
			if (topplePositionOfType9(y, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			z = 2;
			zPlusOne = 3;
			lastZ++;
			for(; z <= lastZ; z = zPlusOne, zPlusOne++) {
				greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
				smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, z);
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = getFromAsymmetricPosition(x, y, zPlusOne);
				if (topplePositionOfType14(x, y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					anyToppled = true;
				}
			}			
			// y = x, z = y - 1
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
			smallerYNeighborValue = getFromAsymmetricPosition(x, yMinusOne, z);
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = getFromAsymmetricPosition(x, y, zPlusOne);
			if (topplePositionOfType9(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				anyToppled = true;
			}
			z = zPlusOne;
			// y = x, z = y
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y, z);
			if (topplePositionOfType10(y, currentValue, greaterXNeighborValue, smallerZNeighborValue, newGrid)) {
				anyToppled = true;
			}
		}
		return anyToppled;
	}

	private static boolean topplePositionOfType1(long currentValue, long greaterXNeighborValue, RandomAccessFile newGrid) throws IOException {
		boolean toppled = false;
		if (greaterXNeighborValue < currentValue) {
			long toShare = currentValue - greaterXNeighborValue;
			long share = toShare/7;
			if (share != 0) {
				toppled = true;
				addToPosition(newGrid, 0, 0, 0, currentValue - toShare + share + toShare%7);
				addToPosition(newGrid, 1, 0, 0, share);
			} else {
				addToPosition(newGrid, 0, 0, 0, currentValue);
			}			
		} else {
			addToPosition(newGrid, 0, 0, 0, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType2(long currentValue, long greaterXNeighborValue, long smallerXNeighborValue, 
			long greaterYNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;// the x coordinate
			nc[1] = 0;// the y coordinate
			nc[2] = 0;// the z coordinate
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
		return topplePosition(newGrid, currentValue, 1, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType3(long currentValue, long greaterXNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
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
		return topplePosition(newGrid, currentValue, 1, 1, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType4(long currentValue, long greaterXNeighborValue, long smallerZNeighborValue, 
			RandomAccessFile newGrid) throws IOException {
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
					addToPosition(newGrid, 1, 1, 0, share + share);// one more for the symmetric position at the other side
					addToPosition(newGrid, 1, 1, 1, currentValue - toShare + share + toShare%7);
					addToPosition(newGrid, 2, 1, 1, share);
				} else if (smallerZNeighborValue < greaterXNeighborValue) {
					// sz < gx < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 0, share + share);
					addToPosition(newGrid, 2, 1, 1, share);
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - smallerZNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 0, share + share);
					addToPosition(newGrid, 1, 1, 1, currentRemainingValue - toShare + share + toShare%4);
				} else {
					// gx < sz < current
					long toShare = currentValue - smallerZNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 0, share + share);
					addToPosition(newGrid, 2, 1, 1, share);
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, currentRemainingValue - toShare + share + toShare%4);
					addToPosition(newGrid, 2, 1, 1, share);
				}
			} else {
				// sz < current <= gx
				long toShare = currentValue - smallerZNeighborValue; 
				long share = toShare/4;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, 1, 1, 0, share + share);
				addToPosition(newGrid, 1, 1, 1, currentValue - toShare + share + toShare%4);
			}
		} else if (greaterXNeighborValue < currentValue) {
			// gx < current <= sz
			long toShare = currentValue - greaterXNeighborValue; 
			long share = toShare/4;
			if (share != 0) {
				toppled = true;
			}
			addToPosition(newGrid, 1, 1, 1, currentValue - toShare + share + toShare%4);
			addToPosition(newGrid, 2, 1, 1, share);
		} else {
			// gx >= current <= sz
			addToPosition(newGrid, 1, 1, 1, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType5(int x, long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue, long greaterYNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts,
			RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x + 1;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x - 1;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType6(int x, int y, long currentValue, long gXValue, long sXValue, 
			int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, 
			long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x + 1;
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
			nc[0] = x - 1;
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
			nc[0] = x;
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
			nc[0] = x;
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
			nc[0] = x;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType7(int x, int coord, long currentValue, long gXValue, long sXValue, 
			int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, 
			int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x + 1;
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
			nc[0] = x - 1;
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
			nc[0] = x;
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
			nc[0] = x;
			nc[1] = coord;
			nc[2] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType8(int coord, long currentValue, long greaterXNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid ) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType9(int coord, int z, long currentValue, long gXValue, long sYValue, 
			int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, 
			long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
			nc[2] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord - 1;
			nc[2] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType10(int coord, long currentValue, long greaterXNeighborValue, 
			long smallerZNeighborValue, RandomAccessFile newGrid) throws IOException {
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
					addToPosition(newGrid, coord, coord, coord - 1, share);
					addToPosition(newGrid, coord, coord, coord, currentValue - toShare + share + toShare%7);
					addToPosition(newGrid, coord + 1, coord, coord, share);
				} else if (smallerZNeighborValue < greaterXNeighborValue) {
					// sz < gx < current
					int coordMinusOne = coord - 1;
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coordMinusOne, share);
					addToPosition(newGrid, coord + 1, coord, coord, share);
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - smallerZNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coordMinusOne, share);
					addToPosition(newGrid, coord, coord, coord, currentRemainingValue - toShare + share + toShare%4);
				} else {
					// gx < sz < current
					int coordPlusOne = coord + 1;
					long toShare = currentValue - smallerZNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord - 1, share);
					addToPosition(newGrid, coordPlusOne, coord, coord, share);
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, currentRemainingValue - toShare + share + toShare%4);
					addToPosition(newGrid, coordPlusOne, coord, coord, share);
				}
			} else {
				// sz < current <= gx
				long toShare = currentValue - smallerZNeighborValue; 
				long share = toShare/4;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, coord, coord, coord - 1, share);
				addToPosition(newGrid, coord, coord, coord, currentValue - toShare + share + toShare%4);
			}
		} else if (greaterXNeighborValue < currentValue) {
			// gx < current <= sz
			long toShare = currentValue - greaterXNeighborValue; 
			long share = toShare/4;
			if (share != 0) {
				toppled = true;
			}
			addToPosition(newGrid, coord, coord, coord, currentValue - toShare + share + toShare%4);
			addToPosition(newGrid, coord + 1, coord, coord, share);
		} else {
			// gx >= current <= sz
			addToPosition(newGrid, coord, coord, coord, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType11(int x, int y, int z, long currentValue, long gXValue, long sXValue, 
			int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, 
			long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantNeighborValues, 
			int[] sortedNeighborsIndexes, int[][] relevantNeighborCoords, int[] relevantNeighborShareMultipliers, RandomAccessFile newGrid) throws IOException {
		int relevantNeighborCount = 0;
		if (gXValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount ] = gXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x + 1;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x - 1;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x;
			nc[1] = y + 1;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x;
			nc[1] = y - 1;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x;
			nc[1] = y;
			nc[2] = z + 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x;
			nc[1] = y;
			nc[2] = z - 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, x, y, z, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionOfType12(int x, int y, long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue, long greaterYNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, 
			RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount ] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x + 1;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x - 1;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = y + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType13(int x, int coord, long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue, long greaterYNeighborValue, long smallerZNeighborValue, 
			long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x + 1;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x - 1;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = coord + 1;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = coord;
			nc[2] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType14(int x, int y, int z, long currentValue, long greaterXNeighborValue, 
			long smallerYNeighborValue,	long greaterZNeighborValue, long smallerZNeighborValue, 
			long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x + 1;
			nc[1] = y;
			nc[2] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = y - 1;
			nc[2] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = y;
			nc[2] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = x;
			nc[1] = y;
			nc[2] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType15(int x, int y, int z, long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue,	long greaterYNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long smallerZNeighborValue, long[] relevantNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantNeighborCoords, RandomAccessFile newGrid) throws IOException {
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x + 1;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x - 1;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x;
			nc[1] = y + 1;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x;
			nc[1] = y - 1;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x;
			nc[1] = y;
			nc[2] = z + 1;
			relevantNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantNeighborValues[relevantNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = x;
			nc[1] = y;
			nc[2] = z - 1;
			relevantNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, x, y, z, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static boolean topplePosition(RandomAccessFile newGrid, long value, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) throws IOException {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, z, neighborValues, sortedNeighborsIndexes, 
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
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share);
					addToPosition(newGrid, x, y, z, value - toShare + share + toShare%3);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share);
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share);
					addToPosition(newGrid, x, y, z, currentRemainingValue - toShare + share + toShare%2);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share);
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share);
					addToPosition(newGrid, x, y, z, currentRemainingValue - toShare + share + toShare%2);
				}				
				break;
			case 1:
				long toShare = value - neighborValues[0];
				long share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					addToPosition(newGrid, nc[0], nc[1], nc[2], share);
				}
				// no break
			case 0:
				addToPosition(newGrid, x, y, z, value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int x, int y, int z, 
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], share);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		addToPosition(newGrid, x, y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(RandomAccessFile newGrid, long value, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) throws IOException {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share*n1Mult);
					addToPosition(newGrid, x, y, z, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share*n0Mult);
					addToPosition(newGrid, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share*n1Mult);
					addToPosition(newGrid, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
					addToPosition(newGrid, nc[0], nc[1], nc[2], share * asymmetricNeighborShareMultipliers[0]);
				}
				// no break
			case 0:
				addToPosition(newGrid, x, y, z, value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		addToPosition(newGrid, x, y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(RandomAccessFile newGrid, long value, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) throws IOException {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, z, neighborValues, sortedNeighborsIndexes, 
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
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share*n1Mult);
					addToPosition(newGrid, x, y, z, value - toShare + share + toShare%3);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share*n1Mult);
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share*n0Mult);
					addToPosition(newGrid, x, y, z, currentRemainingValue - toShare + share + toShare%2);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share*n1Mult);
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share*n1Mult);
					addToPosition(newGrid, x, y, z, currentRemainingValue - toShare + share + toShare%2);
				}				
				break;
			case 1:
				long toShare = value - neighborValues[0];
				long share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					addToPosition(newGrid, nc[0], nc[1], nc[2], share * neighborShareMultipliers[0]);
				}
				// no break
			case 0:
				addToPosition(newGrid, x, y, z, value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		addToPosition(newGrid, x, y, z, value);
		return toppled;
	}
	
	private static boolean topplePosition(RandomAccessFile newGrid, long value, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) throws IOException {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share);
					addToPosition(newGrid, x, y, z, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share);
					addToPosition(newGrid, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], share);
					addToPosition(newGrid, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
					addToPosition(newGrid, nc[0], nc[1], nc[2], share);
				}
				// no break
			case 0:
				addToPosition(newGrid, x, y, z, value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], share);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		addToPosition(newGrid, x, y, z, value);
		return toppled;
	}
	
	@Override
	public long getFromPosition(int x, int y, int z) throws IOException {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (x >= y) {
			if (y >= z) {
				//x >= y >= z
				return getFromAsymmetricPosition(x, y, z);
			} else if (x >= z) { 
				//x >= z > y
				return getFromAsymmetricPosition(x, z, y);
			} else {
				//z > x >= y
				return getFromAsymmetricPosition(z, x, y);
			}
		} else if (y >= z) {
			if (x >= z) {
				//y > x >= z
				return getFromAsymmetricPosition(y, x, z);
			} else {
				//y >= z > x
				return getFromAsymmetricPosition(y, z, x);
			}
		} else {
			// z > y > x
			return getFromAsymmetricPosition(z, y, x);
		}
	}
	
	@Override
	public long getFromAsymmetricPosition(int x, int y, int z) throws IOException {	
		long pos = (Utils.getAnisotropic3DGridPositionCount(x)+((y*y-y)/2+y)+z)*POSITION_BYTES;
		grid.seek(pos);
		return grid.readLong();
	}
	
	private static void addToPosition(RandomAccessFile grid, int x, int y, int z, long value) throws IOException {
		long pos = (Utils.getAnisotropic3DGridPositionCount(x)+((y*y-y)/2+y)+z)*POSITION_BYTES;
		grid.seek(pos);
		long previousValue = grid.readLong();
		grid.seek(pos);
		grid.writeLong(previousValue + value);
	}

	@Override
	public int getAsymmetricMaxX() {
		return maxX;
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
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/" + initialValue;
	}
	
	@Override
	protected HashMap<String, Object> getPropertiesMap() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("initialValue", initialValue);
		properties.put("step", step);
		properties.put("maxX", maxX);
		properties.put("changed", changed);
		return properties;
	}
	
	@Override
	protected void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (long) properties.get("initialValue");
		step = (long) properties.get("step");
		maxX = (int) properties.get("maxX");
		changed = (boolean) properties.get("changed");
	}
	
}