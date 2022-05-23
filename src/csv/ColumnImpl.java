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
        return list.size();
    }
    public int getSize() {
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
         if(s.isEmpty())
             tmp = 4;
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
        if(getNumericCount() == getSize())
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
        for(int i = 0 ; ;i++) {
            try {
                double min = Double.parseDouble(list.get(i));
                for (String s : list) {
                    if (s.isEmpty() != true) {
                        try {
                            double tmp = Double.parseDouble(s);
                            if (min > tmp)
                                min = tmp;
                        } catch (NumberFormatException e) {
                        }
                    }
                }
                return min;
            }
            catch (NumberFormatException e)
            {}
        }
    }

    @Override
    public double getNumericMax(){
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        for(int i = 0 ; ;i++) {
            try {
                double max = Double.parseDouble(list.get(i));
                for (String s : list) {
                    if (s.isEmpty() != true) {
                        try {
                            double tmp = Double.parseDouble(s);
                            if (max < tmp)
                                max = tmp;
                        } catch (NumberFormatException e) {
                        }
                    }
                }
                return max;
            }
            catch (NumberFormatException e)
            {}
        }
    }

    @Override
    public double getMean() {
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        double sum = 0;
        for(String s: list)
        {
            if(s.isEmpty() != true)
            {
                try{
                    sum += Double.parseDouble(s);
                }
                catch (NumberFormatException e) {}
            }
        }

        return Math.round(sum /(double)getNumericCount()* 1000000 ) / 1000000.0;
    }

    @Override
    public double getStd() {
        if(getNumericCount() == 0)
            throw new NumberFormatException();
        double mean = getMean();
        double sum = 0;

        for(String s: list)
        {
            if(s.isEmpty() != true)
            {
                try{
                    sum += Math.pow(Double.parseDouble(s) - mean,2);
                }
                catch (NumberFormatException e) {}
            }
        }
        return Math.round( Math.sqrt(sum /(double)getNumericCount()) * 1000000 ) / 1000000.0;
    }

    public double q1q2q3(double q)
    {
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
        double index = q * (tmp.size() - 1);
        int low = (int)Math.floor(index);
        double r = tmp.get(low) + (index - low) * (tmp.get(low + 1) - tmp.get(low));
        return Math.round((r * 1000000)) / 1000000.0;
    }

    @Override
    public double getQ1() {
        return q1q2q3(0.25);
    }

    @Override
    public double getMedian() {
        return q1q2q3(0.5);
    }

    @Override
    public double getQ3() {
        return q1q2q3(0.75);
    }

    @Override
    public boolean fillNullWithMean() {
        boolean result = false;
        if(type.equals("String")) return result;
        double mean = getMean();
        for(int i =0; i< count(); i++)
        {
            if(list.get(i).isEmpty())
            {
                setValue(i, String.valueOf(mean));
                result = true;
                type = "double";
            }
        }
        return result;
    }

    @Override
    public boolean fillNullWithZero() {
        boolean result = false;
        if(type.equals("String")) return result;
        double mean = getMean();
        for(int i =0; i< count(); i++)
        {
            if(list.get(i).isEmpty())
            {
                setValue(i, "0");
                result = true;
            }
        }
        return result;
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
