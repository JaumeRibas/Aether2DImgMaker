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

import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.ObjectModel2D;

public interface ColorMapper {
	
	<T extends FieldElement<T> & Comparable<T>> ObjectModel2D<Color> getMappedModel(NumericModel2D<T> grid, T minValue, T maxValue);
	
	ObjectModel2D<Color> getMappedModel(LongModel2D grid, long minValue, long maxValue);
	
	ObjectModel2D<Color> getMappedModel(IntModel2D grid, int minValue, int maxValue);
	
	/**
	 * Return the colormap's name in a format that can be used in file names
	 * 
	 * @return the name
	 */
	String getColormapName();
	
}
