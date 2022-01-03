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

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.ObjectModel2D;
import cellularautomata.numbers.BigInt;

public class HueMapper implements ColorMapper {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends FieldElement<T> & Comparable<T>> ObjectModel2D<Color> getMappedModel(NumericModel2D<T> grid, T minValue, T maxValue) {
		ColorMap<T> colorMap = null;
		if (minValue.equals(maxValue)) {
			colorMap = new SolidColorMap<T>(getEmptyColor());
		} else {
			if (minValue instanceof BigInt) {
				colorMap = (ColorMap<T>) new BigIntHueMap((BigInt)minValue, (BigInt)maxValue);
			}  else if (minValue instanceof BigFraction) {
				colorMap = (ColorMap<T>) new BigFractionHueMap((BigFraction)minValue, (BigFraction)maxValue);
			} else {
				throw new UnsupportedOperationException(
						"Missing " + ColorMap.class.getSimpleName() + "<"
								+ minValue.getClass().getSimpleName() + "> implementation for " 
								+ HueMapper.class.getSimpleName());
			} 
		}
		return new ColorMappedModel2D<T>(grid, colorMap);
	}
	
	@Override
	public ObjectModel2D<Color> getMappedModel(LongModel2D grid, long minValue, long maxValue) {
		LongColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(getEmptyColor());
		} else {
			colorMap = new LongHueMap(minValue, maxValue);
		}
		return new ColorMappedLongModel2D(grid, colorMap);
	}

	@Override
	public ObjectModel2D<Color> getMappedModel(IntModel2D grid, int minValue, int maxValue) {
		IntColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(getEmptyColor());
		} else {
			colorMap = new IntHueMap(minValue, maxValue);
		}
		return new ColorMappedIntModel2D(grid, colorMap);
	}
	
	private Color getEmptyColor() {
		float hue = (float)IntHueMap.HUE_MARGIN/255;
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		return new Color(Color.HSBtoRGB(hue, 1, 1));
	}

	@Override
	public String getColormapName() {
		return "Hue colormap";
	}

}
