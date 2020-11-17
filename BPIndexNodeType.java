import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class BPIndexNodeType extends BPNodeType implements Serializable
{
	private static final long serialVersionUID = 3511783724716845372L;

	protected ArrayList<BPNodeType> childs;
	
	public BPIndexNodeType(int degree, int keyType, String path)
	{
		super(degree, keyType, path);
		this.childs = new ArrayList<BPNodeType>();
	}

	public void insertValue(Object key, RecordType newValue)
	{
		int index = this.matchingKey(key);
		BPNodeType childNode = this.childs.get(index);
		childNode.insertValue(key, newValue);
		
		if(childNode.isOverflowed())
		{
			BPNodeType rightChild = childNode.splitNode();
			
			this.keys.add(index, rightChild.getFirstLeafKey());
			this.childs.add(index + 1, rightChild);
			
			if(rightChild instanceof BPLeafNodeType)
			{
				try
				{
					((BPLeafNodeType) rightChild).serialize();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		if(childNode instanceof BPLeafNodeType)
		{
			BPLeafNodeType temp = (BPLeafNodeType) childNode;
			
			try
			{
				temp.serialize();
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void addValue(Object key, RecordType newValue)
	{
		int index = this.matchingKey(key);
		
		BPNodeType childNode = this.childs.get(index);
		
		childNode.addValue(key, newValue);
		
		if(childNode.isOverflowed())
		{
			BPNodeType rightChild = childNode.splitNode();
			Object rightChildKey = rightChild.getFirstLeafKey();
			this.keys.add(index, rightChildKey);
			this.childs.add(index + 1, rightChild);
		}
	}
	
	public void removeValue(Object key)
	{
		int index = this.matchingKey(key);
		
		BPNodeType child = this.getChild(index);
		child.removeValue(key);
		
		if(child.isUnderflowed())
		{
			BPNodeType leftChild;
			BPNodeType rightChild;
			
			if(index == 0) // 오른쪽 노드랑 합침
			{
				leftChild = child;
				rightChild = this.getChild(index + 1);
				leftChild.mergeNode(rightChild);
				this.keys.remove(index);
				this.childs.remove(index + 1);
			}
			else // 왼쪽 노드랑 합침
			{
				leftChild = this.getChild(index - 1);
				rightChild = child;
				leftChild.mergeNode(rightChild);
				this.keys.remove(index - 1);
				this.childs.remove(index);
			}
			
			if(leftChild.isOverflowed())
			{
				BPNodeType newRightChild = leftChild.splitNode();
				
				if(newRightChild instanceof BPLeafNodeType)
				{
					try
					{
						((BPLeafNodeType) newRightChild).serialize();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				
				if(index == 0)
				{
					this.keys.add(index, newRightChild.getFirstLeafKey());
					this.childs.add(index + 1, newRightChild);
					this.printValues();
				}
				else
				{
					this.keys.add(index - 1, newRightChild.getFirstLeafKey());
					this.childs.add(index, newRightChild);
					this.printValues();
				}
				
				if(leftChild instanceof BPLeafNodeType)
				{
					BPLeafNodeType temp = (BPLeafNodeType) leftChild;
					
					try
					{
						temp.serialize();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			if(child instanceof BPLeafNodeType)
			{
				BPLeafNodeType temp = (BPLeafNodeType) child;
				
				try
				{
					temp.serialize();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public BPNodeType getChild(int index)
	{
		if(index == -1 || this.childs.size() == 0)
			return null;
		else
			return this.childs.get(index);
	}
	
	public boolean isOverflowed()
	{
		return this.childs.size() > this.degree;
	}

	public boolean isUnderflowed()
	{
		return this.childs.size() < (this.degree + 1) / 2;
	}

	public BPNodeType splitNode()
	{
		BPIndexNodeType sibling = new BPIndexNodeType(this.degree, this.keyType, this.path);
		sibling.keys.addAll(keys.subList(this.splitIndex + 1, this.keys.size()));
		sibling.childs.addAll(childs.subList(this.splitIndex + 1, this.childs.size()));
		
		this.keys.subList(this.splitIndex, this.keys.size()).clear();
		this.childs.subList(this.splitIndex + 1, this.childs.size()).clear();
		
		return sibling;
	}

	public void mergeNode(BPNodeType mergeNode)
	{
		BPIndexNodeType sibling = (BPIndexNodeType) mergeNode;
		this.keys.add(sibling.getFirstLeafKey());
		this.keys.addAll(sibling.keys);
		this.childs.addAll(sibling.childs);
	}

	public int getValue(Object key)
	{
		int index = this.matchingKey(key);
		
		return this.getChild(index).getValue(key);
	}

	public Object getFirstLeafKey()
	{
		return this.childs.get(0).getFirstLeafKey();
	}

	public void printValues()
	{
		System.out.print("IndexNode: ");
		
		for(int i = 0; i < this.keys.size(); i++)
		{
			System.out.print("[" + i + "] " + this.keys.get(i).toString() + "\t");
		}
		
		System.out.println("");
		
		for(int i = 0; i < this.childs.size(); i++)
		{
			this.childs.get(i).printValues();
			System.out.println("");
		}
	}

	public boolean hasData(Object key)
	{
		int index = this.matchingKey(key);
		
		return this.getChild(index).hasData(key);
	}
	
	public ArrayList<Integer> getAllData()
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for(int i = 0; i < this.childs.size(); i++)
		{
			result.addAll(this.childs.get(i).getAllData());
		}
		
		return result;
	}
	
	public BPLeafNodeType getFirstLeafNode()
	{
		return this.childs.get(0).getFirstLeafNode();
	}
	
	public int getSize()
	{
		int result = 0;
		
		for(int i = 0; i < this.childs.size(); i++)
		{
			result += this.childs.get(i).getSize();
		}
		
		return result;
	}
	
	public ArrayList<Object> getAllKeys()
	{
		BPLeafNodeType node = this.getFirstLeafNode();
		ArrayList<Object> result = new ArrayList<Object>();
		
		while(node != null)
		{
			result.addAll(node.keys);
			node = (BPLeafNodeType) node.next;
		}
		
		return result;
	}
}