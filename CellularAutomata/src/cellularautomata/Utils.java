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
package cellularautomata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.arrays.AnisotropicIntArray;
import cellularautomata.model1d.BooleanModel1D;
import cellularautomata.model1d.IntModel1D;
import cellularautomata.model1d.LongModel1D;
import cellularautomata.model1d.ObjectModel1D;
import cellularautomata.model2d.BooleanModel2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.Model2D;
import cellularautomata.model2d.ObjectModel2D;
import cellularautomata.numbers.BigInt;

public final class Utils {
	
	private Utils() {}
	
	public static boolean isEvenPosition(int[] coordinates) {
		boolean isEven = true;
		for (int i = 0; i != coordinates.length; i++) {
			isEven = isEven == (coordinates[i]%2 == 0);
		}
		return isEven;
	}
	
	public static boolean isEvenPosition(Coordinates coordinates) {
		boolean isEven = true;
		for (int i = coordinates.getCount() - 1; i != -1; i--) {
			isEven = isEven == (coordinates.get(i)%2 == 0);
		}
		return isEven;
	}
	
	public static int getRandomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	/**
	 * Adds a value to each element of an array
	 * 
	 * @param array
	 * @param value
	 * @return
	 */
	public static void addToArray(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			array[i] += value;
		}
	}
	
	public static void abs(int[] array) {
		for (int i = 0; i < array.length; i++) {
			int value = array[i]; 
			if (value < 0) {
				array[i] = -value;
			}
		}
	}
	
	public static void sortDescending(int[] array) {
		Arrays.sort(array);
		//reverse order
		int halfLength = array.length/2;
		for (int i = 0, j = array.length - 1; i < halfLength; i++, j--) {
			int swp = array[i];
			array[i] = array[j];
			array[j] = swp;
		}
	}
	
	public static boolean isSortedDescending(int[] array) {
		int lengthMinusOne = array.length - 1;
		for (int i = 0; i < lengthMinusOne; i++) {
			if (array[i] < array[i + 1]) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean areAllPositive(int[] values) {
		for (int i = 0; i < values.length; i++) {
			if (values[i] < 0) {
				return false;
			}
		}
		return true;
	}
	
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
	
	public static String getAxisLabel(int dimension, int axisIndex) {
		if (dimension < 3) {
			return Constants.LOWERCASE_ABC[axisIndex + 23];// letter 'x'
		} else if (dimension <= Constants.ABC_LENGTH) {
			return Constants.LOWERCASE_ABC[Constants.ABC_LENGTH - dimension + axisIndex];
		} else {
			return "x" + (axisIndex + 1);
		}
	}
	
	public static String getUpperCaseAxisLabel(int dimension, int axisIndex) {
		if (dimension < 3) {
			return Constants.UPPERCASE_ABC[axisIndex + 23];// letter 'x'
		} else if (dimension <= Constants.ABC_LENGTH) {
			return Constants.UPPERCASE_ABC[Constants.ABC_LENGTH - dimension + axisIndex];
		} else {
			return "X" + (axisIndex + 1);
		}
	}
	
	public static <Object_Type extends Comparable<Object_Type>> Object_Type max(Object_Type a, Object_Type b) {
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
			Arrays.fill(array, BigInt.ZERO);
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
			Arrays.fill(array, BigFraction.ZERO);
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
	
	public static AnisotropicIntArray[] buildAnisotropicNDIntArray(int dimension, int side) {
		AnisotropicIntArray[] anisotropicNDArray = new AnisotropicIntArray[side];
		int dimensionMinusOne = dimension - 1;
		for (int x1 = 0; x1 < anisotropicNDArray.length; x1++) {
			anisotropicNDArray[x1] = new AnisotropicIntArray(dimensionMinusOne, x1 + 1);
		}
		return anisotropicNDArray;
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
	
	public static boolean[][][][][] buildAnisotropic5DBooleanArray(int side) {
		boolean[][][][][] anisotropic5DArray = new boolean[side][][][][];
		for (int v = 0; v < anisotropic5DArray.length; v++) {
			anisotropic5DArray[v] = buildAnisotropic4DBooleanArray(v + 1);
		}
		return anisotropic5DArray;
	}
	
	public static boolean[][][][] buildAnisotropic4DBooleanArray(int side) {
		boolean[][][][] anisotropic4DArray = new boolean[side][][][];
		for (int w = 0; w < anisotropic4DArray.length; w++) {
			anisotropic4DArray[w] = buildAnisotropic3DBooleanArray(w + 1);
		}
		return anisotropic4DArray;
	}
	
	public static boolean[][][] buildAnisotropic3DBooleanArray(int side) {
		boolean[][][] anisotropic3DArray = new boolean[side][][];
		for (int x = 0; x < anisotropic3DArray.length; x++) {
			anisotropic3DArray[x] = buildAnisotropic2DBooleanArray(x + 1);
		}
		return anisotropic3DArray;
	}
	
	public static boolean[][] buildAnisotropic2DBooleanArray(int side) {
		boolean[][] anisotropic2DArray = new boolean[side][];
		for (int x = 0; x < anisotropic2DArray.length; x++) {
			anisotropic2DArray[x] = new boolean[x + 1];
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
	
	public static void fill(long[][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			Arrays.fill(array[i], value);
		}
	}
	
	public static void fill(long[][][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			fill(array[i], value);
		}
	}
	
	public static void fill(long[][][][] array, long value) {
		for (int i = 0; i < array.length; i++) {
			fill(array[i], value);
		}
	}
	
	public static void fill(int[][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			Arrays.fill(array[i], value);
		}
	}
	
	public static void fill(int[][][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			fill(array[i], value);
		}
	}
	
	public static void fill(int[][][][] array, int value) {
		for (int i = 0; i < array.length; i++) {
			fill(array[i], value);
		}
	}
	
	public static void fill(Object[][] array, Object value) {
		for (int i = 0; i < array.length; i++) {
			Arrays.fill(array[i], value);
		}
	}
	
	public static void fill(Object[][][] array, Object value) {
		for (int i = 0; i < array.length; i++) {
			fill(array[i], value);
		}
	}
	
	public static void fill(Object[][][][] array, Object value) {
		for (int i = 0; i < array.length; i++) {
			fill(array[i], value);
		}
	}
	
	public static void fillEvenIndexes(boolean[] array, boolean value) {
		for (int i = 0; i < array.length; i += 2) {
			array[i] = value;
		}
	}
	
	public static void fillOddIndexes(boolean[] array, boolean value) {
		for (int i = 1; i < array.length; i += 2) {
			array[i] = value;
		}
	}
	
	public static void fillEvenIndexes(boolean[][] array, boolean value) {
		int lengthMinusOne = array.length - 1;
		if (lengthMinusOne != -1) {
			int i = 0;
			for (; i < lengthMinusOne; i++) {
				fillEvenIndexes(array[i], value);
				i++;
				fillOddIndexes(array[i], value);
			}
			if (i == lengthMinusOne) {
				fillEvenIndexes(array[lengthMinusOne], value);
			}
		}
	}
	
	public static void fillOddIndexes(boolean[][] array, boolean value) {
		int lengthMinusOne = array.length - 1;
		if (lengthMinusOne != -1) {
			int i = 0;
			for (; i < lengthMinusOne; i++) {
				fillOddIndexes(array[i], value);
				i++;
				fillEvenIndexes(array[i], value);
			}
			if (i == lengthMinusOne) {
				fillOddIndexes(array[lengthMinusOne], value);
			}
		}
	}
	
	public static void fillEvenIndexes(boolean[][][] array, boolean value) {
		int lengthMinusOne = array.length - 1;
		if (lengthMinusOne != -1) {
			int i = 0;
			for (; i < lengthMinusOne; i++) {
				fillEvenIndexes(array[i], value);
				i++;
				fillOddIndexes(array[i], value);
			}
			if (i == lengthMinusOne) {
				fillEvenIndexes(array[lengthMinusOne], value);
			}
		}
	}
	
	public static void fillOddIndexes(boolean[][][] array, boolean value) {
		int lengthMinusOne = array.length - 1;
		if (lengthMinusOne != -1) {
			int i = 0;
			for (; i < lengthMinusOne; i++) {
				fillOddIndexes(array[i], value);
				i++;
				fillEvenIndexes(array[i], value);
			}
			if (i == lengthMinusOne) {
				fillOddIndexes(array[lengthMinusOne], value);
			}
		}
	}
	
	public static void fillEvenIndexes(boolean[][][][] array, boolean value) {
		int lengthMinusOne = array.length - 1;
		if (lengthMinusOne != -1) {
			int i = 0;
			for (; i < lengthMinusOne; i++) {
				fillEvenIndexes(array[i], value);
				i++;
				fillOddIndexes(array[i], value);
			}
			if (i == lengthMinusOne) {
				fillEvenIndexes(array[lengthMinusOne], value);
			}
		}
	}
	
	public static void fillOddIndexes(boolean[][][][] array, boolean value) {
		int lengthMinusOne = array.length - 1;
		if (lengthMinusOne != -1) {
			int i = 0;
			for (; i < lengthMinusOne; i++) {
				fillOddIndexes(array[i], value);
				i++;
				fillEvenIndexes(array[i], value);
			}
			if (i == lengthMinusOne) {
				fillOddIndexes(array[lengthMinusOne], value);
			}
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
	
	public static long getAnisotropic3DGridPositionCount(int side) {
		long count = 0;
		for (int i = 1; i <= side; i++) {
			count += (i*i-i)/2+i;
		}
		return count;
	}
	
	public static long getAnisotropic4DGridPositionCount(int side) {
		long count = 0;
		for (int i = 1; i <= side; i++) {
			count += getAnisotropic3DGridPositionCount(i);
		}
		return count;
	}
	
	public static long getAnisotropic5DGridPositionCount(int side) {
		long count = 0;
		for (int i = 1; i <= side; i++) {
			count += getAnisotropic4DGridPositionCount(i);
		}
		return count;
	}
	
	public static <Object_Type extends Comparable<Object_Type>> void sortDescending(int length, Object_Type[] array, int[] sortedIndexes) {
		for (int i = 0; i != length; i++) {
			sortedIndexes[i] = i;
		}
		int lengthMinusOne = length - 1;
		for (int i = 0; i < lengthMinusOne; i++) {
			Object_Type max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < length; j++) {
				Object_Type value = array[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			Object_Type valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}

	public static <Object_Type extends Comparable<Object_Type>> void sortDescendingLength4(Object_Type[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		sortedIndexes[3] = 3;
		for (int i = 0; i < 3; i++) {
			Object_Type max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
				Object_Type value = array[j];
				if (value.compareTo(max) > 0) {
					max = value;
					swapPosition = j;
				}
			}
			Object_Type valSwap = array[i];
			array[i] = array[swapPosition];
			array[swapPosition] = valSwap;
			int indexSwap = sortedIndexes[i];
			sortedIndexes[i] = sortedIndexes[swapPosition];
			sortedIndexes[swapPosition] = indexSwap;
		}
	}
	
	public static <Object_Type extends Comparable<Object_Type>> void sortDescendingLength3(Object_Type[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		Object_Type n0 = array[0], n1 = array[1], n2 = array[2];
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
		int lengthMinusOne = length - 1;
		for (int i = 0; i < lengthMinusOne; i++) {
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

	public static void sortDescendingLength4(long[] array, int[] sortedIndexes) {
		sortedIndexes[0] = 0;
		sortedIndexes[1] = 1;
		sortedIndexes[2] = 2;
		sortedIndexes[3] = 3;
		for (int i = 0; i < 3; i++) {
			long max = array[i];
			int swapPosition = i;
			for (int j = i + 1; j < 4; j++) {
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
		int lengthMinusOne = length - 1;
		for (int i = 0; i < lengthMinusOne; i++) {
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
		int lengthMinusOne = length - 1;
		for (int i = 0; i < lengthMinusOne; i++) {
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
	
	public static void printAsGrid(ObjectModel2D<?> grid) throws Exception {		
		int maxLength = 3;
		int maxY = grid.getMaxY();
		int minY = grid.getMinY();
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int y = maxY; y >= minY; y--) {
			int localMaxX = grid.getMaxX(y);
			for (int x = grid.getMinX(y); x <= localMaxX; x++) {
				int length = grid.getFromPosition(x, y).toString().length();
				if (length > maxLength)
					maxLength = length;
			}
		}
		String headFootGap = "";
		for (int i = 0; i < maxLength; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			int localMaxX = grid.getMaxX(y);
			int localMinX = grid.getMinX(y);
			int x = minX;
			for (; x < localMinX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxLength));
			}
			for (; x <= localMaxX; x++) {
				String strVal = grid.getFromPosition(x, y) + "";
				System.out.print("|" + padLeft(strVal, ' ', maxLength));
			}
			for (; x <= maxX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxLength));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(ObjectModel1D<?> grid) throws Exception {
		int maxLength = 3;
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int x = minX; x <= maxX; x++) {
			int length = grid.getFromPosition(x).toString().length();
			if (length > maxLength)
				maxLength = length;
		}
		String headFootGap = "";
		for (int i = 0; i < maxLength; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		System.out.println(headFoot);
		for (int x = minX; x <= maxX; x++) {
			String strVal = grid.getFromPosition(x) + "";
			System.out.print("|" + padLeft(strVal, ' ', maxLength));
		}
		System.out.println("|");
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(LongModel2D grid) throws Exception {
		int maxLength = 3;
		int maxY = grid.getMaxY();
		int minY = grid.getMinY();
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int y = maxY; y >= minY; y--) {
			int localMaxX = grid.getMaxX(y);
			for (int x = grid.getMinX(y); x <= localMaxX; x++) {
				int length = Long.toString(grid.getFromPosition(x, y)).length();
				if (length > maxLength)
					maxLength = length;
			}
		}
		String headFootGap = "";
		for (int i = 0; i < maxLength; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			int localMaxX = grid.getMaxX(y);
			int localMinX = grid.getMinX(y);
			int x = minX;
			for (; x < localMinX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxLength));
			}
			for (; x <= localMaxX; x++) {
				String strVal = grid.getFromPosition(x, y) + "";
				System.out.print("|" + padLeft(strVal, ' ', maxLength));
			}
			for (; x <= maxX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxLength));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(LongModel1D grid) throws Exception {
		int maxLength = 3;
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int x = minX; x <= maxX; x++) {
			int length = Long.toString(grid.getFromPosition(x)).length();
			if (length > maxLength)
				maxLength = length;
		}
		String headFootGap = "";
		for (int i = 0; i < maxLength; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		System.out.println(headFoot);
		for (int x = minX; x <= maxX; x++) {
			String strVal = grid.getFromPosition(x) + "";
			System.out.print("|" + padLeft(strVal, ' ', maxLength));
		}
		System.out.println("|");
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(IntModel2D grid) throws Exception {
		int maxLength = 3;
		int maxY = grid.getMaxY();
		int minY = grid.getMinY();
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int y = maxY; y >= minY; y--) {
			int localMaxX = grid.getMaxX(y);
			for (int x = grid.getMinX(y); x <= localMaxX; x++) {
				int length = Long.toString(grid.getFromPosition(x, y)).length();
				if (length > maxLength)
					maxLength = length;
			}
		}
		String headFootGap = "";
		for (int i = 0; i < maxLength; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			int localMaxX = grid.getMaxX(y);
			int localMinX = grid.getMinX(y);
			int x = minX;
			for (; x < localMinX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxLength));
			}
			for (; x <= localMaxX; x++) {
				String strVal = grid.getFromPosition(x, y) + "";
				System.out.print("|" + padLeft(strVal, ' ', maxLength));
			}
			for (; x <= maxX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxLength));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(IntModel1D grid) throws Exception {
		int maxLength = 3;
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int x = minX; x <= maxX; x++) {
			int length = Integer.toString(grid.getFromPosition(x)).length();
			if (length > maxLength)
				maxLength = length;
		}
		String headFootGap = "";
		for (int i = 0; i < maxLength; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		System.out.println(headFoot);
		for (int x = minX; x <= maxX; x++) {
			String strVal = grid.getFromPosition(x) + "";
			System.out.print("|" + padLeft(strVal, ' ', maxLength));
		}
		System.out.println("|");
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(BooleanModel2D grid) throws Exception {
		int maxY = grid.getMaxY();
		int minY = grid.getMinY();
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += "---+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			int localMaxX = grid.getMaxX(y);
			int localMinX = grid.getMinX(y);
			int x = minX;
			for (; x < localMinX; x++) {
				System.out.print("|   ");
			}
			for (; x <= localMaxX; x++) {
				String strVal = grid.getFromPosition(x, y) ? "###" : "   ";
				System.out.print("|" + strVal);
			}
			for (; x <= maxX; x++) {
				System.out.print("|   ");
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(BooleanModel1D grid) throws Exception {
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += "---+";
		}
		System.out.println(headFoot);
		for (int x = minX; x <= maxX; x++) {
			String strVal = grid.getFromPosition(x) ? "###" : "   ";
			System.out.print("|" + strVal);
		}
		System.out.println("|");
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(Model2D grid) throws Exception {
		int maxY = grid.getMaxY();
		int minY = grid.getMinY();
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += "---+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			int localMaxX = grid.getMaxX(y);
			int localMinX = grid.getMinX(y);
			int x = minX;
			for (; x < localMinX; x++) {
				System.out.print("|   ");
			}
			for (; x <= localMaxX; x++) {
				System.out.print("|###");
			}
			for (; x <= maxX; x++) {
				System.out.print("|   ");
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static String padLeft(String source, char c, int totalLength) {
		int margin = totalLength - source.length();
		for (int i = 0; i < margin; i++) {
			source = c + source;
		}
		return source;
	}
	
	/**
	 * <p>Returns a plain text {@link String} representing the number, or {@link null} if no {@link String} shorter or equal to {@link maxLength} can be obtained.</p>
	 * <p>The way this {@link String} is obtained is by initially representing the number in base 10. If the length is greater than {@link maxLength}, 
	 * then base equal to {@link Character#MAX_RADIX} is tested, appending a suffix to denote the base.</p>
	 * <p>The format of this suffix is that of a plain text representation of a subscript: "_{base}".</p>
	 * 
	 * @param number the number to be converted to {@link String}
	 * @param maxLength the length the resulting {@link String} cannot exceed
	 * @return a {@link String} representing the number or {@link null}
	 */
	public static String numberToPlainTextMaxLength(BigInt number, int maxLength) {
		String result = number.toString();
		if (result.length() > maxLength) {
			result = number.toString(Character.MAX_RADIX) + "_{" + Character.MAX_RADIX + "}";
			if (result.length() > maxLength) {
				result = null;
			}
		}
		return result;
	}
	
	public static String getFileNameSafeTimeStamp() {
		Calendar currentDate = Calendar.getInstance();
		int month = currentDate.get(Calendar.MONTH) + 1;
		int date = currentDate.get(Calendar.DATE);
		int hour = currentDate.get(Calendar.HOUR_OF_DAY);
		int minute = currentDate.get(Calendar.MINUTE);
		int second = currentDate.get(Calendar.SECOND);
		return currentDate.get(Calendar.YEAR) 
				+ "-" + (month > 9 ? month : "0" + month)
				+ "-" + (date > 9 ? date : "0" + date)
				+ "T" + (hour > 9 ? hour : "0" + hour)
				+ "" + (minute > 9 ? minute : "0" + minute)
				+ "" + (second > 9 ? second : "0" + second)
				+ "_" + currentDate.get(Calendar.MILLISECOND)
				+ "_" + ThreadLocalRandom.current().nextInt(0, 100);
	}
	
	public static boolean ifNull(Boolean value, boolean nullReplacement) {
		if (value == null) {
			return nullReplacement;
		}
		return value;
	}
	
	public static void fillWithRandomValues(int[] array, ThreadLocalRandom generator) {
		for (int i = array.length - 1; i != -1; i--) {
			array[i] = generator.nextInt();
		}
	}
	
	public static void fillWithRandomValues(int[][] array, ThreadLocalRandom generator) {
		for (int i = array.length - 1; i != -1; i--) {
			fillWithRandomValues(array[i], generator);
		}
	}
	
	public static void fillWithRandomValues(int[][][] array, ThreadLocalRandom generator) {
		for (int i = array.length - 1; i != -1; i--) {
			fillWithRandomValues(array[i], generator);
		}
	}
	
	public static void fillWithRandomValues(int[][][][] array, ThreadLocalRandom generator) {
		for (int i = array.length - 1; i != -1; i--) {
			fillWithRandomValues(array[i], generator);
		}
	}
	
	public static void fillWithRandomValues(int[][][][][] array, ThreadLocalRandom generator) {
		for (int i = array.length - 1; i != -1; i--) {
			fillWithRandomValues(array[i], generator);
		}
	}
	
	public static boolean contains(int[] array, int element) {
		for (int i = 0; i != array.length; i++) {
			if (array[i] == element) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isHyperrectangular(Object[][] array) {
		boolean result = false;
		if (array.length != 0) {
			int sideToCompare = array[0].length;
			result = true;
			for (int i = array.length - 1; i != 0 && result; i--) {
				if (array[i].length != sideToCompare) {
					result = false;
				}
			}
		}
		return result;
	}

}
