/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

public interface SymmetricGrid4D extends Grid4D {
	
	//TODO: add extra methods
	
	/**
	 * Returns the smallest w-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the smallest w
	 */
	int getNonsymmetricMinW();
	
	/**
	 * Returns the largest w-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the largest w
	 */
	int getNonsymmetricMaxW();
	
	/**
	 * Returns the smallest w-coordinate of the nonsymmetric section of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}], [{@link #getMinY()}, {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest w
	 */
	int getNonsymmetricMinW(int x, int y, int z);
	
	/**
	 * Returns the largest w-coordinate of the nonsymmetric section of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}], [{@link #getMinY()}, {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest w
	 */
	int getNonsymmetricMaxW(int x, int y, int z);
	
	/**
	 * Returns the smallest x-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	int getNonsymmetricMinX();
	
	/**
	 * Returns the largest x-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the largest x
	 */
	int getNonsymmetricMaxX();
	
	/**
	 * Returns the smallest y-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the smallest y
	 */
	int getNonsymmetricMinY();
	
	/**
	 * Returns the largest y-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the largest y
	 */
	int getNonsymmetricMaxY();
	
	/**
	 * Returns the smallest z-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the smallest z
	 */
	int getNonsymmetricMinZ();
	
	/**
	 * Returns the largest z-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the largest z
	 */
	int getNonsymmetricMaxZ();
	
	/**
	 * Returns a nonsymmetric section of the grid
	 * 
	 * @return a 4D grid
	 */
	Grid4D nonsymmetricSection();

}
