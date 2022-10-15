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

import cellularautomata.model.FileBackedModel;
import cellularautomata.model1d.IsotropicModel1DA;
import cellularautomata.model1d.SymmetricLongModel1D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class FileBackedAether1D extends FileBackedModel implements SymmetricLongModel1D, IsotropicModel1DA {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -9223372036854775807L;
	public static final int POSITION_BYTES = Long.BYTES;

	private long initialValue;
	private long step;
	private int maxX;
	private Boolean changed = null;

	public FileBackedAether1D(long initialValue, String folderPath) throws IOException {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		createGridFolder(folderPath);
		currentFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step));
		grid = new RandomAccessFile(currentFile, "rw");
		grid.setLength(5*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
		grid.writeLong(initialValue);
		step = 0;
		maxX = 2;
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
	public FileBackedAether1D(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		super(backupPath, folderPath);
	}
	
	@Override
	public Boolean nextStep() throws IOException {
		RandomAccessFile newGrid = null;
		try {
			boolean changed = false;
			long currentValue, greaterXNeighborValue, smallerXNeighborValue;
			//x = 0
			currentValue = getFromAsymmetricPosition(0);
			greaterXNeighborValue = getFromAsymmetricPosition(1);
			File newFile = new File(getGridFolderPath() + File.separator + String.format(FILE_NAME_FORMAT, step + 1));
			newGrid = new RandomAccessFile(newFile, "rw");
			newGrid.setLength((maxX + 4)*POSITION_BYTES);//this method doesn't ensure the contents of the file will be empty
			if (greaterXNeighborValue < currentValue) {
				long toShare = currentValue - greaterXNeighborValue;
				long share = toShare/3;
				if (share != 0) {
					changed = true;
					addToPosition(newGrid, 0, currentValue - toShare + share + toShare%3);
					addToPosition(newGrid, 1, share);
				} else {
					addToPosition(newGrid, 0, currentValue);
				}			
			} else {
				addToPosition(newGrid, 0, currentValue);
			}
			//x = 1
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = getFromAsymmetricPosition(2);
			if (smallerXNeighborValue < currentValue) {
				if (greaterXNeighborValue < currentValue) {
					if (smallerXNeighborValue == greaterXNeighborValue) {
						// gn == sn < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 0, share + share);//one more for the symmetric position at the other side
						addToPosition(newGrid, 1, currentValue - toShare + share + toShare%3);
						addToPosition(newGrid, 2, share);
					} else if (smallerXNeighborValue < greaterXNeighborValue) {
						// sn < gn < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 0, share + share);//one more for the symmetric position at the other side
						addToPosition(newGrid, 2, share);
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - smallerXNeighborValue; 
						share = toShare/2;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 0, share + share);//one more for the symmetric position at the other side
						addToPosition(newGrid, 1, currentRemainingValue - toShare + share + toShare%2);
					} else {
						// gn < sn < current
						long toShare = currentValue - smallerXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 0, share + share);//one more for the symmetric position at the other side
						addToPosition(newGrid, 2, share);
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/2;
						if (share != 0) {
							changed = true;
						}
						addToPosition(newGrid, 1, currentRemainingValue - toShare + share + toShare%2);
						addToPosition(newGrid, 2, share);
					}
				} else {
					// sn < current <= gn
					long toShare = currentValue - smallerXNeighborValue; 
					long share = toShare/2;
					if (share != 0) {
						changed = true;
					}
					addToPosition(newGrid, 0, share + share);//one more for the symmetric position at the other side
					addToPosition(newGrid, 1, currentValue - toShare + share + toShare%2);
				}
			} else if (greaterXNeighborValue < currentValue) {
				// gn < current <= sn
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/2;
				if (share != 0) {
					changed = true;
				}
				addToPosition(newGrid, 1, currentValue - toShare + share + toShare%2);
				addToPosition(newGrid, 2, share);
			} else {
				// gn >= current <= sn
				addToPosition(newGrid, 1, currentValue);
			}
			//2 <= x < edge - 2
			int edge = maxX + 2;
			int edgeMinusTwo = edge - 2;
			if (toppleRangeBeyondX1(newGrid, 2, edgeMinusTwo)) {
				changed = true;
			}
			//edge - 2 <= x < edge
			if (toppleRangeBeyondX1(newGrid, edgeMinusTwo, edge)) {
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
	
	private boolean toppleRangeBeyondX1(RandomAccessFile newGrid, int minX, int maxX) throws IOException {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		long oldSmallerXValue, oldCurrentValue = getFromAsymmetricPosition(xMinusOne), oldGreaterXValue = getFromAsymmetricPosition(x);
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne++) {
			//reuse values obtained previously
			oldSmallerXValue = oldCurrentValue;
			oldCurrentValue = oldGreaterXValue;
			oldGreaterXValue = getFromAsymmetricPosition(xPlusOne);
			if (oldSmallerXValue < oldCurrentValue) {
				if (oldGreaterXValue < oldCurrentValue) {
					if (oldSmallerXValue == oldGreaterXValue) {
						// gn == sn < current
						long toShare = oldCurrentValue - oldGreaterXValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, xMinusOne, share);
						addToPosition(newGrid, x, oldCurrentValue - toShare + share + toShare%3);
						addToPosition(newGrid, xPlusOne, share);
					} else if (oldSmallerXValue < oldGreaterXValue) {
						// sn < gn < current
						long toShare = oldCurrentValue - oldGreaterXValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, xMinusOne, share);
						addToPosition(newGrid, xPlusOne, share);
						long currentRemainingValue = oldCurrentValue - share - share;
						toShare = currentRemainingValue - oldSmallerXValue; 
						share = toShare/2;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, xMinusOne, share);
						addToPosition(newGrid, x, currentRemainingValue - toShare + share + toShare%2);
					} else {
						// gn < sn < current
						long toShare = oldCurrentValue - oldSmallerXValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, xMinusOne, share);
						addToPosition(newGrid, xPlusOne, share);
						long currentRemainingValue = oldCurrentValue - share - share;
						toShare = currentRemainingValue - oldGreaterXValue; 
						share = toShare/2;
						if (share != 0) {
							anyToppled = true;
						}
						addToPosition(newGrid, x, currentRemainingValue - toShare + share + toShare%2);
						addToPosition(newGrid, xPlusOne, share);
					}
				} else {
					// sn < current <= gn
					long toShare = oldCurrentValue - oldSmallerXValue; 
					long share = toShare/2;
					if (share != 0) {
						anyToppled = true;
					}
					addToPosition(newGrid, xMinusOne, share);
					addToPosition(newGrid, x, oldCurrentValue - toShare + share + toShare%2);
				}
			} else if (oldGreaterXValue < oldCurrentValue) {
				// gn < current <= sn
				long toShare = oldCurrentValue - oldGreaterXValue; 
				long share = toShare/2;
				if (share != 0) {
					anyToppled = true;
				}
				addToPosition(newGrid, x, oldCurrentValue - toShare + share + toShare%2);
				addToPosition(newGrid, xPlusOne, share);
			} else {
				// gn >= current <= sn
				addToPosition(newGrid, x, oldCurrentValue);
			}
		}
		return anyToppled;
	}
	
	@Override
	public long getFromPosition(int x) throws IOException {	
		if (x < 0) x = -x;
		if (x <= maxX) {
			return getFromAsymmetricPosition(x);
		} else {
			return 0;
		}
	}

	@Override
	public long getFromAsymmetricPosition(int x) throws IOException {
		grid.seek(x*POSITION_BYTES);
		return grid.readLong();
	}
	
	private static void addToPosition(RandomAccessFile grid, int x, long value) throws IOException {
		long pos = x*POSITION_BYTES;
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
		return getName() + "/1D/" + initialValue;
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
