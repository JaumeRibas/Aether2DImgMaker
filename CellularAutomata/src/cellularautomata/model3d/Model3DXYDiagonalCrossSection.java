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
package cellularautomata.model3d;

import java.io.FileNotFoundException;
import java.io.IOException;

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
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
	}
	
	@Override
	public String getXLabel() {
		return source.getZLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getXLabel();
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
			crossSectionMaxZ = source.getMaxZ(x, crossSectionY);
			crossSectionMinZ = source.getMinZ(x, crossSectionY);
			x++;
			crossSectionY += slope;
			while (x <= maxX && crossSectionY >= source.getMinYAtX(x) && crossSectionY <= source.getMaxYAtX(x)) {
				crossSectionMaxX = x;
				int localMaxZ = source.getMaxZ(x, crossSectionY), localMinZ = source.getMinZ(x, crossSectionY);
				if (localMaxZ > crossSectionMaxZ) {
					crossSectionMaxZ = localMaxZ;
				}
				if (localMinZ < crossSectionMinZ) {
					crossSectionMinZ = localMinZ;
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
		return crossSectionMinZ;
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinZ(y, slope*y + yOffsetFromX);
	}
	
	@Override
	public int getMaxX() {
		return crossSectionMaxZ;
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxZ(y, slope*y + yOffsetFromX);
	}
	
	@Override
	public int getMinY() {
		return crossSectionMinX;
	}
	
	@Override
	public int getMinY(int x) {
		for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY+=slope) {
			int localMaxZ = source.getMaxZ(crossSectionX, crossSectionY), localMinZ = source.getMinZ(crossSectionX, crossSectionY);
			if (x >= localMinZ && x <= localMaxZ) {
				return crossSectionX;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}
	
	@Override
	public int getMaxY() {
		return crossSectionMaxX;
	}
	
	@Override
	public int getMaxY(int x) {
		for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY-=slope) {
			int localMaxZ = source.getMaxZ(crossSectionX, crossSectionY), localMinZ = source.getMinZ(crossSectionX, crossSectionY);
			if (x >= localMinZ && x <= localMaxZ) {
				return crossSectionX;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
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
		StringBuilder path = new StringBuilder();
		path.append(source.getSubfolderPath()).append("/").append(source.getYLabel()).append("=");
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
