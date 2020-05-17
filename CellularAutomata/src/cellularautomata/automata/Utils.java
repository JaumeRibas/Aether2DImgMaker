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
			BigInteger two = BigInteger.valueOf(2);
			return sourceValue.add(sourceValue.negate().divide(two).multiply(BigInteger.valueOf(gridDimension).multiply(two).add(BigInteger.ONE))).abs();
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

}
