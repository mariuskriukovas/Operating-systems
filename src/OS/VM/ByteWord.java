package OS.VM;

public class ByteWord
{
    private Object value;


    ByteWord(int value)
    {
        this.value = value;
    }


    public Object getValue()
    {
        return value;
    }
    public void setValue(Object value)
    {
        this.value = value;
    }
}
