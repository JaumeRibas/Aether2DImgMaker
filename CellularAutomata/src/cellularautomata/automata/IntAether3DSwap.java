/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
import java.math.BigInteger;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

import cellularautomata.grid.IntGrid3D;
import cellularautomata.grid.IntGrid3DProcessor;
import cellularautomata.grid.NonSymmetricIntGrid3DSlice;
import cellularautomata.grid.NonSymmetricIntSubGrid3D;
import cellularautomata.grid.SizeLimitedNonSymmetricIntGrid3D;

public class IntAether3DSwap extends SymmetricIntCellularAutomaton3D {

	public static final long PRIMITIVE_MAX_VALUE = Integer.MAX_VALUE;
	
	private static final String PROPERTIES_BACKUP_FILE_NAME = "properties.ser";
	private static final String GRID_FOLDER_NAME = "grid";
	
	private static final byte LEFT = 0;
	private static final byte CENTER = 1;
	private static final byte RIGHT = 2;
	private static final byte UP = 3;
	private static final byte DOWN = 4;	
	private static final byte FRONT = 5;
	private static final byte BACK = 6;
	
	private SizeLimitedNonSymmetricIntGrid3D gridBlockA;
	private SizeLimitedNonSymmetricIntGrid3D gridBlockB;
	private int initialValue;
	private int currentStep;
	private int maxX, maxY, maxZ;
	private File gridFolder;
	private long maxGridBlockSize;
	
	/**
	 * 
	 * @param initialValue
	 * @param maxGridHeapSize the maximum amount of heap space the grid can take up in bytes. This won't limit the total size of the grid.
	 * @param folderPath
	 * @throws Exception
	 */
	public IntAether3DSwap(int initialValue, long maxGridHeapSize, String folderPath) throws Exception {
		if (initialValue < 0) {
			BigInteger maxValue = BigInteger.valueOf(initialValue).add(
					BigInteger.valueOf(initialValue).negate().divide(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(6)));
			if (maxValue.compareTo(BigInteger.valueOf(PRIMITIVE_MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value " + maxValue 
						+ " exceeds implementation's limit (" + PRIMITIVE_MAX_VALUE + ").");
			}
		}
		this.initialValue = initialValue;
		maxGridBlockSize = maxGridHeapSize/2;
		gridBlockA = new SizeLimitedNonSymmetricIntGrid3D(0, maxGridBlockSize);
		gridBlockA.setValueAtPosition(0, 0, 0, initialValue);
		maxX = 1;//we leave a buffer of one position to account for 'negative growth'
		maxY = 0;
		maxZ = 0;
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
	public IntAether3DSwap(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
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

	private SizeLimitedNonSymmetricIntGrid3D loadGridBlockSafe(int minX) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedNonSymmetricIntGrid3D gridBlock = null;
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
			gridBlock = (SizeLimitedNonSymmetricIntGrid3D) in.readObject();
			in.close();
		}
		return gridBlock;
	}
	
	private SizeLimitedNonSymmetricIntGrid3D loadGridBlock(int minX) throws IOException, ClassNotFoundException {
		SizeLimitedNonSymmetricIntGrid3D gridBlock = loadGridBlockSafe(minX);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minX=" + minX + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private SizeLimitedNonSymmetricIntGrid3D findGridBlockSafe(int x) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedNonSymmetricIntGrid3D gridBlock = null;
		File gridBlockFile = null;
		for (int i = 0; i < files.length && !found; i++) {
			File currentFile = files[i];
			String fileName = currentFile.getName();
			int fileMinX, fileMaxX;
			int indexOfSeparator = fileName.indexOf('_');
			try {
				//"minX=".length() == 5
				fileMinX = Integer.parseInt(fileName.substring(5, indexOfSeparator));
				fileMaxX = Integer.parseInt(fileName.substring(indexOfSeparator + 6, fileName.indexOf('.')));
				if (x == fileMinX || x == fileMaxX || x > fileMinX && x < fileMaxX) {
					found = true;
					gridBlockFile = currentFile;
				}
			} catch (NumberFormatException ex) {
				
			}
		}
		if (found) {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(gridBlockFile));
			gridBlock = (SizeLimitedNonSymmetricIntGrid3D) in.readObject();
			in.close();
		}
		return gridBlock;		
	}
	
	private SizeLimitedNonSymmetricIntGrid3D findGridBlock(int x) throws IOException, ClassNotFoundException {
		SizeLimitedNonSymmetricIntGrid3D gridBlock = findGridBlockSafe(x);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block containing x=" + x + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private void saveGridBlock(SizeLimitedNonSymmetricIntGrid3D gridBlock) throws FileNotFoundException, IOException {
		String name = "minX=" + gridBlock.minX + "_maxX=" + gridBlock.maxX + ".ser";
		String pathName = this.gridFolder.getPath() + File.separator + name;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}
	
	private SizeLimitedNonSymmetricIntGrid3D loadOrBuildGridBlock(int minX) throws ClassNotFoundException, IOException {		
		SizeLimitedNonSymmetricIntGrid3D gridBlock = loadGridBlockSafe(minX);
		if (gridBlock != null) {
			return gridBlock;
		} else {
			return new SizeLimitedNonSymmetricIntGrid3D(minX, maxGridBlockSize);
		}
	}

	@Override
	public boolean nextStep() throws Exception {
		triggerBeforeProcessing();
		boolean gridChanged = false;		
		NonSymmetricIntGrid3DSlice[] newGridSlices = 
				new NonSymmetricIntGrid3DSlice[] {
						null, 
						new NonSymmetricIntGrid3DSlice(0), 
						new NonSymmetricIntGrid3DSlice(1)};
		if (gridBlockA.minX > 0) {
			if (gridBlockB != null && gridBlockB.minX == 0) {
				SizeLimitedNonSymmetricIntGrid3D swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			} else {
				saveGridBlock(gridBlockA);
				gridBlockA = null;
				gridBlockA = loadGridBlock(0);
			}
		}
		int currentMaxX = maxX;//it can change during computing
		int x = 0;
		boolean anySlicePositionToppled = computeGridSlice(gridBlockA, 0, newGridSlices);
		gridChanged = gridChanged || anySlicePositionToppled;
		x++;
		while (x <= currentMaxX) {
			while (x <= currentMaxX && x < gridBlockA.maxX) {
				slideGridSlices(newGridSlices, new NonSymmetricIntGrid3DSlice(x + 1));
				anySlicePositionToppled = computeGridSlice(gridBlockA, x, newGridSlices);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(x - 1, newGridSlices[LEFT]);
				x++;
			}
			if (x <= currentMaxX) {
				if (gridBlockB != null) {
					if (gridBlockB.minX != x + 1) {
						saveGridBlock(gridBlockB);
						gridBlockB = null;
						gridBlockB = loadOrBuildGridBlock(x + 1);
					}
				} else {
					gridBlockB = loadOrBuildGridBlock(x + 1);
				}
				slideGridSlices(newGridSlices, new NonSymmetricIntGrid3DSlice(x + 1));
				anySlicePositionToppled = computeLastGridSlice(gridBlockA, gridBlockB, x, newGridSlices);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(x - 1, newGridSlices[LEFT]);
				x++;
				if (x <= currentMaxX) {
					slideGridSlices(newGridSlices, new NonSymmetricIntGrid3DSlice(x + 1));
					anySlicePositionToppled = computeFirstGridSlice(gridBlockA, gridBlockB, x, newGridSlices);
					gridChanged = gridChanged || anySlicePositionToppled;
					gridBlockA.setSlice(x - 1, newGridSlices[LEFT]);
					processGridBlock(gridBlockA);
					SizeLimitedNonSymmetricIntGrid3D swp = gridBlockA;
					gridBlockA = gridBlockB;
					gridBlockB = swp;
					x++;
				}
			}
		}
		gridBlockA.setSlice(currentMaxX, newGridSlices[CENTER]);
		if (currentMaxX == gridBlockA.maxX) {
			processGridBlock(gridBlockA);
			gridBlockB.setSlice(currentMaxX + 1, newGridSlices[RIGHT]);
			processGridBlock(gridBlockB);
		} else {
			gridBlockA.setSlice(currentMaxX + 1, newGridSlices[RIGHT]);
			processGridBlock(gridBlockA);
		}
		currentStep++;
		triggerAfterProcessing();
		return gridChanged;
	}
	
	private void processGridBlock(SizeLimitedNonSymmetricIntGrid3D block) throws Exception {
		if (block.minX <= maxX) {
			IntGrid3D subBlock = null;
			if (block.maxX > maxX) {
				subBlock = new NonSymmetricIntSubGrid3D(block, block.minX, maxX, 0, maxX, 0, maxX);
			} else {
				subBlock = block;
			}
			if (processors != null) {
				for (IntGrid3DProcessor processor : processors) {
					processor.processGridBlock(subBlock);
				}
			}
		}
	}
	
	@Override
	public void processGrid() throws Exception {
		triggerBeforeProcessing();
		if (gridBlockA.minX > 0) {
			if (gridBlockB != null && gridBlockB.minX == 0) {
				SizeLimitedNonSymmetricIntGrid3D swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			} else {
				saveGridBlock(gridBlockA);
				gridBlockA = null;
				gridBlockA = loadGridBlock(0);
			}
		}
		int x = 0;
		while (x <= maxX) {
			x = gridBlockA.maxX;
			if (x <= maxX) {
				if (gridBlockB != null) {
					if (gridBlockB.minX != x + 1) {
						saveGridBlock(gridBlockB);
						gridBlockB = null;
						gridBlockB = loadOrBuildGridBlock(x + 1);
					}
				} else {
					gridBlockB = loadOrBuildGridBlock(x + 1);
				}
				x++;
				if (x <= maxX) {
					processGridBlock(gridBlockA);
					SizeLimitedNonSymmetricIntGrid3D swp = gridBlockA;
					gridBlockA = gridBlockB;
					gridBlockB = swp;
					x++;
				}
			}
		}
		if (maxX == gridBlockA.maxX) {
			processGridBlock(gridBlockA);
			processGridBlock(gridBlockB);
		} else {
			processGridBlock(gridBlockA);
		}
		triggerAfterProcessing();
	}

	private void slideGridSlices(NonSymmetricIntGrid3DSlice[] newGridSlices, NonSymmetricIntGrid3DSlice newSlice) {
		newGridSlices[LEFT] = newGridSlices[CENTER];
		newGridSlices[CENTER] = newGridSlices[RIGHT];
		newGridSlices[RIGHT] = newSlice;
	}
	
	private boolean computeGridSlice(SizeLimitedNonSymmetricIntGrid3D gridBlock, int x, NonSymmetricIntGrid3DSlice[] newGridSlices) {
		return computeGridSlice(gridBlock, gridBlock, gridBlock, x, newGridSlices);
	}
	
	private boolean computeLastGridSlice(SizeLimitedNonSymmetricIntGrid3D leftGridBlock, SizeLimitedNonSymmetricIntGrid3D rightGridBlock,
			int x, NonSymmetricIntGrid3DSlice[] newGridSlices) {
		return computeGridSlice(leftGridBlock, leftGridBlock, rightGridBlock, x, newGridSlices);
	}
	
	private boolean computeFirstGridSlice(SizeLimitedNonSymmetricIntGrid3D leftGridBlock, SizeLimitedNonSymmetricIntGrid3D rightGridBlock,
			int x, NonSymmetricIntGrid3DSlice[] newGridSlices) {
		return computeGridSlice(leftGridBlock, rightGridBlock, rightGridBlock, x, newGridSlices);
	}

	private boolean computeGridSlice(SizeLimitedNonSymmetricIntGrid3D leftGridBlock, SizeLimitedNonSymmetricIntGrid3D centerGridBlock, 
			SizeLimitedNonSymmetricIntGrid3D rightGridBlock, int x, NonSymmetricIntGrid3DSlice[] newGridSlices) {
		boolean anyPositionToppled = false;
		for (int y = 0; y <= x; y++) {
			for (int z = 0; z <= y; z++) {				
				int value = centerGridBlock.getValueAtPosition(x, y, z);
				int rightValue = getValueAtPosition(x, leftGridBlock, centerGridBlock, rightGridBlock, x + 1, y, z);
				int leftValue = getValueAtPosition(x, leftGridBlock, centerGridBlock, rightGridBlock, x - 1, y, z);
				int upValue = getValueAtPosition(x, leftGridBlock, centerGridBlock, rightGridBlock, x, y + 1, z);
				int downValue = getValueAtPosition(x, leftGridBlock, centerGridBlock, rightGridBlock, x, y - 1, z);
				int frontValue = getValueAtPosition(x, leftGridBlock, centerGridBlock, rightGridBlock, x, y, z + 1);
				int backValue = getValueAtPosition(x, leftGridBlock, centerGridBlock, rightGridBlock, x, y, z - 1);				
				boolean positionToppled = computePosition(value, rightValue, leftValue, upValue, downValue, frontValue, backValue, 
						x, y, z, newGridSlices);
				anyPositionToppled = anyPositionToppled || positionToppled;
			}
		}
		return anyPositionToppled;
	}
	
	private int getValueAtPosition(int centerX, SizeLimitedNonSymmetricIntGrid3D leftGridBlock, SizeLimitedNonSymmetricIntGrid3D centerGridBlock, 
			SizeLimitedNonSymmetricIntGrid3D rightGridBlock, int x, int y, int z) {
		int[] nonSymmetricCoords = getNonSymmetricCoords(x, y, z);
		x = nonSymmetricCoords[0];
		y = nonSymmetricCoords[1];
		z = nonSymmetricCoords[2];
		int value;
		if (x == centerX) {
			value = centerGridBlock.getValueAtPosition(x, y, z);
		} else if (x < centerX) {
			value = leftGridBlock.getValueAtPosition(x, y, z);
		} else {
			value = rightGridBlock.getValueAtPosition(x, y, z);
		}
		return value;
	}
	
	private boolean computePosition(int value, int rightValue, int leftValue, int upValue, int downValue, int frontValue, int backValue, 
			int x, int y, int z, NonSymmetricIntGrid3DSlice[] newGridSlices) {
		int[] neighborValues = new int[6];
		byte[] neighborDirections = new byte[6];
		int relevantNeighborCount = 0;
		if (rightValue < value) {
			neighborValues[relevantNeighborCount] = rightValue;
			neighborDirections[relevantNeighborCount] = RIGHT;
			relevantNeighborCount++;
		}
		if (leftValue < value) {
			neighborValues[relevantNeighborCount] = leftValue;
			neighborDirections[relevantNeighborCount] = LEFT;
			relevantNeighborCount++;
		}
		if (upValue < value) {
			neighborValues[relevantNeighborCount] = upValue;
			neighborDirections[relevantNeighborCount] = UP;
			relevantNeighborCount++;
		}
		if (downValue < value) {
			neighborValues[relevantNeighborCount] = downValue;
			neighborDirections[relevantNeighborCount] = DOWN;
			relevantNeighborCount++;
		}
		if (frontValue < value) {
			neighborValues[relevantNeighborCount] = frontValue;
			neighborDirections[relevantNeighborCount] = FRONT;
			relevantNeighborCount++;
		}
		if (backValue < value) {
			neighborValues[relevantNeighborCount] = backValue;
			neighborDirections[relevantNeighborCount] = BACK;
			relevantNeighborCount++;
		}
		boolean toppled = false;
		if (relevantNeighborCount > 0) {
			//sort
			boolean sorted = false;
			while (!sorted) {
				sorted = true;
				for (int i = relevantNeighborCount - 2; i >= 0; i--) {
					if (neighborValues[i] < neighborValues[i+1]) {
						sorted = false;
						int valSwap = neighborValues[i];
						neighborValues[i] = neighborValues[i+1];
						neighborValues[i+1] = valSwap;
						byte dirSwap = neighborDirections[i];
						neighborDirections[i] = neighborDirections[i+1];
						neighborDirections[i+1] = dirSwap;
					}
				}
			}
			//divide
			boolean isFirstNeighbor = true;
			int previousNeighborValue = 0;
			for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
				int neighborValue = neighborValues[i];
				if (neighborValue != previousNeighborValue || isFirstNeighbor) {
					int shareCount = relevantNeighborCount - i + 1;
					int toShare = value - neighborValue;
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
						value = value - toShare + toShare%shareCount + share;
						for (int j = i; j < relevantNeighborCount; j++) {
							addToNeighbor(newGridSlices, x, y, z, neighborDirections[j], share);
						}
					}
					previousNeighborValue = neighborValue;
				}
			}	
		}					
		newGridSlices[CENTER].addValueAtPosition(y, z, value);
		return toppled;
	}

	private void addToNeighbor(NonSymmetricIntGrid3DSlice[] newGridSlices, int x, int y, int z, byte direction, int value) {
		switch(direction) {
		case RIGHT:
			addRight(newGridSlices, x, y, z, value);
			break;
		case LEFT:
			addLeft(newGridSlices, x, y, z, value);
			break;
		case UP:
			addUp(newGridSlices, x, y, z, value);
			break;
		case DOWN:
			addDown(newGridSlices, x, y, z, value);
			break;
		case FRONT:
			addFront(newGridSlices, x, y, z, value);
			break;
		case BACK:
			addBack(newGridSlices, x, y, z, value);
			break;
		}
	}
	
	private void addRight(NonSymmetricIntGrid3DSlice[] newGridSlices, int x, int y, int z, int value) {
		newGridSlices[RIGHT].addValueAtPosition(y, z, value);
		if (x == maxX - 1) {
			maxX++;
		}
	}
	
	private void addLeft(NonSymmetricIntGrid3DSlice[] newGridSlices, int x, int y, int z, int value) {
		if (x > y) {
			int valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
				if (z == y) {
					valueToAdd += value;
					if (x == 1) {
						valueToAdd += 3*value;
					}
				}
			}
			newGridSlices[LEFT].addValueAtPosition(y, z, valueToAdd);
		}
		if (x == maxX) {
			maxX++;
		}
	}
	
	private void addUp(NonSymmetricIntGrid3DSlice[] newGridSlices, int x, int y, int z, int value) {
		if (y < x) {
			int valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
			}
			int yy = y+1;
			newGridSlices[CENTER].addValueAtPosition(yy, z, valueToAdd);
			if (yy > maxY)
				maxY = yy;
		}
	}
	
	private void addDown(NonSymmetricIntGrid3DSlice[] newGridSlices, int x, int y, int z, int value) {
		if (y > z) {	
			int valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (y == 1) {
					valueToAdd += 2*value;
				}
			}
			newGridSlices[CENTER].addValueAtPosition(y - 1, z, valueToAdd);
		}
	}
	
	private void addFront(NonSymmetricIntGrid3DSlice[] newGridSlices, int x, int y, int z, int value) {
		if (z < y) {
			int valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (x == y) {
					valueToAdd += value;
				}
			}
			int zz = z+1;
			newGridSlices[CENTER].addValueAtPosition(y, zz, valueToAdd);
			if (zz > maxZ)
				maxZ = zz;
		}
	}
	
	private void addBack(NonSymmetricIntGrid3DSlice[] newGridSlices, int x, int y, int z, int value) {
		if (z > 0) {
			int valueToAdd = value;
			if (z == 1) {
				valueToAdd += value;
			}
			newGridSlices[CENTER].addValueAtPosition(y, z - 1, valueToAdd);
		}	
	}

	@Override
	public String getName() {
		return "Aether3D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	@Override
	public long getStep() {
		return currentStep;
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
	public int getBackgroundValue() {
		return 0;
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
		Utils.serializeToFile(properties, backupFolderPath, "properties.ser");
	}
	
	private HashMap<String, Object> getPropertiesMap() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("initialValue", initialValue);
		properties.put("currentStep", currentStep);
		properties.put("maxX", maxX);
		properties.put("maxY", maxY);
		properties.put("maxZ", maxZ);
		properties.put("maxGridBlockSize", maxGridBlockSize);
		return properties;
	}
	
	private void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (int) properties.get("initialValue");
		currentStep = (int) properties.get("currentStep");
		maxX = (int) properties.get("maxX"); 
		maxY = (int) properties.get("maxY"); 
		maxZ = (int) properties.get("maxZ");
		maxGridBlockSize = (long) properties.get("maxGridBlockSize");
	}

	@Override
	public int getNonSymmetricValue(int x, int y, int z) throws IOException, ClassNotFoundException {
		int value;
		//TODO: try to keep blocks contiguous
		if (x == gridBlockA.minX || x == gridBlockA.maxX || x > gridBlockA.minX && x < gridBlockA.maxX) {
			value = gridBlockA.getValueAtPosition(x, y, z);
		} else if (gridBlockB != null && (x == gridBlockB.minX || x == gridBlockB.maxX || x > gridBlockB.minX && x < gridBlockB.maxX)) {
			value = gridBlockB.getValueAtPosition(x, y, z);
		} else {
			saveGridBlock(gridBlockA);
			gridBlockA = null;
			gridBlockA = findGridBlock(x);
			value = gridBlockA.getValueAtPosition(x, y, z);
		}
		return value;
	}

	@Override
	public int getValueAtPosition(int x, int y, int z) throws IOException, ClassNotFoundException {
		int[] nonSymmetricCoords = getNonSymmetricCoords(x, y, z);
		x = nonSymmetricCoords[0];
		y = nonSymmetricCoords[1];
		z = nonSymmetricCoords[2];
		if (x <= maxX) {
			return getNonSymmetricValue(x, y, z);
		} else {
			return 0;
		}
	}
	
	private int[] getNonSymmetricCoords(int x, int y, int z) {
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		//sort coordinates
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
		} while (!sorted);
		return new int[] {x, y, z};
	}	
	
	@Override
	public int getMinX() {
		return -maxX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public int getMinY() {
		return -maxX;
	}

	@Override
	public int getMaxY() {
		return maxX;
	}
	
	@Override
	public int getMinZ() {
		return -maxX;
	}

	@Override
	public int getMaxZ() {
		return maxX;
	}
	
	@Override
	public int getNonSymmetricMinX() {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxX() {
		return maxX;
	}
	
	@Override
	public int getNonSymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxY() {
		return maxY;
	}
	
	@Override
	public int getNonSymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxZ() {
		return maxZ;
	}
	
	@Override
	public int getNonSymmetricMinXAtY(int y) {
		return y;
	}

	@Override
	public int getNonSymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMinX(int y, int z) {
		return Math.max(y, z);
	}

	@Override
	public int getNonSymmetricMaxXAtY(int y) {
		return maxX;
	}

	@Override
	public int getNonSymmetricMaxXAtZ(int z) {
		return maxX;
	}

	@Override
	public int getNonSymmetricMaxX(int y, int z) {
		return maxX;
	}

	@Override
	public int getNonSymmetricMinYAtX(int x) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMinY(int x, int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMaxYAtX(int x) {
		return Math.min(maxY, x);
	}

	@Override
	public int getNonSymmetricMaxYAtZ(int z) {
		return maxY;
	}

	@Override
	public int getNonSymmetricMaxY(int x, int z) {
		return Math.min(maxY, x);
	}

	@Override
	public int getNonSymmetricMinZAtX(int x) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinZAtY(int y) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinZ(int x, int y) {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxZAtX(int x) {
		return Math.min(maxZ, x);
	}

	@Override
	public int getNonSymmetricMaxZAtY(int y) {
		return Math.min(maxZ, y);
	}

	@Override
	public int getNonSymmetricMaxZ(int x, int y) {
		return Math.min(maxZ, y);
	}
	
}