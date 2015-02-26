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

class SchedRunData extends AbstractTableModel {
    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Log Id",80, JLabel.CENTER ),
      new SchedColumnData("D/Base", 80, JLabel.CENTER ),
      new SchedColumnData("Job Owner", 100, JLabel.LEFT ),
      new SchedColumnData("Job Name", 200, JLabel.LEFT ),
      new SchedColumnData("Resource Consumer Group", 200, JLabel.LEFT ),
      new SchedColumnData("Elapsed Time", 120, JLabel.LEFT )
    };

    private Vector<jobRun>        jobRunObj;

    private jobRun                mJobRun;

    // Class constructor schedRunData.
    public SchedRunData() {
        jobRunObj = new Vector<jobRun>(40,10);
    }

    // public void focusGained(FocusEvent e) {
    //     System.out.println( "Job table - Focus Gained." );
    // }
    // public void focusLost(FocusEvent e) {
    //     System.out.println( "Job table - Focus Lost." );
    // }

    public int getRowCount() {
        return jobRunObj.size();
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
        mJobRun = getJobRun(nRow);

        switch ( nCol ) {
          case 0:
              return mJobRun.getId();
          case 1:
              return mJobRun.getDatabase();
          case 2:
              return mJobRun.getOwner();
          case 3:
              return mJobRun.getJobName();
          case 4:
              return mJobRun.getResourceConsumerGroup();
          case 5:
              return mJobRun.getElapsedTime();
        }
        return "";
    }


    public boolean addJobRun(String mId,
                             String mDatabase,
                             String mOwner,
                             String mJobName,
                             String mResourceConsumerGroup,
                             String mElapsedTime) {

        mJobRun = new jobRun(mId,
                             mDatabase,
                             mOwner,
                             mJobName,
                             mResourceConsumerGroup,
                             mElapsedTime);

        return jobRunObj.add(mJobRun);
    }

    public jobRun getJobRun(int Id) {
        return jobRunObj.get(Id);
    }

    public int sizeJobRun() {
        return jobRunObj.size();
    }

    public void clearJobRun() {
        jobRunObj.clear();
    }

    class jobRun {

        private String   mId;
        private String   mDatabase;
        private String   mOwner;
        private String   mJobName;
        private String   mResourceConsumerGroup;
        private String   mElapsedTime;

        public jobRun(String Id,
                      String database,
                      String owner,
                      String jobName,
                      String resourceConsumerGroup,
                      String elapsedTime) {
            mId = Id;
            mDatabase = database;
            mOwner = owner;
            mJobName = jobName;
            mResourceConsumerGroup = resourceConsumerGroup;
            mElapsedTime = elapsedTime;
        }
        public String getId() {
            return mId;
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
        public String getResourceConsumerGroup() {
            return mResourceConsumerGroup;
        }
        public String getElapsedTime() {
            return mElapsedTime;
        }
    }
}

