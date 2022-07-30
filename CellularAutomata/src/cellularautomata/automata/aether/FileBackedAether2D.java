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
import cellularautomata.model2d.IsotropicSquareModelA;
import cellularautomata.model2d.SymmetricLongModel2D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 2D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class FileBackedAether2D extends FileBackedModel implements SymmetricLongModel2D, IsotropicSquareModelA {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -6148914691236517205L;
	public static final int POSITION_BYTES = Long.BYTES;
	
	private long initialValue;
	private long step;
	private int maxX;

	public FileBackedAether2D(long initialValue, String folderPath) throws IOException {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		createGridFolder(folderPath);
		currentFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step));
		grid = new RandomAccessFile(currentFile, "rw");
		int gridLength = (6*6-6)/2+6;
		grid.setLength(gridLength*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
		grid.writeLong(initialValue);
		step = 0;
		maxX = 3;
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
	public FileBackedAether2D(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		super(backupPath, folderPath);
	}
	
	@Override
	public boolean nextStep() throws IOException {
		RandomAccessFile newGrid = null;
		try {
			boolean changed = false;
			long currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue;
			// x = 0, y = 0
			currentValue = getFromAsymmetricPosition(0, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(1, 0);
			File newFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step + 1));
			newGrid = new RandomAccessFile(newFile, "rw");
			long newGridXLength = (maxX + 4);
			long newGridLength = (newGridXLength*newGridXLength-newGridXLength)/2+newGridXLength;
			newGrid.setLength(newGridLength*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
			if (greaterXNeighborValue < currentValue) {
				long toShare = currentValue - greaterXNeighborValue;
				long share = toShare/5;
				if (share != 0) {
					changed = true;
					addToPosition(newGrid, 0, 0, currentValue - toShare + share + toShare%5);
					addToPosition(newGrid, 1, 0, share);
				} else {
					addToPosition(newGrid, 0, 0, currentValue);
				}			
			} else {
				addToPosition(newGrid, 0, 0, currentValue);
			}
			// x = 1, y = 0
			int relevantAsymmetricNeighborCount = 0;
			int relevantNeighborCount = 0;
			long[] relevantAsymmetricNeighborValues = new long[4];
			int[] sortedNeighborsIndexes = new int[4];
			int[][] relevantAsymmetricNeighborCoords = new int[4][2];
			int[] relevantAsymmetricNeighborShareMultipliers = new int[4];// to compensate for omitted symmetric positions
			int[] relevantAsymmetricNeighborSymmetryCounts = new int[4];// to compensate for omitted symmetric positions
			// reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(1, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(2, 0);
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;// x coordinate
				nc[1] = 0;// y coordinate
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 0;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
				relevantNeighborCount += 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newGrid, currentValue, 1, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
					relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				changed = true;
			}
			// x = 1, y = 1
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(2, 1);
			if (smallerYNeighborValue < currentValue) {
				if (greaterXNeighborValue < currentValue) {
					if (smallerYNeighborValue == greaterXNeighborValue) {
						// gx = sy < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 1, 0, share + share);// one more for the symmetric position at the other side
						addToPosition(newGrid, 1, 1, currentValue - toShare + share + toShare%5);
						addToPosition(newGrid, 2, 1, share);
					} else if (smallerYNeighborValue < greaterXNeighborValue) {
						// sy < gx < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 1, 0, share + share);
						addToPosition(newGrid, 2, 1, share);
						long currentRemainingValue = currentValue - 4*share;
						toShare = currentRemainingValue - smallerYNeighborValue; 
						share = toShare/3;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 1, 0, share + share);
						addToPosition(newGrid, 1, 1, currentRemainingValue - toShare + share + toShare%3);
					} else {
						// gx < sy < current
						long toShare = currentValue - smallerYNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 1, 0, share + share);
						addToPosition(newGrid, 2, 1, share);
						long currentRemainingValue = currentValue - 4*share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/3;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 1, 1, currentRemainingValue - toShare + share + toShare%3);
						addToPosition(newGrid, 2, 1, share);
					}
				} else {
					// sy < current <= gx
					long toShare = currentValue - smallerYNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
					}
					addToPosition(newGrid, 1, 0, share + share);
					addToPosition(newGrid, 1, 1, currentValue - toShare + share + toShare%3);
				}
			} else if (greaterXNeighborValue < currentValue) {
				// gx < current <= sy
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/3;
				if (share != 0) {
					changed = true;
				}
				addToPosition(newGrid, 1, 1, currentValue - toShare + share + toShare%3);
				addToPosition(newGrid, 2, 1, share);
			} else {
				// gx >= current <= sy
				addToPosition(newGrid, 1, 1, currentValue);
			}
			// x = 2, y = 0
			relevantAsymmetricNeighborCount = 0;
			relevantNeighborCount = 0;
			// reuse values obtained previously
			greaterYNeighborValue = greaterXNeighborValue;
			currentValue = getFromAsymmetricPosition(2, 0);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 0);
			smallerXNeighborValue = getFromAsymmetricPosition(1, 0);
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 3;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 1;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
				relevantNeighborCount += 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newGrid, currentValue, 2, 0, relevantAsymmetricNeighborValues, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
					relevantNeighborCount, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				changed = true;
			}
			// x = 2, y = 1
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(2, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(1, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(3, 1);
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 3;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 0;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 2;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newGrid, currentValue, 2, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				changed = true;
			}
			// x = 2, y = 2
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(3, 2);
			if (smallerYNeighborValue < currentValue) {
				if (greaterXNeighborValue < currentValue) {
					if (smallerYNeighborValue == greaterXNeighborValue) {
						// gx = sy < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 2, 1, share);
						addToPosition(newGrid, 2, 2, currentValue - toShare + share + toShare%5);
						addToPosition(newGrid, 3, 2, share);
					} else if (smallerYNeighborValue < greaterXNeighborValue) {
						// sy < gx < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 2, 1, share);
						addToPosition(newGrid, 3, 2, share);
						long currentRemainingValue = currentValue - 4*share;
						toShare = currentRemainingValue - smallerYNeighborValue; 
						share = toShare/3;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 2, 1, share);
						addToPosition(newGrid, 2, 2, currentRemainingValue - toShare + share + toShare%3);
					} else {
						// gx < sy < current
						long toShare = currentValue - smallerYNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 2, 1, share);
						addToPosition(newGrid, 3, 2, share);
						long currentRemainingValue = currentValue - 4*share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/3;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 2, 2, currentRemainingValue - toShare + share + toShare%3);
						addToPosition(newGrid, 3, 2, share);
					}
				} else {
					// sy < current <= gx
					long toShare = currentValue - smallerYNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
					}
					addToPosition(newGrid, 2, 1, share);
					addToPosition(newGrid, 2, 2, currentValue - toShare + share + toShare%3);
				}
			} else if (greaterXNeighborValue < currentValue) {
				// gx < current <= sy
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/3;
				if (share != 0) {
					changed = true;
				}
				addToPosition(newGrid, 2, 2, currentValue - toShare + share + toShare%3);
				addToPosition(newGrid, 3, 2, share);
			} else {
				// gx >= current <= sy
				addToPosition(newGrid, 2, 2, currentValue);
			}
			// 3 <= x < edge - 2
			int edge = maxX + 2;
			int edgeMinusTwo = edge - 2;
			if (toppleRangeBeyondX2(newGrid, 3, edgeMinusTwo, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, sortedNeighborsIndexes)) { // is it faster to reuse these arrays?
				changed = true;
			}
			//edge - 2 <= x < edge
			if (toppleRangeBeyondX2(newGrid, edgeMinusTwo, edge, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, sortedNeighborsIndexes)) {
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
			return changed;
		} catch (Exception ex) {
			if (newGrid != null)
				newGrid.close();
			close();
			throw ex;
		}
	}
	
	private boolean toppleRangeBeyondX2(RandomAccessFile newGrid, int minX, int maxX, 
			long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, int[] sortedNeighborsIndexes) throws IOException {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++) {
			// y = 0;
			int relevantAsymmetricNeighborCount = 0;
			int relevantNeighborCount = 0;
			long currentValue = getFromAsymmetricPosition(x, 0);
			long greaterYNeighborValue = getFromAsymmetricPosition(x, 1);
			long smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, 0);
			long greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, 0);
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = xMinusOne;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = xPlusOne;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = x;
				nc[1] = 1;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
				relevantNeighborCount += 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newGrid, currentValue, x, 0, relevantAsymmetricNeighborValues, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				anyToppled = true;
			}
			// y = 1
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			long smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(x, 2);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, 1);
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, 1);
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = xMinusOne;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = xPlusOne;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = x;
				nc[1] = 0;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = x;
				nc[1] = 2;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newGrid, currentValue, x, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				anyToppled = true;
			}
			// 2 >= y < x - 1
			int y = 2, yMinusOne = 1, yPlusOne = 3;
			for (; y < xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				relevantAsymmetricNeighborCount = 0;
				// reuse values obtained previously
				smallerYNeighborValue = currentValue;
				currentValue = greaterYNeighborValue;
				greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne);
				smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y);
				greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y);
				if (smallerXNeighborValue < currentValue) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = xMinusOne;
					nc[1] = y;
					relevantAsymmetricNeighborCount++;
				}
				if (greaterXNeighborValue < currentValue) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = xPlusOne;
					nc[1] = y;
					relevantAsymmetricNeighborCount++;
				}
				if (smallerYNeighborValue < currentValue) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = x;
					nc[1] = yMinusOne;
					relevantAsymmetricNeighborCount++;
				}
				if (greaterYNeighborValue < currentValue) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = x;
					nc[1] = yPlusOne;
					relevantAsymmetricNeighborCount++;
				}
				if (topplePosition(newGrid, currentValue, x, y, relevantAsymmetricNeighborValues, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
					anyToppled = true;
				}
			}
			// y = x - 1
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = getFromAsymmetricPosition(x, yPlusOne);
			smallerXNeighborValue = getFromAsymmetricPosition(xMinusOne, y);
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y);
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = xMinusOne;
				nc[1] = y;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = xPlusOne;
				nc[1] = y;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = x;
				nc[1] = yMinusOne;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = x;
				nc[1] = yPlusOne;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newGrid, currentValue, x, y, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				anyToppled = true;
			}
			// y = x
			yMinusOne = y;
			y = x;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(xPlusOne, y);
			if (smallerYNeighborValue < currentValue) {
				if (greaterXNeighborValue < currentValue) {
					if (smallerYNeighborValue == greaterXNeighborValue) {
						// gx = sy < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, x, yMinusOne, share);
						addToPosition(newGrid, x, y, currentValue - toShare + share + toShare%5);
						addToPosition(newGrid, xPlusOne, y, share);
					} else if (smallerYNeighborValue < greaterXNeighborValue) {
						// sy < gx < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, x, yMinusOne, share);
						addToPosition(newGrid, xPlusOne, y, share);
						long currentRemainingValue = currentValue - 4*share;
						toShare = currentRemainingValue - smallerYNeighborValue; 
						share = toShare/3;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, x, yMinusOne, share);
						addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%3);
					} else {
						// gx < sy < current
						long toShare = currentValue - smallerYNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, x, yMinusOne, share);
						addToPosition(newGrid, xPlusOne, y, share);
						long currentRemainingValue = currentValue - 4*share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/3;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%3);
						addToPosition(newGrid, xPlusOne, y, share);
					}
				} else {
					// sy < current <= gx
					long toShare = currentValue - smallerYNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						anyToppled = true;
					}
					addToPosition(newGrid, x, yMinusOne, share);
					addToPosition(newGrid, x, y, currentValue - toShare + share + toShare%3);
				}
			} else if (greaterXNeighborValue < currentValue) {
				// gx < current <= sy
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/3;
				if (share != 0) {
					anyToppled = true;
				}
				addToPosition(newGrid, x, y, currentValue - toShare + share + toShare%3);
				addToPosition(newGrid, xPlusOne, y, share);
			} else {
				// gx >= current <= sy
				addToPosition(newGrid, x, y, currentValue);
			}
		}
		return anyToppled;
	}
	
	private static boolean topplePosition(RandomAccessFile newGrid, long value, int x, int y, long[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount, int[] sortedNeighborsIndexes) throws IOException {
		boolean toppled = false;
		switch (neighborCount) {
			case 4:
				Utils.sortDescendingLength4(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, neighborValues, 
						neighborCoords, neighborShareMultipliers, neighborCount, sortedNeighborsIndexes);
				break;
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, neighborValues, 
						neighborCoords, neighborShareMultipliers, neighborCount, sortedNeighborsIndexes);
				break;
			case 2:
				long n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				int shareCount = 3;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share*n1Mult);
					addToPosition(newGrid, x, y, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share*n1Mult);
					shareCount = 2;
					long currentRemainingValue = value - 2*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share*n0Mult);
					addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share*n1Mult);
					shareCount = 2;
					long currentRemainingValue = value - 2*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share*n1Mult);
					addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%shareCount);
				}
				break;
			case 1:
				long toShare = value - neighborValues[0];
				long share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					addToPosition(newGrid, nc[0], nc[1], share * neighborShareMultipliers[0]);
				}
				// no break
			default: // 0
				addToPosition(newGrid, x, y, value);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int x, int y, long[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount, int[] sortedNeighborsIndexes) throws IOException {
		boolean toppled = false;
		boolean isFirstNeighbor = true;
		long previousNeighborValue = 0;
		for (int i = 0, shareCount = neighborCount + 1; i < neighborCount; i++, shareCount--, isFirstNeighbor = false) {
			long neighborValue = neighborValues[i];
			if (neighborValue != previousNeighborValue || isFirstNeighbor) {
				long toShare = value - neighborValue;
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
						addToPosition(newGrid, nc[0], nc[1], share * neighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
		}
		addToPosition(newGrid, x, y, value);
		return toppled;
	}
	
	private static boolean topplePosition(RandomAccessFile newGrid, long value, int x, int y, long[] neighborValues,
			int[][] neighborCoords, int neighborCount, int[] sortedNeighborsIndexes) throws IOException {
		boolean toppled = false;
		switch (neighborCount) {
			case 4:
				Utils.sortDescendingLength4(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, neighborValues, 
						neighborCoords, neighborCount, sortedNeighborsIndexes);
				break;
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, neighborValues, 
						neighborCoords, neighborCount, sortedNeighborsIndexes);
				break;
			case 2:
				long n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int shareCount = 3;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share);
					addToPosition(newGrid, x, y, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share);
					shareCount = 2;
					long currentRemainingValue = value - 2*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share);
					addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share);
					shareCount = 2;
					long currentRemainingValue = value - 2*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share);
					addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%shareCount);
				}
				break;
			case 1:
				long toShare = value - neighborValues[0];
				long share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					addToPosition(newGrid, nc[0], nc[1], share);
				}
				// no break
			default: // 0
				addToPosition(newGrid, x, y, value);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int x, int y, long[] neighborValues,
			int[][] neighborCoords, int neighborCount, int[] sortedNeighborsIndexes) throws IOException {
		boolean toppled = false;
		boolean isFirstNeighbor = true;
		long previousNeighborValue = 0;
		for (int i = 0, shareCount = neighborCount + 1; i < neighborCount; i++, shareCount--, isFirstNeighbor = false) {
			long neighborValue = neighborValues[i];
			if (neighborValue != previousNeighborValue || isFirstNeighbor) {
				long toShare = value - neighborValue;
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
						addToPosition(newGrid, nc[0], nc[1], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
		}
		addToPosition(newGrid, x, y, value);
		return toppled;
	}
	
	private static boolean topplePosition(RandomAccessFile newGrid, long value, int x, int y, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, int neighborCount, int asymmetricNeighborCount, int[] sortedNeighborsIndexes) throws IOException {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 4:
				Utils.sortDescendingLength4(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount, sortedNeighborsIndexes);
				break;
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount, sortedNeighborsIndexes);
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
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share);
					addToPosition(newGrid, x, y, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share);
					addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share);
					addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%shareCount);
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
					addToPosition(newGrid, nc[0], nc[1], share);
				}
				// no break
			default: // 0
				addToPosition(newGrid, x, y, value);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int x, int y, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, int neighborCount, int asymmetricNeighborCount, int[] sortedNeighborsIndexes) throws IOException {
		boolean toppled = false;
		boolean isFirstNeighbor = true;
		long previousNeighborValue = 0;
		int shareCount = neighborCount + 1;
		for (int i = 0; i < asymmetricNeighborCount; i++, isFirstNeighbor = false) {
			long neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue || isFirstNeighbor) {
				long toShare = value - neighborValue;
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
						addToPosition(newGrid, nc[0], nc[1], share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		addToPosition(newGrid, x, y, value);
		return toppled;
	}
	
	private static boolean topplePosition(RandomAccessFile newGrid, long value, int x, int y, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount, int[] sortedNeighborsIndexes) throws IOException {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 4:
				Utils.sortDescendingLength4(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount, sortedNeighborsIndexes);
				break;
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newGrid, value, x, y, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount, sortedNeighborsIndexes);
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
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share*n1Mult);
					addToPosition(newGrid, x, y, value - toShare + share + toShare%shareCount);
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share*n0Mult);
					addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%shareCount);
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n0Coords[0], n0Coords[1], share*n0Mult);
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share*n1Mult);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					addToPosition(newGrid, n1Coords[0], n1Coords[1], share*n1Mult);
					addToPosition(newGrid, x, y, currentRemainingValue - toShare + share + toShare%shareCount);
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
					addToPosition(newGrid, nc[0], nc[1], share * asymmetricNeighborShareMultipliers[0]);
				}
				// no break
			default: // 0
				addToPosition(newGrid, x, y, value);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(RandomAccessFile newGrid, long value, int x, int y, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount, int[] sortedNeighborsIndexes) throws IOException {
		boolean toppled = false;
		boolean isFirstNeighbor = true;
		long previousNeighborValue = 0;
		int shareCount = neighborCount + 1;
		for (int i = 0; i < asymmetricNeighborCount; i++, isFirstNeighbor = false) {
			long neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue || isFirstNeighbor) {
				long toShare = value - neighborValue;
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
						addToPosition(newGrid, nc[0], nc[1], share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		addToPosition(newGrid, x, y, value);
		return toppled;
	}
	
	@Override
	public long getFromPosition(int x, int y) throws IOException {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		long value = 0;
		if (y > x) {
			if (y <= maxX) {
				value = getFromAsymmetricPosition(y, x);
			}
		} else {
			if (x <= maxX) {
				value = getFromAsymmetricPosition(x, y);
			}
		}
		return value;
	}
	
	@Override
	public long getFromAsymmetricPosition(int x, int y) throws IOException {
		grid.seek((((x*x-x)/2+x)+y)*POSITION_BYTES);
		return grid.readLong();
	}
	
	private static void addToPosition(RandomAccessFile grid, int x, int y, long value) throws IOException {
		long pos = (((x*x-x)/2+x)+y)*POSITION_BYTES;
		grid.seek(pos);
		long previousValue = grid.readLong();
		grid.seek(pos);
		grid.writeLong(previousValue + value);
	}

	@Override
	public int getAsymmetricMaxX() {
		return maxX;
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
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/" + initialValue;
	}
	
	@Override
	protected HashMap<String, Object> getPropertiesMap() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("initialValue", initialValue);
		properties.put("step", step);
		properties.put("maxX", maxX);
		return properties;
	}
	
	@Override
	protected void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (long) properties.get("initialValue");
		step = (long) properties.get("step");
		maxX = (int) properties.get("maxX");
	}
}
