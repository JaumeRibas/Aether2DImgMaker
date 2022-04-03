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

public class Model3DXZDiagonalCrossSection<Source_Type extends Model3D> implements Model2D {

	protected Source_Type source;
	protected int zOffsetFromX;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	
	public Model3DXZDiagonalCrossSection(Source_Type source, int zOffsetFromX) {		
		this.source = source;
		this.zOffsetFromX = zOffsetFromX;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
	}
	
	@Override
	public String getXLabel() {
		return source.getXLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getYLabel();
	}
	
	protected boolean getBounds() {
		int x = source.getMinX();
		int maxX = source.getMaxX();
		int crossSectionZ = x + zOffsetFromX;
		while (x <= maxX && (crossSectionZ < source.getMinZAtX(x) || crossSectionZ > source.getMaxZAtX(x))) {
			x++;
			crossSectionZ++;
		}
		if (x <= maxX) {
			crossSectionMinX = x;
			crossSectionMaxX = x;
			crossSectionMaxY = source.getMaxY(x, crossSectionZ);
			crossSectionMinY = source.getMinY(x, crossSectionZ);
			x++;
			crossSectionZ++;
			while (x <= maxX && crossSectionZ >= source.getMinZAtX(x) && crossSectionZ <= source.getMaxZAtX(x)) {
				crossSectionMaxX = x;
				int localMaxY = source.getMaxY(x, crossSectionZ), localMinY = source.getMinY(x, crossSectionZ);
				if (localMaxY > crossSectionMaxY) {
					crossSectionMaxY = localMaxY;
				}
				if (localMinY < crossSectionMinY) {
					crossSectionMinY = localMinY;
				}
				x++;
				crossSectionZ++;
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
	public int getMinX(int y) {
		for (int crossSectionX = crossSectionMinX, crossSectionZ = crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ++) {
			int localMaxY = source.getMaxY(crossSectionX, crossSectionZ), localMinY = source.getMinY(crossSectionX, crossSectionZ);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionX;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}
	
	@Override
	public int getMaxX() {
		return crossSectionMaxX;
	}
	
	@Override
	public int getMaxX(int y) {
		for (int crossSectionX = crossSectionMaxX, crossSectionZ = crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ--) {
			int localMaxY = source.getMaxY(crossSectionX, crossSectionZ), localMinY = source.getMinY(crossSectionX, crossSectionZ);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionX;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}
	
	@Override
	public int getMinY() {
		return crossSectionMinY;
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinY(x, x + zOffsetFromX);
	}
	
	@Override
	public int getMaxY() {
		return crossSectionMaxY;
	}
	
	@Override
	public int getMaxY(int x) {
		return source.getMaxY(x, x + zOffsetFromX);
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
		String path = source.getSubfolderPath() + "/" + source.getZLabel() + "=" + source.getXLabel();
		if (zOffsetFromX < 0) {
			path += zOffsetFromX;
		} else if (zOffsetFromX > 0) {
			path += "+" + zOffsetFromX;
		}
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
