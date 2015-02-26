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


class SchedRunChainData extends AbstractTableModel {
    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Log Id",80, JLabel.CENTER ),
      new SchedColumnData("D/Base", 70, JLabel.CENTER ),
      new SchedColumnData("Job Owner", 70, JLabel.LEFT ),
      new SchedColumnData("Job Name", 120, JLabel.LEFT ),
      new SchedColumnData("Chain Owner", 70, JLabel.LEFT ),
      new SchedColumnData("Chain Name", 120, JLabel.LEFT ),
      new SchedColumnData("Step Name", 120, JLabel.LEFT ),
      new SchedColumnData("State", 120, JLabel.LEFT )
    };

    private Vector<chainRun>        chainRunObj;

    private chainRun                mChainRun;

    // Class constructor schedRunData.
    public SchedRunChainData() {
        chainRunObj = new Vector<chainRun>(40,10);
    }

    public int getRowCount() {
        return chainRunObj.size();
    }

    public int getColumnCount() {
        return 8;
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
        mChainRun = getChainRun(nRow);

        switch ( nCol ) {
          case 0:
              return mChainRun.getId();
          case 1:
              return mChainRun.getDatabase();
          case 2:
              return mChainRun.getOwner();
          case 3:
              return mChainRun.getJobName();
          case 4:
              return mChainRun.getChainOwner();
          case 5:
              return mChainRun.getChainName();
          case 6:
              return mChainRun.getStepName();
          case 7:
              return mChainRun.getState();
        }
        return "";
    }
    public boolean addChainRun(String mId,
                             String mDatabase,
                             String mOwner,
                             String mJobName,
                             String mChainOwner,
                             String mChainName,
                             String mStepName,
                             String mState) {

        mChainRun = new chainRun(mId,
                             mDatabase,
                             mOwner,
                             mJobName,
                             mChainOwner,
                             mChainName,
                             mStepName,
                             mState);

        return chainRunObj.add(mChainRun);
    }

    public chainRun getChainRun(int Id) {
        return chainRunObj.get(Id);
    }

    public int sizeChainRun() {
        return chainRunObj.size();
    }

    public void clearChainRun() {
        chainRunObj.clear();
    }

    class chainRun {

        private String   mId;
        private String   mDatabase;
        private String   mOwner;
        private String   mJobName;
        private String   mChainOwner;
        private String   mChainName;
        private String   mStepName;
        private String   mState;

        public chainRun(String Id,
                      String database,
                      String owner,
                      String jobName,
                      String chainOwner,
                      String chainName,
                      String stepName,
                      String state) {
            mId = Id;
            mDatabase = database;
            mOwner = owner;
            mJobName = jobName;
            mChainOwner = chainOwner;
            mChainName = chainName;
            mStepName = stepName;
            mState = state;
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
        public String getChainOwner() {
            return mChainOwner;
        }
        public String getChainName() {
            return mChainName;
        }
        public String getStepName() {
            return mStepName;
        }
        public String getState() {
            return mState;
        }
    }
}

