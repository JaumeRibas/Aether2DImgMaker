package cellularautomata.grid2d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Grid2DIterator<G extends Grid2D, T> implements Iterator<T> {
	
	protected G grid;
	protected int x;
	protected int y;
	
	public Grid2DIterator(G grid) {
		this.grid = grid;
		this.x = grid.getMinX();
		this.y = grid.getMinY(x);
	}

	@Override
	public boolean hasNext() {
		return x <= grid.getMaxX();
	}

	@Override
	public T next() {
		if (x > grid.getMaxX())
			throw new NoSuchElementException();
		T next = null;
		try {
			next = getFromGridPosition(x, y);
		} catch (Exception e) {
			throw new NoSuchElementException(e.getMessage());//what should be done here?
		}
		if (y < grid.getMaxY(x)) {
			y++;
		} else {
			x++;
			if (x <= grid.getMaxX()) {
				y = grid.getMinY(x);
			}
		}
		return next;
	}
	
	protected abstract T getFromGridPosition(int x, int y) throws Exception;

}
