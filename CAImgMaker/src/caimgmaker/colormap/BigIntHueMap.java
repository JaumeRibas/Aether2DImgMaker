/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
package caimgmaker.colormap;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

import cellularautomata.numbers.BigInt;

public class BigIntHueMap implements BoundedColorMap<BigInt> {
	
	private BigInt minValue;
	private BigInt maxValue;
	private BigDecimal range;
	private static final BigInt HUE_RANGE = BigInt.valueOf(220);
	private static final int HUE_MARGIN = BigInt.valueOf(255).subtract(HUE_RANGE).intValue();
	
	public BigIntHueMap(BigInt minValue, BigInt maxValue) {
		if (minValue.equals(maxValue)) {
			throw new IllegalArgumentException("Minumim and maximum values cannot be equal.");
		} else if (minValue.compareTo(maxValue) > 0) {
			BigInt swap = minValue;
			minValue = maxValue;
			maxValue = swap;
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
		range = new BigDecimal(maxValue.subtract(minValue).bigIntegerValue());
	}
	
	@Override
	public BigInt getMaxValue() {
		return maxValue;
	}

	@Override
	public BigInt getMinValue() {
		return minValue;
	}

	
	@Override
	public Color getColor(BigInt value) throws IllegalArgumentException {
		if (value.compareTo(minValue) < 0 || value.compareTo(maxValue) > 0)
			throw new IllegalArgumentException("The value " + value + " is out of the [" + minValue + ", " + maxValue + "] range");
		float hue = (new BigDecimal(HUE_RANGE.multiply(value.subtract(minValue)).bigIntegerValue())
				.divide(range, RoundingMode.HALF_UP).floatValue() + HUE_MARGIN)/255;
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
		return color;
	}
}
