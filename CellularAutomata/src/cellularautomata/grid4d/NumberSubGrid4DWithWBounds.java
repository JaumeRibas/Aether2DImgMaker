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

public class NumberSubGrid4DWithWBounds<T extends FieldElement<T> & Comparable<T>> extends SubGrid4DWithWBounds<NumberGrid4D<T>> 
	implements NumberGrid4D<T> {
	
	public NumberSubGrid4DWithWBounds(NumberGrid4D<T> source, int minW, int maxW) {
		super(source, minW, maxW);
	}

	@Override
	public T getFromPosition(int w, int x, int y, int z) throws Exception {
		return source.getFromPosition(w, x, y, z);
	}

}
