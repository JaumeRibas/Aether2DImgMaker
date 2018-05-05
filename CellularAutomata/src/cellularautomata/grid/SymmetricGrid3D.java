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

public interface SymmetricGrid3D extends Grid3D {
	
	/**
	 * Returns the smallest x-coordinate of the non symmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	public int getNonSymmetricMinX();
	
	/**
	 * Returns the smallest x-coordinate of the non symmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonSymmetricMinY()} 
	 * or bigger than {@link #getNonSymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x
	 */
	public int getNonSymmetricMinXAtY(int y);
	
	/**
	 * Returns the smallest x-coordinate of the non symmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getNonSymmetricMinZ()} 
	 * or bigger than {@link #getNonSymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	public int getNonSymmetricMinXAtZ(int z);
	
	/**
	 * Returns the smallest x-coordinate of the non symmetric section of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getNonSymmetricMinY()}, 
	 * {@link #getNonSymmetricMaxY()}] and [{@link #getNonSymmetricMinZ()}, {@link #getNonSymmetricMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	public int getNonSymmetricMinX(int y, int z);
	
	/**
	 * Returns the largest x-coordinate of the non symmetric section of the grid
	 * 
	 * @return the largest x
	 */
	public int getNonSymmetricMaxX();
	
	/**
	 * Returns the largest x-coordinate of the non symmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonSymmetricMinY()} 
	 * or bigger than {@link #getNonSymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest x
	 */
	public int getNonSymmetricMaxXAtY(int y);
	
	/**
	 * Returns the largest x-coordinate of the non symmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getNonSymmetricMinZ()} 
	 * or bigger than {@link #getNonSymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	public int getNonSymmetricMaxXAtZ(int z);
	
	/**
	 * Returns the largest x-coordinate of the non symmetric section of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getNonSymmetricMinY()}, 
	 * {@link #getNonSymmetricMaxY()}] and [{@link #getNonSymmetricMinZ()}, {@link #getNonSymmetricMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	public int getNonSymmetricMaxX(int y, int z);
	
	/**
	 * Returns the smallest y-coordinate of the non symmetric section of the grid
	 * 
	 * @return the smallest y
	 */
	public int getNonSymmetricMinY();
	
	/**
	 * Returns the smallest y-coordinate of the non symmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonSymmetricMinX()} 
	 * or bigger than {@link #getNonSymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest y
	 */
	public int getNonSymmetricMinYAtX(int x);
	
	/**
	 * Returns the smallest y-coordinate of the non symmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getNonSymmetricMinZ()} 
	 * or bigger than {@link #getNonSymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	public int getNonSymmetricMinYAtZ(int z);
	
	/**
	 * Returns the smallest y-coordinate of the non symmetric section of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getNonSymmetricMinX()}, 
	 * {@link #getNonSymmetricMaxX()}] and [{@link #getNonSymmetricMinZ()}, {@link #getNonSymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	public int getNonSymmetricMinY(int x, int z);

	/**
	 * Returns the largest y-coordinate of the non symmetric section of the grid
	 * 
	 * @return the largest y
	 */
	public int getNonSymmetricMaxY();
	
	/**
	 * Returns the largest y-coordinate of the non symmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonSymmetricMinX()} 
	 * or bigger than {@link #getNonSymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest y
	 */
	public int getNonSymmetricMaxYAtX(int x);
	
	/**
	 * Returns the largest y-coordinate of the non symmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getNonSymmetricMinZ()} 
	 * or bigger than {@link #getNonSymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	public int getNonSymmetricMaxYAtZ(int z);
	
	/**
	 * Returns the largest y-coordinate of the non symmetric section of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getNonSymmetricMinX()}, 
	 * {@link #getNonSymmetricMaxX()}] and [{@link #getNonSymmetricMinZ()}, {@link #getNonSymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	public int getNonSymmetricMaxY(int x, int z);
	
	/**
	 * Returns the smallest z-coordinate of the non symmetric section of the grid
	 * 
	 * @return the smallest z
	 */
	public int getNonSymmetricMinZ();
	
	/**
	 * Returns the smallest z-coordinate of the non symmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonSymmetricMinX()} 
	 * or bigger than {@link #getNonSymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest z
	 */
	public int getNonSymmetricMinZAtX(int x);
	
	/**
	 * Returns the smallest z-coordinate of the non symmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonSymmetricMinY()} 
	 * or bigger than {@link #getNonSymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	public int getNonSymmetricMinZAtY(int y);
	
	/**
	 * Returns the smallest z-coordinate of the non symmetric section of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getNonSymmetricMinX()}, 
	 * {@link #getNonSymmetricMaxX()}] and [{@link #getNonSymmetricMinY()}, {@link #getNonSymmetricMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	public int getNonSymmetricMinZ(int x, int y);
	
	/**
	 * Returns the largest z-coordinate of the non symmetric section of the grid
	 * 
	 * @return the largest z
	 */
	public int getNonSymmetricMaxZ();
	
	/**
	 * Returns the largest z-coordinate of the non symmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getNonSymmetricMinX()} 
	 * or bigger than {@link #getNonSymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest z
	 */
	public int getNonSymmetricMaxZAtX(int x);
	
	/**
	 * Returns the largest z-coordinate of the non symmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getNonSymmetricMinY()} 
	 * or bigger than {@link #getNonSymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	public int getNonSymmetricMaxZAtY(int y);
	
	/**
	 * Returns the largest z-coordinate of the non symmetric section of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getNonSymmetricMinX()}, 
	 * {@link #getNonSymmetricMaxX()}] and [{@link #getNonSymmetricMinY()}, {@link #getNonSymmetricMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	public int getNonSymmetricMaxZ(int x, int y);

}
