package csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class ColumnImpl implements Column {
    private String header;
    private List<String> list;
    private String type;

    public ColumnImpl(List<String> list, String header)
    {
        this.header = header;
        this.list = list;
        findType();
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
        return list.size() - (int)getNullCount();
    }

    @Override
    public void print() {


    }
    public int getLength()
    {
     int maxLength = header.length();
     for(String s: list)
     {
         int tmp = s.length();
         if(tmp > maxLength)
             maxLength = tmp;
     }
     return maxLength;
    }
    public String  getType()
    {
        return type;
    }
    public void findType()
    {
        if(isNumericColumn())
        {
            boolean isInt = true;

            for(int i = 0; i< list.size(); i++)
            {
                if(list.get(i).isEmpty() != true) {
                    try {
                        Integer.parseInt(list.get(i));
                    } catch (NumberFormatException e) {
                        isInt = false;
                        break;
                    }
                }
            }
            if(isInt)
                type = "int";
            else
                type = "double";
        }
        else
            type = "String";
    }
    @Override
    public boolean isNumericColumn() {
        if(getNumericCount() == count())
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
        long count = 0;
        for(String s: list)
        {
            if(s.isEmpty() != true) {
                try {
                    Double.parseDouble(s);
                    count++;
                } catch (NumberFormatException e) {
                }
            }
        }
        return count;
    }

    @Override
    public double getNumericMin(){
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        double min = Double.parseDouble(list.get(0));
        for(String s: list)
        {
            if(s.isEmpty() != true) {
                double tmp = Double.parseDouble(s);
                if (min > tmp)
                    min = tmp;
            }
        }
        return min;
    }

    @Override
    public double getNumericMax(){
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        double max = Double.parseDouble(list.get(0));
        for(String s: list)
        {
            if(s.isEmpty() != true) {
                    double tmp = Double.parseDouble(s);
                    if (max < tmp)
                        max = tmp;

            }

        }
        return max;
    }

    @Override
    public double getMean() {
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        double sum = 0;
        for(String s: list)
        {
            sum += Double.parseDouble(s);
        }

        return sum /(double)getNumericCount();
    }

    @Override
    public double getStd() {
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        double mean = getMean();
        double sum = 0;
        for(String s: list)
        {
            sum += Math.sqrt(Double.parseDouble(s) - mean);
        }
        return sum /(double)getNumericCount();
    }

    @Override
    public double getQ1() {
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        List<Double> tmp = new ArrayList<>();
        for(String s: list)
        {
            try{
                if(s.isEmpty() != true) {
                    tmp.add(Double.parseDouble(s));
                }
            }
            catch (NumberFormatException e) {
            }
        }
        Collections.sort(tmp);
        return tmp.get((tmp.size() / 2) / 2  + 1);
    }

    @Override
    public double getMedian() {
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        List<Double> tmp = new ArrayList<>();
        for(String s: list)
        {
            try{
                if(s.isEmpty() != true) {
                    tmp.add(Double.parseDouble(s));
                }
            }
            catch (NumberFormatException e) {
            }
        }
        Collections.sort(tmp);
        if(tmp.size() % 2 == 0)
            return (tmp.get(tmp.size() / 2) + tmp.get(tmp.size() / 2 + 1)) / 2.0;
        else
            return tmp.get(tmp.size() / 2 + 1);
    }

    @Override
    public double getQ3() {
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        List<Double> tmp = new ArrayList<>();
        for(String s: list)
        {
            try{
                if(s.isEmpty() != true) {
                    tmp.add(Double.parseDouble(s));
                }
            }
            catch (NumberFormatException e) {
            }
        }
        Collections.sort(tmp);
        return tmp.get(tmp.size() - ((tmp.size() / 2) / 2  + 1));
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
