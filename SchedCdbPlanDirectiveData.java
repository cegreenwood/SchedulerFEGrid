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

class SchedCdbPlanDirectiveData extends AbstractTableModel {

    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Pluggable DB", 180, JLabel.LEFT ),
      new SchedColumnData("Shares", 60, JLabel.CENTER ),
      new SchedColumnData("Utilization Limit", 60, JLabel.CENTER ),
      new SchedColumnData("Parallel Server Limit", 60, JLabel.CENTER )
    };

    private Vector<cdbPlanDirective>     cdbPlanDirectiveObj;

    private cdbPlanDirective             mCdbPlanDirective;

    // Class constructor schedLogData.
    public SchedCdbPlanDirectiveData() {
        cdbPlanDirectiveObj = new Vector<cdbPlanDirective>(10,5);
    }

    public int getRowCount() {
        return cdbPlanDirectiveObj.size();
    }

    public int getColumnCount() {
        return 4;
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
        mCdbPlanDirective = getCdbPlanDirective(nRow);

        switch ( nCol ) {
          case 0:
              return mCdbPlanDirective.getPluggableDatabase();
          case 1:
              return mCdbPlanDirective.getShares();
          case 2:
              return mCdbPlanDirective.getUtilizationLimit();
          case 3:
              return mCdbPlanDirective.getParallelServerLimit();
        }
        return "";
    }

    public int getIdAt(int nRow) {
        mCdbPlanDirective = getCdbPlanDirective(nRow);
        return mCdbPlanDirective.getId();
    }

    public boolean addCdbPlanDirective(int mId,
                             String mPluggableDb,
                             String mShares,
                             String mUtilizationLimit,
                             String mParallelServerLimit) {

        mCdbPlanDirective = new cdbPlanDirective(mId,
                                         mPluggableDb,
                                         mShares,
                                         mUtilizationLimit,
                                         mParallelServerLimit);

        return cdbPlanDirectiveObj.add(mCdbPlanDirective);

    }

    public cdbPlanDirective getCdbPlanDirective(int Id) {
        return cdbPlanDirectiveObj.get(Id);
    }

    public int sizeCdbPlanDirective() {
        return cdbPlanDirectiveObj.size();
    }

    public void removeCdbPlanDirective(String mPluggableDb) {
        // System.out.println( " Point 1." + mGroup);
        for (int i1 = 0; i1 < cdbPlanDirectiveObj.size(); i1++) {
            mCdbPlanDirective = cdbPlanDirectiveObj.get(i1);
            if (mCdbPlanDirective.getPluggableDatabase().equals(mPluggableDb)) {
                cdbPlanDirectiveObj.removeElementAt(i1);
                break;
            }
        }
    }

    public void clearCdbPlanDirective() {
        cdbPlanDirectiveObj.clear();
    }

    class cdbPlanDirective {

        private int      mId;
        private String   mPluggableDb;
        private String   mShares;
        private String   mUtilizationLimit;
        private String   mParallelServerLimit;

        public cdbPlanDirective(int    id,
                                String pluggableDb,
                                String shares,
                                String utilizationLimit,
                                String parallelServerLimit) {
            mId = id;
            mPluggableDb = pluggableDb;
            mShares = shares;
            mUtilizationLimit = utilizationLimit;
            mParallelServerLimit = parallelServerLimit;
        }
        public int getId() {
            return mId;
        }
        public String getPluggableDatabase() {
            return mPluggableDb;
        }
        public String getShares() {
            return mShares;
        }
        public String getUtilizationLimit() {
            return mUtilizationLimit;
        }
        public String getParallelServerLimit() {
            return mParallelServerLimit;
        }
    }
}



