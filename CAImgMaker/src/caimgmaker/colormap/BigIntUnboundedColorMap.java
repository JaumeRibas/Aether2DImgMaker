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
import java.math.BigInteger;

public class BigIntUnboundedColorMap implements BigIntBoundedColorMap {
	
	private BigInteger minValue;
	private BigInteger maxValue;
	private BigIntColorMap colorMap;
	private Color outOfLowerBoundColor;
	private Color outOfUpperBoundColor;
	
	public BigIntUnboundedColorMap(BigIntColorMap colorMap, BigInteger minValue, BigInteger maxValue, 
			Color outOfLowerBoundColor, Color outOfUpperBoundColor) {
		this.colorMap = colorMap;
		this.outOfLowerBoundColor = outOfLowerBoundColor;
		this.outOfUpperBoundColor = outOfUpperBoundColor;
		if (minValue.compareTo(maxValue) > 0) {
			BigInteger swap = minValue;
			minValue = maxValue;
			maxValue = swap;
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public Color getColor(BigInteger value) throws Exception {
		if (value.compareTo(minValue) < 0) {
			return outOfLowerBoundColor;
		} else if (value.compareTo(maxValue) > 0) {
			return outOfUpperBoundColor;
		} else {
			return colorMap.getColor(value);
		}
	}

	@Override
	public BigInteger getMaxValue() {
		return maxValue;
	}

	@Override
	public BigInteger getMinValue() {
		return minValue;
	}

}
