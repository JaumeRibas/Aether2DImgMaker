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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import cellularautomata.Utils;
import cellularautomata.model.FileBackedModel;
import cellularautomata.model5d.IsotropicHypercubicModel5DA;
import cellularautomata.model5d.SymmetricLongModel5D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 5D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class FileBackedAether5D extends FileBackedModel implements SymmetricLongModel5D, IsotropicHypercubicModel5DA {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -2049638230412172401L;
	public static final int POSITION_BYTES = Long.BYTES;

	private long initialValue;
	private long step;
	private int maxV;
	private Boolean changed = null;

	public FileBackedAether5D(long initialValue, String folderPath) throws IOException {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		createGridFolder(folderPath);
		currentFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step));
		grid = new RandomAccessFile(currentFile, "rw");
		grid.setLength(Utils.getAnisotropic5DGridPositionCount(9)*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
		grid.writeLong(initialValue);
		maxV = 6;
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
	public FileBackedAether5D(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		super(backupPath, folderPath);
	}

	@Override
	public Boolean nextStep() throws IOException {
		RandomAccessFile newGrid = null;
		try {
			boolean changed = false;
			// 0 | 0 | 0 | 0 | 0 | 1
			long currentValue = getFromAsymmetricPosition(0, 0, 0, 0, 0);
			long greaterVNeighborValue = getFromAsymmetricPosition(1, 0, 0, 0, 0);
			File newFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step + 1));
			newGrid = new RandomAccessFile(newFile, "rw");
			newGrid.setLength(Utils.getAnisotropic5DGridPositionCount(maxV + 4)*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
			if (topplePositionType1(currentValue, greaterVNeighborValue, newGrid)) {
				changed = true;
			}
			long[] relevantAsymmetricNeighborValues = new long[10];
			int[] sortedNeighborsIndexes = new int[10];
			int[][] relevantAsymmetricNeighborCoords = new int[10][5];
			int[] relevantAsymmetricNeighborShareMultipliers = new int[10];//to compensate for omitted symmetric positions
			int[] relevantAsymmetricNeighborSymmetryCounts = new int[10];//to compensate for omitted symmetric positions
			// 1 | 0 | 0 | 0 | 0 | 2
			//reuse values obtained previously
			long smallerVNeighborValue = currentValue;
			currentValue = greaterVNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(2, 0, 0, 0, 0);
			long greaterWNeighborValue = getFromAsymmetricPosition(1, 1, 0, 0, 0);
			if (topplePositionType2(currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 1 | 1 | 0 | 0 | 0 | 3
			//reuse values obtained previously
			long smallerWNeighborValue = currentValue;
			currentValue = greaterWNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(2, 1, 0, 0, 0);
			long greaterXNeighborValue = getFromAsymmetricPosition(1, 1, 1, 0, 0);
			if (topplePositionType3(currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 1 | 1 | 1 | 0 | 0 | 4
			//reuse values obtained previously
			long smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(2, 1, 1, 0, 0);
			long greaterYNeighborValue = getFromAsymmetricPosition(1, 1, 1, 1, 0);
			if (topplePositionType4(currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 1 | 1 | 1 | 1 | 0 | 5
			//reuse values obtained previously
			long smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1, 0);
			long greaterZNeighborValue = getFromAsymmetricPosition(1, 1, 1, 1, 1);
			if (topplePositionType5(currentValue, greaterVNeighborValue, smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 1 | 1 | 1 | 1 | 1 | 6
			//reuse values obtained previously
			long smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1, 1);
			if (topplePositionType6(currentValue, greaterVNeighborValue, smallerZNeighborValue, newGrid)) {
				changed = true;
			}
			// 2 | 0 | 0 | 0 | 0 | 7
			currentValue = getFromAsymmetricPosition(2, 0, 0, 0, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(3, 0, 0, 0, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(1, 0, 0, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(2, 1, 0, 0, 0);
			if (topplePositionType7(2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 1 | 0 | 0 | 0 | 8
			//reuse values obtained previously
			smallerWNeighborValue = currentValue;
			currentValue = greaterWNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 1, 0, 0, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(1, 1, 0, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(2, 2, 0, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 1, 1, 0, 0);
			if (topplePositionType8(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, 
					smallerWNeighborValue, 8, greaterXNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 1 | 1 | 0 | 0 | 9
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 1, 1, 0, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(1, 1, 1, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(2, 2, 1, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1, 0);
			if (topplePositionType9(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 6, 
					greaterYNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 1 | 1 | 1 | 0 | 10
			//reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 1, 1, 1, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(1, 1, 1, 1, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1, 1);
			if (topplePositionType10(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 4, 
					greaterZNeighborValue, 4, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 1 | 1 | 1 | 1 | 11
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 1, 1, 1, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(1, 1, 1, 1, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1, 1);
			if (topplePositionType11(2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 5, greaterWNeighborValue, 2, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 0 | 0 | 0 | 12
			currentValue = getFromAsymmetricPosition(2, 2, 0, 0, 0);		
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 0, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 1, 0, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 2, 1, 0, 0);
			if (topplePositionType12(2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue,
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 1 | 0 | 0 | 13
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 1, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 1, 1, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 2, 2, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1, 0);
			if (topplePositionType13(2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 6, 
					greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 1 | 1 | 0 | 14
			//reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1, 1);
			if (topplePositionType14(2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 4, 
					greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 1 | 1 | 1 | 15
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(2, 1, 1, 1, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1, 1);
			if (topplePositionType15(2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 3, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 2 | 0 | 0 | 16
			currentValue = getFromAsymmetricPosition(2, 2, 2, 0, 0);		
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 2, 0, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 2, 1, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1, 0);
			if (topplePositionType16(2, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue,
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 2 | 1 | 0 | 17
			//reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 2, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1, 1);
			if (topplePositionType17(2, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 4, 
					greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 2 | 1 | 1 | 18
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2, 2, 2, 1);
			if (topplePositionType18(2, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 4, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 2 | 2 | 0 | 19
			//reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			currentValue = getFromAsymmetricPosition(2, 2, 2, 2, 0);		
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 0);
			if (topplePositionType19(2, currentValue, greaterVNeighborValue, smallerYNeighborValue, greaterZNeighborValue,
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 2 | 2 | 1 | 20
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(2, 2, 2, 2, 2);
			if (topplePositionType20(2, 1, currentValue, greaterVNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 5, smallerZNeighborValue, 2,
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 2 | 2 | 2 | 2 | 2 | 21
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;		
			greaterVNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 2);
			if (topplePositionType21(2, currentValue, greaterVNeighborValue, smallerZNeighborValue, newGrid)) {
				changed = true;
			}		
			if (toppleRangeType1(3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 3 | 2 | 0 | 0 | 0 | 26
			currentValue = getFromAsymmetricPosition(3, 2, 0, 0, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 0, 0, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 0, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 0, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 1, 0, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 2, 1, 0, 0);
			if (topplePositionType8(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, 
					smallerWNeighborValue, 1, greaterXNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 1 | 0 | 0 | 27
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 1, 0, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 1, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 1, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 1, 1, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 2, 2, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1, 0);
			if (topplePositionType22(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
					smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 1 | 1 | 0 | 28
			//reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 1, 1, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 1, 1, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 1, 1, 1, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1, 1);
			if (topplePositionType23(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
					smallerYNeighborValue, 4, greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 1 | 1 | 1 | 29
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 1, 1, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 1, 1, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 1, 1, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 1, 1, 1, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 1);
			if (topplePositionType24(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 2 | 0 | 0 | 30
			currentValue = getFromAsymmetricPosition(3, 2, 2, 0, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 2, 0, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 2, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 0, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 2, 1, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 0);
			if (topplePositionType9(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 2 | 1 | 0 | 31
			//reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 2, 1, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 1);
			if (topplePositionType25(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
					smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 2 | 1 | 1 | 32
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 2, 1, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 2, 1, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 2, 1, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 1);
			if (topplePositionType26(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 2 | 2 | 0 | 33
			//reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			currentValue = getFromAsymmetricPosition(3, 2, 2, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 2, 2, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 2, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 0);
			if (topplePositionType10(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 2 | 2 | 1 | 34
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 2, 2, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 2, 2, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 2);
			if (topplePositionType27(3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 4,
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 2 | 2 | 2 | 2 | 35
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 2, 2, 2, 2);
			smallerVNeighborValue = getFromAsymmetricPosition(2, 2, 2, 2, 2);
			greaterWNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 2);
			if (topplePositionType11(3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 5, greaterWNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType2(3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 3 | 3 | 2 | 0 | 0 | 39
			currentValue = getFromAsymmetricPosition(3, 3, 2, 0, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 2, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 3, 0, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 3, 1, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1, 0);
			if (topplePositionType13(3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 3 | 2 | 1 | 0 | 40
			//reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 2, 1, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 3, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 3, 1, 1, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1, 1);
			if (topplePositionType28(3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 3 | 2 | 1 | 1 | 41
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 2, 1, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 1, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 3, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 3, 1, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 1);
			if (topplePositionType29(3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 3 | 2 | 2 | 0 | 42
			//reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			currentValue = getFromAsymmetricPosition(3, 3, 2, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2, 0);
			if (topplePositionType14(3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 3 | 2 | 2 | 1 | 43
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 2);		
			if (topplePositionType30(3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 3,
					smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 3 | 2 | 2 | 2 | 44
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(3, 2, 2, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2, 2);
			if (topplePositionType15(3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 3, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType3(3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 3 | 3 | 3 | 2 | 0 | 47
			currentValue = getFromAsymmetricPosition(3, 3, 3, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 3, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 3, 3, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2, 1);
			if (topplePositionType17(3, 2, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 3 | 3 | 2 | 1 | 48
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(3, 3, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2, 2);
			if (topplePositionType31(3, 2, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 3 | 3 | 3 | 2 | 2 | 49
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(3, 3, 3, 3, 2);
			if (topplePositionType18(3, 2, currentValue, greaterVNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 4, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType4(3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType5(4, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType6(4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 4 | 3 | 2 | 0 | 0 | 65
			currentValue = getFromAsymmetricPosition(4, 3, 2, 0, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 2, 0, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 2, 0, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 2, 0, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 3, 0, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 3, 1, 0, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 2, 1, 0);
			if (topplePositionType22(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
					smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 3 | 2 | 1 | 0 | 66
			//reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 2, 1, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 2, 1, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 1, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 3, 1, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 3, 1, 1, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(4, 3, 2, 1, 1);
			if (topplePositionType36(4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 3 | 2 | 1 | 1 | 67
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 2, 1, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 2, 1, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 2, 1, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 1, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 3, 1, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 3, 1, 1, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 1);
			if (topplePositionType37(4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 3 | 2 | 2 | 0 | 68
			//reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			currentValue = getFromAsymmetricPosition(4, 3, 2, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 2, 2, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 2, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 0);
			if (topplePositionType23(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 3 | 2 | 2 | 1 | 69
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 2, 2, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 2, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(4, 3, 2, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 2);
			if (topplePositionType38(4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
					smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 3 | 2 | 2 | 2 | 70
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 2, 2, 2);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 2, 2, 2);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 2, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 2, 2, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 2);
			if (topplePositionType24(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType7(4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 4 | 3 | 3 | 2 | 0 | 73
			currentValue = getFromAsymmetricPosition(4, 3, 3, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 3, 2, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 3, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 3, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(4, 3, 3, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 1);
			if (topplePositionType25(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 3 | 3 | 2 | 1 | 74
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 3, 2, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 3, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(4, 3, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 2);
			if (topplePositionType39(4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 3 | 3 | 2 | 2 | 75
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(5, 3, 3, 2, 2);
			smallerVNeighborValue = getFromAsymmetricPosition(3, 3, 3, 2, 2);
			greaterWNeighborValue = getFromAsymmetricPosition(4, 4, 3, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 3, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 3, 3, 3, 2);
			if (topplePositionType26(4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType8(4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType9(4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 4 | 4 | 3 | 2 | 0 | 86
			currentValue = getFromAsymmetricPosition(4, 4, 3, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(5, 4, 3, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 4, 4, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 4, 2, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 4, 3, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(4, 4, 3, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(4, 4, 3, 2, 1);
			if (topplePositionType28(4, 3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 4 | 3 | 2 | 1 | 87
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(5, 4, 3, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 4, 4, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 4, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 4, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(4, 4, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(4, 4, 3, 2, 2);
			if (topplePositionType43(4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 4 | 4 | 3 | 2 | 2 | 88
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(5, 4, 3, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(4, 3, 3, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(4, 4, 4, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(4, 4, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(4, 4, 3, 3, 2);
			if (topplePositionType29(4, 3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType10(4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType11(5, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType12(5, 4, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType13(5, 4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// 5 | 4 | 3 | 2 | 0 | 121
			currentValue = getFromAsymmetricPosition(5, 4, 3, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(6, 4, 3, 2, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(4, 4, 3, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(5, 5, 3, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(5, 3, 3, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(5, 4, 4, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(5, 4, 2, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(5, 4, 3, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(5, 4, 3, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(5, 4, 3, 2, 1);
			if (topplePositionType36(5, 4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// 5 | 4 | 3 | 2 | 1 | 122
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(6, 4, 3, 2, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(4, 4, 3, 2, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(5, 5, 3, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(5, 3, 3, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(5, 4, 4, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(5, 4, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(5, 4, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(5, 4, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(5, 4, 3, 2, 2);
			if (topplePositionType47(5, 4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newGrid)) {
				changed = true;
			}
			// 5 | 4 | 3 | 2 | 2 | 123
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(6, 4, 3, 2, 2);
			smallerVNeighborValue = getFromAsymmetricPosition(4, 4, 3, 2, 2);
			greaterWNeighborValue = getFromAsymmetricPosition(5, 5, 3, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(5, 3, 3, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(5, 4, 4, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(5, 4, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(5, 4, 3, 3, 2);
			if (topplePositionType37(5, 4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType14(5, 4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			//6 <= v < edge - 2
			int edge = maxV + 2;
			int edgeMinusTwo = edge - 2;
			if (toppleRangeBeyondV5(newGrid, 6, edgeMinusTwo, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) {
				changed = true;
			}
			//edge - 2 <= v < edge
			if (toppleRangeBeyondV5(newGrid, edgeMinusTwo, edge, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) {
				changed = true;
				maxV++;
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
			return changed;
		} catch (Exception ex) {
			if (newGrid != null)
				newGrid.close();
			close();
			throw ex;
		}
	}

	private boolean toppleRangeBeyondV5(RandomAccessFile newGrid, int minV,
			int maxV, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords,
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) throws IOException {
		boolean changed = false;
		int v = minV, vMinusOne = v - 1, vMinusTwo = v - 2, vMinusThree = v - 3, vMinusFour = v - 4, vPlusOne = v + 1, vPlusTwo = v + 2;
		for (; v != maxV; vMinusFour = vMinusThree, vMinusThree = vMinusTwo, vMinusTwo = vMinusOne, vMinusOne = v, v = vPlusOne, vPlusOne = vPlusTwo, vPlusTwo++) {
			if (toppleRangeType11(v, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType15(v, 4, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType16(v, 4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// v | 4 | 3 | 2 | 0 | 156
			long currentValue = getFromAsymmetricPosition(v, 4, 3, 2, 0);
			long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 4, 3, 2, 0);
			long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 4, 3, 2, 0);
			long greaterWNeighborValue = getFromAsymmetricPosition(v, 5, 3, 2, 0);
			long smallerWNeighborValue = getFromAsymmetricPosition(v, 3, 3, 2, 0);
			long greaterXNeighborValue = getFromAsymmetricPosition(v, 4, 4, 2, 0);
			long smallerXNeighborValue = getFromAsymmetricPosition(v, 4, 2, 2, 0);
			long greaterYNeighborValue = getFromAsymmetricPosition(v, 4, 3, 3, 0);
			long smallerYNeighborValue = getFromAsymmetricPosition(v, 4, 3, 1, 0);
			long greaterZNeighborValue = getFromAsymmetricPosition(v, 4, 3, 2, 1);
			if (topplePositionType36(v, 4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | 4 | 3 | 2 | 1 | 157
			//reuse values obtained previously
			long smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 4, 3, 2, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 4, 3, 2, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(v, 5, 3, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(v, 3, 3, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(v, 4, 4, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, 4, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, 4, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, 4, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, 4, 3, 2, 2);
			if (topplePositionType47(v, 4, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newGrid)) {
				changed = true;
			}
			// v | 4 | 3 | 2 | 2 | 158
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 4, 3, 2, 2);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 4, 3, 2, 2);
			greaterWNeighborValue = getFromAsymmetricPosition(v, 5, 3, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(v, 3, 3, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(v, 4, 4, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(v, 4, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(v, 4, 3, 3, 2);
			if (topplePositionType37(v, 4, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType17(v, 4, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			int w = 5, wMinusOne = w - 1, wPlusOne = w + 1;
			for (int wMinusTwo = w - 2, wMinusThree = w - 3; w != vMinusOne; wMinusThree = wMinusTwo, wMinusTwo = wMinusOne, wMinusOne = w, w = wPlusOne, wPlusOne++) {
				if (toppleRangeType15(v, w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
				if (toppleRangeType18(v, w, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
				// v | w | 3 | 2 | 0 | 195
				currentValue = getFromAsymmetricPosition(v, w, 3, 2, 0);
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 0);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 3, 2, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 3, 2, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 1);
				if (topplePositionType36(v, w, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				// v | w | 3 | 2 | 1 | 196
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 1);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 3, 2, 1);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 3, 2, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 2);
				if (topplePositionType47(v, w, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				// v | w | 3 | 2 | 2 | 197
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 2);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 3, 2, 2);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 3, 2, 2);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 2);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 2);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 2);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 2);
				if (topplePositionType37(v, w, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
						greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				if (toppleRangeType19(v, w, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}			
				int x = 4, xMinusOne = x - 1, xPlusOne = x + 1;
				for (int xMinusTwo = x - 2; x != wMinusOne; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
					if (toppleRangeType18(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
							relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
						changed = true;
					}
					// v | w | x | 2 | 0 | 223
					currentValue = getFromAsymmetricPosition(v, w, x, 2, 0);
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 0);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 0);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 0);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 0);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 0);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 0);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 0);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
					if (topplePositionType58(v, w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
							greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
					// v | w | x | 2 | 1 | 224
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 1);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 1);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 1);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 1);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 1);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 1);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 1);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 2);
					if (topplePositionType47(v, w, x, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
					// v | w | x | 2 | 2 | 225
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 2);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 2);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 2);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 2);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 2);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 2);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 2);
					if (topplePositionType59(v, w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
							smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
					int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
					for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
						// v | w | x | y | 0 | 223
						currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
						greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
						smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
						greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
						smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
						greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
						smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
						greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
						smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
						greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
						if (topplePositionType58(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
								greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
								relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
							changed = true;
						}
						// v | w | x | y | 1 | 238
						//reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
						smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
						greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
						smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
						greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
						smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
						greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
						smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
						greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
						if (topplePositionType47(v, w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
								greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
								newGrid)) {
							changed = true;
						}
						int z = 2, zPlusOne = z + 1;
						for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
							// v | w | x | y | z | 243
							//reuse values obtained previously
							smallerZNeighborValue = currentValue;
							currentValue = greaterZNeighborValue;
							greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
							smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
							greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
							smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
							greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
							smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
							greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
							smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
							greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
							if (topplePositionType63(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
									greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newGrid)) {
								changed = true;
							}
						}
						// v | w | x | y | z | 239
						//reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
						smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
						greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
						smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
						greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
						smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
						greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
						smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
						greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
						if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
								greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
								newGrid)) {
							changed = true;
						}
						z = y;
						// v | w | x | y | z | 225
						//reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
						smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
						greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
						smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
						greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
						smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
						greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
						if (topplePositionType59(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
								smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
								relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
							changed = true;
						}
					}
					// v | w | x | y | 0 | 195
					currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
					if (topplePositionType36(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
							greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
					// v | w | x | y | 1 | 226
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
					if (topplePositionType47(v, w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
							greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
					int z = 2, zPlusOne = z + 1;
					for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
						// v | w | x | y | z | 240
						//reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
						smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
						greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
						smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
						greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
						smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
						greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
						smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
						greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
						if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
								greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
								newGrid)) {
							changed = true;
						}
					}
					// v | w | x | y | z | 227
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
					if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
							greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
					z = xMinusOne;
					// v | w | x | y | z | 197
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					if (topplePositionType37(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
							greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
					if (toppleRangeType19(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
							relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
						changed = true;
					}
				}
				if (toppleRangeType16(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
				// v | w | x | 2 | 0 | 200
				currentValue = getFromAsymmetricPosition(v, w, x, 2, 0);
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 0);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
				if (topplePositionType36(v, w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				// v | w | x | 2 | 1 | 201
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 1);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 1);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 2);
				if (topplePositionType47(v, w, x, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				// v | w | x | 2 | 2 | 202
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 2);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 2);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 2);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 2);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 2);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 2);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 2);
				if (topplePositionType37(v, w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
				for (; y != wMinusTwo; yMinusOne = y, y = yPlusOne, yPlusOne++) {
					// v | w | x | y | 0 | 200
					currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
					if (topplePositionType36(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
					// v | w | x | y | 1 | 229
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
					if (topplePositionType47(v, w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
					int z = 2, zPlusOne = z + 1;
					for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
						// v | w | x | y | z | 241
						//reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
						smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
						greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
						smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
						greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
						smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
						greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
						smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
						greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
						if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
								greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
								newGrid)) {
							changed = true;
						}
					}
					// v | w | x | y | z | 230
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
					if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
					z = y;
					// v | w | x | y | z | 202
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					if (topplePositionType37(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
				}
				// v | w | x | y | 0 | 156
				currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
				if (topplePositionType36(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				// v | w | x | y | 1 | 203
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
				if (topplePositionType47(v, w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				int z = 2, zPlusOne = z + 1;
				for (; z != wMinusThree; z = zPlusOne, zPlusOne++) {
					// v | w | x | y | z | 231
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
					if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
							greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
				}
				// v | w | x | y | z | 204
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				z = wMinusTwo;
				// v | w | x | y | z | 158
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				if (topplePositionType37(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
						greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				if (toppleRangeType17(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
			}
			if (toppleRangeType12(v, w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeType20(v, w, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// v | w | 3 | 2 | 0 | 169
			currentValue = getFromAsymmetricPosition(v, w, 3, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 3, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 3, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 1);
			if (topplePositionType36(v, w, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | 3 | 2 | 1 | 170
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 3, 2, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 3, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 2);
			if (topplePositionType47(v, w, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newGrid)) {
				changed = true;
			}
			// v | w | 3 | 2 | 2 | 171
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 2);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 3, 2, 2);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 3, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 2);
			if (topplePositionType37(v, w, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType21(v, w, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			int x = 4, xMinusOne = x - 1, xPlusOne = x + 1;
			for (int xMinusTwo = x - 2; x != vMinusTwo; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
				if (toppleRangeType20(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
				// v | w | x | 2 | 0 | 209
				currentValue = getFromAsymmetricPosition(v, w, x, 2, 0);
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 0);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
				if (topplePositionType36(v, w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				// v | w | x | 2 | 1 | 210
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 1);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 1);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 2);
				if (topplePositionType47(v, w, x, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				// v | w | x | 2 | 2 | 211
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 2);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 2);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 2);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 2);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 2);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 2);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 2);
				if (topplePositionType37(v, w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
				for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
					// v | w | x | y | 0 | 209
					currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
					if (topplePositionType36(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
					// v | w | x | y | 1 | 233
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
					if (topplePositionType47(v, w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
					int z = 2, zPlusOne = z + 1;
					for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
						// v | w | x | y | z | 242
						//reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
						smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
						greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
						smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
						greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
						smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
						greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
						smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
						greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
						if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
								greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
								newGrid)) {
							changed = true;
						}
					}
					// v | w | x | y | z | 234
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
					if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
					z = y;
					// v | w | x | y | z | 211
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					if (topplePositionType37(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
				}
				// v | w | x | y | 0 | 169
				currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
				if (topplePositionType36(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				// v | w | x | y | 1 | 212
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
				if (topplePositionType47(v, w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				int z = 2, zPlusOne = z + 1;
				for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
					// v | w | x | y | z | 235
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
					if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
							greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
				}
				// v | w | x | y | z | 213
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				z = xMinusOne;
				// v | w | x | y | z | 171
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				if (topplePositionType37(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
						greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				if (toppleRangeType21(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
			}
			if (toppleRangeType13(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// v | w | x | 2 | 0 | 174
			currentValue = getFromAsymmetricPosition(v, w, x, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
			if (topplePositionType36(v, w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | x | 2 | 1 | 175
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 2);
			if (topplePositionType47(v, w, x, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newGrid)) {
				changed = true;
			}
			// v | w | x | 2 | 2 | 176
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 2);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 2);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 2);
			if (topplePositionType37(v, w, x, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
			for (; y != vMinusThree; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				// v | w | x | y | 0 | 174
				currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
				if (topplePositionType36(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				// v | w | x | y | 1 | 215
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
				if (topplePositionType47(v, w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				int z = 2, zPlusOne = z + 1;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					// v | w | x | y | z | 236
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
					greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
					if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
							greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
							newGrid)) {
						changed = true;
					}
				}
				// v | w | x | y | z | 216
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
				z = y;
				// v | w | x | y | z | 176
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				if (topplePositionType37(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			// v | w | x | y | 0 | 121
			currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
			if (topplePositionType36(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | x | y | 1 | 177
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
			if (topplePositionType47(v, w, x, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newGrid)) {
				changed = true;
			}
			int z = 2, zPlusOne = z + 1;
			for (; z != vMinusFour; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 217
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						newGrid)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 178
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
			if (topplePositionType47(v, w, x, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					newGrid)) {
				changed = true;
			}
			z = vMinusThree;
			// v | w | x | y | z | 123
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			if (topplePositionType37(v, w, x, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType14(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
		}
		this.changed = changed;
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}

	private boolean toppleRangeType1(int v, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | 0 | 0 | 0 | 0 | 7
		long currentValue = getFromAsymmetricPosition(v, 0, 0, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 0, 0, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 0, 0, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, 1, 0, 0, 0);
		if (topplePositionType7(v, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 1 | 0 | 0 | 0 | 22
		//reuse values obtained previously
		long smallerWNeighborValue = currentValue;
		currentValue = greaterWNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 1, 0, 0, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 1, 0, 0, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 2, 0, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, 1, 1, 0, 0);
		if (topplePositionType8(v, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, 
				smallerWNeighborValue, 8, greaterXNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 1 | 1 | 0 | 0 | 23
		//reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 1, 1, 0, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 1, 1, 0, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 2, 1, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, 1, 1, 1, 0);
		if (topplePositionType9(v, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 6, 
				greaterYNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 1 | 1 | 1 | 0 | 24
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 1, 1, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 1, 1, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 2, 1, 1, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, 1, 1, 1, 1);
		if (topplePositionType10(v, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 4, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 1 | 1 | 1 | 1 | 25
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 1, 1, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 1, 1, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 2, 1, 1, 1);
		if (topplePositionType11(v, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType2(int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		// crd | crd | 0 | 0 | 0 | 12
		long currentValue = getFromAsymmetricPosition(crd, crd, 0, 0, 0);		
		long greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, 0, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(crd, crdMinusOne, 0, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(crd, crd, 1, 0, 0);
		if (topplePositionType12(crd, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue,
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | 1 | 0 | 0 | 36
		//reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, 1, 0, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(crd, crdMinusOne, 1, 0, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(crd, crd, 2, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(crd, crd, 1, 1, 0);
		if (topplePositionType13(crd, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 6, 
				greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | 1 | 1 | 0 | 37
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, 1, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(crd, crdMinusOne, 1, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(crd, crd, 2, 1, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(crd, crd, 1, 1, 1);
		if (topplePositionType14(crd, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | 1 | 1 | 1 | 38
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, 1, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(crd, crdMinusOne, 1, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(crd, crd, 2, 1, 1);
		if (topplePositionType15(crd, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType3(int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		// crd | crd | crd | 0 | 0 | 16
		long currentValue = getFromAsymmetricPosition(crd, crd, crd, 0, 0);		
		long greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, crd, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(crd, crd, crdMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(crd, crd, crd, 1, 0);
		if (topplePositionType16(crd, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue,
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | crd | 1 | 0 | 45
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, crd, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(crd, crd, crdMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(crd, crd, crd, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(crd, crd, crd, 1, 1);
		if (topplePositionType17(crd, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | crd | 1 | 1 | 46
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, crd, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(crd, crd, crdMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(crd, crd, crd, 2, 1);
		if (topplePositionType18(crd, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType4(int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1;
		boolean changed = false;
		// crd | crd | crd | crd | 0 | 19
		long currentValue = getFromAsymmetricPosition(crd, crd, crd, crd, 0);		
		long greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, crd, crd, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(crd, crd, crd, crdMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(crd, crd, crd, crd, 1);
		if (topplePositionType19(crd, currentValue, greaterVNeighborValue, smallerYNeighborValue, greaterZNeighborValue,
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | crd | crd | 1 | 50
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, crd, crd, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(crd, crd, crd, crdMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(crd, crd, crd, crd, 2);
		if (topplePositionType20(crd, 1, currentValue, greaterVNeighborValue, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2,
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// crd | crd | crd | crd | z | 96
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;		
			greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, crd, crd, z);
			smallerYNeighborValue = getFromAsymmetricPosition(crd, crd, crd, crdMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(crd, crd, crd, crd, zPlusOne);
			if (topplePositionType46(crd, z, currentValue, greaterVNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue,
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// crd | crd | crd | crd | z | 51
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;		
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, crd, crd, z);
		smallerYNeighborValue = getFromAsymmetricPosition(crd, crd, crd, crdMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(crd, crd, crd, crd, zPlusOne);
		if (topplePositionType20(crd, z, currentValue, greaterVNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 5, smallerZNeighborValue, 1,
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | crd | crd | crd | 21
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;		
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, crd, crd, crd);
		if (topplePositionType21(crd, currentValue, greaterVNeighborValue, smallerZNeighborValue, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType5(int v, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		boolean changed = false;
		int vPlusOne = v + 1, vMinusOne = v - 1;
		if (toppleRangeType1(v, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | 2 | 0 | 0 | 0 | 52
		long currentValue = getFromAsymmetricPosition(v, 2, 0, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 0, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 0, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 0, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, 1, 0, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, 2, 1, 0, 0);
		if (topplePositionType32(v, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue,
				greaterXNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 1 | 0 | 0 | 53
		//reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 1, 0, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 1, 0, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 1, 0, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, 1, 1, 0, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, 2, 2, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, 2, 1, 1, 0);
		if (topplePositionType22(v, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 1 | 1 | 0 | 54
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 1, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 1, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 1, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, 1, 1, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, 2, 2, 1, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, 2, 1, 1, 1);
		if (topplePositionType23(v, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 4, greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 1 | 1 | 1 | 55
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 1, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 1, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 1, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, 1, 1, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, 2, 2, 1, 1);
		if (topplePositionType24(v, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 2 | 0 | 0 | 56
		currentValue = getFromAsymmetricPosition(v, 2, 2, 0, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 2, 0, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 2, 0, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 2, 0, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, 2, 1, 0, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, 2, 2, 1, 0);
		if (topplePositionType33(v, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue,
				greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 2 | 1 | 0 | 57
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 2, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 2, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 2, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, 2, 1, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, 2, 2, 2, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, 2, 2, 1, 1);
		if (topplePositionType25(v, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 2 | 1 | 1 | 58
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 2, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 2, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 2, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, 2, 1, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, 2, 2, 2, 1);
		if (topplePositionType26(v, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 2 | 2 | 0 | 59
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = getFromAsymmetricPosition(v, 2, 2, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 2, 2, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 2, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 0);
		if (topplePositionType34(v, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerYNeighborValue,
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 2 | 2 | 1 | 60
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 2, 2, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 2, 2, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, 2, 2, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, 2, 2, 2, 2);
		if (topplePositionType27(v, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 4,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 2 | 2 | 2 | 2 | 61
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 2, 2, 2, 2);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 2, 2, 2, 2);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 2);
		if (topplePositionType35(v, 2,currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType6(int v, int w, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		//int w = v - 1;
		int  wMinusOne = w - 1, wPlusOne = w + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | 0 | 0 | 0 | 26
		long currentValue = getFromAsymmetricPosition(v, w, 0, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 0, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 0, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 0, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 0, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, 1, 0, 0);
		if (topplePositionType8(v, w, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, 
				smallerWNeighborValue, 1, greaterXNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 1 | 0 | 0 | 62
		//reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 1, 0, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 1, 0, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 1, 0, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 1, 0, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 2, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 0);
		if (topplePositionType22(v, w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 1 | 1 | 0 | 63
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 1, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 1, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 1, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 1, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 1);
		if (topplePositionType23(v, w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 1 | 1 | 1 | 64
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 1, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 1, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 1, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 1, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 1);
		if (topplePositionType24(v, w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType7(int v, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		//int crd = v - 1;
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | crd | crd | 0 | 0 | 30
		long currentValue = getFromAsymmetricPosition(v, crd, crd, 0, 0);		
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, crd, crdMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, crd, crd, 1, 0);
		if (topplePositionType9(v, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | crd | crd | 1 | 0 | 71
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, crd, crdMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, crd, crd, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, 1, 1);
		if (topplePositionType25(v, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | crd | crd | 1 | 1 | 72
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, crd, crdMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, crd, crd, 2, 1);
		if (topplePositionType26(v, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType8(int v, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		//int v = crd + 1;
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | crd | crd | crd | 0 | 33
		long currentValue = getFromAsymmetricPosition(v, crd, crd, crd, 0);		
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(v, crd, crd, crdMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, crd, 1);
		if (topplePositionType10(v, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | crd | crd | crd | 1 | 76
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, crd, crd, crdMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, crd, 2);
		if (topplePositionType27(v, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | crd | crd | crd | z | 131
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, crd, crd, crdMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, crd, zPlusOne);
			if (topplePositionType27(v, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | crd | crd | crd | z | 77
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, crd, crd, crdMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, crd, zPlusOne);
		if (topplePositionType27(v, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 4, greaterWNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 4,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | crd | crd | crd | crd | 35
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, crd);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, crd);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, crd);
		if (topplePositionType11(v, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 5, greaterWNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int w = crdPlusOne, wMinusOne = crd;
		if (toppleRangeType2(w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 2 | 0 | 0 | 78
		currentValue = getFromAsymmetricPosition(v, w, 2, 0, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 0);
		if (topplePositionType40(w, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue,
				greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 1 | 0 | 79
		//reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 1);
		if (topplePositionType28(w, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 1 | 1 | 80
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 1);
		if (topplePositionType29(w, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 0 | 81
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 0);
		if (topplePositionType41(w, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue,
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 1 | 82
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 2);		
		if (topplePositionType30(w, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 2 | 83
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 2);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 2);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 2);
		if (topplePositionType42(w, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType9(int crd, int x, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		//int x = crd - 1;
		int  crdMinusOne = x, crdPlusOne = crd + 1, xMinusOne = x - 1, xPlusOne = crd;
		boolean changed = false;
		// crd | crd | x | 0 | 0 | 39
		long currentValue = getFromAsymmetricPosition(crd, crd, x, 0, 0);		
		long greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, x, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(crd, crdMinusOne, x, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(crd, crd, xPlusOne, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(crd, crd, xMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(crd, crd, x, 1, 0);
		if (topplePositionType13(crd, x, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | x | 1 | 0 | 84
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, x, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(crd, crdMinusOne, x, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(crd, crd, xPlusOne, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(crd, crd, xMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(crd, crd, x, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(crd, crd, x, 1, 1);
		if (topplePositionType28(crd, x, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd | crd | x | 1 | 1 | 85
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crdPlusOne, crd, x, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(crd, crdMinusOne, x, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(crd, crd, xPlusOne, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(crd, crd, xMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(crd, crd, x, 2, 1);
		if (topplePositionType29(crd, x, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType10(int crd1, int crd2, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		//int crd2 = crd1 - 1;
		int crd1PlusOne = crd1 + 1, crd2MinusOne = crd2 - 1, crd2PlusOne = crd1, crd1MinusOne = crd2, crd1MinusTwo = crd2MinusOne;
		boolean changed = false;
		// crd1 | crd1 | crd2 | crd2 | 0 | 42
		long currentValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2, 0);		
		long greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd2, crd2, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(crd1, crd1MinusOne, crd2, crd2, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2PlusOne, crd2, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2MinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2, 1);
		if (topplePositionType14(crd1, crd2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd1 | crd1 | crd2 | crd2 | 1 | 89
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd2, crd2, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(crd1, crd1MinusOne, crd2, crd2, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2PlusOne, crd2, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2MinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2, 2);		
		if (topplePositionType30(crd1, crd2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crd1MinusTwo; z = zPlusOne, zPlusOne++) {
			// crd1 | crd1 | crd2 | crd2 | z | 144
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd2, crd2, z);
			smallerWNeighborValue = getFromAsymmetricPosition(crd1, crd1MinusOne, crd2, crd2, z);
			greaterXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2PlusOne, crd2, z);
			smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2MinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2, zPlusOne);		
			if (topplePositionType30(crd1, crd2, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// crd1 | crd1 | crd2 | crd2 | z | 90
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd2, crd2, z);
		smallerWNeighborValue = getFromAsymmetricPosition(crd1, crd1MinusOne, crd2, crd2, z);
		greaterXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2PlusOne, crd2, z);
		smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2MinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2, crd2, zPlusOne);		
		if (topplePositionType30(crd1, crd2, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd1 | crd1 | crd2 | crd2 | crd2 | 44
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd2, crd2, crd2);
		smallerWNeighborValue = getFromAsymmetricPosition(crd1, crd1MinusOne, crd2, crd2, crd2);
		greaterXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd2PlusOne, crd2, crd2);
		if (topplePositionType15(crd1, crd2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 3, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType3(crd1, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// crd1 | crd1 | crd1 | 2 | 0 | 91
		currentValue = getFromAsymmetricPosition(crd1, crd1, crd1, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, 2, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, 2, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, 3, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, 1, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, 2, 1);
		if (topplePositionType44(crd1, 2, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd1 | crd1 | crd1 | 2 | 1 | 92
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, 2, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, 2, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, 3, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, 2, 2);
		if (topplePositionType31(crd1, 2, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd1 | crd1 | crd1 | 2 | 2 | 93
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, 2, 2);
		smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, 2, 2);
		greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, 3, 2);
		if (topplePositionType45(crd1, 2, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
		for (; y != crd1MinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// crd1 | crd1 | crd1 | y | 0 | 91
			currentValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, 1);
			if (topplePositionType44(crd1, y, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
					greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// crd1 | crd1 | crd1 | y | 1 | 145
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yMinusOne, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, 2);
			if (topplePositionType31(crd1, y, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = 2;
			zPlusOne = z + 1;
			for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// crd1 | crd1 | crd1 | y | z | 192
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, zPlusOne);
				if (topplePositionType57(crd1, y, z, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			// crd1 | crd1 | crd1 | y | z | 146
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, zPlusOne);
			if (topplePositionType31(crd1, y, z, currentValue, greaterVNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = zPlusOne;
			// crd1 | crd1 | crd1 | y | z | 93
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, z);
			if (topplePositionType45(crd1, y, currentValue, greaterVNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// crd1 | crd1 | crd1 | y | 0 | 47
		currentValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yMinusOne, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, 1);
		if (topplePositionType17(crd1, y, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// crd1 | crd1 | crd1 | y | 1 | 94
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, 2);
		if (topplePositionType31(crd1, y, 1, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		z = 2;
		zPlusOne = z + 1;
		for (; z != crd1MinusTwo; z = zPlusOne, zPlusOne++) {
			// crd1 | crd1 | crd1 | y | z | 147
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, zPlusOne);
			if (topplePositionType31(crd1, y, z, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// crd1 | crd1 | crd1 | y | z | 95
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, z);
		smallerYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, y, zPlusOne);
		if (topplePositionType31(crd1, y, z, currentValue, greaterVNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 4, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		z = zPlusOne;
		// crd1 | crd1 | crd1 | y | z | 49
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(crd1PlusOne, crd1, crd1, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1MinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(crd1, crd1, crd1, yPlusOne, z);
		if (topplePositionType18(crd1, y, currentValue, greaterVNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 4, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType4(crd1, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType11(int v, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		if (toppleRangeType5(v, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeType22(v, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | 3 | 2 | 0 | 0 | 100
		long currentValue = getFromAsymmetricPosition(v, 3, 2, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 2, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 2, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 2, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, 2, 2, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, 3, 3, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, 3, 1, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, 3, 2, 1, 0);
		if (topplePositionType22(v, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 3 | 2 | 1 | 0 | 101
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 2, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 2, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 2, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, 2, 2, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, 3, 3, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, 3, 1, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, 3, 2, 1, 1);
		if (topplePositionType36(v, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 3 | 2 | 1 | 1 | 102
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 2, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 2, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 2, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, 2, 2, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, 3, 3, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, 3, 1, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 1);
		if (topplePositionType37(v, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 3 | 2 | 2 | 0 | 103
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = getFromAsymmetricPosition(v, 3, 2, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 2, 2, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 2, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 2, 2, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, 2, 2, 2, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, 3, 3, 2, 0);
		if (topplePositionType23(v, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 3 | 2 | 2 | 1 | 104
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 2, 2, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 2, 2, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 2, 2, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, 2, 2, 2, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, 3, 3, 2, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, 3, 2, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 2);
		if (topplePositionType38(v, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 3 | 2 | 2 | 2 | 105
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 2, 2, 2);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 2, 2, 2);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 2, 2, 2);
		smallerWNeighborValue = getFromAsymmetricPosition(v, 2, 2, 2, 2);
		greaterXNeighborValue = getFromAsymmetricPosition(v, 3, 3, 2, 2);
		if (topplePositionType24(v, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType23(v, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | 3 | 3 | 2 | 0 | 108
		currentValue = getFromAsymmetricPosition(v, 3, 3, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 3, 2, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 3, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 3, 2, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, 3, 3, 3, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(v, 3, 3, 1, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, 3, 3, 2, 1);
		if (topplePositionType25(v, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 3 | 3 | 2 | 1 | 109
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 3, 2, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 3, 2, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 3, 2, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, 3, 3, 3, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, 3, 3, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, 3, 3, 2, 2);
		if (topplePositionType39(v, 3, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | 3 | 3 | 2 | 2 | 110
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, 3, 3, 2, 2);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, 3, 3, 2, 2);
		greaterWNeighborValue = getFromAsymmetricPosition(v, 4, 3, 2, 2);
		smallerXNeighborValue = getFromAsymmetricPosition(v, 3, 2, 2, 2);
		greaterYNeighborValue = getFromAsymmetricPosition(v, 3, 3, 3, 2);
		if (topplePositionType26(v, 3, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType24(v, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType12(int v, int w, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		//int w = v - 1;
		int  wMinusOne = w - 1, wPlusOne = w + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		if (toppleRangeType6(v, w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 2 | 0 | 0 | 113
		long currentValue = getFromAsymmetricPosition(v, w, 2, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 0);
		if (topplePositionType22(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 1 | 0 | 114
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 1);
		if (topplePositionType36(v, w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 1 | 1 | 115
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 1);
		if (topplePositionType37(v, w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 0 | 116
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 2, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 0);
		if (topplePositionType23(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 1 | 117
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 2, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 2, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 2);
		if (topplePositionType38(v, w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 2 | 118
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 2);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 2, 2);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 2, 2);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 2);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 2);
		if (topplePositionType24(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType13(int v, int w, int x, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int  wMinusOne = w - 1, wPlusOne = w + 1, xMinusOne = x - 1, xPlusOne = x + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | x | 0 | 0 | 65
		long currentValue = getFromAsymmetricPosition(v, w, x, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
		if (topplePositionType22(v, w, x, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 119
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
		if (topplePositionType36(v, w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 120
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
		if (topplePositionType37(v, w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType14(int v, int w, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		//int w = v - 1;
		int wMinusOne = w - 1, wMinusTwo = w - 2, wPlusOne = w + 1, crdMinusOne = crd - 1, crdPlusOne = crd + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | crd | crd | 0 | 68
		long currentValue = getFromAsymmetricPosition(v, w, crd, crd, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 1);
		if (topplePositionType23(v, w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 124
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 2);
		if (topplePositionType38(v, w, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != wMinusTwo; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 179
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);
			if (topplePositionType38(v, w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 125
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, z);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);
		if (topplePositionType38(v, w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 70
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, crd);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, crd);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, crd);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, crd);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, crd);
		if (topplePositionType24(v, w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType7(v, w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int xMinusOne = crd, x = w;
		// v | w | x | 2 | 0 | 126
		currentValue = getFromAsymmetricPosition(v, w, x, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
		if (topplePositionType25(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 2 | 1 | 127
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, 2, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 2);
		if (topplePositionType39(v, w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 2 | 2 | 128
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 2);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 2);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 2);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 2);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 2);
		if (topplePositionType26(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
		for (; y != wMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// v | w | x | y | 0 | 126
			currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
			if (topplePositionType25(v, w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | x | y | 1 | 180
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
			if (topplePositionType39(v, w, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = 2;
			zPlusOne = z + 1;
			for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 218
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType39(v, w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 181
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
			if (topplePositionType39(v, w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = y;
			// v | w | x | y | z | 128
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			if (topplePositionType26(v, w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | x | y | 0 | 73
		currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
		if (topplePositionType25(v, w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | y | 1 | 129
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
		if (topplePositionType39(v, w, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		z = 2;
		zPlusOne = z + 1;
		for (; z != wMinusTwo; z = zPlusOne, zPlusOne++) {
			// v | w | x | y | z | 182
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
			if (topplePositionType39(v, w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | x | y | z | 130
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
		if (topplePositionType39(v, w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		z = zPlusOne;
		// v | w | x | y | z | 75
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
		if (topplePositionType26(v, w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 3, greaterWNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType8(v, w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int wMinusThree = wMinusTwo;
		wMinusTwo = wMinusOne;
		wMinusOne = w;
		w = wPlusOne;
		wPlusOne++;
		if (toppleRangeType25(v, w, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 3 | 2 | 0 | 134
		currentValue = getFromAsymmetricPosition(v, w, 3, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 1);
		if (topplePositionType28(w, 3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 3 | 2 | 1 | 135
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 2);
		if (topplePositionType43(w, 3, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 3 | 2 | 2 | 136
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 3, 2, 2);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 3, 2, 2);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 4, 2, 2);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 2);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 3, 3, 2);
		if (topplePositionType29(w, 3, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType26(v, w, 3, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		x = 4;
		xMinusOne = x - 1;
		int xPlusOne = x + 1;
		for (int xMinusTwo = x - 2; x != wMinusOne; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
			if (toppleRangeType25(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			// v | w | x | 2 | 0 | 183
			currentValue = getFromAsymmetricPosition(v, w, x, 2, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
			if (topplePositionType54(w, x, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | x | 2 | 1 | 184
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 2);
			if (topplePositionType43(w, x, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | x | 2 | 2 | 185
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 2);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 2);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 2);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 2);
			if (topplePositionType55(w, x, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			y = 3;
			yMinusOne = y - 1;
			yPlusOne = y + 1;
			for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				// v | w | x | y | 0 | 183
				currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
				if (topplePositionType54(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				// v | w | x | y | 1 | 219
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
				if (topplePositionType43(w, x, y, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				z = 2;
				zPlusOne = z + 1;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					// v | w | x | y | z | 237
					//reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
					smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
					greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
					smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
					greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
					smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
					greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
					if (topplePositionType62(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue,
							smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
						changed = true;
					}
				}
				// v | w | x | y | z | 220
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
				z = y;
				// v | w | x | y | z | 185
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				if (topplePositionType55(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			// v | w | x | y | 0 | 134
			currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
			if (topplePositionType28(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | x | y | 1 | 186
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
			if (topplePositionType43(w, x, y, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = 2;
			zPlusOne = z + 1;
			for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 221
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 187
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
			if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = xMinusOne;
			// v | w | x | y | z | 136
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			if (topplePositionType29(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			if (toppleRangeType26(v, w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
		}
		if (toppleRangeType9(w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | x | 2 | 0 | 139
		currentValue = getFromAsymmetricPosition(v, w, x, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
		if (topplePositionType28(w, x, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 2 | 1 | 140
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 2);
		if (topplePositionType43(w, x, 2, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 2 | 2 | 141
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 2);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 2, 2);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 2, 2);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 2, 2);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 2);
		if (topplePositionType29(w, x, 2, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		y = 3;
		yMinusOne = y - 1;
		yPlusOne = y + 1;
		for (; y != wMinusTwo; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// v | w | x | y | 0 | 139
			currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
			if (topplePositionType28(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | x | y | 1 | 189
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
			if (topplePositionType43(w, x, y, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = 2;
			zPlusOne = z + 1;
			for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 222
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
				greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 190
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
			if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = y;
			// v | w | x | y | z | 141
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			if (topplePositionType29(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | x | y | 0 | 86
		currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
		if (topplePositionType28(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | y | 1 | 142
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
		if (topplePositionType43(w, x, y, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		z = 2;
		zPlusOne = z + 1;
		for (; z != wMinusThree; z = zPlusOne, zPlusOne++) {
			// v | w | x | y | z | 191
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
			if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | x | y | z | 143
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
		if (topplePositionType43(w, x, y, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 2, greaterYNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		z = wMinusTwo;
		// v | w | x | y | z | 88
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, y, z);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
		if (topplePositionType29(w, x, y, currentValue, greaterVNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 3, smallerXNeighborValue, 3, greaterYNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType10(w, x, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType15(int v, int w, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int  wMinusOne = w - 1, wPlusOne = w + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		if (toppleRangeType22(v, w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		// v | w | 2 | 0 | 0 | 148
		long currentValue = getFromAsymmetricPosition(v, w, 2, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 0);
		if (topplePositionType48(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerXNeighborValue, greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 1 | 0 | 149
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 1);
		if (topplePositionType36(v, w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 1 | 1 | 150
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 1);
		if (topplePositionType37(v, w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 0 | 151
		//reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;		
		currentValue = getFromAsymmetricPosition(v, w, 2, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 2, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 0);
		if (topplePositionType49(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 1 | 152
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 2, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 2, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 2);
		if (topplePositionType38(v, w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 2 | 2 | 2 | 153
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 2, 2, 2);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 2, 2, 2);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 2, 2, 2);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 2, 2, 2);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 3, 2, 2);
		smallerZNeighborValue = getFromAsymmetricPosition(v, w, 2, 2, 1);
		if (topplePositionType50(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType16(int v, int w, int x, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int  wMinusOne = w - 1, wPlusOne = w + 1, xMinusOne = x - 1, xPlusOne = x + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | x | 0 | 0 | 100
		long currentValue = getFromAsymmetricPosition(v, w, x, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
		if (topplePositionType22(v, w, x, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 154
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
		if (topplePositionType36(v, w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 155
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
		if (topplePositionType37(v, w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType17(int v, int w, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		//int crd = w - 1;
		int wMinusOne = w - 1, wMinusTwo = w - 2, wPlusOne = w + 1, crdMinusOne = crd - 1, crdPlusOne = crd + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | crd | crd | 0 | 103
		long currentValue = getFromAsymmetricPosition(v, w, crd, crd, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 1);
		if (topplePositionType23(v, w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 159
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 2);
		if (topplePositionType38(v, w, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != wMinusTwo; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 205
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);
			if (topplePositionType38(v, w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 160
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, z);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);
		if (topplePositionType38(v, w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 3, greaterXNeighborValue, 2,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 105
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, crd);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, crd);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, crd);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, crd);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, crd);
		if (topplePositionType24(v, w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 4, greaterXNeighborValue, 2,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType23(v, w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int x = w, xMinusOne = wMinusOne;
		// v | w | x | 2 | 0 | 161
		currentValue = getFromAsymmetricPosition(v, w, x, 2, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, 2, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
		if (topplePositionType51(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 2 | 1 | 162
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, 2, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 2);
		if (topplePositionType39(v, w, 2, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 2 | 2 | 163
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 2, 2);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 2, 2);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 2, 2);
		smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, 2, 2);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 3, 2);
		if (topplePositionType52(v, w, 2, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int y = 3, yMinusOne = y - 1, yPlusOne = y + 1;
		for (; y != wMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
			// v | w | x | y | 0 | 161
			currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, 0);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
			if (topplePositionType51(v, w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
					greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			// v | w | x | y | 1 | 206
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
			smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, 1);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
			if (topplePositionType39(v, w, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			for (z = 2, zPlusOne = z + 1; z != yMinusOne; z = zPlusOne, zPlusOne++) {
				// v | w | x | y | z | 232
				//reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
				smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
				greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
				smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
				greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
				smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
				greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
				if (topplePositionType61(v, w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue,
						smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
					changed = true;
				}
			}
			// v | w | x | y | z | 207
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
			if (topplePositionType39(v, w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
			z = y;
			// v | w | x | y | z | 163
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			if (topplePositionType52(v, w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | x | y | 0 | 108
		currentValue = getFromAsymmetricPosition(v, w, x, y, 0);
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 0);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 0);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 1);
		if (topplePositionType25(v, w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | y | 1 | 164
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, 2);
		if (topplePositionType39(v, w, y, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		for (z = 2, zPlusOne = z + 1; z != wMinusTwo; z = zPlusOne, zPlusOne++) {
			// v | w | x | y | z | 208
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
			smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
			greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
			if (topplePositionType39(v, w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | x | y | z | 165
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(v, x, xMinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, x, yMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, y, zPlusOne);
		if (topplePositionType39(v, w, y, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 3,
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		z = wMinusOne;
		// v | w | x | y | z | 110
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, y, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, y, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, y, z);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, y, z);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, yPlusOne, z);
		if (topplePositionType26(v, w, y, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		if (toppleRangeType24(v, w, newGrid, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType18(int v, int w, int x, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int  wMinusOne = w - 1, wPlusOne = w + 1, xMinusOne = x - 1, xPlusOne = x + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | x | 0 | 0 | 148
		long currentValue = getFromAsymmetricPosition(v, w, x, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
		if (topplePositionType48(v, w, x, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerXNeighborValue, greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 193
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
		if (topplePositionType36(v, w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 194
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
		if (topplePositionType37(v, w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType19(int v, int w, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1, wMinusOne = w - 1, wPlusOne = w + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | crd | crd | 0 | 151
		long currentValue = getFromAsymmetricPosition(v, w, crd, crd, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 1);
		if (topplePositionType49(v, w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 198
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 2);
		if (topplePositionType38(v, w, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 228
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);
			if (topplePositionType60(v, w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, 
					greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 199
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, z);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);
		if (topplePositionType38(v, w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 153
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, crd);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, crd);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, crd);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, crd);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, crd);
		smallerZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, crdMinusOne);
		if (topplePositionType50(v, w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType20(int v, int w, int x, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int  wMinusOne = w - 1, wPlusOne = w + 1, xMinusOne = x - 1, xPlusOne = x + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | x | 0 | 0 | 113
		long currentValue = getFromAsymmetricPosition(v, w, x, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
		if (topplePositionType22(v, w, x, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 167
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
		if (topplePositionType36(v, w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 168
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, x, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, x, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
		if (topplePositionType37(v, w, x, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType21(int v, int w, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int  wMinusOne = w - 1, wPlusOne = w + 1, crdMinusOne = crd - 1, crdPlusOne = crd + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | crd | crd | 0 | 116
		long currentValue = getFromAsymmetricPosition(v, w, crd, crd, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 1);
		if (topplePositionType23(v, w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 172
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 2);
		if (topplePositionType38(v, w, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 214
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);
			if (topplePositionType38(v, w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 173
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, z);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);
		if (topplePositionType38(v, w, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 2, greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 118
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, crd);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, crd, crd, crd);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, crd, crd, crd);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, crd);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, crd);
		if (topplePositionType24(v, w, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, 2, greaterWNeighborValue, 2, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType22(int v, int w, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int wMinusOne = w - 1, wPlusOne = w + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | w | 0 | 0 | 0 | 52
		long currentValue = getFromAsymmetricPosition(v, w, 0, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 0, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 0, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 0, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 0, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, 1, 0, 0);
		if (topplePositionType32(v, w, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerWNeighborValue,
				greaterXNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 1 | 0 | 0 | 97
		//reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 1, 0, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 1, 0, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 1, 0, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 1, 0, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 2, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 0);
		if (topplePositionType22(v, w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 1 | 1 | 0 | 98
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 1, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 1, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 1, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 1, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, 1, 1, 1);
		if (topplePositionType23(v, w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | 1 | 1 | 1 | 99
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, 1, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, w, 1, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, wPlusOne, 1, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, 1, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, 2, 1, 1);
		if (topplePositionType24(v, w, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerWNeighborValue, 1, greaterXNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType23(int v, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | crd | crd | 0 | 0 | 56
		long currentValue = getFromAsymmetricPosition(v, crd, crd, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, 0, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, 0, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, crd, crdMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, crd, crd, 1, 0);
		if (topplePositionType33(v, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerXNeighborValue,
				greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | crd | crd | 1 | 0 | 106
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, 1, 0);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, 1, 0);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, crd, crdMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, crd, crd, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, 1, 1);
		if (topplePositionType25(v, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | crd | crd | 1 | 1 | 107
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, 1, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, 1, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, crd, crdMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, crd, crd, 2, 1);
		if (topplePositionType26(v, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType24(int v, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1, vPlusOne = v + 1, vMinusOne = v - 1;
		boolean changed = false;
		// v | crd | crd | crd | 0 | 59
		long currentValue = getFromAsymmetricPosition(v, crd, crd, crd, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, 0);
		long smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, 0);
		long greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(v, crd, crd, crdMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, crd, 1);
		if (topplePositionType34(v, crd, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerYNeighborValue,
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | crd | crd | crd | 1 | 111
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, 1);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, 1);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, crd, crd, crdMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, crd, 2);
		if (topplePositionType27(v, crd, 1, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | crd | crd | crd | z | 166
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, z);
			smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, z);
			greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, crd, crd, crdMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, crd, zPlusOne);
			if (topplePositionType53(v, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | crd | crd | crd | z | 112
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, z);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, z);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, crd, crd, crdMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, crd, crd, crd, zPlusOne);
		if (topplePositionType27(v, crd, z, currentValue, greaterVNeighborValue, smallerVNeighborValue, 1, greaterWNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 4,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | crd | crd | crd | crd | 61
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, crd, crd, crd, crd);
		smallerVNeighborValue = getFromAsymmetricPosition(vMinusOne, crd, crd, crd, crd);
		greaterWNeighborValue = getFromAsymmetricPosition(v, crdPlusOne, crd, crd, crd);
		if (topplePositionType35(v, crd,currentValue, greaterVNeighborValue, smallerVNeighborValue, greaterWNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType25(int v, int w, int x, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int wMinusOne = w - 1, xPlusOne = x + 1, xMinusOne = x - 1, vPlusOne = v + 1;
		boolean changed = false;
		// v | w | x | 0 | 0 | 78
		long currentValue = getFromAsymmetricPosition(v, w, x, 0, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 0, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 0, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 0, 0);
		long smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 0, 0);
		long greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 0);
		if (topplePositionType40(w, x, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue,
				greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 0 | 132
		//reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 0);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 0);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 0);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 0);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, x, 1, 1);
		if (topplePositionType28(w, x, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | x | 1 | 1 | 133
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, x, 1, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, x, 1, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, xPlusOne, 1, 1);
		smallerXNeighborValue = getFromAsymmetricPosition(v, w, xMinusOne, 1, 1);
		greaterYNeighborValue = getFromAsymmetricPosition(v, w, x, 2, 1);
		if (topplePositionType29(w, x, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}

	private boolean toppleRangeType26(int v, int w, int crd, RandomAccessFile newGrid, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) throws IOException {
		int crdMinusOne = crd - 1, crdPlusOne = crd + 1, wMinusOne = w - 1, vPlusOne = v + 1;
		boolean changed = false;
		// v | w | crd | crd | 0 | 81
		long currentValue = getFromAsymmetricPosition(v, w, crd, crd, 0);
		long greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 0);
		long smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 0);
		long greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 0);
		long smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 0);
		long greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 1);
		if (topplePositionType41(w, crd, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue,
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | 1 | 137
		//reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, 1);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, 1);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, 1);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, 1);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, 2);		
		if (topplePositionType30(w, crd, 1, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1,
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		int z = 2, zPlusOne = z + 1;
		for (; z != crdMinusOne; z = zPlusOne, zPlusOne++) {
			// v | w | crd | crd | z | 188
			//reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
			smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
			greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
			smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
			greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);		
			if (topplePositionType56(w, crd, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue,
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
				changed = true;
			}
		}
		// v | w | crd | crd | z | 138
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, z);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, z);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, z);
		smallerYNeighborValue = getFromAsymmetricPosition(v, w, crd, crdMinusOne, z);
		greaterZNeighborValue = getFromAsymmetricPosition(v, w, crd, crd, zPlusOne);		
		if (topplePositionType30(w, crd, z, currentValue, greaterVNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3,
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		// v | w | crd | crd | crd | 83
		//reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterVNeighborValue = getFromAsymmetricPosition(vPlusOne, w, crd, crd, crd);
		smallerWNeighborValue = getFromAsymmetricPosition(v, wMinusOne, crd, crd, crd);
		greaterXNeighborValue = getFromAsymmetricPosition(v, w, crdPlusOne, crd, crd);
		if (topplePositionType42(w, crd, currentValue, greaterVNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newGrid)) {
			changed = true;
		}
		return changed;
	}		

	private static boolean topplePositionType1(long currentValue, long gVValue, RandomAccessFile newGrid) throws IOException {
		boolean toppled = false;
		if (gVValue < currentValue) {
			long toShare = currentValue - gVValue;
			long share = toShare/11;
			if (share != 0) {
				toppled = true;
				addToPosition(newGrid, 0, 0, 0, 0, 0, currentValue - toShare + share + toShare%11);
				addToPosition(newGrid, 1, 0, 0, 0, 0, share);
			} else {
				addToPosition(newGrid, 0, 0, 0, 0, 0, currentValue);
			}			
		} else {
			addToPosition(newGrid, 0, 0, 0, 0, 0, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionType2(long currentValue, long gVValue, long sVValue, long gWValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
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
		return topplePosition(newGrid, currentValue, 1, 0, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType3(long currentValue, long gVValue, long sWValue, long gXValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
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
		return topplePosition(newGrid, currentValue, 1, 1, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType4(long currentValue, long gVValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
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
		return topplePosition(newGrid, currentValue, 1, 1, 1, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType5(long currentValue, long gVValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
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
		return topplePosition(newGrid, currentValue, 1, 1, 1, 1, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType6(long currentValue, long gVValue, long sZValue, RandomAccessFile newGrid) throws IOException {
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gVValue < currentValue) {
				if (sZValue == gVValue) {
					//gv = sz < current
					long toShare = currentValue - gVValue; 
					long share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 1, 0, share + share);//one more for the symmetric position at the other side
					addToPosition(newGrid, 1, 1, 1, 1, 1, currentValue - toShare + share + toShare%11);
					addToPosition(newGrid, 2, 1, 1, 1, 1, share);
				} else if (sZValue < gVValue) {
					//sz < gv < current
					long toShare = currentValue - gVValue; 
					long share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 1, 0, share + share);
					addToPosition(newGrid, 2, 1, 1, 1, 1, share);
					long currentRemainingValue = currentValue - 10*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/6;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 1, 0, share + share);
					addToPosition(newGrid, 1, 1, 1, 1, 1, currentRemainingValue - toShare + share + toShare%6);
				} else {
					//gv < sz < current
					long toShare = currentValue - sZValue; 
					long share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 1, 0, share + share);
					addToPosition(newGrid, 1 + 1, 1, 1, 1, 1, share);
					long currentRemainingValue = currentValue - 10*share;
					toShare = currentRemainingValue - gVValue; 
					share = toShare/6;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, 1, 1, 1, 1, 1, currentRemainingValue - toShare + share + toShare%6);
					addToPosition(newGrid, 2, 1, 1, 1, 1, share);
				}
			} else {
				//sz < current <= gv
				long toShare = currentValue - sZValue; 
				long share = toShare/6;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, 1, 1, 1, 1, 0, share + share);
				addToPosition(newGrid, 1, 1, 1, 1, 1, currentValue - toShare + share + toShare%6);
			}
		} else if (gVValue < currentValue) {
			//gv < current <= sz
			long toShare = currentValue - gVValue; 
			long share = toShare/6;
			if (share != 0) {
				toppled = true;
			}
			addToPosition(newGrid, 1, 1, 1, 1, 1, currentValue - toShare + share + toShare%6);
			addToPosition(newGrid, 2, 1, 1, 1, 1, share);
		} else {
			//gv >= current <= sz
			addToPosition(newGrid, 1, 1, 1, 1, 1, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionType7(int v, long currentValue, long gVValue, long sVValue, long gWValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 8;
			relevantNeighborCount += 8;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, 0, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType8(int v, int w, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType9(int v, int coord, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType10(int v, int coord, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType11(int v, int coord, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType12(int coord, long currentValue, long gVValue, long sWValue, long gXValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType13(int coord, int x, long currentValue, long gVValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = x;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType14(int coord1, int coord2, long currentValue, long gVValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2 + 1;
			nc[3] = coord2;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2 - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord2, coord2, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType15(int coord1, int coord2, long currentValue, long gVValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2 + 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord2, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType16(int coord, long currentValue, long gVValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType17(int coord, int y, long currentValue, long gVValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType18(int coord1, int coord2, long currentValue, long gVValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
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
			nc[0] = coord1;
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
			nc[0] = coord1;
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
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType19(int coord, long currentValue, long gVValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType20(int coord, int z, long currentValue, long gVValue, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType21(int coord, long currentValue, long gVValue, long sZValue, RandomAccessFile newGrid) throws IOException {
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gVValue < currentValue) {
				if (sZValue == gVValue) {
					//gv = sz < current
					long toShare = currentValue - gVValue; 
					long share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coord, coord - 1, share);
					addToPosition(newGrid, coord, coord, coord, coord, coord, currentValue - toShare + share + toShare%11);
					addToPosition(newGrid, coord + 1, coord, coord, coord, coord, share);
				} else if (sZValue < gVValue) {
					//sz < gv < current
					int coordMinusOne = coord - 1;
					long toShare = currentValue - gVValue; 
					long share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coord, coordMinusOne, share);
					addToPosition(newGrid, coord + 1, coord, coord, coord, coord, share);
					long currentRemainingValue = currentValue - 10*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/6;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coord, coordMinusOne, share);
					addToPosition(newGrid, coord, coord, coord, coord, coord, currentRemainingValue - toShare + share + toShare%6);
				} else {
					//gv < sz < current
					int coordPlusOne = coord + 1;
					long toShare = currentValue - sZValue; 
					long share = toShare/11;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coord, coord - 1, share);
					addToPosition(newGrid, coordPlusOne, coord, coord, coord, coord, share);
					long currentRemainingValue = currentValue - 10*share;
					toShare = currentRemainingValue - gVValue; 
					share = toShare/6;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, coord, coord, coord, coord, coord, currentRemainingValue - toShare + share + toShare%6);
					addToPosition(newGrid, coordPlusOne, coord, coord, coord, coord, share);
				}
			} else {
				//sz < current <= gv
				long toShare = currentValue - sZValue; 
				long share = toShare/6;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, coord, coord, coord, coord, coord - 1, share);
				addToPosition(newGrid, coord, coord, coord, coord, coord, currentValue - toShare + share + toShare%6);
			}
		} else if (gVValue < currentValue) {
			//gv < current <= sz
			long toShare = currentValue - gVValue; 
			long share = toShare/6;
			if (share != 0) {
				toppled = true;
			}
			addToPosition(newGrid, coord, coord, coord, coord, coord, currentValue - toShare + share + toShare%6);
			addToPosition(newGrid, coord + 1, coord, coord, coord, coord, share);
		} else {
			//gv >= current <= sz
			addToPosition(newGrid, coord, coord, coord, coord, coord, currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionType22(int v, int w, int x, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType23(int v, int w, int coord, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType24(int v, int w, int coord, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, coord, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType25(int v, int coord, int y, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType26(int v, int coord1, int coord2, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType27(int v, int coord, int z, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType28(int coord, int x, int y, long currentValue, long gVValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType29(int coord1, int x, int coord2, long currentValue, long gVValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = x;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = x;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = x + 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = x - 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = x;
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
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = x;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, x, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType30(int coord1, int coord2, int z, long currentValue, long gVValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2 + 1;
			nc[3] = coord2;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2 - 1;
			nc[4] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord2, coord2, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType31(int coord, int y, int z, long currentValue, long gVValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType32(int v, int w, long currentValue, long gVValue, long sVValue, long gWValue, long sWValue, long gXValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = 1;
			nc[3] = 0;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType33(int v, int coord, long currentValue, long gVValue, long sVValue, long gWValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType34(int v, int coord, long currentValue, long gVValue, long sVValue, long gWValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType35(int v, int coord, long currentValue, long gVValue, long sVValue, long gWValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType36(int v, int w, int x, int y, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType37(int v, int w, int x, int coord, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType38(int v, int w, int coord, int z, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType39(int v, int coord, int y, int z, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType40(int coord, int x, long currentValue, long gVValue, long sWValue, long gXValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = x;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType41(int coord1, int coord2, long currentValue, long gVValue, long sWValue, long gXValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2 + 1;
			nc[3] = coord2;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2 - 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord2, coord2, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType42(int coord1, int coord2, long currentValue, long gVValue, long sWValue, long gXValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2 + 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord2, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType43(int coord, int x, int y, int z, long currentValue, long gVValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType44(int coord, int y, long currentValue, long gVValue, long sXValue, long gYValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType45(int coord1, int coord2, long currentValue, long gVValue, long sXValue, long gYValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
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
			nc[0] = coord1;
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
			nc[0] = coord1;
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
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType46(int coord, int z, long currentValue, long gVValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType47(int v, int w, int x, int y, int z, long currentValue, long gVValue, long sVValue, int sVShareMultiplier, long gWValue, int gWShareMultiplier, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, RandomAccessFile newGrid) throws IOException {
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionType48(int v, int w, int x, long currentValue, long gVValue, long sVValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = 1;
			nc[4] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType49(int v, int w, int coord, long currentValue, long gVValue, long sVValue, long gWValue, long sWValue, long gXValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType50(int v, int w, int coord, long currentValue, long gVValue, long sVValue, long gWValue, long sWValue, long gXValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, coord, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType51(int v, int coord, int y, long currentValue, long gVValue, long sVValue, long gWValue, long sXValue, long gYValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType52(int v, int coord1, int coord2, long currentValue, long gVValue, long sVValue, long gWValue, long sXValue, long gYValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord1;
			nc[2] = coord1;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord1, coord1, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType53(int v, int coord, int z, long currentValue, long gVValue, long sVValue, long gWValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType54(int coord, int x, int y, long currentValue, long gVValue, long sWValue, long gXValue, long sXValue, long gYValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType55(int coord1, int x, int coord2, long currentValue, long gVValue, long sWValue, long gXValue, long sXValue, long gYValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = x;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = x;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = x + 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = x - 1;
			nc[3] = coord2;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = x;
			nc[3] = coord2 + 1;
			nc[4] = coord2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = x;
			nc[3] = coord2;
			nc[4] = coord2 - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, x, coord2, coord2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType56(int coord1, int coord2, int z, long currentValue, long gVValue, long sWValue, long gXValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1 + 1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1 - 1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2 + 1;
			nc[3] = coord2;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2 - 1;
			nc[4] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord1;
			nc[1] = coord1;
			nc[2] = coord2;
			nc[3] = coord2;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord1, coord1, coord2, coord2, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType57(int coord, int y, int z, long currentValue, long gVValue, long sXValue, long gYValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, coord, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType58(int v, int w, int x, int y, long currentValue, long gVValue, long sVValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType59(int v, int w, int x, int coord, long currentValue, long gVValue, long sVValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = coord;
			nc[4] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType60(int v, int w, int coord, int z, long currentValue, long gVValue, long sVValue, long gWValue, long sWValue, long gXValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = w;
			nc[2] = coord;
			nc[3] = coord;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType61(int v, int coord, int y, int z, long currentValue, long gVValue, long sVValue, long gWValue, long sXValue, long gYValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = v + 1;
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
			nc[0] = v - 1;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
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
			nc[0] = v;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, coord, coord, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType62(int coord, int x, int y, int z, long currentValue, long gVValue, long sWValue, long gXValue, long sXValue, long gYValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, RandomAccessFile newGrid) throws IOException {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = coord + 1;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord - 1;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
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
			nc[0] = coord;
			nc[1] = coord;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, coord, coord, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType63(int v, int w, int x, int y, int z, long currentValue, long gVValue, long sVValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, RandomAccessFile newGrid) throws IOException {
		int relevantNeighborCount = 0;
		if (gVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v + 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (sVValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sVValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v - 1;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v;
			nc[1] = w + 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v;
			nc[1] = w - 1;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v;
			nc[1] = w;
			nc[2] = x + 1;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v;
			nc[1] = w;
			nc[2] = x - 1;
			nc[3] = y;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y + 1;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y - 1;
			nc[4] = z;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z + 1;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = v;
			nc[1] = w;
			nc[2] = x;
			nc[3] = y;
			nc[4] = z - 1;
			relevantNeighborCount++;
		}
		return topplePosition(newGrid, currentValue, v, w, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantNeighborCount);
	}

	private static boolean topplePosition(RandomAccessFile newGrid, long value, int v, int w, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) throws IOException {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, v, w, x, y, z, neighborValues, sortedNeighborsIndexes, 
					neighborCoords, 3);
			break;
		case 2:
			long n0Val = neighborValues[0], n1Val = neighborValues[1];
			int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
			if (n0Val == n1Val) {
				//n0Val = n1Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				addToPosition(newGrid, v, w, x, y, z, value - toShare + share + toShare%3);
			} else if (n0Val < n1Val) {
				//n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				addToPosition(newGrid, v, w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			} else {
				//n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				addToPosition(newGrid, v, w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			}				
			break;
		case 1:
			long toShare = value - neighborValues[0];
			long share = toShare/2;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%2 + share;
				int[] nc = neighborCoords[0];
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share);
			}
			//no break
		case 0:
			addToPosition(newGrid, v, w, x, y, z, value);
			break;
		default: //10, 9, 8, 7, 6, 5, 4
			Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, v, w, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
					neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int v, int w, int x, int y, int z, 
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		addToPosition(newGrid, v, w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(RandomAccessFile newGrid, long value, int v, int w, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) throws IOException {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
		case 3:
			Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, v, w, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
					asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
			break;
		case 2:
			long n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
			int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
			int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
			int shareCount = neighborCount + 1;
			if (n0Val == n1Val) {
				//n0Val = n1Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				addToPosition(newGrid, v, w, x, y, z, value - toShare + share + toShare%shareCount);
			} else if (n0Val < n1Val) {
				//n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				addToPosition(newGrid, v, w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
			} else {
				//n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				addToPosition(newGrid, v, w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share * asymmetricNeighborShareMultipliers[0]);
			}
			//no break
		case 0:
			addToPosition(newGrid, v, w, x, y, z, value);
			break;
		default: //10, 9, 8, 7, 6, 5, 4
			Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, v, w, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
					asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int v, int w, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		addToPosition(newGrid, v, w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(RandomAccessFile newGrid, long value, int v, int w, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) throws IOException {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, v, w, x, y, z, neighborValues, sortedNeighborsIndexes, 
					neighborCoords, neighborShareMultipliers, 3);
			break;
		case 2:
			long n0Val = neighborValues[0], n1Val = neighborValues[1];
			int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
			int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
			if (n0Val == n1Val) {
				//n0Val = n1Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				addToPosition(newGrid, v, w, x, y, z, value - toShare + share + toShare%3);
			} else if (n0Val < n1Val) {
				//n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				addToPosition(newGrid, v, w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			} else {
				//n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share*n0Mult);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share*n1Mult);
				addToPosition(newGrid, v, w, x, y, z, currentRemainingValue - toShare + share + toShare%2);
			}				
			break;
		case 1:
			long toShare = value - neighborValues[0];
			long share = toShare/2;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%2 + share;
				int[] nc = neighborCoords[0];
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share * neighborShareMultipliers[0]);
			}
			//no break
		case 0:
			addToPosition(newGrid, v, w, x, y, z, value);
			break;
		default: //10, 9, 8, 7, 6, 5, 4
			Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, v, w, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
					neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int v, int w, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		addToPosition(newGrid, v, w, x, y, z, value);
		return toppled;
	}

	private static boolean topplePosition(RandomAccessFile newGrid, long value, int v, int w, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) throws IOException {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
		case 3:
			Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, v, w, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
					asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
			break;
		case 2:
			long n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
			int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
			int shareCount = neighborCount + 1;
			if (n0Val == n1Val) {
				//n0Val = n1Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				addToPosition(newGrid, v, w, x, y, z, value - toShare + share + toShare%shareCount);
			} else if (n0Val < n1Val) {
				//n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				addToPosition(newGrid, v, w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
			} else {
				//n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n0Coords[0], n0Coords[1], n0Coords[2], n0Coords[3], n0Coords[4], share);
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				addToPosition(newGrid, n1Coords[0], n1Coords[1], n1Coords[2], n1Coords[3], n1Coords[4], share);
				addToPosition(newGrid, v, w, x, y, z, currentRemainingValue - toShare + share + toShare%shareCount);
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share);
			}
			//no break
		case 0:
			addToPosition(newGrid, v, w, x, y, z, value);
			break;
		default: //10, 9, 8, 7, 6, 5, 4
			Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newGrid, value, v, w, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
					asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int v, int w, int x, int y, int z, 
			long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
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
				addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share);
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
						addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		addToPosition(newGrid, v, w, x, y, z, value);
		return toppled;
	}

	@Override
	public long getFromPosition(int v, int w, int x, int y, int z) throws IOException {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (w < 0) w = -w;
		if (v < 0) v = -v;
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
			if (w > v) {
				sorted = false;
				int swp = w;
				w = v;
				v = swp;
			}
		} while (!sorted);
		return getFromAsymmetricPosition(v, w, x, y, z);
	}

	@Override
	public long getFromAsymmetricPosition(int v, int w, int x, int y, int z) throws IOException {	
		long pos = (Utils.getAnisotropic5DGridPositionCount(v)
				+ Utils.getAnisotropic4DGridPositionCount(w)
				+ Utils.getAnisotropic3DGridPositionCount(x)
				+ ((y*y-y)/2+y)
				+ z)
				*POSITION_BYTES;
		grid.seek(pos);
		return grid.readLong();
	}
	
	private static void addToPosition(RandomAccessFile grid, int v, int w, int x, int y, int z, long value) throws IOException {
		long pos = (Utils.getAnisotropic5DGridPositionCount(v)
				+ Utils.getAnisotropic4DGridPositionCount(w)
				+ Utils.getAnisotropic3DGridPositionCount(x)
				+ ((y*y-y)/2+y)
				+ z)
				*POSITION_BYTES;
		grid.seek(pos);
		long previousValue = grid.readLong();
		grid.seek(pos);
		grid.writeLong(previousValue + value);
	}

	@Override
	public int getAsymmetricMaxV() {
		return maxV;
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
		return getName() + "/5D/" + initialValue;
	}
	
	@Override
	protected HashMap<String, Object> getPropertiesMap() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("initialValue", initialValue);
		properties.put("step", step);
		properties.put("maxV", maxV);
		return properties;
	}
	
	@Override
	protected void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (long) properties.get("initialValue");
		step = (long) properties.get("step");
		maxV = (int) properties.get("maxV");
	}

}