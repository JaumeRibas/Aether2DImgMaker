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

import cellularautomata.numbers.BigInt;

public class AetherUtils {
	
	/**
	 * <p>Gives the maximum value difference between neighbors throughout the evolution of an Aether model with single source initial configuration.</p>
	 * <p>If the source value is larger or equal to zero, the maximum value difference between neighbors is equal to the source value</p>
	 * 
	 * @param gridDimension the dimension of the grid
	 * @param sourceValue the value of the single source initial configuration
	 * @return
	 */
	public static BigInt getMaxNeighboringValuesDifferenceFromSingleSource(int gridDimension, BigInt sourceValue) {
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

	public static BigInt getMinAllowedSingleSourceValue(int dimension, BigInt maxAllowedValue) {
		int maxAllowedValueComparedToZero = maxAllowedValue.compareTo(BigInt.ZERO);
		if (maxAllowedValueComparedToZero < 0) {
			throw new IllegalArgumentException("Max allowed cannot be less than zero.");
		} else if (maxAllowedValueComparedToZero == 0) {
			return BigInt.ZERO;
		}
		if (dimension <= 0) {
			throw new IllegalArgumentException("Grid dimension must be greater than zero.");
		} else if (dimension == 1) {
			return maxAllowedValue.negate();
		}
		BigInt two = BigInt.valueOf(2);
		BigInt doubleDimensionMinusOne = BigInt.valueOf(dimension).multiply(two).subtract(BigInt.ONE);
		if (maxAllowedValue.compareTo(doubleDimensionMinusOne) < 0) {
			return BigInt.ONE.negate();
		}
		/*  
		 0 -> 0
		-1 -> 1
		-2 -> 2*dimension - 1
		-3 -> previous - 1
		-4 -> previous + 2*dimension
		-5 -> previous - 1
		-6 -> previous + 2*dimension
		 ... 
		*/
		BigInt minSingleSourceValue1 = two.multiply(maxAllowedValue).divide(doubleDimensionMinusOne.negate());
		BigInt minSingleSourceValue2 = minSingleSourceValue1.subtract(BigInt.ONE);//it can be off by 1
		if (getMaxNeighboringValuesDifferenceFromSingleSource(dimension, minSingleSourceValue2).compareTo(maxAllowedValue) > 0) {
			return minSingleSourceValue1;
		} else {
			return minSingleSourceValue2;
		}
	}
}
