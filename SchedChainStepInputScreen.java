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

class SchedChainStepInputScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton                         mEnter, mCancel, mLeft, mRight;
    private JTabbedPane                     mTabPane;
    private JPanel                          dataPane, dataPanel;
    private JPanel                          dataPane117, dataPane118, dataPane119;
    private SchedInpScreenArea              mInpScreens;
    private SchedInpScreenArea.PaneObject   mPane, mPane117, mPane118, mPane119;
    private SchedGlobalData                 mArea;
    private int                             currentScreenId, oldScreenId;
    private int                             mItemNo;

    private SchedInpScreenArea.PaneObject.TextItem       mTextItem;
    private SchedInpScreenArea.PaneObject.TextAreaItem   mTextAreaItem;

    private Scheduler                       mParentFrame;
    private SchedDataArea                   mDataArea;
    private boolean                         mDispose;
    private String[]                        Str10;

    private SchedDataArea.ChainStepsItem    mChainStepsItem;
    private ClassLoader  cl;
    private String                          mChainName;

    public SchedChainStepInputScreen(Scheduler          parentFrame,
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

        setSize( 620, 460 );
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

        dataPane117 = new JPanel();
        dataPane117 = setupPane(517);
        mTabPane.add("Option 1", dataPane117);
        mTabPane.setMnemonicAt(0,KeyEvent.VK_1);
        mTabPane.setToolTipTextAt(0,"Creating a Standard Chain Step.");
        mTabPane.setBackgroundAt(0, mArea.getScreenColor(42));

        dataPane118 = new JPanel();
        dataPane118 = setupPane(518);
        mTabPane.add("Option 2", dataPane118);
        mTabPane.setMnemonicAt(1,KeyEvent.VK_2);
        mTabPane.setToolTipTextAt(1,"Creating an Event Chain Step.");
        mTabPane.setBackgroundAt(1, mArea.getScreenColor(42));

        dataPane119 = new JPanel();
        dataPane119 = setupPane(519);
        mTabPane.add("Option 3", dataPane119);
        mTabPane.setMnemonicAt(2,KeyEvent.VK_3);
        mTabPane.setToolTipTextAt(2,"Creating an Event Chain Step With an Event Schedule.");
        mTabPane.setBackgroundAt(2, mArea.getScreenColor(42));

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

    private JPanel setupPane(int mScreenId) {

        for (int r1 = 0; r1 < mInpScreens.sizeScreenObj(); r1++) {
            mPane = mInpScreens.getScreenObj(r1);

            if (mPane.getScreenNo() == mScreenId) {
                if (mScreenId == 117) {
                    mPane117 = mInpScreens.getScreenObj(r1);
                }
                if (mScreenId == 118) {
                    mPane118 = mInpScreens.getScreenObj(r1);
                }
                if (mScreenId == 119) {
                    mPane119 = mInpScreens.getScreenObj(r1);
                }
                r1 = mInpScreens.sizeScreenObj();
            }
        }

        // Border blackline = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        dataPanel = new JPanel();
        dataPanel.setSize( 620, 380 );
        // dataPanel.setBorder(blackline);
        dataPanel.setLayout(new SpringLayout());

        dataPanel.setBackground(mArea.getScreenColor(mPane.getBgrndColour()));
        dataPanel.setForeground(mArea.getScreenColor(mPane.getFgrndColour()));

        cl = this.getClass().getClassLoader();
        Icon LookIcon = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook1.jpg"));

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

                dataPanel.add(m_T1.getComboBoxType1(mArea),
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
                    JButton m_B1 = new JButton(LookIcon);

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
        // dataPane3.setSize( 620, 60 );
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

    public void CreateChainStep1() {

        Str10 = new String[3];

        for (int i4 = 0; i4 < 2; i4++) {
            mTextItem = mPane117.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        if (mParentFrame.getCurrentChainOwner().equals(mDataArea.getUserName())) {
            mChainName = mParentFrame.getCurrentChain();
        }
        else {
            mChainName = mParentFrame.getCurrentChainOwner() + "." + 
                         mParentFrame.getCurrentChain();
        }

        int returnValue = mDataArea.CreateChainStep1(mChainName, Str10[0], Str10[1]);

        if (returnValue == 0) {
            mChainStepsItem = mDataArea.GetChainStepsItem(
                                         mParentFrame.getCurrentChainOwner(),
                                         mParentFrame.getCurrentChain(),
                                         Str10[0], 0);

            if (mChainStepsItem == null) {
                mParentFrame.errorBox("Error - Cannot read Chain Step from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createChainStepTreeItem(mChainStepsItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void CreateChainStep2() {
        Str10 = new String[3];
        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane118.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else 
                Str10[i4] = mTextItem.get_Text();
        }

        if (mParentFrame.getCurrentChainOwner().equals(mDataArea.getUserName())) {
            mChainName = mParentFrame.getCurrentChain();
        }
        else {
            mChainName = mParentFrame.getCurrentChainOwner() + "." + 
                         mParentFrame.getCurrentChain();
        }

        int returnValue = mDataArea.CreateChainStep2(mChainName, Str10[0], Str10[1], Str10[2]);

        if (returnValue == 0) {
            mChainStepsItem = mDataArea.GetChainStepsItem(
                                         mParentFrame.getCurrentChainOwner(),
                                         mParentFrame.getCurrentChain(),
                                         Str10[0], 0);
            if (mChainStepsItem == null) {
                mParentFrame.errorBox("Error - Cannot read Chain Step from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createChainStepTreeItem(mChainStepsItem);
            }
        }
        else {
            mParentFrame.errorBox(mDataArea.getSysMessage().toString());
            mDispose = false;
        }
    }

    public void CreateChainStep3() {
        Str10 = new String[2];
        for (int i4 = 0; i4 < 2; i4++) {
            mTextItem = mPane119.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else 
                Str10[i4] = mTextItem.get_Text();
        }

        if (mParentFrame.getCurrentChainOwner().equals(mDataArea.getUserName())) {
            mChainName = mParentFrame.getCurrentChain();
        }
        else {
            mChainName = mParentFrame.getCurrentChainOwner() + "." + 
                         mParentFrame.getCurrentChain();
        }

        int returnValue = mDataArea.CreateChainStep3(mChainName, Str10[0], Str10[1]);

        if (returnValue == 0) {
            mChainStepsItem = mDataArea.GetChainStepsItem(
                                         mParentFrame.getCurrentChainOwner(),
                                         mParentFrame.getCurrentChain(),
                                         Str10[0], 0);
            if (mChainStepsItem == null) {
                mParentFrame.errorBox("Error - Cannot read Chain Step from Database.");
                mDispose = false;
            }
            else {
                mParentFrame.createChainStepTreeItem(mChainStepsItem);
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
                    CreateChainStep1();
                    break;
                case 1:
                    CreateChainStep2();
                    break;
                case 2:
                    CreateChainStep3();
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
                    mTextItem = mPane117.getTextObj(mItemNo);
                    break;
                case 1:
                    mTextItem = mPane118.getTextObj(mItemNo);
                    break;
                case 2:
                    mTextItem = mPane119.getTextObj(mItemNo);
                    break;
            }
            SchedDataInputScreen dataDialog = 
                    new SchedDataInputScreen(this, mTextItem );
            dataDialog.setVisible( true );

        }
    }
}

