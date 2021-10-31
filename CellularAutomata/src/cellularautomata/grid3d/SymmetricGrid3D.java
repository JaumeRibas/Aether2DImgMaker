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
package cellularautomata.grid3d;

import cellularautomata.grid.SymmetricGrid;

public interface SymmetricGrid3D extends Grid3D, SymmetricGrid {
	
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
	int getAsymmetricMinXAtY(int y);
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getAsymmetricMinZ()} 
	 * or bigger than {@link #getAsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	int getAsymmetricMinXAtZ(int z);
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinY()}, 
	 * {@link #getAsymmetricMaxY()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	int getAsymmetricMinX(int y, int z);
	
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
	int getAsymmetricMaxXAtY(int y);
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getAsymmetricMinZ()} 
	 * or bigger than {@link #getAsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	int getAsymmetricMaxXAtZ(int z);
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinY()}, 
	 * {@link #getAsymmetricMaxY()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	int getAsymmetricMaxX(int y, int z);
	
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
	int getAsymmetricMinYAtX(int x);
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getAsymmetricMinZ()} 
	 * or bigger than {@link #getAsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	int getAsymmetricMinYAtZ(int z);
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	int getAsymmetricMinY(int x, int z);

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
	int getAsymmetricMaxYAtX(int x);
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getAsymmetricMinZ()} 
	 * or bigger than {@link #getAsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	int getAsymmetricMaxYAtZ(int z);
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	int getAsymmetricMaxY(int x, int z);
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest z
	 */
	int getAsymmetricMinZ();
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getAsymmetricMinX()} 
	 * or bigger than {@link #getAsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest z
	 */
	int getAsymmetricMinZAtX(int x);
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getAsymmetricMinY()} 
	 * or bigger than {@link #getAsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	int getAsymmetricMinZAtY(int y);
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}] and [{@link #getAsymmetricMinY()}, {@link #getAsymmetricMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	int getAsymmetricMinZ(int x, int y);
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest z
	 */
	int getAsymmetricMaxZ();
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getAsymmetricMinX()} 
	 * or bigger than {@link #getAsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest z
	 */
	int getAsymmetricMaxZAtX(int x);
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getAsymmetricMinY()} 
	 * or bigger than {@link #getAsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	int getAsymmetricMaxZAtY(int y);
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}] and [{@link #getAsymmetricMinY()}, {@link #getAsymmetricMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	int getAsymmetricMaxZ(int x, int y);
	
	@Override
	default Grid3D asymmetricSection() {
		return new AsymmetricGridSection3D<SymmetricGrid3D>(this);
	}

}
