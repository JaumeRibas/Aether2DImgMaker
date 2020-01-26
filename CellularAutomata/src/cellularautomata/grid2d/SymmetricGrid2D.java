/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.grid2d;

public interface SymmetricGrid2D extends Grid2D {

	/**
	 * Returns the smallest x-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	int getNonsymmetricMinX();
	
	/**
	 * Returns the smallest x-coordinate of the nonsymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonsymmetricMinY()} 
	 * or bigger than {@link #getNonsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x
	 */
	int getNonsymmetricMinX(int y);
	
	/**
	 * Returns the largest x-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the largest x
	 */
	int getNonsymmetricMaxX();
	
	/**
	 * Returns the largest x-coordinate of the nonsymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonsymmetricMinY()}
	 * or bigger than {@link #getNonsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest x
	 */
	int getNonsymmetricMaxX(int y);
	
	/**
	 * Returns the smallest y-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the smallest y
	 */
	int getNonsymmetricMinY();
	
	/**
	 * Returns the smallest y-coordinate of the nonsymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonsymmetricMinX()}
	 * or bigger than {@link #getNonsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest y
	 */
	int getNonsymmetricMinY(int x);
	
	/**
	 * Returns the largest y-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the largest y
	 */
	int getNonsymmetricMaxY();
	
	/**
	 * Returns the largest y-coordinate of the nonsymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonsymmetricMinX()}
	 * or bigger than {@link #getNonsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest y
	 */
	int getNonsymmetricMaxY(int x);
	
	/**
	 * Returns a nonsymmetric section of the grid
	 * 
	 * @return a 2D grid
	 */
	Grid2D nonsymmetricSection();

}
