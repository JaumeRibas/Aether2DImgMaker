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

import cellularautomata.model2d.BooleanModel2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.ObjectModel2D;

public interface ColorMapper {
	
	<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> ObjectModel2D<Color> getMappedModel(NumericModel2D<Number_Type> grid, Number_Type minValue, Number_Type maxValue);
	
	ObjectModel2D<Color> getMappedModel(LongModel2D grid, long minValue, long maxValue);
	
	ObjectModel2D<Color> getMappedModel(IntModel2D grid, int minValue, int maxValue);
	
	ObjectModel2D<Color> getMappedModel(BooleanModel2D grid);
	
	/**
	 * Return the colormap's name in a format that can be used in file names
	 * 
	 * @return the name
	 */
	String getColormapName();
	
}
