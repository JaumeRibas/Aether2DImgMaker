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

public class Model4DWYDiagonalCrossSection<Source_Type extends Model4D> implements Model3D {

	protected Source_Type source;
	protected int slope;
	protected int yOffsetFromW;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model4DWYDiagonalCrossSection(Source_Type source, boolean positiveSlope, int yOffsetFromW) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.yOffsetFromW = yOffsetFromW;
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
	    return source.getXLabel();
	}

	@Override
	public String getZLabel() {
	    return source.getZLabel();
	}

	protected boolean getBounds() {
	    int w = source.getMinW();
	    int maxW = source.getMaxW();
	    int crossSectionY = slope*w + yOffsetFromW;
	    while (w <= maxW && (crossSectionY < source.getMinYAtW(w) || crossSectionY > source.getMaxYAtW(w))) {
	        w++;
	        crossSectionY += slope;
	    }
	    if (w <= maxW) {
	        crossSectionMinW = w;
	        crossSectionMaxW = w;
	        crossSectionMinX = source.getMinXAtWY(w, crossSectionY);
	        crossSectionMaxX = source.getMaxXAtWY(w, crossSectionY);
	        crossSectionMinZ = source.getMinZAtWY(w, crossSectionY);
	        crossSectionMaxZ = source.getMaxZAtWY(w, crossSectionY);
	        w++;
	        crossSectionY += slope;
	        while (w <= maxW && crossSectionY >= source.getMinYAtW(w) && crossSectionY <= source.getMaxYAtW(w)) {
	            crossSectionMaxW = w;
	            int localMinX = source.getMinXAtWY(w, crossSectionY);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxXAtWY(w, crossSectionY);
	            if (localMaxX > crossSectionMaxX) {
	                crossSectionMaxX = localMaxX;
	            }
	            int localMinZ = source.getMinZAtWY(w, crossSectionY);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtWY(w, crossSectionY);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
	            }
	            w++;
	            crossSectionY += slope;
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
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (y >= source.getMinXAtWY(crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (y >= source.getMinXAtWY(crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (z >= source.getMinZAtWY(crossSectionW, crossSectionY)
	                && z <= source.getMaxZAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (z >= source.getMinZAtWY(crossSectionW, crossSectionY)
	                && z <= source.getMaxZAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinX(int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (y >= source.getMinXAtWY(crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtWY(crossSectionW, crossSectionY)
	                && z >= source.getMinZ(crossSectionW, y, crossSectionY)
	                && z <= source.getMaxZ(crossSectionW, y, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxX(int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (y >= source.getMinXAtWY(crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtWY(crossSectionW, crossSectionY)
	                && z >= source.getMinZ(crossSectionW, y, crossSectionY)
	                && z <= source.getMaxZ(crossSectionW, y, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    return source.getMinXAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxXAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinX(crossSectionW, crossSectionY, z);
	    int localMinX;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinX = source.getMinX(crossSectionW, crossSectionY, z)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxX(crossSectionW, crossSectionY, z);
	    int localMaxX;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxX = source.getMaxX(crossSectionW, crossSectionY, z)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinY(int x, int z) {
	    return source.getMinX(x, slope*x + yOffsetFromW, z);
	}

	@Override
	public int getMaxY(int x, int z) {
	    return source.getMaxX(x, slope*x + yOffsetFromW, z);
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
	    return source.getMinZAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxZAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinZ(crossSectionW, y, crossSectionY);
	    int localMinZ;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZ(crossSectionW, y, crossSectionY)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxZ(crossSectionW, y, crossSectionY);
	    int localMaxZ;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZ(crossSectionW, y, crossSectionY)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZ(int x, int y) {
	    return source.getMinZ(x, y, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxZ(int x, int y) {
	    return source.getMaxZ(x, y, slope*x + yOffsetFromW);
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
	    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append("/").append(source.getYLabel()).append("=");
	    if (slope == -1) {
	        path.append("-");
	    }
	    path.append(source.getWLabel());
	    if (yOffsetFromW < 0) {
	        path.append(yOffsetFromW);
	    } else if (yOffsetFromW > 0) {
	        path.append("+").append(yOffsetFromW);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
