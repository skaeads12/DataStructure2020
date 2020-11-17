import java.io.Serializable;

public class RecordType implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	RecordType next;
	RecordType prev;
	private Object[] data;
	private int[] fieldType;
	private int fieldSize;
	
	public RecordType(Object[] data, int[] fieldType, int fieldSize)
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
	
	public Object getDataWithIndex(int index)
	{
		return this.data[index];
	}
	
	public int[] getFieldType()
	{
		return this.fieldType;
	}
	
	public int getFieldSize()
	{
		return this.fieldSize;
	}
	
	public int getKey()
	{
		return Integer.parseInt(this.data[0].toString());
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
