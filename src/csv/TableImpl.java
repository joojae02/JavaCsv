package csv;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

class TableImpl implements Table {
    private List<ColumnImpl> columnList = new ArrayList<ColumnImpl>();

    public TableImpl(List<List<String>> list)
    {
        List<List<String>> tmpList = new ArrayList<List<String>>();

        for(int i = 0; i < list.get(0).size();i++)// ~891
        {
            List<String> tmp = new ArrayList<String>();
            for(int j = 0; j < list.size(); j++)
                tmp.add(list.get(j).get(i));
            tmpList.add(tmp);
        }
        for(int i = 0; i < tmpList.size(); i++)
        {
            ColumnImpl column = new ColumnImpl(tmpList.get(i),tmpList.get(0).get(i));
            columnList.add(column);
        }
        for(int i = 0; i < tmpList.size();i++)
        {
            for(int j = 0; j < tmpList.get(i).size(); j++)
            {
                System.out.print(tmpList.get(i).get(j) + " / ");
            }
            System.out.println();
        }
    }

    @Override
    public String toString() {
        String result = "<csv.Table@" + this.hashCode() + ">\n"
        + "RangeIndex: " + columnList.get(0).count() +"0 to " + columnList.get(0).count() +"\n"
        + "Data columns" + "(total " +columnList.size() + "columns) :\n"
        + String.format(" %s |%11s | %6s %8s | %6s", "#","Columns", "Count", "Non-Null","Dtype");
        for(int i = 0; i< columnList.size(); i++)
        {
            result += String.format(" %d |%11s | %6s %8s | %6s", i,columnList.get(i).getHeader(), columnList.get(i).count(),
                    (columnList.get(i).count() != columnList.get(i).getNullCount()) ? "non-null":"null",);
        }
        return result;
    }

    @Override
    public void print() {

        for(int i = 0; i< columnList.get(0).count(); i++)
        {
            String [] tmp = new String[columnList.size()];
            for(int j = 0; j < columnList.size();  j++)
            {
                if(columnList.get(j).getValue(i).isEmpty())
                    tmp[j] = null;
                else
                    tmp[j] = columnList.get(j).getValue(i);
            }

            System.out.println(String.format("%11s| %8s| %6s| %60s| %6s| %4s| %6s| %6s| %20s| %15s| %20s| %8s|",
                    tmp[0],tmp[1],tmp[2],tmp[3],tmp[4],tmp[5],tmp[6],tmp[7],tmp[8],tmp[9],tmp[10],tmp[11] ));
        }
    }

    @Override
    public Table getStats() {
        return null;
    }

    @Override
    public Table head() {
        return null;
    }

    @Override
    public Table head(int lineCount) {
        return null;
    }

    @Override
    public Table tail() {
        return null;
    }

    @Override
    public Table tail(int lineCount) {
        return null;
    }

    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        return null;
    }

    @Override
    public Table selectRowsAt(int... indices) {
        return null;
    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        return null;
    }

    @Override
    public Table selectColumnsAt(int... indices) {
        return null;
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        return null;
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
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
