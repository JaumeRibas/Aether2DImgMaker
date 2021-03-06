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

import cellularautomata.grid3d.Grid3D;

/**
 * 	x -> w
 * 	y -> x
 * 	z -> y
 * 
 * @author Jaume
 *
 */
public class Grid4DZCrossSection<G extends Grid4D> implements Grid3D {

	protected G source;
	protected int z;
	
	public Grid4DZCrossSection(G source, int z) {
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new IllegalArgumentException("Z coordinate outside of grid bounds.");
		}
		this.source = source;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinWAtZ(z);
	}
	
	@Override
	public int getMinXAtY(int y) {
		return source.getMinWAtXZ(y, z);
	}
	
	@Override
	public int getMinXAtZ(int z) {
		return source.getMinWAtYZ(z, this.z);
	}
	
	@Override
	public int getMinX(int y, int z) {
		return source.getMinW(y, z, this.z);
	}

	@Override
	public int getMaxX() {
		return source.getMaxWAtZ(z);
	}
	
	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxWAtXZ(y, z);
	}
	
	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxWAtYZ(z, this.z);
	}
	
	@Override
	public int getMaxX(int y, int z) {
		return source.getMaxW(y, z, this.z);
	}

	@Override
	public int getMinY() {
		return source.getMinXAtZ(z);
	}
	
	@Override
	public int getMinYAtX(int x) {
		return source.getMinXAtWZ(x, z);
	}
	
	@Override
	public int getMinYAtZ(int z) {
		return source.getMinXAtYZ(z, this.z);
	}
	
	@Override
	public int getMinY(int x, int z) {
		return source.getMinX(x, z, this.z);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxXAtZ(z);
	}
	
	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxXAtWZ(x, z);
	}
	
	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxXAtYZ(z, this.z);
	}
	
	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxX(x, z, this.z);
	}
	
	@Override
	public int getMinZ() {
		return source.getMinYAtZ(z);
	}
	
	@Override
	public int getMinZAtX(int x) {
		return source.getMinYAtWZ(x, z);
	}
	
	@Override
	public int getMinZAtY(int y) {
		return source.getMinYAtXZ(y, z);
	}
	
	@Override
	public int getMinZ(int x, int y) {
		return source.getMinY(x, y, z);
	}
	
	@Override
	public int getMaxZ() {
		return source.getMaxYAtZ(z);
	}
	
	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxYAtWZ(x, z);
	}
	
	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxYAtXZ(y, z);
	}
	
	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxY(x, y, z);
	}
}
