/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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

import cellularautomata.PartialCoordinates;
import cellularautomata.model2d.LongModel2D;

public class AsymmetricLongModelSection3D extends AsymmetricModelSection3D<SymmetricLongModel3D> implements LongModel3D {
	
	public AsymmetricLongModelSection3D(SymmetricLongModel3D source) {
		super(source);
	}

	@Override
	public long getFromPosition(int x, int y, int z) throws Exception {
		return source.getFromAsymmetricPosition(x, y, z);
	}
	
	@Override
	public LongModel3D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return LongModel3D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public LongModel2D crossSection(int axis, int coordinate) {
		return LongModel3D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public LongModel2D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return LongModel3D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
