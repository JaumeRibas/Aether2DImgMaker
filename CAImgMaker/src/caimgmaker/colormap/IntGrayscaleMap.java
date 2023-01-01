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

public class IntGrayscaleMap implements IntBoundedColorMap {
	
	private int minValue;
	private int maxValue;
	private double range;
	private float minBrightness;
	private int brightnessRange;
	
	public IntGrayscaleMap(int minValue, int maxValue, int minBrightness) {
		if (minBrightness < 0 || minBrightness > 255) {
			throw new IllegalArgumentException("The minimum brightness is out of the [0, 255] range");
		}
		this.minBrightness = minBrightness;
		brightnessRange = 255-minBrightness;
		if (minValue == maxValue) {
			throw new IllegalArgumentException("Minumim and maximum values cannot be equal.");
		} else if (minValue > maxValue) {
			int swap = minValue;
			minValue = maxValue;
			maxValue = swap;
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
		range = maxValue - minValue;
	}
	
	@Override
	public Color getColor(int value) throws IllegalArgumentException {
		if (value < minValue || value > maxValue)
			throw new IllegalArgumentException("The value " + value + " is out of the [" + minValue + ", " + maxValue + "] range");
		float brightness = (float) ((brightnessRange * ((value - minValue)/range) + minBrightness)/255);
		Color color = new Color(Color.HSBtoRGB(0, 0, brightness));
		return color;
	}

	@Override
	public int getMaxValue() {
		return maxValue;
	}

	@Override
	public int getMinValue() {
		return minValue;
	}

}
