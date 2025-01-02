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
package cellularautomata.model1d;

import cellularautomata.model.Model;
import cellularautomata.PartialCoordinates;

public interface Model1D extends Model {
	
	@Override
	default int getGridDimension() {
		return 1;
	}
	
	default String getXLabel() {
		return "x";
	}
	
	@Override
	default String getAxisLabel(int axis) {
		return getXLabel();
	}
	
	/**
	 * Returns the smallest x-coordinate
	 * 
	 * @return the smallest x
	 */
	int getMinX();
	
	/**
	 * Returns the largest x-coordinate
	 * 
	 * @return the largest x
	 */
	int getMaxX();

	@Override
	default int getMaxCoordinate(int axis) {
		return getMaxX();
	}

	@Override
	default int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return getMaxX();
	}

	@Override
	default int getMinCoordinate(int axis) {
		return getMinX();
	}

	@Override
	default int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		return getMinX();
	}
	
	@Override
	default Model1D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return subsection(minCoordinates.get(0), maxCoordinates.get(0));
	}
	
	/**
	 * Returns a decorated {@link Model1D} with the passed bounds.
	 * 
	 * @param minX
	 * @param maxX
	 * @return a {@link Model1D} decorating the current grid 
	 */
	default Model1D subsection(Integer minX, Integer maxX) {
		return new SubModel1D<Model1D>(this, minX, maxX);
	}
}
