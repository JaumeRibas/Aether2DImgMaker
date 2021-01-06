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
package cellularautomata2.arrays;

import java.util.Arrays;
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
}
