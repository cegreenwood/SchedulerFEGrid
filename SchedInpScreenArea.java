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
import java.awt.*;
import java.util.*;
import java.text.*;

class SchedInpScreenArea {

    private NumberFormat amount2Format;
    // private Color        lineColor;

    private ClassLoader  cl;

    public SchedInpScreenArea() {
        screenObj = new Vector<PaneObject>(10,10);
        indexObj  = new Vector<PaneIndex>(10,10);
        setUpFormats();
    }

    public void clearVectors() {
        screenObj.clear();
        indexObj.clear();
    }

    public void processScreenData(int    screenId,
                                  int    screenNo,
                                  String versionStr,
                                  int    fgrndColour,
                                  int    bgrndColour) {

        boolean mFound = false;
        for (int i1 = 0; i1 < indexObj.size(); i1++) {
            PaneIndex lPaneIndex = indexObj.get(i1);
            if (lPaneIndex.getScreenId() == screenId) {
                mFound = true;
                break;
            }
        }

        PaneIndex mPaneIndex = new PaneIndex(screenId, screenNo, versionStr);
        indexObj.add(mPaneIndex);

        if (! mFound) {
            PaneObject mPaneInp = new PaneObject(screenId, screenNo, versionStr);
            screenObj.add(mPaneInp);

            mPaneInp.setFgrndColour(fgrndColour);
            mPaneInp.setBgrndColour(bgrndColour);
        }
    }

    public int getScreenId(int screenNo,
                           String version) {
        for (int i1 = 0; i1 < indexObj.size(); i1++) {
            PaneIndex lPaneIndex = indexObj.get(i1);
            if ((lPaneIndex.getScreenNo() == screenNo) &&
                (lPaneIndex.getVersion().equals(version)))
            {
                return lPaneIndex.getScreenId();
            }
        }
        return 0;
    }

    class PaneIndex {
        private int                 m_ScreenId;
        private int                 m_ScreenNo;
        private String              m_Version;

        PaneIndex(int mScreenId,
                  int mScreenNo,
                  String mVersion) {

            m_ScreenId = mScreenId;
            m_ScreenNo = mScreenNo;
            m_Version = mVersion;
        }

        public int getScreenId() {
             return m_ScreenId;
        }
        public int getScreenNo() {
            return m_ScreenNo;
        }
        public String getVersion() {
            return m_Version;
        }
    }

    public PaneObject getScreenObj(int m_Row) {
        return screenObj.get(m_Row);
    }

    public PaneObject getScreen(int m_ScreenId) {
        for (int i1 = 0; i1 < screenObj.size(); i1++) {
            PaneObject mPane = screenObj.get(i1);
            if (mPane.getScreenId() == m_ScreenId) {
                return mPane;
            }
        }
        return null;
    }

    public int sizeScreenObj() {
        return screenObj.size();
    }

    private void setUpFormats() {
        amount2Format = NumberFormat.getIntegerInstance();
        amount2Format.setMinimumIntegerDigits(1);
        amount2Format.setMaximumIntegerDigits(3);
    }

    // public void PageSetup(SchedGlobalData mArea, SchedInpScreenArea.PaneObject mInpPane) {

    //     int s1 = mInpPane.sizeLabelObj();
    //     int t1 = mInpPane.sizeTextObj();
    //     int t2 = mInpPane.sizeTextAreaObj();
    //     int d1 = mInpPane.sizeDateTimeObj();

    //     Color mColor;
    //     Color bColor = mArea.getScreenColor(mInpPane.getFgrndColour());

    //     cl = this.getClass().getClassLoader();


    //     if (mInpPane.getBgrndColour() > 0) {
    //         mColor = mArea.getScreenColor(mInpPane.getBgrndColour());
    //         mInpPane.setBColor(mColor);
    //     }
    //     if (mInpPane.getFgrndColour() > 0) {
    //         mColor = mArea.getScreenColor(mInpPane.getFgrndColour());
    //         mInpPane.setFColor(mColor);
    //     }

    //     for (int i2 = 0; i2 < s1; i2++) {

    //         SchedInpScreenArea.PaneObject.LabelItem m_P1 = mInpPane.getLabelObj(i2);

    //         int m_Style = m_P1.get_FontStyle();
    //         if (m_Style == 3)
    //             m_P1.setFont(new Font(m_P1.get_Font(), Font.ITALIC, m_P1.get_FontSize()));
    //         else {
    //             if (m_Style == 2)
    //                 m_P1.setFont(new Font(m_P1.get_Font(), Font.BOLD, m_P1.get_FontSize()));
    //             else 
    //                 m_P1.setFont(new Font(m_P1.get_Font(), Font.PLAIN, m_P1.get_FontSize()));
    //         }

    //         if (m_P1.get_ForeColor() > 0) {
    //             mColor = mArea.getScreenColor(m_P1.get_ForeColor());
    //             if (mColor != null) {
    //                 m_P1.setForeground(mColor);
    //             }
    //         }
    //         if (m_P1.get_BackColor() > 0) {
    //             mColor = mArea.getScreenColor(m_P1.get_BackColor());
    //             if (mColor != null) {
    //                 m_P1.setOpaque(true);
    //                 m_P1.setBackground(mColor);
    //             }
    //         }
    //     }

    //     for (int i3 = 0; i3 < t1; i3++) {

    //         SchedInpScreenArea.PaneObject.TextItem m_T1 = mInpPane.getTextObj(i3);


    //         int m_Style = m_T1.get_FontStyle();
    //         if (m_Style == 3)
    //             m_T1.setFont(new Font(m_T1.get_Font(), Font.ITALIC, m_T1.get_FontSize()));
    //         else {
    //             if (m_Style == 2)
    //                 m_T1.setFont(new Font(m_T1.get_Font(), Font.BOLD, m_T1.get_FontSize()));
    //             else 
    //                 m_T1.setFont(new Font(m_T1.get_Font(), Font.PLAIN, m_T1.get_FontSize()));
    //         }
    //         if (m_T1.get_ForeColor() > 0) {
    //             mColor = mArea.getScreenColor(m_T1.get_ForeColor());
    //             if (mColor != null) {
    //                 m_T1.setForeground(mColor);
    //                 if ( m_T1.get_Combo().equals("Y") ) {
    //                     m_T1.setComboForeground(mColor);
    //                 }
    //             }
    //         }

    //         if (m_T1.get_BackColor() > 0) {
    //             mColor = mArea.getScreenColor(m_T1.get_BackColor());
    //             if (mColor != null) {
    //                 m_T1.setOpaque(true);
    //                 m_T1.setBackground(mColor);
    //                 if ( m_T1.get_Combo().equals("Y") ) {
    //                     m_T1.setComboBackground(mColor);
    //                 }
    //             }

    //         }

    //         if (m_T1.get_FormatType() == 1) {
    //             m_T1.setHorizontalAlignment(JTextField.RIGHT);
    //         }

    //         m_T1.setBorder(BorderFactory.createLineBorder(bColor));

    //         m_T1.setEditable(true);
    //         m_T1.setFocusable(true);

    //     }


    //     for (int i4 = 0; i4 < t2; i4++) {

    //         SchedInpScreenArea.PaneObject.TextAreaItem m_T2 = mInpPane.getTextAreaObj(i4);

    //         int m_Style = m_T2.get_FontStyle();
    //         if (m_Style == 3)
    //             m_T2.setFont(new Font(m_T2.get_Font(), Font.ITALIC, m_T2.get_FontSize()));
    //         else {
    //             if (m_Style == 2)
    //                 m_T2.setFont(new Font(m_T2.get_Font(), Font.BOLD, m_T2.get_FontSize()));
    //             else 
    //                 m_T2.setFont(new Font(m_T2.get_Font(), Font.PLAIN, m_T2.get_FontSize()));
    //         }
    //         if (m_T2.get_ForeColor() > 0) {
    //             mColor = mArea.getScreenColor(m_T2.get_ForeColor());
    //             if (mColor != null) {
    //                 m_T2.setForeground(mColor);
    //                 // m_T2.setBorder(BorderFactory.createLineBorder(mColor));
    //             }
    //         }


    //         if (m_T2.get_BackColor() > 0) {
    //             mColor = mArea.getScreenColor(m_T2.get_BackColor());
    //             if (mColor != null) {
    //                 m_T2.setOpaque(true);
    //                 m_T2.setBackground(mColor);
    //             }
    //         }
    //         m_T2.setBorder(BorderFactory.createLineBorder(bColor));

    //         m_T2.setEditable(true);
    //         m_T2.setFocusable(true);
    //     }
    //     for (int i5 = 0; i5 < d1; i5++) {
    //         SchedInpScreenArea.PaneObject.DateTimeItem m_D1 = mInpPane.getDateTimeObj(i5);
    //         if (m_D1.get_BackColor() > 0) {
    //             mColor = mArea.getScreenColor(m_D1.get_BackColor());
    //             if (mColor != null) {
    //                 m_D1.setOpaque(true);
    //                 m_D1.setBackground(mColor);
    //             }
    //         }
    //         if (m_D1.get_ForeColor() > 0) {
    //             mColor = mArea.getScreenColor(m_D1.get_ForeColor());
    //             if (mColor != null) {
    //                 m_D1.setOpaque(true);
    //                 m_D1.setForeground(mColor);
    //             }
    //         }
    //     }
    // }

    class PaneObject {
        private JScrollPane         m_rightView;
        // private JPanel              m_Pane;
        private int                 m_ScreenId;
        private int                 m_ScreenNo;
        private int                 Priority;
        private int                 bgrndColour;
        private int                 fgrndColour;
        private Color               bColor;
        private Color               fColor;
        private String              m_Version;

        public PaneObject(int screenId, int screenNo, String Version) {
            labelObj = new Vector<LabelItem>(50,25);
            textObj = new Vector<TextItem>(50,25);
            textAreaObj = new Vector<TextAreaItem>(10,10);
            dateTimeObj = new Vector<DateTimeItem>(10,10);

            // m_Pane = new JPanel();
            // m_Pane.setOpaque(true);

            m_ScreenId = screenId;
            m_ScreenNo = screenNo;
            m_Version = Version;
            Priority = 1;
        }
        public void setBgrndColour(int m_BgrndColour) {
            bgrndColour = m_BgrndColour;
        }
        public void setFgrndColour(int m_FgrndColour) {
            fgrndColour = m_FgrndColour;
        }
        public int getBgrndColour() {
            return bgrndColour;
        }
        public int getFgrndColour() {
            return fgrndColour;
        }
        public void setBColor(Color m_BColor) {
            bColor = m_BColor;
        }
        public void setFColor(Color m_FColor) {
            fColor = m_FColor;
        }
        public Color getBColor() {
            return bColor;
        }
        public Color getFColor() {
            return fColor;
        }
        public String getVersion() {
            return m_Version;
        }

        public JScrollPane getScrollPane() {
            return m_rightView;
        }

        // public JPanel getPanel() {
        //     return m_Pane;
        // }
        // public void setPanel(JPanel mPanel) {
        //     m_Pane = mPanel;
        // }

        public int getScreenId() {
            return m_ScreenId;
        }
        public int getScreenNo() {
            return m_ScreenNo;
        }

        // Following are the methods associated with a text object.
        public int sizeTextObj() {
            return textObj.size();
        }
        public boolean addTextObj(TextItem m_Item) {
            return textObj.add(m_Item);
        }
        public TextItem getTextObj(int m_Row) {
            return textObj.get(m_Row);
        }
        public TextItem getTextItemId(int m_ItemId) {
            TextItem m_TextItem = null;
            for (int i = 0; i < textObj.size(); i++) {
                m_TextItem = textObj.get(i);
                if (m_TextItem.get_ItemId() == m_ItemId) {
                    break;
                }
            }
            return m_TextItem;
        }
        public String getTextObjId(int m_ItemId) {
            TextItem m_TextItem = null;
            for (int i = 0; i < textObj.size(); i++) {
                m_TextItem = textObj.get(i);
                if (m_TextItem.get_ItemId() == m_ItemId) {
                    break;
                }
            }
            return m_TextItem.getText();
        }
        public void updateTextObj(String m_Text, int m_ItemId) {
            for (int i = 0; i < textObj.size(); i++) {
                TextItem m_TextItem = textObj.get(i);
                if (m_TextItem.get_ItemId() == m_ItemId) {
                    m_TextItem.update_Text(m_Text);
                }
            }
        }

        class TextItem extends JTextField {
            public static final long serialVersionUID = 1L;

            private int                m_RowType;
            private int                m_XPoint;
            private int                m_YPoint;
            private int                m_XWidth;
            private int                m_YHeight;
            private String             m_Description;
            private String             m_Font;
            private int                m_FontSize;
            private int                m_FontStyle;
            private int                m_ForeColor;
            private int                m_BackColor;
            private int                m_PageNo;
            private int                m_ItemId;
            private int                m_ColLength;
            private int                m_FormatType;
            private String             m_Button;
            private String             m_Combo;
            private int                m_ComboId;
            private JComboBox<String>  m_ComboBox;
            private int                m_ComboFlag;

            private String m_String;

            public TextItem(int RowType,
                          String LDescription,
                          int XPoint, int YPoint,
                          int XWidth, int YHeight,
                          String LFont,
                          int LFontSize, int LFontStyle,
                          int LPageNo,  int LItemId,
                          int LForeColor, int LBackColor, int LColLength,
                          int LFormatType, String LButton,
                          String LCombo, int LComboId) {

                // super(amount2Format);

                // super.setColumns(LColLength);
                super.setEditable(true);
                super.setBorder(BorderFactory.createLineBorder(
                                SchedFile.getMiscArea().getScreenColor(9)));

                m_RowType = RowType;
                m_XPoint = XPoint;
                m_YPoint = YPoint;
                m_XWidth = XWidth;
                m_YHeight = YHeight;
                this.m_Description = LDescription;
                this.m_Font = LFont;
                m_FontSize = LFontSize;
                m_FontStyle = LFontStyle;
                m_ForeColor = LForeColor;
                m_BackColor = LBackColor;
                m_PageNo = LPageNo;
                m_ItemId = LItemId;
                m_ColLength = LColLength;
                m_FormatType = LFormatType;
                m_Button = LButton;
                m_Combo = LCombo;
                m_ComboId = LComboId;
                m_ComboFlag = 0;
                if ( m_Combo.equals("Y") ) {
                    m_ComboBox = new JComboBox<String>();
                    m_ComboBox.setBorder(BorderFactory.createLineBorder(
                                SchedFile.getMiscArea().getScreenColor(9)));
                }
            }

            public int get_RowType() {
                return m_RowType;
            }

            public JComboBox<String> getComboBoxType1(SchedGlobalData Area) {
                int i3 = 0;
                int i4 = 1;
                m_ComboBox.removeAllItems();
                SchedGlobalData.screenCombo mComboObj;

                m_ComboBox.addItem("Select Column...");

                for (int i = 0; i < Area.sizeComboObj(); i++) {
                    mComboObj = Area.getComboObj(i);
                    if ( mComboObj.getComboId() == m_ComboId ) {
                        m_ComboBox.addItem(mComboObj.getColumnText());
                    }
                }
                m_ComboFlag = 1;
                m_ComboBox.setSelectedIndex(0);
                return m_ComboBox;
            }

            public JComboBox<String> getComboBoxType2(SchedDataArea mDataArea) {
                int i3 = 0;
                int i4 = 1;
                m_ComboBox.removeAllItems();
                SchedDataArea.screenCombo mComboObj;

                m_ComboBox.addItem("Select Column...");

                for (int i = 0; i < mDataArea.sizeComboObj(); i++) {

                    mComboObj = mDataArea.getComboObj(i);

                    if ( mComboObj.getComboId() == m_ComboId ) {

                        if (m_ComboId == 53 ||
                            m_ComboId == 54 ||
                            m_ComboId == 55) {
                            if (mComboObj.getOwner().equals(mDataArea.getUserName().toUpperCase())) {
                                i3 = i3 + 1;
                                m_ComboBox.insertItemAt(mComboObj.getColumnText(),i3);
                            }
                            else {
                                m_ComboBox.insertItemAt(mComboObj.getOwner() + "." +
                                                 mComboObj.getColumnText(), i4);
                            }
                            i4 = i4 + 1;
                        }
                        else {
                            m_ComboBox.addItem(mComboObj.getColumnText());
                        }
                    }
                }
                m_ComboFlag = 1;
                m_ComboBox.setSelectedIndex(0);
                return m_ComboBox;
            }
            public void setZero() {
                // super.setText(String.format("%3d",0));
                this.setText(amount2Format.format(0));
            }
            public String getSelectedItem() {
                if (! m_ComboBox.getSelectedItem().equals("Select Column...") ) {
                    return (String)m_ComboBox.getSelectedItem();
                }
                else {
                    return " ";
                }
            }
            public int getSelectedIndex() {
                return m_ComboBox.getSelectedIndex();
            }
            public void setComboBackground(Color  mColor) {
                m_ComboBox.setBackground(mColor);
            }
            public void setComboForeground(Color mColor) {
                m_ComboBox.setForeground(mColor);
            }
            public int get_XPoint() {
                return m_XPoint;
            }
            public int get_YPoint() {
                return m_YPoint;
            }
            public int get_Width() {
                return m_XWidth;
            }
            public int get_Height() {
                return m_YHeight;
            }
            public String get_Description() {
                return m_Description;
            }
            public String get_Text() {
                return super.getText();
            }
            public int get_ColLength() {
                return m_ColLength;
            }
            public int get_FormatType() {
                return m_FormatType;
            }
            public String get_Button() {
                return m_Button;
            }
            public String get_Combo() {
                return m_Combo;
            }
            public int get_ComboId() {
                return m_ComboId;
            }
            public void update_Text(String LText) {
                super.setText(LText);
            }
            public String get_Font() {
                return m_Font;
            };
            public int get_FontSize() {
                return m_FontSize;
            }
            public int get_FontStyle() {
                return m_FontStyle;
            }
            public int get_ForeColor() {
                return m_ForeColor;
            };
            public int get_BackColor() {
                return m_BackColor;
            };
            public int get_PageNo() {
                return m_PageNo;
            }
            public int get_ItemId() {
                return m_ItemId;
            }


            // class ComboItem extends JComboBox {
            //     public static final long serialVersionUID = 1L;

            //     private int    mComboId;

            //     public ComboItem(int comboId) {
            //         mComboId = comboId;
            //         super.addItem("Select Column...");
            //     }
            // }
        }

        public int sizeTextAreaObj() {
            return textAreaObj.size();
        }
        public boolean addTextAreaObj(TextAreaItem m_Item) {
            return textAreaObj.add(m_Item);
        }
        public TextAreaItem getTextAreaObj(int m_Row) {
            return textAreaObj.get(m_Row);
        }
        public void updateTextAreaObj(String m_Text, int m_ItemId) {
            for (int i = 0; i < textAreaObj.size(); i++) {
                TextAreaItem m_TextAreaItem = textAreaObj.get(i);
                if (m_TextAreaItem.get_ItemId() == m_ItemId) {
                    m_TextAreaItem.update_Text(m_Text);
                }
            }
        }

        class TextAreaItem extends JTextArea {
            public static final long serialVersionUID = 1L;

            private int        m_XPoint;
            private int        m_YPoint;
            private int        m_XWidth;
            private int        m_YHeight;
            private String     m_Description;
            private String     m_Font;
            private int        m_FontSize;
            private int        m_FontStyle;
            private int        m_ForeColor;
            private int        m_BackColor;
            private int        m_PageNo;
            private int        m_ItemId;
            private int        m_ColLength;
            private int        m_FormatType;
            private String     m_Button;

            private String     m_String;

            public TextAreaItem(String LDescription,
                                int XPoint, int YPoint,
                                int XWidth, int YHeight,
                                String LFont,
                                int LFontSize, int LFontStyle,
                                int LPageNo,  int LItemId,
                                int LForeColor, int LBackColor, int LColLength,
                                int LFormatType, String LButton) {

                super.setColumns(LColLength);
                super.setEditable(true);
                super.setLineWrap(true);
                super.setWrapStyleWord(true);
                super.setBorder(BorderFactory.createLineBorder(
                                SchedFile.getMiscArea().getScreenColor(9)));

                m_XPoint = XPoint;
                m_YPoint = YPoint;
                m_XWidth = XWidth;
                m_YHeight = YHeight;
                this.m_Description = LDescription;
                this.m_Font = LFont;
                m_FontSize = LFontSize;
                m_FontStyle = LFontStyle;
                m_ForeColor = LForeColor;
                m_BackColor = LBackColor;
                m_PageNo = LPageNo;
                m_ItemId = LItemId;
                m_ColLength = LColLength;
                m_FormatType = LFormatType;
                m_Button = LButton;
            }

            public int get_XPoint() {
                return m_XPoint;
            }
            public int get_YPoint() {
                return m_YPoint;
            }
            public int get_Width() {
                return m_XWidth;
            }
            public int get_Height() {
                return m_YHeight;
            }
            public String get_Description() {
                return m_Description;
            }
            public String get_Text() {
                return super.getText();
            }
            public int get_ColLength() {
                return m_ColLength;
            }
            public int get_FormatType() {
                return m_FormatType;
            }
            public String get_Button() {
                return m_Button;
            }
            public void update_Text(String LText) {
                super.setText(LText);
            }
            public String get_Font() {
                return m_Font;
            };
            public int get_FontSize() {
                return m_FontSize;
            }
            public int get_FontStyle() {
                return m_FontStyle;
            }
            public int get_ForeColor() {
                return m_ForeColor;
            };
            public int get_BackColor() {
                return m_BackColor;
            };
            public int get_PageNo() {
                return m_PageNo;
            }
            public int get_ItemId() {
                return m_ItemId;
            }
        }

        public boolean addLabelObj(LabelItem m_Item) {
            return labelObj.add(m_Item);
        }
        public LabelItem getLabelObj(int m_Row) {
            return labelObj.get(m_Row);
        }
        public int sizeLabelObj() {
            return labelObj.size();
        }

        class LabelItem extends JLabel {
            public static final long serialVersionUID = 1L;

            private int    m_XPoint;
            private int    m_YPoint;
            private int    m_XWidth;
            private int    m_YHeight;
            private String m_Description;
            private String m_Font;
            private int    m_FontSize;
            private int    m_FontStyle;
            private int    m_ForeColor;
            private int    m_BackColor;
            private int    m_PageNo;

            public LabelItem(String LDescription,
                           int XPoint, int YPoint,
                           int XWidth, int YHeight,
                           String LFont,
                           int LFontSize, int LFontStyle,
                           int LPageNo, int LForeColor, int LBackColor) {
                super(LDescription);
                m_XPoint = XPoint;
                m_YPoint = YPoint;
                m_XWidth = XWidth;
                m_YHeight = YHeight;
                this.m_Description = LDescription;
                this.m_Font = LFont;
                m_FontSize = LFontSize;
                m_FontStyle = LFontStyle;
                m_PageNo = LPageNo;
                m_ForeColor = LForeColor;
                m_BackColor = LBackColor;
            }
            public int get_XPoint() {
              return m_XPoint;
            }
            public int get_YPoint() {
              return m_YPoint;
            }
            public int get_Width() {
              return m_XWidth;
            }
            public int get_Height() {
              return m_YHeight;
            }
            public String get_Description() {
              return m_Description;
            }
            public void update_Description(String LDescription) {
              this.m_Description = LDescription;
            }
            public String get_Font() {
              return m_Font;
            };
            public int get_FontSize() {
              return m_FontSize;
            }
            public int get_FontStyle() {
              return m_FontStyle;
            }
            public int get_PageNo() {
              return m_PageNo;
            }
            public int get_ForeColor() {
              return m_ForeColor;
            };
            public int get_BackColor() {
              return m_BackColor;
            };
        }

        public DateTimeItem getDateTimeObj(int m_Row) {
            return dateTimeObj.get(m_Row);
        }
        public boolean addDateTimeObj(DateTimeItem m_Item) {
            return dateTimeObj.add(m_Item);
        }
        public int sizeDateTimeObj() {
            return dateTimeObj.size();
        }

        class DateTimeItem extends SchedDateTime {
            public static final long serialVersionUID = 1L;

            private int        m_XPoint;
            private int        m_YPoint;
            private int        m_XWidth;
            private int        m_YHeight;
            private String     m_Description;
            private String     m_Font;
            private int        m_FontSize;
            private int        m_FontStyle;
            private int        m_ForeColor;
            private int        m_BackColor;
            private int        m_PageNo;
            private int        m_ItemId;
            private int        m_ColLength;
            private int        m_FormatType;

            private String m_String;

            public DateTimeItem(String LDescription,
                          int XPoint, int YPoint,
                          int XWidth, int YHeight,
                          String LFont,
                          int LFontSize, int LFontStyle,
                          int LPageNo,  int LItemId,
                          int LForeColor, int LBackColor, int LColLength,
                          int LFormatType) {

                super.setBorder(BorderFactory.createLineBorder(
                                SchedFile.getMiscArea().getScreenColor(9)));
                m_XPoint = XPoint;
                m_YPoint = YPoint;
                m_XWidth = XWidth;
                m_YHeight = YHeight;
                m_Description = LDescription;
                m_Font = LFont;
                m_FontSize = LFontSize;
                m_FontStyle = LFontStyle;
                m_ForeColor = LForeColor;
                m_BackColor = LBackColor;
                m_PageNo = LPageNo;
                m_ItemId = LItemId;
                m_ColLength = LColLength;
                m_FormatType = LFormatType;
            }

            public void initObj(SchedGlobalData  Area,
                                String    DateStr) {
                super.initObj(Area, DateStr);
            }
            public String getDateString() {
                return super.getDateString();
            }
            public int get_XPoint() {
                return m_XPoint;
            }
            public int get_YPoint() {
                return m_YPoint;
            }
            public int get_Width() {
                return m_XWidth;
            }
            public int get_Height() {
                return m_YHeight;
            }
            public String get_Description() {
                return m_Description;
            }
            public String get_Font() {
              return m_Font;
            };
            public int get_FontSize() {
              return m_FontSize;
            }
            public int get_FontStyle() {
              return m_FontStyle;
            }
            public int get_ForeColor() {
              return m_ForeColor;
            };
            public int get_BackColor() {
              return m_BackColor;
            };
            public int get_PageNo() {
                return m_PageNo;
            }
            public int get_ItemId() {
                return m_ItemId;
            }
            public int get_ColLength() {
                return m_ColLength;
            }
            public int get_FormatType() {
                return m_FormatType;
            }
        }

        private Vector<LabelItem>     labelObj;
        private Vector<TextItem>      textObj;
        private Vector<TextAreaItem>  textAreaObj;
        private Vector<DateTimeItem> dateTimeObj;
    }


    
    // private JPanel                  m_JPanel;
    // private PaneObject              m_PaneObject;
    private Vector<PaneIndex>       indexObj;
    private Vector<PaneObject>      screenObj;
}
