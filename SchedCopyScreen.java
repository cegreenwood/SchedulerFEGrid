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
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;

import java.awt.*;

class SchedCopyScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton                                      mEnter, mCancel;
    private JPanel                                       dataPane, dataPanel, dataPane1;

    private Scheduler                                    mParentFrame;
    private SchedDataArea                                mDataArea;
    private SchedDataArea.JobItem                        mJobItem;
    private SchedDataArea.JobArgsItem                    mJobArgsItem;
    private SchedDataArea.ProgramItem                    mProgramItem;
    private SchedDataArea.ProgramArgsItem                mProgramArgsItem;
    private SchedDataArea.ScheduleItem                   mScheduleItem;
    private SchedDataArea.JobClassItem                   mJobClassItem;
    private SchedDataArea.WindowItem                     mWindowItem;
    private SchedDataArea.WindowGroupItem                mWindowGroupItem;
    private SchedDataArea.ChainsItem                     mChainItem, mNewChainItem;
    private SchedDataArea.ChainStepsItem                 mChainStepItem;
    private SchedDataArea.ChainRulesItem                 mChainRuleItem;
    private SchedDataArea.FileWatchersItem               mFileWatcherItem;
    private SchedDataArea.GroupItem                      mGroupItem;

    private SchedGlobalData                              mArea;
    private SchedInpScreenArea                           mInpScreens;
    private SchedInpScreenArea.PaneObject                mPane;
    private SchedInpScreenArea.PaneObject.TextItem       mTextItem;
    private SchedInpScreenArea.PaneObject.TextAreaItem   mTextAreaItem;

    private String                                       mObjectType, mObjectName;
    private int                                          mNodeId;

    private boolean                                      mDispose;
    private ClassLoader  cl;

    public  static final int          SCREEN_NO  = 524;

    public SchedCopyScreen(Scheduler           parentFrame,
                           SchedDataArea       dataArea,
                           SchedGlobalData     Area,
                           SchedInpScreenArea  InpScreens,
                           String              ObjectType,
                           String              ObjectName,
                           int                 NodeId)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        mParentFrame = parentFrame;
        mDataArea = dataArea;
        mArea = Area;
        mInpScreens = InpScreens;
        mObjectType = ObjectType;
        mObjectName = ObjectName;
        mNodeId = NodeId;
        mDataArea.setReturnNo(1);

        // setTitle( "Scheduler FE Grid" );
        setSize( 620, 460 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        // this.setLocation(150,100);
        this.setLocation((int)mParentFrame.getLocationOnScreen().getX() + 160,
                         (int)mParentFrame.getLocationOnScreen().getY() + 100);

        dataPane = new JPanel();
        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        // dataPane.setSize(700,500);

        SpringLayout lm = new SpringLayout();
        dataPane.setLayout(lm);

        dataPane1 = new JPanel();
        dataPane1 = setupPane();

        dataPane.add(dataPane1,
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(0),
                                                 Spring.constant(606),
                                                 Spring.constant(360)));

        dataPane.add(setupButtons(),
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(360),
                                                 Spring.constant(606),
                                                 Spring.constant(64)));;

        getContentPane().add( dataPane );
    }

    private JPanel setupPane() {

        int mScreenId = mInpScreens.getScreenId(SCREEN_NO, mDataArea.getVersion());

        mPane = mInpScreens.getScreen(mScreenId);

        cl = this.getClass().getClassLoader();
        Icon LookIcon1 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook1.jpg"));
        Icon LookIcon2 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook2.jpg"));

        Border blackline = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

        dataPanel = new JPanel();
        dataPanel.setSize( 600, 380 );
        dataPanel.setBorder(blackline);
        dataPanel.setLayout(new SpringLayout());

        dataPanel.setBackground(mArea.getScreenColor(mPane.getBgrndColour()));
        dataPanel.setForeground(mArea.getScreenColor(mPane.getFgrndColour()));

        // Setting up the labels.
        for (int i2 = 0; i2 < mPane.sizeLabelObj(); i2++) {

            SchedInpScreenArea.PaneObject.LabelItem m_P1 = mPane.getLabelObj(i2);

            dataPanel.add(m_P1,
                        new SpringLayout.Constraints(Spring.constant(m_P1.get_XPoint()),
                                                 Spring.constant(m_P1.get_YPoint()),
                                                 Spring.constant(m_P1.get_Width()),
                                                 Spring.constant(m_P1.get_Height())));
        }

        // Setting up the text items.
        for (int i3 = 0; i3 < mPane.sizeTextObj(); i3++) {

            SchedInpScreenArea.PaneObject.TextItem m_T1 = mPane.getTextObj(i3);


            if (m_T1.get_Combo().equals("Y")) {
                if (m_T1.get_ComboId() < 50) 
                    dataPanel.add(m_T1.getComboBoxType1(mArea),
                        new SpringLayout.Constraints(Spring.constant(m_T1.get_XPoint()),
                                                     Spring.constant(m_T1.get_YPoint()),
                                                     Spring.constant(m_T1.get_Width()),
                                                     Spring.constant(m_T1.get_Height())));
                else
                    dataPanel.add(m_T1.getComboBoxType2(mDataArea),
                        new SpringLayout.Constraints(Spring.constant(m_T1.get_XPoint()),
                                                     Spring.constant(m_T1.get_YPoint()),
                                                     Spring.constant(m_T1.get_Width()),
                                                     Spring.constant(m_T1.get_Height())));
            }
            else {
                m_T1.update_Text("");
                if (m_T1.get_ItemId() == 1) {
                    m_T1.update_Text(mObjectType);
                    m_T1.setEditable(false);
                }
                if (m_T1.get_ItemId() == 2) {
                    m_T1.update_Text(mObjectName);
                    m_T1.setEditable(false);
                }
                dataPanel.add(m_T1,
                        new SpringLayout.Constraints(Spring.constant(m_T1.get_XPoint()),
                                                     Spring.constant(m_T1.get_YPoint()),
                                                     Spring.constant(m_T1.get_Width()),
                                                     Spring.constant(m_T1.get_Height())));

                if (m_T1.get_Button().equals("Y")) {
                    JButton m_B1 = new JButton();
                    if (m_T1.get_RowType() == 5) m_B1.setIcon(LookIcon2);
                    else                         m_B1.setIcon(LookIcon1);

                    if (m_T1.get_BackColor() > 0) {
                        Color mColor = mArea.getScreenColor(m_T1.get_BackColor());
                        if (mColor != null) {
                            m_B1.setOpaque(true);
                            m_B1.setBackground(mPane.getBColor());
                        }
                    }
                    dataPanel.add(m_B1,
                           new SpringLayout.Constraints(
                               Spring.constant(m_T1.get_XPoint() + m_T1.get_Width() + 5),
                               Spring.constant(m_T1.get_YPoint()),
                               Spring.constant(30),
                               Spring.constant(m_T1.get_Height())));
                    m_B1.addActionListener( this );
                    m_B1.setActionCommand( String.valueOf("Edit" + i3) );
                }
            }
        }
        return dataPanel;
    }

    private JPanel setupButtons() {

        JPanel dataPane3 = new JPanel();
        // dataPane3.setSize( 620, 60 );
        dataPane3.setLayout(new SpringLayout());
        dataPane3.setBackground(mArea.getScreenColor(42));

        // Add button to screen.
        mEnter = new JButton("  OK  ");
        mEnter.setBackground( mArea.getButtonColor() );
        mEnter.addActionListener( this );
        mEnter.setActionCommand("OK");
        mEnter.setMnemonic(KeyEvent.VK_O);

        dataPane3.add(mEnter, new SpringLayout.Constraints(Spring.constant(200),
                                                 Spring.constant(10),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));

        mCancel = new JButton("Cancel");
        mCancel.setBackground( mArea.getButtonColor() );
        mCancel.addActionListener( this );
        mCancel.setActionCommand("Exit");
        mCancel.setMnemonic(KeyEvent.VK_C);

        dataPane3.add(mCancel, new SpringLayout.Constraints(Spring.constant(320),
                                                 Spring.constant(10),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));

        return dataPane3;
    }

    public void actionPerformed( ActionEvent e ) {

        if ( e.getActionCommand().equals("OK") ) {
            performCopy();
            if (mDispose) dispose();
        }
        if ( e.getActionCommand().equals("Exit") ) {
            dispose();
        }
    }

    private void performCopy() {
        int returnValue = 0;
        mDispose = true;

        if (mObjectType.equals("JOB")) {
            if (mDataArea.CopyJob(mObjectName, mPane.getTextObjId(3)) == 0) {
                mJobItem = mDataArea.GetJobItem(
                                 mDataArea.getUserName(),
                                 mPane.getTextObjId(3), 0);
                if (mJobItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Job from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createJobTreeItem(mJobItem);
                }
            }
            else {
                dispose();
                mParentFrame.errorBox("Error - " + mDataArea.getSysMessage());
            }
        }
        if (mObjectType.equals("PROGRAM")) {
            mProgramItem = mDataArea.getProgramId(mNodeId);
            returnValue = mDataArea.CreateProgram(mPane.getTextObjId(3),
                                                      mProgramItem.getProgramType(),
                                                      mProgramItem.getProgramAction(),
                                                      mProgramItem.getComments());

            if (returnValue == 0) {
                mProgramItem = mDataArea.GetProgramItem(
                                        mDataArea.getUserName(),
                                        mPane.getTextObjId(3),
                                        0);
                if (mProgramItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Program from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createProgramTreeItem(mProgramItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }

        if (mObjectType.equals("SCHEDULE")) {

            mScheduleItem = mDataArea.getScheduleId(mNodeId);

            returnValue = mDataArea.CreateSchedule1(
                                  mPane.getTextObjId(3),
                                  mScheduleItem.getStartDate(),
                                  mScheduleItem.getRepeatInterval(),
                                  mScheduleItem.getEndDate(),
                                  mScheduleItem.getComments());

            if (returnValue == 0) {
                mScheduleItem = mDataArea.GetScheduleItem(
                                                  mDataArea.getUserName(),
                                                  mPane.getTextObjId(3),
                                                  0);

                if (mScheduleItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Schedule from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createScheduleTreeItem(mScheduleItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }

        }
        if (mObjectType.equals("CHAIN")) {

            mChainItem = mDataArea.getChainsId(mNodeId);

            returnValue = mDataArea.CreateChain(
                                  mPane.getTextObjId(3),
                                  mChainItem.getRuleSetOwner() + "." + mChainItem.getRuleSetName(),
                                  mChainItem.getEvaluationInterval(),
                                  mChainItem.getComments());

            if (returnValue == 0) {
                mNewChainItem = mDataArea.GetChainsItem(
                                           mDataArea.getUserName(),
                                           mPane.getTextObjId(3), 0);
                if (mNewChainItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Chain from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createChainTreeItem(mNewChainItem);

                    for (int i = 0; i < mDataArea.ChainStepsSize(); i++) {
                        mChainStepItem = mDataArea.getChainSteps(i);

                        if ((mChainStepItem.getOwner().equals(mChainItem.getOwner())) &&
                            (mChainStepItem.getChainName().equals(mChainItem.getChainName())))
                        {

                            if (mChainStepItem.getProgramName() != null) {
                                returnValue = mDataArea.CreateChainStep1(
                                                        mPane.getTextObjId(3),
                                                        mChainStepItem.getStepName(),
                                                        mChainStepItem.getProgramOwner() + "." +
                                                        mChainStepItem.getProgramName());
                            }
                            else {

                                if (mChainStepItem.getEventCondition() != null)
                                {
                                    returnValue = mDataArea.CreateChainStep2(
                                                        mPane.getTextObjId(3),
                                                        mChainStepItem.getStepName(),
                                                        mChainStepItem.getEventCondition(),
                                                        mChainStepItem.getEventQueueOwner() + "." +
                                                        mChainStepItem.getEventQueueName() + "," +
                                                        mChainStepItem.getEventQueueAgent());
                                }
                                else {
                                    if (mChainStepItem.getEventScheduleName() != null)
                                    {
                                        returnValue = mDataArea.CreateChainStep3(
                                                        mPane.getTextObjId(3),
                                                        mChainStepItem.getStepName(),
                                                        mChainStepItem.getEventScheduleOwner() + "." +
                                                        mChainStepItem.getEventScheduleName());
                                    }
                                }
                            }
                            if (returnValue == 0) {

                                mChainStepItem = mDataArea.GetChainStepsItem(
                                         mDataArea.getUserName(),
                                         mPane.getTextObjId(3),
                                         mChainStepItem.getStepName(),
                                         0);

                                if (mChainStepItem == null) {
                                    SchedFile.EnterErrorEntry(
                                           "SchedCopyScreen.performCopy"," : Error...Reading Chain Step Item.");
                                    SchedFile.EnterErrorEntry(
                                           "SchedCopyScreen.performCopy","           " + mDataArea.getSysMessage());
                                }
                                else {
                                    mParentFrame.createChainStepTreeItem(mChainStepItem);
                                }
                            }
                            else {
                                SchedFile.EnterErrorEntry(
                                           "SchedCopyScreen.performCopy"," : Error...Creating Chain Step Item.");
                                SchedFile.EnterErrorEntry(
                                           "SchedCopyScreen.performCopy","           " + mDataArea.getSysMessage());

                            }

                        }
                    }
                    for (int i = 0; i < mDataArea.ChainRulesSize(); i++) {

                        mChainRuleItem = mDataArea.getChainRules(i);
                        if ( (mChainRuleItem.getOwner().equals(mChainItem.getOwner())) && 
                             (mChainRuleItem.getChainName().equals(mChainItem.getChainName())) )
                        {

                            returnValue = mDataArea.CreateChainRule(
                                                        mPane.getTextObjId(3),
                                                        mChainRuleItem.getRuleName(),
                                                        mChainRuleItem.getConditions(),
                                                        mChainRuleItem.getAction(),
                                                        mChainRuleItem.getComments());

                            if (returnValue != 0) {
                                SchedFile.EnterErrorEntry(
                                           "SchedCopyScreen.performCopy"," : Error...Creating Chain Rule Item.");
                                SchedFile.EnterErrorEntry(
                                           "SchedCopyScreen.performCopy","           " + mDataArea.getSysMessage());
                            }
                            mChainRuleItem = mDataArea.GetChainRulesItem(
                                          mDataArea.getUserName(),
                                          mPane.getTextObjId(3),
                                          mChainRuleItem.getRuleName());

                            if (mChainRuleItem == null) {
                                SchedFile.EnterErrorEntry(
                                           "SchedCopyScreen.performCopy"," : Error...Reading Chain Rule Item.");
                                SchedFile.EnterErrorEntry(
                                           "SchedCopyScreen.performCopy","           " + mDataArea.getSysMessage());
                            }
                            else {
                                mParentFrame.createChainRuleTreeItem(mChainRuleItem);
                            }
                        }
                    }
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        if (mObjectType.equals("CHAINSTEP")) {

            mChainStepItem = mDataArea.getChainStepsId(mNodeId);

            if (mChainStepItem.getProgramName().length() == 0) {
                if (mChainStepItem.getEventScheduleName().length() == 0) {
                    returnValue = mDataArea.CreateChainStep2(
                                      mChainStepItem.getChainName(),
                                      mPane.getTextObjId(3),
                                      mChainStepItem.getEventCondition(),
                                      mChainStepItem.getEventQueueOwner() + "." +
                                          mChainStepItem.getEventQueueName() + "," +
                                          mChainStepItem.getEventQueueAgent());
                }
                else {
                    returnValue = mDataArea.CreateChainStep3(
                                      mChainStepItem.getChainName(),
                                      mPane.getTextObjId(3),
                                      mChainStepItem.getEventScheduleOwner() + "." +
                                          mChainStepItem.getEventScheduleName());
                }
            }
            else {
                returnValue = mDataArea.CreateChainStep1(
                                      mChainStepItem.getChainName(),
                                      mPane.getTextObjId(3),
                                      mChainStepItem.getProgramOwner() + "." +
                                          mChainStepItem.getProgramName());
            }
            if (returnValue == 0) {
                mChainStepItem = mDataArea.GetChainStepsItem(
                                         mDataArea.getUserName(),
                                         mChainStepItem.getChainName(),
                                         mPane.getTextObjId(3),
                                         0);
                if (mChainStepItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Chain Step Item from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createChainStepTreeItem(mChainStepItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }

        }
        if (mObjectType.equals("CHAINRULE")) {

            mChainRuleItem = mDataArea.getChainRulesId(mNodeId);

            returnValue = mDataArea.CreateChainRule(
                                  mChainRuleItem.getChainName(),
                                  mPane.getTextObjId(3),
                                  mChainRuleItem.getConditions(),
                                  mChainRuleItem.getAction(),
                                  mChainRuleItem.getComments());

            if (returnValue == 0) {
                mChainRuleItem = mDataArea.GetChainRulesItem(
                                          mChainRuleItem.getOwner(),
                                          mChainRuleItem.getChainName(), 
                                          mPane.getTextObjId(3));
                if (mChainRuleItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Chain Rule Item from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createChainRuleTreeItem(mChainRuleItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        if (mObjectType.equals("JOB CLASS")) {

            mJobClassItem = mDataArea.getJobClassId(mNodeId);

            returnValue = mDataArea.CreateJobClass(
                                  mPane.getTextObjId(3),
                                  mJobClassItem.getResourceConsumerGroup(),
                                  mJobClassItem.getService(),
                                  mJobClassItem.getComments());

            if (returnValue == 0) {
                mJobClassItem = mDataArea.GetJobClassItem(
                                        mPane.getTextObjId(3), 0);
                if (mJobClassItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Job from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createJobClassTreeItem(mJobClassItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        if (mObjectType.equals("WINDOW")) {

            mWindowItem = mDataArea.getWindowId(mNodeId);

            if (mWindowItem.getScheduleName() == null) {
                returnValue = mDataArea.CreateWindow2(
                                      mPane.getTextObjId(3),
                                      mWindowItem.getResourcePlan(),
                                      mWindowItem.getStartDate(),
                                      mWindowItem.getRepeatInterval(),
                                      mWindowItem.getEndDate(),
                                      mWindowItem.getDuration(),
                                      mWindowItem.getComments());
            }
            else {
                returnValue = mDataArea.CreateWindow1(
                                      mPane.getTextObjId(3),
                                      mWindowItem.getResourcePlan(),
                                      mWindowItem.getScheduleOwner() + "." +
                                          mWindowItem.getScheduleName(),
                                      mWindowItem.getDuration(),
                                      mWindowItem.getComments());
            }

            if (returnValue == 0) {
                mWindowItem = mDataArea.GetWindowItem(mPane.getTextObjId(3), 0);
                if (mWindowItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Window from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createWindowTreeItem(mWindowItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }

        }
        if (mObjectType.equals("WINDOW GROUP")) {

            mWindowGroupItem = mDataArea.getWindowGroupId(mNodeId);

            returnValue = mDataArea.CreateWindowGroup(
                                  mPane.getTextObjId(3),
                                  null,
                                  mWindowGroupItem.getComments());
            if (returnValue == 0) {
                mWindowGroupItem = mDataArea.GetWindowGroupItem(
                                  mPane.getTextObjId(3), 0);
                if (mWindowGroupItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Window Group from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createWindowGroupTreeItem(mWindowGroupItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        if (mObjectType.equals("FILE WATCHER")) {

            mFileWatcherItem = mDataArea.getFileWatchersId(mNodeId);

            returnValue = mDataArea.CreateFileWatcher(
                                  mPane.getTextObjId(3),
                                  mFileWatcherItem.getDirectoryPath(),
                                  mFileWatcherItem.getFileName(),
                                  mFileWatcherItem.getCredentialOwner() + "." + 
                                          mFileWatcherItem.getCredentialName(),
                                  mFileWatcherItem.getComments());

            if (returnValue == 0) {
                mFileWatcherItem = mDataArea.GetFileWatcherItem(
                                  mDataArea.getUserName(),
                                  mPane.getTextObjId(3),
                                  0);
                if (mFileWatcherItem == null) {
                    mParentFrame.errorBox("Error - Cannot read File Watcher Item from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createFileWatcherTreeItem(mFileWatcherItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        if (mObjectType.equals("GROUP")) {

            mGroupItem = mDataArea.getGroupsId(mNodeId);
            String lUserName = null;
            if ( mGroupItem.getGroupType().equals("WINDOW") )
                lUserName = "SYS";
            else
                lUserName = mDataArea.getUserName();

            returnValue = mDataArea.CreateGroup(
                                  mPane.getTextObjId(3),
                                  mGroupItem.getGroupType(),
                                  null,
                                  mGroupItem.getComments());

            if (returnValue == 0) {
                mGroupItem = mDataArea.GetGroupItem(
                                  lUserName,
                                  mPane.getTextObjId(3),
                                  0);
                if (mGroupItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Group Item from Database.");
                    mDispose = false;
                }
                else {
                    if ( mGroupItem.getGroupType().equals("WINDOW") )
                        mParentFrame.createGroupWindowTreeItem(mGroupItem);
                    if ( mGroupItem.equals("DB_DEST") )
                        mParentFrame.createGroupDbDestTreeItem(mGroupItem);
                    if ( mGroupItem.equals("EXTERNAL_DEST") )
                        mParentFrame.createGroupExtDestTreeItem(mGroupItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }
}

