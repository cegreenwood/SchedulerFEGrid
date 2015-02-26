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


class SchedJobLogStdData extends AbstractTableModel {
    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Log Date/Time", 140, JLabel.CENTER ),
      new SchedColumnData("Log Id",80, JLabel.CENTER ),
      new SchedColumnData("D/Base", 80, JLabel.CENTER ),
      new SchedColumnData("Job Owner", 100, JLabel.LEFT ),
      new SchedColumnData("Job Name", 200, JLabel.LEFT ),
      new SchedColumnData("Operation", 100, JLabel.CENTER ),
      new SchedColumnData("Status", 100, JLabel.LEFT )
    };

    private Vector<jobLogStd>        jobLogObj;

    private jobLogStd                mJobLogStd;

    // Class constructor schedLogData.
    public SchedJobLogStdData() {
        jobLogObj = new Vector<jobLogStd>(40,10);
    }

    public int getRowCount() {
        return jobLogObj.size();
    }

    public int getColumnCount() {
        return 7;
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
        mJobLogStd = getJobLog(nRow);

        switch ( nCol ) {
          case 0:
              return mJobLogStd.getLogDate();
          case 1:
              return mJobLogStd.getLogId();
          case 2:
              return mJobLogStd.getDatabase();
          case 3:
              return mJobLogStd.getOwner();
          case 4:
              return mJobLogStd.getJobName();
          case 5:
              return mJobLogStd.getOperation();
          case 6:
              return mJobLogStd.getStatus();
        }
        return "";
    }
    public boolean addJobLog(String mLogDate,
                             String mLogId,
                             String mDatabase,
                             String mOwner,
                             String mJobName,
                             String mOperation,
                             String mStatus) {

        mJobLogStd = new jobLogStd(mLogDate,
                                   mLogId,
                                   mDatabase,
                                   mOwner,
                                   mJobName,
                                   mOperation,
                                   mStatus);

        return jobLogObj.add(mJobLogStd);

    }

    public jobLogStd getJobLog(int logId) {
        return jobLogObj.get(logId);
    }

    public int sizeJobLog() {
        return jobLogObj.size();
    }

    public void clearJobLog() {
        jobLogObj.clear();
    }

    class jobLogStd {

        private String   mLogDate;
        private String   mLogId;
        private String   mDatabase;
        private String   mOwner;
        private String   mJobName;
        private String   mOperation;
        private String   mStatus;

        public jobLogStd(String logDate,
                         String logId,
                         String database,
                         String owner,
                         String jobName,
                         String operation,
                         String status) {
            mLogDate = logDate;
            mLogId = logId;
            mDatabase = database;
            mOwner = owner;
            mJobName = jobName;
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
        public String getOwner() {
            return mOwner;
        }
        public String getJobName() {
            return mJobName;
        }
        public String getOperation() {
            return mOperation;
        }
        public String getStatus() {
            return mStatus;
        }
    }
}

