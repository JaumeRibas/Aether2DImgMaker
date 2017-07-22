/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
    Copyright (C) 2017 Jaume Ribas

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
import java.math.BigInteger;
import java.math.MathContext;

public class BigIntegerHueMap implements BigIntegerColorMap {
	private BigDecimal hueIncreasePerUnit;
	private BigInteger minValue;
	private BigInteger maxValue;
	private Color[] colors;
	private static final BigInteger MAX_COLOR_COUNT = BigInteger.valueOf(100);
	private static final BigDecimal HUE_RANGE = new BigDecimal(220);
	private static final BigDecimal FULL_RANGE = new BigDecimal(255);
	private static final BigDecimal HUE_MARGIN = FULL_RANGE.subtract(HUE_RANGE);
	
	public BigIntegerHueMap(BigInteger min, BigInteger max) {
		this.minValue = min;
		this.maxValue = max;
		computeColors();
	}
	
	private void computeColors() {
		BigInteger lRange = maxValue.subtract(minValue).add(BigInteger.ONE);
		this.hueIncreasePerUnit = HUE_RANGE.divide(new BigDecimal(lRange), MathContext.DECIMAL128);
		if (lRange.compareTo(MAX_COLOR_COUNT) > 0) {
			colors = null;
			return;
		}
		int range = lRange.intValue();
		this.colors = new Color[range];
		int i = 0;
		if (range == 1) {
			colors[0] = computeColor(minValue);
		} else {
			for (BigInteger value = minValue; value.compareTo(maxValue) <= 0; value = value.add(BigInteger.ONE), i++) {
				colors[i] = computeColor(value);
			}
		}
	}
	
	private Color computeColor(BigInteger value) {
		float hue = new BigDecimal(value.subtract(minValue).add(BigInteger.ONE))
							.multiply(hueIncreasePerUnit).add(HUE_MARGIN)
							.divide(FULL_RANGE, MathContext.DECIMAL128).floatValue();
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
		return color;
	}
	
	@Override
	public Color getColor(BigInteger value) throws IllegalArgumentException {
		if (colors != null) {
			return colors[value.subtract(minValue).intValue()];
		} else {
			if (value.compareTo(minValue) < 0 || value.compareTo(maxValue) > 0) 
				throw new IllegalArgumentException("Value " + value + " outside range (" + minValue + "-" + maxValue + ")");
			return computeColor(value);
		}
	}
}
