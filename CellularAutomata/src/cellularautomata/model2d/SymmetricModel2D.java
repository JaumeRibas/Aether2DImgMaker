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
package cellularautomata.model2d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.SymmetricModel;

public interface SymmetricModel2D extends Model2D, SymmetricModel {

	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	int getAsymmetricMinX();
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getAsymmetricMinY()} 
	 * or bigger than {@link #getAsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x
	 */
	int getAsymmetricMinX(int y);
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest x
	 */
	int getAsymmetricMaxX();
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getAsymmetricMinY()}
	 * or bigger than {@link #getAsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest x
	 */
	int getAsymmetricMaxX(int y);
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest y
	 */
	int getAsymmetricMinY();
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getAsymmetricMinX()}
	 * or bigger than {@link #getAsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest y
	 */
	int getAsymmetricMinY(int x);
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest y
	 */
	int getAsymmetricMaxY();
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getAsymmetricMinX()}
	 * or bigger than {@link #getAsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest y
	 */
	int getAsymmetricMaxY(int x);
	
	@Override
	default int getAsymmetricMaxCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getAsymmetricMaxX();
		case 1: 
			return getAsymmetricMaxY();
		default: throw new IllegalArgumentException("Axis must be 0 or 1. Got " + axis + ".");
		}
	}

	@Override
	default int getAsymmetricMaxCoordinate(int axis, PartialCoordinates coordinates) {
		switch (axis) {
		case 0: 
			Integer y = coordinates.get(1);
			if (y == null) {
				return getAsymmetricMaxX();
			} else {
				return getAsymmetricMaxX(y);
			}
		case 1: 
			Integer x = coordinates.get(0);
			if (x == null) {
				return getAsymmetricMaxY();
			} else {
				return getAsymmetricMaxY(x);
			}
		default: throw new IllegalArgumentException("Axis must be 0 or 1. Got " + axis + ".");
		}
	}
	
	@Override
	default int getAsymmetricMinCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getAsymmetricMinX();
		case 1: 
			return getAsymmetricMinY();
		default: throw new IllegalArgumentException("Axis must be 0 or 1. Got " + axis + ".");
		}
	}

	@Override
	default int getAsymmetricMinCoordinate(int axis, PartialCoordinates coordinates) {
		switch (axis) {
		case 0: 
			Integer y = coordinates.get(1);
			if (y == null) {
				return getAsymmetricMinX();
			} else {
				return getAsymmetricMinX(y);
			}
		case 1: 
			Integer x = coordinates.get(0);
			if (x == null) {
				return getAsymmetricMinY();
			} else {
				return getAsymmetricMinY(x);
			}
		default: throw new IllegalArgumentException("Axis must be 0 or 1. Got " + axis + ".");
		}
	}
	
	@Override
	default Model2D asymmetricSection() {
		return new AsymmetricModelSection2D<SymmetricModel2D>(this);
	}
}
