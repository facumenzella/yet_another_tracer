package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;


public class Stack {
	
	private int index;
	private int kELEMENTS = Integer.MAX_VALUE; // Danger!!
	private StackElement[] stack;
	
	public Stack() {
		stack = new StackElement[kELEMENTS];
		for (int i = 0; i < stack.length; i++) {
			stack[i] = new StackElement();
		}
	}
	
	public StackElement peek() {
		return stack[index-1];
	}
	
	public boolean isEmpty() {
		return index == 0;
	}
	
	public void push(final KDNode node, final double t, final double max) {
		StackElement e = stack[index++];
		e.node = node;
		e.t = t;
		e.max = max;
	}
	
	public StackElement pop() {
		return stack[--index];
	}
	
	public class StackElement {
		public KDNode node;
		public double t;
		public double max;
		
		private StackElement(){};
		
	}

	
}

