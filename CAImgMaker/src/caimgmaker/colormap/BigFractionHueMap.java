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
package caimgmaker.colormap;

import java.awt.Color;
import org.apache.commons.math3.fraction.BigFraction;

public class BigFractionHueMap implements BoundedColorMap<BigFraction> {
	
	private BigFraction minValue;
	private BigFraction maxValue;
	private BigFraction range;
	private static final BigFraction HUE_RANGE = new BigFraction(220);
	private static final int HUE_MARGIN = new BigFraction(255).subtract(HUE_RANGE).intValue();
	
	public BigFractionHueMap(BigFraction minValue, BigFraction maxValue) {
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
	public BigFraction getMaxValue() {
		return maxValue;
	}

	@Override
	public BigFraction getMinValue() {
		return minValue;
	}

	
	@Override
	public Color getColor(BigFraction value) throws IllegalArgumentException {
		if (value.compareTo(minValue) < 0 || value.compareTo(maxValue) > 0)
			throw new IllegalArgumentException("The value " + value + " is out of the [" + minValue + ", " + maxValue + "] range");
		float hue = (HUE_RANGE.multiply(value.subtract(minValue)).divide(range).floatValue() + HUE_MARGIN)/255;
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
		return color;
	}
}
