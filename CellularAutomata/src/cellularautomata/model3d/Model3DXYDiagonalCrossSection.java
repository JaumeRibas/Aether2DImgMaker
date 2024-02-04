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
package cellularautomata.model3d;

import cellularautomata.model2d.Model2D;

public class Model3DXYDiagonalCrossSection<Source_Type extends Model3D> implements Model2D {

	protected Source_Type source;
	protected int slope;
	protected int yOffsetFromX;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model3DXYDiagonalCrossSection(Source_Type source, boolean positiveSlope, int yOffsetFromX) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.yOffsetFromX = yOffsetFromX;
	    if (!getBounds()) {
	        throw new IllegalArgumentException("The cross section is out of bounds.");
	    }
	}

	@Override
	public String getXLabel() {
	    return source.getXLabel();
	}

	@Override
	public String getYLabel() {
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
	        crossSectionMinZ = source.getMinZ(x, crossSectionY);
	        crossSectionMaxZ = source.getMaxZ(x, crossSectionY);
	        x++;
	        crossSectionY += slope;
	        while (x <= maxX && crossSectionY >= source.getMinYAtX(x) && crossSectionY <= source.getMaxYAtX(x)) {
	            crossSectionMaxX = x;
	            int localMinZ = source.getMinZ(x, crossSectionY);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZ(x, crossSectionY);
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
	    return crossSectionMinX;
	}

	@Override
	public int getMaxX() {
	    return crossSectionMaxX;
	}

	@Override
	public int getMinX(int y) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (y >= source.getMinZ(crossSectionX, crossSectionY)
	                && y <= source.getMaxZ(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxX(int y) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (y >= source.getMinZ(crossSectionX, crossSectionY)
	                && y <= source.getMaxZ(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinY() {
	    return crossSectionMinZ;
	}

	@Override
	public int getMaxY() {
	    return crossSectionMaxZ;
	}

	@Override
	public int getMinY(int x) {
	    return source.getMinZ(x, slope*x + yOffsetFromX);
	}

	@Override
	public int getMaxY(int x) {
	    return source.getMaxZ(x, slope*x + yOffsetFromX);
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
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
