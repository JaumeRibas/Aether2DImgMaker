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
package cellularautomata2.arrays;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {
	
	public static boolean isEvenPosition(int[] coordinates) {
		int sum = 0;
		for (int i = 0; i < coordinates.length; i++) {
			sum += coordinates[i];
		}
		return sum%2 == 0;
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
	
	public static <T> void copyArrayWithoutElement(T[] sourceArray, T[] targetArray, int indexToSkip) {
		System.arraycopy(sourceArray, 0, targetArray, 0, indexToSkip);
		System.arraycopy(sourceArray, indexToSkip + 1, targetArray, indexToSkip, targetArray.length - indexToSkip);
	}
	
}
