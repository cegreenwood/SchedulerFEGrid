/*
 * Class - SchedTree
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

public class SchedTree {

    static boolean                    firstEntry;
    static int                        elementNo;

    static SchedDataNode              parentNode, childNode, tempNode, tempNode1;

    public static void createTree(SchedGlobalData  area,
                                  SchedDataArea    dataArea,
                                  SchedScreenArea  screenArea,
                                  SchedDataNode    rootNode) {

        if ( area.blockedOption(SchedConsts.JOB_SCREEN_NO, 1) == false ) {
            // Entering the jobs into the tree.

            SchedDataNode topNode = new SchedDataNode(
                                      SchedConsts.JOBS_TREE, "",
                                      dataArea.getConnectId(),
                                      dataArea.getNextSeqNo(),
                                      "F",
                                      dataArea.getConnectId(),
                                      0, 0,
                                      SchedConsts.JOB_SCREEN_NO);

            rootNode.add(topNode);

            createJobsSubTree(topNode, area, dataArea, screenArea);
        }
        if ( area.blockedOption(SchedConsts.PROGRAM_SCREEN_NO, 1) == false ) {
            // Entering the programs into the tree.

            SchedDataNode topNode = new SchedDataNode(
                        SchedConsts.PROGRAMS_TREE, "",
                        dataArea.getConnectId(),
                        dataArea.getNextSeqNo(),
                        "F",
                        dataArea.getConnectId(),
                        0, 0,
                        SchedConsts.PROGRAM_SCREEN_NO);
            rootNode.add(topNode);

            createProgramSubTree(topNode, dataArea, screenArea);
        }

        if ( area.blockedOption(SchedConsts.SCHEDULE_SCREEN_NO, 1) == false ) {
            // Attaching the Schedules to the tree.

            SchedDataNode topNode = new SchedDataNode(
                        SchedConsts.SCHEDULES_TREE, "",
                        dataArea.getConnectId(),
                        dataArea.getNextSeqNo(),
                        "F",
                        dataArea.getConnectId(),
                        0, 0,
                        SchedConsts.SCHEDULE_SCREEN_NO);
            rootNode.add(topNode);

            createScheduleSubTree(topNode, dataArea, screenArea);
        }

        if ( area.blockedOption(SchedConsts.CHAINS_SCREEN_NO, 1) == false ) {
            // Attach the chain to the tree.

            SchedDataNode topNode = new SchedDataNode(
                        SchedConsts.CHAINS_TREE, "",
                        dataArea.getConnectId(),
                        dataArea.getNextSeqNo(), 
                        "F",
                        dataArea.getConnectId(),
                        0, 0,
                        SchedConsts.CHAINS_SCREEN_NO);
            rootNode.add(topNode);

            createChainSubTree(topNode, area, dataArea, screenArea);
        }
        if (dataArea.getVersionNo() > 2) {
            if ( area.blockedOption(SchedConsts.CREDENTIALS_SCREEN_NO, 1) == false ) {

                SchedDataNode topNode = new SchedDataNode(
                        SchedConsts.CREDENTIALS_TREE, "",
                        dataArea.getConnectId(),
                        dataArea.getNextSeqNo(),
                        "F",
                        dataArea.getConnectId(),
                        0, 0,
                        SchedConsts.CREDENTIALS_SCREEN_NO);
                rootNode.add(topNode);

                createCredentialSubTree(topNode, dataArea, screenArea);
            }
        }
        if (dataArea.getVersionNo() > 3) {
            if (( area.blockedOption(SchedConsts.DB_DESTINATION_SCREEN_NO, 1) == false ) ||
                ( area.blockedOption(SchedConsts.EXT_DESTINATION_SCREEN_NO, 1) == false )) {

                SchedDataNode topNode = new SchedDataNode(
                            SchedConsts.DESTINATIONS_TREE, "",
                            dataArea.getConnectId(),
                            dataArea.getNextSeqNo(),
                            "F",
                            dataArea.getConnectId(),
                            0, 0,
                            SchedConsts.DESTINATION_ICON_NO);
                rootNode.add(topNode);

                if ( area.blockedOption(SchedConsts.DB_DESTINATION_SCREEN_NO, 1) == false ) {
                    tempNode = new SchedDataNode(
                                SchedConsts.DBDEST_TREE, "",
                                dataArea.getConnectId(),
                                dataArea.getNextSeqNo(),
                                "F",
                                dataArea.getConnectId(),
                                0, 0,
                                SchedConsts.DB_DESTINATION_SCREEN_NO);
                    topNode.add(tempNode);

                    createDbDestSubTree(tempNode, dataArea, screenArea);
                }
                if ( area.blockedOption(SchedConsts.EXT_DESTINATION_SCREEN_NO, 1) == false ) {
                    tempNode = new SchedDataNode(
                                SchedConsts.EXTDEST_TREE, "",
                                dataArea.getConnectId(),
                                dataArea.getNextSeqNo(),
                                "F",
                                dataArea.getConnectId(),
                                0, 0,
                                SchedConsts.EXT_DESTINATION_SCREEN_NO);
                    topNode.add(tempNode);

                    createExtDestSubTree(tempNode, dataArea, screenArea);
                }
            }

            if ( area.blockedOption(SchedConsts.FILE_WATCHERS_SCREEN_NO, 1) == false ) {

                SchedDataNode topNode = new SchedDataNode(
                            SchedConsts.FILE_WATCHERS_TREE, "",
                            dataArea.getConnectId(),
                            dataArea.getNextSeqNo(), 
                            "F",
                            dataArea.getConnectId(),
                            0, 0,
                            SchedConsts.FILE_WATCHERS_SCREEN_NO);
                rootNode.add(topNode);

                createFileWatchersSubTree(topNode, dataArea, screenArea);
            }
        }

        if ( area.blockedOption(SchedConsts.JOB_CLASS_SCREEN_NO, 1) == false ) {
            // Attaching the Job Class to the tree.

            SchedDataNode topNode = new SchedDataNode(
                        SchedConsts.JOB_CLASS_TREE, "",
                        dataArea.getConnectId(),
                        dataArea.getNextSeqNo(), 
                        "F",
                        dataArea.getConnectId(),
                        0, 0,
                        SchedConsts.JOB_CLASS_SCREEN_NO);
            rootNode.add(topNode);

            createJobClassSubTree(topNode, dataArea, screenArea);
        }

        if ( area.blockedOption(SchedConsts.WINDOW_SCREEN_NO, 1) == false ) {
            // Attaching the Window to the tree.

            SchedDataNode topNode = new SchedDataNode(
                        SchedConsts.WINDOWS_TREE, "",
                        dataArea.getConnectId(),
                        dataArea.getNextSeqNo(), 
                        "F",
                        dataArea.getConnectId(),
                        0, 0,
                        SchedConsts.WINDOW_SCREEN_NO);
            rootNode.add(topNode);

            createWindowSubTree(topNode, dataArea, screenArea);
        }

        if (dataArea.getVersionNo() > 3) {
            SchedDataNode groupNode = new SchedDataNode(
                        SchedConsts.GROUP_TREE, "",
                        dataArea.getConnectId(),
                        dataArea.getNextSeqNo(),
                        "F",
                        dataArea.getConnectId(),
                        0, 0,
                        SchedConsts.GROUP_ICON_NO);
            rootNode.add(groupNode);

            if ( area.blockedOption(SchedConsts.WINDOW_GROUP_SCREEN_NO, 1) == false ) {

                SchedDataNode topNode = new SchedDataNode(
                            SchedConsts.WINDOW_GROUP_TREE, "",
                            dataArea.getConnectId(),
                            dataArea.getNextSeqNo(), 
                            "F",
                            dataArea.getConnectId(),
                            0, 0,
                            SchedConsts.WINDOW_GROUP_SCREEN_NO);
                groupNode.add(topNode);

                createGroupWindowSubTree(topNode, dataArea, screenArea);
            }

            if ( area.blockedOption(SchedConsts.GROUP_SCREEN_NO, 1) == false ) {
                SchedDataNode topNode = new SchedDataNode(
                            SchedConsts.DBDEST_GROUP_TREE, "",
                            dataArea.getConnectId(),
                            dataArea.getNextSeqNo(),
                            "F",
                            dataArea.getConnectId(),
                            0, 0,
                            SchedConsts.DB_GROUP_ICON_NO);
                groupNode.add(topNode);

                createGroupDbDestSubTree(topNode, dataArea, screenArea);

                topNode = new SchedDataNode(
                            SchedConsts.EXTDEST_GROUP_TREE, "",
                            dataArea.getConnectId(),
                            dataArea.getNextSeqNo(),
                            "F",
                            dataArea.getConnectId(),
                            0, 0,
                            SchedConsts.EXT_GROUP_ICON_NO);
                groupNode.add(topNode);

                createGroupExtDestSubTree(topNode, dataArea, screenArea);
            }
        }
        else {
            if ( area.blockedOption(SchedConsts.WINDOW_GROUP_SCREEN_NO, 1) == false ) {
                SchedDataNode topNode = new SchedDataNode(
                            SchedConsts.WINDOW_GROUP_TREE, "",
                            dataArea.getConnectId(),
                            dataArea.getNextSeqNo(), 
                            "F",
                            dataArea.getConnectId(),
                            0, 0,
                            SchedConsts.WINDOW_GROUP_SCREEN_NO);
                rootNode.add(topNode);

                createWindowGroupSubTree(topNode, dataArea, screenArea);
            }
        }

        if ( area.blockedOption(SchedConsts.GLOBAL_ATTRIBUTES_SCREEN_NO, 1) == false ) {

            SchedDataNode topNode = new SchedDataNode(
                        SchedConsts.GLOBAL_ATTRIBUTES_TREE, "",
                        dataArea.getConnectId(),
                        dataArea.getNextSeqNo(), 
                        "F",
                        dataArea.getConnectId(),
                        0, 0,
                        SchedConsts.GLOBAL_ATTRIBUTES_SCREEN_NO);
            rootNode.add(topNode);

            if (dataArea.GlobalAttributesSize() > 0) {

                for (int i2 = 0; i2 < dataArea.GlobalAttributesSize(); i2++) {

                    SchedDataArea.GlobalAttributesItem lGlobalAttributesItem = 
                                dataArea.getGlobalAttributes(i2);

                    tempNode = new SchedDataNode(lGlobalAttributesItem.getAttributeName(),
                                        null,
                                        parentNode.getNodeId(),
                                        lGlobalAttributesItem.getId(),
                                        "L",
                                        dataArea.getConnectId(),
                                        SchedConsts.GLOBAL_ATTRIBUTES_SCREEN_NO,
                                        screenArea.getScreenId(
                                                     SchedConsts.GLOBAL_ATTRIBUTES_SCREEN_NO,
                                                     dataArea.getVersion()),
                                        SchedConsts.GLOBAL_ATTRIBUTES_SCREEN_NO);
                    topNode.add(tempNode);
                }
            }
        }
    }

    private static void createJobsSubTree(SchedDataNode    jobsNode,
                                          SchedGlobalData  mArea,
                                          SchedDataArea    mDataArea,
                                          SchedScreenArea  mScreen) {
        if (mDataArea.jobSize() > 0) {

            SchedDataArea.JobItem gJobItem = mDataArea.getJob(0);

            parentNode = new SchedDataNode(gJobItem.getOwner(),
                                    null,
                                    jobsNode.getNodeId(),
                                    mDataArea.getNextSeqNo(),
                                    "F",
                                    mDataArea.getConnectId(),
                                    0, 0,
                                    SchedConsts.JOB_SCREEN_NO);
            jobsNode.add(parentNode);

            for (int i2 = 0; i2 < mDataArea.jobSize(); i2++) {
                SchedDataArea.JobItem lJobItem = mDataArea.getJob(i2);


                if ( ! gJobItem.getOwner().equals(lJobItem.getOwner())) {
                    gJobItem = mDataArea.getJob(i2);

                    parentNode = new SchedDataNode(gJobItem.getOwner(),
                                              null,
                                              jobsNode.getNodeId(),
                                              mDataArea.getNextSeqNo(),
                                              "F",
                                              mDataArea.getConnectId(),
                                              0, 0,
                                              SchedConsts.JOB_SCREEN_NO);
                    jobsNode.add(parentNode);
                }

                SchedDataNode nodeCell = new SchedDataNode(lJobItem.getJobName(),
                                        lJobItem.getOwner(),
                                        parentNode.getNodeId(),
                                        lJobItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.JOB_SCREEN_NO,
                                        mScreen.getScreenId(
                                                     SchedConsts.JOB_SCREEN_NO,
                                                     mDataArea.getVersion()),
                                        SchedConsts.JOB_SCREEN_NO);
                parentNode.add(nodeCell);

                createJobLinksSubTree(mArea, mDataArea, mScreen, lJobItem, nodeCell);
            }
        }
    }

    private static void createJobLinksSubTree(SchedGlobalData  mArea,
                                              SchedDataArea    mDataArea,
                                              SchedScreenArea  mScreen,
                                              SchedDataArea.JobItem mJobItem,
                                              SchedDataNode    mNodeCell) {
        firstEntry = true;

        if ( mArea.blockedOption(SchedConsts.JOB_ARGS_SCREEN_NO, 1) == false ) {
            // Attach the job arguments to the jobs on the tree.

            for (int i3 = 0; i3 < mDataArea.jobArgsSize(); i3++) {
                SchedDataArea.JobArgsItem lJobArgsItem = mDataArea.getJobArgs(i3);

                if ( ( lJobArgsItem.getJobName().equals( mJobItem.getJobName() ) ) &&
                     ( lJobArgsItem.getOwner().equals(mJobItem.getOwner()) ) )
                {
                    if (firstEntry) {

                        childNode = new SchedDataNode(SchedConsts.JOB_ARGS_TREE,
                                                      null,
                                                      mNodeCell.getNodeId(),
                                                      mDataArea.getNextSeqNo(),
                                                      "F",
                                                      mDataArea.getConnectId(),
                                                      0, 0,
                                                      SchedConsts.JOB_ARGS_SCREEN_NO);
                        mNodeCell.add(childNode);
                        firstEntry = false;
                    }
                    Integer Int1 = new Integer(lJobArgsItem.getArgumentPosition());
                    tempNode = new SchedDataNode(Int1.toString(),
                                                 lJobArgsItem.getOwner(),
                                                 childNode.getNodeId(),
                                                 lJobArgsItem.getId(),
                                                 "L",
                                                 mDataArea.getConnectId(),
                                                 SchedConsts.JOB_ARGS_SCREEN_NO,
                                                 mScreen.getScreenId(
                                                         SchedConsts.JOB_ARGS_SCREEN_NO,
                                                         mDataArea.getVersion()),
                                                 SchedConsts.JOB_ARGS_SCREEN_NO);
                    childNode.add(tempNode);
                }
            }
            // End of the job arguments section.
        }

        if ( mArea.blockedOption(SchedConsts.NOTIFICATIONS_SCREEN_NO, 1) == false ) {
            // Attach the notification e-mails to the jobs on the tree.
            int mEntryNo = 0;
            firstEntry = true;
            for (int i3 = 0; i3 < mDataArea.NotificationsSize(); i3++) {
                SchedDataArea.NotificationsItem lNotificationsItem = mDataArea.getNotifications(i3);
                if ( ( lNotificationsItem.getOwner().equals( mNodeCell.getOwner() ) ) &&
                     ( lNotificationsItem.getJobName().equals( mNodeCell.getNodeName() ) ) )
                {
                    if (firstEntry) {
                        childNode = new SchedDataNode(SchedConsts.NOTIFICATIONS_TREE,
                                                      null,
                                                      mNodeCell.getNodeId(),
                                                      mDataArea.getNextSeqNo(),
                                                      "F",
                                                      mDataArea.getConnectId(),
                                                      0, 0,
                                                      SchedConsts.NOTIFICATIONS_SCREEN_NO);
                        mNodeCell.add(childNode);
                        firstEntry = false;
                    }
                    mEntryNo = mEntryNo + 1;
                    Integer Int1 = new Integer(mEntryNo);
                    tempNode = new SchedDataNode(Int1.toString(),
                                            lNotificationsItem.getOwner(),
                                            parentNode.getNodeId(),
                                            lNotificationsItem.getId(),
                                            "L",
                                            mDataArea.getConnectId(),
                                            SchedConsts.NOTIFICATIONS_SCREEN_NO,
                                            mScreen.getScreenId(
                                                        SchedConsts.NOTIFICATIONS_SCREEN_NO,
                                                        mDataArea.getVersion()),
                                            SchedConsts.NOTIFICATIONS_SCREEN_NO);
                    childNode.add(tempNode);
                }
            }
        }

        if ( mArea.blockedOption(SchedConsts.PROGRAM_SCREEN_NO, 1) == false ) {
            // Attach the programs to the jobs on the tree.
            // System.out.println("*1*" + mJobItem.getJobName());
            if ( SchedFile.getFileOption(1) ) {

                if ( (mJobItem.getProgramOwner() != null) && (mJobItem.getProgramName() != null) ) {

                    for (int i3 = 0; i3 < mDataArea.programSize(); i3++)
                    {
                        SchedDataArea.ProgramItem lProgramItem = mDataArea.getProgram(i3);
                        if ( ( lProgramItem.getOwner().equals(mJobItem.getProgramOwner() ) ) &&
                             ( lProgramItem.getProgramName().equals(mJobItem.getProgramName() ) ) )
                        {
                            tempNode = new SchedDataNode(
                                            "Program - " + lProgramItem.getProgramName(),
                                            lProgramItem.getOwner(),
                                            mNodeCell.getNodeId(),
                                            lProgramItem.getId(),
                                            "L",
                                            mDataArea.getConnectId(),
                                            SchedConsts.PROGRAM_SCREEN_NO,
                                            mScreen.getScreenId(
                                                        SchedConsts.PROGRAM_SCREEN_NO,
                                                        mDataArea.getVersion()),
                                            SchedConsts.PROGRAM_SCREEN_NO);
                            mNodeCell.add(tempNode);
                        }
                    }
                }
            }
            // End of program section.
        }

        if ( mArea.blockedOption(SchedConsts.SCHEDULE_SCREEN_NO, 1) == false ) {
            // Attach the schedules to the jobs on the tree.
            if ( SchedFile.getFileOption(2) ) {
                if ( (mJobItem.getScheduleOwner() != null) && (mJobItem.getScheduleName() != null ) ) {

                    if ( mJobItem.getScheduleType() != null && mJobItem.getScheduleType().equals("NAMED") ) {
                        for (int i3 = 0; i3 < mDataArea.scheduleSize(); i3++)
                        {
                            SchedDataArea.ScheduleItem lScheduleItem = mDataArea.getSchedule(i3);
                            if ( ( mJobItem.getScheduleOwner().equals(lScheduleItem.getOwner() ) ) &&
                                 ( mJobItem.getScheduleName().equals(lScheduleItem.getScheduleName() ) ) )
                            {

                                tempNode = new SchedDataNode(
                                            "Schedule - " + lScheduleItem.getScheduleName(),
                                            lScheduleItem.getOwner(),
                                            mNodeCell.getNodeId(),
                                            lScheduleItem.getId(),
                                            "L",
                                            mDataArea.getConnectId(),
                                            SchedConsts.SCHEDULE_SCREEN_NO,
                                            mScreen.getScreenId(
                                                        SchedConsts.SCHEDULE_SCREEN_NO,
                                                        mDataArea.getVersion()),
                                            SchedConsts.SCHEDULE_SCREEN_NO);
                                mNodeCell.add(tempNode);
                            }
                        }
                    }
                    // End of looking for Schedule type Schedules.

                    if (mJobItem.getScheduleType() != null && mJobItem.getScheduleType().equals("WINDOW") ) {

                        for (int i3 = 0; i3 < mDataArea.WindowSize(); i3++)
                        {
                            SchedDataArea.WindowItem lWindowItem = mDataArea.getWindow(i3);

                            if ( mJobItem.getScheduleName().equals(lWindowItem.getWindowName() ) )
                            {

                                tempNode = new SchedDataNode(
                                            "Window - " + lWindowItem.getWindowName(),
                                            "SYS",
                                            mNodeCell.getNodeId(),
                                            lWindowItem.getId(),
                                            "L",
                                            mDataArea.getConnectId(),
                                            SchedConsts.WINDOW_SCREEN_NO,
                                            mScreen.getScreenId(
                                                        SchedConsts.WINDOW_SCREEN_NO,
                                                        mDataArea.getVersion()),
                                            SchedConsts.WINDOW_SCREEN_NO);
                                mNodeCell.add(tempNode);
                            }
                        }
                    }
                    // End of looking for Window type Schedules.

                    if ( mJobItem.getScheduleType() != null && mJobItem.getScheduleType().equals("WINDOW_GROUP") ) {

                        for (int i3 = 0; i3 < mDataArea.WindowGroupSize(); i3++)
                        {

                            SchedDataArea.WindowGroupItem lWindowGroupItem = mDataArea.getWindowGroup(i3);

                            if ( mJobItem.getScheduleName().equals(lWindowGroupItem.getWindowGroupName() ) )
                            {

                                tempNode = new SchedDataNode(
                                            "Window Group - " + lWindowGroupItem.getWindowGroupName(),
                                            "SYS",
                                            mNodeCell.getNodeId(),
                                            lWindowGroupItem.getId(),
                                            "L",
                                            mDataArea.getConnectId(),
                                            SchedConsts.WINDOW_GROUP_SCREEN_NO,
                                            mScreen.getScreenId(
                                                        SchedConsts.WINDOW_GROUP_SCREEN_NO,
                                                        mDataArea.getVersion()),
                                            SchedConsts.WINDOW_GROUP_SCREEN_NO);
                                mNodeCell.add(tempNode);
                            }
                        }
                    }
                    // End of looking for Window Group type Schedules.
                }
            }
        }
        // End of the schedule section.
    }

    private static void createProgramSubTree(SchedDataNode   programsNode,
                                             SchedDataArea   mDataArea,
                                             SchedScreenArea mScreen) {
        if (mDataArea.programSize() > 0) {

            SchedDataArea.ProgramItem gProgramItem = mDataArea.getProgram(0);
            parentNode = new SchedDataNode(gProgramItem.getOwner(),
                                    null,
                                    programsNode.getNodeId(),
                                    mDataArea.getNextSeqNo(),
                                    "F",
                                    mDataArea.getConnectId(),
                                    0, 0,
                                    SchedConsts.PROGRAM_SCREEN_NO);
            programsNode.add(parentNode);

            for (int i2 = 0; i2 < mDataArea.programSize(); i2++) {

                SchedDataArea.ProgramItem lProgramItem = mDataArea.getProgram(i2);

                // System.out.println(" C1 " + lProgramItem.getOwner() + "." + lProgramItem.getProgramName());

                if ( ! gProgramItem.getOwner().equals(lProgramItem.getOwner())) {
                    gProgramItem = mDataArea.getProgram(i2);

                    parentNode = new SchedDataNode(gProgramItem.getOwner(),
                                            null,
                                            programsNode.getNodeId(),
                                            mDataArea.getNextSeqNo(),
                                            "F",
                                            mDataArea.getConnectId(),
                                            0, 0,
                                            SchedConsts.PROGRAM_SCREEN_NO);
                    programsNode.add(parentNode);
                }

                SchedDataNode nodeCell = new SchedDataNode(lProgramItem.getProgramName(),
                                        lProgramItem.getOwner(),
                                        parentNode.getNodeId(),
                                        lProgramItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.PROGRAM_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.PROGRAM_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.PROGRAM_SCREEN_NO);
                parentNode.add(nodeCell);

                createProgramLinksSubTree(nodeCell, mDataArea, mScreen);
            }
        }
    }

    private static void createProgramLinksSubTree(SchedDataNode   mNodeCell,
                                                  SchedDataArea   mDataArea,
                                                  SchedScreenArea mScreen) {
        firstEntry = true;
        for (int i3 = 0; i3 < mDataArea.programArgsSize(); i3++) {
            SchedDataArea.ProgramArgsItem lProgramArgsItem = mDataArea.getProgramArgs(i3);

            if ( ( lProgramArgsItem.getProgramName().equals( mNodeCell.getNodeName() ) ) &&
                 ( lProgramArgsItem.getOwner().equals(mNodeCell.getOwner()))) {
                if (firstEntry) {
                    childNode = new SchedDataNode(
                                          SchedConsts.PROGRAM_ARGS_TREE,
                                          null,
                                          mNodeCell.getNodeId(),
                                          mDataArea.getNextSeqNo(),
                                          "F",
                                          mDataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.PROGRAM_ARGS_SCREEN_NO);
                    mNodeCell.add(childNode);
                    firstEntry = false;
                }
                tempNode = new SchedDataNode(
                                        lProgramArgsItem.getArgumentName(),
                                        lProgramArgsItem.getOwner(),
                                        childNode.getNodeId(),
                                        lProgramArgsItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.PROGRAM_ARGS_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.PROGRAM_ARGS_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.PROGRAM_ARGS_SCREEN_NO);
                childNode.add(tempNode);
            }
        }
    }

    private static void createScheduleSubTree(SchedDataNode   schedulesNode,
                                              SchedDataArea   mDataArea,
                                              SchedScreenArea mScreen) {
        if (mDataArea.scheduleSize() > 0) {

            SchedDataArea.ScheduleItem gScheduleItem = mDataArea.getSchedule(0);

            parentNode = new SchedDataNode(
                                      gScheduleItem.getOwner(),
                                      null,
                                      schedulesNode.getNodeId(),
                                      mDataArea.getNextSeqNo(),
                                      "F",
                                      mDataArea.getConnectId(),
                                      0, 0,
                                      SchedConsts.SCHEDULE_SCREEN_NO);
            schedulesNode.add(parentNode);

            for (int i2 = 0; i2 < mDataArea.scheduleSize(); i2++) {
                // System.out.println(" D3 " + i2);

                SchedDataArea.ScheduleItem lScheduleItem = mDataArea.getSchedule(i2);

                // System.out.println(" C2 " + lScheduleItem.getOwner() + "." +
                //     lScheduleItem.getScheduleName());

                if ( ! gScheduleItem.getOwner().equals(lScheduleItem.getOwner())) {
                    gScheduleItem = mDataArea.getSchedule(i2);
                    parentNode = new SchedDataNode(
                                              gScheduleItem.getOwner(),
                                              null,
                                              schedulesNode.getNodeId(),
                                              mDataArea.getNextSeqNo(),
                                              "F",
                                              mDataArea.getConnectId(),
                                              0, 0,
                                              SchedConsts.SCHEDULE_SCREEN_NO);
                    schedulesNode.add(parentNode);
                }

                tempNode = new SchedDataNode(
                                        lScheduleItem.getScheduleName(),
                                        lScheduleItem.getOwner(),
                                        parentNode.getNodeId(),
                                        lScheduleItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.SCHEDULE_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.SCHEDULE_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.SCHEDULE_SCREEN_NO);
                parentNode.add(tempNode);
            }
        }
    }

    private static void createChainSubTree(SchedDataNode   chainsNode,
                                           SchedGlobalData mArea,
                                           SchedDataArea   mDataArea,
                                           SchedScreenArea mScreen) {
        if (mDataArea.ChainsSize() > 0) {

            SchedDataArea.ChainsItem gChainsItem = mDataArea.getChains(0);

            parentNode = new SchedDataNode(
                                      gChainsItem.getOwner(),
                                      null,
                                      chainsNode.getNodeId(),
                                      mDataArea.getNextSeqNo(),
                                      "F",
                                      mDataArea.getConnectId(),
                                      0, 0,
                                      SchedConsts.CHAINS_SCREEN_NO);
            chainsNode.add(parentNode);

            for (int i2 = 0; i2 < mDataArea.ChainsSize(); i2++) {

                SchedDataArea.ChainsItem lChainsItem = mDataArea.getChains(i2);

                if ( ! gChainsItem.getOwner().equals(lChainsItem.getOwner())) {
                    gChainsItem = mDataArea.getChains(i2);

                    parentNode = new SchedDataNode(
                                              gChainsItem.getOwner(),
                                              null,
                                              chainsNode.getNodeId(),
                                              mDataArea.getNextSeqNo(),
                                              "F",
                                              mDataArea.getConnectId(),
                                              0, 0,
                                              SchedConsts.CHAINS_SCREEN_NO);
                    chainsNode.add(parentNode);
                }

                childNode = new SchedDataNode(
                                         lChainsItem.getChainName(),
                                         lChainsItem.getOwner(),
                                         parentNode.getNodeId(),
                                         lChainsItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.CHAINS_SCREEN_NO,
                                         mScreen.getScreenId(
                                                    SchedConsts.CHAINS_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                         SchedConsts.CHAINS_SCREEN_NO);
                parentNode.add(childNode);

                if ( mArea.blockedOption(13, 1) == false ) {
                    firstEntry = true;

                    for (int i3 = 0; i3 < mDataArea.ChainRulesSize(); i3++) {

                        SchedDataArea.ChainRulesItem lChainRulesItem = mDataArea.getChainRules(i3);

                        if ( (lChainsItem.getOwner().equals(lChainRulesItem.getOwner())) &&
                             (lChainsItem.getChainName().equals(lChainRulesItem.getChainName())))
                        {
                            if (firstEntry)
                            {
                                tempNode = new SchedDataNode(
                                                  SchedConsts.CHAIN_RULES_TREE,
                                                  null,
                                                  childNode.getNodeId(),
                                                  mDataArea.getNextSeqNo(),
                                                  "F",
                                                  mDataArea.getConnectId(),
                                                  0, 0,
                                                  SchedConsts.CHAIN_RULES_SCREEN_NO);
                                childNode.add(tempNode);
                                firstEntry = false;
                            }
                            SchedDataNode tempNode1 = new SchedDataNode(
                                                     lChainRulesItem.getRuleName(),
                                                     lChainRulesItem.getOwner(),
                                                     tempNode.getNodeId(),
                                                     lChainRulesItem.getId(),
                                                     "L",
                                                     mDataArea.getConnectId(),
                                                     SchedConsts.CHAIN_RULES_SCREEN_NO,
                                                     mScreen.getScreenId(
                                                                SchedConsts.CHAIN_RULES_SCREEN_NO,
                                                                mDataArea.getVersion()),
                                                     SchedConsts.CHAIN_RULES_SCREEN_NO);
                            tempNode.add(tempNode1);
                        }
                    }
                }
                if ( mArea.blockedOption(12, 1) == false ) {
                    firstEntry = true;

                    for (int i3 = 0; i3 < mDataArea.ChainStepsSize(); i3++) {

                        SchedDataArea.ChainStepsItem lChainStepsItem = mDataArea.getChainSteps(i3);

                        if ( lChainsItem.getOwner().equals(lChainStepsItem.getOwner()) &&
                                 lChainsItem.getChainName().equals(lChainStepsItem.getChainName()))
                        {

                            if (firstEntry)
                            {
                                tempNode = new SchedDataNode(
                                                  SchedConsts.CHAIN_STEPS_TREE,
                                                  null,
                                                  childNode.getNodeId(),
                                                  mDataArea.getNextSeqNo(),
                                                  "F",
                                                  mDataArea.getConnectId(),
                                                  0, 0,
                                                  SchedConsts.CHAIN_STEPS_SCREEN_NO);
                                childNode.add(tempNode);
                                firstEntry = false;
                            }
                            SchedDataNode tempNode1 = new SchedDataNode(
                                                    lChainStepsItem.getStepName(),
                                                    lChainStepsItem.getOwner(),
                                                    tempNode.getNodeId(),
                                                    lChainStepsItem.getId(),
                                                    "L",
                                                    mDataArea.getConnectId(),
                                                    SchedConsts.CHAIN_STEPS_SCREEN_NO,
                                                    mScreen.getScreenId(
                                                               SchedConsts.CHAIN_STEPS_SCREEN_NO,
                                                               mDataArea.getVersion()),
                                                    SchedConsts.CHAIN_STEPS_SCREEN_NO);
                            tempNode.add(tempNode1);

                            if ( SchedFile.getFileOption(3) ) {
                                for (int i4 = 0; i4 < mDataArea.programSize(); i4++)
                                {
                                    SchedDataArea.ProgramItem lProgramItem = mDataArea.getProgram(i4);
                                    if ( ( lChainStepsItem.getProgramName().equals(lProgramItem.getProgramName() ) ) &&
                                              ( lChainStepsItem.getProgramOwner().equals(lProgramItem.getOwner() ) ) )
                                    {

                                        SchedDataNode tempNode2 = new SchedDataNode(
                                                               "Program - " + lProgramItem.getProgramName(),
                                                               lProgramItem.getOwner(),
                                                               tempNode1.getNodeId(),
                                                               lProgramItem.getId(),
                                                               "L",
                                                               mDataArea.getConnectId(),
                                                               SchedConsts.PROGRAM_SCREEN_NO,
                                                               mScreen.getScreenId(
                                                                          SchedConsts.PROGRAM_SCREEN_NO,
                                                                          mDataArea.getVersion()),
                                                               SchedConsts.PROGRAM_SCREEN_NO);
                                        tempNode1.add(tempNode2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createCredentialSubTree(SchedDataNode   credentialsNode,
                                                SchedDataArea   mDataArea,
                                                SchedScreenArea mScreen) {

        if (mDataArea.CredentialsSize() > 0) {

            SchedDataArea.CredentialsItem gCredentialsItem = mDataArea.getCredentials(0);

            parentNode = new SchedDataNode(gCredentialsItem.getOwner(),
                                      null,
                                      credentialsNode.getNodeId(),
                                      mDataArea.getNextSeqNo(),
                                      "F",
                                      mDataArea.getConnectId(),
                                      0, 0,
                                      SchedConsts.CREDENTIALS_SCREEN_NO);
            credentialsNode.add(parentNode);

            for (int i2 = 0; i2 < mDataArea.CredentialsSize(); i2++) {

                SchedDataArea.CredentialsItem lCredentialsItem = mDataArea.getCredentials(i2);

                if ( ! gCredentialsItem.getOwner().equals(lCredentialsItem.getOwner())) {
                    gCredentialsItem = mDataArea.getCredentials(i2);

                    parentNode = new SchedDataNode(gCredentialsItem.getOwner(),
                                              null,
                                              credentialsNode.getNodeId(),
                                              mDataArea.getNextSeqNo(),
                                              "F",
                                              mDataArea.getConnectId(),
                                              0, 0,
                                              SchedConsts.CREDENTIALS_SCREEN_NO);
                    credentialsNode.add(parentNode);
                }

                SchedDataNode childNode = new SchedDataNode(lCredentialsItem.getCredentialName(),
                                         lCredentialsItem.getOwner(),
                                         parentNode.getNodeId(),
                                         lCredentialsItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.CREDENTIALS_SCREEN_NO,
                                         mScreen.getScreenId(
                                                     SchedConsts.CREDENTIALS_SCREEN_NO,
                                                     mDataArea.getVersion()),
                                         SchedConsts.CREDENTIALS_SCREEN_NO);
                parentNode.add(childNode);
            }
        }
    }

    private static void createDbDestSubTree(SchedDataNode   dbDestsNode,
                                            SchedDataArea   mDataArea,
                                            SchedScreenArea mScreen) {
        if ( mDataArea.DbDestsSize() > 0 ) {

            SchedDataArea.DbDestsItem gDbDestsItem = mDataArea.getDbDests(0);
            parentNode = new SchedDataNode(gDbDestsItem.getOwner(),
                                        null,
                                        dbDestsNode.getNodeId(),
                                        mDataArea.getNextSeqNo(),
                                        "F",
                                        mDataArea.getConnectId(),
                                        0, 0,
                                        SchedConsts.DB_DESTINATION_SCREEN_NO);
            dbDestsNode.add(parentNode);

            for (int i1 = 0; i1 < mDataArea.DbDestsSize(); i1++) {
                SchedDataArea.DbDestsItem lDbDestsItem = mDataArea.getDbDests(i1);


                if ( ! lDbDestsItem.getOwner().equals(gDbDestsItem.getOwner()) )
                {
                    gDbDestsItem = mDataArea.getDbDests(i1);

                    parentNode = new SchedDataNode(lDbDestsItem.getOwner(),
                                        null,
                                        dbDestsNode.getNodeId(),
                                        mDataArea.getNextSeqNo(),
                                        "F",
                                        mDataArea.getConnectId(),
                                        0, 0,
                                        SchedConsts.DB_DESTINATION_SCREEN_NO);
                    dbDestsNode.add(parentNode);
                }

                SchedDataNode childNode = new SchedDataNode(
                                        lDbDestsItem.getDestinationName(),
                                        lDbDestsItem.getOwner(),
                                        parentNode.getNodeId(),
                                        lDbDestsItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.DB_DESTINATION_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.DB_DESTINATION_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.DB_DESTINATION_SCREEN_NO);
                parentNode.add(childNode);

            }
        }
    }

    private static void createExtDestSubTree(SchedDataNode   extDestsNode,
                                             SchedDataArea   mDataArea,
                                             SchedScreenArea mScreen) {
        if ( mDataArea.ExtDestsSize() > 0 ) {

            for (int i2 = 0; i2 < mDataArea.ExtDestsSize(); i2++) {

                SchedDataArea.ExtDestsItem lExtDestsItem = mDataArea.getExtDests(i2);

                SchedDataNode childNode = new SchedDataNode(
                                         lExtDestsItem.getDestinationName(),
                                         "SYS",
                                         extDestsNode.getNodeId(),
                                         lExtDestsItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.EXT_DESTINATION_SCREEN_NO,
                                         mScreen.getScreenId(
                                                    SchedConsts.EXT_DESTINATION_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                         SchedConsts.EXT_DESTINATION_SCREEN_NO);
                extDestsNode.add(childNode);
            }
        }
    }

    private static void createFileWatchersSubTree(SchedDataNode   fileWatchersNode,
                                                  SchedDataArea   mDataArea,
                                                  SchedScreenArea mScreen) {

        if (mDataArea.fileWatchersSize() > 0) {

            SchedDataArea.FileWatchersItem gFileWatchersItem = mDataArea.getFileWatchers(0);

            parentNode = new SchedDataNode(gFileWatchersItem.getOwner(),
                                           null,
                                           fileWatchersNode.getNodeId(),
                                           mDataArea.getNextSeqNo(),
                                           "F",
                                           mDataArea.getConnectId(),
                                           0, 0,
                                           SchedConsts.FILE_WATCHERS_SCREEN_NO);
            fileWatchersNode.add(parentNode);

            for (int i2 = 0; i2 < mDataArea.fileWatchersSize(); i2++) {

                SchedDataArea.FileWatchersItem lFileWatchersItem = mDataArea.getFileWatchers(i2);

                if ( ! gFileWatchersItem.getOwner().equals(lFileWatchersItem.getOwner())) {
                    gFileWatchersItem = mDataArea.getFileWatchers(i2);

                    parentNode = new SchedDataNode(gFileWatchersItem.getOwner(),
                                                 null,
                                                 fileWatchersNode.getNodeId(),
                                                 mDataArea.getNextSeqNo(),
                                                 "F",
                                                 mDataArea.getConnectId(),
                                                 0, 0,
                                                 SchedConsts.FILE_WATCHERS_SCREEN_NO);
                    fileWatchersNode.add(parentNode);
                }
                SchedDataNode childNode = new SchedDataNode(
                                              lFileWatchersItem.getFileWatcherName(),
                                              lFileWatchersItem.getOwner(),
                                              parentNode.getNodeId(),
                                              lFileWatchersItem.getId(),
                                              "L",
                                              mDataArea.getConnectId(),
                                              SchedConsts.FILE_WATCHERS_SCREEN_NO,
                                              mScreen.getScreenId(
                                                         SchedConsts.FILE_WATCHERS_SCREEN_NO,
                                                         mDataArea.getVersion()),
                                              SchedConsts.FILE_WATCHERS_SCREEN_NO);
                parentNode.add(childNode);
            }
        }
    }

    private static void createJobClassSubTree(SchedDataNode   jobClassNode,
                                              SchedDataArea   mDataArea,
                                              SchedScreenArea mScreen) {

        if (mDataArea.jobClassSize() > 0) {

            for (int i2 = 0; i2 < mDataArea.jobClassSize(); i2++) {

                SchedDataArea.JobClassItem lJobClassItem = mDataArea.getJobClass(i2);

                childNode = new SchedDataNode(lJobClassItem.getJobClassName(),
                                        "SYS",
                                        jobClassNode.getNodeId(),
                                        lJobClassItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.JOB_CLASS_SCREEN_NO,
                                        mScreen.getScreenId(
                                                   SchedConsts.JOB_CLASS_SCREEN_NO,
                                                   mDataArea.getVersion()),
                                        SchedConsts.JOB_CLASS_SCREEN_NO);
                jobClassNode.add(childNode);

                if ( SchedFile.getFileOption(4) ) {
                    for (int i3 = 0; i3 < mDataArea.jobSize(); i3++)
                    {
                        SchedDataArea.JobItem lJobItem = mDataArea.getJob(i3);
                        if ( ( lJobClassItem.getJobClassName().equals(lJobItem.getJobClass() ) ) )
                        {

                            tempNode = new SchedDataNode("Job - " + lJobItem.getJobName(),
                                                       lJobItem.getOwner(),
                                                       lJobClassItem.getId(),
                                                       lJobItem.getId(),
                                                       "L",
                                                       mDataArea.getConnectId(),
                                                       SchedConsts.JOB_SCREEN_NO,
                                                       mScreen.getScreenId(
                                                                  SchedConsts.JOB_SCREEN_NO,
                                                                  mDataArea.getVersion()),
                                                       SchedConsts.JOB_SCREEN_NO);
                            childNode.add(tempNode);
                        }
                    }
                }
            }
        }
    }

    private static void createWindowSubTree(SchedDataNode   windowNode,
                                            SchedDataArea   mDataArea,
                                            SchedScreenArea mScreen) {
        if (mDataArea.WindowSize() > 0) {

            for (int i2 = 0; i2 < mDataArea.WindowSize(); i2++) {

                SchedDataArea.WindowItem lWindowItem = mDataArea.getWindow(i2);

                SchedDataNode nodeCell = new SchedDataNode(lWindowItem.getWindowName(),
                                         "SYS",
                                         windowNode.getNodeId(),
                                         lWindowItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.WINDOW_SCREEN_NO,
                                         mScreen.getScreenId(
                                                     SchedConsts.WINDOW_SCREEN_NO,
                                                     mDataArea.getVersion()),
                                         SchedConsts.WINDOW_SCREEN_NO);
                windowNode.add(nodeCell);

                createWindowLinkSubTree(lWindowItem, nodeCell, mDataArea, mScreen);
            }
        }
    }

    private static void createWindowLinkSubTree(SchedDataArea.WindowItem mWindowItem,
                                                SchedDataNode windowNode,
                                                SchedDataArea mDataArea,
                                                SchedScreenArea mScreen) {
        if ( ( SchedFile.getFileOption(5) ) &&
           (mWindowItem.getScheduleOwner() != null && mWindowItem.getScheduleName() != null) )
        {
            for (int i3 = 0; i3 < mDataArea.scheduleSize(); i3++) {
                SchedDataArea.ScheduleItem lScheduleItem = mDataArea.getSchedule(i3);
                if ( ( mWindowItem.getScheduleOwner().equals(lScheduleItem.getOwner()) ) &&
                     ( mWindowItem.getScheduleName().equals(lScheduleItem.getScheduleName()) ) ) {
                    tempNode = new SchedDataNode("Schedule - " + lScheduleItem.getScheduleName(),
                                               lScheduleItem.getOwner(),
                                               mWindowItem.getId(),
                                               lScheduleItem.getId(),
                                               "L",
                                               mDataArea.getConnectId(),
                                               SchedConsts.SCHEDULE_SCREEN_NO,
                                               mScreen.getScreenId(
                                                           SchedConsts.SCHEDULE_SCREEN_NO,
                                                           mDataArea.getVersion()),
                                               SchedConsts.SCHEDULE_SCREEN_NO);
                    windowNode.add(tempNode);
                }
            }
        }
    }

    private static void createWindowGroupSubTree(SchedDataNode   windowGroupNode,
                                                 SchedDataArea   mDataArea,
                                                 SchedScreenArea mScreen) {
        if (mDataArea.WindowGroupSize() > 0) {
            for (int i2 = 0; i2 < mDataArea.WindowGroupSize(); i2++) {
                SchedDataArea.WindowGroupItem lWindowGroupItem = mDataArea.getWindowGroup(i2);

                parentNode = new SchedDataNode(
                                    lWindowGroupItem.getWindowGroupName(),
                                    "SYS",
                                    windowGroupNode.getNodeId(),
                                    lWindowGroupItem.getId(),
                                    "L",
                                    mDataArea.getConnectId(),
                                    SchedConsts.WINDOW_GROUP_SCREEN_NO,
                                    mScreen.getScreenId(
                                                    SchedConsts.WINDOW_GROUP_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                    SchedConsts.WINDOW_GROUP_SCREEN_NO);
                windowGroupNode.add(parentNode);

            }
        }
    }

    private static void createGroupWindowSubTree(SchedDataNode   windowGroupNode,
                                                 SchedDataArea   mDataArea,
                                                 SchedScreenArea mScreen) {

        if (mDataArea.groupsSize() > 0) {

            for (int i2 = 0; i2 < mDataArea.groupsSize(); i2++) {

                SchedDataArea.GroupItem lGroupItem = mDataArea.getGroup(i2);

                if (lGroupItem.getGroupType().equals("WINDOW") ) {
                    parentNode = new SchedDataNode(
                                        lGroupItem.getGroupName(),
                                        "SYS",
                                        windowGroupNode.getNodeId(),
                                        lGroupItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.GROUP_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.GROUP_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.WINDOW_GROUP_SCREEN_NO);

                    windowGroupNode.add(parentNode);

                    for (int i3 = 0; i3 < mDataArea.groupMembersSize(); i3++) {

                        SchedDataArea.GroupMembersItem lGroupMembersItem = mDataArea.getGroupMembers(i3);

                        if ( lGroupMembersItem.getGroupName().equals(lGroupItem.getGroupName()) )
                        {


                            String lMembersName = lGroupMembersItem.getMemberName();

                            if ((lMembersName.indexOf(".") > 0) && (lMembersName.length()) > 0)
                            {
                                String lWindowName = lMembersName.substring(lMembersName.indexOf(".") + 2,
                                                                        lMembersName.length() - 1);

                                for (int i4 = 0; i4 < mDataArea.WindowSize(); i4++)
                                {

                                    SchedDataArea.WindowItem lWindowItem = mDataArea.getWindow(i4);

                                    if ( lWindowName.equals(lWindowItem.getWindowName()) )
                                    {
                                        tempNode = new SchedDataNode(lWindowName,
                                                           "SYS",
                                                           parentNode.getNodeId(),
                                                           lWindowItem.getId(),
                                                           "L",
                                                           mDataArea.getConnectId(),
                                                           SchedConsts.WINDOW_SCREEN_NO,
                                                           mScreen.getScreenId(
                                                                       SchedConsts.WINDOW_SCREEN_NO,
                                                                       mDataArea.getVersion()),
                                                           SchedConsts.WINDOW_SCREEN_NO);
                                        parentNode.add(tempNode);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createGroupDbDestSubTree(SchedDataNode   DbDestinationGroupNode,
                                                 SchedDataArea   mDataArea,
                                                 SchedScreenArea mScreen) {

        if (mDataArea.groupsSize() > 0) {
            SchedDataArea.GroupItem gGroupItem = mDataArea.getGroup(0);

            boolean firstGroup = true;

            for (int i2 = 0; i2 < mDataArea.groupsSize(); i2++) {


                SchedDataArea.GroupItem lGroupItem = mDataArea.getGroup(i2);

                if ( lGroupItem.getGroupType().equals("DB_DEST") ) {

                    if ((firstGroup) || ( ! gGroupItem.getOwner().equals(lGroupItem.getOwner()))) {

                        gGroupItem = mDataArea.getGroup(i2);

                        parentNode = new SchedDataNode(lGroupItem.getOwner(),
                                        null,
                                        DbDestinationGroupNode.getNodeId(),
                                        mDataArea.getNextSeqNo(),
                                        "F",
                                        mDataArea.getConnectId(),
                                        0, 0,
                                        SchedConsts.DB_GROUP_ICON_NO);

                        DbDestinationGroupNode.add(parentNode);
                        firstGroup = false;
                    }

                    childNode = new SchedDataNode(lGroupItem.getGroupName(),
                                   lGroupItem.getOwner(),
                                   parentNode.getNodeId(),
                                   lGroupItem.getId(),
                                   "L",
                                   mDataArea.getConnectId(),
                                   SchedConsts.GROUP_SCREEN_NO,
                                   mScreen.getScreenId(
                                               SchedConsts.GROUP_SCREEN_NO,
                                               mDataArea.getVersion()),
                                   SchedConsts.DB_GROUP_ICON_NO);

                    parentNode.add(childNode);

                    firstEntry = true;
                    for (int i3 = 0; i3 < mDataArea.groupMembersSize(); i3++) {

                        SchedDataArea.GroupMembersItem lGroupMembersItem = mDataArea.getGroupMembers(i3);

                        if (( lGroupMembersItem.getOwner().equals(lGroupItem.getOwner()) ) &&
                            ( lGroupMembersItem.getGroupName().equals(lGroupItem.getGroupName()) ) )
                        {

                            if (lGroupMembersItem.getMemberName().equals("LOCAL")) {
                                tempNode1 = new SchedDataNode(lGroupMembersItem.getMemberName(),
                                                           lGroupMembersItem.getOwner(),
                                                           tempNode.getNodeId(),
                                                           mDataArea.getNextSeqNo(),
                                                           "L",
                                                           mDataArea.getConnectId(),
                                                           0, 0,
                                                           SchedConsts.DB_DESTINATION_SCREEN_NO);
                                childNode.add(tempNode1);
                            }
                            for (int i4 = 0; i4 < mDataArea.DbDestsSize(); i4++)
                            {

                                SchedDataArea.DbDestsItem lDbDestsItem = mDataArea.getDbDests(i4);

                                if ( lGroupMembersItem.getMemberName().equals(lDbDestsItem.getDestinationName()) )
                                {

                                    tempNode1 = new SchedDataNode(lGroupMembersItem.getMemberName(),
                                                           lGroupMembersItem.getOwner(),
                                                           tempNode.getNodeId(),
                                                           lDbDestsItem.getId(),
                                                           "L",
                                                           mDataArea.getConnectId(),
                                                           SchedConsts.DB_DESTINATION_SCREEN_NO,
                                                           mScreen.getScreenId(
                                                                       SchedConsts.DB_DESTINATION_SCREEN_NO,
                                                                       mDataArea.getVersion()),
                                                           SchedConsts.DB_DESTINATION_SCREEN_NO);
                                    childNode.add(tempNode1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createGroupExtDestSubTree(SchedDataNode   ExtDestinationGroupNode,
                                                  SchedDataArea   mDataArea,
                                                  SchedScreenArea mScreen) {

        if (mDataArea.groupsSize() > 0) {
            SchedDataArea.GroupItem gGroupItem = mDataArea.getGroup(0);

            boolean firstGroup = true;

            for (int i2 = 0; i2 < mDataArea.groupsSize(); i2++) {


                SchedDataArea.GroupItem lGroupItem = mDataArea.getGroup(i2);

                if ( lGroupItem.getGroupType().equals("EXTERNAL_DEST") ) {

                    if ((firstGroup) || ( ! gGroupItem.getOwner().equals(lGroupItem.getOwner()))) {

                        gGroupItem = mDataArea.getGroup(i2);

                        parentNode = new SchedDataNode(lGroupItem.getOwner(),
                                        null,
                                        ExtDestinationGroupNode.getNodeId(),
                                        mDataArea.getNextSeqNo(),
                                        "F",
                                        mDataArea.getConnectId(),
                                        0, 0,
                                        SchedConsts.EXT_GROUP_ICON_NO);

                        ExtDestinationGroupNode.add(parentNode);
                        firstGroup = false;
                    }

                    childNode = new SchedDataNode(lGroupItem.getGroupName(),
                                   lGroupItem.getOwner(),
                                   parentNode.getNodeId(),
                                   lGroupItem.getId(),
                                   "L",
                                   mDataArea.getConnectId(),
                                   SchedConsts.GROUP_SCREEN_NO,
                                   mScreen.getScreenId(
                                               SchedConsts.DB_DESTINATION_SCREEN_NO,
                                               mDataArea.getVersion()),
                                   SchedConsts.EXT_GROUP_ICON_NO);

                    parentNode.add(childNode);

                    for (int i3 = 0; i3 < mDataArea.groupMembersSize(); i3++) {

                        SchedDataArea.GroupMembersItem lGroupMembersItem = mDataArea.getGroupMembers(i3);
                        // System.out.println( " 1. " + lGroupItem.getGroupName() + "--" +
                        //                    lGroupMembersItem.getGroupName());

                        if (( lGroupMembersItem.getOwner().equals(lGroupItem.getOwner()) ) &&
                            ( lGroupMembersItem.getGroupName().equals(lGroupItem.getGroupName()) ) )
                        {

                            if (lGroupMembersItem.getMemberName().equals("LOCAL")) {
                                tempNode1 = new SchedDataNode(lGroupMembersItem.getMemberName(),
                                                           lGroupMembersItem.getOwner(),
                                                           tempNode.getNodeId(),
                                                           mDataArea.getNextSeqNo(),
                                                           "L",
                                                           mDataArea.getConnectId(),
                                                           0, 0,
                                                           SchedConsts.EXT_DESTINATION_SCREEN_NO);
                                childNode.add(tempNode1);
                            }
                            else {
                                for (int i4 = 0; i4 < mDataArea.ExtDestsSize(); i4++)
                                {

                                    SchedDataArea.ExtDestsItem lExtDestsItem = mDataArea.getExtDests(i4);

                                    if ( lGroupMembersItem.getMemberName().equals(lExtDestsItem.getDestinationName()) )
                                    {

                                        tempNode1 = new SchedDataNode(lGroupMembersItem.getMemberName(),
                                                           lGroupMembersItem.getOwner(),
                                                           tempNode.getNodeId(),
                                                           lExtDestsItem.getId(),
                                                           "L",
                                                           mDataArea.getConnectId(),
                                                           SchedConsts.EXT_DESTINATION_SCREEN_NO,
                                                           mScreen.getScreenId(
                                                                       SchedConsts.EXT_DESTINATION_SCREEN_NO,
                                                                       mDataArea.getVersion()),
                                                           SchedConsts.EXT_DESTINATION_SCREEN_NO);
                                        childNode.add(tempNode1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void dropJobTreeItem(SchedDataArea.JobItem mJobItem,
                                       DefaultTreeModel mTreeModel,
                                       SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.JOBS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mJobItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(tempNode1, mJobItem.getJobName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropJobArgTreeItem(SchedDataArea.JobArgsItem mJobArgsItem,
                                          DefaultTreeModel mTreeModel,
                                          SchedDataNode  rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.JOBS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mJobArgsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mJobArgsItem.getJobName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.JOB_ARGS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            Enumeration e2 = tempNode1.children();

            boolean mContinue1 = true;
            while (  mContinue1 ) {
                if ( e2.hasMoreElements() ) {
                    SchedDataNode childNode = (SchedDataNode)e2.nextElement();
                    // System.out.println(" G1 " + tempNode.getNodeName());

                    if ( childNode.getNodeId() == mJobArgsItem.getId() ) {

                        mTreeModel.removeNodeFromParent(childNode);
                        mContinue1 = false;
                    }
                }
                else {
                    mContinue1 = false;
                }
            }
        }
    }

    public static void dropNotificationTreeItem(SchedDataArea.NotificationsItem mNotificationsItem,
                                                DefaultTreeModel mTreeModel,
                                                SchedDataNode  rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.JOBS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mNotificationsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mNotificationsItem.getJobName(), 1);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.NOTIFICATIONS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            Enumeration e2 = tempNode1.children();

            boolean mContinue1 = true;
            while (  mContinue1 ) {
                if ( e2.hasMoreElements() ) {
                    SchedDataNode childNode = (SchedDataNode)e2.nextElement();
                    // System.out.println(" G1 " + childNode.getNodeName());

                    if ( childNode.getNodeId() == mNotificationsItem.getId() ) {

                        mTreeModel.removeNodeFromParent(childNode);
                        mContinue1 = false;
                    }
                }
                else {
                    mContinue1 = false;
                }
            }
        }

    }

    public static void dropProgramTreeItem(SchedDataArea.ProgramItem mProgramItem,
                                           DefaultTreeModel mTreeModel,
                                           SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.PROGRAMS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mProgramItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(tempNode1, mProgramItem.getProgramName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropProgramArgTreeItem(SchedDataArea.ProgramArgsItem mProgramArgsItem,
                                              DefaultTreeModel mTreeModel,
                                              SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.PROGRAMS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mProgramArgsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mProgramArgsItem.getProgramName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.PROGRAM_ARGS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(tempNode1, mProgramArgsItem.getArgumentName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropScheduleTreeItem(SchedDataArea.ScheduleItem mScheduleItem,
                                            DefaultTreeModel mTreeModel,
                                            SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.SCHEDULES_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mScheduleItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            childNode = findChildNode(tempNode1, mScheduleItem.getScheduleName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropChainTreeItem(SchedDataArea.ChainsItem mChainsItem,
                                         DefaultTreeModel mTreeModel,
                                         SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.CHAINS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(tempNode1, mChainsItem.getChainName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropChainRuleTreeItem(SchedDataArea.ChainRulesItem mChainRulesItem,
                                             DefaultTreeModel mTreeModel,
                                             SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.CHAINS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainRulesItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainRulesItem.getChainName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.CHAIN_RULES_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            childNode = findChildNode(tempNode1, mChainRulesItem.getRuleName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropChainStepTreeItem(SchedDataArea.ChainStepsItem mChainStepsItem,
                                             DefaultTreeModel mTreeModel,
                                             SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.CHAINS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainStepsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainStepsItem.getChainName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.CHAIN_STEPS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(tempNode1, mChainStepsItem.getStepName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropJobClassTreeItem(SchedDataArea.JobClassItem mJobClassItem,
                                            DefaultTreeModel mTreeModel,
                                            SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.JOB_CLASS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            childNode = findChildNode(tempNode1, mJobClassItem.getJobClassName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropWindowTreeItem(SchedDataArea.WindowItem mWindowItem,
                                          DefaultTreeModel mTreeModel,
                                          SchedDataNode rootNode) {

        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.WINDOWS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            childNode = findChildNode(tempNode1, mWindowItem.getWindowName(), 1);
            if (childNode == null) mFound = false;
            else  mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropWindowGroupTreeItem(SchedDataArea.GroupItem mGroupItem,
                                               DefaultTreeModel mTreeModel,
                                               SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.GROUP_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.WINDOW_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            childNode = findChildNode(tempNode1, mGroupItem.getGroupName(), 1);

            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropDbDestGroupTreeItem(SchedDataArea.GroupItem mGroupItem,
                                               DefaultTreeModel mTreeModel,
                                               SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.GROUP_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.DBDEST_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mGroupItem.getOwner(), 1);
            if (parentNode == null) mFound = false;

            if (mFound) {
                childNode = findChildNode(parentNode, mGroupItem.getGroupName(), 1);
                mTreeModel.removeNodeFromParent(childNode);
            }
        }
    }

    public static void dropExtDestGroupTreeItem(SchedDataArea.GroupItem mGroupItem,
                                                DefaultTreeModel mTreeModel,
                                                SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.GROUP_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.EXTDEST_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mGroupItem.getOwner(), 1);
            if (parentNode == null) mFound = false;

            if (mFound) {
                childNode = findChildNode(parentNode, mGroupItem.getGroupName(), 1);
                mTreeModel.removeNodeFromParent(childNode);
            }
        }
    }

    public static void dropCredentialTreeItem(SchedDataArea.CredentialsItem mCredentialsItem,
                                              DefaultTreeModel mTreeModel,
                                              SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.CREDENTIALS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            parentNode = findChildNode(tempNode1, mCredentialsItem.getOwner(), 1);
            if (parentNode == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(parentNode, mCredentialsItem.getCredentialName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void dropFileWatcherTreeItem(SchedDataArea.FileWatchersItem mFileWatchersItem,
                                               DefaultTreeModel mTreeModel,
                                               SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.FILE_WATCHERS_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            parentNode = findChildNode(tempNode1, mFileWatchersItem.getOwner(), 1);
            if (parentNode == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(parentNode, mFileWatchersItem.getFileWatcherName(), 1);
            if (childNode == null) mFound = false;
            else mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void removeWinGroupAssignTreeItem(String  mWindowGroupName,
                                                    String  mWindowName,
                                                    DefaultTreeModel mTreeModel,
                                                    SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.GROUP_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.WINDOW_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mWindowGroupName, 1);
            if (parentNode == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(parentNode, mWindowName, 1);
            if (childNode == null) mFound = false;
        }

        if (mFound) {
            mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void removeDbDestGroupAssignTreeItem(String  mOwner,
                                                       String  mGroupName,
                                                       String  mDbDestination,
                                                       DefaultTreeModel mTreeModel,
                                                       SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.GROUP_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.DBDEST_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mOwner.toUpperCase(), 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mGroupName, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.DBDEST_TREE, 0);
            if (parentNode == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(parentNode, mDbDestination, 1);
            if (childNode == null) mFound = false;
        }

        if (mFound) {
            mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void removeExtDestGroupAssignTreeItem(String  mOwner,
                                                        String  mGroupName,
                                                        String  mDbDestination,
                                                        DefaultTreeModel mTreeModel,
                                                        SchedDataNode rootNode) {
        boolean mFound = true;

        SchedDataNode tempNode1 = findChildNode(rootNode, SchedConsts.GROUP_TREE, 0);
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.EXTDEST_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mOwner.toUpperCase(), 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mGroupName, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.EXTDEST_TREE, 0);
            if (parentNode == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(parentNode, mDbDestination, 1);
            if (childNode == null) mFound = false;
        }

        if (mFound) {
            mTreeModel.removeNodeFromParent(childNode);
        }
    }

    public static void createJobTreeItem(SchedDataArea.JobItem mJobItem,
                                         DefaultTreeModel      mTreeModel,
                                         SchedDataNode         rootNode,
                                         SchedDataArea         mDataArea,
                                         SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.JOBS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            // System.out.println("A1 --" + mJobItem.getOwner() + "--" + mJobItem.getJobName());
            parentNode = findChildNode(tempNode1, mJobItem.getOwner(), 1);

            if (parentNode == null) {

                parentNode = new SchedDataNode(mJobItem.getOwner(),
                                    null,
                                    tempNode1.getNodeId(),
                                    mDataArea.getNextSeqNo(),
                                    "F", 
                                    mDataArea.getConnectId(),
                                    0, 0,
                                    SchedConsts.JOB_SCREEN_NO);

                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }

            childNode = findChildNode(parentNode, mJobItem.getJobName(), 1);
            if (childNode == null) {

                childNode = new SchedDataNode(mJobItem.getJobName(),
                                    mJobItem.getOwner(),
                                    parentNode.getNodeId(),
                                    mJobItem.getId(),
                                    "L",
                                    mDataArea.getConnectId(),
                                    SchedConsts.JOB_SCREEN_NO,
                                    mScreen.getScreenId(
                                                SchedConsts.JOB_SCREEN_NO,
                                                mDataArea.getVersion()),
                                    SchedConsts.JOB_SCREEN_NO);
                parentNode.add(childNode);

                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);

            }
        }
    }

    public static void createJobArgsTreeItem(SchedDataArea.JobArgsItem mJobArgsItem,
                                             DefaultTreeModel mTreeModel,
                                             SchedDataNode    rootNode,
                                             SchedDataArea    mDataArea,
                                             SchedScreenArea  mScreen) {

        Integer Int1 = new Integer(mJobArgsItem.getArgumentPosition());
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.JOBS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mJobArgsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mJobArgsItem.getJobName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.JOB_ARGS_TREE, 0);
            if (parentNode == null) {
                parentNode = new SchedDataNode(SchedConsts.JOB_ARGS_TREE,
                                        null,
                                        tempNode1.getNodeId(),
                                        mDataArea.getNextSeqNo(),
                                        "F",
                                        mDataArea.getConnectId(),
                                        0, 0,
                                        SchedConsts.JOB_ARGS_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }
            childNode = findChildNode(parentNode, Int1.toString(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(Int1.toString(),
                                    mJobArgsItem.getOwner(),
                                    parentNode.getNodeId(),
                                    mJobArgsItem.getId(),
                                    "L",
                                    mDataArea.getConnectId(),
                                    SchedConsts.JOB_ARGS_SCREEN_NO,
                                    mScreen.getScreenId(
                                                SchedConsts.JOB_ARGS_SCREEN_NO,
                                                mDataArea.getVersion()),
                                    SchedConsts.JOB_ARGS_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createNotificationTreeItem(SchedDataArea.NotificationsItem mNotificationsItem,
                                                  DefaultTreeModel mTreeModel,
                                                  SchedDataNode    rootNode,
                                                  SchedDataArea    mDataArea,
                                                  SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.JOBS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mNotificationsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mNotificationsItem.getJobName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.NOTIFICATIONS_TREE, 0);
            if (parentNode == null) {

                parentNode = new SchedDataNode(SchedConsts.NOTIFICATIONS_TREE,
                                            null,
                                            tempNode1.getNodeId(),
                                            mDataArea.getNextSeqNo(),
                                            "F",
                                            mDataArea.getConnectId(),
                                            0, 0,
                                            SchedConsts.NOTIFICATIONS_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, 0);
            }

            Enumeration e1 = parentNode.children();

            int mEntryNo = 0;
            boolean mContinue = true;
            while (  mContinue ) {

                if ( e1.hasMoreElements() ) {
                    tempNode1 = (SchedDataNode)e1.nextElement();
                    mEntryNo = mEntryNo + 1;
                }
                else {
                    mContinue = false;
                }
            }
            Integer Int1 = new Integer(mEntryNo + 1);
            childNode = new SchedDataNode(Int1.toString(),
                                    mNotificationsItem.getOwner(),
                                    parentNode.getNodeId(),
                                    mNotificationsItem.getId(),
                                    "L",
                                    mDataArea.getConnectId(),
                                    SchedConsts.NOTIFICATIONS_SCREEN_NO,
                                    mScreen.getScreenId(
                                                SchedConsts.NOTIFICATIONS_SCREEN_NO,
                                                mDataArea.getVersion()),
                                    SchedConsts.NOTIFICATIONS_SCREEN_NO);
            parentNode.add(childNode);
            mTreeModel.insertNodeInto(childNode, parentNode, mEntryNo);
        }
    }

    public static void createProgramTreeItem(SchedDataArea.ProgramItem mProgramItem,
                                             DefaultTreeModel mTreeModel,
                                             SchedDataNode    rootNode,
                                             SchedDataArea    mDataArea,
                                             SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.PROGRAMS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mProgramItem.getOwner(), 1);
            if (parentNode == null) {
                parentNode = new SchedDataNode(mProgramItem.getOwner(),
                                    null,
                                    tempNode1.getNodeId(),
                                    mDataArea.getNextSeqNo(),
                                    "F", 
                                    mDataArea.getConnectId(),
                                    0, 0,
                                    SchedConsts.PROGRAM_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, 0);
            }

            childNode = findChildNode(parentNode, mProgramItem.getProgramName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mProgramItem.getProgramName(),
                                mProgramItem.getOwner(),
                                parentNode.getNodeId(),
                                mProgramItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.PROGRAM_SCREEN_NO,
                                mScreen.getScreenId(
                                            SchedConsts.PROGRAM_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.PROGRAM_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createProgramArgsTreeItem(SchedDataArea.ProgramArgsItem mProgramArgsItem,
                                                 DefaultTreeModel mTreeModel,
                                                 SchedDataNode    rootNode,
                                                 SchedDataArea    mDataArea,
                                                 SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.PROGRAMS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mProgramArgsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mProgramArgsItem.getProgramName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.PROGRAM_ARGS_TREE, 0);
            if (parentNode == null) {
                parentNode = new SchedDataNode(SchedConsts.PROGRAM_ARGS_TREE,
                                        null,
                                        tempNode1.getNodeId(),
                                        mDataArea.getNextSeqNo(),
                                        "F",
                                        mDataArea.getConnectId(),
                                        0, 0,
                                        SchedConsts.PROGRAM_ARGS_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, 0);
            }

            if (mProgramArgsItem.getArgumentName() != null)
                childNode = findChildNode(parentNode, mProgramArgsItem.getArgumentName(), 1);
            else {
                childNode = null;
                elementNo = 0;
            }

            if (childNode == null) {
                childNode = new SchedDataNode(mProgramArgsItem.getArgumentName(),
                                         mProgramArgsItem.getOwner(),
                                         parentNode.getNodeId(),
                                         mProgramArgsItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.PROGRAM_ARGS_SCREEN_NO,
                                         mScreen.getScreenId(
                                                     SchedConsts.PROGRAM_ARGS_SCREEN_NO,
                                                     mDataArea.getVersion()),
                                         SchedConsts.PROGRAM_ARGS_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createScheduleTreeItem(SchedDataArea.ScheduleItem mScheduleItem,
                                              DefaultTreeModel mTreeModel,
                                              SchedDataNode    rootNode,
                                              SchedDataArea    mDataArea,
                                              SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.SCHEDULES_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mScheduleItem.getOwner(), 1);
            if (parentNode == null) {
                parentNode = new SchedDataNode(mScheduleItem.getOwner(),
                                        null,
                                        tempNode1.getNodeId(),
                                        mDataArea.getNextSeqNo(),
                                        "F", 
                                        mDataArea.getConnectId(),
                                        0, 0,
                                        SchedConsts.SCHEDULE_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }
            childNode = findChildNode(parentNode, mScheduleItem.getScheduleName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mScheduleItem.getScheduleName(),
                                        mScheduleItem.getOwner(),
                                        parentNode.getNodeId(),
                                        mScheduleItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.SCHEDULE_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.SCHEDULE_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.SCHEDULE_SCREEN_NO);

                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createChainTreeItem(SchedDataArea.ChainsItem mChainsItem,
                                               DefaultTreeModel mTreeModel,
                                               SchedDataNode    rootNode,
                                               SchedDataArea    mDataArea,
                                               SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.CHAINS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mChainsItem.getOwner(), 1);
            if (parentNode == null) {
                parentNode = new SchedDataNode(mChainsItem.getOwner(),
                                    null,
                                    tempNode1.getNodeId(),
                                    mDataArea.getNextSeqNo(),
                                    "F", 
                                    mDataArea.getConnectId(),
                                    0, 0,
                                    SchedConsts.CHAINS_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }

            childNode = findChildNode(parentNode, mChainsItem.getChainName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mChainsItem.getChainName(),
                                    mChainsItem.getOwner(),
                                    parentNode.getNodeId(),
                                    mDataArea.getNextSeqNo(),
                                    "L", 
                                    mDataArea.getConnectId(),
                                    SchedConsts.CHAINS_SCREEN_NO,
                                    mScreen.getScreenId(
                                                SchedConsts.CHAINS_SCREEN_NO,
                                                mDataArea.getVersion()),
                                    SchedConsts.CHAINS_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createChainRuleTreeItem(SchedDataArea.ChainRulesItem mChainRulesItem,
                                               DefaultTreeModel mTreeModel,
                                               SchedDataNode    rootNode,
                                               SchedDataArea    mDataArea,
                                               SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.CHAINS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainRulesItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainRulesItem.getChainName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.CHAIN_RULES_TREE, 0);
            if (parentNode == null) {
                parentNode = new SchedDataNode(SchedConsts.CHAIN_RULES_TREE,
                                          null,
                                          tempNode1.getNodeId(),
                                          mDataArea.getNextSeqNo(),
                                          "F", 
                                          mDataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.CHAIN_RULES_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, 0);
            }

            childNode = findChildNode(parentNode, mChainRulesItem.getRuleName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mChainRulesItem.getRuleName(),
                                         mChainRulesItem.getOwner(),
                                         parentNode.getNodeId(),
                                         mChainRulesItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.CHAIN_RULES_SCREEN_NO,
                                         mScreen.getScreenId(
                                                     SchedConsts.CHAIN_RULES_SCREEN_NO,
                                                     mDataArea.getVersion()),
                                         SchedConsts.CHAIN_RULES_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createChainStepTreeItem(SchedDataArea.ChainStepsItem mChainStepsItem,
                                               DefaultTreeModel mTreeModel,
                                               SchedDataNode    rootNode,
                                               SchedDataArea    mDataArea,
                                               SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.CHAINS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainStepsItem.getOwner(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mChainStepsItem.getChainName(), 1);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.CHAIN_STEPS_TREE, 0);
            if (parentNode == null) {
                parentNode = new SchedDataNode(SchedConsts.CHAIN_STEPS_TREE,
                                          null,
                                          tempNode.getNodeId(),
                                          mDataArea.getNextSeqNo(),
                                          "F", 
                                          mDataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.CHAIN_STEPS_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, 0);
            }

            childNode = findChildNode(parentNode, mChainStepsItem.getStepName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mChainStepsItem.getStepName(),
                                         mChainStepsItem.getOwner(),
                                         parentNode.getNodeId(),
                                         mChainStepsItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.CHAIN_STEPS_SCREEN_NO,
                                         mScreen.getScreenId(
                                                     SchedConsts.CHAIN_STEPS_SCREEN_NO,
                                                     mDataArea.getVersion()),
                                         SchedConsts.CHAIN_STEPS_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createJobClassTreeItem(SchedDataArea.JobClassItem mJobClassItem,
                                              DefaultTreeModel mTreeModel,
                                              SchedDataNode    rootNode,
                                              SchedDataArea    mDataArea,
                                              SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.JOB_CLASS_TREE, 0);
            if (parentNode == null) mFound = false;
        }
        if (mFound) {
            childNode = findChildNode(parentNode, mJobClassItem.getJobClassName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mJobClassItem.getJobClassName(),
                                "SYS",
                                parentNode.getNodeId(),
                                mJobClassItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.JOB_CLASS_SCREEN_NO,
                                mScreen.getScreenId(
                                            SchedConsts.JOB_CLASS_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.JOB_CLASS_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createWindowTreeItem(SchedDataArea.WindowItem mWindowItem,
                                            DefaultTreeModel mTreeModel,
                                            SchedDataNode    rootNode,
                                            SchedDataArea    mDataArea,
                                            SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.WINDOWS_TREE, 0);
            if (parentNode == null) mFound = false;
        }
        if (mFound) {
            childNode = findChildNode(parentNode, mWindowItem.getWindowName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mWindowItem.getWindowName(),
                                "SYS",
                                parentNode.getNodeId(),
                                mWindowItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.WINDOW_SCREEN_NO,
                                mScreen.getScreenId(
                                            SchedConsts.WINDOW_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.WINDOW_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createWindowGroupTreeItem(SchedDataArea.WindowGroupItem mWindowGroupItem,
                                                 DefaultTreeModel mTreeModel,
                                                 SchedDataNode    rootNode,
                                                 SchedDataArea    mDataArea,
                                                 SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            SchedDataNode parentNode = findChildNode(tempNode1, SchedConsts.WINDOW_GROUP_TREE, 0);
            if (parentNode == null) mFound = false;

            if (mFound) {
                childNode = findChildNode(parentNode, mWindowGroupItem.getWindowGroupName(), 1);
                if (childNode == null) {
                    childNode = new SchedDataNode(mWindowGroupItem.getWindowGroupName(),
                                "SYS",
                                parentNode.getNodeId(),
                                mWindowGroupItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.WINDOW_GROUP_SCREEN_NO,
                                mScreen.getScreenId(
                                            SchedConsts.WINDOW_GROUP_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.WINDOW_GROUP_SCREEN_NO);

                    parentNode.add(childNode);
                    mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
                }
            }
        }
    }

    public static void createGroupWindowTreeItem(SchedDataArea.GroupItem mGroupItem,
                                                 DefaultTreeModel  mTreeModel,
                                                 SchedDataNode     rootNode,
                                                 SchedDataArea     mDataArea,
                                                 SchedScreenArea   mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, SchedConsts.WINDOW_GROUP_TREE, 0);
            if (parentNode == null) mFound = false;
        }

        if (mFound) {
            childNode = findChildNode(parentNode, mGroupItem.getGroupName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(mGroupItem.getGroupName(),
                                "SYS",
                                parentNode.getNodeId(),
                                mGroupItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.GROUP_SCREEN_NO,
                                mScreen.getScreenId(
                                            SchedConsts.GROUP_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.WINDOW_GROUP_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createGroupDbDestTreeItem(SchedDataArea.GroupItem  mGroupItem,
                                                 DefaultTreeModel mTreeModel,
                                                 SchedDataNode    rootNode,
                                                 SchedDataArea    mDataArea,
                                                 SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.DBDEST_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mGroupItem.getOwner(), 1);

            if (parentNode == null) {
                parentNode = new SchedDataNode(mGroupItem.getOwner(),
                                               null,
                                               tempNode1.getNodeId(),
                                               mDataArea.getNextSeqNo(),
                                               "F",
                                               mDataArea.getConnectId(),
                                               0, 0,
                                               SchedConsts.DB_GROUP_ICON_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }

            childNode = findChildNode(parentNode, mGroupItem.getGroupName(), 1);

            if (childNode == null) {
                childNode = new SchedDataNode(mGroupItem.getGroupName(),
                                          mGroupItem.getOwner(),
                                          parentNode.getNodeId(),
                                          mGroupItem.getId(),
                                          "L",
                                          mDataArea.getConnectId(),
                                          SchedConsts.GROUP_SCREEN_NO,
                                          mScreen.getScreenId(
                                                      SchedConsts.GROUP_SCREEN_NO,
                                                      mDataArea.getVersion()),
                                          SchedConsts.DB_GROUP_ICON_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createGroupExtDestTreeItem(SchedDataArea.GroupItem  mGroupItem,
                                                  DefaultTreeModel mTreeModel,
                                                  SchedDataNode    rootNode,
                                                  SchedDataArea    mDataArea,
                                                  SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.EXTDEST_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mGroupItem.getOwner(), 1);

            if (parentNode == null) {
                parentNode = new SchedDataNode(mGroupItem.getOwner(),
                                               null,
                                               tempNode1.getNodeId(),
                                               mDataArea.getNextSeqNo(),
                                               "F",
                                               mDataArea.getConnectId(),
                                               0, 0,
                                               SchedConsts.EXT_GROUP_ICON_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }

            childNode = findChildNode(parentNode, mGroupItem.getGroupName(), 1);

            if (childNode == null) {
                childNode = new SchedDataNode(mGroupItem.getGroupName(),
                                          mGroupItem.getOwner(),
                                          parentNode.getNodeId(),
                                          mGroupItem.getId(),
                                          "L",
                                          mDataArea.getConnectId(),
                                          SchedConsts.GROUP_SCREEN_NO,
                                          mScreen.getScreenId(
                                                      SchedConsts.GROUP_SCREEN_NO,
                                                      mDataArea.getVersion()),
                                          SchedConsts.EXT_GROUP_ICON_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createWinGroupAssignTreeItem(SchedDataArea.WinGroupMembersItem mWinGroupMembersItem,
                                                    DefaultTreeModel mTreeModel,
                                                    SchedDataNode rootNode,
                                                    SchedDataArea mDataArea,
                                                    SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.WINDOW_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mWinGroupMembersItem.getWindowGroupName(), 0);
            if (parentNode == null) mFound = false;
        }
        if (mFound) {
            // System.out.println(" S1 " + mWinGroupMembersItem.getWindowName());
            childNode = findChildNode(parentNode, mWinGroupMembersItem.getWindowName(), 0);
            if (childNode == null) {

                for (int i4 = 0; i4 < mDataArea.WindowSize(); i4++)
                {

                    SchedDataArea.WindowItem lWindowItem = mDataArea.getWindow(i4);

                    if ( mWinGroupMembersItem.getWindowName().equals(lWindowItem.getWindowName()) )
                    {
                        childNode = new SchedDataNode(mWinGroupMembersItem.getWindowName(),
                                        "SYS",
                                        parentNode.getNodeId(),
                                        lWindowItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.WINDOW_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.WINDOW_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.WINDOW_SCREEN_NO);
                        parentNode.add(childNode);
                        mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
                        break;
                    }
                }
            }
        }
    }

    public static void createDbDestGroupAssignTreeItem(SchedDataArea.GroupMembersItem mGroupMembersItem,
                                                       DefaultTreeModel mTreeModel,
                                                       SchedDataNode    rootNode,
                                                       SchedDataArea    mDataArea,
                                                       SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.DBDEST_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mGroupMembersItem.getOwner(), 0);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            parentNode = findChildNode(tempNode1, mGroupMembersItem.getGroupName(), 0);
            if (parentNode == null) mFound = false;
        }
        if (mFound) {

            childNode = findChildNode(parentNode, mGroupMembersItem.getMemberName(), 1);
            if (childNode == null) {

                if (mGroupMembersItem.getMemberName().equals("LOCAL")) {
                    childNode = new SchedDataNode(mGroupMembersItem.getMemberName(),
                                                  mGroupMembersItem.getOwner(),
                                                  parentNode.getNodeId(),
                                                  mDataArea.getNextSeqNo(),
                                                  "L",
                                                  mDataArea.getConnectId(),
                                                  0, 0,
                                                  SchedConsts.DB_DESTINATION_SCREEN_NO);
                    parentNode.add(childNode);
                    mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
                }
                for (int i4 = 0; i4 < mDataArea.DbDestsSize(); i4++)
                {

                    SchedDataArea.DbDestsItem lDbDestsItem = mDataArea.getDbDests(i4);

                    if ( mGroupMembersItem.getMemberName().equals(lDbDestsItem.getDestinationName()) )
                    {
                        childNode = new SchedDataNode(
                                        mGroupMembersItem.getMemberName(),
                                        mGroupMembersItem.getOwner(),
                                        parentNode.getNodeId(),
                                        lDbDestsItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.DB_DESTINATION_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.DB_DESTINATION_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.DB_DESTINATION_SCREEN_NO);
                        parentNode.add(childNode);
                        mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
                        break;
                    }
                }
            }
        }
    }

    public static void createExtDestGroupAssignTreeItem(SchedDataArea.GroupMembersItem mGroupMembersItem,
                                                        DefaultTreeModel mTreeModel,
                                                        SchedDataNode    rootNode,
                                                        SchedDataArea    mDataArea,
                                                        SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.EXTDEST_GROUP_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            tempNode1 = findChildNode(tempNode1, mGroupMembersItem.getOwner(), 0);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            parentNode = findChildNode(tempNode1, mGroupMembersItem.getGroupName(), 0);
            if (parentNode == null) mFound = false;
        }
        if (mFound) {

            childNode = findChildNode(parentNode, mGroupMembersItem.getMemberName(), 1);
            if (childNode == null) {

                if (mGroupMembersItem.getMemberName().equals("LOCAL")) {
                    childNode = new SchedDataNode(mGroupMembersItem.getMemberName(),
                                                  mGroupMembersItem.getOwner(),
                                                  parentNode.getNodeId(),
                                                  mDataArea.getNextSeqNo(),
                                                  "L",
                                                  mDataArea.getConnectId(),
                                                  0, 0,
                                                  SchedConsts.EXT_DESTINATION_SCREEN_NO);
                    parentNode.add(childNode);
                    mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
                }
                for (int i4 = 0; i4 < mDataArea.ExtDestsSize(); i4++)
                {

                    SchedDataArea.ExtDestsItem lExtDestsItem = mDataArea.getExtDests(i4);

                    if ( mGroupMembersItem.getMemberName().equals(lExtDestsItem.getDestinationName()) )
                    {
                        childNode = new SchedDataNode(
                                        mGroupMembersItem.getMemberName(),
                                        mGroupMembersItem.getOwner(),
                                        parentNode.getNodeId(),
                                        lExtDestsItem.getId(),
                                        "L",
                                        mDataArea.getConnectId(),
                                        SchedConsts.EXT_DESTINATION_SCREEN_NO,
                                        mScreen.getScreenId(
                                                    SchedConsts.EXT_DESTINATION_SCREEN_NO,
                                                    mDataArea.getVersion()),
                                        SchedConsts.EXT_DESTINATION_SCREEN_NO);
                        parentNode.add(childNode);
                        mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
                        break;
                    }
                }
            }
        }
    }

    public static void createDBDestTreeItem(SchedDataArea.DbDestsItem  mDbDestsItem,
                                            DefaultTreeModel mTreeModel,
                                            SchedDataNode    rootNode,
                                            SchedDataArea    mDataArea,
                                            SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.DESTINATIONS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.DBDEST_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }
        if (mFound) {
            parentNode = findChildNode(tempNode1, mDbDestsItem.getOwner(), 1);
            if (parentNode == null) {
                parentNode = new SchedDataNode(
                                          mDbDestsItem.getOwner(),
                                          null,
                                          tempNode1.getNodeId(),
                                          mDataArea.getNextSeqNo(),
                                          "F", 
                                          mDataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.DB_DESTINATION_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }
            childNode = findChildNode(parentNode, mDbDestsItem.getDestinationName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(
                                         mDbDestsItem.getDestinationName(),
                                         mDbDestsItem.getOwner(),
                                         parentNode.getNodeId(),
                                         mDbDestsItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.DB_DESTINATION_SCREEN_NO,
                                         mScreen.getScreenId(
                                                     SchedConsts.DB_DESTINATION_SCREEN_NO,
                                                     mDataArea.getVersion()),
                                         SchedConsts.DB_DESTINATION_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createCredentialTreeItem(SchedDataArea.CredentialsItem  mCredentialsItem,
                                                DefaultTreeModel  mTreeModel,
                                                SchedDataNode     rootNode,
                                                SchedDataArea     mDataArea,
                                                SchedScreenArea   mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = SchedTree.findChildNode(tempNode1, SchedConsts.CREDENTIALS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mCredentialsItem.getOwner(), 1);
            if (parentNode == null) {
                parentNode = new SchedDataNode(
                                          mCredentialsItem.getOwner(),
                                          null,
                                          tempNode1.getNodeId(),
                                          mDataArea.getNextSeqNo(),
                                          "F", 
                                          mDataArea.getConnectId(),
                                          0, 0,
                                          SchedConsts.CREDENTIALS_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }
            childNode = findChildNode(parentNode, mCredentialsItem.getCredentialName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(
                                         mCredentialsItem.getCredentialName(),
                                         mCredentialsItem.getOwner(),
                                         parentNode.getNodeId(),
                                         mCredentialsItem.getId(),
                                         "L",
                                         mDataArea.getConnectId(),
                                         SchedConsts.CREDENTIALS_SCREEN_NO,
                                         mScreen.getScreenId(
                                                     SchedConsts.CREDENTIALS_SCREEN_NO,
                                                     mDataArea.getVersion()),
                                         SchedConsts.CREDENTIALS_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    public static void createFileWatcherTreeItem(SchedDataArea.FileWatchersItem  mFileWatchersItem,
                                                 DefaultTreeModel mTreeModel,
                                                 SchedDataNode    rootNode,
                                                 SchedDataArea    mDataArea,
                                                 SchedScreenArea  mScreen) {
        boolean mFound = true;
        SchedDataNode tempNode1 = findConnectionNode(rootNode, mDataArea.getConnectId());
        if (tempNode1 == null) mFound = false;

        if (mFound) {
            tempNode1 = findChildNode(tempNode1, SchedConsts.FILE_WATCHERS_TREE, 0);
            if (tempNode1 == null) mFound = false;
        }

        if (mFound) {
            parentNode = findChildNode(tempNode1, mFileWatchersItem.getOwner(), 1);
            if (parentNode == null) {
                parentNode = new SchedDataNode(
                                    mFileWatchersItem.getOwner(),
                                    null,
                                    tempNode1.getNodeId(),
                                    mDataArea.getNextSeqNo(),
                                    "F",
                                    mDataArea.getConnectId(),
                                    0, 0,
                                    SchedConsts.FILE_WATCHERS_SCREEN_NO);
                tempNode1.add(parentNode);
                mTreeModel.insertNodeInto(parentNode, tempNode1, elementNo);
            }

            childNode = findChildNode(parentNode, mFileWatchersItem.getFileWatcherName(), 1);
            if (childNode == null) {
                childNode = new SchedDataNode(
                                mFileWatchersItem.getFileWatcherName(),
                                mFileWatchersItem.getOwner(),
                                parentNode.getNodeId(),
                                mFileWatchersItem.getId(),
                                "L",
                                mDataArea.getConnectId(),
                                SchedConsts.FILE_WATCHERS_SCREEN_NO,
                                mScreen.getScreenId(
                                            SchedConsts.FILE_WATCHERS_SCREEN_NO,
                                            mDataArea.getVersion()),
                                SchedConsts.FILE_WATCHERS_SCREEN_NO);
                parentNode.add(childNode);
                mTreeModel.insertNodeInto(childNode, parentNode, elementNo);
            }
        }
    }

    private static SchedDataNode findChildNode(SchedDataNode mNode, String mString, int mExactFit) {

        Enumeration e1 = mNode.children();

        elementNo = 0;
        boolean mFound = false;
        boolean mContinue = true;
        SchedDataNode localNode = null;

        while (  mContinue ) {

            if ( e1.hasMoreElements() ) {
                localNode = (SchedDataNode)e1.nextElement();

                if ( localNode.getNodeName().compareTo(mString) == 0 ) {
                    mFound = true;
                    mContinue = false;
                }
                if ( mExactFit == 1 ) {
                    if ( localNode.getNodeName().compareTo(mString) >= 0 )
                        mContinue = false;
                    else
                        elementNo = elementNo + 1;
                }
            }
            else {
                mContinue = false;
            }
        }
        // System.out.println(" 2. Search --" + mFound );
        if (! mFound ) return null;
        return localNode;
    }
    private static SchedDataNode findConnectionNode(SchedDataNode mNode, int mConnectionId) {

        Enumeration e1 = mNode.children();
        boolean mFound = false;
        boolean mContinue = true;
        SchedDataNode localNode = null;

        while (  mContinue ) {
            if ( e1.hasMoreElements() ) {
                localNode = (SchedDataNode)e1.nextElement();

                if ( localNode.getConnectId() == mConnectionId ) {
                    mFound = true;
                    mContinue = false;
                }
            }
            else {
                mContinue = false;
            }
        }
        // System.out.println(" 2. Search --" + mFound );
        if (! mFound ) return null;
        return localNode;
    }
}
