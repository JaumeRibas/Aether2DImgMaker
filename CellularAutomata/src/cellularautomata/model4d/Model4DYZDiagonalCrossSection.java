/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
package cellularautomata.model4d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model3d.Model3D;

public class Model4DYZDiagonalCrossSection<G extends Model4D> implements Model3D {

	protected G source;
	protected int zOffsetFromY;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	
	public Model4DYZDiagonalCrossSection(G source, int zOffsetFromY) {		
		this.source = source;
		this.zOffsetFromY = zOffsetFromY;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
	}
	
	@Override
	public String getXLabel() {
		return source.getWLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getXLabel();
	}
	
	@Override
	public String getZLabel() {
		return source.getYLabel();
	}
	
	protected boolean getBounds() {
		int y = source.getMinY();
		int maxY = source.getMaxY();
		int crossSectionZ = y + zOffsetFromY;
		while (y <= maxY && (crossSectionZ < source.getMinZAtY(y) || crossSectionZ > source.getMaxZAtY(y))) {
			y++;
			crossSectionZ++;
		}
		if (y <= maxY) {
			crossSectionMinY = y;
			crossSectionMaxY = y;
			crossSectionMaxW = source.getMaxWAtYZ(y, crossSectionZ);
			crossSectionMinW = source.getMinWAtYZ(y, crossSectionZ);
			crossSectionMaxX = source.getMaxXAtYZ(y, crossSectionZ);
			crossSectionMinX = source.getMinXAtYZ(y, crossSectionZ);
			y++;
			crossSectionZ++;
			while (y <= maxY && crossSectionZ >= source.getMinZAtY(y) && crossSectionZ <= source.getMaxZAtY(y)) {
				crossSectionMaxY = y;
				int localMaxW = source.getMaxWAtYZ(y, crossSectionZ), localMinW = source.getMinWAtYZ(y, crossSectionZ);
				if (localMaxW > crossSectionMaxW) {
					crossSectionMaxW = localMaxW;
				}
				if (localMinW < crossSectionMinW) {
					crossSectionMinW = localMinW;
				}
				int localMaxX = source.getMaxXAtYZ(y, crossSectionZ), localMinX = source.getMinXAtYZ(y, crossSectionZ);
				if (localMaxX > crossSectionMaxX) {
					crossSectionMaxX = localMaxX;
				}
				if (localMinX < crossSectionMinX) {
					crossSectionMinX = localMinX;
				}
				y++;
				crossSectionZ++;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int getMinX() { 
		return crossSectionMinW; 
	}

	@Override
	public int getMaxX() { 
		return crossSectionMaxW; 
	}

	@Override
	public int getMinXAtY(int y) {
		return source.getMinWAtX(y);
	}

	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxWAtX(y);
	}

	@Override
	public int getMinXAtZ(int z) {
		return source.getMinWAtYZ(z, z + zOffsetFromY);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxWAtYZ(z, z + zOffsetFromY);
	}

	@Override
	public int getMinX(int y, int z) {
		return source.getMinW(y, z, z + zOffsetFromY);
	}

	@Override
	public int getMaxX(int y, int z) {
		return source.getMaxW(y, z, z + zOffsetFromY);
	}

	@Override
	public int getMinY() { 
		return crossSectionMinX;
	}

	@Override
	public int getMaxY() { 
		return crossSectionMaxX;
	}

	@Override
	public int getMinYAtX(int x) { 
		return source.getMinXAtW(x);
	}

	@Override
	public int getMaxYAtX(int x) { 
		return source.getMaxXAtW(x);
	}

	@Override
	public int getMinYAtZ(int z) {
		return source.getMinXAtYZ(z, z + zOffsetFromY);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxXAtYZ(z, z + zOffsetFromY);
	}

	@Override
	public int getMinY(int x, int z) {
		return source.getMinX(x, z, z + zOffsetFromY);
	}

	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxX(x, z, z + zOffsetFromY);
	}

	@Override
	public int getMinZ() {
		return crossSectionMinY;
	}

	@Override
	public int getMaxZ() { 
		return crossSectionMaxY;
	}

	@Override
	public int getMinZAtX(int x) {
		for (int crossSectionY = crossSectionMinY, crossSectionZ = crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ++) {
			int localMaxW = source.getMaxWAtYZ(crossSectionY, crossSectionZ), localMinW = source.getMinWAtYZ(crossSectionY, crossSectionZ);
			if (x >= localMinW && x <= localMaxW) {
				return crossSectionY;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}

	@Override
	public int getMaxZAtX(int x) {
		for (int crossSectionY = crossSectionMaxY, crossSectionZ = crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ--) {
			int localMaxW = source.getMaxWAtYZ(crossSectionY, crossSectionZ), localMinW = source.getMinWAtYZ(crossSectionY, crossSectionZ);
			if (x >= localMinW && x <= localMaxW) {
				return crossSectionY;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}

	@Override
	public int getMinZAtY(int y) { 
		for (int crossSectionY = crossSectionMinY, crossSectionZ = crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ++) {
			int localMaxX = source.getMaxXAtYZ(crossSectionY, crossSectionZ), localMinX = source.getMinXAtYZ(crossSectionY, crossSectionZ);
			if (y >= localMinX && y <= localMaxX) {
				return crossSectionY;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}

	@Override
	public int getMaxZAtY(int y) {
		for (int crossSectionY = crossSectionMaxY, crossSectionZ = crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ--) {
			int localMaxX = source.getMaxXAtYZ(crossSectionY, crossSectionZ), localMinX = source.getMinXAtYZ(crossSectionY, crossSectionZ);
			if (y >= localMinX && y <= localMaxX) {
				return crossSectionY;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}

	@Override
	public int getMinZ(int x, int y) { 
		for (int crossSectionY = crossSectionMinY, crossSectionZ = crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ++) {
			int localMaxW = source.getMaxWAtYZ(crossSectionY, crossSectionZ), localMinW = source.getMinWAtYZ(crossSectionY, crossSectionZ);
			if (x >= localMinW && x <= localMaxW) {
				int localMaxX = source.getMaxX(x, crossSectionY, crossSectionZ), localMinX = source.getMinX(x, crossSectionY, crossSectionZ);
				if (y >= localMinX && y <= localMaxX) {
					return crossSectionY;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxZ(int x, int y) {
		for (int crossSectionY = crossSectionMaxY, crossSectionZ = crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ--) {
			int localMaxW = source.getMaxWAtYZ(crossSectionY, crossSectionZ), localMinW = source.getMinWAtYZ(crossSectionY, crossSectionZ);
			if (x >= localMinW && x <= localMaxW) {
				int localMaxX = source.getMaxX(x, crossSectionY, crossSectionZ), localMinX = source.getMinX(x, crossSectionY, crossSectionZ);
				if (y >= localMinX && y <= localMaxX) {
					return crossSectionY;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getBounds()) {
			throw new UnsupportedOperationException("Cross section is out of bounds.");
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
		String path = source.getSubfolderPath() + "/" + source.getZLabel() + "=" + source.getYLabel();
		if (zOffsetFromY < 0) {
			path += zOffsetFromY;
		} else if (zOffsetFromY > 0) {
			path += "+" + zOffsetFromY;
		}
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
