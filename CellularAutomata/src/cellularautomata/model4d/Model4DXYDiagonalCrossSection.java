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

public class Model4DXYDiagonalCrossSection<Source_Type extends Model4D> implements Model3D {

	protected Source_Type source;
	protected int slope;
	protected int yOffsetFromX;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model4DXYDiagonalCrossSection(Source_Type source, boolean positiveSlope, int yOffsetFromX) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.yOffsetFromX = yOffsetFromX;
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
	    int x = source.getMinX();
	    int maxX = source.getMaxX();
	    int crossSectionY = slope*x + yOffsetFromX;
	    while (x <= maxX && (crossSectionY < source.getMinYAtX(x) || crossSectionY > source.getMaxYAtX(x))) {
	        x++;
	        crossSectionY += slope;
	    }
	    if (x <= maxX) {
	        crossSectionMinX = x;
	        crossSectionMaxX = x;
	        crossSectionMinW = source.getMinWAtXY(x, crossSectionY);
	        crossSectionMaxW = source.getMaxWAtXY(x, crossSectionY);
	        crossSectionMinZ = source.getMinZAtXY(x, crossSectionY);
	        crossSectionMaxZ = source.getMaxZAtXY(x, crossSectionY);
	        x++;
	        crossSectionY += slope;
	        while (x <= maxX && crossSectionY >= source.getMinYAtX(x) && crossSectionY <= source.getMaxYAtX(x)) {
	            crossSectionMaxX = x;
	            int localMinW = source.getMinWAtXY(x, crossSectionY);
	            if (localMinW < crossSectionMinW) {
	                crossSectionMinW = localMinW;
	            }
	            int localMaxW = source.getMaxWAtXY(x, crossSectionY);
	            if (localMaxW > crossSectionMaxW) {
	                crossSectionMaxW = localMaxW;
	            }
	            int localMinZ = source.getMinZAtXY(x, crossSectionY);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtXY(x, crossSectionY);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
	            }
	            x++;
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
	    return source.getMinWAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxWAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinXAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinW(crossSectionX, crossSectionY, z);
	    int localMinW;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinW = source.getMinW(crossSectionX, crossSectionY, z)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxW(crossSectionX, crossSectionY, z);
	    int localMaxW;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxW = source.getMaxW(crossSectionX, crossSectionY, z)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinX(int y, int z) {
	    return source.getMinW(y, slope*y + yOffsetFromX, z);
	}

	@Override
	public int getMaxX(int y, int z) {
	    return source.getMaxW(y, slope*y + yOffsetFromX, z);
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
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (x >= source.getMinWAtXY(crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxYAtX(int x) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (x >= source.getMinWAtXY(crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinYAtZ(int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (z >= source.getMinZAtXY(crossSectionX, crossSectionY)
	                && z <= source.getMaxZAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxYAtZ(int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (z >= source.getMinZAtXY(crossSectionX, crossSectionY)
	                && z <= source.getMaxZAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinY(int x, int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (x >= source.getMinWAtXY(crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtXY(crossSectionX, crossSectionY)
	                && z >= source.getMinZ(x, crossSectionX, crossSectionY)
	                && z <= source.getMaxZ(x, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxY(int x, int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (x >= source.getMinWAtXY(crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtXY(crossSectionX, crossSectionY)
	                && z >= source.getMinZ(x, crossSectionX, crossSectionY)
	                && z <= source.getMaxZ(x, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinZ(x, crossSectionX, crossSectionY);
	    int localMinZ;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinZ = source.getMinZ(x, crossSectionX, crossSectionY)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxZ(x, crossSectionX, crossSectionY);
	    int localMaxZ;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxZ = source.getMaxZ(x, crossSectionX, crossSectionY)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinZAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxZAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinZ(int x, int y) {
	    return source.getMinZ(x, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxZ(int x, int y) {
	    return source.getMaxZ(x, y, slope*y + yOffsetFromX);
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
	    path.append(source.getXLabel());
	    if (yOffsetFromX < 0) {
	        path.append(yOffsetFromX);
	    } else if (yOffsetFromX > 0) {
	        path.append("+").append(yOffsetFromX);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
	    source.backUp(backupPath, backupName);
	}

}
