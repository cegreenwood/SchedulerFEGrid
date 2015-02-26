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

class SchedOptionScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton         mEnter1, mEnter2, mEnter3, mEnter4;
    private JTabbedPane     mTabPane;
    private JPanel          optionPane1, optionPane2, optionPane3, optionPane4;

    private JCheckBox       mCheck1, mCheck2, mCheck3;
    private JCheckBox       mCheck4, mCheck5, mCheck6;
    private JCheckBox       mLogCheck1, mLogCheck2;
    private JCheckBox       mEventCheck1, mEventCheck2;
    private JCheckBox       mGenCheck1, mGenCheck2;

    // Instance attributes used in this dialog.
    private Scheduler        parentFrame;
    private SchedGlobalData  mArea;

    // Dialog constructor.
    public SchedOptionScreen(Scheduler parentFrame,
                             SchedGlobalData  Area)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        // Save the owner frame in case we need it later.
        this.parentFrame = parentFrame;
        mArea = Area;

        // Set the characteristics for this dialog instance.
        setTitle( "Scheduler FE Options" );
        setSize( 400, 360 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        // this.setLocation(150,100);
        this.setLocation((int)parentFrame.getLocationOnScreen().getX() + 140,
                         (int)parentFrame.getLocationOnScreen().getY() + 100);

        // Create the tabbed pane.
        mTabPane = new JTabbedPane();

        // Create a panel for the components.
        optionPane1 = new JPanel();
        optionPane1.setLayout(new SpringLayout());
        optionPane1.setBackground( mArea.getBackgroundColor() );

        mTabPane.add("Tree Links", optionPane1);
        mTabPane.setBackground(mArea.getBackgroundColor());
        mTabPane.setMnemonicAt(0, KeyEvent.VK_T);

        JLabel label_1 = new JLabel("Tree Link Options");
        label_1.setFont(new Font("Helvetica", Font.BOLD, 16) );
        optionPane1.add(label_1, new SpringLayout.Constraints(
                                                 Spring.constant(140),
                                                 Spring.constant(20),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        // mCheck1 = new JCheckBox("Include Job/Jobs Completed Links",SchedFile.getFileOption(0));
        // mCheck1.setBackground( mArea.getScreenColor(21) );
        // mCheck1.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        // optionPane1.add(mCheck1, new SpringLayout.Constraints(
        //                                          Spring.constant(80),
        //                                          Spring.constant(60),
        //                                          Spring.constant(220),
        //                                          Spring.constant(20)));

        mCheck2 = new JCheckBox("Include Job/Program Links",SchedFile.getFileOption(1));
        mCheck2.setBackground( mArea.getBackgroundColor() );
        mCheck2.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        optionPane1.add(mCheck2, new SpringLayout.Constraints(
                                                 Spring.constant(80),
                                                 Spring.constant(75),
                                                 Spring.constant(220),
                                                 Spring.constant(20)));

        mCheck3 = new JCheckBox("Include Job/Schedule Links",SchedFile.getFileOption(2));
        mCheck3.setBackground( mArea.getBackgroundColor() );
        mCheck3.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        optionPane1.add(mCheck3, new SpringLayout.Constraints(
                                                 Spring.constant(80),
                                                 Spring.constant(100),
                                                 Spring.constant(220),
                                                 Spring.constant(20)));


        mCheck5 = new JCheckBox("Include Job Class/Jobs Links",SchedFile.getFileOption(4));
        mCheck5.setBackground( mArea.getBackgroundColor() );
        mCheck5.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        optionPane1.add(mCheck5, new SpringLayout.Constraints(
                                                 Spring.constant(80),
                                                 Spring.constant(125),
                                                 Spring.constant(220),
                                                 Spring.constant(20)));

        mCheck6 = new JCheckBox("Include Windows/Schedule Links",SchedFile.getFileOption(5));
        mCheck6.setBackground( mArea.getBackgroundColor() );
        mCheck6.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        optionPane1.add(mCheck6, new SpringLayout.Constraints(
                                                 Spring.constant(80),
                                                 Spring.constant(150),
                                                 Spring.constant(220),
                                                 Spring.constant(20)));

        mEnter1 = new JButton("  OK  ");
        mEnter1.setBackground( mArea.getButtonColor() );
        optionPane1.add(mEnter1, new SpringLayout.Constraints(
                                                 Spring.constant(150),
                                                 Spring.constant(230),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter1.addActionListener( this );
        mEnter1.setActionCommand("Exit");
        mEnter1.setMnemonic(KeyEvent.VK_O);

        // End of first pane.
        // Start of second pane.

        // optionPane2 = new JPanel();
        // optionPane2.setLayout(new SpringLayout());
        // optionPane2.setBackground( mArea.getBackgroundColor() );

        // mTabPane.add("Log View", optionPane2);
        // mTabPane.setMnemonicAt(1, KeyEvent.VK_L);

        // JLabel label_2 = new JLabel("Log View Options");
        // label_2.setFont(new Font("Helvetica", Font.BOLD, 16) );
        // optionPane2.add(label_2, new SpringLayout.Constraints(
        //                                          Spring.constant(120),
        //                                          Spring.constant(20),
        //                                          Spring.constant(180),
        //                                          Spring.constant(20)));
        // mLogCheck1 = new JCheckBox("Exclude Jobs in  Schema EXFSYS",SchedFile.getFileOption(6));
        // mLogCheck1.setBackground( mArea.getBackgroundColor() );
        // mLogCheck1.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        // optionPane2.add(mLogCheck1, new SpringLayout.Constraints(
        //                                          Spring.constant(80),
        //                                          Spring.constant(100),
        //                                          Spring.constant(220),
        //                                          Spring.constant(20)));
        // mLogCheck2 = new JCheckBox("Exclude Jobs in Schema SYS",SchedFile.getFileOption(7));
        // mLogCheck2.setBackground( mArea.getBackgroundColor() );
        // mLogCheck2.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        // optionPane2.add(mLogCheck2, new SpringLayout.Constraints(
        //                                          Spring.constant(80),
        //                                          Spring.constant(125),
        //                                          Spring.constant(220),
        //                                          Spring.constant(20)));

        // mEnter2 = new JButton("  OK  ");
        // mEnter2.setBackground( mArea.getButtonColor() );
        // optionPane2.add(mEnter2, new SpringLayout.Constraints(
        //                                          Spring.constant(150),
        //                                          Spring.constant(230),
        //                                          Spring.constant(80),
        //                                          Spring.constant(25)));
        // mEnter2.addActionListener( this );
        // mEnter2.setActionCommand("Exit");
        // mEnter2.setMnemonic(KeyEvent.VK_O);

        // End of second pane.
        // Start of third pane.
        // optionPane3 = new JPanel();
        // optionPane3.setLayout(new SpringLayout());
        // optionPane3.setBackground( mArea.getBackgroundColor() );

        // mTabPane.add("Event View", optionPane3);
        // mTabPane.setMnemonicAt(1, KeyEvent.VK_E);

        // JLabel label_3 = new JLabel("Event View Options");
        // label_3.setFont(new Font("Helvetica", Font.BOLD, 16) );
        // optionPane3.add(label_3, new SpringLayout.Constraints(
        //                                          Spring.constant(120),
        //                                          Spring.constant(20),
        //                                          Spring.constant(180),
        //                                          Spring.constant(20)));
        // mEventCheck1 = new JCheckBox("Exclude Jobs in  Schema EXFSYS",SchedFile.getFileOption(8));
        // mEventCheck1.setBackground( mArea.getBackgroundColor() );
        // mEventCheck1.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        // optionPane3.add(mEventCheck1, new SpringLayout.Constraints(
        //                                          Spring.constant(80),
        //                                          Spring.constant(100),
        //                                          Spring.constant(220),
        //                                          Spring.constant(20)));
        // mEventCheck2 = new JCheckBox("Exclude Jobs in Schema SYS",SchedFile.getFileOption(9));
        // mEventCheck2.setBackground( mArea.getBackgroundColor() );
        // mEventCheck2.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        // optionPane3.add(mEventCheck2, new SpringLayout.Constraints(
        //                                          Spring.constant(80),
        //                                          Spring.constant(125),
        //                                          Spring.constant(220),
        //                                          Spring.constant(20)));

        // mEnter3 = new JButton("  OK  ");
        // mEnter3.setBackground( mArea.getButtonColor() );
        // optionPane3.add(mEnter3, new SpringLayout.Constraints(
        //                                          Spring.constant(150),
        //                                          Spring.constant(230),
        //                                          Spring.constant(80),
        //                                          Spring.constant(25)));
        // mEnter3.addActionListener( this );
        // mEnter3.setActionCommand("Exit");
        // mEnter3.setMnemonic(KeyEvent.VK_O);

        // End of third pane.

        // Start of fourth pane.
        optionPane4 = new JPanel();
        optionPane4.setLayout(new SpringLayout());
        optionPane4.setBackground( mArea.getBackgroundColor() );

        mTabPane.add("General View", optionPane4);
        mTabPane.setMnemonicAt(1, KeyEvent.VK_G);

        JLabel label_4 = new JLabel("General Options");
        label_4.setFont(new Font("Helvetica", Font.BOLD, 16) );
        optionPane4.add(label_4, new SpringLayout.Constraints(
                                                 Spring.constant(120),
                                                 Spring.constant(20),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        mGenCheck1 = new JCheckBox("Include Text on Buttons",SchedFile.getFileOption(10));
        mGenCheck1.setBackground( mArea.getBackgroundColor() );
        mGenCheck1.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        optionPane4.add(mGenCheck1, new SpringLayout.Constraints(
                                                 Spring.constant(80),
                                                 Spring.constant(100),
                                                 Spring.constant(220),
                                                 Spring.constant(20)));
        mGenCheck2 = new JCheckBox("Run Jobs using Current Session",SchedFile.getFileOption(11));
        mGenCheck2.setBackground( mArea.getBackgroundColor() );
        mGenCheck2.setFont(new Font("Helvetica", Font.PLAIN, 12) );
        optionPane4.add(mGenCheck2, new SpringLayout.Constraints(
                                                 Spring.constant(80),
                                                 Spring.constant(125),
                                                 Spring.constant(220),
                                                 Spring.constant(20)));
        mEnter4 = new JButton("  OK  ");
        mEnter4.setBackground( mArea.getButtonColor() );
        optionPane4.add(mEnter4, new SpringLayout.Constraints(
                                                 Spring.constant(150),
                                                 Spring.constant(230),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter4.addActionListener( this );
        mEnter4.setActionCommand("Exit");
        mEnter4.setMnemonic(KeyEvent.VK_O);

        // End of fourth pane.

        getContentPane().add( mTabPane );

    }

    public void actionPerformed( ActionEvent e ) {
      if ( e.getActionCommand().equals("Exit") ) {

        // SchedFile.saveFileOption(0, mCheck1.isSelected() );
        SchedFile.saveFileOption(1, mCheck2.isSelected() );
        SchedFile.saveFileOption(2, mCheck3.isSelected() );
        SchedFile.saveFileOption(3, false );
        SchedFile.saveFileOption(4, mCheck5.isSelected() );
        SchedFile.saveFileOption(5, mCheck6.isSelected() );

        // SchedFile.saveFileOption(6, mLogCheck1.isSelected() );
        // SchedFile.saveFileOption(7, mLogCheck2.isSelected() );

        // SchedFile.saveFileOption(8, mEventCheck1.isSelected() );
        // SchedFile.saveFileOption(9, mEventCheck2.isSelected() );

        SchedFile.saveFileOption(10, mGenCheck1.isSelected() );
        SchedFile.saveFileOption(11, mGenCheck2.isSelected() );

        dispose();
      }
    }
}


