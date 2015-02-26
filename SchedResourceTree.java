/*
 * Class - SchedResourceTree
 *
 * Written by Colin Greenwood.
 *
 * Version - 1.0.0.0
 *
 * Date - 1st Nov 2010
 * Date - 28th Apr 2011. Updated for use in Resource FE Plus
 *                       for the Oracle 11g R2 Version.
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

import java.sql.*;
import java.awt.*;
import java.util.*;
import javax.swing.tree.*;

public class SchedResourceTree {

    public  static final int                 PENDING_SCREEN_NO = 1;
    public  static final int                 USERS_SCREEN_NO  = 34;
    public  static final int                 GROUP_MAPPING_SCREEN_NO = 36;


    static int                        elementNo;

    public static void createResourceTree(SchedGlobalData  area,
                                          SchedDataArea    dataArea,
                                          SchedScreenArea  screenArea,
                                          SchedDataNode    rootNode1,
                                          SchedDataNode    rootNode2) {

        if (dataArea.isPendingArea(true)) {
            // System.out.println( "--1--"); 

            SchedDataNode parentNode1 = new SchedDataNode(
                                          SchedConsts.PLANS_TREE,
                                          null,
                                          rootNode2.getNodeId(),
                                          dataArea.getNextSeqNo(),
                                          "F",
                                          dataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.PLAN_SCREEN_NO);
            rootNode2.add(parentNode1);

            if (dataArea.isCdbDatabase())
                createCdbPlansSubTree(dataArea, screenArea, parentNode1, true);
            else
                createPlansSubTree(dataArea, screenArea, parentNode1, true);

            SchedDataNode parentNode2 = new SchedDataNode(
                                          SchedConsts.CONSUMER_GROUPS_TREE,
                                          null,
                                          rootNode2.getNodeId(),
                                          dataArea.getNextSeqNo(),
                                          "F",
                                          dataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.CONSUMER_GROUP_SCREEN_NO);
            rootNode2.add(parentNode2);
            createConsumerGroupsSubTree(dataArea, screenArea, parentNode2, true);

            SchedDataNode parentNode3 = new SchedDataNode(
                                           SchedConsts.MAPPING_PRIORITY_TREE,
                                           "SYS",
                                           rootNode2.getNodeId(),
                                           dataArea.getNextSeqNo(),
                                           "L",
                                           dataArea.getConnectId(),
                                           SchedConsts.MAPPING_PRIORITY_SCREEN_NO,
                                           screenArea.getScreenId(
                                                     SchedConsts.MAPPING_PRIORITY_SCREEN_NO,
                                                     dataArea.getVersion()),
                                           SchedConsts.MAPPING_PRIORITY_SCREEN_NO);
            rootNode2.add(parentNode3);
        }

        // System.out.println( "--2--"); 
        // Entering the plans into the tree.
        SchedDataNode parentNode1 = new SchedDataNode(
                                          SchedConsts.PLANS_TREE,
                                          null,
                                          rootNode1.getNodeId(),
                                          dataArea.getNextSeqNo(),
                                          "F",
                                          dataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.PLAN_SCREEN_NO);
        rootNode1.add(parentNode1);

        if (dataArea.isCdbDatabase())
            createCdbPlansSubTree(dataArea, screenArea, parentNode1, false);
        else
            createPlansSubTree(dataArea, screenArea, parentNode1, false);

        // Entering the consumer groups into the tree.
        SchedDataNode parentNode2 = new SchedDataNode(
                                          SchedConsts.CONSUMER_GROUPS_TREE,
                                          null,
                                          rootNode1.getNodeId(),
                                          dataArea.getNextSeqNo(),
                                          "F",
                                          dataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.CONSUMER_GROUP_SCREEN_NO);
        rootNode1.add(parentNode2);
        createConsumerGroupsSubTree(dataArea, screenArea, parentNode2, false);

        SchedDataNode parentNode3 = new SchedDataNode(
                                       SchedConsts.MAPPING_PRIORITY_TREE,
                                       "SYS",
                                       rootNode2.getNodeId(),
                                       dataArea.getNextSeqNo(),
                                       "L",
                                       dataArea.getConnectId(),
                                       SchedConsts.MAPPING_PRIORITY_SCREEN_NO,
                                       screenArea.getScreenId(
                                                   SchedConsts.MAPPING_PRIORITY_SCREEN_NO,
                                                   dataArea.getVersion()),
                                       SchedConsts.MAPPING_PRIORITY_SCREEN_NO);
        rootNode1.add(parentNode3);
    }

    private static void createPlansSubTree(
                                        SchedDataArea   mDataArea,
                                        SchedScreenArea mScreenArea,
                                        SchedDataNode   planNode,
                                        boolean         mPending) {

        for (int i2 = 0; i2 < mDataArea.planSize(); i2++) {

            SchedDataArea.PlanItem lPlanItem = mDataArea.getPlan(i2);

            if (mPending) {
                if ((lPlanItem.getStatus() != null) &&
                    (lPlanItem.getStatus().equals(SchedConsts.PENDING_STATUS))) {

                    SchedDataNode childNode = new SchedDataNode(
                                        lPlanItem.getPlan(),
                                        "SYS",
                                        planNode.getNodeId(),
                                        lPlanItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.PLAN_SCREEN_NO,
                                        mScreenArea.getScreenId(
                                                    SchedConsts.PLAN_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.PLAN_SCREEN_NO);
                    planNode.add(childNode);
                }
            }
            else {
                if ((lPlanItem.getStatus() == null) ||
                    (lPlanItem.getStatus().compareTo(SchedConsts.PENDING_STATUS) != 0)) {

                    SchedDataNode childNode = new SchedDataNode(
                                        lPlanItem.getPlan(),
                                        "SYS",
                                        planNode.getNodeId(),
                                        lPlanItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.PLAN_SCREEN_NO,
                                        mScreenArea.getScreenId(
                                                    SchedConsts.PLAN_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.PLAN_SCREEN_NO);
                    planNode.add(childNode);
                }
            }
        }
    }

    private static void createCdbPlansSubTree(
                                        SchedDataArea   mDataArea,
                                        SchedScreenArea mScreenArea,
                                        SchedDataNode   cdbPlanNode,
                                        boolean         mPending) {

        for (int i2 = 0; i2 < mDataArea.cdbPlanSize(); i2++) {

            SchedDataArea.CdbPlanItem lCdbPlanItem = mDataArea.getCdbPlan(i2);

            if (mPending) {
                if ((lCdbPlanItem.getStatus() != null) &&
                    (lCdbPlanItem.getStatus().equals(SchedConsts.PENDING_STATUS))) {

                    SchedDataNode childNode = new SchedDataNode(
                                        lCdbPlanItem.getPlan(),
                                        "SYS",
                                        cdbPlanNode.getNodeId(),
                                        lCdbPlanItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.CDB_PLAN_SCREEN_NO,
                                        mScreenArea.getScreenId(
                                                    SchedConsts.CDB_PLAN_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.PLAN_SCREEN_NO);
                    cdbPlanNode.add(childNode);
                }
            }
            else {
                if ((lCdbPlanItem.getStatus() == null) ||
                    (lCdbPlanItem.getStatus().compareTo(SchedConsts.PENDING_STATUS) != 0)) {

                    SchedDataNode childNode = new SchedDataNode(
                                        lCdbPlanItem.getPlan(),
                                        "SYS",
                                        cdbPlanNode.getNodeId(),
                                        lCdbPlanItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.CDB_PLAN_SCREEN_NO,
                                        mScreenArea.getScreenId(
                                                    SchedConsts.CDB_PLAN_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.PLAN_SCREEN_NO);
                    cdbPlanNode.add(childNode);
                }
            }
        }
    }

    private static void createConsumerGroupsSubTree(
                                        SchedDataArea   mDataArea,
                                        SchedScreenArea mScreenArea,
                                        SchedDataNode   consumerGroupNode,
                                        boolean         mPending) {
        boolean mFirst = true;
        for (int i2 = 0; i2 < mDataArea.consumerGroupSize(); i2++) {

            SchedDataArea.ConsumerGroupItem lConsumerGroupItem = mDataArea.getConsumerGroup(i2);

            if (mPending) {
                if ((lConsumerGroupItem.getStatus() != null) &&
                    (lConsumerGroupItem.getStatus().equals(SchedConsts.PENDING_STATUS))) {
                    SchedDataNode groupNode = new SchedDataNode(
                                        lConsumerGroupItem.getConsumerGroup(),
                                        "SYS",
                                        consumerGroupNode.getNodeId(),
                                        lConsumerGroupItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.CONSUMER_GROUP_SCREEN_NO,
                                        mScreenArea.getScreenId(
                                                    SchedConsts.CONSUMER_GROUP_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.CONSUMER_GROUP_SCREEN_NO);
                    consumerGroupNode.add(groupNode);

                    SchedDataNode parentNode = groupNode;
                }
            }
            else {
                if ((lConsumerGroupItem.getStatus() == null) ||
                    (lConsumerGroupItem.getStatus().compareTo(SchedConsts.PENDING_STATUS) != 0)) {
                    SchedDataNode groupNode = new SchedDataNode(
                                        lConsumerGroupItem.getConsumerGroup(),
                                        "SYS",
                                        consumerGroupNode.getNodeId(),
                                        lConsumerGroupItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.CONSUMER_GROUP_SCREEN_NO,
                                        mScreenArea.getScreenId(
                                                    SchedConsts.CONSUMER_GROUP_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.CONSUMER_GROUP_SCREEN_NO);
                    consumerGroupNode.add(groupNode);

                    SchedDataNode parentNode = groupNode;

                }
            }
        }
    }


    public static void dropPlanItem(SchedDataArea.PlanItem mPlanItem,
                                          DefaultTreeModel mTreeModel,
                                          SchedDataNode rootNode) {

        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.PLANS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            SchedDataNode childNode = findChildNode(tempNode1, mPlanItem.getPlan(), 1);
            if (childNode == null) mFound = false;
            else  mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropCdbPlanItem(SchedDataArea.CdbPlanItem mCdbPlanItem,
                                       DefaultTreeModel mTreeModel,
                                       SchedDataNode rootNode) {

        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.PLANS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            SchedDataNode childNode = findChildNode(tempNode1, mCdbPlanItem.getPlan(), 1);
            if (childNode == null) mFound = false;
            else  mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropConsumerGroupItem(SchedDataArea.ConsumerGroupItem mConsumerGroupItem,
                                             DefaultTreeModel mTreeModel,
                                             SchedDataNode rootNode) {

        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.CONSUMER_GROUPS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            SchedDataNode childNode = findChildNode(tempNode1, mConsumerGroupItem.getConsumerGroup(), 1);
            if (childNode == null) mFound = false;
            else  mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void createResourcePlanTreeItem(SchedDataArea.PlanItem mPlanItem,
                                              DefaultTreeModel mTreeModel,
                                              SchedDataNode rootNode,
                                              SchedDataArea     mDataArea,
                                              SchedScreenArea   mScreenArea) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.PLANS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {

            SchedDataNode childNode = findChildNode(tempNode1, mPlanItem.getPlan(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mPlanItem.getPlan(),
                                "SYS",
                                tempNode1.getNodeId(),
                                mPlanItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.PLAN_SCREEN_NO,
                                mScreenArea.getScreenId(
                                            SchedConsts.PLAN_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.PLAN_SCREEN_NO);
                tempNode1.add(childNode);
                mTreeModel.insertNodeInto(childNode, tempNode1, elementNo);

            }
        }
    }

    public static void createCdbResourcePlanTreeItem(SchedDataArea.CdbPlanItem mCdbPlanItem,
                                              DefaultTreeModel  mTreeModel,
                                              SchedDataNode     rootNode,
                                              SchedDataArea     mDataArea,
                                              SchedScreenArea   mScreenArea) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.PLANS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {

            SchedDataNode childNode = findChildNode(tempNode1, mCdbPlanItem.getPlan(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mCdbPlanItem.getPlan(),
                                "SYS",
                                tempNode1.getNodeId(),
                                mCdbPlanItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.CDB_PLAN_SCREEN_NO,
                                mScreenArea.getScreenId(
                                            SchedConsts.CDB_PLAN_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.PLAN_SCREEN_NO);
                tempNode1.add(childNode);
                mTreeModel.insertNodeInto(childNode, tempNode1, elementNo);

            }
        }
    }

    public static void createConsumerGroupTreeItem(
                                            SchedDataArea.ConsumerGroupItem mConsumerGroupItem,
                                            DefaultTreeModel  mTreeModel,
                                            SchedDataNode     rootNode,
                                            SchedDataArea     mDataArea,
                                            SchedScreenArea   mScreenArea) {
        // System.out.println("--1--");
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.CONSUMER_GROUPS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            SchedDataNode childNode = findChildNode(tempNode1, mConsumerGroupItem.getConsumerGroup(), 1);
            childNode = new SchedDataNode(mConsumerGroupItem.getConsumerGroup(),
                                "SYS",
                                tempNode1.getNodeId(),
                                mConsumerGroupItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.CONSUMER_GROUP_SCREEN_NO,
                                mScreenArea.getScreenId(
                                            SchedConsts.CONSUMER_GROUP_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.CONSUMER_GROUP_SCREEN_NO);
            tempNode1.add(childNode);
            mTreeModel.insertNodeInto(childNode, tempNode1, elementNo);
        }
    }

    public static void createPendingArea(SchedDataArea dataArea,
                                         SchedDataNode rootNode) {
        SchedDataNode parentNode1 = findChildNode(rootNode, SchedConsts.PENDING_AREA_TREE, 0);
        if (parentNode1 == null) {
            SchedDataNode parentNode2 = new SchedDataNode(
                                 SchedConsts.PENDING_AREA_TREE,
                                 null,
                                 rootNode.getNodeId(),
                                 dataArea.getNextSeqNo(),
                                 "F",
                                 dataArea.getConnectId(),
                                 0, 0,
                                 PENDING_SCREEN_NO);
            rootNode.add(parentNode2);
        }
    }

    public static void clearPendingArea(SchedDataNode rootNode) {
        boolean mFound = true;
        SchedDataNode parentNode1 = findChildNode(rootNode, SchedConsts.PENDING_AREA_TREE, 0);

        if (parentNode1 == null) mFound = false;
        else  parentNode1.removeAllChildren();
    }

    private static SchedDataNode findChildNode(SchedDataNode mNode, String mString, int mExactFit) {

        Enumeration e1 = mNode.children();

        SchedDataNode tempNode = null;
        elementNo = 0;
        boolean mFound = false;
        boolean mContinue = true;

        while (  mContinue ) {

            if ( e1.hasMoreElements() ) {
                tempNode = (SchedDataNode)e1.nextElement();

                if ( tempNode.getNodeName().compareTo(mString) == 0 ) {
                    mFound = true;
                    mContinue = false;
                }
                if ( mExactFit == 1 ) {
                    if ( tempNode.getNodeName().compareTo(mString) >= 0 )
                        mContinue = false;
                    else
                        elementNo = elementNo + 1;
                }
            }
            else {
                mContinue = false;
            }
        }
        if (! mFound ) return null;
        return tempNode;
    }

}
