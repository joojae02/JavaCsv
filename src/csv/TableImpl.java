package csv;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

class TableImpl implements Table {
    private List<ColumnImpl> columnList = new ArrayList<>();

    public TableImpl(List<List<String>> list)
    {
        List<List<String>> tmpList = new ArrayList<>();
        for(int i = 0; i < list.get(0).size();i++)//
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 1; j < list.size(); j++)
                tmp.add(list.get(j).get(i));
            tmpList.add(tmp);
        }
        for(int i = 0; i < list.get(0).size(); i++)
        {
            ColumnImpl column = new ColumnImpl(tmpList.get(i),list.get(0).get(i));
            columnList.add(column);
        }
//        for(int i = 0; i < tmpList.size();i++)
//        {
//            for(int j = 0; j < tmpList.get(i).size(); j++)
//            {
//                System.out.print(tmpList.get(i).get(j) + " / ");
//            }
//            System.out.println();
//        }
    }

    @Override
    public String toString() {
        String result = this.getClass() +"@" + Integer.toHexString(this.hashCode()) + ">\n"
        + "RangeIndex: " + columnList.get(0).count() +" entries, 0 to " + (columnList.get(0).count() - 1) +"\n"
        + "Data columns" + "(total " +columnList.size() + "columns) :\n"
        + String.format(" %s |%11s | %6s %8s | %6s\n", "#","Columns", "Count", "Non-Null","Dtype");
        for(int i = 0; i< columnList.size(); i++)
        {
            result += String.format(" %d | %11s | %6s %8s | %6s\n", i,columnList.get(i).getHeader(), columnList.get(i).count(),
                    (columnList.get(i).count() != columnList.get(i).getNullCount()) ? "non-null":"null",columnList.get(i).getType());
        }
        return result;
    }

    @Override
    public void print() {

        for(int i = 0; i< columnList.size(); i++)
            System.out.printf(String.format(" %%%ds |",columnList.get(i).getLength()),columnList.get(i).getHeader());

        System.out.println();
        for(int i = 0; i< columnList.get(0).count(); i++)
        {
            String [] tmp = new String[columnList.size()];
            for(int j = 0; j < columnList.size();  j++)
            {
                if(columnList.get(j).getValue(i).isEmpty())
                    tmp[j] = null;
                else
                    tmp[j] = columnList.get(j).getValue(i);
                System.out.printf(String.format(" %%%ds |",columnList.get(j).getLength()),tmp[j]);
            }
            System.out.println();
        }
    }

    @Override
    public Table getStats() {

        List<List<String>> tmpList = new ArrayList<>();
        for(int i = 0; i< 9; i++)
        {
            tmpList.add(new ArrayList<>());
        }
        tmpList.get(0).add("");tmpList.get(1).add("count");
        tmpList.get(2).add("mean");tmpList.get(3).add("std");tmpList.get(4).add("min");
        tmpList.get(5).add("25%");tmpList.get(6).add("50%");tmpList.get(7).add("75%");tmpList.get(8).add("max");
        for(int i = 0; i< columnList.size(); i++) {
            try{
                tmpList.get(2).add(String.valueOf(columnList.get(i).getMean()));
                tmpList.get(3).add(String.valueOf(columnList.get(i).getStd()));
                tmpList.get(4).add(String.valueOf(columnList.get(i).getNumericMin()));
                tmpList.get(5).add(String.valueOf(columnList.get(i).getQ1()));
                tmpList.get(6).add(String.valueOf(columnList.get(i).getMedian()));
                tmpList.get(7).add(String.valueOf(columnList.get(i).getQ3()));
                tmpList.get(8).add(String.valueOf(columnList.get(i).getNumericMax()));
                tmpList.get(0).add(columnList.get(i).getHeader());
                tmpList.get(1).add(String.valueOf(columnList.get(i).getNumericCount()));
            }
            catch (NumberFormatException e) {
                System.out.println(i+ ", "+ e );
            }
        }

        Table states = new TableImpl(tmpList);
        return states;
    }

    @Override
    public Table head() {
        return head(5);
    }

    @Override
    public Table head(int lineCount) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = 0; i< columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());
        for (int i = 0; i< lineCount; i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 0; j< columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }

        Table head = new TableImpl(tmpList);
        return head;
    }

    @Override
    public Table tail() {
        return tail(5);
    }

    @Override
    public Table tail(int lineCount) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = 0; i< columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());
        int size = columnList.get(0).size();
        for (int i = size - lineCount; i< size; i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 0; j< columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table tail = new TableImpl(tmpList);
        return tail;
    }

    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = 0; i< columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i = beginIndex; i< endIndex; i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 0; j< columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;

    }

    @Override
    public Table selectRowsAt(int... indices) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = 0; i< columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i : indices)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 0; j< columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = beginIndex; i< endIndex; i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i = 0; i< columnList.get(beginIndex).size(); i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = beginIndex; j< endIndex; j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;
    }

    @Override
    public Table selectColumnsAt(int... indices) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i : indices)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i = 0; i< columnList.get(indices[0]).size(); i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j: indices) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        return null;
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        if(isAscending)
        {

        }
        return null;
    }

    @Override
    public Table shuffle() {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Column getColumn(int index) {
        return null;
    }

    @Override
    public Column getColumn(String name) {
        return null;
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
