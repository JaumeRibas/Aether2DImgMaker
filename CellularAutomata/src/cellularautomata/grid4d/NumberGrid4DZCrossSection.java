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
package cellularautomata.grid4d;

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid3d.NumberGrid3D;

public class NumberGrid4DZCrossSection<T extends FieldElement<T> & Comparable<T>, G extends NumberGrid4D<T>> 
	extends ObjectGrid4DZCrossSection<T, G> implements NumberGrid3D<T> {

	public NumberGrid4DZCrossSection(G source, int z) {
		super(source, z);
	}
}
