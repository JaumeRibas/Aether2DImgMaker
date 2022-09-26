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
package cellularautomata.model;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;

public class LongSubModel extends SubModel<LongModel> implements LongModel {
	
	public LongSubModel(LongModel grid, PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		super(grid, minCoordinates, maxCoordinates);
	}

	@Override
	public long getFromPosition(Coordinates coordinates) throws Exception {
		return source.getFromPosition(coordinates);
	}
	
	@Override
	public LongModel subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return LongModel.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public LongModel crossSection(int axis, int coordinate) {
		return LongModel.super.crossSection(axis, coordinate);
	}
	
	@Override
	public LongModel diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return LongModel.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
