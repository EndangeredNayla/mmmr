package org.mmmr.services.swing.common;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

public class ETableRecordBean implements ETableRecord {
    protected Object object;

    private List<String> orderedFields;

    /**
     * 
     * Instantieer een nieuwe ETableRecordBean
     * 
     * @param orderedFields
     * @param o
     */
    public ETableRecordBean(List<String> orderedFields, Object o) {
        this.object = o;
        this.orderedFields = orderedFields;
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#get(int)
     */
    @Override
    public Object get(int column) {
        try {
            return PropertyUtils.getProperty(this.object, this.orderedFields.get(column));
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#getBean()
     */
    @Override
    public Object getBean() {
        return this.object;
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#getStringValue(int)
     */
    @Override
    public String getStringValue(int column) {
        Object value = this.get(column);
        return value == null ? null : "" + value;
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#set(int, java.lang.Object)
     */
    @Override
    public void set(int column, Object newValue) {
        try {
            PropertyUtils.setProperty(this.object, this.orderedFields.get(column), newValue);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#size()
     */
    @Override
    public int size() {
        return this.orderedFields.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "" + this.object;
    }
}
