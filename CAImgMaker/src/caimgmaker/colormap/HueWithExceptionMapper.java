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
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.model2d.BooleanModel2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.ObjectModel2D;
import cellularautomata.numbers.BigInt;

public class HueWithExceptionMapper implements ColorMapper {

	private Object exceptionValue;
	private Color exceptionColor;
	
	public HueWithExceptionMapper(Object exceptionValue, Color exceptionColor) {
		this.exceptionValue = exceptionValue;
		this.exceptionColor = exceptionColor;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> ObjectModel2D<Color> getMappedModel(NumericModel2D<Number_Type> grid, Number_Type minValue, Number_Type maxValue) {
		ColorMap<Number_Type> colorMap = null;
		if (minValue.equals(maxValue)) {
			colorMap = new SolidColorMap<Number_Type>(getEmptyColor());
		} else {
			if (minValue instanceof BigInt) {
				colorMap = (ColorMap<Number_Type>) new BigIntHueMap((BigInt)minValue, (BigInt)maxValue);
			}  else if (minValue instanceof BigFraction) {
				colorMap = (ColorMap<Number_Type>) new BigFractionHueMap((BigFraction)minValue, (BigFraction)maxValue);
			} else {
				throw new UnsupportedOperationException(
						"Missing " + ColorMap.class.getSimpleName() + "<"
								+ minValue.getClass().getSimpleName() + "> implementation for " 
								+ HueWithExceptionMapper.class.getSimpleName());
			} 
		}
		return new ColorMappedObjectGrid2DWithException<Number_Type>(grid, colorMap, (Number_Type) exceptionValue, exceptionColor);
	}

	@Override
	public ObjectModel2D<Color> getMappedModel(LongModel2D grid, long minValue, long maxValue) {
		LongColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(getEmptyColor());
		} else {
			colorMap = new LongHueMap(minValue, maxValue);
		}
		return new ColorMappedLongGrid2DWithException(grid, colorMap, (Long)exceptionValue, exceptionColor);
	}

	@Override
	public ObjectModel2D<Color> getMappedModel(IntModel2D grid, int minValue, int maxValue) {
		IntColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(getEmptyColor());
		} else {
			colorMap = new IntHueMap(minValue, maxValue);
		}
		return new ColorMappedIntGrid2DWithException(grid, colorMap, (Integer)exceptionValue, exceptionColor);
	}

	@Override
	public ObjectModel2D<Color> getMappedModel(BooleanModel2D grid) {
		return new ColorMappedBooleanGrid2DWithException(grid, new BooleanHueMap(), (Boolean)exceptionValue, exceptionColor);
	}
	
	private Color getEmptyColor() {
		float hue = (float)IntHueMap.HUE_MARGIN/255;
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		return new Color(Color.HSBtoRGB(hue, 1, 1));
	}

	@Override
	public String getColormapName() {
		return "Hue colormap with exception (" + exceptionValue + "->" + exceptionColor + ")";
	}

}
