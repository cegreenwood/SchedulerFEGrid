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

class SchedJobInputScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton                             mEnter, mCancel, mLeft, mRight;
    private JTabbedPane                         mTabPane;
    private JPanel                              dataPane, dataPanel;
    private JPanel                              dataPane500, dataPane501, dataPane502;
    private JPanel                              dataPane503, dataPane504, dataPane505;

    private SchedInpScreenArea                  mInpScreens;
    private SchedInpScreenArea.PaneObject       mPane;
    private SchedInpScreenArea.PaneObject       mPane500, mPane501, mPane502;
    private SchedInpScreenArea.PaneObject       mPane503, mPane504, mPane505;
    private SchedGlobalData                     mArea;

    private int                                 currentScreenId, oldScreenId;
    private int                                 returnValue;
    private int                                 mItemNo;
    private String[]                            Str10;

    private SchedInpScreenArea.PaneObject.TextItem       mTextItem;
    private SchedInpScreenArea.PaneObject.TextAreaItem   mTextAreaItem;
    private SchedInpScreenArea.PaneObject.DateTimeItem   mDateTimeItem;

    private Scheduler                           mParentFrame;
    private SchedDataArea                       mDataArea;
    private boolean                             mDispose;
    private SchedDataArea.JobItem               mJobItem;

    private ClassLoader  cl;

    public SchedJobInputScreen(Scheduler           parentFrame,
                               SchedDataArea       dataArea,
                               SchedGlobalData     Area,
                               SchedInpScreenArea  InpScreens)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        mParentFrame = parentFrame;
        mDataArea = dataArea;
        mInpScreens = InpScreens;
        mArea = Area;
        mDataArea.setReturnNo(1);

        // setTitle( "Scheduler FE Grid" );
        if (mDataArea.getVersionNo() < 4) setSize( 620, 460 );
        else                              setSize( 620, 510 );

        setDefaultCloseOperation( DISPOSE_ON_CLOSE );

        this.setLocation((int)mParentFrame.getLocationOnScreen().getX() + 160,
                         (int)mParentFrame.getLocationOnScreen().getY() + 100);

        dataPane = new JPanel();
        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        dataPane.setBackground(mArea.getScreenColor(42));

        SpringLayout lm = new SpringLayout();
        dataPane.setLayout(lm);

        UIManager.put("TabbedPane.selected", mArea.getScreenColor(42));
        mTabPane = new JTabbedPane();
        mTabPane.setBackground(mArea.getScreenColor(42));

        dataPane500 = new JPanel();
        dataPane500 = setupPane(500);
        mTabPane.add("Option 1", dataPane500);
        mTabPane.setMnemonicAt(0,KeyEvent.VK_1);
        mTabPane.setToolTipTextAt(0,"Creating a Standard Job.");
        mTabPane.setBackgroundAt(0, mArea.getScreenColor(42));

        dataPane501 = new JPanel();
        dataPane501 = setupPane(501);
        mTabPane.add("Option 2", dataPane501);
        mTabPane.setMnemonicAt(1,KeyEvent.VK_2);
        mTabPane.setToolTipTextAt(1,"Creating a Job With a Named Program and Named Schedule.");
        mTabPane.setBackgroundAt(1, mArea.getScreenColor(42));

        dataPane502 = new JPanel();
        dataPane502 = setupPane(502);
        mTabPane.add("Option 3", dataPane502);
        mTabPane.setMnemonicAt(2,KeyEvent.VK_3);
        mTabPane.setToolTipTextAt(2,"Creating a Job With a Named Program and Inline Schedule.");
        mTabPane.setBackgroundAt(2, mArea.getScreenColor(42));

        dataPane503 = new JPanel();
        dataPane503 = setupPane(503);
        mTabPane.add("Option 4", dataPane503);
        mTabPane.setMnemonicAt(3,KeyEvent.VK_4);
        mTabPane.setToolTipTextAt(3,"Creating a Job With a Named Schedule and Inline Program.");
        mTabPane.setBackgroundAt(3, mArea.getScreenColor(42));

        if (mDataArea.getVersionNo() > 1) {
            dataPane504 = new JPanel();
            dataPane504 = setupPane(504);
            mTabPane.add("Option 5", dataPane504);
            mTabPane.setMnemonicAt(4,KeyEvent.VK_5);
            mTabPane.setToolTipTextAt(4,"Creating a Job With an Inline Program and Event.");
            mTabPane.setBackgroundAt(4, mArea.getScreenColor(42));

            dataPane505 = new JPanel();
            dataPane505 = setupPane(505);
            mTabPane.add("Option 6", dataPane505);
            mTabPane.setMnemonicAt(5,KeyEvent.VK_6);
            mTabPane.setToolTipTextAt(5,"Creating a Job With a Named Program and Event.");
            mTabPane.setBackgroundAt(5, mArea.getScreenColor(42));
        }

        int lHeight = 0;
        if (mDataArea.getVersionNo() < 4) 
            lHeight = 370;
        else
            lHeight = 420;

        dataPane.add(mTabPane,
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(0),
                                                 Spring.constant(606),
                                                 Spring.constant(lHeight)));



        dataPane.add(setupButtons(),
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(lHeight),
                                                 Spring.constant(606),
                                                 Spring.constant(50)));;

        getContentPane().add( dataPane );
    }

    private JPanel setupPane(int mScreenNo) {

        int mScreenId = mInpScreens.getScreenId(mScreenNo, mDataArea.getVersion());
        mPane = mInpScreens.getScreen(mScreenId);

        switch (mScreenNo) {
            case 500:
                mPane500 = mInpScreens.getScreen(mScreenId);
                break;
            case 501:
                mPane501 = mInpScreens.getScreen(mScreenId);
                break;
            case 502:
                mPane502 = mInpScreens.getScreen(mScreenId);
                break;
            case 503:
                mPane503 = mInpScreens.getScreen(mScreenId);
                break;
            case 504:
                mPane504 = mInpScreens.getScreen(mScreenId);
                break;
            case 505:
                mPane505 = mInpScreens.getScreen(mScreenId);
                break;
        }

        Border blackline = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

        dataPanel = new JPanel();
        if (mDataArea.getVersionNo() < 4) dataPanel.setSize( 606, 380 );
        else                              dataPanel.setSize( 606, 430 );

        dataPanel.setBorder(blackline);
        dataPanel.setLayout(new SpringLayout());

        dataPanel.setBackground(mArea.getScreenColor(mPane.getBgrndColour()));
        dataPanel.setForeground(mArea.getScreenColor(mPane.getFgrndColour()));

        cl = this.getClass().getClassLoader();
        Icon LookIcon1 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook1.jpg"));
        Icon LookIcon2 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook2.jpg"));

        // Loop through all the label objects.
        for (int i2 = 0; i2 < mPane.sizeLabelObj(); i2++) {

            SchedInpScreenArea.PaneObject.LabelItem m_P1 = mPane.getLabelObj(i2);
            dataPanel.add(m_P1,
                        new SpringLayout.Constraints(Spring.constant(m_P1.get_XPoint()),
                                                 Spring.constant(m_P1.get_YPoint()),
                                                 Spring.constant(m_P1.get_Width()),
                                                 Spring.constant(m_P1.get_Height())));
        }

        // Loop through all the text objects.
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

        // Loop through all the textArea objects.
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
        dataPane3.setSize( 606, 60 );
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

    public void CreateJob1() {
        Str10 = new String[11];
        if (mDataArea.getVersionNo() < 4) {
            for (int i4 = 0; i4 < 6; i4++) {
                mTextItem = mPane500.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }
        else {
            for (int i4 = 0; i4 < 8; i4++) {
                mTextItem = mPane500.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }

        mDateTimeItem = mPane500.getDateTimeObj(0);
        Str10[8] = mDateTimeItem.getDateString();

        mDateTimeItem = mPane500.getDateTimeObj(1);
        Str10[9] = mDateTimeItem.getDateString();

        mTextAreaItem = mPane500.getTextAreaObj(0);
        Str10[10] = mTextAreaItem.get_Text();

        try {
            int Int3 = 0;
            if (Str10[5].length() > 0) {
                Int3 = Integer.parseInt(Str10[5]);
            }

            int returnValue;
            if (mDataArea.getVersionNo() < 4) {
                returnValue = mDataArea.CreateJob1(
                            Str10[0], Str10[1], Str10[2], Int3,
                            Str10[8], Str10[3], Str10[9], Str10[4],
                            Str10[10], "", "" );
            }
            else {
                returnValue = mDataArea.CreateJob1(
                            Str10[0], Str10[1], Str10[2], Int3,
                            Str10[8], Str10[3], Str10[9], Str10[4],
                            Str10[10], Str10[6], Str10[7] );
            }

            if (returnValue == 0) {

                String Str1 = Str10[0];
                if ( Str1.indexOf('.') < 0 ) {
                    mJobItem = mDataArea.GetJobItem(
                                    mDataArea.getUserName(),
                                    Str10[0], 0);
                }
                else {

                    String Str2 = Str1.substring(0, Str1.indexOf('.'));
                    String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                    mJobItem = mDataArea.GetJobItem(
                                    Str2,
                                    Str3, 0);
                }

                if (mJobItem == null) {
                    mParentFrame.errorBox("Error - Cannot read new Job from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createJobTreeItem(mJobItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        catch(NumberFormatException nfe) {
            mParentFrame.errorBox("Error - Invalid value for No. of Arguments.");
            mDispose = false;
        }
    }

    public void CreateJob2() {

        Str10 = new String[7];
        if (mDataArea.getVersionNo() < 4) {
            for (int i4 = 0; i4 < 4; i4++) {
                mTextItem = mPane501.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }
        else {
            for (int i4 = 0; i4 < 6; i4++) {
                mTextItem = mPane501.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }

        mTextAreaItem = mPane501.getTextAreaObj(0);
        Str10[6] = mTextAreaItem.get_Text();

        int returnValue;
        if (mDataArea.getVersionNo() < 4) {
            returnValue = mDataArea.CreateJob2(Str10[0], Str10[1], Str10[2],
                                                  Str10[3], Str10[6], "", "");
        }
        else {
            returnValue = mDataArea.CreateJob2(Str10[0], Str10[1], Str10[2],
                                                  Str10[3], Str10[6], Str10[4], Str10[5]);
        }

        if (returnValue == 0) {
            String Str1 = Str10[0];
            if ( Str1.indexOf('.') < 0 ) {
                mJobItem = mDataArea.GetJobItem(
                                     mDataArea.getUserName(),
                                     Str10[0], 0);
            }
            else {

                String Str2 = Str1.substring(0, Str1.indexOf('.'));
                String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                mJobItem = mDataArea.GetJobItem(
                                    Str2,
                                    Str3, 0);
            }

            if (mJobItem == null) {
                mParentFrame.errorBox("Error - Cannot read new Job from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createJobTreeItem(mJobItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void CreateJob3() {
        Str10 = new String[9];
        if (mDataArea.getVersionNo() < 4) {
            for (int i4 = 0; i4 < 4; i4++) {
                mTextItem = mPane502.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }
        else {
            for (int i4 = 0; i4 < 6; i4++) {
                mTextItem = mPane502.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }

        mDateTimeItem = mPane502.getDateTimeObj(0);
        Str10[6] = mDateTimeItem.getDateString();

        mDateTimeItem = mPane502.getDateTimeObj(1);
        Str10[7] = mDateTimeItem.getDateString();

        mTextAreaItem = mPane502.getTextAreaObj(0);
        Str10[8] = mTextAreaItem.get_Text();

        int returnValue;
        if (mDataArea.getVersionNo() < 4) {
            returnValue = mDataArea.CreateJob3(Str10[0], Str10[1], Str10[6],
                                Str10[2], Str10[7], Str10[3], Str10[8], "", "");
        }
        else {
            returnValue = mDataArea.CreateJob3(Str10[0], Str10[1], Str10[6],
                                Str10[2], Str10[7], Str10[3], Str10[8], Str10[4], Str10[5]);
        }

        if (returnValue == 0) {
            String Str1 = Str10[0];
            if ( Str1.indexOf('.') < 0 ) {
                mJobItem = mDataArea.GetJobItem(
                                      mDataArea.getUserName(),
                                      Str10[0], 0);
            }
            else {

                String Str2 = Str1.substring(0, Str1.indexOf('.'));
                String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                mJobItem = mDataArea.GetJobItem(
                                    Str2,
                                    Str3, 0);
            }

            if (mJobItem == null) {
                mParentFrame.errorBox("Error - Cannot read new Job from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createJobTreeItem(mJobItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void CreateJob4() {
        Str10 = new String[9];
        if (mDataArea.getVersionNo() < 4) {
            for (int i4 = 0; i4 < 6; i4++) {
                mTextItem = mPane503.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }
        else {
            for (int i4 = 0; i4 < 8; i4++) {
                mTextItem = mPane503.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }

        mTextAreaItem = mPane503.getTextAreaObj(0);
        Str10[8] = mTextAreaItem.get_Text();

        try {
            int Int3 = 0;
            if (Str10[5].length() > 0) {
                Int3 = Integer.parseInt(Str10[5]);
            }

            int returnValue;
            if (mDataArea.getVersionNo() < 4) {
                returnValue = mDataArea.CreateJob4(Str10[0], Str10[1], Str10[2],
                                    Str10[3], Int3, Str10[4], Str10[8], "", "");
            }
            else {
                returnValue = mDataArea.CreateJob4(Str10[0], Str10[1], Str10[2],
                                    Str10[3], Int3, Str10[4], Str10[8], Str10[6], Str10[7]);
            }

            if (returnValue == 0) {
                String Str1 = Str10[0];
                if ( Str1.indexOf('.') < 0 ) {
                    mJobItem = mDataArea.GetJobItem(
                                     mDataArea.getUserName(),
                                     Str10[0], 0);
                }
                else {

                    String Str2 = Str1.substring(0, Str1.indexOf('.'));
                    String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                    mJobItem = mDataArea.GetJobItem(
                                    Str2,
                                    Str3, 0);
                }

                if (mJobItem == null) {
                    mParentFrame.errorBox("Error - Cannot read new Job from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createJobTreeItem(mJobItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        catch(NumberFormatException nfe) {
            mParentFrame.errorBox("Error - Invalid value for No. of Arguments.");
            mDispose = false;
        }
    }

    public void CreateJob5() {
        Str10 = new String[14];

        if (mDataArea.getVersionNo() < 4) {
            for (int i4 = 0; i4 < 7; i4++) {
                mTextItem = mPane504.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }
        else {
            for (int i4 = 0; i4 < 9; i4++) {
                mTextItem = mPane504.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }

        mDateTimeItem = mPane504.getDateTimeObj(0);
        Str10[9] = mDateTimeItem.getDateString();

        mDateTimeItem = mPane504.getDateTimeObj(1);
        Str10[10] = mDateTimeItem.getDateString();

        mTextAreaItem = mPane504.getTextAreaObj(0);
        Str10[11] = mTextAreaItem.get_Text();

        try {
            int Int3 = 0;
            if (Str10[8].length() > 0) {
                Int3 = Integer.parseInt(Str10[8]);
            }

            int returnValue;
            if (mDataArea.getVersionNo() < 4) {
                returnValue = mDataArea.CreateJob5(Str10[0], Str10[1], Str10[2], Int3,
                         Str10[9], Str10[3], Str10[4], Str10[10], Str10[5],
                         Str10[11], "", "");
            }
            else {
                returnValue = mDataArea.CreateJob5(Str10[0], Str10[1], Str10[2], Int3,
                         Str10[9], Str10[3], Str10[4], Str10[10], Str10[5],
                         Str10[11], Str10[6], Str10[7]);
            }

            if (returnValue == 0) {
                String Str1 = Str10[0];
                if ( Str1.indexOf('.') < 0 ) {
                    mJobItem = mDataArea.GetJobItem(
                                           mDataArea.getUserName(),
                                           Str10[0], 0);
                }
                else {

                    String Str2 = Str1.substring(0, Str1.indexOf('.'));
                    String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                    mJobItem = mDataArea.GetJobItem(
                                    Str2,
                                    Str3, 0);
                }

                if (mJobItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Job from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createJobTreeItem(mJobItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
        catch(NumberFormatException nfe) {
            mParentFrame.errorBox("Error - Invalid value for No. of Arguments.");
            mDispose = false;
        }
    }

    public void CreateJob6() {
        Str10 = new String[13];

        if (mDataArea.getVersionNo() < 4) {
            for (int i4 = 0; i4 < 7; i4++) {
                mTextItem = mPane505.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }
        else {
            for (int i4 = 0; i4 < 9; i4++) {
                mTextItem = mPane505.getTextObj(i4);
                if ( mTextItem.get_Combo().equals("Y") ) {
                    Str10[i4] = mTextItem.getSelectedItem();
                }
                else {
                    Str10[i4] = mTextItem.get_Text();
                }
            }
        }

        mDateTimeItem = mPane505.getDateTimeObj(0);
        Str10[9] = mDateTimeItem.getDateString();

        mDateTimeItem = mPane505.getDateTimeObj(1);
        Str10[10] = mDateTimeItem.getDateString();

        mTextAreaItem = mPane505.getTextAreaObj(0);
        Str10[11] = mTextAreaItem.get_Text();

        int returnValue;
        if (mDataArea.getVersionNo() < 4) {
            returnValue = mDataArea.CreateJob6(Str10[0], Str10[1], Str10[9], Str10[2],
                                       Str10[3], Str10[10], Str10[4], Str10[11],
                                       "", "");
        }
        else {
            returnValue = mDataArea.CreateJob6(Str10[0], Str10[1], Str10[9], Str10[2],
                                       Str10[3], Str10[10], Str10[4], Str10[11],
                                       Str10[7], Str10[8]);
        }

        if (returnValue == 0) {
            String Str1 = Str10[0];
            if ( Str1.indexOf('.') < 0 ) {
                mJobItem = mDataArea.GetJobItem(
                                     mDataArea.getUserName(),
                                     Str10[0], 0);
            }
            else {

                String Str2 = Str1.substring(0, Str1.indexOf('.'));
                String Str3 = Str1.substring(Str1.indexOf('.') + 1);

                mJobItem = mDataArea.GetJobItem(
                                    Str2,
                                    Str3, 0);
            }

            if (mJobItem == null) {
                mParentFrame.errorBox("Error - Cannot read Job from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createJobTreeItem(mJobItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }


    public void actionPerformed( ActionEvent e ) {
        if ( e.getActionCommand().equals("OK") ) {
            mDispose = true;
            switch ( mTabPane.getSelectedIndex() ) {
                case 0:
                    CreateJob1();
                    break;
                case 1:
                    CreateJob2();
                    break;
                case 2:
                    CreateJob3();
                    break;
                case 3:
                    CreateJob4();
                    break;
                case 4:
                    CreateJob5();
                    break;
                case 5:
                    CreateJob6();
                    break;
            }
            mDataArea.setReturnNo(0);
            if (mDispose) {
                dispose();
            }
        }
        if ( e.getActionCommand().equals("Exit") ) {
            mDataArea.setReturnNo(1);
            dispose();
        }
        if ( e.getActionCommand().startsWith("Edit") ) {
            // String str1 = e.getActionCommand().substring(4);
            mItemNo = Integer.parseInt( e.getActionCommand().substring(4) );

            switch ( mTabPane.getSelectedIndex() ) {
                case 0:
                    mTextItem = mPane500.getTextObj(mItemNo);
                    break;
                case 1:
                    mTextItem = mPane501.getTextObj(mItemNo);
                    break;
                case 2:
                    mTextItem = mPane502.getTextObj(mItemNo);
                    break;
                case 3:
                    mTextItem = mPane503.getTextObj(mItemNo);
                    break;
                case 4:
                    mTextItem = mPane504.getTextObj(mItemNo);
                    break;
                case 5:
                    mTextItem = mPane505.getTextObj(mItemNo);
                    break;
            }
            SchedDataInputScreen dataDialog = 
                    new SchedDataInputScreen(this, mTextItem );
            dataDialog.setVisible( true );

        }
    }
}

