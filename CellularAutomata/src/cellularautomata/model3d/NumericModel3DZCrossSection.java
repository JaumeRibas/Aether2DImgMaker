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
package cellularautomata.model3d;

import org.apache.commons.math3.FieldElement;

import cellularautomata.model2d.NumericModel2D;

public class NumericModel3DZCrossSection<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> 
	extends ObjectModel3DZCrossSection<NumericModel3D<Number_Type>, Number_Type> implements NumericModel2D<Number_Type> {

	public NumericModel3DZCrossSection(NumericModel3D<Number_Type> source, int z) {
		super(source, z);
	}

}
