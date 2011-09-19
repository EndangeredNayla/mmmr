package org.mmmr.services.swing.common;

import java.util.List;
import java.util.Vector;

import ca.odell.glazedlists.gui.WritableTableFormat;

/**
 * @author jdlandsh
 */
public class ETableHeaders implements WritableTableFormat<ETableRecord> {
    protected final List<String> columnNames = new Vector<String>();

    @SuppressWarnings("rawtypes")
    protected final List<Class> columnClasses = new Vector<Class>();

    protected final List<Boolean> editable = new Vector<Boolean>();

    /**
     * Instantieer een nieuwe Headers
     * 
     * @param names
     */
    public ETableHeaders() {
        super();
    }

    /**
     * J_DOC
     * 
     * @param column
     */
    public void add(String column) {
        this.add(column, Object.class);
    }

    /**
     * 
     * J_DOC
     * 
     * @param column
     * @param clazz
     */
    public void add(String column, Class<?> clazz) {
        this.add(column, clazz, Boolean.FALSE);
    }

    /**
     * 
     * J_DOC
     * 
     * @param column
     * @param clazz
     * @param edit
     */
    public void add(String column, Class<?> clazz, Boolean edit) {
        this.columnNames.add(column);
        this.columnClasses.add(clazz);
        this.editable.add(edit);
    }

    /**
     * J_DOC
     * 
     * @param columnIndex
     * @return
     */
    public Class<?> getColumnClass(int columnIndex) {
        if (this.columnNames.size() == 0) {
            return Object.class;
        }
        return this.columnClasses.get(columnIndex);
    }

    /**
     * @see ca.odell.glazedlists.gui.TableFormat#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return this.columnNames.size();
    }

    /**
     * @see ca.odell.glazedlists.gui.TableFormat#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        return this.columnNames.get(column);
    }

    /**
     * @see ca.odell.glazedlists.gui.TableFormat#getColumnValue(java.lang.Object, int)
     */
    @Override
    public Object getColumnValue(ETableRecord row, int column) {
        return row.get(column);
    }

    /**
     * 
     * @see ca.odell.glazedlists.gui.WritableTableFormat#isEditable(java.lang.Object, int)
     */
    @Override
    public boolean isEditable(ETableRecord baseObject, int column) {
        return Boolean.TRUE.equals(this.editable.get(column));
    }

    /**
     * 
     * @see ca.odell.glazedlists.gui.WritableTableFormat#setColumnValue(java.lang.Object, java.lang.Object, int)
     */
    @Override
    public ETableRecord setColumnValue(ETableRecord row, Object newValue, int column) {
        row.set(column, newValue);
        return row;
    }
}