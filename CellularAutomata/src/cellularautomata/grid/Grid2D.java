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
package cellularautomata.grid;

public interface Grid2D extends Grid {
	
	/**
	 * Returns the smallest x-coordinate
	 * 
	 * @return the smallest x
	 */
	public int getMinX();
	
	/**
	 * Returns the smallest x-coordinate at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x
	 */
	public default int getMinX(int y) {
		return getMinX();
	}
	
	/**
	 * Returns the largest x-coordinate
	 * 
	 * @return the largest x
	 */
	public int getMaxX();
	
	/**
	 * Returns the largest x-coordinate at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()}
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest x
	 */
	public default int getMaxX(int y) {
		return getMaxX();
	}
	
	/**
	 * Returns the smallest y-coordinate
	 * 
	 * @return the smallest y
	 */
	public int getMinY();
	
	/**
	 * Returns the smallest y-coordinate at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()}
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest y
	 */
	public default int getMinY(int x) {
		return getMinY();
	}
	
	/**
	 * Returns the largest y-coordinate
	 * 
	 * @return the largest y
	 */
	public int getMaxY();
	
	/**
	 * Returns the largest y-coordinate at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()}
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest y
	 */
	public default int getMaxY(int x) {
		return getMaxY();
	}
	
	/**
	 * Returns a wrapped {@link Grid2D} with the passed bounds.
	 * 
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @return a {@link Grid2D} wrapping the current grid 
	 */
	public Grid2D subGrid(int minX, int maxX, int minY, int maxY);
}
