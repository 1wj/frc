package com.digiwin.app.frc.service.athena.file.util;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.frc.service.athena.util.DmcClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

/**
 * pdf工具类
 * @author Jiangzhou
 * @date 2022/04/25
 */
@Slf4j
@Component
public class PDFUtil {
    // # 常用字体程序及对应编码
    // STSong-Light ==> UniGB-UCS2-H
    // HeiseiKakuGo-W5 ==> UniJIS-UCS2-H
    // HeiseiMin-W3 ==> UniJIS-UCS2-H
    public static String fontName = "application//module//FZYTK.TTF";

    public static final String fontEncoding = BaseFont.IDENTITY_H;
    // 最大宽度
    public static int maxWidth = 550;

    /**
     * 此标记前部分正常后部分背景着色
     */
    public static String markSymbol = "<span>";
    static{
        String frcReportFile = DWApplicationConfigUtils.getProperty("frcReportFile");
        File file = new File(frcReportFile);
        if(!file.exists()){//如果文件夹不存在
            file.mkdirs();//创建文件夹
        }
        String dmcFileId = DWApplicationConfigUtils.getProperty("frcReportEncodingId");
        File file1 = new File(frcReportFile+"/FZYTK.TTF");
        if(!file1.exists()){
            //下载文件
            try {
                DmcClient.download(dmcFileId,frcReportFile+"/FZYTK.TTF");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("下载载体文件失败");
            }
        }
        fontName = frcReportFile+"/FZYTK.TTF";
    }

    /**
     * 获取基础文字
     *
     * @param style
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static Font getFont(int style) throws DocumentException, IOException {
        return new Font(BaseFont.createFont(fontName, fontEncoding, false), 8, style);
    }


    /**
     * 创建默认列宽，指定列数、水平(居中、右、左)的表格、边框
     *
     * @param colNumber
     * @param align
     * @param border
     * @return
     */
    public static PdfPTable createTable(int colNumber, int align, int border) {
        PdfPTable table = new PdfPTable(colNumber);
        try {
            table.setTotalWidth(maxWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(align);
            table.getDefaultCell().setBorder(border);
            table.setSplitLate(true);
            table.setSplitRows(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    /**
     * 创建默认列宽，指定列数、水平(居中、右、左)的表格、边框
     *
     * @param colNumber
     * @param align
     * @param border
     * @return
     */
    public static PdfPTable createTable(float[] colNumber, int align, int border) {
        PdfPTable table = new PdfPTable(colNumber);
        try {
            table.setTotalWidth(maxWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(align);
            table.getDefaultCell().setBorder(border);
            table.setSplitLate(true);
            table.setSplitRows(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 创建单元格（指定字体、水平居…、隐藏边框）
     *
     * @param value
     *            内容
     * @param font
     *            字体类型
     * @param align
     *            水平内容格式
     * @param isDisableBorderSide
     *            是否隐藏边框
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static PdfPCell createCell(String value, Font font, int align, boolean isDisableBorderSide)
            throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        if (StringUtils.isNotEmpty(value) && value.contains(markSymbol)) {
            Phrase phrase = new Phrase(value.split(markSymbol)[0], font);
            Font font2 = getFont(Font.NORMAL);
            font2.setColor(BaseColor.RED);
            Chunk textAsChunk = new Chunk(value.split(markSymbol)[1], font2);
            phrase.add(textAsChunk);
            cell.setPhrase(phrase);
        } else {
            cell.setPhrase(new Phrase(value, font));
        }
        cell.setBorderWidth(1f);
        cell.setBorderColor(BaseColor.BLACK);
        if (isDisableBorderSide) {
            cell.disableBorderSide(15);
        }
        return cell;
    }

    /**
     * 创建单元格（指定字体、水平居…、隐藏边框、单元格跨x列合并）
     *
     * @param value
     *            内容
     * @param font
     *            字体类型
     * @param align
     *            水平内容格式
     * @param isDisableBorderSide
     *            是否隐藏边框
     * @param colspan
     *            合并列数
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static PdfPCell createCell(String value, Font font, int align, boolean isDisableBorderSide, int colspan)
            throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        cell.setColspan(colspan);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setBorderWidth(1f);
        cell.setBorderColor(BaseColor.BLACK);
        cell.setBorder(3);
        if (isDisableBorderSide) {
            cell.disableBorderSide(15);
        }
        if (StringUtils.isNotEmpty(value) && value.contains(markSymbol)) {
            Phrase phrase = new Phrase(value.split(markSymbol)[0], font);
            Font font2 = getFont(Font.NORMAL);
            font2.setColor(BaseColor.RED);
            Chunk textAsChunk = new Chunk(value.split(markSymbol)[1], font2);
            phrase.add(textAsChunk);
            cell.setPhrase(phrase);
        }else {
            cell.setPhrase(new Phrase(value, font));
        }
        cell.setUseBorderPadding(true);
        return cell;
    }


    /**
     * 获取图片单元格（通过路径获取云图片未添加）
     *
     * @param url
     *            图片地址
     * @param width
     *            宽度
     * @param heigh
     *            高度
     * @param fit
     *            是否自适应图片大小
     * @param isDisableBorderSide
     *            是否隐藏边框
     * @return
     * @throws BadElementException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static PdfPCell getImageCell(String url, int width, int heigh, boolean fit, boolean isDisableBorderSide)
            throws BadElementException, MalformedURLException, IOException {
        Image image = Image.getInstance(url);
        image.scaleAbsolute(width, heigh);
        PdfPCell pdfPCell = new PdfPCell(image, fit);
        if (isDisableBorderSide) {
            pdfPCell.disableBorderSide(15);
        }
        return pdfPCell;
    }

    /**
     * 获取图片单元格（通过路径获取云图片未添加）
     *
     * @param url
     *            图片地址
     * @param width
     *            宽度
     * @param heigh
     *            高度
     * @param fit
     *            是否自适应图片大小
     * @param isDisableBorderSide
     *            是否隐藏边框
     * @param colspan
     *            合并列数
     * @return
     * @throws BadElementException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static PdfPCell getImageCell(String url, int width, int heigh, boolean fit, boolean isDisableBorderSide,
                                        int colspan) throws BadElementException, MalformedURLException, IOException {
        PdfPCell imageCell = getImageCell(url, width, heigh, fit, isDisableBorderSide);
        imageCell.setColspan(colspan);
        return imageCell;
    }

    /**
     * 获取图片+文字单元格（通过路径获取云图片未添加）
     *
     * @param url
     *            图片地址
     * @param width
     *            宽度
     * @param heigh
     *            高度
     * @param fit
     *            是否自适应图片大小
     * @param isDisableBorderSide
     *            是否隐藏边框
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws DocumentException
     */
    public static PdfPCell getImageTextCell(String content, String url, int width, int heigh, boolean fit,
                                            boolean isDisableBorderSide) throws MalformedURLException, IOException, DocumentException {
        Image image = Image.getInstance(url);
        image.scaleAbsolute(width, heigh);
        PdfPCell pdfPCell = new PdfPCell();
        if (isDisableBorderSide) {
            pdfPCell.disableBorderSide(15);
        }
        pdfPCell.addElement(new Paragraph(content, getFont(Font.NORMAL)));
        if (fit) {
            pdfPCell.setPadding(pdfPCell.getBorderWidth() / 2.0F);
        } else {
            image.setScaleToFitLineWhenOverflow(false);
            pdfPCell.setPadding(0.0F);
            pdfPCell.addElement(image);
        }
        return pdfPCell;
    }

    /**
     * 获取图片+文字单元格（通过路径获取云图片未添加）
     *
     * @param url
     *            图片地址
     * @param width
     *            宽度
     * @param heigh
     *            高度
     * @param fit
     *            是否自适应图片大小
     * @param isDisableBorderSide
     *            是否隐藏边框
     * @param colspan
     *            合并列数
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws DocumentException
     */
    public static PdfPCell getImageTextCell(String content, String url, int width, int heigh, boolean fit,
                                            boolean isDisableBorderSide, int colspan) throws MalformedURLException, IOException, DocumentException {
        PdfPCell pdfPCell = getImageTextCell(content, url, width, heigh, fit, isDisableBorderSide);
        pdfPCell.setColspan(colspan);
        return pdfPCell;
    }

    /**
     * 添加数据
     *
     * @param document
     * @param writer
     * @param content
     *            内容数组
     * @param style
     * @param isDisableBorderSide
     * @param topInterval
     *            第一行文字与上一行间隔
     * @throws DocumentException
     * @throws IOException
     */
    public static void addLine(Document document, PdfWriter writer, PdfPTable table, String[] content, int style,
                               boolean isDisableBorderSide, int columnNum, int topInterval) throws DocumentException, IOException {
        int count = content.length % columnNum;
        for (int i = 0; i < content.length; i++) {
            // 最后一个需要根据取余数进行合并
            PdfPCell cell = null;
            if (i == content.length - 1 && count != 0) {
                cell = PDFUtil.createCell(content[i], PDFUtil.getFont(style), Element.ALIGN_LEFT, isDisableBorderSide,
                        columnNum - count + 1);

            } else {
                cell = PDFUtil.createCell(content[i], PDFUtil.getFont(style), Element.ALIGN_LEFT, isDisableBorderSide);
            }
            if (i < columnNum && topInterval != 0) {
                cell.setPaddingTop(topInterval);
            }
            table.addCell(cell);

        }
    }

    /**
     * 添加一行合并数据
     *
     * @param document
     * @param writer
     * @param content
     *            内容数组
     * @param style
     * @param isDisableBorderSide
     * @throws DocumentException
     * @throws IOException
     */
    public static void addLineMerge(Document document, PdfWriter writer, PdfPTable table, String content, int style,
                                    boolean isDisableBorderSide, int colspan) throws DocumentException, IOException {
        table.addCell(
                PDFUtil.createCell(content, PDFUtil.getFont(style), Element.ALIGN_LEFT, isDisableBorderSide, colspan));
    }

    /**
     * 添加一行合并数据
     *
     * @param document
     * @param writer
     * @param content
     *            内容数组
     * @param style
     * @param isDisableBorderSide
     * @param topInterval
     *            与上一行间隔
     * @throws DocumentException
     * @throws IOException
     */
    public static void addLineMerge(Document document, PdfWriter writer, PdfPTable table, String content, int style,
                                    boolean isDisableBorderSide, int colspan, int topInterval) throws DocumentException, IOException {
        PdfPCell cell =
                PDFUtil.createCell(content, PDFUtil.getFont(style), Element.ALIGN_LEFT, isDisableBorderSide, colspan);
        cell.setPaddingTop(topInterval);
        table.addCell(cell);
    }

    /**
     * 生成页面矩形边框
     *
     * @param writer
     * @param isFirst
     *            是否是第一页
     * @param x
     *            左下角离左边界长度
     * @param y
     *            左下角离下边界长度
     * @param w
     *            矩形k宽度
     * @param h
     *            矩形高度
     */
    public static void addRectangle(PdfWriter writer, int pageSize, boolean isFirst, float x, float y, float w,
                                    float h) {
        PdfContentByte cb = writer.getDirectContent();
        int start = 0;
        int end = 1;
        if (!isFirst) {
            start = 1;
            end = pageSize;
        }
        for (int i = start; i < end; i++) {
            cb.saveState();
            cb.setColorStroke(BaseColor.BLACK);
            cb.rectangle(x, y, w, h);
            cb.stroke();
            cb.fill();
            cb.restoreState();
        }

    }


    /**
     * 添加一组图片(基础信息的问题图片用，后续可优化为通用方法)
     *
     * @param table
     * @param urls
     * @param columnNum 列数
     * @param width 图片宽度
     * @param heigh 图片高度
     * @param fit 图片是否自适应
     * @param isDisableBorderSide 是否隐藏单元格边框
     * @throws BadElementException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static void addImageList(PdfPTable table, List<String> urls, int columnNum, int width, int heigh,
                                    boolean fit,
                                    boolean isDisableBorderSide) throws BadElementException, MalformedURLException, IOException {
        int count = urls.size() % columnNum;
        for (int i = 0; i < urls.size(); i++) {
            String pictureId = urls.get(i);
            if (i == urls.size() - 1 && count != 0) {
                table.addCell(getImageCell("D:\\桌面\\" + pictureId, width, heigh, fit, isDisableBorderSide,
                        columnNum - count + 1));
            } else {
                table.addCell(getImageCell("D:\\桌面\\" + pictureId, width, heigh, fit, isDisableBorderSide));
            }

        }
    }

    /**
     * 单元格添加表格
     *
     * @param document
     * @param writer
     * @param table
     * @param content
     *            表格内容,二维数组里的每组数据都是一条数据，第一个为表头
     * @param style
     *            文字格式
     * @param isDisableBorderSide
     *            是否需要隐藏边框
     * @param isDisableCellTableBorderSide
     *            是否需要隐藏内部表格边框
     * @param newTableColumnNum
     *            新表格列数
     * @param colspan
     *            老表格需要合并几列存放表格
     * @param topInterval
     *            表格与上一列间隔
     * @throws DocumentException
     * @throws IOException
     */
    public static void addCellTable(Document document, PdfWriter writer, PdfPTable table,PdfPTable cellTable, String[][] content, int style,
                                    boolean isDisableBorderSide, boolean isDisableCellTableBorderSide, int newTableColumnNum, int colspan,
                                    int topInterval)
            throws DocumentException, IOException {
        cellTable.setTotalWidth(maxWidth - 80);
        cellTable.setLockedWidth(true);
        cellTable.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        cellTable.getDefaultCell().setBorderWidth(10);
        cellTable.setSplitLate(false);
        cellTable.setSplitRows(true);
        for (int i = 0; i < content.length; i++) {
            String[] text = content[i];
            if (i == 0) {
                // 标题
                addLine(document, writer, cellTable, text, Font.BOLD, isDisableCellTableBorderSide, newTableColumnNum,
                        0);
            } else {
                addLine(document, writer, cellTable, text, style, isDisableCellTableBorderSide, newTableColumnNum, 0);
            }
        }
        PdfPCell cell = new PdfPCell(cellTable);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        if (isDisableBorderSide) {
            cell.disableBorderSide(15);
        }
        cell.setColspan(colspan);
        cell.setPaddingTop(topInterval);
        table.addCell(cell);
    }

    /**
     * 单元格添加表格
     *
     * @param document
     * @param writer
     * @param table
     * @param content
     *            表格内容,二维数组里的每组数据都是一条数据，第一个为表头
     * @param style
     *            文字格式
     * @param isDisableBorderSide
     *            是否需要隐藏边框
     * @param isDisableCellTableBorderSide
     *            是否需要隐藏内部表格边框
     * @param newTableColumnNum
     *            新表格列数
     * @param colspan
     *            老表格需要合并几列存放表格
     * @param topInterval
     *            表格与上一列间隔
     * @throws DocumentException
     * @throws IOException
     */
    public static void addTextCellTable(Document document, PdfWriter writer, PdfPTable table,PdfPTable cellTable, String content, int style,int alignStyle,
                                    boolean isDisableBorderSide, boolean isDisableCellTableBorderSide, int newTableColumnNum, int colspan,
                                    int topInterval)
            throws DocumentException, IOException {
        cellTable.setTotalWidth(maxWidth - 80);
        cellTable.setLockedWidth(true);
        cellTable.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        cellTable.getDefaultCell().setBorderWidth(10);
        cellTable.setSplitLate(false);
        cellTable.setSplitRows(true);
        cellTable.addCell(createCell(content, PDFUtil.getFont(style), Element.ALIGN_LEFT, isDisableBorderSide));
        PdfPCell cell = new PdfPCell(cellTable);
        cell.setVerticalAlignment(alignStyle);
        cell.setHorizontalAlignment(alignStyle);
        if (isDisableBorderSide) {
            cell.disableBorderSide(15);
        }
        cell.setColspan(colspan);
        cell.setPaddingTop(topInterval);
        table.addCell(cell);
    }



    /**
     * 比较两个日期 大于0 date1时间在date2后 小于0 date1时间在date2前 等于0 date1时间于date2相等
     *
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static int compareDate(String date1, String date2) {
        if (StringUtils.isEmpty(date1) || StringUtils.isEmpty(date2)) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date1).compareTo(sdf.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 对时间进行格式化操作，判断是否为空的同时只要年月日
     * @param date 传入的时间
     * @return 修改后的时间
     */
    public static String formatDate(String date) {
        if (StringUtils.isEmpty(date)) {
            return date;
        }
        return date.split(" ")[0];
    }


    /**
     * 对json数组排序，
     * @param jsonArr
     * @param sortKey 排序关键字
     * @param is_desc is_desc-false升序列  is_desc-true降序 (排序字段为字符串)
     * @return
     */
    public static JSONArray jsonArraySort(JSONArray jsonArr, String sortKey, boolean is_desc) {
        //存放排序结果json数组
        JSONArray sortedJsonArray = new JSONArray();
        //用于排序的list
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        //将参数json数组每一项取出，放入list
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        //快速排序，重写compare方法，完成按指定字段比较，完成排序
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            //排序字段
            private  final String KEY_NAME = sortKey;
            //重写compare方法
            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();
                try {
                    valA = a.getString(KEY_NAME);
                    valB = b.getString(KEY_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //是升序还是降序
                if (is_desc){
                    return -valA.compareTo(valB);
                } else {
                    return -valB.compareTo(valA);
                }

            }
        });
        //将排序后结果放入结果jsonArray
        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

    /**
     * 把格林威治时间进行相应格式化操作
     * @param longtime
     * @return
     */
    public static String getTimeYMD(String longtime) {
        String formatTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(longtime));
        return formatTime;
    }
}
