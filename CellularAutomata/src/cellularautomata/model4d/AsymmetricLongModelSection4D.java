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
package cellularautomata.model4d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model3d.LongModel3D;

public class AsymmetricLongModelSection4D extends AsymmetricModelSection4D<SymmetricLongModel4D> implements LongModel4D {
	
	public AsymmetricLongModelSection4D(SymmetricLongModel4D grid) {
		super(grid);
	}

	@Override
	public long getFromPosition(int w, int x, int y, int z) throws Exception {
		return source.getFromAsymmetricPosition(w, x, y, z);
	}
	
	@Override
	public LongModel4D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return LongModel4D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public LongModel3D crossSection(int axis, int coordinate) {
		return LongModel4D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public LongModel3D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return LongModel4D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
