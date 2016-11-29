package OfflinePoker;

public class GenList <Type> {
	public Node<Type> head; 
	public int length = 0;
	
	public void Add(Type value) {
		if(this.head != null)
			if (this.head.value != null) {
				Node<Type> newNode = new Node<Type>(), currentNode = this.head;
				while(currentNode.next != null)
					currentNode = currentNode.next;
				newNode.value = value; 
				currentNode.next = newNode;
			} 
			else {
				head.value = value;
			}
		else {
			this.head = new Node<Type>();
			this.head.value = value;
		}
		length++;
	}
	
	public Node<Type> Delete(Node<Type> delNode){
		if(this.head.value != null){
			Node<Type> curNode = head;
			if(this.head != delNode) {
				while((curNode.next != delNode) && (curNode.next != null)){	
					curNode = curNode.next;
				}
				if(curNode.next == null){
					System.out.println("Element ist nicht in der Liste und kann nicht gelöscht werden.");
					return null;
				}
				else {
					Node<Type> returnNode = curNode.next;
					if(curNode.next.next != null) {
						curNode.next = curNode.next.next;
						length--;
						return returnNode;
					}
					else {
						curNode.next = null;
						length--;
						return returnNode;
					}
				}
			}
			else {
				this.head = this.head.next;
				length--;
				return curNode;
			}
		}
		else {
			System.out.println("Element kann nicht gelöscht werden. Die Liste ist leer.");
			return null;
		}
	}
	
	public Node<Type> getItem(int i) {
		if(i <= length) {
			Node<Type> node = head;
			for(int index=0; index<i; index++)
				node = node.next;
			return node;
		}
		else {
			System.out.println("Element nicht gefunden. IndexOutOfBoundsExecution");
			return null;
		}
	}
}

class Node <Type> {
	public Type value = null;
	public Node<Type> next = null;
}