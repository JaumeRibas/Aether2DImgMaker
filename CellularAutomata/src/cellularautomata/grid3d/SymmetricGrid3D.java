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
package cellularautomata.grid3d;

public interface SymmetricGrid3D extends Grid3D {
	
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
	int getNonsymmetricMinXAtY(int y);
	
	/**
	 * Returns the smallest x-coordinate of the nonsymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getNonsymmetricMinZ()} 
	 * or bigger than {@link #getNonsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	int getNonsymmetricMinXAtZ(int z);
	
	/**
	 * Returns the smallest x-coordinate of the nonsymmetric section of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getNonsymmetricMinY()}, 
	 * {@link #getNonsymmetricMaxY()}] and [{@link #getNonsymmetricMinZ()}, {@link #getNonsymmetricMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	int getNonsymmetricMinX(int y, int z);
	
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
	int getNonsymmetricMaxXAtY(int y);
	
	/**
	 * Returns the largest x-coordinate of the nonsymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getNonsymmetricMinZ()} 
	 * or bigger than {@link #getNonsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	int getNonsymmetricMaxXAtZ(int z);
	
	/**
	 * Returns the largest x-coordinate of the nonsymmetric section of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getNonsymmetricMinY()}, 
	 * {@link #getNonsymmetricMaxY()}] and [{@link #getNonsymmetricMinZ()}, {@link #getNonsymmetricMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	int getNonsymmetricMaxX(int y, int z);
	
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
	int getNonsymmetricMinYAtX(int x);
	
	/**
	 * Returns the smallest y-coordinate of the nonsymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getNonsymmetricMinZ()} 
	 * or bigger than {@link #getNonsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	int getNonsymmetricMinYAtZ(int z);
	
	/**
	 * Returns the smallest y-coordinate of the nonsymmetric section of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getNonsymmetricMinX()}, 
	 * {@link #getNonsymmetricMaxX()}] and [{@link #getNonsymmetricMinZ()}, {@link #getNonsymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	int getNonsymmetricMinY(int x, int z);

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
	int getNonsymmetricMaxYAtX(int x);
	
	/**
	 * Returns the largest y-coordinate of the nonsymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getNonsymmetricMinZ()} 
	 * or bigger than {@link #getNonsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	int getNonsymmetricMaxYAtZ(int z);
	
	/**
	 * Returns the largest y-coordinate of the nonsymmetric section of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getNonsymmetricMinX()}, 
	 * {@link #getNonsymmetricMaxX()}] and [{@link #getNonsymmetricMinZ()}, {@link #getNonsymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	int getNonsymmetricMaxY(int x, int z);
	
	/**
	 * Returns the smallest z-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the smallest z
	 */
	int getNonsymmetricMinZ();
	
	/**
	 * Returns the smallest z-coordinate of the nonsymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonsymmetricMinX()} 
	 * or bigger than {@link #getNonsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest z
	 */
	int getNonsymmetricMinZAtX(int x);
	
	/**
	 * Returns the smallest z-coordinate of the nonsymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonsymmetricMinY()} 
	 * or bigger than {@link #getNonsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	int getNonsymmetricMinZAtY(int y);
	
	/**
	 * Returns the smallest z-coordinate of the nonsymmetric section of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getNonsymmetricMinX()}, 
	 * {@link #getNonsymmetricMaxX()}] and [{@link #getNonsymmetricMinY()}, {@link #getNonsymmetricMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	int getNonsymmetricMinZ(int x, int y);
	
	/**
	 * Returns the largest z-coordinate of the nonsymmetric section of the grid
	 * 
	 * @return the largest z
	 */
	int getNonsymmetricMaxZ();
	
	/**
	 * Returns the largest z-coordinate of the nonsymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonsymmetricMinX()} 
	 * or bigger than {@link #getNonsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest z
	 */
	int getNonsymmetricMaxZAtX(int x);
	
	/**
	 * Returns the largest z-coordinate of the nonsymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonsymmetricMinY()} 
	 * or bigger than {@link #getNonsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	int getNonsymmetricMaxZAtY(int y);
	
	/**
	 * Returns the largest z-coordinate of the nonsymmetric section of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getNonsymmetricMinX()}, 
	 * {@link #getNonsymmetricMaxX()}] and [{@link #getNonsymmetricMinY()}, {@link #getNonsymmetricMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	int getNonsymmetricMaxZ(int x, int y);
	
	/**
	 * Returns a nonsymmetric section of the grid
	 * 
	 * @return a 3D grid
	 */
	Grid3D nonsymmetricSection();

}
