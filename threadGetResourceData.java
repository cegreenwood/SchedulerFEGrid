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

import javax.swing.tree.*;

import java.util.*;

class threadGetResourceData implements Runnable {

    public  static final String   ROOT_TREE = "RESOURCE MANAGER";
    public  static final String   PLANS_TREE = "PLANS";

    private SchedDataNode               parentNode, mRootNode, tempNode;

    private SchedDataArea.CurrentPlanItem        gCurrentPlanItem;

    private SchedDataArea         mDataArea;
    private boolean               mContinue;
    private boolean               mUpdated;
    private Scheduler             parentFrame;
    private DefaultTreeModel      mTreeModel;

    public threadGetResourceData(Scheduler        parentFrame,
                                 SchedDataArea    dataArea,
                                 SchedDataNode    rootNode,
                                 DefaultTreeModel treeModel) {
        if ( parentFrame.mDebug == 0)
            SchedFile.WriteDebugLine("threadGetResourceData.threadGetResourceData");

        // System.out.println(" Running - Jobs running 1");
        this.parentFrame = parentFrame;
        mDataArea = dataArea;
        mRootNode = rootNode;
        mTreeModel = treeModel;
        mContinue = true;
    }

    public void stopJob() {
        if ( parentFrame.mDebug == 0) SchedFile.WriteDebugLine("threadGetResourceData.stopJob");

        mContinue = false;
    }

    public void run() {
        if ( parentFrame.mDebug == 0) SchedFile.WriteDebugLine("threadGetResourceData.run");

        // System.out.println(" Running - Jobs running...");
        Enumeration e1 = mRootNode.children();

        boolean mFound = false;
        mContinue = true;
        while (  mContinue ) {
            if ( e1.hasMoreElements() ) {
                parentNode = (SchedDataNode)e1.nextElement();

                if ( parentNode.getNodeName().compareTo(PLANS_TREE) == 0 ) {
                    mFound = true;
                    mContinue = false;
                }
            }
            else {
                mContinue = false;
            }
        }

        if (mFound) {
            int mCount = 0;



            mContinue = true;
            while (mContinue) {
                // System.out.println(" Running - Count -" + mCount); 

                mCount = mCount + 1;

                mDataArea.clearCurrentPlansVector();
                mDataArea.GetCurrentPlansData(mDataArea);

                Enumeration e2 = parentNode.children();
                boolean mLoop = true;
                while (mLoop) {

                    if ( e2.hasMoreElements() ) {
                        tempNode = (SchedDataNode)e2.nextElement();

                        mUpdated = false;
                        for (int i = 0; i < mDataArea.currentPlansSize(); i++) {
                            gCurrentPlanItem = mDataArea.getCurrentPlan(i);

                            if (tempNode.getNodeName().equals(gCurrentPlanItem.getName())) {
                                if ((gCurrentPlanItem.getIsTopPlan().equals("TRUE")) && 
                                    ( ! tempNode.getTopPlan())) {
                                    tempNode.setTopPlan(true);
                                    mUpdated = true;
                                }
                                if ( ! tempNode.getRunningPlan() ) {
                                    tempNode.setRunningPlan(true);
                                    mUpdated = true;
                                }
                                if (mUpdated) {
                                    mTreeModel.nodeChanged(tempNode);
                                }
                                mUpdated = true;
                            }
                        }
                        if (( ! mUpdated) && (tempNode.getRunningPlan())) {
                            tempNode.setTopPlan(false);
                            tempNode.setRunningPlan(false);
                            mTreeModel.nodeChanged(tempNode);
                            // System.out.println(" Running - " + tempNode.getNodeName()); 
                        }
                    }
                    else {
                        mLoop = false;
                    }
                }

                //Sleeps for 5 seconds.
                try {
                    Thread.sleep(5000);
                } catch ( InterruptedException e ) {
                    // System.out.println(" Running - Terminating");
                    mContinue = false;
                }
            }
        }
    }
}

