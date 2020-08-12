/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

public class Utils {
	
	public static Object deserializeFromFile(String pathName) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(pathName));
		Object obj = in.readObject();
		in.close();
		return obj;
	}
	
	public static void serializeToFile(Object obj, String path, String name) throws FileNotFoundException, IOException {
		String pathName = path + "/" + name;
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(obj);
		out.flush();
		out.close();
	}
	
	/**
	 * <p>Gives the maximum value difference between neighbors throughout the evolution of an Aether model with single source initial configuration.</p>
	 * <p>If the source value is larger or equal to zero, the maximum value difference between neighbors is equal to the source value</p>
	 * 
	 * @param gridDimension the dimension of the grid
	 * @param sourceValue the value of the single source initial configuration
	 * @return
	 */
	public static BigInteger getAetherMaxNeighboringValuesDifferenceFromSingleSource(int gridDimension, BigInteger sourceValue) {
		if (gridDimension <= 0) {
			throw new IllegalArgumentException("Grid dimension must be greater than zero.");
		}
		if (sourceValue.compareTo(BigInteger.ZERO) < 0) {
			if (gridDimension > 1) {
				BigInteger two = BigInteger.valueOf(2);
				return sourceValue.add(sourceValue.negate().divide(two).multiply(BigInteger.valueOf(gridDimension).multiply(two).add(BigInteger.ONE))).abs();
			} else {
				return sourceValue.negate();
			}
		} else {
			return sourceValue; 
		}
	}
	
	public static long[][][][] buildAnisotropic4DLongArray(int side) {
		long[][][][] anisotropic4DArray = new long[side][][][];
		for (int w = 0; w < anisotropic4DArray.length; w++) {
			anisotropic4DArray[w] = buildAnisotropic3DLongArray(w + 1);
		}
		return anisotropic4DArray;
	}
	
	public static long[][][] buildAnisotropic3DLongArray(int side) {
		long[][][] anisotropic3DArray = new long[side][][];
		for (int x = 0; x < anisotropic3DArray.length; x++) {
			anisotropic3DArray[x] = buildAnisotropic2DLongArray(x + 1);
		}
		return anisotropic3DArray;
	}
	
	public static long[][] buildAnisotropic2DLongArray(int side) {
		long[][] anisotropic2DArray = new long[side][];
		for (int x = 0; x < anisotropic2DArray.length; x++) {
			anisotropic2DArray[x] = new long[x + 1];
		}
		return anisotropic2DArray;
	}
	
	public static int[][][][] buildAnisotropic4DIntArray(int side) {
		int[][][][] anisotropic4DArray = new int[side][][][];
		for (int w = 0; w < anisotropic4DArray.length; w++) {
			anisotropic4DArray[w] = buildAnisotropic3DIntArray(w + 1);
		}
		return anisotropic4DArray;
	}
	
	public static int[][][] buildAnisotropic3DIntArray(int side) {
		int[][][] anisotropic3DArray = new int[side][][];
		for (int x = 0; x < anisotropic3DArray.length; x++) {
			anisotropic3DArray[x] = buildAnisotropic2DIntArray(x + 1);
		}
		return anisotropic3DArray;
	}
	
	public static int[][] buildAnisotropic2DIntArray(int side) {
		int[][] anisotropic2DArray = new int[side][];
		for (int x = 0; x < anisotropic2DArray.length; x++) {
			anisotropic2DArray[x] = new int[x + 1];
		}
		return anisotropic2DArray;
	}
	
	public static short[][][][] buildAnisotropic4DShortArray(int side) {
		short[][][][] anisotropic4DArray = new short[side][][][];
		for (int w = 0; w < anisotropic4DArray.length; w++) {
			anisotropic4DArray[w] = buildAnisotropic3DShortArray(w + 1);
		}
		return anisotropic4DArray;
	}
	
	public static short[][][] buildAnisotropic3DShortArray(int side) {
		short[][][] anisotropic3DArray = new short[side][][];
		for (int x = 0; x < anisotropic3DArray.length; x++) {
			anisotropic3DArray[x] = buildAnisotropic2DShortArray(x + 1);
		}
		return anisotropic3DArray;
	}
	
	public static short[][] buildAnisotropic2DShortArray(int side) {
		short[][] anisotropic2DArray = new short[side][];
		for (int x = 0; x < anisotropic2DArray.length; x++) {
			anisotropic2DArray[x] = new short[x + 1];
		}
		return anisotropic2DArray;
	}
	
	public static void setAllArrayIndexes(long[] array, long value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}
	
	public static void setAllArrayIndexes(long[][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static void setAllArrayIndexes(long[][][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static void setAllArrayIndexes(long[][][][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static void setAllArrayIndexes(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}
	
	public static void setAllArrayIndexes(int[][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static void setAllArrayIndexes(int[][][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static void setAllArrayIndexes(int[][][][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static void setAllArrayIndexes(short[] array, short value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}
	
	public static void setAllArrayIndexes(short[][] array, short value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static void setAllArrayIndexes(short[][][] array, short value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static void setAllArrayIndexes(short[][][][] array, short value) {
		for (int i = 0; i < array.length; i++) {
			setAllArrayIndexes(array[i], value);
		}
	}
	
	public static long roundUpToEightMultiple(long value) {
		long reminder = value % 8;
		if (reminder > 0) {
			value += 8 - reminder;
		}
		return value;
	}
	
	public static long getAnisotropicGridVolume(int dimension, int side) {
		switch (dimension) {
			case 0:
				return 1;
			case 1://this case could be omitted
				return side;
			default:
				int volume = 0;
				dimension--;
				for (int i = 1; i <= side; i++) {
					volume += getAnisotropicGridVolume(dimension, i);
				}
				return volume;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, long[] neighborValues, int[][] neighborCoords) {
		// TODO faster sorting algorithm?
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int j = neighborCount - 1, i = j - 1; i >= 0; i--, j--) {
				if (neighborValues[i] < neighborValues[j]) {
					sorted = false;
					long valSwap = neighborValues[i];
					neighborValues[i] = neighborValues[j];
					neighborValues[j] = valSwap;
					int[] coordSwap = neighborCoords[i];
					neighborCoords[i] = neighborCoords[j];
					neighborCoords[j] = coordSwap;
				}
			}
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, long[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		// TODO faster sorting algorithm?
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int j = neighborCount - 1, i = j - 1; i >= 0; i--, j--) {
				if (neighborValues[i] < neighborValues[j]) {
					sorted = false;
					long valSwap = neighborValues[i];
					neighborValues[i] = neighborValues[j];
					neighborValues[j] = valSwap;
					int[] coordSwap = neighborCoords[i];
					neighborCoords[i] = neighborCoords[j];
					neighborCoords[j] = coordSwap;
					int otherSwap = otherNeighborIntegerField[i];
					otherNeighborIntegerField[i] = otherNeighborIntegerField[j];
					otherNeighborIntegerField[j] = otherSwap;
				}
			}
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, long[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		// TODO faster sorting algorithm?
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int j = neighborCount - 1, i = j - 1; i >= 0; i--, j--) {
				if (neighborValues[i] < neighborValues[j]) {
					sorted = false;
					long valSwap = neighborValues[i];
					neighborValues[i] = neighborValues[j];
					neighborValues[j] = valSwap;
					int[] coordSwap = neighborCoords[i];
					neighborCoords[i] = neighborCoords[j];
					neighborCoords[j] = coordSwap;
					int multiplierSwap = neighborShareMultipliers[i];
					neighborShareMultipliers[i] = neighborShareMultipliers[j];
					neighborShareMultipliers[j] = multiplierSwap;
					int symmetrySwap = neighborSymmetryCounts[i];
					neighborSymmetryCounts[i] = neighborSymmetryCounts[j];
					neighborSymmetryCounts[j] = symmetrySwap;
				}
			}
		}
	}

	public static void sort4NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		// TODO faster sorting algorithm?
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 2; i >= 0; i--) {
				if (neighborValues[i] < neighborValues[i+1]) {
					sorted = false;
					long valSwap = neighborValues[i];
					neighborValues[i] = neighborValues[i+1];
					neighborValues[i+1] = valSwap;
					int[] coordSwap = neighborCoords[i];
					neighborCoords[i] = neighborCoords[i+1];
					neighborCoords[i+1] = coordSwap;
					int otherSwap = otherNeighborIntegerField[i];
					otherNeighborIntegerField[i] = otherNeighborIntegerField[i+1];
					otherNeighborIntegerField[i+1] = otherSwap;
				}
			}
		}
	}
	
	public static void sort4NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		// TODO faster sorting algorithm?
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 2, j = 3; i >= 0; i--, j--) {
				if (neighborValues[i] < neighborValues[j]) {
					sorted = false;
					long valSwap = neighborValues[i];
					neighborValues[i] = neighborValues[j];
					neighborValues[j] = valSwap;
					int[] coordSwap = neighborCoords[i];
					neighborCoords[i] = neighborCoords[j];
					neighborCoords[j] = coordSwap;
					int multiplierSwap = neighborShareMultipliers[i];
					neighborShareMultipliers[i] = neighborShareMultipliers[j];
					neighborShareMultipliers[j] = multiplierSwap;
					int symmetrySwap = neighborSymmetryCounts[i];
					neighborSymmetryCounts[i] = neighborSymmetryCounts[j];
					neighborSymmetryCounts[j] = symmetrySwap;
				}
			}
		}
	}
	
	public static void sort4NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords) {
		// TODO faster sorting algorithm?
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 2; i >= 0; i--) {
				if (neighborValues[i] < neighborValues[i+1]) {
					sorted = false;
					long valSwap = neighborValues[i];
					neighborValues[i] = neighborValues[i+1];
					neighborValues[i+1] = valSwap;
					int[] coordSwap = neighborCoords[i];
					neighborCoords[i] = neighborCoords[i+1];
					neighborCoords[i+1] = coordSwap;
				}
			}
		}
	}
	
	public static void sort3NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		long n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
		if (n0 >= n1) {
			if (n1 < n2) {
				if (n0 >= n2) { 
					// n0 >= n2 > n1
					neighborValues[1] = n2;
					neighborValues[2] = n1;
					int[] coordSwap = neighborCoords[1];
					neighborCoords[1] = neighborCoords[2];
					neighborCoords[2] = coordSwap;
					int otherSwap = otherNeighborIntegerField[1];
					otherNeighborIntegerField[1] = otherNeighborIntegerField[2];
					otherNeighborIntegerField[2] = otherSwap;
				} else {
					// n2 > n0 >= n1
					neighborValues[0] = n2;
					neighborValues[1] = n0;
					neighborValues[2] = n1;
					int[] auxCoord = neighborCoords[2];
					neighborCoords[2] = neighborCoords[1];
					neighborCoords[1] = neighborCoords[0];
					neighborCoords[0] = auxCoord;
					int auxOther = otherNeighborIntegerField[2];
					otherNeighborIntegerField[2] = otherNeighborIntegerField[1];
					otherNeighborIntegerField[1] = otherNeighborIntegerField[0];
					otherNeighborIntegerField[0] = auxOther;
				}
			}
		} else if (n1 >= n2) {
			if (n0 >= n2) {
				// n1 > n0 >= n2
				neighborValues[0] = n1;
				neighborValues[1] = n0;
				int[] coordSwap = neighborCoords[0];
				neighborCoords[0] = neighborCoords[1];
				neighborCoords[1] = coordSwap;
				int otherSwap = otherNeighborIntegerField[0];
				otherNeighborIntegerField[0] = otherNeighborIntegerField[1];
				otherNeighborIntegerField[1] = otherSwap;
			} else {
				// n1 >= n2 > n0
				neighborValues[0] = n1;
				neighborValues[1] = n2;
				neighborValues[2] = n0;
				int[] auxCoord = neighborCoords[0];
				neighborCoords[0] = neighborCoords[1];
				neighborCoords[1] = neighborCoords[2];
				neighborCoords[2] = auxCoord;
				int auxOther = otherNeighborIntegerField[0];
				otherNeighborIntegerField[0] = otherNeighborIntegerField[1];
				otherNeighborIntegerField[1] = otherNeighborIntegerField[2];
				otherNeighborIntegerField[2] = auxOther;
			}
		} else {
			// n2 > n1 > n0
			neighborValues[0] = n2;
			neighborValues[2] = n0;
			int[] coordSwap = neighborCoords[0];
			neighborCoords[0] = neighborCoords[2];
			neighborCoords[2] = coordSwap;
			int otherSwap = otherNeighborIntegerField[0];
			otherNeighborIntegerField[0] = otherNeighborIntegerField[2];
			otherNeighborIntegerField[2] = otherSwap;
		}
	}
	
	public static void sort3NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		long n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
		if (n0 >= n1) {
			if (n1 < n2) {
				if (n0 >= n2) { 
					// n0 >= n2 > n1
					neighborValues[1] = n2;
					neighborValues[2] = n1;
					int[] coordSwap = neighborCoords[1];
					neighborCoords[1] = neighborCoords[2];
					neighborCoords[2] = coordSwap;
					int multiplierSwap = neighborShareMultipliers[1];
					neighborShareMultipliers[1] = neighborShareMultipliers[2];
					neighborShareMultipliers[2] = multiplierSwap;
					int symmetrySwap = neighborSymmetryCounts[1];
					neighborSymmetryCounts[1] = neighborSymmetryCounts[2];
					neighborSymmetryCounts[2] = symmetrySwap;
				} else {
					// n2 > n0 >= n1
					neighborValues[0] = n2;
					neighborValues[1] = n0;
					neighborValues[2] = n1;
					int[] auxCoord = neighborCoords[2];
					neighborCoords[2] = neighborCoords[1];
					neighborCoords[1] = neighborCoords[0];
					neighborCoords[0] = auxCoord;
					int auxMultiplier = neighborShareMultipliers[2];
					neighborShareMultipliers[2] = neighborShareMultipliers[1];
					neighborShareMultipliers[1] = neighborShareMultipliers[0];
					neighborShareMultipliers[0] = auxMultiplier;
					int auxSymmetry = neighborSymmetryCounts[2];
					neighborSymmetryCounts[2] = neighborSymmetryCounts[1];
					neighborSymmetryCounts[1] = neighborSymmetryCounts[0];
					neighborSymmetryCounts[0] = auxSymmetry;
				}
			}
		} else if (n1 >= n2) {
			if (n0 >= n2) {
				// n1 > n0 >= n2
				neighborValues[0] = n1;
				neighborValues[1] = n0;
				int[] coordSwap = neighborCoords[0];
				neighborCoords[0] = neighborCoords[1];
				neighborCoords[1] = coordSwap;
				int multiplierSwap = neighborShareMultipliers[0];
				neighborShareMultipliers[0] = neighborShareMultipliers[1];
				neighborShareMultipliers[1] = multiplierSwap;
				int symmetrySwap = neighborSymmetryCounts[0];
				neighborSymmetryCounts[0] = neighborSymmetryCounts[1];
				neighborSymmetryCounts[1] = symmetrySwap;
			} else {
				// n1 >= n2 > n0
				neighborValues[0] = n1;
				neighborValues[1] = n2;
				neighborValues[2] = n0;
				int[] auxCoord = neighborCoords[0];
				neighborCoords[0] = neighborCoords[1];
				neighborCoords[1] = neighborCoords[2];
				neighborCoords[2] = auxCoord;
				int auxMultiplier = neighborShareMultipliers[0];
				neighborShareMultipliers[0] = neighborShareMultipliers[1];
				neighborShareMultipliers[1] = neighborShareMultipliers[2];
				neighborShareMultipliers[2] = auxMultiplier;
				int auxSymmetry = neighborSymmetryCounts[0];
				neighborSymmetryCounts[0] = neighborSymmetryCounts[1];
				neighborSymmetryCounts[1] = neighborSymmetryCounts[2];
				neighborSymmetryCounts[2] = auxSymmetry;
			}
		} else {
			// n2 > n1 > n0
			neighborValues[0] = n2;
			neighborValues[2] = n0;
			int[] coordSwap = neighborCoords[0];
			neighborCoords[0] = neighborCoords[2];
			neighborCoords[2] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[0];
			neighborShareMultipliers[0] = neighborShareMultipliers[2];
			neighborShareMultipliers[2] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[0];
			neighborSymmetryCounts[0] = neighborSymmetryCounts[2];
			neighborSymmetryCounts[2] = symmetrySwap;
		}
	}
	
	public static void sort3NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords) {
		long n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
		if (n0 >= n1) {
			if (n1 < n2) {
				if (n0 >= n2) { 
					// n0 >= n2 > n1
					neighborValues[1] = n2;
					neighborValues[2] = n1;
					int[] coordSwap = neighborCoords[1];
					neighborCoords[1] = neighborCoords[2];
					neighborCoords[2] = coordSwap;
				} else {
					// n2 > n0 >= n1
					neighborValues[0] = n2;
					neighborValues[1] = n0;
					neighborValues[2] = n1;
					int[] auxCoord = neighborCoords[2];
					neighborCoords[2] = neighborCoords[1];
					neighborCoords[1] = neighborCoords[0];
					neighborCoords[0] = auxCoord;
				}
			}
		} else if (n1 >= n2) {
			if (n0 >= n2) {
				// n1 > n0 >= n2
				neighborValues[0] = n1;
				neighborValues[1] = n0;
				int[] coordSwap = neighborCoords[0];
				neighborCoords[0] = neighborCoords[1];
				neighborCoords[1] = coordSwap;
			} else {
				// n1 >= n2 > n0
				neighborValues[0] = n1;
				neighborValues[1] = n2;
				neighborValues[2] = n0;
				int[] auxCoord = neighborCoords[0];
				neighborCoords[0] = neighborCoords[1];
				neighborCoords[1] = neighborCoords[2];
				neighborCoords[2] = auxCoord;
			}
		} else {
			// n2 > n1 > n0
			neighborValues[0] = n2;
			neighborValues[2] = n0;
			int[] coordSwap = neighborCoords[0];
			neighborCoords[0] = neighborCoords[2];
			neighborCoords[2] = coordSwap;
		}
	}

}
