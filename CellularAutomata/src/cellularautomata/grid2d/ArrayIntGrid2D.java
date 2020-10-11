package cellularautomata.grid2d;

/**
 * Represents a fixed size 2D region of an integer grid backed by a 2D int array.
 * The shape and orientation of the region is such that no line parallel to an axis crosses its bounds in more than two places.
 * 
 * @author Jaume
 *
 */
public class ArrayIntGrid2D implements IntGrid2D {
	
	private int[][] array;
	private int minX;
	private int maxX;
	private int[] localYMinima;
	private int minY;
	private int maxY;
	
	private static final String UNSUPPORTED_SHAPE_ERROR = 
			"The shape and orientation of the region must be such that no line parallel to an axis crosses its bounds in more than two places.";
	
	/**
	 * Constructs an {@code ArrayIntGrid2D} with the specified bounds
	 * 
	 * @param minX the smallest x-coordinate within the region
	 * @param localYMinima an array of the smallest y-coordinates at each x-coordinate of the region. Beginning at {@code minX}.
	 * @param localYMaxima an array of the greatest y-coordinates at each x-coordinate of the region. Beginning at {@code minX}.
	 */
	public ArrayIntGrid2D(int minX, int[] localYMinima, int[] localYMaxima) {
		if (localYMinima.length != localYMaxima.length) {
			throw new IllegalArgumentException("Local y minima length must be equal to maxima length.");
		}
		if (localYMinima.length == 0) {
			throw new IllegalArgumentException("Local y minima length cannot be 0.");
		}
		long longMaxX = (long)localYMinima.length + minX - 1;
		if (longMaxX > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max x (" + longMaxX + ") is too big. It cannot be bigger than " + Integer.MAX_VALUE + ".");
		}
		this.minX = minX;
		maxX = (int)longMaxX;
		this.localYMinima = localYMinima;
		array = new int[localYMinima.length][];
		boolean maximaDescending = false;
		boolean minimaAscending = false;
		int previousLocalMinY = localYMinima[0];
		int previousLocalMaxY = localYMaxima[0];
		minY = previousLocalMinY;
		maxY = previousLocalMaxY;
		for (int i = 0; i < array.length; i++) {
			int localMinY = localYMinima[i];
			int localMaxY = localYMaxima[i];
			if (localMinY > localMaxY) {
				throw new IllegalArgumentException("Local min y greater than local max y at index " + i);
			}
			long size = (long)localMaxY - localMinY + 1;
			if (size > Integer.MAX_VALUE) {
				throw new IllegalArgumentException(
						"Size in y at index " + i + " (" + size + ") is too big. It cannot be bigger than " + Integer.MAX_VALUE + ".");
			}
			if (minimaAscending) {
				if (localMinY < previousLocalMinY) {
					throw new IllegalArgumentException("Unsupported local y minima. " + UNSUPPORTED_SHAPE_ERROR);
				}
			} else if (localMinY > previousLocalMinY) {
				minimaAscending = true;
			} else if (localMinY < minY) {
				minY = localMinY;
			}
			if (maximaDescending) {
				if (localMaxY > previousLocalMaxY) {
					throw new IllegalArgumentException("Unsupported local y maxima. " + UNSUPPORTED_SHAPE_ERROR);
				}
			} else if (localMaxY < previousLocalMaxY) {
				maximaDescending = true;
			} else if (localMaxY > maxY) {
				maxY = localMaxY;
			}
			array[i] = new int[localMaxY - localMinY + 1];
			previousLocalMinY = localMinY;
			previousLocalMaxY = localMaxY;
		}
	}

	@Override
	public int getMinX() {
		return minX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public int getMinY() {
		return minY;
	}

	@Override
	public int getMaxY() {
		return maxY;
	}
	
	@Override
	public int getMinX(int y) {
		int localMinX = minX;
		int edgeMinY = localYMinima[0];
		int edgeMaxY = array[0].length + edgeMinY - 1;
		if (y > edgeMaxY) {
			int i = 1;
			localMinX++;
			while (y > array[i].length + localYMinima[i] - 1) {
				localMinX++;
				i++;
			}
		} else if (y < edgeMinY) {
			int i = 1;
			localMinX++;
			while (y < localYMinima[i]) {
				localMinX++;
				i++;
			}
		}
		return localMinX;
	}
	
	@Override
	public int getMaxX(int y) {
		int localMaxX = maxX;
		int i = array.length - 1;
		int edgeMinY = localYMinima[i];
		int edgeMaxY = array[i].length + edgeMinY - 1;
		if (y > edgeMaxY) {
			i--;
			localMaxX--;
			while (y > array[i].length + localYMinima[i] - 1) {
				localMaxX--;
				i--;
			}
		} else if (y < edgeMinY) {
			i--;
			localMaxX--;
			while (y < localYMinima[i]) {
				localMaxX--;
				i--;
			}
		}
		return localMaxX;
	}
	
	@Override
	public int getMinY(int x) {
		return localYMinima[x - minX];
	}
	
	@Override
	public int getMaxY(int x) {
		int index = x - minX;
		return array[index].length + localYMinima[index] - 1;
	}

	@Override
	public int getValueAtPosition(int x, int y) {
		int i = x - minX;
		int j = y - localYMinima[i];
		return array[i][j];
	}
	
	public void setValueAtPosition(int x, int y, int value) {
		int i = x - minX;
		int j = y - localYMinima[i];
		array[i][j] = value;
	}
	
	public int addAndGetValueAtPosition(int x, int y, int value) {
		int i = x - minX;
		int j = y - localYMinima[i];
		return array[i][j] += value;
	}
}
