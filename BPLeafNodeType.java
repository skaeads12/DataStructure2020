import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BPLeafNodeType extends BPNodeType implements Serializable
{
	private static final long serialVersionUID = 230766523477444827L;
	ArrayList<RecordType> values;
	
	public BPLeafNodeType(int degree, int keyType, String path)
	{
		super(degree, keyType, path);
		this.values = new ArrayList<RecordType>();
	}

	// 새 레코드를 삽입할 때 -> Deserialize, 데이터 삽입, Serialize
	public void insertValue(Object key, RecordType newValue)
	{
		try
		{
			int index = this.matchingKey(key);
			
			this.deserialize();
			
			if(this.values == null)
				this.values = new ArrayList<RecordType>();
			
			this.keys.add(index, key);
			this.values.add(index, newValue);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	// 파일로 집어넣을 때 -> 다 마치고 나면 Serialization을 해줄 것
	public void addValue(Object key, RecordType newValue)
	{
		int index = this.matchingKey(key);
		keys.add(index, key);
		values.add(index, newValue);
	}
	
	public void removeValue(Object key)
	{
		int index = this.findKey(key);
		
		if(index > -1)
		{
			try
			{
				this.deserialize();
				this.keys.remove(index);
				this.values.remove(index);
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

	public boolean isOverflowed()
	{
		return this.keys.size() > this.degree - 1;
	}

	public boolean isUnderflowed()
	{
		return this.keys.size() < ((this.degree + 1) / 2) - 1;
	}

	public BPNodeType splitNode()
	{
		BPLeafNodeType newNode = new BPLeafNodeType(this.degree, this.keyType, this.path);
		ArrayList<RecordType> values = this.values;
		
		newNode.keys.addAll(this.keys.subList(this.splitIndex + 1, this.keys.size()));
		newNode.values.addAll(values.subList(this.splitIndex + 1, values.size()));
			
		this.keys.subList(this.splitIndex + 1, this.keys.size()).clear();
		values.subList(this.splitIndex + 1, values.size()).clear();
			
		newNode.next = this.next;
		this.next = newNode;
		
		
		return newNode;
	}

	public void mergeNode(BPNodeType mergeNode)
	{
		BPLeafNodeType rightNode = (BPLeafNodeType) mergeNode;
		
		if(this.values == null)
		{
			try
			{
				this.deserialize();
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
		
		if(rightNode.values == null)
		{
			try
			{
				rightNode.deserialize();
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
		
		this.next = rightNode.next;
		
		this.keys.addAll(rightNode.keys);
		this.values.addAll(rightNode.values);
		
		rightNode.keys = null;
		rightNode.values = null;
	}

	public int getValue(Object key)
	{
		return -1;
	}
	
	public int getSize()
	{
		return this.keys.size();
	}

	public Object getFirstLeafKey()
	{
		if(this.keys.size() > 0)
			return this.keys.get(0);
		else
			return null;
	}

	public void printValues()
	{
		System.out.print("LeafNode: ");
		
		for(int i = 0; i < keys.size(); i++)
		{
			System.out.print(this.keys.get(i) + "\t");
		}
		
		System.out.println("");
	}

	public boolean hasData(Object key)
	{
		int index = this.findKey(key);
		
		if(index != -1)
			return true;
		else
			return false;
	}
	
	public List<Integer> getData(int length)
	{
		return null;
	}
	
	public ArrayList<Integer> getAllData()
	{
		return null;
	}
	
	public BPLeafNodeType getFirstLeafNode()
	{
		return this;
	}
	
	public ArrayList<Object> getAllKeys()
	{
		return this.keys;
	}
	
	public void serialize() throws IOException
	{
		String pagePath = this.path + "record_" + this.getFirstLeafKey() + ".def";
		FileOutputStream fos = new FileOutputStream(pagePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		oos.writeObject(this.values);
		
		fos.close();
		oos.close();
		
		this.values = null;
	}
	
	public void deserialize() throws IOException, ClassNotFoundException
	{
		String pagePath = this.path + "record_" + this.getFirstLeafKey() + ".def";
		File file = new File(pagePath);
		
		if(!file.exists())
			return;
		
		FileInputStream fis = new FileInputStream(pagePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		Object value = ois.readObject();
		ArrayList<RecordType> values = (ArrayList<RecordType>) value;
		
		file.delete();
		
		ois.close();
		fis.close();
		
		this.values = values;
	}
}