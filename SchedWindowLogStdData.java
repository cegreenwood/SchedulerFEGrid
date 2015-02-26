/**
 *
 * Copyright 2015 Blueshire Services Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.blueshireservices.schedulergrid;

import java.io.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import org.jdesktop.swingx.JXTable;

class SchedWindowLogStdData extends AbstractTableModel {
    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Log Date/Time", 140, JLabel.CENTER ),
      new SchedColumnData("Log Id",80, JLabel.CENTER ),
      new SchedColumnData("D/Base", 80, JLabel.CENTER ),
      new SchedColumnData("Window Name", 200, JLabel.LEFT ),
      new SchedColumnData("Operation", 100, JLabel.CENTER ),
      new SchedColumnData("Status", 100, JLabel.LEFT )
    };

    private Vector<windowLogStd>        windowLogObj;

    private windowLogStd                mWindowLogStd;

    // Class constructor schedLogData.
    public SchedWindowLogStdData() {
        windowLogObj = new Vector<windowLogStd>(40,10);
    }

    public int getRowCount() {
        return windowLogObj.size();
    }

    public int getColumnCount() {
        return 6;
    }

    public String getColumnName(int nCol) {
        return m_columns[nCol].m_title;
    }

    public boolean isCellEditable(int nRow, int nCol) {
        return false;
    }

    public Object getValueAt(int nRow, int nCol) {
        if (nRow < 0 || nRow > getRowCount()) {
            return "";
        }
        mWindowLogStd = getWindowLog(nRow);

        switch ( nCol ) {
          case 0:
              return mWindowLogStd.getLogDate();
          case 1:
              return mWindowLogStd.getLogId();
          case 2:
              return mWindowLogStd.getDatabase();
          case 3:
              return mWindowLogStd.getWindowName();
          case 4:
              return mWindowLogStd.getOperation();
          case 5:
              return mWindowLogStd.getStatus();
        }
        return "";
    }

    public boolean addWindowLog(String mLogDate,
                                String mLogId,
                                String mDatabase,
                                String mWindowName,
                                String mWinDuration,
                                String mActDuration) {

        mWindowLogStd = new windowLogStd(mLogDate,
                                         mLogId,
                                         mDatabase,
                                         mWindowName,
                                         mWinDuration,
                                         mActDuration);

        return windowLogObj.add(mWindowLogStd);

    }

    public windowLogStd getWindowLog(int logId) {
        return windowLogObj.get(logId);
    }

    public int sizeWindowLog() {
        return windowLogObj.size();
    }

    public void clearWindowLog() {
        windowLogObj.clear();
    }

    class windowLogStd {

        private String   mLogDate;
        private String   mLogId;
        private String   mDatabase;
        private String   mWindowName;
        private String   mOperation;
        private String   mStatus;

        public windowLogStd(String logDate,
                            String logId,
                            String database,
                            String windowName,
                            String operation,
                            String status) {
            mLogDate = logDate;
            mLogId = logId;
            mDatabase = database;
            mWindowName = windowName;
            mOperation = operation;
            mStatus = status;
        }
        public String getLogDate() {
            return mLogDate;
        }
        public String getLogId() {
            return mLogId;
        }
        public String getDatabase() {
            return mDatabase;
        }
        public String getWindowName() {
            return mWindowName;
        }
        public String getOperation() {
            return mOperation;
        }
        public String getStatus() {
            return mStatus;
        }
    }
}


