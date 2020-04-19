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
package cellularautomata.grid4d;

public interface SymmetricGrid4D extends Grid4D {
	
	//TODO: add missing methods
	
	/**
	 * Returns the smallest w-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest w
	 */
	int getAsymmetricMinW();
	
	/**
	 * Returns the largest w-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest w
	 */
	int getAsymmetricMaxW();
	
	/**
	 * Returns the smallest w-coordinate of the asymmetric section of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}], [{@link #getMinY()}, {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest w
	 */
	int getAsymmetricMinW(int x, int y, int z);
	
	/**
	 * Returns the largest w-coordinate of the asymmetric section of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}], [{@link #getMinY()}, {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest w
	 */
	int getAsymmetricMaxW(int x, int y, int z);
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	int getAsymmetricMinX();
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest x
	 */
	int getAsymmetricMaxX();
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest y
	 */
	int getAsymmetricMinY();
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest y
	 */
	int getAsymmetricMaxY();
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest z
	 */
	int getAsymmetricMinZ();
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest z
	 */
	int getAsymmetricMaxZ();

	int getAsymmetricMinWAtZ(int z);

	int getAsymmetricMinWAtXZ(int x, int z);

	int getAsymmetricMinWAtYZ(int y, int z);

	int getAsymmetricMaxWAtZ(int z);

	int getAsymmetricMaxWAtXZ(int x, int z);

	int getAsymmetricMaxWAtYZ(int y, int z);

	int getAsymmetricMinXAtZ(int z);

	int getAsymmetricMinXAtWZ(int w, int z);

	int getAsymmetricMinXAtYZ(int y, int z);

	int getAsymmetricMinX(int w, int y, int z);

	int getAsymmetricMaxXAtZ(int z);

	int getAsymmetricMaxXAtWZ(int w, int z);

	int getAsymmetricMaxXAtYZ(int y, int z);

	int getAsymmetricMaxX(int w, int y, int z);

	int getAsymmetricMinYAtZ(int z);

	int getAsymmetricMaxYAtWZ(int w, int z);

	int getAsymmetricMinYAtXZ(int x, int z);

	int getAsymmetricMinY(int w, int x, int z);

	int getAsymmetricMaxYAtZ(int z);

	int getAsymmetricMinYAtWZ(int w, int z);

	int getAsymmetricMaxYAtXZ(int x, int z);

	int getAsymmetricMaxY(int w, int x, int z);
	
	/**
	 * Returns an asymmetric section of the grid
	 * 
	 * @return a 4D grid
	 */
	default Grid4D asymmetricSection() {
		return new AsymmetricGridSection4D<SymmetricGrid4D>(this);
	}

}
