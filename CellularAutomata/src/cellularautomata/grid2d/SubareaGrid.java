package cellularautomata.grid2d;


public class SubareaGrid<G extends Grid2D> implements Grid2D { 

	private G baseGrid;
	private int subareaWidth;
	private int subareaHeight;
	
	public SubareaGrid(G baseGrid, int subareaWidth, int subareaHeight) {
		if (subareaWidth < 1) {
			throw new IllegalArgumentException("Subarea width cannot be smaller than one.");
		}
		if (subareaHeight < 1) {
			throw new IllegalArgumentException("Subarea height cannot be smaller than one.");
		}
		this.baseGrid = baseGrid;
		this.subareaWidth = subareaWidth;
		this.subareaHeight = subareaHeight;
	}
	
	@SuppressWarnings("unchecked")
	public G getSubareaAtPosition(int x, int y) {
		int minX = x * subareaWidth;
		int maxX = minX + subareaWidth - 1;
		int minY = y * subareaHeight;
		int maxY = minY + subareaHeight - 1;
		return (G) baseGrid.subGrid(minX, maxX, minY, maxY);
	}

	@Override
	public int getMinX() {
		return baseGrid.getMinX() / subareaWidth;
	}

	@Override
	public int getMaxX() {
		return baseGrid.getMaxX() / subareaWidth;
	}

	@Override
	public int getMinY() {
		return baseGrid.getMinY() / subareaHeight;
	}

	@Override
	public int getMaxY() {
		return baseGrid.getMaxY() / subareaHeight;
	}
	
	@Override
	public int getMinX(int y) {
		int subareaMinY = y * subareaHeight;
		int subareaMaxY = subareaMinY + subareaHeight - 1;
		int subareaMinX = baseGrid.getMinX(subareaMinY);
		for (int baseY = subareaMinY + 1; baseY <= subareaMaxY; baseY++) {
			int minX = baseGrid.getMinX(baseY);
			if (minX < subareaMinX) {
				subareaMinX = minX;
			}
		}
		return subareaMinX / subareaWidth;
	}

	@Override
	public int getMaxX(int y) {
		int subareaMinY = y * subareaHeight;
		int subareaMaxY = subareaMinY + subareaHeight - 1;
		int subareaMaxX = baseGrid.getMaxX(subareaMinY);
		for (int baseY = subareaMinY + 1; baseY <= subareaMaxY; baseY++) {
			int maxX = baseGrid.getMaxX(baseY);
			if (maxX > subareaMaxX) {
				subareaMaxX = maxX;
			}
		}
		return subareaMaxX / subareaWidth;
	}

	@Override
	public int getMinY(int x) {
		int subareaMinX = x * subareaWidth;
		int subareaMaxX = subareaMinX + subareaWidth - 1;
		int subareaMinY = baseGrid.getMinY(subareaMinX);
		for (int baseX = subareaMinX + 1; baseX <= subareaMaxX; baseX++) {
			int minY = baseGrid.getMinY(baseX);
			if (minY < subareaMinY) {
				subareaMinY = minY;
			}
		}
		return subareaMinY / subareaHeight;
	}

	@Override
	public int getMaxY(int x) {
		int subareaMinX = x * subareaWidth;
		int subareaMaxX = subareaMinX + subareaWidth - 1;
		int subareaMaxY = baseGrid.getMaxY(subareaMinX);
		for (int baseX = subareaMinX + 1; baseX <= subareaMaxX; baseX++) {
			int maxY = baseGrid.getMaxY(baseX);
			if (maxY > subareaMaxY) {
				subareaMaxY = maxY;
			}
		}
		return subareaMaxY / subareaHeight;
	}

	@Override
	public G subGrid(int minX, int maxX, int minY, int maxY) {
		throw new UnsupportedOperationException();
	}

	public int getRegionWidth() {
		return subareaWidth;
	}

	public int getRegionHeight() {
		return subareaHeight;
	}
}
