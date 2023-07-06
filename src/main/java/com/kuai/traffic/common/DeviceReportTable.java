package com.kuai.traffic.common;

import java.awt.Component;
import java.util.Date;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.kuai.traffic.TrafficFrame;

@SuppressWarnings("serial")
public class DeviceReportTable extends JTable {
  private String[] columns = new String[] {
      "Timestamp", "Data"
  };
   
  private Object[][] tdata = new Object[][] {
  };
   
  public DeviceReportTable() {
    super();

    DefaultTableModel model = new DefaultTableModel(tdata, columns);
    setModel(model);
  }

  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
      Component component = super.prepareRenderer(renderer, row, column);
      int rendererWidth = component.getPreferredSize().width;
      TableColumn tableColumn = getColumnModel().getColumn(column);
      tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
      return component;
   }

  public void addDataRow(String dataRow) {
    setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

    DefaultTableModel model = (DefaultTableModel)getModel();

    String timestamp = TrafficFrame.getInstance().getTimeStringInMilliSeconds((new Date()).getTime());
    
    Vector<String> row = new Vector<>();
    row.add(timestamp);
    row.add(dataRow);
    model.addRow(row);
    
    scrollRectToVisible(getCellRect(getRowCount()-1, 0, true));
  }
}
