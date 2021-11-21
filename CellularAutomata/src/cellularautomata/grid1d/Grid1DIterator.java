package cellularautomata.grid1d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Grid1DIterator<G extends Grid1D, T> implements Iterator<T> {
	
	protected G grid;
	protected int x;
	
	public Grid1DIterator(G grid) {
		this.grid = grid;
		this.x = grid.getMinX();
	}

	@Override
	public boolean hasNext() {
		return x <= grid.getMaxX();
	}

	@Override
	public T next() {
		if (x > grid.getMaxX())
			throw new NoSuchElementException();
		T next = getFromGridPosition(x);
		x++;
		return next;
	}
	
	protected abstract T getFromGridPosition(int x);

}
