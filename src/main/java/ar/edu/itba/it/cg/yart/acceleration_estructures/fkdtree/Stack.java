package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;


public class Stack {
	
	public int index;
	private int kELEMENTS = 1000000; // Danger!!
	private StackElement[] stack;
	
	public Stack() {
		stack = new StackElement[kELEMENTS];
		for (int i = 0; i < stack.length; i++) {
			stack[i] = new StackElement();
		}
	}
	
	public boolean isEmpty() {
		return index == 0;
	}
	
	public int peek() {
		return index;
	}
	
	public void push(final KDNode node, final double min, final double max) {
		StackElement e = stack[index++];
		e.node = node;
		e.min = min;
		e.max = max;
	}
	
	public StackElement pop() {
		return stack[--index];
	}
	
	public class StackElement {
		public KDNode node;
		public double min;
		public double max;
		
		private StackElement(){};
		
	}

	@Override
	public String toString() {
		return "[Index: " + index + "]";
	}
}

