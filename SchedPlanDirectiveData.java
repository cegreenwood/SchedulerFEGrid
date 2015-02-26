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


class SchedPlanDirectiveData extends AbstractTableModel {

    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Consumer Group", 180, JLabel.LEFT ),
      new SchedColumnData("Level 1", 60, JLabel.CENTER ),
      new SchedColumnData("Level 2", 60, JLabel.CENTER ),
      new SchedColumnData("Level 3", 60, JLabel.CENTER ),
      new SchedColumnData("Level 4", 60, JLabel.CENTER ),
      new SchedColumnData("Level 5", 60, JLabel.CENTER ),
      new SchedColumnData("Level 6", 60, JLabel.CENTER ),
      new SchedColumnData("Level 7", 60, JLabel.CENTER ),
      new SchedColumnData("Level 8", 60, JLabel.CENTER )
    };

    private Vector<planDirective>        planDirectiveObj;

    private planDirective                mPlanDirective;

    // Class constructor schedLogData.
    public SchedPlanDirectiveData() {
        planDirectiveObj = new Vector<planDirective>(40,10);
    }

    public int getRowCount() {
        return planDirectiveObj.size();
    }

    public int getColumnCount() {
        return 9;
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
        mPlanDirective = getPlanDirective(nRow);

        switch ( nCol ) {
          case 0:
              return mPlanDirective.getConsumerGroup();
          case 1:
              return mPlanDirective.getLevel1();
          case 2:
              return mPlanDirective.getLevel2();
          case 3:
              return mPlanDirective.getLevel3();
          case 4:
              return mPlanDirective.getLevel4();
          case 5:
              return mPlanDirective.getLevel5();
          case 6:
              return mPlanDirective.getLevel6();
          case 7:
              return mPlanDirective.getLevel7();
          case 8:
              return mPlanDirective.getLevel8();
        }
        return "";
    }

    public int getIdAt(int nRow) {
        mPlanDirective = getPlanDirective(nRow);
        return mPlanDirective.getId();
    }

    public boolean addPlanDirective(int mId,
                             String mConsumerGroup,
                             String mLevel1,
                             String mLevel2,
                             String mLevel3,
                             String mLevel4,
                             String mLevel5,
                             String mLevel6,
                             String mLevel7,
                             String mLevel8) {

        mPlanDirective = new planDirective(mId,
                                         mConsumerGroup,
                                         mLevel1,
                                         mLevel2,
                                         mLevel3,
                                         mLevel4,
                                         mLevel5,
                                         mLevel6,
                                         mLevel7,
                                         mLevel8);

        return planDirectiveObj.add(mPlanDirective);

    }

    public planDirective getPlanDirective(int Id) {
        return planDirectiveObj.get(Id);
    }

    public int sizePlanDirective() {
        return planDirectiveObj.size();
    }

    public void removePlanDirective(String mGroup) {
        // System.out.println( " Point 1." + mGroup);
        for (int i1 = 0; i1 < planDirectiveObj.size(); i1++) {
            mPlanDirective = planDirectiveObj.get(i1);
            if (mPlanDirective.getConsumerGroup().equals(mGroup)) {
                planDirectiveObj.removeElementAt(i1);
                break;
            }
        }
    }

    public void clearPlanDirective() {
        planDirectiveObj.clear();
    }

    class planDirective {

        private int      mId;
        private String   mConsumerGroup;
        private String   mLevel1;
        private String   mLevel2;
        private String   mLevel3;
        private String   mLevel4;
        private String   mLevel5;
        private String   mLevel6;
        private String   mLevel7;
        private String   mLevel8;

        public planDirective(int   id,
                            String consumerGroup,
                            String level1,
                            String level2,
                            String level3,
                            String level4,
                            String level5,
                            String level6,
                            String level7,
                            String level8) {
            mId = id;
            mConsumerGroup = consumerGroup;
            mLevel1 = level1;
            mLevel2 = level2;
            mLevel3 = level3;
            mLevel4 = level4;
            mLevel5 = level5;
            mLevel6 = level6;
            mLevel7 = level7;
            mLevel8 = level8;
        }
        public int getId() {
            return mId;
        }
        public String getConsumerGroup() {
            return mConsumerGroup;
        }
        public String getLevel1() {
            return mLevel1;
        }
        public String getLevel2() {
            return mLevel2;
        }
        public String getLevel3() {
            return mLevel3;
        }
        public String getLevel4() {
            return mLevel4;
        }
        public String getLevel5() {
            return mLevel5;
        }
        public String getLevel6() {
            return mLevel6;
        }
        public String getLevel7() {
            return mLevel7;
        }
        public String getLevel8() {
            return mLevel8;
        }
    }
}

