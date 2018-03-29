package com.king.app.jactionbar.parser;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;

import com.king.app.jactionbar.JMenu;
import com.king.app.jactionbar.JMenuItem;
import com.king.app.jactionbar.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 描述:parse menu file
 * codes are referred to MenuInflater
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/28 9:04
 */
public class MenuParser {

    /** Menu tag name in XML. */
    private static final String XML_MENU = "menu";

    /** Group tag name in XML. */
    private static final String XML_GROUP = "group";

    /** Item tag name in XML. */
    private static final String XML_ITEM = "item";

    public JMenu inflate(Context context, int menuRes) throws XmlPullParserException, IOException {
        XmlResourceParser parser = context.getResources().getLayout(menuRes);

        AttributeSet attrs = Xml.asAttributeSet(parser);
        int eventType = parser.getEventType();
        JMenu menu = new JMenu();

        String tagName;
        String unknownTagName = null;
        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.getName();
                if (tagName.equals(XML_MENU)) {
                    // Go to next tag
                    eventType = parser.next();
                    break;
                }

                throw new RuntimeException("Expecting menu, got " + tagName);
            }
            eventType = parser.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);

        boolean reachedEndOfMenu = false;
        boolean lookingForEndOfUnknownTag = false;
        while (!reachedEndOfMenu) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (lookingForEndOfUnknownTag) {
                        break;
                    }

                    tagName = parser.getName();
                    if (tagName.equals(XML_GROUP)) {
//                        menuState.readGroup(attrs);
                    } else if (tagName.equals(XML_ITEM)) {
                        readItem(context, attrs, menu);
                    } else if (tagName.equals(XML_MENU)) {
                        // A menu start tag denotes a submenu for an item
//                        SubMenu subMenu = menuState.addSubMenuItem();
//                        registerMenu(subMenu, attrs);
//
//                        // Parse the submenu into returned SubMenu
//                        parseMenu(parser, attrs, subMenu);
                    } else {
                        lookingForEndOfUnknownTag = true;
                        unknownTagName = tagName;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (lookingForEndOfUnknownTag && tagName.equals(unknownTagName)) {
                        lookingForEndOfUnknownTag = false;
                        unknownTagName = null;
                    } else if (tagName.equals(XML_GROUP)) {
//                        menuState.resetGroup();
                    } else if (tagName.equals(XML_ITEM)) {
                        // Add the item if it hasn't been added (if the item was
                        // a submenu, it would have been added already)
//                        if (!menuState.hasAddedItem()) {
//                            if (menuState.itemActionProvider != null &&
//                                    menuState.itemActionProvider.hasSubMenu()) {
//                                registerMenu(menuState.addSubMenuItem(), attrs);
//                            } else {
//                                registerMenu(menuState.addItem(), attrs);
//                            }
//                        }
                    } else if (tagName.equals(XML_MENU)) {
                        reachedEndOfMenu = true;
                    }
                    break;

                case XmlPullParser.END_DOCUMENT:
                    throw new RuntimeException("Unexpected end of document");
            }

            eventType = parser.next();
        }
        return menu;
    }

    private void readItem(Context context, AttributeSet attrs, JMenu menu) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.JMenuItem);
        int id = a.getResourceId(R.styleable.JMenuItem_android_id, -1);
        int iconRes = a.getResourceId(R.styleable.JMenuItem_android_icon, -1);
        String title = a.getText(R.styleable.JMenuItem_android_title).toString();
        int showAsAction = a.getInt(R.styleable.JMenuItem_showAsAction, -1);

        JMenuItem item = new JMenuItem();
        item.setId(id);
        item.setIconRes(iconRes);
        item.setTitle(title);
        item.setShowAsAction(showAsAction);
        if (menu.getItemList() == null) {
            menu.setItemList(new ArrayList<JMenuItem>());
        }
        menu.getItemList().add(item);
    }
}
