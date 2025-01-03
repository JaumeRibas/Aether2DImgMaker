/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
import org.apache.commons.math3.util.Decimal64;

import cellularautomata.model2d.BooleanModel2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.ObjectModel2D;
import cellularautomata.numbers.BigInt;

public class GrayscaleMapper implements ColorMapper {

	private int minBrightness;
	private Color outOfLowerBoundColor;
	private Color outOfUpperBoundColor;	
	
	public GrayscaleMapper(int minBrightness) {
		this.minBrightness = minBrightness;
	}
	
	public GrayscaleMapper(int minBrightness, Color outOfBoundsColor) {
		this.minBrightness = minBrightness;
		this.outOfLowerBoundColor = outOfBoundsColor;
		this.outOfUpperBoundColor = outOfBoundsColor;
	}
	
	public GrayscaleMapper(int minBrightness, Color outOfLowerBoundColor, Color outOfUpperBoundColor) {
		this.minBrightness = minBrightness;
		this.outOfLowerBoundColor = outOfLowerBoundColor;
		this.outOfUpperBoundColor = outOfUpperBoundColor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> ObjectModel2D<Color> getMappedModel(NumericModel2D<Number_Type> grid, Number_Type minValue, Number_Type maxValue) {
		ColorMap<Number_Type> colorMap = null;
		if (minValue.equals(maxValue)) {
			colorMap = new SolidColorMap<Number_Type>(new Color(0, 0, minBrightness/255));
		} else {
			if (minValue instanceof BigInt) {
				colorMap = (ColorMap<Number_Type>) new BigIntGrayscaleMap((BigInt)minValue, (BigInt)maxValue, minBrightness);
			}  else if (minValue instanceof BigFraction) {
				colorMap = (ColorMap<Number_Type>) new BigFractionGrayscaleMap((BigFraction)minValue, (BigFraction)maxValue, minBrightness);
			} else if (minValue instanceof Decimal64) {
				colorMap = (ColorMap<Number_Type>) new Decimal64GrayscaleMap((Decimal64)minValue, (Decimal64)maxValue, minBrightness);
			} else {
				throw new UnsupportedOperationException(
						"Missing " + ColorMap.class.getSimpleName() + "<"
								+ minValue.getClass().getSimpleName() + "> implementation for " 
								+ GrayscaleMapper.class.getSimpleName());
			} 
		}
		if (outOfLowerBoundColor != null) {
			colorMap = new UnboundedColorMap<Number_Type>(colorMap, minValue, maxValue, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedObjectGrid2D<Number_Type>(grid, colorMap);
	}

	@Override
	public ObjectModel2D<Color> getMappedModel(LongModel2D grid, long minValue, long maxValue) {
		LongColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(new Color(0, 0, minBrightness/255));
		} else {
			colorMap = new LongGrayscaleMap(minValue, maxValue, minBrightness);
		}
		if (outOfLowerBoundColor != null) {
			colorMap = new LongUnboundedColorMap(colorMap, minValue, maxValue, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedLongGrid2D(grid, colorMap);
	}

	@Override
	public ObjectModel2D<Color> getMappedModel(IntModel2D grid, int minValue, int maxValue) {
		IntColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(new Color(0, 0, minBrightness/255));
		} else {
			colorMap = new IntGrayscaleMap(minValue, maxValue, minBrightness);
		}
		if (outOfLowerBoundColor != null) {
			colorMap = new IntUnboundedColorMap(colorMap, minValue, maxValue, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedIntGrid2D(grid, colorMap);
	}

	@Override
	public ObjectModel2D<Color> getMappedModel(BooleanModel2D grid) {
		return new ColorMappedBooleanGrid2D(grid, new BooleanGrayscaleMap());
	}

	@Override
	public String getColormapName() {
		return "Grayscale";
	}

}
