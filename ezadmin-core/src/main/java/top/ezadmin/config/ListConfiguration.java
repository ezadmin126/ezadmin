package top.ezadmin.config;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import top.ezadmin.config.dto.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListConfiguration {
    Map<String, ListPageConfig> list_map = new HashMap<>();
    public static ListConfiguration instance() {
        return new ListConfiguration();
    }
    public ListPageConfig parseXml(String config) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(config)));
            Element root = doc.getDocumentElement();
            ListPageConfig pageConfig = new ListPageConfig();
            // id属性
            pageConfig.setId(root.getAttribute("id"));
            // 存储所有属性到attr
            NamedNodeMap attrs = root.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                pageConfig.getAttr().put(attr.getNodeName(), attr.getNodeValue());
            }

            // header
            NodeList headerList = root.getElementsByTagName("header");
            if (headerList.getLength() > 0) {
                pageConfig.setHeader(headerList.item(0).getTextContent().trim());
            }
            // tabs
            NodeList tabsList = root.getElementsByTagName("tabs");
            if (tabsList.getLength() > 0) {
                List<TabConfig> tabs = new ArrayList<>();
                NodeList tabNodes = ((Element) tabsList.item(0)).getElementsByTagName("tab");
                for (int i = 0; i < tabNodes.getLength(); i++) {
                    Element tabEl = (Element) tabNodes.item(i);
                    TabConfig tab = new TabConfig();
                    tab.setLabel(tabEl.getTextContent().trim());
                    tab.setSelected(Boolean.parseBoolean(tabEl.getAttribute("selected")));
                    tab.setUrl(tabEl.getAttribute("url"));
                    tabs.add(tab);
                }
                pageConfig.setTabs(tabs);
            }
            // searchForm
            NodeList searchFormList = root.getElementsByTagName("searchForm");
            if (searchFormList.getLength() > 0) {
                List<FieldConfig> fields = new ArrayList<>();
                NodeList fieldNodes = ((Element) searchFormList.item(0)).getElementsByTagName("field");
                for (int i = 0; i < fieldNodes.getLength(); i++) {
                    Element fieldEl = (Element) fieldNodes.item(i);
                    FieldConfig field = new FieldConfig();
                    field.setName(fieldEl.getAttribute("name"));
                    field.setType(fieldEl.getAttribute("type"));
                    field.setJdbctype(fieldEl.getAttribute("jdbctype"));
                    field.setOper(fieldEl.getAttribute("oper"));
                    field.setLabel(fieldEl.getTextContent().trim());
                    fields.add(field);
                }
                pageConfig.setSearchForm(fields);
            }
            // tableButtons
            NodeList tableButtonsList = root.getElementsByTagName("tableButtons");
            if (tableButtonsList.getLength() > 0) {
                List<ButtonConfig> buttons = new ArrayList<>();
                NodeList buttonNodes = ((Element) tableButtonsList.item(0)).getElementsByTagName("button");
                for (int i = 0; i < buttonNodes.getLength(); i++) {
                    Element btnEl = (Element) buttonNodes.item(i);
                    ButtonConfig btn = new ButtonConfig();
                    btn.setType(btnEl.getAttribute("type"));
                    btn.setUrl(btnEl.getAttribute("url"));
                    btn.setOpentype(btnEl.getAttribute("opentype"));
                    btn.setWindowname(btnEl.getAttribute("windowname"));
                    btn.setLabel(btnEl.getTextContent().trim());
                    buttons.add(btn);
                }
                pageConfig.setTableButtons(buttons);
            }
            // table
            NodeList tableList = root.getElementsByTagName("table");
            if (tableList.getLength() > 0) {
                TableConfig tableConfig = new TableConfig();
                Element tableEl = (Element) tableList.item(0);
                // columns
                NodeList columnsList = tableEl.getElementsByTagName("columns");
                if (columnsList.getLength() > 0) {
                    List<ColumnConfig> columns = new ArrayList<>();
                    Element columnsEl = (Element) columnsList.item(0);
                    // rowbutton
                    NodeList rowbuttonList = columnsEl.getElementsByTagName("rowbutton");
                    if (rowbuttonList.getLength() > 0) {
                        Element rowbuttonEl = (Element) rowbuttonList.item(0);
                        RowButtonConfig rowbutton = new RowButtonConfig();
                        rowbutton.setWidth(rowbuttonEl.getAttribute("width"));
                        rowbutton.setFixed(rowbuttonEl.getAttribute("fixed"));
                        List<ButtonConfig> rowBtns = new ArrayList<>();
                        NodeList rowBtnNodes = rowbuttonEl.getElementsByTagName("button");
                        for (int i = 0; i < rowBtnNodes.getLength(); i++) {
                            Element btnEl = (Element) rowBtnNodes.item(i);
                            ButtonConfig btn = new ButtonConfig();
                            btn.setType(btnEl.getAttribute("type"));
                            btn.setUrl(btnEl.getAttribute("url"));
                            btn.setOpentype(btnEl.getAttribute("opentype"));
                            btn.setWindowname(btnEl.getAttribute("windowname"));
                            btn.setLabel(btnEl.getTextContent().trim());
                            rowBtns.add(btn);
                        }
                        rowbutton.setButtons(rowBtns);
                        tableConfig.setRowbutton(rowbutton);
                    }
                    // columns
                    NodeList columnNodes = columnsEl.getElementsByTagName("column");
                    for (int i = 0; i < columnNodes.getLength(); i++) {
                        Element colEl = (Element) columnNodes.item(i);
                        ColumnConfig col = new ColumnConfig();
                        col.setName(colEl.getAttribute("name"));
                        col.setWidth(colEl.getAttribute("width"));
                        col.setMinwidth(colEl.getAttribute("minwidth"));
                        col.setFixed(colEl.getAttribute("fixed"));
                        col.setJdbctype(colEl.getAttribute("jdbctype"));
                        col.setUrl(colEl.getAttribute("url"));
                        col.setBody(colEl.getAttribute("body"));
                        col.setLabel(colEl.getTextContent().trim());
                        columns.add(col);
                    }
                    tableConfig.setColumns(columns);
                }
                pageConfig.setTable(tableConfig);
            }
            // selectSql
            NodeList selectList = root.getElementsByTagName("select");
            if (selectList.getLength() > 0) {
                Element selectEl = (Element) selectList.item(0);
                pageConfig.setSelectSql(selectEl.getTextContent().trim());
                // 处理orderby和groupby属性
                if (selectEl.hasAttribute("orderby")) {
                    pageConfig.setOrderBySql(selectEl.getAttribute("orderby"));
                }
                if (selectEl.hasAttribute("groupby")) {
                    pageConfig.setGroupBySql(selectEl.getAttribute("groupby"));
                }
            }
            // countSql
            NodeList countList = root.getElementsByTagName("count");
            if (countList.getLength() > 0) {
                pageConfig.setCountSql(countList.item(0).getTextContent().trim());
            }
            // footer
            NodeList footerList = root.getElementsByTagName("footer");
            if (footerList.getLength() > 0) {
                pageConfig.setFooter(footerList.item(0).getTextContent().trim());
            }
            return pageConfig;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public ListPageConfig parseHtml(String id) {
        return  new ListPageConfig();
    }
}
