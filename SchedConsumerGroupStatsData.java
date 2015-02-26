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

class SchedConsumerGroupStatsData extends AbstractTableModel {

    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Id", 60, JLabel.LEFT ),
      new SchedColumnData("Consumer Group", 180, JLabel.LEFT ),
      new SchedColumnData("Active Sessions", 60, JLabel.CENTER ),
      new SchedColumnData("Queue Length", 60, JLabel.CENTER ),
      new SchedColumnData("Consumed CPU Time", 60, JLabel.CENTER ),
      new SchedColumnData("CPU Waits", 60, JLabel.CENTER ),
      new SchedColumnData("CPU Wait Time", 60, JLabel.CENTER )
    };

    private Vector<consumerGroup>        consumerGroupObj;

    private consumerGroup                mConsumerGroup;

    // Class constructor SchedConsumerGroupStatsData.
    public SchedConsumerGroupStatsData() {
        consumerGroupObj = new Vector<consumerGroup>(20,10);
    }

    public int getRowCount() {
        return consumerGroupObj.size();
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
        mConsumerGroup = getConsumerGroup(nRow);

        switch ( nCol ) {
          case 0:
              return mConsumerGroup.getId();
          case 1:
              return mConsumerGroup.getName();
          case 2:
              return mConsumerGroup.getActiveSessions();
          case 3:
              return mConsumerGroup.getQueueLength();
          case 4:
              return mConsumerGroup.getConsumedCpuTime();
          case 5:
              return mConsumerGroup.getCpuWaits();
          case 6:
              return mConsumerGroup.getCpuWaitTime();
        }
        return "";
    }

    public boolean addConsumerGroup(
                              String mId,
                              String mName,
                              String mActiveSessions,
                              String mQueueLength,
                              String mConsumedCpuTime,
                              String mCpuWaits,
                              String mCpuWaitTime) {

        mConsumerGroup = new consumerGroup(
                             mId,
                             mName,
                             mActiveSessions,
                             mQueueLength,
                             mConsumedCpuTime,
                             mCpuWaits,
                             mCpuWaitTime);

        return consumerGroupObj.add(mConsumerGroup);
    }

    public consumerGroup getConsumerGroup(int Id) {
        return consumerGroupObj.get(Id);
    }

    public int sizeConsumerGroup() {
        return consumerGroupObj.size();
    }

    public void clearConsumerGroups() {
        consumerGroupObj.clear();
    }

    class consumerGroup {
        private String   mId;
        private String   mName;
        private String   mActiveSessions;
        private String   mQueueLength;
        private String   mConsumedCpuTime;
        private String   mCpuWaits;
        private String   mCpuWaitTime;

        public consumerGroup(
                       String id,
                       String name,
                       String activeSessions,
                       String queueLength,
                       String consumedCpuTime,
                       String cpuWaits,
                       String cpuWaitTime) {
            mId = id;
            mName = name;
            mActiveSessions = activeSessions;
            mQueueLength = queueLength;
            mConsumedCpuTime = consumedCpuTime;
            mCpuWaits = cpuWaits;
            mCpuWaitTime = cpuWaitTime;
        }
        public String getId() {
            return mId;
        }
        public String getName() {
            return mName;
        }
        public String getActiveSessions() {
            return mActiveSessions;
        }
        public String getQueueLength() {
            return mQueueLength;
        }
        public String getConsumedCpuTime() {
            return mConsumedCpuTime;
        }
        public String getCpuWaits() {
            return mCpuWaits;
        }
        public String getCpuWaitTime() {
            return mCpuWaitTime;
        }
    }
}

