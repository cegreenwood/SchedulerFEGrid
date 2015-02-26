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

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;

import static uk.co.blueshireservices.schedulergrid.SchedConsts.*;

public class SchedCellRenderer
       extends JLabel
       implements TreeCellRenderer
{
    public static final long serialVersionUID = 1L;

    private  ImageIcon[]   treeIconClosed;
    private  ImageIcon[]   treeIconOpen;
    private  ImageIcon[]   treeIconLeaf;
    private  ImageIcon[]   treeIconConnection;

    private  boolean       bSelected, bExpanded;
    private  boolean       mCurrent;

    private Color          bColor;
    private Color          rColor1;
    private Color          rColor2;

    private ClassLoader  cl;

    public SchedCellRenderer() {

        cl = this.getClass().getClassLoader();

        treeIconConnection = new ImageIcon[4];
        treeIconClosed = new ImageIcon[22];
        treeIconOpen = new ImageIcon[22];
        treeIconLeaf = new ImageIcon[22];
        // System.out.println( " Event  1 -" + PACKAGE_DIR);

        treeIconConnection[0] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Connection1.png"));
        treeIconConnection[1] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Connection2.png"));
        treeIconConnection[2] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Connection3.png"));
        treeIconConnection[3] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Connection4.png"));

        treeIconClosed[0] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Folder1.gif"));
        treeIconClosed[1] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Connection1.png"));
        treeIconClosed[2] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Job1.gif"));
        treeIconClosed[3] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Program1.gif"));
        treeIconClosed[4] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Schedule1.gif"));
        treeIconClosed[5] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Chain1.gif"));
        treeIconClosed[6] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ChainRule1.gif"));
        treeIconClosed[7] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ChainStep1.gif"));
        treeIconClosed[8] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Credential1.gif"));
        treeIconClosed[9] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_JobClass1.gif"));
        treeIconClosed[10] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Window1.gif"));
        treeIconClosed[11] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_WindowG1.gif"));
        treeIconClosed[12] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Global1.gif"));
        treeIconClosed[13] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Watcher1.gif"));
        treeIconClosed[14] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Dest1.gif"));
        treeIconClosed[15] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestD1.gif"));
        treeIconClosed[16] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestE1.gif"));
        treeIconClosed[17] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Group1.gif"));
        treeIconClosed[18] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestDG1.gif"));
        treeIconClosed[19] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestEG1.gif"));
        treeIconClosed[20] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Consumer1.gif"));
        treeIconClosed[21] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_GroupMap1.gif"));

        treeIconOpen[0] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Folder2.gif"));
        treeIconOpen[1] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Connection2.png"));
        treeIconOpen[2] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Job2.gif"));
        treeIconOpen[3] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Program2.gif"));
        treeIconOpen[4] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Schedule2.gif"));
        treeIconOpen[5] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Chain2.gif"));
        treeIconOpen[6] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ChainRule2.gif"));
        treeIconOpen[7] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ChainStep2.gif"));
        treeIconOpen[8] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Credential2.gif"));
        treeIconOpen[9] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_JobClass2.gif"));
        treeIconOpen[10] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Window2.gif"));
        treeIconOpen[11] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_WindowG2.gif"));
        treeIconOpen[12] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Global2.gif"));
        treeIconOpen[13] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Watcher2.gif"));
        treeIconOpen[14] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Dest2.gif"));
        treeIconOpen[15] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestD2.gif"));
        treeIconOpen[16] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestE2.gif"));
        treeIconOpen[17] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Group2.gif"));
        treeIconOpen[18] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestDG2.gif"));
        treeIconOpen[19] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestEG2.gif"));
        treeIconOpen[20] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Consumer2.gif"));
        treeIconOpen[21] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_GroupMap2.gif"));

        treeIconLeaf[0] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Leaf.gif"));
        treeIconLeaf[1] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_Connection1.png"));
        treeIconLeaf[2] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_JobLeaf.gif"));
        treeIconLeaf[3] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ProgramLeaf.gif"));
        treeIconLeaf[4] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ScheduleLeaf.gif"));
        treeIconLeaf[5] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ChainLeaf.gif"));
        treeIconLeaf[6] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ChainRuleLeaf.gif"));
        treeIconLeaf[7] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ChainStepLeaf.gif"));
        treeIconLeaf[8] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_CredentialLeaf.gif"));
        treeIconLeaf[9] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_JobClassLeaf.gif"));
        treeIconLeaf[10] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_WindowLeaf.gif"));
        treeIconLeaf[11] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_WindowGLeaf.gif"));
        treeIconLeaf[12] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_GlobalLeaf.gif"));
        treeIconLeaf[13] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_WatcherLeaf.gif"));
        treeIconLeaf[14] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestDLeaf.gif"));
        treeIconLeaf[15] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestELeaf.gif"));
        treeIconLeaf[16] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestDGLeaf.gif"));
        treeIconLeaf[17] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_DestEGLeaf.gif"));
        treeIconLeaf[18] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_ConsumerLeaf.gif"));
        treeIconLeaf[19] = new ImageIcon(cl.getResource(PACKAGE_DIR + "Tree_GroupMapLeaf.gif"));

        setOpaque (true);
    }

    public void setHighLightColor( Color mColor ) {
        bColor = mColor;
    }
    public void setRunningPlanColor1( Color mColor ) {
        rColor1 = mColor;
    }
    public void setRunningPlanColor2( Color mColor ) {
        rColor2 = mColor;
    }
    public void setSelected() {
        mCurrent = true;
    }
    public void setUnselected() {
        mCurrent = false;
    }

    public Component getTreeCellRendererComponent( JTree tree,
                Object value, boolean bSelected, boolean bExpanded,
                boolean bLeaf, int iRow, boolean bHasFocus)

    {
        SchedDataNode node = (SchedDataNode)value;
        String labelText = (String)node.getNodeName();
        int mIconType = (int)node.getIconType();

        this.bSelected = bSelected;
        this.bExpanded = bExpanded;

        if (bSelected && mCurrent) {
            setBackground( bColor );
            if (node.getRunningPlan()) {
                if (node.getTopPlan()) {
                    setForeground( rColor1 );
                }
                else {
                    setForeground( rColor2 );
                }
            }
            else {
                setForeground( Color.white );
            }
        }
        else {
            if (node.getRunningPlan()) {
                if (node.getTopPlan()) {
                    setBackground( rColor1 );
                }
                else {
                    setBackground( rColor2 );
                }
                setForeground( Color.black );
            }
            else {
                setBackground( Color.white );
                setForeground( Color.black );
            }
        }

        if (mIconType == CONNECTION_SCREEN_NO) {
            if (node.getAllowsChildren())
                setIcon(treeIconConnection[2]);
            else {
                if (node.isConnected())  setIcon(treeIconConnection[3]);
                else                     setIcon(treeIconConnection[0]);
            }
        }
        else {
            if (node.getNodeType().equals("L")) {
                if (mIconType == JOB_SCREEN_NO)
                    setIcon(treeIconLeaf[2]);
                else if (mIconType == PROGRAM_SCREEN_NO)
                    setIcon(treeIconLeaf[3]);
                else if (mIconType == SCHEDULE_SCREEN_NO)
                    setIcon(treeIconLeaf[4]);
                else if (mIconType == CHAINS_SCREEN_NO)
                    setIcon(treeIconLeaf[5]);
                else if (mIconType == CHAIN_RULES_SCREEN_NO)
                    setIcon(treeIconLeaf[6]);
                else if (mIconType == CHAIN_STEPS_SCREEN_NO)
                    setIcon(treeIconLeaf[7]);
                else if (mIconType == CREDENTIALS_SCREEN_NO)
                    setIcon(treeIconLeaf[8]);
                else if (mIconType == JOB_CLASS_SCREEN_NO)
                    setIcon(treeIconLeaf[9]);
                else if (mIconType == WINDOW_SCREEN_NO)
                    setIcon(treeIconLeaf[10]);
                else if (mIconType == WINDOW_GROUP_SCREEN_NO)
                    setIcon(treeIconLeaf[11]);
                else if (mIconType == GLOBAL_ATTRIBUTES_SCREEN_NO)
                    setIcon(treeIconLeaf[12]);
                else if (mIconType == FILE_WATCHERS_SCREEN_NO)
                    setIcon(treeIconLeaf[13]);
                else if (mIconType == DB_DESTINATION_SCREEN_NO)
                    setIcon(treeIconLeaf[14]);
                else if (mIconType == EXT_DESTINATION_SCREEN_NO)
                    setIcon(treeIconLeaf[15]);
                else if (mIconType == DB_GROUP_ICON_NO)
                    setIcon(treeIconLeaf[16]);
                else if (mIconType == EXT_GROUP_ICON_NO)
                    setIcon(treeIconLeaf[17]);
                else if (mIconType == PLAN_SCREEN_NO)
                    setIcon(treeIconLeaf[2]);
                else if (mIconType == CONSUMER_GROUP_SCREEN_NO)
                    setIcon(treeIconLeaf[18]);
                else if (mIconType == MAPPING_PRIORITY_SCREEN_NO)
                    setIcon(treeIconLeaf[19]);
                else
                    setIcon(treeIconLeaf[0]);
            }
            else {
                if (bExpanded) {
                    if (mIconType == JOB_SCREEN_NO)
                        setIcon(treeIconOpen[2]);
                    else if (mIconType == PROGRAM_SCREEN_NO)
                        setIcon(treeIconOpen[3]);
                    else if (mIconType == SCHEDULE_SCREEN_NO)
                        setIcon(treeIconOpen[4]);
                    else if (mIconType == CHAINS_SCREEN_NO)
                        setIcon(treeIconOpen[5]);
                    else if (mIconType == CHAIN_RULES_SCREEN_NO)
                        setIcon(treeIconOpen[6]);
                    else if (mIconType == CHAIN_STEPS_SCREEN_NO)
                        setIcon(treeIconOpen[7]);
                    else if (mIconType == CREDENTIALS_SCREEN_NO)
                        setIcon(treeIconOpen[8]);
                    else if (mIconType == JOB_CLASS_SCREEN_NO)
                        setIcon(treeIconOpen[9]);
                    else if (mIconType == WINDOW_SCREEN_NO)
                        setIcon(treeIconOpen[10]);
                    else if (mIconType == WINDOW_GROUP_SCREEN_NO)
                        setIcon(treeIconOpen[11]);
                    else if (mIconType == GLOBAL_ATTRIBUTES_SCREEN_NO)
                        setIcon(treeIconOpen[12]);
                    else if (mIconType == FILE_WATCHERS_SCREEN_NO)
                        setIcon(treeIconOpen[13]);
                    else if (mIconType == DESTINATION_ICON_NO)
                        setIcon(treeIconOpen[14]);
                    else if (mIconType == DB_DESTINATION_SCREEN_NO)
                        setIcon(treeIconOpen[15]);
                    else if (mIconType == EXT_DESTINATION_SCREEN_NO)
                        setIcon(treeIconOpen[16]);
                    else if (mIconType == GROUP_ICON_NO)
                        setIcon(treeIconOpen[17]);
                    else if (mIconType == DB_GROUP_ICON_NO)
                        setIcon(treeIconOpen[18]);
                    else if (mIconType == EXT_GROUP_ICON_NO)
                        setIcon(treeIconOpen[19]);
                    else if (mIconType == PLAN_SCREEN_NO)
                        setIcon(treeIconOpen[2]);
                    else if (mIconType == CONSUMER_GROUP_SCREEN_NO)
                        setIcon(treeIconOpen[20]);
                    else if (mIconType == MAPPING_PRIORITY_SCREEN_NO)
                        setIcon(treeIconOpen[21]);
                    else
                        setIcon(treeIconOpen[0]);
                }
                else {

                    if (mIconType == JOB_SCREEN_NO)
                        setIcon(treeIconClosed[2]);
                    else if (mIconType == PROGRAM_SCREEN_NO)
                        setIcon(treeIconClosed[3]);
                    else if (mIconType == SCHEDULE_SCREEN_NO)
                        setIcon(treeIconClosed[4]);
                    else if (mIconType == CHAINS_SCREEN_NO)
                        setIcon(treeIconClosed[5]);
                    else if (mIconType == CHAIN_RULES_SCREEN_NO)
                        setIcon(treeIconClosed[6]);
                    else if (mIconType == CHAIN_STEPS_SCREEN_NO)
                        setIcon(treeIconClosed[7]);
                    else if (mIconType == CREDENTIALS_SCREEN_NO)
                        setIcon(treeIconClosed[8]);
                    else if (mIconType == JOB_CLASS_SCREEN_NO)
                        setIcon(treeIconClosed[9]);
                    else if (mIconType == WINDOW_SCREEN_NO)
                        setIcon(treeIconClosed[10]);
                    else if (mIconType == WINDOW_GROUP_SCREEN_NO)
                        setIcon(treeIconClosed[11]);
                    else if (mIconType == GLOBAL_ATTRIBUTES_SCREEN_NO)
                        setIcon(treeIconClosed[12]);
                    else if (mIconType == FILE_WATCHERS_SCREEN_NO)
                        setIcon(treeIconClosed[13]);
                    else if (mIconType == DESTINATION_ICON_NO)
                        setIcon(treeIconClosed[14]);
                    else if (mIconType == DB_DESTINATION_SCREEN_NO)
                        setIcon(treeIconClosed[15]);
                    else if (mIconType == EXT_DESTINATION_SCREEN_NO)
                        setIcon(treeIconClosed[16]);
                    else if (mIconType == GROUP_ICON_NO)
                        setIcon(treeIconClosed[17]);
                    else if (mIconType == DB_GROUP_ICON_NO)
                        setIcon(treeIconClosed[18]);
                    else if (mIconType == EXT_GROUP_ICON_NO)
                        setIcon(treeIconClosed[19]);
                    else if (mIconType == PLAN_SCREEN_NO)
                        setIcon(treeIconClosed[2]);
                    else if (mIconType == CONSUMER_GROUP_SCREEN_NO)
                        setIcon(treeIconClosed[20]);
                    else if (mIconType == MAPPING_PRIORITY_SCREEN_NO)
                        setIcon(treeIconClosed[21]);
                    else
                        setIcon(treeIconClosed[0]);
                }
            }
        }

        setText(labelText);

        return this;
    }
}
