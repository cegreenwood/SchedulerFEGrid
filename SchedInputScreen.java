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

class SchedInputScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    public  static final int          PROGRAM_SCREEN_NO  = 506;
    public  static final int          JOB_CLASS_SCREEN_NO  = 509;
    public  static final int          WINDOW_GROUP_SCREEN_NO  = 512;
    public  static final int          JOB_ARGS_SCREEN_NO  = 513;
    public  static final int          CHAIN_SCREEN_NO  = 515;
    public  static final int          CHAIN_RULE_SCREEN_NO  = 516;
    public  static final int          PURGE_LOGS_SCREEN_NO  = 520;
    public  static final int          ASSIGN_WINDOW_SCREEN_NO  = 521;
    public  static final int          CREDENTIAL_SCREEN_NO  = 523;
    public  static final int          FILE_WATCHER_SCREEN_NO = 526;
    public  static final int          NOTIFICATION_SCREEN_NO = 527;
    public  static final int          DBDEST_SCREEN_NO = 528;
    public  static final int          ASSIGN_DBDEST_SCREEN_NO = 529;
    public  static final int          ASSIGN_EXTDEST_SCREEN_NO = 530;
    public  static final int          GROUP_SCREEN_NO = 533;

    public  static final int          PLAN_DIRECTIVE_SCREEN_NO = 560;
    public  static final int          RESOURCE_PLAN_SCREEN_NO = 561;
    public  static final int          CONSUMER_GROUP_SCREEN_NO = 562;
    public  static final int          GROUP_MAPPING_SCREEN_NO = 563;
    public  static final int          SWITCH_USER_SCREEN_NO = 564;
    public  static final int          SWITCH_SESSION_SCREEN_NO = 565;
    public  static final int          ASSIGN_PRIVILEGE_SCREEN_NO = 566;
    public  static final int          CDB_RESOURCE_PLAN_SCREEN_NO = 568;
    public  static final int          CDB_PLAN_DIRECTIVE_SCREEN_NO = 569;

    private JButton                                      mEnter, mCancel;
    private JPanel                                       dataPane, dataPanel;
    private JPanel                                       dataPane1;
    private SchedInpScreenArea                           mInpScreens;
    private SchedInpScreenArea.PaneObject                mPane;
    private SchedInpScreenArea.PaneObject.TextItem       mTextItem;
    private SchedInpScreenArea.PaneObject.TextAreaItem   mTextAreaItem;
    private SchedGlobalData                              mArea;

    private int                                          mScreenNo;
    private int                                          mOptionId;
    private int                                          mItemNo;
    private Scheduler                                    mParentFrame;
    private SchedDataArea                                mDataArea;
    private boolean                                      mDispose;
    private String                                       mString;
    private String                                       mChainName;
    private String[]                                     Str10;
    private SchedDataArea.ProgramItem                    mProgramItem;
    private SchedDataArea.JobClassItem                   mJobClassItem;
    private SchedDataArea.JobArgsItem                    mJobArgItem;
    private SchedDataArea.ProgramArgsItem                mProgramArgItem;
    private SchedDataArea.WindowGroupItem                mWindowGroupItem;
    private SchedDataArea.WinGroupMembersItem            mWinGroupMembersItem;
    private SchedDataArea.GroupItem                      mGroupItem;
    private SchedDataArea.GroupMembersItem               mGroupMembersItem;
    private SchedDataArea.ChainsItem                     mChainsItem;
    private SchedDataArea.ChainRulesItem                 mChainRulesItem;
    private SchedDataArea.CredentialsItem                mCredentialItem;
    private SchedDataArea.DbDestsItem                    mDbDestsItem;
    private SchedDataArea.FileWatchersItem               mFileWatchersItem;
    private SchedDataArea.NotificationsItem              mNotificationsItem;

    private SchedDataArea.PlanItem                       mPlanItem;
    private SchedDataArea.CdbPlanItem                    mCdbPlanItem;
    private SchedDataArea.ConsumerGroupItem              mConsumerGroupItem;
    private SchedDataArea.ConsumerPrivItem               mConsumerPrivItem;
    private SchedDataArea.PlanDirectiveItem              mPlanDirectiveItem;
    private SchedDataArea.CdbPlanDirectiveItem           mCdbPlanDirectiveItem;
    private SchedDataArea.GroupMappingsItem              mGroupMappingsItem;

    private ClassLoader  cl;

    public SchedInputScreen(Scheduler           parentFrame,
                            SchedDataArea       dataArea,
                            SchedGlobalData     Area,
                            SchedInpScreenArea  InpScreens,
                            int                 ScreenNo,
                            int                 OptionId)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        mParentFrame = parentFrame;
        mDataArea = dataArea;
        mInpScreens = InpScreens;
        mOptionId = OptionId;
        mArea = Area;
        mDataArea.setReturnNo(1);

        mScreenNo = ScreenNo;

        // setTitle( "Scheduler FE Grid" );
        setSize( 620, 460 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        // this.setLocation(150,100);
        this.setLocation((int)mParentFrame.getLocationOnScreen().getX() + 160,
                         (int)mParentFrame.getLocationOnScreen().getY() + 100);

        dataPane = new JPanel();
        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        // dataPane.setSize(600,460);

        SpringLayout lm = new SpringLayout();
        dataPane.setLayout(lm);

        getContentPane().add( dataPane );

        dataPane1 = new JPanel();
        dataPane1 = setupPane();
        dataPane.setBackground(mArea.getScreenColor(42));

        dataPane.add(dataPane1,
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(0),
                                                 Spring.constant(606),
                                                 Spring.constant(370)));

        dataPane.add(setupButtons(),
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(370),
                                                 Spring.constant(606),
                                                 Spring.constant(48)));;


    }

    private JPanel setupPane() {

        int mScreenId = mInpScreens.getScreenId(mScreenNo, mDataArea.getVersion());

        mPane = mInpScreens.getScreen(mScreenId);

        cl = this.getClass().getClassLoader();
        Icon LookIcon1 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook1.jpg"));
        Icon LookIcon2 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook2.jpg"));

        Border blackline = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

        dataPanel = new JPanel();
        dataPanel.setSize( 612, 364 );
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


            if ((m_T1.get_Combo().equals("Y")) && (m_T1.get_RowType() == 2)) {
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

                if (m_T1.get_FormatType() == 1) {
                    m_T1.setZero();
                }
                else {
                    m_T1.update_Text("");
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

        // Setting up the text area items.
        for (int i4 = 0; i4 < mPane.sizeTextAreaObj(); i4++) {

            SchedInpScreenArea.PaneObject.TextAreaItem m_T2 = mPane.getTextAreaObj(i4);
            m_T2.update_Text("");

            dataPanel.add(m_T2,
                        new SpringLayout.Constraints(Spring.constant(m_T2.get_XPoint()),
                                                     Spring.constant(m_T2.get_YPoint()),
                                                     Spring.constant(m_T2.get_Width()),
                                                     Spring.constant(m_T2.get_Height())));

        }
        // Loop through all the DataTime objects.
        for (int i5 = 0; i5 < mPane.sizeDateTimeObj(); i5++) {

            SchedInpScreenArea.PaneObject.DateTimeItem m_T3 = mPane.getDateTimeObj(i5);
            m_T3.initObj(mArea, "");
            dataPanel.add(m_T3,
                        new SpringLayout.Constraints(Spring.constant(m_T3.get_XPoint()),
                                                     Spring.constant(m_T3.get_YPoint()),
                                                     Spring.constant(m_T3.get_Width()),
                                                     Spring.constant(m_T3.get_Height())));
        }
        return dataPanel;
    }

    private JPanel setupButtons() {

        JPanel dataPane3 = new JPanel();
        dataPane3.setSize( 620, 60 );
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

    private void CreateProgram() {
        Str10 = new String[4];

        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        // Get 4th Argument.
        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[3] = mTextAreaItem.get_Text();

        int returnValue = mDataArea.CreateProgram(Str10[0], Str10[1], Str10[2], Str10[3]);

        if (returnValue == 0) {

            String Str1 = Str10[0];
            if ( Str1.indexOf('.') < 0 ) {
                mProgramItem = mDataArea.GetProgramItem(
                                 mDataArea.getUserName(),
                                 Str10[0],
                                 0);
            }
            else {

                String Str2 = Str1.substring(0, Str1.indexOf('.'));
                String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                mProgramItem = mDataArea.GetProgramItem(
                                    Str2,
                                    Str3,
                                    0);
            }

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

    private void CreateJobClass() {
        Str10 = new String[4];

        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[3] = mTextAreaItem.get_Text();

        int returnValue = mDataArea.CreateJobClass(Str10[0], Str10[1], Str10[2], Str10[3]);

        if (returnValue == 0) {
            mJobClassItem = mDataArea.GetJobClassItem(Str10[0], 0);
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

    private void CreateWindowGroup() {
        Str10 = new String[3];

        for (int i4 = 0; i4 < 2; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[2] = mTextAreaItem.get_Text();

        int returnValue = mDataArea.CreateWindowGroup(Str10[0], Str10[1], Str10[2]);

        if (returnValue == 0) {
            mWindowGroupItem = mDataArea.GetWindowGroupItem(Str10[0], 0);
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

    private void CreateJobArg() {

        String Str1 = mParentFrame.getCurrentNode().getOwner() + "." +
                      mParentFrame.getCurrentNode().getNodeName();

        mTextItem = mPane.getTextObj(0);

        try {
            int Int2 = Integer.parseInt(mTextItem.get_Text());

            mTextItem = mPane.getTextObj(1);
            String Str3 = mTextItem.get_Text();

            int returnValue = mDataArea.CreateJobArg(Str1, Int2, Str3);

            if (returnValue == 0) {
                if (mDataArea.jobArgExists(
                            mParentFrame.getCurrentNode().getOwner(),
                            mParentFrame.getCurrentNode().getNodeName(),
                            Int2))
                {

                    mJobArgItem = mDataArea.GetJobArgsItem(
                                           mParentFrame.getCurrentNode().getOwner(),
                                           mParentFrame.getCurrentNode().getNodeName(),
                                           Int2, 1);

                    if (mJobArgItem == null) {
                        mParentFrame.errorBox("Error - Cannot read Job Argument from Database.");
                        mDispose = false;
                    }
                }
                else {
                    mJobArgItem = mDataArea.GetJobArgsItem(
                                           mParentFrame.getCurrentNode().getOwner(),
                                           mParentFrame.getCurrentNode().getNodeName(),
                                           Int2, 0);

                    if (mJobArgItem == null) {
                        mParentFrame.errorBox("Error - Cannot read Job Argument from Database.");
                        mDispose = false;
                    }
                    else {
                        mParentFrame.createJobArgsTreeItem(mJobArgItem);
                    }
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        catch(NumberFormatException nfe) {
            mParentFrame.errorBox("Error - Invalid value for Argument Position.");
            mDispose = false;
        }
    }

    private void CreateChain() {
        Str10 = new String[4];

        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[3] = mTextAreaItem.get_Text();
        int returnValue = mDataArea.CreateChain(Str10[0], Str10[1], Str10[2], Str10[3]);

        if (returnValue == 0) {
            mChainsItem = mDataArea.GetChainsItem(
                                        mDataArea.getUserName(),
                                        Str10[0], 0);
            if (mChainsItem == null) {
                mParentFrame.errorBox("Error - Cannot read Chain from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createChainTreeItem(mChainsItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    private void CreateChainRule() {
        Str10 = new String[4];

        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[3] = mTextAreaItem.get_Text();

        if (mParentFrame.getCurrentChainOwner().equals(mDataArea.getUserName())) {
            mChainName = mParentFrame.getCurrentChain();
        }
        else {
            mChainName = mParentFrame.getCurrentChainOwner() + "." + 
                         mParentFrame.getCurrentChain();
        }

        int returnValue = mDataArea.CreateChainRule(
                                   mChainName,
                                   Str10[0],
                                   Str10[1],
                                   Str10[2],
                                   Str10[3]);

        if (returnValue == 0) {
            mChainRulesItem = mDataArea.GetChainRulesItem(
                                  mParentFrame.getCurrentChainOwner(),
                                  mParentFrame.getCurrentChain(),
                                  Str10[0]);

            if (mChainRulesItem == null) {
                mParentFrame.errorBox("Error - Cannot read Chain Rule from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createChainRuleTreeItem(mChainRulesItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }


    private void PurgeLogs() {
        Str10 = new String[3];

        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        if (Str10[1].length() == 0) {
            mParentFrame.errorBox("Error - A Log Type must be selected.");
            mDispose = false;
        }
        else {
            int returnValue = mDataArea.PurgeLog(Str10[0], Str10[1], Str10[2]);

            if (returnValue != 0) {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }

    private void AssignWindow() {

        mTextItem = mPane.getTextObj(0);
        String Str1 = mTextItem.getSelectedItem();

        if (mTextItem.getSelectedIndex() == 0) {
            mParentFrame.errorBox("Error - A Window Group must be selected.");
            mDispose = false;
        }
        else {
            int returnValue = mDataArea.AssignToGroup("SYS" + "." + mParentFrame.getWindowName(),
                                                      "SYS" + "." + Str1);

            if (returnValue == 0) {
                mWinGroupMembersItem = mDataArea.GetWinGroupMemberItem(
                                            Str1,
                                            mParentFrame.getWindowName());
                mParentFrame.createWinGroupAssignTreeItem(
                                            mWinGroupMembersItem);
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }

    public void CreateCredential() {
        Str10 = new String[5];

        for (int i4 = 0; i4 < 4; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[4] = mTextAreaItem.get_Text();

        int returnValue = mDataArea.CreateCredential(Str10[0], Str10[1], Str10[2], Str10[3], Str10[4]);

        if (returnValue == 0) {

            String Str1 = Str10[0];
            if ( Str1.indexOf('.') < 0 ) {

                mCredentialItem = mDataArea.GetCredentialItem(
                                                mDataArea.getUserName(),
                                                Str10[0], 0);
            }
            else {

                String Str2 = Str1.substring(0, Str1.indexOf('.'));
                String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                mCredentialItem = mDataArea.GetCredentialItem(
                                                Str2,
                                                Str3, 0);
            }

            if (mCredentialItem == null) {
                mParentFrame.errorBox("Error - Cannot read Credential from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createCredentialTreeItem(mCredentialItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void CreateFileWatcher() {
        Str10 = new String[5];

        for (int i4 = 0; i4 < 4; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }
        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[4] = mTextAreaItem.get_Text();

        int returnValue = mDataArea.CreateFileWatcher(Str10[0], Str10[1], Str10[2],
                                                      Str10[3], Str10[4]);

        if (returnValue == 0) {

            String Str1 = Str10[0];
            if ( Str1.indexOf('.') < 0 ) {
                mFileWatchersItem = mDataArea.GetFileWatcherItem(mDataArea.getUserName(),
                                                             Str10[0],
                                                             0);
            }
            else {

                String Str2 = Str1.substring(0, Str1.indexOf('.'));
                String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                mFileWatchersItem = mDataArea.GetFileWatcherItem(Str2,
                                                             Str3,
                                                             0);
            }

            if (mFileWatchersItem == null) {
                mParentFrame.errorBox("Error - Cannot read File Watcher from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createFileWatcherTreeItem(mFileWatchersItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    private void CreateNotification() {
        String Str1 = mParentFrame.getCurrentNode().getOwner() + "." +
                      mParentFrame.getCurrentNode().getNodeName();

        Str10 = new String[6];

        for (int i4 = 0; i4 < 6; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if (( mTextItem.get_Combo().equals("Y")) && (mTextItem.get_RowType() == 2))
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        int returnValue = mDataArea.CreateNotification(Str1, Str10[0], Str10[1], Str10[2],
                                                       Str10[3], Str10[4], Str10[5]);

        if (returnValue == 0) {
            mNotificationsItem = mDataArea.GetNotificationsItem(
                                        mParentFrame.getCurrentNode().getOwner(),
                                        mParentFrame.getCurrentNode().getNodeName(),
                                        Str10[0], Str10[1], Str10[4],
                                        mDataArea);
            if (mNotificationsItem == null) {
                mParentFrame.errorBox("Error - Cannot read Notification from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createNotificationTreeItem(mNotificationsItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }

    }

    private void CreateDbDestination() {
        Str10 = new String[4];

        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[3] = mTextAreaItem.get_Text();

        int returnValue = mDataArea.CreateDbDestination(
                                   Str10[0],
                                   Str10[1],
                                   Str10[2],
                                   Str10[3]);

        if (returnValue == 0) {
            String Str1 = Str10[0];
            if ( Str1.indexOf('.') < 0 ) {
                mDbDestsItem = mDataArea.GetDBDestsItem(
                                  mDataArea.getUserName(),
                                  Str1);
            }
            else {
                String Str2 = Str1.substring(0, Str1.indexOf('.'));
                String Str3 = Str1.substring(Str1.indexOf('.') + 1);
                mDbDestsItem = mDataArea.GetDBDestsItem(
                                  Str2,
                                  Str3);
            }
            if (mDbDestsItem == null) {
                mParentFrame.errorBox("Error - Cannot read Database Destination from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createDBDestTreeItem(mDbDestsItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    private void AssignDbDestination() {

        mTextItem = mPane.getTextObj(0);
        String Str1 = mTextItem.getSelectedItem();

        if (mTextItem.getSelectedIndex() == 0) {
            mParentFrame.errorBox("Error - A Destination Group must be selected.");
            mDispose = false;
        }
        else {

            int returnValue = mDataArea.AssignToGroup(Str1, mParentFrame.getGroupName());

            if (returnValue == 0) {
                String mGroupName;
                if (mParentFrame.getGroupName().indexOf('.') < 0)
                    mGroupName = mParentFrame.getGroupName();
                else
                    mGroupName = mParentFrame.getGroupName().substring(
                                     mParentFrame.getGroupName().indexOf('.') + 1);

                if ( Str1.indexOf('.') < 0 ) {

                    mGroupMembersItem = mDataArea.GetGroupMemberItem(
                                                mDataArea.getUserName(),
                                                mGroupName,
                                                Str1);
                }
                else {
                    String Str2 = Str1.substring(0, Str1.indexOf('.'));
                    String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                    mGroupMembersItem = mDataArea.GetGroupMemberItem(
                                                Str2,
                                                mGroupName,
                                                Str3);
                }

                mParentFrame.createDbDestGroupAssignTreeItem(
                                                mGroupMembersItem);
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }

    private void AssignExtDestination() {

        mTextItem = mPane.getTextObj(0);
        String Str1 = mTextItem.getSelectedItem();

        if (mTextItem.getSelectedIndex() == 0) {
            mParentFrame.errorBox("Error - A Destination Group must be selected.");
            mDispose = false;
        }
        else {

            int returnValue = mDataArea.AssignToGroup(Str1, mParentFrame.getGroupName());

            if (returnValue == 0) {
                String mGroupName;
                if (mParentFrame.getGroupName().indexOf('.') < 0)
                    mGroupName = mParentFrame.getGroupName();
                else
                    mGroupName = mParentFrame.getGroupName().substring(
                                     mParentFrame.getGroupName().indexOf('.') + 1);

                if ( Str1.indexOf('.') < 0 ) {
                    mGroupMembersItem = mDataArea.GetGroupMemberItem(
                                                mDataArea.getUserName(),
                                                mGroupName,
                                                Str1);
                    mParentFrame.createExtDestGroupAssignTreeItem(
                                                mGroupMembersItem);
                }
                else {
                    String Str2 = Str1.substring(0, Str1.indexOf('.'));
                    String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                    mGroupMembersItem = mDataArea.GetGroupMemberItem(
                                                Str2,
                                                mGroupName,
                                                Str3);
                    mParentFrame.createExtDestGroupAssignTreeItem(
                                                mGroupMembersItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }

    private void CreateGroup() {

        Str10 = new String[4];

        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[3] = mTextAreaItem.get_Text();

        String lGroupOwner = null;
        String lGroupName = null;
        if ( Str10[0].indexOf('.') < 0 ) {
            if (Str10[1].equals("WINDOW"))
                lGroupOwner = "SYS";
            else
                lGroupOwner = mDataArea.getUserName();
            lGroupName  = Str10[0];
        }
        else {
            lGroupOwner = Str10[0].substring(0, Str10[0].indexOf('.'));
            lGroupName  = Str10[0].substring(Str10[0].indexOf('.') + 1);
        }

        int returnValue = mDataArea.CreateGroup(Str10[0], Str10[1], Str10[2], Str10[3]);

        if (returnValue == 0) {

            mGroupItem = mDataArea.GetGroupItem(lGroupOwner, lGroupName, 0);
            if (mGroupItem == null) {
                mParentFrame.errorBox("Error - Cannot read Window Group from Database.");
                mDispose = false;
            }
            else {
                if ( Str10[1].equals("WINDOW") )
                    mParentFrame.createGroupWindowTreeItem(mGroupItem);
                if ( Str10[1].equals("DB_DEST") )
                    mParentFrame.createGroupDbDestTreeItem(mGroupItem);
                if ( Str10[1].equals("EXTERNAL_DEST") )
                    mParentFrame.createGroupExtDestTreeItem(mGroupItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void CreatePlanDirective() {
        Str10 = new String[5];

        Str10[0] = mPane.getTextObjId(1);
        Str10[1] = mPane.getTextObjId(10);
        Str10[2] = mPane.getTextObjId(12);

        if ( ! Str10[2].equals("TRUE") ) Str10[1] = "FALSE";

        int Int1 = 0;
        int Int2 = 0;
        int Int3 = 0;
        int Int4 = 0;
        int Int5 = 0;
        int Int6 = 0;
        int Int7 = 0;
        int Int8 = 0;
        int Int11 = 0;
        int Int13 = 0;
        int Int14 = 0;
        int Int15 = 0;
        int Int16 = 0;
        int Int17 = 0;
        int Int18 = 0;
        int Int19 = 0;
        int Int20 = 0;
        int Int21 = 0;
        int Int22 = 0;

        if (mPane.getTextObjId(2).length() > 0) Int1 = Integer.parseInt(mPane.getTextObjId(2));
        if (mPane.getTextObjId(3).length() > 0) Int2 = Integer.parseInt(mPane.getTextObjId(3));
        if (mPane.getTextObjId(4).length() > 0) Int3 = Integer.parseInt(mPane.getTextObjId(4));
        if (mPane.getTextObjId(5).length() > 0) Int4 = Integer.parseInt(mPane.getTextObjId(5));
        if (mPane.getTextObjId(6).length() > 0) Int5 = Integer.parseInt(mPane.getTextObjId(6));
        if (mPane.getTextObjId(7).length() > 0) Int6 = Integer.parseInt(mPane.getTextObjId(7));
        if (mPane.getTextObjId(8).length() > 0) Int7 = Integer.parseInt(mPane.getTextObjId(8));
        if (mPane.getTextObjId(9).length() > 0) Int8 = Integer.parseInt(mPane.getTextObjId(9));
        if (mPane.getTextObjId(11).length() > 0) Int11 = Integer.parseInt(mPane.getTextObjId(11));
        if (mPane.getTextObjId(13).length() > 0) Int13 = Integer.parseInt(mPane.getTextObjId(13));
        if (mPane.getTextObjId(14).length() > 0) Int14 = Integer.parseInt(mPane.getTextObjId(14));
        if (mPane.getTextObjId(15).length() > 0) Int15 = Integer.parseInt(mPane.getTextObjId(15));
        if (mPane.getTextObjId(16).length() > 0) Int16 = Integer.parseInt(mPane.getTextObjId(16));
        if (mPane.getTextObjId(17).length() > 0) Int17 = Integer.parseInt(mPane.getTextObjId(17));
        if (mPane.getTextObjId(18).length() > 0) Int18 = Integer.parseInt(mPane.getTextObjId(18));
        if (mPane.getTextObjId(19).length() > 0) Int19 = Integer.parseInt(mPane.getTextObjId(19));

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[4] = mTextAreaItem.get_Text();

        int returnValue = 0;
        if (mDataArea.getVersionNo() < 3) {
            returnValue = mDataArea.CreateResourceDirective10g(
                                        mParentFrame.getCurrentNode().getNodeName(),
                                        Str10[0], Int1, Int2, Int3, Int4, Int5, Int6, Int7, Int8,
                                        Str10[1], Int11, Str10[2],
                                        Int13, Int14, Int15, Int16, Int17, Int18, Int19, Str10[4]);
        }
        else {
            if (mPane.getTextObjId(20).length() > 0) Int20 = Integer.parseInt(mPane.getTextObjId(20));
            if (mPane.getTextObjId(21).length() > 0) Int21 = Integer.parseInt(mPane.getTextObjId(21));
            if (mPane.getTextObjId(22).length() > 0) Int22 = Integer.parseInt(mPane.getTextObjId(22));

            Str10[3] = mPane.getTextObjId(22);
            if ( ! Str10[3].equals("TRUE") ) Str10[2] = "FALSE";

            returnValue = mDataArea.CreateResourceDirective11g(
                                        mParentFrame.getCurrentNode().getNodeName(),
                                        Str10[0], Int1, Int2, Int3, Int4, Int5, Int6, Int7, Int8,
                                        Str10[1], Int11, Str10[2],
                                        Int13, Int14, Int15, Int16, Int17, Int18, Int19,
                                        Int20, Int21, Str10[3], Int22, Str10[4]);
        }

        if (returnValue == 0) {
            mPlanDirectiveItem = mDataArea.GetPlanDirectiveItem(mParentFrame.getCurrentNode().getNodeName(),
                                                                Str10[0], 0);
            if (mPlanDirectiveItem == null) {
                mParentFrame.errorBox("Error - Cannot read Plan Directive from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.addPlanDirectiveItem(mPlanDirectiveItem);
            }
        }
        else {
            // System.out.println(" Error ");
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }


    public void CreateCdbPlanDirective() {
        Str10 = new String[2];

        mTextItem = mPane.getTextItemId(1);
        Str10[0] = mTextItem.getSelectedItem();

        int Int1 = 0;
        int Int2 = 0;
        int Int3 = 0;
        if (mPane.getTextObjId(2).length() > 0) Int1 = Integer.parseInt(mPane.getTextObjId(2));
        if (mPane.getTextObjId(3).length() > 0) Int2 = Integer.parseInt(mPane.getTextObjId(3));
        if (mPane.getTextObjId(4).length() > 0) Int3 = Integer.parseInt(mPane.getTextObjId(4));

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[1] = mTextAreaItem.get_Text();

        int returnValue = 0;
        // System.out.println(" CDB 1 - " + mParentFrame.getCurrentNode().getNodeName() + "--" +
        //                    Str10[0] + "--" + Int1 + "--" + Int2 + "--" + Int3 + "--" + Str10[1]);
        returnValue = mDataArea.CreateCdbResourceDirective(
                                        mParentFrame.getCurrentNode().getNodeName(),
                                        Str10[0], Int1, Int2, Int3, Str10[1]);

        if (returnValue == 0) {
            mCdbPlanDirectiveItem = mDataArea.GetCdbPlanDirectiveItem(
                                            mParentFrame.getCurrentNode().getNodeName(),
                                            Str10[0], 0);
            if (mCdbPlanDirectiveItem == null) {
                mParentFrame.errorBox("Error - Cannot read CDB Plan Directive from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.addCdbPlanDirectiveItem(mCdbPlanDirectiveItem);
            }
        }
        else {
            // System.out.println(" Error ");
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void CreateResourcePlan() {
        Str10 = new String[3];

        for (int i4 = 0; i4 < 2; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[2] = mTextAreaItem.get_Text();

        int returnValue = mDataArea.CreateResourcePlan(Str10[0], Str10[1], Str10[2]);

        if (returnValue == 0) {

            mPlanItem = mDataArea.GetPlanItem(Str10[0], 0);
            if (mPlanItem == null) {
                mParentFrame.errorBox("Error - Cannot read Resource Plan from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createResourcePlanTreeItem(mPlanItem);
            }
        }
        else {
            // System.out.println(" Error ");
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }

    }

    public void CreateCdbResourcePlan() {
        Str10 = new String[2];

        mTextItem = mPane.getTextObj(0);
        Str10[0] = mTextItem.get_Text();

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[1] = mTextAreaItem.get_Text();

        int returnValue = mDataArea.CreateCdbResourcePlan(Str10[0], Str10[1]);

        if (returnValue == 0) {

            mCdbPlanItem = mDataArea.GetCdbPlanItem(Str10[0], 0);
            if (mCdbPlanItem == null) {
                mParentFrame.errorBox("Error - Cannot read CDB Resource Plan from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createCdbResourcePlanTreeItem(mCdbPlanItem);

                mCdbPlanDirectiveItem = mDataArea.GetCdbPlanDirectiveItem(
                                                mCdbPlanItem.getPlan(),
                                                "ORA$AUTOTASK", 0);

                mCdbPlanDirectiveItem = mDataArea.GetCdbPlanDirectiveItem(
                                                mCdbPlanItem.getPlan(),
                                                "ORA$DEFAULT_PDB_DIRECTIVE", 0);
            }
        }
        else {
            // System.out.println(" Error ");
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }

    }

    public void CreateConsumerGroup() {
        Str10 = new String[3];

        mTextItem = mPane.getTextObj(0);
        Str10[0] = mTextItem.get_Text();

        mTextAreaItem = mPane.getTextAreaObj(0);
        Str10[1] = mTextAreaItem.get_Text();

        // System.out.println(" Consumer Group " + Str10[0] + "--" + Str10[1]);

        int returnValue = mDataArea.CreateConsumerGroup(Str10[0], Str10[1]);

        if (returnValue == 0) {
            mConsumerGroupItem = mDataArea.GetConsumerGroupItem(Str10[0], 0);
            if (mConsumerGroupItem == null) {
                mParentFrame.errorBox("Error - Cannot read Consumer Group from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createConsumerGroupTreeItem(mConsumerGroupItem);
            }
        }
        else {
            // System.out.println(" Error ");
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }

    }

    public void CreateGroupMapping() {
        Str10 = new String[3];
        for (int i4 = 0; i4 < 2; i4++) {
            mTextItem = mPane.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        int returnValue = mDataArea.SetConsumerGroupMapping(
                        mParentFrame.getCurrentNode().getNodeName(),
                        Str10[0], Str10[1]);
        if (returnValue == 0) {
            mGroupMappingsItem = mDataArea.GetGroupMappingsItem(Str10[0], Str10[1], 0);
            if (mGroupMappingsItem == null) {
                mParentFrame.errorBox("Error - Cannot read Group Mapping from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.addGroupMappingItem(mGroupMappingsItem);
            }
        }
        else {
            // System.out.println(" Error ");
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void CreateSwitchUser() {
        mTextItem = mPane.getTextObj(0);
        String Str1 = mTextItem.getSelectedItem();

        // System.out.println(" Group " + Str1.length() + "--" + Str1 + "--");

        if (mTextItem.getSelectedIndex() == 0) {
            mParentFrame.errorBox("Error - A Consumer Group must be selected.");
            mDispose = false;
        }
        else {
            int returnValue = mDataArea.SwitchConsumerGroupUsers(mParentFrame.getUsername(), Str1);

            if (returnValue != 0) {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }
    public void CreateSwitchSession() {
        mTextItem = mPane.getTextObj(0);
        String Str1 = mTextItem.getSelectedItem();

        // System.out.println(" Group " + Str1.length() + "--" + Str1 + "--");

        if (mTextItem.getSelectedIndex() == 0) {
            mParentFrame.errorBox("Error - A Consumer Group must be selected.");
            mDispose = false;
        }
        else {
            int returnValue = mDataArea.SwitchConsumerGroupSession(
                                        mParentFrame.getSid(),
                                        mParentFrame.getSerial(), Str1);

            if (returnValue != 0) {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }

    public void CreateConsumerPriv() {

        mTextItem = mPane.getTextObj(0);
        int returnValue = mDataArea.SetConsumerPrivilege(
                        mParentFrame.getCurrentNode().getNodeName(),
                        mTextItem.getText());

        if (returnValue == 0) {
            mConsumerPrivItem = mDataArea.GetConsumerPrivItem(
                                           mDataArea,
                                           mParentFrame.getCurrentNode().getNodeName(),
                                           mTextItem.getText());
            if (mConsumerPrivItem == null) {
                mParentFrame.errorBox("Error - Cannot read Consumer Privilege from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.addConsumerPrivItem(mConsumerPrivItem);
            }
        }
        else {
            // System.out.println(" Error ");
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void actionPerformed( ActionEvent e ) {

        if ( e.getActionCommand().equals("OK") ) {
            mDispose = true;

            mDataArea.setReturnNo(0);
            switch (mScreenNo) {
                case PROGRAM_SCREEN_NO:
                    CreateProgram();
                    break;
                case JOB_CLASS_SCREEN_NO:
                    CreateJobClass();
                    break;
                case WINDOW_GROUP_SCREEN_NO:
                    CreateWindowGroup();
                    break;
                case JOB_ARGS_SCREEN_NO:
                    CreateJobArg();
                    break;
                case CHAIN_SCREEN_NO:
                    CreateChain();
                    break;
                case CHAIN_RULE_SCREEN_NO:
                    CreateChainRule();
                    break;
                case PURGE_LOGS_SCREEN_NO:
                    PurgeLogs();
                    break;
                case ASSIGN_WINDOW_SCREEN_NO:
                    AssignWindow();
                    break;
                case CREDENTIAL_SCREEN_NO:
                    CreateCredential();
                    break;
                case FILE_WATCHER_SCREEN_NO:
                    CreateFileWatcher();
                    break;
                case NOTIFICATION_SCREEN_NO:
                    CreateNotification();
                    break;
                case DBDEST_SCREEN_NO:
                    CreateDbDestination();
                    break;
                case ASSIGN_DBDEST_SCREEN_NO:
                    AssignDbDestination();
                    break;
                case ASSIGN_EXTDEST_SCREEN_NO:
                    AssignExtDestination();
                    break;
                case GROUP_SCREEN_NO:
                    CreateGroup();
                    break;
                case PLAN_DIRECTIVE_SCREEN_NO:
                    CreatePlanDirective();
                    break;
                case CDB_PLAN_DIRECTIVE_SCREEN_NO:
                    CreateCdbPlanDirective();
                    break;
                case RESOURCE_PLAN_SCREEN_NO:
                    CreateResourcePlan();
                    break;
                case CDB_RESOURCE_PLAN_SCREEN_NO:
                    CreateCdbResourcePlan();
                    break;
                case CONSUMER_GROUP_SCREEN_NO:
                    CreateConsumerGroup();
                    break;
                case GROUP_MAPPING_SCREEN_NO:
                    CreateGroupMapping();
                    break;
                case SWITCH_USER_SCREEN_NO:
                    CreateSwitchUser();
                    break;
                case SWITCH_SESSION_SCREEN_NO:
                    CreateSwitchSession();
                    break;
                case ASSIGN_PRIVILEGE_SCREEN_NO:
                    CreateConsumerPriv();
                    break;
            }
            if (mDispose) {
                dispose();
            }
        }
        if ( e.getActionCommand().equals("Exit") ) {
            mDataArea.setReturnNo(1);
            dispose();
        }
        if ( e.getActionCommand().startsWith("Edit") ) {
            mItemNo = Integer.parseInt( e.getActionCommand().substring(4) );

            SchedInpScreenArea.PaneObject.TextItem m_T1 = mPane.getTextObj(mItemNo);

            if (m_T1.get_RowType() == 5) {
                SchedEventInputScreen eventDialog = new SchedEventInputScreen(this, m_T1);

                eventDialog.setVisible( true );
            }
            else {
                SchedDataInputScreen dataDialog = 
                        new SchedDataInputScreen(this, m_T1 );
                dataDialog.setVisible( true );
            }
        }
    }
}

