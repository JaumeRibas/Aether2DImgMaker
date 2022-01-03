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
package cellularautomata.model5d;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SubModel5D<G extends Model5D> implements Model5D {

	protected G source;
	protected int minV;
	protected int maxV;
	protected int minW;
	protected int maxW;
	protected int minX;
	protected int maxX;
	protected int minY;
	protected int maxY;
	protected int minZ;
	protected int maxZ;
	protected int absoluteMinV;
	protected int absoluteMaxV;
	protected int absoluteMinW;
	protected int absoluteMaxW;
	protected int absoluteMinX;
	protected int absoluteMaxX;
	protected int absoluteMinY;
	protected int absoluteMaxY;
	protected int absoluteMinZ;
	protected int absoluteMaxZ;
	
	protected SubModel5D() {}

	public SubModel5D(G source, int minV, int maxV, int minW, int maxW, 
			int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		if (minV > maxV) {
			throw new IllegalArgumentException("Min v cannot be bigger than max v.");
		}
		if (minW > maxW) {
			throw new IllegalArgumentException("Min w cannot be bigger than max w.");
		}
		if (minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be bigger than max x.");
		}
		if (minY > maxY) {
			throw new IllegalArgumentException("Min y cannot be bigger than max y.");
		}
		if (minZ > maxZ) {
			throw new IllegalArgumentException("Min z cannot be bigger than max z.");
		}
		this.source = source;
		if (!getActualBounds(minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ))
			throw new IllegalArgumentException("Subsection is out of bounds.");
		this.absoluteMaxV = maxV;
		this.absoluteMinV = minV;
		this.absoluteMaxW = maxW;
		this.absoluteMinW = minW;
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
		this.absoluteMaxY = maxY;
		this.absoluteMinY = minY;
		this.absoluteMaxZ = maxZ;
		this.absoluteMinZ = minZ;
	}
	
	protected boolean getActualBounds(int minV, int maxV, int minW, int maxW, int minX, int maxX, int minY, 
			int maxY, int minZ, int maxZ) {
		int sourceMinV = source.getMinV();
		int sourceMaxV = source.getMaxV();
		int sourceMinW = source.getMinW();
		int sourceMaxW = source.getMaxW();
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		int sourceMinY = source.getMinY();
		int sourceMaxY = source.getMaxY();
		int sourceMinZ = source.getMinZ();
		int sourceMaxZ = source.getMaxZ();
		if (minV > sourceMaxV || maxV < sourceMinV 
				|| minW > sourceMaxW || maxW < sourceMinW 
				|| minX > sourceMaxX || maxX < sourceMinX
				|| minY > sourceMaxY || maxY < sourceMinY
				|| minZ > sourceMaxZ || maxZ < sourceMinZ) {
			return false;
		} else {
			//TODO validate that passed bounds are within local bounds
			this.minV = Math.max(minV, sourceMinV);
			this.maxV = Math.min(maxV, sourceMaxV);
			this.minW = Math.max(minW, sourceMinW);
			this.maxW = Math.min(maxW, sourceMaxW);
			this.minX = Math.max(minX, sourceMinX);
			this.maxX = Math.min(maxX, sourceMaxX);
			this.minY = Math.max(minY, sourceMinY);
			this.maxY = Math.min(maxY, sourceMaxY);
			this.minZ = Math.max(minZ, sourceMinZ);
			this.maxZ = Math.min(maxZ, sourceMaxZ);
			return true;
		}
	}

	@Override
	public int getMinV() { return minV; }

	@Override
	public int getMaxV() { return maxV; }

	@Override
	public int getMinVAtW(int w) { return Math.max(minV, source.getMinVAtW(w)); }

	@Override
	public int getMaxVAtW(int w) { return Math.min(maxV, source.getMaxVAtW(w)); }

	@Override
	public int getMinVAtX(int x) { return Math.max(minV, source.getMinVAtX(x)); }

	@Override
	public int getMaxVAtX(int x) { return Math.min(maxV, source.getMaxVAtX(x)); }

	@Override
	public int getMinVAtY(int y) { return Math.max(minV, source.getMinVAtY(y)); }

	@Override
	public int getMaxVAtY(int y) { return Math.min(maxV, source.getMaxVAtY(y)); }

	@Override
	public int getMinVAtZ(int z) { return Math.max(minV, source.getMinVAtZ(z)); }

	@Override
	public int getMaxVAtZ(int z) { return Math.min(maxV, source.getMaxVAtZ(z)); }

	@Override
	public int getMinVAtWX(int w, int x) { return Math.max(minV, source.getMinVAtWX(w, x)); }

	@Override
	public int getMaxVAtWX(int w, int x) { return Math.min(maxV, source.getMaxVAtWX(w, x)); }

	@Override
	public int getMinVAtWY(int w, int y) { return Math.max(minV, source.getMinVAtWY(w, y)); }

	@Override
	public int getMaxVAtWY(int w, int y) { return Math.min(maxV, source.getMaxVAtWY(w, y)); }

	@Override
	public int getMinVAtWZ(int w, int z) { return Math.max(minV, source.getMinVAtWZ(w, z)); }

	@Override
	public int getMaxVAtWZ(int w, int z) { return Math.min(maxV, source.getMaxVAtWZ(w, z)); }

	@Override
	public int getMinVAtXY(int x, int y) { return Math.max(minV, source.getMinVAtXY(x, y)); }

	@Override
	public int getMaxVAtXY(int x, int y) { return Math.min(maxV, source.getMaxVAtXY(x, y)); }

	@Override
	public int getMinVAtXZ(int x, int z) { return Math.max(minV, source.getMinVAtXZ(x, z)); }

	@Override
	public int getMaxVAtXZ(int x, int z) { return Math.min(maxV, source.getMaxVAtXZ(x, z)); }

	@Override
	public int getMinVAtYZ(int y, int z) { return Math.max(minV, source.getMinVAtYZ(y, z)); }

	@Override
	public int getMaxVAtYZ(int y, int z) { return Math.min(maxV, source.getMaxVAtYZ(y, z)); }

	@Override
	public int getMinVAtWXY(int w, int x, int y) { return Math.max(minV, source.getMinVAtWXY(w, x, y)); }

	@Override
	public int getMaxVAtWXY(int w, int x, int y) { return Math.min(maxV, source.getMaxVAtWXY(w, x, y)); }

	@Override
	public int getMinVAtWXZ(int w, int x, int z) { return Math.max(minV, source.getMinVAtWXZ(w, x, z)); }

	@Override
	public int getMaxVAtWXZ(int w, int x, int z) { return Math.min(maxV, source.getMaxVAtWXZ(w, x, z)); }

	@Override
	public int getMinVAtWYZ(int w, int y, int z) { return Math.max(minV, source.getMinVAtWYZ(w, y, z)); }

	@Override
	public int getMaxVAtWYZ(int w, int y, int z) { return Math.min(maxV, source.getMaxVAtWYZ(w, y, z)); }

	@Override
	public int getMinVAtXYZ(int x, int y, int z) { return Math.max(minV, source.getMinVAtXYZ(x, y, z)); }

	@Override
	public int getMaxVAtXYZ(int x, int y, int z) { return Math.min(maxV, source.getMaxVAtXYZ(x, y, z)); }

	@Override
	public int getMinV(int w, int x, int y, int z) { return Math.max(minV, source.getMinV(w, x, y, z)); }

	@Override
	public int getMaxV(int w, int x, int y, int z) { return Math.min(maxV, source.getMaxV(w, x, y, z)); }

	@Override
	public int getMinW() { return minW; }

	@Override
	public int getMaxW() { return maxW; }

	@Override
	public int getMinWAtV(int v) { return Math.max(minW, source.getMinWAtV(v)); }

	@Override
	public int getMaxWAtV(int v) { return Math.min(maxW, source.getMaxWAtV(v)); }

	@Override
	public int getMinWAtX(int x) { return Math.max(minW, source.getMinWAtX(x)); }

	@Override
	public int getMaxWAtX(int x) { return Math.min(maxW, source.getMaxWAtX(x)); }

	@Override
	public int getMinWAtY(int y) { return Math.max(minW, source.getMinWAtY(y)); }

	@Override
	public int getMaxWAtY(int y) { return Math.min(maxW, source.getMaxWAtY(y)); }

	@Override
	public int getMinWAtZ(int z) { return Math.max(minW, source.getMinWAtZ(z)); }

	@Override
	public int getMaxWAtZ(int z) { return Math.min(maxW, source.getMaxWAtZ(z)); }

	@Override
	public int getMinWAtVX(int v, int x) { return Math.max(minW, source.getMinWAtVX(v, x)); }

	@Override
	public int getMaxWAtVX(int v, int x) { return Math.min(maxW, source.getMaxWAtVX(v, x)); }

	@Override
	public int getMinWAtVY(int v, int y) { return Math.max(minW, source.getMinWAtVY(v, y)); }

	@Override
	public int getMaxWAtVY(int v, int y) { return Math.min(maxW, source.getMaxWAtVY(v, y)); }

	@Override
	public int getMinWAtVZ(int v, int z) { return Math.max(minW, source.getMinWAtVZ(v, z)); }

	@Override
	public int getMaxWAtVZ(int v, int z) { return Math.min(maxW, source.getMaxWAtVZ(v, z)); }

	@Override
	public int getMinWAtXY(int x, int y) { return Math.max(minW, source.getMinWAtXY(x, y)); }

	@Override
	public int getMaxWAtXY(int x, int y) { return Math.min(maxW, source.getMaxWAtXY(x, y)); }

	@Override
	public int getMinWAtXZ(int x, int z) { return Math.max(minW, source.getMinWAtXZ(x, z)); }

	@Override
	public int getMaxWAtXZ(int x, int z) { return Math.min(maxW, source.getMaxWAtXZ(x, z)); }

	@Override
	public int getMinWAtYZ(int y, int z) { return Math.max(minW, source.getMinWAtYZ(y, z)); }

	@Override
	public int getMaxWAtYZ(int y, int z) { return Math.min(maxW, source.getMaxWAtYZ(y, z)); }

	@Override
	public int getMinWAtVXY(int v, int x, int y) { return Math.max(minW, source.getMinWAtVXY(v, x, y)); }

	@Override
	public int getMaxWAtVXY(int v, int x, int y) { return Math.min(maxW, source.getMaxWAtVXY(v, x, y)); }

	@Override
	public int getMinWAtVXZ(int v, int x, int z) { return Math.max(minW, source.getMinWAtVXZ(v, x, z)); }

	@Override
	public int getMaxWAtVXZ(int v, int x, int z) { return Math.min(maxW, source.getMaxWAtVXZ(v, x, z)); }

	@Override
	public int getMinWAtVYZ(int v, int y, int z) { return Math.max(minW, source.getMinWAtVYZ(v, y, z)); }

	@Override
	public int getMaxWAtVYZ(int v, int y, int z) { return Math.min(maxW, source.getMaxWAtVYZ(v, y, z)); }

	@Override
	public int getMinWAtXYZ(int x, int y, int z) { return Math.max(minW, source.getMinWAtXYZ(x, y, z)); }

	@Override
	public int getMaxWAtXYZ(int x, int y, int z) { return Math.min(maxW, source.getMaxWAtXYZ(x, y, z)); }

	@Override
	public int getMinW(int v, int x, int y, int z) { return Math.max(minW, source.getMinW(v, x, y, z)); }

	@Override
	public int getMaxW(int v, int x, int y, int z) { return Math.min(maxW, source.getMaxW(v, x, y, z)); }

	@Override
	public int getMinX() { return minX; }

	@Override
	public int getMaxX() { return maxX; }

	@Override
	public int getMinXAtV(int v) { return Math.max(minX, source.getMinXAtV(v)); }

	@Override
	public int getMaxXAtV(int v) { return Math.min(maxX, source.getMaxXAtV(v)); }

	@Override
	public int getMinXAtW(int w) { return Math.max(minX, source.getMinXAtW(w)); }

	@Override
	public int getMaxXAtW(int w) { return Math.min(maxX, source.getMaxXAtW(w)); }

	@Override
	public int getMinXAtY(int y) { return Math.max(minX, source.getMinXAtY(y)); }

	@Override
	public int getMaxXAtY(int y) { return Math.min(maxX, source.getMaxXAtY(y)); }

	@Override
	public int getMinXAtZ(int z) { return Math.max(minX, source.getMinXAtZ(z)); }

	@Override
	public int getMaxXAtZ(int z) { return Math.min(maxX, source.getMaxXAtZ(z)); }

	@Override
	public int getMinXAtVW(int v, int w) { return Math.max(minX, source.getMinXAtVW(v, w)); }

	@Override
	public int getMaxXAtVW(int v, int w) { return Math.min(maxX, source.getMaxXAtVW(v, w)); }

	@Override
	public int getMinXAtVY(int v, int y) { return Math.max(minX, source.getMinXAtVY(v, y)); }

	@Override
	public int getMaxXAtVY(int v, int y) { return Math.min(maxX, source.getMaxXAtVY(v, y)); }

	@Override
	public int getMinXAtVZ(int v, int z) { return Math.max(minX, source.getMinXAtVZ(v, z)); }

	@Override
	public int getMaxXAtVZ(int v, int z) { return Math.min(maxX, source.getMaxXAtVZ(v, z)); }

	@Override
	public int getMinXAtWY(int w, int y) { return Math.max(minX, source.getMinXAtWY(w, y)); }

	@Override
	public int getMaxXAtWY(int w, int y) { return Math.min(maxX, source.getMaxXAtWY(w, y)); }

	@Override
	public int getMinXAtWZ(int w, int z) { return Math.max(minX, source.getMinXAtWZ(w, z)); }

	@Override
	public int getMaxXAtWZ(int w, int z) { return Math.min(maxX, source.getMaxXAtWZ(w, z)); }

	@Override
	public int getMinXAtYZ(int y, int z) { return Math.max(minX, source.getMinXAtYZ(y, z)); }

	@Override
	public int getMaxXAtYZ(int y, int z) { return Math.min(maxX, source.getMaxXAtYZ(y, z)); }

	@Override
	public int getMinXAtVWY(int v, int w, int y) { return Math.max(minX, source.getMinXAtVWY(v, w, y)); }

	@Override
	public int getMaxXAtVWY(int v, int w, int y) { return Math.min(maxX, source.getMaxXAtVWY(v, w, y)); }

	@Override
	public int getMinXAtVWZ(int v, int w, int z) { return Math.max(minX, source.getMinXAtVWZ(v, w, z)); }

	@Override
	public int getMaxXAtVWZ(int v, int w, int z) { return Math.min(maxX, source.getMaxXAtVWZ(v, w, z)); }

	@Override
	public int getMinXAtVYZ(int v, int y, int z) { return Math.max(minX, source.getMinXAtVYZ(v, y, z)); }

	@Override
	public int getMaxXAtVYZ(int v, int y, int z) { return Math.min(maxX, source.getMaxXAtVYZ(v, y, z)); }

	@Override
	public int getMinXAtWYZ(int w, int y, int z) { return Math.max(minX, source.getMinXAtWYZ(w, y, z)); }

	@Override
	public int getMaxXAtWYZ(int w, int y, int z) { return Math.min(maxX, source.getMaxXAtWYZ(w, y, z)); }

	@Override
	public int getMinX(int v, int w, int y, int z) { return Math.max(minX, source.getMinX(v, w, y, z)); }

	@Override
	public int getMaxX(int v, int w, int y, int z) { return Math.min(maxX, source.getMaxX(v, w, y, z)); }

	@Override
	public int getMinY() { return minY; }

	@Override
	public int getMaxY() { return maxY; }

	@Override
	public int getMinYAtV(int v) { return Math.max(minY, source.getMinYAtV(v)); }

	@Override
	public int getMaxYAtV(int v) { return Math.min(maxY, source.getMaxYAtV(v)); }

	@Override
	public int getMinYAtW(int w) { return Math.max(minY, source.getMinYAtW(w)); }

	@Override
	public int getMaxYAtW(int w) { return Math.min(maxY, source.getMaxYAtW(w)); }

	@Override
	public int getMinYAtX(int x) { return Math.max(minY, source.getMinYAtX(x)); }

	@Override
	public int getMaxYAtX(int x) { return Math.min(maxY, source.getMaxYAtX(x)); }

	@Override
	public int getMinYAtZ(int z) { return Math.max(minY, source.getMinYAtZ(z)); }

	@Override
	public int getMaxYAtZ(int z) { return Math.min(maxY, source.getMaxYAtZ(z)); }

	@Override
	public int getMinYAtVW(int v, int w) { return Math.max(minY, source.getMinYAtVW(v, w)); }

	@Override
	public int getMaxYAtVW(int v, int w) { return Math.min(maxY, source.getMaxYAtVW(v, w)); }

	@Override
	public int getMinYAtVX(int v, int x) { return Math.max(minY, source.getMinYAtVX(v, x)); }

	@Override
	public int getMaxYAtVX(int v, int x) { return Math.min(maxY, source.getMaxYAtVX(v, x)); }

	@Override
	public int getMinYAtVZ(int v, int z) { return Math.max(minY, source.getMinYAtVZ(v, z)); }

	@Override
	public int getMaxYAtVZ(int v, int z) { return Math.min(maxY, source.getMaxYAtVZ(v, z)); }

	@Override
	public int getMinYAtWX(int w, int x) { return Math.max(minY, source.getMinYAtWX(w, x)); }

	@Override
	public int getMaxYAtWX(int w, int x) { return Math.min(maxY, source.getMaxYAtWX(w, x)); }

	@Override
	public int getMinYAtWZ(int w, int z) { return Math.max(minY, source.getMinYAtWZ(w, z)); }

	@Override
	public int getMaxYAtWZ(int w, int z) { return Math.min(maxY, source.getMaxYAtWZ(w, z)); }

	@Override
	public int getMinYAtXZ(int x, int z) { return Math.max(minY, source.getMinYAtXZ(x, z)); }

	@Override
	public int getMaxYAtXZ(int x, int z) { return Math.min(maxY, source.getMaxYAtXZ(x, z)); }

	@Override
	public int getMinYAtVWX(int v, int w, int x) { return Math.max(minY, source.getMinYAtVWX(v, w, x)); }

	@Override
	public int getMaxYAtVWX(int v, int w, int x) { return Math.min(maxY, source.getMaxYAtVWX(v, w, x)); }

	@Override
	public int getMinYAtVWZ(int v, int w, int z) { return Math.max(minY, source.getMinYAtVWZ(v, w, z)); }

	@Override
	public int getMaxYAtVWZ(int v, int w, int z) { return Math.min(maxY, source.getMaxYAtVWZ(v, w, z)); }

	@Override
	public int getMinYAtVXZ(int v, int x, int z) { return Math.max(minY, source.getMinYAtVXZ(v, x, z)); }

	@Override
	public int getMaxYAtVXZ(int v, int x, int z) { return Math.min(maxY, source.getMaxYAtVXZ(v, x, z)); }

	@Override
	public int getMinYAtWXZ(int w, int x, int z) { return Math.max(minY, source.getMinYAtWXZ(w, x, z)); }

	@Override
	public int getMaxYAtWXZ(int w, int x, int z) { return Math.min(maxY, source.getMaxYAtWXZ(w, x, z)); }

	@Override
	public int getMinY(int v, int w, int x, int z) { return Math.max(minY, source.getMinY(v, w, x, z)); }

	@Override
	public int getMaxY(int v, int w, int x, int z) { return Math.min(maxY, source.getMaxY(v, w, x, z)); }

	@Override
	public int getMinZ() { return minZ; }

	@Override
	public int getMaxZ() { return maxZ; }

	@Override
	public int getMinZAtV(int v) { return Math.max(minZ, source.getMinZAtV(v)); }

	@Override
	public int getMaxZAtV(int v) { return Math.min(maxZ, source.getMaxZAtV(v)); }

	@Override
	public int getMinZAtW(int w) { return Math.max(minZ, source.getMinZAtW(w)); }

	@Override
	public int getMaxZAtW(int w) { return Math.min(maxZ, source.getMaxZAtW(w)); }

	@Override
	public int getMinZAtX(int x) { return Math.max(minZ, source.getMinZAtX(x)); }

	@Override
	public int getMaxZAtX(int x) { return Math.min(maxZ, source.getMaxZAtX(x)); }

	@Override
	public int getMinZAtY(int y) { return Math.max(minZ, source.getMinZAtY(y)); }

	@Override
	public int getMaxZAtY(int y) { return Math.min(maxZ, source.getMaxZAtY(y)); }

	@Override
	public int getMinZAtVW(int v, int w) { return Math.max(minZ, source.getMinZAtVW(v, w)); }

	@Override
	public int getMaxZAtVW(int v, int w) { return Math.min(maxZ, source.getMaxZAtVW(v, w)); }

	@Override
	public int getMinZAtVX(int v, int x) { return Math.max(minZ, source.getMinZAtVX(v, x)); }

	@Override
	public int getMaxZAtVX(int v, int x) { return Math.min(maxZ, source.getMaxZAtVX(v, x)); }

	@Override
	public int getMinZAtVY(int v, int y) { return Math.max(minZ, source.getMinZAtVY(v, y)); }

	@Override
	public int getMaxZAtVY(int v, int y) { return Math.min(maxZ, source.getMaxZAtVY(v, y)); }

	@Override
	public int getMinZAtWX(int w, int x) { return Math.max(minZ, source.getMinZAtWX(w, x)); }

	@Override
	public int getMaxZAtWX(int w, int x) { return Math.min(maxZ, source.getMaxZAtWX(w, x)); }

	@Override
	public int getMinZAtWY(int w, int y) { return Math.max(minZ, source.getMinZAtWY(w, y)); }

	@Override
	public int getMaxZAtWY(int w, int y) { return Math.min(maxZ, source.getMaxZAtWY(w, y)); }

	@Override
	public int getMinZAtXY(int x, int y) { return Math.max(minZ, source.getMinZAtXY(x, y)); }

	@Override
	public int getMaxZAtXY(int x, int y) { return Math.min(maxZ, source.getMaxZAtXY(x, y)); }

	@Override
	public int getMinZAtVWX(int v, int w, int x) { return Math.max(minZ, source.getMinZAtVWX(v, w, x)); }

	@Override
	public int getMaxZAtVWX(int v, int w, int x) { return Math.min(maxZ, source.getMaxZAtVWX(v, w, x)); }

	@Override
	public int getMinZAtVWY(int v, int w, int y) { return Math.max(minZ, source.getMinZAtVWY(v, w, y)); }

	@Override
	public int getMaxZAtVWY(int v, int w, int y) { return Math.min(maxZ, source.getMaxZAtVWY(v, w, y)); }

	@Override
	public int getMinZAtVXY(int v, int x, int y) { return Math.max(minZ, source.getMinZAtVXY(v, x, y)); }

	@Override
	public int getMaxZAtVXY(int v, int x, int y) { return Math.min(maxZ, source.getMaxZAtVXY(v, x, y)); }

	@Override
	public int getMinZAtWXY(int w, int x, int y) { return Math.max(minZ, source.getMinZAtWXY(w, x, y)); }

	@Override
	public int getMaxZAtWXY(int w, int x, int y) { return Math.min(maxZ, source.getMaxZAtWXY(w, x, y)); }

	@Override
	public int getMinZ(int v, int w, int x, int y) { return Math.max(minZ, source.getMinZ(v, w, x, y)); }

	@Override
	public int getMaxZ(int v, int w, int x, int y) { return Math.min(maxZ, source.getMaxZ(v, w, x, y)); }

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinV, absoluteMaxV, absoluteMinW, absoluteMaxW, absoluteMinX, absoluteMaxX, absoluteMinY, absoluteMaxY, absoluteMinZ, absoluteMaxZ)) {
			throw new UnsupportedOperationException("Subsection is out of bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName();
	}

	@Override
	public String getSubfolderPath() {
		return source.getSubfolderPath() + "/" + absoluteMinV + "<=" + source.getVLabel() + "<=" + absoluteMaxV 
				+ "_" + absoluteMinW + "<=" + source.getWLabel() + "<=" + absoluteMaxW 
				+ "_" + absoluteMinX + "<=" + source.getXLabel() + "<=" + absoluteMaxX 
				+ "_" + absoluteMinY + "<=" + source.getYLabel() + "<=" + absoluteMaxY 
				+ "_" + absoluteMinZ + "<=" + source.getZLabel() + "<=" + absoluteMaxZ;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
