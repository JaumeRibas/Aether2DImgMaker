/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

public class Model4DWZDiagonalCrossSection<Source_Type extends Model4D> implements Model3D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromW;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model4DWZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromW) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromW = zOffsetFromW;
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
	    return source.getYLabel();
	}

	protected boolean getBounds() {
	    int w = source.getMinW();
	    int maxW = source.getMaxW();
	    int crossSectionZ = slope*w + zOffsetFromW;
	    while (w <= maxW && (crossSectionZ < source.getMinZAtW(w) || crossSectionZ > source.getMaxZAtW(w))) {
	        w++;
	        crossSectionZ += slope;
	    }
	    if (w <= maxW) {
	        crossSectionMinW = w;
	        crossSectionMaxW = w;
	        crossSectionMinX = source.getMinXAtWZ(w, crossSectionZ);
	        crossSectionMaxX = source.getMaxXAtWZ(w, crossSectionZ);
	        crossSectionMinY = source.getMinYAtWZ(w, crossSectionZ);
	        crossSectionMaxY = source.getMaxYAtWZ(w, crossSectionZ);
	        w++;
	        crossSectionZ += slope;
	        while (w <= maxW && crossSectionZ >= source.getMinZAtW(w) && crossSectionZ <= source.getMaxZAtW(w)) {
	            crossSectionMaxW = w;
	            int localMinX = source.getMinXAtWZ(w, crossSectionZ);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxXAtWZ(w, crossSectionZ);
	            if (localMaxX > crossSectionMaxX) {
	                crossSectionMaxX = localMaxX;
	            }
	            int localMinY = source.getMinYAtWZ(w, crossSectionZ);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxYAtWZ(w, crossSectionZ);
	            if (localMaxY > crossSectionMaxY) {
	                crossSectionMaxY = localMaxY;
	            }
	            w++;
	            crossSectionZ += slope;
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
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (y >= source.getMinXAtWZ(crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (y >= source.getMinXAtWZ(crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (z >= source.getMinYAtWZ(crossSectionW, crossSectionZ)
	                && z <= source.getMaxYAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (z >= source.getMinYAtWZ(crossSectionW, crossSectionZ)
	                && z <= source.getMaxYAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinX(int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (y >= source.getMinXAtWZ(crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtWZ(crossSectionW, crossSectionZ)
	                && z >= source.getMinY(crossSectionW, y, crossSectionZ)
	                && z <= source.getMaxY(crossSectionW, y, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxX(int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (y >= source.getMinXAtWZ(crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtWZ(crossSectionW, crossSectionZ)
	                && z >= source.getMinY(crossSectionW, y, crossSectionZ)
	                && z <= source.getMaxY(crossSectionW, y, crossSectionZ)) {
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
	    return source.getMinXAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxXAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinX(crossSectionW, z, crossSectionZ);
	    int localMinX;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinX = source.getMinX(crossSectionW, z, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxX(crossSectionW, z, crossSectionZ);
	    int localMaxX;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxX = source.getMaxX(crossSectionW, z, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinY(int x, int z) {
	    return source.getMinX(x, z, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxY(int x, int z) {
	    return source.getMaxX(x, z, slope*x + zOffsetFromW);
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
	    return source.getMinYAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxYAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinY(crossSectionW, y, crossSectionZ);
	    int localMinY;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinY = source.getMinY(crossSectionW, y, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxY(crossSectionW, y, crossSectionZ);
	    int localMaxY;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxY(crossSectionW, y, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZ(int x, int y) {
	    return source.getMinY(x, y, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxZ(int x, int y) {
	    return source.getMaxY(x, y, slope*x + zOffsetFromW);
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
	    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append("/").append(source.getZLabel()).append("=");
	    if (slope == -1) {
	        path.append("-");
	    }
	    path.append(source.getWLabel());
	    if (zOffsetFromW < 0) {
	        path.append(zOffsetFromW);
	    } else if (zOffsetFromW > 0) {
	        path.append("+").append(zOffsetFromW);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
