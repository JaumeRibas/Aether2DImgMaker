/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
	
	int getAsymmetricMinXAtW(int w);

	int getAsymmetricMaxXAtW(int w);

	int getAsymmetricMinYAtWX(int w, int x);

	int getAsymmetricMaxYAtWX(int w, int x);

	int getAsymmetricMinZ(int w, int x, int y);

	int getAsymmetricMaxZ(int w, int x, int y);
	
	int getAsymmetricMinYAtW(int w);
	
	int getAsymmetricMaxYAtW(int w);
	
	int getAsymmetricMinZAtW(int w);
	
	int getAsymmetricMaxZAtW(int w);

	int getAsymmetricMinWAtX(int x);

	int getAsymmetricMaxWAtX(int x);

	int getAsymmetricMinWAtY(int y);

	int getAsymmetricMaxWAtY(int y);

	int getAsymmetricMinWAtXY(int x, int y);

	int getAsymmetricMaxWAtXY(int x, int y);

	int getAsymmetricMinXAtY(int y);

	int getAsymmetricMaxXAtY(int y);

	int getAsymmetricMinXAtWY(int w, int y);

	int getAsymmetricMaxXAtWY(int w, int y);

	int getAsymmetricMinYAtX(int x);

	int getAsymmetricMaxYAtX(int x);

	int getAsymmetricMinZAtX(int x);

	int getAsymmetricMaxZAtX(int x);

	int getAsymmetricMinZAtY(int y);

	int getAsymmetricMaxZAtY(int y);

	int getAsymmetricMinZAtWX(int w, int x);

	int getAsymmetricMaxZAtWX(int w, int x);

	int getAsymmetricMinZAtWY(int w, int y);

	int getAsymmetricMaxZAtWY(int w, int y);

	int getAsymmetricMinZAtXY(int x, int y);

	int getAsymmetricMaxZAtXY(int x, int y);
	
	/**
	 * Returns an asymmetric section of the grid
	 * 
	 * @return a 4D grid
	 */
	default Grid4D asymmetricSection() {
		return new AsymmetricGridSection4D<SymmetricGrid4D>(this);
	}

}
