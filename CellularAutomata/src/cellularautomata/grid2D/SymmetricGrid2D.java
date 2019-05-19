/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.grid2D;

public interface SymmetricGrid2D extends Grid2D {

	/**
	 * Returns the smallest x-coordinate of the non symmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	int getNonSymmetricMinX();
	
	/**
	 * Returns the smallest x-coordinate of the non symmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonSymmetricMinY()} 
	 * or bigger than {@link #getNonSymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x
	 */
	int getNonSymmetricMinX(int y);
	
	/**
	 * Returns the largest x-coordinate of the non symmetric section of the grid
	 * 
	 * @return the largest x
	 */
	int getNonSymmetricMaxX();
	
	/**
	 * Returns the largest x-coordinate of the non symmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonSymmetricMinY()}
	 * or bigger than {@link #getNonSymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest x
	 */
	int getNonSymmetricMaxX(int y);
	
	/**
	 * Returns the smallest y-coordinate of the non symmetric section of the grid
	 * 
	 * @return the smallest y
	 */
	int getNonSymmetricMinY();
	
	/**
	 * Returns the smallest y-coordinate of the non symmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonSymmetricMinX()}
	 * or bigger than {@link #getNonSymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest y
	 */
	int getNonSymmetricMinY(int x);
	
	/**
	 * Returns the largest y-coordinate of the non symmetric section of the grid
	 * 
	 * @return the largest y
	 */
	int getNonSymmetricMaxY();
	
	/**
	 * Returns the largest y-coordinate of the non symmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonSymmetricMinX()}
	 * or bigger than {@link #getNonSymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest y
	 */
	int getNonSymmetricMaxY(int x);

}
