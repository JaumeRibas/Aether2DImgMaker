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
package cellularautomata.model3d;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.IntModel;
import cellularautomata.model2d.IntModel2D;

public class IntModelAs3D extends ModelAs3D<IntModel> implements IntModel3D {

	public IntModelAs3D(IntModel source) {
		super(source);
	}

	@Override
	public int getFromPosition(int x, int y, int z) throws Exception {
		return source.getFromPosition(new Coordinates(x, y, z));
	}
	
	@Override
	public IntModel3D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return IntModel3D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public IntModel2D crossSection(int axis, int coordinate) {
		return IntModel3D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public IntModel2D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return IntModel3D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
