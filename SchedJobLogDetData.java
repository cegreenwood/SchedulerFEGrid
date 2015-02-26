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

class SchedJobLogDetData extends AbstractTableModel {
    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Log Date/Time", 140, JLabel.CENTER ),
      new SchedColumnData("Log Id",80, JLabel.CENTER ),
      new SchedColumnData("D/Base",80, JLabel.CENTER ),
      new SchedColumnData("Job Owner", 100, JLabel.LEFT ),
      new SchedColumnData("Job Name", 200, JLabel.LEFT ),
      new SchedColumnData("Last Run Duration", 100, JLabel.CENTER ),
      new SchedColumnData("Status", 100, JLabel.LEFT )
    };

    private Vector<jobLogDet>        jobLogObj;

    private jobLogDet                mJobLogDet;

    // Class constructor schedJobLogData.
    public SchedJobLogDetData() {
        jobLogObj = new Vector<jobLogDet>(40,10);
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
        mJobLogDet = getJobLog(nRow);

        switch ( nCol ) {
          case 0:
              return mJobLogDet.getLogDate();
          case 1:
              return mJobLogDet.getLogId();
          case 2: 
              return mJobLogDet.getDatabase();
          case 3: 
              return mJobLogDet.getOwner();
          case 4: 
              return mJobLogDet.getJobName();
          case 5: 
              return mJobLogDet.getDuration();
          case 6: 
              return mJobLogDet.getStatus();
        }
        return "";
    }

    public boolean addJobLog(String mLogDate,
                             String mLogId,
                             String mDatabase,
                             String mOwner,
                             String mJobName,
                             String mDuration,
                             String mStatus) {

        mJobLogDet = new jobLogDet(mLogDate,
                                   mLogId,
                                   mDatabase,
                                   mOwner,
                                   mJobName,
                                   mDuration,
                                   mStatus);

        return jobLogObj.add(mJobLogDet);

    }

    public jobLogDet getJobLog(int logId) {
        return jobLogObj.get(logId);
    }

    public int sizeJobLog() {
        return jobLogObj.size();
    }

    public void clearJobLog() {
        jobLogObj.clear();
    }

    class jobLogDet {

        private String   mLogDate;
        private String   mLogId;
        private String   mDatabase;
        private String   mOwner;
        private String   mJobName;
        private String   mDuration;
        private String   mStatus;

        public jobLogDet(String logDate,
                         String logId,
                         String database,
                         String owner,
                         String jobName,
                         String duration,
                         String status) {
            mLogDate = logDate;
            mLogId = logId;
            mDatabase = database;
            mOwner = owner;
            mJobName = jobName;
            mDuration = duration;
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
        public String getDuration() {
            return mDuration;
        }
        public String getStatus() {
            return mStatus;
        }
    }
}
