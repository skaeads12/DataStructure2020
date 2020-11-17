import java.io.Serializable;
import java.util.ArrayList;

public abstract class BPNodeType implements Serializable
{
	private static final long serialVersionUID = 1986147902982300789L;
	
	protected ArrayList<Object> keys;
	protected int degree;
	protected int splitIndex;
	protected int keyType;
	protected BPNodeType next;
	protected String path;
	
	public BPNodeType(int degree, int keyType, String path)
	{
		this.keys = new ArrayList<Object>();
		this.degree = degree;
		this.splitIndex = ((degree + 1) / 2) - 1;
		this.keyType = keyType;
		this.next = null;
		this.path = path;
	}
	
	abstract void insertValue(Object key, RecordType newValue);
	
	abstract void addValue(Object key, RecordType newValue);
	
	abstract void removeValue(Object key);
	
	abstract boolean isOverflowed();
	
	abstract boolean isUnderflowed();
	
	abstract BPNodeType splitNode();
	
	abstract void mergeNode(BPNodeType mergeNode);
	
	abstract int getValue(Object key);
	
	abstract Object getFirstLeafKey();
	
	abstract void printValues();
	
	// 데이터가 삽입되어야 할 위치 리턴
	public int matchingKey(Object key)
	{
		int keySize = this.keys.size();
		
		if(this.keyType == Database.INTEGER_TYPE)
		{
			int target = Integer.parseInt(key.toString());
			
			for(int i = 0; i < keySize; i++)
			{
				int source = Integer.parseInt(this.keys.get(i).toString());
				
				if(source > target)
					return i;
			}
			
			return keySize;
		}
		else if(this.keyType == Database.DOUBLE_TYPE)
		{
			double target = Double.parseDouble(key.toString());
			
			for(int i = 0; i < keySize; i++)
			{
				double source = Double.parseDouble(this.keys.get(i).toString());
				
				if(source > target)
					return i;
			}
			
			return keySize;
		}
		else if(this.keyType == Database.CHAR_TYPE)
		{
			char target = key.toString().charAt(0);
			
			for(int i = 0; i < keySize; i++)
			{
				char source = this.keys.get(i).toString().charAt(0);
				
				if(source > target)
					return i;
			}
			
			return keySize;
		}
		else
		{
			String target = key.toString();
			
			for(int i = 0; i < keySize; i++)
			{
				String source = this.keys.get(i).toString();
				
				if(source.compareTo(target) > 0)
					return i;
			}
			
			return keySize;
		}
	}
	
	// 데이터가 존재한다면 해당 인덱스 리턴, 없으면 -1 리턴
	public int findKey(Object key)
	{
		int keySize = this.keys.size();
		
		if(this.keyType == Database.INTEGER_TYPE)
		{
			int target = Integer.parseInt(key.toString());
			
			for(int i = 0; i < keySize; i++)
			{
				int source = Integer.parseInt(this.keys.get(i).toString());
				
				if(source == target)
					return i;
			}
			
			return -1;
		}
		else if(this.keyType == Database.DOUBLE_TYPE)
		{
			double target = Double.parseDouble(key.toString());
			
				for(int i = 0; i < keySize; i++)
		{
				double source = Double.parseDouble(this.keys.get(i).toString());
				
				if(source == target)
					return i;
			}
			
			return -1;
		}
		else if(this.keyType == Database.CHAR_TYPE)
		{
			char target = key.toString().charAt(0);
			
			for(int i = 0; i < keySize; i++)
			{
				char source = this.keys.get(i).toString().charAt(0);
					
				if(source == target)
					return i;
			}
			
			return -1;
		}
		else
		{
			String target = key.toString();
			
			for(int i = 0; i < keySize; i++)
			{
				String source = this.keys.get(i).toString();
				
				if(source.equals(target))
					return i;
			}
			
			return -1;
		}
	}
	
	abstract boolean hasData(Object key);
	
	abstract ArrayList<Integer> getAllData();
	
	abstract BPLeafNodeType getFirstLeafNode();
	
	abstract int getSize();
	
	abstract ArrayList<Object> getAllKeys();
}
