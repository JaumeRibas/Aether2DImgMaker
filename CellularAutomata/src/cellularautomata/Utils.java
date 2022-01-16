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
package cellularautomata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

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
	
	public static char getAxisLetterFromIndex(int dimension, int axisIndex) {
		if (dimension < 3) {
			return (char) (axisIndex + 120);//120 letter 'x'
		} else {
			return (char) (122 - dimension + axisIndex  + 1);//122 letter 'z'
		}
	}
	
	public static char getUpperCaseAxisLetterFromIndex(int dimension, int axisIndex) {
		return Character.toUpperCase(getAxisLetterFromIndex(dimension, axisIndex));
	}
	
	public static <T extends Comparable<T>> T max(T a, T b) {
		if (a.compareTo(b) > 0)
			return a;
		return b;
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
	
	public static long[][][][][] buildAnisotropic5DLongArray(int side) {
		long[][][][][] anisotropic5DArray = new long[side][][][][];
		for (int v = 0; v < anisotropic5DArray.length; v++) {
			anisotropic5DArray[v] = buildAnisotropic4DLongArray(v + 1);
		}
		return anisotropic5DArray;
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
	
	public static int[][][][][] buildAnisotropic5DIntArray(int side) {
		int[][][][][] anisotropic5DArray = new int[side][][][][];
		for (int v = 0; v < anisotropic5DArray.length; v++) {
			anisotropic5DArray[v] = buildAnisotropic4DIntArray(v + 1);
		}
		return anisotropic5DArray;
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
	
	public static void fillArray(long[][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			Arrays.fill(array[i], value);
		}
	}
	
	public static void fillArray(long[][][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			fillArray(array[i], value);
		}
	}
	
	public static void fillArray(long[][][][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			fillArray(array[i], value);
		}
	}
	
	public static void fillArray(int[][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			Arrays.fill(array[i], value);
		}
	}
	
	public static void fillArray(int[][][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			fillArray(array[i], value);
		}
	}
	
	public static void fillArray(int[][][][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			fillArray(array[i], value);
		}
	}
	
	public static long roundUpToEightMultiple(long value) {
		long remainder = value % 8;
		if (remainder > 0) {
			value += 8 - remainder;
		}
		return value;
	}
	
	public static long getAnisotropicGridPositionCount(int dimension, int side) {
		switch (dimension) {
			case 0:
				return 1;
			case 1://this case could be omitted
				return side;
			default:
				long count = 0;
				dimension--;
				for (int i = 1; i <= side; i++) {
					count += getAnisotropicGridPositionCount(dimension, i);
				}
				return count;
		}
	}
	
	public static <T extends Comparable<T>> void sortDescending(int length, T[] array, int[] sortedIndexes) {
		for (int i = 0; i != length; i++) {
			sortedIndexes[i] = i;
		}
		int neighborCountMinusOne = length - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			T max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < length; j++) {
				T value = array[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			T valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}

	public static void sortDescendingLength4(BigInteger[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		sortedIndexes[3] = 3;
		for (int i = 0; i < 3; i++) {
			BigInteger max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				BigInteger value = array[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			BigInteger valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}
	
	public static <T extends Comparable<T>> void sortDescendingLength3(T[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		T n0 = array[0], n1 = array[1], n2 = array[2];
		if (n0.compareTo(n1) >= 0) {
			if (n1.compareTo(n2) < 0) {
				if (n0.compareTo(n2) >= 0) { 
					// n0 >= n2 > n1
					array[1] = n2;
					array[2] = n1;
					int indexSwap = sortedIndexes[1];
					sortedIndexes[1] = sortedIndexes[2];
					sortedIndexes[2] = indexSwap;
				} else {
					// n2 > n0 >= n1
					array[0] = n2;
					array[1] = n0;
					array[2] = n1;
					int auxIndex = sortedIndexes[2];
					sortedIndexes[2] = sortedIndexes[1];
					sortedIndexes[1] = sortedIndexes[0];
					sortedIndexes[0] = auxIndex;
				}
			}
		} else if (n1.compareTo(n2) >= 0) {
			if (n0.compareTo(n2) >= 0) {
				// n1 > n0 >= n2
				array[0] = n1;
				array[1] = n0;
				int indexSwap = sortedIndexes[0];
				sortedIndexes[0] = sortedIndexes[1];
				sortedIndexes[1] = indexSwap;
			} else {
				// n1 >= n2 > n0
				array[0] = n1;
				array[1] = n2;
				array[2] = n0;
				int auxIndex = sortedIndexes[0];
				sortedIndexes[0] = sortedIndexes[1];
				sortedIndexes[1] = sortedIndexes[2];
				sortedIndexes[2] = auxIndex;
			}
		} else {
			// n2 > n1 > n0
			array[0] = n2;
			array[2] = n0;
			int indexSwap = sortedIndexes[0];
			sortedIndexes[0] = sortedIndexes[2];
			sortedIndexes[2] = indexSwap;
		}
	}
	
	public static void sortDescending(int length, long[] array, int[] sortedIndexes) {
		for (int i = 0; i != length; i++) {
			sortedIndexes[i] = i;
		}
		int neighborCountMinusOne = length - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			long max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < length; j++) {
				long value = array[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			long valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}
	
	public static void sortDescending(int length, BigInt[] array, int[] sortedIndexes) {
		for (int i = 0; i != length; i++) {
			sortedIndexes[i] = i;
		}
		int neighborCountMinusOne = length - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			BigInt max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < length; j++) {
				BigInt value = array[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			BigInt valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}

	public static void sortDescendingLength4(long[] neighborValues, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		sortedIndexes[3] = 3;
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
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}
	
	public static void sortDescendingLength3(long[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		long n0 = array[0], n1 = array[1], n2 = array[2];
		if (n0 >= n1) {
			if (n1 < n2) {
				if (n0 >= n2) { 
					// n0 >= n2 > n1
					array[1] = n2;
					array[2] = n1;
					int indexSwap = sortedIndexes[1];
					sortedIndexes[1] = sortedIndexes[2];
					sortedIndexes[2] = indexSwap;
				} else {
					// n2 > n0 >= n1
					array[0] = n2;
					array[1] = n0;
					array[2] = n1;
					int auxIndex = sortedIndexes[2];
					sortedIndexes[2] = sortedIndexes[1];
					sortedIndexes[1] = sortedIndexes[0];
					sortedIndexes[0] = auxIndex;
				}
			}
		} else if (n1 >= n2) {
			if (n0 >= n2) {
				// n1 > n0 >= n2
				array[0] = n1;
				array[1] = n0;
				int indexSwap = sortedIndexes[0];
				sortedIndexes[0] = sortedIndexes[1];
				sortedIndexes[1] = indexSwap;
			} else {
				// n1 >= n2 > n0
				array[0] = n1;
				array[1] = n2;
				array[2] = n0;
				int auxIndex = sortedIndexes[0];
				sortedIndexes[0] = sortedIndexes[1];
				sortedIndexes[1] = sortedIndexes[2];
				sortedIndexes[2] = auxIndex;
			}
		} else {
			// n2 > n1 > n0
			array[0] = n2;
			array[2] = n0;
			int indexSwap = sortedIndexes[0];
			sortedIndexes[0] = sortedIndexes[2];
			sortedIndexes[2] = indexSwap;
		}
	}
	
	public static void sortDescending(int length, double[] array, int[] sortedIndexes) {
		for (int i = 0; i != length; i++) {
			sortedIndexes[i] = i;
		}
		int neighborCountMinusOne = length - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			double max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < length; j++) {
				double value = array[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			double valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}
	
	public static void sortDescendingLength4(double[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		sortedIndexes[3] = 3;
		for (int i = 0; i < 3; i++) {
			double max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				double value = array[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			double valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}
	
	public static void sortDescendingLength3(double[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		double n0 = array[0], n1 = array[1], n2 = array[2];
		if (n0 >= n1) {
			if (n1 < n2) {
				if (n0 >= n2) { 
					// n0 >= n2 > n1
					array[1] = n2;
					array[2] = n1;
					int indexSwap = sortedIndexes[1];
					sortedIndexes[1] = sortedIndexes[2];
					sortedIndexes[2] = indexSwap;
				} else {
					// n2 > n0 >= n1
					array[0] = n2;
					array[1] = n0;
					array[2] = n1;
					int auxIndex = sortedIndexes[2];
					sortedIndexes[2] = sortedIndexes[1];
					sortedIndexes[1] = sortedIndexes[0];
					sortedIndexes[0] = auxIndex;
				}
			}
		} else if (n1 >= n2) {
			if (n0 >= n2) {
				// n1 > n0 >= n2
				array[0] = n1;
				array[1] = n0;
				int indexSwap = sortedIndexes[0];
				sortedIndexes[0] = sortedIndexes[1];
				sortedIndexes[1] = indexSwap;
			} else {
				// n1 >= n2 > n0
				array[0] = n1;
				array[1] = n2;
				array[2] = n0;
				int auxIndex = sortedIndexes[0];
				sortedIndexes[0] = sortedIndexes[1];
				sortedIndexes[1] = sortedIndexes[2];
				sortedIndexes[2] = auxIndex;
			}
		} else {
			// n2 > n1 > n0
			array[0] = n2;
			array[2] = n0;
			int indexSwap = sortedIndexes[0];
			sortedIndexes[0] = sortedIndexes[2];
			sortedIndexes[2] = indexSwap;
		}
	}
	
	public static void sortDescending(int length, int[] array, int[] sortedIndexes) {
		for (int i = 0; i != length; i++) {
			sortedIndexes[i] = i;
		}
		int neighborCountMinusOne = length - 1;
		for (int i = 0; i < neighborCountMinusOne; i++) {
			int max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < length; j++) {
				int value = array[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}
	
	public static void sortDescendingLength4(int[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		sortedIndexes[3] = 3;
		for (int i = 0; i < 3; i++) {
			int max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				int value = array[j];
				if (value > max) {
					max = value;
					swapPosition = j;
				}
			}
			int aSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = aSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}
	
	public static void sortDescendingLength3(int[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		int n0 = array[0], n1 = array[1], n2 = array[2];
		if (n0 >= n1) {
			if (n1 < n2) {
				if (n0 >= n2) { 
					// n0 >= n2 > n1
					array[1] = n2;
					array[2] = n1;
					int indexSwap = sortedIndexes[1];
					sortedIndexes[1] = sortedIndexes[2];
					sortedIndexes[2] = indexSwap;
				} else {
					// n2 > n0 >= n1
					array[0] = n2;
					array[1] = n0;
					array[2] = n1;
					int auxIndex = sortedIndexes[2];
					sortedIndexes[2] = sortedIndexes[1];
					sortedIndexes[1] = sortedIndexes[0];
					sortedIndexes[0] = auxIndex;
				}
			}
		} else if (n1 >= n2) {
			if (n0 >= n2) {
				// n1 > n0 >= n2
				array[0] = n1;
				array[1] = n0;
				int indexSwap = sortedIndexes[0];
				sortedIndexes[0] = sortedIndexes[1];
				sortedIndexes[1] = indexSwap;
			} else {
				// n1 >= n2 > n0
				array[0] = n1;
				array[1] = n2;
				array[2] = n0;
				int auxIndex = sortedIndexes[0];
				sortedIndexes[0] = sortedIndexes[1];
				sortedIndexes[1] = sortedIndexes[2];
				sortedIndexes[2] = auxIndex;
			}
		} else {
			// n2 > n1 > n0
			array[0] = n2;
			array[2] = n0;
			int indexSwap = sortedIndexes[0];
			sortedIndexes[0] = sortedIndexes[2];
			sortedIndexes[2] = indexSwap;
		}
	}

}
