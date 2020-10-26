import java.util.ArrayList;
import java.util.Comparator;

public class LinkedListType
{
	private String name;
	
	private String[] fieldName;
	private int[] fieldType;
	private int fieldSize;
	
	private LinkedNodeType head;
	private LinkedNodeType tail;
	
	int recordSize;
	
	public LinkedListType(String name)
	{
		this.name = name;
		this.recordSize = 0;
		this.head = null;
		this.tail = null;
		
		this.fieldName = null;
		this.fieldType = null;
		this.fieldSize = 0;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setFieldName(String[] fieldName)
	{
		this.fieldName = fieldName;
	}
	
	public String[] getFieldName()
	{
		return this.fieldName;
	}
	
	public void setFieldType(int[] fieldType)
	{
		this.fieldType = fieldType;
	}
	
	public int[] getFieldType()
	{
		return this.fieldType;
	}
	
	public void setFieldSize(int fieldSize)
	{
		this.fieldSize = fieldSize;
	}
	
	public int getFieldSize()
	{
		return this.fieldSize;
	}
	
	public int getRecordSize()
	{
		return this.recordSize;
	}
	
	public boolean isEmpty()
	{
		return this.recordSize == 0;
	}
	
	public void swapRecord(int recordIndex1, int recordIndex2)
	{
		Object[] temp = this.getRecord(recordIndex1).getData().clone();
		
		this.getRecord(recordIndex1).setData(this.getRecord(recordIndex2).getData());
		this.getRecord(recordIndex2).setData(temp);
	}
	
	public Object[][] getDataFromObject()
	{
		if(this.recordSize == 0)
		{
			return null;
		}
		
		LinkedNodeType temp = this.head;
		Object[][] result = new Object[this.recordSize][this.fieldSize];
		
		int index = 0;
		while(temp != null)
		{
			result[index] = temp.getData();
			temp = temp.next;
			index++;
		}
		return result;
	}
	
	public ArrayList<Object[]> getDataFromArrayList()
	{
		if(this.recordSize == 0)
		{
			return null;
		}
		
		Object[][] data = this.getDataFromObject();
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		
		for(int i = 0; i < data.length; i++)
		{
			result.add(data[i]);
		}
		
		return result;
	}
	
	public LinkedNodeType getRecord(int index)
	{
		if(index > this.recordSize)
		{
			return null;
		}
		
		LinkedNodeType temp = this.head;
		
		for(int i = 0; i < index; i++)
		{
			temp = temp.next;
		}
		
		return temp;
	}
	
	public void insertData(Object[] record)
	{
		LinkedNodeType temp = this.tail;
		LinkedNodeType newData = new LinkedNodeType(record, this.fieldType, this.fieldSize);
		
		if(this.recordSize == 0)
		{
			this.head = newData;
			this.tail = newData;
			
			newData.next = null;
			newData.prev = null;
		}
		else
		{
			temp.next = newData;
			newData.next = null;
			newData.prev = temp;
			this.tail = newData;
		}
		
		this.recordSize++;
	}
	
	public Object removeNode(LinkedNodeType removeNode)
	{
		if(this.head == null)
		{
			return null;
		}
		
		Object tempData = removeNode.getData();
		
		if(this.recordSize == 1)
		{
			this.head = null;
			this.tail = null;
			removeNode.free();
		}
		else
		{
			if(removeNode.prev != null)
				removeNode.prev.next = removeNode.next;
			
			if(removeNode.next != null)
				removeNode.next.prev = removeNode.prev;
			
			if(head == removeNode)
				head = removeNode.next;
			
			if(tail == removeNode)
				tail = removeNode.prev;
			
			removeNode.free();
		}
		
		this.recordSize--;
		
		return tempData;
	}
	
	public LinkedNodeType findData(Object key, int fieldIndex)
	{
		if(this.recordSize == 0)
		{
			return null;
		}
		
		LinkedNodeType temp = this.head;
		
		while(temp != null)
		{
			Object value = temp.getData()[fieldIndex];
			if(value == key || value.equals(key))
			{
				return temp;
			}
			
			temp = temp.next;
		}
		
		//끝내 찾지 못했을 경우
		return null;
	}
	
	public void sortNode(int fieldIndex, boolean ascend)
	{
		switch(this.fieldType[fieldIndex])
		{
		case Database.STRING_TYPE:
			this.sortNodeForString(0, this.recordSize - 1, fieldIndex, ascend);
			break;
		case Database.DOUBLE_TYPE:
		case Database.INTEGER_TYPE:
		case Database.CHAR_TYPE:
			this.sortNodeForEtc(0, this.recordSize - 1, fieldIndex, ascend);
			break;
		}
	}
	
	public void sortNodeForString(int start, int end, int fieldIndex, boolean ascend)
	{
		if(ascend)
		{
			int i = start;
			int j = end;
				
			String pivot = String.valueOf(this.getRecord((i + j) / 2).getData()[fieldIndex]);
				
			do
			{
				while(this.getRecord(i).getData()[fieldIndex].toString().compareTo(pivot) < 0)
					i++;
				
				while(this.getRecord(j).getData()[fieldIndex].toString().compareTo(pivot) > 0)
					j--;
				
				if(i <= j)
				{
					this.swapRecord(i, j);
					i++;
					j--;
				}
			}
			while(i <= j);
				
			if(start < j) this.sortNodeForString(start, j, fieldIndex, ascend);
			if(end > i) this.sortNodeForString(i, end, fieldIndex, ascend);
		}
		else
		{
			int i = start;
			int j = end;
				
			String pivot = String.valueOf(this.getRecord((i + j) / 2).getData()[fieldIndex]);
				
			do
			{
				while(this.getRecord(i).getData()[fieldIndex].toString().compareTo(pivot) > 0)
					i++;
				
				while(this.getRecord(j).getData()[fieldIndex].toString().compareTo(pivot) < 0)
					j--;
				
				if(i <= j)
				{
					this.swapRecord(i, j);
					i++;
					j--;
				}
			}
			while(i <= j);
				
			if(start < j) this.sortNodeForString(start, j, fieldIndex, ascend);
			if(end > i) this.sortNodeForString(i, end, fieldIndex, ascend);
		}
	}
	
	public void sortNodeForEtc(int start, int end, int fieldIndex, boolean ascend)
	{
		if(ascend)
		{
			int i = start;
			int j = end;
			
			Object pivot = this.getRecord((i + j) / 2).getData()[fieldIndex];
			
			do
			{
				while(this.compareNumber(this.getRecord(i).getData()[fieldIndex], pivot, fieldType[fieldIndex]))
					i++;
				
				while(this.compareNumber(pivot, this.getRecord(j).getData()[fieldIndex], fieldType[fieldIndex]))
					j--;
				
				if(i <= j)
				{
					this.swapRecord(i, j);
					i++;
					j--;
				}
			}
			while(i <= j);
			
			if(start < j) this.sortNodeForEtc(start, j, fieldIndex, ascend);
			if(end > i) this.sortNodeForEtc(i, end, fieldIndex, ascend);
		}
		else
		{
			int i = start;
			int j = end;
			
			Object pivot = this.getRecord((i + j) / 2).getData()[fieldIndex];
			
			do
			{
				while(this.compareNumber(pivot, this.getRecord(i).getData()[fieldIndex], fieldType[fieldIndex]))
					i++;
				
				while(this.compareNumber(this.getRecord(j).getData()[fieldIndex], pivot, fieldType[fieldIndex]))
					j--;
				
				if(i <= j)
				{
					this.swapRecord(i, j);
					i++;
					j--;
				}
				
			}
			while(i <= j);

			if(start < j) this.sortNodeForEtc(start, j, fieldIndex, ascend);
			if(end > i) this.sortNodeForEtc(i, end, fieldIndex, ascend);
		}
	}
	
	public boolean compareNumber(Object source, Object target, int fieldType)
	{
		switch(fieldType)
		{
		case Database.DOUBLE_TYPE:
			double sourceDouble = Double.valueOf(source.toString());
			double targetDouble = Double.valueOf(target.toString());
			return sourceDouble > targetDouble;
		case Database.INTEGER_TYPE:
			int sourceInt = Integer.parseInt(source.toString());
			int targetInt = Integer.parseInt(target.toString());
			return sourceInt > targetInt;
		case Database.CHAR_TYPE:
			return Character.valueOf(source.toString().toCharArray()[0]) > Character.valueOf(target.toString().toCharArray()[0]);
		}
		
		return false;
	}
	
	public boolean compareNumberDescend(Object source, Object target, int fieldType)
	{
		switch(fieldType)
		{
		case Database.DOUBLE_TYPE:
			return Double.parseDouble(source.toString()) < Double.parseDouble(target.toString());
		case Database.INTEGER_TYPE:
			return Integer.parseInt(source.toString()) < Integer.parseInt(target.toString());
		case Database.CHAR_TYPE:
			return Character.valueOf(source.toString().toCharArray()[0]) < Character.valueOf(target.toString().toCharArray()[0]);
		}
		
		return false;
	}
}