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
package cellularautomata.automata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import cellularautomata.numbers.BigInt;

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
	
	public static <T extends Comparable<T>> T max(T a, T b) {
		if (a.compareTo(b) > 0)
			return a;
		return b;
	}
	
	/**
	 * <p>Gives the maximum value difference between neighbors throughout the evolution of an Aether model with single source initial configuration.</p>
	 * <p>If the source value is larger or equal to zero, the maximum value difference between neighbors is equal to the source value</p>
	 * 
	 * @param gridDimension the dimension of the grid
	 * @param sourceValue the value of the single source initial configuration
	 * @return
	 */
	public static BigInt getAetherMaxNeighboringValuesDifferenceFromSingleSource(int gridDimension, BigInt sourceValue) {
		if (gridDimension <= 0) {
			throw new IllegalArgumentException("Grid dimension must be greater than zero.");
		}
		if (sourceValue.compareTo(BigInt.ZERO) < 0) {
			if (gridDimension > 1) {
				BigInt two = BigInt.valueOf(2);
				return sourceValue.add(sourceValue.negate().divide(two).multiply(BigInt.valueOf(gridDimension).multiply(two).add(BigInt.ONE))).abs();
			} else {
				return sourceValue.negate();
			}
		} else {
			return sourceValue; 
		}
	}
	
	public static BigInt[][][][] buildAnisotropic4DBigIntArray(int side) {
		BigInt[][][][] anisotropic4DArray = new BigInt[side][][][];
		for (int w = 0; w < anisotropic4DArray.length; w++) {
			anisotropic4DArray[w] = buildAnisotropic3DBigIntArray(w + 1);
		}
		return anisotropic4DArray;
	}
	
	public static BigInt[][][] buildAnisotropic3DBigIntArray(int side) {
		BigInt[][][] anisotropic3DArray = new BigInt[side][][];
		for (int x = 0; x < anisotropic3DArray.length; x++) {
			anisotropic3DArray[x] = buildAnisotropic2DBigIntArray(x + 1);
		}
		return anisotropic3DArray;
	}
	
	public static BigInt[][] buildAnisotropic2DBigIntArray(int side) {
		BigInt[][] anisotropic2DArray = new BigInt[side][];
		for (int x = 0; x < anisotropic2DArray.length; x++) {
			int length = x + 1;
			BigInt[] array = new BigInt[length];
			for (int i = 0; i < length; i++) {
				array[i] = BigInt.ZERO;
			}
			anisotropic2DArray[x] = array;
		}
		return anisotropic2DArray;
	}
	
	public static BigFraction[][][][] buildAnisotropic4DBigFractionArray(int side) {
		BigFraction[][][][] anisotropic4DArray = new BigFraction[side][][][];
		for (int w = 0; w < anisotropic4DArray.length; w++) {
			anisotropic4DArray[w] = buildAnisotropic3DBigFractionArray(w + 1);
		}
		return anisotropic4DArray;
	}
	
	public static BigFraction[][][] buildAnisotropic3DBigFractionArray(int side) {
		BigFraction[][][] anisotropic3DArray = new BigFraction[side][][];
		for (int x = 0; x < anisotropic3DArray.length; x++) {
			anisotropic3DArray[x] = buildAnisotropic2DBigFractionArray(x + 1);
		}
		return anisotropic3DArray;
	}
	
	public static BigFraction[][] buildAnisotropic2DBigFractionArray(int side) {
		BigFraction[][] anisotropic2DArray = new BigFraction[side][];
		for (int x = 0; x < anisotropic2DArray.length; x++) {
			int length = x + 1;
			BigFraction[] array = new BigFraction[length];
			for (int i = 0; i < length; i++) {
				array[i] = BigFraction.ZERO;
			}
			anisotropic2DArray[x] = array;
		}
		return anisotropic2DArray;
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
	
	public static double[][][][] buildAnisotropic4DDoubleArray(int side) {
		double[][][][] anisotropic4DArray = new double[side][][][];
		for (int w = 0; w < anisotropic4DArray.length; w++) {
			anisotropic4DArray[w] = buildAnisotropic3DDoubleArray(w + 1);
		}
		return anisotropic4DArray;
	}
	
	public static double[][][] buildAnisotropic3DDoubleArray(int side) {
		double[][][] anisotropic3DArray = new double[side][][];
		for (int x = 0; x < anisotropic3DArray.length; x++) {
			anisotropic3DArray[x] = buildAnisotropic2DDoubleArray(x + 1);
		}
		return anisotropic3DArray;
	}
	
	public static double[][] buildAnisotropic2DDoubleArray(int side) {
		double[][] anisotropic2DArray = new double[side][];
		for (int x = 0; x < anisotropic2DArray.length; x++) {
			anisotropic2DArray[x] = new double[x + 1];
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
		long remainder = value % 8;
		if (remainder > 0) {
			value += 8 - remainder;
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
	
	public static <T extends Comparable<T>> void sortNeighborsByValueDesc(int neighborCount, T[] neighborValues, int[][] neighborCoords) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			T max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				T value = neighborValues[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			T valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static <T extends Comparable<T>> void sortNeighborsByValueDesc(int neighborCount, T[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			T max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				T value = neighborValues[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			T valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static <T extends Comparable<T>> void sortNeighborsByValueDesc(int neighborCount, T[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			T max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				T value = neighborValues[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			T valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}

	public static void sort4NeighborsByValueDesc(BigInteger[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		for (int i = 0; i < 3; i++) {
			BigInteger max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				BigInteger value = neighborValues[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			BigInteger valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(BigInteger[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		for (int i = 0; i < 3; i++) {
			BigInteger max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				BigInteger value = neighborValues[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			BigInteger valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(BigInteger[] neighborValues, int[][] neighborCoords) {
		for (int i = 0; i < 3; i++) {
			BigInteger max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				BigInteger value = neighborValues[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			BigInteger valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static <T extends Comparable<T>> void sort3NeighborsByValueDesc(T[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		T n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
		if (n0.compareTo(n1) >= 0) {
			if (n1.compareTo(n2) < 0) {
				if (n0.compareTo(n2) >= 0) { 
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
		} else if (n1.compareTo(n2) >= 0) {
			if (n0.compareTo(n2) >= 0) {
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
	
	public static <T extends Comparable<T>> void sort3NeighborsByValueDesc(T[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		T n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
		if (n0.compareTo(n1) >= 0) {
			if (n1.compareTo(n2) < 0) {
				if (n0.compareTo(n2) >= 0) { 
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
		} else if (n1.compareTo(n2) >= 0) {
			if (n0.compareTo(n2) >= 0) {
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
	
	public static <T extends Comparable<T>> void sort3NeighborsByValueDesc(T[] neighborValues, int[][] neighborCoords) {
		T n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
		if (n0.compareTo(n1) >= 0) {
			if (n1.compareTo(n2) < 0) {
				if (n0.compareTo(n2) >= 0) { 
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
		} else if (n1.compareTo(n2) >= 0) {
			if (n0.compareTo(n2) >= 0) {
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
	
	public static void sortNeighborsByValueDesc(int neighborCount, long[] neighborValues, int[][] neighborCoords) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			long max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				long value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			long valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, long[] neighborValues, byte[] neighborDirections) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			long max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				long value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			long valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			byte directionSwap = neighborDirections[i];
			neighborDirections[i] = neighborDirections[swapPosition];
			neighborDirections[swapPosition] = directionSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, BigInt[] neighborValues, byte[] neighborDirections) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			BigInt max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				BigInt value = neighborValues[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			BigInt valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			byte directionSwap = neighborDirections[i];
			neighborDirections[i] = neighborDirections[swapPosition];
			neighborDirections[swapPosition] = directionSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, short[] neighborValues, byte[] neighborDirections) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			short max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				short value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			short valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			byte directionSwap = neighborDirections[i];
			neighborDirections[i] = neighborDirections[swapPosition];
			neighborDirections[swapPosition] = directionSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, long[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			long max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				long value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			long valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, long[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			long max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				long value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			long valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}

	public static void sort4NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		for (int i = 0; i < 3; i++) {
			long max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				long value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			long valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		for (int i = 0; i < 3; i++) {
			long max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				long value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			long valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(long[] neighborValues, int[][] neighborCoords) {
		for (int i = 0; i < 3; i++) {
			long max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				long value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			long valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
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
	
	public static void sortNeighborsByValueDesc(int neighborCount, double[] neighborValues, int[][] neighborCoords) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			double max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				double value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			double valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, double[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			double max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				double value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			double valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, double[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			double max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				double value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			double valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}

	public static void sort4NeighborsByValueDesc(double[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		for (int i = 0; i < 3; i++) {
			double max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				double value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			double valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(double[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		for (int i = 0; i < 3; i++) {
			double max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				double value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			double valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(double[] neighborValues, int[][] neighborCoords) {
		for (int i = 0; i < 3; i++) {
			double max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				double value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			double valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static void sort3NeighborsByValueDesc(double[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		double n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
	
	public static void sort3NeighborsByValueDesc(double[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		double n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
	
	public static void sort3NeighborsByValueDesc(double[] neighborValues, int[][] neighborCoords) {
		double n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
	
	public static void sortNeighborsByValueDesc(int neighborCount, int[] neighborValues, int[][] neighborCoords) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			int max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				int value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, int[] neighborValues, byte[] neighborDirections) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			int max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				int value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			byte directionSwap = neighborDirections[i];
			neighborDirections[i] = neighborDirections[swapPosition];
			neighborDirections[swapPosition] = directionSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, int[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			int max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				int value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, int[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			int max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				int value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}

	public static void sort4NeighborsByValueDesc(int[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		for (int i = 0; i < 3; i++) {
			int max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				int value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(int[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		for (int i = 0; i < 3; i++) {
			int max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				int value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(int[] neighborValues, int[][] neighborCoords) {
		for (int i = 0; i < 3; i++) {
			int max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				int value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static void sort3NeighborsByValueDesc(int[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		int n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
	
	public static void sort3NeighborsByValueDesc(int[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		int n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
	
	public static void sort3NeighborsByValueDesc(int[] neighborValues, int[][] neighborCoords) {
		int n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
	
	
	public static void sortNeighborsByValueDesc(int neighborCount, short[] neighborValues, int[][] neighborCoords) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			short max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				short value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			short valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, short[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			short max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				short value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			short valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sortNeighborsByValueDesc(int neighborCount, short[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		int neighborCountMinusOne = neighborCount - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			short max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < neighborCount; j++) {
				short value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			short valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}

	public static void sort4NeighborsByValueDesc(short[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		for (int i = 0; i < 3; i++) {
			short max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				short value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			short valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int otherSwap = otherNeighborIntegerField[i];
			otherNeighborIntegerField[i] = otherNeighborIntegerField[swapPosition];
			otherNeighborIntegerField[swapPosition] = otherSwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(short[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		for (int i = 0; i < 3; i++) {
			short max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				short value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			short valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
			int multiplierSwap = neighborShareMultipliers[i];
			neighborShareMultipliers[i] = neighborShareMultipliers[swapPosition];
			neighborShareMultipliers[swapPosition] = multiplierSwap;
			int symmetrySwap = neighborSymmetryCounts[i];
			neighborSymmetryCounts[i] = neighborSymmetryCounts[swapPosition];
			neighborSymmetryCounts[swapPosition] = symmetrySwap;
		}
	}
	
	public static void sort4NeighborsByValueDesc(short[] neighborValues, int[][] neighborCoords) {
		for (int i = 0; i < 3; i++) {
			short max = neighborValues[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				short value = neighborValues[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			short valSwap = neighborValues[i];
			neighborValues[i] = neighborValues[swapPosition];
			neighborValues[swapPosition] = valSwap;
			int[] coordSwap = neighborCoords[i];
			neighborCoords[i] = neighborCoords[swapPosition];
			neighborCoords[swapPosition] = coordSwap;
		}
	}
	
	public static void sort3NeighborsByValueDesc(short[] neighborValues, int[][] neighborCoords,
			int[] otherNeighborIntegerField) {
		short n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
	
	public static void sort3NeighborsByValueDesc(short[] neighborValues, int[][] neighborCoords,
			int[] neighborShareMultipliers, int[] neighborSymmetryCounts) {
		short n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
	
	public static void sort3NeighborsByValueDesc(short[] neighborValues, int[][] neighborCoords) {
		short n0 = neighborValues[0], n1 = neighborValues[1], n2 = neighborValues[2];
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
