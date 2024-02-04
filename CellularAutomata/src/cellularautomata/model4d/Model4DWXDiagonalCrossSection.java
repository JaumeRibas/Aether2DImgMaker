/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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

import cellularautomata.model3d.Model3D;

public class Model4DWXDiagonalCrossSection<Source_Type extends Model4D> implements Model3D {

	protected Source_Type source;
	protected int slope;
	protected int xOffsetFromW;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model4DWXDiagonalCrossSection(Source_Type source, boolean positiveSlope, int xOffsetFromW) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.xOffsetFromW = xOffsetFromW;
	    if (!getBounds()) {
	        throw new IllegalArgumentException("The cross section is out of bounds.");
	    }
	}

	@Override
	public String getXLabel() {
	    return source.getWLabel();
	}

	@Override
	public String getYLabel() {
	    return source.getYLabel();
	}

	@Override
	public String getZLabel() {
	    return source.getZLabel();
	}

	protected boolean getBounds() {
	    int w = source.getMinW();
	    int maxW = source.getMaxW();
	    int crossSectionX = slope*w + xOffsetFromW;
	    while (w <= maxW && (crossSectionX < source.getMinXAtW(w) || crossSectionX > source.getMaxXAtW(w))) {
	        w++;
	        crossSectionX += slope;
	    }
	    if (w <= maxW) {
	        crossSectionMinW = w;
	        crossSectionMaxW = w;
	        crossSectionMinY = source.getMinYAtWX(w, crossSectionX);
	        crossSectionMaxY = source.getMaxYAtWX(w, crossSectionX);
	        crossSectionMinZ = source.getMinZAtWX(w, crossSectionX);
	        crossSectionMaxZ = source.getMaxZAtWX(w, crossSectionX);
	        w++;
	        crossSectionX += slope;
	        while (w <= maxW && crossSectionX >= source.getMinXAtW(w) && crossSectionX <= source.getMaxXAtW(w)) {
	            crossSectionMaxW = w;
	            int localMinY = source.getMinYAtWX(w, crossSectionX);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxYAtWX(w, crossSectionX);
	            if (localMaxY > crossSectionMaxY) {
	                crossSectionMaxY = localMaxY;
	            }
	            int localMinZ = source.getMinZAtWX(w, crossSectionX);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtWX(w, crossSectionX);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
	            }
	            w++;
	            crossSectionX += slope;
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
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (y >= source.getMinYAtWX(crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (y >= source.getMinYAtWX(crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (z >= source.getMinZAtWX(crossSectionW, crossSectionX)
	                && z <= source.getMaxZAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (z >= source.getMinZAtWX(crossSectionW, crossSectionX)
	                && z <= source.getMaxZAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinX(int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (y >= source.getMinYAtWX(crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtWX(crossSectionW, crossSectionX)
	                && z >= source.getMinZ(crossSectionW, crossSectionX, y)
	                && z <= source.getMaxZ(crossSectionW, crossSectionX, y)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxX(int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (y >= source.getMinYAtWX(crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtWX(crossSectionW, crossSectionX)
	                && z >= source.getMinZ(crossSectionW, crossSectionX, y)
	                && z <= source.getMaxZ(crossSectionW, crossSectionX, y)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinY() {
	    return crossSectionMinY;
	}

	@Override
	public int getMaxY() {
	    return crossSectionMaxY;
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinYAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxYAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinY(crossSectionW, crossSectionX, z);
	    int localMinY;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinY = source.getMinY(crossSectionW, crossSectionX, z)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxY(crossSectionW, crossSectionX, z);
	    int localMaxY;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxY(crossSectionW, crossSectionX, z)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinY(int x, int z) {
	    return source.getMinY(x, slope*x + xOffsetFromW, z);
	}

	@Override
	public int getMaxY(int x, int z) {
	    return source.getMaxY(x, slope*x + xOffsetFromW, z);
	}

	@Override
	public int getMinZ() {
	    return crossSectionMinZ;
	}

	@Override
	public int getMaxZ() {
	    return crossSectionMaxZ;
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinZAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxZAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinZ(crossSectionW, crossSectionX, y);
	    int localMinZ;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZ(crossSectionW, crossSectionX, y)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxZ(crossSectionW, crossSectionX, y);
	    int localMaxZ;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZ(crossSectionW, crossSectionX, y)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZ(int x, int y) {
	    return source.getMinZ(x, slope*x + xOffsetFromW, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
	    return source.getMaxZ(x, slope*x + xOffsetFromW, y);
	}

	@Override
	public Boolean nextStep() throws Exception {
	    Boolean changed = source.nextStep();
	    if (!getBounds()) {
	        throw new UnsupportedOperationException("The cross section is out of bounds.");
	    }
	    return changed;
	}

	@Override
	public Boolean isChanged() {
	    return source.isChanged();
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
	    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append("/").append(source.getXLabel()).append("=");
	    if (slope == -1) {
	        path.append("-");
	    }
	    path.append(source.getWLabel());
	    if (xOffsetFromW < 0) {
	        path.append(xOffsetFromW);
	    } else if (xOffsetFromW > 0) {
	        path.append("+").append(xOffsetFromW);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
