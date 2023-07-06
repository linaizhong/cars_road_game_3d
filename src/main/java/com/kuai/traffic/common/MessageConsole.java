package com.kuai.traffic.common;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MessageConsole {
  private JTextComponent textComponent;
  private Document document;
  private boolean isAppend;
  private DocumentListener limitLinesListener;

  private String toggleText = "DEBUG";
  private boolean showToggleText = true;

  private String toggleFilter = "";
  private boolean showToggleFilter = false;
  
//  private List<String> logs = new ArrayList<>();

  public MessageConsole(JTextComponent textComponent) {
    this(textComponent, true);
  }

  /*
   * Use the text component specified as a simply console to display text messages.
   *
   * The messages can either be appended to the end of the console or inserted as the first line of
   * the console.
   */
  public MessageConsole(JTextComponent textComponent, boolean isAppend) {
    this.textComponent = textComponent;
    this.document = textComponent.getDocument();
    this.isAppend = isAppend;
    textComponent.setEditable(false);
  }

  public void clear() {
    this.textComponent.setText("");
  }

  /*
   * Redirect the output from the standard output to the console using the default text color and
   * null PrintStream
   */
  public void redirectOut() {
    redirectOut(null, null);
  }

  /*
   * Redirect the output from the standard output to the console using the specified color and
   * PrintStream. When a PrintStream is specified the message will be added to the Document before
   * it is also written to the PrintStream.
   */
  public void redirectOut(Color textColor, PrintStream printStream) {
    ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
    System.setOut(new PrintStream(cos, true));
  }

  /*
   * Redirect the output from the standard error to the console using the default text color and
   * null PrintStream
   */
  public void redirectErr() {
    redirectErr(null, null);
  }

  /*
   * Redirect the output from the standard error to the console using the specified color and
   * PrintStream. When a PrintStream is specified the message will be added to the Document before
   * it is also written to the PrintStream.
   */
  public void redirectErr(Color textColor, PrintStream printStream) {
    ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
    System.setErr(new PrintStream(cos, true));
  }

  /*
   * To prevent memory from being used up you can control the number of lines to display in the
   * console
   *
   * This number can be dynamically changed, but the console will only be updated the next time the
   * Document is updated.
   */
  public void setMessageLines(int lines) {
    if (limitLinesListener != null)
      document.removeDocumentListener(limitLinesListener);

    limitLinesListener = new LimitLinesDocumentListener(lines, isAppend);
    document.addDocumentListener(limitLinesListener);
  }

  public void setToggleText(String toggleText) {
    this.toggleText = toggleText;
  }

  public boolean isShowToggleText() {
    return this.showToggleText;
  }

  public void setShowToggleText(boolean showToggleText) {
    this.showToggleText = showToggleText;
  }

  public String getToggleFilter() {
    return toggleFilter;
  }

  public void setToggleFilter(String toggleFilter) {
    this.toggleFilter = toggleFilter;
  }

  public boolean isShowToggleFilter() {
    return showToggleFilter;
  }

  public void setShowToggleFilter(boolean showToggleFilter) {
    this.showToggleFilter = showToggleFilter;
  }

  public String getToggleText() {
    return toggleText;
  }

  /*
   * Class to intercept output from a PrintStream and add it to a Document. The output can
   * optionally be redirected to a different PrintStream. The text displayed in the Document can be
   * color coded to indicate the output source.
   */
  class ConsoleOutputStream extends ByteArrayOutputStream {
    private final String EOL = System.getProperty("line.separator");
    private SimpleAttributeSet attributes;
    private PrintStream printStream;
    private StringBuffer buffer = new StringBuffer(80);
    private boolean isFirstLine;
    List<String> logs = new ArrayList<>();

    /*
     * Specify the option text color and PrintStream
     */
    public ConsoleOutputStream(Color textColor, PrintStream printStream) {
      if (textColor != null) {
        attributes = new SimpleAttributeSet();
        StyleConstants.setForeground(attributes, textColor);
      }

      this.printStream = printStream;

      if (isAppend)
        isFirstLine = true;

//      (new Thread(new Runnable() {
//        @Override
//        public void run() {
//          while(true) {
//            if(!logs.isEmpty()) {
//              printStream.println("Testing 001: " + logs.size());
//              
//              append(logs.get(0));
//            }
//
//            try {
//              Thread.sleep(10);
//            } catch(Exception e) {}
//          }
//        }
//      })).start();
//      
//      Timer t = new Timer(100, new ActionListener() {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//        }
//      });
//      t.start();
    }

    /*
     * Override this method to intercept the output text. Each line of text output will actually
     * involve invoking this method twice:
     *
     * a) for the actual text message b) for the newLine string
     *
     * The message will be treated differently depending on whether the line will be appended or
     * inserted into the Document
     */
    public void flush() {
      String message = toString();

      if (message.length() == 0)
        return;

      if (isAppend)
        handleAppend(message);
      else
        handleInsert(message);

      reset();
    }

    /*
     * We don't want to have blank lines in the Document. The first line added will simply be the
     * message. For additional lines it will be:
     *
     * newLine + message
     */
    private void handleAppend(String message) {
      // This check is needed in case the text in the Document has been
      // cleared. The buffer may contain the EOL string from the previous
      // message.

      if ((message.indexOf(toggleText) > 0) && !showToggleText)
        return;

      if (!showToggleFilter || ((message.indexOf(toggleFilter) >= 0) && showToggleFilter)) {
        // if(showToggleFilter)
        // message += "\n";

        if (document.getLength() == 0)
          buffer.setLength(0);

        if (EOL.equals(message)) {
          buffer.append(message);
        } else {
          buffer.append(message);
          clearBuffer();
        }
      }
    }

    /*
     * We don't want to merge the new message with the existing message so the line will be inserted
     * as:
     *
     * message + newLine
     */
    private void handleInsert(String message) {
      if ((message.indexOf(toggleText) > 0) && !showToggleText)
        return;

      if (!showToggleFilter || ((message.indexOf(toggleFilter) >= 0) && showToggleFilter)) {
        // if(showToggleFilter)
        // message += "\n";

        buffer.append(message);

        if (EOL.equals(message)) {
          clearBuffer();
        }
      }
    }

    /*
     * The message and the newLine have been added to the buffer in the appropriate order so we can
     * now update the Document and send the text to the optional PrintStream.
     */
    private void clearBuffer() {
      // In case both the standard out and standard err are being redirected
      // we need to insert a newline character for the first line only

      if (isFirstLine && document.getLength() != 0) {
        buffer.insert(0, "\n");
      }

      isFirstLine = false;
      String line = buffer.toString();

      //printStream.print("Testing 001: " + line);
      
      if (isAppend) {
        //logs.add(line);
        
        append(line);
      } else {
        insert(line);
      }

      if (printStream != null) {
        printStream.print(line);
      }

      buffer.setLength(0);
    }

    private void append(String line) {
    	try {
        final int offset = document.getLength();
        if(offset >= 0) {
            document.insertString(offset, line, attributes);
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                textComponent.repaint();
                
//            	if(document != null && textComponent != null)
//            		textComponent.setCaretPosition(document.getLength());
              }
            });
        }
      } catch (BadLocationException ble) {
      }
    }

    private void insert(String line) {
      try {
        document.insertString(0, line, attributes);
        (new Thread(new Runnable() {
          @Override
          public void run() {
            textComponent.repaint();
            textComponent.setCaretPosition(0);
          }
        })).start();
      } catch (BadLocationException ble) {
      }
    }
  }
  
//  
//  class LogDisplay extends SwingWorker<List<String>, String> {
//    private PrintStream printStream;
//    SimpleAttributeSet attributes;
//    
//    public LogDisplay(SimpleAttributeSet attr, PrintStream ps) {
//      this.attributes = attr;
//      this.printStream = ps;
//    }
//    
//    protected List<String> doInBackground() throws Exception {
//      List<String> contents = new ArrayList<>();
//      
//      synchronized(logs) {
//        if(!logs.isEmpty()) {
//          for(String line : logs) {
//            printStream.print("Testing 002: " + line);
//            contents.add(line);
//            publish(line);
//          }
//          
//          logs.clear();
//        }
//      }
//      
//      return contents;
//    }
//
//    @Override
//    protected void process(List<String> chunks) {
//      for (String line : chunks) {
//        if (line.length() > 0) {
//          printStream.print("Testing 003: " + line);
//          append(line);
//        }
//      }
//    }
//
//    private void append(String line) {
//      try {
//        printStream.print("Testing 004: " + line);
//        int offset = document.getLength();
//        document.insertString(offset, line, attributes);
//        textComponent.setCaretPosition(document.getLength());
//      } catch (BadLocationException ble) {
//      }
//    }
//  }
}