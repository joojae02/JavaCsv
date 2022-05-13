package csv;

import java.util.ArrayList;
import java.util.List;

class ColumnImpl implements Column {
    private String header;
    private List<String> list;
    public ColumnImpl(List<String> list, String header)
    {
        this.header = header;
        this.list = list;
    }
    public String getHeader()
    {
        return header;
    }

    @Override
    public String getValue(int index) {
        return list.get(index);
    }

    @Override
    public <T extends Number> T getValue(int index, Class<T> t) {
        return null;
    }

    @Override
    public void setValue(int index, String value) {
        list.set(index,value);
    }

    @Override
    public <T extends Number> void setValue(int index, T value) {
        list.set(index,value.toString());
    }

    @Override
    public int count() {
        return list.size();
    }

    @Override
    public void print() {


    }

    @Override
    public boolean isNumericColumn() {
        if(getNumericCount() == list.size())
            return true;
        else
            return false;
    }

    @Override
    public long getNullCount() {
        long count = 0;
        for(String s : list)
        {
            if(s.isEmpty())
                count++;
        }
        return count;
    }

    @Override
    public long getNumericCount() {
        int count = 0;
        for(String s: list)
        {
            if(s.isEmpty() != true) {
                try {
                    double d = Double.parseDouble(s);
                    count++;
                } catch (NumberFormatException e) {
                }
            }
        }
        return count;
    }

    @Override
    public double getNumericMin() {
        return 0;
    }

    @Override
    public double getNumericMax() {
        return 0;
    }

    @Override
    public double getMean() {
        return 0;
    }

    @Override
    public double getStd() {
        return 0;
    }

    @Override
    public double getQ1() {
        return 0;
    }

    @Override
    public double getMedian() {
        return 0;
    }

    @Override
    public double getQ3() {
        return 0;
    }

    @Override
    public boolean fillNullWithMean() {
        return false;
    }

    @Override
    public boolean fillNullWithZero() {
        return false;
    }

    @Override
    public boolean standardize() {
        return false;
    }

    @Override
    public boolean normalize() {
        return false;
    }

    @Override
    public boolean factorize() {
        return false;
    }
}
