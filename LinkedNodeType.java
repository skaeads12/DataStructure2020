
public class LinkedNodeType
{
	LinkedNodeType next;
	LinkedNodeType prev;
	private Object[] data;
	private int[] fieldType;
	private int fieldSize;
	
	public LinkedNodeType(Object[] data, int[] fieldType, int fieldSize)
	{
		this.data = data;
		this.fieldSize = fieldSize;
		this.fieldType = fieldType;
	}
	
	public void setData(Object[] data)
	{
		this.data = data;
	}
	
	public void setFieldSize(int fieldSize)
	{
		this.fieldSize = fieldSize;
	}
	
	public void setFieldType(int[] fieldType)
	{
		this.fieldType = fieldType;
	}
	
	public Object[] getData()
	{
		return this.data;
	}
	
	public int[] getFieldType()
	{
		return this.fieldType;
	}
	
	public int getFieldSize()
	{
		return this.fieldSize;
	}
	
	public void free()
	{
		data = null;
		next = null;
		prev = null;
		fieldType = null;
		fieldSize = 0;
	}
}
