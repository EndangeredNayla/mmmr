package org.mmmr.services.swing.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

/**
 * J_DOC
 * 
 * @author jdlandsh
 */
public class ETable extends JTable implements ETableI {
    /**
     * J_DOC
     */
    protected class FilterPopup extends JWindow {
        /** serialVersionUID */
        private static final long serialVersionUID = 5033445579635687866L;

        protected JTextField popupTextfield = new JTextField();

        protected int popupForColumn = -1;

        protected Map<Integer, String> popupFilters = new HashMap<Integer, String>();

        /**
         * Instantieer een nieuwe FilterPopup
         * 
         * @param frame
         */
        public FilterPopup(Frame frame) {
            super(frame);

            this.popupTextfield.setBackground(new Color(246, 243, 149));
            this.getContentPane().add(this.popupTextfield, BorderLayout.CENTER);
            this.popupTextfield.setFocusTraversalKeysEnabled(false);
            this.popupTextfield.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    FilterPopup.this.setVisible(false);
                }
            });
            this.popupTextfield.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        // preview filtering
                        System.out.println("preview filter " + FilterPopup.this.popupTextfield.getText());
                        ETable.this.matcherEditor.fire(new RecordMatcher(FilterPopup.this.popupForColumn, FilterPopup.this.popupTextfield.getText()));
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        // revert filtering, close
                        System.out.println("revert filter");
                        ETable.this.matcherEditor.fire(new RecordMatcher(FilterPopup.this.popupForColumn, FilterPopup.this.popupFilters
                                .get(FilterPopup.this.popupForColumn)));
                        FilterPopup.this.setVisible(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        // commit filtering
                        System.out.println("filter " + FilterPopup.this.popupTextfield.getText());
                        ETable.this.matcherEditor.fire(new RecordMatcher(FilterPopup.this.popupForColumn, FilterPopup.this.popupTextfield.getText()));
                        FilterPopup.this.popupFilters.put(FilterPopup.this.popupForColumn, FilterPopup.this.popupTextfield.getText());
                        FilterPopup.this.setVisible(false);
                    } else {
                        //
                    }
                }
            });
        }

        /**
         * J_DOC
         * 
         * @param p
         */
        public void activate(Point p) {
            this.popupForColumn = ETable.this.getTableHeader().columnAtPoint(p);
            String filter = this.popupFilters.get(this.popupForColumn);
            this.popupTextfield.setText(filter);
            Rectangle headerRect = ETable.this.getTableHeader().getHeaderRect(this.popupForColumn);
            Point pt = ETable.this.getLocationOnScreen();
            pt.translate(headerRect.x - 1, -headerRect.height - 1);
            this.setLocation(pt);
            this.setSize(headerRect.width, headerRect.height);
            this.toFront();
            this.setVisible(true);
            this.requestFocusInWindow();
            this.popupTextfield.requestFocusInWindow();
        }

        /**
         * J_DOC
         */
        public void clear() {
            this.popupFilters.clear();
            this.popupForColumn = -1;
        }
    }

    /**
     * J_DOC
     */
    protected class FilterPopupActivate extends MouseAdapter {
        /**
         * 
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON3)) {
                ETable.this.getFilterPopup().activate(e.getPoint());
            }
        }
    }

    /**
     * J_DOC
     */
    protected class RecordMatcher implements Matcher<ETableRecord> {
        protected final Pattern pattern;

        protected final int column;

        /**
         * Instantieer een nieuwe RecordMatcher
         * 
         * @param column
         * @param text
         */
        public RecordMatcher(int column, String text) {
            this.column = column;
            this.pattern = text == null ? null : Pattern.compile(text, Pattern.CASE_INSENSITIVE);
        }

        /**
         * @see ca.odell.glazedlists.matchers.Matcher#matches(java.lang.Object)
         */
        @Override
        public boolean matches(ETableRecord item) {
            if (this.pattern == null) {
                return true;
            }
            String value = item.getStringValue(this.column);
            if (value == null) {
                return false;
            }
            return this.pattern.matcher(value).find();
        }
    }

    /**
     * J_DOC
     */
    protected class RecordMatcherEditor extends AbstractMatcherEditor<ETableRecord> {
        public void fire(Matcher<ETableRecord> matcher) {
            this.fireChanged(matcher);
        }
    }

    /** serialVersionUID */
    private static final long serialVersionUID = 6515690492295488815L;

    protected EventList<ETableRecord> records;

    protected SortedList<ETableRecord> sortedRecords;

    protected FilterList<ETableRecord> filteredRecords;

    protected EventTableModel<ETableRecord> tableModel;

    protected EventSelectionModel<ETableRecord> selectionModel;

    protected ETableHeaders tableFormat;

    protected TableComparatorChooser<ETableRecord> tableSorter;

    protected FilterPopup filterPopup = null;

    protected RecordMatcherEditor matcherEditor;

    /**
     * Instantieer een nieuwe ETable
     */
    protected ETable() {
        this(false);
    }

    /**
     * Instantieer een nieuwe ETable
     * 
     * @param threadSafe
     */
    protected ETable(boolean threadSafe) {
        this.records = (threadSafe ? GlazedLists.threadSafeList(new BasicEventList<ETableRecord>()) : new BasicEventList<ETableRecord>());
        this.sortedRecords = new SortedList<ETableRecord>(this.records, null);
        this.tableFormat = new ETableHeaders();
        this.matcherEditor = new RecordMatcherEditor();
        this.filteredRecords = new FilterList<ETableRecord>(this.sortedRecords, this.matcherEditor);
        this.tableModel = new EventTableModel<ETableRecord>(this.filteredRecords, this.tableFormat) {
            private static final long serialVersionUID = -8936359559294414836L;

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return ETable.this.tableFormat.getColumnClass(columnIndex);
            };
        };
        this.setModel(this.tableModel);

        this.selectionModel = new EventSelectionModel<ETableRecord>(this.records);
        this.selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setColumnSelectionAllowed(true);
        this.setRowSelectionAllowed(true);
        this.setSelectionModel(this.selectionModel);

        this.tableSorter = TableComparatorChooser.install(this, this.sortedRecords, AbstractTableComparatorChooser.MULTIPLE_COLUMN_MOUSE,
                this.tableFormat);

        this.getTableHeader().addMouseListener(new FilterPopupActivate());
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#addRecord(be.ugent.oasis.tools.hqlbuilder.ETable.ETableRecord)
     */
    @Override
    public void addRecord(final ETableRecord record) {
        this.records.add(record);
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#addRecords(java.util.Collection)
     */
    @Override
    public void addRecords(final Collection<ETableRecord> r) {
        this.records.addAll(r);
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#clear()
     */
    @Override
    public void clear() {
        this.records.clear();
        this.tableSorter.dispose();
        this.tableModel.setTableFormat(new ETableHeaders());
        this.getFilterPopup().clear();
    }

    /**
     * 
     * @see javax.swing.JTable#createDefaultTableHeader()
     */
    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(this.columnModel) {
            private static final long serialVersionUID = -378778832166135907L;

            @Override
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int index = this.columnModel.getColumnIndexAtX(p.x);

                // int realIndex = columnModel.getColumn(index).getModelIndex();
                String headerValue = String.valueOf(this.columnModel.getColumn(index).getHeaderValue());

                String filter = ETable.this.getFilterPopup().popupFilters.get(index);

                if ((filter != null) && (filter.trim().length() > 0)) {
                    headerValue += "<br/>" + "filter: '" + filter + "'";
                } else {
                    headerValue += "<br/>" + "no filter";
                }

                headerValue += "<br/><br/>right click to edit filter<br/>enter to preview filter<br/>tab to accept filter";

                return "<html><body>" + headerValue + "</body></html>";
            }
        };
    }

    /**
     * J_DOC
     * 
     * @return
     */
    public ETableI getEventSafe() {
        final ETable table = this;
        javassist.util.proxy.ProxyFactory f = new javassist.util.proxy.ProxyFactory();
        f.setInterfaces(new Class[] { ETableI.class });
        javassist.util.proxy.MethodHandler mi = new javassist.util.proxy.MethodHandler() {
            @Override
            public Object invoke(final Object self, final java.lang.reflect.Method method, final java.lang.reflect.Method paramMethod2,
                    final Object[] args) throws Throwable {
                boolean edt = javax.swing.SwingUtilities.isEventDispatchThread();

                if (edt) {
                    return method.invoke(table, args);
                }

                final Object[] values = new Object[] { null, null };
                Runnable doRun = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            values[0] = method.invoke(table, args);
                        } catch (Exception ex) {
                            values[1] = ex;
                        }
                    }
                };
                boolean wait = !method.getReturnType().equals(Void.TYPE);
                if (!wait) {
                    SwingUtilities.invokeLater(doRun);
                    return Void.TYPE;
                }
                SwingUtilities.invokeAndWait(doRun);
                if (values[1] != null) {
                    throw Exception.class.cast(values[1]);
                }
                return values[0];
            }
        };
        Object proxy;
        try {
            proxy = f.createClass().newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        ((javassist.util.proxy.ProxyObject) proxy).setHandler(mi);
        return (ETableI) proxy;
    }

    /**
     * J_DOC
     * 
     * @return
     */
    protected FilterPopup getFilterPopup() {
        if (this.filterPopup == null) {
            this.filterPopup = new FilterPopup(this.getFrame(this));
        }

        return this.filterPopup;
    }

    /**
     * J_DOC
     * 
     * @param comp
     * @return
     */
    protected Frame getFrame(Component comp) {
        if (comp == null) {
            comp = this;
        }
        if (comp.getParent() instanceof Frame) {
            return (Frame) comp.getParent();
        }
        return this.getFrame(comp.getParent());
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#getRecordAtVisualRow(int)
     */
    @Override
    public ETableRecord getRecordAtVisualRow(int i) {
        return this.filteredRecords.get(i);
    }

    /**
     * 
     * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        try {
            java.awt.Point p = e.getPoint();
            int rowIndex = this.rowAtPoint(p);
            int colIndex = this.columnAtPoint(p);
            int realColumnIndex = this.convertColumnIndexToModel(colIndex);

            return String.valueOf(this.getModel().getValueAt(rowIndex, realColumnIndex));
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    /**
     * Sets the preferred width of the visible column specified by vColIndex. The column will be just wide enough to show the column head and the
     * widest cell in the column. margin pixels are added to the left and right (resulting in an additional width of 2*margin pixels).
     * 
     * @param table
     * @param vColIndex
     * @param margin
     */
    public void packColumn(int vColIndex, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) this.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;

        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = this.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(this, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        // Get maximum width of column data
        for (int r = 0; r < this.getRowCount(); r++) {
            renderer = this.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(this, this.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // Add margin
        width += 2 * margin;

        // Set the width
        col.setPreferredWidth(width);
        col.setWidth(width);
        col.setMaxWidth(width);
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#removeAllRecords()
     */
    @Override
    public void removeAllRecords() {
        this.records.clear();
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#removeRecord(be.ugent.oasis.tools.hqlbuilder.ETable.ETableRecord)
     */
    @Override
    public void removeRecord(final ETableRecord record) {
        this.records.remove(record);
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#removeRecordAtVisualRow(int)
     */
    @Override
    public void removeRecordAtVisualRow(final int i) {
        this.records.remove(this.sortedRecords.get(i));
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#setHeaders(be.ugent.oasis.tools.hqlbuilder.ETable.ETableHeaders)
     */
    @Override
    public void setHeaders(final ETableHeaders headers) {
        this.tableSorter.dispose();
        this.tableModel.setTableFormat(headers);
        this.tableSorter = TableComparatorChooser.install(ETable.this, this.sortedRecords, AbstractTableComparatorChooser.MULTIPLE_COLUMN_MOUSE,
                headers);
        this.tableFormat = headers;
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#sort(int)
     */
    @Override
    public void sort(final int col) {
        this.tableSorter.clearComparator();
        this.tableSorter.appendComparator(col, 0, false);
    }

    public void test() {
        if (this.getColumnSelectionAllowed() && !this.getRowSelectionAllowed()) {
            // Column selection is enabled
            // Get the indices of the selected columns
            int[] vColIndices = this.getSelectedColumns();
            System.out.println("cols:" + Arrays.toString(vColIndices));
        } else if (!this.getColumnSelectionAllowed() && this.getRowSelectionAllowed()) {
            // Row selection is enabled
            // Get the indices of the selected rows
            int[] rowIndices = this.getSelectedRows();
            System.out.println("cols:" + Arrays.toString(rowIndices));
        } else if (this.getCellSelectionEnabled()) {
            // Individual cell selection is enabled

            // In SINGLE_SELECTION mode, the selected cell can be retrieved using
            // setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            @SuppressWarnings("unused")
            int rowIndex = this.getSelectedRow();
            @SuppressWarnings("unused")
            int colIndex = this.getSelectedColumn();

            // In the other modes, the set of selected cells can be retrieved using
            // setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            // setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            // Get the min and max ranges of selected cells
            int rowIndexStart = this.getSelectedRow();
            int rowIndexEnd = this.getSelectionModel().getMaxSelectionIndex();
            int colIndexStart = this.getSelectedColumn();
            int colIndexEnd = this.getColumnModel().getSelectionModel().getMaxSelectionIndex();

            // Check each cell in the range
            for (int r = rowIndexStart; r <= rowIndexEnd; r++) {
                for (int c = colIndexStart; c <= colIndexEnd; c++) {
                    if (this.isCellSelected(r, c)) {
                        // cell is selected
                        System.out.println("cell:(" + c + "," + r + ")");
                        System.out.println(this.getEventSafe().getRecordAtVisualRow(r).get(c));
                    }
                }
            }
        }
    }

    /**
     * @see be.ugent.oasis.tools.hqlbuilder.ETableI#unsort()
     */
    @Override
    public void unsort() {
        this.tableSorter.clearComparator();
    }
}
