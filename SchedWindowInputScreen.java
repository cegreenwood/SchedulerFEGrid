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

class SchedWindowInputScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private int                             mBgrdColorNo;
    private JButton                         mEnter, mCancel, mLeft, mRight;
    private JTabbedPane                     mTabPane;
    private JPanel                          dataPane, dataPanel;
    private JPanel                          dataPane510, dataPane511;
    private SchedInpScreenArea              mInpScreens;
    private SchedInpScreenArea.PaneObject   mPane, mPane510, mPane511;
    private SchedGlobalData                 mArea;
    private int                             currentScreenId, oldScreenId;
    private int                             mItemNo;

    private SchedInpScreenArea.PaneObject.TextItem       mTextItem;
    private SchedInpScreenArea.PaneObject.TextAreaItem   mTextAreaItem;
    private SchedInpScreenArea.PaneObject.DateTimeItem   mDateTimeItem;

    private Scheduler                       mParentFrame;
    private SchedDataArea                   mDataArea;
    private boolean                         mDispose;
    private String[]                        Str10;
    private SchedDataArea.WindowItem        mWindowItem;

    private ClassLoader  cl;

    public SchedWindowInputScreen(Scheduler           parentFrame,
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
        setSize( 620, 460 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        // this.setLocation(150,100);
        this.setLocation((int)mParentFrame.getLocationOnScreen().getX() + 160,
                         (int)mParentFrame.getLocationOnScreen().getY() + 100);

        dataPane = new JPanel();

        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        dataPane.setBackground(mArea.getScreenColor(42));
        SpringLayout lm = new SpringLayout();
        dataPane.setLayout(lm);

        UIManager.put("TabbedPane.selected", mArea.getScreenColor(42));
        mTabPane = new JTabbedPane();

        dataPane510 = new JPanel();
        dataPane510 = setupPane(510);
        mTabPane.add("Option 1", dataPane510);
        mTabPane.setMnemonicAt(0,KeyEvent.VK_1);
        mTabPane.setToolTipTextAt(0,"Creating a Window With a Named Schedule.");
        mTabPane.setBackgroundAt(0, mArea.getScreenColor(42));

        dataPane511 = new JPanel();
        dataPane511 = setupPane(511);
        mTabPane.add("Option 2", dataPane511);
        mTabPane.setMnemonicAt(1,KeyEvent.VK_2);
        mTabPane.setToolTipTextAt(1,"Creating a Window With an Inline Schedule.");
        mTabPane.setBackgroundAt(1, mArea.getScreenColor(42));


        dataPane.add(mTabPane,
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(0),
                                                 Spring.constant(606),
                                                 Spring.constant(370)));



        dataPane.add(setupButtons(),
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(370),
                                                 Spring.constant(606),
                                                 Spring.constant(50)));;

        getContentPane().add( dataPane );
    }

    private JPanel setupPane(int mScreenNo) {

        int mScreenId = mInpScreens.getScreenId(mScreenNo, mDataArea.getVersion());
        mPane = mInpScreens.getScreen(mScreenId);

        if (mScreenNo == 510) {
            mPane510 = mInpScreens.getScreen(mScreenId);
        }
        else {
            mPane511 = mInpScreens.getScreen(mScreenId);
        }

        // Border blackline = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        dataPanel = new JPanel();
        dataPanel.setSize( 620, 380 );
        // dataPanel.setBorder(blackline);
        dataPanel.setLayout(new SpringLayout());

        dataPanel.setBackground(mArea.getScreenColor(mPane.getBgrndColour()));
        dataPanel.setForeground(mArea.getScreenColor(mPane.getFgrndColour()));

        cl = this.getClass().getClassLoader();
        Icon LookIcon1 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook1.jpg"));
        Icon LookIcon2 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook2.jpg"));

        for (int i2 = 0; i2 < mPane.sizeLabelObj(); i2++) {

            SchedInpScreenArea.PaneObject.LabelItem m_P1 = mPane.getLabelObj(i2);
            dataPanel.add(m_P1,
                        new SpringLayout.Constraints(Spring.constant(m_P1.get_XPoint()),
                                                 Spring.constant(m_P1.get_YPoint()),
                                                 Spring.constant(m_P1.get_Width()),
                                                 Spring.constant(m_P1.get_Height())));
        }

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

                dataPanel.add(m_T1,
                        new SpringLayout.Constraints(Spring.constant(m_T1.get_XPoint()),
                                                     Spring.constant(m_T1.get_YPoint()),
                                                     Spring.constant(m_T1.get_Width()),
                                                     Spring.constant(m_T1.get_Height())));
                if (m_T1.get_Button().equals("Y")) {
                    JButton m_B1 = new JButton();
                    if (m_T1.get_RowType() == 5)  m_B1.setIcon(LookIcon2);
                    else                          m_B1.setIcon(LookIcon1);

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
        mEnter.setBackground(mArea.getButtonColor());

        mEnter.addActionListener( this );
        mEnter.setActionCommand("OK");
        mEnter.setMnemonic(KeyEvent.VK_O);

        dataPane3.add(mEnter, new SpringLayout.Constraints(Spring.constant(200),
                                                 Spring.constant(10),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));

        mCancel = new JButton("Cancel");
        mCancel.setBackground(mArea.getButtonColor());
        mCancel.addActionListener( this );
        mCancel.setActionCommand("Exit");
        mCancel.setMnemonic(KeyEvent.VK_C);

        dataPane3.add(mCancel, new SpringLayout.Constraints(Spring.constant(320),
                                                 Spring.constant(10),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        return dataPane3;
    }

    public void CreateWindow1() {
        Str10 = new String[5];
        for (int i4 = 0; i4 < 4; i4++) {
            mTextItem = mPane510.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") ) {
                Str10[i4] = mTextItem.getSelectedItem();
            }
            else {
                Str10[i4] = mTextItem.get_Text();
            }
        }

        mTextAreaItem = mPane510.getTextAreaObj(0);
        Str10[4] = mTextAreaItem.get_Text();

        if (Str10[3].length() == 0) {
            mParentFrame.errorBox("Error - Duration has to be entered.");
            mDispose = false;
        }

        if ( mDispose ) {

            int returnValue = mDataArea.CreateWindow1(Str10[0], Str10[1], Str10[2], Str10[3], Str10[4]);

            if (returnValue == 0) {
                mWindowItem = mDataArea.GetWindowItem(Str10[0], 0);
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
    }

    public void CreateWindow2() {
        Str10 = new String[7];
        for (int i4 = 0; i4 < 4; i4++) {
            mTextItem = mPane511.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") ) {
                Str10[i4] = mTextItem.getSelectedItem();
            }
            else {
                Str10[i4] = mTextItem.get_Text();
            }
        }

        mDateTimeItem = mPane511.getDateTimeObj(0);
        Str10[4] = mDateTimeItem.getDateString();

        mDateTimeItem = mPane511.getDateTimeObj(1);
        Str10[5] = mDateTimeItem.getDateString();

        mTextAreaItem = mPane511.getTextAreaObj(0);
        Str10[6] = mTextAreaItem.get_Text();

        // System.out.println(" X1." + Str10[0] + "--" + Str10[1] + "--" + Str10[2] +
        //                             Str10[3] + "--" + Str10[4] + "--" + Str10[5] + "--" + Str10[6]);

        if (Str10[3].length() == 0) {
            mParentFrame.errorBox("Error - Duration has to be entered.");
            mDispose = false;
        }
        else {
            if (Str10[4].length() == 0 && Str10[2].length() == 0) {
                mParentFrame.errorBox("Error - Start Date and Repeat Interval" +
                                      "\n cannot both be left blank.");
                mDispose = false;
            }
        }
        if ( mDispose ) {

            int returnValue = mDataArea.CreateWindow2(Str10[0], Str10[1], Str10[4],
                                                Str10[2], Str10[5], Str10[3], Str10[6]);

            if (returnValue == 0) {
                mWindowItem = mDataArea.GetWindowItem(Str10[0], 0);
                if (mWindowItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Window from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createWindowTreeItem(mWindowItem);
                }
            }
            else {
                System.out.println(" X1." + mDataArea.getSysMessage().toString());
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getActionCommand().equals("OK") ) {

            mDispose = true;
            switch ( mTabPane.getSelectedIndex() ) {
                case 0:
                    CreateWindow1();
                    break;
                case 1:
                    CreateWindow2();
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
            mItemNo = Integer.parseInt( e.getActionCommand().substring(4) );
            switch ( mTabPane.getSelectedIndex() ) {
                case 0:
                    mTextItem = mPane510.getTextObj(mItemNo);
                    break;
                case 1:
                    mTextItem = mPane511.getTextObj(mItemNo);
                    break;
            }
            SchedDataInputScreen dataDialog = 
                    new SchedDataInputScreen(this, mTextItem );
            dataDialog.setVisible( true );

        }
    }
}

