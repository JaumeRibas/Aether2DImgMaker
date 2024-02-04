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
package caimgmaker.colormap;

import java.awt.Color;
import java.math.BigDecimal;

import org.apache.commons.math3.fraction.BigFraction;

public class BigFractionGrayscaleMap implements BoundedColorMap<BigFraction> {
	
	private BigFraction minValue;
	private BigFraction maxValue;
	private BigFraction range;
	private float minBrightness;
	private BigFraction brightnessRange;
	
	public BigFractionGrayscaleMap(BigFraction minValue, BigFraction maxValue, int minBrightness) {
		if (minBrightness < 0 || minBrightness > 255) {
			throw new IllegalArgumentException("The minimum brightness is out of the [0, 255] range");
		}
		this.minBrightness = minBrightness;
		brightnessRange = new BigFraction(255-minBrightness);
		if (minValue.equals(maxValue)) {
			throw new IllegalArgumentException("Minumim and maximum values cannot be equal.");
		} else if (minValue.compareTo(maxValue) > 0) {
			BigFraction swap = minValue;
			minValue = maxValue;
			maxValue = swap;
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
		range = maxValue.subtract(minValue);
	}
	
	@Override
	public Color getColor(BigFraction value) throws IllegalArgumentException {
		if (value.compareTo(minValue) < 0 || value.compareTo(maxValue) > 0)
			throw new IllegalArgumentException("The value " + value + " is out of the [" + minValue + ", " + maxValue + "] range");
		@SuppressWarnings("deprecation")
		float brightness = (brightnessRange.multiply(value.subtract(minValue)).divide(range).bigDecimalValue(7, BigDecimal.ROUND_HALF_UP).floatValue() + minBrightness)/255;
		Color color = new Color(Color.HSBtoRGB(0, 0, brightness));
		return color;
	}

	@Override
	public BigFraction getMaxValue() {
		return maxValue;
	}

	@Override
	public BigFraction getMinValue() {
		return minValue;
	}

}
