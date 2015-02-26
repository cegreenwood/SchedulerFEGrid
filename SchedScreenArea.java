/**
 * Written by Colin Greenwood
 * Date June 2008
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

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.text.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.event.*;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

class SchedScreenArea {


    // private SchedDataArea                      dataArea;
    private Scheduler                          parentFrame;
    private NumberFormat                       amount2Format;
    private ClassLoader                        cl;

    public SchedScreenArea() {
        screenObj = new Vector<PaneObject>(10,10);
        indexObj  = new Vector<PaneIndex>(10,10);
        tabObj = new Vector<TabObject>(10,10);
        setUpFormats();
    }

    public void clearVectors() {
        screenObj.clear();
        indexObj.clear();
    }

    public void processScreenData(int    screenId,
                                  int    screenNo,
                                  String versionStr,
                                  int    fgrndColour,
                                  int    bgrndColour,
                                  int    tabBgrndColour,
                                  int    tabId) {

        boolean mFound = false;
        for (int i1 = 0; i1 < indexObj.size(); i1++) {
            PaneIndex lPaneIndex = indexObj.get(i1);
            if (lPaneIndex.getScreenId() == screenId) {
                mFound = true;
                break;
            }
        }

        PaneIndex mPaneIndex = new PaneIndex(screenId, screenNo, versionStr);
        indexObj.add(mPaneIndex);

        if (! mFound) {
            PaneObject mPane = new PaneObject(screenId, screenNo);
            screenObj.add(mPane);

            mPane.setFgrndColour(fgrndColour);
            mPane.setBgrndColour(bgrndColour);
            mPane.setTabBgrndColour(tabBgrndColour);
            mPane.setTabId(tabId);
        }
    }

    public int getScreenId(int screenNo,
                           String version) {
        for (int i1 = 0; i1 < indexObj.size(); i1++) {
            PaneIndex lPaneIndex = indexObj.get(i1);
            if ((lPaneIndex.getScreenNo() == screenNo) &&
                (lPaneIndex.getVersion().equals(version)))
            {
                return lPaneIndex.getScreenId();
            }
        }
        return 0;
    }
    // public void setDataArea(SchedDataArea   mDataArea) {
    //     dataArea = mDataArea;
    // }
    public void setScheduler(Scheduler   mScheduler) {
        parentFrame = mScheduler;
    }

    class PaneIndex {
        private int                 m_ScreenId;
        private int                 m_ScreenNo;
        private String              m_Version;

        PaneIndex(int mScreenId,
                  int mScreenNo,
                  String mVersion) {

            m_ScreenId = mScreenId;
            m_ScreenNo = mScreenNo;
            m_Version = mVersion;
        }

        public int getScreenId() {
             return m_ScreenId;
        }
        public int getScreenNo() {
            return m_ScreenNo;
        }
        public String getVersion() {
            return m_Version;
        }
    }

    public boolean addScreenObj(PaneObject m_Pane) {
        return screenObj.add(m_Pane);
    }
    public PaneObject getScreenObj(int m_Row) {
        return screenObj.get(m_Row);
    }
    public PaneObject getScreen(int m_No) {
        for (int i1 = 0; i1 < screenObj.size(); i1++) {
            PaneObject mPane = screenObj.get(i1);
            if (mPane.getScreenId() == m_No) {
                return mPane;
            }
        }
        return null;
    }
    public boolean isScreenNo(int m_No) {
        for (int i1 = 0; i1 < screenObj.size(); i1++) {
            PaneObject mPane = screenObj.get(i1);
            if (mPane.getScreenNo() == m_No) {
                return true;
            }
        }
        return false;
    }
    public int sizeScreenObj() {
        return screenObj.size();
    }

    public void setEditPane(SchedGlobalData mArea,
                            SchedScreenArea.PaneObject mPane,
                            boolean mEdit) {
        Color mColor;
        int t1 = mPane.sizeTextObj();
        int t2 = mPane.sizeTextAreaObj();

        for (int i3 = 0; i3 < t1; i3++) {
            SchedScreenArea.PaneObject.TextItem m_T1 = mPane.getTextObj(i3);

            if (mEdit) {
                m_T1.setBackground(Color.white);
                m_T1.setEditable(true);
                m_T1.setFocusable(true);
            }
            else {
                mColor = mArea.getScreenColor(m_T1.get_BackColor());
                if (mColor != null) {
                    m_T1.setBackground(mColor);
                }
                m_T1.setEditable(false);
                m_T1.setFocusable(false);
            }
        }
        for (int i4 = 0; i4 < t2; i4++) {
            SchedScreenArea.PaneObject.TextAreaItem m_T2 = mPane.getTextAreaObj(i4);

            if (mEdit) {
                m_T2.setBackground(Color.white);
                m_T2.setEditable(true);
                m_T2.setFocusable(true);
            }
            else {
                mColor = mArea.getScreenColor(m_T2.get_BackColor());
                if (mColor != null) {
                    m_T2.setBackground(mColor);
                }
                m_T2.setEditable(false);
                m_T2.setFocusable(false);
            }
        }
    }

    private void setUpFormats() {
        amount2Format = NumberFormat.getNumberInstance();
        amount2Format.setMinimumFractionDigits(2);
    }

    public void PageSetup(SchedGlobalData mArea,
                          SchedScreenArea.PaneObject mPane) {

        int s1 = mPane.sizeLabelObj();
        int t1 = mPane.sizeTextObj();
        int t2 = mPane.sizeTextAreaObj();
        TabObject mTabObject = null;

        int mWidth = 0;
        int mHeight = 0;
        int mValue = 0;

        int m_PageNo;
        Color mColor;
        Color bColor = mArea.getScreenColor(mPane.getFgrndColour());

        cl = this.getClass().getClassLoader();
        Icon LookIcon1 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook1.jpg"));
        Icon LookIcon2 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook2.jpg"));

        if (mPane.getTabBgrndColour() > 0) {
            mColor = mArea.getScreenColor(mPane.getTabBgrndColour());
            if (mColor != null)   mPane.getTabbedPane().setBackground(mColor);
        }
        // System.out.println(" Page Setup  - " + r1 + "-" + s1 + "-" + t1 + "-");
        int lError = 0;
        for (int i = 0; i < tabObj.size(); i++) {
            mTabObject = tabObj.get(i);
            // System.out.println( " S2-" + m_TextAreaItem.get_ItemId());
            if (mTabObject.getTabId() == mPane.getTabId()) {
                lError = 1;
                break;
            }
        }

        for (int i1 = 0; i1 < mTabObject.getTabSize(); i1++) {

            SchedScreenArea.TabObject.TabPage m_TabPage = mTabObject.getTabPage(i1);
            m_PageNo = m_TabPage.getTabPageNo();

            m_JPanel = new JPanel();

            if (mPane.getBgrndColour() > 0) {
                mColor = mArea.getScreenColor(mPane.getBgrndColour());
                m_JPanel.setBackground(mColor);
            }
            m_JPanel.setLayout(new SpringLayout());

            if ((mPane.getScreenNo() == SchedConsts.PLAN_SCREEN_NO) && (m_PageNo == 2)) {

                JScrollPane mScrollPane = mPane.setupPlanDirectives(mArea);
                m_JPanel.add(mScrollPane,
                        new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(15),
                                                 Spring.constant(660),
                                                 Spring.constant(94)));
            }

            if ((mPane.getScreenNo() == SchedConsts.CDB_PLAN_SCREEN_NO) && (m_PageNo == 2)) {

                JScrollPane mScrollPane = mPane.setupCdbPlanDirectives(mArea);
                m_JPanel.add(mScrollPane,
                        new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(15),
                                                 Spring.constant(660),
                                                 Spring.constant(94)));
            }

            if ((mPane.getScreenNo() == SchedConsts.CONSUMER_GROUP_SCREEN_NO) && (m_PageNo == 2)) {
                JScrollPane mScrollPane = mPane.setupConsumerGroupMappings(mArea);
                m_JPanel.add(mScrollPane,
                        new SpringLayout.Constraints(Spring.constant(100),
                                                 Spring.constant(30),
                                                 Spring.constant(400),
                                                 Spring.constant(320)));
            }
            if ((mPane.getScreenNo() == SchedConsts.CONSUMER_GROUP_SCREEN_NO) && (m_PageNo == 3)) {
                JScrollPane mScrollPane = mPane.setupConsumerPrivs(mArea);
                m_JPanel.add(mScrollPane,
                        new SpringLayout.Constraints(Spring.constant(100),
                                                 Spring.constant(30),
                                                 Spring.constant(400),
                                                 Spring.constant(320)));
            }
            if (mPane.getScreenNo() == SchedConsts.MAPPING_PRIORITY_SCREEN_NO) {
                JScrollPane mScrollPane = mPane.setupMappingPriorityMappings(mArea);
                m_JPanel.add(mScrollPane,
                        new SpringLayout.Constraints(Spring.constant(140),
                                                 Spring.constant(80),
                                                 Spring.constant(420),
                                                 Spring.constant(202)));
            }

            // Loops through Label objects.
            for (int i2 = 0; i2 < s1; i2++) {
                // System.out.println(" Label  - " + i2);

                SchedScreenArea.PaneObject.LabelItem m_P1 = mPane.getLabelObj(i2);
                if ((m_P1.get_PageNo() == m_PageNo) &&
                    (m_P1.get_Display().equals("Y"))) {

                    // System.out.println(" D " + i2 + "--" + m_P1.get_Description());

                    int m_Style = m_P1.get_FontStyle();
                    if (m_Style == 3)
                        m_P1.setFont(new Font(m_P1.get_Font(),
                                              Font.ITALIC,
                                              m_P1.get_FontSize()));
                    else {
                        if (m_Style == 2)
                            m_P1.setFont(new Font(m_P1.get_Font(),
                                                  Font.BOLD,
                                                  m_P1.get_FontSize()));
                        else 
                            m_P1.setFont(new Font(m_P1.get_Font(),
                                                  Font.PLAIN,
                                                  m_P1.get_FontSize()));
                    }
                    if (m_P1.get_ForeColor() > 0) {
                        mColor = mArea.getScreenColor(m_P1.get_ForeColor());
                        if (mColor != null) {
                            m_P1.setForeground(mColor);
                        }
                    }
                    if (m_P1.get_BackColor() > 0) {
                        mColor = mArea.getScreenColor(m_P1.get_BackColor());
                        if (mColor != null) {
                            m_P1.setOpaque(true);
                            m_P1.setBackground(mColor);
                        }
                    }
                    m_JPanel.add(m_P1,
                        new SpringLayout.Constraints(Spring.constant(m_P1.get_XPoint()),
                                                 Spring.constant(m_P1.get_YPoint()),
                                                 Spring.constant(m_P1.get_Width()),
                                                 Spring.constant(m_P1.get_Height())));

                    mValue = m_P1.get_XPoint() + m_P1.get_Width();
                    if (mValue > mWidth)
                        mWidth = mValue;

                    mValue = m_P1.get_YPoint() + m_P1.get_Height();
                    if (mValue > mHeight)
                        mHeight = mValue;

                }
            }

            // Loops through Text objects.
            for (int i3 = 0; i3 < t1; i3++) {
                // System.out.println(" Text  - " + i3);

                SchedScreenArea.PaneObject.TextItem m_T1 = mPane.getTextObj(i3);
                if ((m_T1.get_PageNo() == m_PageNo) &&
                    (m_T1.get_Display().equals("Y"))) {

                    // System.out.println(" E " + i3 + "--" + m_T1.get_Description());

                    int m_Style = m_T1.get_FontStyle();
                    if (m_Style == 3)
                        m_T1.setFont(new Font(m_T1.get_Font(),
                                              Font.ITALIC,
                                              m_T1.get_FontSize()));
                    else {
                        if (m_Style == 2)
                            m_T1.setFont(new Font(m_T1.get_Font(),
                                                  Font.BOLD,
                                                  m_T1.get_FontSize()));
                        else 
                            m_T1.setFont(new Font(m_T1.get_Font(),
                                                  Font.PLAIN,
                                                  m_T1.get_FontSize()));
                    }
                    if (m_T1.get_ForeColor() > 0) {
                        mColor = mArea.getScreenColor(m_T1.get_ForeColor());
                        if (mColor != null) {
                            m_T1.setForeground(mColor);
                        }
                    }

                    if (m_T1.get_BackColor() > 0) {
                        mColor = mArea.getScreenColor(m_T1.get_BackColor());
                        if (mColor != null) {
                            m_T1.setOpaque(true);
                            m_T1.setBackground(mColor);
                        }

                    }


                    if (m_T1.get_FormatType() == 1) {
                        m_T1.setHorizontalAlignment(JTextField.RIGHT);
                    }

                    if (m_T1.get_Button().equals("Y")) {
                       JButton m_B1 = new JButton();
                       if (m_T1.get_RowType() == 5) m_B1.setIcon(LookIcon2);
                       else                         m_B1.setIcon(LookIcon1);

                       if (m_T1.get_BackColor() > 0) {
                           mColor = mArea.getScreenColor(m_T1.get_BackColor());
                           if (mColor != null) {
                               m_B1.setOpaque(true);
                               m_B1.setBackground(mColor);
                           }

                       }

                       m_JPanel.add(m_B1,
                           new SpringLayout.Constraints(
                               Spring.constant(m_T1.get_XPoint() + m_T1.get_Width() + 5),
                               Spring.constant(m_T1.get_YPoint()),
                               Spring.constant(30),
                               Spring.constant(m_T1.get_Height())));
                           m_B1.addActionListener( new SchedControl1() );
                           m_B1.setActionCommand( String.valueOf(i3) );
                    }

                    m_T1.setBorder(BorderFactory.createLineBorder(bColor));

                    m_T1.setEditable(false);
                    m_T1.setFocusable(false);

                    m_JPanel.add(m_T1,
                        new SpringLayout.Constraints(
                            Spring.constant(m_T1.get_XPoint()),
                            Spring.constant(m_T1.get_YPoint()),
                            Spring.constant(m_T1.get_Width()),
                            Spring.constant(m_T1.get_Height())));

                    mValue = m_T1.get_XPoint() + m_T1.get_Width();
                    if (mValue > mWidth)
                        mWidth = mValue;

                    mValue = m_T1.get_YPoint() + m_T1.get_Height();
                    if (mValue > mHeight)
                        mHeight = mValue;

                }
            }

            for (int i4 = 0; i4 < t2; i4++) {
                // System.out.println(" TextArea  - " + i4);

                SchedScreenArea.PaneObject.TextAreaItem m_T2 = mPane.getTextAreaObj(i4);
                if ((m_T2.get_PageNo() == m_PageNo)  &&
                    (m_T2.get_Display().equals("Y"))) {
                    // System.out.println(" E " + i4 + "--" + m_T2.get_Description());

                    int m_Style = m_T2.get_FontStyle();
                    if (m_Style == 3)
                        m_T2.setFont(new Font(m_T2.get_Font(),
                                              Font.ITALIC,
                                              m_T2.get_FontSize()));
                    else {
                        if (m_Style == 2)
                            m_T2.setFont(new Font(m_T2.get_Font(),
                                                  Font.BOLD,
                                                  m_T2.get_FontSize()));
                        else 
                            m_T2.setFont(new Font(m_T2.get_Font(),
                                                  Font.PLAIN,
                                                  m_T2.get_FontSize()));
                    }
                    if (m_T2.get_ForeColor() > 0) {
                        mColor = mArea.getScreenColor(m_T2.get_ForeColor());
                        if (mColor != null) {
                            m_T2.setForeground(mColor);
                            // m_T2.setBorder(BorderFactory.createLineBorder(mColor));
                        }
                    }


                    if (m_T2.get_BackColor() > 0) {
                        mColor = mArea.getScreenColor(m_T2.get_BackColor());
                        if (mColor != null) {
                            m_T2.setOpaque(true);
                            m_T2.setBackground(mColor);
                        }

                    }
                    m_T2.setBorder(BorderFactory.createLineBorder(bColor));

                    m_T2.setEditable(false);
                    m_T2.setFocusable(false);

                    m_JPanel.add(m_T2,
                        new SpringLayout.Constraints(Spring.constant(m_T2.get_XPoint()),
                                                     Spring.constant(m_T2.get_YPoint()),
                                                     Spring.constant(m_T2.get_Width()),
                                                    Spring.constant(m_T2.get_Height())));

                    mValue = m_T2.get_XPoint() + m_T2.get_Width();
                    if (mValue > mWidth)
                        mWidth = mValue;

                    mValue = m_T2.get_YPoint() + m_T2.get_Height();
                    if (mValue > mHeight)
                        mHeight = mValue;

                }
            }

            mPane.addTab(m_TabPage.getDesc(), m_JPanel, i1);

        }
        mWidth = mWidth + 20;
        mHeight = mHeight + 20;
        // mPane.setMinimumSize(mWidth, mHeight);
        Dimension mDimension = new Dimension(mWidth, mHeight);
        mPane.setPreferredSize(mDimension);
    }

    // public void resetPriority() {
    //     for (int i = 0; i < screenObj.size(); i++) {
    //         m_PaneObject = screenObj.get(i);
    //         m_PaneObject.setPriority(m_PaneObject.getPriority() + 1);
    //     }
    // }

    // public void remove() {
    //     int i2 = 0;
    //     for (int i = 0; i < screenObj.size(); i++) {
    //         m_PaneObject = screenObj.get(i);
    //         if (m_PaneObject.getPriority() > i2) {
    //             i2 = i;
    //         }
    //     }
    //     screenObj.remove(i2);
    // }


    class PaneObject implements ListSelectionListener, ActionListener {

        private JTabbedPane         m_TabbedPane;
        private int                 m_ScreenNo;
        private int                 m_ScreenId;
        private int                 Priority;
        private int                 bgrndColour;
        private int                 fgrndColour;
        private int                 tabBgrndColour;
        private int                 tabId;

        private SchedDataArea                        m_DataArea;
        private SchedDataArea.PlanDirectiveItem      m_PlanDirectiveItem;
        private SchedDataArea.CdbPlanDirectiveItem   m_CdbPlanDirectiveItem;
        private SchedDataArea.GroupMappingsItem      m_GroupMappingsItem;

        private SchedPlanDirectiveData               mDataPlanDirectiveStd;
        private SchedCdbPlanDirectiveData            mDataCdbPlanDirectiveStd;
        private SchedConsumerGroupMappingData        mDataConsumerGroupStd;
        private SchedConsumerGroupPrivData           mDataConsumerPrivStd;
        private SchedMappingPriorityData             mDataMappingPriorityStd;
        private JXTable                              mPlanDirectiveTable;
        private JXTable                              mCdbPlanDirectiveTable;
        private JXTable                              mGroupMappingTable;
        private JXTable                              mGroupPrivTable;
        private JXTable                              mMappingPriorityTable;

        private boolean                              mPlanDirectPop = false;
        private boolean                              mCdbPlanDirectPop = false;

        public PaneObject(int screenId, int screenNo) {
            labelObj = new Vector<LabelItem>(50,25);
            textObj = new Vector<TextItem>(50,25);
            textAreaObj = new Vector<TextAreaItem>(10,10);
            columnObj  = new Vector<ColumnItem>(10,10);

            m_TabbedPane = new JTabbedPane();
            m_TabbedPane.setOpaque(true);
            m_ScreenId = screenId;
            m_ScreenNo = screenNo;
            Priority = 1;

            // Add listener only to Consumer Group Screen.
            if (m_ScreenNo == SchedConsts.CONSUMER_GROUP_SCREEN_NO)
                m_TabbedPane.addChangeListener(new TabChangeListener());

        }
        public void setTabId(int mTabId) {
            tabId = mTabId;
        }
        public void setDataArea(SchedDataArea mDataArea) {
            // System.out.println(" setDataArea.");
            m_DataArea = mDataArea;
        }
        public SchedDataArea getDataArea() {
            return m_DataArea;
        }

        public void setPreferredSize(Dimension mDimension) {
            m_TabbedPane.setPreferredSize(mDimension);
        }
        public void setBgrndColour(int m_BgrndColour) {
            bgrndColour = m_BgrndColour;
        }
        public void setFgrndColour(int m_FgrndColour) {
            fgrndColour = m_FgrndColour;
        }
        public void setTabBgrndColour(int m_TabBgrndColour) {
            tabBgrndColour = m_TabBgrndColour;
        }
        public int getTabId() {
            return tabId;
        }
        public int getBgrndColour() {
            return bgrndColour;
        }
        public int getFgrndColour() {
            return fgrndColour;
        }
        public int getTabBgrndColour() {
            return tabBgrndColour;
        }

        public void addTab(String m_Desc, JPanel m_JPanel, int mTabNo) {
            m_TabbedPane.addTab(m_Desc, m_JPanel);
            int mAsciiValue = m_Desc.charAt(0);
            m_TabbedPane.setMnemonicAt(mTabNo, mAsciiValue);
        }

        public JTabbedPane getTabbedPane() {
            return m_TabbedPane;
        }
        public void setMinimumSize(int mWidth, int mHeight) {
            m_TabbedPane.setMinimumSize(new Dimension(mWidth, mHeight));
        }
        public int getScreenNo() {
            return m_ScreenNo;
        }
        public int getScreenId() {
            return m_ScreenId;
        }

        public JScrollPane setupPlanDirectives(SchedGlobalData mArea) {

            // Set up the table for the Plan Directive data.
            mDataPlanDirectiveStd = new SchedPlanDirectiveData();

            mPlanDirectiveTable = new JXTable();
            mPlanDirectiveTable.setAutoCreateColumnsFromModel(false);
            mPlanDirectiveTable.setModel(mDataPlanDirectiveStd);

            ListSelectionModel selectionModel = mPlanDirectiveTable.getSelectionModel();
            selectionModel.addListSelectionListener( this );

            mPlanDirectiveTable.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mArea.getScreenColor(36),
                               mArea.getScreenColor(37)));

            DefaultTableCellRenderer renderer1 = new DefaultTableCellRenderer();
            renderer1.setBackground(mArea.getScreenColor(31));

            for (int j1 = 0; j1 < mDataPlanDirectiveStd.getColumnCount(); j1++) {
                renderer1.setHorizontalAlignment(
                            SchedPlanDirectiveData.m_columns[j1].m_alignment);
                TableColumn mColumn = new TableColumn(j1,
                            mDataPlanDirectiveStd.m_columns[j1].m_width,
                            renderer1,
                            null);
                mPlanDirectiveTable.addColumn(mColumn);
            }

            JScrollPane mPanel1 = new JScrollPane(mPlanDirectiveTable);
            mPanel1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            mPanel1.setBorder(new LineBorder(mArea.getScreenColor(38), 1, false));
            mPanel1.getViewport().setBackground(mArea.getScreenColor(32));

            return mPanel1;
        }
        public void clearPlanDirective() {
            mDataPlanDirectiveStd.clearPlanDirective();
            mPlanDirectiveTable.tableChanged(new TableModelEvent(mDataPlanDirectiveStd));
            mPlanDirectiveTable.repaint();
        }
        public void updatePlanDirective(SchedDataArea.PlanDirectiveItem m_PlanDirectiveItem) {
            // SchedDataArea mDataArea = mPane.getDataArea();

            updateTextObj(m_PlanDirectiveItem.getGroup(), 13);
            updateTextObj(m_PlanDirectiveItem.getType(), 14);
            updateTextObj(m_PlanDirectiveItem.getStatus(), 15);
            updateTextObj(m_PlanDirectiveItem.getSwitchGroup(), 16);
            updateTextObj(m_PlanDirectiveItem.getMandatory(), 17);

            updateTextObj(Integer.toString(m_PlanDirectiveItem.getSwitchTime()), 18);
            updateTextObj(m_PlanDirectiveItem.getSwitchEstimate(), 19);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getActiveSessPool()), 20);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getParallelDegreeLimitP1()), 21);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMaxEstExecTime()), 22);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMaxIdleTime()), 23);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMaxIdleBlockerTime()), 24);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getUndoPool()), 25);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getQueueingP1()), 26);
            updateTextAreaObj(m_PlanDirectiveItem.getComments(), 27);

            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMgmt_P1()), 28);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMgmt_P2()), 29);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMgmt_P3()), 30);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMgmt_P4()), 31);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMgmt_P5()), 32);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMgmt_P6()), 33);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMgmt_P7()), 34);
            updateTextObj(Integer.toString(m_PlanDirectiveItem.getMgmt_P8()), 35);

            if (m_DataArea.getVersionNo() > 2) {
                updateTextObj(Integer.toString(m_PlanDirectiveItem.getSwitchIoReqs()), 36);
                updateTextObj(Integer.toString(m_PlanDirectiveItem.getSwitchIoMegabytes()), 37);
                updateTextObj(m_PlanDirectiveItem.getSwitchForCall(), 38);
            }
            if (m_DataArea.getVersionNo() > 3) {
                updateTextObj(Integer.toString(m_PlanDirectiveItem.getMaxUtilLimit()), 39);
            }
        }
        public void populatePlanDirectiveTable(int    mId,
                                               String mGroup,
                                               String mLevel1,
                                               String mLevel2,
                                               String mLevel3,
                                               String mLevel4,
                                               String mLevel5,
                                               String mLevel6,
                                               String mLevel7,
                                               String mLevel8) {

            // System.out.println(" 1  - " + mGroup + "--" + mLevel1 + 
            //                "--" + mLevel2 + "--" + mLevel3 + "--" + mLevel4 + "--" + mLevel5);
            mDataPlanDirectiveStd.addPlanDirective(mId,
                             mGroup,
                             mLevel1,
                             mLevel2,
                             mLevel3,
                             mLevel4,
                             mLevel5,
                             mLevel6,
                             mLevel7,
                             mLevel8);
            mPlanDirectiveTable.tableChanged(new TableModelEvent(mDataPlanDirectiveStd));
            mPlanDirectiveTable.repaint();
        }
        public int getSelectedPlanDirective() {
            return mPlanDirectiveTable.getSelectedRow();
        }

        public void removePlanDirective(String mGroup) {
            mDataPlanDirectiveStd.removePlanDirective(mGroup);

            updateTextObj(null, 13);
            updateTextObj(null, 14);
            updateTextObj(null, 15);
            updateTextObj(null, 16);
            updateTextObj(null, 17);
            updateTextObj(null, 18);
            updateTextObj(null, 19);
            updateTextObj(null, 20);
            updateTextObj(null, 21);
            updateTextObj(null, 22);
            updateTextObj(null, 23);
            updateTextObj(null, 24);
            updateTextObj(null, 25);
            updateTextObj(null, 26);
            updateTextAreaObj(null, 27);
            // updateTextObj(null, 28);
            // updateTextObj(null, 29);
            // updateTextAreaObj(null, 30);
            // updateTextObj(null, 31);
            // updateTextObj(null, 32);
            // updateTextObj(null, 33);
            // updateTextObj(null, 34);
            // updateTextObj(null, 35);
            updateTextObj(null, 36);
            updateTextObj(null, 37);
            updateTextObj(null, 38);
            // updateTextObj(null, 39);

            mPlanDirectiveTable.repaint();
        }
        public int getSelectedPlanDirectiveId() {
            int mRetNo = 0;
            if (mPlanDirectiveTable.getSelectedRow() >= 0) {
                mRetNo = mDataPlanDirectiveStd.getIdAt(mPlanDirectiveTable.getSelectedRow());
            }
            return mRetNo;
        }

        public JScrollPane setupCdbPlanDirectives(SchedGlobalData mArea) {

            // Set up the table for the CDB Plan Directive data.
            mDataCdbPlanDirectiveStd = new SchedCdbPlanDirectiveData();

            mCdbPlanDirectiveTable = new JXTable();
            mCdbPlanDirectiveTable.setAutoCreateColumnsFromModel(false);
            mCdbPlanDirectiveTable.setModel(mDataCdbPlanDirectiveStd);

            ListSelectionModel selectionModel = mCdbPlanDirectiveTable.getSelectionModel();
            selectionModel.addListSelectionListener( this );

            mCdbPlanDirectiveTable.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mArea.getScreenColor(36),
                               mArea.getScreenColor(37)));

            DefaultTableCellRenderer renderer1 = new DefaultTableCellRenderer();
            renderer1.setBackground(mArea.getScreenColor(31));

            for (int j1 = 0; j1 < mDataCdbPlanDirectiveStd.getColumnCount(); j1++) {
                renderer1.setHorizontalAlignment(
                            SchedCdbPlanDirectiveData.m_columns[j1].m_alignment);
                TableColumn mColumn = new TableColumn(j1,
                            mDataCdbPlanDirectiveStd.m_columns[j1].m_width,
                            renderer1,
                            null);
                mCdbPlanDirectiveTable.addColumn(mColumn);
            }

            JScrollPane mPanel1 = new JScrollPane(mCdbPlanDirectiveTable);
            mPanel1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            mPanel1.setBorder(new LineBorder(mArea.getScreenColor(38), 1, false));
            mPanel1.getViewport().setBackground(mArea.getScreenColor(32));

            return mPanel1;
        }
        public void clearCdbPlanDirective() {
            mDataCdbPlanDirectiveStd.clearCdbPlanDirective();
            mCdbPlanDirectiveTable.tableChanged(new TableModelEvent(mDataCdbPlanDirectiveStd));
            mCdbPlanDirectiveTable.repaint();
        }
        public void updateCdbPlanDirective(SchedDataArea.CdbPlanDirectiveItem m_CdbPlanDirectiveItem) {

            updateTextObj(m_CdbPlanDirectiveItem.getPluggableDatabase(), 6);
            updateTextObj(m_CdbPlanDirectiveItem.getStatus(), 7);
            updateTextObj(m_CdbPlanDirectiveItem.getMandatory(), 8);
            updateTextAreaObj(m_CdbPlanDirectiveItem.getComments(), 9);
            updateTextObj(Integer.toString(m_CdbPlanDirectiveItem.getShares()), 10);
            updateTextObj(Integer.toString(m_CdbPlanDirectiveItem.getUtilizationLimit()), 11);
            updateTextObj(Integer.toString(m_CdbPlanDirectiveItem.getParallelServerLimit()), 12);
        }
        public void populateCdbPlanDirectiveTable(int    mId,
                                                  String mPluggableDb,
                                                  String mShares,
                                                  String mUtilizationLimit,
                                                  String mParallelServerLimit) {

            // System.out.println(" 1  - " + mId + "--" + mPluggableDb +  
            //                "--" + mShares + "--" + mUtilizationLimit + "--" + mParallelServerLimit);
            mDataCdbPlanDirectiveStd.addCdbPlanDirective(mId,
                             mPluggableDb,
                             mShares,
                             mUtilizationLimit,
                             mParallelServerLimit);
            mCdbPlanDirectiveTable.tableChanged(new TableModelEvent(mDataCdbPlanDirectiveStd));
            mCdbPlanDirectiveTable.repaint();
        }
        public int getSelectedCdbPlanDirective() {
            return mCdbPlanDirectiveTable.getSelectedRow();
        }
        public void removeCdbPlanDirective(String mPluggableDb) {
            mDataCdbPlanDirectiveStd.removeCdbPlanDirective(mPluggableDb);

            updateTextObj(null, 6);
            updateTextObj(null, 7);
            updateTextObj(null, 8);
            updateTextAreaObj(null, 9);
            updateTextObj(null, 10);
            updateTextObj(null, 11);
            updateTextObj(null, 12);

            mCdbPlanDirectiveTable.repaint();
        }
        public int getSelectedCdbPlanDirectiveId() {
            int mRetNo = 0;
            if (mCdbPlanDirectiveTable.getSelectedRow() >= 0) {
                mRetNo = mDataCdbPlanDirectiveStd.getIdAt(mCdbPlanDirectiveTable.getSelectedRow());
            }
            return mRetNo;
        }

        public JScrollPane setupConsumerGroupMappings(SchedGlobalData mArea) {

            // Set up the table for the Consumer Group data.
            mDataConsumerGroupStd = new SchedConsumerGroupMappingData();

            mGroupMappingTable = new JXTable();
            mGroupMappingTable.setAutoCreateColumnsFromModel(false);
            mGroupMappingTable.setModel(mDataConsumerGroupStd);

            ListSelectionModel selectionModel = mGroupMappingTable.getSelectionModel();
            selectionModel.addListSelectionListener( this );

            mGroupMappingTable.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mArea.getScreenColor(36),
                               mArea.getScreenColor(37)));

            DefaultTableCellRenderer renderer1 = new DefaultTableCellRenderer();
            renderer1.setBackground(mArea.getScreenColor(31));

            for (int j1 = 0; j1 < mDataConsumerGroupStd.getColumnCount(); j1++) {
                renderer1.setHorizontalAlignment(
                            SchedConsumerGroupMappingData.m_columns[j1].m_alignment);
                TableColumn mColumn = new TableColumn(j1,
                            mDataConsumerGroupStd.m_columns[j1].m_width,
                            renderer1,
                            null);
                mGroupMappingTable.addColumn(mColumn);
            }

            JScrollPane mPanel1 = new JScrollPane(mGroupMappingTable);
            mPanel1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            mPanel1.setBorder(new LineBorder(mArea.getScreenColor(38), 1, false));
            mPanel1.getViewport().setBackground(mArea.getScreenColor(32));

            return mPanel1;
        }
        public void clearGroupMapping() {
            mDataConsumerGroupStd.clearGroupMapping();
            mGroupMappingTable.tableChanged(new TableModelEvent(mDataConsumerGroupStd));
            mGroupMappingTable.repaint();
        }

        public void removeGroupMapping(String mAttribute, String mValue) {
            mDataConsumerGroupStd.removeGroupMappingEntry(mAttribute,
                                                      mValue);
            mGroupMappingTable.tableChanged(new TableModelEvent(mDataConsumerGroupStd));

            mGroupMappingTable.repaint();
        }

        public void populateGroupMappingTable(String mAttribute,
                                              String mValue,
                                              String mStatus) {

            // System.out.println(" 1  - " + mGroup + "--" + mLevel1 + 
            //                    "--" + mLevel2 + "--" + mLevel3 + "--" + mLevel4 + "--" + mLevel5);
            mDataConsumerGroupStd.addGroupMapping(
                                 mAttribute,
                                 mValue,
                                 mStatus);
            mGroupMappingTable.tableChanged(new TableModelEvent(mDataConsumerGroupStd));
            mGroupMappingTable.repaint();
        }
        public int getSelectedGroupMapping() {
            return mGroupMappingTable.getSelectedRow();
        }
        public String getSelectedGroupMappingAttribute() {
            return (String)mDataConsumerGroupStd.getValueAt(mGroupMappingTable.getSelectedRow(),0);
        }
        public String getSelectedGroupMappingValue() {
            return (String)mDataConsumerGroupStd.getValueAt(mGroupMappingTable.getSelectedRow(),1);
        }
        public JScrollPane setupConsumerPrivs(SchedGlobalData mArea) {

            // Set up the table for the Consumer Privileges data.
            mDataConsumerPrivStd = new SchedConsumerGroupPrivData();

            mGroupPrivTable = new JXTable();
            mGroupPrivTable.setAutoCreateColumnsFromModel(false);
            mGroupPrivTable.setModel(mDataConsumerPrivStd);

            ListSelectionModel selectionModel = mGroupPrivTable.getSelectionModel();
            selectionModel.addListSelectionListener( this );

            mGroupPrivTable.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mArea.getScreenColor(36),
                               mArea.getScreenColor(37)));

            DefaultTableCellRenderer renderer1 = new DefaultTableCellRenderer();
            renderer1.setBackground(mArea.getScreenColor(31));

            for (int j1 = 0; j1 < mDataConsumerPrivStd.getColumnCount(); j1++) {
                renderer1.setHorizontalAlignment(
                            SchedConsumerGroupPrivData.m_columns[j1].m_alignment);
                TableColumn mColumn = new TableColumn(j1,
                            mDataConsumerPrivStd.m_columns[j1].m_width,
                            renderer1,
                            null);
                mGroupPrivTable.addColumn(mColumn);
            }

            JScrollPane mPanel1 = new JScrollPane(mGroupPrivTable);
            mPanel1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            mPanel1.setBorder(new LineBorder(mArea.getScreenColor(38), 1, false));
            mPanel1.getViewport().setBackground(mArea.getScreenColor(32));

            return mPanel1;
        }

        public void clearConsumerPriv() {
            mDataConsumerPrivStd.clearConsumerPriv();
            mGroupPrivTable.tableChanged(new TableModelEvent(mDataConsumerPrivStd));
            mGroupPrivTable.repaint();
        }

        public void removeGroupPriv(String mGrantee) {
            mDataConsumerPrivStd.removeConsumerPrivEntry(mGrantee);
            mGroupPrivTable.tableChanged(new TableModelEvent(mDataConsumerPrivStd));

            mGroupPrivTable.repaint();
        }

        public void populateConsumerPrivTable(String mGrantee,
                                          String mGrantOption,
                                          String mInitialGroup) {
            mDataConsumerPrivStd.addConsumerPriv(
                            mGrantee,
                            mGrantOption,
                            mInitialGroup);
            mGroupPrivTable.tableChanged(new TableModelEvent( mDataConsumerPrivStd));
            mGroupPrivTable.repaint();
        }
        public int getSelectedGroupPriv() {
            return mGroupPrivTable.getSelectedRow();
        }
        public String getSelectedGroupPrivGrantee() {
            return (String)mDataConsumerPrivStd.getValueAt(mGroupPrivTable.getSelectedRow(),0);
        }

        // End of Group Privilege methods.

        public int getMappingTableRow() {
            return mMappingPriorityTable.getSelectedRow();
        }

        public JScrollPane setupMappingPriorityMappings(SchedGlobalData mArea) {
            // Set up the table for the Mapping Priority data.
            mDataMappingPriorityStd = new SchedMappingPriorityData();

            mMappingPriorityTable = new JXTable();
            mMappingPriorityTable.setAutoCreateColumnsFromModel(false);
            mMappingPriorityTable.setModel(mDataMappingPriorityStd);

            ListSelectionModel selectionModel = mMappingPriorityTable.getSelectionModel();
            selectionModel.addListSelectionListener( this );

            mMappingPriorityTable.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mArea.getScreenColor(36),
                               mArea.getScreenColor(37)));

            DefaultTableCellRenderer renderer1 = new DefaultTableCellRenderer();

            renderer1.setBackground(mArea.getScreenColor(31));

            for (int j1 = 0; j1 < mDataMappingPriorityStd.getColumnCount(); j1++) {
                // renderer1.setHorizontalAlignment(
                //                mDataMappingPriorityStd.alignment[j1]);
                TableColumn mColumn = new TableColumn(j1,
                            mDataMappingPriorityStd.columnWidth[j1],
                            renderer1,
                            null);
                mMappingPriorityTable.addColumn(mColumn);
            }

            JScrollPane mPanel1 = new JScrollPane(mMappingPriorityTable);
            // mPanel1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            mPanel1.setBorder(new LineBorder(mArea.getScreenColor(38), 1, false));
            mPanel1.getViewport().setBackground(mArea.getScreenColor(32));

            return mPanel1;
        }
        public void clearMappingPriority() {
            mDataMappingPriorityStd.clearMappingPriority();
        }

        public void populateMappingPriorityTable(String mPriority,
                                                 String mAttribute,
                                                 String mStatus) {

            // System.out.println(" 1  - " + mGroup + "--" + mLevel1 + 
            //                    "--" + mLevel2 + "--" + mLevel3 + "--" + mLevel4 + "--" + mLevel5);
            boolean mUpdate = mDataMappingPriorityStd.addMappingPriority(
                             mPriority,
                             mAttribute,
                             mStatus);

            mMappingPriorityTable.tableChanged(new TableModelEvent(mDataMappingPriorityStd));
            mMappingPriorityTable.repaint();
        }

        public void actionPerformed( ActionEvent e ) {
            System.out.println( " Event - Connect" + e.getActionCommand());
        }

        /** Required by the ListSelectionListener */
        public void valueChanged(ListSelectionEvent e) {
            // int lScreenId = getScreenId(parentFrame.getCurrentScreenNo(), m_DataArea.getVersion());

            if (getScreenNo() == SchedConsts.PLAN_SCREEN_NO) {
                if (mPlanDirectiveTable.getSelectedRow() < 0) {
                    if (mPlanDirectPop) {

                        updateTextObj(null, 13);
                        updateTextObj(null, 14);
                        updateTextObj(null, 15);
                        updateTextObj(null, 16);
                        updateTextObj(null, 17);
                        updateTextObj(null, 18);
                        updateTextObj(null, 19);
                        updateTextObj(null, 20);
                        updateTextObj(null, 21);
                        updateTextObj(null, 22);
                        updateTextObj(null, 23);
                        updateTextObj(null, 24);
                        updateTextObj(null, 25);
                        updateTextObj(null, 26);
                        updateTextAreaObj(null, 27);
                        updateTextObj(null, 28);
                        updateTextObj(null, 29);
                        updateTextObj(null, 30);
                        updateTextObj(null, 31);
                        updateTextObj(null, 32);
                        updateTextObj(null, 33);
                        updateTextObj(null, 34);
                        updateTextObj(null, 35);
                        if (m_DataArea.getVersionNo() > 2) {
                             updateTextObj(null, 36);
                             updateTextObj(null, 37);
                             updateTextObj(null, 38);
                        }
                        if (m_DataArea.getVersionNo() > 3) {
                             updateTextObj(null, 39);
                        }
                        mPlanDirectPop = false;
                    }
                }
                else {
                    int lDirectiveId = mDataPlanDirectiveStd.getIdAt(mPlanDirectiveTable.getSelectedRow());

                    for (int i1 = 0; i1 < m_DataArea.PlanDirectiveSize(); i1++) {
                        SchedDataArea.PlanDirectiveItem l_PlanDirectiveItem = m_DataArea.getPlanDirective(i1);
                        if (l_PlanDirectiveItem.getId() == lDirectiveId) {

                            m_PlanDirectiveItem = l_PlanDirectiveItem;
                            updatePlanDirective(m_PlanDirectiveItem);

                            mPlanDirectPop = true;
                            break;
                        }
                    }
                }
            }

            if (getScreenNo() == SchedConsts.CDB_PLAN_SCREEN_NO) {
                // System.out.println(" Point X1 - ");

                if (mCdbPlanDirectiveTable.getSelectedRow() < 0) {

                    if (mCdbPlanDirectPop) {
                        updateTextObj(null, 6);
                        updateTextObj(null, 7);
                        updateTextObj(null, 8);
                        updateTextAreaObj(null, 9);
                        updateTextObj(null, 10);
                        updateTextObj(null, 11);
                        updateTextObj(null, 12);

                        mCdbPlanDirectPop = false;
                    }
                }
                else {
                    // System.out.println(" Point X4 - ");
                    int lDirectiveId = mDataCdbPlanDirectiveStd.getIdAt(mCdbPlanDirectiveTable.getSelectedRow());

                    for (int i1 = 0; i1 < m_DataArea.CdbPlanDirectiveSize(); i1++) {
                        SchedDataArea.CdbPlanDirectiveItem l_CdbPlanDirectiveItem = m_DataArea.getCdbPlanDirective(i1);
                        if (l_CdbPlanDirectiveItem.getId() == lDirectiveId) {
                            m_CdbPlanDirectiveItem = l_CdbPlanDirectiveItem;
                            updateCdbPlanDirective(m_CdbPlanDirectiveItem);

                            mCdbPlanDirectPop = true;
                            break;
                        }
                    }
                }
            }


            if (getScreenNo() == SchedConsts.CONSUMER_GROUP_SCREEN_NO) {
                if (mGroupMappingTable.getSelectedRow() >= 0) {
                    for (int i1 = 0; i1 < m_DataArea.groupMappingsSize(); i1++) {
                        SchedDataArea.GroupMappingsItem m_GroupMappingsItem = m_DataArea.getGroupMappings(i1);

                        // System.out.println(" G3 " + m_GroupMappingsItem.getAttribute() + "--" +
                        //            m_GroupMappingsItem.getValue() + "==" + 
                        //            mDataConsumerGroupStd.getValueAt(mGroupMappingTable.getSelectedRow(),0) + "--" +
                        //            mDataConsumerGroupStd.getValueAt(mGroupMappingTable.getSelectedRow(),1));

                        if (m_GroupMappingsItem.getAttribute().equals(
                            (String)mDataConsumerGroupStd.getValueAt(mGroupMappingTable.getSelectedRow(),0)) &&
                             m_GroupMappingsItem.getValue().equals(
                            (String)mDataConsumerGroupStd.getValueAt(mGroupMappingTable.getSelectedRow(),1))
                           ) {
                            setGroupMappingsItem(m_GroupMappingsItem);
                            // System.out.println(" G4 " + mGroupMappingTable.getSelectedRow());
                            break;
                        }
                    }
                }
            }
        }

        public SchedDataArea.PlanDirectiveItem getPlanDirective() {
            return m_PlanDirectiveItem;
        }
        public SchedDataArea.CdbPlanDirectiveItem getCdbPlanDirective() {
            return m_CdbPlanDirectiveItem;
        }
        public void setGroupMappingsItem(SchedDataArea.GroupMappingsItem groupMappingsItem) {
            m_GroupMappingsItem = groupMappingsItem;
        }
        public SchedDataArea.GroupMappingsItem getGroupMappingsItem() {
            return m_GroupMappingsItem;
        }
        public boolean addLabelObj(LabelItem m_Item) {
            return labelObj.add(m_Item);
        }
        public LabelItem getLabelObj(int m_Row) {
            return labelObj.get(m_Row);
        }
        public int sizeLabelObj() {
            return labelObj.size();
        }
        public int sizeTextObj() {
            return textObj.size();
        }
        public int sizeTextAreaObj() {
            return textAreaObj.size();
        }

        public boolean addTextObj(TextItem m_Item) {
            return textObj.add(m_Item);
        }
        public boolean addTextAreaObj(TextAreaItem m_Item) {
            return textAreaObj.add(m_Item);
        }
        public TextItem getTextObj(int m_Row) {
            return textObj.get(m_Row);
        }
        public TextAreaItem getTextAreaObj(int m_Row) {
            return textAreaObj.get(m_Row);
        }
        public void updateTextObj(String m_Text, int m_ItemId) {
            for (int i = 0; i < textObj.size(); i++) {
                TextItem m_TextItem = textObj.get(i);
                if (m_TextItem.get_ItemId() == m_ItemId) {
                    m_TextItem.update_Text(m_Text);
                    break;
                }
            }
        }
        public void updateTextValue(String m_Text, int m_ItemId) {
            for (int i = 0; i < textObj.size(); i++) {
                TextItem m_TextItem = textObj.get(i);
                if (m_TextItem.get_ItemId() == m_ItemId) {
                    m_TextItem.update_TextValue(m_Text);
                    break;
                }
            }
        }
        public void updateTextAreaObj(String m_Text, int m_ItemId) {
            for (int i = 0; i < textAreaObj.size(); i++) {
                TextAreaItem m_TextAreaItem = textAreaObj.get(i);
                if (m_TextAreaItem.get_ItemId() == m_ItemId) {
                    m_TextAreaItem.update_Text(m_Text);
                    break;
                }
            }
        }

        public void updateTextObjects(String m_Text, int m_ItemId) {
            boolean found = false;
            for (int i = 0; i < textObj.size(); i++) {
                TextItem m_TextItem = textObj.get(i);
                // System.out.println( " S1-" + m_TextItem.get_ItemId());
                if (m_TextItem.get_ItemId() == m_ItemId) {
                    m_TextItem.update_Text(m_Text);
                    found = true;
                    break;
                }
            }
            if (! found) {
                for (int i = 0; i < textAreaObj.size(); i++) {
                    TextAreaItem m_TextAreaItem = textAreaObj.get(i);
                    // System.out.println( " S2-" + m_TextAreaItem.get_ItemId());
                    if (m_TextAreaItem.get_ItemId() == m_ItemId) {
                        m_TextAreaItem.update_Text(m_Text);
                        break;
                    }
                }
            }
        }

        public void setTextEditable(boolean m_Editable) {
            for (int i = 0; i < textObj.size(); i++) {
                TextItem m_TextItem = textObj.get(i);
                m_TextItem.setEditable(m_Editable);
            }
        }
        public int getPriority() {
            return Priority;
        }
        public void setPriority(int m_Priority) {
            Priority = m_Priority;
        }

        public boolean addColumnObj(ColumnItem m_ColumnItem) {
            return columnObj.add(m_ColumnItem);
        }
        public ColumnItem getColumnObj(int m_ColumnId) {
            return columnObj.get(m_ColumnId);
        }
        public int sizeColumnObj() {
            return columnObj.size();
        }
        class ColumnItem {
            public ColumnItem(int    m_ColumnId,
                              int    m_OptionId,
                              String m_TableName,
                              String m_ColumnName,
                              String m_ColumnNameDesc,
                              String m_ColumnType) {
                columnId = m_ColumnId;
                optionId = m_OptionId;
                tableName = m_TableName;
                columnName = m_ColumnName;
                columnNameDesc = m_ColumnNameDesc;
                columnType = m_ColumnType;
            }
            public int getColumnId() {
                return columnId;
            }
            public int getOptionId() {
                return optionId;
            }
            public String getTableName() {
                return tableName;
            }
            public String getColumnName() {
                return columnName;
            }
            public String getColumnNameDesc() {
                return columnNameDesc;
            }
            public String getColumnType() {
                return columnType;
            }

            private int    columnId;
            private int    optionId;
            private String tableName;
            private String columnName;
            private String columnNameDesc;
            private String columnType;
        }

        // class TextItem extends JFormattedTextField {
        class TextItem extends JTextField {
            public static final long serialVersionUID = 1L;

            private int    m_RowType;
            private int    m_XPoint;
            private int    m_YPoint;
            private int    m_XWidth;
            private int    m_YHeight;
            private String m_Description;
            private String m_TextValue;
            private String m_Font;
            private int    m_FontSize;
            private int    m_FontStyle;
            private int    m_ForeColor;
            private int    m_BackColor;
            private int    m_PageNo;
            private int    m_ItemId;
            private int    m_ColLength;
            private int    m_FormatType;
            private String m_Button;
            private String m_Combo;
            private int    m_ComboId;
            private String m_String;
            private String m_Display;

            public TextItem(int RowType,
                          String LDescription,
                          int XPoint, int YPoint,
                          int XWidth, int YHeight,
                          String LFont,
                          int LFontSize, int LFontStyle,
                          int LPageNo,  int LItemId,
                          int LForeColor, int LBackColor, int LColLength,
                          int LFormatType, String LButton,
                          String LCombo, int LComboId,
                          String LDisplay) {

                // super(amount2Format);

                // super.setColumns(LColLength);
                super.setEditable(false);

                m_RowType = RowType;
                m_XPoint = XPoint;
                m_YPoint = YPoint;
                m_XWidth = XWidth;
                m_YHeight = YHeight;
                this.m_Description = LDescription;
                this.m_Font = LFont;
                m_FontSize = LFontSize;
                m_FontStyle = LFontStyle;
                m_ForeColor = LForeColor;
                m_BackColor = LBackColor;
                m_PageNo = LPageNo;
                m_ItemId = LItemId;
                m_ColLength = LColLength;
                m_FormatType = LFormatType;
                m_Button = LButton;
                m_Combo = LCombo;
                m_ComboId = LComboId;
                m_Display = LDisplay;
            }

            public int get_RowType() {
                return m_RowType;
            }
            public int get_XPoint() {
                return m_XPoint;
            }
            public int get_YPoint() {
                return m_YPoint;
            }
            public int get_Width() {
                return m_XWidth;
            }
            public int get_Height() {
                return m_YHeight;
            }
            public String get_Description() {
                return m_Description;
            }
            public String get_Text() {
                return super.getText();
            }
            public int get_ColLength() {
                return m_ColLength;
            }
            public int get_FormatType() {
                return m_FormatType;
            }
            public String get_Button() {
                return m_Button;
            }
            public String get_Combo() {
                return m_Combo;
            }
            public int get_ComboId() {
                return m_ComboId;
            }
            public void update_Text(String LText) {
                super.setText(LText);
                m_TextValue = LText;
            }
            public void update_TextValue(String LText) {
                m_TextValue = LText;
            }
            public String get_TextValue() {
                return m_TextValue;
            }
            public String get_Font() {
                return m_Font;
            };
            public int get_FontSize() {
                return m_FontSize;
            }
            public int get_FontStyle() {
                return m_FontStyle;
            }
            public int get_ForeColor() {
                return m_ForeColor;
            };
            public int get_BackColor() {
                return m_BackColor;
            };
            public int get_PageNo() {
                return m_PageNo;
            }
            public int get_ItemId() {
                return m_ItemId;
            }
            public String get_Display() {
                return m_Display;
            };
        }

        class TextAreaItem extends JTextArea {
            public static final long serialVersionUID = 1L;

            private int    m_XPoint;
            private int    m_YPoint;
            private int    m_XWidth;
            private int    m_YHeight;
            private String m_Description;
            private String m_Font;
            private int    m_FontSize;
            private int    m_FontStyle;
            private int    m_ForeColor;
            private int    m_BackColor;
            private int    m_PageNo;
            private int    m_ItemId;
            private int    m_ColLength;
            private int    m_FormatType;
            private String m_Button;
            private String m_Display;

            private String m_String;

            public TextAreaItem(String LDescription,
                                int XPoint, int YPoint,
                                int XWidth, int YHeight,
                                String LFont,
                                int LFontSize, int LFontStyle,
                                int LPageNo,  int LItemId,
                                int LForeColor, int LBackColor, int LColLength,
                                int LFormatType, String LButton,
                                String LDisplay) {

                super.setColumns(LColLength);
                super.setEditable(false);
                super.setLineWrap(true);
                super.setWrapStyleWord(true);
                // super.setBorder(BorderFactory.createLineBorder(mArea.getScreenColor(LForeColor)));

                m_XPoint = XPoint;
                m_YPoint = YPoint;
                m_XWidth = XWidth;
                m_YHeight = YHeight;
                this.m_Description = LDescription;
                this.m_Font = LFont;
                m_FontSize = LFontSize;
                m_FontStyle = LFontStyle;
                m_ForeColor = LForeColor;
                m_BackColor = LBackColor;
                m_PageNo = LPageNo;
                m_ItemId = LItemId;
                m_ColLength = LColLength;
                m_FormatType = LFormatType;
                m_Button = LButton;
                m_Display = LDisplay;
            }

            public int get_XPoint() {
                return m_XPoint;
            }
            public int get_YPoint() {
                return m_YPoint;
            }
            public int get_Width() {
                return m_XWidth;
            }
            public int get_Height() {
                return m_YHeight;
            }
            public String get_Description() {
                return m_Description;
            }
            public String get_Text() {
                return super.getText();
            }
            public int get_ColLength() {
                return m_ColLength;
            }
            public int get_FormatType() {
                return m_FormatType;
            }
            public String get_Button() {
                return m_Button;
            }
            public void update_Text(String LText) {
                // System.out.println(" C " + LText);
                super.setText(LText);
            }
            public String get_Font() {
                return m_Font;
            };
            public int get_FontSize() {
                return m_FontSize;
            }
            public int get_FontStyle() {
                return m_FontStyle;
            }
            public int get_ForeColor() {
                return m_ForeColor;
            };
            public int get_BackColor() {
                return m_BackColor;
            };
            public int get_PageNo() {
                return m_PageNo;
            }
            public int get_ItemId() {
                return m_ItemId;
            }
            public String get_Display() {
                return m_Display;
            };
        }

        class LabelItem extends JLabel {
            public static final long serialVersionUID = 1L;

            private int    m_XPoint;
            private int    m_YPoint;
            private int    m_XWidth;
            private int    m_YHeight;
            private String m_Description;
            private String m_Font;
            private int    m_FontSize;
            private int    m_FontStyle;
            private int    m_ForeColor;
            private int    m_BackColor;
            private int    m_PageNo;
            private String m_Display;

            public LabelItem(String LDescription,
                           int XPoint, int YPoint,
                           int XWidth, int YHeight,
                           String LFont,
                           int LFontSize, int LFontStyle,
                           int LPageNo, int LForeColor, int LBackColor,
                           String LDisplay) {
                super(LDescription);
                m_XPoint = XPoint;
                m_YPoint = YPoint;
                m_XWidth = XWidth;
                m_YHeight = YHeight;
                this.m_Description = LDescription;
                this.m_Font = LFont;
                m_FontSize = LFontSize;
                m_FontStyle = LFontStyle;
                m_PageNo = LPageNo;
                m_ForeColor = LForeColor;
                m_BackColor = LBackColor;
                m_Display = LDisplay;
            }
            public int get_XPoint() {
              return m_XPoint;
            }
            public int get_YPoint() {
              return m_YPoint;
            }
            public int get_Width() {
              return m_XWidth;
            }
            public int get_Height() {
              return m_YHeight;
            }
            public String get_Description() {
              return m_Description;
            }
            public void update_Description(String LDescription) {
              this.m_Description = LDescription;
            }
            public String get_Font() {
              return m_Font;
            };
            public int get_FontSize() {
              return m_FontSize;
            }
            public int get_FontStyle() {
              return m_FontStyle;
            }
            public int get_PageNo() {
              return m_PageNo;
            }
            public int get_ForeColor() {
              return m_ForeColor;
            };
            public int get_BackColor() {
              return m_BackColor;
            };
            public String get_Display() {
                return m_Display;
            };
        }

        private Vector<LabelItem>    labelObj;
        private Vector<TextItem>     textObj;
        private Vector<TextAreaItem> textAreaObj;
        private Vector<ColumnItem>   columnObj;
    }

    public void addTab(int mTabId, int mTabPageNo, String mDesc) {
        boolean found = false;
        for (int i = 0; i < tabObj.size(); i++) {
            TabObject mTabObject = tabObj.get(i);
            // System.out.println( " S2-" + m_TextAreaItem.get_ItemId());
            if (mTabObject.getTabId() == mTabId) {
                TabObject.TabPage mTabPage = mTabObject.new TabPage(mTabPageNo, mDesc);
                mTabObject.addTabPage(mTabPage);
                found = true;
                break;
            }
        }
        if (! found) {
            TabObject mTabObject = new TabObject(mTabId);
            tabObj.add(mTabObject);
            TabObject.TabPage mTabPage = mTabObject.new TabPage(mTabPageNo, mDesc);
            mTabObject.addTabPage(mTabPage);
        }
    }

    class TabObject {

        public TabObject(int m_TabId) {
            tabId = m_TabId;
            tabPage  = new Vector<TabPage>(10,3);
        }
        public int getTabId() {
            return tabId;
        }
        public TabPage getTabPage(int mTabId) {
            return tabPage.get(mTabId);
        }
        public boolean addTabPage(TabPage mTabPage) {
            return tabPage.add(mTabPage);
        }
        public int getTabSize() {
            return tabPage.size();
        }

        class TabPage {
            public TabPage(int m_TabPageNo, String m_TabDesc) {
                tabPageNo = m_TabPageNo;
                tabDesc = m_TabDesc;
            }
            public String getDesc() {
                return tabDesc;
            }
            public int getTabPageNo() {
                return tabPageNo;
            }

            private int    tabPageNo;
            private String tabDesc;
        }

        private int                 tabId;
        private Vector<TabPage>     tabPage;
    }

    class TabChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            // System.out.println(" X1.");
            if ((JTabbedPane) e.getSource() != null)
                parentFrame.setupResourceMenu(((JTabbedPane) e.getSource()).getSelectedIndex());
        }
    }

    private JPanel                  m_JPanel;
    private PaneObject              m_PaneObject;

    private Vector<PaneIndex>       indexObj;
    private Vector<PaneObject>      screenObj;
    private Vector<TabObject>       tabObj;
}
