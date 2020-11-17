import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class BPTreeType implements Serializable
{
	private static final long serialVersionUID = 4294539835557806399L;
	
	private BPNodeType root;
	private int keyType;
	private int degree;
	private int height;
	private String path;
	
	public BPTreeType(int degree, int keyType, String path)
	{
		this.root = new BPLeafNodeType(degree, keyType, path);
		this.degree = degree;
		this.keyType = keyType;
		this.height = 1;
		this.path = path;
	}
	
	public void insertNode(Object key, RecordType newValue)
	{
		this.root.insertValue(key, newValue);
		
		if(this.root.isOverflowed())
		{
			BPIndexNodeType newRoot = new BPIndexNodeType(this.degree, this.keyType, this.path);
			BPNodeType sibling = this.root.splitNode();
			
			newRoot.childs.add(this.root);
			newRoot.childs.add(sibling);
			newRoot.keys.add(sibling.getFirstLeafKey());
			
			if(this.root instanceof BPLeafNodeType)
			{
				BPLeafNodeType tempLeft = (BPLeafNodeType) this.root;
				BPLeafNodeType tempRight = (BPLeafNodeType) sibling;
				
				try
				{
					tempLeft.serialize();
					tempRight.serialize();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			this.root = newRoot;
			this.height++;
		}
		
		if(this.root instanceof BPLeafNodeType)
		{
			BPLeafNodeType temp = (BPLeafNodeType) this.root;
			
			try
			{
				temp.serialize();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void addData(Object key, RecordType newValue)
	{
		this.root.addValue(key, newValue);
		
		if(this.root.isOverflowed())
		{
			BPIndexNodeType newRoot = new BPIndexNodeType(this.degree, this.keyType, this.path);
			BPNodeType sibling = this.root.splitNode();
			newRoot.childs.add(this.root);
			newRoot.childs.add(sibling);
			newRoot.keys.add(sibling.getFirstLeafKey());
			this.root = newRoot;
			this.height++;
		}
	}
	
	public void removeData(Object key)
	{
		this.root.removeValue(key);
		
		if(this.root instanceof BPLeafNodeType)
		{
			try
			{
				((BPLeafNodeType) this.root).serialize();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			BPIndexNodeType temp = (BPIndexNodeType) this.root;
			
			if(temp.childs.size() == 1)
			{
				this.root = temp.getChild(0);
			}
		}
	}
	
	public Object[] searchData(Object key)
	{
		BPNodeType temp = this.root;
		
		while(temp != null)
		{
			if(temp instanceof BPIndexNodeType)
			{
				int index = temp.matchingKey(key);
				temp = ((BPIndexNodeType) temp).childs.get(index);
			}
			else
			{
				BPLeafNodeType result = (BPLeafNodeType) temp;
				
				int index = result.findKey(key);
				
				try
				{
					if(index == -1)
						return null;
					
					result.deserialize();
					Object[] record = result.values.get(index).getData().clone();
					result.serialize();
					
					return record;
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}
	
	public BPLeafNodeType searchNode(Object key)
	{
		BPNodeType temp = this.root;

		while(temp != null)
		{
			if(temp instanceof BPIndexNodeType)
			{
				int index = temp.matchingKey(key);
				temp = ((BPIndexNodeType) temp).childs.get(index);
			}
			else
			{
				BPLeafNodeType result = (BPLeafNodeType) temp;
				
				return result;
			}
		}
		
		return null;
	}
	
	public void printValues()
	{
		this.root.printValues();
	}
	
	public ArrayList<RecordType> getData(int startIndex, int endIndex)
	{
		ArrayList<RecordType> result = new ArrayList<RecordType>();
		
		int length = endIndex - startIndex;
		
		BPLeafNodeType temp = this.root.getFirstLeafNode();
		int index = 0;
		
		while(temp.next != null && index < startIndex)
		{
			index += temp.keys.size();
			temp = (BPLeafNodeType) temp.next;
		}
		
		while(temp != null && length > 0)
		{
			try
			{
				temp.deserialize();
				ArrayList<RecordType> record = temp.values;
				result.addAll(record);
				length -= record.size();
				temp.serialize();
				temp = (BPLeafNodeType) temp.next;
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	public BPLeafNodeType getFirstLeafNode()
	{
		return this.root.getFirstLeafNode();
	}
	
	public void setRoot(BPNodeType root)
	{
		this.root = root;
	}
	
	public BPNodeType getRoot()
	{
		return this.root;
	}
	
	public ArrayList<Object> getAllKeys()
	{
		return this.root.getAllKeys();
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public void serializeRecords() throws ClassNotFoundException, IOException
	{
		BPLeafNodeType node = this.root.getFirstLeafNode();
		
		while(node != null)
		{
			node.serialize();
			node = (BPLeafNodeType) node.next;
		}
	}
	
	public ArrayList<RecordType> deserializeRecords(int index) throws ClassNotFoundException, IOException
	{
		BPLeafNodeType node = this.root.getFirstLeafNode();
		
		for(int i = 0; i < index; i++)
		{
			if(node.next != null)
				node = (BPLeafNodeType) node.next;
			else
			{
				node.deserialize();
				ArrayList<RecordType> result = (ArrayList<RecordType>) node.values.clone();
				node.serialize();
				return result;
			}
		}
		
		node.deserialize();
		ArrayList<RecordType> result = (ArrayList<RecordType>) node.values.clone();
		node.serialize();
		
		return result;
	}
	
	public int getLeafNodeSize()
	{
		BPLeafNodeType node = this.root.getFirstLeafNode();
		int count = 0;
		
		while(node.next != null)
		{
			node = (BPLeafNodeType) node.next;
			count++;
		}
		
		return count;
	}
	
	public void saveRecords(String path) throws ClassNotFoundException, IOException
	{
		ArrayList<RecordType> records = new ArrayList<RecordType>();
		
		BPLeafNodeType temp = this.root.getFirstLeafNode();
		
		while(temp != null)
		{
			temp.deserialize();
			records.addAll((ArrayList<RecordType>) temp.values.clone());
			temp.serialize();
			temp = (BPLeafNodeType) temp.next;
		}
		
		FileOutputStream fos = new FileOutputStream(path);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(records);
		
		oos.flush();
		fos.flush();
	}
	
	public void loadRecords(ArrayList<RecordType> records)
	{
		int length = records.size();
		
		for(int i = 0; i < length; i++)
		{
			RecordType record = records.remove(0);
			Object key = record.getKey();
			
			BPLeafNodeType node = this.searchNode(key);
			
			if(node.values == null)
			{
				node.values = new ArrayList<RecordType>();
			}
			
			node.values.add(record);
		}
		
		try
		{
			this.serializeRecords();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void modifyRecord(Object key, int fieldIndex, Object newValue)
	{
		BPLeafNodeType node = this.searchNode(key);
		
		try
		{
			node.deserialize();
			int index = node.findKey(key);
			RecordType record = node.values.get(index);
			
			Object[] data = record.getData();
			Object[] newData = new Object[data.length];
			
			for(int i = 0; i < newData.length; i++)
			{
				if(i == fieldIndex)
				{
					newData[i] = newValue;
				}
				else
				{
					newData[i] = data[i];
				}
				
			}
			
			record.setData(newData);
			
			node.values.set(index, record);
			node.serialize();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
}