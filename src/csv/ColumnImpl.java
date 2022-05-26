package csv;

import javax.management.StringValueExp;
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
        try{
            if(t == Integer.class || t == Double.class)
            {
                return t.cast(getValue(index)) ;
            }
            else
                throw new NumberFormatException();
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
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
        System.out.printf(String.format(" %%%ds \n",getLength()),getHeader());
        for(int i = 0; i < count();  i++)
        {
            String tmp;
            if(getValue(i).isEmpty())
                tmp = null;
            else
                tmp = getValue(i);
            System.out.printf(String.format(" %%%ds \n",getLength()),tmp);
        }


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
            if(s.isEmpty() != true)
            {
                try{
                    sum += Math.pow(Double.parseDouble(s) - mean,2);
                }
                catch (NumberFormatException e) {}
            }
        }
        return Math.sqrt(sum /(double)getNumericCount());
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
        return r;
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
        boolean result = false;
        if(type.equals("String")) return result;
        double mean = getMean();
        double std = getStd();
        for(int i = 0; i< count(); i++)
        {
            if(!list.get(i).isEmpty())
            {
                setValue(i, String.valueOf(Math.round((Double.parseDouble(getValue(i)) - mean) / std * 1000000 ) / 1000000.0) );
                result = true;
            }
        }
        type = "double";
        return result;
    }

    @Override
    public boolean normalize() {
        boolean result = false;
        if(type.equals("String")) return result;
        double max = getNumericMax();
        double min = getNumericMin();
        for(int i = 0; i< count(); i++)
        {
            if(!list.get(i).isEmpty())
            {
                setValue(i, String.valueOf(Math.round((Double.parseDouble(getValue(i)) - min) / (max - min) * 1000000 ) / 1000000.0) );
                result = true;
            }
        }
        type = "double";
        return result;
    }

    @Override
    public boolean factorize() {
        List<String> tmpList = new ArrayList<>();
        boolean twoElement = true;
        for(int i  =0; i< count(); i++)
        {
            String tmp = getValue(i);
            if(!tmp.isEmpty())
            {
                int check = 0;
                for(String s: tmpList)
                {
                    if(!tmp.equals(s))
                        check++;
                }
                if(check == tmpList.size())
                    tmpList.add(tmp);
                if(tmpList.size() > 2)
                {
                    twoElement = false;
                    break;
                }
            }
        }
        if(twoElement)
        {
            for(int i  =0; i< count(); i++)
            {
                if(getValue(i).equals(tmpList.get(0)))
                    setValue(i, "1");
                else
                    setValue(i, "0");
            }
            type = "int";
        }
        return twoElement;
    }

}
