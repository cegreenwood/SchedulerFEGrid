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

class SchedWindowLogDetData extends AbstractTableModel {
    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Log Date/Time", 140, JLabel.CENTER ),
      new SchedColumnData("Log Id",80, JLabel.CENTER ),
      new SchedColumnData("D/Base", 80, JLabel.CENTER ),
      new SchedColumnData("Window Name", 200, JLabel.LEFT ),
      new SchedColumnData("Window Dur.", 100, JLabel.CENTER ),
      new SchedColumnData("Actual Dur.", 100, JLabel.LEFT )
    };

    private Vector<windowLogDet>        windowLogObj;

    private windowLogDet                mWindowLogDet;

    // Class constructor schedLogData.
    public SchedWindowLogDetData() {
        windowLogObj = new Vector<windowLogDet>(40,10);
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
        mWindowLogDet = getWindowLog(nRow);

        switch ( nCol ) {
          case 0:
              return mWindowLogDet.getLogDate();
          case 1:
              return mWindowLogDet.getLogId();
          case 2:
              return mWindowLogDet.getDatabase();
          case 3:
              return mWindowLogDet.getWindowName();
          case 4:
              return mWindowLogDet.getWinDuration();
          case 5:
              return mWindowLogDet.getActDuration();
        }
        return "";
    }

    public boolean addWindowLog(String mLogDate,
                             String mLogId,
                             String mDatabase,
                             String mWindowName,
                             String mWinDuration,
                             String mActDuration) {

        mWindowLogDet = new windowLogDet(mLogDate,
                                         mLogId,
                                         mDatabase,
                                         mWindowName,
                                         mWinDuration,
                                         mActDuration);

        return windowLogObj.add(mWindowLogDet);

    }

    public windowLogDet getWindowLog(int logId) {
        return windowLogObj.get(logId);
    }

    public int sizeWindowLog() {
        return windowLogObj.size();
    }

    public void clearWindowLog() {
        windowLogObj.clear();
    }

    class windowLogDet {

        private String   mLogDate;
        private String   mLogId;
        private String   mDatabase;
        private String   mWindowName;
        private String   mWinDuration;
        private String   mActDuration;

        public windowLogDet(String logDate,
                            String logId,
                            String database,
                            String windowName,
                            String winDuration,
                            String actDuration) {
            mLogDate = logDate;
            mLogId = logId;
            mDatabase = database;
            mWindowName = windowName;
            mWinDuration = winDuration;
            mActDuration = actDuration;
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
        public String getWinDuration() {
            return mWinDuration;
        }
        public String getActDuration() {
            return mActDuration;
        }
    }
}

