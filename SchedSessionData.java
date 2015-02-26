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

class SchedSessionData extends AbstractTableModel {

    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Sid",50, JLabel.CENTER ),
      new SchedColumnData("Serial", 50, JLabel.LEFT ),
      new SchedColumnData("User", 100, JLabel.LEFT ),
      new SchedColumnData("Consumer Group", 180, JLabel.LEFT ),
      new SchedColumnData("State", 80, JLabel.LEFT ),
      new SchedColumnData("CPU Time", 60, JLabel.LEFT ),
      new SchedColumnData("CPU Wait Time", 60, JLabel.LEFT ),
      new SchedColumnData("Queued Time", 60, JLabel.LEFT )
    };

    private Vector<session>        sessionObj;

    private session                mSession;

    // Class constructor schedSessionData.
    public SchedSessionData() {
        sessionObj = new Vector<session>(50,10);
    }

    public int getRowCount() {
        return sessionObj.size();
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
        mSession = getSession(nRow);

        switch ( nCol ) {
          case 0:
              return mSession.getSid();
          case 1:
              return mSession.getSerial();
          case 2:
              return mSession.getUsername();
          case 3:
              return mSession.getConsumerGroup();
          case 4:
              return mSession.getState();
          case 5:
              return mSession.getCpuTime();
          case 6:
              return mSession.getWaitTime();
          case 7:
              return mSession.getQueuedTime();
        }
        return "";
    }

    public boolean addSession(String mSid,
                              String mSerial,
                              String mUsername,
                              String mConsumerGroup,
                              String mState,
                              String mCpuTime,
                              String mWaitTime,
                              String mQueuedTime) {

        mSession = new session(mSid,
                             mSerial,
                             mUsername,
                             mConsumerGroup,
                             mState,
                             mCpuTime,
                             mWaitTime,
                             mQueuedTime);

        return sessionObj.add(mSession);
    }

    public session getSession(int Id) {
        return sessionObj.get(Id);
    }

    public int sizeSession() {
        return sessionObj.size();
    }

    public void clearSessions() {
        sessionObj.clear();
    }

    class session {

        private String   mSid;
        private String   mSerial;
        private String   mUsername;
        private String   mConsumerGroup;
        private String   mState;
        private String   mCpuTime;
        private String   mWaitTime;
        private String   mQueuedTime;

        public session(String sid,
                       String serial,
                       String username,
                       String consumerGroup,
                       String state,
                       String cpuTime,
                       String waitTime,
                       String queuedTime) {
            mSid = sid;
            mSerial = serial;
            mUsername = username;
            mConsumerGroup = consumerGroup;
            mState = state;
            mCpuTime = cpuTime;
            mWaitTime = waitTime;
            mQueuedTime = queuedTime;
        }
        public String getSid() {
            return mSid;
        }
        public String getSerial() {
            return mSerial;
        }
        public String getUsername() {
            return mUsername;
        }
        public String getConsumerGroup() {
            return mConsumerGroup;
        }
        public String getState() {
            return mState;
        }
        public String getCpuTime() {
            return mCpuTime;
        }
        public String getWaitTime() {
            return mWaitTime;
        }
        public String getQueuedTime() {
            return mQueuedTime;
        }
    }
}

